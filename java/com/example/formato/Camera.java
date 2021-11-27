
package com.example.formato;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

/**
 * Created by DeltecSistemas on 21/10/2015.
 */
public class Camera extends Activity {
    private Uri fileUri;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int MEDIA_TYPE_IMAGE = 1;
    private static final String IMAGE_DIRECTORY_NAME = "FotosOrdenesScr";
    int cf_id, ef_id, cuadrilla;
    String cf_tabla_referencia;
    ItemFormato2 item;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////////// INICIO METODOS CAMARA /////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Bundle bolsaR = getIntent().getExtras();
        cf_id = bolsaR.getInt("cf_id");
        ef_id = bolsaR.getInt("ef_id");
        cuadrilla = bolsaR.getInt("cuadrilla");
        cf_tabla_referencia = bolsaR.getString("cf_tabla_referencia");
        item = (ItemFormato2) bolsaR.getSerializable("item");

        captureImage();
    }

    /*
     * Capturing image
     */
    private void captureImage() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                previewCapturedImage();
            }
            else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "User cancelled image capture", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getApplicationContext(),"Sorry! Failed to capture image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*
     * Display image from a path to ImageView
     */
    private void previewCapturedImage() {
        try {
            //imgPreview.setVisibility(View.VISIBLE);
            // bimatp factory
            BitmapFactory.Options options = new BitmapFactory.Options();
            String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            String strMedidor = "MED:";
            String strDatos = "";
            // downsizing image as it throws OutOfMemory Exception for larger
            // images

            options.inSampleSize = 6;
            final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(),options);

            Bitmap fbitmap = bitmap.copy(android.graphics.Bitmap.Config.ARGB_8888, true);
            //options.inJustDecodeBounds = true;
            //Bitmap fbitmap = scaleImage(options,fileUri,800,600);

            File file = new File(fileUri.getPath());
            try {
                FileOutputStream out = new FileOutputStream(file);

                Canvas newCanvas = new Canvas(fbitmap);
                Paint paintText = new Paint();
                paintText.setColor(Color.RED);
                paintText.setTextSize(12);
                paintText.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)); // Text Overlapping Pattern	            // some more settings...
                //paintText.setShadowLayer(10f, 10f, 10f, Color.BLACK);

                newCanvas.drawBitmap(fbitmap, 0, 0, paintText);
                newCanvas.drawText(timeStamp + "   " + strMedidor, 20, 10, paintText);
                paintText.setTextSize(25);
                newCanvas.drawText(strDatos, 20, 33, paintText);
                //newCanvas.drawText(strDatos, 20, 64, paintText);

                fbitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);

                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            //imgPreview.setImageBitmap(bitmap);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        guardarFoto();
    }


    private boolean isDeviceSupportCamera() {
        if(getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        }
        return false;
    }

    public Bitmap scaleImage(BitmapFactory.Options options, Uri uri,int targetWidth, int targetHeight) {

        Bitmap bitmap = null;
        double ratioWidth = ((float) targetWidth) / (float) options.outWidth;
        double ratioHeight = ((float) targetHeight) / (float) options.outHeight;
        double ratio = Math.min(ratioWidth, ratioHeight);
        int dstWidth = (int) Math.round(ratio * options.outWidth);
        int dstHeight = (int) Math.round(ratio * options.outHeight);
        ratio = Math.floor(1.0 / ratio);
        int sample = nearest2pow((int) ratio);

        options.inJustDecodeBounds = false;
        if (sample <= 0) {
            sample = 1;
        }
        options.inSampleSize = (int) sample;
        options.inPurgeable = true;
        try {
            InputStream is;
            is = this.getContentResolver().openInputStream(uri);
            bitmap = BitmapFactory.decodeStream(is, null, options);
            Bitmap bitmap2 = Bitmap.createScaledBitmap(bitmap, dstWidth,
                    dstHeight, true);
            bitmap = bitmap2;
            is.close();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return bitmap;
    }

    public static int nearest2pow(int value) {
        return value == 0 ? 0
                : (32 - Integer.numberOfLeadingZeros(value - 1)) / 2;
    }

    /**
     * Creating file uri to store image/video
     */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /*
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create " + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }
        return mediaFile;
    }

    /**
     * Inserta una URL de la foto en BD
     */
    public void guardarFoto(){

        EjecucionFormatoModel managerFormato = new EjecucionFormatoModel(this);
        managerFormato.open();
        managerFormato.ingresarActualizarRegistroEjecucion(cf_id, ef_id, cuadrilla, cf_tabla_referencia, fileUri.getPath());
        managerFormato.close();
        item.setResultado(fileUri.getPath());

        deleteLatest();

        finish();
    }

    private void deleteLatest() {
        // TODO Auto-generated method stub
        File f = new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera" );

        //Log.i("Log", "file name in delete folder :  "+f.toString());
        File [] files = f.listFiles();

        //Log.i("Log", "List of files is: " +files.toString());
        Arrays.sort(files, new Comparator<Object>() {
            public int compare(Object o1, Object o2) {

                if (((File) o1).lastModified() > ((File) o2).lastModified()) {
                    //         Log.i("Log", "Going -1");
                    return -1;
                } else if (((File) o1).lastModified() < ((File) o2).lastModified()) {
                    //     Log.i("Log", "Going +1");
                    return 1;
                } else {
                    //     Log.i("Log", "Going 0");
                    return 0;
                }
            }
        });

        if(files.length > 0){
            try{
                Log.i("Log", "Count of the FILES AFTER DELETING ::"+files[0].length());
                System.out.println( "BORRO2 FOTO " + files[0].getPath());
                files[0].delete();
            }catch(Exception ex){}
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////////// FIN METODOS CAMARA ////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
}
