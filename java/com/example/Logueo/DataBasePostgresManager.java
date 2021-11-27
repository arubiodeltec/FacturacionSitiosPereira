package com.example.Logueo;


import android.os.Bundle;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Esta clase administradora conexiones con base de datos Servidor
 * @author: Jasson Trujillo Ortiz
 * @version: 22/09/2016/A
 * @see <a href = "http://www.deltec.com.co" /> Deltec S.A. </a>
 */
public class DataBasePostgresManager {

	Connection conn;
	public DataBasePostgresConection c;

	/** 
	 * Constructor Manejador de Bd SERVER
	 */
	public DataBasePostgresManager(int tipoUsuario){

		c  = new DataBasePostgresConection(tipoUsuario);
	} //Cierre Constructor

    public Connection getDBConnection() {

        Connection dbConnection = null;
        try {
            Class.forName(c.getDbDriver());
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        try {

            //dbConnection = DriverManager.getConnection(c.getConection(),c.getUserName(),c.getPassword());

			//String url = "jdbc:postgresql://localhost/test";
			Properties props = new Properties();
			props.setProperty("user",c.getUserName());
			props.setProperty("password",c.getPassword());
			props.setProperty("allowEncodingChanges","false");

			dbConnection= DriverManager.getConnection(c.getConection(), props);

			return dbConnection;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return dbConnection;
    }

	public Connection getDBConnectionVieja() {

		Connection dbConnection = null;
		try {
			Class.forName(c.getDbDriver());
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}

		try {
			dbConnection = DriverManager.getConnection(c.getConection(),c.getUserName(),c.getPassword());
			return dbConnection;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		return dbConnection;
	}




	/**
	 * Autenticacion del usuario en el servidor
	 * @param
	 * @param
	 * @return
	 */
	public Bundle autenticacionCuadrillaBd(String codigo, String cedula) {
        String stsql;
        Statement st;
        ResultSet rs;
		Bundle datosUsuario = new Bundle();

		stsql = "SELECT b.bod_codigo, ENCODE(CONVERT_TO(b.bod_nombre, 'SQL_ASCII'), 'ESCAPE') AS bod_nombre, p.per_telefono1, p.per_telefono2, p.per_cargo " +
				"	FROM bodega b, persona p " +
				"	WHERE b.bod_codigo=p.per_codigo AND b.bod_codigo=" + codigo + " AND p.per_documento='" + cedula + "'";

		try {
			conn = getDBConnection();

			st = conn.createStatement();
			rs = st.executeQuery(stsql);
			while(rs.next()) {


				datosUsuario.putString("nombreKey", rs.getString(rs.findColumn("bod_nombre")));
				datosUsuario.putInt("codigoKey", rs.getInt(rs.findColumn("bod_codigo")));
				datosUsuario.putString("per_cargo", rs.getString (rs.findColumn("per_cargo")));
            }

        }catch (Exception e){
            Log.i("error","Sin conexion ");
        }
		return datosUsuario;
    }

	/** 
	 * Consulta las ordenes asignadas a la cuadrilla
	 * @param  codigo Codigo del usuario
	 */
	public ResultSet sincronizarOrdenes(String codigo, int tipoUsuario){
		String stsql = "";
		Statement st=null;	
		ResultSet rs= null;


		stsql = "	SELECT * " +
				"	FROM lecturas_ejecutar_fact " +
				"	WHERE   prog_recurso_humano=" + codigo ;

		if(tipoUsuario == 2)
			stsql = "	SELECT DISTINCT o.ose_codigo, o.ose_precarga, o.ose_tip_codigo, c.cli_contrato, ENCODE(CONVERT_TO(c.cli_nombre, 'SQL_ASCII'), 'ESCAPE') AS cli_nombre, " +
					"		ci.cic_descripcion AS ciu_nombre, o.ose_direccion AS direccion1, c.cli_direccion As direccion2, " +
					"		t.tip_nombre as producto, o.ose_ciclo as ciclo, c.cli_direccion_descriptiva AS barrio, " +
					"		t1.tip_nombre as consumo, (m.ome_prefijo || ' ' || o.ose_elemento)::text as elemento, o.ose_lectura_anterior as lectura_anterior, " +
					"		o.ose_consumo_promedio as consumo_promedio, o.ose_lectura_actual as lectura_actual, o.ose_tope_medicion as cantidad_digitos, " +
					"       o.ose_tipo_producto,o.ose_ruta, o.ose_ruta_consecutivo " +
					"	FROM orden_servicio_temp o  " +
					"		INNER JOIN programacion_supervisor p ON  o.ose_codigo = p.prs_ose_codigo" +
					"		INNER JOIN  cliente_orden_temp c ON c.ose_codigo = o.ose_codigo" +
					"       INNER JOIN orden_medidor_temp m ON o.ose_codigo = m.ose_codigo" +
                    "		INNER JOIN tipo t ON t.tip_codigo=o.ose_tipo_producto  " +
					"		INNER JOIN tipo t1 ON t1.tip_codigo=o.ose_tipo_consumo " +
					"		INNER JOIN ciclo ci ON ci.cic_codigo=o.ose_ciclo  " +
					"	WHERE  o.ose_est_codigo=27 AND p.prs_est_codigo = 30 AND p.prs_registro_vigente = TRUE AND p.prs_recurso_humano= " + codigo ;

		try {
			conn = getDBConnection();
			st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); //CONSULTAR TAMANO
			rs = st.executeQuery(stsql);	
			conn.close();
		} catch (SQLException se) {
			System.out.println("oops! No se puede conectar. Error: " + se.toString());
		}

		return rs;
	}// Cierre Sincronizacion de Ordenes

	public ResultSet detalle_orden_facturacion(String ose_codigo){
		String stsql = "";
		Statement st=null;
		ResultSet rs= null;


		stsql = "  SELECT ose_codigo, ruta, orden_id, \"CODDELTEC\", ose_est_codigo, ose_fecha_creacion, ose_usuario_creacion " +
				"    FROM facturacion.detalle_orden_facturacion " +
				"   WHERE ose_codigo IN(" + ose_codigo + ")";

		Log.d("QUERY: ", stsql);
		try {
			conn = getDBConnection();
			st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); //CONSULTAR TAMANO
			rs = st.executeQuery(stsql);
			conn.close();
		} catch (SQLException se) {
			System.out.println("oops! No se puede conectar. Error: " + se.toString());
		}

		return rs;
	}// Cierre Sincronizacion de Ordenes


	// Get Fields from FacImpresion
	public ResultSet getFacImpresion(String ordenId, String CODDELTEC){
		String stsql = "";
		Statement st=null;
		ResultSet rs= null;

		stsql = " SELECT facturacion.fac_impresion.* FROM facturacion.fac_impresion " +
				" WHERE orden_id IN (" + ordenId + ")" +
				" AND \"CODDELTEC\" IN (" + CODDELTEC + ") ";

		Log.d("QUERY: ", stsql);

		try {
			conn = getDBConnection();
			st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); //CONSULTAR TAMANO
			rs = st.executeQuery(stsql);
			conn.close();
		} catch (SQLException se) {
			System.out.println("oops! No se puede conectar. Error: " + se.toString());
		}

		return rs;
	}// Cierre Sincronizacion de Ordenes


