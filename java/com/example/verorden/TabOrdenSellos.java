package com.example.verorden;

import com.example.gestiondeltec.R;
import java.util.ArrayList;
import java.util.List;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

import androidx.fragment.app.Fragment;

import com.example.Logueo.DataBaseManager;

/**
 * Esta clase Fragment para el ingreso y retiro de Sellos a la orden SCR
 * @author: Jasson Trujillo Ortiz
 * @version: 10/10/2016/A
 * @see <a href = "http://www.deltec.com.co" /> Deltec S.A. </a>
 */
public class TabOrdenSellos extends Fragment implements OnClickListener {

	public static final String TAG = "tabSellos";
	public static Bundle bolsaDatosIniciales;
	private DataBaseManager manager;
	private Cursor cursor;
	ListView listaSellosInstalados, listaSellosRetirados;
	EditText etSelloSerie;
	Button btnAgregarSello, btnRetirarSello;
	RadioButton rbSelloRojo, rbSelloTransparente;
	Spinner spPrefijo;
	List<ItemSello> itemsInstalados = new ArrayList<ItemSello>();
	List<ItemSello> itemsRetirados = new ArrayList<ItemSello>();
	int ose_codigo = 0,rec_codigo_actual=0,cuadrilla = 0;
	String rec_nombre_actual = "";

	public static TabOrdenSellos newInstance(Bundle arguments){
		TabOrdenSellos f = new TabOrdenSellos();
		if(arguments != null){
			f.setArguments(arguments);
			bolsaDatosIniciales = arguments;
		}
		return f;
	}


