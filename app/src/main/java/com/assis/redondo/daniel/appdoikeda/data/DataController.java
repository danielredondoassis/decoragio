package com.assis.redondo.daniel.appdoikeda.data;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.assis.redondo.daniel.appdoikeda.api.loader.FrameAndInsumoLoader;
import com.assis.redondo.daniel.appdoikeda.api.model.FrameModel;
import com.assis.redondo.daniel.appdoikeda.api.model.InsumoModel;
import com.assis.redondo.daniel.appdoikeda.api.model.ProjectModel;
import com.assis.redondo.daniel.appdoikeda.api.response.FrameModelResponse;
import com.assis.redondo.daniel.appdoikeda.data.db.DatabaseHelper;
import com.assis.redondo.daniel.appdoikeda.data.db.DbFrameModel;
import com.assis.redondo.daniel.appdoikeda.data.db.DbInsumoModel;
import com.assis.redondo.daniel.appdoikeda.utils.dispatcher.EventDispatcher;
import com.j256.ormlite.dao.Dao;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DataController extends EventDispatcher {

    private static DataController instance;
    private Dao<DbInsumoModel, Integer> mInsumosDao;
    private Dao<DbFrameModel, Integer> mFramePricesDao;
    private DatabaseHelper dbHelper;

    private Activity mCtx;
    private BigDecimal mHeight = new BigDecimal(0);
    private BigDecimal mWidth = new BigDecimal(0);
    private ProjectModel mProjectModel;
    private static final String TAG = DataController.class.getSimpleName();
    private DbFrameModel mSelectedFrame;

    public BigDecimal getProjectTotal() {
        return projectTotal;
    }

    private BigDecimal projectTotal;

    public static synchronized DataController getInstance(Activity activity) {
        if (instance == null) {
            instance = new DataController(activity);
        }
        return instance;
    }

    public DataController(Activity ctx) {
        this.mCtx = ctx;
        this.mProjectModel = new ProjectModel();
        this.dbHelper = new DatabaseHelper(mCtx);
        this.projectTotal = new BigDecimal(0);

        try {

            mFramePricesDao = dbHelper.getFramePrices();
            mInsumosDao = dbHelper.getInsumos();
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    public ArrayList<DbFrameModel> getFrameModels() {
        try {
            List<DbFrameModel> frameModels = mFramePricesDao.queryForAll();
            if (frameModels != null && frameModels.size() > 0) {
                return new ArrayList<DbFrameModel>(frameModels);
            } else {
                return null;
            }
        } catch (SQLException e) {
            return null;
        }
    }

    public ArrayList<DbInsumoModel> getInsumos() {
        try {
            List<DbInsumoModel> insumos = mInsumosDao.queryForAll();
            if (insumos != null && insumos.size() > 0) {
                return new ArrayList<DbInsumoModel>(insumos);
            } else {
                return null;
            }
        } catch (SQLException e) {
            return null;
        }
    }



    public BigDecimal getSquareMeterAmount(){
        // Metro_Quadrado - (altura/100)*(largura/100)
        try{
            BigDecimal squareMeterAmount;
            BigDecimal divisor = new BigDecimal("100");
            squareMeterAmount = (mHeight.divide(divisor,2,RoundingMode.CEILING)).multiply(mWidth.divide(divisor, 2, RoundingMode.CEILING));

            Log.v("SquarteMeterAmount: ", squareMeterAmount.toString());
            return squareMeterAmount;

        } catch (Exception e){
            e.printStackTrace();
        }
        return new BigDecimal(0);
    }

    public BigDecimal getLinearMeterAmount(){
        // Metro_Linear - ((altura+largura)*2)/100
        try{
            BigDecimal linearMeterAmount;
            BigDecimal multiplicand = new BigDecimal("2");
            BigDecimal divisor = new BigDecimal("100");
            linearMeterAmount = mHeight.add(mWidth).multiply(multiplicand).divide(divisor);

            Log.v("linearMeterAmount: ", linearMeterAmount.toString());

            return linearMeterAmount;


        } catch (Exception e){
            e.printStackTrace();
        }
        return new BigDecimal(0);
    }

    public BigDecimal getGlassValue(){
        // Metro_Quadrado*Tabela_de_valores(campo Vidro) - se a operação for menor que R$ 10 - Definir o valor do campo para 10

        try {
            BigDecimal multiplicand = new BigDecimal("10");
            BigDecimal insumosMultiplicand = new BigDecimal(getInsumos().get(0).getGlassValue());

            BigDecimal result = getSquareMeterAmount().multiply(insumosMultiplicand);
            if(result.intValue() < 10){
                return multiplicand;
            } else {
                return result;
            }


        } catch (Exception e){
            e.printStackTrace();
        }
        return new BigDecimal(0);
    }

    public BigDecimal getBackgroundValue(){
        // Metro_Quadrado*Tabela_de_valores(campo Fundo) -se a operação for menor que R$ 10 - Definir o valor do campo para 10

        try {
            BigDecimal multiplicand = new BigDecimal("10");
            BigDecimal insumosMultiplicand = new BigDecimal(getInsumos().get(0).getBackgroundValue());

            BigDecimal result = getSquareMeterAmount().multiply(insumosMultiplicand);
            if (result.intValue() < 10) {
                return multiplicand;
            } else {
                return result;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return new BigDecimal(0);
    }

    public BigDecimal getCollageValue(){
        // Metro_Quadrado*Tabela_de_valores(campo Colagem)

        try {
            BigDecimal insumosMultiplicand = new BigDecimal(getInsumos().get(0).getCollageValue());
            BigDecimal result = getSquareMeterAmount().multiply(insumosMultiplicand);

            Log.v("CollageValue: ", result.toString());

            return result;
        } catch (Exception e){
            e.printStackTrace();
        }
        return new BigDecimal(0);
    }

    public BigDecimal getPassepartoutValue(){
        // se input Passepartout=preenchido;
        // Metro_Quadrado*Tabela_de_valores(campo Vidro) - se a operação for menor que R$ 20 - Definir o valor do campo para 20

        try {
            BigDecimal multiplicand = new BigDecimal("20");
            BigDecimal insumosMultiplicand = new BigDecimal(getInsumos().get(0).getPasspartoutValue());

            BigDecimal result = getSquareMeterAmount().multiply(insumosMultiplicand);
            if(result.intValue() < 20){
                return multiplicand;
            } else {
                return result;
            }

        } catch (Exception e){
            e.printStackTrace();
        }
        return new BigDecimal(0);
    }

    public void setHeight(BigDecimal height) {
        this.mHeight = height;
    }

    public void setWidth(BigDecimal width){
        this.mWidth = width;

    }

    public BigDecimal calculateProject(){
        /* Soma dos Campos: Valor da Moldura, Valor Do Vidro, Valor Do fundo, Valor da Colagem; Valor do Passepartout ****
         **** A soma dos campos está ligada ao valor de preenchimento do form,
         **** ou seja se o campo vidro estiver setado como não ele não deve fazer parte da soma */
        this.projectTotal = new BigDecimal(0);
        setHeight(mProjectModel.getHeight());
        setWidth(mProjectModel.getWidth());
        if(mProjectModel.getFrameValue() != null) projectTotal = projectTotal.add(mProjectModel.getFrameValue().multiply(getLinearMeterAmount()));
        if(mProjectModel.isBackgroundOption()) projectTotal = projectTotal.add(getBackgroundValue());
        if(mProjectModel.isCollageOption()) projectTotal = projectTotal.add(getCollageValue());
        if(mProjectModel.isGlassOption()) projectTotal = projectTotal.add(getGlassValue());
        if(mProjectModel.getPassePartout() != null && !mProjectModel.getPassePartout().contentEquals("")) projectTotal = projectTotal.add(getPassepartoutValue());

        return projectTotal.setScale(2, RoundingMode.CEILING);

    }

    public ProjectModel getProjectModel() {
        return this.mProjectModel;
    }

    public void saveInsumos(InsumoModel result) {
        try {
            DbInsumoModel insumos = new DbInsumoModel();
            insumos.setInsumoId(1337);
            insumos.setBackgroundValue(result.getBackground());
            insumos.setCollageValue(result.getCollage());
            insumos.setGlassValue(result.getGlass());
            insumos.setPasspartoutValue(result.getPasspartout());
            mInsumosDao.createOrUpdate(insumos);
        } catch (Exception e){
            e.printStackTrace();
            Toast.makeText(mCtx,"Ocorreu um erro ao salvar seu novo arquivo de Insumos, verifique o JSON e tente novamente",Toast.LENGTH_LONG).show();
        }
    }

    public void saveFramePrices(FrameModelResponse result) {

        try {
            for(FrameModel vo : result.getResponse()) {
                DbFrameModel frame = new DbFrameModel();
                frame.setFrameId(vo.getFrameCode());
                frame.setFrameValue(vo.getFrameValue());
                mFramePricesDao.createOrUpdate(frame);
            }
        } catch (Exception e){
            e.printStackTrace();
            Toast.makeText(mCtx,"Ocorreu um erro ao salvar seu novo arquivo de Molduras, verifique o JSON e tente novamente",Toast.LENGTH_LONG).show();
        }

    }

    public void loadFramesAndInsumos() {
        FrameAndInsumoLoader loader = new FrameAndInsumoLoader();
        loader.init(mCtx);
    }

    public void setSelectedFrameId(DbFrameModel frame) {
        this.mSelectedFrame = frame;
        if(frame != null) this.mProjectModel.setFrame(new BigDecimal(frame.getFrameValue()));
    }

    public String getSelectedFrameId() {

        if(mSelectedFrame != null){
            if(mSelectedFrame.getFrameId() != null) {
                return mSelectedFrame.getFrameId();
            }
        }
        return null;
    }

    public void loadFrameFromFile(File file) {
        FrameAndInsumoLoader loader = new FrameAndInsumoLoader();
        loader.loadFramesFromFile(mCtx, file);
    }

    public void loadInsumosFromFile(File file) {
        FrameAndInsumoLoader loader = new FrameAndInsumoLoader();
        loader.loadInsumosFromFile(mCtx, file);
    }
}
