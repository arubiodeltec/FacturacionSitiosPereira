package com.example.formato;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.example.gestiondeltec.R;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
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
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class VerFormato extends FragmentActivity implements
        ActionBar.TabListener {

    private ViewPager vPager;
    private TabsAdapter tAdapter;
    private ActionBar aBar;

    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int MEDIA_TYPE_IMAGE = 1;
    private static final String IMAGE_DIRECTORY_NAME = "FotosOrdenesScr";

    private Uri fileUri;
    int ose_codigo = 0, cuadrilla = 0, tipoFoto = 0, ose_precarga = 0;

    private EjecucionFormatoModel manager;

    String tipoProducto;
    int tf_id = 0, ef_id = 0;

    ProgressDialog barProgressDialog;
    Handler updateBarHandler;

    Cursor cursor;
    public boolean banderaEnvio = true;
    //ArrayList<Fragment> mFragments;

    ///private ImageView imgPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orden);
        vPager = (ViewPager) findViewById(R.id.contenedor);

        String mf_nombre;
        Bundle bolsaR = getIntent().getExtras();
        //Bundle bolsarEnviar = new Bundle();
        cuadrilla = bolsaR.getInt("cuadrilla");
        tf_id = bolsaR.getInt("tf_id");
        ef_id = bolsaR.getInt("ef_id");
        //bolsarEnviar.putString("cuadrilla", bolsaR.getString("cuadrilla"));

        manager = new EjecucionFormatoModel(this);
        manager.open();
        cursor = manager.getModulos(tf_id);

        tAdapter = new TabsAdapter(getSupportFragmentManager(), cursor, ef_id, tf_id, cuadrilla);
        aBar = getActionBar();
        vPager.setAdapter(tAdapter);
        aBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        if (cursor.moveToFirst()) {
            do {
                mf_nombre = cursor.getString(cursor.getColumnIndex("nombre"));
                aBar.addTab(aBar.newTab().setText(mf_nombre).setTabListener(this));
            } while (cursor.moveToNext());//accessing data upto last row from table
        }

        vPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                aBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

        updateBarHandler = new Handler();

        manager.close();
        cursor.close();

        //imgPreview = (ImageView) findViewById(R.id.imageViewFoto);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_formato, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_cargar_formato:

                return true;
            case R.id.menu_refrescar_formato:

                return true;
            case R.id.menu_enviar_formato:
                //enviarFormato();
                //sentFormat();
                sentFormatImg2();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////////// INICIO METODOS CAMARA /////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                previewCapturedImage();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "User cancelled image capture", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Sorry! Failed to capture image", Toast.LENGTH_SHORT).show();
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
            timeStamp += " OS:" + ose_precarga;
            String strTipoOrden = "" + tipoProducto;

            // downsizing image as it throws OutOfMemory Exception for larger
            // images

            options.inSampleSize = 6;
            final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);

            Bitmap fbitmap = bitmap.copy(android.graphics.Bitmap.Config.ARGB_8888, true);
            //options.inJustDecodeBounds = true;
            //Bitmap fbitmap = scaleImage(options,fileUri,800,600);

            File file = new File(fileUri.getPath());
            try {
                FileOutputStream out = new FileOutputStream(file);

                Canvas newCanvas = new Canvas(fbitmap);
                Paint paintText = new Paint();
                paintText.setColor(Color.YELLOW);
                paintText.setTextSize(25);
                paintText.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)); // Text Overlapping Pattern	            // some more settings...
                //paintText.setShadowLayer(10f, 10f, 10f, Color.BLACK);

                newCanvas.drawBitmap(fbitmap, 0, 0, paintText);
                newCanvas.drawText(timeStamp, 20, 20, paintText);
                newCanvas.drawText(strTipoOrden, 20, 42, paintText);

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

        if (tipoFoto == 1) {
            cerrarIntent();
        }
    }


    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        }
        return false;
    }

    public Bitmap scaleImage(BitmapFactory.Options options, Uri uri, int targetWidth, int targetHeight) {

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

    /*
     * Capturing image
     */
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
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
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////////// FIN METODOS CAMARA ////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
        //Log.i("ActionBar FRAGMENT", tab.getText() + " reseleccionada.");
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        //Log.i("ActionBar FRAGMENT", tab.getText() + " seleccionada.");
        vPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
        //Log.i("ActionBar FRAGMENT", tab.getText() + " deseleccionada.");
    }

    /**
     * Inserta una URL de la foto en BD
     */
    public void guardarFoto() {
        /*manager.open();
		manager.insertarFoto(ose_codigo, fileUri.getPath());
		manager.close();*/
    }

    private void cerrarIntent() {
        Intent i = new Intent(this, VerFormato.class);
        this.setResult(RESULT_OK, i);
        this.finish();
    }

    @Override
    public void finish() {
        System.gc();
        Intent i = new Intent(this, VerFormato.class);
        this.setResult(RESULT_OK, i);
        super.finish();
        System.out.println("Intenta cerrar la apk");
    }

    public void enviarFormato() {
        //Traer informacion de BD

        //Serializar
        JSONObject jsobject = new JSONObject();
        JSONObject jsobEjecucionFormato = new JSONObject();

        JSONObject jsobEstructura = new JSONObject();
        JSONArray jsarrRegistros = new JSONArray();


        try {
            jsobEstructura.put("estado", 1);
            jsobEstructura.put("calificacion", 1);
            jsobEstructura.put("tipo_formato", tf_id);
            jsobEstructura.put("fecha_creacion", "2015-10-26");

            for (int idx = 1; idx < 5; idx++) {
                JSONObject jsobEstructura1 = new JSONObject();
                JSONObject jsobRespuesta1 = new JSONObject();
                JSONObject jsobEstructuraRespuesta = new JSONObject();
                JSONObject estructuraRegistro = new JSONObject();

                jsobEstructura1.put("campo_formato", idx + 7);
                jsobEstructura1.put("fecha_creacion", "2015-10-26");
                jsobEstructura1.put("hora_creacion", "09:57:57");

                jsobEstructuraRespuesta.put("resultado", 1);
                jsobEstructuraRespuesta.put("fecha_creacion", "2015-10-26");
                jsobEstructuraRespuesta.put("hora_creacion", "09:57:57");

                jsobRespuesta1.put("estructura", jsobEstructuraRespuesta);
                jsobRespuesta1.put("tipo", "RespuestaNumerica");

                estructuraRegistro.put("estructura", jsobEstructura1);
                estructuraRegistro.put("respuesta", jsobRespuesta1);

                jsarrRegistros.put(estructuraRegistro);
            }

            jsobEjecucionFormato.put("estructura", jsobEstructura);
            jsobEjecucionFormato.put("registros", jsarrRegistros);

            jsobject.put("ejecucionformato", jsobEjecucionFormato);

            RestFormatoModel sent = new RestFormatoModel(this);
            sent.enviarRest("jmunoz", "jmunoz", jsobject.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sentFormat() {
        //Traer informacion de BD
        EjecucionFormatoModel manager = new EjecucionFormatoModel(this);
        manager.open();
        Cursor cursor = manager.getEjecucionFormato(ef_id);
        int ef_estado, ef_calificacion, cf_id;
        String ef_fecha_creacion, ef_hora_creacion, re_fecha_creacion, re_hora_creacion;
        String resultado = "", cf_tabla_referencia = "";

        //Serializar
        JSONObject jsobject = new JSONObject();
        JSONObject jsobEjecucionFormato = new JSONObject();
        JSONObject jsobEstructura = new JSONObject();
        JSONArray jsarrRegistros = new JSONArray();

        if (cursor.moveToFirst()) {
            ef_estado = cursor.getInt(cursor.getColumnIndex("ef_estado"));
            ef_fecha_creacion = cursor.getString(cursor.getColumnIndex("ef_fecha_creacion"));
            ef_hora_creacion = cursor.getString(cursor.getColumnIndex("ef_hora_creacion"));
            ef_calificacion = cursor.getInt(cursor.getColumnIndex("ef_calificacion"));
            do {
                JSONObject jsobEstructura1 = new JSONObject();
                JSONObject jsobRespuesta1 = new JSONObject();
                JSONObject jsobEstructuraRespuesta = new JSONObject();
                JSONObject estructuraRegistro = new JSONObject();
                cf_id = cursor.getInt(cursor.getColumnIndex("cf_id"));
                re_fecha_creacion = cursor.getString(cursor.getColumnIndex("re_fecha_creacion"));
                re_hora_creacion = cursor.getString(cursor.getColumnIndex("re_hora_creacion"));
                resultado = "";

                if (!cursor.isNull(cursor.getColumnIndex("rn_resultado"))) {
                    resultado = String.valueOf(cursor.getInt(cursor.getColumnIndex("rn_resultado")));
                    //tc_id = 1;//RESPUESTA NUMERICA
                    cf_tabla_referencia = "RespuestaNumerica";
                    try {
                        jsobEstructuraRespuesta.put("resultado", cursor.getInt(cursor.getColumnIndex("rn_resultado")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (!cursor.isNull(cursor.getColumnIndex("ra_resultado"))) {
                    resultado = String.valueOf(cursor.getInt(cursor.getColumnIndex("ra_resultado")));
                    //tc_id = 2;//RESPUESTA ABIERTA
                    cf_tabla_referencia = "RespuestaAbierta";
                    try {
                        jsobEstructuraRespuesta.put("resultado", cursor.getString(cursor.getColumnIndex("ra_resultado")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (!cursor.isNull(cursor.getColumnIndex("rf_resultado"))) {
                    resultado = String.valueOf(cursor.getInt(cursor.getColumnIndex("rf_resultado")));
                    //tc_id = 5;//RESPUETA FECHA
                    cf_tabla_referencia = "RespuestaFecha";
                    try {
                        jsobEstructuraRespuesta.put("resultado", cursor.getString(cursor.getColumnIndex("rf_resultado")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    jsobEstructura1.put("campo_formato", cf_id);
                    jsobEstructura1.put("fecha_creacion", re_fecha_creacion);
                    jsobEstructura1.put("hora_creacion", re_hora_creacion);

                    //jsobEstructuraRespuesta.put("resultado", resultado);
                    jsobEstructuraRespuesta.put("fecha_creacion", re_fecha_creacion);
                    jsobEstructuraRespuesta.put("hora_creacion", re_hora_creacion);

                    jsobRespuesta1.put("estructura", jsobEstructuraRespuesta);
                    jsobRespuesta1.put("tipo", cf_tabla_referencia);

                    estructuraRegistro.put("estructura", jsobEstructura1);
                    estructuraRegistro.put("respuesta", jsobRespuesta1);

                    jsarrRegistros.put(estructuraRegistro);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());//accessing data upto last row from table

            try {
                jsobEstructura.put("estado", ef_estado);
                jsobEstructura.put("calificacion", ef_calificacion);
                jsobEstructura.put("tipo_formato", tf_id);
                jsobEstructura.put("fecha_creacion", ef_fecha_creacion);
                jsobEstructura.put("hora_creacion", ef_hora_creacion);

                jsobEjecucionFormato.put("estructura", jsobEstructura);
                jsobEjecucionFormato.put("registros", jsarrRegistros);

                jsobject.put("ejecucionformato", jsobEjecucionFormato);

                RestFormatoModel sent = new RestFormatoModel(this);
                sent.enviarRest("jmunoz", "jmunoz", jsobject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        manager.close();
    }


    public void sentFormatImg() {
        //Traer informacion de BD
        EjecucionFormatoModel manager = new EjecucionFormatoModel(this);
        manager.open();
        Cursor cursor = manager.getEjecucionFormato(ef_id);
        String pathImg;

        if (cursor.moveToFirst()) {
            do {

                if (!cursor.isNull(cursor.getColumnIndex("ra_resultado"))) {
                    //tc_id = 2;//RESPUESTA ABIERTA
                    RestFormatoModel sent = new RestFormatoModel(this);
                    sent.enviarRestImgFile("jmunoz", "jmunoz", cursor.getString(cursor.getColumnIndex("ra_resultado")));
                }
            } while (cursor.moveToNext());//accessing data upto last row from table

        }
        manager.close();
    }

    public void sentFormatImg2() {
        //Traer informacion de BD
        EjecucionFormatoModel manager = new EjecucionFormatoModel(this);
        manager.open();
        Cursor cursor = manager.getEjecucionFormato(ef_id);

        //Serializar
        JSONObject jsobject = new JSONObject();

        if (cursor.moveToFirst()) {
            do {
                if (!cursor.isNull(cursor.getColumnIndex("ra_resultado"))) {
                    try {
                        //jsobEstructuraRespuesta.put("resultado", cursor.getString(cursor.getColumnIndex("ra_resultado")));
                        File myFile = new File(cursor.getString(cursor.getColumnIndex("ra_resultado")));
                        //jsobject.put("archivo",convertFileToByteArray(myFile));

                        RestFormatoModel sent1 = new RestFormatoModel(this);
                        sent1.enviarRestImgFile("jmunoz", "jmunoz", cursor.getString(cursor.getColumnIndex("ra_resultado")));

                        jsobject.put("archivo",23432432);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } while (cursor.moveToNext());//accessing data upto last row from table

            //RestFormatoModel sent = new RestFormatoModel(this);
            //sent.enviarRestImg("jmunoz", "jmunoz", jsobject.toString());
        }
        manager.close();
    }

    public static byte[] convertFileToByteArray(File f)
    {
        byte[] byteArray = null;
        try
        {
            InputStream inputStream = new FileInputStream(f);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024*8];
            int bytesRead =0;

            while ((bytesRead = inputStream.read(b)) != -1)
            {
                bos.write(b, 0, bytesRead);
            }

            byteArray = bos.toByteArray();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return byteArray;
    }

}
