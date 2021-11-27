package com.example.Logueo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;

import java.io.File;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * Esta clase administradora conexiones con base de datos
 * @author: Jasson Trujillo Ortiz
 * @version: 22/09/2016/A
 * @see <a href = "http://www.deltec.com.co" /> Deltec S.A. </a>
 */
public class DataBaseManager {

	public static final String TABLE_ORDEN ="orden_servicio";
	public static final String OS_ID = "_id";
	public static final String OS_OSE_CODIGO = "ose_codigo";
	public static final String OS_OSE_PRECARGA = "ose_precarga";
	public static final String OS_OSE_TIP_ORDEN = "tip_orden";
	public static final String OS_CLI_CONTRATO = "cli_contrato";
	public static final String OS_CLI_NOMBRE = "cli_nombre";
	public static final String OS_CIU_NOMBRE = "ciu_nombre";
	public static final String OS_DIRECCION1 = "direccion1";
	public static final String OS_DIRECCION2 = "direccion2";
	public static final String OS_PRODUCTO = "producto";
	public static final String OS_BARRIO = "barrio";
	public static final String OS_CICLO = "ciclo";
	public static final String OS_RUTA = "ruta";
	public static final String OS_RUTA_CONS = "ruta_cons";
	public static final String OS_ELEMENTO = "elemento";
	public static final String OS_INFO_ADD = "info_add";
	public static final String OS_ESTADO = "estado";
	public static final String OS_OBS = "obs";	
	public static final String OS_ANOMALIA = "anomalia";
	public static final String OS_FECHA_ANOMALIA = "fecha_anomalia";
	public static final String OS_REG_FOTO = "reg_foto";
	public static final String OS_TITULAR = "titular";
	public static final String OS_FECHA_PAGO = "fecha_pago";
	public static final String OS_ENTIDAD_RECAUDO = "entidad_recaudo";
	public static final String OS_PROPIETARIO = "propietario";
	public static final String OS_DATAFONO = "datafono";	
	public static final String OS_CONSUMO = "consumo";
	public static final String OS_LECTURA_ANTERIOR = "lectura_anterior";
	public static final String OS_CONSUMO_PROMEDIO= "consumo_promedio";
	public static final String OS_LECTURA_ACTUAL = "lectura_actual";
	public static final String OS_CANTIDAD_DIGITOS = "cantidad_digitos";
	public static final String OS_TIPO_PRODUCTO = "ose_tipo_producto";
	public static final String OS_CUADRILLA = "cuadrilla";
	public static final String OS_SINCRONIZADO = "sincronizado";
	public static final String OS_ESTADO_FENS = "estado_fens";


	public static final String TABLE_USUARIO ="usuario";
	public static final String U_ID = "_id";
	public static final String U_CUADRILLA = "codigo";
	public static final String U_CEDULA = "cedula";
	public static final String U_TIPO = "tipo";
	public static final String U_NOMBRE = "nombre";
	public static final String U_FECHA_CREACION = "fecha_creacion";
	public static final String U_FECHA_LOGUEO = "fecha_logueo";

	public static final String TABLE_STOCK ="stock_cuadrilla";
	public static final String SC_ID = "_id";
	public static final String SC_REC_CODIGO = "rec_codigo";
	public static final String SC_REC_NOMBRE = "rec_nombre";
	public static final String SC_REC_SERIE = "rec_serie";
	public static final String SC_REC_CANTIDAD = "rec_cantidad";
	public static final String SC_REC_SERIADO = "rec_seriado";
	public static final String SC_REC_UNIDAD = "rec_unidad";
	public static final String SC_REC_PREFIJO = "rec_prefijo";
	public static final String SC_REC_CUADRILLA = "cuadrilla";

	public static final String TABLE_MATERIAL_INSTALADO ="material_instalado";
	public static final String MI_ID = "_id";
	public static final String MI_OSE_CODIGO = "ose_codigo";
	public static final String MI_REC_CODIGO = "rec_codigo";
	public static final String MI_REC_NOMBRE = "rec_nombre";
	public static final String MI_REC_CANTIDAD = "rec_cantidad";
	public static final String MI_REC_SERIE = "rec_serie";
	public static final String MI_PREFIJO= "rec_prefijo";
	public static final String MI_FECHA_CREACION = "fecha_creacion";
	public static final String MI_TIPO = "tipo";
	public static final String MI_BODEGA = "cuadrilla";
	public static final String MI_SINCRONIZADO = "sincronizado";


	public static final String TABLE_EJECUCION ="ejecucion_orden";
	public static final String EO_ID = "_id";
	public static final String EO_OSE_CODIGO = "ose_codigo";
	public static final String EO_LECTURA = "lectura";
	public static final String EO_CODIGO_NO_LECTURA = "codigo_observacion_no_lectura";
	public static final String EO_CODIGO_OBS_LECTURA = "codigo_observacion_lectura";
	public static final String EO_OBS_NO_LECTURA = "observacion_no_lectura";
	public static final String EO_INDICADOR_LECTURA = "indicador_lectura";
	public static final String EO_FECHA_CREACION = "fecha_creacion";
	public static final String EO_FECHA_ACTUALIZACION = "fecha_actualizacion";
	public static final String EO_CRITICA = "critica";
	public static final String EO_INTENTOS = "intentos";
	public static final String EO_CONSUMO = "consumo";
	public static final String EO_ENCONTRO_MEDIDOR = "encontro_medidor";
	public static final String EO_MEDIDOR_CORRECTO = "medidor_correcto";
	public static final String EO_ACTIVIDAD = "actividad";
	public static final String EO_SELLOS_INSTALADOS = "sellos_instalados";
	public static final String EO_SERIE_MEDIDOR_ENCONTRADO = "medidor_encontrado";
	public static final String EO_RETIRO_ACOMETIDA = "retiro_acometida";
	public static final String EO_DATOS_RETIRO_ACOMETIDA = "datos_retiro_acometida";
	public static final String EO_CENSO_CARGA= "censo_carga";
	public static final String EO_RECONEXION_NO_AUTORIZADA = "reconexion_no_autorizada";	
	public static final String EO_MOTIVO_EJECUCION = "motivo_ejecucion";
	public static final String EO_LATITUD= "latitud";	
	public static final String EO_LONGITUD = "longitud";
	public static final String EO_SINCRONIZADO = "sincronizado";

	public static final String TABLE_OBSERVACION ="observaciones";
	public static final String OB_ID = "_id";
	public static final String OB_TIP_CODIGO = "tip_codigo";
	public static final String OB_SOLCONSUMO = "sol_consumo";
	public static final String OB_TIP_NOMBRE = "tip_nombre";

	public static final String TABLE_FOTO ="foto_orden";
	public static final String FT_ID = "_id";
	public static final String FT_OSE_CODIGO = "ose_codigo";
	public static final String FT_FECHA = "fecha";
	public static final String FT_SINCRONIZADO = "sincronizado";
	public static final String FT_FOTO_URL = "foto_url";

	public static final String TABLE_TERMINAL ="terminal";
	public static final String TE_ID = "_id";
	public static final String TE_TERMINAL = "terminal";
	public static final String TE_FECHA = "fecha";

	public static final String TABLE_GPS ="posicion_gps";	
	public static final String GP_ID = "_id";
	public static final String GP_TERMINAL = "terminal";
	public static final String GP_CUADRILLA = "cuadrilla";
	public static final String GP_LATITUD = "latitud";
	public static final String GP_LONGITUD = "longitud";
	public static final String GP_ALTITUD = "altitud";
	public static final String GP_VELOCIDAD = "velocidad";
	public static final String GP_FECHA = "fecha";
	public static final String GP_VEHICULO = "vehiculo";	
	public static final String GP_ESTADO = "estado";
	public static final String GP_ORDEN = "orden";
	public static final String GP_BATERIA = "bateria";
	public static final String GP_SINCRONIZADO = "sincronizado";

	public static final String TABLE_ASISTENCIA ="asistencia";
	public static final String ASI_FECHA = "asi_fecha";
	public static final String ASI_PER_CODIGO = "asi_per_codigo";
	public static final String ASI_SUPERVISOR = "asi_supervisor";
	public static final String ASI_ESTADO = "asi_estado";
	public static final String ASI_TIPO = "asi_tipo";
	public static final String ASI_FECHA_CREACION = "asi_fecha_creacion";
	public static final String ASI_FECHA_ACTUALIZACION = "asi_fecha_actulizacion";
	public static final String ASI_OBSERVACION = "asi_observacion";
	public static final String ASI_SINCRONIZADO = "asi_sincronizado";

	public static final String TABLE_SUPERVISOR_LECTOR ="supervisor_lector";
	public static final String SUP_SUPER_PER_CODIGO = "sup_super_per_codigo";
	public static final String SUP_LECT_PER_CODIGO = "sup_lect_per_codigo";
	public static final String SUP_LECT_PER_NOMBRE = "sup_lect_per_nombre";
	public static final String SUP_VIGENTE = "sup_vigente";
	public static final String SUP_FECHA_CREACION = "sup_fecha_creacion";
	public static final String SUP_FECHA_ACTUALIZACION = "sup_fecha_actualizacion";

	public static final String TABLE_MEDIDOR_ENCONTRADO ="medidor_encontrado";
	public static final String MED_ID = "med_id";
	public static final String MED_CICLO = "med_ciclo";
	public static final String MED_RUTA = "med_ruta";
	public static final String MED_CONS_RUTA_ANTERIOR = "med_cons_ruta_anterior";
	public static final String MED_CONS_RUTA_POSTERIOR = "med_cons_ruta_posterior";
	public static final String MED_DIRECCION = "med_direccion";
	public static final String MED_MEDIDOR = "med_medidor";
	public static final String MED_INDICADOR_MED = "med_indicador_med";
	public static final String MED_LECTURA = "med_lectura";
	public static final String MED_SERVICIO = "med_servicio";
	public static final String MED_OBSERVACION = "med_observacion";
	public static final String MED_PERSONA = "med_persona";
	public static final String MED_LATITUD = "med_latitud";
	public static final String MED_LONGITUD = "med_longitud";
	public static final String MED_FECHA = "med_fecha";
	public static final String MED_SINCRONIZADO = "med_sincronizado";



//	inicio Tablas de Facturacion en Sitio

	public static final String TABLE_SCM_CAUSANOLECTURA = "SCM_CAUSANOLECTURA";
	public static final String CNL_CODCAUSA	    = "CODCAUSA";
	public static final String CNL_DESCRIPCION	= "DESCRIPCION";
	public static final String CNL_SOLCONSUMO	= "SOLCONSUMO";
	public static final String CNL_TIPCODIGO	= "TIP_CODIGO";

	public static final String TABLE_SCM_OBSERVACIONADICIONAL = "SCM_OBSERVACIONADICIONAL";
	public static final String OBA_CODOBSADICIONAL = "CODOBSADICIONAL";
	public static final String OBA_DESOBSADICIONAL = "DESOBSADICIONAL";
	public static final String OBA_TIPCODIGO = "TIP_CODIGO";

	public static final String TABLE_SCM_OBSCONSUMO = "SCM_OBSCONSUMO";
	public static  final String OBC_CODOBSERVACION	=	"CODOBSERVACION";
	public static  final String OBC_DESCRIPCION		=	"DESCRIPCION";
	public static  final String OBC_SOLCONSUMO		= 	"SOLCONSUMO";
	public static final String  OBC_TIPCODIGO = "TIP_CODIGO";

	public static final String TABLE_SCM_TIPOS_LECTURA = "SCM_TIPOS_LECTURA";
	public static final String TIL_TIPO_LECTURA	  =  "TIPO_LECTURA";
	public static final String TIL_D_TIPO_LECTURA =  "D_TIPO_LECTURA";

	public static final String TABLE_SCM_ACCION_ANEXOS = "SCM_ACCION_ANEXOS";
	public static final String ACA_NUMERO_REVISION  = "NUMERO_REVISION";
	public static final String ACA_NOMBRE_ARCHIVO 	= "NOMBRE_ARCHIVO";
	public static final String ACA_TITULO		    = "TITULO";
	public static final String ACA_DESCRIPCION		= "DESCRIPCION";
	public static final String ACA_RUTA		    	= "RUTA";

	public static final String TABLE_SCM_ANEXOS = "SCM_ANEXOS";
	public static final String ANE_RUTA				= "RUTA";
	public static final String ANE_ORDEN_ID			= "ORDEN_ID";
	public static final String ANE_NOMBRE_ARCHIVO	= "NOMBRE_ARCHIVO";
	public static final String ANE_TIPO				= "TIPO";
	public static final String ANE_DESCRIPCION		= "DESCRIPCION";
	public static final String ANE_FECHA_SISTEMA	= "FECHA_SISTEMA";

	public static final String TABLE_SCM_ELEMENTOS_LECTURAS_DES = "SCM_ELEMENTOS_LECTURAS_DES";
	public static final String ELD_RUTA				 ="RUTA";
	public static final String ELD_ORDEN_ID			 ="ORDEN_ID";
	public static final String ELD_TIPO			     ="TIPO";
	public static final String ELD_SERIE			 ="SERIE";
	public static final String ELD_MARCA			 ="MARCA";
	public static final String ELD_TIPO_LECTURA		 ="TIPO_LECTURA";
	public static final String ELD_FECHA_LECTURA	 ="FECHA_LECTURA";
	public static final String ELD_LECTURA_TOMADA	 ="LECTURA_TOMADA";
	public static final String ELD_OBSERVACION		 ="OBSERVACION";
	public static final String ELD_OBSERVACION_AD	 ="OBSERVACION_AD";
	public static final String ELD_OBSERVACION_TEXTO ="OBSERVACION_TEXTO";
	public static final String ELD_INTENTOS			 ="INTENTOS";
	public static final String ELD_CONSUMO			 ="CONSUMO";
	public static final String ELD_SOLCONSUMO		 ="SOLCONSUMO";
	public static final String ELD_FECHA_DESCARGA	 ="FECHA_DESCARGA";
	public static final String ELD_ESTADO			 ="ESTADO";

	public static final String TABLE_SCM_CLI_CONCEPTOS_DES  = "SCM_CLI_CONCEPTOS_DES";
	public static final String CND_RUTA 			 = "RUTA ";
	public static final String CND_ORDEN_ID		 = "ORDEN_ID";
	public static final String CND_CODIGO_CONCEPTO	 = "CODIGO_CONCEPTO";
	public static final String CND_SALDOANTERIOR	 = "SALDOANTERIOR";
	public static final String CND_CANTIDAD		 = "CANTIDAD";
	public static final String CND_FACTOR			 = "FACTOR";
	public static final String CND_TARIFA			 = "TARIFA";
	public static final String CND_VALOR			 = "VALOR";
	public static final String CND_FECHA_DESCARGA	 = "FECHA_DESCARGA";

	public static final String TABLE_SCM_MULTITABLA = "SCM_MULTITABLA";
	public static final String  MTT_TABLA 			= "TABLA";
	public static final String  MTT_CODIGO 			= "CODIGO";
	public static final String  MTT_DESCRIPCION		= "DESCRIPCION";
	public static final String  MTT_FILTRO			= "FILTRO";
	public static final String  MTT_PARAMETROS		= "PARAMETROS";

	public static final String TABLE_DETALLE_ORDEN_FACTURACION = "DETALLE_ORDEN_FACTURACION";
	public static final String OSE_CODIGO			= "OSE_CODIGO";
	public static final String ORDEN_ID 			= "ORDEN_ID";
	public static final String RUTA_ORDEN 			= "RUTA_ORDEN";
	public static final String SINCRONIZADO         = "SINCRONIZADO";

	public static final String TABLE_FAC_IMPRESION = "FAC_IMPRESION";
	public static final String IMP_RUTA					= "RUTA";
	public static final String IMP_ORDEN_ID 			= "ORDEN_ID";
	public static final String IMP_IDIMPRESION 			= "IDIMPRESION";
	public static final String IMP_POSXY 				= "POSXY";
	public static final String IMP_FUENTE 				= "FUENTE";
	public static final String IMP_JUSTIFICACION 		= "JUSTIFICACION";
	public static final String IMP_FUNCION 				= "FUNCION";
	public static final String IMP_PARAMETROS 			= "PARAMETROS";
	public static final String IMP_VALOR 				= "VALOR";
	public static final String IMP_FECHA_ACTUALIZACION	= "FECHA_ACTUALIZACION";

