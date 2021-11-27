package com.example.verorden;

import com.example.gestiondeltec.R;
import java.util.List;
import com.example.Logueo.DataBaseManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

import androidx.fragment.app.Fragment;

/**
 * Esta clase Fragment para el registro de los datos de la SCR Actividad Orden
 * @author: Jasson Trujillo Ortiz
 * @version: 10/10/2016/A
 * @see <a href = "http://www.deltec.com.co" /> Deltec S.A. </a>
 */
public class TabOrdenDatos extends Fragment implements OnClickListener  {
		
	TextView tvTipoOrden, tvDireccionOrden, tvMedidorOrden;
	EditText etFechaAnomalia, etFechaPago;
	Spinner spActividad, spMotivoActividad,spAcometidaEstado,spAcometidaColor,spAcometidaTipo;
	LinearLayout layoutDatosRetiroAcometida;
	CheckBox chkRetiroAcometida; 
	RadioButton Ejecutada, NoEjecutada;
	String tipoOrden="70";
	public static Bundle bolsaDatosIniciales;
	private DataBaseManager manager;
	Button btConfirmarActividad;
	int ose_codigo;
	public int year = 0, month= 0, day = 0;
	
	public static final String TAG = "TabOrdenDatos";
	private FragmentIterationListener mCallback = null;
	
	/**
	 * Interface para interacturar con el activity VerFormato
	 * @author DELTEC
	 *
	 */
	public interface FragmentIterationListener{
        public void onFragmentIteration(Bundle parameters);
    }	
	
	public static TabOrdenDatos newInstance(Bundle arguments){
		TabOrdenDatos f = new TabOrdenDatos();
        if(arguments != null){
            f.setArguments(arguments);
            bolsaDatosIniciales = arguments;
        }
        return f;
    }
		
	//El Fragment va a cargar su layout, el cual debemos especificar
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
				
		View view = inflater.inflate(R.layout.tab_orden_datos, container, false);
		
		tvTipoOrden = (TextView) view.findViewById(R.id.tvTipoOrden);
		tvDireccionOrden = (TextView) view.findViewById(R.id.tvDatosDireccion);
		tvMedidorOrden = (TextView) view.findViewById(R.id.tvDatosMedidorOrden);
		
		layoutDatosRetiroAcometida = (LinearLayout) view.findViewById(R.id.datosRetiroAcometida);
		chkRetiroAcometida = (CheckBox) view.findViewById(R.id.chkRetiroAcometida);
		Ejecutada = (RadioButton)view.findViewById(R.id.rbActividadEfectiva);
		NoEjecutada = (RadioButton)view.findViewById(R.id.rbActividadNoEfectiva);
		spActividad = (Spinner) view.findViewById(R.id.spinnerActividadEjecutada);
		spMotivoActividad = (Spinner) view.findViewById(R.id.spinnerMotivoActividad);
		spAcometidaEstado = (Spinner) view.findViewById(R.id.spinnerAcometidaEstado);
		spAcometidaColor = (Spinner) view.findViewById(R.id.spinnerAcometidaColor);
		spAcometidaTipo = (Spinner) view.findViewById(R.id.spinnerAcometidaTipo);
		btConfirmarActividad = (Button)view.findViewById(R.id.btConfirmarActividad);
		
		/// SPINER POR XML //////
		ArrayAdapter <CharSequence>  adapterAcometidaEstado = ArrayAdapter.createFromResource(getActivity(), R.array.estado_acometida, android.R.layout.simple_spinner_item);
		adapterAcometidaEstado.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spAcometidaEstado.setAdapter(adapterAcometidaEstado);
		
		ArrayAdapter <CharSequence>  adapterAcometidaColor = ArrayAdapter.createFromResource(getActivity(), R.array.color_acometida, android.R.layout.simple_spinner_item);
		adapterAcometidaColor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spAcometidaColor.setAdapter(adapterAcometidaColor);
		
		ArrayAdapter <CharSequence>  adapterAcometidaTipo = ArrayAdapter.createFromResource(getActivity(), R.array.tipo_acometida, android.R.layout.simple_spinner_item);
		adapterAcometidaTipo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spAcometidaTipo.setAdapter(adapterAcometidaTipo);
		
		manager = new DataBaseManager(getActivity());
		
