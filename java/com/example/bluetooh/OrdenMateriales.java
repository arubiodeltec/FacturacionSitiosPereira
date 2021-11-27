package com.example.bluetooh;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

import com.example.Logueo.DataBaseManager;
import com.example.gestiondeltec.R;

/**
 * Esta clase Fragment para el ingreso y retiro de Sellos a la orden SCR
 * @author: Jasson Trujillo Ortiz
 * @version: 10/10/2016/A
 * @see <a href = "http://www.deltec.com.co" /> Deltec S.A. </a>
 */
public class OrdenMateriales extends Activity implements OnClickListener {

	public static final String TAG = "tabSellos";
	public static Bundle bolsaDatosIniciales;
	private DataBaseManager manager;
	private Cursor cursor;
	ListView lvMaterial, lvMaterialOrden;
	EditText etBuscarMaterial;
	Button btBuscarMaterial;
	Spinner spMovimientoMaterial;
	List<ItemMaterial> itemsMaterial = new ArrayList<ItemMaterial>();
	List<ItemMaterial> itemsMaterialOrden = new ArrayList<ItemMaterial>();
	int ose_codigo = 0,rec_codigo_actual=0,cuadrilla = 0;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_materiales);

		lvMaterial = (ListView) findViewById(R.id.lvMateriales);	
		lvMaterialOrden = (ListView) findViewById(R.id.lvMaterialesOrden);	
		spMovimientoMaterial = (Spinner) findViewById(R.id.spMovimientoMaterial);
		btBuscarMaterial = (Button) findViewById(R.id.buttonBuscarMaterial);
		etBuscarMaterial = (EditText) findViewById(R.id.etBuscarMaterial);

		Bundle bolsaDatosIniciales = getIntent().getExtras();
		cuadrilla = bolsaDatosIniciales.getInt("cuadrilla");

		/// SPINER POR XML //////
		ArrayAdapter <CharSequence>  adapterMovimientoMaterial = ArrayAdapter.createFromResource(this, R.array.movimiento_material, android.R.layout.simple_spinner_item);
		adapterMovimientoMaterial.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spMovimientoMaterial.setAdapter(adapterMovimientoMaterial);

		manager = new DataBaseManager(this);
		btBuscarMaterial.setOnClickListener(this);

		spMovimientoMaterial.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub				
				cargarListViewMateriales(etBuscarMaterial.getText().toString(),spMovimientoMaterial.getSelectedItemPosition());
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});
		
		lvMaterialOrden.setAdapter(new ItemMaterialAdapter(this, itemsMaterialOrden));		
		lvMaterial.setAdapter(new ItemMaterialAdapter(this, itemsMaterial));

		cargarListViewMateriales(etBuscarMaterial.getText().toString(),spMovimientoMaterial.getSelectedItemPosition());
		cargarListViewMaterialesAgrupados();
	}


	/**
	 * Metodo que carga los sellos Instalados y Retirados de la orden
	 */
	private void cargarListViewMateriales(String inActDescripcion, int tipoMovimiento){

		int rec_codigo = 0, rec_seriado = 1, tipo = 0, nodo = 0;
		double rec_cantidad = 0; 
		String rec_prefijo = "", rec_serie = "", rec_nombre = "", rec_unidad = "Und";
		int tipoBusqueda = tipoMovimiento;

		itemsMaterial.clear();

		manager.open();
		cursor = manager.cargarMateriales(inActDescripcion,ose_codigo, tipoBusqueda);	
		if(cursor.moveToFirst()){
			do{
				switch(tipoBusqueda){
				case 0://MATERIAL INSTALADO
					rec_codigo = cursor.getInt(cursor.getColumnIndex("rec_codigo"));	
					rec_nombre = cursor.getString(cursor.getColumnIndex("rec_nombre"));			
					rec_seriado = cursor.getInt(cursor.getColumnIndex("rec_seriado"));	
					rec_serie =  cursor.getString(cursor.getColumnIndex("rec_serie"));	
					rec_cantidad = cursor.getInt(cursor.getColumnIndex("rec_cantidad"));
					rec_prefijo = cursor.getString(cursor.getColumnIndex("rec_prefijo"));
					tipo = cursor.getInt(cursor.getColumnIndex("tipo"));
					nodo = cursor.getInt(cursor.getColumnIndex("nodo"));
					break;
				case 1://MATERIAL STOCK
					rec_codigo = cursor.getInt(cursor.getColumnIndex("rec_codigo"));	
					rec_nombre = cursor.getString(cursor.getColumnIndex("rec_nombre"));			
					rec_seriado = cursor.getInt(cursor.getColumnIndex("rec_seriado"));	
					rec_serie =  cursor.getString(cursor.getColumnIndex("rec_serie"));	
					rec_cantidad = cursor.getInt(cursor.getColumnIndex("rec_cantidad"));	
					rec_unidad = cursor.getString(cursor.getColumnIndex("rec_unidad"));
					rec_prefijo = cursor.getString(cursor.getColumnIndex("rec_prefijo"));
					tipo = 0;
					break;
				default:
					break;
				}

				itemsMaterial.add(new ItemMaterial(rec_codigo, ose_codigo, rec_cantidad, rec_prefijo, rec_serie, rec_nombre, rec_unidad, rec_seriado,nodo, tipo));	
			}while(cursor.moveToNext());//accessing data upto last row from table
		}

		cursor.close();
		manager.close();	
		lvMaterial.invalidateViews();
		System.gc();
	}

	private void cargarListViewMaterialesAgrupados(){

		int rec_codigo, rec_seriado, tipo = 0, nodo = 0;
		double rec_cantidad = 0; 
		String rec_prefijo = "", rec_serie = "", rec_nombre = "", rec_unidad = "Und";

		itemsMaterialOrden.clear();

		manager.open();
		cursor = manager.cargarMaterialAgrupado(String.valueOf(cuadrilla));	
		if(cursor.moveToFirst()){
			do{
				rec_codigo = cursor.getInt(cursor.getColumnIndex("rec_codigo"));	
				rec_nombre = cursor.getString(cursor.getColumnIndex("rec_nombre"));			
				rec_seriado = cursor.getInt(cursor.getColumnIndex("rec_seriado"));
				rec_cantidad = cursor.getDouble(cursor.getColumnIndex("rec_cantidad"));
				rec_serie = cursor.getString(cursor.getColumnIndex("rec_serie"));				
				rec_prefijo = cursor.getString(cursor.getColumnIndex("rec_prefijo"));
				nodo = cursor.getInt(cursor.getColumnIndex("nodo"));

				itemsMaterialOrden.add(new ItemMaterial(rec_codigo, ose_codigo, rec_cantidad, rec_prefijo, rec_serie, rec_nombre, rec_unidad, rec_seriado,nodo, tipo));	
			}while(cursor.moveToNext());//accessing data upto last row from table
		}

		cursor.close();
		manager.close();	
		lvMaterialOrden.invalidateViews();
		System.gc();
	}



	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.buttonBuscarMaterial:
			cargarListViewMateriales(etBuscarMaterial.getText().toString(),spMovimientoMaterial.getSelectedItemPosition());
			//agregarSello();	
			//retirarSello();
			break;
		}
	}


}
