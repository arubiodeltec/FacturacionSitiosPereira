package com.example.gestionOrdenes;


import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.actsis.fensliq.ManejoDatos;
import com.example.Logueo.DataBaseManager;
import com.example.Logueo.DbHelper;
import com.example.apiretrofit.ApiClient;
import com.example.apiretrofit.ApiServices;
import com.example.lectura.Auth;
import com.example.lectura.EnvioDatos;
import com.example.lectura.MyApplication;

import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by DELTEC on 10/07/2015.
 */
public class EjecucionOrdenesModel {

    private SQLiteDatabase db;
    private DataBaseManager m;

    /**
     * Constructor Manejador de Bd
     * @param  context  Contexto
     */
    public EjecucionOrdenesModel(Context context){

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
    public Cursor cargarCursorOrdenesCicloRuta(String codigo_cuadrilla,String busqueda, String estado, String orden, String ciclo, String ruta, String tip_orden){
        return db.rawQuery(" SELECT " + m.ORDEN_ID + ", " + m.RUTA_ORDEN + ", " + m.OS_DIRECCION1 + ", "+ m.OS_ESTADO_FENS + "," + m.OS_ELEMENTO + ", " +
                        m.OS_BARRIO + ", " + m.OS_PRODUCTO + ", " + m.OS_CIU_NOMBRE + ", " +
                        m.TABLE_ORDEN + "." + m.OS_OSE_CODIGO + ", " + m.OS_CLI_NOMBRE + ", " + m.OS_CLI_CONTRATO + ", " +
                        m.OS_OSE_PRECARGA + ", " +  m.OS_RUTA + ", " +  m.OS_RUTA_CONS + ", " + m.OS_CONSUMO +
                        " FROM " + m.TABLE_ORDEN + " LEFT JOIN " + m.TABLE_DETALLE_ORDEN_FACTURACION + " ON " +
                        m.TABLE_ORDEN + "." + m.OS_OSE_CODIGO + " = " + m.TABLE_DETALLE_ORDEN_FACTURACION + "." + m.OSE_CODIGO +
                        " WHERE " + m.OS_CUADRILLA + "= ? AND " + m.OS_ESTADO + " = ? AND ( " + m.OS_ELEMENTO + " LIKE ? OR " +
                        m.OS_CLI_CONTRATO + " LIKE ? OR " + m.OS_DIRECCION1  + " LIKE ? OR "  + m.OS_RUTA_CONS + " LIKE ? ) AND " +
                        m.OS_CICLO + " LIKE ? AND " + m.OS_RUTA + " LIKE ?" + " AND " + m.OS_OSE_TIP_ORDEN + "= ?" +
                        " ORDER BY " + m.OS_RUTA + " " + orden + ", "+ m.OS_RUTA_CONS + " " + orden + ", "+ m.OS_PRODUCTO + " " + orden +
                        " LIMIT  900",
                new String[]{codigo_cuadrilla, estado, "%"+ busqueda +"%", "%"+ busqueda +"%", "%"+ busqueda +"%", "%"+ busqueda +"%", "%"+ ciclo +"%", "%"+ ruta +"%", tip_orden}
        );
    }// Cierre Cargar Cursor Ordenes

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

    /**
     * Insertar datos del GPS
     * @param terminal
     * @param cuadrilla
     * @param latitud
     * @param longitud
     * @param altitud
     * @param velocidad
     * @param IMEI Homologado con vehiculo
     * @param estado
     * @param ose_codigo
     * @param bateria
     */
    public void insertarGps(String terminal,String cuadrilla,String latitud,String longitud, String altitud,
                            String velocidad, String IMEI,String estado, String ose_codigo, String bateria ){

        m.insertarGps(terminal, cuadrilla, latitud, longitud, altitud, velocidad, IMEI, estado, ose_codigo, bateria);
    }//Close insertGps

    /**
     * Genera un cursor con las ordenes pendientes por enviar
     * @param typePend 1 = Reader 2 = Check
     * @return
     */
    public Cursor ordenesPendintes(int typePend){

        String sqlConsulta = "SELECT " + m.TABLE_EJECUCION + ".*, " + m.TABLE_ORDEN + "." + m.OS_CLI_CONTRATO +
                "  FROM "+ m.TABLE_ORDEN +", " + m.TABLE_EJECUCION + "" +
                "  WHERE "+ m.TABLE_ORDEN + "." + m.OS_OSE_CODIGO + "="+ m.TABLE_EJECUCION + "." + m.EO_OSE_CODIGO +
                "  AND "+ m.TABLE_ORDEN + "." + m.OS_ESTADO +" = 27 AND " + m.TABLE_ORDEN + "." + m.OS_SINCRONIZADO + "=0 AND "
                + m.TABLE_ORDEN + "." + m.OS_OSE_TIP_ORDEN + "=209";

        if(typePend == 2)
            sqlConsulta = "SELECT " + m.TABLE_EJECUCION + ".*, " + m.TABLE_ORDEN + "." + m.OS_CLI_CONTRATO +
                    "  FROM "+ m.TABLE_ORDEN +", " + m.TABLE_EJECUCION + "" +
                    "  WHERE "+ m.TABLE_ORDEN + "." + m.OS_OSE_CODIGO + "="+ m.TABLE_EJECUCION + "." + m.EO_OSE_CODIGO +
                    "  AND "+ m.TABLE_ORDEN + "." + m.OS_ESTADO +" = 27 AND " + m.TABLE_ORDEN + "." + m.OS_SINCRONIZADO + "=0 AND "
                    + m.TABLE_ORDEN + "." + m.OS_OSE_TIP_ORDEN + "=211";

        return db.rawQuery(sqlConsulta, null);
    }// Cierre Ordenes Pendientes

    /**
     * Update Read Sincronizate
     * @param ose_codigos
     */
    public void updateSincronizado_lecturas(String ose_codigos){
        String sqlConsulta = "UPDATE " + m.TABLE_EJECUCION + " SET " + m.EO_SINCRONIZADO + " = 1 WHERE " + m.EO_OSE_CODIGO + " IN ( " + ose_codigos + " );";
        db.execSQL(sqlConsulta);

        sqlConsulta = "UPDATE " + m.TABLE_ORDEN + " SET " + m.OS_SINCRONIZADO + " = 1 WHERE " + m.OS_OSE_CODIGO + " IN ( " + ose_codigos + " );";
        db.execSQL(sqlConsulta);
    }// Close updateSincronizado_lecturas


    public void envioInformacionServidor(int intSesionId, String strToken) throws Exception {
        String[] lisDatosDescarga = null;
        String strRuta = "";
        String strOrdenId = "";
        String strApp = "";
        String strProceso = "";
        String strTablaApp = "";
        String strUsuario="REVMVEVD";
        String strClave= "cGVyZWlyYQ==";
        int intSesion = intSesionId;
        String strTok =strToken;

        String sqlConsulta = "SELECT *, "+ m.TABLE_SCM_ORDENES_TRABAJO +"."+m.OT_RUTA+ " FROM "+ m.TABLE_DETALLE_ORDEN_FACTURACION +" INNER JOIN "+ m.TABLE_SCM_ORDENES_TRABAJO+" ON "+ m.TABLE_DETALLE_ORDEN_FACTURACION+"."+m.ORDEN_ID+" = "+ m.TABLE_SCM_ORDENES_TRABAJO+"."+m.OT_ORDEN_ID+" WHERE " + m.TABLE_DETALLE_ORDEN_FACTURACION+"."+ m.SINCRONIZADO + " = 0";
        Cursor cursor = db.rawQuery(sqlConsulta, null);
        if (cursor.moveToFirst()) {
            do {

                lisDatosDescarga = null;
                strRuta = cursor.getString(cursor.getColumnIndex("RUTA"));
                strOrdenId = cursor.getString(cursor.getColumnIndex("ORDEN_ID"));
                ManejoDatos datos = new ManejoDatos(new DbHelper(MyApplication.getContext()).getDatabaseName(), MyApplication.getContext());
                strApp = "SCM";
                strProceso = "DESCARGA DATOS";
                strTablaApp = "FAC_LABORCONCEPTOS";
                //String strTablaApp2 = "SCM_ACCION_ANEXOS";
                //String strTablaApp3 = "SCM_ELEMENTOS_LECTURAS";
                String[] srtColumnas = {"RUTA|RUTA|VARCHAR2|N", "ORDEN_ID|ORDEN_ID|VARCHAR2|N", "IDCONCEPTO|IDCONCEPTO|NUMBER|N", "SALDOANTERIOR|SALDOANTERIOR|VARCHAR2|N", "CANTIDAD|CANTIDAD|VARCHAR2|N", "FACTOR|FACTOR|VARCHAR2|N", "TARIFA|TARIFA|VARCHAR2|N", "VALOR|VALOR|VARCHAR2|N"};
                if (lisDatosDescarga == null) {
                    lisDatosDescarga = datos.obtenerDatosDescarga(strTablaApp, srtColumnas, strRuta, strOrdenId);
                } else {
                    String[] lisDatosDescargaTemp = datos.obtenerDatosDescarga(strTablaApp, srtColumnas, strRuta, strOrdenId);
                    String[] Arrayconcat = new String[lisDatosDescarga.length + lisDatosDescargaTemp.length];
                    System.arraycopy(lisDatosDescarga, 0, Arrayconcat, 0, lisDatosDescarga.length);
                    System.arraycopy(lisDatosDescargaTemp, 0, Arrayconcat, lisDatosDescarga.length, lisDatosDescargaTemp.length);
                    lisDatosDescarga = Arrayconcat;
                }

                Call<EnvioDatos> call = new ApiClient().getApiClient().create(ApiServices.class).postDescargaDatos(intSesion,strTok,strApp,strProceso,strTablaApp,lisDatosDescarga);
                call.enqueue(new Callback<EnvioDatos>() {
                    @Override
                    public void onResponse(Call<EnvioDatos> call, Response<EnvioDatos> response) {
                        Log.i("DatosEnviados",response.body().getSucces().toString());
                    }

                    @Override
                    public void onFailure(Call<EnvioDatos> call, Throwable t) {
                        Log.i("DatosErroneos","Message: "+t.getMessage());
                    }
                });
                lisDatosDescarga = null;
                strTablaApp = "SCM_ELEMENTOS_LECTURAS";
                //String[] srtColumnas2 = {"RUTA|RUTA|VARCHAR2|N", "ORDEN_ID|ORDEN_ID|VARCHAR2|N", "TIPO|TIPO|VARCHAR2|N", "SERIE|SERIE|VARCHAR2|N", "MARCA|MARCA|VARCHAR2|N", "MODELO|MODELO|VARCHAR2|N", "TIPO_LECTURA|TIPO_LECTURA|VARCHAR2|N", "ENTEROS|ENTEROS|NUMBER|N", "DECIMALES|DECIMALES|NUMBER|N", "FACTOR_MULTIPLICACION|FACTOR_MULTIPLICACION|VARCHAR2|N", "FECHA_ANTERIOR|FECHA_ANTERIOR|DATE|N", "LECTURA_ANTERIOR|LECTURA_ANTERIOR|VARCHAR2|N", "LIMITE_INFERIOR_CONSUMO|LIMITE_INFERIOR_CONSUMO|VARCHAR2|N", "LIMITE_SUPERIOR_CONSUMO|LIMITE_SUPERIOR_CONSUMO|VARCHAR2|N", "UBICACION|UBICACION|NUMBER|N", "SECUENCIA|SECUENCIA|NUMBER|N", "CONSUMO1|CONSUMO1|NUMBER|N", "SOLCONSUMO1|SOLCONSUMO1|VARCHAR2|N", "CONSUMO2|CONSUMO2|NUMBER|N", "SOLCONSUMO2|SOLCONSUMO2|VARCHAR2|N", "FECHA_LECTURA|FECHA_LECTURA|DATE|N", "LECTURA_TOMADA|LECTURA_TOMADA|VARCHAR2|N", "OBSERVACION|OBSERVACION|VARCHAR2|N", "OBSERVACION_AD|OBSERVACION_AD|VARCHAR2|N", "OBSERVACION_TEXTO|OBSERVACION_TEXTO|VARCHAR2|N", "INTENTOS|INTENTOS|VARCHAR2|N", "CONSUMO|CONSUMO|NUMBER|N", "SOLCONSUMO|SOLCONSUMO|VARCHAR2|N", "FECHA_ACTUALIZACION|FECHA_ACTUALIZACION|DATE|N"};
                String[] srtColumnas2 = {"RUTA|RUTA|VARCHAR2|N", "ORDEN_ID|ORDEN_ID|VARCHAR2|N", "TIPO|TIPO|VARCHAR2|N", "SERIE|SERIE|VARCHAR2|N", "MARCA|MARCA|VARCHAR2|N", "TIPO_LECTURA|TIPO_LECTURA|VARCHAR2|N", "FECHA_LECTURA|FECHA_LECTURA|DATE|N", "LECTURA_TOMADA|LECTURA_TOMADA|VARCHAR2|N", "OBSERVACION|OBSERVACION|VARCHAR2|N", "OBSERVACION_AD|OBSERVACION_AD|VARCHAR2|N", "OBSERVACION_TEXTO|OBSERVACION_TEXTO|VARCHAR2|N", "INTENTOS|INTENTOS|VARCHAR2|N", "CONSUMO|CONSUMO|NUMBER|N", "SOLCONSUMO|SOLCONSUMO|VARCHAR2|N", "ESTADO|ESTADO|VARCHAR2|N"};
                if (lisDatosDescarga == null) {
                    lisDatosDescarga = datos.obtenerDatosDescarga(strTablaApp, srtColumnas2, strRuta, strOrdenId);
                    System.out.println(Arrays.toString(lisDatosDescarga));
                } else {

                    String[] lisDatosDescargaTemp = datos.obtenerDatosDescarga(strTablaApp, srtColumnas2, strRuta, strOrdenId);
                    String[] Arrayconcat = new String[lisDatosDescarga.length + lisDatosDescargaTemp.length];
                    System.arraycopy(lisDatosDescarga, 0, Arrayconcat, 0, lisDatosDescarga.length);
                    System.arraycopy(lisDatosDescargaTemp, 0, Arrayconcat, lisDatosDescarga.length, lisDatosDescargaTemp.length);
                    lisDatosDescarga = Arrayconcat;
                }

                Call<EnvioDatos> call2 = new ApiClient().getApiClient().create(ApiServices.class).postDescargaDatos(intSesion,strTok,strApp,strProceso,strTablaApp,lisDatosDescarga);
                call2.enqueue(new Callback<EnvioDatos>() {
                    @Override
                    public void onResponse(Call<EnvioDatos> call, Response<EnvioDatos> response) {

                    }

                    @Override
                    public void onFailure(Call<EnvioDatos> call, Throwable t) {

                    }
                });

                System.out.println(Arrays.toString(lisDatosDescarga));
            } while (cursor.moveToNext());//accessing data upto last row from table


            //String Menssage = this.EnvioDatosApi(strApp, strProceso, strTablaApp, strRuta,lisDatosDescarga);
            //System.out.println(Menssage);
//            Call<Auth> call = new ApiClient().getApiClient().create(ApiServices.class).postAuth(strUsuario,strClave);
//            call.enqueue(new Callback<Auth>() {
//                @Override
//                public void onResponse(Call<Auth> call, Response<Auth> response) {
//
//                    if (response.isSuccessful() && response.body() != null){
//                        SharedPreferences preferences= MyApplication.getContext().getSharedPreferences("credenciales", Context.MODE_PRIVATE);
//                        int intSesionId = response.body().getIntSesionId();
//                        String strToken = response.body().getStrToken();
//                        SharedPreferences.Editor editor= preferences.edit();
//                        editor.putInt("intSesionId", intSesionId);
//                        editor.putString("strToken", strToken);
//                        editor.commit();
//                            System.out.println(response.body().toString());
//                            //Log.d("IMPRIMIR LISTA: ", "HABER QUE LLEGA" + Arrays.toString(response.body().getLisDatosDescarga()));
//                            System.out.println(intSesionId);
//                            System.out.println(strToken);
////                            Toast.makeText(MyApplication.getContext(),
////                                    response.body().getIntSesionId(),
////                                    Toast.LENGTH_SHORT).show();
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<Auth> call, Throwable t) {
//
//                }
//
//            });
//            if (cursor.moveToFirst()) {
//            do {
//                strRuta = cursor.getString(cursor.getColumnIndex("RUTA_ORDEN"));
//                strOrdenId = cursor.getString(cursor.getColumnIndex("ORDEN_ID"));
//                ManejoDatos datos = new ManejoDatos(new DbHelper(MyApplication.getContext()).getDatabaseName(), MyApplication.getContext());
//                strApp = "SCM";
//                strProceso = "DESCARGA DATOS";
//                strTablaApp = "SCM_ELEMENTOS_LECTURAS";
//                //String strTablaApp2 = "SCM_ACCION_ANEXOS";
//                //String strTablaApp3 = "SCM_ELEMENTOS_LECTURAS";
//                String[] srtColumnas = {"RUTA|RUTA|VARCHAR2|N", "ORDEN_ID|ORDEN_ID|VARCHAR2|N", "TIPO|TIPO|VARCHAR2|N", "SERIE|SERIE|VARCHAR2|N", "MARCA|MARCA|VARCHAR2|N", "TIPO_LECTURA|TIPO_LECTURA|VARCHAR2|N", "FECHA_LECTURA|FECHA_LECTURA|DATE|N", "LECTURA_TOMADA|LECTURA_TOMADA|VARCHAR2|N", "OBSERVACION|OBSERVACION|VARCHAR2|N", "OBSERVACION_AD|OBSERVACION_AD|VARCHAR2|N", "OBSERVACION_TEXTO|OBSERVACION_TEXTO|VARCHAR2|N", "INTENTOS|INTENTOS|VARCHAR2|N", "CONSUMO|CONSUMO|NUMBER|N", "SOLCONSUMO|SOLCONSUMO|VARCHAR2|N"};
//                if (lisDatosDescarga == null) {
//                    lisDatosDescarga = datos.obtenerDatosDescarga(strTablaApp, srtColumnas, strRuta, strOrdenId);
//                } else {
//                    String[] lisDatosDescargaTemp = datos.obtenerDatosDescarga(strTablaApp, srtColumnas, strRuta, strOrdenId);
//                    String[] Arrayconcat = new String[lisDatosDescarga.length + lisDatosDescargaTemp.length];
//                    System.arraycopy(lisDatosDescarga, 0, Arrayconcat, 0, lisDatosDescarga.length);
//                    System.arraycopy(lisDatosDescargaTemp, 0, Arrayconcat, lisDatosDescarga.length, lisDatosDescargaTemp.length);
//                    lisDatosDescarga = Arrayconcat;
//                }
//
//                System.out.println(Arrays.toString(lisDatosDescarga));
//            } while (cursor.moveToNext());//accessing data upto last row from table
////            Call<EnvioDatos> call = new ApiClient().getApiClient().create(ApiServices.class).postDescargaDatos(strApp,strProceso,strTablaApp,strRuta,lisDatosDescarga);
////            call.enqueue(new Callback<EnvioDatos>() {
////                @Override
////                public void onResponse(Call<EnvioDatos> call, Response<EnvioDatos> response) {
////                    if (response.isSuccessful() && response.body() != null){
////                        Boolean succes= response.body().getSucces();
////                        if(succes){
////                           // Log.d("AQUI FUE SCM LECTURAS: ", "post submitted to API." + response.body().getLisDatosDescarga());
////                            System.out.println(response.body().getMessage());
////                            Toast.makeText(MyApplication.getContext(),
////                                    response.body().getMessage(),
////                                    Toast.LENGTH_SHORT).show();
////
////
////                        }
////                    }else{
////                        System.out.println(response);
////                        Toast.makeText(MyApplication.getContext(),
////                                response.body().getMessage(),
////                                Toast.LENGTH_SHORT).show();
////                    }
////                }
////
////                @Override
////                public void onFailure(Call<EnvioDatos> call, Throwable t) {
////                    Toast.makeText(MyApplication.getContext(),
////                            t.getLocalizedMessage(),
////                            Toast.LENGTH_SHORT).show();
////
////                }
////            });


////            Call<EnvioDatos> call2 = new ApiClient().getApiClient().create(ApiServices.class).postDescargaDatos(sesionId,token,strApp,strProceso,strTablaApp,lisDatosDescarga);
////            call2.enqueue(new Callback<EnvioDatos>() {
////                @Override
////                public void onResponse(Call<EnvioDatos> call, Response<EnvioDatos> response) {
////
////                }
////
////                @Override
////                public void onFailure(Call<EnvioDatos> call, Throwable t) {
////
////                }
////            });
//
////            Call<EnvioDatos> call = new ApiClient().getApiClient().create(ApiServices.class).postDescargaDatos(strApp,strProceso,strTablaApp,strRuta,lisDatosDescarga);
////            call.enqueue(new Callback<EnvioDatos>() {
////                @Override
////                public void onResponse(Call<EnvioDatos> call, Response<EnvioDatos> response) {
////                    if (response.isSuccessful() && response.body() != null){
////                        Boolean succes= response.body().getSucces();
////                        if(succes){
////                            System.out.println(response.body().toString());
////                            //Log.d("IMPRIMIR LISTA: ", "HABER QUE LLEGA" + Arrays.toString(response.body().getLisDatosDescarga()));
////                            System.out.println(response.body().getMessage());
////                            Toast.makeText(MyApplication.getContext(),
////                                    response.body().getMessage(),
////                                    Toast.LENGTH_SHORT).show();
////
////
////                        }
////                    }else{
////                        System.out.println(response);
////                        Toast.makeText(MyApplication.getContext(),
////                                response.body().getMessage(),
////                                Toast.LENGTH_SHORT).show();
////                    }
////                }
////
////                @Override
////                public void onFailure(Call<EnvioDatos> call, Throwable t) {
////                    Toast.makeText(MyApplication.getContext(),
////                            t.getLocalizedMessage(),
////                            Toast.LENGTH_SHORT).show();
////
////                }
////            });
//            lisDatosDescarga= null;
//
//        }// Close updateSincronizado_lecturas
//        if (cursor.moveToFirst()) {
//            do {
//                strRuta = cursor.getString(cursor.getColumnIndex("RUTA_ORDEN"));
//                strOrdenId = cursor.getString(cursor.getColumnIndex("ORDEN_ID"));
//                ManejoDatos datos = new ManejoDatos(new DbHelper(MyApplication.getContext()).getDatabaseName(), MyApplication.getContext());
//                strApp = "SCM";
//                strProceso = "DESCARGA DATOS";
//                strTablaApp = "SCM_ELEMENTOS_LECTURAS";
//                //String strTablaApp2 = "SCM_ACCION_ANEXOS";
//                //String strTablaApp3 = "SCM_ELEMENTOS_LECTURAS";
//                String[] srtColumnas = {"RUTA|RUTA|VARCHAR2|N", "ORDEN_ID|ORDEN_ID|VARCHAR2|N", "TIPO|TIPO|VARCHAR2|N", "SERIE|SERIE|VARCHAR2|N", "MARCA|MARCA|VARCHAR2|N", "TIPO_LECTURA|TIPO_LECTURA|VARCHAR2|N", "FECHA_LECTURA|FECHA_LECTURA|DATE|N", "LECTURA_TOMADA|LECTURA_TOMADA|VARCHAR2|N", "OBSERVACION|OBSERVACION|VARCHAR2|N", "OBSERVACION_AD|OBSERVACION_AD|VARCHAR2|N", "OBSERVACION_TEXTO|OBSERVACION_TEXTO|VARCHAR2|N", "INTENTOS|INTENTOS|VARCHAR2|N", "CONSUMO|CONSUMO|NUMBER|N", "SOLCONSUMO|SOLCONSUMO|VARCHAR2|N"};
//                if (lisDatosDescarga == null) {
//                    lisDatosDescarga = datos.obtenerDatosDescarga(strTablaApp, srtColumnas, strRuta, strOrdenId);
//                } else {
//                    String[] lisDatosDescargaTemp = datos.obtenerDatosDescarga(strTablaApp, srtColumnas, strRuta, strOrdenId);
//                    String[] Arrayconcat = new String[lisDatosDescarga.length + lisDatosDescargaTemp.length];
//                    System.arraycopy(lisDatosDescarga, 0, Arrayconcat, 0, lisDatosDescarga.length);
//                    System.arraycopy(lisDatosDescargaTemp, 0, Arrayconcat, lisDatosDescarga.length, lisDatosDescargaTemp.length);
//                    lisDatosDescarga = Arrayconcat;
//                }
//
//                System.out.println(Arrays.toString(lisDatosDescarga));
//            } while (cursor.moveToNext());//accessing data upto last row from table
////            Call<EnvioDatos> call = new ApiClient().getApiClient().create(ApiServices.class).postDescargaDatos(strApp,strProceso,strTablaApp,strRuta,lisDatosDescarga);
////            call.enqueue(new Callback<EnvioDatos>() {
////                @Override
////                public void onResponse(Call<EnvioDatos> call, Response<EnvioDatos> response) {
////                    if (response.isSuccessful() && response.body() != null){
////                        Boolean succes= response.body().getSucces();
////                        if(succes){
////                           // Log.d("AQUI FUE SCM LECTURAS: ", "post submitted to API." + response.body().getLisDatosDescarga());
////                            System.out.println(response.body().getMessage());
////                            Toast.makeText(MyApplication.getContext(),
////                                    response.body().getMessage(),
////                                    Toast.LENGTH_SHORT).show();
////
////
////                        }
////                    }else{
////                        System.out.println(response);
////                        Toast.makeText(MyApplication.getContext(),
////                                response.body().getMessage(),
////                                Toast.LENGTH_SHORT).show();
////                    }
////                }
////
////                @Override
////                public void onFailure(Call<EnvioDatos> call, Throwable t) {
////                    Toast.makeText(MyApplication.getContext(),
////                            t.getLocalizedMessage(),
////                            Toast.LENGTH_SHORT).show();
////
////                }
////            });
        }
    }

    /**
     * Retorna Cursor con la lista de pendientes
     * @return cursor con fotos pendientes por enviar
     */

    public Cursor fotosPendintes(){

        String sqlConsulta = "SELECT * " +
                "  FROM "+ m.TABLE_FOTO +" "+
                "  WHERE " + m.FT_SINCRONIZADO + "=0";
        return db.rawQuery(sqlConsulta, null);
    }// Cierre material disponible

    /**
     * VERIFICA SI LA FOTO CONTINUA PENDIENTE POR ENVIAR
     * @param id_foto
     * @return
     */
    public boolean existeFotoPendiente(String id_foto){

        boolean exiteFoto = false;
        String[] columnas = new String[]{ m.FT_ID};
        Cursor cursor = db.query( m.TABLE_FOTO, columnas,  m.FT_ID + "=? AND " +  m.FT_SINCRONIZADO + " =0",new String[]{id_foto},null,null,null,null);
        if (cursor.moveToFirst()) {
            exiteFoto = true;
        }
        cursor.close();
        return exiteFoto;
    }//Close existeFotoPendiente

//    public String EnvioDatosApi (String strApp, String strProceso, String strTablaApp, String strRuta, String [] lisDatosDescarga){
//        final String[] resultadoEnvio = new String[1];
//        Call<EnvioDatos> call = new ApiClient().getApiClient().create(ApiServices.class).postDatos(strApp,strProceso,strTablaApp,strRuta,lisDatosDescarga);
//        call.enqueue(new Callback<EnvioDatos>() {
//            @Override
//            public void onResponse(Call<EnvioDatos> call, Response<EnvioDatos> response) {
//                if (response.isSuccessful() && response.body() != null){
//                    Boolean succes= response.body().getSucces();
//                    if(succes){
//                        Log.d("AQUI FUE: ", "post submitted to API." + response.body().getLisDatosDescarga());
//                        System.out.println(response.body().getMessage());
//                        resultadoEnvio[0] = response.body().getMessage().toString();
//                        Toast.makeText(MyApplication.getContext(),
//                                response.body().getMessage(),
//                                Toast.LENGTH_SHORT).show();
//
//
//                    }
//                }else{
//                    System.out.println(response);
//                    Toast.makeText(MyApplication.getContext(),
//                            response.body().getMessage(),
//                            Toast.LENGTH_SHORT).show();
//                    resultadoEnvio[0] = response.body().getMessage().toString();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<EnvioDatos> call, Throwable t) {
//                Toast.makeText(MyApplication.getContext(),
//                        t.getLocalizedMessage(),
//                        Toast.LENGTH_SHORT).show();
//
//            }
//        });
//
//        return resultadoEnvio[0];
//    }

    /**
     * Marca las fotos enviadas con sincronizado = 1
     * @param ose_codigo
     * @param fecha
     */
    public void actualizarEnvioFoto(String ose_codigo, String fecha){

        db.update(m.TABLE_FOTO, generarCvUpdateFotoEnviada(1), m.FT_OSE_CODIGO + "=? AND " + m.FT_FECHA + "=?", new String[] {ose_codigo,fecha});
    }/**


     * Marca las fotos enviadas con sincronizado = 1
     * @param id_foto
     */
    public void actualizarEnvioFoto(String id_foto){

        db.update(m.TABLE_FOTO, generarCvUpdateFotoEnviada(1), m.FT_ID + "=?", new String[]{id_foto});
    }

    /**
     * Genera bolsa con el valor sincronizado
     * @param sincronizado
     * @return
     */
    private ContentValues generarCvUpdateFotoEnviada(int sincronizado){

        ContentValues valores = new ContentValues();
        valores.put(m.FT_SINCRONIZADO, sincronizado);
        return valores;
    }// Close generarCvUpdateFotoEnviada

    /**
     * Techinical orders returns
     * @param cuadrilla
     * @return
     */
    public String getOrdenesCuadrilla(String cuadrilla){

        String listaOrdenes = "";
        String[] columnas = new String[]{m.OS_OSE_CODIGO};
        Cursor cursor = db.query(m.TABLE_ORDEN, columnas, m.OS_CUADRILLA  + "=?",new String[]{cuadrilla},null,null,null);
        Boolean firsTime = true;
        if (cursor.moveToFirst()) {
            do {
                if(!firsTime){
                    listaOrdenes += ",";
                }else firsTime = false;

                listaOrdenes += String.valueOf(cursor.getInt(cursor.getColumnIndex(m.OS_OSE_CODIGO)));
            } while (cursor.moveToNext());
        }
        cursor.close(); // closing connection
        return listaOrdenes;// returning lables
    }// Close getOrdenesCuadrilla

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
     * Retorna observacion segun codigo
     * @param tip_codigo
     * @return
     */
    public String getObsCodigo(String tip_codigo){

        String nombre_obs = "";
        String[] columnas = new String[]{m.OB_TIP_NOMBRE};
        Cursor cursor = db.query(m.TABLE_OBSERVACION, columnas, m.OB_TIP_CODIGO  + "=?" ,new String[]{tip_codigo},null,null,null,null);
        if (cursor.moveToFirst()) {
            nombre_obs = cursor.getString(cursor.getColumnIndex(m.OB_TIP_NOMBRE));
        }
        cursor.close();
        return nombre_obs;
    }// Cierre Retonar Codigo Observacion

    /**
     * Existe Orden con el mismo cliente ejecutada
     * @param ose_codigo
     * @param suscriptor
     * @return
     */
    public boolean existJobPreExecute(int ose_codigo, int suscriptor){

        boolean exitJob = false;
        String sqlConsulta = "SELECT o.estado from orden_servicio o " +
                " WHERE o.cli_contrato= '" + suscriptor + "' AND o.ose_codigo != " + ose_codigo;

        Cursor cursor = db.rawQuery(sqlConsulta, null);
        if (cursor.moveToFirst()) {
            do {
                if(cursor.getInt(0) == 27)
                    exitJob = true;
            } while (cursor.moveToNext());
        }else exitJob = true;
        return exitJob;
    }

    public Cursor medidoresPendientes(){

        String sqlConsulta =
                "  SELECT * " +
                "   FROM "+ m.TABLE_MEDIDOR_ENCONTRADO +" "+
                "  WHERE " + m.MED_SINCRONIZADO + "=0";
        return db.rawQuery(sqlConsulta, null);
    }// Cierre material disponible

    public void actualizarEnvioMedidores(String med_id){

        db.update(m.TABLE_MEDIDOR_ENCONTRADO, generarCvUpdateMedidorEnviado(1), m.MED_ID + "=?", new String[]{med_id});
    }

    private ContentValues generarCvUpdateMedidorEnviado(int sincronizado){

        ContentValues valores = new ContentValues();
        valores.put(m.MED_SINCRONIZADO, sincronizado);
        return valores;
    }// Close generarCvUpdateFotoEnviada
}
