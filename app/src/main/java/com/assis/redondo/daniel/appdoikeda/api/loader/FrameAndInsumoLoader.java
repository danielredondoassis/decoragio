package com.assis.redondo.daniel.appdoikeda.api.loader;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.widget.Toast;

import com.assis.redondo.daniel.appdoikeda.R;
import com.assis.redondo.daniel.appdoikeda.api.model.InsumoModel;
import com.assis.redondo.daniel.appdoikeda.api.response.FrameModelResponse;
import com.assis.redondo.daniel.appdoikeda.data.DataController;
import com.assis.redondo.daniel.appdoikeda.data.GenericViewController;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class FrameAndInsumoLoader {

    private Activity context;
    private DataController mDataController;
    private boolean fromUri;
    private Uri mUri;
    private File mFile;
    private ProgressDialog mLoadingDialog;


    public void init(Activity context){
        this.context = context;
        mDataController = DataController.getInstance(context);
        fromUri = false;
        TaskGetInsumos getFrequencyTypes = new TaskGetInsumos();
        TaskGetFrames getFrames = new TaskGetFrames();
        getFrequencyTypes.execute();
        getFrames.execute();

    }

    public void loadInsumosFromFile(Activity context, File File){
        this.context = context;
        mDataController = DataController.getInstance(context);
        fromUri = true;
        mFile = File;
        if(mFile.exists()) {
            TaskGetInsumos getInsumos = new TaskGetInsumos();
            getInsumos.execute();
        }
    }

    public void loadFramesFromFile(Activity context, File File){
        this.context = context;
        mDataController = DataController.getInstance(context);
        fromUri = true;
        mFile = File;
        if(mFile.exists()) {
            TaskGetFrames getFrames = new TaskGetFrames();
            getFrames.execute();
        }

    }

    public class TaskGetInsumos extends AsyncTask<String, Void, InsumoModel> {

        @Override
        protected InsumoModel doInBackground(String... arg0) {

            try {

                InputStream is;

                if(fromUri){
                    is = new FileInputStream(mFile);
                } else {
                    is = context.getAssets().open("insumos.json");
                }

                Gson gson = new GsonBuilder().create();
                Reader reader = new InputStreamReader(is);

                InsumoModel insumos = gson.fromJson(reader, InsumoModel.class);
                is.close();

                return insumos;
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(InsumoModel result) {
            if(result != null) {
                if(result.getBackground() == null || result.getCollage() == null || result.getGlass() == null || result.getPasspartout() == null){
                    Toast.makeText(context,"Ocorreu um erro ao salvar seu novo arquivo de Insumos, verifique o JSON e tente novamente",Toast.LENGTH_LONG).show();
                } else {
                    mDataController.saveInsumos(result);
                    Toast.makeText(context,"Insumos atualizados com sucesso",Toast.LENGTH_LONG).show();
                }
                GenericViewController.getInstance(context).hideLoading();
            } else {
                Toast.makeText(context,"Ocorreu um erro ao salvar seu novo arquivo de Insumos, verifique o JSON e tente novamente",Toast.LENGTH_LONG).show();
            }
            GenericViewController.getInstance(context).hideLoading();
        }

    }

    public class TaskGetFrames extends AsyncTask<String, Void, FrameModelResponse> {

        @Override
        protected FrameModelResponse doInBackground(String... arg0) {
            try {

                InputStream is;

                if(fromUri){
                    is = new FileInputStream(mFile);
                } else {
                    is = context.getAssets().open("molduras.json");
                }

                Gson gson = new GsonBuilder().create();
                Reader reader = new InputStreamReader(is);

                FrameModelResponse framePrices = gson.fromJson(reader, FrameModelResponse.class);
                is.close();

                return framePrices;
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(FrameModelResponse result) {
            if(result != null) {
                mDataController.saveFramePrices(result);
                GenericViewController.getInstance(context).refreshList();
                Toast.makeText(context,"Molduras atualizadas com sucesso",Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context,"Ocorreu um erro ao salvar seu novo arquivo de Molduras, verifique o JSON e tente novamente",Toast.LENGTH_LONG).show();
            }
            GenericViewController.getInstance(context).hideLoading();
        }


    }
}
