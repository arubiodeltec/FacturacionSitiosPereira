package com.example.asistencia;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.example.Logueo.DataBaseManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by DELTEC on 21/07/2015.
 */
public class EjecucionAsistenciaModel {

    private SQLiteDatabase db;
    private DataBaseManager m;

    /**
     * Constructor Manejador de Bd
     * @param  context  Contexto
     */
    public EjecucionAsistenciaModel(Context context){

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
    public void close(){
        m.close();
    }//Close close BD

    /**
     * Cargar Cursor Ordenes
     *  @param  codigo_cuadrilla a quien pertenecen las ordenes
     */
    public Cursor cargarCursorAsistencia(String codigo_cuadrilla,String busqueda, int estado){

        String strEstado = "";

        String sqlConsulta =
                "   SELECT  sup_super_per_codigo, sup_lect_per_codigo, sup_lect_per_nombre, " +
                "           asi_fecha, asi_estado, asi_tipo, asi_observacion, asi_sincronizado, asi_estado,asi_tipo, asi_fecha_creacion, date('now') AS fecha_actual  " +
                "   FROM supervisor_lector LEFT JOIN asistencia ON sup_lect_per_codigo = asi_per_codigo AND asi_fecha_creacion >= date('now')" +
                        strEstado +
                "   WHERE sup_super_per_codigo = " + codigo_cuadrilla +
                "   AND (sup_lect_per_codigo LIKE '%" + busqueda + "%' OR sup_lect_per_nombre LIKE '%" + busqueda + "%')" +
                "    ORDER BY sup_lect_per_nombre ASC";

        System.out.println("Consulto Asistencia: " + sqlConsulta);
        return db.rawQuery(sqlConsulta, null);
    }// Cierre Cargar Cursor

    public void insertAssistants(int sup_super_per_codigo , int lect_per_codigo,String per_nombre){

        long aff = 0;
        aff = db.insert(m.TABLE_SUPERVISOR_LECTOR, null, generarContentValuesInstertarAssintant(sup_super_per_codigo, lect_per_codigo, per_nombre));
        System.out.println( "INSERTO ASISTENTE "+ per_nombre + " REVISION " + aff );
    }

    /**
     *
     * @param sup_super_per_codigo
     * @param lect_per_codigo
     * @param per_nombre
     * @return
     */
    private ContentValues generarContentValuesInstertarAssintant(int sup_super_per_codigo , int lect_per_codigo,String per_nombre){
        ContentValues valores = new ContentValues();
        valores.put(m.SUP_SUPER_PER_CODIGO, sup_super_per_codigo );
        valores.put(m.SUP_LECT_PER_CODIGO, lect_per_codigo);
        valores.put(m.SUP_LECT_PER_NOMBRE, per_nombre);
        valores.put(m.SUP_VIGENTE, 1);
        valores.put(m.SUP_FECHA_CREACION, getDateTime());
        valores.put(m.SUP_FECHA_ACTUALIZACION, getDateTime());

        return valores;
    }// Cierre Generacion de valores

    /**
     * Eliminar una  orden enviada al servidor
     */
    public void deleteAssintant(String sup_super_per_codigo){
        //Nombre tabla, parametros y vector de estring
        db.delete(m.TABLE_SUPERVISOR_LECTOR, m.SUP_SUPER_PER_CODIGO + "=?", new String[]{sup_super_per_codigo});
        System.out.println("BORRO ASISTENTES DE " + sup_super_per_codigo + " DELETE ");
    } // Cierre Eliminar una  orden

    /**
     * Obtener la fecha y hora actual
     */
    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    } // Cierre obtener Hora

    /**
     * Ingreso una asistencia diaria o la actualizo
     * @param asi_fecha
     * @param asi_per_codigo
     * @param asi_supervisor
     * @param asi_estado
     * @param asi_tipo
     * @param asi_fecha_creacion
     * @param asi_observacion
     */
    public void ingresarActualizarAsistencia(String asi_fecha, int asi_per_codigo,int asi_supervisor, int asi_estado,
                                          int asi_tipo,String asi_fecha_creacion, String asi_observacion){
        int rowsAff = 0;
        //ACTUALIZO CANTIDAD STOCK A CERO
        rowsAff = db.update(m.TABLE_ASISTENCIA, generarContentValuesUpdateAsistencia(asi_fecha, asi_estado, asi_tipo, asi_observacion),
                m.ASI_FECHA_CREACION  + "=? AND " + m.ASI_PER_CODIGO + " =? AND " + m.ASI_SUPERVISOR + "=?",
                new String[] {asi_fecha_creacion,String.valueOf(asi_per_codigo),String.valueOf(asi_supervisor)});

        System.out.println("INTENTO ACTUALIZAR ASISTENCIA LECTOR " + asi_per_codigo + " sup " + asi_supervisor + " resultado " + rowsAff);

        if(rowsAff == 0){
            db.insert(m.TABLE_ASISTENCIA, null, generarContentValuesInsertAsistencia(asi_fecha, asi_per_codigo, asi_supervisor,  asi_estado,
            asi_tipo, asi_fecha_creacion,  asi_observacion));
            System.out.println( "INSERTO ASISTENCIA " );
        }
    }// Ingresa o actualizar Asistencia

    private ContentValues generarContentValuesUpdateAsistencia(String asi_fecha, int asi_estado, int asi_tipo, String asi_observacion){

        ContentValues valores = new ContentValues();
        valores.put(m.ASI_FECHA, asi_fecha);
        valores.put(m.ASI_FECHA_ACTUALIZACION, getDateTime());
        valores.put(m.ASI_ESTADO, asi_estado);
        valores.put(m.ASI_TIPO, asi_tipo);
        valores.put(m.ASI_OBSERVACION, asi_observacion );
        valores.put(m.ASI_SINCRONIZADO, 0);

        return valores;
    }

