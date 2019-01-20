package com.assis.redondo.daniel.appdoikeda.view;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.assis.redondo.daniel.appdoikeda.R;
import com.assis.redondo.daniel.appdoikeda.data.DataController;
import com.assis.redondo.daniel.appdoikeda.data.GenericViewController;
import com.assis.redondo.daniel.appdoikeda.utils.FileUtil;
import com.assis.redondo.daniel.appdoikeda.utils.dispatcher.DataEvent;
import com.assis.redondo.daniel.appdoikeda.utils.dispatcher.Event;
import com.assis.redondo.daniel.appdoikeda.utils.dispatcher.EventListener;
import com.assis.redondo.daniel.appdoikeda.view.adapter.FrameAdapter;
import com.assis.redondo.daniel.appdoikeda.view.adapter.SearchFrameAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;


public class FramesActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PICK_FILE = 1;
    private static final int ACTIVITY_CHOOSE_FILE = 5;
    public static final int PERMISSIONS_REQUEST = 100;

    @BindView(R.id.searchRecycler)
    RecyclerView searchRecycler;
    @BindView(R.id.frameRecycler)
    RecyclerView frameRecycler;
    private FrameAdapter mAdapter;
    private SearchFrameAdapter mSearchAdapter;
    private LinearLayoutManager mLayoutManager;
    private LinearLayoutManager mSearchLayoutManager;

    private DataController mDataController;
    private AlertDialog.Builder mFillFormDialog;
    private UpdateFileType mUpdateFileType;
    private ProgressDialog mLoadingDialog;

    public void updateFile(File file, UpdateFileType type){
        showLoading();
        if(type == UpdateFileType.MOLDURAS){
            mDataController.loadFrameFromFile(file);
        } else if (type == UpdateFileType.INSUMOS){
            mDataController.loadInsumosFromFile(file);
        }
    }

    enum UpdateFileType{
        INSUMOS,
        MOLDURAS;
    }

    private EventListener hideLoadingListener = new EventListener() {

        @Override
        public void onEvent(Event event) {
            hideLoading();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frames);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Molduras");

        mDataController = DataController.getInstance(this);

        mAdapter = new FrameAdapter(this, this);
        mSearchAdapter = new SearchFrameAdapter(this, this);
        mLayoutManager = new LinearLayoutManager(this);
        mSearchLayoutManager = new LinearLayoutManager(this);

        frameRecycler.setLayoutManager(mLayoutManager);
        frameRecycler.setHasFixedSize(true);
        frameRecycler.setAdapter(mAdapter);

        searchRecycler.setAdapter(mSearchAdapter);
        searchRecycler.setLayoutManager(mSearchLayoutManager);
        searchRecycler.setHasFixedSize(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                showInputInfoDialog();
            }
        });

        fab.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        GenericViewController.getInstance(this).removeListener(DataEvent.HIDE_LOADING, hideLoadingListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        GenericViewController.getInstance(this).addListener(DataEvent.HIDE_LOADING, hideLoadingListener);
    }

    public void showInputInfoDialog() {
        final Context ctx = this;
        if (ctx != null) {
            if (mFillFormDialog == null) {
                mFillFormDialog = new AlertDialog.Builder(ctx);
                mFillFormDialog.setTitle("Atenção");
                mFillFormDialog.setMessage("Deseja atualizar os valores de insumos ou molduras? Selecione abaixo:");

                mFillFormDialog.setPositiveButton("INSUMOS", (dialog, which) -> {
                    mUpdateFileType = UpdateFileType.INSUMOS;
                    checkPermissions(ACTIVITY_CHOOSE_FILE);
                    dialog.dismiss();
                    mFillFormDialog = null;
                });

                mFillFormDialog.setNeutralButton("CANCELAR", (dialog, which) -> {
                    dialog.dismiss();
                    mFillFormDialog = null;
                });

                mFillFormDialog.setNegativeButton("MOLDURAS", (dialog, which) -> {
                    mUpdateFileType = UpdateFileType.MOLDURAS;
                    checkPermissions(ACTIVITY_CHOOSE_FILE);
                    dialog.dismiss();
                    mFillFormDialog = null;
                });

                mFillFormDialog.setOnDismissListener(dialog -> mFillFormDialog = null);

                mFillFormDialog.show();
            }
        }
    }

    public String[] UPLOAD_PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public void checkPermissions(int type) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasPermissions(this, UPLOAD_PERMISSIONS)) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE) ||
                        ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    askPermissions();
                } else {
                    requestPermissions(UPLOAD_PERMISSIONS, PERMISSIONS_REQUEST);
                }
            } else {
                if(type == ACTIVITY_CHOOSE_FILE) {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("*/*");
                    startActivityForResult(intent, ACTIVITY_CHOOSE_FILE);
                }
            }
        } else {
            if(type == ACTIVITY_CHOOSE_FILE) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                startActivityForResult(intent, ACTIVITY_CHOOSE_FILE);
            }
        }
    }

    private void askPermissions() {
        showBasicAlertWithAction(getResources().getString(R.string.dialog_permission_title), getResources().getString(R.string.upload_permission_message));
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        for (String permission : permissions)
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
                return false;
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_frames, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.show_prices) {
            mAdapter.setPricesFlag(false);
            mAdapter.notifyDataSetChanged();
            return true;
        } else if (id == R.id.hide_prices) {
            mAdapter.setPricesFlag(true);
            mAdapter.notifyDataSetChanged();
            return true;
        } else if(id == android.R.id.home){
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST: {
                if (grantResults.length == 2) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                            intent.addCategory(Intent.CATEGORY_OPENABLE);
                            intent.setType("*/*");
                            startActivityForResult(intent, ACTIVITY_CHOOSE_FILE);
                    } else if (grantResults[0] == PackageManager.PERMISSION_DENIED || grantResults[1] == PackageManager.PERMISSION_DENIED){
                        String permission = permissions[0];

                        boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, permission );

                        if ((!showRationale && (Manifest.permission.READ_EXTERNAL_STORAGE.equals(permission) || Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permission))))
                            showReinforceAlertWithAction(getResources().getString(R.string.dialog_permission_title), getResources().getString(R.string.upload_permission_denial));
                    }
                }
            }
        }
    }

    public void showBasicAlertWithAction(String title, String message) {
        final AlertDialog alert = new AlertDialog.Builder(this)
                .setTitle(title)
                .setCancelable(true)
                .setMessage(message)
                .create();

        alert.setButton(AlertDialog.BUTTON_POSITIVE, "OK", (dialog, which) -> {
            requestPermissions(UPLOAD_PERMISSIONS, PERMISSIONS_REQUEST);
            alert.dismiss();
        });

        alert.show();
    }

    public void showReinforceAlertWithAction(String title, String message) {
        final AlertDialog alert = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .create();

        alert.setButton(AlertDialog.BUTTON_POSITIVE, "SIM", (dialog, which) -> {
            alert.dismiss();
        });

        alert.setButton(AlertDialog.BUTTON_NEGATIVE, "NÃO", (dialog, which) -> {
            requestPermissions(UPLOAD_PERMISSIONS, PERMISSIONS_REQUEST);
            alert.dismiss();
        });
        alert.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ACTIVITY_CHOOSE_FILE) {
            if (data != null && data.getData() != null) {
                File file = FileUtil.getFileForUri(FramesActivity.this, data.getData());
                if (file != null && file.length() > 0 && file.exists()) {
                    updateFile(file,mUpdateFileType);
                } else {
                    showBasicAlert("Atenção", "Ocorreu um erro ao selecionar seu novo arquivo de moldura/insumo, tente novamente.");
                }
            }
        }
    }

    protected void showBasicAlert(String title, String message) {
        final AlertDialog alert = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .create();

        alert.setButton(AlertDialog.BUTTON_POSITIVE, "OK", (dialog, which) -> alert.dismiss());
        alert.show();
    }

    public void showLoading() {
        runOnUiThread(new TimerTask() {
            @Override
            public void run() {
                if (mLoadingDialog == null) {
                    mLoadingDialog = ProgressDialog.show(FramesActivity.this, getString(R.string.loading_title), getString(R.string.loading_message), true);
                    mLoadingDialog.setCancelable(false);
                } else {
                    mLoadingDialog.show();
                }
            }
        });

    }

    public void hideLoading() {
        runOnUiThread(new TimerTask() {
            @Override
            public void run() {
                if (mLoadingDialog != null) {
                    mLoadingDialog.dismiss();
                }
            }
        });

    }

}
