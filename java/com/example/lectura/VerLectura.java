package com.example.lectura;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.actsis.fensimp.Factura;
import com.actsis.fensliq.Critica;
import com.actsis.fensliq.Liquidador;
import com.example.Logueo.DbHelper;
import com.example.config.GestionMedidorEncontrado;
import com.example.gestiondeltec.R;
import com.example.location.LocationManager;
import com.example.location.LocationSyncActivity;
import com.zebra.sdk.comm.BluetoothConnectionInsecure;
import com.zebra.sdk.comm.Connection;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class VerLectura extends LocationSyncActivity implements OnClickListener {

    LinearLayout layoutPanelPrincipal, layoutLectura, layoutMedidorEncontrado;
    CheckBox chkMotivo;
    Spinner spMotivoLectura, spObsLectura, spUnidadMedida;
    TextView tvLecturaTipoOrden, tvLecturaDireccion, tvLecturaDireccionAux, tvTituloLectura,
            tvLectFranja, tvLectConsumo, tvLectRutaConsecutivo;
    EditText etLectura, etObsLectura, etSerieMedidorEncontrado, etMarcaMedidorEncontrado,
            etDiametroMedidorEncontrado;
    Button btConfirmarLectura;
    ImageView foto;
    Switch stEncontroMedidor;

    private Uri fileUri;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int MEDIA_TYPE_IMAGE = 1;
    private static final String IMAGE_DIRECTORY_NAME = "FotosOrdenesScr";

    int ose_codigo = 0, cuadrilla = 0, tipoFoto = 0, ose_precarga = 0, cli_contrato = 0,
            consecutivo = 0, ruta = 0, tipo_productoRA = 0;
    String tipoProducto = "", medidor = "", direccion = "", direccion2 = "", consumo = "", estado_fes = "", orden_fens = "", ruta_orden_fens = "", consumo_franja = "";
    String theBtMacAddress = "";
    private VerLecturaModel manager;

    private static final int REQUEST_ENABLE_BT = 0;
    Cursor datosLectura;
    int lectura_anterior, lectura_actual, consumo_promedio, estado, intentos, intentos2 = 2,
            indicador_lectura, causa, observacion;
    String urlFoto = "", strCausa = "";
    int cantidadDigitos = 0, tipoUsuario = 1, tipoOrden = 1;

    int fotosTomadas = 0, limiteFotos = 1;
    Boolean debeTomarFotosAdd = false;
    List<String> lablesObs = null, labelsObsImpedimento = null;
    ArrayAdapter<String> dataAdapterObsLect = null, dataAdapterObsLectImpe = null;

    Connection thePrinterConn;

    String MODEL = "", IMEI = "";
    double VERSION;
    Bundle bolsaR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_orden_tomar_lectura);

        tvTituloLectura = (TextView) findViewById(R.id.tvTituloTomarLectura);
        layoutPanelPrincipal = (LinearLayout) findViewById(R.id.LecturaPanelPrincipal);
        layoutLectura = (LinearLayout) findViewById(R.id.LecturaPanelLectura);
        layoutMedidorEncontrado = (LinearLayout) findViewById(R.id.LecturaPanelMedidorEncontrado);

        tvLecturaTipoOrden = (TextView) findViewById(R.id.tvLecturaTipoOrden);
        tvLecturaDireccion = (TextView) findViewById(R.id.tvLecturaDireccionOrden);
        tvLecturaDireccionAux = (TextView) findViewById(R.id.tvLecturaDireccionAux);
        chkMotivo = (CheckBox) findViewById(R.id.checkBoxReconexionNoAutorizada);
        btConfirmarLectura = (Button) findViewById(R.id.btTomarLectura);
        etLectura = (EditText) findViewById(R.id.etLecturaMedidor);
        etObsLectura = (EditText) findViewById(R.id.etObservacionLectura);
        etSerieMedidorEncontrado = (EditText) findViewById(R.id.etSerieMedidorEncontrado);
        etMarcaMedidorEncontrado = (EditText) findViewById(R.id.etMarcaMedidorEncontrado);
        etDiametroMedidorEncontrado = (EditText) findViewById(R.id.etDiametroMedidorEncontrado);
        tvLectFranja = (TextView) findViewById(R.id.tvLectFranja);
        tvLectConsumo = (TextView) findViewById(R.id.tvLectConsumo);

        foto = (ImageView) findViewById(R.id.imageViewFoto);
        spMotivoLectura = (Spinner) findViewById(R.id.spinnerMotivoNoLectura);
        spObsLectura = (Spinner) findViewById(R.id.spObervacionLectura);
        stEncontroMedidor = (Switch) findViewById(R.id.stEncontroMedidor);
        spUnidadMedida = (Spinner) findViewById(R.id.spUnidadMedidor);

        ArrayAdapter<CharSequence> adapterUnidadMedida = ArrayAdapter.createFromResource(this,
                R.array.unidad_medida, android.R.layout.simple_spinner_item);
        adapterUnidadMedida.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spUnidadMedida.setAdapter(adapterUnidadMedida);

        btConfirmarLectura.setBackgroundResource(android.R.drawable.btn_default);

        tvLectRutaConsecutivo = (TextView) findViewById(R.id.tvLectRutaConsecutivo);

        manager = new VerLecturaModel(this);

        bolsaR = getIntent().getExtras();
    }

    @Override
    public void onResume() {
        super.onResume();


        if (bolsaR != null) {
            orden_fens = bolsaR.getString("orden_id");
            ruta_orden_fens = bolsaR.getString("ruta_orden");
            ose_codigo = bolsaR.getInt("ose_codigo");
            cuadrilla = bolsaR.getInt("cuadrilla");
            tipoProducto = bolsaR.getString("producto");
            ose_precarga = bolsaR.getInt("ose_precarga");
            medidor = bolsaR.getString("elemento");
            direccion = bolsaR.getString("direccion");
            cli_contrato = bolsaR.getInt("cli_contrato");
            ruta = bolsaR.getInt("ruta");
            consecutivo = bolsaR.getInt("consecutivo");
            tipoUsuario = bolsaR.getInt("tipo");
            tipoOrden = bolsaR.getInt("tipo_orden");
            consumo = bolsaR.getString("consumo");
            consumo_franja = bolsaR.getString("consumo");
            if (consumo_franja.contains("REACTIVA")) {
                tipo_productoRA = 42;
            } else {
                tipo_productoRA = 10;
            }
            theBtMacAddress = bolsaR.getString("theBtMacAddress");
            estado_fes = bolsaR.getString("estado_fens");
            if (estado_fes == null || estado_fes == "N") {
                AlertDialog.Builder invalid_input_dialog = new AlertDialog.Builder(this);
                invalid_input_dialog.setTitle("INFORMACION ORDEN")
                        .setMessage("LA ORDEN A EJECUTAR ES ESCRITORIO")
                        .setCancelable(true)
                        .setPositiveButton("OK", null);
                AlertDialog dialog = invalid_input_dialog.create();
                dialog.show();
            } else {
                AlertDialog.Builder invalid_input_dialog = new AlertDialog.Builder(this);
                invalid_input_dialog.setTitle("INFORMACION ORDEN")
                        .setMessage("LA ORDEN A EJECUTAR ES FACTURACION EN SITIO")
                        .setCancelable(true)
                        .setPositiveButton("OK", null);
                AlertDialog dialog = invalid_input_dialog.create();
                dialog.show();
            }

            MODEL = bolsaR.getString("MODEL");
            IMEI = bolsaR.getString("IMEI");
            VERSION = bolsaR.getDouble("VERSION");
        }

        System.out.println("LLEGA LA MAC " + theBtMacAddress);

        tvTituloLectura.setText(medidor);
        tvLectConsumo.setText(String.valueOf(tipoProducto));
        tvLecturaTipoOrden.setText(String.valueOf(cli_contrato));
        tvLecturaDireccion.setText(direccion);
        stEncontroMedidor.setText("Encontro Medidor " + medidor + "?");
        tvLectFranja.setText("Medidor " + consumo);
        tvLecturaDireccionAux.setVisibility(View.GONE);
        tvLectRutaConsecutivo.setText(String.valueOf(ruta) + "     " + String.valueOf(consecutivo));

        manager.open();

        //int checkCausa= 0;
        // Cargar motivos no lectura cod 60

        List<String> lables = manager.getAllLabels(1);
        List<String> lablesObs = manager.getAllLabels(0);
        //datosLectura = manager.cargarDatosLectura(String.valueOf(ose_codigo));
        //lablesObs = manager.getAllLabels("61", 1);
        //labelsObsImpedimento = manager.getAllLabels("61", 0);

        datosLectura = manager.cargarDatosLectura(String.valueOf(ose_codigo));
        String strCantidadDigitos = "9999";


        if (datosLectura.moveToFirst()) {
            lectura_anterior = datosLectura.getInt(
                    datosLectura.getColumnIndex("lectura_anterior"));
            consumo_promedio = datosLectura.getInt(
                    datosLectura.getColumnIndex("consumo_promedio"));
            estado = datosLectura.getInt(datosLectura.getColumnIndex("estado"));
            direccion2 = datosLectura.getString(datosLectura.getColumnIndex("direccion2"));

            try {
                cantidadDigitos = datosLectura.getInt(datosLectura.getColumnIndex("cantidad_digitos"));
                strCantidadDigitos = String.valueOf(cantidadDigitos);
                if (cantidadDigitos > 0)
                    etLectura.setFilters(new InputFilter[]{
                            new InputFilter.LengthFilter((strCantidadDigitos.length() + 1))});

            } catch (Exception ex) {
            }

			/*if(direccion2.isEmpty()){
				tvLecturaDireccionAux.setVisibility(View.GONE);
			}else tvLecturaDireccionAux.setText(consumo);*/

            if (!datosLectura.isNull(datosLectura.getColumnIndex("indicador_lectura"))) {
                intentos =
                        datosLectura.getInt(datosLectura.getColumnIndex("intentos"));
                indicador_lectura =
                        datosLectura.getInt(datosLectura.getColumnIndex("indicador_lectura"));
                lectura_actual =
                        datosLectura.getInt(datosLectura.getColumnIndex("lectura"));
                causa =
                        datosLectura.getInt(datosLectura.getColumnIndex("codigo_observacion_no_lectura"));
                observacion =
                        datosLectura.getInt(datosLectura.getColumnIndex("codigo_observacion_lectura"));
            } else {
                intentos = 3;//DISPONIBLES TODOS LOS INTENTOS
                indicador_lectura = 3;//SIN REGISTRO
            }
            urlFoto = manager.cargarUltimaFoto(String.valueOf(ose_codigo));
            if (urlFoto != "") {
                Uri imgUri = Uri.parse(urlFoto);
                foto.setImageURI(imgUri);
            }
        }
        datosLectura.close();
        manager.close();

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter
                = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, lables);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spMotivoLectura.setAdapter(dataAdapter);

        // Creating adapter for spinner
        dataAdapterObsLect = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, lablesObs);
        dataAdapterObsLect.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        dataAdapterObsLectImpe = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, labelsObsImpedimento);
        dataAdapterObsLectImpe.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner

        spObsLectura.setAdapter(dataAdapterObsLect);
        btConfirmarLectura.setOnClickListener(this);

        chkMotivo.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                // TODO Auto-generated method stub
                if (chkMotivo.isChecked()) {
                    spMotivoLectura.setVisibility(View.VISIBLE);
                    layoutLectura.setVisibility(View.VISIBLE);
                    etLectura.setVisibility(View.GONE);
                    spObsLectura.setVisibility(View.GONE);
                } else {
                    spMotivoLectura.setVisibility(View.GONE);
                    etLectura.setVisibility(View.VISIBLE);
                    spObsLectura.setVisibility(View.VISIBLE);
                }
            }
        });

        stEncontroMedidor.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                // TODO Auto-generated method stub
                if (stEncontroMedidor.isChecked()) {
                    layoutMedidorEncontrado.setVisibility(View.GONE);
                } else {
                    layoutMedidorEncontrado.setVisibility(View.VISIBLE);
                }
            }
        });

        spMotivoLectura.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                String motivoStr = (String) spMotivoLectura.getSelectedItem();
                if (motivoStr.contains("37")) {
                    spObsLectura.setAdapter(dataAdapterObsLectImpe);
                } else {
                    spObsLectura.setAdapter(dataAdapterObsLect);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        if ((theBtMacAddress == "" || theBtMacAddress == null) && tipoUsuario == 1) {//Lector

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

        iniciarConexion();
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("fileUri", fileUri);
        outState.putBundle("bolsaR", bolsaR);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            fileUri = savedInstanceState.getParcelable("fileUri");
            bolsaR = savedInstanceState.getBundle("bolsaR");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_orden, menu);
        return true;
    }
    private final int MY_PERMISSIONS_REQUEST_CAMARA = 1;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_camera:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMARA);

                }else{
                    captureImage();
                }
                return true;
            case R.id.menu_print:
                Toast.makeText(this, "Imprimiendo...", Toast.LENGTH_SHORT).show();
                imprimirTirilla();
                //imprimirRecibo();
                //cerrarIntent();
                return true;
            case R.id.menu_factura:
                Toast.makeText(this, "Marcando No Entregado..", Toast.LENGTH_SHORT).show();
                if (orden_fens != null) {
                    try {
                        MarcarOrdenNoEntregado();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                return true;
            case R.id.menu_medidorEncontrado:
                openMedidorEncontrado();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.btTomarLectura:
                validarLectura();
                break;
        }
    }


    /**
     * Return text read validate
     */
    private void validarLectura() {
        final String lecturaStr = etLectura.getText().toString();
        final String motivoNoLecturaStr = (String) spMotivoLectura.getSelectedItem();
        final String obsLectura = (String) spObsLectura.getSelectedItem();
        final String obsLecturaAbierta = (String) etObsLectura.getText().toString();

        String msjMostrado = "", strMensaje = "", strTitulo = "", mensaje_critica = "";
        String serieMedidor = "", marcaMedidor = "", diametroMedidor = "", unidad_medidor = "", datos_medidor = "";
        int consumo = 0;

        if (!stEncontroMedidor.isChecked()) {
            serieMedidor = etSerieMedidorEncontrado.getText().toString();
            marcaMedidor = etMarcaMedidorEncontrado.getText().toString();
            diametroMedidor = etDiametroMedidorEncontrado.getText().toString();
            unidad_medidor = (String) spUnidadMedida.getSelectedItem();
            if (serieMedidor.length() == 0)
                msjMostrado += "DEBE INGRESAR LA SERIE DEL MEDIDOR";
        }

        if (chkMotivo.isChecked()) {
            strMensaje = motivoNoLecturaStr;
            strTitulo = medidor + " " + tipoProducto;
            lectura_actual = -1;

            if ((motivoNoLecturaStr.contains("36") || motivoNoLecturaStr.contains("58")) && obsLectura.length() > 6) {
                // 36 - NO EXISTE DIRECCION O 58 - DEMOLICION
                msjMostrado += " NO PUEDE AGREGAR OBSERVACIONES CON " + motivoNoLecturaStr;
            } else if (motivoNoLecturaStr.contains("37") && obsLectura.length() < 6) {
                // 37 - IMPEDIMENTO SIN OBS LECTURA
                msjMostrado += " DEBE SELECCIONAR UNA OBSERVACION DE LECTURA ";
            } else if (motivoNoLecturaStr.contains("13") && obsLecturaAbierta.length() < 3) {
                // 13 MEDIDOR CAMBIADO SIN REGISTRAR EL MEDIDOR EN LA OBS
                msjMostrado += " DEBE INDICAR LA SERIE DEL MEDIDOR ENCONTRADO ";
            } else if (motivoNoLecturaStr.contains("71") && obsLecturaAbierta.length() < 3) {
                // 71 1 - APARATO DE MEDICION NO INSTALADO
                msjMostrado += " DEBE ESCRIBIR UNA OBSERVACION DEL MEDIDOR NO INSTALADO ";
            } else if (motivoNoLecturaStr.contains("45") && obsLecturaAbierta.length() < 3) {
                // 45 - DIRECCION ERRADA
                msjMostrado += " DEBE ESCRIBIR LA DIRECCION DEL PREDIO ";
            } else if (motivoNoLecturaStr.contains("71") && !tipoProducto.contains("ENERGIA")) {
                // 71 MACRO NO INSTALADO EN TERRENO
                msjMostrado += " NO PUEDE AGREGAR CAUSA 71 CON AGUA ";
            }
        } else if (!lecturaStr.isEmpty()) {
            lectura_actual = Integer.parseInt(lecturaStr);
            consumo = lectura_actual - lectura_anterior / 1000;
            double criticaBajo = 0.20, criticaAlto = 0.20; //CONSUMO mAYOR o IGUAL a 40

            if (tipoProducto.contains("FACTURACION") || tipoProducto.contains("ENERGIA")) { // entra como FACTURACION Y NO ENERGIA ADURAN
                criticaAlto = 0.60;
            } else if (consumo_promedio < 40) {
                criticaBajo = 0.60;
                criticaAlto = 0.60;
            }

            String tipo_producto = String.valueOf(tipo_productoRA);
            String ruta_fens = ruta_orden_fens;
            Critica critica2 = new Critica(new DbHelper(MyApplication.getContext()).getDatabaseName(), MyApplication.getContext());
            try {
                Boolean result = critica2.validarLectura(ruta_fens, orden_fens, lecturaStr, tipo_producto);
                if (result) {
                    mensaje_critica = "CONSUMO NORMAL";
                } else {
                    if (consumo < (consumo_promedio * (1 - criticaBajo))) {
                        if (lectura_actual < lectura_anterior / 1000) {
                            mensaje_critica += "CONSUMO NEGATIVO";
                        } else mensaje_critica += " -BAJO CONSUMO";
                    } else
                        mensaje_critica = " -ALTO CONSUMO";
                }
            } catch (Exception e) {
                e.printStackTrace();

            }


//			if (consumo < (consumo_promedio * (1 - criticaBajo)))  {
//				if (lectura_actual < lectura_anterior / 1000)
//				{
//					mensaje_critica += "CONSUMO NEGATIVO";
//				}
//				else mensaje_critica += " -BAJO CONSUMO";
//			}
//			else if (consumo > (consumo_promedio * (criticaAlto + 1)))  {
//				mensaje_critica = " -ALTO CONSUMO";
//			}else
//				mensaje_critica = "CONSUMO NORMAL";

            if (consumo == 1) {
                strTitulo = "VERIFIQUE DIGITO CAMBIANTE";
                strMensaje = "Consumo 1 URGENTE ****<<VERIFIQUE>>**** DIGITO CAMBIANTE, por favor VERIFIQUE DIGITO CAMBIANTE";
            }
        } else msjMostrado = " Debe ingresar la LECTURA";

        if (intentos <= 0)
            msjMostrado = "Se agotaron los cambios en la LECTURA";

        /*
        if(obsLectura != null){

            if(obsLectura.contains("74") && tipoProducto.contains("ENERGIA"))
                msjMostrado += " No puede agregar esta observacion con ENERGIA";

            if (obsLectura.contains("70") && obsLectura.contains("59"))
            {
                if (consultaOrdenPreviaDesocupadoParado(obsLectura))
                    msjMostrado += " No puede Agregar la observacion " + obsLectura;
            }
            else if (obsLectura.contains("45") && obsLecturaAbierta == "")
            { // 774 45-DIRECCION ERRADA
                msjMostrado += " DEBE INDICAR LA DIRECCION DEL PREDIO ";
            }
        }
        */

        intentos2--;

        if (lectura_actual == (lectura_anterior / 1000)) {
            //LECTURAS IGUALES  ||  //CAUSA && 758 CAMBIAR APARATO MEDICION O 764 DEMOLICION
            mensaje_critica = "LECTURAS IGUALES";

            showEqualRead(lecturaStr, obsLectura, motivoNoLecturaStr, obsLecturaAbierta);

            /*
            if(msjMostrado == "") {
                showEqualRead(lecturaStr, obsLectura, motivoNoLecturaStr, obsLecturaAbierta);
            }else Toast.makeText(getActivity(), msjMostrado, Toast.LENGTH_SHORT).show();
            */
        } else if (msjMostrado == "") {

            strMensaje = "CONFIRMA OJO PRESENTA UN " + mensaje_critica + "\n-INFORMACION A TENER EN CUENTA: \n" + "-LECTURA ANTERIOR: " + lectura_anterior / 1000 + "\n-CONSUMO PROMEDIO: " + consumo_promedio;
            strTitulo = medidor + " LEC:" + lecturaStr;// + " " + mensaje_critica; // "LECTURA " + medidor;

            if (mensaje_critica == "CONSUMO NORMAL") {//CONSUMO NORMAL NO MUESTRO MENSAJE
                //ingresoLectura(medidor,lecturaStr,obsLectura, consumo); COMENTADO ALEJO FUTURA VERSION
                confirmarLecturaMotivo(lecturaStr, obsLectura, motivoNoLecturaStr, obsLecturaAbierta);
            } else if (intentos2 >= 0) {
                AlertDialog.Builder invalid_input_dialog = new AlertDialog.Builder(this);
                final String finalMensaje_critica = mensaje_critica;
                invalid_input_dialog.setTitle(strTitulo)
                        .setMessage(strMensaje)
                        .setCancelable(true)
                        .setPositiveButton("CONFIRMAR", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (finalMensaje_critica.contains("NEGATIVO")) {
                                    confirmarLecturaMotivo(lecturaStr, "K - ANALISIS ESPECIAL",
                                            motivoNoLecturaStr, obsLecturaAbierta);
                                } else confirmarLecturaMotivo(lecturaStr, obsLectura,
                                        motivoNoLecturaStr, obsLecturaAbierta);
                            }
                        })
                        .setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                            }
                        })
                        .show();
            } else
                confirmarLecturaMotivo(lecturaStr, obsLectura, motivoNoLecturaStr, obsLecturaAbierta);
        } else Toast.makeText(this, msjMostrado, Toast.LENGTH_SHORT).show();
    }

    private void showEqualRead(String lecturaStr, String obsLectura, String motivoNoLecturaStr, String obsLecturaAbierta) {

        LayoutInflater inflater = this.getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_lecturas_iguales, null);
        String tituloStr = "LECTURAS IGUALES";

        TextView tvTituloLecIguales = (TextView) layout.findViewById(R.id.tvTituloLecIguales);
        TextView tvTituloLectLectura = (TextView) layout.findViewById(R.id.tvTituloLectLectura);
        final TextView tvTituloObsLectura = (TextView) layout.findViewById(R.id.tvTituloObsLectura);
        final Spinner spLectIguales1 = (Spinner) layout.findViewById(R.id.spLectIguales1);
        final LinearLayout lyMotivoLectIguales = (LinearLayout) layout.findViewById(R.id.lyMotivoLectIguales);
        final Spinner spLectIguales2 = (Spinner) layout.findViewById(R.id.spLectIguales2);
        final EditText etObsLecturaIguales = (EditText) layout.findViewById(R.id.etObsLecturaIguales);

        uploadSpinnerEqualRead(spLectIguales1, spLectIguales2);
        lyMotivoLectIguales.setVisibility(View.GONE);

        spLectIguales1.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub
                String obsLecturaIguales = (String) spLectIguales1.getSelectedItem();
                if (obsLecturaIguales.contains("DANADO") || obsLecturaIguales.contains("DEMOLIDO")) {
                    lyMotivoLectIguales.setVisibility(View.VISIBLE);
                } else lyMotivoLectIguales.setVisibility(View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

        spLectIguales2.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub
                String obsLecturaIguales = (String) spLectIguales2.getSelectedItem();
                if (obsLecturaIguales.contains("CON APARATO DESCONECTADO")) {
                    tvTituloObsLectura.setText("INGRESE LA ***<< LECTURA >>***");
                    tvTituloObsLectura.requestFocus();
                } else {
                    tvTituloObsLectura.setText("Observacion Lectura Abierta");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

        tvTituloLectLectura.setText(lecturaStr);
        if (chkMotivo.isChecked()) {
            tvTituloLecIguales.setText(motivoNoLecturaStr);
            tituloStr = motivoNoLecturaStr;
            lyMotivoLectIguales.setVisibility(View.VISIBLE);
        }

        if (obsLecturaAbierta.length() >= 1) {
            etObsLecturaIguales.setText(obsLecturaAbierta, TextView.BufferType.EDITABLE);
        }

        AlertDialog MyDialog;
        AlertDialog.Builder MyBuilder = new AlertDialog.Builder(this);
        MyBuilder.setTitle(tituloStr);
        MyBuilder.setView(layout);
        MyBuilder.setCancelable(true);
        MyBuilder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String strLectIguales1 = (String) spLectIguales1.getSelectedItem();
                String strLectIguales2 = (String) spLectIguales2.getSelectedItem();
                String strObsAbierta = etObsLecturaIguales.getEditableText().toString();

                if (strLectIguales1 == "DEMOLIDO" && strLectIguales2 == "CON APARATO CONECTADO") {
                    cambiarLecturaCausa("DEMOLIDO CON APARATO CONECTADO");
                    if (!debeTomarFotosAdd) {
                        fotosTomadas = 0;
                        limiteFotos = 2;
                    }
                    debeTomarFotosAdd = true;

                } else confirmEqualRead(strLectIguales1, strLectIguales2, strObsAbierta);
            }
        });
        MyBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        MyDialog = MyBuilder.create();
        MyDialog.show();
    }

    private void cambiarLecturaCausa(String strObsAbierta) {
        if (chkMotivo.isChecked()) {
            chkMotivo.setChecked(false);
        }
        etObsLectura.setText(strObsAbierta);
        etLectura.requestFocus();
        Toast.makeText(this, "INGRESE LA LECTURA DEL MEDIDOR", Toast.LENGTH_LONG).show();
    }

    /**
     * @param strLectIguales1
     * @param strLectIguales2
     * @param strObsAbierta
     */
    private void confirmEqualRead(String strLectIguales1, String strLectIguales2, String strObsAbierta) {

        String lecturaStr = etLectura.getText().toString();
        String motivoNoLecturaStr = (String) spMotivoLectura.getSelectedItem();
        String obsLectura = (String) spObsLectura.getSelectedItem();


        if (strLectIguales1 == "C - PREDIO DESOCUPADO") {//806 70-predio desocupado
            if (strObsAbierta.length() < 3)
                strObsAbierta = "DESOCUPADO LECT:" + lecturaStr;
            obsLectura = "C - PREDIO DESOCUPADO";

        } else if (strLectIguales1 == "Y - PREDIO SUSPENDIDO") {
            if (strObsAbierta.length() < 3)
                strObsAbierta = "SUSPENDIDO LECT:" + lecturaStr;
            obsLectura = "Y - PREDIO SUSPENDIDO";

        } else if (strLectIguales1 == "Q - MEDIDOR FRENADO") {//"769"; //MEDIDOR CON VIDRIO AVERIADO QUITO LECTURA PONGO CAUSA 16
            if (strObsAbierta.length() < 3)
                strObsAbierta = "FRENADO LECT:" + lecturaStr;
            obsLectura = "Q - MEDIDOR FRENADO";
        }

        if (!debeTomarFotosAdd) {
            fotosTomadas = 0;
            limiteFotos = 2;
        }
        debeTomarFotosAdd = true;
        confirmarLecturaMotivo(lecturaStr, obsLectura, motivoNoLecturaStr, strObsAbierta);
    }

    private void uploadSpinnerEqualRead(Spinner sp1, Spinner sp2) {

        String motivoNoLecturaStr = (String) spMotivoLectura.getSelectedItem();
        List<String> labelsp1 = new ArrayList<String>();
        List<String> labelsp2 = new ArrayList<String>();
        labelsp1.add("C - PREDIO DESOCUPADO");
        labelsp1.add("Y - PREDIO SUSPENDIDO");
        labelsp1.add("Q - MEDIDOR FRENADO");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, labelsp1);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp1.setAdapter(dataAdapter);

        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, labelsp2);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp2.setAdapter(dataAdapter2);
    }

    /**
     * Ingresa  la Lectura o el Motivo de no lectura a la BD
     *
     * @param lecturaStr
     * @param obsLectura
     * @param motivoNoLecturaStr
     * @param obsLecturaAbierta
     */
    private void confirmarLecturaMotivo(String lecturaStr, String obsLectura, String motivoNoLecturaStr,
                                        String obsLecturaAbierta) {

        int motivoNoLectura = 0, codigoObsLectura = 0, codigoObsLecturaFens = 0, consumo = 0;
        int encontroMedidor = 0, medidorCorrecto = 1, critica = 0, inDesviacion = 0;
        double desviacion = 0;
        String lectura = "";
        String strLongitud = "";
        String strLatitud = "";
        String serieMedidor = "", marcaMedidor = "", diametroMedidor = "", unidad_medidor = "", datos_medidor = "";
        String motivoLecturaConfirmar = motivoNoLecturaStr;
        String obsLecturaStrCon = obsLectura;

        LocationManager locationManager = LocationManager.getInstance(this);
        Location location = locationManager.getLastLocation();

        Boolean debeTomarFoto = false;
        //CAMBIO RURALES
        debeTomarFoto = true;

        btConfirmarLectura.setTextColor(Color.BLUE);

        try {
            strLongitud = String.valueOf(location.getLongitude());
            strLatitud = String.valueOf(location.getLatitude());
        } catch (Exception ex) {
        }

        manager.open();

        if (chkMotivo.isChecked()) {
            motivoNoLectura = manager.getCodigoObs(motivoNoLecturaStr, "60");
            indicador_lectura = 0;
            causa = motivoNoLectura;
            strCausa = motivoNoLecturaStr;
            debeTomarFoto = true;

        } else if (!lecturaStr.isEmpty()) {
            lectura_actual = Integer.parseInt(lecturaStr);
            lectura = String.valueOf(lectura_actual);
            indicador_lectura = 1;
            consumo = lectura_actual - lectura_anterior / 1000;
            System.out.println(consumo);
            double criticaBajo = 0.20, criticaAlto = 0.20; //CONSUMO mAYOR o IGUAL a 40

            if (tipoProducto.contains("ENERGIA") || tipoProducto.contains("FACTURACION")) { // AGREGADO ADURAN
                criticaAlto = 0.60;
            } else if (consumo_promedio < 40) {
                criticaBajo = 0.60;
                criticaAlto = 0.60;
            }

            if (consumo < (consumo_promedio * (1 - criticaBajo))) {
                critica = 1;
                debeTomarFoto = true;
            } else if (consumo > (consumo_promedio * (criticaAlto + 1))) {
                critica = 3;
                debeTomarFoto = true;
            } else {// 2. CONSUMO NORMAL
                critica = 2;
                if (tipoProducto.contains("ENERGIA")) {
                    if (consumo >= 500)
                        debeTomarFoto = true;
                } else if (consumo >= 50) //**  AGUA  **//
                    debeTomarFoto = true;

                if (intentos < 3) // FOTO A LAS MODIFICADAS
                    debeTomarFoto = true;
            }
            desviacion = calcularDesviacion(consumo, consumo_promedio);

            try {
                inDesviacion = (int) desviacion;
            } catch (Exception ex) {
            }
        }

        if (!stEncontroMedidor.isChecked()) {
            serieMedidor = etSerieMedidorEncontrado.getText().toString();
            marcaMedidor = etMarcaMedidorEncontrado.getText().toString();
            diametroMedidor = etDiametroMedidorEncontrado.getText().toString();
            unidad_medidor = (String) spUnidadMedida.getSelectedItem();
            datos_medidor = "MEDIDOR:" + serieMedidor + "_MARCA:" + marcaMedidor
                    + "_DIAMETRO:" + diametroMedidor + "_UNIDAD:" + unidad_medidor;
            medidorCorrecto = 0;
        }

        codigoObsLectura = manager.getCodigoObs(obsLectura, "61");
        if (!obsLecturaAbierta.isEmpty()) {
            if (obsLecturaAbierta.length() > 0)
                obsLecturaAbierta = removerTildes(obsLecturaAbierta.toUpperCase());
        }

        codigoObsLecturaFens = manager.getCodigoObs2(obsLectura, "61");
        if (!obsLecturaAbierta.isEmpty()) {
            if (obsLecturaAbierta.length() > 0)
                obsLecturaAbierta = removerTildes(obsLecturaAbierta.toUpperCase());
        }

        intentos--;
        serieMedidor = medidor;
//		String tipo_producto= String.valueOf(tipo_productoRA);
//		String ruta_fens = "0"+ruta_orden_fens;
//        Critica critica2 = new Critica(new DbHelper(MyApplication.getContext()).getDatabaseName(), MyApplication.getContext());
//
//        try {
//			Boolean result = critica2.validarLectura(ruta_fens, orden_fens, lectura, tipo_producto);
//            if(result){
//
//            }else{
//				AlertDialog.Builder invalid_input_dialog = new AlertDialog.Builder(this);
//				final String finalLectura = lectura;
//				final int finalencontroMedidor = encontroMedidor;
//				final int finalCodigoObsLecturaFens = codigoObsLecturaFens;
//				final int finalCodigoObsLectura = codigoObsLectura;
//				final int finalMotivoNoLectura = motivoNoLectura;
//				final int finalCritica = critica;
//				final int finalMedidorCorrecto = medidorCorrecto;
//				final String finalSerieMedidor = serieMedidor;
//				final String finalStrLongitud = strLongitud;
//				final String finalStrLatitud = strLatitud;
//				final String finalmotivoLecturaConfirmar = motivoLecturaConfirmar;
//				final String finalobsLecturaStrCon = obsLecturaStrCon;
//				final int finalConsumo = consumo;
//				final double finalDesviacion = desviacion;
//				invalid_input_dialog.setTitle("INFORMACION ORDEN")
//						.setMessage("EL CONSUMO EN LA VALIDACION PRESENTA INCONSISTENCIAS POR FAVOR VALIDAR")
//						.setCancelable(true)
//						.setPositiveButton("CONFIRMAR", new DialogInterface.OnClickListener(){
//							public void onClick(DialogInterface dialog, int which) {
//								String obConsumoString = "K - ANALISIS ESPECIAL";
//								manager.ingresarActualizarLectura(
//										String.valueOf(ose_codigo), finalLectura, finalCodigoObsLecturaFens, finalCodigoObsLectura, finalMotivoNoLectura,
//										indicador_lectura, finalCritica, intentos, finalencontroMedidor, finalMedidorCorrecto,
//										finalSerieMedidor, finalStrLongitud, finalStrLatitud, finalConsumo, String.valueOf(finalDesviacion), finalmotivoLecturaConfirmar, obConsumoString, estado_fes, tipo_productoRA);
//
//							}
//
//							}).setNegativeButton("CANCELAR", new DialogInterface.OnClickListener(){
//								@Override
//								public void onClick(DialogInterface dialog, int which) {
//									// TODO Auto-generated method stub
//								}
//							})	.show();
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        manager.ingresarActualizarLectura(
                String.valueOf(ose_codigo), lectura, codigoObsLecturaFens, codigoObsLectura, motivoNoLectura,
                indicador_lectura, critica, intentos, encontroMedidor, medidorCorrecto,
                serieMedidor, strLongitud, strLatitud, consumo, String.valueOf(desviacion), motivoNoLecturaStr, obsLectura, estado_fes, tipo_productoRA);

//		manager.ingresarActualizarLectura(
//				String.valueOf(ose_codigo), lectura, codigoObsLecturaFens, codigoObsLectura, motivoNoLectura,
//                indicador_lectura, critica, intentos, encontroMedidor, medidorCorrecto,
//				serieMedidor, strLongitud, strLatitud, consumo, String.valueOf(desviacion), motivoNoLecturaStr, obsLectura, estado_fes, tipo_productoRA);

        manager.finalizarOrdenSinActividad(String.valueOf(ose_codigo), obsLecturaAbierta,
                "", 0, "", "", desviacion);

        if (tipoOrden == 2) {
            manager.finalizarOrdenRevision(String.valueOf(ose_codigo));
            debeTomarFoto = true;
        }

        if (codigoObsLectura > 0)
            debeTomarFoto = true;

        if (debeTomarFoto) {
            tipoFoto = 1;

			/*if(motivoNoLecturaStr.contains("71")){
				if (!debeTomarFotosAdd)
				{
					fotosTomadas = 0;
					limiteFotos = 3;
				}
				debeTomarFotosAdd = true;
			}*/ //COMENTADO PARA DESARROLLO Y CONTINUAR
            captureImage();
            Toast.makeText(this, "MEDIDOR: " + medidor + " LECTURA:" + lectura, Toast.LENGTH_LONG).show();

            btConfirmarLectura.setTextColor(Color.BLUE);
            if (tipoUsuario == 1 && manager.existJobPreExecute(ose_codigo, cli_contrato)) {
                //imprimirTirilla();
            }
            manager.close();
        } else {
            btConfirmarLectura.setTextColor(Color.BLUE);
            if (tipoUsuario == 1 && manager.existJobPreExecute(ose_codigo, cli_contrato)) {
                //imprimirTirilla();
            }
            manager.close();


            this.cerrarIntent();
        }


    }

    public void MarcarOrdenNoEntregado() throws Exception {
        manager.marcarLecturaNoEntregado(orden_fens, ruta_orden_fens);
    }


    public void ingresoLectura(String serie_medidor_encontrado, String lectura, String obsLectura, int consumo) {
        Cursor cursor = manager.cargarCursorOrden(serie_medidor_encontrado);
        if (cursor.moveToFirst()) {
            do {
                String strRuta = cursor.getString(cursor.getColumnIndex("RUTA"));
                String strOrdenId = cursor.getString(cursor.getColumnIndex("ORDEN_ID"));
                String strTipoLectura = cursor.getString(cursor.getColumnIndex("TIPO_LECTURA"));
                int intConsumo = consumo;
                String strLectura = lectura;
                String strObservacion = cursor.getString(cursor.getColumnIndex("OBSERVACION"));
                String strSolConsumo = "";
                Date datFechaLectura = new Date();
                Boolean bolCriticaFens = false;
                try {
                    Critica critica2 = new Critica(new DbHelper(MyApplication.getContext()).getDatabaseName(), MyApplication.getContext());
                    final Boolean result = critica2.validarLectura(strRuta, strOrdenId, strLectura, strTipoLectura);
                    System.out.println(result);
                    if (result) { // 1. PASO AQUI SE VERIFICA QUE VALIDAR LECTURA SEA TRUE
                        strObservacion = manager.getCodigoObs3(obsLectura, "1");
                        strSolConsumo = manager.getCodigoObs3(obsLectura, "2");
                        bolCriticaFens = result; // SE GUARDA EL VALOR
                        try {
                            critica2.ingresarLectura(strRuta, strOrdenId, strTipoLectura, intConsumo, strLectura, strObservacion, strSolConsumo, datFechaLectura, bolCriticaFens); //INGRESAR LECTURA
                        } catch (Exception e) {
                            String mensaje = e.getMessage();
                            if (mensaje == "Cliente ya fue FACTURADO.") {
//								AlertDialog.Builder invalid_input_dialog = new AlertDialog.Builder(this);
//								invalid_input_dialog.setTitle("Información")
//										.setMessage("Esto es un mensaje de alerta.")
//										.setCancelable(true)
//										.setPositiveButton("CONFIRMAR", new DialogInterface.OnClickListener(){
//											@Override
//											public void onClick(DialogInterface dialog, int which) {
//
//											}
//										})
//										.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener(){
//											@Override
//											public void onClick(DialogInterface dialog, int which) {
//												// TODO Auto-generated method stub
//											}
//										})
//										.show();
                            }
                        }
                        int intFactorRedondeo = 0; // DETERMINAR VALOR CON ACTSIS
                        Double dblLimiteRedondeo = 0.0; // DETERMINAR VALOR CON ACTSIS
                        Liquidador liquida = new Liquidador(new DbHelper(MyApplication.getContext()).getDatabaseName(), intFactorRedondeo, dblLimiteRedondeo, MyApplication.getContext());
                        liquida.liquidarOt(strRuta, strOrdenId);

                    } else {
                        critica2.marcarNoEntreago(strRuta, strOrdenId);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }
    }

    public void imprimirFactura() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.facturacion);
        builder.setMessage(R.string.facturar);
        builder.setPositiveButton("Generar Factura", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        Dialog dialog = builder.create();
        dialog.show();
    }

    public double calcularDesviacion(int consumo, int promedio) {
        double desviacion = 0;
        if (consumo == 0) {
            if (promedio == 0) {
                desviacion = 0;
            } else {
                desviacion = -9999;
            }
        } else {
            if (promedio == 0) {
                if (consumo > 80) {
                    desviacion = 20;
                } else if (consumo > 40) {
                    desviacion = 12;
                } else if (consumo > 20) {
                    desviacion = 8;
                } else if (consumo > 10) {
                    desviacion = 6;
                } else if (consumo > 5) {
                    desviacion = 4;
                } else desviacion = 2;
            } else {
                double d_consumo = 0, d_promedio = 0;
                d_consumo = Double.parseDouble(String.valueOf(consumo));
                d_promedio = Double.parseDouble(String.valueOf(promedio));
                desviacion = (d_consumo - d_promedio) / d_promedio;
            }
        }
        return desviacion;
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
        String original = "áàäéèëíìïóòöúùuñÁÀÄÉÈËÍÌÏÓÒÖÚÙÜÑçÇ";
        // Cadena de caracteres ASCII que reemplazaron los originales.
        String ascii = "aaaeeeiiiooouuunAAAEEEIIIOOOUUUNcC";
        String output = input;
        try {
            for (int i = 0; i < original.length(); i++) {
                // Reemplazamos los caracteres especiales.
                output = output.replace(original.charAt(i), ascii.charAt(i));
            }//for i
        } catch (Exception e) {
            output = input;
        }

        output = output.replace(';', '.');
        output = output.replace('"', ' ');
        output = output.replace("'", " ");

        return output;
    }//remove1

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
                    String ruta_fens = ruta_orden_fens;
                    Log.d("orden_id", orden_fens);
                    Log.d("MI RUTA", "0" + ruta_orden_fens);

                    Factura factura = new Factura(new DbHelper(MyApplication.getContext()).getDatabaseName(), ruta_fens, orden_fens, "dd-MM-yyyy hh:mm:ss", "dd/MMM/yyyy", Integer.toString(lengthRead), MyApplication.getContext());
                    try {
                        arreglo2 = factura.obtenerFacturaByte();

                        sendCpclOverBluetooth(theBtMacAddress, arreglo2);
                        manager.close();
                    } catch (Exception e) {

                    }
//                arreglo =   factura.obtenerFactura() +"\r\n"
//                            + " LEFT \r\n"
//                            + " FORM \r\n"
//                            + " PRINT \r\n";
//                System.out.println(arreglo);

//                impresion = ""+ lengthRead +" 1\r\n"
//                            + arreglo2 + " \r\n"
//                            + " FORM \r\n"
//                            + " PRINT \r\n";
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
                    int lengthRead2 = 500, countMed = 0;

                    String cpclData = "";

                    manager.open();
                    Cursor cursor = manager.consultaLecturaImprimirEmcali(String.valueOf(cli_contrato));

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

                        cpclData += "TEXT 0 0 15 " + inicial + " MATRICULA " + suscriptor + "   FECHA " + fecha_lectura + "\r\n";//FECHA
                        inicial = inicial + 15;

                        cpclData += strImprimirCampos(cliente, inicial);
                        inicial = inicial + factor;

                        cpclData += "TEXT 0 0 15 " + inicial + " DIRECCION            Ruta:" + rutaImpr + "  Cons:" + conseImpr + "\r\n";//FECHA
                        inicial = inicial + 15;

                        cpclData += strImprimirDireccion(direccion, inicial + 1);
                        inicial = inicial + factor;

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
                            if (strObsLectura.length() > 2) {
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
                                    if (strObsLectura.length() > 2) {
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

                    cpclData += "TEXT 0 0 15 " + inicial + "                                   lector:" + cuadrilla + "\r\n";//FECHA
                    inicial = inicial + factor;

                    cursor.close();

                    cpclData += "LEFT \r\n"
                            + "FORM\r\n"
                            + "PRINT\r\n";
                    manager.close();

                    if (countMed == 0)
                        lengthRead2 = 380;

                    String head = "! U1 JOURNAL\r\n ! U1 SETFF 50 2\r\n"
                            + "! 0 200 200 " + lengthRead2 + " 1\r\n"
                            + "PCX 200 43 !<PEREIRA1.PCX\r\n"
                            + "TEXT 5 0 15 25 ENERGIA DE PEREIRA\r\n"
                            + "TEXT 0 0 15 47 S.A.E.S.P.\r\n"
                            + "TEXT 0 2 15 60 CONSTANCIA DE LECTURA  \r\n"
                            + "TEXT 0 0 15 82 Nit: 816002019-9\r\n";

                    //System.out.println(cpclData);

                    final String salidaImprimir = head + cpclData;

                    sendCpclOverBluetooth2(theBtMacAddress, salidaImprimir);

                }

                //Log.d("ARREGLO: ", arreglo);
                if (opciones[which] == "MARCAR NO ENTREGADO") {
                    try {
                        MarcarOrdenNoEntregado();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                //System.out.println(arreglo);


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
//		if(theBtMacAddress == ""){
//
//			BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//			if (mBluetoothAdapter == null) {
//				// Device does not support Bluetooth
//			}
//
//			if (!mBluetoothAdapter.isEnabled()) {
//				Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//			}
//
//			Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
//			// If there are paired devices
//			if (pairedDevices.size() > 0) {
//				// Loop through paired devices
//				for (BluetoothDevice device : pairedDevices) {
//					// Add the name and address to an array adapter to show in a ListView
//					System.out.println(device.getName() + "\n" + device.getAddress());
//					theBtMacAddress = device.getAddress();
//				}
//			}
//		}
//
//		int inicial = 94, factor = 25;
//		String lectura = "", observacionLectura = "", fecha_lectura = "", cliente = "", direccion = "", medidor = "", franja = "", servicio = "";
//		int rutaImpr = 0, conseImpr = 0;
//		int codigoObsLectura = 0, codigoNoLectura = 0, indicadorLectura = 0, suscriptor = 0 ;
//		String strMotivoNoLectura = "", strObsLectura = "";
//		int lengthRead = 500, countMed = 0;
//
//		String cpclData = "";
//
//		manager.open();
//		Cursor cursor = manager.consultaLecturaImprimirEmcali(String.valueOf(cli_contrato));
//
//		if(cursor.moveToFirst()){
//			cliente = cursor.getString(cursor.getColumnIndex("cli_nombre"));
//			suscriptor = cursor.getInt(cursor.getColumnIndex("cli_contrato"));
//			direccion = cursor.getString(cursor.getColumnIndex("direccion1"));
//			medidor = cursor.getString(cursor.getColumnIndex("elemento"));
//			franja = cursor.getString(cursor.getColumnIndex("consumo"));
//			servicio = cursor.getString(cursor.getColumnIndex("producto"));
//			rutaImpr = cursor.getInt(cursor.getColumnIndex("ruta"));
//			conseImpr = cursor.getInt(cursor.getColumnIndex("ruta_cons"));
//
//			lectura = cursor.getString(cursor.getColumnIndex("lectura"));
//			codigoNoLectura = cursor.getInt(cursor.getColumnIndex("codigo_observacion_no_lectura"));
//			codigoObsLectura = cursor.getInt(cursor.getColumnIndex("codigo_observacion_lectura"));
//			observacionLectura = cursor.getString(cursor.getColumnIndex("observacion_no_lectura"));
//			fecha_lectura = cursor.getString(cursor.getColumnIndex("fecha_creacion"));
//			indicadorLectura = cursor.getInt(cursor.getColumnIndex("indicador_lectura"));
//
//			//cpclData += "TEXT 0 0 15 " + inicial + " " + fecha_lectura + "\r\n";//FECHA
//			inicial = inicial + 10;
//
//			cpclData += "TEXT 0 0 15 " + inicial + " MATRICULA " + suscriptor + "   FECHA " +  fecha_lectura + "\r\n";//FECHA
//			inicial = inicial + 15;
//
//			cpclData += strImprimirCampos(cliente, inicial);
//			inicial = inicial + factor;
//
//			cpclData += "TEXT 0 0 15 " + inicial + " DIRECCION            Ruta:"+ rutaImpr + "  Cons:" + conseImpr + "\r\n";//FECHA
//			inicial = inicial + 15;
//
//			cpclData += strImprimirDireccion(direccion, inicial + 1);
//			inicial = inicial + factor;
//
//			cpclData += "TEXT 0 0 15 " + inicial + " MEDIDOR        FRANJA:" + franja + "\r\n";//FECHA
//			inicial = inicial + 15;
//			cpclData += "TEXT 5 0 15 " + inicial + "  " + medidor + "\r\n";//FECHA
//			//cpclData += strImprimirCampos(medidor , inicial);//Matricula Cliente
//			inicial = inicial + factor;
//
//			if (indicadorLectura == 1)
//			{
//				cpclData += "TEXT 0 2 15 " + inicial + " LECTURA:\r\n";//FECHA
//				//inicial = inicial + factor;
//
//				cpclData += "TEXT 0 6 45 " + (inicial + 5) + "  " + lectura + "\r\n";
//				inicial = inicial + factor + 25;
//				//cpclData += strImprimirCampos("LECTURA: " + lectura + " -" + servicio, inicial);//TITULO LECTURA
//			}
//			else
//			{
//				cpclData += "TEXT 0 2 15 " + inicial + " NO LECTURA:\r\n";//FECHA
//				inicial = inicial + factor;
//				strMotivoNoLectura = manager.getObsCodigo(String.valueOf(codigoNoLectura));
//				cpclData += strImprimirCampos(strMotivoNoLectura, inicial);//CAUSAL DE NO LECTURA
//				inicial = inicial + factor;
//			}
//
//			cpclData += "\r\n";
//
//			if(codigoObsLectura > 0){
//				strObsLectura = manager.getObsCodigo(String.valueOf(codigoObsLectura));
//				if(strObsLectura.length() > 2){
//					cpclData += "TEXT 0 0 15 " + inicial + " OBSERVACION:\r\n";//FECHA
//					inicial = inicial + 15;
//
//					cpclData += strImprimirCampos(strObsLectura, inicial);//OBSERVACION LECTURA
//					cpclData += "\r\n";
//					inicial = inicial + factor;
//				}
//			}
//
//			if(cursor.moveToNext()){
//				do{
//					countMed++;
//					medidor = cursor.getString(cursor.getColumnIndex("elemento"));
//					franja = cursor.getString(cursor.getColumnIndex("consumo"));
//					servicio = cursor.getString(cursor.getColumnIndex("producto"));
//
//					lectura = cursor.getString(cursor.getColumnIndex("lectura"));
//					codigoNoLectura = cursor.getInt(cursor.getColumnIndex("codigo_observacion_no_lectura"));
//					codigoObsLectura = cursor.getInt(cursor.getColumnIndex("codigo_observacion_lectura"));
//					observacionLectura = cursor.getString(cursor.getColumnIndex("observacion_no_lectura"));
//					indicadorLectura = cursor.getInt(cursor.getColumnIndex("indicador_lectura"));
//
//					cpclData += "TEXT 0 0 15 " + inicial + " MEDIDOR        FRANJA:" + franja + "\r\n";//FECHA
//					inicial = inicial + 15;
//
//					cpclData += "TEXT 5 0 15 " + inicial + "  " + medidor + "\r\n";//FECHA
//					//cpclData += strImprimirCampos(medidor , inicial);//Matricula Cliente
//					inicial = inicial + factor;
//
//					if (indicadorLectura == 1)
//					{
//						cpclData += "TEXT 0 2 15 " + inicial + " LECTURA:\r\n";//FECHA
//						//inicial = inicial + factor;
//
//						cpclData += "TEXT 0 6 45 " + (inicial + 5) + "  " + lectura + "\r\n";
//						inicial = inicial + factor + 25;
//						//cpclData += strImprimirCampos("LECTURA: " + lectura + " -" + servicio, inicial);//TITULO LECTURA
//					}
//					else
//					{
//						cpclData += "TEXT 0 2 15 " + inicial + " NO LECTURA:\r\n";//FECHA
//						inicial = inicial + factor;
//						strMotivoNoLectura = manager.getObsCodigo(String.valueOf(codigoNoLectura));
//						cpclData += strImprimirCampos(strMotivoNoLectura, inicial);//CAUSAL DE NO LECTURA
//						inicial = inicial + factor;
//					}
//
//					cpclData += "\r\n";
//
//					if(codigoObsLectura > 0){
//						strObsLectura = manager.getObsCodigo(String.valueOf(codigoObsLectura));
//						if(strObsLectura.length() > 2){
//							cpclData += "TEXT 0 0 15 " + inicial + " OBSERVACION:\r\n";//FECHA
//							inicial = inicial + 15;
//
//							cpclData += strImprimirCampos(strObsLectura, inicial);//OBSERVACION LECTURA
//							cpclData += "\r\n";
//							inicial = inicial + factor;
//						}
//					}
//
//				}while(cursor.moveToNext());//accessing data upto last row from table
//			}
//		}
//
//		cpclData += "TEXT 0 0 15 " + inicial + "                                   lector:" + cuadrilla + "\r\n";//FECHA
//		inicial = inicial + factor;
//
//		cursor.close();
//
//		cpclData += "LEFT \r\n"
//				+ "FORM\r\n"
//				+ "PRINT\r\n";
//		manager.close();
//
//		if(countMed == 0)
//			lengthRead = 380;
//
//		String head = "! U1 JOURNAL\r\n ! U1 SETFF 50 2\r\n"
//				+ "! 0 200 200 "+ lengthRead +" 1\r\n"
//				+ "PCX 200 43 !<PEREIRA1.PCX\r\n"
//				+ "TEXT 5 0 15 25 ENERGIA DE PEREIRA\r\n"
//				+ "TEXT 0 0 15 47 S.A.E.S.P.\r\n"
//				+ "TEXT 0 2 15 60 CONSTANCIA DE LECTURA  \r\n"
//				+ "TEXT 0 0 15 82 Nit: 816002019-9\r\n";
//
//		//System.out.println(cpclData);
//
//		final String salidaImprimir =head + cpclData;
//
//		sendCpclOverBluetooth(theBtMacAddress, salidaImprimir);
//	}
//
//	public void imprimirRecibo(){
//
//		if(theBtMacAddress == ""){
//
//			BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//			if (mBluetoothAdapter == null) {
//				// Device does not support Bluetooth
//			}
//
//			if (!mBluetoothAdapter.isEnabled()) {
//				Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//			}
//
//			Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
//			// If there are paired devices
//			if (pairedDevices.size() > 0) {
//				// Loop through paired devices
//				for (BluetoothDevice device : pairedDevices) {
//					// Add the name and address to an array adapter to show in a ListView
//					System.out.println(device.getName() + "\n" + device.getAddress());
//					theBtMacAddress = device.getAddress();
//				}
//			}
//		}
//
//		int inicial = 100, factor = 25;
//		String lectura = "", observacionLectura = "", fecha_lectura = "", cliente = "", direccion = "", medidor = "", franja = "", servicio = "";
//		int codigoObsLectura = 0, codigoNoLectura = 0, indicadorLectura = 0, suscriptor = 0 ;
//		String strMotivoNoLectura = "", strObsLectura = "";
//
//		manager.open();
//
//		Cursor cursor = manager.consultaLecturaImprimir(String.valueOf(ose_codigo));
//
//		if(cursor.moveToFirst()){
//			cliente = cursor.getString(cursor.getColumnIndex("cli_nombre"));
//			suscriptor = cursor.getInt(cursor.getColumnIndex("cli_contrato"));
//			direccion = cursor.getString(cursor.getColumnIndex("direccion1"));
//			medidor = cursor.getString(cursor.getColumnIndex("elemento"));
//			franja = cursor.getString(cursor.getColumnIndex("consumo"));
//			servicio = cursor.getString(cursor.getColumnIndex("producto"));
//
//			lectura = cursor.getString(cursor.getColumnIndex("lectura"));
//			codigoNoLectura = cursor.getInt(cursor.getColumnIndex("codigo_observacion_no_lectura"));
//			codigoObsLectura = cursor.getInt(cursor.getColumnIndex("codigo_observacion_lectura"));
//			observacionLectura = cursor.getString(cursor.getColumnIndex("observacion_no_lectura"));
//			fecha_lectura = cursor.getString(cursor.getColumnIndex("fecha_creacion"));
//			indicadorLectura = cursor.getInt(cursor.getColumnIndex("indicador_lectura"));
//		}
//
//		cursor.close();
//
//		String cpclData = "! 0 200 200 420 1\r\n"
//				+ "TEXT 0 3 15 25         REFUGIO \r\n"
//				+ "TEXT 0 3 15 45      VILLA LORENA\r\n"
//				+ "TEXT 0 0 15 65            NIT: 6714886-4 REG.SIMPL\r\n"
//				+ "TEXT 0 0 15 75              TELEFE (57-2)6649275\r\n"
//				+ "TEXT 0 3 15 100 2015-4-29         13:10\r\n"
//				+ "TEXT 0 3 15 125 REG               00003\r\n"
//				+ "TEXT 0 3 15 175 INGRESO  X36     $8.000\r\n"
//				+ "TEXT 0 3 15 200 SUBTL          $288.000\r\n"
//				+ "TEXT 0 3 15 225 VARIOS   X36     $1.500\r\n"
//				+ "TEXT 0 3 15 250 SUBTL           $54.000\r\n"
//				+ "TEXT 0 3 15 275 TOTAL          $342.000\r\n";
//
//
//		cpclData += "LEFT \r\n"
//				+ "FORM\r\n"
//				+ "PRINT\r\n";
//		manager.close();
//
//		//System.out.println(cpclData);
//
//		final String salidaImprimir = cpclData;
//
//		sendCpclOverBluetooth(theBtMacAddress, salidaImprimir);
    }


    private void sendCpclOverBluetooth(final String theBtMacAddress, final byte[] datosImprimir) {

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
                    thePrinterConn.write(datosImprimir);
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

    private void sendCpclOverBluetooth2(final String theBtMacAddress, final String datosImprimir) {

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
                    //sendCpclOverBluetooth2Intento(theBtMacAddress, datosImprimir);
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

    private void sendCpclOverBluetooth2Intento(final String theBtMacAddress, final byte[] datosImprimir) {
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
                    //Thread.sleep(7000);
                    // Instantiate insecure connection for given Bluetooth&reg; MAC Address.
                    //Connection thePrinterConn = new BluetoothConnectionInsecure(theBtMacAddress,500,500);
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

    public String strImprimirDireccion(String campo1, int i_iterador) {
        String entrada = "", strAux = "";
        int iterador = i_iterador, tamano = 35;

        campo1 = removerTildes(campo1);
        if (campo1.length() > tamano) {
            entrada = campo1.substring(0, tamano);
            strAux += "TEXT 0 2 20 " + iterador + " " + entrada + "\r\n";
        } else {
            strAux += "TEXT 0 2 20 " + iterador + " " + campo1 + "\r\n";
        }

        return strAux;
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
            String strMedidor = "MED:" + medidor;
            String strDatos = "";

            if (indicador_lectura == 1)
                strDatos = "LECT:" + String.valueOf(lectura_actual);
            else strDatos = "CAUSA:" + strCausa;

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
                if (tipoUsuario == 1)
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

        if (fotosTomadas >= limiteFotos) {
            debeTomarFotosAdd = false;
            if (tipoFoto == 1) {
                cerrarIntent();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Captura Fotos Adicionales", Toast.LENGTH_SHORT).show();
            captureImage();
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
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // start the image capture Intent
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

    /**
     * Inserta una URL de la foto en BD
     */
    public void guardarFoto() {
        manager.open();
        manager.insertarFoto(ose_codigo, fileUri.getPath());
        manager.close();

        deleteLatest();
    }

    private void deleteLatest() {
        // TODO Auto-generated method stub
        File f = new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera");

        //Log.i("Log", "file name in delete folder :  "+f.toString());
        File[] files = f.listFiles();

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

        if (files.length > 0) {
            try {
                Log.i("Log", "Count of the FILES AFTER DELETING ::" + files[0].length());
                System.out.println("BORRO2 FOTO " + files[0].getPath());
                files[0].delete();
            } catch (Exception ex) {
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////////// FIN METODOS CAMARA ////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////

    private void cerrarIntent() {
        Intent i = new Intent(this, VerLectura.class);
        this.setResult(RESULT_OK, i);
        this.finish();
    }

    /**
     * Retorna true o false si la obs anterior cuadra
     *
     * @param inObservacionActual
     * @return
     */
    public Boolean consultaOrdenPreviaDesocupadoParado(String inObservacionActual) {
        Boolean salida = false;
        int observacionActual = 0;
        manager.open();
        observacionActual = manager.getCodigoObs(inObservacionActual, "61");
        salida = manager.consultaOrdenPreviaDesocupadoParado(ose_codigo, cli_contrato, observacionActual);
        manager.close();

        return salida;
    }

    public void openMedidorEncontrado() {

        int ruta_anterior = consecutivo, ruta_posterior = 0, ciclo = 0, contrato_posterior = 0;

        Bundle bolsa;
        manager.open();
        bolsa = manager.consultaDatosMedidor(String.valueOf(cuadrilla), ruta_anterior);
        manager.close();

        if (!bolsa.isEmpty()) {
            ciclo = bolsa.getInt("ciclo");
            ruta_posterior = bolsa.getInt("ruta_posterior", 0);
            contrato_posterior = bolsa.getInt("contrato_posterior", 0);
        }

        Intent i = new Intent(this, GestionMedidorEncontrado.class);

        i.putExtra("ruta", ruta);
        i.putExtra("cuadrilla", cuadrilla);
        i.putExtra("ciclo", ciclo);
        i.putExtra("direccion", direccion);
        i.putExtra("ruta_anterior", ruta_anterior);
        i.putExtra("ruta_posterior", ruta_posterior);

        i.putExtra("contrato_anterior", cli_contrato);
        i.putExtra("contrato_posterior", contrato_posterior);

        startActivity(i);
    }


}