	@Override
	public View onCreateView(LayoutInflater inflater, 
			ViewGroup container, Bundle savedInstanceState) { 

		View view = inflater.inflate(R.layout.tab_orden_sellos, container, false);			
		btnAgregarSello = (Button)view.findViewById(R.id.btAgregarSello);
		btnRetirarSello = (Button)view.findViewById(R.id.btRetirarSello);
		listaSellosInstalados = (ListView) view.findViewById(R.id.lvInstalados);
		listaSellosRetirados = (ListView) view.findViewById(R.id.lvRetirados);
		spPrefijo = (Spinner) view.findViewById(R.id.spinnerSelloPrefijo);
		etSelloSerie = (EditText) view.findViewById(R.id.etSelloSerie);
		rbSelloRojo = (RadioButton)view.findViewById(R.id.rbActividadEfectiva);
		rbSelloTransparente = (RadioButton)view.findViewById(R.id.rbActividadNoEfectiva);

		if(bolsaDatosIniciales != null){
			ose_codigo = bolsaDatosIniciales.getInt("ose_codigo");
			cuadrilla = bolsaDatosIniciales.getInt("cuadrilla");
		}

		manager = new DataBaseManager(getActivity());
		
		if(rbSelloRojo.isChecked()){
			rec_codigo_actual = 1474646;//SELLO ROJO
			rec_nombre_actual = "SELLO ROJO";
		}else{
			rec_codigo_actual = 1475442;//SELLO TRANSPARENTE
			rec_nombre_actual = "TRANSPARENTE";
		}		

		cargarSpinner(spPrefijo);		
		cargarListViewSellos();		
		
		btnAgregarSello.setOnClickListener(this);
		btnRetirarSello.setOnClickListener(this);

		listaSellosInstalados.setAdapter(new ItemSelloAdapter(getActivity(), itemsInstalados));
		// Register a callback to be invoked when an item in this AdapterView has been clicked
		listaSellosInstalados.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {

				ItemSello item = (ItemSello) listaSellosInstalados.getAdapter().getItem(position);	        
				String itSerie = item.getSerie();

				Toast.makeText(getActivity(), "Eliminar el Sello " + itSerie, Toast.LENGTH_SHORT).show();

				/*AlertDialog.Builder invalid_input_dialog = new AlertDialog.Builder(getActivity());
		                  invalid_input_dialog.setTitle("Eliminar el Sello " + itSerie)
		                  .setMessage("Desea Eliminar el sello")
		                  .setCancelable(true)
		                  .setPositiveButton("Ok", new DialogInterface.OnClickListener(){
		                      @Override
		                        public void onClick(DialogInterface dialog, int which) {

		                        System.out.println("The result is true");
		                      }
		                  })
		                  .setNegativeButton("No", new DialogInterface.OnClickListener(){

		                    @Override
		                    public void onClick(DialogInterface dialog, int which) {
		                        // TODO Auto-generated method stub

		                        System.out.println("The result is false");                        
		                    }

		                  })
		                  .show();*/
			}
		});
		
		listaSellosRetirados.setAdapter(new ItemSelloAdapter(getActivity(), itemsRetirados));
		// Register a callback to be invoked when an item in this AdapterView has been clicked
		listaSellosRetirados.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {

				ItemSello item = (ItemSello) listaSellosRetirados.getAdapter().getItem(position);	        
				String itSerie = item.getSerie();

				Toast.makeText(getActivity(), "Eliminar el Sello " + itSerie, Toast.LENGTH_SHORT).show();
			}
		});
		
		rbSelloRojo.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if(rbSelloRojo.isChecked()){
					rec_codigo_actual = 1474646;//SELLO ROJO
					rec_nombre_actual = "SELLO ROJO";
				}else{
					rec_codigo_actual = 1475442;//SELLO TRANSPARENTE
					rec_nombre_actual = "TRANSPARENTE";
				}
				cargarSpinner(spPrefijo);
			}       
		});	

		return view;
	}
	
	/**
	 * Metodo que carga los sellos Instalados y Retirados de la orden
	 */
	private void cargarListViewSellos(){
		int rec_codigo,tipo;
		String rec_nombre,rec_serie,rec_prefijo;
		
		itemsInstalados.clear();
		
		manager.open();
		cursor = manager.cargarMaterialOrden(String.valueOf(ose_codigo),"1");	
		if(cursor.moveToFirst()){
			do{
				rec_codigo = cursor.getInt(cursor.getColumnIndex("rec_codigo"));	
				rec_nombre = cursor.getString(cursor.getColumnIndex("rec_nombre"));
				tipo = 1;
				rec_serie = cursor.getString(cursor.getColumnIndex("rec_serie"));
				rec_prefijo = cursor.getString(cursor.getColumnIndex("rec_prefijo"));

				itemsInstalados.add(new ItemSello(rec_codigo, ose_codigo, rec_prefijo, rec_serie, rec_nombre, tipo));	

			}while(cursor.moveToNext());//accessing data upto last row from table
		}
		
		itemsRetirados.clear();
		cursor = manager.cargarMaterialOrden(String.valueOf(ose_codigo),"0");				
		if(cursor.moveToFirst()){
			do{
				rec_codigo = cursor.getInt(cursor.getColumnIndex("rec_codigo"));	
				rec_nombre = cursor.getString(cursor.getColumnIndex("rec_nombre"));
				tipo = 0;
				rec_serie = cursor.getString(cursor.getColumnIndex("rec_serie"));
				rec_prefijo = cursor.getString(cursor.getColumnIndex("rec_prefijo"));

				itemsRetirados.add(new ItemSello(rec_codigo, ose_codigo, rec_prefijo, rec_serie, rec_nombre, tipo));	

			}while(cursor.moveToNext());//accessing data upto last row from table
		}
		manager.close();		
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.btAgregarSello:
			agregarSello();	
			
			break;
		case R.id.btRetirarSello: 
			retirarSello();
			break;
		}
	}
	
	/**
	 * Registra un Sello en el sotck de la cuadrilla
	 */
	private void agregarSello(){
		
		String serieSello = etSelloSerie.getText().toString();
		String prefijoSello = (String) spPrefijo.getSelectedItem();
		if(!serieSello.isEmpty()){
			manager.open();
			if(manager.exiteMaterialDisponible(serieSello, prefijoSello)){
				if(rbSelloRojo.isChecked()){
					rec_codigo_actual = 1474646;//SELLO ROJO
					rec_nombre_actual = "SELLO ROJO";
				}else {
					rec_codigo_actual = 1475442;//SELLO TRANSPARENTE
					rec_nombre_actual = "TRANSPARENTE SELLO";
				}
				
				manager.instalarMaterial(ose_codigo, rec_codigo_actual, serieSello, 1, rec_nombre_actual, cuadrilla, prefijoSello, 0);
				itemsInstalados.add(new ItemSello(rec_codigo_actual, ose_codigo, prefijoSello, serieSello, rec_nombre_actual, 1));	
				listaSellosInstalados.invalidateViews();
				etSelloSerie.setText("");
			}else 
				Toast.makeText(getActivity(), "El sello " + prefijoSello + "_" + serieSello + " no se encuentra disponible en su stock", Toast.LENGTH_SHORT).show();
			
			manager.close();
		}else
			Toast.makeText(getActivity(), "Debe ingresar la serie del sello", Toast.LENGTH_SHORT).show();		
	}
	
	/**
	 * Registra Retiros de Sellos
	 */
	private void retirarSello(){
		String serieSello = etSelloSerie.getText().toString();
		String prefijoSello = (String) spPrefijo.getSelectedItem();
		if(!serieSello.isEmpty()){

			if(rbSelloRojo.isChecked()){
				rec_codigo_actual = 1474646;//SELLO ROJO
				rec_nombre_actual = "SELLO ROJO";
			}else {
				rec_codigo_actual = 1475442;//SELLO TRANSPARENTE
				rec_nombre_actual = "TRANSPARENTE SELLO";
			}
			
			manager.open();
			manager.instalarRetirarMaterial(ose_codigo, rec_codigo_actual, serieSello, 1, rec_nombre_actual, cuadrilla, prefijoSello, 0);
			manager.close();
			
			itemsRetirados.add(new ItemSello(rec_codigo_actual, ose_codigo, prefijoSello, serieSello, rec_nombre_actual, 0));	
			listaSellosRetirados.invalidateViews();
			etSelloSerie.setText("");
		}else
			Toast.makeText(getActivity(), "Debe ingresar la serie del sello", Toast.LENGTH_SHORT).show();	
	}

	/**
     * Asigna datos Obs TIPO de la Bd a un Spinner PREFIJOS DE LOS SELLOS
     * @param sp
     */
	private void cargarSpinner(Spinner sp){
		
		manager.open();
		// Cargar prefijos de los sellos del codigo actual
		List<String> lables = manager.getAllPrefijosSellos(rec_codigo_actual);	
		manager.close();
		// Creating adapter for spinner
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, lables); 
		// Drop down layout style - list view with radio button
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
		// attaching data adapter to spinner
		sp.setAdapter(dataAdapter);
	}

}
