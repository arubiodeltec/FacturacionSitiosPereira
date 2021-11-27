package com.example.verorden;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.example.Logueo.DataBaseManager;

/**
 * Created by DeltecSistemas on 04/03/2016.
 */
public class VerOrdenManager {

    private SQLiteDatabase db;
    private DataBaseManager m;

    /**
     * Constructor Manejador de Bd
     * @param  context  Contexto
     */
    public VerOrdenManager(Context context){

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
    public Cursor cargarCursorOrdenesCicloRuta(
            String codigo_cuadrilla,String busqueda, String estado, String orden, String ciclo,
            String ruta, String tip_orden){

        String[] columnas = new String[]{m.OS_DIRECCION1,m.OS_ELEMENTO,m.OS_BARRIO,m.OS_PRODUCTO,
                m.OS_CIU_NOMBRE, m.OS_OSE_CODIGO, m.OS_CLI_NOMBRE,m.OS_CLI_CONTRATO, m.OS_OSE_PRECARGA,
                m.OS_RUTA, m.OS_RUTA_CONS, m.OS_CONSUMO, m.OS_TIPO_PRODUCTO};
        return db.query(m.TABLE_ORDEN, columnas,
                m.OS_CUADRILLA + "=? AND " + m.OS_ESTADO + " =? AND ( " + m.OS_ELEMENTO
                        + " LIKE? OR " + m.OS_CLI_CONTRATO + " LIKE? OR " + m.OS_DIRECCION1
                        + " LIKE? OR "  + m.OS_RUTA_CONS + " LIKE? ) AND " + m.OS_CICLO
                        + " LIKE? AND " + m.OS_RUTA + " LIKE?" + " AND " + m.OS_OSE_TIP_ORDEN + "=?",
                new String[]{codigo_cuadrilla,estado,"%"+ busqueda +"%","%"+ busqueda +"%","%"
                        + busqueda +"%","%"+ busqueda +"%","%"+ ciclo +"%","%"+ ruta +"%", tip_orden},
                null,null,m.OS_RUTA + " " + orden + ","+ m.OS_RUTA_CONS + " "
                        + orden + ","+ m.OS_PRODUCTO + " " + orden,"900");
    }// Cierre Cargar Cursor Ordenes

    /**
     * Consulta datos de impresion para la constancia
     * @param suscriptor
     * @return
     */
    public Cursor consultaLecturaImprimirEmcali(String suscriptor) {
        // TODO Auto-generated method stub
        String sqlConsulta =
                "SELECT * " +
                        "  FROM "+ m.TABLE_EJECUCION +" AS oe INNER JOIN " + m.TABLE_ORDEN + " AS os ON oe." + m.EO_OSE_CODIGO + "= os." + m.OS_OSE_CODIGO + " " +
                        "  WHERE os." + m.OS_CLI_CONTRATO + " =" + suscriptor ;

        return db.rawQuery(sqlConsulta, null);
    }

    /**
     * Genera un reporte con el total de ordenes de la cuadrilla, las pendeintes, ejecutadas y enviadas
     * @param cuadrilla
     * @return
     */
    public Bundle reporteOredenes(String cuadrilla){
        int total,ejecutadas,pendientes,enviadas, ciclo, revisiones, rev_enviadas, total_fotos = 0 , fotos_enviadas = 0;
        Bundle bolsa = new Bundle();
        String sqlConsulta = "SELECT ciclo, count(*) AS total, COALESCE(sum(CASE WHEN "+ m.OS_ESTADO +" = 27 THEN 1 ELSE 0 END), 0) AS ejecutadas," +
                " COALESCE(sum(CASE WHEN "+ m.OS_ESTADO +" = 1 THEN 1 ELSE 0 END), 0) AS pendientes," +
                " COALESCE(sum(CASE WHEN "+ m.OS_SINCRONIZADO +" = 1 THEN 1 ELSE 0 END), 0) AS enviadas," +
                " COALESCE(sum(CASE WHEN "+ m.OS_OSE_TIP_ORDEN +" IN (210,211) THEN 1 ELSE 0 END), 0) AS revisiones," +
                " COALESCE(sum(CASE WHEN "+ m.OS_OSE_TIP_ORDEN +" IN (211) AND "+ m.OS_SINCRONIZADO +" = 1  THEN 1 ELSE 0 END), 0) AS rev_enviadas" +
                "  FROM "+ m.TABLE_ORDEN +" WHERE "+ m.OS_CUADRILLA +" = " + cuadrilla;

        Cursor cursor = db.rawQuery(sqlConsulta, null);

        if (cursor.moveToFirst()) {
            total = cursor.getInt(cursor.getColumnIndex("total"));
            ejecutadas = cursor.getInt(cursor.getColumnIndex("ejecutadas"));
            pendientes = cursor.getInt(cursor.getColumnIndex("pendientes"));
            enviadas = cursor.getInt(cursor.getColumnIndex("enviadas"));
            revisiones = cursor.getInt(cursor.getColumnIndex("revisiones"));
            rev_enviadas = cursor.getInt(cursor.getColumnIndex("rev_enviadas"));
            ciclo  = cursor.getInt(cursor.getColumnIndex("ciclo"));

            bolsa.putInt("total", total);
            bolsa.putInt("ejecutadas", ejecutadas);
            bolsa.putInt("pendientes",pendientes);
            bolsa.putInt("enviadas",enviadas);
            bolsa.putInt("revisiones",revisiones);
            bolsa.putInt("rev_enviadas",rev_enviadas);
            bolsa.putInt("ciclo", ciclo);
        }

        sqlConsulta = "SELECT count(*) AS total_fotos, COALESCE(sum(CASE WHEN "+ m.FT_SINCRONIZADO +" = 1 THEN 1 ELSE 0 END), 0) AS fotos_enviadas" +
                "  FROM "+ m.TABLE_FOTO;
        cursor = db.rawQuery(sqlConsulta, null);
        if (cursor.moveToFirst()) {
            total_fotos = cursor.getInt(cursor.getColumnIndex("total_fotos"));
            fotos_enviadas = cursor.getInt(cursor.getColumnIndex("fotos_enviadas"));
        }

        bolsa.putInt("total_fotos", total_fotos);
        bolsa.putInt("fotos_enviadas", fotos_enviadas);

        cursor.close();
        return bolsa;
    }// Cierre material disponible
}
