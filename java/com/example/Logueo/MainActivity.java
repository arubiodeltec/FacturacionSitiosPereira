package com.example.Logueo;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.actsis.fensliq.Tablas_BD;
import com.example.gestionOrdenes.EjecucionOrdenes;
import com.example.gestiondeltec.R;
import com.example.lectura.MyApplication;
import com.example.location.EnvioDatosExecutor;
import com.example.location.LocationSyncActivity;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends LocationSyncActivity implements OnClickListener{


	EditText etIngresoCodigo, etIngresoCedula,etIngresoIdTerminal, etIngresoClave;
	Button btnIngresar, btnBorrar;
	TextView tvTitulo, tvIdTerminal;
	CheckBox chkBoxCargarOrdenes;
	ProgressBar barraProgreso;//Declaraci?n de la barra de progreso.
	String codigo = "",  cedula= "", nombreObtenido ="", idTerminal = "",
			strLatitud = "", strLongitud = "", strAltitud = "", strVelocidad = "", clave = "";
	int codigoObtenido = 0, trabajoRealizar = 0, progreso = 0, terminal = -1;
	int tipoUsuario = 1;
	boolean cargarOrdenes = true, registrarId = false, existeObs = false;
	Connection conn;
	private DbHelper dbHelper;
	private DataBaseManager manager, managerGPS;
	private DataBasePostgresManager managerServer;

	public boolean banderaEnvio = true;	
	int intLogueoUsuario = 0, intCargoOrdenes = -1, intCargoStock = -1;

	IntentFilter ifilter;
	Intent batteryStatus;

	int level, scale;
	float batteryPct;
	String IMEI= "";
	double VERSION = 1.9;

	int iteradorTimer = 0;
	MyTimerTask myTask;
	Timer myTimer;
	boolean inicioEnvio = false, salidaActivity = false;

	@RequiresApi(api = Build.VERSION_CODES.M)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		requestPermmission();
		etIngresoCodigo = (EditText)findViewById(R.id.etIngresoCodigo);
		etIngresoCedula = (EditText)findViewById(R.id.etIngresoCedula);
		etIngresoIdTerminal = (EditText)findViewById(R.id.etIdTerminal);
		etIngresoClave = (EditText)findViewById(R.id.etIngresoClave);
		tvIdTerminal = (TextView)findViewById(R.id.tvIdTerminal);
		tvTitulo = (TextView)findViewById(R.id.tvTitulo);

		btnIngresar = (Button)findViewById(R.id.btnIngresarApk);
		btnBorrar = (Button)findViewById(R.id.btnBorrarCampos);
		barraProgreso = (ProgressBar)findViewById(R.id.progressBar1);
		chkBoxCargarOrdenes = (CheckBox) findViewById(R.id.checkBoxCargarOrdenes);

		btnIngresar.setOnClickListener(this);
		btnBorrar.setOnClickListener(this);


		manager = new DataBaseManager(this);

		managerGPS = new DataBaseManager(this);
		setProgressBarIndeterminateVisibility(false);

		IMEI = getIMEI();

		managerServer = new DataBasePostgresManager(tipoUsuario);  	

		manager.open();		
		terminal = manager.exiteRegistroTerminal();
		manager.close();

		if(terminal == 0){
			etIngresoIdTerminal.setVisibility(View.VISIBLE);
			registrarId = true;
		}else tvIdTerminal.setText("ID " + String.valueOf(terminal));

		tvTitulo.setText("FACTURACION V " + VERSION);

		ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		batteryStatus = this.registerReceiver(null, ifilter);

	}

	//pedir permisos al ingrersar a la plataforma
	private final int REQUEST_CODE = 200;

	@RequiresApi(api = Build.VERSION_CODES.M)
	private void requestPermmission() {

		int permissionLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
		int permissionStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
		int permissionCamera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
		int permissionBackground = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION);

		if (permissionLocation!=PackageManager.PERMISSION_GRANTED
				&& permissionStorage!=PackageManager.PERMISSION_GRANTED
				&& permissionCamera!=PackageManager.PERMISSION_GRANTED
				&& permissionBackground!=PackageManager.PERMISSION_GRANTED){

			requestPermissions(new
					String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
					Manifest.permission.ACCESS_COARSE_LOCATION,
					Manifest.permission.CAMERA,
					Manifest.permission.ACCESS_BACKGROUND_LOCATION},REQUEST_CODE);

		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		outState.putBoolean("bandera_envio", banderaEnvio);
		outState.putBoolean("inicio_envio", inicioEnvio);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState){
		super.onRestoreInstanceState(savedInstanceState);
		banderaEnvio  = savedInstanceState.getBoolean("bandera_envio");
		inicioEnvio = savedInstanceState.getBoolean("inicio_envio");
	}

	@Override
	public void onResume() {
		super.onResume();
		if(!inicioEnvio){
			myTask = new MyTimerTask();
			myTimer = new Timer();
			myTimer.schedule(myTask, 300000, 4000000);
			//myTimer.schedule(myTask, 2000, 1000);
			inicioEnvio = true;
			insertar_estados_ingreso("6");
		}else{
			Log.i("Segundo ","resume");
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		inicioEnvio = true;
	}

	/*
	 * Clase Timer controla el envio automatico
	 */
	class MyTimerTask extends TimerTask {
		public void run() {
			iteradorTimer++;	 
			System.out.println("Envio Automatico MAIN " + iteradorTimer);
			iniciarHiloEnvio();
		}
	}//Cierra Clase MyTimerTask

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present. 
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_salir_apk:
			finalizarApk();					
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * CUADRO DE DIALOGO PARA CERRAR LA APK
	 */
	private void finalizarApk(){

		AlertDialog.Builder invalid_input_dialog = new AlertDialog.Builder(this);
		invalid_input_dialog.setTitle("ALERTA")
		.setMessage("DESEA SALIR DEL APLICATIVO ")
		.setCancelable(true)
		.setPositiveButton("SALIR", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				insertar_estados_ingreso("20");
				salidaActivity = true;
				finish();
			}
		})
		.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub		                     
			}
		})
		.show();
	}

	@Override
	public void  finish () {
		System.out.println("Intenta cerrar la apk");  
		if(salidaActivity){
			System.out.println("Cierra la apk"); 
			super.finish();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		System.out.println( "ENVIO MANUAL" );
		if (requestCode==111 && resultCode == RESULT_OK ){

			btnIngresar.setBackgroundResource(android.R.drawable.btn_default);
			if(banderaEnvio){
				if(chkBoxCargarOrdenes.isChecked())
					chkBoxCargarOrdenes.setChecked(false);

				iniciarHiloEnvio();
				System.out.println( "FULL ENVIO MANUAL" );
			}
		}
	}

	/**
	 * Obtener el numero IMEI del celular
	 * @return string IMEI
	 */
	public String getIMEI(){

		TelephonyManager mngr = (TelephonyManager) this.getSystemService(this.TELEPHONY_SERVICE); 
		String imei = ""; //mngr.getDeviceId();
		System.out.println("IMEI CELULAR " + imei);
		return imei;
	}// Cierre GetIMEI

	/**
	 * Verifica la clave de acceso diario al aplicativo
	 * @return True si es correcta o False de no serlo
	 */
	private Boolean verificarClave(){
		Boolean salida = false;
		Calendar rightNow = Calendar.getInstance();
		int day = rightNow.get(Calendar.DAY_OF_MONTH);
		int month = rightNow.get(Calendar.MONTH) + 1;
		int year =  rightNow.get(Calendar.YEAR);
		int claveIngresada = 0;

		claveIngresada = Integer.parseInt(clave);		
		int claveConfirmacion = (day*month*year)%(day+month+year);
		if(claveIngresada == claveConfirmacion)
			salida = true;

		return salida;
	}// Cierra verificarClave

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated Method Stub
		switch(arg0.getId()){
		case R.id.btnIngresarApk:
			iniciarTrabajo(); 
			break;
		case R.id.btnBorrarCampos:
			etIngresoCodigo.setText("");
			etIngresoCedula.setText("");	
			btnIngresar.setBackgroundResource(android.R.drawable.btn_default);
			btnIngresar.setEnabled(true);

			/*RestFormatoModel rest = new RestFormatoModel(this);
			rest.obtenerOrdenes("jmunoz","jmunoz","18");*/
			break;		
		}
	}// Cierre onClick

	/**
	 * Inicia el logueo (LOCAL o SERVER) al Aplicativo APK
	 */
	public void iniciarTrabajo(){
		String msjMostrado = "";
		codigo = etIngresoCodigo.getText().toString();
		cedula = etIngresoCedula.getText().toString();
		idTerminal = etIngresoIdTerminal.getText().toString();
		clave = etIngresoClave.getText().toString();

		if(registrarId && idTerminal.isEmpty()){
			msjMostrado = "Debe Indicar ID de Celular";
			etIngresoIdTerminal.setError(msjMostrado);
		}else if(registrarId){
			terminal = Integer.parseInt(idTerminal);
			if(terminal <= 0) {
				msjMostrado += "Id terminal debe ser un numero mayor que cero";
				etIngresoIdTerminal.setError(msjMostrado);
			}
			else tvIdTerminal.setText("ID " + String.valueOf(terminal));
		}

		if(codigo.isEmpty()) {
			msjMostrado = "Debe Indicar Codigo de Usuario";
			etIngresoCodigo.setError(msjMostrado);
		}

		if(cedula.isEmpty()) {
			msjMostrado = "Debe Indicar el codigo de Acceso";
			etIngresoCedula.setError(msjMostrado);
		}

		if(clave.isEmpty()) {
			msjMostrado = "Debe ingresar la clave, sino la tiene solicitela al supervisor";
			etIngresoClave.setError(msjMostrado);
		}
		else if(!verificarClave()) {
			msjMostrado = "Clave Incorrecta, sino la tiene solicitela al supervisor";
			etIngresoClave.setError(msjMostrado);
		}

		if(msjMostrado == ""){
			if(registrarId){
				manager.open();
				manager.ingresarIdTerminal(idTerminal);
				manager.close();
				etIngresoIdTerminal.setVisibility(View.GONE);
				registrarId = false;
				terminal = Integer.parseInt(idTerminal);
				insertar_estados_ingreso("21");
			}

			if(chkBoxCargarOrdenes.isChecked()){
				manager.open();
				existeObs = manager.existeObsCargadas();
				manager.close();
				iniciarLogueoUsuarioServer();
				btnIngresar.setBackgroundColor(Color.RED);
				btnIngresar.setEnabled(false);
			}else{
				iniciarLogueoLocal();
			}
		}//else Toast.makeText(getApplicationContext(), msjMostrado, Toast.LENGTH_SHORT).show();
	}// Cierre iniciarLogueo

	/**
	 * Cargar el logueo Local al aplicativo
	 */
	public void iniciarLogueoLocal(){
		//Autentico el usuario desde la BD local
		cargarOrdenes = false;
		manager.open();
		Cursor cursor = manager.consultarUsuario(codigo);		
		if(cursor.moveToFirst()){
			int CedulaObtenida = cursor.getInt(cursor.getColumnIndex("cedula"));
			String cedulaBD = String.valueOf(CedulaObtenida);
			if(cedula.equals(cedulaBD)){
				codigoObtenido = cursor.getInt(cursor.getColumnIndex("codigo"));
				nombreObtenido  = cursor.getString (cursor.getColumnIndex("nombre"));
				tipoUsuario = cursor.getInt(cursor.getColumnIndex("tipo"));
				System.out.println( "Logueo Local Usuario " + nombreObtenido );
				iniciarEjecucionOrdenes();
			}
		}else Toast.makeText(getApplicationContext(), "Usuario/Password Incorrecto", Toast.LENGTH_SHORT).show();
		manager.close();
		insertar_estados_ingreso("1");
	}//Cierre logueoLocal

	/** 
	 * iniciarLogueoUsuarioServer Arranca el Thread para el inicio del logueo en el sistema
	 */
	public void iniciarLogueoUsuarioServer(){

		trabajoRealizar = 1;
		new ejecutarTask().execute();
	}// Cierre iniciarLogueoUsuarioServer

	/** 
	 * iniciarCargaOrdenesServer Arranca el Thread para cargar las ordenes en sistema
	 */
	public void iniciarCargaOrdenesServer(){

		setProgressBarIndeterminateVisibility(true);
		trabajoRealizar = 2;
		new ejecutarTask().execute();
	}// Cierre iniciarCargaOrdenesServer

	/**
	 * 
	 */
	public void iniciarCargaObservaciones(){
		if(!existeObs){
			trabajoRealizar = 3;
			new ejecutarTask().execute();
		}else iniciarCargaStockServer();
	}

	/** 
	 * iniciarCargaStockServer Arranca el Thread para cargar el Stcok en sistema
	 */
	public void iniciarCargaStockServer(){

		setProgressBarIndeterminateVisibility(true);
		trabajoRealizar = 4;
		new ejecutarTask().execute();
	}// Cierre iniciarCargaStockServer

	/**
	 * Arranca el Ativity listado de Ordenes a Ejecutar
	 */
	public void iniciarEjecucionOrdenes(){ 
		Intent int1 = new Intent(this,EjecucionOrdenes.class);
		Bundle bolsa = new Bundle();				
		bolsa.putString("nombreKey", nombreObtenido);
		bolsa.putInt("codigoKey", codigoObtenido);
		bolsa.putInt("tipo", tipoUsuario);
		bolsa.putInt("terminalKey", terminal);
		bolsa.putString("imei", IMEI);
		bolsa.putDouble("VERSION", VERSION);
		int1.putExtras(bolsa);
		startActivityForResult(int1,111);
	}// Cierre iniciarEjecucionOrdenes

	/**
	 * Arranca el hilo envio ordenes
	 */
	private void iniciarHiloEnvio(){
		Thread thr = new Thread(new Runnable() { 
			public void run() {
				if(banderaEnvio){
					banderaEnvio = false;
					enviarOrdenesEjecutadas(); 
				}
				System.out.println( "ENVIO INFO " + banderaEnvio); 
			} 
		});
		thr.start();
	}// CIerra iniciarHiloEnvio

	private static final int NTHREDS = 5;
	/**
	 * Ejecuta el pool para el envio de ordenes
	 */
	public void enviarOrdenesEjecutadas(){

		ExecutorService executor = Executors.newFixedThreadPool(NTHREDS);
		for (int i = 1; i < 3; i++) {

			Runnable worker = new EnvioDatosExecutor(this.getBaseContext(), i, codigoObtenido, tipoUsuario);
			executor.execute(worker);
		}

		//No se aceptan m?s hilos hasta que finalizan los que est?n en la cola
		// and finish all existing threads in the queue
		executor.shutdown();

		// espero hasta que todos los hilos han finalizado
		while (!executor.isTerminated()) {
		}
		banderaEnvio = true;
		System.out.println( "FINALIZO EXECUTOR HILOS" );
	}// Cierra enviarOrdenesEjecutadas

	/**
	 * Insertar el estado al gps
	 * @param estado
	 */
	public void insertar_estados_ingreso(String estado){
		level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
		batteryPct = level / (float)scale; 

		managerGPS.open();
		managerGPS.insertarGps(String.valueOf(terminal), String.valueOf(codigoObtenido), strLatitud, strLongitud, strAltitud, strVelocidad,IMEI, estado, "0", String.valueOf(level));
		managerGPS.close();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////    INICIO GPS   /////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void onLocationChanged(Location location){
		//Do cool stuff here 		
		strLatitud = String.valueOf(location.getLatitude());
		strLongitud = String.valueOf(location.getLongitude());
		strAltitud = String.valueOf(location.getAltitude());
		strVelocidad = String.valueOf(location.getSpeed());

		System.out.println( "GPS latitud" + location.getLatitude() );
		System.out.println( "GPS longitud" + location.getLongitude() );

		actualizarGps();		
	}

	public void actualizarGps(){

		level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
		batteryPct = level / (float)scale; 

		managerGPS.open();
		managerGPS.insertarGps(String.valueOf(terminal), String.valueOf(codigoObtenido), strLatitud, strLongitud,strAltitud,strVelocidad,"0", "0", "0", String.valueOf(level));
		managerGPS.close();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////    FIN GPS       /////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////    INICIO LOGUEO SERVER  ////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Clase ejecutarTask 
	 * @author DELTEC
	 * Controla el logueo al server, carga de Observaciones y carga de ordenes
	 */
	private class ejecutarTask extends AsyncTask<Void,Void,Void>{

		String msjMostrado = "";
		@Override
		protected void onPreExecute(){//Se ejcuta en primer plano
			switch(trabajoRealizar){
			case 1://LOGUEO
				msjMostrado = "Buscando Usuario....";			
				break;
			case 2:
				msjMostrado = "Cargando Trabajo...."; 
				break;
			case 3:
				msjMostrado = "Cargando Observaciones...."; 
				break;
			case 4:
				msjMostrado = "Iniciando....";
				break;
			}
			if(cargarOrdenes){
				Toast.makeText(getApplicationContext(), msjMostrado + " " + trabajoRealizar, Toast.LENGTH_LONG).show();
			}else Toast.makeText(getApplicationContext(), msjMostrado, Toast.LENGTH_SHORT).show();
		}			

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub Se ejecuta en segundo plano
			int tiempo = 500;

			switch(trabajoRealizar){
			case 1:// Consultar Usuario en el Sistema Local/Server
				intLogueoUsuario = 0;
				logueoServer();				
				while(intLogueoUsuario == 0){
					try {//Dormir Hilo
						Thread.sleep(tiempo);
					} catch (InterruptedException e) { }	
				}
				break;
			case 2:// Sincronizar Ordenes a Ejecutar Local/Server				
				intCargoOrdenes = -1;
				sincronizarOrdenes();				
				while(intCargoOrdenes == -1){
					try {//Dormir Hilo
						Thread.sleep(tiempo*11);
					} catch (InterruptedException e) { }	
				}
				iniciarEjecucionOrdenes();
				try {//Dormir Hilo
					Thread.sleep(tiempo);
				} catch (InterruptedException e) {}	
				break;
			case 3://Sincronizar Observaciones				
				//sincronizarObservaciones();
				try {//Dormir Hilo
					Thread.sleep(tiempo);
				} catch (InterruptedException e) {}	
				break;
				/*case 4://Sincronizar Stock de materiales
				iniciarEjecucionOrdenes();
				intCargoStock = -1;
				sincronizarStock();					
				while(intCargoStock == -1){
					try {//Dormir Hilo
						Thread.sleep(tiempo*8);
					} catch (InterruptedException e) {}	
				}				
				break;*/
			}
			return null;
		}	

		@Override
		protected void onPostExecute(Void aVoid){

			switch(trabajoRealizar){
			case 1://LOGUEO				
				if(intLogueoUsuario == 1){
					msjMostrado = "Bienvenido " + nombreObtenido;
					insertar_estados_ingreso("1");
					iniciarCargaOrdenesServer();					
				}else{
					msjMostrado =  "Contrase?a incorrecta....";
					btnIngresar.setBackgroundResource(android.R.drawable.btn_default);
					btnIngresar.setEnabled(true);
				}			
				break;
			case 2:
				// Sincronizar Ordenes				
				if(intCargoOrdenes > 0){
					msjMostrado = "Cargo ordenes " + intCargoOrdenes;										
				}else{
					msjMostrado =  "No tiene ordenes asignadas";
				}
				btnIngresar.setBackgroundResource(android.R.drawable.btn_default);
				btnIngresar.setEnabled(true);
				iniciarCargaObservaciones();
				break;
			case 3:				
				msjMostrado =  "Cargo observaciones";
				//iniciarCargaStockServer();				
				break;
				/*case 4:
				if(intCargoStock > 0){
					msjMostrado = "Stock de " + nombreObtenido + " cargado " + intCargoStock;					
				}else{
					msjMostrado =  "Sin Stock de Materiales....";
				}
				break;*/

			}
			if(cargarOrdenes){
				Toast.makeText(getApplicationContext(), msjMostrado, Toast.LENGTH_LONG).show();
			}else Toast.makeText(getApplicationContext(), msjMostrado, Toast.LENGTH_SHORT).show();
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			barraProgreso.setProgress(progreso);//Actualizamos la barra de progreso 
		}

		/** 
		 * Logueo usuario por el server en el sistema
		 */
		public void logueoServer(){	

			cargarOrdenes = true;
			Thread sqlThread = new Thread() {
				public void run() {
					Bundle datosUsuario = managerServer.autenticacionCuadrillaBd(codigo, cedula);	
					if(!datosUsuario.isEmpty()){
						codigoObtenido = datosUsuario.getInt("codigoKey");
						nombreObtenido = datosUsuario.getString("nombreKey");
						String cargo = datosUsuario.getString("per_cargo");
						
						if(cargo.contains("SUPERVISOR") || cargo.contains("VERIFICADOR"))
							tipoUsuario = 2;
						else tipoUsuario = 1;
						
						DataBaseManager managerOrden = new DataBaseManager(getBaseContext());
						managerOrden.open();
						managerOrden.insertar_usuario(codigoObtenido,cedula , tipoUsuario, nombreObtenido);
						managerOrden.eliminarOrdenNoEnviada();
						managerOrden.eliminarStockMaterial();
						managerOrden.close();
						intLogueoUsuario = 1;
						System.out.println( "Logueo Usuario " + nombreObtenido );
						
						System.out.println( "Logueo cargo " + cargo + " tipo usuario " + tipoUsuario );
					}else{
						System.out.println( "Usuario no encontrado " ); 
						intLogueoUsuario = 2;
					}
				}
			};
			sqlThread.start();	

		} // Cierre Logueo usuario en el sistema

		/** 
		 * Insertar Ordenes de rs ResultSet en Base de datos
		 * @param  rs
		 */
		private String  insertarOrdenesServicio(ResultSet rs)
		{
			String ordenesFacturacion = "";
			try
			{
				int i_ose_codigo   = rs.findColumn("ose_codigo");
				int i_ose_precarga    = rs.findColumn("ose_precarga");
				int i_tip_orden    = rs.findColumn("ose_tip_codigo");//LECTURA
				int i_cli_contrato    = rs.findColumn("cli_contrato");
				int i_cli_nombre    = rs.findColumn("cli_nombre");
				int i_ciu_nombre    = rs.findColumn("ciu_nombre");
				int i_direccion1    = rs.findColumn("direccion1");
				int i_direccion2    = rs.findColumn("direccion2");
				int i_producto    = rs.findColumn("producto");
				int i_barrio    = rs.findColumn("barrio");
				int i_ciclo    = rs.findColumn("ciclo");
				int i_ose_ruta    = rs.findColumn("ose_ruta");
				int i_ose_ruta_consecutivo    = rs.findColumn("ose_ruta_consecutivo");
				int i_elemento    = rs.findColumn("elemento");
				int i_lectura_anterior    = rs.findColumn("lectura_anterior");//LECTURA
				int i_consumo_promedio    = rs.findColumn("consumo_promedio");//LECTURA
				int i_lectura_actual    = rs.findColumn("lectura_actual");//LECTURA
				int i_cantidad_digitos    = rs.findColumn("cantidad_digitos");//LECTURA
				int i_tipo_producto    = rs.findColumn("ose_tipo_producto");//LECTURA
				int i_franja    = rs.findColumn("consumo");//LECTURA
				int i_estado_fens = rs.findColumn("ose_estado_fens");

				DataBaseManager managerOrden = new DataBaseManager(getBaseContext());


				int ose_codigo = 0, ose_precarga = 0, tip_orden = 0, cli_contrato = 0, ciclo = 0,
						ose_ruta_consecutivo = 0, lectura_anterior = 0,consumo_promedio = 0, lectura_actual = 0 ;
				long ose_ruta = 0;
				String cli_nombre = "", ciu_nombre = "", direccion1 = "", direccion2 = "", producto = "", 
						barrio = "", elemento = "", cantidad_digitos = "", tipo_producto = "", franja = "", estado_fens="";
				int medidor = 0;

				rs.last(); //me voy al ?ltimo
				int tamano = rs.getRow(); //pillo el tama?o
				rs.beforeFirst(); // lo dejo donde estaba para tratarlo 

				barraProgreso.setProgress(0);//Ponemos la barra de progreso a 0
				barraProgreso.setMax(tamano);//El m?ximo de la barra de progreso son 60, de los 60 segundo que va a durar la tarea en segundo plano.


				managerOrden.open();

				while(rs.next()) {

					intCargoOrdenes++;

					ose_codigo = rs.getInt(i_ose_codigo);
					ose_precarga = rs.getInt(i_ose_precarga);
					tip_orden = rs.getInt(i_tip_orden);					
					cli_contrato = rs.getInt(i_cli_contrato);
					cli_nombre  = rs.getString (i_cli_nombre);
					ciu_nombre  = rs.getString (i_ciu_nombre);
					direccion1  = rs.getString (i_direccion1);
					direccion2  = rs.getString (i_direccion2);
					producto  = rs.getString (i_producto);
					barrio  = rs.getString (i_barrio);
					ciclo = rs.getInt(i_ciclo);
					ose_ruta = rs.getLong(i_ose_ruta);
					ose_ruta_consecutivo = rs.getInt(i_ose_ruta_consecutivo);
					elemento = rs.getString(i_elemento);
					lectura_anterior = rs.getInt(i_lectura_anterior);
					consumo_promedio = rs.getInt(i_consumo_promedio);
					lectura_actual = rs.getInt(i_lectura_actual);
					cantidad_digitos = rs.getString(i_cantidad_digitos);
					tipo_producto = rs.getString(i_tipo_producto);
					franja = rs.getString(i_franja);
					estado_fens= rs.getString(i_estado_fens);

					try {//CONVIERTO MEDIDOR EN NUMERICO
						medidor = Integer.parseInt(elemento);
						elemento = String.valueOf(medidor);
					} catch (NumberFormatException e) {

					}
					ordenesFacturacion = ordenesFacturacion +","+ ose_codigo;
					managerOrden.insertar_orden(ose_codigo, ose_precarga,tip_orden, cli_contrato, cli_nombre, ciu_nombre, direccion1, direccion2, 
							producto, barrio, ciclo,(int) ose_ruta, ose_ruta_consecutivo, 1, "",codigoObtenido,elemento,lectura_anterior,consumo_promedio,
							lectura_actual,cantidad_digitos, tipo_producto ,franja, 0, estado_fens);

					progreso++;
					publishProgress();
				}
				managerOrden.close();

			}
			catch(Exception e){
				ordenesFacturacion = "";
			}

			Log.d("ordenesFacturacion: ", ordenesFacturacion);

			intCargoOrdenes++;
			return ordenesFacturacion.substring(1,ordenesFacturacion.length());
		} // Cierre insertar Ordenes de rs ResultSet en Base de datos


		/**
		 * Insertar Detalle de Ordenes de rs ResultSet en Base de datos
		 * @param  rs
		 */
		private String  insertarDetalleOrdenes(ResultSet rs) {
			String ordenID = "'";
			String CODDELTEC = "'";
			try {
				rs.last(); //me voy al ?ltimo
				int tamano = rs.getRow(); //pillo el tama?o
				rs.beforeFirst(); // lo dejo donde estaba para tratarlo

				barraProgreso.setProgress(0);//Ponemos la barra de progreso a 0
				barraProgreso.setMax(tamano);//El m?ximo de la barra de progreso son 60, de los 60 segundo que va a durar la tarea en segundo plano.

				DataBaseManager managerOrden = new DataBaseManager(getBaseContext());
				managerOrden.open();

				Log.d("INICIO", "");
				//Tablas_BD tablas_bd = new Tablas_BD ( new DbHelper(MyApplication.getContext()).getDatabaseName(), MyApplication.getContext());
				//tablas_bd.crearTablas();
				while (rs.next()) {

					managerOrden.insertarDetalleOrdenes(rs.getString("ose_codigo"), rs.getString("orden_id"), rs.getString("ruta"));

					ordenID += rs.getString("orden_id") + "','";
					CODDELTEC += rs.getString("CODDELTEC") + "','";
					progreso++;
					publishProgress();
				}
				//managerOrden.close();
				rs.close();
				ordenID += "0'";
				//ordenID = ordenID.substring(0,ordenID.length()-2); Comentado por que saca la orden 0
				CODDELTEC = CODDELTEC.substring(0, CODDELTEC.length() - 2);

				sincronizarGetRangos(CODDELTEC);
				sincronizarFacLaborConceptos(ordenID, CODDELTEC);
				sincronizarElementoLectura(ordenID, CODDELTEC);
				sincronizarScmMultitabla(CODDELTEC);
				sincronizarOrdenesTrabajo(ordenID, CODDELTEC);
				sincronizarFacImpresion(ordenID, CODDELTEC);

				// Elementos Lectura
				// Rangos
				//managerOrden.open();
				//rs = managerServer.getFacImpresion(ordenID, CODDELTEC);
/*
				while (rs.next()) {

					String ruta = rs.getString("ruta").contains("^") ? rs.getString("ruta").replace("^","|") : rs.getString("ruta");
					String orden_id = rs.getString("orden_id").contains("^") ? rs.getString("orden_id").replace("^","|") : rs.getString("orden_id");
					String idimpresion = rs.getString("idimpresion").contains("^") ? rs.getString("idimpresion").replace("^","|") : rs.getString("idimpresion");
					String posxy = rs.getString("posxy").contains("^") ? rs.getString("posxy").replace("^","|") : rs.getString("posxy");
					String fuente = rs.getString("fuente").contains("^") ? rs.getString("fuente").replace("^","|") : rs.getString("fuente");
					String justificacion = rs.getString("justificacion").contains("^") ? rs.getString("justificacion").replace("^","|") : rs.getString("justificacion");
					String funcion = rs.getString("funcion").contains("^") ? rs.getString("funcion").replace("^","|") : rs.getString("funcion");
					String parametros = "";
					if(rs.getString("parametros") != null ) {
						parametros = rs.getString("parametros");
					}
					String valor = "";
					if(rs.getString("valor") != null){
						valor = rs.getString("valor");
					}
					String fecha_actualizacion = String.valueOf(rs.getDate("fecha_actualizacion")).contains("^") ? String.valueOf(rs.getDate("fecha_actualizacion")).replace("^","|") : String.valueOf(rs.getDate("fecha_actualizacion"));

					managerOrden.insertarFacImpresion(ruta, orden_id, idimpresion, posxy, fuente, justificacion, funcion, parametros, valor, fecha_actualizacion);

					progreso++;
					publishProgress();
				}
*/
				//rs.close();




//				progreso++;
//				publishProgress();


//				rs = managerServer.getRangos(CODDELTEC);
//				while (rs.next()) {
//
//					String ruta = rs.getString("ruta");
//					String tipoRango = rs.getString("tiporango");
//
//					String codRango= rs.getString("codrango");
//					String desde = "";
//					if(rs.getString("desde") != null ) {
//						desde = rs.getString("desde");
//					}
//					String hasta = "";
//					if(rs.getString("hasta") != null ) {
//						hasta = rs.getString("hasta");
//					}
//					String valor = "";
//					if(rs.getString("valor") != null ) {
//						valor = rs.getString("valor");
//					}
//					String valorresta = "";
//					if(rs.getString("valorresta") != null ) {
//						valorresta = rs.getString("valorresta");
//					}
//					String fecha_actualizacion = String.valueOf(rs.getDate("fecha_actualizacion"));
//
//					managerOrden.insertarFacRangos(ruta,
//							tipoRango, codRango, desde, hasta,
//							valor, valorresta, fecha_actualizacion);
//
//					progreso++;
//					publishProgress();
//				}
				// Labor Conceptos
//				rs = managerServer.getFacLaborConceptos(ordenID, CODDELTEC);
//				while (rs.next()) {
//
//					String ruta = rs.getString("ruta");
//					String orden_id = rs.getString("orden_id");
//					String idconcepto = rs.getString("idconcepto");
//					String orden = rs.getString("orden");
//					String saldoanterior = rs.getString("saldoanterior");
//					String idgrupolectura = rs.getString("idgrupolectura");
//					String cantbase = rs.getString("cantbase");
//					String canttiporango = rs.getString("canttiporango");
//					String cantcodrango = rs.getString("cantcodrango");
//					String cantrutina = rs.getString("cantrutina");
//					String cantidad = rs.getString("cantidad");
//					String factbase = rs.getString("factbase");
//					String factcodrango = rs.getString("factcodrango");
//					String factrutina = rs.getString("factrutina");
//					String factor = rs.getString("factor");
//					String tarifabase = rs.getString("tarifabase");
//					String tarifatiporango = rs.getString("tarifatiporango");
//					String tarifarutina = rs.getString("tarifarutina");
//					String tarifa = rs.getString("tarifa");
//					String valorbase = rs.getString("valorbase");
//					String valortiporango = rs.getString("valortiporango");
//					String valorcodrango = rs.getString("valorcodrango");
//					String valorrutina = rs.getString("valorrutina");
//					String valor = rs.getString("valor");
//					String fecha_actualizacion = String.valueOf(rs.getDate("fecha_actualizacion"));
//
//					managerOrden.insertarFacLaborConceptos(ruta, orden_id, idconcepto, orden, saldoanterior,
//							idgrupolectura, cantbase, canttiporango, cantcodrango, cantrutina, cantidad,
//							factbase, factcodrango, factrutina, factor, tarifabase, tarifatiporango,
//							tarifarutina, tarifa, valorbase, valortiporango, valorcodrango, valorrutina,
//							valor, fecha_actualizacion);
//
//					progreso++;
//					publishProgress();
//				}
//				rs = managerServer.getFacImpresion(ordenID, CODDELTEC);
//				while (rs.next()) {
//
//
//
//					String ruta = rs.getString("ruta").contains("^") ? rs.getString("ruta").replace("^","|") : rs.getString("ruta");
//					String orden_id = rs.getString("orden_id").contains("^") ? rs.getString("orden_id").replace("^","|") : rs.getString("orden_id");
//					String idimpresion = rs.getString("idimpresion").contains("^") ? rs.getString("idimpresion").replace("^","|") : rs.getString("idimpresion");
//					String posxy = rs.getString("posxy").contains("^") ? rs.getString("posxy").replace("^","|") : rs.getString("posxy");
//					String fuente = rs.getString("fuente").contains("^") ? rs.getString("fuente").replace("^","|") : rs.getString("fuente");
//					String justificacion = rs.getString("justificacion").contains("^") ? rs.getString("justificacion").replace("^","|") : rs.getString("justificacion");
//					String funcion = rs.getString("funcion").contains("^") ? rs.getString("funcion").replace("^","|") : rs.getString("funcion");
//					String parametros = "";
//					if(rs.getString("parametros") != null ) {
//						parametros = rs.getString("parametros").contains("^") ? rs.getString("parametros").replace("^","|") : rs.getString("parametros");
//					}
//					String valor = "";
//					if(rs.getString("valor") != null){
//						valor = rs.getString("valor").contains("^") ? rs.getString("valor").replace("^","|") : rs.getString("valor");
//					}
//					String fecha_actualizacion = String.valueOf(rs.getDate("fecha_actualizacion")).contains("^") ? String.valueOf(rs.getDate("fecha_actualizacion")).replace("^","|") : String.valueOf(rs.getDate("fecha_actualizacion"));
//
//					managerOrden.insertarFacImpresion(ruta, orden_id, idimpresion, posxy, fuente, justificacion, funcion, parametros, valor, fecha_actualizacion);
//
//					progreso++;
//					publishProgress();
//				}
//				rs = managerServer.getElementosLectura(ordenID, CODDELTEC);
//				while (rs.next()) {
//
//					String ruta = rs.getString("ruta");
//					String orden_id = rs.getString("orden_id");
//					String tipo = rs.getString("tipo");
//					String serie = rs.getString("serie");
//					String marca = rs.getString("marca");
//					String modelo = rs.getString("modelo");
//					String tipo_lectura = rs.getString("tipo_lectura");
//					String enteros = Integer.toString(rs.getInt("enteros"));
//					String decimales = Integer.toString(rs.getInt("decimales"));
//					String factor_multiplicacion = Integer.toString(rs.getInt("factor_multiplicacion"));
//					String fecha_anterior = String.valueOf(rs.getDate("fecha_anterior"));
//					String lectura_anterior = rs.getString("lectura_anterior");
//					String limite_inferior_consumo = rs.getString("limite_inferior_consumo");
//					String limite_superior_consumo = rs.getString("limite_superior_consumo");
//					String ubicacion = String.valueOf(rs.getInt("ubicacion"));
//					String secuencia = String.valueOf(rs.getInt("secuencia"));
//					String consumo1 = String.valueOf(rs.getInt("consumo1"));
//					String solconsumo1 = rs.getString("solconsumo1");
//					String consumo2 = String.valueOf(rs.getInt("consumo2"));
//					String solconsumo2 = rs.getString("solconsumo2");
//					String fecha_lectura = String.valueOf(rs.getDate("fecha_lectura"));
//					String lectura_tomada = rs.getString("lectura_tomada");
//					String observacion = rs.getString("observacion");
//					String observacion_ad = rs.getString("observacion_ad");
//					String observacion_texto = rs.getString("observacion_texto");
//					String intentos = rs.getString("intentos");
//					String consumo = String.valueOf(rs.getInt("consumo"));
//					String solconsumo = rs.getString("solconsumo");
//					String fecha_actualizacion = String.valueOf(rs.getDate("fecha_actualizacion"));
//
//					managerOrden.insertarElementosLectura(ruta, orden_id, tipo, serie, marca, modelo, tipo_lectura,
//							enteros, decimales, factor_multiplicacion, fecha_anterior, lectura_anterior, limite_inferior_consumo,
//							limite_superior_consumo, ubicacion, secuencia, consumo1, solconsumo1, consumo2,
//							solconsumo2, fecha_lectura, lectura_tomada, observacion, observacion_ad, observacion_texto,
//							intentos, consumo, solconsumo, fecha_actualizacion);
//
//					progreso++;
//					publishProgress();
//				}

				// Ordenes de Trabajo
//				rs = managerServer.getOrdenesTrabajo(ordenID, CODDELTEC);
//				while (rs.next()) {
//
//					String ruta = rs.getString("ruta");
//					String orden_id = rs.getString("orden_id");
//					String estado = rs.getString("estado");
//					String nombre = rs.getString("nombre");
//					String direccion = rs.getString("direccion");
//					String gps = rs.getString("gps");
//					String imprimir_factura = rs.getString("imprimir_factura");
//					String exigir_foto = rs.getString("exigir_foto");
//					String orden_id_relacionado = rs.getString("orden_id_relacionado");
//					String fecha_actualizacion = rs.getString("fecha_actualizacion");
//					String tipo = rs.getString("tipo");
//					String ruta_lectura = String.valueOf(rs.getInt("ruta_lectura"));
//
//					managerOrden.insertarOrdenesTrabajo(ruta, orden_id, estado, nombre, direccion, gps, imprimir_factura, exigir_foto,
//							orden_id_relacionado, fecha_actualizacion, tipo, ruta_lectura);
//
//					progreso++;
//					publishProgress();
//				}
				// Facturaci?n Impresi?n


				managerOrden.close();

			} catch (Exception e) {
				Log.d("insertarDetalleOrdenes", e.toString());
			}

			return "";
		} // Cierre insertar Detalle Ordenes de rs ResultSet en Base de datos

		/** 
		 * Insertar Ordenes de rs ResultSet en Base de datos
		 * @param   rs
		 */
		private void insertarStock(ResultSet rs)
		{
			try
			{
				int i_rec_codigo   = rs.findColumn("rec_codigo");
				int i_rec_nombre	= rs.findColumn("rec_nombre");
				int i_rec_prefijo	= rs.findColumn("dei_prefijo");
				int i_rec_serie    = rs.findColumn("dei_serie");
				int i_rec_cantidad    = rs.findColumn("dei_cantidad");
				int i_rec_seriado    = rs.findColumn("rec_seriado");
				int i_rec_unidad    = rs.findColumn("tip_nombre");

				int rec_codigo = 0, rec_cantidad = 0;
				String rec_nombre = "", rec_prefijo = "", rec_serie = "", rec_seriado = "", rec_unidad = "";

				DataBaseManager managerOrden = new DataBaseManager(getBaseContext());

				rs.last(); //me voy al ?ltimo
				int tamano = rs.getRow(); //pillo el tama?o
				rs.beforeFirst(); // lo dejo donde estaba para tratarlo 

				barraProgreso.setProgress(0);//Ponemos la barra de progreso a 0
				barraProgreso.setMax(tamano);//El m?ximo de la barra de progreso son 60, de los 60 segundo que va a durar la tarea en segundo plano.

				progreso = 0;
				int contador = 0;

				while(rs.next()) {

					intCargoStock++;

					rec_codigo = rs.getInt(i_rec_codigo);					
					rec_nombre = rs.getString(i_rec_nombre);
					rec_prefijo  = rs.getString (i_rec_prefijo);
					rec_serie  = rs.getString (i_rec_serie);
					rec_cantidad = rs.getInt(i_rec_cantidad);
					rec_seriado  = rs.getString (i_rec_seriado);
					rec_unidad  = rs.getString (i_rec_unidad);

					managerOrden.open();
					managerOrden.insertar_stock(rec_codigo, rec_nombre, rec_prefijo, rec_serie, rec_cantidad, rec_seriado, rec_unidad, codigoObtenido);
					managerOrden.close();
					progreso++;
					if (progreso < tamano){
						contador++;
						if(contador == 100){
							contador = 0;

							publishProgress();
						}
					}else{
						publishProgress();
						System.gc();
					}
				}	
			}
			catch(Exception e){
			}
			intCargoStock++;
		} // Cierre insertar Ordenes de rs ResultSet en Base de datos

		/**
		 * Ejecuta el hilo de carga ordenes eliminando las enviadas y las fotos
		 */
		
		public void sincronizarGetRangos(String CodDeltec) throws SQLException {

            System.out.println("PAUPURRI" + CodDeltec);
			DataBaseManager managerOrden = new DataBaseManager(getBaseContext());
			managerOrden.open();

			ResultSet rs;
			rs = managerServer.getRangos(CodDeltec);

			managerOrden.begin();
			while (rs.next()) {

				String ruta = rs.getString("ruta");
				String tipoRango = rs.getString("tiporango");

				String codRango= rs.getString("codrango");
				String desde = "";
				if(rs.getString("desde") != null ) {
					desde = rs.getString("desde");
				}
				String hasta = "";
				if(rs.getString("hasta") != null ) {
					hasta = rs.getString("hasta");
				}
				String valor = "";
				if(rs.getString("valor") != null ) {
					valor = rs.getString("valor");
				}
				String valorresta = "";
				if(rs.getString("valorresta") != null ) {
					valorresta = rs.getString("valorresta");
				}
				String fecha_actualizacion = String.valueOf(rs.getDate("fecha_actualizacion"));

				managerOrden.insertarFacRangos(ruta,
						tipoRango, codRango, desde, hasta,
						valor, valorresta, fecha_actualizacion);

				progreso++;
				publishProgress();
			}
			rs.close();

			managerOrden.end();
			managerOrden.close();
			
		}

		public void sincronizarFacLaborConceptos(String ordenesId, String codDeltec) throws SQLException {
			String OrdenesId = ordenesId;
			String CodDeltec = codDeltec;

			DataBaseManager managerOrden = new DataBaseManager(getBaseContext());
			managerOrden.open();

			ResultSet rs;
			rs = managerServer.getFacLaborConceptos(OrdenesId, CodDeltec);

			managerOrden.begin();
			while (rs.next()) {


				String ruta = rs.getString("ruta");
				String orden_id = rs.getString("orden_id");
				String idconcepto = rs.getString("idconcepto");
				String orden = rs.getString("orden");
				String saldoanterior = rs.getString("saldoanterior");
				String idgrupolectura = rs.getString("idgrupolectura");
				String cantbase = rs.getString("cantbase");
				String canttiporango = rs.getString("canttiporango");
				String cantcodrango = rs.getString("cantcodrango");
				String cantrutina = rs.getString("cantrutina");
				String cantidad = rs.getString("cantidad");
				String factbase = rs.getString("factbase");
				String factiporango = rs.getString("facttiporango");
				String factcodrango = rs.getString("factcodrango");
				String factrutina = rs.getString("factrutina");
				String factor = rs.getString("factor");
				String tarifabase = rs.getString("tarifabase");
				String tarifatiporango = rs.getString("tarifatiporango");
				String tarifarutina = rs.getString("tarifarutina");
				String tarifa = rs.getString("tarifa");
				String valorbase = rs.getString("valorbase");
				String valortiporango = rs.getString("valortiporango");
				String valorcodrango = rs.getString("valorcodrango");
				String valorrutina = rs.getString("valorrutina");
				String valor = rs.getString("valor");
				String fecha_actualizacion = String.valueOf(rs.getDate("fecha_actualizacion"));

				managerOrden.insertarFacLaborConceptos(ruta, orden_id, idconcepto, orden, saldoanterior,
						idgrupolectura, cantbase, canttiporango, cantcodrango, cantrutina, cantidad,
						factbase,factiporango, factcodrango, factrutina, factor, tarifabase, tarifatiporango,
						tarifarutina, tarifa, valorbase, valortiporango, valorcodrango, valorrutina,
						valor, fecha_actualizacion);

				progreso++;
				publishProgress();
			}
			rs.close();

			managerOrden.end();
			managerOrden.close();

		}
		public void sincronizarElementoLectura(String ordenesId, String codDeltec) throws SQLException {
			DataBaseManager managerOrden = new DataBaseManager(getBaseContext());

			managerOrden.open();
			ResultSet rs;

			rs = managerServer.getElementosLectura(ordenesId, codDeltec);

			managerOrden.begin();
			while (rs.next()) {

				String ruta = rs.getString("ruta");
				String orden_id = rs.getString("orden_id");
				String tipo = rs.getString("tipo");
				String serie = rs.getString("serie");
				String marca = rs.getString("marca");
				String modelo = rs.getString("modelo");
				String tipo_lectura = rs.getString("tipo_lectura");
				String enteros = Integer.toString(rs.getInt("enteros"));
				String decimales = Integer.toString(rs.getInt("decimales"));
				String factor_multiplicacion = Integer.toString(rs.getInt("factor_multiplicacion"));
				String fecha_anterior = String.valueOf(rs.getDate("fecha_anterior"));
				String lectura_anterior = rs.getString("lectura_anterior");
				String limite_inferior_consumo = rs.getString("limite_inferior_consumo");
				String limite_superior_consumo = rs.getString("limite_superior_consumo");
				String ubicacion = String.valueOf(rs.getInt("ubicacion"));
				String secuencia = String.valueOf(rs.getInt("secuencia"));
				String consumo1 = String.valueOf(rs.getInt("consumo1"));
				String solconsumo1 = rs.getString("solconsumo1");
				String consumo2 = String.valueOf(rs.getInt("consumo2"));
				String solconsumo2 = rs.getString("solconsumo2");
				String fecha_lectura = String.valueOf(rs.getDate("fecha_lectura"));
				String lectura_tomada = rs.getString("lectura_tomada");
				String observacion = rs.getString("observacion");
				String observacion_ad = rs.getString("observacion_ad");
				String observacion_texto = rs.getString("observacion_texto");
				String intentos = rs.getString("intentos");
				String consumo = String.valueOf(rs.getInt("consumo"));
				String solconsumo = rs.getString("solconsumo");
				String fecha_actualizacion = String.valueOf(rs.getDate("fecha_actualizacion"));

				managerOrden.insertarElementosLectura(ruta, orden_id, tipo, serie, marca, modelo, tipo_lectura,
						enteros, decimales, factor_multiplicacion, fecha_anterior, lectura_anterior, limite_inferior_consumo,
						limite_superior_consumo, ubicacion, secuencia, consumo1, solconsumo1, consumo2,
						solconsumo2, fecha_lectura, lectura_tomada, observacion, observacion_ad, observacion_texto,
						intentos, consumo, solconsumo, fecha_actualizacion);

				//publishProgress();
			}
			rs.close();

			managerOrden.end();
			//managerOrden.close();

		}
		public void sincronizarScmMultitabla(String codDeltec) throws SQLException {
			DataBaseManager managerOrden = new DataBaseManager(getBaseContext());

			managerOrden.open();
			ResultSet rs;

			rs = managerServer.getMultitabla(codDeltec);
			managerOrden.begin();
			while (rs.next()) {

				String tabla = rs.getString("tabla");
				String codigo = rs.getString("codigo");
				String descripcion = rs.getString("decripcion");
				String filtro = rs.getString("filtro");
				String parametros = rs.getString("parametros");

				managerOrden.insertarMultitabla(tabla,codigo,descripcion,filtro,parametros);

			}
			rs.close();
			managerOrden.end();

		}

		public void sincronizarOrdenesTrabajo(String ordenesId, String codDeltec) throws SQLException {
			DataBaseManager managerOrden = new DataBaseManager(getBaseContext());

			managerOrden.open();
			ResultSet rs;

			rs = managerServer.getOrdenesTrabajo(ordenesId, codDeltec);
			managerOrden.begin();
			while (rs.next()) {

				String ruta = rs.getString("ruta");
				String orden_id = rs.getString("orden_id");
				String estado = rs.getString("estado");
				String nombre = rs.getString("nombre");
				String direccion = rs.getString("direccion");
				String gps = rs.getString("gps");
				String imprimir_factura = rs.getString("imprimir_factura");
				String exigir_foto = rs.getString("exigir_foto");
				String orden_id_relacionado = rs.getString("orden_id_relacionado");
				String fecha_actualizacion = rs.getString("fecha_actualizacion");
				String tipo = rs.getString("tipo");
				String ruta_lectura = String.valueOf(rs.getInt("ruta_lectura"));

				managerOrden.insertarOrdenesTrabajo(ruta, orden_id, estado, nombre, direccion, gps, imprimir_factura, exigir_foto,
						orden_id_relacionado, fecha_actualizacion, tipo, ruta_lectura);

			}
			rs.close();
			managerOrden.end();

		}



		public void sincronizarFacImpresion(String ordenesId, String codDeltec) throws SQLException {
			DataBaseManager managerOrden = new DataBaseManager(getBaseContext());
			managerOrden.open();
			ResultSet rs;
			rs = managerServer.getFacImpresion(ordenesId, codDeltec);
			managerOrden.begin();
			while (rs.next()) {

				String ruta = rs.getString("ruta").contains("^") ? rs.getString("ruta").replace("^","|") : rs.getString("ruta");
				String orden_id = rs.getString("orden_id").contains("^") ? rs.getString("orden_id").replace("^","|") : rs.getString("orden_id");
				String idimpresion = rs.getString("idimpresion").contains("^") ? rs.getString("idimpresion").replace("^","|") : rs.getString("idimpresion");
				String posxy = rs.getString("posxy").contains("^") ? rs.getString("posxy").replace("^","|") : rs.getString("posxy");
				String fuente = rs.getString("fuente").contains("^") ? rs.getString("fuente").replace("^","|") : rs.getString("fuente");
				String justificacion = rs.getString("justificacion").contains("^") ? rs.getString("justificacion").replace("^","|") : rs.getString("justificacion");
				String funcion = rs.getString("funcion").contains("^") ? rs.getString("funcion").replace("^","|") : rs.getString("funcion");
				String parametros = "";
				if(rs.getString("parametros") != null ) {
					parametros = rs.getString("parametros").contains("^") ? rs.getString("parametros").replace("^","|") : rs.getString("parametros");
				}
				String valor = "";
				if(rs.getString("valor") != null){
					valor = rs.getString("valor").contains("^") ? rs.getString("valor").replace("^","|") : rs.getString("valor");
				}
				String fecha_actualizacion = String.valueOf(rs.getDate("fecha_actualizacion")).contains("^") ? String.valueOf(rs.getDate("fecha_actualizacion")).replace("^","|") : String.valueOf(rs.getDate("fecha_actualizacion"));

				managerOrden.insertarFacImpresion(ruta, orden_id, idimpresion, posxy, fuente, justificacion, funcion, parametros, valor, fecha_actualizacion);
//				if(contador % 20 == 0){
//					managerOrden.close();
//					managerOrden.open();
//					contador= 1;
//				}
//				progreso++;
//				publishProgress();
			}
			rs.close();

			managerOrden.end();
			//managerOrden.close();

		}

			public void sincronizarOrdenes(){

			Thread sqlThread = new Thread() {
				public void run() {
					progreso = 0;
					barraProgreso.setProgress(0);//Ponemos la barra de progreso a 0
					barraProgreso.setMax(3);//El m?ximo de la barra de progreso son 60, de los 60 segundo que va a durar la tarea en segundo plano.
					progreso++;						
					publishProgress();	
					ResultSet rs  = managerServer.sincronizarOrdenes(codigo, tipoUsuario);
					DataBaseManager managerOrden = new DataBaseManager(getBaseContext());						
					progreso++;						
					publishProgress();						
					managerOrden.open();
					managerOrden.eliminarOrdenesEnviadas();
					managerOrden.eliminarFotosEnviadas();
					/*SCM*/
                    managerOrden.eliminarSCM();
					/*SCM*/
					managerOrden.close();
					progreso++;
					publishProgress();

					String  ordenesFacturacion = insertarOrdenesServicio(rs);

					rs = managerServer.getObsConsumo();
					insertarObservacionSCM(rs);
					rs  = managerServer.sincronizarObservaciones();
					insertarObservaciones(rs);
					rs = managerServer.detalle_orden_facturacion(ordenesFacturacion);
					insertarDetalleOrdenes(rs);



					// for(String orden: ordenesFacturacion){
					// Toast.makeText(getApplicationContext(), ordenesFacturacion, Toast.LENGTH_LONG).show();
					// }

//					ResultSet rs  = managerServer.sincronizarOrdenes(codigo, tipoUsuario);
				}
			};
			sqlThread.start();
		}// Cierre SincronizarOrdenes

		public  void  insertarObservacionSCM( ResultSet rs){
			try {
			// SCM OSERVACIONES CONSUMO
			rs.last(); //me voy al ?ltimo
			int tamano = rs.getRow(); //pillo el tama?o
			rs.beforeFirst(); // lo dejo donde estaba para tratarlo

			barraProgreso.setProgress(0);//Ponemos la barra de progreso a 0
			barraProgreso.setMax(tamano);//El m?ximo de la barra de progreso son 60, de los 60 segundo que va a durar la tarea en segundo plano.


			DataBaseManager managerOrden = new DataBaseManager(getBaseContext());
			managerOrden.open();

			while (rs.next()) {

				String cod_observacion = rs.getString("codobservacion");
				String descripcion = "";
				if(rs.getString("descripcion") != null ) {
					descripcion = rs.getString("descripcion").contains("^") ? rs.getString("descripcion").replace("^","|") : rs.getString("descripcion");
				}
				//String descripcion = rs.getString("descripcion");
				String sol_consumo = "";
				if(rs.getString("solconsumo") != null ) {
					sol_consumo = rs.getString("solconsumo").contains("^") ? rs.getString("solconsumo").replace("^","|") : rs.getString("solconsumo");
				}
				//String sol_consumo = rs.getString("solconsumo");
				String tip_codigo = rs.getString("tip_codigo");

				managerOrden.insertarObsConsumo(cod_observacion, descripcion, sol_consumo, tip_codigo);

				progreso++;
				publishProgress();
			}

			// SCM OSERVACIONES NO LECTURA
			rs = managerServer.getObsNoLectura();
			while (rs.next()) {

				String cod_observacion = rs.getString("codcausa");
				String descripcion = rs.getString("descripcion");
				String sol_consumo = rs.getString("solconsumo");
				String tip_codigo = rs.getString("tip_codigo");

				managerOrden.insertarObsNoLectura(cod_observacion, descripcion, sol_consumo, tip_codigo);

				progreso++;
				publishProgress();
			}
			}catch(Exception e){

			}
		}
		/**
		 * Sincroniza el stock de material
		 */
		public void sincronizarStock(){	

			Thread sqlThread = new Thread() {
				public void run() {
					ResultSet rs  = managerServer.sincronizarStock(codigo);	
					insertarStock(rs); 
					//manager.insertar_stock_masivo_bulk(rs,codigoObtenido);		
				}
			};
			sqlThread.start();	
		}// Cierre SincronizarStock

		/** 
		 * Sincronizar observaciones del servidor
		 */
		public void sincronizarObservaciones(){	

			Thread sqlThread = new Thread() {
				public void run() {
					ResultSet rs  = managerServer.sincronizarObservaciones();						 
					insertarObservaciones(rs);	
					//manager.insertar_stock_masivo_bulk(rs,codigoObtenido);		
				}
			};
			sqlThread.start();
		}// Cierre insertar Observaciones de rs ResultSet en Base de datos


		/** 
		 * Insertar Observaciones de rs ResultSet en Base de datos
		 * @param   rs
		 */
		private void insertarObservaciones(ResultSet rs)
		{
			DataBaseManager managerOrden = new DataBaseManager(getBaseContext());

			try
			{
				int i_cod_observacion  = rs.findColumn("codobservacion");
				int i_tip_codigo   = rs.findColumn("tip_codigo");
				int i_sol_consumo    = rs.findColumn("solconsumo");
				int i_tip_nombre   = rs.findColumn("descripcion");
				int tip_codigo = 0;
				String tip_nombre = "", sol_consumo= "", cod_observacion = "";

				while(rs.next()) {
					cod_observacion = rs.getString(i_cod_observacion);
					tip_codigo = rs.getInt(i_tip_codigo);
					sol_consumo = rs.getString(i_sol_consumo);
					tip_nombre = rs.getString(i_tip_nombre);

//					cod_observacion = rs.getString(i_cod_observacion);
//					tip_codigo = rs.getInt(i_tip_codigo);
//					if(rs.getString(i_sol_consumo) != null ) {
//						sol_consumo = rs.getString(i_sol_consumo);
//					}
//					if(rs.getString(i_tip_nombre) != null ) {
//						tip_nombre = rs.getString(i_tip_nombre);
//					}


//					if(tip_nombre.length() < 4)
//						tip_nombre = "";
					managerOrden.open();
					managerOrden.insertar_observacion(cod_observacion, tip_codigo, sol_consumo, tip_nombre);
					managerOrden.close();
				}
				//TRASLADAR A PESADA CAMPOS FIJOS
				//managerOrden.insertar_observacion(1474646, 77, "SELLO ROJO");
				//managerOrden.insertar_observacion(1475442, 77, "SELLO TRANSPARENTE");
			}
			catch(Exception e){

			}

		} // Cierre insertar Observaciones de rs ResultSet en Base de datos
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////    FIN LOGUEO SERVER  ////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}
