package com.witgoedxpert.technician.Helper;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;

import androidx.core.content.FileProvider;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {

    public static Bitmap getPictureFromPath(String currentPhotoPath, int requiredSize) {
        try {
            // Get the dimensions of the bitmap
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;

            BitmapFactory.decodeFile(currentPhotoPath, o);

            int scale = 1;

            while (o.outWidth / scale > requiredSize || o.outHeight / scale > requiredSize) {
                scale *= 2;
            }

            // Decode the image file into a Bitmap sized to fill the View
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;

            return BitmapFactory.decodeFile(currentPhotoPath, o2);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap decodeIntentData(Uri data, int requiredSize) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            ContentResolver cr = UIUtils.getAppContext().getContentResolver();
            BitmapFactory.decodeStream(cr.openInputStream(data), null, o);

            o.inSampleSize = calculateInSampleSize(o, requiredSize, requiredSize);
            o.inJustDecodeBounds = false;

            return BitmapFactory.decodeStream(cr.openInputStream(data), null, o);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Bitmap decodeByteArray(byte[] data, int requiredSize) {
        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        ContentResolver cr = UIUtils.getAppContext().getContentResolver();
        BitmapFactory.decodeByteArray(data, 0, data.length, o);

        o.inSampleSize = calculateInSampleSize(o, requiredSize, requiredSize);
        o.inJustDecodeBounds = false;

        return BitmapFactory.decodeByteArray(data, 0, data.length, o);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static File createCachedFileToShare(Bitmap bitmap) {
        File imagesFolder = new File(UIUtils.getAppContext().getCacheDir(), "images");
        try {
            imagesFolder.mkdirs();
            File file = new File(imagesFolder, "shared_image.png");

            FileOutputStream stream = new FileOutputStream(file); // overwrites this image every time
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
            stream.flush();
            stream.close();

            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Uri getUri(File file) {
        Context context = UIUtils.getAppContext();
        if (file != null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                return Uri.fromFile(file);
            }
            return FileProvider.getUriForFile(context,
                    context.getPackageName() + ".provider", file);
        }
        return null;
    }

    public static File createEmptyFile(String fileName, String extension) {
        File root = UIUtils.getAppContext().getExternalCacheDir();
        File myDir = null;
        try {
            myDir = File.createTempFile(fileName, extension, root);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return myDir;
    }

    public static Bitmap processExif(String photoPath, Bitmap bitmap) {
        ExifInterface ei;

        try {
            ei = new ExifInterface(photoPath);

            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    bitmap = rotateImage(bitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    bitmap = rotateImage(bitmap, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    bitmap = rotateImage(bitmap, 270);
                    break;
                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    break;
            }
        } catch (IOException e) {
            Log.d("FileUtils", "Exif not found");
            e.printStackTrace();
        }

        return bitmap;
    }

    private static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    public static File getFileFromUri(final Context context, final Uri uri) throws Exception {

        String path = null;

        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (DocumentsContract.isDocumentUri(context, uri)) { // TODO: 2015. 11. 17. KITKAT


                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];


                    if ("primary".equalsIgnoreCase(type)) {
                        path = Environment.getExternalStorageDirectory() + "/" + split[1];
                    }

                    // TODO handle non-primary volumes

                } else if (isDownloadsDocument(uri)) { // DownloadsProvider

                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    path = getDataColumn(context, contentUri, null, null);

                } else if (isMediaDocument(uri)) { // MediaProvider


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
                    final String[] selectionArgs = new String[]{
                            split[1]
                    };

                    path = getDataColumn(context, contentUri, selection, selectionArgs);

                } else if (isGoogleDrive(uri)) { // Google Drive
                    String TAG = "isGoogleDrive";
                    path = TAG;
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(";");
                    final String acc = split[0];
                    final String doc = split[1];

                    /*
                     * @details google drive document data. - acc , docId.
                     * */

                    return saveFileIntoExternalStorageByUri(context, uri);


                } // MediaStore (and general)
            } else if ("content".equalsIgnoreCase(uri.getScheme())) {
                path = getDataColumn(context, uri, null, null);
            }
            // File
            else if ("file".equalsIgnoreCase(uri.getScheme())) {
                path = uri.getPath();
            }

            return new File(path);
        } else {

            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            return new File(cursor.getString(cursor.getColumnIndex("_data")));
        }

    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is GoogleDrive.
     */

    public static boolean isGoogleDrive(Uri uri) {
        return uri.getAuthority().equalsIgnoreCase("com.google.android.apps.docs.storage");
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
        final String column = MediaStore.Images.Media.DATA;
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
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


    public static File makeEmptyFileIntoExternalStorageWithTitle(String title) {
        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        return new File(root, title);
    }


    public static String getFileName(Context context, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }


    public static void saveBitmapFileIntoExternalStorageWithTitle(Bitmap bitmap, String title) throws Exception {

        FileOutputStream fileOutputStream = new FileOutputStream(makeEmptyFileIntoExternalStorageWithTitle(title + ".png"));
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
        fileOutputStream.close();
    }


    public static File saveFileIntoExternalStorageByUri(Context context, Uri uri) throws Exception {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        int originalSize = inputStream.available();

        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        String fileName = getFileName(context, uri);
        File file = makeEmptyFileIntoExternalStorageWithTitle(fileName);
        bis = new BufferedInputStream(inputStream);
        bos = new BufferedOutputStream(new FileOutputStream(
                file, false));

        byte[] buf = new byte[originalSize];
        bis.read(buf);
        do {
            bos.write(buf);
        } while (bis.read(buf) != -1);

        bos.flush();
        bos.close();
        bis.close();

        return file;

    }
    public static String getPath(Uri uri, Context context) {
        String result = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(proj[0]);
                result = cursor.getString(column_index);
            }
            cursor.close();
        }
        if (result == null) {
            result = "Not found";
        }
        return result;
    }

}
