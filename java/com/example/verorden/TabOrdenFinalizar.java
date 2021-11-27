package com.example.verorden;

import com.example.gestiondeltec.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;

import androidx.fragment.app.Fragment;

/**
 * Esta clase Fragment que finaliza la orden SCR
 * @author: Jasson Trujillo Ortiz
 * @version: 10/11/2016/A
 * @see <a href = "http://www.deltec.com.co" /> Deltec S.A. </a>
 */
public class TabOrdenFinalizar extends Fragment implements OnClickListener {

	public static final String TAG = "tabFinalizar";
	EditText etActa, etObservacion;
	LinearLayout PanelreconexionNoAutorizada;
	CheckBox checkBoxReconexionNoAutorizada;
	Button btFinalizarOrden;
	RadioButton rbReconexionPorMedidor, rbReconexionFueraMedidor;
	int ose_codigo = 0, reconexionNoAutorizada = 0;

	private FragmentIterationListenerFinalizar mCallback = null;

	/**
	 * Interfaz para interacturar con la orden
	 * @author DELTEC
	 *
	 */
	public interface FragmentIterationListenerFinalizar{
		public void onFragmentIterationFinalizar(Bundle parameters);
	}	

	public static TabOrdenFinalizar newInstance(Bundle arguments){
		TabOrdenFinalizar f = new TabOrdenFinalizar();
		if(arguments != null){
			f.setArguments(arguments);
		}
		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {		
		View view = inflater.inflate(R.layout.tab_orden_finalizar, container, false);

		etActa = (EditText) view.findViewById(R.id.etActaOrden);
		etObservacion  = (EditText) view.findViewById(R.id.etObservaciones);
		PanelreconexionNoAutorizada = (LinearLayout) view.findViewById(R.id.PanelReconexionNoAutorizada);
		checkBoxReconexionNoAutorizada = (CheckBox) view.findViewById(R.id.checkBoxReconexionNoAutorizada);
		btFinalizarOrden = (Button) view.findViewById(R.id.btFinalizarOrden); 
		rbReconexionFueraMedidor = (RadioButton) view.findViewById(R.id.rbReconexionPorFueraMedidor); 
		rbReconexionPorMedidor = (RadioButton) view.findViewById(R.id.rbReconexionPorMedidor); 

		btFinalizarOrden.setOnClickListener(this);

		checkBoxReconexionNoAutorizada.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if(checkBoxReconexionNoAutorizada.isChecked()){
					PanelreconexionNoAutorizada.setVisibility(View.VISIBLE);
				}else{
					PanelreconexionNoAutorizada.setVisibility(View.GONE);
				}
			}       
		});	

		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

	}

	//El fragment se ha adjuntado al Activity
	@Override
	public void onAttach(android.app.Activity activity) {
		super.onAttach(activity);
		mCallback = (FragmentIterationListenerFinalizar) activity;
	}

	//El Activity que contiene el Fragment ha terminado su creacion
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true); //Indicamos que este Fragment tiene su propio menu de opciones
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
		case R.id.btFinalizarOrden:
			finalizarOrden();
			break;
		}

	}

	public void finalizarOrden(){
		String actaStr = etActa.getText().toString();
		String observacionStr = etObservacion.getText().toString();

		if(checkBoxReconexionNoAutorizada.isChecked()){
			if(rbReconexionPorMedidor.isChecked()){
				reconexionNoAutorizada = 1;
			}else reconexionNoAutorizada = 2;
		}else reconexionNoAutorizada = 0;

		Bundle bundle = new Bundle();
		bundle.putString("acta", actaStr);
		bundle.putString("observacion",observacionStr);
		bundle.putInt("reconexion", reconexionNoAutorizada);
		mCallback.onFragmentIterationFinalizar(bundle);
	}
}