	public static final String TABLE_SCM_ELEMENTOS_LECTURA = "SCM_ELEMENTOS_LECTURAS";
	public static final String  ELL_RUTA					= "RUTA";
	public static final String  ELL_ORDEN_ID				= "ORDEN_ID";
	public static final String  ELL_TIPO					= "TIPO";
	public static final String  ELL_ERIE					= "SERIE";
	public static final String  ELL_MARCA					= "MARCA";
	public static final String  ELL_MODELO					= "MODELO";
	public static final String  ELL_TIPO_LECTURA			= "TIPO_LECTURA";
	public static final String  ELL_ENTEROS					= "ENTEROS";
	public static final String  ELL_DECIMALES				= "DECIMALES";
	public static final String  ELL_FACTOR_MULTIPLICACION	= "FACTOR_MULTIPLICACION";
	public static final String  ELL_FECHA_ANTERIOR			= "FECHA_ANTERIOR";
	public static final String  ELL_LECTURA_ANTERIOR		= "LECTURA_ANTERIOR";
	public static final String  ELL_LIMITE_INFERIOR_CONSUMO	= "LIMITE_INFERIOR_CONSUMO";
	public static final String  ELL_LIMITE_SUPERIOR_CONSUMO	= "LIMITE_SUPERIOR_CONSUMO";
	public static final String  ELL_UBICACION				= "UBICACION";
	public static final String  ELL_SECUENCIA				= "SECUENCIA";
	public static final String  ELL_CONSUMO1				= "CONSUMO1";
	public static final String  ELL_SOLCONSUMO1				= "SOLCONSUMO1";
	public static final String  ELL_CONSUMO2				= "CONSUMO2";
	public static final String  ELL_SOLCONSUMO2				= "SOLCONSUMO2";
	public static final String  ELL_FECHA_LECTURA			= "FECHA_LECTURA";
	public static final String  ELL_LECTURA_TOMADA			= "LECTURA_TOMADA";
	public static final String  ELL_OBSERVACION				= "OBSERVACION";
	public static final String  ELL_OBSERVACION_AD			= "OBSERVACION_AD";
	public static final String  ELL_OBSERVACION_TEXTO		= "OBSERVACION_TEXTO";
	public static final String  ELL_INTENTOS				= "INTENTOS";
	public static final String  ELL_CONSUMO					= "CONSUMO";
	public static final String  ELL_SOLCONSUMO				= "SOLCONSUMO";
	public static final String  ELL_FECHA_ACTUALIZACION		= "FECHA_ACTUALIZACION";

	public static final String TABLE_FAC_LABORCONCEPTOS  = "FAC_LABORCONCEPTOS";
	public static final String  LAC_RUTA				= "RUTA";
	public static final String  LAC_ORDEN_ID			= "ORDEN_ID";
	public static final String  LAC_IDCONCEPTO			= "IDCONCEPTO";
	public static final String  LAC_ORDEN				= "ORDEN";
	public static final String  LAC_SALDOANTERIOR		= "SALDOANTERIOR";
	public static final String  LAC_IDGRUPOLECTURA		= "IDGRUPOLECTURA";
	public static final String  LAC_CANTBASE			= "CANTBASE";
	public static final String  LAC_CANTTIPORANGO		= "CANTTIPORANGO";
	public static final String  LAC_CANTCODRANGO		= "CANTCODRANGO";
	public static final String  LAC_CANTRUTINA			= "CANTRUTINA";
	public static final String  LAC_CANTIDAD			= "CANTIDAD";
	public static final String  LAC_FACTBASE			= "FACTBASE";
	public static final String  LAC_FACTTIPORANGO		= "FACTTIPORANGO";
	public static final String  LAC_FACTCODRANGO		= "FACTCODRANGO";
	public static final String  LAC_FACTRUTINA			= "FACTRUTINA";
	public static final String  LAC_FACTOR				= "FACTOR";
	public static final String  LAC_TARIFABASE			= "TARIFABASE";
	public static final String  LAC_TARIFATIPORANGO		= "TARIFATIPORANGO";
	public static final String  LAC_TARIFACODRANGO		= "TARIFACODRANGO";
	public static final String  LAC_TARIFARUTINA		= "TARIFARUTINA";
	public static final String  LAC_TARIFA				= "TARIFA";
	public static final String  LAC_VALORBASE			= "VALORBASE";
	public static final String  LAC_VALORTIPORANGO		= "VALORTIPORANGO";
	public static final String  LAC_VALORCODRANGO		= "VALORCODRANGO";
	public static final String  LAC_VALORRUTINA			= "VALORRUTINA";
	public static final String  LAC_VALOR				= "VALOR";
	public static final String  LAC_FECHA_ACTUALIZACION	= "FECHA_ACTUALIZACION";

	public static final String TABLE_FAC_RANGOS     = "FAC_RANGOS";
	public static final String RAN_RUTA 				= "RUTA";
	public static final String RAN_TIPORANGO 			= "TIPORANGO";
	public static final String RAN_CODRANGO 			= "CODRANGO";
	public static final String RAN_DESDE 				= "DESDE";
	public static final String RAN_HASTA 				= "HASTA";
	public static final String RAN_VALOR 				= "VALOR";
	public static final String RAN_VALORRESTA 			= "VALORRESTA";
	public static final String RAN_FECHA_ACTUALIZACION	= "FECHA_ACTUALIZACION";

	public static final String TABLE_SCM_ORDENES_TRABAJO = "SCM_ORDENES_TRABAJO";
	public static final String OT_RUTA				  	= "RUTA";
	public static final String OT_ORDEN_ID			  	= "ORDEN_ID";
	public static final String OT_ESTADO				= "ESTADO";
	public static final String OT_NOMBRE				= "NOMBRE";
	public static final String OT_DIRECCION				= "DIRECCION";
	public static final String OT_GPS					= "GPS";
	public static final String OT_IMPRIMIR_FACTURA		= "IMPRIMIR_FACTURA";
	public static final String OT_EXIGIR_FOTO			= "EXIGIR_FOTO";
	public static final String OT_ORDEN_ID_RELACIONADO	= "ORDEN_ID_RELACIONADO";
	public static final String OT_FECHA_ACTUALIZACION	= "FECHA_ACTUALIZACION";
	public static final String OT_TIPO					= "TIPO";
	public static final String OT_RUTA_LECTURA			= "RUTA_LECTURA";

	public static final String TABLE_SFAC_GRUPOS_LECTURA = "FAC_GRUPOS_LECTURA";
	public static final String  GRL_IDGRUPOLECTURA        =  "IDGRUPOLECTURA";
	public static final String  GRL_IDTIPOLECTURA         =  "IDTIPOLECTURA";
	public static final String  GRL_IDTIPOLECTURARELAC    =  "IDTIPOLECTURARELAC";


	public static final String CREATE_TABLE_SCM_CAUSANOLECTURA = " CREATE TABLE " + TABLE_SCM_CAUSANOLECTURA + " ( "
			+ CNL_CODCAUSA	    + " text,"
			+ CNL_DESCRIPCION	+ " text,"
			+ CNL_SOLCONSUMO	+ " text,"
			+ CNL_TIPCODIGO	+ " text );";
	public static final String CREATE_TABLE_SCM_OBSERVACIONADICIONAL = "CREATE TABLE "+TABLE_SCM_OBSERVACIONADICIONAL+" ( "
			+ OBA_CODOBSADICIONAL + " text,"
			+ OBA_DESOBSADICIONAL + " text);";

	public static final String CREATE_TABLE_SCM_OBSCONSUMO = "CREATE TABLE "+TABLE_SCM_OBSCONSUMO+" ( "
			+ OBC_CODOBSERVACION	+	" text,"
			+ OBC_DESCRIPCION		+	" text,"
			+ OBC_SOLCONSUMO		+ 	" text,"
			+ OBC_TIPCODIGO			+ " text);";

	public static final String CREATE_TABLE_SCM_TIPOS_LECTURA = "CREATE TABLE "+TABLE_SCM_TIPOS_LECTURA+" ( "
			+ TIL_TIPO_LECTURA	   +  " text,"
			+ TIL_D_TIPO_LECTURA   +  " text);";

	public static final String CREATE_TABLE_SCM_ACCION_ANEXOS = "CREATE TABLE "+TABLE_SCM_ACCION_ANEXOS+" ( "
			+ ACA_NUMERO_REVISION  + " text,"
			+ ACA_NOMBRE_ARCHIVO 	+ " text,"
			+ ACA_TITULO		    + " text,"
			+ ACA_DESCRIPCION		+ " text,"
			+ ACA_RUTA		    	+ " integer);";

	public static final String CREATE_TABLE_SCM_ANEXOS = "CREATE TABLE " +TABLE_SCM_ANEXOS+" ( "
			+ ANE_RUTA				+ " integer,"
			+ ANE_ORDEN_ID			+ " text,"
			+ ANE_NOMBRE_ARCHIVO	+ " text,"
			+ ANE_TIPO				+ " text,"
			+ ANE_DESCRIPCION		+ " text,"
			+ ANE_FECHA_SISTEMA		+ " text);";

	public static final String CREATE_TABLE_SCM_ELEMENTOS_LECTURAS_DES = "CREATE TABLE "+TABLE_SCM_ELEMENTOS_LECTURAS_DES+" ( "
			+ ELD_RUTA				    +  " integer,  "
			+ ELD_ORDEN_ID			    +  " text,"
			+ ELD_TIPO			        +  " text,"
			+ ELD_SERIE			    	+  " text,"
			+ ELD_MARCA			    	+  " text,"
			+ ELD_TIPO_LECTURA		    +  " text,"
			+ ELD_FECHA_LECTURA	    	+  " text,"
			+ ELD_LECTURA_TOMADA	    +  " text,"
			+ ELD_OBSERVACION		    +  " text,"
			+ ELD_OBSERVACION_AD	    +  " text,"
			+ ELD_OBSERVACION_TEXTO    	+  " text,"
			+ ELD_INTENTOS			    +  " text,"
			+ ELD_CONSUMO			    +  " numeric(11,0),"
			+ ELD_SOLCONSUMO		    +  " text,"
			+ ELD_FECHA_DESCARGA	    +  " text,"
			+ ELD_ESTADO			    +  " text);";

	public static final String CREATE_TABLE_SCM_CLI_CONCEPTOS_DES  = "CREATE TABLE "+TABLE_SCM_CLI_CONCEPTOS_DES+" ( "
			+ CND_RUTA 			  +	 	" integer,"
			+ CND_ORDEN_ID		  +	 	" text,"
			+ CND_CODIGO_CONCEPTO +	 	" numeric(4,0),"
			+ CND_SALDOANTERIOR	  +	 	" text,"
			+ CND_CANTIDAD		  +	 	" text,"
			+ CND_FACTOR		  +	 	" text,"
			+ CND_TARIFA		  +	 	" text,"
			+ CND_VALOR			  +	 	" text,"
			+ CND_FECHA_DESCARGA  +	 	" text);";

	public static final String CREATE_TABLE_SCM_MULTITABLA = "CREATE TABLE "+TABLE_SCM_MULTITABLA+" ( "
			+ MTT_TABLA 			+ " text,"
			+ MTT_CODIGO 			+ " text,"
			+ MTT_DESCRIPCION		+ " text,"
			+ MTT_FILTRO			+ " text,"
			+ MTT_PARAMETROS		+ " text);";


	public static final String CREATE_TABLE_DETALLE_ORDEN_FACTURACION = "CREATE TABLE "+TABLE_DETALLE_ORDEN_FACTURACION+" ( "
			+  OSE_CODIGO				+ " text,"
			+  RUTA_ORDEN				+ " text,"
			+  ORDEN_ID 				+ " text,"
            +  SINCRONIZADO             + " integer);";

	public static final String CREATE_TABLE_FAC_IMPRESION = "CREATE TABLE "+TABLE_FAC_IMPRESION+" ( "
			+  IMP_RUTA					+ " text,"
			+  IMP_ORDEN_ID 			+ " text,"
			+  IMP_IDIMPRESION 			+ " text,"
			+  IMP_POSXY 				+ " text,"
			+  IMP_FUENTE 				+ " text,"
			+  IMP_JUSTIFICACION 		+ " text,"
			+  IMP_FUNCION 				+ " text,"
			+  IMP_PARAMETROS 			+ " TEXT,"
			+  IMP_VALOR 				+ " text,"
			+  IMP_FECHA_ACTUALIZACION	+ " text);";

	public static final String CREATE_TABLE_SCM_ELEMENTOS_LECTURA = "CREATE TABLE "+TABLE_SCM_ELEMENTOS_LECTURA+" ( "
			+   ELL_RUTA					+ " text,"
			+   ELL_ORDEN_ID				+ " text,"
			+   ELL_TIPO					+ " text,"
			+   ELL_ERIE					+ " text,"
			+   ELL_MARCA					+ " text,"
			+   ELL_MODELO					+ " text,"
			+   ELL_TIPO_LECTURA			+ " text,"
			+   ELL_ENTEROS					+ " numeric(1,0),"
			+   ELL_DECIMALES				+ " numeric(1,0),"
			+   ELL_FACTOR_MULTIPLICACION	+ " text,"
			+   ELL_FECHA_ANTERIOR			+ " text,"
			+   ELL_LECTURA_ANTERIOR		+ " text,"
			+   ELL_LIMITE_INFERIOR_CONSUMO	+ " text,"
			+   ELL_LIMITE_SUPERIOR_CONSUMO	+ " text,"
			+   ELL_UBICACION				+ " numeric(3,0),"
			+   ELL_SECUENCIA				+ " numeric(4,0),"
			+   ELL_CONSUMO1				+ " numeric(11,0),"
			+   ELL_SOLCONSUMO1				+ " text,"
			+   ELL_CONSUMO2				+ " numeric(11,0),"
			+   ELL_SOLCONSUMO2				+ " text,"
			+   ELL_FECHA_LECTURA			+ " text,"
			+   ELL_LECTURA_TOMADA			+ " text,"
			+   ELL_OBSERVACION				+ " text,"
			+   ELL_OBSERVACION_AD			+ " text,"
			+   ELL_OBSERVACION_TEXTO		+ " text,"
			+   ELL_INTENTOS				+ " text,"
			+   ELL_CONSUMO					+ " numeric(11,0),"
			+   ELL_SOLCONSUMO				+ " text,"
			+   ELL_FECHA_ACTUALIZACION		+ " text);";

	public static final String CREATE_TABLE_FAC_LABORCONCEPTOS  = "CREATE TABLE "+TABLE_FAC_LABORCONCEPTOS+" ( "
			+  	LAC_RUTA				+ " text,"
			+  	LAC_ORDEN_ID			+ " text,"
			+  	LAC_IDCONCEPTO			+ " numeric(4,0),"
			+  	LAC_ORDEN				+ " numeric(2,0),"
			+  	LAC_SALDOANTERIOR		+ " text,"
			+  	LAC_IDGRUPOLECTURA		+ " text,"
			+  	LAC_CANTBASE			+ " text,"
			+  	LAC_CANTTIPORANGO		+ " text,"
			+  	LAC_CANTCODRANGO		+ " text,"
			+  	LAC_CANTRUTINA			+ " text,"
			+  	LAC_CANTIDAD			+ " text,"
			+  	LAC_FACTBASE			+ " text,"
			+  	LAC_FACTTIPORANGO		+ " text,"
			+  	LAC_FACTCODRANGO		+ " text,"
			+  	LAC_FACTRUTINA			+ " text,"
			+  	LAC_FACTOR				+ " text,"
			+  	LAC_TARIFABASE			+ " text,"
			+  	LAC_TARIFATIPORANGO		+ " text,"
			+  	LAC_TARIFACODRANGO		+ " text,"
			+  	LAC_TARIFARUTINA		+ " text,"
			+  	LAC_TARIFA				+ " text,"
			+  	LAC_VALORBASE			+ " text,"
			+  	LAC_VALORTIPORANGO		+ " text,"
			+  	LAC_VALORCODRANGO		+ " text,"
			+  	LAC_VALORRUTINA			+ " text,"
			+  	LAC_VALOR				+ " text,"
			+  	LAC_FECHA_ACTUALIZACION	+ " text);";