    private ContentValues generarContentValuesInsertAsistencia(String asi_fecha, int asi_per_codigo,int asi_supervisor, int asi_estado,
                                                               int asi_tipo,String asi_fecha_creacion, String asi_observacion){
        ContentValues valores = new ContentValues();
        valores.put(m.ASI_FECHA, asi_fecha);
        valores.put(m.ASI_PER_CODIGO, asi_per_codigo);
        valores.put(m.ASI_SUPERVISOR, asi_supervisor);
        valores.put(m.ASI_ESTADO, asi_estado);
        valores.put(m.ASI_TIPO, asi_tipo);
        valores.put(m.ASI_FECHA_CREACION, asi_fecha_creacion);
        valores.put(m.ASI_FECHA_ACTUALIZACION, getDateTime());
        valores.put(m.ASI_OBSERVACION, asi_observacion);
        valores.put(m.ASI_SINCRONIZADO, 0);

        return valores;
    }

    public Bundle reporteAsistencia(String cuadrilla){
        int total,ejecutadas,pendientes,enviadas;
        Bundle bolsa = new Bundle();
        String sqlConsulta = "SELECT count(*) AS total," +
                "       COALESCE(sum(CASE WHEN asi_per_codigo > 0 THEN 1 ELSE 0 END), 0) AS ejecutadas," +
                "       COALESCE(sum(CASE WHEN asi_per_codigo > 0 THEN 0 ELSE 1 END), 0) AS pendientes," +
                "       COALESCE(sum(CASE WHEN ASI_SINCRONIZADO = 1 THEN 1 ELSE 0 END), 0) AS enviadas" +
                "       FROM supervisor_lector LEFT JOIN asistencia ON asi_supervisor = sup_super_per_codigo " +
                "               AND asi_per_codigo = sup_lect_per_codigo AND asi_fecha_creacion >= date('now')" +
                "       WHERE sup_super_per_codigo = " + cuadrilla;

        Cursor cursor = db.rawQuery(sqlConsulta, null);

        if (cursor.moveToFirst()) {
            total = cursor.getInt(cursor.getColumnIndex("total"));
            ejecutadas = cursor.getInt(cursor.getColumnIndex("ejecutadas"));
            pendientes = cursor.getInt(cursor.getColumnIndex("pendientes"));
            enviadas = cursor.getInt(cursor.getColumnIndex("enviadas"));

            bolsa.putInt("total", total);
            bolsa.putInt("ejecutadas", ejecutadas);
            bolsa.putInt("pendientes",pendientes);
            bolsa.putInt("enviadas",enviadas);
        }

        cursor.close();
        return bolsa;
    }// Cierre material disponible

    /**
     * Genera un cursor con las ordenes pendientes por enviar
     * @return
     */
    public Cursor asistenciaPendinte(){

        String sqlConsulta = "SELECT * FROM asistencia WHERE  asi_sincronizado = 0";
        return db.rawQuery(sqlConsulta, null);
    }// Cierre Ordenes Pendientes

    /**
     * Update Assintants Sincronizate
     * @param asi_supervisor
     * @param asi_persona
     * @param fecha
     */
    public void updateSincronizado_asistencia(int asi_supervisor, int asi_persona, String fecha){
        String sqlConsulta = "  UPDATE " + m.TABLE_ASISTENCIA + " SET " + m.ASI_SINCRONIZADO + " = 1 WHERE " +
                m.ASI_SUPERVISOR + "=" + asi_supervisor  + " AND  " + m.ASI_PER_CODIGO +  "= " + asi_persona + " AND  " + m.ASI_FECHA_CREACION + "='" + fecha + "'" ;
        db.execSQL(sqlConsulta);
    }// Close updateSincronizado_lecturas

    /**
     * Getting all labels
     * returns list of labels
     * */
    public List<String> getAllLabels(String cti_codigo, int tipo){
        List<String> labels = new ArrayList<String>();

        String[] columnas = new String[]{m.OB_TIP_CODIGO, m.OB_TIP_NOMBRE};
        String filtro = m.OB_SOLCONSUMO  + "=?";

        if(tipo==0)
            filtro += " AND " + m.OB_TIP_CODIGO + " IN ( 0, 779, 765, 770, 773, 775, 777, 778, 995, 807 )";

        Cursor cursor = db.query(m.TABLE_OBSERVACION, columnas, filtro,new String[]{cti_codigo},null,null,null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                labels.add(cursor.getString(cursor.getColumnIndex(m.OB_TIP_NOMBRE)));
            } while (cursor.moveToNext());
        }

        // closing connection
        cursor.close();
        // returning lables
        return labels;
    }

    /**
     * Retonar el Codigo tip_codigo de la Observacion Solicitada
     * @param tip_nombre Nombre de la Observacion
     * @param cti_codigo Clasificacion de la Observacion
     * @return Codigo de la Orden
     */
    public int getCodigoObs(String tip_nombre, String cti_codigo){

        int tipo_obs = 0;

        String[] columnas = new String[]{m.OB_TIP_CODIGO};
        Cursor cursor = db.query(m.TABLE_OBSERVACION, columnas, m.OB_TIP_NOMBRE  + "=? AND " + m.OB_SOLCONSUMO + "=?" ,new String[]{tip_nombre,cti_codigo},null,null,null,null);

        if (cursor.moveToFirst()) {
            tipo_obs = cursor.getInt(cursor.getColumnIndex(m.OB_TIP_CODIGO));
        }
        cursor.close();
        return tipo_obs;
    }// Cierre Retonar Codigo Observacion
}
