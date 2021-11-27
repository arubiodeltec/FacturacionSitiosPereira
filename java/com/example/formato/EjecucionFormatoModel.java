package com.example.formato;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.Logueo.DataBaseManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by DELTEC on 11/09/2015.
 */
public class EjecucionFormatoModel {
    private SQLiteDatabase db;
    private DataBaseManager m;

    /**
     * Constructor Manejador de Bd
     *
     * @param context Contexto
     */
    public EjecucionFormatoModel(Context context) {

        m = new DataBaseManager(context);
    } //Cierre Constructor

    /**
     * Open BD
     */
    public void open() {
        m.open();
        db = m.getBD();
    }//Close openBD

    /**
     * Close BD
     */
    public void close() {
        m.close();
    }//Close close BD

    public void insertTipoFormato(int tf_id, String tf_nombre) {

        try {
            long aff = 0;
            aff = db.insert(m.TABLE_INSPECCIONES_TIPOFORMATO, null, getCvInsertTipoFormato(tf_id, tf_nombre));
            System.out.println("INSERTO TIPOfORMATO " + tf_nombre + " REVISION " + aff);
        } catch (Exception e) {
            System.out.println("NO INSERTO TIPOfORMATO " + tf_nombre + " " + e.getMessage());
        }
    }

    private ContentValues getCvInsertTipoFormato(int tf_id, String tf_nombre) {
        ContentValues valores = new ContentValues();
        valores.put(m.ITF_ID, tf_id);
        valores.put(m.ITF_NOMBRE, tf_nombre);
        valores.put(m.ITF_FECHA_CREACION, getDate());//TEMPORAL
        valores.put(m.ITF_HORA_CREACION, getTime());//TEMPORAL

        return valores;
    }

    public void insertModuloFormato(int mf_id, String mf_nombre, int mf_codigo, int mf_numero_orden, int tipo_formato_id) {

        try {
            long aff = 0;
            aff = db.insert(m.TABLE_INSPECCIONES_MODULOFORMATO, null, getCvInsertModuloFormato(mf_id, mf_nombre, mf_codigo, mf_numero_orden, tipo_formato_id));
            System.out.println("INSERTO TIPOfORMATO " + mf_nombre + " REVISION " + aff);
        } catch (Exception e) {
            System.out.println("NO INSERTO TIPOfORMATO " + mf_nombre + " " + e.getMessage());
        }
    }

    private ContentValues getCvInsertModuloFormato(int mf_id, String mf_nombre, int mf_codigo, int mf_numero_orden, int tipo_formato_id) {
        ContentValues valores = new ContentValues();
        valores.put(m.IMF_ID, mf_id);
        valores.put(m.IMF_NOMBRE, mf_nombre);
        valores.put(m.IMF_CODIGO, mf_codigo);
        valores.put(m.IMF_NUMERO_ORDEN, mf_numero_orden);
        valores.put(m.IMF_TIPO_FORMATO_ID, tipo_formato_id);
        valores.put(m.IMF_FECHA_CREACION, getDate());
        valores.put(m.IMF_HORA_CREACION, getTime());

        return valores;
    }

    public void insertTipoCampo(int tc_id, String tc_descripcion) {

        try {
            long aff = 0;
            aff = db.insert(m.TABLE_INSPECCIONES_TIPOCAMPO, null, getCvInsertTipoCampo(tc_id, tc_descripcion));
            System.out.println("INSERTO TIPOfORMATO " + tc_descripcion + " REVISION " + aff);
        } catch (Exception e) {
            System.out.println("NO INSERTO TIPOfORMATO " + tc_descripcion + " " + e.getMessage());
        }
    }

    private ContentValues getCvInsertTipoCampo(int tc_id, String tc_descripcion) {
        ContentValues valores = new ContentValues();
        valores.put(m.ITC_ID, tc_id);
        valores.put(m.ITC_DESCRIPCION, tc_descripcion);
        valores.put(m.ITC_FECHA_CREACION, getDate());
        valores.put(m.ITC_HORA_CREACION, getTime());

        return valores;
    }