	public static final String CREATE_TABLE_FAC_RANGOS = "CREATE TABLE "+TABLE_FAC_RANGOS+" ( "
			+  RAN_RUTA 				+ " text,"
			+  RAN_TIPORANGO 			+ " text,"
			+  RAN_CODRANGO 			+ " text,"
			+  RAN_DESDE 				+ " real,"
			+  RAN_HASTA 				+ " real,"
			+  RAN_VALOR 				+ " real,"
			+  RAN_VALORRESTA 			+ " real,"
			+  RAN_FECHA_ACTUALIZACION	+ " text);";

	public static final String CREATE_TABLE_SCM_ORDENES_TRABAJO = "CREATE TABLE "+TABLE_SCM_ORDENES_TRABAJO+" ( "
			+  OT_RUTA				  	+ " text,"
			+  OT_ORDEN_ID			  	+ " text,"
			+  OT_ESTADO				+ " text,"
			+  OT_NOMBRE				+ " text,"
			+  OT_DIRECCION				+ " text,"
			+  OT_GPS					+ " text,"
			+  OT_IMPRIMIR_FACTURA		+ " text,"
			+  OT_EXIGIR_FOTO			+ " text,"
			+  OT_ORDEN_ID_RELACIONADO	+ " text,"
			+  OT_FECHA_ACTUALIZACION	+ " text,"
			+  OT_TIPO					+ " text,"
			+  OT_RUTA_LECTURA			+ " numeric(11,0));";

	public static final String CREATE_TABLE_SFAC_GRUPOS_LECTURA = "CREATE TABLE "+TABLE_SFAC_GRUPOS_LECTURA+" ( "
			+  GRL_IDGRUPOLECTURA        +  " text,"
			+  GRL_IDTIPOLECTURA         +  " text,"
			+  GRL_IDTIPOLECTURARELAC    +  " text);";

	//	Fin  de Facturacion en Sitio
	public static final String CREATE_TABLE_USUARIO = "CREATE TABLE " + TABLE_USUARIO + " ("
			+ U_ID + " integer primary key autoincrement, "			
			+ U_CUADRILLA + " numeric (8,0) UNIQUE, "
			+ U_CEDULA + " numeric (8,0), "
			+ U_TIPO + " integer, "
			+ U_NOMBRE + " text, "
			+ U_FECHA_CREACION + " text, "
			+ U_FECHA_LOGUEO + " text );";

	public static final String CREATE_TABLE_ORDEN = "CREATE TABLE " + TABLE_ORDEN + " ("
			+ OS_ID + " integer primary key autoincrement, "			
			+ OS_OSE_CODIGO + " numeric (8,0) UNIQUE, "
			+ OS_OSE_PRECARGA + " numeric (8,0), "
			+ OS_OSE_TIP_ORDEN + " integer, "
			+ OS_CLI_CONTRATO + " numeric (8,0), "
			+ OS_CLI_NOMBRE + " text, "
			+ OS_CIU_NOMBRE + " text, "
			+ OS_DIRECCION1 + " text, "
			+ OS_DIRECCION2 + " text, "
			+ OS_PRODUCTO + " text, "
			+ OS_BARRIO + " text, "
			+ OS_CICLO + " integer, "
			+ OS_RUTA + " integer, "
			+ OS_RUTA_CONS + " numeric (8,0), "
			+ OS_ELEMENTO + " text, "
			+ OS_INFO_ADD + " text, "		
			+ OS_ESTADO + " integer, "
			+ OS_OBS + " text, "
			+ OS_ANOMALIA + " text, "
			+ OS_FECHA_ANOMALIA + " text, "
			+ OS_REG_FOTO + " integer, "
			+ OS_TITULAR + " text, "
			+ OS_FECHA_PAGO + " text, "
			+ OS_ENTIDAD_RECAUDO + " text, "
			+ OS_PROPIETARIO + " text, "
			+ OS_DATAFONO + " text, "
			+ OS_CONSUMO + " text, "
			+ OS_LECTURA_ANTERIOR + " integer, "
			+ OS_CONSUMO_PROMEDIO +  " integer, "
			+ OS_LECTURA_ACTUAL  +  " integer, "	
			+ OS_CANTIDAD_DIGITOS + " text, "
			+ OS_TIPO_PRODUCTO + " text, "
			+ OS_CUADRILLA + " integer, "
			+ OS_SINCRONIZADO + " integer, "
            + OS_ESTADO_FENS + " text, "
			+ " FOREIGN KEY(" + OS_CUADRILLA + ") REFERENCES " + TABLE_USUARIO + " ("+ U_CUADRILLA + "));";


	public static final String CREATE_TABLE_STOCK = "CREATE TABLE " + TABLE_STOCK + " ("
			+ SC_ID + " integer primary key autoincrement, "			
			+ SC_REC_CODIGO + " numeric (8,0), "
			+ SC_REC_NOMBRE + " text, "
			+ SC_REC_SERIE + " text, "
			+ SC_REC_CANTIDAD + " integer, "
			+ SC_REC_SERIADO + " integer, "
			+ SC_REC_UNIDAD + " text, "
			+ SC_REC_PREFIJO + " text, "
			+ SC_REC_CUADRILLA + " integer, "
			+ " FOREIGN KEY(" + SC_REC_CUADRILLA + ") REFERENCES " + TABLE_ORDEN + " (" + U_CUADRILLA + "));";


	public static final String CREATE_TABLE_MATERIAL_INSTALADO = "CREATE TABLE " + TABLE_MATERIAL_INSTALADO + " ("
			+ MI_ID + " integer primary key autoincrement, "			
			+ MI_OSE_CODIGO + " numeric (8,0), "
			+ MI_REC_CODIGO + " numeric (8,0), "
			+ MI_REC_NOMBRE + " text, "
			+ MI_REC_CANTIDAD + " integer, "
			+ MI_REC_SERIE + " text, "
			+ MI_PREFIJO + " text, "
			+ MI_FECHA_CREACION + " text, "
			+ MI_TIPO + " integer, "
			+ MI_BODEGA + " integer, "
			+ MI_SINCRONIZADO + " integer, "
			+ " FOREIGN KEY(" + MI_OSE_CODIGO + ") REFERENCES " + TABLE_ORDEN + " (" + OS_OSE_CODIGO + ")  ON DELETE CASCADE );";


	public static final String CREATE_TABLE_EJECUCION= "CREATE TABLE " + TABLE_EJECUCION + " ("
			+ EO_ID + " integer primary key autoincrement, "			
			+ EO_OSE_CODIGO + " numeric (8,0) UNIQUE, "
			+ EO_LECTURA + " text, "
			+ EO_CODIGO_NO_LECTURA + " integer, "
			+ EO_CODIGO_OBS_LECTURA + " integer, "
			+ EO_OBS_NO_LECTURA + " text, "
			+ EO_INDICADOR_LECTURA + " integer, "
			+ EO_FECHA_CREACION + " text, "
			+ EO_FECHA_ACTUALIZACION + " text, "
			+ EO_CRITICA + " integer, "
			+ EO_INTENTOS + " integer, "
			+ EO_CONSUMO + " integer, "
			+ EO_ENCONTRO_MEDIDOR + " integer, "
			+ EO_MEDIDOR_CORRECTO + " integer, "
			+ EO_ACTIVIDAD + " integer, "
			+ EO_SELLOS_INSTALADOS + " text, "
			+ EO_SERIE_MEDIDOR_ENCONTRADO + " text, "
			+ EO_RETIRO_ACOMETIDA + " integer, "
			+ EO_DATOS_RETIRO_ACOMETIDA + " text, "
			+ EO_CENSO_CARGA + " integer, "
			+ EO_RECONEXION_NO_AUTORIZADA + " numeric (8,0), "
			+ EO_MOTIVO_EJECUCION + " text, "			
			+ EO_LATITUD  + " text, "
			+ EO_LONGITUD  + " text, "
			+ EO_SINCRONIZADO + " integer, "
			+ " FOREIGN KEY(" + EO_OSE_CODIGO + ") REFERENCES " + TABLE_ORDEN + " ("+ OS_OSE_CODIGO + ") ON DELETE CASCADE );";

	public static final String CREATE_TABLE_OBSERVACION = "CREATE TABLE " + TABLE_OBSERVACION + " ("
			+ OB_ID + " text, "
			+ OB_TIP_CODIGO + " numeric (8,0) UNIQUE, "
			+ OB_SOLCONSUMO + " text, "
			+ OB_TIP_NOMBRE + " text);";

	public static final String CREATE_TABLE_FOTO= "CREATE TABLE " + TABLE_FOTO + " ("
			+ FT_ID + " integer primary key autoincrement, "			
			+ FT_OSE_CODIGO + " numeric (8,0), "
			+ FT_SINCRONIZADO + " integer, "
			+ FT_FECHA + " text, "			
			+ FT_FOTO_URL + " text, "
			+ " FOREIGN KEY(" + FT_OSE_CODIGO + ") REFERENCES " + TABLE_ORDEN + " ("+ OS_OSE_CODIGO + "));";

	public static final String CREATE_TABLE_TERMINAL = "CREATE TABLE " + TABLE_TERMINAL + " ("
			+ TE_ID + " integer primary key autoincrement, "			
			+ TE_TERMINAL + " numeric (8,0) UNIQUE, "
			+ TE_FECHA + " text);";

	public static final String CREATE_TABLE_GPS = "CREATE TABLE " + TABLE_GPS + " ("
			+ GP_ID + " integer primary key autoincrement, "
			+ GP_TERMINAL + " numeric (8,0), "
			+ GP_CUADRILLA + " numeric (8,0), "			
			+ GP_LATITUD + " text, "
			+ GP_LONGITUD + " text, "
			+ GP_ALTITUD + " text, "
			+ GP_VELOCIDAD + " ntext, "
			+ GP_FECHA + " text, "
			+ GP_VEHICULO + " text, "			
			+ GP_ESTADO + " integer, "
			+ GP_ORDEN + " numeric (8,0), "
			+ GP_BATERIA + " text, "
			+ GP_SINCRONIZADO + " integer);";

	public static final String CREATE_TABLE_ASISTENCIA = "CREATE TABLE " + TABLE_ASISTENCIA + " ("
			+ ASI_FECHA + " numeric (8,0), "
			+ ASI_PER_CODIGO + " numeric (8,0), "
			+ ASI_SUPERVISOR + " numeric (8,0), "
			+ ASI_ESTADO + " integer, "
			+ ASI_TIPO + " integer, "
			+ ASI_FECHA_CREACION + " text, "
			+ ASI_FECHA_ACTUALIZACION + " text, "
			+ ASI_OBSERVACION + " text, "
			+ ASI_SINCRONIZADO + " integer, "
			+ " PRIMARY KEY ("+ ASI_FECHA_CREACION + "," + ASI_SUPERVISOR + "," + ASI_PER_CODIGO  + " ),"
			+ " FOREIGN KEY(" + ASI_SUPERVISOR + ") REFERENCES " + TABLE_USUARIO + " (" + U_CUADRILLA + "));";

	public static final String CREATE_TABLE_SUPERVISOR_LECTOR = "CREATE TABLE " + TABLE_SUPERVISOR_LECTOR + " ("
			+ SUP_SUPER_PER_CODIGO  + " numeric (8,0), "
			+ SUP_LECT_PER_CODIGO  + " numeric (8,0), "
			+ SUP_LECT_PER_NOMBRE  + " text, "
			+ SUP_VIGENTE  + " integer, "
			+ SUP_FECHA_CREACION  + " text, "
			+ SUP_FECHA_ACTUALIZACION  + " text," +
			" PRIMARY KEY ("+ SUP_SUPER_PER_CODIGO + "," + SUP_LECT_PER_CODIGO + " ));";


	public static final String TABLE_INSPECCIONES_TIPOFORMATO ="inspecciones_tipoformato";
	public static final String ITF_ID = "id";
	public static final String ITF_FECHA_CREACION = "fecha_creacion";
	public static final String ITF_HORA_CREACION = "hora_creacion";
	public static final String ITF_NOMBRE = "nombre";
	public static final String ITF_DESCRIPCION = "descripcion";


	public static final String CREATE_TABLE_INSPECCIONES_TIPOFORMATO= "CREATE TABLE " + TABLE_INSPECCIONES_TIPOFORMATO + " ("
			+ ITF_ID  + " numeric (8,0), "
			+ ITF_FECHA_CREACION  + " text, "
			+ ITF_HORA_CREACION  + " text, "
			+ ITF_NOMBRE  + " text, "
			+ ITF_DESCRIPCION  + " text, "
			+ " PRIMARY KEY ("+ ITF_ID +"));";

	public static final String TABLE_INSPECCIONES_MODULOFORMATO ="inspecciones_moduloformato";
	public static final String IMF_ID = "id";
	public static final String IMF_FECHA_CREACION = "fecha_creacion";
	public static final String IMF_HORA_CREACION = "hora_creacion";
	public static final String IMF_NOMBRE = "nombre";
	public static final String IMF_CODIGO = "codigo";
	public static final String IMF_NUMERO_ORDEN = "numero_orden";
	public static final String IMF_TIPO_FORMATO_ID = "tipo_formato_id";

	public static final String CREATE_TABLE_INSPECCIONES_MODULOFORMATO= "CREATE TABLE " + TABLE_INSPECCIONES_MODULOFORMATO + " ("
			+ IMF_ID  + " numeric (8,0), "
			+ IMF_FECHA_CREACION  + " text, "
			+ IMF_HORA_CREACION  + " text, "
			+ IMF_NOMBRE  + " text, "
			+ IMF_CODIGO  + " integer, "
			+ IMF_NUMERO_ORDEN  + " integer, "
			+ IMF_TIPO_FORMATO_ID  + " integer, "
			+ " PRIMARY KEY ("+ IMF_ID + " ),"
			+ " FOREIGN KEY(" + IMF_TIPO_FORMATO_ID + ") REFERENCES " + TABLE_INSPECCIONES_TIPOFORMATO + " (" + ITF_ID + "));";

	public static final String TABLE_INSPECCIONES_TIPOCAMPO ="inspecciones_tipocampo";
	public static final String ITC_ID = "id";
	public static final String ITC_FECHA_CREACION = "fecha_creacion";
	public static final String ITC_HORA_CREACION = "hora_creacion";
	public static final String ITC_DESCRIPCION = "descripcion";

	public static final String CREATE_TABLE_INSPECCIONES_TIPOCAMPO= "CREATE TABLE " + TABLE_INSPECCIONES_TIPOCAMPO + " ("
			+ ITC_ID  + " numeric (8,0), "
			+ ITC_FECHA_CREACION  + " text, "
			+ ITC_HORA_CREACION  + " text, "
			+ ITC_DESCRIPCION  + " text, "
			+ " PRIMARY KEY ("+ ITC_ID +"));";

	public static final String TABLE_INSPECCIONES_CAMPOFORMATO ="inspecciones_campoformato";
	public static final String ICF_ID = "id";
	public static final String ICF_FECHA_CREACION = "fecha_creacion";
	public static final String ICF_HORA_CREACION = "hora_creacion";
	public static final String ICF_NOMBRE = "nombre";
	public static final String ICF_CODIGO = "codigo";
	public static final String ICF_NUMERO_ORDEN = "numero_orden";
	public static final String ICF_TABLA_REFERENCIA= "tabla_referencia";
    public static final String ICF_DESCRIPCION = "descripcion";
    public static final String ICF_PARENT_ID = "parent_id";
    public static final String ICF_LEVEL = "level";
	public static final String ICF_MODULO_FORMATO_ID = "modulo_formato_id";
	public static final String ICF_TIPO_CAMPO_ID = "tipo_campo_id";

