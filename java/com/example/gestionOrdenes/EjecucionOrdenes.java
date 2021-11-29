package com.example.gestionOrdenes;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.actsis.fensimp.Factura;
import com.example.Logueo.DataBaseManager;
import com.example.Logueo.DbHelper;
import com.example.Logueo.MainActivity;
import com.example.apiretrofit.ApiClient;
import com.example.apiretrofit.ApiServices;
import com.example.asistencia.EjecucionAsistencia;
import com.example.bluetooh.BluetoothDemo;
import com.example.bluetooh.OrdenMateriales;
import com.example.config.GestionInfoCargaDatos;
import com.example.config.GestionObservaciones;
import com.example.formato.EjecucionFormatos;
import com.example.formato.ListarFormatos;
import com.example.gestiondeltec.R;
import com.example.lectura.Auth;
import com.example.lectura.MyApplication;
import com.example.lectura.VerLectura;
import com.example.location.LocationManager;
import com.example.location.LocationSyncActivity;
import com.example.seguimientoOrdenes.SeguimientoOrdenes;
import com.example.verorden.VerOrden;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.zebra.sdk.comm.BluetoothConnectionInsecure;
import com.zebra.sdk.comm.Connection;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EjecucionOrdenes extends LocationSyncActivity implements OnClickListener {

    private static final int NOTIF_ALERTA_ID = 0;
    private TextView tvUsuario, tvTotalOrdenes, tvTotalEjectudadas, tvTotalPendientes, tvTotalEnviadas, tvDatosTrabajo, tvTituloEstado;
    private EditText etBuscar;
    private Cursor cursor;
    private ListView lista;
    private Button btBuscar;
    private Spinner spOrdenEjecucion, spOrdenEstado;
    private EjecucionOrdenesModel manager, managerConsulta;
    List<Item> items = new ArrayList<Item>();

    int codigo_cuadrilla = 0, terminal = -1, ose_codigo_temp = 0, cliente_temp = 0;
    boolean orden_pendiente = true, salidaActivity = false;
    String strLatitud = "", strLongitud = "", strAltitud = "", strVelocidad = "", nombreCuadrilla = "";
    String strRuta = "", strCiclo = "", orden_id = "", rutaOrden = "";

    IntentFilter ifilter;
    Intent batteryStatus;
    int level, scale;
    float batteryPct;

    ProgressDialog barProgressDialog;
    Handler updateBarHandler, getOrdenHandler;

    int iteradorTimer = 0;
    public boolean banderaEnvio = true, flagUploadJob = true;
    MyTimerTask myTask;
    Timer myTimer, myTimerSuper;
    MyTimerTaskCargarTrabajo myTaskCargarTrabajo;
    boolean inicioEnvio = false, inicioEnvioCargarTrabajo = false;

    String IMEI = "", strBuscar = "";
    double VERSION;
    Bundle bolsaR;
    int indiceInicial = 0;
    int tipoUsuario = 1;

    String theBtMacAddress = "";
    private static final int REQUEST_ENABLE_BT = 0;

    Connection thePrinterConn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ejecucion);

        lista = (ListView) findViewById(R.id.lvSeguimiento);
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

        bolsaR = getIntent().getExtras();
        manager = new EjecucionOrdenesModel(this);
        managerConsulta = new EjecucionOrdenesModel(this);

        btBuscar.setBackgroundResource(android.R.drawable.btn_default);

        ArrayAdapter<CharSequence> adapterOrdenEjecucion = ArrayAdapter.createFromResource(this, R.array.orden_ejecucion, android.R.layout.simple_spinner_item);
        adapterOrdenEjecucion.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spOrdenEjecucion.setAdapter(adapterOrdenEjecucion);

        ArrayAdapter<CharSequence> adapterOrdenEstado = ArrayAdapter.createFromResource(this, R.array.orden_estado, android.R.layout.simple_spinner_item);
        adapterOrdenEstado.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spOrdenEstado.setAdapter(adapterOrdenEstado);

        btBuscar.setOnClickListener(this);

        lista.requestFocus();
        getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        spOrdenEjecucion.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                cargarOrdenes(etBuscar.getText().toString(), spOrdenEstado.getSelectedItemPosition(), (String) spOrdenEjecucion.getSelectedItem(), strCiclo, strRuta);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

        spOrdenEstado.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                cargarOrdenes(etBuscar.getText().toString(), spOrdenEstado.getSelectedItemPosition(), (String) spOrdenEjecucion.getSelectedItem(), strCiclo, strRuta);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

        lista.setAdapter(new ItemAdapter(this, items));
        // Register a callback to be invoked when an item in this AdapterView has been clicked
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
                Item item = (Item) lista.getAdapter().getItem(position);
                abrirOrden(item);
            }
        });

        cargarOrdenes(etBuscar.getText().toString(), spOrdenEstado.getSelectedItemPosition(), (String) spOrdenEjecucion.getSelectedItem(), strCiclo, strRuta);

        ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        batteryStatus = this.registerReceiver(null, ifilter);

        if (theBtMacAddress == "" && tipoUsuario == 1) {//Lector

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
            iniciarConexion();
        }

        openApplicationForGeolocalization();

    }

    /**
     * FUNCION QUE ABRE SEGUNDA APLICACIÓN PARA EJECUTAR METODO INICIAR TRABAJO
     */
    public void openApplicationForGeolocalization(){
        boolean activityFound = false;

        Uri location = Uri.parse("com://www.geolocalizaciondeltec.com");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);
        Bundle bundle = new Bundle();
        bundle.putSerializable("estado","activar");


        mapIntent.putExtras(bundle);
        try{
            startActivity(mapIntent);
        }catch (Exception e){

            activityFound = true;
            Log.i("NoFound","intent failed");
        }
        //CONDICION QUE EVALUA CUANDO NO ESTA INSTALADA LA APLICACIÓN DE GEOLOCALIZACIÓN
        if (activityFound){

                    AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("Instale la aplicación de geolocalización")
					.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							dialogInterface.dismiss();
							Intent intent = new Intent(getApplicationContext(),MainActivity.class);
							startActivity(intent);

						}
					}).setCancelable(false)
					.create();

                alert.show();

            }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("envio", inicioEnvio);
        outState.putString("ciclo", strCiclo);
        outState.putString("ruta", strRuta);
        outState.putInt("tipoUsuario", tipoUsuario);
        outState.putBoolean("envioCargarTrabajo", inicioEnvioCargarTrabajo);
        outState.putBundle("bolsaR", bolsaR);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            inicioEnvio = savedInstanceState.getBoolean("envio");
            strCiclo = savedInstanceState.getString("ciclo");
            strRuta = savedInstanceState.getString("ruta");
            tipoUsuario = savedInstanceState.getInt("tipoUsuario");
            inicioEnvioCargarTrabajo = savedInstanceState.getBoolean("envioCargarTrabajo");

            bolsaR = savedInstanceState.getBundle("bolsaR");
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        if (bolsaR != null) {
            nombreCuadrilla = bolsaR.getString("nombreKey");
            if (nombreCuadrilla.length() > 15)
                nombreCuadrilla.substring(0, 15);

            tvUsuario.setText(nombreCuadrilla);
            codigo_cuadrilla = bolsaR.getInt("codigoKey");
            tipoUsuario = bolsaR.getInt("tipo");
            terminal = bolsaR.getInt("terminalKey");
            IMEI = bolsaR.getString("imei");
            VERSION = bolsaR.getDouble("VERSION");
        }

        if (tipoUsuario == 2)
            tvTituloEstado.setText("SUPERVISOR");

        if (!inicioEnvio) {
            updateBarHandler = new Handler();
            myTask = new MyTimerTask();
            myTimer = new Timer();
            myTimer.schedule(myTask, 800000, 3000000);
            //myTimer.schedule(myTask, 2000, 1000);
            inicioEnvio = true;
        }

        if (tipoUsuario == 2 && !inicioEnvioCargarTrabajo) {
            getOrdenHandler = new Handler();
            myTaskCargarTrabajo = new MyTimerTaskCargarTrabajo();
            myTimerSuper = new Timer();
            myTimerSuper.schedule(myTaskCargarTrabajo, 600000, 1000000);
            //myTimerSuper.schedule(myTaskCargarTrabajo, 30000, 30000);
            inicioEnvioCargarTrabajo = true;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        inicioEnvio = true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_ejecucion, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refrescar:
                cargarOrdenes(etBuscar.getText().toString(), spOrdenEstado.getSelectedItemPosition(), (String) spOrdenEjecucion.getSelectedItem(), strCiclo, strRuta);
                return true;
            case R.id.menu_enviar:
                enviarDatosServer();
                return true;
            case R.id.menu_ver_report:
                viewReadReport();
                return true;
            case R.id.menu_revision:
                uploadReadCheckJobDialog();
                return true;
            case R.id.menu_seguimiento:
                if (tipoUsuario == 2)
                    visualizarSeguimiento();
                return true;
            case R.id.menu_upload_job:
                if (tipoUsuario == 2)
                    cargarOrdenesPendintes();
                return true;
            case R.id.menu_bluetooh_manager:
                AlertDialog.Builder invalid_input_dialog = new AlertDialog.Builder(this);
                invalid_input_dialog.setTitle("Administrar Impresora")
                        .setMessage("OJO solo abra el modulo cuando pierda conexion con la impresora o se lo indiquen")
                        .setCancelable(true)
                        .setPositiveButton("ABRIR", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                openManagerBluetooh();
                            }
                        })
                        .setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                            }
                        })
                        .show();
                return true;
            case R.id.menu_abrir_asistencia:
                Intent i = new Intent(this, VerOrden.class);
                i.putExtra("ose_codigo", 0);
                i.putExtra("elemento", "33333");
                i.putExtra("direccion", "direccion");
                i.putExtra("producto", "agua");
                i.putExtra("cuadrilla", codigo_cuadrilla);
                i.putExtra("ose_precarga", 0);
                i.putExtra("nombre", "Jasson");
                i.putExtra("tipo", 1);
                i.putExtra("theBtMacAddress", theBtMacAddress);
                i.putExtra("terminalKey", terminal);
                i.putExtra("nombreKey", nombreCuadrilla);
                i.putExtra("estado", 0);
                i.putExtra("orden", 0);
                i.putExtra("MODEL", Build.MODEL);
                i.putExtra("IMEI", IMEI);
                i.putExtra("VERSION", VERSION);

                System.gc();
                startActivityForResult(i, 999);
                return true;
            case R.id.menu_abrir_formatos:
                if (tipoUsuario == 2)
                    openManagerFormatos2();
                return true;
            case R.id.menu_salir:
                finalizarEnviar();
                return true;
            case R.id.menu_update_obs:
                openDialogUpdateObs();
                return true;
            case R.id.menu_enviar_med:
                enviarDatosMedidores();
                return true;
            case R.id.menu_descargar_datos:
                try {
                    envio_ordenes();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            case R.id.menu_fens_datos:
                AlertDialog.Builder invalid_input_dialog2 = new AlertDialog.Builder(this);
                invalid_input_dialog2.setTitle("INFORMACION DE FENS")
                        .setMessage("Abriendo datos")
                        .setCancelable(true)
                        .setPositiveButton("ABRIR", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                openInfoFens();
                            }
                        })
                        .setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                            }
                        })
                        .show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 111 && resultCode == RESULT_OK) {
            cargarOrdenes(etBuscar.getText().toString(), spOrdenEstado.getSelectedItemPosition(), (String) spOrdenEjecucion.getSelectedItem(), strCiclo, strRuta);

            LocationManager locationManager = LocationManager.getInstance(this);
            Location location = locationManager.getLastLocation();

            try {
                strLatitud = String.valueOf(location.getLatitude());
                strLongitud = String.valueOf(location.getLongitude());
                strAltitud = String.valueOf(location.getAltitude());
                strVelocidad = String.valueOf(location.getSpeed());
            } catch (Exception ex) {
            }

            insertar_estados_ingreso("3", ose_codigo_temp);
            manager.open();
            boolean tieneTrabajo = manager.existJobPreExecute(ose_codigo_temp, cliente_temp);
            manager.close();

            if (tipoUsuario == 1 && tieneTrabajo) {
                imprimirTirilla();
            }
        } else if (requestCode == 112 && resultCode == RESULT_OK) {
            if (tipoUsuario == 1) {//Lector

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
                iniciarConexion();
            }
        } else if (requestCode == 999 && resultCode == RESULT_FIRST_USER) {
            Bundle bolsa = data.getExtras();
            if (bolsa != null) {
                int estado = bolsa.getInt("estado");
                int orden = bolsa.getInt("orden");

                Intent i = new Intent(this, VerOrden.class);
                i.putExtra("ose_codigo", 0);
                i.putExtra("elemento", "33333");
                i.putExtra("direccion", "direccion");
                i.putExtra("producto", "agua");
                i.putExtra("cuadrilla", codigo_cuadrilla);
                i.putExtra("ose_precarga", 0);
                i.putExtra("nombre", "Jasson");
                i.putExtra("tipo", 1);
                i.putExtra("theBtMacAddress", theBtMacAddress);
                i.putExtra("terminalKey", terminal);
                i.putExtra("imei", IMEI);
                i.putExtra("nombreKey", nombreCuadrilla);
                i.putExtra("estado", estado);
                i.putExtra("orden", orden);
                i.putExtra("MODEL", Build.MODEL);
                i.putExtra("IMEI", IMEI);
                i.putExtra("VERSION", VERSION);

                System.gc();
                startActivityForResult(i, 999);
            }
        }
    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        switch (arg0.getId()) {
            case R.id.buttonBuscarOrden:
                //cargarOrdenes(etBuscar.getText().toString(),spOrdenEstado.getSelectedItemPosition(), (String)spOrdenEjecucion.getSelectedItem(),strCiclo, strRuta);
                consultarOrdenesListadas(etBuscar.getText().toString().trim());
                break;
        }
    }

    @Override
    public void finish() {
        if (salidaActivity) {
            Intent i = new Intent(this, EjecucionOrdenes.class);
            this.setResult(RESULT_OK, i);
            super.finish();
        }
        System.out.println("Intenta cerrar la apk");
    }

    /*
     * Clase Timer controla el envio automatico
     */
    class MyTimerTask extends TimerTask {
        public void run() {
            iteradorTimer++;
            System.out.println("Envio Automatico " + iteradorTimer);
            insertar_estados_ingreso("17", 0);
            enviarInformacion();
        }
    }//Cierra Clase MyTimerTask

    /*
     * Clase Timer controla consulta ordenes asignadas
     */
    class MyTimerTaskCargarTrabajo extends TimerTask {
        public void run() {
            iteradorTimer++;
            System.out.println("Cargue ordenes nuevas " + iteradorTimer);
            insertar_estados_ingreso("20", 0);
            cargarOrdenesPendintes();
        }
    }//Cierra Clase MyTimerTask

    /**
     * Abre el activity para visualizar el stock de materiales
     */
    public void visualizarStockMateriales() {
        Intent int1 = new Intent(this, OrdenMateriales.class);
        Bundle bolsa = new Bundle();
        bolsa.putInt("cuadrilla", codigo_cuadrilla);
        int1.putExtras(bolsa);
        //startActivityForResult(int1,222);
        startActivity(int1);
    }// Cierre visualizarStockMateriales

    /**
     * Abre el activity para ver la Orden de Servicio a partir de un ITEM
     *
     * @param item
     */
    public void abrirOrden(Item item) {
        //Intent i = new Intent("com.example.gestiondeltec.VerFormato");
        if (orden_pendiente) {
            // sending data to new activity

            ose_codigo_temp = item.getose_codigo();
            orden_id = item.getOrdenId();
            rutaOrden = item.getRutaOrden();
            cliente_temp = item.getCliContrato();
            insertar_estados_ingreso("2", ose_codigo_temp);
            //Intent i = new Intent(this,VerFormato.class);

            int tipoOrden = 1;//LECTURA
            if (spOrdenEstado.getSelectedItemPosition() == 2)
                tipoOrden = 2;//REVISION

            Intent i = new Intent(this, VerLectura.class);
            i.putExtra("ose_codigo", ose_codigo_temp);
            i.putExtra("orden_id", orden_id);
            i.putExtra("ruta_orden", rutaOrden);
            i.putExtra("elemento", item.getElemento());
            i.putExtra("direccion", item.getdireccion());
            i.putExtra("producto", item.getProducto());
            i.putExtra("cuadrilla", codigo_cuadrilla);
            i.putExtra("ose_precarga", item.getOse_precarga());
            i.putExtra("cli_contrato", item.getCliContrato());
            i.putExtra("ruta", item.getRuta());
            i.putExtra("consecutivo", item.getConsecutivo());
            i.putExtra("estado_fens", item.getEstado_fens());
            i.putExtra("tipo", tipoUsuario);
            i.putExtra("tipo_orden", tipoOrden);
            i.putExtra("consumo", item.getConsumo());
            i.putExtra("MODEL", Build.MODEL);
            i.putExtra("IMEI", IMEI);
            i.putExtra("VERSION", VERSION);

            startActivityForResult(i, 111);
        } else
            Toast.makeText(getApplicationContext(), item.getProducto() + " al Medidor:" + item.getElemento(), Toast.LENGTH_SHORT).show();
    }//Cierra AbrirOrden

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
        String direccion, elemento, barrio, producto, info_add, cli_nombre;
        int ose_codigo_tmp, ose_precarga, cli_contrato, ruta, consecutivo;
        String tip_orden = "209", consumo = "", ordenId = "", rutaOrden = "", estado_fens = "";

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

        items.clear();

        managerConsulta.open();
        cursor = managerConsulta.cargarCursorOrdenesCicloRuta(String.valueOf(codigo_cuadrilla), "", String.valueOf(estado), orden, cicloStr, rutaStr, tip_orden);
        if (cursor.moveToFirst()) {
            do {

                ordenId = cursor.getString(cursor.getColumnIndex("ORDEN_ID"));
                rutaOrden = cursor.getString(cursor.getColumnIndex("RUTA_ORDEN"));
                direccion = cursor.getString(cursor.getColumnIndex("direccion1"));
                elemento = cursor.getString(cursor.getColumnIndex("elemento"));
                barrio = cursor.getString(cursor.getColumnIndex("barrio"));
                ose_codigo_tmp = cursor.getInt(cursor.getColumnIndex("ose_codigo"));
                producto = cursor.getString(cursor.getColumnIndex("producto"));
                info_add = cursor.getString(cursor.getColumnIndex("ciu_nombre"));
                ose_precarga = cursor.getInt(cursor.getColumnIndex("ose_precarga"));
                cli_contrato = cursor.getInt(cursor.getColumnIndex("cli_contrato"));
                cli_nombre = cursor.getString(cursor.getColumnIndex("cli_nombre"));
                ruta = cursor.getInt(cursor.getColumnIndex("ruta"));
                consecutivo = cursor.getInt(cursor.getColumnIndex("ruta_cons"));
                consumo = cursor.getString(cursor.getColumnIndex("consumo"));
                estado_fens = cursor.getString(cursor.getColumnIndex("estado_fens"));

                items.add(new Item(ordenId, rutaOrden, direccion, elemento, barrio, producto, info_add, ose_codigo_tmp, ose_precarga, cli_contrato, cli_nombre, ruta, consecutivo, consumo, estado_fens));
            } while (cursor.moveToNext());//accessing data upto last row from table
        }

        lista.invalidateViews();

        cursor.close();
        managerConsulta.close();

        actualizarPendientes();
        System.gc();
    }// Cierra cargarOrdenes

    /**
     * Buscar una orden en el listview de ordenes
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

        if (!lista.getAdapter().isEmpty()) {
            for (int idx = indiceInicial; idx < lista.getAdapter().getCount(); idx++) {
                Item item = (Item) lista.getAdapter().getItem(idx);
                strMedidor = item.getElemento();
                strContrato = String.valueOf(item.getCliContrato());
                if (strMedidor.contains(busqueda) || strContrato.contains(busqueda)) {
                    lista.setSelection(idx);
                    indiceInicial = idx + 1;
                    break;
                } else if (idx == (lista.getAdapter().getCount() - 1)) {
                    indiceInicial = 0;
                    Toast.makeText(getApplicationContext(), "No encontrado", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * Actualiza Totales ordenes, ejecutadas, anuladas, enviadas, pendientes
     */
    public Bundle actualizarPendientes() {
        String codigo_cuadrilla_str = String.valueOf(codigo_cuadrilla);
        Bundle bolsa;
        managerConsulta.open();
        bolsa = managerConsulta.reporteOredenes(codigo_cuadrilla_str);
        managerConsulta.close();
        if (!bolsa.isEmpty()) {
            tvTotalOrdenes.setText(String.valueOf(bolsa.getInt("total")));
            tvTotalEjectudadas.setText(String.valueOf(bolsa.getInt("ejecutadas")));
            tvTotalPendientes.setText(String.valueOf(bolsa.getInt("pendientes")));
            tvTotalEnviadas.setText(String.valueOf(bolsa.getInt("enviadas")));
            if (strRuta != "") {
                tvDatosTrabajo.setText("Ruta " + strRuta + " /" + String.valueOf(items.size()));
            } else {
                tvDatosTrabajo.setText("Ciclo " + String.valueOf(bolsa.getInt("ciclo") + " /" + String.valueOf(items.size())));
            }
        }
        return bolsa;
    }// Cierra actualizarPendientes

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////    INICIO GPS   /////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Inserta estados GPS a ordenes
     *
     * @param estado
     * @param ose_codigo
     */
    public void insertar_estados_ingreso(String estado, int ose_codigo) {

        level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        batteryPct = level / (float) scale;

        manager.open();
        manager.insertarGps(String.valueOf(terminal), String.valueOf(codigo_cuadrilla), strLatitud, strLongitud, strAltitud, strVelocidad, IMEI, estado, String.valueOf(ose_codigo), String.valueOf(level));
        manager.close();
    }// Cierra insertar_estados_ingreso


    public void envio_ordenes() throws Exception {

        level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        batteryPct = level / (float) scale;

        manager.open();
        String strUsuario = "REVMVEVD";
        String strClave = "cGVyZWlyYQ==";
        Call<Auth> call = new ApiClient().getApiClient().create(ApiServices.class).postAuth(strUsuario, strClave);
        call.enqueue(new Callback<Auth>() {
            @Override
            public void onResponse(Call<Auth> call, Response<Auth> response) {

                if (response.isSuccessful() && response.body() != null) {
                    SharedPreferences preferences = MyApplication.getContext().getSharedPreferences("credenciales", Context.MODE_PRIVATE);
                    int intSesionId = response.body().getIntSesionId();
                    String strToken = response.body().getStrToken();
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt("intSesionId", intSesionId);
                    editor.putString("strToken", strToken);
                    editor.commit();
                    System.out.println(response.body().toString());
                    System.out.println(intSesionId);
                    System.out.println(strToken);
                    try {
                        manager.open();
                        manager.envioInformacionServidor(intSesionId, strToken);
                        manager.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Auth> call, Throwable t) {

            }

        });

        manager.close();
    }// Cierra insertar_estados_ingreso

    @Override
    public void onLocationChanged(Location location) {
        //Do cool stuff here
        strLatitud = String.valueOf(location.getLatitude());
        strLongitud = String.valueOf(location.getLongitude());
        strAltitud = String.valueOf(location.getAltitude());
        strVelocidad = String.valueOf(location.getSpeed());

        System.out.println("GPS latitud5: " + location.getLatitude());
        System.out.println("GPS longitud5: " + location.getLongitude());
        actualizarGps();
    }

    /**
     * Actualiza estados GPS
     */
    public void actualizarGps() {
        level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        batteryPct = level / (float) scale;

        manager.open();
        manager.insertarGps(String.valueOf(terminal), String.valueOf(codigo_cuadrilla), strLatitud, strLongitud, strAltitud, strVelocidad, IMEI, "0", "0", String.valueOf(level));
        manager.close();
    }// Cierre actualizarGps

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////    FIN GPS   ////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Cierra el activity o envia el trabajo pendiente
     */
    private void finalizarEnviar() {

        AlertDialog.Builder invalid_input_dialog = new AlertDialog.Builder(this);
        invalid_input_dialog.setTitle("ENVIAR LECTURAS " + tvTotalPendientes.getText().toString() + " PENDIENTES / SALIR ")
                .setMessage("DESEA ENVIAR LETURAS PENDIENTES O SALIR DEL APLICATIVO")
                .setCancelable(true)
                .setPositiveButton("ENVIAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        insertar_estados_ingreso("18", 0);

                        if (banderaEnvio) {
                            launchBarDialog();
                        } else
                            Toast.makeText(getApplicationContext(), "Actualmente se encuentra enviando informacion, por favor espere", Toast.LENGTH_SHORT).show();
                        System.out.println("ATUALIZANDO STOCK DE MATERIALES");
                    }
                })
                .setNegativeButton("SALIR", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        desloguear();
                    }
                })
                .show();
    }// Cierra finalizarEnviar


    public void viewReadReport() {
        LayoutInflater inflater = this.getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_reporte_lector, null);
        TextView tvRepTotalOrdenes = (TextView) layout.findViewById(R.id.tvRepTotalOrdenes);
        TextView tvRepTotalFotos = (TextView) layout.findViewById(R.id.tvRepTotalFotos);
        TextView tvRepTotalFotosEnvi = (TextView) layout.findViewById(R.id.tvRepTotalFotosEnvi);
        TextView tvRepTotalEnviadas = (TextView) layout.findViewById(R.id.tvRepTotalEnviadas);
        TextView tvRepTotalPendientes = (TextView) layout.findViewById(R.id.tvRepTotalPendientes);
        TextView tvRepTotalRevEnviadas = (TextView) layout.findViewById(R.id.tvRepTotalRevEnviadas);
        TextView tvRepTotalRevisiones = (TextView) layout.findViewById(R.id.tvRepTotalRevisiones);
        BarChart barChart = (BarChart) layout.findViewById(R.id.BarChartReport);

        Bundle bolsa = actualizarPendientes();
        int totalOrdenes = 0, totalFotos = 0, totalFotosEnviadas = 0,
                totalEnviadas = 0, totalPendientes = 0, totalRevEnviadas = 0, totalRevisiones = 0;

        if (!bolsa.isEmpty()) {
            totalOrdenes = bolsa.getInt("total");
            totalPendientes = bolsa.getInt("pendientes");
            totalEnviadas = bolsa.getInt("enviadas");
            totalRevisiones = bolsa.getInt("revisiones");
            totalRevEnviadas = bolsa.getInt("rev_enviadas");
            totalFotos = bolsa.getInt("total_fotos");
            totalFotosEnviadas = bolsa.getInt("fotos_enviadas");
        }

        tvRepTotalOrdenes.setText(String.valueOf(totalOrdenes));
        tvRepTotalFotos.setText(String.valueOf(totalFotos));
        tvRepTotalFotosEnvi.setText(String.valueOf(totalFotosEnviadas));
        tvRepTotalEnviadas.setText(String.valueOf(totalEnviadas));
        tvRepTotalPendientes.setText(String.valueOf(totalPendientes));
        tvRepTotalRevEnviadas.setText(String.valueOf(totalRevEnviadas));
        tvRepTotalRevisiones.setText(String.valueOf(totalRevisiones));

        ArrayList<BarEntry> entries = new ArrayList<BarEntry>();
        entries.add(new BarEntry(totalOrdenes, 0));
        entries.add(new BarEntry(totalPendientes, 1));
        entries.add(new BarEntry(totalEnviadas, 2));
        entries.add(new BarEntry(totalFotosEnviadas, 3));
        entries.add(new BarEntry(totalRevisiones, 4));
        //entries.add(new BarEntry(totalRevEnviadas, 5));


        BarDataSet dataset = new BarDataSet(entries, "# of Calls");
        dataset.setBarSpacePercent(5f);
        ArrayList<String> labels = new ArrayList<String>();
        labels.add("Total");
        labels.add("Pend");
        labels.add("Envi");
        labels.add("Foto");
        labels.add("Revi");
        //labels.add("Envi");

        dataset.setColors(ColorTemplate.JOYFUL_COLORS);
        BarData data = new BarData(labels, dataset);
        barChart.setData(data);
        barChart.getLegend().setEnabled(false);
        barChart.setDescription("");
        barChart.getAxisLeft().setDrawLabels(false);
        barChart.getAxisRight().setDrawLabels(false);
        barChart.animateY(500);
        barChart.invalidate();

        AlertDialog MyDialog;
        AlertDialog.Builder MyBuilder = new AlertDialog.Builder(this);
        MyBuilder.setTitle("Reporte Pendientes");
        MyBuilder.setView(layout);
        MyBuilder.setPositiveButton("Regresar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        MyDialog = MyBuilder.create();
        MyDialog.show();
    }


    /**
     * Sale del intent
     */
    public void desloguear() {
        if (myTimer != null) {
            myTimer.cancel();
            myTask.cancel();
        }
        insertar_estados_ingreso("4", 0);
        System.out.println("MATO EL TIMER");
        salidaActivity = true;
        this.finish();
    }// Cierre desloguear

    /**
     * Envia datos al server
     */
    private void enviarDatosServer() {

        AlertDialog.Builder invalid_input_dialog = new AlertDialog.Builder(this);
        invalid_input_dialog.setTitle("ENVIAR LECTURAS " + tvTotalEjectudadas.getText().toString() + " EJECUTADAS ")
                .setMessage("OJO POR RECOMENDACION DEBE ENVIAR ENVIAR CADA 2 HORAS, DESEA ENVIAR " + tvTotalEjectudadas.getText().toString() + " LECTURAS PENDIENTES ")
                .setCancelable(true)
                .setPositiveButton("ENVIAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        insertar_estados_ingreso("18", 0);
                        if (banderaEnvio) {
                            launchBarDialog();
                        } else
                            Toast.makeText(getApplicationContext(), "Actualmente se encuentra enviando informacion, por favor espere", Toast.LENGTH_SHORT).show();
                        System.out.println("ATUALIZANDO LECTURAS A ENVIAR");
                    }
                })
                .setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                    }
                })
                .show();
    }// Cierre enviarDatosServer

    /**
     * Lanza el envio de ordenes pendientes por un dialog
     */
    public void launchBarDialog() {
        barProgressDialog = new ProgressDialog(this);

        barProgressDialog.setTitle("ENVIO TRABAJO EJECUTADO ...");
        barProgressDialog.setMessage("Enviando Ordenes ...");
        barProgressDialog.setProgressStyle(barProgressDialog.STYLE_HORIZONTAL);
        barProgressDialog.setProgress(0);
        barProgressDialog.setMax(20);
        barProgressDialog.show();

        banderaEnvio = false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Cursor cursor;
                    int ose_codigo, tamano = 0, progreso = 0, actualizadas = 0, id_foto = 0;
                    boolean actualizoOrden = false;
                    String strLecturas = "", fecha = "";

                    EjecucionOrdenesModel managerOrden = new EjecucionOrdenesModel(getBaseContext());

                    managerOrden.open();
                    cursor = managerOrden.ordenesPendintes(1);

                    if (cursor.moveToFirst()) {
                        EjecucionOrdenesModelServer managerServerEnvio = new EjecucionOrdenesModelServer(tipoUsuario);
                        tamano = cursor.getCount();
                        barProgressDialog.setProgress(0);//Ponemos la barra de progreso a 0
                        barProgressDialog.setMax(10);//El m�ximo de la barra de progreso son 60, de los 60 segundo que va a durar la tarea en segundo plano.
                        progreso = 0;

                        strLecturas = generarArregloLecturas(cursor);
                        actualizadas = managerServerEnvio.ejecutar_consulta(strLecturas, tamano, tipoUsuario);

                        Thread.sleep(50);
                        updateBarHandler.post(new Runnable() {
                            public void run() {
                                barProgressDialog.incrementProgressBy(6);
                                barProgressDialog.setMessage("Enviando Lecturas ...");
                            }
                        });

                        if (actualizadas == tamano) {
                            managerOrden.updateSincronizado_lecturas(actualizarEstadoLecturas(cursor));
                        } else {
                            guardarArchivo("SELECT InsertUpdateLectura4('" + strLecturas + "'," + tamano + ")");
                        }

                        Thread.sleep(50);
                        updateBarHandler.post(new Runnable() {
                            public void run() {
                                barProgressDialog.incrementProgressBy(4);
                                barProgressDialog.setMessage("Actualizando Lecturas ...");
                                actualizarPendientes();
                            }
                        });
                    }

                    cursor = managerOrden.ordenesPendintes(2);
                    if (cursor.moveToFirst()) {
                        EjecucionOrdenesModelServer managerServerEnvio = new EjecucionOrdenesModelServer(tipoUsuario);
                        tamano = cursor.getCount();
                        barProgressDialog.setProgress(0);//Ponemos la barra de progreso a 0
                        barProgressDialog.setMax(10);//El m�ximo de la barra de progreso son 60, de los 60 segundo que va a durar la tarea en segundo plano.
                        progreso = 0;

                        strLecturas = generarArregloLecturas(cursor);
                        actualizadas = managerServerEnvio.ejecutar_consulta(strLecturas, tamano, 3);//TipoUsuario Envio 3 como Revisiones

                        Thread.sleep(50);
                        updateBarHandler.post(new Runnable() {
                            public void run() {
                                barProgressDialog.incrementProgressBy(6);
                                barProgressDialog.setMessage("Enviando Revisiones ...");
                            }
                        });

                        if (actualizadas == tamano) {

                            managerOrden.updateSincronizado_lecturas(actualizarEstadoLecturas(cursor));
                        }

                        Thread.sleep(50);
                        updateBarHandler.post(new Runnable() {
                            public void run() {
                                barProgressDialog.incrementProgressBy(4);
                                barProgressDialog.setMessage("Actualizando Lecturas ...");
                                actualizarPendientes();
                            }
                        });
                    }

                    System.gc();

                    String foto_url;
                    cursor = managerOrden.fotosPendintes();
                    if (cursor.moveToFirst()) {
                        EjecucionOrdenesModelServer managerServerEnvio = new EjecucionOrdenesModelServer(tipoUsuario);
                        tamano = cursor.getCount();
                        barProgressDialog.setProgress(0);//Ponemos la barra de progreso a 0
                        barProgressDialog.setMax(tamano);//El m�ximo de la barra de progreso son 60, de los 60 segundo que va a durar la tarea en segundo plano.
                        progreso = 0;

                        do {
                            ose_codigo = cursor.getInt(cursor.getColumnIndex("ose_codigo"));
                            fecha = cursor.getString(cursor.getColumnIndex("fecha"));
                            foto_url = cursor.getString(cursor.getColumnIndex("foto_url"));
                            id_foto = cursor.getInt(cursor.getColumnIndex("_id"));
                            Thread.sleep(100);
                            if (managerOrden.existeFotoPendiente(String.valueOf(id_foto))) {//La foto esta pendiente por enviar
                                actualizoOrden = managerServerEnvio.insertarFoto(ose_codigo, fecha, foto_url);
                                System.out.println("FOTO POSTGRES ose_codigo " + ose_codigo);
                                if (actualizoOrden) {
                                    managerOrden.actualizarEnvioFoto(String.valueOf(id_foto));
                                    System.out.println("ACTUALIZO ENVIO FOTO ose_codigo " + ose_codigo);
                                }
                                Thread.sleep(100);
                            }

                            progreso++;
                            if (progreso < tamano) {
                                Thread.sleep(5);

                                updateBarHandler.post(new Runnable() {
                                    public void run() {
                                        barProgressDialog.incrementProgressBy(1);
                                        barProgressDialog.setMessage("Enviando fotos ...");
                                    }
                                });
                            } else {
                                barProgressDialog.dismiss();
                                System.gc();
                            }

                        } while (cursor.moveToNext());//accessing data upto last row from table
                    }
                    cursor.close();
                    managerOrden.close();
                    barProgressDialog.dismiss();
                    banderaEnvio = true;

                } catch (Exception e) {
                    barProgressDialog.dismiss();
                    banderaEnvio = true;
                }
            }
        }).start();
        actualizarPendientes();
    }// Cierra launchBarDialog Barra de envio de ordenes

    private static final String IMAGE_DIRECTORY_NAME = "FilesAccount";

    public void guardarArchivo(String contenido) {
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create " + IMAGE_DIRECTORY_NAME + " directory");

            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "report_" + timeStamp + ".bat");

        FileOutputStream stream;
        try {
            stream = new FileOutputStream(mediaFile);
            stream.write(contenido.getBytes());
            stream.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Envia ordenes sin barra de envio ni fotos
     */
    public void enviarInformacion() {


        if (banderaEnvio) {
            banderaEnvio = false;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Cursor cursor;
                        int ose_codigo, tamano = 0, actualizadas = 0, id_foto = 0;
                        boolean actualizoOrden = false;
                        String strLecturas = "", fecha = "";

                        EjecucionOrdenesModel managerOrden = new EjecucionOrdenesModel(getBaseContext());
                        managerOrden.open();
                        cursor = managerOrden.ordenesPendintes(1);

                        if (cursor.moveToFirst()) {
                            EjecucionOrdenesModelServer managerServerEnvio = new EjecucionOrdenesModelServer(tipoUsuario);
                            tamano = cursor.getCount();
                            strLecturas = generarArregloLecturas(cursor);
                            actualizadas = managerServerEnvio.ejecutar_consulta(strLecturas, tamano, tipoUsuario);

                            Thread.sleep(50);
                            if (actualizadas == tamano) {
                                System.out.println("Envio " + tamano + " Lecturas");
                                managerOrden.updateSincronizado_lecturas(actualizarEstadoLecturas(cursor));
                                System.out.println("Actualizo " + tamano + " Lecturas BD local");
                            }
                        }
                        System.gc();

                        String foto_url;
                        cursor = managerOrden.fotosPendintes();
                        if (cursor.moveToFirst()) {
                            EjecucionOrdenesModelServer managerServerEnvio = new EjecucionOrdenesModelServer(tipoUsuario);
                            do {
                                ose_codigo = cursor.getInt(cursor.getColumnIndex("ose_codigo"));
                                fecha = cursor.getString(cursor.getColumnIndex("fecha"));
                                foto_url = cursor.getString(cursor.getColumnIndex("foto_url"));
                                id_foto = cursor.getInt(cursor.getColumnIndex("_id"));
                                Thread.sleep(100);
                                if (managerOrden.existeFotoPendiente(String.valueOf(id_foto))) {
                                    //La foto esta pendiente por enviar
                                    actualizoOrden = managerServerEnvio.insertarFoto(ose_codigo, fecha, foto_url);
                                    System.out.println("FOTO POSTGRES ose_codigo " + ose_codigo);
                                    if (actualizoOrden) {
                                        managerOrden.actualizarEnvioFoto(String.valueOf(id_foto));
                                        System.out.println("ACTUALIZO ENVIO FOTO ose_codigo " + ose_codigo);
                                    }
                                    Thread.sleep(100);
                                }
                            } while (cursor.moveToNext());//accessing data upto last row from table
                        }
                        cursor.close();
                        managerOrden.close();
                        banderaEnvio = true;

                    } catch (Exception e) {
                    }
                }
            }).start();

        }
    }// Cierra enviarInformacion

    /**
     * Genera un arreglo enviado al PL con todas las lecturas pendientes
     *
     * @param cursor Cursor con las lecturas pendientes
     * @return String con las lecturas para el PL
     */
    private String generarArregloLecturas(Cursor cursor) {
        String salida = "";
        int ose_codigo;
        String lectura;
        int codigo_observacion_no_lectura;
        int codigo_observacion_lectura;
        String observacion_no_lectura;
        int indicador_lectura;
        int critica;
        String fecha;
        String fecha_actualizacion;
        int intentos;
        int encontro_medidor;
        int medidor_correcto;
        String serie_medidor_encontrado;
        int actividad;
        String sellos_instalados;
        String motivo_ejecucion;
        int retiro_acometida;
        int cli_contrato;
        String datos_retiro_acometida;
        int reconexion_no_autorizada;
        String censo_carga, latitud, longitud;
        String fecha_envio = getDateTime();
        String desviacion = "0";
        int consumo = 0;

        int i = 0;

        int topeLecturas = cursor.getCount();

        do {
            ose_codigo = cursor.getInt(cursor.getColumnIndex("ose_codigo"));
            lectura = cursor.getString(cursor.getColumnIndex("lectura"));
            codigo_observacion_no_lectura = cursor.getInt(cursor.getColumnIndex("codigo_observacion_no_lectura"));
            codigo_observacion_lectura = cursor.getInt(cursor.getColumnIndex("codigo_observacion_lectura"));
            observacion_no_lectura = cursor.getString(cursor.getColumnIndex("observacion_no_lectura"));
            indicador_lectura = cursor.getInt(cursor.getColumnIndex("indicador_lectura"));
            critica = cursor.getInt(cursor.getColumnIndex("critica"));
            fecha = cursor.getString(cursor.getColumnIndex("fecha_creacion"));
            fecha_actualizacion = cursor.getString(cursor.getColumnIndex("fecha_actualizacion"));
            intentos = cursor.getInt(cursor.getColumnIndex("intentos"));
            encontro_medidor = cursor.getInt(cursor.getColumnIndex("encontro_medidor"));
            medidor_correcto = cursor.getInt(cursor.getColumnIndex("medidor_correcto"));
            serie_medidor_encontrado = cursor.getString(cursor.getColumnIndex("medidor_encontrado"));
            actividad = cursor.getInt(cursor.getColumnIndex("actividad"));
            sellos_instalados = cursor.getString(cursor.getColumnIndex("sellos_instalados"));
            motivo_ejecucion = cursor.getString(cursor.getColumnIndex("motivo_ejecucion"));
            retiro_acometida = cursor.getInt(cursor.getColumnIndex("retiro_acometida"));
            datos_retiro_acometida = cursor.getString(cursor.getColumnIndex("datos_retiro_acometida"));
            reconexion_no_autorizada = cursor.getInt(cursor.getColumnIndex("reconexion_no_autorizada"));
            cli_contrato = cursor.getInt(cursor.getColumnIndex("cli_contrato"));
            censo_carga = "0";
            latitud = cursor.getString(cursor.getColumnIndex("latitud"));
            longitud = cursor.getString(cursor.getColumnIndex("longitud"));
            consumo = cursor.getInt(cursor.getColumnIndex("consumo"));
            desviacion = cursor.getString(cursor.getColumnIndex("reconexion_no_autorizada"));

            salida += ose_codigo + ";" + lectura + ";" + codigo_observacion_no_lectura + ";" + codigo_observacion_lectura + ";" +
                    observacion_no_lectura + ";" + indicador_lectura + ";" + critica + ";" + fecha + ";" + fecha_envio + ";" + fecha_actualizacion + ";" +
                    /*lme_lectura_1*/ ";" + /*lme_lectura_2*/ ";" +/*lme_lectura_3*/ ";" + intentos + ";" + desviacion + ";"
                    + consumo + ";" /*+ medidor_correcto + ";" + serie_medidor_encontrado  + ";"*/ + latitud + ";" + longitud;
            if (i < topeLecturas - 1) {
                salida += "||";
            }
            i++;

        } while (cursor.moveToNext());//accessing data upto last row from table

        return salida;
    }// Cierre generarArregloLecturas

    /**
     * Actualiza el estado de las lecturas enviadas (topeLecturas)
     *
     * @param cursor con lecturas enviadas
     * @return String de ose_codigos para el update
     */
    private String actualizarEstadoLecturas(Cursor cursor) {
        String salida = "";
        int ose_codigo;

        int i = 0;

        int topeLecturas = cursor.getCount();
        if (cursor.moveToFirst()) {

            do {
                ose_codigo = cursor.getInt(cursor.getColumnIndex("ose_codigo"));

                salida += ose_codigo;
                if (i < topeLecturas - 1) {
                    salida += ",";
                }
                i++;

            } while (cursor.moveToNext());//accessing data upto last row from table
        }
        cursor.close();

        return salida;
    }// Cierra actualizarEstadoLecturas

    /**
     * Obtener la fecha y hora actual
     *
     * @return String con la fecha actual
     */
    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    } // Cierre obtener Hora

    /**
     * Abre el activity para visualizar el stock de materiales
     */
    public void visualizarSeguimiento() {
        Intent int1 = new Intent(this, SeguimientoOrdenes.class);
        Bundle bolsa = new Bundle();
        bolsa.putInt("cuadrilla", codigo_cuadrilla);
        int1.putExtras(bolsa);
        //startActivityForResult(int1,222);
        startActivity(int1);
    }// Cierre visualizarStockMateriales

    /**
     * Envia ordenes sin barra de envio ni fotos
     */
    public void cargarOrdenesPendintes() {

        if (flagUploadJob) {
            flagUploadJob = false;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        ResultSet rs;
                        String strListaOrdenes = "";
                        EjecucionOrdenesModelServer managerServerEnvio = new EjecucionOrdenesModelServer(tipoUsuario);
                        EjecucionOrdenesModel managerOrden = new EjecucionOrdenesModel(getBaseContext());

                        managerOrden.open();
                        strListaOrdenes = managerOrden.getOrdenesCuadrilla(String.valueOf(codigo_cuadrilla));
                        rs = managerServerEnvio.getOrdenesAgregadas(String.valueOf(codigo_cuadrilla), strListaOrdenes);
                        managerOrden.close();
                        final int upload = insertarOrdenesServicio(rs, 1);

                        if (upload > 0) {
                            getOrdenHandler.post(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "SE LE CARGARON " + upload + " lecturas para SUPERVISAR", Toast.LENGTH_LONG).show();
                                    showNotification(nombreCuadrilla + ", " + upload + " lecturas para SUPERVISAR se le cargan", "CARGA LECTURAS SUPERVISAR");
                                    cargarOrdenes(etBuscar.getText().toString(), spOrdenEstado.getSelectedItemPosition(), (String) spOrdenEjecucion.getSelectedItem(), strCiclo, strRuta);
                                }
                            });
                        }

                        Thread.sleep(50);
                        System.gc();
                        flagUploadJob = true;

                    } catch (Exception e) {

                    }
                }
            }).start();
        }
    }// Cierra enviarInformacion

    /**
     * Show Dialog Builder for upload read check
     */
    public void uploadReadCheckJobDialog() {
        AlertDialog.Builder invalid_input_dialog = new AlertDialog.Builder(this);
        invalid_input_dialog.setTitle("CARGAR REVISIONES")
                .setMessage("OJO SOLO cargue revisiones despues de terminar sus rutas o cuando se lo indiquen")
                .setCancelable(true)
                .setPositiveButton("CONFIRMAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        uploadReadCheckJob();
                    }
                })
                .setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                    }
                })
                .show();
    }// End uploadReadCheckJobDialog

    public void openManagerAsistencia() {
        Intent i = new Intent(this, EjecucionAsistencia.class);
        Bundle bolsa = new Bundle();
        bolsa.putInt("cuadrilla", codigo_cuadrilla);
        bolsa.putString("nombreKey", nombreCuadrilla);

        i.putExtras(bolsa);
        startActivityForResult(i, 113);
    }// End uploadReadCheckJobDialog

    public void openManagerFormatos() {
        Intent i = new Intent(this, EjecucionFormatos.class);
        Bundle bolsa = new Bundle();
        bolsa.putInt("cuadrilla", codigo_cuadrilla);
        bolsa.putString("nombreKey", nombreCuadrilla);

        i.putExtras(bolsa);
        startActivityForResult(i, 114);
    }

    public void openManagerFormatos2() {
        Intent i = new Intent(this, ListarFormatos.class);
        Bundle bolsa = new Bundle();
        bolsa.putInt("cuadrilla", codigo_cuadrilla);
        bolsa.putString("nombreKey", nombreCuadrilla);

        i.putExtras(bolsa);
        startActivityForResult(i, 115);
    }

    public void openManagerBluetooh() {
        Intent i = new Intent(this, BluetoothDemo.class);
        startActivityForResult(i, 112);
    }// End uploadReadCheckJobDialog

    /**
     * Function upload job to check Read
     */
    public void uploadReadCheckJob() {

        barProgressDialog = new ProgressDialog(this);
        barProgressDialog.setTitle("CARGANDO VERIFICACIONES ...");
        barProgressDialog.setMessage("Cargando ordenes ...");
        barProgressDialog.setProgressStyle(barProgressDialog.STYLE_HORIZONTAL);
        barProgressDialog.setProgress(0);
        barProgressDialog.setMax(200);
        barProgressDialog.show();
        System.gc();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ResultSet rs;
                    EjecucionOrdenesModelServer managerServerEnvio = new EjecucionOrdenesModelServer(tipoUsuario);
                    rs = managerServerEnvio.getReadCheckJob(String.valueOf(codigo_cuadrilla));
                    final int upload = insertarOrdenesServicio(rs, 2);
                    Thread.sleep(100);

                    if (upload > 0) {
                        updateBarHandler.post(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), "SE LE CARGARON " + upload + " lecturas para REVISAR", Toast.LENGTH_LONG).show();
                                cargarOrdenes(etBuscar.getText().toString(), spOrdenEstado.getSelectedItemPosition(), (String) spOrdenEjecucion.getSelectedItem(), strCiclo, strRuta);
                            }
                        });
                    }
                    System.gc();
                    barProgressDialog.dismiss();

                } catch (Exception e) {
                    barProgressDialog.dismiss();
                }
            }
        }).start();
    }// End uploadReadCheckJob

    /**
     * Insertar Ordenes de rs ResultSet en Base de datos
     *
     * @param rs
     */
    private int insertarOrdenesServicio(ResultSet rs, int callType) {
        int tamano = 0;
        try {
            int i_ose_codigo = rs.findColumn("ose_codigo");
            int i_ose_precarga = rs.findColumn("ose_precarga");
            int i_tip_orden = rs.findColumn("ose_tip_codigo");//LECTURA
            int i_cli_contrato = rs.findColumn("cli_contrato");
            int i_cli_nombre = rs.findColumn("cli_nombre");
            int i_ciu_nombre = rs.findColumn("ciu_nombre");
            int i_direccion1 = rs.findColumn("direccion1");
            int i_direccion2 = rs.findColumn("direccion2");
            int i_producto = rs.findColumn("producto");
            int i_barrio = rs.findColumn("barrio");
            int i_ciclo = rs.findColumn("ciclo");
            int i_ose_ruta = rs.findColumn("ose_ruta");
            int i_ose_ruta_consecutivo = rs.findColumn("ose_ruta_consecutivo");
            int i_elemento = rs.findColumn("elemento");
            int i_lectura_anterior = rs.findColumn("lectura_anterior");//LECTURA
            int i_consumo_promedio = rs.findColumn("consumo_promedio");//LECTURA
            int i_lectura_actual = rs.findColumn("lectura_actual");//LECTURA
            int i_cantidad_digitos = rs.findColumn("cantidad_digitos");//LECTURA
            int i_tipo_producto = rs.findColumn("ose_tipo_producto");//LECTURA
            int i_franja = rs.findColumn("consumo");//LECTURA
            int i_estado_fens = rs.findColumn("ose_estado_fens");


            DataBaseManager managerOrden = new DataBaseManager(getBaseContext());

            int ose_codigo = 0, ose_precarga = 0, tip_orden = 0, cli_contrato = 0, ciclo = 0, ose_ruta = 0,
                    ose_ruta_consecutivo = 0, lectura_anterior = 0, consumo_promedio = 0, lectura_actual = 0;
            String cli_nombre = "", ciu_nombre = "", direccion1 = "", direccion2 = "", producto = "",
                    barrio = "", elemento = "", cantidad_digitos = "", tipo_producto = "", franja = "", estado_fens = "";
            int medidor = 0;

            rs.last(); //me voy al �ltimo
            tamano = rs.getRow(); //pillo el tama�o
            rs.beforeFirst(); // lo dejo donde estaba para tratarlo

            if (callType == 2) {
                barProgressDialog.setProgress(0);//Ponemos la barra de progreso a 0
                barProgressDialog.setMax(tamano + 1);//El m�ximo de la barra de progreso son 60, de los 60 segundo que va a durar la tarea en segundo plano.
            }

            managerOrden.open();
            while (rs.next()) {

                ose_codigo = rs.getInt(i_ose_codigo);
                ose_precarga = rs.getInt(i_ose_precarga);
                tip_orden = rs.getInt(i_tip_orden);
                cli_contrato = rs.getInt(i_cli_contrato);
                cli_nombre = rs.getString(i_cli_nombre);
                ciu_nombre = rs.getString(i_ciu_nombre);
                direccion1 = rs.getString(i_direccion1);
                direccion2 = rs.getString(i_direccion2);
                producto = rs.getString(i_producto);
                barrio = rs.getString(i_barrio);
                ciclo = rs.getInt(i_ciclo);
                ose_ruta = rs.getInt(i_ose_ruta);
                ose_ruta_consecutivo = rs.getInt(i_ose_ruta_consecutivo);
                elemento = rs.getString(i_elemento);
                lectura_anterior = rs.getInt(i_lectura_anterior);
                consumo_promedio = rs.getInt(i_consumo_promedio);
                lectura_actual = rs.getInt(i_lectura_actual);
                cantidad_digitos = rs.getString(i_cantidad_digitos);
                tipo_producto = rs.getString(i_tipo_producto);
                franja = rs.getString(i_franja);
                estado_fens = rs.getString(i_estado_fens);

                try {//CONVIERTO MEDIDOR EN NUMERICO
                    medidor = Integer.parseInt(elemento);
                    elemento = String.valueOf(medidor);
                } catch (NumberFormatException e) {

                }

                if (callType == 1) {
                    managerOrden.insertar_orden(ose_codigo, ose_precarga, tip_orden, cli_contrato, cli_nombre, ciu_nombre, direccion1, direccion2,
                            producto, barrio, ciclo, ose_ruta, ose_ruta_consecutivo, 1, "", codigo_cuadrilla, elemento, lectura_anterior, consumo_promedio,
                            lectura_actual, cantidad_digitos, tipo_producto, franja, 0, estado_fens);
                } else {
                    managerOrden.insertCheckRed(ose_codigo, ose_precarga, 210, cli_contrato, cli_nombre, ciu_nombre, direccion1, direccion2,
                            producto, barrio, ciclo, ose_ruta, ose_ruta_consecutivo, 27, "", codigo_cuadrilla, elemento, lectura_anterior, consumo_promedio,
                            lectura_actual, cantidad_digitos, tipo_producto, franja, 1, estado_fens);
                }

                if (callType == 2) {
                    updateBarHandler.post(new Runnable() {
                        public void run() {
                            barProgressDialog.incrementProgressBy(1);
                            barProgressDialog.setMessage("Cargando Rev-Lecturas ...");
                        }
                    });
                }
                Thread.sleep(100);
            }
            managerOrden.close();
        } catch (Exception e) {
        }
        return tamano;
    } // Cierre insertar Ordenes de rs ResultSet en Base de datos

    public void showNotification(String msg, String title) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getBaseContext())
                .setSmallIcon(android.R.drawable.stat_sys_warning)
                .setLargeIcon((((BitmapDrawable) getResources()
                        .getDrawable(R.drawable.ic_launcher)).getBitmap()))
                .setContentTitle(title)
                .setContentText(msg)
                .setContentInfo("4")
                .setTicker("Alerta!")
                .setAutoCancel(true);

        Intent notificationIntent = this.getIntent();// new Intent(getApplicationContext(), EjecucionOrdenes.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.setContentIntent(contentIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(NOTIF_ALERTA_ID, mBuilder.build());

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

    public void imprimirTirilla() {

        AlertDialog.Builder invalid_input_dialog = new AlertDialog.Builder(this);
        invalid_input_dialog.setTitle("IMPRIMIR PENDIENTES / SALIR ");

        final CharSequence[] opciones = new CharSequence[3];
        opciones[0] = "FACTURACION EN SITIO";
        opciones[1] = "CONSTANCIA DE LECTURA";
        opciones[2] = "MARCAR NO ENTREGADO";

        invalid_input_dialog.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (opciones[which] == "FACTURACION EN SITIO") {
                    if (theBtMacAddress == "") {

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


                    String arreglo = "";
                    String impresion = "";
                    byte[] arreglo2 = new byte[0];
                    int lengthRead = 2000;
                    String ruta_fens = rutaOrden;
                    Log.d("orden_id", orden_id);
                    Log.d("MI RUTA", "0" + rutaOrden);

                    Factura factura = new Factura(new DbHelper(MyApplication.getContext()).getDatabaseName(), ruta_fens, orden_id, "dd-MM-yyyy hh:mm:ss", "dd/MMM/yyyy", Integer.toString(lengthRead), MyApplication.getContext());
                    try {
                        arreglo2 = factura.obtenerFacturaByte();


//                arreglo =   factura.obtenerFactura() +"\r\n"
//                            + " LEFT \r\n"
//                            + " FORM \r\n"
//                            + " PRINT \r\n";
//                System.out.println(arreglo);

//                impresion = ""+ lengthRead +" 1\r\n"
//                            + arreglo2 + " \r\n"
//                            + " FORM \r\n"
//                            + " PRINT \r\n";

                    } catch (Exception e) {

                    }
                    //Log.d("ARREGLO: ", arreglo);

                    //System.out.println(arreglo);

                    sendCpclOverBluetooth(theBtMacAddress, arreglo2);
                    pairPrinter(theBtMacAddress, arreglo2);

                    cursor.close();
                    manager.close();
                }
                if (opciones[which] == "CONSTANCIA DE LECTURA") {
                    if (theBtMacAddress == "") {

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
                    int codigoObsLectura = 0, codigoNoLectura = 0, indicadorLectura = 0, suscriptor = 0;
                    String strMotivoNoLectura = "", strObsLectura = "";
                    int lengthRead2 = 520, countMed = 0;

                    String cpclData = "";

                    manager.open();
                    Cursor cursor = manager.consultaLecturaImprimirEmcali(String.valueOf(cliente_temp));

                    if (cursor.moveToFirst()) {
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
                        cpclData += "TEXT 0 0 15 " + inicial + " MATRICULA             " + fecha_lectura + "\r\n";//FECHA
                        inicial = inicial + 15;

                        //cpclData += strImprimirCampos(String.valueOf(suscriptor), inicial);
                        //inicial = inicial + factor;

                        cpclData += "TEXT 0 6 15 " + inicial + " " + suscriptor + "\r\n";//FECHA
                        inicial = inicial + factor + 20;

                        cpclData += "TEXT 0 0 15 " + inicial + " USUARIO       \r\n";//FECHA
                        inicial = inicial + 15;

                        cpclData += strImprimirCampos(cliente, inicial);
                        inicial = inicial + factor;

                        cpclData += "TEXT 0 0 15 " + inicial + " DIRECCION            Ruta:" + rutaImpr + "  Cons:" + conseImpr + "\r\n";//FECHA
                        inicial = inicial + 15;

                        cpclData += strImprimirCampos(direccion, inicial);
                        inicial = inicial + factor;

                        cpclData += "TEXT 0 0 15 " + inicial + " MEDIDOR        FRANJA:" + franja + "\r\n";//FECHA
                        inicial = inicial + 15;

                        cpclData += strImprimirCampos(medidor, inicial + 1);
                        inicial = inicial + factor;


                        cpclData += "TEXT 5 0 15 " + inicial + "  " + medidor + "\r\n";//FECHA
                        //cpclData += strImprimirCampos(medidor , inicial);//Matricula Cliente
                        inicial = inicial + factor;


                        if (indicadorLectura == 1) {
                            cpclData += "TEXT 0 2 15 " + inicial + " LECTURA:\r\n";//FECHA
                            //inicial = inicial + factor;

                            cpclData += "TEXT 0 6 45 " + (inicial + 5) + "  " + lectura + "\r\n";
                            inicial = inicial + factor + 25;
                            //cpclData += strImprimirCampos("LECTURA: " + lectura + " -" + servicio, inicial);//TITULO LECTURA
                        } else {
                            cpclData += "TEXT 0 2 15 " + inicial + " NO LECTURA:\r\n";//FECHA
                            inicial = inicial + factor;
                            strMotivoNoLectura = manager.getObsCodigo(String.valueOf(codigoNoLectura));
                            cpclData += strImprimirCampos(strMotivoNoLectura, inicial);//CAUSAL DE NO LECTURA
                            inicial = inicial + factor;
                        }

                        cpclData += "\r\n";

                        if (codigoObsLectura > 0) {
                            strObsLectura = manager.getObsCodigo(String.valueOf(codigoObsLectura));
                            if (strObsLectura.length() > 2 && !strObsLectura.contains("IRREGULAR")) {
                                cpclData += "TEXT 0 0 15 " + inicial + " OBSERVACION:\r\n";//FECHA
                                inicial = inicial + 15;

                                cpclData += strImprimirCampos(strObsLectura, inicial);//OBSERVACION LECTURA
                                cpclData += "\r\n";
                                inicial = inicial + factor;
                            }
                        }

                        if (cursor.moveToNext()) {
                            do {
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

                                if (indicadorLectura == 1) {
                                    cpclData += "TEXT 0 2 15 " + inicial + " LECTURA:\r\n";//FECHA
                                    //inicial = inicial + factor;

                                    cpclData += "TEXT 0 6 45 " + (inicial + 5) + "  " + lectura + "\r\n";
                                    inicial = inicial + factor + 25;
                                    //cpclData += strImprimirCampos("LECTURA: " + lectura + " -" + servicio, inicial);//TITULO LECTURA
                                } else {
                                    cpclData += "TEXT 0 2 15 " + inicial + " NO LECTURA:\r\n";//FECHA
                                    inicial = inicial + factor;
                                    strMotivoNoLectura = manager.getObsCodigo(String.valueOf(codigoNoLectura));
                                    cpclData += strImprimirCampos(strMotivoNoLectura, inicial);//CAUSAL DE NO LECTURA
                                    inicial = inicial + factor;
                                }

                                cpclData += "\r\n";

                                if (codigoObsLectura > 0) {
                                    strObsLectura = manager.getObsCodigo(String.valueOf(codigoObsLectura));
                                    if (strObsLectura.length() > 2 && !strObsLectura.contains("IRREGULAR")) {
                                        cpclData += "TEXT 0 0 15 " + inicial + " OBSERVACION:\r\n";//FECHA
                                        inicial = inicial + 15;

                                        cpclData += strImprimirCampos(strObsLectura, inicial);//OBSERVACION LECTURA
                                        cpclData += "\r\n";
                                        inicial = inicial + factor;
                                    }
                                }

                            } while (cursor.moveToNext());//accessing data upto last row from table
                        }
                    }

                    cpclData += "TEXT 0 0 15 " + inicial + "                                   lector:" + codigo_cuadrilla + "\r\n";//FECHA

                    cpclData += "LEFT \r\n"
                            + "FORM\r\n"
                            + "PRINT\r\n";


                    if (countMed == 0)
                        lengthRead2 = 400;

                    String head //= "! U1 JOURNAL\r\n ! U1 SETFF 50 2\r\n"
                            = "! 0 200 200 " + lengthRead2 + " 1\r\n"
                            + "PCX 200 43 !<PEREIRA1.PCX\r\n"
                            + "TEXT 5 0 15 25 ENERGIA DE PEREIRA\r\n"
                            + "TEXT 0 0 15 47 S.A.E.S.P.\r\n"
                            + "TEXT 0 2 15 60 CONSTANCIA DE LECTURA  \r\n"
                            + "TEXT 0 0 15 82 Nit: 816002019-9\r\n";


                    //System.out.println(cpclData);

                    final String salidaImprimir = head + cpclData;
                    sendCpclOverBluetooth2(theBtMacAddress, salidaImprimir);
                    cursor.close();
                    manager.close();

                }


                Toast.makeText(getApplicationContext(), "He seleccionado la " + opciones[which], Toast.LENGTH_SHORT).show();
            }
        });
        invalid_input_dialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog2 = invalid_input_dialog.create();
        alertDialog2.show();

        /*if(theBtMacAddress == ""){

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
        int lengthRead = 520, countMed = 0;

        String cpclData = "";

        manager.open();
        Cursor cursor = manager.consultaLecturaImprimirEmcali(String.valueOf(cliente_temp));

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

            *//*
            cpclData += "TEXT 5 0 15 " + inicial + "  " + medidor + "\r\n";//FECHA
            //cpclData += strImprimirCampos(medidor , inicial);//Matricula Cliente
            inicial = inicial + factor;
            *//*

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

        cpclData += "TEXT 0 0 15 " + inicial + "                                   lector:" + codigo_cuadrilla + "\r\n";//FECHA

        cpclData += "LEFT \r\n"
                + "FORM\r\n"
                + "PRINT\r\n";


        if(countMed == 0)
            lengthRead = 400;

        String head //= "! U1 JOURNAL\r\n ! U1 SETFF 50 2\r\n"
                = "! 0 200 200 "+ lengthRead +" 1\r\n"
                + "PCX 200 43 !<PEREIRA1.PCX\r\n"
                + "TEXT 5 0 15 25 ENERGIA DE PEREIRA\r\n"
                + "TEXT 0 0 15 47 S.A.E.S.P.\r\n"
                + "TEXT 0 2 15 60 CONSTANCIA DE LECTURA  \r\n"
                + "TEXT 0 0 15 82 Nit: 816002019-9\r\n";


        //System.out.println(cpclData);

        final String salidaImprimir = head + cpclData;


        if (orden_id != null){
            String arreglo = "";
            String impresion = "";
            byte[] arreglo2 ;
            lengthRead = 1000;
            String ruta_fens= "0"+rutaOrden;
            Log.d("orden_id", orden_id);
            Log.d("MI RUTA", "0"+rutaOrden);

            Factura factura = new Factura( new DbHelper(MyApplication.getContext()).getDatabaseName(), ruta_fens, orden_id, "dd/MMM/yyyy", Integer.toString(lengthRead), MyApplication.getContext());
            try {
               //arreglo2 = factura.obtenerFacturaByte();
                //System.out.println(arreglo2);
//               impresion = arreglo2
//                           + " LEFT \r\n"
//                           + " FORM \r\n"
//                           + " PRINT \r\n";


                arreglo =   factura.obtenerFactura() +"\r\n"
                            + " LEFT \r\n"
                            + " FORM \r\n"
                            + " PRINT \r\n";
                System.out.println(arreglo);

//                impresion = ""+ lengthRead +" 1\r\n"
//                            + arreglo2 + " \r\n"
//                            + " FORM \r\n"
//                            + " PRINT \r\n";

            } catch (Exception e) {
                e.printStackTrace();
                sendCpclOverBluetooth(theBtMacAddress, salidaImprimir);
                pairPrinter(theBtMacAddress, salidaImprimir);
            }
            //Log.d("ARREGLO: ", arreglo);

            //System.out.println(arreglo);

            sendCpclOverBluetooth(theBtMacAddress, arreglo);
            pairPrinter(theBtMacAddress, impresion);


        }else {
            sendCpclOverBluetooth(theBtMacAddress, salidaImprimir);
            pairPrinter(theBtMacAddress, salidaImprimir);
        }
        cursor.close();
        manager.close();*/
    }

    public void pairPrinter(final String theBtMacAddress, final byte[] datosImprimir) {
        final UUID SerialPortServiceClass_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        final BluetoothAdapter BA = BluetoothAdapter.getDefaultAdapter();
        final String PrinterBsid = theBtMacAddress; // "00:22:58:08:48:66";

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                OutputStream sOut;
                BluetoothSocket socket;
                BA.cancelDiscovery();

                if (PrinterBsid != "") {

                    try {
                        BluetoothDevice BD = BA.getRemoteDevice(PrinterBsid);
                        socket = BD.createInsecureRfcommSocketToServiceRecord(SerialPortServiceClass_UUID);

                        if (!socket.isConnected()) {
                            socket.connect();
                            Thread.sleep(1000);
                            Log.d("3434", "conecto el socket");
                        }

                        int limit = 2000; //
                        while ((limit > 0) && (!socket.isConnected())) {
                            Thread.sleep(1);
                            limit--;
                        }
                        Log.d("3434", "limit is <" + limit + "> mac:" + theBtMacAddress);
                        if (0 == limit) {
                            Log.e("666", "Slow socket!");
                            throw new IOException("SlowSocket");
                        }

                        sOut = socket.getOutputStream();
                        //sOut.write("! U1 JOURNAL\r\n! U1 SETFF 50 2\r\n".getBytes());
                        sOut.write(datosImprimir);
                        Thread.sleep(2000);
                        sOut.close();

                        socket.close();
                        BA.cancelDiscovery();

                    } catch (IOException e) {
                        Log.e("", "IOException");
                        e.printStackTrace();
                        return;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        t.start();
    }

    private void sendCpclOverBluetooth(final String theBtMacAddress, final byte[] datosImprimir) {

        new Thread(new Runnable() {
            public void run() {
                try {
                    // Initialize
                    Looper.prepare();
                    // Open the connection - physical connection is established here.
                    thePrinterConn.open();
                    //thePrinterConn.write("! U1 JOURNAL\r\n! U1 SETFF 50 2\r\n".getBytes());
                    Thread.sleep(100);
                    thePrinterConn.write(datosImprimir);
                    // Make sure the data got to the printer before closing the connection
                    Thread.sleep(500);
                    // Close the insecure connection to release resources.
                    thePrinterConn.close();
                    Looper.myLooper().quit();

                } catch (Exception e) {
                    // Handle communications error here.
                    e.printStackTrace();
                    sendCpclOverBluetooth2Intento(theBtMacAddress, datosImprimir);
                }
            }
        }).start();
    }

    private void sendCpclOverBluetooth2(final String theBtMacAddress, final String datosImprimir) {

        new Thread(new Runnable() {
            public void run() {
                try {
                    // Initialize
                    Looper.prepare();
                    // Open the connection - physical connection is established here.
                    thePrinterConn.open();
                    //thePrinterConn.write("! U1 JOURNAL\r\n! U1 SETFF 50 2\r\n".getBytes());
                    Thread.sleep(100);
                    thePrinterConn.write(datosImprimir.getBytes());
                    // Make sure the data got to the printer before closing the connection
                    Thread.sleep(500);
                    // Close the insecure connection to release resources.
                    thePrinterConn.close();
                    Looper.myLooper().quit();

                } catch (Exception e) {
                    // Handle communications error here.
                    e.printStackTrace();
                    //sendCpclOverBluetooth2Intento(theBtMacAddress, datosImprimir);
                }
            }
        }).start();
    }

    private void sendCpclOverBluetooth2Intento(final String theBtMacAddress, final byte[] datosImprimir) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    // Initialize
                    Looper.prepare();
                    // Open the connection - physical connection is established here.
                    thePrinterConn.open();
                    //thePrinterConn.write("! U1 JOURNAL\r\n! U1 SETFF 50 2\r\n".getBytes());
                    thePrinterConn.write(datosImprimir);
                    // Make sure the data got to the printer before closing the connection
                    Thread.sleep(500);
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

    private void sendCpclOverBluetooth3Intento(final String theBtMacAddress, final byte[] datosImprimir) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    // Initialize
                    Looper.prepare();
                    // Open the connection - physical connection is established here.
                    thePrinterConn.open();
                    //thePrinterConn.write("! U1 JOURNAL\r\n! U1 SETFF 50 2\r\n".getBytes());
                    thePrinterConn.write(datosImprimir);
                    // Make sure the data got to the printer before closing the connection
                    Thread.sleep(900);
                    // Close the insecure connection to release resources.
                    thePrinterConn.close();
                    Looper.myLooper().quit();
                } catch (Exception e) {
                    // Handle communications error here.
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public String strImprimirCampos(String campo1, int i_iterador) {
        String entrada = "", strAux = "";
        int iterador = i_iterador, tamano = 22;

        campo1 = removerTildes(campo1);
        if (campo1.length() > tamano) {
            entrada = campo1.substring(0, tamano);
            strAux += "TEXT 0 3 20 " + iterador + " " + entrada + "\r\n";
        } else {
            strAux += "TEXT 0 3 20 " + iterador + " " + campo1 + "\r\n";
        }

        return strAux;
    }

    /**
     * Funci�n que elimina acentos y caracteres especiales de
     * una cadena de texto.
     *
     * @param input
     * @return cadena de texto limpia de acentos y caracteres especiales.
     */
    public static String removerTildes(String input) {
        // Cadena de caracteres original a sustituir.
        //String original = "??????????????u???????????????????";
        String original = "??????????????????????????????????";
        // Cadena de caracteres ASCII que reemplazar�n los originales.
        String ascii = "aaaeeeiiiooouuunAAAEEEIIIOOOUUUNcC";
        String output = input;
        for (int i = 0; i < original.length(); i++) {
            // Reemplazamos los caracteres especiales.
            output = output.replace(original.charAt(i), ascii.charAt(i));
        }//for i
        return output;
    }//remove1

    public void openDialogUpdateObs() {
        LayoutInflater inflater = this.getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_update_obs, null);
        final EditText etCodigoAcceso = (EditText) layout.findViewById(R.id.etCodigoAcceso);

        AlertDialog MyDialog;
        AlertDialog.Builder MyBuilder = new AlertDialog.Builder(this);
        MyBuilder.setTitle("ACTUALIZAR OBSERVACIONES");
        MyBuilder.setView(layout);
        MyBuilder.setPositiveButton("CONFIRMAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String codigo = etCodigoAcceso.getText().toString();
                if (codigo.contains("3309194")) {
                    openUpdateObs();
                }
            }
        });
        MyBuilder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        MyDialog = MyBuilder.create();
        MyDialog.show();
    }

    public void openUpdateObs() {

        Intent i = new Intent(this, GestionObservaciones.class);

        i.putExtra("tipo", tipoUsuario);
        i.putExtra("tipo", codigo_cuadrilla);
        i.putExtra("theBtMacAddress", theBtMacAddress);
        i.putExtra("strLatitud", strLatitud);
        i.putExtra("strLongitud", strLongitud);

        startActivityForResult(i, 222);
    }
    public void openInfoFens() {

        Intent i = new Intent(this, GestionInfoCargaDatos.class);
        i.putExtra("tipo", codigo_cuadrilla);
        i.putExtra("theBtMacAddress", theBtMacAddress);
        i.putExtra("strLatitud", strLatitud);
        i.putExtra("strLongitud", strLongitud);
        startActivityForResult(i, 882);
    }




    /**
     * Envia datos al server
     */
    private void enviarDatosMedidores() {

        AlertDialog.Builder invalid_input_dialog = new AlertDialog.Builder(this);
        invalid_input_dialog.setTitle("ENVIAR MEDIDORES NO CODIFICADOS ")
                .setMessage("DESDEA ENVIAR MEDIDORES NO CODIFICADOS PENDIENTES? ")
                .setCancelable(true)
                .setPositiveButton("ENVIAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        launchBarDialogMedidoresNoCodificados();
                    }
                })
                .setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                    }
                })
                .show();
    }// Cierre enviarDatosServer


    /**
     * Lanza el envio de ordenes pendientes por un dialog
     */
    public void launchBarDialogMedidoresNoCodificados() {
        barProgressDialog = new ProgressDialog(this);

        barProgressDialog.setTitle("ENVIO MEDIDORES NO CODIFICADOS ...");
        barProgressDialog.setMessage("Enviando Ordenes ...");
        barProgressDialog.setProgressStyle(barProgressDialog.STYLE_HORIZONTAL);
        barProgressDialog.setProgress(0);
        barProgressDialog.setMax(20);
        barProgressDialog.show();

        banderaEnvio = false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Cursor cursor;
                    int med_id, med_ciclo, tamano = 0, progreso = 0, actualizadas = 0, med_ruta = 0,
                            med_cons_ruta_anterior = 0, med_cons_ruta_posterior = 0,
                            med_indicador_med = 0, med_servicio = 0, med_persona = 0;
                    boolean actualizoOrden = false;
                    String med_direccion = "", fecha = "", med_medidor = "", med_lectura = "",
                            med_observacion = "", med_latitud = "", med_longitud = "",
                            med_fecha = "";

                    EjecucionOrdenesModel managerOrden = new EjecucionOrdenesModel(getBaseContext());

                    managerOrden.open();

                    cursor = managerOrden.medidoresPendientes();
                    if (cursor.moveToFirst()) {
                        EjecucionOrdenesModelServer managerServerEnvio = new EjecucionOrdenesModelServer(tipoUsuario);
                        tamano = cursor.getCount();
                        barProgressDialog.setProgress(0);//Ponemos la barra de progreso a 0
                        barProgressDialog.setMax(tamano);//El maximo de la barra de progreso son 60
                        progreso = 0;

                        do {
                            med_id = cursor.getInt(cursor.getColumnIndex("med_id"));
                            med_ciclo = cursor.getInt(cursor.getColumnIndex("med_ciclo"));
                            med_ruta = cursor.getInt(cursor.getColumnIndex("med_ruta"));
                            med_cons_ruta_anterior = cursor.getInt(cursor.getColumnIndex("med_cons_ruta_anterior"));
                            med_cons_ruta_posterior = cursor.getInt(cursor.getColumnIndex("med_cons_ruta_posterior"));
                            med_direccion = cursor.getString(cursor.getColumnIndex("med_direccion"));
                            med_medidor = cursor.getString(cursor.getColumnIndex("med_medidor"));
                            med_indicador_med = cursor.getInt(cursor.getColumnIndex("med_indicador_med"));
                            med_lectura = cursor.getString(cursor.getColumnIndex("med_lectura"));
                            med_servicio = cursor.getInt(cursor.getColumnIndex("med_servicio"));
                            med_observacion = cursor.getString(cursor.getColumnIndex("med_observacion"));
                            med_persona = cursor.getInt(cursor.getColumnIndex("med_persona"));
                            med_latitud = cursor.getString(cursor.getColumnIndex("med_latitud"));
                            med_longitud = cursor.getString(cursor.getColumnIndex("med_longitud"));
                            med_fecha = cursor.getString(cursor.getColumnIndex("med_fecha"));

                            Thread.sleep(100);
                            actualizoOrden =
                                    managerServerEnvio.insertar_medidor(med_ciclo, med_ruta, med_cons_ruta_anterior,
                                            med_cons_ruta_posterior, med_direccion, med_medidor, med_indicador_med,
                                            med_lectura, med_servicio, med_observacion, med_persona, med_latitud,
                                            med_longitud, med_fecha);
                            System.out.println("ACTUALIZO MEDIDOR POSTGRES " + actualizoOrden);
                            if (actualizoOrden) {
                                managerOrden.actualizarEnvioMedidores(String.valueOf(med_id));
                                System.out.println("ACTUALIZO MEDIDOR ID " + med_id);
                            }
                            Thread.sleep(100);

                            progreso++;
                            if (progreso < tamano) {
                                Thread.sleep(5);

                                updateBarHandler.post(new Runnable() {
                                    public void run() {
                                        barProgressDialog.incrementProgressBy(1);
                                        barProgressDialog.setMessage("Enviando MEDIDORES ...");
                                    }
                                });
                            } else {
                                barProgressDialog.dismiss();
                                System.gc();
                            }

                        } while (cursor.moveToNext());//accessing data upto last row from table
                    }
                    cursor.close();
                    managerOrden.close();
                    barProgressDialog.dismiss();
                    banderaEnvio = true;

                } catch (Exception e) {
                    barProgressDialog.dismiss();
                    banderaEnvio = true;
                }
            }
        }).start();
        actualizarPendientes();
    }// Cierra launchBarDialog Barra de envio de ordenes


}