    public void insertCampoFormato(int cf_id, String cf_nombre, int cf_codigo, int cf_numero_orden, int cf_parent_id,
                                   String cf_tabla_referencia, int modulo_formato_id, int tipo_campo_id, String cf_descripcion, int cf_level) {

        try {
            long aff = 0;
            aff = db.insert(m.TABLE_INSPECCIONES_CAMPOFORMATO, null, getCvInsertCampoFormato(cf_id, cf_nombre, cf_codigo, cf_numero_orden,
                    cf_parent_id, cf_tabla_referencia, modulo_formato_id, tipo_campo_id, cf_descripcion, cf_level));
            System.out.println("INSERTO TIPOfORMATO " + cf_nombre + " REVISION " + aff);
        } catch (Exception e) {
            System.out.println("NO INSERTO TIPOfORMATO " + cf_nombre + " " + e.getMessage());
        }
    }

    private ContentValues getCvInsertCampoFormato(int cf_id, String cf_nombre, int cf_codigo, int cf_numero_orden, int cf_parent_id,
                                                  String cf_tabla_referencia, int modulo_formato_id, int tipo_campo_id, String cf_descripcion, int cf_level) {
        ContentValues valores = new ContentValues();
        valores.put(m.ICF_ID, cf_id);
        valores.put(m.ICF_NOMBRE, cf_nombre);
        valores.put(m.ICF_CODIGO, cf_codigo);
        valores.put(m.ICF_NUMERO_ORDEN, cf_numero_orden);
        valores.put(m.ICF_TABLA_REFERENCIA, cf_tabla_referencia);
        valores.put(m.ICF_MODULO_FORMATO_ID, modulo_formato_id);
        valores.put(m.ICF_TIPO_CAMPO_ID, tipo_campo_id);
        valores.put(m.ICF_FECHA_CREACION, getDate());
        valores.put(m.ICF_HORA_CREACION, getTime());
        valores.put(m.ICF_DESCRIPCION, cf_descripcion);
        valores.put(m.ICF_PARENT_ID, cf_parent_id);
        valores.put(m.ICF_LEVEL, cf_level);

        return valores;
    }

    public Cursor cargarCursorFormato(int codigo_formato, int codigo_ejecucion_formato) {

        String sqlConsulta =
                "   SELECT  tf.id AS tf_id, tf.nombre AS tf_nombre, tf.descripcion AS tf_des,\n" +
                        "   mf.id AS mf_id, mf.nombre AS mf_nombre, mf.codigo AS mf_codigo, mf.numero_orden AS mf_numero_orden,\n" +
                        "   cf.id AS cf_id, cf.nombre AS cf_nombre, cf.codigo AS cf_codigo, cf.numero_orden AS cf_numero_orden,\n" +
                        "   cf.parent_id AS cf_parent_id, cf.level AS cf_level, cf.descripcion AS cf_descripcion, cf.tabla_referencia AS cf_tabla_referencia,\n" +
                        "   tc.id AS tc_id, tc.descripcion AS tc_descripcion,\n" +
                        "   ef.estado As ef_estado, re.hora_creacion AS re_hora_creacion\n" +
                        "   FROM\n" +
                        "       inspecciones_tipoformato tf\n" +
                        "       INNER JOIN inspecciones_moduloformato mf ON tf.id = mf.tipo_formato_id\n" +
                        "       INNER JOIN inspecciones_campoformato cf ON cf.modulo_formato_id = mf.id\n" +
                        "       INNER JOIN inspecciones_tipocampo tc ON cf.tipo_campo_id = tc.id\n" +
                        "       LEFT JOIN inspecciones_ejecucionformato ef ON tf.id = ef.tipo_formato_id AND ef.id = " + codigo_ejecucion_formato +
                        "       LEFT JOIN inspecciones_registroejecucion re ON re.ejecucion_formato_id = ef.id AND re.campo_formato_id = cf.id\n" +
                        "   WHERE \n" +
                        "       tf.id = " + codigo_formato +
                        "       AND cf.level = 0 " +
                        "   ORDER BY mf.numero_orden, cf.numero_orden";

        //System.out.println("Consulto Formato: " + sqlConsulta);
        return db.rawQuery(sqlConsulta, null);
    }

