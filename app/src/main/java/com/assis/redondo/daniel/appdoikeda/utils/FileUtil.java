package com.assis.redondo.daniel.appdoikeda.utils;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.util.List;

/**
 * Created by rogerioshimizu on 10/26/15.
 */
public class FileUtil {

    public static void copyFile(File origin, File destination) throws IOException {

        FileInputStream inStream = new FileInputStream(origin);
        FileOutputStream outStream = new FileOutputStream(destination);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();

    }

    public static String getMimeType(File file) {
        try {
            Uri uri = Uri.fromFile(file);
            String type = null;
            String extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
            if (extension != null) {
                type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            }
            return type;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getImagePathFromGalleryUri(Context context, Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }

    public static File createTempReceiptFile(Context context, byte[] imageBytes) {
        try {
            if (imageBytes != null) {
                File file = null;
                try {
                    file = FileUtil.createImageFile(context);
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                    bos.write(imageBytes);
                    bos.flush();
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return file != null ? file.length() > 0 && file.exists() ? file : null : null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static File createImageFile(Context context) throws IOException {
        // Create an image file name

        String imageFileName = "CURRENT_IMAGE";
        File storageDir = context.getExternalFilesDir(null);
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
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

    public static File getFileForUri(Context context, Uri uri) {

        InputStream is = null;
        FileOutputStream os = null;
        String fullPath = null;

        try {
            String scheme = uri.getScheme();
            String name = null;

            if (scheme.equals("file")) {
                List<String> pathSegments = uri.getPathSegments();
                if (pathSegments.size() > 0) {
                    name = pathSegments.get(pathSegments.size() - 1);
                }
            } else if (scheme.equals("content")) {
                Cursor cursor = context.getContentResolver().query(uri, new String[]{
                        MediaStore.MediaColumns.DISPLAY_NAME
                }, null, null, null);
                cursor.moveToFirst();
                int nameIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME);
                if (nameIndex >= 0) {
                    name = cursor.getString(nameIndex);
                }
            } else {
                return null;
            }

            if (name == null) return null;

            int n = name.lastIndexOf(".");
            String fileName, fileExt;

            if (n == -1) {
                return null;
            } else {
                fileName = name.substring(0, n);
                fileExt = name.substring(n);
            }

            File storageDir = context.getExternalFilesDir(null);
            if (!storageDir.exists()) storageDir.mkdirs();
            File file = File.createTempFile(
                    fileName,  /* prefix */
                    fileExt,         /* suffix */
                    storageDir      /* directory */
            );

            fullPath = file.getPath();

            is = context.getContentResolver().openInputStream(uri);
            os = new FileOutputStream(fullPath);

            byte[] buffer = new byte[4096];
            int count;
            while ((count = is.read(buffer)) > 0) {
                os.write(buffer, 0, count);
            }
            os.close();
            is.close();

            if (file != null && file.length() > 0 && file.exists()) {
                return file;
            } else {
                return null;
            }

        } catch (Exception e) {
            if (is != null) try {
                is.close();
            } catch (Exception e1) {
            }
            if (os != null) try {
                os.close();
            } catch (Exception e1) {
            }
            if (fullPath != null) {
                File f = new File(fullPath);
                f.delete();
            }
            return null;
        }
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
}
