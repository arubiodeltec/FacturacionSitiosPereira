package com.example.verorden;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.os.Looper;
import android.os.Parcelable;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.example.Logueo.DataBaseManager;
import com.example.config.GestionMedidorEncontrado;
import com.example.gestionOrdenes.EjecucionOrdenes;
import com.example.gestiondeltec.R;
import com.example.lectura.VerLecturaModel;
import com.zebra.sdk.comm.BluetoothConnectionInsecure;
import com.zebra.sdk.comm.Connection;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class VerOrden extends FragmentActivity implements
        ActionBar.TabListener, VerLecturaFragment.FragmentIterationListenerVerLectura {

    private ViewPager vPager = null;
    private TabsAdapter tAdapter = null;
    //private ActionBar aBar;

    private TextView tvUsuario, tvTotalOrdenes, tvTotalEjectudadas, tvTotalPendientes, tvTotalEnviadas, tvDatosTrabajo, tvTituloEstado;
    private EditText etBuscar;
    private Button btBuscar;
    private Spinner spOrdenEjecucion, spOrdenEstado;

    private static final int REQUEST_ENABLE_BT = 0;
    Connection thePrinterConn;

    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int MEDIA_TYPE_IMAGE = 1;
    private static final String IMAGE_DIRECTORY_NAME = "FotosOrdenesScr";

    private Uri fileUri;
    int ose_codigo = 0, cuadrilla = 0, tipoFoto = 0, terminal = 0;
    String strRuta = "", strCiclo = "";

    private DataBaseManager manager;
    private VerOrdenManager managerVerOrden;

    String tipoProducto, nombreCuadrilla, strBuscar = "";
    int indiceInicial = 0, indiceObjetivo = 0;

    Handler updateBarHandler;

    List<Bundle> ordenes = new ArrayList<Bundle>();
    Cursor cursor;

    int fotosTomadas = 0, limiteFotos = 1, tipoUsuario = 1;
    Boolean debeTomarFotosAdd = false;

    String medidor  = "", strCausa = "", theBtMacAddress ="";
    int indicador_lectura = 1, lectura_actual = 0, critica = 2, currentItem = 0;
    boolean avanzar = false;
    FragmentManager fm = getSupportFragmentManager();
    boolean salidaActivity = false;
    Bundle bolsaR;
    int     estado = 0, orden = 0,
            ose_codigo_aux = 0, cli_contrato_aux = 0;

    String MODEL = "", IMEI = "";
    double VERSION;

    ///private ImageView imgPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orden);

        vPager = (ViewPager) findViewById(R.id.contenedor);
        etBuscar = (EditText) findViewById(R.id.editTextTextoBuscar);
        tvUsuario = (TextView) findViewById(R.id.tvSegNombreCuadrilla);
        tvTotalOrdenes = (TextView) findViewById(R.id.tvRepTotalOrdenes);
        tvTotalEjectudadas = (TextView) findViewById(R.id.tvRepTotalEnviadas);
        tvTotalPendientes = (TextView) findViewById(R.id.tvRepTotalPendientes);
        tvTotalEnviadas = (TextView) findViewById(R.id.tvTotalEnviadas);
        tvDatosTrabajo = (TextView) findViewById(R.id.tvDatosTrabajo);
        tvTituloEstado = (TextView) findViewById(R.id.tvTituloEstado);
        spOrdenEjecucion = (Spinner) findViewById(R.id.spOrdenEjecucion);
        spOrdenEstado = (Spinner) findViewById(R.id.spOrdenEstado);
        btBuscar = (Button) findViewById(R.id.buttonBuscarOrden);
        btBuscar.setBackgroundResource(android.R.drawable.btn_default);

        bolsaR = getIntent().getExtras();

        if(bolsaR != null) {
            cuadrilla = bolsaR.getInt("cuadrilla");
            tipoProducto = bolsaR.getString("producto");
            tipoUsuario = bolsaR.getInt("tipo");
            theBtMacAddress = bolsaR.getString("theBtMacAddress");

            nombreCuadrilla = bolsaR.getString("nombreKey");
            if (nombreCuadrilla.length() > 10)
                nombreCuadrilla = nombreCuadrilla.substring(0, 10);

            tvUsuario.setText(nombreCuadrilla);
            terminal = bolsaR.getInt("terminalKey");
            estado = bolsaR.getInt("estado");
            orden = bolsaR.getInt("orden");

            MODEL = bolsaR.getString("MODEL");
            IMEI = bolsaR.getString("IMEI");
            VERSION = bolsaR.getDouble("VERSION");
        }

        manager = new DataBaseManager(this);
        managerVerOrden = new VerOrdenManager(this);

        ArrayAdapter<CharSequence> adapterOrdenEjecucion = ArrayAdapter.createFromResource(this, R.array.orden_ejecucion, android.R.layout.simple_spinner_item);
        adapterOrdenEjecucion.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spOrdenEjecucion.setAdapter(adapterOrdenEjecucion);

        ArrayAdapter<CharSequence> adapterOrdenEstado = ArrayAdapter.createFromResource(this, R.array.orden_estado, android.R.layout.simple_spinner_item);
        adapterOrdenEstado.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spOrdenEstado.setAdapter(adapterOrdenEstado);


        btBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                consultarOrdenesListadas(etBuscar.getText().toString().trim());
            }
        });

        spOrdenEjecucion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                //cargarOrdenes(etBuscar.getText().toString(), spOrdenEstado.getSelectedItemPosition(), (String) spOrdenEjecucion.getSelectedItem(), strCiclo, strRuta);
                if(orden != spOrdenEjecucion.getSelectedItemPosition()) {
                    actualizarVista();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

        spOrdenEstado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                //cargarOrdenes(etBuscar.getText().toString(), spOrdenEstado.getSelectedItemPosition(), (String) spOrdenEjecucion.getSelectedItem(), strCiclo, strRuta);
                if(estado != spOrdenEstado.getSelectedItemPosition()){
                    actualizarVista();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

        actualizarPendientes();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        currentItem = vPager.getCurrentItem();
        outState.putInt("currentItem", currentItem);
        outState.putBoolean("avanzar", avanzar);
        outState.putBundle("bolsaR", bolsaR);
        outState.putInt("ose_codigo", ose_codigo);
        outState.putString("ciclo", strCiclo);
        outState.putString("ruta", strRuta);
        outState.putInt("tipoUsuario", tipoUsuario);
        outState.putParcelable("fileUri", fileUri);
        outState.putInt("cli_contrato_aux", cli_contrato_aux);
        outState.putParcelableArrayList("ordenes", (ArrayList<? extends Parcelable>)  ordenes);
        outState.putString("medidor",medidor);

        outState.putString("MODEL",MODEL);
        outState.putString("IMEI",IMEI);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            currentItem = savedInstanceState.getInt("currentItem");
            avanzar = savedInstanceState.getBoolean("avanzar");
            bolsaR = savedInstanceState.getBundle("bolsaR");
            ose_codigo = savedInstanceState.getInt("ose_codigo");
            strCiclo = savedInstanceState.getString("ciclo");
            strRuta = savedInstanceState.getString("ruta");
            tipoUsuario = savedInstanceState.getInt("tipoUsuario");
            fileUri = savedInstanceState.getParcelable("fileUri");
            cli_contrato_aux = savedInstanceState.getInt("cli_contrato_aux");
            ordenes = savedInstanceState.getParcelableArrayList("ordenes");
            medidor = savedInstanceState.getString("medidor");

            MODEL = savedInstanceState.getString("MODEL");
            IMEI = savedInstanceState.getString("IMEI");
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        if(bolsaR != null) {
            cuadrilla = bolsaR.getInt("cuadrilla");
            tipoProducto = bolsaR.getString("producto");
            tipoUsuario = bolsaR.getInt("tipo");
            theBtMacAddress = bolsaR.getString("theBtMacAddress");

            terminal = bolsaR.getInt("terminalKey");
            estado = bolsaR.getInt("estado");
            orden = bolsaR.getInt("orden");


            spOrdenEstado.setSelection(estado);
            spOrdenEjecucion.setSelection(orden);
        }

        //////tAdapter = new TabsAdapter(getSupportFragmentManager(), ordenes);
        //aBar = getActionBar();
        //////vPager.setAdapter(tAdapter);
        //aBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        //vPager.setCurrentItem(1,false);

        updateBarHandler = new Handler();
        iniciarConexion();

        if(avanzar){
            currentItem++;
            avanzar = false;
            //tAdapter.notifyDataSetChanged();
        }

        if(ordenes.size() == 0) {
            cargarOrdenes(etBuscar.getText().toString(), spOrdenEstado.getSelectedItemPosition(),
                    (String) spOrdenEjecucion.getSelectedItem(), strCiclo, strRuta);
        }

        if (tAdapter == null){
            tAdapter = new TabsAdapter(fm, ordenes);
        }
        else {
            tAdapter.notifyDataSetChanged();
        }

        vPager.setAdapter(tAdapter);

        vPager.setCurrentItem(currentItem, false);
        vPager.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

    }

    @Override
    public void onPause() {
        super.onPause();
        //inicioEnvio = true;
    }

    @Override
    public void finish() {
        if (salidaActivity) {
            Intent i = new Intent(this, EjecucionOrdenes.class);
            this.setResult(RESULT_OK, i);
            super.finish();
        }
        System.out.println("Intenta cerrar la apk VerOrden");
    }

    public void actualizarVista(){
        Intent i = new Intent(this, EjecucionOrdenes.class);
        estado = spOrdenEstado.getSelectedItemPosition();
        orden = spOrdenEjecucion.getSelectedItemPosition();

        i.putExtra("estado",estado);
        i.putExtra("orden",orden);

        this.setResult(RESULT_FIRST_USER, i);
        super.finish();
    }

    private void iniciarConexion() {

        new Thread(new Runnable() {
            public void run() {
                try {
                    // Instantiate insecure connection for given Bluetooth&reg; MAC Address.
                    thePrinterConn = new BluetoothConnectionInsecure(theBtMacAddress);
                } catch (Exception e) {
                    // Handle communications error here.
                    e.printStackTrace();
                }
            }
        }).start();
    }


    /**
     * Cargar las ordenes en el listview a partir de los parametros de la firma
     *
     * @param textBuscar Busqueda Medidor, Ruta, Cliente
     * @param estado
     * @param orden
     * @param cicloStr
     * @param rutaStr
     */
    public void cargarOrdenes(String textBuscar, int estado, String orden, String cicloStr, String rutaStr) {
        String direccion, elemento, producto;
        int ose_codigo_tmp, cli_contrato_in, consecutivo, tipo_orden, ruta;
        String tip_orden = "209", consumo = "", cli_nombre = "";

        switch (estado) {
            case 0: //PENDIENTES
                estado = 1;
                break;
            case 1://Finalizadas
                estado = 27;
                break;
            case 2://REVISAR AUN NO ESTA
                estado = 27;
                tip_orden = "210";
                break;
            default:
                estado = 1;
                break;
        }

        ordenes.clear();

        managerVerOrden.open();
        cursor = managerVerOrden.cargarCursorOrdenesCicloRuta(String.valueOf(cuadrilla), "", String.valueOf(estado), orden, cicloStr, rutaStr, tip_orden);
        if (cursor.moveToFirst()) {
            do {
                direccion = cursor.getString(cursor.getColumnIndex("direccion1"));
                elemento = cursor.getString(cursor.getColumnIndex("elemento"));
                ose_codigo_tmp = cursor.getInt(cursor.getColumnIndex("ose_codigo"));
                producto = cursor.getString(cursor.getColumnIndex("producto"));
                cli_contrato_in = cursor.getInt(cursor.getColumnIndex("cli_contrato"));
                ruta = cursor.getInt(cursor.getColumnIndex("ruta"));
                consecutivo = cursor.getInt(cursor.getColumnIndex("ruta_cons"));
                tipo_orden = 1;
                consumo = cursor.getString(cursor.getColumnIndex("consumo"));
                cli_nombre = cursor.getString(cursor.getColumnIndex("cli_nombre"));

                Bundle bolsa = new Bundle();
                bolsa.putInt("ose_codigo", ose_codigo_tmp);
                bolsa.putString("producto", producto);
                bolsa.putString("direccion", direccion);
                bolsa.putString("elemento", elemento);
                bolsa.putInt("cli_contrato", cli_contrato_in);
                bolsa.putInt("ruta", ruta);
                bolsa.putInt("consecutivo", consecutivo);
                bolsa.putInt("tipo", tipo_orden);
                bolsa.putString("consumo", consumo);
                bolsa.putString("theBtMacAddress", theBtMacAddress);
                bolsa.putString("cli_nombre", cli_nombre);

                ordenes.add(bolsa);

            } while (cursor.moveToNext());//accessing data upto last row from table

            //tAdapter.notifyDataSetChanged();
            //vPager.invalidate();
        }

        cursor.close();
        managerVerOrden.close();
        System.gc();
    }// Cierra cargarOrdenes


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_orden, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_camera:
                currentItem = vPager.getCurrentItem();
                ose_codigo = ordenes.get(currentItem).getInt("ose_codigo");
                tipoFoto = 0;
                captureImage();
                return true;
            case R.id.menu_salir:
                finalizarEnviar();
                return true;
            case R.id.menu_print:
                currentItem = vPager.getCurrentItem();
                cli_contrato_aux = ordenes.get(currentItem).getInt("cli_contrato");
                imprimirTirilla(cli_contrato_aux);
                return true;
            case R.id.menu_factura:

                Toast.makeText(this,"FACTURANDO",Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_medidorEncontrado:
                openMedidorEncontrado();
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
            }
            else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(),"User cancelled image capture", Toast.LENGTH_SHORT).show();
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
            String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault()).format(new Date());
            String strMedidor = "MED:" + medidor;
            String strDatos = "";

            if(indicador_lectura == 1)
                strDatos = "LECT:" + String.valueOf(lectura_actual);
            else strDatos = "CAUSA:" + strCausa;

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
                if(tipoUsuario == 1)
                    paintText.setColor(Color.YELLOW);
                else
                    paintText.setColor(Color.RED);
                paintText.setTextSize(12);
                paintText.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)); // Text Overlapping Pattern	            // some more settings...
                //paintText.setShadowLayer(10f, 10f, 10f, Color.BLACK);

                newCanvas.drawBitmap(fbitmap, 0, 0, paintText);
                newCanvas.drawText(timeStamp + "   " + strMedidor, 20, 10, paintText);
                newCanvas.drawText(MODEL + " V" + VERSION + "  " + IMEI, 20, fbitmap.getHeight() - 10, paintText);
                //paintText.setTextSize(25);
                //newCanvas.drawText(strDatos, 20, 33, paintText);
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
        fotosTomadas++;

        if (fotosTomadas >= limiteFotos)
        {
            debeTomarFotosAdd = false;
            if(tipoFoto == 1){
                avanzar = true;
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Captura Fotos Adicionales", Toast.LENGTH_SHORT).show();
            captureImage();
        }
    }


    public static int nearest2pow(int value) {
        return value == 0 ? 0
                : (32 - Integer.numberOfLeadingZeros(value - 1)) / 2;
    }

    /*
     * Capturing image
     */
    private void captureImage() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        //victor
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
        manager.open();
        manager.insertarFoto(ose_codigo, fileUri.getPath());
        manager.close();

        deleteLatest();
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



    /**
     * Funci?n que elimina acentos y caracteres especiales de
     * una cadena de texto.
     * @param input
     * @return cadena de texto limpia de acentos y caracteres especiales.
     */
    public static String removerTildes(String input) {
        // Cadena de caracteres original a sustituir.
        String original = "??????????????????????????????????";
        // Cadena de caracteres ASCII que reemplazaron los originales.
        String ascii    = "aaaeeeiiiooouuunAAAEEEIIIOOOUUUNcC";
        String output = input;
        for (int i=0; i<original.length(); i++) {
            // Reemplazamos los caracteres especiales.
            output = output.replace(original.charAt(i), ascii.charAt(i));
        }//for i

        output = output.replace(';','.');
        output = output.replace('"',' ');
        output = output.replace("'"," ");

        return output;
    }//remove1


    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        System.out.println("EJECUTO MOVIMIENTOS EN LOS TABS");
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onFragmentIterationVerLectura(Bundle parameters) {
        int cli_contrato = 0, call = 0;

        call = parameters.getInt("call");

        switch (call){
            case 0:
                cli_contrato = parameters.getInt("cli_contrato");
                imprimirTirilla(cli_contrato);
                break;
            case 1:
                ose_codigo = parameters.getInt("ose_codigo");
                indicador_lectura = parameters.getInt("indicador_lectura");
                lectura_actual = parameters.getInt("lectura_actual");
                strCausa = parameters.getString("motivoNoLecturaStr");
                critica = parameters.getInt("critica");
                tipoFoto = parameters.getInt("tipo_foto");
                fotosTomadas = parameters.getInt("fotosTomadas");
                medidor = parameters.getString("medidor");
                //limiteFotos = parameters.getInt("limiteFotos");
                limiteFotos = 1;
                debeTomarFotosAdd = parameters.getBoolean("debeTomarFotosAdd");
                captureImage();
                actualizarPendientes();
                break;
        }
    }

    public void imprimirTirilla(int cli_contrato){

        if(theBtMacAddress == ""){

            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                // Device does not support Bluetooth
            }

            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }

            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            // If there are paired devices
            if (pairedDevices.size() > 0) {
                // Loop through paired devices
                for (BluetoothDevice device : pairedDevices) {
                    // Add the name and address to an array adapter to show in a ListView
                    System.out.println(device.getName() + "\n" + device.getAddress());
                    theBtMacAddress = device.getAddress();
                }
            }
        }

        int inicial = 94, factor = 25;
        String lectura = "", observacionLectura = "", fecha_lectura = "", cliente = "", direccion = "", medidor = "", franja = "", servicio = "";
        int rutaImpr = 0, conseImpr = 0;
        int codigoObsLectura = 0, codigoNoLectura = 0, indicadorLectura = 0, suscriptor = 0 ;
        String strMotivoNoLectura = "", strObsLectura = "";
        int lengthRead = 500, countMed = 0;

        String cpclData = "";

        managerVerOrden.open();
        manager.open();
        Cursor cursor = managerVerOrden.consultaLecturaImprimirEmcali(String.valueOf(cli_contrato));

        if(cursor.moveToFirst()){
            cliente = cursor.getString(cursor.getColumnIndex("cli_nombre"));
            suscriptor = cursor.getInt(cursor.getColumnIndex("cli_contrato"));
            direccion = cursor.getString(cursor.getColumnIndex("direccion1"));
            medidor = cursor.getString(cursor.getColumnIndex("elemento"));
            franja = cursor.getString(cursor.getColumnIndex("consumo"));
            servicio = cursor.getString(cursor.getColumnIndex("producto"));
            rutaImpr = cursor.getInt(cursor.getColumnIndex("ruta"));
            conseImpr = cursor.getInt(cursor.getColumnIndex("ruta_cons"));

            lectura = cursor.getString(cursor.getColumnIndex("lectura"));
            codigoNoLectura = cursor.getInt(cursor.getColumnIndex("codigo_observacion_no_lectura"));
            codigoObsLectura = cursor.getInt(cursor.getColumnIndex("codigo_observacion_lectura"));
            observacionLectura = cursor.getString(cursor.getColumnIndex("observacion_no_lectura"));
            fecha_lectura = cursor.getString(cursor.getColumnIndex("fecha_creacion"));
            indicadorLectura = cursor.getInt(cursor.getColumnIndex("indicador_lectura"));

            //cpclData += "TEXT 0 0 15 " + inicial + " " + fecha_lectura + "\r\n";//FECHA
            inicial = inicial + 10;

            //Nit: 816002019-9
            cpclData += "TEXT 0 0 15 " + inicial + " MATRICULA             " +  fecha_lectura + "\r\n";//FECHA
            inicial = inicial + 15;

            //cpclData += strImprimirCampos(String.valueOf(suscriptor), inicial);
            //inicial = inicial + factor;

            cpclData += "TEXT 0 6 15 " + inicial + " " +  suscriptor + "\r\n";//FECHA
            inicial = inicial + factor + 20;

            cpclData += "TEXT 0 0 15 " + inicial + " USUARIO       \r\n";//FECHA
            inicial = inicial + 15;

            cpclData += strImprimirCampos(cliente, inicial);
            inicial = inicial + factor;

            cpclData += "TEXT 0 0 15 " + inicial + " DIRECCION            Ruta:"+ rutaImpr + "  Cons:" + conseImpr + "\r\n";//FECHA
            inicial = inicial + 15;

            cpclData += strImprimirCampos(direccion, inicial);
            inicial = inicial + factor;

            cpclData += "TEXT 0 0 15 " + inicial + " MEDIDOR        FRANJA:" + franja + "\r\n";//FECHA
            inicial = inicial + 15;

            cpclData += strImprimirCampos(medidor, inicial + 1);
            inicial = inicial + factor;

            /*
            cpclData += "TEXT 5 0 15 " + inicial + "  " + medidor + "\r\n";//FECHA
            //cpclData += strImprimirCampos(medidor , inicial);//Matricula Cliente
            inicial = inicial + factor;
            */

            if (indicadorLectura == 1)
            {
                cpclData += "TEXT 0 2 15 " + inicial + " LECTURA:\r\n";//FECHA
                //inicial = inicial + factor;

                cpclData += "TEXT 0 6 45 " + (inicial + 5) + "  " + lectura + "\r\n";
                inicial = inicial + factor + 25;
                //cpclData += strImprimirCampos("LECTURA: " + lectura + " -" + servicio, inicial);//TITULO LECTURA
            }
            else
            {
                cpclData += "TEXT 0 2 15 " + inicial + " NO LECTURA:\r\n";//FECHA
                inicial = inicial + factor;
                strMotivoNoLectura = manager.getObsCodigo(String.valueOf(codigoNoLectura));
                cpclData += strImprimirCampos(strMotivoNoLectura, inicial);//CAUSAL DE NO LECTURA
                inicial = inicial + factor;
            }

            cpclData += "\r\n";

            if(codigoObsLectura > 0){
                strObsLectura = manager.getObsCodigo(String.valueOf(codigoObsLectura));
                if(strObsLectura.length() > 2 && !strObsLectura.contains("IRREGULAR")){
                    cpclData += "TEXT 0 0 15 " + inicial + " OBSERVACION:\r\n";//FECHA
                    inicial = inicial + 15;

                    cpclData += strImprimirCampos(strObsLectura, inicial);//OBSERVACION LECTURA
                    cpclData += "\r\n";
                    inicial = inicial + factor;
                }
            }

            if(cursor.moveToNext()){
                do{
                    countMed++;
                    medidor = cursor.getString(cursor.getColumnIndex("elemento"));
                    franja = cursor.getString(cursor.getColumnIndex("consumo"));
                    servicio = cursor.getString(cursor.getColumnIndex("producto"));

                    lectura = cursor.getString(cursor.getColumnIndex("lectura"));
                    codigoNoLectura = cursor.getInt(cursor.getColumnIndex("codigo_observacion_no_lectura"));
                    codigoObsLectura = cursor.getInt(cursor.getColumnIndex("codigo_observacion_lectura"));
                    observacionLectura = cursor.getString(cursor.getColumnIndex("observacion_no_lectura"));
                    indicadorLectura = cursor.getInt(cursor.getColumnIndex("indicador_lectura"));

                    cpclData += "TEXT 0 0 15 " + inicial + " MEDIDOR        FRANJA:" + franja + "\r\n";//FECHA
                    inicial = inicial + 15;

                    cpclData += "TEXT 5 0 15 " + inicial + "  " + medidor + "\r\n";//FECHA
                    //cpclData += strImprimirCampos(medidor , inicial);//Matricula Cliente
                    inicial = inicial + factor;

                    if (indicadorLectura == 1)
                    {
                        cpclData += "TEXT 0 2 15 " + inicial + " LECTURA:\r\n";//FECHA
                        //inicial = inicial + factor;

                        cpclData += "TEXT 0 6 45 " + (inicial + 5) + "  " + lectura + "\r\n";
                        inicial = inicial + factor + 25;
                        //cpclData += strImprimirCampos("LECTURA: " + lectura + " -" + servicio, inicial);//TITULO LECTURA
                    }
                    else
                    {
                        cpclData += "TEXT 0 2 15 " + inicial + " NO LECTURA:\r\n";//FECHA
                        inicial = inicial + factor;
                        strMotivoNoLectura = manager.getObsCodigo(String.valueOf(codigoNoLectura));
                        cpclData += strImprimirCampos(strMotivoNoLectura, inicial);//CAUSAL DE NO LECTURA
                        inicial = inicial + factor;
                    }

                    cpclData += "\r\n";

                    if(codigoObsLectura > 0){
                        strObsLectura = manager.getObsCodigo(String.valueOf(codigoObsLectura));
                        if(strObsLectura.length() > 2 && !strObsLectura.contains("IRREGULAR")){
                            cpclData += "TEXT 0 0 15 " + inicial + " OBSERVACION:\r\n";//FECHA
                            inicial = inicial + 15;

                            cpclData += strImprimirCampos(strObsLectura, inicial);//OBSERVACION LECTURA
                            cpclData += "\r\n";
                            inicial = inicial + factor;
                        }
                    }

                }while(cursor.moveToNext());//accessing data upto last row from table
            }
        }

        cpclData += "TEXT 0 0 15 " + inicial + "                                   lector:" + cuadrilla + "\r\n";//FECHA
        inicial = inicial + factor;

        cursor.close();

        cpclData += "LEFT \r\n"
                + "FORM\r\n"
                + "PRINT\r\n";
        manager.close();
        managerVerOrden.close();

        if(countMed == 0)
            lengthRead = 390;

        String head = "! U1 JOURNAL\r\n ! U1 SETFF 50 2\r\n"
                + "! 0 200 200 "+ lengthRead +" 1\r\n"
                + "PCX 200 43 !<PEREIRA1.PCX\r\n"
                + "TEXT 5 0 15 25 ENERGIA DE PEREIRA\r\n"
                + "TEXT 0 0 15 47 S.A.E.S.P.\r\n"
                + "TEXT 0 2 15 60 CONSTANCIA DE LECTURA  \r\n"
                + "TEXT 0 0 15 82 Nit: 816002019-9\r\n";

        //System.out.println(cpclData);

        final String salidaImprimir =head + cpclData;

        sendCpclOverBluetooth(theBtMacAddress, salidaImprimir);
    }


    /**
     * Buscar bluetooh
     * @param theBtMacAddress
     * @param datosImprimir
     */
    private void sendCpclOverBluetooth(final String theBtMacAddress, final String datosImprimir) {

        new Thread(new Runnable() {
            public void run() {
                try {
                    // Instantiate insecure connection for given Bluetooth&reg; MAC Address.
                    //Connection thePrinterConn = new BluetoothConnectionInsecure(theBtMacAddress);

                    // Initialize
                    Looper.prepare();
                    // Open the connection - physical connection is established here.
                    thePrinterConn.open();
                    //thePrinterConn.write("! U1 JOURNAL\r\n! U1 SETFF 50 2\r\n".getBytes());
                    //Thread.sleep(50);
                    // This example prints "This is a CPCL test." near the top of the label.
                    // Send the data to printer as a byte array.
                    thePrinterConn.write(datosImprimir.getBytes());
                    // Make sure the data got to the printer before closing the connection
                    Thread.sleep(2000);
                    // Close the insecure connection to release resources.
                    thePrinterConn.close();
                    //Thread.sleep(100);

                    Looper.myLooper().quit();

                } catch (Exception e) {
                    // Handle communications error here.
                    e.printStackTrace();
                    sendCpclOverBluetooth2Intento(theBtMacAddress, datosImprimir);
                }
            }
        }).start();
    }

    private boolean isPrinterReady(Connection thePrinterConn) {
        boolean isOK = false;
        try {
            thePrinterConn.open();
            // Creates a ZebraPrinter object to use Zebra specific functionality like getCurrentStatus()
            com.zebra.sdk.printer.ZebraPrinter printer = com.zebra.sdk.printer.ZebraPrinterFactory.getInstance(thePrinterConn);

            com.zebra.sdk.printer.PrinterStatus printerStatus = printer.getCurrentStatus();
            if (printerStatus.isReadyToPrint) {
                isOK = true;
            } else if (printerStatus.isPaused) {
                System.out.println("Cannot Print because the printer is paused.");
            } else if (printerStatus.isHeadOpen) {
                System.out.println("Cannot Print because the printer media door is open.");
            } else if (printerStatus.isPaperOut) {
                System.out.println("Cannot Print because the paper is out.");
            } else {
                System.out.println("Cannot Print.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isOK;
    }

    private void sendCpclOverBluetooth2Intento(final String theBtMacAddress, final String datosImprimir) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    //Thread.sleep(5000);
                    // Instantiate insecure connection for given Bluetooth&reg; MAC Address.
                    //Connection thePrinterConn = new BluetoothConnectionInsecure(theBtMacAddress,500,500);
                    // Initialize
                    Looper.prepare();
                    // Open the connection - physical connection is established here.
                    thePrinterConn.open();
                    //thePrinterConn.write("! U1 JOURNAL\r\n! U1 SETFF 50 2\r\n".getBytes());
                    thePrinterConn.write(datosImprimir.getBytes());
                    // Make sure the data got to the printer before closing the connection
                    Thread.sleep(1500);
                    // Close the insecure connection to release resources.
                    thePrinterConn.close();
                    Looper.myLooper().quit();
                } catch (Exception e) {
                    // Handle communications error here.
                    e.printStackTrace();
                    sendCpclOverBluetooth3Intento(theBtMacAddress, datosImprimir);
                }
            }
        }).start();
    }

    private void sendCpclOverBluetooth3Intento(final String theBtMacAddress, final String datosImprimir) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    //Thread.sleep(7000);
                    // Instantiate insecure connection for given Bluetooth&reg; MAC Address.
                    //Connection thePrinterConn = new BluetoothConnectionInsecure(theBtMacAddress,500,500);
                    // Initialize
                    Looper.prepare();
                    // Open the connection - physical connection is established here.
                    thePrinterConn.open();
                    //thePrinterConn.write("! U1 JOURNAL\r\n! U1 SETFF 50 2\r\n".getBytes());
                    thePrinterConn.write(datosImprimir.getBytes());
                    // Make sure the data got to the printer before closing the connection
                    Thread.sleep(500);
                    // Close the insecure connection to release resources.
                    thePrinterConn.close();
                    Looper.myLooper().quit();
                } catch (Exception e) {
                    // Handle communications error here.
                    e.printStackTrace();
                    try{
                        thePrinterConn.close();
                    }catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public String strImprimirCampos(String campo1, int i_iterador)
    {
        String entrada = "", strAux = "";
        int iterador = i_iterador, tamano = 22;

        campo1 = removerTildes(campo1);
        if (campo1.length() > tamano)
        {
            entrada = campo1.substring(0, tamano);
            strAux += "TEXT 0 3 20 " + iterador + " " + entrada + "\r\n";
        }
        else
        {
            strAux += "TEXT 0 3 20 " + iterador + " " + campo1 + "\r\n";
        }

        return strAux;
    }

    public String strImprimirDireccion(String campo1, int i_iterador)
    {
        String entrada = "", strAux = "";
        int iterador = i_iterador, tamano = 35;

        campo1 = removerTildes(campo1);
        if (campo1.length() > tamano)
        {
            entrada = campo1.substring(0, tamano);
            strAux += "TEXT 0 2 20 " + iterador + " " + entrada + "\r\n";
        }
        else
        {
            strAux += "TEXT 0 2 20 " + iterador + " " + campo1 + "\r\n";
        }

        return strAux;
    }



    /**
     * Cierra el activity o envia el trabajo pendiente
     */
    private void finalizarEnviar() {

        AlertDialog.Builder invalid_input_dialog = new AlertDialog.Builder(this);
        invalid_input_dialog.setTitle("SALIR VSITA LATERAL")
                .setMessage("DESDEA SALIR DE LA VISTA LATERAL")
                .setCancelable(true)
                .setPositiveButton("SALIR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        salidaActivity = true;
                        finish();
                    }
                })
                .setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                    }
                })
                .show();
    }// Cierra finalizarEnviar

    /**
     * Actualiza Totales ordenes, ejecutadas, anuladas, enviadas, pendientes
     */
    public Bundle actualizarPendientes() {
        String codigo_cuadrilla_str = String.valueOf(cuadrilla);
        Bundle bolsa;
        managerVerOrden.open();
        bolsa = managerVerOrden.reporteOredenes(codigo_cuadrilla_str);
        managerVerOrden.close();
        if (!bolsa.isEmpty()) {
            tvTotalOrdenes.setText(String.valueOf(bolsa.getInt("total")));
            tvTotalEjectudadas.setText(String.valueOf(bolsa.getInt("ejecutadas")));
            tvTotalPendientes.setText(String.valueOf(bolsa.getInt("pendientes")));
            tvTotalEnviadas.setText(String.valueOf(bolsa.getInt("enviadas")));
        }
        return bolsa;
    }// Cierra actualizarPendientes

    public void openMedidorEncontrado() {

        int ruta, ruta_anterior, ruta_posterior = 0, ciclo= 0,contrato = 0, contrato_posterior = 0;
        String direccion= "";

        currentItem = vPager.getCurrentItem();
        ose_codigo = ordenes.get(currentItem).getInt("ose_codigo");
        ruta_anterior = ordenes.get(currentItem).getInt("consecutivo");
        ruta = ordenes.get(currentItem).getInt("ruta");
        direccion = ordenes.get(currentItem).getString("direccion");
        contrato = ordenes.get(currentItem).getInt("cli_contrato");

        VerLecturaModel managerLectura = new VerLecturaModel(this);


        Bundle bolsa;
        managerLectura.open();
        bolsa = managerLectura.consultaDatosMedidor(String.valueOf(cuadrilla),ruta_anterior);
        managerLectura.close();

        if (!bolsa.isEmpty()) {
            ciclo = bolsa.getInt("ciclo");
            ruta_posterior = bolsa.getInt("ruta_posterior",0);
            contrato_posterior = bolsa.getInt("contrato_posterior",0);
        }

        Intent i = new Intent(this, GestionMedidorEncontrado.class);

        i.putExtra("ruta", ruta);
        i.putExtra("cuadrilla", cuadrilla);
        i.putExtra("ciclo", ciclo);
        i.putExtra("direccion", direccion);
        i.putExtra("ruta_anterior", ruta_anterior);
        i.putExtra("ruta_posterior", ruta_posterior);

        i.putExtra("contrato_anterior", contrato);
        i.putExtra("contrato_posterior", contrato_posterior);

        startActivity(i);
    }

    /**
     * Buscar una orden en el array de ordenes
     *
     * @param busqueda
     */
    private void consultarOrdenesListadas(String busqueda) {

        String strMedidor, strContrato;

        if (busqueda.compareTo(strBuscar) != 0) {
            System.out.println("CAMBIO BUSQUEDA " + busqueda + " por " + strBuscar);
            indiceInicial = 0;
            strBuscar = busqueda;
        }

        if (!ordenes.isEmpty()) {
            for (int idx = indiceInicial; idx < ordenes.size(); idx++) {

                Bundle item = ordenes.get(idx);

                strMedidor = item.getString("elemento");
                strContrato = String.valueOf(item.getInt("cli_contrato"));
                if (strMedidor.contains(busqueda) || strContrato.contains(busqueda)) {
                    vPager.setCurrentItem(idx,true);

                    indiceInicial = idx + 1;
                    break;
                } else if (idx == (ordenes.size() - 1)) {
                    indiceInicial = 0;
                    Toast.makeText(getApplicationContext(), "No encontrado", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    private void consultarOrdenesListadasRun(String busqueda) {

        String strMedidor, strContrato;

        if (busqueda.compareTo(strBuscar) != 0) {
            System.out.println("CAMBIO BUSQUEDA " + busqueda + " por " + strBuscar);
            indiceInicial = 0;
            strBuscar = busqueda;
        }

        if (!ordenes.isEmpty()) {
            for (int idx = indiceInicial; idx < ordenes.size(); idx++) {

                Bundle item = ordenes.get(idx);

                strMedidor = item.getString("elemento");
                strContrato = String.valueOf(item.getInt("cli_contrato"));
                if (strMedidor.contains(busqueda) || strContrato.contains(busqueda)) {
                    indiceObjetivo = idx;
                    indiceInicial = idx + 1;
                    SwitchPage(3);
                    break;
                } else if (idx == (ordenes.size() - 1)) {
                    indiceInicial = 0;
                    indiceObjetivo = 0;
                    Toast.makeText(getApplicationContext(), "No encontrado", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }



    static Timer timer = new Timer();
    public void SwitchPage(int seconds)
    {
        if(timer != null) {
            timer.cancel();
        }

        runOnUiThread(new Runnable() {

            public void run() {

                if(indiceObjetivo < tAdapter.getCount()) {

                    vPager.setCurrentItem(indiceObjetivo, true);
                }
                else {
                    indiceObjetivo=0;
                    vPager.setCurrentItem(indiceObjetivo, true);
                }
            }
        });

        //timer = new Timer(); // At this line a new Thread will be created
        //timer.schedule(new SwitchPageTask(),2000, seconds * 2000);
        // delay in milliseconds
    }


    class SwitchPageTask extends TimerTask
    {
        @Override
        public void run() {

            runOnUiThread(new Runnable() {

                public void run() {

                    if(indiceObjetivo < tAdapter.getCount()) {

                        vPager.setCurrentItem(indiceObjetivo, true);
                    }
                    else {
                        indiceObjetivo=0;
                        vPager.setCurrentItem(indiceObjetivo, true);
                    }
                }
            });
        }
    }
}