	public static final String CREATE_TABLE_INSPECCIONES_CAMPOFORMATO= "CREATE TABLE " + TABLE_INSPECCIONES_CAMPOFORMATO + " ("
			+ ICF_ID  + " numeric (8,0), "
			+ ICF_FECHA_CREACION  + " text, "
			+ ICF_HORA_CREACION  + " text, "
			+ ICF_NOMBRE  + " text, "
			+ ICF_CODIGO  + " integer, "
			+ ICF_NUMERO_ORDEN  + " integer, "
			+ ICF_TABLA_REFERENCIA  + " text, "
            + ICF_DESCRIPCION + " text,"
            + ICF_PARENT_ID  + " integer, "
			+ ICF_LEVEL  + " integer, "
            + ICF_MODULO_FORMATO_ID  + " integer, "
			+ ICF_TIPO_CAMPO_ID  + " integer, "
			+ " PRIMARY KEY ("+ ICF_ID + " ),"
			+ " FOREIGN KEY(" + ICF_MODULO_FORMATO_ID + ") REFERENCES " + TABLE_INSPECCIONES_MODULOFORMATO + " (" + IMF_ID + "),"
			+ " FOREIGN KEY(" + ICF_TIPO_CAMPO_ID + ") REFERENCES " + TABLE_INSPECCIONES_TIPOCAMPO + " (" + ITC_ID + "));";


	public static final String TABLE_INSPECCIONES_EJECUCIONFORMATO ="inspecciones_ejecucionformato";
	public static final String IEF_ID = "id";
	public static final String IEF_FECHA_CREACION = "fecha_creacion";
	public static final String IEF_HORA_CREACION = "hora_creacion";
	public static final String IEF_ESTADO = "estado";
	public static final String IEF_CALIFICACION = "calificacion";
	public static final String IEF_TIPO_FORMATO_ID = "tipo_formato_id";

	public static final String CREATE_TABLE_INSPECCIONES_EJECUCIONFORMATO= "CREATE TABLE " + TABLE_INSPECCIONES_EJECUCIONFORMATO + " ("
			+ IEF_ID  + " numeric (8,0), "
			+ IEF_FECHA_CREACION  + " text, "
			+ IEF_HORA_CREACION  + " text, "
			+ IEF_ESTADO  + " integer, "
			+ IEF_CALIFICACION  + " integer, "
			+ IEF_TIPO_FORMATO_ID  + " integer, "
			+ " PRIMARY KEY ("+ ITC_ID +"),"
			+ " FOREIGN KEY(" + IEF_TIPO_FORMATO_ID + ") REFERENCES " + TABLE_INSPECCIONES_TIPOFORMATO + " (" + ITF_ID + "));";


	public static final String TABLE_INSPECCIONES_REGISTROEJECUCION ="inspecciones_registroejecucion";
	public static final String IRE_ID = "id";
	public static final String IRE_FECHA_CREACION = "fecha_creacion";
	public static final String IRE_HORA_CREACION = "hora_creacion";
	public static final String IRE_CAMPO_FORMATO_ID = "campo_formato_id";
	public static final String IRE_CREADO_POR_ID = "creado_por_id";
	public static final String IRE_EJECUCION_FORMATO_ID = "ejecucion_formato_id";
	public static final String IRE_MODIFICADO_POR_ID = "modificado_por_id";

	public static final String CREATE_TABLE_INSPECCIONES_REGISTROEJECUCION = "CREATE TABLE " + TABLE_INSPECCIONES_REGISTROEJECUCION + " ("
			+ IRE_ID  + " integer primary key autoincrement, "
			+ IRE_FECHA_CREACION  + " text, "
			+ IRE_HORA_CREACION  + " text, "
			+ IRE_CAMPO_FORMATO_ID  + " integer, "
			+ IRE_CREADO_POR_ID  + " integer, "
			+ IRE_EJECUCION_FORMATO_ID  + " integer, "
			+ IRE_MODIFICADO_POR_ID  + " integer, "
			+ " FOREIGN KEY(" + IRE_CAMPO_FORMATO_ID + ") REFERENCES " + TABLE_INSPECCIONES_CAMPOFORMATO + " (" + ICF_ID + "),"
			+ " FOREIGN KEY(" + IRE_EJECUCION_FORMATO_ID + ") REFERENCES " + TABLE_INSPECCIONES_EJECUCIONFORMATO + " (" + IEF_ID + "));";


	public static final String TABLE_INSPECCIONES_RESPUESTAABIERTA ="inspecciones_respuestaabierta";
	public static final String IRA_ID = "id";
	public static final String IRA_FECHA_CREACION = "fecha_creacion";
	public static final String IRA_HORA_CREACION = "hora_creacion";
	public static final String IRA_RESULTADO = "resultado";
	public static final String IRA_CREADO_POR_ID = "creado_por_id";
	public static final String IRA_REGISTRO_EJECUCION_ID = "ejecucion_formato_id";
	public static final String IRA_MODIFICADO_POR_ID = "modificado_por_id";

	public static final String CREATE_TABLE_INSPECCIONES_RESPUESTAABIERTA = "CREATE TABLE " + TABLE_INSPECCIONES_RESPUESTAABIERTA + " ("
			+ IRA_ID  + " integer primary key autoincrement, "
			+ IRA_FECHA_CREACION  + " text, "
			+ IRA_HORA_CREACION  + " text, "
			+ IRA_RESULTADO  + " text, "
			+ IRA_CREADO_POR_ID  + " integer, "
			+ IRA_REGISTRO_EJECUCION_ID  + " integer, "
			+ IRA_MODIFICADO_POR_ID  + " integer, "
			+ " FOREIGN KEY(" + IRA_REGISTRO_EJECUCION_ID + ") REFERENCES " + TABLE_INSPECCIONES_REGISTROEJECUCION + " (" + IRE_ID + "));";

	public static final String TABLE_INSPECCIONES_RESPUESTAFECHA ="inspecciones_respuestafecha";
	public static final String IRF_ID = "id";
	public static final String IRF_FECHA_CREACION = "fecha_creacion";
	public static final String IRF_HORA_CREACION = "hora_creacion";
	public static final String IRF_RESULTADO = "resultado";
	public static final String IRF_CREADO_POR_ID = "creado_por_id";
	public static final String IRF_REGISTRO_EJECUCION_ID = "ejecucion_formato_id";
	public static final String IRF_MODIFICADO_POR_ID = "modificado_por_id";

	public static final String CREATE_TABLE_INSPECCIONES_RESPUESTAFECHA = "CREATE TABLE " + TABLE_INSPECCIONES_RESPUESTAFECHA + " ("
			+ IRF_ID  + " integer primary key autoincrement, "
			+ IRF_FECHA_CREACION  + " text, "
			+ IRF_HORA_CREACION  + " text, "
			+ IRF_RESULTADO  + " text, "
			+ IRF_REGISTRO_EJECUCION_ID  + " integer, "
			+ IRF_CREADO_POR_ID  + " integer, "
			+ IRF_MODIFICADO_POR_ID  + " integer, "
			+ " FOREIGN KEY(" + IRF_REGISTRO_EJECUCION_ID + ") REFERENCES " + TABLE_INSPECCIONES_REGISTROEJECUCION + " (" + IRE_ID + "));";


	public static final String TABLE_INSPECCIONES_RESPUESTANUMERICA ="inspecciones_respuestanumerica";
	public static final String IRN_ID = "id";
	public static final String IRN_FECHA_CREACION = "fecha_creacion";
	public static final String IRN_HORA_CREACION = "hora_creacion";
	public static final String IRN_RESULTADO = "resultado";
	public static final String IRN_CREADO_POR_ID = "creado_por_id";
	public static final String IRN_REGISTRO_EJECUCION_ID = "ejecucion_formato_id";
	public static final String IRN_MODIFICADO_POR_ID = "modificado_por_id";

	public static final String CREATE_TABLE_INSPECCIONES_RESPUESTANUMERICA = "CREATE TABLE " + TABLE_INSPECCIONES_RESPUESTANUMERICA + " ("
			+ IRN_ID  + " integer primary key autoincrement, "
			+ IRN_FECHA_CREACION  + " text, "
			+ IRN_HORA_CREACION  + " text, "
			+ IRN_RESULTADO  + " integer, "
			+ IRN_CREADO_POR_ID  + " integer, "
			+ IRN_REGISTRO_EJECUCION_ID  + " integer, "
			+ IRN_MODIFICADO_POR_ID  + " integer, "
			+ " FOREIGN KEY(" + IRN_REGISTRO_EJECUCION_ID + ") REFERENCES " + TABLE_INSPECCIONES_REGISTROEJECUCION + " (" + IRE_ID + "));";


	public static final String CREATE_TABLE_MEDIDOR_ENCONTRADO = "CREATE TABLE " + TABLE_MEDIDOR_ENCONTRADO + " ("
			+ MED_ID + " integer primary key autoincrement, "
			+ MED_CICLO + " integer, "
			+ MED_RUTA + " integer, "
			+ MED_CONS_RUTA_ANTERIOR + " numeric (8,0), "
			+ MED_CONS_RUTA_POSTERIOR + " numeric (8,0), "
			+ MED_DIRECCION + " text, "
			+ MED_MEDIDOR + " text, "
			+ MED_INDICADOR_MED + " integer, "
			+ MED_LECTURA + " text, "
			+ MED_SERVICIO + " integer, "
			+ MED_OBSERVACION + " text, "
			+ MED_PERSONA + " text, "
			+ MED_LATITUD + " text, "
			+ MED_LONGITUD + " text, "
			+ MED_FECHA + " text, "
			+ MED_SINCRONIZADO + " integer );";



	private DbHelper helper;
	private SQLiteDatabase db;

	/** 
	 * Constructor Manejador de Bd
	 * @param  context  Contexto
	 */
	public DataBaseManager(Context context){
		helper = new DbHelper(context);
		db = helper.getWritableDatabase();
	} //Cierre Constructor

	public void open() {
		db = helper.getWritableDatabase();
	}

	public void begin() {
		db.beginTransaction();
	}

	public void end() {
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	public void close(){
		helper.close();
	}

	public SQLiteDatabase getBD(){
		return db;
	}

	/**
	 * Contenedor de valores Ingreso Datos
	 * @param ose_codigo
	 * @param ose_precarga
	 * @param tip_orden
	 * @param cli_contrato
	 * @param cli_nombre
	 * @param ciu_nombre
	 * @param direccion1
	 * @param direccion2
	 * @param producto
	 * @param barrio
	 * @param ciclo
	 * @param ruta
	 * @param ruta_cons
	 * @param estado
	 * @param obs
	 * @param cuadrilla
	 * @param elemento
	 * @param lectura_anterior
	 * @param consumo_promedio
	 * @param lectura_actual
	 * @param cantidad_digitos
	 * @param tipo_producto
	 * @param franja
	 * @param sincronizado
	 * @return
	 */
	private ContentValues generarContentValues(int ose_codigo,int ose_precarga,int tip_orden, int cli_contrato,String cli_nombre,String ciu_nombre,
			String direccion1, String direccion2, String producto,String barrio, int ciclo,int ruta,int ruta_cons,int estado,
			String obs,int cuadrilla,String elemento,int lectura_anterior, int consumo_promedio,int lectura_actual,String cantidad_digitos,String tipo_producto,String franja, int sincronizado, String estado_fens){
		ContentValues valores = new ContentValues();
		valores.put(OS_OSE_CODIGO, ose_codigo); 
		valores.put(OS_OSE_PRECARGA, ose_precarga); 
		valores.put(OS_OSE_TIP_ORDEN, tip_orden); 
		valores.put(OS_CLI_CONTRATO, cli_contrato);
		valores.put(OS_CLI_NOMBRE, cli_nombre);
		valores.put(OS_CIU_NOMBRE, ciu_nombre);
		valores.put(OS_DIRECCION1, direccion1);
		valores.put(OS_DIRECCION2, direccion2);
		valores.put(OS_PRODUCTO, producto);
		valores.put(OS_BARRIO, barrio);
		valores.put(OS_CICLO, ciclo);
		valores.put(OS_RUTA, ruta);
		valores.put(OS_RUTA_CONS, ruta_cons);
		valores.put(OS_ESTADO, estado);
		valores.put(OS_OBS, obs);
		valores.put(OS_CUADRILLA, cuadrilla);
		valores.put(OS_ELEMENTO,elemento);
		valores.put(OS_LECTURA_ANTERIOR,lectura_anterior);
		valores.put(OS_CONSUMO_PROMEDIO,consumo_promedio);	
		valores.put(OS_LECTURA_ACTUAL,lectura_actual);	
		valores.put(OS_CANTIDAD_DIGITOS,cantidad_digitos);	
		valores.put(OS_TIPO_PRODUCTO,tipo_producto);
		valores.put(OS_CONSUMO,franja);
		valores.put(OS_SINCRONIZADO, sincronizado);
		valores.put(OS_ESTADO_FENS, estado_fens);

		return valores;
	}// Cierre Contenedor de valores Ingreso Datos

	/** 
	 * Contenedor de valores Observaciones
	 * @param    tip_codigo
	 * @param    cti_codigo
	 * @param    tip_nombre
	 */
	private ContentValues generarContentValuesObservaciones(String cod_observacion, int tip_codigo,String cti_codigo,String tip_nombre){
		ContentValues valores = new ContentValues();
		valores.put(OB_ID, cod_observacion);
		valores.put(OB_TIP_CODIGO, tip_codigo);
		valores.put(OB_SOLCONSUMO, cti_codigo);
		valores.put(OB_TIP_NOMBRE, tip_nombre);

		return valores;
	}// Cierre Contenedor de valores Observaciones


	/** 
	 * Contenedor de valores Actualizacion Datos
	 * @param   anomalia
	 * @param   fecha_anomalia
	 * @param   reg_foto
	 * @param   titular
	 * @param   fecha_pago
	 * @param   entidad_recaudo
	 * @param   propietario
	 * @param   datafono
	 * @param   obs
	 * @param   sincronizado
	 */
	private ContentValues generarContentValuesUpdate(String anomalia,String fecha_anomalia,int reg_foto,String titular,
			String fecha_pago, String entidad_recaudo, String propietario,String datafono, String obs, int sincronizado){
		ContentValues valores = new ContentValues();
		valores.put(OS_ANOMALIA, anomalia); 
		valores.put(OS_FECHA_ANOMALIA, fecha_anomalia); 
		valores.put(OS_REG_FOTO, reg_foto);
		valores.put(OS_TITULAR, titular);
		valores.put(OS_FECHA_PAGO, fecha_pago);
		valores.put(OS_ENTIDAD_RECAUDO, entidad_recaudo);
		valores.put(OS_PROPIETARIO, propietario);
		valores.put(OS_DATAFONO, datafono);
		valores.put(OS_OBS, obs);
		valores.put(OS_SINCRONIZADO, sincronizado);

		return valores;
	}// Cierre Contenedor de valores Actualizacion Datos


	/**
	 * Contenedor de valores Ingreso Datos
	 * @param codigo_cuadrilla
	 * @param cedula
	 * @param tipo
	 * @param nombre
	 * @return
	 */
	private ContentValues generarContentValuesUsuario(int codigo_cuadrilla,String cedula,int tipo,String nombre){
		ContentValues valores = new ContentValues();
		valores.put(U_CUADRILLA, codigo_cuadrilla); 
		valores.put(U_CEDULA, cedula); 
		valores.put(U_TIPO, tipo);
		valores.put(U_NOMBRE, nombre);
		valores.put(U_FECHA_CREACION, getDateTime());
		valores.put(U_FECHA_LOGUEO, getDateTime());

		return valores;
	}// Cierre Contenedor de valores Ingreso Datos

	/**
	 * Contenedor de valores Ingreso Datos
	 * @param rec_codigo
	 * @param rec_nombre
	 * @param rec_prefijo
	 * @param rec_serie
	 * @param rec_cantidad
	 * @param rec_seriado
	 * @param rec_unidad
	 * @param cuadrilla
	 * @return
	 */
	private ContentValues generarContentValuesStock(int rec_codigo,String rec_nombre,String rec_prefijo,String rec_serie,int rec_cantidad,
			String rec_seriado, String rec_unidad, int cuadrilla){
		ContentValues valores = new ContentValues();
		valores.put(SC_REC_CODIGO, rec_codigo); 
		valores.put(SC_REC_NOMBRE, rec_nombre); 
		valores.put(SC_REC_SERIE, rec_serie);
		valores.put(SC_REC_CANTIDAD, rec_cantidad);
		valores.put(SC_REC_SERIADO, rec_seriado);
		valores.put(SC_REC_UNIDAD, rec_unidad);
		valores.put(SC_REC_PREFIJO, rec_prefijo);
		valores.put(SC_REC_CUADRILLA, cuadrilla);

		return valores;
	}// Cierre Contenedor de valores Ingreso Datos


	/** 
	 * Obtener la fecha y hora actual
	 */ 
	public String getDateTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		Date date = new Date();
		return dateFormat.format(date);
	} // Cierre obtener Hora


