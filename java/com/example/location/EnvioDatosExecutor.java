package com.example.location;

import java.io.IOException;

import org.apache.http.HttpClientConnection;
import org.apache.http.HttpResponse;
import org.apache.http.impl.DefaultBHttpClientConnection;

import android.content.Context;
import android.database.Cursor;

import com.example.Logueo.DataBaseManager;
import com.example.Logueo.DataBasePostgresManager;
import com.example.gestionOrdenes.EjecucionOrdenesModel;
import com.example.gestionOrdenes.EjecucionOrdenesModelServer;


public class EnvioDatosExecutor implements Runnable {
	private Context mContext;
	int trabajoRealizar, cuadrilla;
	String msjMostrado = "";
	int tipoUsuario = 1;
	public EnvioDatosExecutor(Context mContext, int inTrabajoRealizar, int codigoObtenido, int tipoUsuario){
		this.mContext = mContext;
		this.trabajoRealizar = inTrabajoRealizar;
		this.cuadrilla = codigoObtenido;
		this.tipoUsuario = tipoUsuario;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		switch(trabajoRealizar){
		/*case 1:
			//enviarOrdenesPendientes(); 
			msjMostrado = "Ordenes pendientes....";				 
			break;
		case 2:
			//enviarFotosPendientes();
			msjMostrado = "Fotos pendientes....";	
			break;*/
		case 1:
			enviarGpsPendiente();
			msjMostrado = "Gps Pendiente....";	
			break;
		case 2:
			/*if(cuadrilla > 0)
				enviarOrdenesAnuladas();*/
			msjMostrado = "Cargando Ordenes Anuldas....";	
			break;
		}
		System.out.println( "ENVIO ORDENES: " + msjMostrado );
	}

	private void enviarOrdenesPendientes(){
		EjecucionOrdenesModel manager;
		Cursor cursor, cursorGps;
		int ose_codigo; String lectura; int codigo_observacion_no_lectura;
		int codigo_observacion_lectura;String observacion_no_lectura; int indicador_lectura; int critica;
		String fecha;int intentos;int encontro_medidor;int medidor_correcto;String serie_medidor_encontrado;
		int actividad;String sellos_instalados; String motivo_ejecucion; int retiro_acometida; int cli_contrato;
		String datos_retiro_acometida;int reconexion_no_autorizada;String censo_carga, latitud, longitud;
		boolean actualizoOrden = false;

		manager = new EjecucionOrdenesModel(mContext);

		manager.open();
		cursor = manager.ordenesPendintes(1);

		if(cursor.moveToFirst()){
			DataBasePostgresManager managerServerEnvio = new DataBasePostgresManager(tipoUsuario);
			do{
				ose_codigo = cursor.getInt(cursor.getColumnIndex("ose_codigo"));				
				lectura = cursor.getString(cursor.getColumnIndex("lectura"));
				codigo_observacion_no_lectura = cursor.getInt(cursor.getColumnIndex("codigo_observacion_no_lectura"));
				codigo_observacion_lectura = cursor.getInt(cursor.getColumnIndex("codigo_observacion_lectura"));
				observacion_no_lectura = cursor.getString(cursor.getColumnIndex("observacion_no_lectura"));
				indicador_lectura = cursor.getInt(cursor.getColumnIndex("indicador_lectura"));
				critica = cursor.getInt(cursor.getColumnIndex("critica"));				
				fecha = cursor.getString(cursor.getColumnIndex("fecha_creacion"));
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
				latitud = "";
				longitud = "";
				
				/*cursorGps = manager.getGpsOrden(String.valueOf(ose_codigo));
				if(cursorGps.moveToFirst()){
					latitud = cursorGps.getString(cursorGps.getColumnIndex("latitud"));
					longitud = cursorGps.getString(cursorGps.getColumnIndex("longitud"));																
				}*/

				System.out.println( "ORDEN POSTGRES ose_codigo " + ose_codigo );

				actualizoOrden = managerServerEnvio.actualizarOrdenServicio(ose_codigo, lectura, codigo_observacion_no_lectura, 
						codigo_observacion_lectura, observacion_no_lectura, indicador_lectura, critica, fecha, 
						intentos, encontro_medidor, medidor_correcto, serie_medidor_encontrado, actividad, 
						sellos_instalados, motivo_ejecucion, retiro_acometida, datos_retiro_acometida, 
						reconexion_no_autorizada, censo_carga,latitud,longitud );

				if(actualizoOrden){
					
					if(actividad == 100){// TRASLADAR A PESADA
						managerServerEnvio.insertarClientePesada(cli_contrato, codigo_observacion_lectura);
					}
					
					ejecutarMovimientosMaterial(ose_codigo);
					//manager.actualizarEnvioOrden(String.valueOf(ose_codigo));
					System.out.println( "ACTUALIZO ENVIO ose_codigo " + ose_codigo );
				}

			}while(cursor.moveToNext());//accessing data upto last row from table
		}
		manager.close();
	}

