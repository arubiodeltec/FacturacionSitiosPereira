package com.example.verorden;

import com.example.gestiondeltec.R;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import com.example.Logueo.DataBaseManager;
import android.os.Looper;

import androidx.fragment.app.Fragment;

import com.zebra.sdk.comm.BluetoothConnectionInsecure;
import com.zebra.sdk.comm.Connection;


/**
 * Esta clase Fragment para la toma de lecturas
 * @author: Jasson Trujillo Ortiz
 * @version: 15/10/2016/A
 * @see <a href = "http://www.deltec.com.co" /> Deltec S.A. </a>
 */
public class TabOrdenTomarLectura extends Fragment implements OnClickListener  {

	public static final String TAG = "tabTomarLectura";
	private static final int REQUEST_ENABLE_BT = 0;
	private static final UUID MY_UUID = null;
	LinearLayout layoutPanelPrincipal, layoutLectura, layoutMedidorEncontrado;
	CheckBox chkMotivo;
	Spinner spMotivoLectura, spObsLectura;
	TextView tvLecturaTipoOrden,tvLecturaMedidor,tvLecturaDireccion;
	EditText etLectura, etObsLectura;
	Button btConfirmarLectura;
	Switch stMedidorCorrecto;
	ImageView foto;
	int ose_codigo;
	String theBtMacAddress = "";
	private DataBaseManager manager;
	public static Bundle bolsaDatosIniciales;

	private FragmentIterationListenerLectura mCallback = null;


	/**
	 * INTERFAZ PARA INTERACTUAR CON EL ACTIVITY
	 * @author DELTEC
	 *
	 */
	public interface FragmentIterationListenerLectura{
		public void onFragmentIterationLectura();
	}	

	public static TabOrdenTomarLectura newInstance(Bundle arguments){
		TabOrdenTomarLectura f = new TabOrdenTomarLectura();
		if(arguments != null){
			f.setArguments(arguments);
			bolsaDatosIniciales = arguments;
		}
		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, 
			ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.tab_orden_tomar_lectura, container, false);

		layoutPanelPrincipal = (LinearLayout) view.findViewById(R.id.LecturaPanelPrincipal);
		layoutLectura = (LinearLayout) view.findViewById(R.id.LecturaPanelLectura);
		tvLecturaTipoOrden = (TextView)view.findViewById(R.id.tvLecturaTipoOrden);
		tvLecturaDireccion = (TextView)view.findViewById(R.id.tvLecturaDireccionOrden);
		btConfirmarLectura = (Button)view.findViewById(R.id.btTomarLectura);
		etLectura = (EditText)view.findViewById(R.id.etLecturaMedidor);
		etObsLectura = (EditText)view.findViewById(R.id.etObservacionLectura);
		tvLecturaMedidor =(TextView)view.findViewById(R.id.tvTituloTomarLectura);
		foto = (ImageView)view.findViewById(R.id.imageViewFoto);
		spMotivoLectura = (Spinner)view.findViewById(R.id.spinnerMotivoNoLectura);
		spObsLectura = (Spinner)view.findViewById(R.id.spObervacionLectura);

		manager = new DataBaseManager(getActivity());

		manager.open();
		// Cargar motivos no lectura cod 60
		List<String> lables = manager.getAllLabels("60", 1);		
		List<String> lablesObs = manager.getAllLabels("61", 1);	
		manager.close();