	// Insertar En FacImpresion
	public void insertarDetalleOrdenes(String ose_codigo, String orden_id, String ruta_orden) {

		ContentValues valores = new ContentValues();
		valores.put("OSE_CODIGO", ose_codigo);
		valores.put("ORDEN_ID", orden_id);
		valores.put("RUTA_ORDEN", ruta_orden);
		db.insert(TABLE_DETALLE_ORDEN_FACTURACION, null, valores);
	}



	// Insertar En FacImpresion
	public void insertarFacImpresion(String ruta, String orden_id, String idimpresion, String posxy,
									 String fuente, String justificacion, String funcion, String parametros,
									 String valor, String fecha_actualizacion) {

		ContentValues valores = new ContentValues();
		valores.put("RUTA", ruta);
		valores.put("ORDEN_ID", orden_id);
		valores.put("IDIMPRESION", idimpresion);
		valores.put("POSXY", posxy);
		valores.put("FUENTE", fuente);
		valores.put("JUSTIFICACION", justificacion);
		valores.put("FUNCION", funcion);
		valores.put("PARAMETROS", parametros);
		valores.put("VALOR", valor);
		valores.put("FECHA_ACTUALIZACION", fecha_actualizacion);
		db.insert(TABLE_FAC_IMPRESION, null, valores);
//		String consulta= "INSERT INTO "+ TABLE_FAC_IMPRESION + " VALUES ("+ ruta +",'"+ orden_id +"',"+
//				"'"+ idimpresion +"', '"+ posxy +"', '"+ fuente +"'," +
//				"'"+ justificacion +"', '"+ funcion +"', '"+ parametros +"','"+ valor +"'," +
//				"'"+ fecha_actualizacion +"')";
//
//				db.execSQL(consulta);



	}


	// Insertar En FacLaborConceptos
	public void insertarFacLaborConceptos(String ruta, String orden_id, String idconcepto, String orden, String saldoanterior,
										  String idgrupolectura, String cantbase, String canttiporango, String cantcodrango, String cantrutina, String cantidad,
										  String factbase, String factiporango, String factcodrango, String factrutina, String factor, String tarifabase, String tarifatiporango,
										  String tarifarutina, String tarifa, String valorbase, String valortiporango, String valorcodrango, String valorrutina,
										  String valor, String fecha_actualizacion) {

		ContentValues valores = new ContentValues();
		valores.put("RUTA", ruta);
		valores.put("ORDEN_ID", orden_id);
		valores.put("IDCONCEPTO", idconcepto);
		valores.put("ORDEN", orden);
		valores.put("SALDOANTERIOR", saldoanterior);
		valores.put("IDGRUPOLECTURA", idgrupolectura);
		valores.put("CANTBASE", cantbase);
		valores.put("CANTTIPORANGO", canttiporango);
		valores.put("CANTCODRANGO", cantcodrango);
		valores.put("CANTRUTINA", cantrutina);
		valores.put("CANTIDAD", cantidad);
		valores.put("FACTBASE", factbase);
        valores.put("FACTTIPORANGO", factiporango);
		valores.put("FACTCODRANGO", factcodrango);
		valores.put("FACTRUTINA", factrutina);
		valores.put("FACTOR", factor);
		valores.put("TARIFABASE", tarifabase);
		valores.put("TARIFATIPORANGO", tarifatiporango);
		valores.put("TARIFARUTINA", tarifarutina);
		valores.put("TARIFA", tarifa);
		valores.put("VALORBASE", valorbase);
		valores.put("VALORTIPORANGO", valortiporango);
		valores.put("VALORCODRANGO", valorcodrango);
		valores.put("VALORRUTINA", valorrutina);
		valores.put("VALOR", valor);
		valores.put("FECHA_ACTUALIZACION", fecha_actualizacion);

		db.insert(TABLE_FAC_LABORCONCEPTOS, null, valores);

	}


	// Insertar En FacRangos
	public void insertarFacRangos(String ruta, String tipoRango, String codRango, String desde, String hasta,
								  String valor, String valorresta, String fecha_actualizacion) {

		ContentValues valores = new ContentValues();
		valores.put("RUTA", ruta);
		valores.put("TIPORANGO", tipoRango);
		valores.put("CODRANGO", codRango);
		valores.put("DESDE", desde);
		valores.put("HASTA", hasta);
		valores.put("VALOR", valor);
		valores.put("VALORRESTA", valorresta);
		valores.put("FECHA_ACTUALIZACION", fecha_actualizacion);

		db.insert(TABLE_FAC_RANGOS, null, valores);
	}


	// Insertar En FacRangos
	public void insertarElementosLectura(String ruta, String orden_id, String tipo, String serie, String marca, String modelo, String tipo_lectura,
										 String enteros, String decimales, String factor_multiplicacion, String fecha_anterior, String lectura_anterior, String limite_inferior_consumo,
										 String limite_superior_consumo, String ubicacion, String secuencua, String consumo1, String solconsumo1, String consumo2,
										 String solconsumo2, String fecha_lectura, String lectura_tomada, String observacion, String observacion_ad, String observacion_texto,
										 String intentos, String consumo, String solconsumo, String fecha_actualizacion) {

		ContentValues valores = new ContentValues();
		valores.put("RUTA", ruta);
		valores.put("ORDEN_ID", orden_id);
		valores.put("TIPO", tipo);
		valores.put("SERIE", serie);
		valores.put("MARCA", marca);
		valores.put("MODELO", modelo);
		valores.put("TIPO_LECTURA", tipo_lectura);
		valores.put("ENTEROS", enteros);
		valores.put("DECIMALES", decimales);
		valores.put("FACTOR_MULTIPLICACION", factor_multiplicacion);
		valores.put("FECHA_ANTERIOR", fecha_anterior);
		valores.put("LECTURA_ANTERIOR", lectura_anterior);
		valores.put("LIMITE_INFERIOR_CONSUMO", limite_inferior_consumo);
		valores.put("LIMITE_SUPERIOR_CONSUMO", limite_superior_consumo);
		valores.put("UBICACION", ubicacion);
		valores.put("SECUENCIA", secuencua);
		valores.put("CONSUMO1", consumo1);
		valores.put("SOLCONSUMO1", solconsumo1);
		valores.put("CONSUMO2", consumo2);
		valores.put("SOLCONSUMO2", solconsumo2);
		valores.put("FECHA_LECTURA", fecha_lectura);
		valores.put("LECTURA_TOMADA", lectura_tomada);
		valores.put("OBSERVACION", observacion);
		valores.put("OBSERVACION_AD", observacion_ad);
		valores.put("OBSERVACION_TEXTO", observacion_texto);
		valores.put("INTENTOS", intentos);
		valores.put("CONSUMO", consumo);
		valores.put("SOLCONSUMO", solconsumo);
		valores.put("FECHA_ACTUALIZACION", fecha_actualizacion);
		db.insert(TABLE_SCM_ELEMENTOS_LECTURA, null, valores);
//		String consulta= "INSERT INTO "+ TABLE_SCM_ELEMENTOS_LECTURA + " VALUES ("+ ruta +",'"+ orden_id +"',"+
//				"'"+ tipo +"', '"+ serie +"', '"+ marca +"'," +
//				"'"+ modelo +"', '"+ tipo_lectura +"', "+ enteros +","+ decimales +"," +
//				"'"+ factor_multiplicacion +"','"+ fecha_anterior + "'," +
//				"'"+ lectura_anterior +"', '"+ limite_inferior_consumo +"','"+ limite_superior_consumo +"', "+ ubicacion +", "+ secuencua +"," +
//				""+ consumo1 +", '"+ solconsumo1 +"', "+ consumo2 +",'"+ solconsumo2 +"','"+ fecha_lectura +"','"+ lectura_tomada +"','"+ observacion +"','"+ observacion_ad +"','"+observacion_texto+"'," +
//				"'"+ intentos+ "',"+ consumo +",'"+ solconsumo +"','"+ fecha_actualizacion +"')";

		//db.execSQL(consulta);

		//db.insert(TABLE_SCM_ELEMENTOS_LECTURA, null, valores);
	}

    public void insertarMultitabla(String tabla, String codigo, String descripcion, String filtro, String parametros) {

        ContentValues valores = new ContentValues();
        valores.put("TABLA", tabla);
        valores.put("CODIGO", codigo);
        valores.put("DESCRIPCION", descripcion);
        valores.put("FILTRO", filtro);
        valores.put("PARAMETROS", parametros);

        db.insert(TABLE_SCM_MULTITABLA, null, valores);
        //db.close();
    }

	// Insertar En FacRangos
	public void insertarOrdenesTrabajo(String ruta, String orden_id, String estado, String nombre, String direccion,
									   String gps, String imprimir_factura, String exigir_foto,
									   String orden_id_relacionado, String fecha_actualizacion,
									   String tipo, String ruta_lectura) {

		ContentValues valores = new ContentValues();
		valores.put("RUTA", ruta);
		valores.put("ORDEN_ID", orden_id);
		valores.put("ESTADO", estado);
		valores.put("NOMBRE", nombre);
		valores.put("DIRECCION", direccion);
		valores.put("GPS", gps);
		valores.put("IMPRIMIR_FACTURA", imprimir_factura);
		valores.put("EXIGIR_FOTO", exigir_foto);
		valores.put("ORDEN_ID_RELACIONADO", orden_id_relacionado);
		valores.put("FECHA_ACTUALIZACION", fecha_actualizacion);
		valores.put("TIPO", tipo);
		valores.put("RUTA_LECTURA", ruta_lectura);

		db.insert(TABLE_SCM_ORDENES_TRABAJO, null, valores);
		//db.close();
	}

	public void insertarObsConsumo(String cod_observacion, String descripcion, String sol_consumo, String tip_codigo) {

		ContentValues valores = new ContentValues();
		valores.put("CODOBSERVACION", cod_observacion);
		valores.put("DESCRIPCION", descripcion);
		valores.put("SOLCONSUMO", sol_consumo);
		valores.put("TIP_CODIGO", tip_codigo);

		db.insert(TABLE_SCM_OBSCONSUMO, null, valores);
	}

	public void insertarObsNoLectura(String cod_observacion, String descripcion, String sol_consumo, String tip_codigo) {

		ContentValues valores = new ContentValues();
		valores.put("CODCAUSA", cod_observacion);
		valores.put("DESCRIPCION", descripcion);
		valores.put("SOLCONSUMO", sol_consumo);
		valores.put("TIP_CODIGO", tip_codigo);

		db.insert(TABLE_SCM_CAUSANOLECTURA, null, valores);
	}



	/** 
	 * Insertar una orden a la BD
	 * @param   ose_codigo
	 * @param   ose_precarga
	 * @param  cli_contrato 
	 * @param  cli_nombre 
	 * @param  ciu_nombre 
	 * @param  direccion1 
	 * @param  direccion2 
	 * @param  producto 
	 * @param  barrio 
	 * @param  ciclo 
	 * @param  ruta 
	 * @param  ruta_cons 
	 * @param  estado 
	 * @param  obs 
	 * @param  sincronizado 
	 */
	public void insertar_orden(int ose_codigo,int ose_precarga,int ose_tip_orden, int cli_contrato,String cli_nombre,String ciu_nombre,
			String direccion1, String direccion2, String producto,String barrio, int ciclo,int ruta,int ruta_cons,int estado,
			String obs, int cuadrilla,String elemento,int lectura_anterior, int consumo_promedio,int lectura_actual,
			String cantidad_digitos,String tipo_producto,String franja, int sincronizado, String estado_fens){

		db.insert(TABLE_ORDEN, null, generarContentValues(ose_codigo, ose_precarga, ose_tip_orden, cli_contrato, cli_nombre,
				ciu_nombre, direccion1, direccion2, producto, barrio, ciclo, ruta, ruta_cons, estado, obs, cuadrilla, elemento,
				lectura_anterior, consumo_promedio, lectura_actual, cantidad_digitos, tipo_producto, franja, sincronizado, estado_fens));

	} //Cierre Insertar una Orden

	/** 
	 * Insertar una orden a la BD
	 * @param   tip_codigo
	 * @param   cti_codigo
	 * @param   tip_nombre
	 */
	public void insertar_observacion(String cod_observacion, int tip_codigo, String cti_codigo,String tip_nombre){
		long retorno  = -5;
		String str = " da = "; 		
		retorno = db.insert(TABLE_OBSERVACION, null, generarContentValuesObservaciones(cod_observacion, tip_codigo,cti_codigo, tip_nombre));
		System.out.println("INSERTO OBS " + tip_nombre);
		str += " " + retorno;
	} //Cierre Insertar una Orden


	/** 
	 * Insertar usuario en la BD
	 * @param   codigo_cuadrilla
	 * @param   cedula
	 * @param   tipo
	 * @param   nombre
	 */
	public void insertar_usuario(int codigo_cuadrilla,String cedula,int tipo,String nombre){

		try {
			db.insert(TABLE_USUARIO, null, generarContentValuesUsuario(codigo_cuadrilla, cedula, tipo, nombre));
		}catch(Exception e){}
	} //Cierre Insertar usuario en la BD

	/** 
	 * Insertar una stock del cuadrillero
	 * @param   rec_codigo
	 * @param   rec_nombre
	 * @param   rec_prefijo
	 * @param   rec_serie
	 * @param   rec_cantidad
	 * @param   rec_seriado
	 * @param   rec_unidad
	 * @param   cuadrilla
	 */
	public void insertar_stock(int rec_codigo,String rec_nombre,String rec_prefijo,String rec_serie,int rec_cantidad,
			String rec_seriado, String rec_unidad, int cuadrilla){	

		db.insert(TABLE_STOCK, null, generarContentValuesStock(rec_codigo, rec_nombre, rec_prefijo, rec_serie,
				rec_cantidad, rec_seriado, rec_unidad, cuadrilla));

	} //Cierre Insertar una Orden