		if(bolsaDatosIniciales != null){
			ose_codigo = bolsaDatosIniciales.getInt("ose_codigo");
			tvTipoOrden.setText(bolsaDatosIniciales.getString("producto"));
			tvDireccionOrden.setText(bolsaDatosIniciales.getString("direccion"));
			tvMedidorOrden.setText(bolsaDatosIniciales.getString("elemento"));
			//Tipo de Orden
			tipoOrden = getTipoOrden(bolsaDatosIniciales.getString("producto"));
		}
		
		Ejecutada.setOnClickListener(this);
		NoEjecutada.setOnClickListener(this);
		
		btConfirmarActividad.setOnClickListener(this);
		
		spActividad.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				String actividadStr = (String) spActividad.getSelectedItem();				
				if(actividadStr.contains("TRASLADAR A PESADA")){
					cargarSpinner("78",spMotivoActividad);
				}else cargarSpinner("76",spMotivoActividad);
				
				//enviarDatosActivity();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});
		
		spMotivoActividad.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				//enviarDatosActivity();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});
	
		cargarSpinner("76",spMotivoActividad);

		chkRetiroAcometida.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if(chkRetiroAcometida.isChecked()){
					layoutDatosRetiroAcometida.setVisibility(View.VISIBLE);
				}else{
					layoutDatosRetiroAcometida.setVisibility(View.GONE);
				}
				//enviarDatosActivity();
			}       
		});	
		
		Ejecutada.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if(Ejecutada.isChecked()){
					cargarSpinner(tipoOrden,spActividad);
				}else{
					cargarSpinner("60",spActividad);
				}
			}       
		});	
		
		if(Ejecutada.isChecked()){
			cargarSpinner(tipoOrden,spActividad);
		}else{
			cargarSpinner("60",spActividad);
		}
		
		cargarActividad();
			      
		return view;
	}
    
	//El fragment se ha adjuntado al Activity
    @Override
    public void onAttach(android.app.Activity activity) {
        super.onAttach(activity);
        mCallback = (FragmentIterationListener) activity;
    }
    
    //El Activity que contiene el Fragment ha terminado su creacion
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //setHasOptionsMenu(true); //Indicamos que este Fragment tiene su propio menu de opciones
    }
    
    //El Fragment ha sido quitado de su Activity y ya no esta disponible
    @Override
    public void onDetach() {
        mCallback = null;
        super.onDetach();
    }
    
    //La vista ha sido creada y cualquier configuracion guardada esta cargada
    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
    	// TODO Auto-generated method stub
    	switch(v.getId()){
    	case R.id.rbActividadEfectiva:
    		if(Ejecutada.isChecked()){
    			cargarSpinner(tipoOrden,spActividad);
    		}			
    		break;
    	case R.id.rbActividadNoEfectiva:
    		if(NoEjecutada.isChecked()){
    			cargarSpinner("60",spActividad);
    		}		
    		break;
    	case R.id.btConfirmarActividad:
    		String actividadStr = (String) spActividad.getSelectedItem();
			String motivoActividadStr = (String) spMotivoActividad.getSelectedItem();
			
			AlertDialog.Builder invalid_input_dialog = new AlertDialog.Builder(getActivity());
			invalid_input_dialog.setTitle(actividadStr)
			.setMessage("CONFIRMAR " + actividadStr + " MOTIVO:" + motivoActividadStr)
			.setCancelable(true)
			.setPositiveButton("CONFIRMAR", new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					confirmarActividad();
				}
			})
			.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
				}
			})
			.show();
    		break;
    	}
    }
    
    private void cargarActividad(){
    	
    	manager.open();
    	Cursor cursor = manager.consultaActividadOrden(String.valueOf(ose_codigo));
    	
    	if(cursor.moveToFirst()){
    		String causa = "", actividad = "", motivo = "";
    		
    		if(!cursor.isNull(1)){//Actividad no Efectiva si es cero Null por el leftJoin
    			causa = cursor.getString(1);
    			if(!NoEjecutada.isChecked()){
    				NoEjecutada.setChecked(true);
    				cargarSpinner("60",spActividad);
    			}
    			setSpinText(spActividad,causa);
    			btConfirmarActividad.setTextColor(Color.BLUE);
    		}else if(!cursor.isNull(0)){//Actividad Efectiva
    			actividad = cursor.getString(0);
    			if(!Ejecutada.isChecked()){
    				Ejecutada.setChecked(true);   
    				cargarSpinner(tipoOrden,spActividad);
    			}
    			setSpinText(spActividad,actividad);
    			btConfirmarActividad.setTextColor(Color.BLUE);
    		}
    		
    		if(!cursor.isNull(2)){//MOTIVO ACTIVIDAD
    			motivo = cursor.getString(2);
    			if(motivo != "")
    				setSpinText(spMotivoActividad,motivo);
    		}
		}
    	
    	cursor.close();
    	manager.close();
    }
    
    public void setSpinText(Spinner spin, String text)
    {
        for(int i= 0; i < spin.getAdapter().getCount(); i++)
        {
            if(spin.getAdapter().getItem(i).toString().contains(text))
            {
                spin.setSelection(i);
            }
        }

    }
    
    private void confirmarActividad(){
    	
    	String actividadStr = (String) spActividad.getSelectedItem();
		String motivoActividadStr = (String) spMotivoActividad.getSelectedItem();
		int calificacion = 0,retiroAcometida = 0;
		int actividad, codigo_no_lectura = 0, codigo_lectura = 0;;
		if(Ejecutada.isChecked())
			calificacion = 1;
		if(chkRetiroAcometida.isChecked())
			retiroAcometida = 1;

		String msjMostrado = "";
		
		
		manager.open();
		
		if(calificacion == 0){
			actividad = manager.getCodigoObs(actividadStr, "60");
			codigo_no_lectura = actividad;
		}else actividad = manager.getCodigoObs(actividadStr, tipoOrden);

		if(actividadStr.contains("TRASLADAR A PESADA")){
			codigo_lectura = manager.getCodigoObs(motivoActividadStr, "78");
		}else codigo_lectura = manager.getCodigoObs(motivoActividadStr, "76");
		
		if(chkRetiroAcometida.isChecked())
			retiroAcometida = 1;

		if(msjMostrado == ""){

			manager.ingresarActualizarActividad(String.valueOf(ose_codigo), actividad, codigo_no_lectura,retiroAcometida,"", motivoActividadStr,codigo_lectura);
			btConfirmarActividad.setTextColor(Color.BLUE);
		}else Toast.makeText(getActivity(), msjMostrado, Toast.LENGTH_SHORT).show();
		
		manager.close();
	}
    
    /**
     * Asigna datos Obs TIPO de la Bd a un Spinner
     * @param cti_codigo
     * @param sp
     */
    private void cargarSpinner(String cti_codigo,Spinner sp){
    	
    	manager.open();
    	// Cargar motivos no lectura cod 60
    	List<String> lables = manager.getAllLabels(cti_codigo, 1);	
    	manager.close();    	
    	// Creating adapter for spinner
    	ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, lables); 
    	// Drop down layout style - list view with radio button
    	dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
    	// attaching data adapter to spinner
    	sp.setAdapter(dataAdapter);
    }
	
	/**
	 * Retorna el cti_codigo de la orden para consultas
	 * @param descripcion
	 * @return Return tipo de orden SCR
	 */
	private String getTipoOrden(String descripcion){
		String ctiCodigoOrden = "";
		if(descripcion.contains("REVISION")){
			ctiCodigoOrden = "73"; 
		}else if(descripcion.contains("RECONEXION")){
			ctiCodigoOrden = "72"; 
		}else{ //Suspension
			ctiCodigoOrden = "70"; 
		}
		
		return ctiCodigoOrden;
	}
	
	public void enviarDatosActivity(){
		Bundle bundle = new Bundle();
		String actividadStr = (String) spActividad.getSelectedItem();
		String motivoActividadStr = (String) spMotivoActividad.getSelectedItem();
		int calificacion = 0,retiroAcometida = 0;
		if(Ejecutada.isChecked())
			calificacion = 1;
		if(chkRetiroAcometida.isChecked())
			retiroAcometida = 1;
		
		bundle.putString("actividad", actividadStr);
		bundle.putString("motivoActividad", motivoActividadStr);
		bundle.putInt("calificacion", calificacion);
		bundle.putInt("retiroAcometida", retiroAcometida);
		bundle.putString("tipoOrden", tipoOrden);
		mCallback.onFragmentIteration(bundle);
	}
}