    /**
     *
     * @param codigo_formato
     * @param codigo_ejecucion_formato
     * @param mf_id
     * @return
     */
    public Cursor cargarCursorFormato(int codigo_formato, int codigo_ejecucion_formato, int mf_id) {

        String sqlConsulta =
                "   SELECT  tf.id AS tf_id, tf.nombre AS tf_nombre, tf.descripcion AS tf_des,\n" +
                        "   mf.id AS mf_id, mf.nombre AS mf_nombre, mf.codigo AS mf_codigo, mf.numero_orden AS mf_numero_orden,\n" +
                        "   cf.id AS cf_id, cf.nombre AS cf_nombre, cf.codigo AS cf_codigo, cf.numero_orden AS cf_numero_orden,\n" +
                        "   cf.parent_id AS cf_parent_id, cf.level AS cf_level, cf.descripcion AS cf_descripcion, cf.tabla_referencia AS cf_tabla_referencia,\n" +
                        "   tc.id AS tc_id, tc.descripcion AS tc_descripcion,\n" +
                        "   ef.estado As ef_estado, re.hora_creacion AS re_hora_creacion\n" +
                        "   FROM\n" +
                        "       inspecciones_tipoformato tf\n" +
                        "       INNER JOIN inspecciones_moduloformato mf ON tf.id = mf.tipo_formato_id\n" +
                        "       INNER JOIN inspecciones_campoformato cf ON cf.modulo_formato_id = mf.id\n" +
                        "       INNER JOIN inspecciones_tipocampo tc ON cf.tipo_campo_id = tc.id\n" +
                        "       LEFT JOIN inspecciones_ejecucionformato ef ON tf.id = ef.tipo_formato_id AND ef.id = " + codigo_ejecucion_formato +
                        "       LEFT JOIN inspecciones_registroejecucion re ON re.ejecucion_formato_id = ef.id AND re.campo_formato_id = cf.id\n" +
                        "   WHERE \n" +
                        "       tf.id = " + codigo_formato +
                        "       AND cf.level = 0 " +
                        "       AND mf.id = " + mf_id +
                        "   ORDER BY mf.numero_orden, cf.numero_orden";

        //System.out.println("Consulto Formato: " + sqlConsulta);
        return db.rawQuery(sqlConsulta, null);
    }