	/**
	 * Insertar una stock MASIVO por bluk del cuadrillero
	 * @param rs
	 * @param cuadrilla
	 */
	public void insertar_stock_masivo_bulk(ResultSet rs,int cuadrilla){

		try
		{
			int i_rec_codigo   = rs.findColumn("rec_codigo");
			int i_rec_nombre	= rs.findColumn("rec_nombre");
			int i_rec_prefijo	= rs.findColumn("dei_prefijo");
			int i_rec_serie    = rs.findColumn("dei_serie");
			int i_rec_cantidad    = rs.findColumn("dei_cantidad");
			int i_rec_seriado    = rs.findColumn("rec_seriado");
			int i_rec_unidad    = rs.findColumn("tip_nombre");
			int idx = 0;

			int rec_codigo = 0, rec_cantidad = 0;
			String rec_nombre = "", rec_prefijo = "", rec_serie = "", rec_seriado = "", rec_unidad = "";

			String sql = "INSERT INTO "+ TABLE_STOCK +" VALUES (?,?,?,?,?,?,?,?,?);";	
			SQLiteStatement statement = db.compileStatement(sql);
			db.beginTransaction();

			while(rs.next()) {

				rec_codigo = rs.getInt(i_rec_codigo);					
				rec_nombre = rs.getString(i_rec_nombre);
				rec_prefijo  = rs.getString (i_rec_prefijo);
				rec_serie  = rs.getString (i_rec_serie);
				rec_cantidad = rs.getInt(i_rec_cantidad);
				rec_seriado  = rs.getString (i_rec_seriado);
				rec_unidad  = rs.getString (i_rec_unidad);

				statement.clearBindings(); 
				statement.bindLong(1, idx);
				statement.bindLong(2, rec_codigo);
				statement.bindString(3, rec_nombre);
				statement.bindString(4, rec_serie);
				statement.bindLong(5, rec_cantidad);
				statement.bindString(6,rec_seriado);
				statement.bindString(7, rec_unidad);
				statement.bindString(8, rec_prefijo);
				statement.bindLong(9, cuadrilla);

				statement.execute();
				idx++;

				System.out.println( "Inserto " + idx );
			}

			db.setTransactionSuccessful();	
			db.endTransaction();
		}
		catch(Exception e){
			System.out.println( "ERROR TRANSACTION " + e.getMessage() );
		}
	} //Cierre Insertar una Stock Masivo



	/** 
	 * Cargar Cursor Ordenes
	 *  @param  codigo_cuadrilla a quien pertenecen las ordenes
	 */ 
	public Cursor cargarCursorOrdenes(String codigo_cuadrilla,String busqueda, String estado, String orden){

		String[] columnas = new String[]{OS_DIRECCION1,OS_ELEMENTO,OS_BARRIO,OS_PRODUCTO,OS_CIU_NOMBRE, OS_OSE_CODIGO,OS_CLI_NOMBRE,OS_CLI_CONTRATO, OS_OSE_PRECARGA, OS_RUTA, OS_RUTA_CONS};
		return db.query(TABLE_ORDEN, columnas, OS_CUADRILLA + "=? AND " + OS_ESTADO + " =? AND ( " + OS_ELEMENTO + " LIKE? OR " + OS_CLI_CONTRATO + " LIKE? OR " + OS_DIRECCION1 + " LIKE? )" ,
				new String[]{codigo_cuadrilla,estado,"%"+ busqueda +"%","%"+ busqueda +"%","%"+ busqueda +"%"},null,null,OS_RUTA + " " + orden + ","+ OS_RUTA_CONS + " " + orden ,"100");
	}// Cierre Cargar Cursor Ordenes

	public Cursor cargarConteoOrdenes(){
		String sqlConsulta = "SELECT ose_codigo" +
				"  	FROM orden_servicio " ;
		return db.rawQuery(sqlConsulta, null);
	}// Cierre Cargar Cursor Ordenes

	public Cursor getCountFacImpresion(String ordenesCargadas, String CODDELTEC){
	    int tamano = 0;
		String sqlConsulta = " SELECT (SELECT count(*)" +
				" FROM DETALLE_ORDEN_FACTURACION AS d  " +
				" INNER JOIN fac_impresion fi ON d.orden_id = fi.orden_id " +
				" WHERE d.ose_codigo IN (" + ordenesCargadas + ")) fac_impresion, " +
				"(SELECT count(*) FROM fac_rangos  as fra ) rangos, " +
				"(SELECT count(*) FROM detalle_orden_facturacion as d " +
				" INNER JOIN fac_laborconceptos fla ON d.orden_id = fla.orden_id " +
				" WHERE d.ose_codigo in (" + ordenesCargadas + "))  fac_laborconceptos, " +
				" (SELECT count(*) FROM detalle_orden_facturacion as d " +
				" INNER JOIN scm_elementos_lecturas scele ON d.orden_id = scele.orden_id "+
				" WHERE d.ose_codigo in( " + ordenesCargadas + ")) scm_elementos_lectura, " +
				" (SELECT count(*) FROM detalle_orden_facturacion as d " +
				" INNER JOIN scm_ordenes_trabajo scmor ON d.orden_id = scmor.orden_id " +
				" WHERE d.ose_codigo in (" + ordenesCargadas + ") ) scm_trabajo";


		System.out.println(sqlConsulta);
		Cursor conteo = db.rawQuery(sqlConsulta, null);


		//System.out.println(tamano);
		return conteo;
	}// Cierre Cargar Cursor Ordenes


	public Cursor consultarUsuario(String codigo_cuadrilla){

		String[] columnas = new String[]{U_CUADRILLA, U_CEDULA, U_NOMBRE, U_TIPO};
		return db.query(TABLE_USUARIO, columnas, U_CUADRILLA  + "=?",new String[]{codigo_cuadrilla},null,null,null);
	}

	/** 
	 * Buscar una Orden
	 *  @param   cli_contrato a quien pertenecen las ordenes
	 */ 
	public Cursor buscarOrden(String cli_contrato){
		//query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy)
		String[] columnas = new String[]{OS_DIRECCION1,OS_CLI_NOMBRE,OS_BARRIO,OS_OSE_CODIGO};
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return db.query(TABLE_ORDEN, columnas,OS_CLI_CONTRATO+ "=?",new String[]{cli_contrato},null,null,null);
	}// Cierre Buscar una Orden


	/**
	 * Eliminar una  orden enviada al servidor
	 */
	public void eliminarOrdenesEnviadas(){
		//Nombre tabla, parametros y vector de estring		
		db.delete(TABLE_EJECUCION, EO_SINCRONIZADO + "=?", new String[] {"1"});
		db.delete(TABLE_ORDEN, OS_SINCRONIZADO + "=?", new String[] {"1"});
		db.delete(TABLE_MATERIAL_INSTALADO, MI_SINCRONIZADO + "=?", new String[]{"1"});

	} // Cierre Eliminar una  orden


	/**
	 * Eliminar una  orden enviada al servidor
	 */
	public void eliminarSCM(){
		//Nombre tabla, parametros y vector de estring
		db.delete(TABLE_DETALLE_ORDEN_FACTURACION, null, null);
		db.delete(TABLE_FAC_IMPRESION, null, null);
		db.delete(TABLE_FAC_LABORCONCEPTOS, null, null);
		db.delete(TABLE_FAC_RANGOS, null, null);
		db.delete(TABLE_SCM_ELEMENTOS_LECTURA, null, null);
		db.delete(TABLE_SCM_ORDENES_TRABAJO, null, null);
		db.delete(TABLE_SCM_OBSCONSUMO, null, null);
		db.delete(TABLE_SCM_CAUSANOLECTURA, null, null);
		db.delete(TABLE_OBSERVACION, null, null);
		//db.delete(TABLE_OBSERVACION, OB_ID + "> ?", new String[]{"0"});
	} // Cierre Eliminar una  orden


	/** 
	 * Eliminar una  orden enviada al servidor
	 *  @param   ose_codigo
	 */ 
	private void eliminarFotosEnviadas(String ose_codigo, String fecha){
		//Nombre tabla, parametros y vector de estring		
		db.delete(TABLE_FOTO, FT_OSE_CODIGO + "=? AND " + FT_FECHA + "=?", new String[]{ose_codigo, fecha});

	} // Cierre Eliminar una  orden


	/**
	 * Eliminar una  orden NO ENVIADA
	 */
	public void eliminarOrdenNoEnviada(){
		//Nombre tabla, parametros y vector de estring
		db.delete(TABLE_ORDEN, OS_ESTADO + "=? AND " + OS_SINCRONIZADO + "=?", new String[]{"1", "0"});
	} // Cierre Eliminar una  orden

	/**
	 * Eliminar una  stock Material
	 */
	public void eliminarStockMaterial(){//Modificacion eliminacion de materiales solo los que tengan mas que cero
		//Nombre tabla, parametros y vector de estring
		db.delete(TABLE_STOCK, SC_REC_CANTIDAD + " > 0", new String[]{});
	} // Cierre Eliminar una  orden

	public void eliminarMultiple(String nom1, String nom2){
		db.delete(TABLE_ORDEN, OS_OSE_CODIGO + "IN (?,?)", new String[]{nom1, nom2});
	}

	public void ModificarDatosOrden(String ose_codigo, String anomalia,String fecha_anomalia,int reg_foto,String titular,
			String fecha_pago, String entidad_recaudo, String propietario,String datafono, String obs,int sincronizado) {

		db.update(TABLE_ORDEN, generarContentValuesUpdate(anomalia, fecha_anomalia, reg_foto, titular, fecha_pago,
				entidad_recaudo, propietario, datafono, obs, sincronizado), OS_OSE_CODIGO + "=?", new String[]{ose_codigo});
	}

	/**
	 * Retorna las observaciones segun el cti_codigo enviado
	 // * @param cti_codigo
	 * @return Cursor con las Obs de TIPO
	 */
	/* public Cursor getObservaciones(String cti_codigo){

		String[] columnas = new String[]{OB_TIP_CODIGO, OB_TIP_NOMBRE};
		return db.query(TABLE_OBSERVACION, columnas, OB_CTI_CODIGO  + "=?",new String[]{cti_codigo},null,null,null);
	} */ //COMENTADO @ALEJO DURAN NO SE USA



	public boolean existeObsCargadas(){

		boolean exiteObs = false;
		String[] columnas = new String[]{OB_TIP_CODIGO};
		Cursor cursor = db.query(TABLE_OBSERVACION, columnas, null ,null, null, null, null, null);

		if (cursor.moveToFirst()) {
			exiteObs = true;
		}
		cursor.close();
		return exiteObs;
	}

	/**
	 * Cargar Cursor Ordenes
	 * @param ose_codigo
	 * @param tipo
	 * @return
	 */
	public Cursor cargarMaterialOrden(String ose_codigo, String tipo){

		//id,int rec_codigo,int ose_codigo, String rec_prefijo, String rec_serie,String rec_nombre, int tipo

		String[] columnas = new String[]{MI_ID,MI_REC_CODIGO,MI_OSE_CODIGO,MI_PREFIJO,MI_REC_SERIE, MI_REC_NOMBRE}; 
		return db.query(TABLE_MATERIAL_INSTALADO, columnas, MI_OSE_CODIGO + "=? AND " + MI_TIPO + "=?",new String[]{ose_codigo,tipo},null,null,null,null);
	}// Cierre Cargar Cursor Ordenes

	/**
	 * Getting all labels
	 * @param rec_codigo
	 * @return list of labels
	 */
	public List<String> getAllPrefijosSellos(int rec_codigo){
		List<String> labels = new ArrayList<String>();

		//String[] columnas = new String[]{SC_REC_PREFIJO};
		//db.query(TABLE_STOCK, columnas, SC_REC_CUADRILLA  + "=?",new String[]{cuadrilla},null,null,null);
		Cursor cursor = db.rawQuery(" SELECT " + SC_REC_PREFIJO + " FROM " + TABLE_STOCK + " WHERE " + SC_REC_CODIGO + "=" + rec_codigo + " AND " + SC_REC_NOMBRE + " like 'SELLO%' GROUP BY " + SC_REC_PREFIJO, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				labels.add(cursor.getString(cursor.getColumnIndex(SC_REC_PREFIJO)));
			} while (cursor.moveToNext());
		}

		// closing connection
		cursor.close();