	// Get Fields from fac_laborconceptos
	public ResultSet getFacLaborConceptos(String ordenId, String CODDELTEC){
		String stsql = "";
		Statement st=null;
		ResultSet rs= null;


		stsql = " SELECT * FROM facturacion.fac_laborconceptos " +
				" WHERE orden_id IN (" + ordenId + ")" +
				" AND \"CODDELTEC\" IN (" + CODDELTEC + ") ";

		Log.d("QUERY: ", stsql);

		try {
			conn = getDBConnection();
			st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); //CONSULTAR TAMANO
			rs = st.executeQuery(stsql);
			conn.close();
		} catch (SQLException se) {
			System.out.println("oops! No se puede conectar. Error: " + se.toString());
		}

		return rs;
	}// Cierre Sincronizacion de Ordenes

	public ResultSet sincronizarDatosServer(String ordenesCargadas, String CODDELTEC) {
		String stsql = "";
		Statement st = null;
		ResultSet rs = null;

		stsql = "SELECT " +
				"(SELECT count(*) FROM facturacion.detalle_orden_facturacion as d " +
				"INNER JOIN facturacion.fac_impresion fi ON d.orden_id = fi.orden_id " +
				"WHERE d.ose_codigo in (" + ordenesCargadas + ")) fac_impresion, " +
				"(SELECT count(*) FROM facturacion.fac_rangos  as fra " +
				"WHERE \"CODDELTEC\" IN ('" + CODDELTEC + "')) rangos, " +
				"(SELECT count(*) FROM facturacion.detalle_orden_facturacion as d " +
				"INNER JOIN facturacion.fac_laborconceptos fla ON d.orden_id = fla.orden_id " +
				"WHERE d.ose_codigo in (" + ordenesCargadas + "))  fac_laborconceptos, " +
				"(SELECT count(*) FROM facturacion.detalle_orden_facturacion as d " +
				"INNER JOIN facturacion.scm_elementos_lectura scele ON d.orden_id = scele.orden_id " +
				"WHERE d.ose_codigo in( " + ordenesCargadas + ")) scm_elementos_lectura, " +
				"(SELECT count(*) FROM facturacion.detalle_orden_facturacion as d " +
				"INNER JOIN facturacion.scm_ordenes_trabajo scmor ON d.orden_id = scmor.orden_id " +
				"WHERE d.ose_codigo in (" + ordenesCargadas + ") ) scm_trabajo ";
		Log.d("QUERY: ", stsql);
		try {
			conn = getDBConnection();
			st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); //CONSULTAR TAMANO
			rs = st.executeQuery(stsql);
			conn.close();
		} catch (SQLException se) {
			System.out.println("oops! No se puede conectar. Error: " + se.toString());
		}

		return rs;
	}// getRangos

	// Get Fields from rangos
	public ResultSet getRangos(String CODDELTEC){
		String stsql = "";
		Statement st=null;
		ResultSet rs= null;


		stsql = " SELECT * FROM facturacion.fac_rangos" +
				" WHERE \"CODDELTEC\" IN (" + CODDELTEC + ") ";

		Log.d("QUERY: ", stsql);

		try {
			conn = getDBConnection();
			st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); //CONSULTAR TAMANO
			rs = st.executeQuery(stsql);
			conn.close();
		} catch (SQLException se) {
			System.out.println("oops! No se puede conectar. Error: " + se.toString());
		}

		return rs;
	}// getRangos



	// Get Fields from Elementos Lectura
	public ResultSet getElementosLectura(String ordenID, String CODDELTEC){
		String stsql = "";
		Statement st=null;
		ResultSet rs= null;


		stsql = " SELECT * FROM facturacion.scm_elementos_lectura" +
				" WHERE orden_id IN (" + ordenID + ") " +
				" AND \"CODDELTEC\" IN (" + CODDELTEC + ") ";

		Log.d("QUERY: ", stsql);

		try {
			conn = getDBConnection();
			st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); //CONSULTAR TAMANO
			rs = st.executeQuery(stsql);
			conn.close();
		} catch (SQLException se) {
			System.out.println("oops! No se puede conectar. Error: " + se.toString());
		}

		return rs;
	}// getElementosLectura
    public ResultSet getMultitabla(String CODDELTEC){
        String stsql = "";
        Statement st=null;
        ResultSet rs= null;


        stsql = " SELECT facturacion.scm_multitabla.* FROM facturacion.scm_multitabla" +
                " WHERE \"CODDELTEC\" IN (" + CODDELTEC + ") ";

        Log.d("QUERY: ", stsql);

        try {
            conn = getDBConnection();
            st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); //CONSULTAR TAMANO
            rs = st.executeQuery(stsql);
            conn.close();
        } catch (SQLException se) {
            System.out.println("oops! No se puede conectar. Error: " + se.toString());
        }

        return rs;
    }// getElementosL

	// Get Fields from Ordenes Trabajo
	public ResultSet getOrdenesTrabajo(String ordenID, String CODDELTEC){
		String stsql = "";
		Statement st=null;
		ResultSet rs= null;


		stsql = " SELECT facturacion.scm_ordenes_trabajo.* FROM facturacion.scm_ordenes_trabajo" +
				" WHERE orden_id IN (" + ordenID + ") " +
				" AND \"CODDELTEC\" IN (" + CODDELTEC + ") ";

		Log.d("QUERY: ", stsql);

		try {
			conn = getDBConnection();
			st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); //CONSULTAR TAMANO
			rs = st.executeQuery(stsql);
			conn.close();
		} catch (SQLException se) {
			System.out.println("oops! No se puede conectar. Error: " + se.toString());
		}

		return rs;
	}// getElementosLectura


	public ResultSet getObsConsumo(){
		String stsql = "";
		Statement st=null;
		ResultSet rs= null;


		stsql = " SELECT facturacion.scm_obsconsumo.* FROM facturacion.scm_obsconsumo ORDER BY tip_codigo";

		Log.d("QUERY: ", stsql);

		try {
			conn = getDBConnection();
			st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); //CONSULTAR TAMANO
			rs = st.executeQuery(stsql);
			conn.close();
		} catch (SQLException se) {
			System.out.println("oops! No se puede conectar. Error: " + se.toString());
		}

		return rs;
	}// getOBSCONSUMO

	public ResultSet getObsNoLectura(){
		String stsql = "";
		Statement st=null;
		ResultSet rs= null;


		stsql = " SELECT facturacion.scm_causanolectura.* FROM facturacion.scm_causanolectura ORDER BY codcausa DESC";

		Log.d("QUERY: ", stsql);

		try {
			conn = getDBConnection();
			st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); //CONSULTAR TAMANO
			rs = st.executeQuery(stsql);
			conn.close();
		} catch (SQLException se) {
			System.out.println("oops! No se puede conectar. Error: " + se.toString());
		}

		return rs;
	}// getCausanolectura

	public ResultSet sincronizarOrdenesVieja(String codigo, int tipoUsuario){
		String stsql = "";
		Statement st=null;
		ResultSet rs= null;

		stsql = "	SELECT DISTINCT	o.ose_codigo, o.ose_precarga, o.ose_tip_codigo, c.cli_contrato, ENCODE(CONVERT_TO(c.cli_nombre, 'SQL_ASCII'), 'ESCAPE') AS cli_nombre, ci.cic_descripcion AS ciu_nombre, o.ose_direccion AS direccion1, " +
				"		c.cli_direccion As direccion2, t.tip_nombre as producto, o.ose_ciclo as ciclo, c.cli_direccion_descriptiva AS barrio, " +
				"		t1.tip_nombre as consumo, o.ose_elemento as elemento, o.ose_lectura_anterior as lectura_anterior,	" +
				"		o.ose_consumo_promedio as consumo_promedio, o.ose_lectura_actual as lectura_actual, o.ose_tope_medicion as cantidad_digitos, " +
				"		o.ose_tipo_producto,o.ose_ruta, o.ose_ruta_consecutivo " +
				"	FROM orden_servicio_temp o  " +
				"		INNER JOIN programacion p ON  o.ose_prog_codigo = p.prog_codigo" +
				"		INNER JOIN  cliente_orden_temp c ON c.ose_codigo = o.ose_codigo" +
				"       INNER JOIN orden_medidor_temp m ON o.ose_codigo = m.ose_codigo"  +
				"		INNER JOIN tipo t ON t.tip_codigo=o.ose_tipo_producto  " +
				"		INNER JOIN tipo t1 ON t1.tip_codigo=o.ose_tipo_consumo " +
				"		INNER JOIN ciclo ci ON ci.cic_codigo=o.ose_ciclo  " +
				"	WHERE   o.ose_est_codigo=30 and  p.prog_recurso_humano=" + codigo ;

		if(tipoUsuario == 2)
			stsql = "	SELECT DISTINCT o.ose_codigo, o.ose_precarga, o.ose_tip_codigo, c.cli_contrato, ENCODE(CONVERT_TO(c.cli_nombre, 'SQL_ASCII'), 'ESCAPE') AS cli_nombre, " +
					"		ci.cic_descripcion AS ciu_nombre, o.ose_direccion AS direccion1, c.cli_direccion As direccion2, " +
					"		t.tip_nombre as producto, o.ose_ciclo as ciclo, c.cli_direccion_descriptiva AS barrio, " +
					"		t1.tip_nombre as consumo, (m.ome_prefijo || ' ' || o.ose_elemento)::text as elemento, o.ose_lectura_anterior as lectura_anterior, " +
					"		o.ose_consumo_promedio as consumo_promedio, o.ose_lectura_actual as lectura_actual, o.ose_tope_medicion as cantidad_digitos, " +
					"       o.ose_tipo_producto,o.ose_ruta, o.ose_ruta_consecutivo " +
					"	FROM orden_servicio_temp o  " +
					"		INNER JOIN programacion_supervisor p ON  o.ose_codigo = p.prs_ose_codigo" +
					"		INNER JOIN  cliente_orden_temp c ON c.ose_codigo = o.ose_codigo" +
					"       INNER JOIN orden_medidor_temp m ON o.ose_codigo = m.ose_codigo" +
					"		INNER JOIN tipo t ON t.tip_codigo=o.ose_tipo_producto  " +
					"		INNER JOIN tipo t1 ON t1.tip_codigo=o.ose_tipo_consumo " +
					"		INNER JOIN ciclo ci ON ci.cic_codigo=o.ose_ciclo  " +
					"	WHERE  o.ose_est_codigo=27 AND p.prs_est_codigo = 30 AND p.prs_registro_vigente = TRUE AND p.prs_recurso_humano= " + codigo ;

		try {
			conn = getDBConnection();
			st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); //CONSULTAR TAMA?O
			rs = st.executeQuery(stsql);
			conn.close();
		} catch (SQLException se) {
			System.out.println("oops! No se puede conectar. Error: " + se.toString());
		}

		return rs;
	}// Cierre Sincronizacion de Ordenes

	/** 
	 * Consulta stock asignado a la cuadrilla
	 * @param  codigo Codigo del usuario
	 */
	public ResultSet sincronizarStock(String codigo){
		String stsql = "";
		Statement st=null;	
		ResultSet rs= null;

		stsql = "SELECT recurso.rec_codigo, recurso.rec_nombre ,detalle_inventario.dei_prefijo, " +
				"		detalle_inventario.dei_serie, detalle_inventario.dei_cantidad, " +
				"		recurso.rec_seriado, tipo.tip_nombre " +
				"	FROM 	bodega, detalle_inventario, inventario, recurso, tipo " +
				"	WHERE 	bodega.bod_codigo = inventario.bod_codigo AND " +
                "			tipo.tip_codigo=recurso.tip_tip_codigo AND " +
                "			recurso.rec_codigo = detalle_inventario.rec_codigo AND " +
				"			inventario.inv_codigo = detalle_inventario.inv_codigo AND " +
				"			bodega.bod_codigo= " + codigo + " AND " +
				"			recurso.cre_codigo=1 AND " +
				"			inventario.tip_codigo=55 ORDER BY recurso.rec_codigo ASC";

		try {
			conn = getDBConnection();
			st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); //CONSULTAR TAMA?O
			rs = st.executeQuery(stsql);	
			conn.close();
		} catch (SQLException se) {
			System.out.println("oops! No se puede conectar. Error: " + se.toString());
		}

		return rs;
	}// Cierre Sincronizacion de Ordenes

	/**
	 * Consulta las ordenes asignadas a la cuadrilla
	 * @return
	 */
	public ResultSet sincronizarObservaciones() {
        String stsql = "";
		Statement st=null;	
		ResultSet rs= null;


		stsql = "SELECT * " +
				"FROM facturacion.scm_obsconsumo " +
				"UNION " +
				"SELECT * " +
				"FROM facturacion.scm_causanolectura " +
				"ORDER BY tip_codigo";

		/* stsql = " 	SELECT t.tip_codigo, t.cti_codigo, ( t.tip_homologa || ' - ' || t.tip_nombre)::text AS tip_nombre " +
				"		FROM tipo t " +
				"	WHERE t.cti_codigo IN( 60, 61, 69) AND t.tip_descripcion <> '0'  ORDER BY t.tip_codigo"; */
		//COMENTADO POR ALEJO DURAN OLD CONSULTA OBSERVACIONES

		/*
		stsql = " 	SELECT t.tip_codigo, t.cti_codigo, ( t.tip_homologa || ' - ' || t.tip_nombre)::text AS tip_nombre " +
				"		FROM tipo t " +
				"	WHERE t.cti_codigo IN( 60, 61, 69) AND t.tip_codigo NOT IN ( 996, 776 )  ORDER BY t.tip_codigo";
				*/


		try {
			Class.forName("org.postgresql.Driver");
			conn = getDBConnection();;
			st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); //CONSULTAR TAMA?O
			rs = st.executeQuery(stsql);	
			conn.close();
		} catch (SQLException se) {
			System.out.println("oops! No se puede conectar. Error: " + se.toString());
		} catch (ClassNotFoundException e) {
			System.out.println("oops! No se encuentra la clase. Error: " + e.getMessage());
		}

		return rs;
	}// Cierre Sincronizacion de Ordenes

    public ResultSet selectDatosConteo() {
        String stsql = "";
        Statement st=null;
        ResultSet rs= null;


        stsql = "SELECT * " +
                "FROM facturacion.scm_obsconsumo " +
                "UNION " +
                "SELECT * " +
                "FROM facturacion.scm_causanolectura " +
                "ORDER BY tip_codigo";

		/* stsql = " 	SELECT t.tip_codigo, t.cti_codigo, ( t.tip_homologa || ' - ' || t.tip_nombre)::text AS tip_nombre " +
				"		FROM tipo t " +
				"	WHERE t.cti_codigo IN( 60, 61, 69) AND t.tip_descripcion <> '0'  ORDER BY t.tip_codigo"; */
        //COMENTADO POR ALEJO DURAN OLD CONSULTA OBSERVACIONES

		/*
		stsql = " 	SELECT t.tip_codigo, t.cti_codigo, ( t.tip_homologa || ' - ' || t.tip_nombre)::text AS tip_nombre " +
				"		FROM tipo t " +
				"	WHERE t.cti_codigo IN( 60, 61, 69) AND t.tip_codigo NOT IN ( 996, 776 )  ORDER BY t.tip_codigo";
				*/


        try {
            Class.forName("org.postgresql.Driver");
            conn = getDBConnection();;
            st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); //CONSULTAR TAMA?O
            rs = st.executeQuery(stsql);
            conn.close();
        } catch (SQLException se) {
            System.out.println("oops! No se puede conectar. Error: " + se.toString());
        } catch (ClassNotFoundException e) {
            System.out.println("oops! No se encuentra la clase. Error: " + e.getMessage());
        }

        return rs;
    }

	public boolean actualizarOrdenServicio(int ose_codigo, String lectura, int codigo_observacion_no_lectura,
			int codigo_observacion_lectura,String observacion_no_lectura, int indicador_lectura, int critica,
			String fecha,int intentos,int encontro_medidor,int medidor_correcto,String serie_medidor_encontrado,
			int actividad,String sellos_instalados, String motivo_ejecucion, int retiro_acometida,
			String datos_retiro_acometida,int reconexion_no_autorizada,String censo_carga, String latitud, String longitud ){
		String stsqlUpdate = "",stsqlInsert = "";
		Statement st=null;	
		int aff = 0;
		boolean ejecuto = false,bencontro_medidor = false, bmedidor_correcto = false;

		if(encontro_medidor==1)
			bencontro_medidor = true;

		if(medidor_correcto==1)
			bmedidor_correcto = true;

		stsqlUpdate = " UPDATE lectura_medidor_temp SET lme_lectura = '"+ lectura +"'," +
				"	lme_codigo_observacion_no_lectura = "+ codigo_observacion_no_lectura +"," +
				"   lme_codigo_observacion_lectura = "+ codigo_observacion_lectura +"," +
				"	lme_observacion_no_lectura = '"+ observacion_no_lectura +"'," +
				"	lme_indicador_lectura = "+ indicador_lectura +", lme_critica = "+ critica +"," +
				"	lme_fecha_actualizacion = '"+ fecha +"', lme_fecha_carga = now()," +
				"	lme_intentos = "+ intentos +", lme_desviacion = 0, lme_latitud = '" + latitud + "'," +
				"	lme_longitud = '" + longitud + "', lme_encontro_medidor = "+ bencontro_medidor +", " +
				"	lme_medidor_correcto =  "+ bmedidor_correcto +", lme_serie_medidor_encontrado = '"+ serie_medidor_encontrado +"', " +
				"	lme_actividad =  "+ actividad +", lme_sellos_instalados = '"+ sellos_instalados +"'," +
				"	lme_motivo_ejecucion = '"+ motivo_ejecucion +"', lme_retiro_acometida = "+ retiro_acometida +", " +
				"	lme_datos_retiro_acometida='"+ datos_retiro_acometida +"',lme_reconexion_no_autorizada=" + reconexion_no_autorizada +"," +
				"	lme_censo_carga="+ censo_carga +
				"	WHERE lme_ose_codigo = "+ ose_codigo; 		

		try {
			Class.forName("org.postgresql.Driver");
			conn = getDBConnection();;
			conn.setAutoCommit(false);
			st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); //CONSULTAR TAMA?O

			aff = st.executeUpdate(stsqlUpdate);

			if(aff == 0){
				stsqlInsert = "INSERT INTO lectura_medidor_temp (lme_ose_codigo, lme_lectura, lme_codigo_observacion_no_lectura," +
						"lme_codigo_observacion_lectura, lme_observacion_no_lectura, lme_indicador_lectura, lme_critica," +
						"lme_fecha_creacion, lme_fecha_carga, lme_fecha_actualizacion," +
						"lme_intentos, lme_desviacion, lme_latitud, lme_longitud, lme_encontro_medidor, lme_medidor_correcto," +
						"lme_serie_medidor_encontrado, lme_actividad, lme_sellos_instalados,lme_motivo_ejecucion," +
						"lme_retiro_acometida, lme_datos_retiro_acometida,lme_reconexion_no_autorizada,lme_censo_carga)" +
						"VALUES("+ ose_codigo +", '"+ lectura +"'," +
						""+ codigo_observacion_no_lectura +", "+ codigo_observacion_lectura +", '"+ observacion_no_lectura +"'," +
						""+ indicador_lectura +", "+ critica +", '"+ fecha +"', now(), '"+ fecha +"'," +
						""+ intentos +", 0,'"+ latitud + "','"+ longitud +"'," +
						""+ bencontro_medidor +", "+ bmedidor_correcto +",'"+ serie_medidor_encontrado +"', "+ actividad +", '"+ sellos_instalados +"'," +
						"'"+ motivo_ejecucion +"', "+ retiro_acometida +", '"+ datos_retiro_acometida +"',"+ reconexion_no_autorizada +","+ censo_carga +")";
				aff = st.executeUpdate(stsqlInsert); 
			}
			st.close(); 
			conn.commit();
			conn.close();
		} catch (SQLException se) {
			System.out.println("oops! No se puede conectar. Error: " + se.toString());
		} catch (ClassNotFoundException e) {
			System.out.println("oops! No se encuentra la clase. Error: " + e.getMessage());
		}		

		if(aff == 1)
			ejecuto = true;

		return ejecuto;
	}

	public int ejecutar_gps(String arg_gps, int cantidad_gps){
		int aff = 0;		
		String stsql = "SELECT insert_gps_new2('" + arg_gps + "'," + cantidad_gps + ")";

		Statement st=null;	
		ResultSet rs= null;

		try {
			Class.forName("org.postgresql.Driver");
			conn = getDBConnection();
			if(conn != null) {
				st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); //CONSULTAR TAMA?O
				rs = st.executeQuery(stsql);
				conn.close();

				while (rs.next()) {
					aff = rs.getInt(1);
				}
			}
		} catch (SQLException se) {
			System.out.println("oops! No se puede conectar. Error: " + se.toString());
		} catch (ClassNotFoundException e) {
			System.out.println("oops! No se encuentra la clase. Error: " + e.getMessage());
		}

		return aff;
	}

	public boolean insertarClientePesada(int clp_cli_contrato, int clp_codigo_observacion_lectura ){
		String stsqlInsert = "";
		Statement st=null;	
		int aff = 0;
		boolean ejecuto = false;

		stsqlInsert = "INSERT INTO clientes_pesada (clp_cli_contrato, clp_codigo_observacion_lectura) " +
				" VALUES("+ clp_cli_contrato +","+ clp_codigo_observacion_lectura +")";	

		try {
			Class.forName("org.postgresql.Driver");
			conn = getDBConnection();
			conn.setAutoCommit(false);
			st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); //CONSULTAR TAMA?O
			aff = st.executeUpdate(stsqlInsert);

			st.close(); 
			conn.commit();
			conn.close();
		} catch (SQLException se) {
			System.out.println("oops! No se puede conectar. Error: " + se.toString());
		} catch (ClassNotFoundException e) {
			System.out.println("oops! No se encuentra la clase. Error: " + e.getMessage());
		}		

		if(aff == 1)
			ejecuto = true;

		return ejecuto;
	}

}
