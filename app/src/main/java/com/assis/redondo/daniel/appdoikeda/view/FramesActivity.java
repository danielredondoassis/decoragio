package com.assis.redondo.daniel.appdoikeda.view;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
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
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.assis.redondo.daniel.appdoikeda.R;
import com.assis.redondo.daniel.appdoikeda.api.response.FrameModelResponse;
import com.assis.redondo.daniel.appdoikeda.data.DataController;
import com.assis.redondo.daniel.appdoikeda.data.GenericViewController;
import com.assis.redondo.daniel.appdoikeda.utils.dispatcher.DataEvent;
import com.assis.redondo.daniel.appdoikeda.utils.dispatcher.Event;
import com.assis.redondo.daniel.appdoikeda.utils.dispatcher.EventListener;
import com.assis.redondo.daniel.appdoikeda.view.adapter.FrameAdapter;
import com.assis.redondo.daniel.appdoikeda.view.adapter.SearchFrameAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FramesActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PICK_FILE = 1;
    private static final int ACTIVITY_CHOOSE_FILE = 5;

    @Bind(R.id.searchRecycler)
    RecyclerView searchRecycler;
    @Bind(R.id.frameRecycler)
    RecyclerView frameRecycler;
    private FrameAdapter mAdapter;
    private SearchFrameAdapter mSearchAdapter;
    private LinearLayoutManager mLayoutManager;
    private LinearLayoutManager mSearchLayoutManager;

    private String[] mPerms = {"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};
    private File mFile;
    private DataController mDataController;
    private AlertDialog.Builder mFillFormDialog;
    private UpdateFileType mUpdateFileType;
    private ProgressDialog mLoadingDialog;

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults){

        switch(permsRequestCode){
            case 1:
                boolean readAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                mPerms[0] = "0";
                mPerms[1] = "0";
                if(readAccepted && cameraAccepted) updateFile(mUpdateFileType);
                break;
        }
    }

    public void updateFile(UpdateFileType type){
        showLoading();
        if(type == UpdateFileType.MOLDURAS){
            mDataController.loadFrameFromFile(mFile);
        } else if (type == UpdateFileType.INSUMOS){
            mDataController.loadInsumosFromFile(mFile);
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

                mFillFormDialog.setPositiveButton("INSUMOS", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mUpdateFileType = UpdateFileType.INSUMOS;
                        openFileChooserIntent();
                        dialog.dismiss();
                        mFillFormDialog = null;
                    }
                });

                mFillFormDialog.setNeutralButton("CANCELAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        mFillFormDialog = null;
                    }
                });

                mFillFormDialog.setNegativeButton("MOLDURAS", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mUpdateFileType = UpdateFileType.MOLDURAS;
                        openFileChooserIntent();
                        dialog.dismiss();
                        mFillFormDialog = null;
                    }
                });

                mFillFormDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        mFillFormDialog = null;
                    }
                });

                mFillFormDialog.show();
            }
        }
    }

    private void openFileChooserIntent() {
        try {
            Intent mediaIntent = new Intent(Intent.ACTION_GET_CONTENT);
            mediaIntent.setType("*/*"); //set mime type as per requirement
            startActivityForResult(mediaIntent, ACTIVITY_CHOOSE_FILE);
        } catch (Exception e){
            e.printStackTrace();
        }
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;
        String path     = "";
        if(requestCode == ACTIVITY_CHOOSE_FILE) {
            Uri uri = data.getData();
            String FilePath = getPath(this,uri); // should the path be here in this string
            System.out.print("Path  = " + FilePath);

            mFile = new File(FilePath);

            if(mFile.exists()) {

                if(ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) == 0 &&
                        ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == 0){
                    updateFile(mUpdateFileType);
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
            }
        }
    }


    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
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