	private void ejecutarMovimientosMaterial(int ose_codigo){

	}

	private void enviarFotosPendientes(){
		EjecucionOrdenesModel manager;
		Cursor cursor;
		int ose_codigo; 
		String fecha; String foto_url;
		boolean actualizoOrden = false;

		manager = new EjecucionOrdenesModel(mContext);
		manager.open();
		cursor = manager.fotosPendintes();		
		if(cursor.moveToFirst()){
			EjecucionOrdenesModelServer managerServerEnvio = new EjecucionOrdenesModelServer(tipoUsuario);
			do{
				ose_codigo = cursor.getInt(cursor.getColumnIndex("ose_codigo"));							
				fecha = cursor.getString(cursor.getColumnIndex("fecha"));
				foto_url = cursor.getString(cursor.getColumnIndex("foto_url"));		

				actualizoOrden = managerServerEnvio.insertarFoto(ose_codigo, fecha, foto_url);
				System.out.println( "FOTO POSTGRES ose_codigo " + ose_codigo );

				if(actualizoOrden){
					manager.actualizarEnvioFoto(String.valueOf(ose_codigo),fecha);
					System.out.println( "ACTUALIZO ENVIO FOTO ose_codigo " + ose_codigo );
				}

			}while(cursor.moveToNext());//accessing data upto last row from table
		}
		manager.close();
	}

	private void enviarGpsPendiente(){
		DataBaseManager manager;
		Cursor cursor;
		String strArgGps = "",strArgGpsid= "";
		int tamano = 0, aff = 0;

		manager = new DataBaseManager(mContext);
		manager.open();
		cursor = manager.gpsPendintes();		
		strArgGps = generarStrGps(cursor);

		if(!strArgGps.isEmpty()){
			tamano = cursor.getCount();
			DataBasePostgresManager managerServerEnvio = new DataBasePostgresManager(tipoUsuario);
			aff = managerServerEnvio.ejecutar_gps(strArgGps,tamano);
			if(aff == tamano){
				strArgGpsid = generarStrGpsId(cursor); 
				manager.gpsUpdatePendientes(strArgGpsid);
			}
		}
		manager.close();
	}

	private String generarStrGps(Cursor cursor){

		String strArgGps ="";			
		if(cursor.moveToFirst()){
			int cuadrilla = 0, orden_servicio = 0, estado = 0, terminal = 0, tamano = 0; 
			String latitud = "", longitud = "", fecha = "", bateria = "", imei = "";
			boolean primeraVez = true;			
			do{
				orden_servicio = cursor.getInt(cursor.getColumnIndex("orden"));		
				cuadrilla = cursor.getInt(cursor.getColumnIndex("cuadrilla"));
				latitud = cursor.getString(cursor.getColumnIndex("latitud"));
				longitud = cursor.getString(cursor.getColumnIndex("longitud"));
				fecha = cursor.getString(cursor.getColumnIndex("fecha"));
				estado = cursor.getInt(cursor.getColumnIndex("estado"));
				bateria = cursor.getString(cursor.getColumnIndex("bateria"));
				terminal = cursor.getInt(cursor.getColumnIndex("terminal"));
				imei = cursor.getString(cursor.getColumnIndex("vehiculo"));

				if(primeraVez){
					primeraVez = false;
				}else strArgGps += "||";

				strArgGps += orden_servicio + ";"
						+ cuadrilla + ";"
						+ latitud + ";"
						+ longitud + ";"
						+ fecha + ";"
						+ estado + ";"
						+ bateria + ";"
						+ terminal + ";"
						+ imei;				
			}while(cursor.moveToNext());//accessing data upto last row from table
		}

		return strArgGps;		
	}

	private String generarStrGpsId(Cursor cursor){

		String strArgGps ="";			
		if(cursor.moveToFirst()){
			int id = 0;
			boolean primeraVez = true;			
			do{
				id = cursor.getInt(cursor.getColumnIndex("_id"));		

				if(primeraVez){
					primeraVez = false;
				}else strArgGps += ",";

				strArgGps += id;			
			}while(cursor.moveToNext());//accessing data upto last row from table
		}

		return strArgGps;		
	}


	private String generarStrOrdenAnular(Cursor cursor){

		String strArgOrden ="";			
		if(cursor.moveToFirst()){
			int orden_servicio = 0; 
			boolean primeraVez = true;			
			do{
				orden_servicio = cursor.getInt(cursor.getColumnIndex("ose_codigo"));		

				if(primeraVez){
					primeraVez = false;
				}else strArgOrden += ",";

				strArgOrden += orden_servicio;

			}while(cursor.moveToNext());//accessing data upto last row from table
		}

		return strArgOrden;		
	}
}