		// returning lables
		return labels;
	}

	/** 
	 * EL material se encuentra en el stock y la cantidad es mayor que cero
	 *  @param   rec_serie
	 *  @param   rec_prefijo
	 */ 
	public boolean exiteMaterialDisponible(String rec_serie, String rec_prefijo){

		boolean exiteMaterialDisponible = false;

		String[] columnas = new String[]{SC_REC_SERIE};
		Cursor cursor = db.query(TABLE_STOCK, columnas, SC_REC_SERIE + "=? AND " + SC_REC_PREFIJO + "=? AND " + SC_REC_CANTIDAD + " > 0", new String[]{rec_serie, rec_prefijo}, null, null, null, null);

		if (cursor.moveToFirst()) {
			exiteMaterialDisponible = true;
		}
		cursor.close();
		return exiteMaterialDisponible;
	}// Cierre material disponible

	/**
	 * Registra un material en la tabla instalados y actualiza su cantidad en el stock
	 * @param ose_codigo
	 * @param rec_codigo
	 * @param rec_serie
	 * @param rec_cantidad
	 * @param rec_nombre
	 * @param cuadrilla
	 * @param rec_prefijo
	 * @param sincronizado
	 */
	public void instalarMaterial(int ose_codigo, int rec_codigo,String rec_serie,int rec_cantidad,String rec_nombre,
			int cuadrilla, String rec_prefijo,int sincronizado){

		int rowsAff = 0;
		//ACTUALIZO CANTIDAD STOCK A CERO
		rowsAff = db.update(TABLE_STOCK, generarContentValuesStockMaterial(0),  
				SC_REC_SERIE + "=? AND " + SC_REC_PREFIJO + "=?", new String[] {rec_serie,rec_prefijo});

		System.out.println( "ACTUALIZOSTOCK " + rowsAff );

		db.insert(TABLE_MATERIAL_INSTALADO, null, generarContentValuesInstalarMaterial(ose_codigo, rec_codigo, rec_serie,
				rec_cantidad, rec_nombre, 1, cuadrilla, rec_prefijo, sincronizado));

		System.out.println("INSTALO MATERIAL ");

	}// Cierre Instalacion de material

	/**
	 * Instala un acta
	 * @param ose_codigo
	 * @param rec_codigo
	 * @param rec_serie
	 * @param rec_cantidad
	 * @param rec_nombre
	 * @param cuadrilla
	 * @param rec_prefijo
	 * @param sincronizado
	 */
	public void instalarMaterialActa(int ose_codigo, int rec_codigo,String rec_serie,int rec_cantidad,String rec_nombre,
			int cuadrilla, String rec_prefijo,int sincronizado){

		int rowsAff = 0;
		//ACTUALIZO CANTIDAD STOCK A CERO
		rowsAff = db.update(TABLE_STOCK, generarContentValuesStockMaterial(0),  
				SC_REC_SERIE + "=? AND " + SC_REC_CODIGO + "=?", new String[] {rec_serie,String.valueOf(rec_codigo)});

		System.out.println( "ACTUALIZOSTOCK " + rowsAff );

		db.insert(TABLE_MATERIAL_INSTALADO, null, generarContentValuesInstalarMaterial(ose_codigo,rec_codigo, rec_serie,
				rec_cantidad, rec_nombre,1, cuadrilla,  rec_prefijo,  sincronizado));

		System.out.println("INSTALO MATERIAL ");

	}// Cierre Instalacion de material

	/**
	 * Registra un material en la tabla instalados y actualiza su cantidad en el stock
	 * @param ose_codigo
	 * @param rec_codigo
	 * @param rec_serie
	 * @param rec_cantidad
	 * @param rec_nombre
	 * @param cuadrilla
	 * @param rec_prefijo
	 * @param sincronizado
	 */
	public void instalarRetirarMaterial(int ose_codigo, int rec_codigo,String rec_serie,int rec_cantidad,String rec_nombre,
			int cuadrilla, String rec_prefijo,int sincronizado){

		db.insert(TABLE_MATERIAL_INSTALADO, null, generarContentValuesInstalarMaterial(ose_codigo, rec_codigo, rec_serie,
				rec_cantidad, rec_nombre, 0, cuadrilla, rec_prefijo, sincronizado));

		System.out.println("INSTALO MATERIAL ");

	}// Cierre Instalacion de material

	/** 
	 * Contenedor de valores Actualizacion Datos Stock
	 * @param   rec_cantidad cantidad de material
	 */
	private ContentValues generarContentValuesStockMaterial(int rec_cantidad){
		ContentValues valores = new ContentValues();
		valores.put(SC_REC_CANTIDAD, rec_cantidad); 

		return valores;
	}// Cierre Contenedor de valores Actualizacion Datos Stock

	/**
	 * Generador de valores para Instalar un material
	 * @param ose_codigo
	 * @param rec_codigo
	 * @param rec_serie
	 * @param rec_cantidad
	 * @param rec_nombre
	 * @param tipo
	 * @param cuadrilla
	 * @param rec_prefijo
	 * @param sincronizado
	 * @return Bolsa de Valores
	 */
	private ContentValues generarContentValuesInstalarMaterial(int ose_codigo, int rec_codigo,String rec_serie,
			int rec_cantidad,String rec_nombre,int tipo,int cuadrilla, String rec_prefijo,int sincronizado){
		ContentValues valores = new ContentValues();
		valores.put(MI_OSE_CODIGO, ose_codigo);
		valores.put(MI_REC_CODIGO, rec_codigo); 
		valores.put(MI_REC_NOMBRE, rec_nombre); 
		valores.put(MI_REC_CANTIDAD, rec_cantidad); 
		valores.put(MI_REC_SERIE, rec_serie); 
		valores.put(MI_PREFIJO, rec_prefijo); 
		valores.put(MI_FECHA_CREACION, getDateTime()); 
		valores.put(MI_TIPO, tipo); 
		valores.put(MI_BODEGA, cuadrilla); 
		valores.put(MI_SINCRONIZADO, sincronizado); 

		return valores;
	}// Cierre Contenedor de valores Actualizacion Datos Stock

	/**
	 * Consulta si existe lectura agregada a  la orden
	 * @param ose_codigo
	 * @return
	 */
	public boolean existeLecturaMotivo(String ose_codigo) {
		// TODO Auto-generated method stub
		boolean exiteLecturaMotivo = false;

		String[] columnas = new String[]{EO_OSE_CODIGO,EO_INDICADOR_LECTURA};
		Cursor cursor = db.query(TABLE_EJECUCION, columnas, EO_OSE_CODIGO + "=?" ,new String[]{ose_codigo},null,null,null,null);

		if (cursor.moveToFirst()) {			
			if(!cursor.isNull(1))			
				exiteLecturaMotivo = true;
		}	
		cursor.close();
		return exiteLecturaMotivo;
	}


	public boolean existeActividadIngresada(String ose_codigo) {
		// TODO Auto-generated method stub
		boolean exiteActividad = false;
		int actividad = 0;

		String[] columnas = new String[]{EO_OSE_CODIGO,EO_ACTIVIDAD};
		Cursor cursor = db.query(TABLE_EJECUCION, columnas, EO_OSE_CODIGO + "=?" ,new String[]{ose_codigo},null,null,null,null);

		if (cursor.moveToFirst()) {
			if(!cursor.isNull(1)){
				actividad = cursor.getInt(1);
				if(actividad > 0)
					exiteActividad = true;
			}
		}
		cursor.close();
		return exiteActividad;
	}

	/** 
	 * EL material se encuentra en el stock y la cantidad es mayor que cero
	 *  @param   rec_serie
	 *  @param   rec_codigo
	 */ 
	public boolean existeActaDisponible(String rec_serie, String rec_codigo){

		boolean exiteMaterialDisponible = false;

		String[] columnas = new String[]{SC_REC_SERIE};
		Cursor cursor = db.query(TABLE_STOCK, columnas, SC_REC_SERIE + "=? AND " + SC_REC_CODIGO + "=? AND " + SC_REC_CANTIDAD + " > 0" ,new String[]{rec_serie,rec_codigo},null,null,null,null);

		if (cursor.moveToFirst()) {
			exiteMaterialDisponible = true;
		}
		cursor.close();

		return exiteMaterialDisponible;
	}// Cierre material disponible

	/**
	 * 
	 * @param ose_codigo
	 * @param actividad
	 * @param codigo_no_lectura
	 * @param sellos
	 * @param retiro_acometida
	 * @param datos_retiro_acometida
	 * @param censo_carga
	 * @param reconexion_no_autorizada
	 * @param motivo_ejecucion
	 */
	public void finalizarOrden(String ose_codigo, int actividad, int codigo_no_lectura,String observacion_no_lectura, 
			String sellos,int retiro_acometida,String datos_retiro_acometida,String censo_carga,
			int reconexion_no_autorizada,String motivo_ejecucion, int codigo_obs_lectura){

		int rowsAff = 0;
		//ACTUALIZO CANTIDAD STOCK A CERO
		rowsAff = db.update(TABLE_EJECUCION, generarContentValuesUpdateFinalizarOrden(actividad, codigo_no_lectura, observacion_no_lectura,
						sellos, retiro_acometida, datos_retiro_acometida, censo_carga, reconexion_no_autorizada, motivo_ejecucion, codigo_obs_lectura),
				EO_OSE_CODIGO + "=?", new String[] {ose_codigo});
		System.out.println("GUARDO ORDEN FINALIZO " + rowsAff);

		rowsAff = db.update(TABLE_ORDEN, generarContentValuesUpdateEstadoOrdenOrden(27),  
				EO_OSE_CODIGO + "=?", new String[] {ose_codigo});
		System.out.println("CAMBIO ESTADO ORDEN FINALIZO " + rowsAff);
	}

	/**
	 * Generar Content Values Orden
	 * @param estado
	 * @return
	 */
	public ContentValues generarContentValuesUpdateEstadoOrdenOrden(int estado){

		ContentValues valores = new ContentValues();
		valores.put(OS_ESTADO, estado);
		valores.put(OS_SINCRONIZADO, 0);
		return valores;
	}

	/**
	 * 
	 * @param actividad
	 * @param codigo_no_lectura
	 * @param sellos
	 * @param retiro_acometida
	 * @param datos_retiro_acometida
	 * @param censo_carga
	 * @param reconexion_no_autorizada
	 * @param motivo_ejecucion
	 * @return
	 */
	private ContentValues generarContentValuesUpdateFinalizarOrden(int actividad, int codigo_no_lectura,String observacion_no_lectura,String sellos,int retiro_acometida,
                                                                   String datos_retiro_acometida,String censo_carga,int reconexion_no_autorizada,String motivo_ejecucion, int codigo_obs_lectura){
		ContentValues valores = new ContentValues();
		valores.put(EO_ACTIVIDAD, actividad); 
		valores.put(EO_CODIGO_NO_LECTURA, codigo_no_lectura); 
		valores.put(EO_OBS_NO_LECTURA, observacion_no_lectura); 
		valores.put(EO_SELLOS_INSTALADOS, sellos); 
		valores.put(EO_RETIRO_ACOMETIDA, retiro_acometida); 
		valores.put(EO_DATOS_RETIRO_ACOMETIDA, datos_retiro_acometida); 
		valores.put(EO_CENSO_CARGA, censo_carga); 
		valores.put(EO_RECONEXION_NO_AUTORIZADA, reconexion_no_autorizada); 
		valores.put(EO_MOTIVO_EJECUCION, motivo_ejecucion); 
		valores.put(EO_CODIGO_OBS_LECTURA, codigo_obs_lectura); 
		valores.put(EO_SINCRONIZADO, 0); 

		return valores;
	}

	/**
	 * Ingresa o Actualiza la actividad de la orden de servicio
	 * @param ose_codigo
	 * @param actividad
	 * @param codigo_no_lectura
	 * @param retiro_acometida
	 * @param datos_retiro_acometida
	 * @param motivo_ejecucion
	 * @param codigo_obs_lectura
	 */
	public void ingresarActualizarActividad(String ose_codigo, int actividad, int codigo_no_lectura,int retiro_acometida,
			String datos_retiro_acometida, String motivo_ejecucion, int codigo_obs_lectura){

		int rowsAff = 0;
		//ACTUALIZO CANTIDAD STOCK A CERO
		rowsAff = db.update(TABLE_EJECUCION, generarContentValuesUpdateActividadOrden(actividad, codigo_no_lectura,
						retiro_acometida, datos_retiro_acometida, motivo_ejecucion, codigo_obs_lectura),
				EO_OSE_CODIGO + "=?", new String[] {ose_codigo});
		System.out.println("ACTUALIZO ACTIVIDAD ORDEN " + rowsAff);

		if(rowsAff == 0){
			db.insert(TABLE_EJECUCION, null, generarContentValuesInstertarActividad(ose_codigo, actividad, codigo_no_lectura, 
					retiro_acometida, datos_retiro_acometida, motivo_ejecucion,codigo_obs_lectura));
			System.out.println( "INSERTO ACTIVIDAD ORDEN " );  
		}
	}

	/**
	 * Contender que acctualiza la actividad 
	 * @param actividad
	 * @param codigo_no_lectura
	 * @param retiro_acometida
	 * @param datos_retiro_acometida
	 * @param motivo_ejecucion
	 * @param codigo_obs_lectura
	 * @return
	 */
	private ContentValues generarContentValuesUpdateActividadOrden(int actividad, int codigo_no_lectura,
			int retiro_acometida,String datos_retiro_acometida, String motivo_ejecucion, int codigo_obs_lectura){
		ContentValues valores = new ContentValues();
		valores.put(EO_ACTIVIDAD, actividad); 
		valores.put(EO_CODIGO_NO_LECTURA, codigo_no_lectura); 
		valores.put(EO_RETIRO_ACOMETIDA, retiro_acometida); 
		valores.put(EO_DATOS_RETIRO_ACOMETIDA, datos_retiro_acometida); 
		valores.put(EO_MOTIVO_EJECUCION, motivo_ejecucion); 
		valores.put(EO_CODIGO_OBS_LECTURA, codigo_obs_lectura); 
		valores.put(EO_SINCRONIZADO, 0); 

		return valores;
	} 

	/**
	 * Contenedor de valores para insertar una actividad
	 * @param ose_codigo
	 * @param actividad
	 * @param codigo_no_lectura
	 * @param retiro_acometida
	 * @param datos_retiro_acometida
	 * @param motivo_ejecucion
	 * @param codigo_obs_lectura
	 * @return
	 */
	private ContentValues generarContentValuesInstertarActividad(String ose_codigo,int actividad, int codigo_no_lectura,
			int retiro_acometida, String datos_retiro_acometida, String motivo_ejecucion, int codigo_obs_lectura){
		ContentValues valores = new ContentValues();
		valores.put(EO_OSE_CODIGO, ose_codigo);
		valores.put(EO_ACTIVIDAD, actividad); 
		valores.put(EO_CODIGO_NO_LECTURA, codigo_no_lectura); 
		valores.put(EO_RETIRO_ACOMETIDA, retiro_acometida); 
		valores.put(EO_DATOS_RETIRO_ACOMETIDA, datos_retiro_acometida); 
		valores.put(EO_MOTIVO_EJECUCION, motivo_ejecucion); 
		valores.put(EO_CODIGO_OBS_LECTURA, codigo_obs_lectura); 
		valores.put(EO_SINCRONIZADO, 0); 

		return valores;
	}// Cierre Contenedor de valores INSERTAR Actividad

	/**
	 * Genera un arreglo de sellos 
	 * @param ose_codigo
	 * @return
	 */
	public String getArregloSellos(String ose_codigo){
		//id,int rec_codigo,int ose_codigo, String rec_prefijo, String rec_serie,String rec_nombre, int tipo
		String listSellosStr = "", rec_prefijo = "", rec_serie = "", tipoStr;
		Cursor cursor;
		int rec_codigo, tipo = 0;
		boolean primeraVez = true;
		String[] columnas = new String[]{MI_REC_CODIGO,MI_PREFIJO,MI_REC_SERIE, MI_TIPO}; 
		cursor = db.query(TABLE_MATERIAL_INSTALADO, columnas, MI_OSE_CODIGO + "=? AND " + SC_REC_NOMBRE + " LIKE ?", new String[]{ose_codigo, "%SELLO%"}, null, null, MI_TIPO + " DESC", null);

		if(cursor.moveToFirst()){
			do{
				rec_codigo = cursor.getInt(cursor.getColumnIndex(MI_REC_CODIGO));	
				tipo = cursor.getInt(cursor.getColumnIndex(MI_TIPO)); 
				rec_serie = cursor.getString(cursor.getColumnIndex(MI_REC_SERIE));
				rec_prefijo = cursor.getString(cursor.getColumnIndex(MI_PREFIJO));

				if (tipo == 1)
					tipoStr = "I:";
				else tipoStr = "R:";

				if (primeraVez){
					listSellosStr += "";
					primeraVez = false;
				}else listSellosStr += "|";

				listSellosStr += tipoStr + rec_codigo + ":" + rec_prefijo + ":" + rec_serie;

			}while(cursor.moveToNext());//accessing data upto last row from table
		}
		cursor.close();

		return listSellosStr;
	}// Cierre Cargar Cursor Ordenes

	/**
	 * Actualizar Envio orden
	 * @param ose_codigo
	 */
	public void actualizarEnvioOrden(String ose_codigo){

		db.update(TABLE_EJECUCION, generarCvUpdateOrdenEjecutadaEnviada(1), EO_OSE_CODIGO + "=?", new String[]{ose_codigo});
		db.update(TABLE_ORDEN, generarCvUpdateOrdenEnviada(1), OS_OSE_CODIGO + "=?", new String[]{ose_codigo});
		db.update(TABLE_MATERIAL_INSTALADO, generarCvUpdateMaterialEjecutadaEnviada(1), MI_OSE_CODIGO + "=?", new String[]{ose_codigo});
	}

	private ContentValues generarCvUpdateOrdenEnviada(int sincronizado){ 
		ContentValues valores = new ContentValues();
		valores.put(OS_SINCRONIZADO, sincronizado); 

		return valores;
	}

	private ContentValues generarCvUpdateOrdenEjecutadaEnviada(int sincronizado){ 
		ContentValues valores = new ContentValues();
		valores.put(EO_SINCRONIZADO, sincronizado); 

		return valores;
	}//

	private ContentValues generarCvUpdateMaterialEjecutadaEnviada(int sincronizado){ 
		ContentValues valores = new ContentValues();
		valores.put(MI_SINCRONIZADO, sincronizado); 

		return valores;
	}//MI_SINCRONIZADO

	/**
	 * Retorna Cursor con la lista de pendientes
	 * @return cursor con fotos pendientes por enviar
	 */
	public Cursor fotosPendintes() {
		String sqlConsulta = "SELECT * " +
				"  FROM "+ TABLE_FOTO + " " +
				"  WHERE " + FT_SINCRONIZADO + "=0";

		Cursor cursor = db.rawQuery(sqlConsulta, null);

		return cursor;
	}// Cierre material disponible

	/**
	 * Verifica si existe registro del ID terminal en la BD
	 * @return
	 */


	public int exiteRegistroTerminal() {
		int TerminalRegistrada = 0;
		String[] columnas = new String[]{TE_TERMINAL};
		Cursor cursor = db.query(TABLE_TERMINAL, columnas, null, null, null, null, null, null);

		if (cursor.moveToFirst()) {
			TerminalRegistrada = cursor.getInt(cursor.getColumnIndex(TE_TERMINAL));
		}
		return TerminalRegistrada;
	}

	/**
	 * Ingresa idTerminal a la BD
	 * @param idTerminal
	 */
	public void ingresarIdTerminal(String idTerminal){

		db.insert(TABLE_TERMINAL, null, generarCvInsertarTerminal(idTerminal));
		System.out.println("INSERTO ID TERMINAL " + idTerminal);
	}

	/**
	 * Genera bolsa para insertar ID terminal
	 * @param idTerminal
	 * @return
	 */
	private ContentValues generarCvInsertarTerminal(String idTerminal){
		ContentValues valores = new ContentValues();
		valores.put(TE_TERMINAL, idTerminal);
		valores.put(TE_FECHA, getDateTime()); 
		return valores;
	}

	public void insertarGps(String terminal,String cuadrilla,String latitud,
			String longitud, String altitud, String velocidad, String vehiculo,
			String estado, String orden, String bateria ){

		db.insert(TABLE_GPS, null, generarCvInsertarGps(terminal, cuadrilla, latitud,
				longitud, altitud, velocidad, vehiculo, estado, orden, bateria));
		System.out.println("INSERTO GPS ESTADO " + estado + " LATITUD: " + latitud + " LONGITUD: " + longitud);
	}

	private ContentValues generarCvInsertarGps(String terminal,String cuadrilla,
			String latitud, String longitud, String altitud, String velocidad,
			String vehiculo, String estado, String orden, String bateria){
		ContentValues valores = new ContentValues();
		valores.put(GP_TERMINAL, terminal);
		valores.put(GP_CUADRILLA, cuadrilla);
		valores.put(GP_LATITUD, latitud);
		valores.put(GP_LONGITUD, longitud);
		valores.put(GP_ALTITUD, altitud);
		valores.put(GP_VELOCIDAD, velocidad);
		valores.put(GP_FECHA,  getDateTime());
		valores.put(GP_VEHICULO, vehiculo);
		valores.put(GP_ESTADO, estado);
		valores.put(GP_ORDEN, orden);
		valores.put(GP_BATERIA, bateria);
		valores.put(GP_SINCRONIZADO, 0); 
		return valores;
	}

	public Cursor gpsPendintes(){ 
		String sqlConsulta = "SELECT * " +
				"  FROM "+ TABLE_GPS +" "+
				"  WHERE " + GP_SINCRONIZADO + "=0 LIMIT 100";

		Cursor cursor = db.rawQuery(sqlConsulta, null);

		return cursor;
	}

	public Cursor consultaActividadOrden(String ose_codigo) {
		// TODO Auto-generated method stub

		String sqlConsulta = 
				"SELECT t1." + OB_TIP_NOMBRE + " AS actividad " +
						", t2." + OB_TIP_NOMBRE + " AS causa " +
						", oe." + EO_MOTIVO_EJECUCION + " AS motivo" +
						"  FROM "+ TABLE_EJECUCION +" AS oe LEFT JOIN " + TABLE_OBSERVACION + " AS t1 ON oe." + EO_ACTIVIDAD + "= t1." + OB_TIP_CODIGO + " "
						+ " LEFT JOIN " + TABLE_OBSERVACION + " AS t2 ON oe." + EO_CODIGO_NO_LECTURA + "= t2." + OB_TIP_CODIGO + 
						"  WHERE oe." + EO_OSE_CODIGO + " =" + ose_codigo ;



		return db.rawQuery(sqlConsulta, null);
	}

	public void gpsUpdatePendientes2(String ids_gps){
		String sqlConsulta = "UPDATE " + TABLE_GPS + " SET " + GP_SINCRONIZADO + " = 1 WHERE " + GP_ID + " IN ( " + ids_gps + " )";
		db.execSQL(sqlConsulta);		
	}

	public void gpsUpdatePendientes(String ids_gps){
		int aff = 0;
		aff = db.update(TABLE_GPS, generarCvUpdateGPSEnviada(1),  GP_ID + " IN ( " + ids_gps + " )", null);
	}

	private ContentValues generarCvUpdateGPSEnviada(int sincronizado){
		ContentValues valores = new ContentValues();
		valores.put(GP_SINCRONIZADO, sincronizado); 

		return valores;
	}

	/**
	 * Retorna todas las ordenes de la cuadrilla para consutlar cuales se anularon
	 * @return
	 */
	public Cursor getOrdenesAnular() {
		// TODO Auto-generated method stub

		String[] columnas = new String[]{OS_OSE_CODIGO};
		Cursor cursor = db.query(TABLE_ORDEN, columnas, OS_ESTADO + "=?", new String[]{"1"}, null, null, null, null);
		return cursor;
	}

	public void gpsUpdateOrdenesAnuladas(String ordenes){
		int aff = 0;
		aff = db.update(TABLE_ORDEN, generarCvUpdateOrdenAnulada(1, 27),  OS_OSE_CODIGO + " IN ( " + ordenes + " )", null);
	}

	private ContentValues generarCvUpdateOrdenAnulada(int sincronizado, int estado){
		ContentValues valores = new ContentValues();
		valores.put(OS_SINCRONIZADO, sincronizado);
		valores.put(OS_ESTADO, estado); 

		return valores;
	}

	public void eliminarFotosEnviadas(){
		Cursor cursor;
		int ose_codigo; 
		String fecha; String foto_url;

		cursor = fotosEnviadas();		
		if(cursor.moveToFirst()){
			do{
				ose_codigo = cursor.getInt(cursor.getColumnIndex("ose_codigo"));							
				fecha = cursor.getString(cursor.getColumnIndex("fecha"));
				foto_url = cursor.getString(cursor.getColumnIndex("foto_url"));	

				File file = new File(foto_url);
				if(file.delete()){
					eliminarFotosEnviadas(String.valueOf(ose_codigo),fecha);
					System.out.println( "BORRO FOTO " + ose_codigo + " FECHA: " + fecha);
				}
			}while(cursor.moveToNext());//accessing data upto last row from table
		}
		cursor.close();
	}

	/**
	 * Retorna Cursor con la lista de pendientes
	 * @return cursor con fotos pendientes por enviar
	 */
	public Cursor fotosEnviadas(){
		String sqlConsulta = "SELECT * " +
				"  FROM "+ TABLE_FOTO +" "+
				"  WHERE " + FT_SINCRONIZADO + "=1";
		Cursor cursor = db.rawQuery(sqlConsulta, null);
		return cursor;
	}// Cierre material disponible

	public Cursor getGpsOrden(String ose_codigo){

		//id,int rec_codigo,int ose_codigo, String rec_prefijo, String rec_serie,String rec_nombre, int tipo

		String[] columnas = new String[]{GP_LATITUD,GP_LONGITUD}; 
		return db.query(TABLE_GPS, columnas, GP_ORDEN + "=? AND " + GP_ESTADO + "=?",new String[]{ose_codigo,"3"},null,null,null,null);
	}// Cierre Cargar Cursor Ordenes

	/** 
	 * Cargar Cursor Materiales en BD por busqueda
	 */ 
	public Cursor cargarMateriales(String busqueda,int ose_codigo, int tipoBusqueda){
		Cursor consultaMateriales =  null;
		String strOse_codigo = String.valueOf(ose_codigo);


		switch(tipoBusqueda){
		case 0://MATERIAL INSTALADO POR ORDEN
			String sqlConsulta = "SELECT mi._id, mi.rec_codigo, mi.rec_prefijo, mi.rec_cantidad, mi.rec_serie, mi.rec_nombre, 1 AS rec_seriado, mi.tipo, oe.ose_precarga AS nodo" +
					"  	FROM orden_servicio AS oe INNER JOIN material_instalado AS mi ON mi.ose_codigo = oe.ose_codigo " +
					"	WHERE oe.estado = 27 AND ( mi.rec_serie LIKE '%"+ busqueda +"%' OR mi.rec_nombre LIKE '%"+ busqueda +"%' OR oe.ose_precarga LIKE '%"+ busqueda +"%' )  ORDER BY mi._id DESC" ;
			consultaMateriales =  db.rawQuery(sqlConsulta, null);
			break;
		case 1://STOCK MATERIAL		
			consultaMateriales = db.query(TABLE_STOCK, new String[]{SC_REC_CODIGO,SC_REC_NOMBRE,SC_REC_SERIE,SC_REC_CANTIDAD,SC_REC_SERIADO,SC_REC_UNIDAD,SC_REC_PREFIJO}, 
					SC_REC_CANTIDAD + " > 0 AND ( " + SC_REC_CODIGO + " LIKE? OR " + SC_REC_SERIE + " LIKE? OR " + SC_REC_NOMBRE + " LIKE? ) "  ,
					new String[]{"%"+ busqueda +"%","%"+ busqueda +"%","%"+ busqueda +"%"},null,null,SC_REC_NOMBRE + " DESC",null);
			break;
		case 2://MATERIAL PARA RETIRAR		

			break;
		default:

			break;
		}


		return consultaMateriales;
	}// Cierre Cargar Cursor Materiales en BD

	public Cursor cargarMaterialAgrupado(String cuadrilla){

		String sqlConsulta = "SELECT 1 AS _id, rec_codigo, '' AS rec_prefijo, COUNT(*) AS rec_cantidad, '' AS rec_serie, rec_nombre, rec_seriado, 1 AS nodo" +
				"  	FROM stock_cuadrilla " +
				"	WHERE cuadrilla = " + cuadrilla + " GROUP BY rec_codigo, rec_nombre, rec_seriado ORDER BY rec_nombre" ;
		return db.rawQuery(sqlConsulta, null);
	}// Cierre Cargar Cursor Ordenes

	public List<String> getLablesLectura(String tipo){
		List<String> labels = new ArrayList<String>();		
		String sqlConsulta = 
				"SELECT CAST("+ tipo +" AS TEXT) from orden_servicio group by 1 " +
						" UNION SELECT '' AS " + tipo;

		Cursor cursor = db.rawQuery(sqlConsulta, null);
		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				labels.add(cursor.getString(0));
			} while (cursor.moveToNext());
		}
		// closing connection
		cursor.close();

		// returning lables
		return labels;
	}

	/**
	 * INSERT READ CHEQUED
	 * @param ose_codigo
	 * @param ose_precarga
	 * @param ose_tip_orden
	 * @param cli_contrato
	 * @param cli_nombre
	 * @param ciu_nombre
	 * @param direccion1
	 * @param direccion2
	 * @param producto
	 * @param barrio
	 * @param ciclo
	 * @param ruta
	 * @param ruta_cons
	 * @param estado
	 * @param obs
	 * @param cuadrilla
	 * @param elemento
	 * @param lectura_anterior
	 * @param consumo_promedio
	 * @param lectura_actual
	 * @param cantidad_digitos
	 * @param tipo_producto
	 * @param franja
	 * @param sincronizado
	 */
	public void insertCheckRed(int ose_codigo,int ose_precarga,int ose_tip_orden, int cli_contrato,String cli_nombre,String ciu_nombre,
			String direccion1, String direccion2, String producto,String barrio, int ciclo,int ruta,int ruta_cons,int estado,
			String obs, int cuadrilla,String elemento,int lectura_anterior, int consumo_promedio,int lectura_actual,
			String cantidad_digitos,String tipo_producto,String franja, int sincronizado, String estado_fens){

		String[] columnas = new String[]{OS_OSE_CODIGO};
		Cursor cursor = db.query(TABLE_ORDEN, columnas, OS_OSE_CODIGO  + "=?",new String[]{String.valueOf(ose_codigo)},null,null,null);
		Boolean firsTime = false;
		long aff = 0;

		if (cursor.moveToFirst()) {
			do {
				firsTime = true;
			} while (cursor.moveToNext());
		}

		if(!firsTime){
			aff = db.insert(TABLE_ORDEN, null, generarContentValues(ose_codigo,ose_precarga,ose_tip_orden, cli_contrato, cli_nombre, 
					ciu_nombre, direccion1,  direccion2,  producto, barrio, ciclo, ruta, ruta_cons, estado, obs,cuadrilla,elemento, 
					lectura_anterior, consumo_promedio, lectura_actual, cantidad_digitos, tipo_producto, franja, sincronizado, estado_fens));
			System.out.println( "INSERTO "+ ose_codigo + " REVISION " + aff );
		}else{
			try{
				aff = db.update(TABLE_ORDEN, generarCvUpdateOrden(ose_tip_orden), OS_OSE_CODIGO + "=? AND " + OS_SINCRONIZADO + "=?", new String[] {String.valueOf(ose_codigo),"1"});
				System.out.println( "ACTUALIZO "+ ose_codigo + " REVISION " + aff );
			}catch(Exception e){
				System.out.println( "ERROR ACTUALIZO REVISION " + e.getMessage() );
			}
		}
		cursor.close();
	}

	private ContentValues generarCvUpdateOrden(int ose_tip_orden){
		ContentValues valores = new ContentValues();
		valores.put(OS_OSE_TIP_ORDEN, ose_tip_orden); 

		return valores;
	}

	/**
	 * Retonar el Codigo tip_codigo de la Observacion Solicitada
	 * @param tip_nombre Nombre de la Observacion
	 * @param cti_codigo Clasificacion de la Observacion
	 * @return Codigo de la Orden
	 */
	public int getCodigoObs(String tip_nombre, String cti_codigo){

		int tipo_obs = 0;

		String[] columnas = new String[]{OB_TIP_CODIGO};
		Cursor cursor = db.query(TABLE_OBSERVACION, columnas, OB_TIP_NOMBRE  + "=? AND " + OB_SOLCONSUMO + "=?" ,new String[]{tip_nombre,cti_codigo},null,null,null,null);

		if (cursor.moveToFirst()) {
			tipo_obs = cursor.getInt(cursor.getColumnIndex(OB_TIP_CODIGO));
		}
		cursor.close();
		return tipo_obs;
	}// Cierre Retonar Codigo Observacion

	/**
	 * Getting all labels
	 * returns list of labels
	 * */
	public List<String> getAllLabels(String cti_codigo, int tipo){
		List<String> labels = new ArrayList<String>();

		String[] columnas = new String[]{OB_TIP_CODIGO, OB_TIP_NOMBRE};
		String filtro = OB_TIP_NOMBRE  + "=?";

		if(tipo==0)
			filtro += " AND " + OB_TIP_CODIGO + " IN ( 0, 779, 765, 770, 773, 775, 777, 778, 995, 807 )";

		Cursor cursor = db.query(TABLE_OBSERVACION, columnas, filtro,new String[]{cti_codigo},null,null,null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				labels.add(cursor.getString(cursor.getColumnIndex(OB_TIP_NOMBRE)));
			} while (cursor.moveToNext());
		}

		// closing connection
		cursor.close();
		// returning lables
		return labels;
	}

	/**
	 * Retorna observacion segun codigo
	 * @param tip_codigo
	 * @return
	 */
	public String getObsCodigo(String tip_codigo){

		String nombre_obs = "";
		String[] columnas = new String[]{OB_TIP_NOMBRE};
		Cursor cursor = db.query(TABLE_OBSERVACION, columnas, OB_TIP_CODIGO  + "=?" ,new String[]{tip_codigo},null,null,null,null);
		if (cursor.moveToFirst()) {
			nombre_obs = cursor.getString(cursor.getColumnIndex(OB_TIP_NOMBRE));
		}
		cursor.close();
		return nombre_obs;
	}// Cierre Retonar Codigo Observacion

	/**
	 * Inserta una foto en base de datos
	 * @param ose_codigo
	 * @param foto_url
	 */
	public void insertarFoto(int ose_codigo, String foto_url){

		db.insert(TABLE_FOTO, null, generarContentValuesInstertarFoto(ose_codigo, foto_url));
		System.out.println("INSERTO FOTO " + foto_url);
	}// Cierre Insertar foto en BD

	/**
	 * Genera bolsa de valores para insertar fotos
	 * @param ose_codigo
	 * @param foto_url
	 * @return Bolsa Valores
	 */
	private ContentValues generarContentValuesInstertarFoto(int ose_codigo,String foto_url){
		ContentValues valores = new ContentValues();
		valores.put(FT_OSE_CODIGO, ose_codigo);
		valores.put(FT_SINCRONIZADO, 0);
		valores.put(FT_FECHA, getDateTime());
		valores.put(FT_FOTO_URL, foto_url);

		return valores;
	}// Cierre Generacion de valores

	/**
	 * Eliminar una  orden enviada al servidor
	 */
	public void eliminarObservaciones() {
		//Nombre tabla, parametros y vector de estring
		db.delete(TABLE_OBSERVACION, OB_ID + "> ?", new String[]{"0"});
	} // Cierre Eliminar una  orden

	/**
	 * Return large table TABLE_OBSERVACION and TABLE_CENSO
	 *
	 * @return
	 */
	public Bundle getCountObsCodigo() {

		Bundle bolsaSalida = new Bundle();
		String nombre_obs = "";

		Cursor cursor = db.query(TABLE_OBSERVACION, null, OB_ID + "> ?", new String[]{"0"}, null, null, null, null);
		int tamano = cursor.getCount();
		bolsaSalida.putInt("observacion", tamano);

		/*
		cursor = db.query(TABLE_CENSO, null, CE_ELE_CODIGO + "> ?", new String[]{"0"}, null, null, null, null);
		tamano = cursor.getCount();
		bolsaSalida.putInt("censo", tamano);
		*/

		cursor.close();

		return bolsaSalida;
	}// Cierre getCountObsCodigo
}