		// Creating adapter for spinner
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, lables); 
		// Drop down layout style - list view with radio button
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
		// attaching data adapter to spinner
		spMotivoLectura.setAdapter(dataAdapter);

		// Creating adapter for spinner
		ArrayAdapter<String> dataAdapterObsLect = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, lablesObs); 
		// Drop down layout style - list view with radio button
		dataAdapterObsLect.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
		// attaching data adapter to spinner
		spObsLectura.setAdapter(dataAdapterObsLect);

		btConfirmarLectura.setOnClickListener(this);

		if(bolsaDatosIniciales != null){
			ose_codigo = bolsaDatosIniciales.getInt("ose_codigo");
			tvLecturaTipoOrden.setText(bolsaDatosIniciales.getString("producto"));
			tvLecturaDireccion.setText(bolsaDatosIniciales.getString("direccion"));
			tvLecturaMedidor.setText(bolsaDatosIniciales.getString("elemento"));
			//Tipo de Orden
		}
		
		//imprimirTirilla();

		return view;
	}

	public void imprimirTirilla(){

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
		
		int inicial = 100, factor = 23;
		String lectura = "", observacionLectura = "", fecha_lectura = "", cliente = "", direccion = "", medidor = "", franja = "", servicio = "";
		int codigoObsLectura = 0, codigoNoLectura = 0, indicadorLectura = 0, suscriptor = 0 ;
		String strMotivoNoLectura = "", strObsLectura = "";
		
		manager.open();
		
		Cursor cursor = null;// manager.consultaLecturaImprimir(String.valueOf(ose_codigo));
    	
    	if(cursor.moveToFirst()){
    		cliente = cursor.getString(cursor.getColumnIndex("cli_nombre"));
    		suscriptor = cursor.getInt(cursor.getColumnIndex("cli_contrato"));
    		direccion = cursor.getString(cursor.getColumnIndex("direccion1"));
    		medidor = cursor.getString(cursor.getColumnIndex("elemento"));
    		franja = cursor.getString(cursor.getColumnIndex("consumo"));
    		servicio = cursor.getString(cursor.getColumnIndex("producto"));
    		
    		lectura = cursor.getString(cursor.getColumnIndex("lectura"));
    		codigoNoLectura = cursor.getInt(cursor.getColumnIndex("codigo_observacion_no_lectura"));			    		
    		codigoObsLectura = cursor.getInt(cursor.getColumnIndex("codigo_observacion_lectura"));
    		observacionLectura = cursor.getString(cursor.getColumnIndex("observacion_no_lectura"));
    		fecha_lectura = cursor.getString(cursor.getColumnIndex("fecha_creacion"));
    		indicadorLectura = cursor.getInt(cursor.getColumnIndex("indicador_lectura"));
    				    		
		}
    	
    	cursor.close();	
    	
    	String cpclData = "! 0 200 200 380 1\r\n"
				+ "PCX 200 43 !<PEREIRA1.PCX\r\n"
				+ "TEXT 5 0 15 25 ENERGIA DE PEREIRA\r\n"
				+ "TEXT 0 0 15 47 S.A.E.S.P.\r\n"
				+ "TEXT 0 2 15 60 CONSTANCIA DE LECTURA  \r\n"
				+ "TEXT 0 0 15 82 Nit: 816002019-9\r\n";

		
		cpclData += "TEXT 0 2 20 " + inicial + " FECHA:" + fecha_lectura + "\r\n";//FECHA 
		inicial = inicial + factor;
		cpclData += strImprimirCampos("SUSCR:" + suscriptor + "  " + cliente, inicial);
		inicial = inicial + factor;
		cpclData += strImprimirCampos("DIR:" + direccion, inicial);
		
		
		inicial = inicial + factor;
		cpclData += strImprimirCampos("MEDIDOR:" + medidor + " FRANJA:" + franja, inicial);//Matricula Cliente
		
		inicial = inicial + factor;
		if (indicadorLectura == 1)
        {
			cpclData += strImprimirCampos("LECTURA: " + lectura + " -" + servicio, inicial);//TITULO LECTURA 
        }
        else 
        {
        	strMotivoNoLectura = manager.getObsCodigo(String.valueOf(codigoNoLectura));
        	cpclData += strImprimirCampos("NO LECTURA:" + strMotivoNoLectura, inicial);//CAUSAL DE NO LECTURA
        }
		
		cpclData += "\r\n";
		
		if(codigoObsLectura > 0){
			inicial = inicial + factor;
			strObsLectura = manager.getObsCodigo(String.valueOf(codigoObsLectura));
			cpclData += strImprimirCampos("OBS: " + strObsLectura, inicial);//OBSERVACION LECTURA 
			cpclData += "\r\n";
		}
		
		cpclData += "LEFT \r\n" 
				+ "FORM\r\n"
				+ "PRINT\r\n";
		manager.close();
		
		//System.out.println(cpclData);
		
		final String salidaImprimir = cpclData;

		sendCpclOverBluetooth(theBtMacAddress, salidaImprimir);
	}


	private void sendCpclOverBluetooth(final String theBtMacAddress, final String datosImprimir) {
		
		

		new Thread(new Runnable() {
			public void run() {
				try {

					
					// Instantiate insecure connection for given Bluetooth&reg; MAC Address.
					Connection thePrinterConn = new BluetoothConnectionInsecure(theBtMacAddress);

					// Initialize
					Looper.prepare();

					// Open the connection - physical connection is established here.
					thePrinterConn.open();

					thePrinterConn.write("! U1 JOURNAL\r\n! U1 SETFF 50 2\r\n".getBytes());

					// This example prints "This is a CPCL test." near the top of the label.

					// Send the data to printer as a byte array.
					thePrinterConn.write(datosImprimir.getBytes());

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
	
	public String strImprimirCampos(String campo1, int i_iterador)
    {
		String entrada = "", strAux = "";
        int iterador = i_iterador, tamano = 45;

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

	//El fragment se ha adjuntado al Activity
	@Override
	public void onAttach(android.app.Activity activity) {
		super.onAttach(activity);
		mCallback = (FragmentIterationListenerLectura) activity;
	}

	//El Activity que contiene el Fragment ha terminado su creaci�n
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	//El Fragment ha sido quitado de su Activity y ya no est� disponible
	@Override
	public void onDetach() {
		mCallback = null;
		super.onDetach();
	}

	//La vista ha sido creada y cualquier configuraci�n guardada est� cargada
	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.btTomarLectura:
			confirmarLecturaMotivo();
			break;
		}
	}

	/**
	 * Ingresa  la Lectura o el Motivo de no lectura a la BD
	 */
	private void confirmarLecturaMotivo(){
		String motivoNoLecturaStr = (String) spMotivoLectura.getSelectedItem();
		String lecturaStr = etLectura.getText().toString();
		String obsLectura = (String) spObsLectura.getSelectedItem();
		String obsLecturaAbierta = (String) etObsLectura.getText().toString();
		
		int motivoNoLectura = 0, codigoObsLectura = 0, indicadorLectura = 0;
		int encontroMedidor = 0, medidorCorrecto = 0, critica = 0, intentos = 0;
		String msjMostrado = "", lectura = "";

		manager.open();
		
		if(chkMotivo.isChecked()){
			motivoNoLectura = manager.getCodigoObs(motivoNoLecturaStr, "60");
			indicadorLectura = 0;

		}else if(!lecturaStr.isEmpty()){				
			lectura = lecturaStr;
			indicadorLectura = 1;
		}else msjMostrado = " Debe ingresar la lectura";
		
		if(!lecturaStr.isEmpty())
			codigoObsLectura =  manager.getCodigoObs(obsLectura, "61");
			

		if(msjMostrado == ""){
			
			obsLecturaAbierta = removerTildes(obsLecturaAbierta);
			
			//manager.ingresarActualizarLectura(String.valueOf(ose_codigo), lectura,
			//		codigoObsLectura, motivoNoLectura, indicadorLectura, critica, intentos, encontroMedidor, medidorCorrecto, obsLecturaAbierta, "", "",0,"0");
			mCallback.onFragmentIterationLectura();
			btConfirmarLectura.setTextColor(Color.BLUE);
			imprimirTirilla();
		}else Toast.makeText(getActivity(), msjMostrado, Toast.LENGTH_SHORT).show();

		manager.close();
	}
	
	/**
	 * Funci�n que elimina acentos y caracteres especiales de
	 * una cadena de texto.
	 * @param input
	 * @return cadena de texto limpia de acentos y caracteres especiales.
	 */
	public static String removerTildes(String input) {
		// Cadena de caracteres original a sustituir.
		String original = "áàäéèëíìïóòöúùuñÁÀÄÉÈËÍÌÏÓÒÖÚÙÜÑçÇ";
		// Cadena de caracteres ASCII que reemplazar�n los originales.
		String ascii = "aaaeeeiiiooouuunAAAEEEIIIOOOUUUNcC";
		String output = input;
		for (int i=0; i<original.length(); i++) {
			// Reemplazamos los caracteres especiales.
			output = output.replace(original.charAt(i), ascii.charAt(i));
		}//for i
		return output;
	}//remove1

}