    /**
     * Obtener la fecha actual
     */
    private String getDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    } // Cierre obtener Hora

    /**
     * Obtener tiempo actual
     *
     * @return
     */
    private String getTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    } // Cierre obtener Hora

    /**
     * @param cf_id
     * @return
     */
    public Cursor getSeleccionMultiple(int cf_id, int ef_id) {

        String sqlConsulta =
                "   SELECT " +
                        "       cf1.id AS cf_id, cf1.nombre AS cf_nombre,cf1.descripcion AS cf_descripcion," +
                        "       cf1.codigo AS cf_codigo, cf1.numero_orden AS cf_numero_orden," +
                        "       cf1.parent_id AS cf_parent, cf1.tabla_referencia AS cf_tabla_referencia, cf1.level AS cf_level," +
                        "       rn.resultado, cf1.tipo_campo_id AS tc_id, cf1.descripcion AS cf_descripcion," +
                        "       cf2.nombre AS cf2_nombre, cf2.id AS id_hijo, cf2.tipo_campo_id, cf2.tabla_referencia" +
                        "   FROM inspecciones_campoformato cf1" +
                        "       LEFT JOIN inspecciones_campoformato cf2 ON cf1.id = cf2.parent_id" +
                        "       LEFT JOIN inspecciones_registroejecucion re ON re.campo_formato_id = cf1.parent_id AND re.ejecucion_formato_id = " + ef_id +
                        "       LEFT JOIN inspecciones_respuestanumerica rn ON rn.ejecucion_formato_id = re.id AND rn.resultado = cf1.id " +
                        "   WHERE cf1.parent_id = " + cf_id;
        //System.out.println("Consulto Formato: " + sqlConsulta);
        return db.rawQuery(sqlConsulta, null);
    }

    /**
     * @param cf_id
     * @return
     */
    public Cursor getDatosSeleccionMultiple(int cf_id, int ef_id) {

        String sqlConsulta =
                " SELECT " +
                "       cf1.id AS cf_id, cf1.nombre AS cf_nombre,cf1.descripcion AS cf_descripcion," +
                "       cf1.codigo AS cf_codigo, cf1.numero_orden AS cf_numero_orden," +
                "       cf1.parent_id AS cf_parent, cf1.tabla_referencia AS cf_tabla_referencia, cf1.level AS cf_level," +
                "       rn.resultado," +
                "       cf1.tipo_campo_id AS tc_id, cf1.descripcion AS cf_descripcion" +
                " FROM  inspecciones_campoformato cf1" +
                "     LEFT JOIN inspecciones_registroejecucion re ON re.campo_formato_id = cf1.parent_id AND re.ejecucion_formato_id =  " + ef_id +
                "     LEFT JOIN inspecciones_respuestanumerica rn ON rn.ejecucion_formato_id = re.id AND rn.resultado = cf1.id" +
                " WHERE cf1.parent_id =  " + cf_id +
                " ORDER BY cf_numero_orden";
        //System.out.println("Consulto Formato: " + sqlConsulta);
        return db.rawQuery(sqlConsulta, null);
    }

    /**
     *
     * @param cf_id
     * @param ef_id
     * @param cf_tabla_referencia
     * @return
     */
    public String getUnicaRespuesta(int cf_id, int ef_id,String cf_tabla_referencia) {

        String salida = "";
        String sqlConsulta =
                "   SELECT  rn.resultado\n" +
                        " FROM inspecciones_registroejecucion re LEFT JOIN "+ cf_tabla_referencia +" rn ON rn.ejecucion_formato_id = re.id\n" +
                        " WHERE re.campo_formato_id = "+ cf_id + " AND re.ejecucion_formato_id = " + ef_id;
        Cursor cursor = db.rawQuery(sqlConsulta, null);
        if (cursor.moveToFirst()) {
            salida = cursor.getString(0);
            //System.out.println( "SALIDA " + salida  + " UNICA RESPUESTA: " + sqlConsulta);
        }
        cursor.close();
        return salida;
    }

    /**
     * Insert into Formato Ejecucion
     *
     * @param ef_id
     * @param ef_estado
     * @param ef_calificacion
     * @param ef_tipo_formato_id
     */
    public void insertEjecucionFormato(int ef_id, int ef_estado, int ef_calificacion, int ef_tipo_formato_id) {

        try {
            long aff = 0;
            aff = db.insert(m.TABLE_INSPECCIONES_EJECUCIONFORMATO, null, getCvInsertEjecucionFormato(ef_id, ef_estado, ef_calificacion, ef_tipo_formato_id));
            //System.out.println("INSERTO EJECUCIONFORMATO " + ef_id + " REVISION " + aff);
        } catch (Exception e) {
            //System.out.println("NO INSERTO EJECUCIONFORMATO " + ef_id + " " + e.getMessage());
        }
    }

    /**
     * @param ef_id
     * @param ef_estado
     * @param ef_calificacion
     * @param ef_tipo_formato_id
     * @return
     */
    private ContentValues getCvInsertEjecucionFormato(int ef_id, int ef_estado, int ef_calificacion, int ef_tipo_formato_id) {

        ContentValues valores = new ContentValues();
        valores.put(m.IEF_ID, ef_id);
        valores.put(m.IEF_ESTADO, ef_estado);
        valores.put(m.IEF_CALIFICACION, ef_calificacion);
        valores.put(m.IEF_TIPO_FORMATO_ID, ef_tipo_formato_id);
        valores.put(m.IEF_FECHA_CREACION, getDate());
        valores.put(m.IEF_HORA_CREACION, getTime());
        return valores;
    }

    /**
     * @param cf_id
     * @param ef_id
     * @param ef_persona
     * @param cf_tabla_referencia
     * @param er_resultado
     */
    public void ingresarActualizarRegistroEjecucion(int cf_id, int ef_id, int ef_persona, String cf_tabla_referencia, String er_resultado) {

        int rowsAff = 0, re_id = 0;
        String tablaReferencia =  m.TABLE_INSPECCIONES_RESPUESTAFECHA;
        //ACTUALIZO CANTIDAD STOCK A CERO
        rowsAff = db.update(m.TABLE_INSPECCIONES_REGISTROEJECUCION, generarCVUpdateRegistroEjecucion(ef_persona),
                m.IRE_CAMPO_FORMATO_ID + "=? AND " + m.IRE_EJECUCION_FORMATO_ID + " =?", new String[]{String.valueOf(cf_id), String.valueOf(ef_id)});

        if(cf_tabla_referencia.contains("inspecciones_respuestanumerica"))
            tablaReferencia = m.TABLE_INSPECCIONES_RESPUESTANUMERICA;
        else if(cf_tabla_referencia.contains("inspecciones_respuestaabierta"))
            tablaReferencia = m.TABLE_INSPECCIONES_RESPUESTAABIERTA;

        if (rowsAff == 0) {
            db.insert(m.TABLE_INSPECCIONES_REGISTROEJECUCION, null, generarCVInsertRegistroEjecucion(cf_id, ef_id, ef_persona));
            re_id = last_insert_id();

            db.insert(tablaReferencia,null, generarCVInsertResultadoRegistro(er_resultado,re_id,ef_persona));
        }else{
            re_id = getIdRegistroEjecucion(cf_id,ef_id);
            rowsAff = db.update(tablaReferencia, generarCVUpdateResultadoRegistro(er_resultado, ef_persona),
                    m.IRA_REGISTRO_EJECUCION_ID + " =?", new String[]{String.valueOf(re_id)});
            if(rowsAff == 0){
                db.insert(tablaReferencia,null, generarCVInsertResultadoRegistro(er_resultado,re_id,ef_persona));
            }
        }
    }// Cierra ingresarActualizarRegistroEjecucion

    /**
     * @param ef_persona
     * @return
     */
    private ContentValues generarCVUpdateRegistroEjecucion(int ef_persona) {
        ContentValues valores = new ContentValues();
        valores.put(m.IRE_MODIFICADO_POR_ID, ef_persona);
        valores.put(m.IRE_FECHA_CREACION, getDate());
        valores.put(m.IRE_HORA_CREACION, getTime());

        return valores;
    }// Cierre Contenedor de valores UPDATE LECTURA

    /**
     *
     * @param cf_id
     * @param ef_id
     * @param ef_persona
     * @return
     */
    private ContentValues generarCVInsertRegistroEjecucion(int cf_id, int ef_id, int ef_persona) {

        ContentValues valores = new ContentValues();
        valores.put(m.IRE_EJECUCION_FORMATO_ID, ef_id);
        valores.put(m.IRE_CAMPO_FORMATO_ID, cf_id);
        valores.put(m.IRE_CREADO_POR_ID, ef_persona);
        valores.put(m.IRE_MODIFICADO_POR_ID, ef_persona);
        valores.put(m.IRE_FECHA_CREACION, getDate());
        valores.put(m.IRE_HORA_CREACION, getTime());

        return valores;
    }

    /**
     *
     * @param er_resultado
     * @param er_registro_ejecucion_id
     * @param er_persona
     * @return
     */
    private ContentValues generarCVInsertResultadoRegistro(String er_resultado, int er_registro_ejecucion_id, int er_persona) {

        ContentValues valores = new ContentValues();
        valores.put(m.IRA_RESULTADO, er_resultado);
        valores.put(m.IRA_REGISTRO_EJECUCION_ID, er_registro_ejecucion_id);
        valores.put(m.IRA_CREADO_POR_ID, er_persona);
        valores.put(m.IRA_MODIFICADO_POR_ID, er_persona);
        valores.put(m.IRA_FECHA_CREACION, getDate());
        valores.put(m.IRA_HORA_CREACION, getTime());

        return valores;
    }

    /**
     *
     * @param er_resultado
     * @param er_persona
     * @return
     */
    private ContentValues generarCVUpdateResultadoRegistro(String er_resultado, int er_persona) {

        ContentValues valores = new ContentValues();
        valores.put(m.IRA_RESULTADO, er_resultado);
        valores.put(m.IRA_CREADO_POR_ID, er_persona);
        valores.put(m.IRA_MODIFICADO_POR_ID, er_persona);
        valores.put(m.IRA_FECHA_CREACION, getDate());
        valores.put(m.IRA_HORA_CREACION, getTime());

        return valores;
    }

    /**
     *
     * @return
     */
    private int last_insert_id() {

        int last_id = 0;
        String sqlConsulta = "SELECT last_insert_rowid()";
        try {
            Cursor cursor = db.rawQuery(sqlConsulta, null);
            if (cursor.moveToFirst()) {
                last_id = cursor.getInt(0);
                //System.out.println("ULTIMO ID " + last_id);
            }
            cursor.close();
        } catch (Exception e) {
            System.out.println("NO REPORTO ULTIMO ID " + last_id + " " + e.getMessage());
        }
        return last_id;
    }

    /**
     * Retorna Id del Registros de Ejecucion a Actualizar
     * @param cf_id
     * @param ef_id
     * @return
     */
    private int getIdRegistroEjecucion(int cf_id,int ef_id){

        int er_id = 0;
        try {
        Cursor cursor = db.query(m.TABLE_INSPECCIONES_REGISTROEJECUCION,
                new String[]{m.IRA_ID},
                m.IRE_CAMPO_FORMATO_ID + "=? AND " + m.IRE_EJECUCION_FORMATO_ID + " =?",
                new String[]{String.valueOf(cf_id), String.valueOf(ef_id)}, null, null, null, null);

            if (cursor.moveToFirst()) {
                er_id = cursor.getInt(0);
                //System.out.println("ER_ID A ACTUALIZAR" + er_id);
            }
            cursor.close();
        } catch (Exception e) {
            System.out.println("NO REPORTO ER_ID " + cf_id + " " + ef_id + " " + e.getMessage());
        }
        return er_id;
    }


    public int deleteEjecucionRespuesta(int er_respuesta, int ef_id, String cf_tabla_referencia ) {

        int last_id = 0;
        String sqlConsulta =
                "DELETE FROM inspecciones_respuestanumerica WHERE id IN (\n" +
                "   SELECT rn.id FROM "+cf_tabla_referencia+" rn \n" +
                "       INNER JOIN inspecciones_registroejecucion re ON rn.ejecucion_formato_id = re.id\n" +
                "   WHERE rn.resultado = "+ er_respuesta +" AND re.ejecucion_formato_id = "+ ef_id +")";
        try {
            db.execSQL(sqlConsulta);
        } catch (Exception e) {
            System.out.println("NO ELIMINO ID " + er_respuesta + " DE " + cf_tabla_referencia + " "  + e.getMessage());
        }
        return last_id;
    }

    public void ingresarActualizarRegistroEjecucionMultiple(int cf_id, int ef_id, int ef_persona, String cf_tabla_referencia, String er_resultado) {

        int rowsAff = 0, re_id = 0;
        String tablaReferencia =  m.TABLE_INSPECCIONES_RESPUESTAFECHA;
        //ACTUALIZO CANTIDAD STOCK A CERO
        rowsAff = db.update(m.TABLE_INSPECCIONES_REGISTROEJECUCION, generarCVUpdateRegistroEjecucion(ef_persona),
                m.IRE_CAMPO_FORMATO_ID + "=? AND " + m.IRE_EJECUCION_FORMATO_ID + " =?", new String[]{String.valueOf(cf_id), String.valueOf(ef_id)});

        //System.out.println("ACTUALIZO RegistroEjecucion " + rowsAff);
        if(cf_tabla_referencia.contains("inspecciones_respuestanumerica"))
            tablaReferencia = m.TABLE_INSPECCIONES_RESPUESTANUMERICA;
        else if(cf_tabla_referencia.contains("inspecciones_respuestaabierta"))
            tablaReferencia = m.TABLE_INSPECCIONES_RESPUESTAABIERTA;
        else if(cf_tabla_referencia.contains("inspecciones_respuestafecha"))
            tablaReferencia = m.TABLE_INSPECCIONES_RESPUESTAFECHA;

        if (rowsAff == 0) {
            db.insert(m.TABLE_INSPECCIONES_REGISTROEJECUCION, null, generarCVInsertRegistroEjecucion(cf_id, ef_id, ef_persona));
            //System.out.println(cf_id + " INSERTO RegistroEjecucion " + ef_id);
            re_id = last_insert_id();

            db.insert(tablaReferencia,null, generarCVInsertResultadoRegistro(er_resultado,re_id,ef_persona));
        }else{
            re_id = getIdRegistroEjecucion(cf_id, ef_id);
            rowsAff = db.update(tablaReferencia,  generarCVUpdateResultadoRegistro(er_resultado, ef_persona),
                    m.IRA_REGISTRO_EJECUCION_ID + "=? AND " + m.IRA_RESULTADO + " =?", new String[]{String.valueOf(re_id), String.valueOf(er_resultado)});
            if (rowsAff == 0){
                db.insert(tablaReferencia,null, generarCVInsertResultadoRegistro(er_resultado, re_id, ef_persona));
            }
            //System.out.println(" ACTUALIZO RESULTADO REFGISTRO " + ef_id + " CON " + rowsAff + " "  + tablaReferencia);
        }
    }// Cierra ingresarActualizarRegistroEjecucion

    /**
     *
     * @param codigo_formato
     * @param codigo_ejecucion_formato
     * @param cf_id
     * @return
     */
    public Cursor cargarCursorCampoFormato(int codigo_formato, int codigo_ejecucion_formato, int cf_id) {

        String sqlConsulta =
                "   SELECT  tf.id AS tf_id, tf.nombre AS tf_nombre, tf.descripcion AS tf_des," +
                        "   mf.id AS mf_id, mf.nombre AS mf_nombre, mf.codigo AS mf_codigo, mf.numero_orden AS mf_numero_orden," +
                        "   cf.id AS cf_id, cf.nombre AS cf_nombre, cf.codigo AS cf_codigo, cf.numero_orden AS cf_numero_orden," +
                        "   cf.parent_id AS cf_parent_id, cf.level AS cf_level, cf.descripcion AS cf_descripcion, cf.tabla_referencia AS cf_tabla_referencia," +
                        "   tc.id AS tc_id, tc.descripcion AS tc_descripcion," +
                        "   ef.estado As ef_estado, re.hora_creacion AS re_hora_creacion" +
                        "   FROM" +
                        "       inspecciones_tipoformato tf" +
                        "       INNER JOIN inspecciones_moduloformato mf ON tf.id = mf.tipo_formato_id" +
                        "       INNER JOIN inspecciones_campoformato cf ON cf.modulo_formato_id = mf.id" +
                        "       INNER JOIN inspecciones_tipocampo tc ON cf.tipo_campo_id = tc.id" +
                        "       LEFT JOIN inspecciones_ejecucionformato ef ON tf.id = ef.tipo_formato_id AND ef.id = " + codigo_ejecucion_formato +
                        "       LEFT JOIN inspecciones_registroejecucion re ON re.ejecucion_formato_id = ef.id AND re.campo_formato_id = cf.id" +
                        "   WHERE " +
                        "       tf.id = " + codigo_formato +
                        "       AND cf.id = " + cf_id +
                        "   ORDER BY mf.numero_orden, cf.numero_orden";

        //System.out.println("Consulto Formato: " + sqlConsulta);
        return db.rawQuery(sqlConsulta, null);
    }

    public int getHijo(int campo_formato) {

        int hijo = 0;
        String sqlConsulta =
                "   SELECT cf1.id AS id_hijo " +
                        " FROM inspecciones_campoformato cf1 " +
                        " WHERE cf1.parent_id = " +campo_formato;

        //System.out.println("Consulto Formato: " + sqlConsulta);
        Cursor cursor = db.rawQuery(sqlConsulta, null);
        if (cursor.moveToFirst()) {
            do {
                hijo = cursor.getInt(cursor.getColumnIndex("id_hijo"));
            } while (cursor.moveToNext());//accessing data upto last row from table
        }
        cursor.close();

        return hijo;
    }

    public Cursor getModulos(int tf_id){
        //query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy)
        String[] columnas = new String[]{m.IMF_ID,m.IMF_NOMBRE};
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return db.query(m.TABLE_INSPECCIONES_MODULOFORMATO, columnas,m.IMF_TIPO_FORMATO_ID+ "=?",new String[]{String.valueOf(tf_id)},null,null,m.IMF_NUMERO_ORDEN + " ASC");
    }// Cierre Buscar una Orden


    public List<String> getFormatos(){
        List<String> labels = new ArrayList<String>();

        String[] columnas = new String[]{m.ITF_NOMBRE};

        Cursor cursor = db.query(m.TABLE_INSPECCIONES_TIPOFORMATO, columnas, null,null,null,null,null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                labels.add(cursor.getString(cursor.getColumnIndex(m.ITF_NOMBRE)));
            } while (cursor.moveToNext());
        }

        // closing connection
        cursor.close();
        // returning lables
        return labels;
    }

    public int getCodigoFormato(String tf_nombre){

        int tf_id = 0;

        String[] columnas = new String[]{m.ITF_ID};
        Cursor cursor = db.query(m.TABLE_INSPECCIONES_TIPOFORMATO, columnas, m.ITF_NOMBRE  + "=?",new String[]{tf_nombre},null,null,null,null);

        if (cursor.moveToFirst()) {
            tf_id = cursor.getInt(cursor.getColumnIndex(m.ITF_ID));
        }
        cursor.close();
        return tf_id;
    }

    public Cursor cargarCursorFormatoEjecutado(int codigo_formato, int estado) {

        String restriciones = "";
        if(codigo_formato > 0) {
            if(restriciones != "")
                restriciones = " AND tf.id = " + codigo_formato + " ";
            else restriciones += " WHERE tf.id = " + codigo_formato + " ";
        }

        if(estado > 0) {
            if(restriciones != "")
                restriciones = " AND ef.estado = " + estado + " ";
            else restriciones +=" WHERE ef.estado = " + estado + " ";
        }

        String sqlConsulta =
                        "   SELECT  tf.id AS tf_id, tf.nombre AS tf_nombre," +
                        "   ef.estado As ef_estado, ef.fecha_creacion AS ef_fecha_creacion, ef.hora_creacion AS ef_hora_creacion," +
                        "	ef.id AS ef_id, ef.calificacion AS ef_calificacion, ef.estado AS ef_estado"  +
                        "   FROM " +
                        "       inspecciones_tipoformato tf INNER JOIN inspecciones_ejecucionformato ef ON tf.id = ef.tipo_formato_id  "
                        + restriciones +
                        "   ORDER BY ef.fecha_creacion";

        System.out.println(sqlConsulta);

        return db.rawQuery(sqlConsulta, null);
    }

    public int getNextEjecucionFormato(){

        int tf_id = 0;

        String[] columnas = new String[]{m.IEF_ID};
        Cursor cursor = db.rawQuery("SELECT (MAX(" + m.IEF_ID + ") + 1) AS max_id FROM "+ m.TABLE_INSPECCIONES_EJECUCIONFORMATO, null);

        if (cursor.moveToFirst()) {
            tf_id = cursor.getInt(cursor.getColumnIndex("max_id"));
        }
        cursor.close();
        return tf_id;
    }

    /**
     * Obtiene el
     * @param ef_id
     * @return
     */
    public Cursor getEjecucionFormato(int ef_id){

        String sqlConsulta =
                        "SELECT ef.id AS ef_id,ef.tipo_formato_id AS tf_id, ef.estado As ef_estado, ef.fecha_creacion AS ef_fecha_creacion, " +
                        "   ef.hora_creacion AS ef_hora_creacion, ef.calificacion AS ef_calificacion, re.campo_formato_id AS cf_id, " +
                        "   re.fecha_creacion AS re_fecha_creacion, re.hora_creacion AS re_hora_creacion, " +
                        "   rn.resultado AS rn_resultado, ra.resultado AS ra_resultado, rf.resultado AS rf_resultado " +
                        "FROM " +
                        "   inspecciones_ejecucionformato ef  INNER JOIN inspecciones_registroejecucion re ON ef.id  = re.ejecucion_formato_id " +
                        "   LEFT JOIN inspecciones_respuestanumerica rn ON rn.ejecucion_formato_id = re.id " +
                        "   LEFT JOIN inspecciones_respuestaabierta ra ON ra.ejecucion_formato_id = re.id " +
                        "   LEFT JOIN inspecciones_respuestafecha rf ON rf.ejecucion_formato_id = re.id " +
                        "WHERE ef.id = " + ef_id;

        System.out.println(sqlConsulta);

        return db.rawQuery(sqlConsulta, null);
    }// Cierre getEjecucionFormato


}
