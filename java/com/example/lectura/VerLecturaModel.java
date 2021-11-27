package com.example.lectura;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.app.Application;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.actsis.fensliq.Critica;
import com.actsis.fensliq.Liquidador;
import com.actsis.fensliq.ManejoDatos;
import com.example.Logueo.DataBaseManager;
import com.example.Logueo.DbHelper;
import com.example.Logueo.ObtenerContexto;
import com.example.apiretrofit.ApiClient;
import com.example.apiretrofit.ApiServices;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.PendingIntent.getActivity;

/**
 * Created by DELTEC on 10/07/2015.
 */
public class VerLecturaModel extends Application {
    private SQLiteDatabase db;
    private DataBaseManager m;
    ApiServices mAPIService;
    ProgressDialog progressDialog;


    /**
     * Constructor Manejador de Bd
     * @param  context  Contexto
     */
    public VerLecturaModel(Context context){

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


    public List<String> getAllLabels(int checkCausa){
 // AUTOR Alejo Duran
        if(checkCausa == 1){
            List<String> labels = new ArrayList<String>();
            String[] columnas = new String[]{m.CNL_CODCAUSA, m.CNL_DESCRIPCION};
            String filtro = "";
            //String filtro = m.OB_CTI_CODIGO  + "=?";

        /*if(tipo==0)
            filtro += " AND " + m.OB_TIP_CODIGO + " IN ( 0, 779, 765, 770, 773, 775, 777, 778, 995, 807, 1006, 1017, 1018 )"; */
            //String sqlConsulta = "SELECT *" + " FROM "+ m.TABLE_SCM_ELEMENTOS_LECTURA + " WHERE " + m.ELD_SERIE + " = " + serie_medidor_encontrado;
            //Cursor cursor = db.rawQuery(sqlConsulta, null);
            Cursor cursor = db.query(m.TABLE_SCM_CAUSANOLECTURA, columnas, null, null, null, null, null, null);
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    labels.add(cursor.getString(cursor.getColumnIndex(m.OBC_DESCRIPCION)));
                } while (cursor.moveToNext());
            }
            // closing connection
            cursor.close();
            // returning lables
            return labels;
        }else {

            List<String> labels = new ArrayList<String>();
            String[] columnas = new String[]{m.OBC_CODOBSERVACION, m.OBC_DESCRIPCION};
            String filtro = "";
            //String filtro = m.OB_CTI_CODIGO  + "=?";

        /*if(tipo==0)
            filtro += " AND " + m.OB_TIP_CODIGO + " IN ( 0, 779, 765, 770, 773, 775, 777, 778, 995, 807, 1006, 1017, 1018 )"; */
            //String sqlConsulta = "SELECT *" + " FROM "+ m.TABLE_SCM_ELEMENTOS_LECTURA + " WHERE " + m.ELD_SERIE + " = " + serie_medidor_encontrado;
            //Cursor cursor = db.rawQuery(sqlConsulta, null);
            Cursor cursor = db.query(m.TABLE_SCM_OBSCONSUMO, columnas, null, null, null, null, null, null);
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    labels.add(cursor.getString(cursor.getColumnIndex(m.OBC_DESCRIPCION)));
                } while (cursor.moveToNext());
            }
            // closing connection
            cursor.close();
            // returning lables
            return labels;
        }


    }

    /**
     * Cargar Informacion de la lectura
     * @param ose_codigo
     * @return cursor
     */
    public Cursor cargarDatosLectura(String ose_codigo){

        String sqlConsulta = "SELECT o.consumo_promedio, o.lectura_anterior, o.estado, o.direccion2, o.cantidad_digitos, e.indicador_lectura, " +
                "        e.lectura, e.codigo_observacion_no_lectura, e.codigo_observacion_lectura, e.intentos" +
                "  	FROM orden_servicio AS o LEFT JOIN ejecucion_orden AS e ON o.ose_codigo = e.ose_codigo " +
                "   WHERE o.ose_codigo = " + ose_codigo;
        return db.rawQuery(sqlConsulta, null);
    }// Cierre Cargar Cursor Ordenes

    /**
     * Cargar Ultima foto Lectura
     *  @param  ose_codigo
     */
    public String cargarUltimaFoto(String ose_codigo){

        String urlFoto = "";
        Cursor cursor;
        String[] columnas = new String[]{m.FT_FOTO_URL};
        cursor = db.query(m.TABLE_FOTO, columnas, m.FT_OSE_CODIGO + "=?", new String[]{ose_codigo},null,null,m.FT_ID + " DESC" ,"1");

        if(cursor.moveToFirst()){
            urlFoto = cursor.getString(cursor.getColumnIndex("foto_url"));
        }
        cursor.close();

        return urlFoto;
    }// Cierre Cargar Ultima foto Lectura

    /**
     * Retonar el Codigo tip_codigo de la Observacion Solicitada
     * @param tip_nombre Nombre de la Observacion
     * @param cti_codigo Clasificacion de la Observacion
     * @return Codigo de la Orden
     */
    public int getCodigoObs(String tip_nombre, String cti_codigo){

        int tipo_obs = 0;

        String[] columnas = new String[]{m.OB_TIP_CODIGO};
        Cursor cursor = db.query(m.TABLE_OBSERVACION, columnas, m.OB_TIP_NOMBRE  + " LIKE ? ",new String[]{"%" + tip_nombre},null,
                null,null,null);

        if (cursor.moveToFirst()) {
            tipo_obs = cursor.getInt(cursor.getColumnIndex(m.OB_TIP_CODIGO));
        }
        cursor.close();
        return tipo_obs;
    }// Cierre Retonar Codigo Observacion

    public int getCodigoObs2(String tip_nombre, String cti_codigo){

        int tipo_obs = 0;

        String[] columnas = new String[]{m.OB_TIP_CODIGO};
        Cursor cursor = db.query(m.TABLE_OBSERVACION, columnas, m.OB_TIP_NOMBRE  + " LIKE ?"
                     ,new String[]{"%" + tip_nombre},null,
                null,null,null);

        if (cursor.moveToFirst()) {
            tipo_obs = cursor.getInt(cursor.getColumnIndex(m.OB_TIP_CODIGO));
        }
        cursor.close();
        return tipo_obs;
    }// CIERRE DE METODO OBTIENE CODIGO @AUTOR ALEJO DURAN

    public String getCodigoObs3(String tip_nombre, String estado){

        String tipo_obs = "";
        if (estado == "1") {
            String[] columnas = new String[]{m.OB_ID};
            Cursor cursor = db.query(m.TABLE_OBSERVACION, columnas, m.OB_TIP_NOMBRE  + " LIKE ?"
                    ,new String[]{"%" + tip_nombre},null,
                    null,null,null);

            if (cursor.moveToFirst()) {
                tipo_obs = cursor.getString(cursor.getColumnIndex(m.OB_ID));
            }
            cursor.close();
            return tipo_obs;
        }
        String[] columnas = new String[]{m.OB_SOLCONSUMO};
        Cursor cursor = db.query(m.TABLE_OBSERVACION, columnas, m.OB_TIP_NOMBRE  + " LIKE ?"
                ,new String[]{"%" + tip_nombre},null,
                null,null,null);

        if (cursor.moveToFirst()) {
            tipo_obs = cursor.getString(cursor.getColumnIndex(m.OB_SOLCONSUMO));
        }
        cursor.close();
        return tipo_obs;
    }// CIERRE DE METODO OBTIENE CODIGO @AUTOR ALEJO DURAN

    public Cursor cargarCursorOrden(String serie_medidor_encontrado){
        String sqlConsulta = "SELECT *" + " FROM "+ m.TABLE_SCM_ELEMENTOS_LECTURA + " WHERE " + m.ELD_SERIE + " = " + serie_medidor_encontrado;
        return db.rawQuery(sqlConsulta, null);
    }
    /**
     * Ingresa o actualizar una Lectura/Motivo en la tabla ejecucion_orden
     * @param ose_codigo
     * @param lectura
     * @param codigo_no_lectura
     * @param codigo_obs_lectura
     * @param indicador_lectura
     * @param critica
     * @param intentos
     * @param encontro_medidor
     * @param medidor_correcto
     * @param serie_medidor_encontrado
     */
    public void ingresarActualizarLectura(
            String ose_codigo, String lectura,int codigo_obs_lectura, int codigo_obs_lectura_normal, int codigo_no_lectura,
            int indicador_lectura, int critica,int intentos,int encontro_medidor,
            int medidor_correcto,String serie_medidor_encontrado, String longitud, String latitud,
            int consumo, String reconexion_no, String motivoConsumo, String obsLectura, String estado_fes, int franja_consumo){

//        int rowsAff = 0;
        //ACTUALIZO CANTIDAD STOCK A CERO

        if(estado_fes == null ){
            int rowsAff = 0;
            //ACTUALIZO CANTIDAD STOCK A CERO
            rowsAff = db.update(m.TABLE_EJECUCION, generarContentValuesUpdateLectura(lectura, codigo_obs_lectura_normal,codigo_no_lectura, indicador_lectura,
                    critica, intentos, encontro_medidor,  medidor_correcto, serie_medidor_encontrado,longitud, latitud, consumo, reconexion_no),
                    m.EO_OSE_CODIGO + "=?", new String[] {ose_codigo});

            System.out.println("ACTUALIZO ORDEN LECTURA " + rowsAff);

            if(rowsAff == 0){
                db.insert(m.TABLE_EJECUCION, null, generarContentValuesInstertarLectura(ose_codigo, lectura, codigo_obs_lectura_normal, codigo_no_lectura, indicador_lectura,
                        critica, intentos, encontro_medidor,  medidor_correcto, serie_medidor_encontrado, longitud, latitud, consumo, reconexion_no));
                System.out.println( "INSERTO LECTURA " );
            }

        }else if(estado_fes != null){
/*            int rowsAff = 0;
            rowsAff = db.update(m.TABLE_EJECUCION, generarContentValuesUpdateLectura(lectura, codigo_obs_lectura_normal,codigo_no_lectura, indicador_lectura,
                    critica, intentos, encontro_medidor,  medidor_correcto, serie_medidor_encontrado,longitud, latitud, consumo, reconexion_no),
                    m.EO_OSE_CODIGO + "=?", new String[] {ose_codigo});
            if(rowsAff != 0){
                String sqlConsulta = "SELECT *" + " FROM "+ m.TABLE_SCM_ELEMENTOS_LECTURA + " WHERE " + m.ELL_ERIE + " = " + serie_medidor_encontrado + " AND " + m.ELL_TIPO_LECTURA + " = " + franja_consumo ;
                Cursor cursor = db.rawQuery(sqlConsulta, null);
                if (cursor.moveToFirst()) {
                    do {
                        String strRuta = cursor.getString(cursor.getColumnIndex("RUTA"));
                        String strOrdenId = cursor.getString(cursor.getColumnIndex("ORDEN_ID"));
                        String strTipoLectura = cursor.getString(cursor.getColumnIndex("TIPO_LECTURA"));
                        int intConsumo = consumo;
                        String strLectura = lectura;
                        String strObservacion =  cursor.getString(cursor.getColumnIndex("OBSERVACION")) ;
                        //strObservacion
                        String strSolConsumo = "";


                        SimpleDateFormat dateFormat = new SimpleDateFormat(
                                "dd-MM-YYYY HH:mm:ss", Locale.getDefault());
                        Date datFechaLectura = new Date();
                        dateFormat.format(datFechaLectura);
                        System.out.println(datFechaLectura);
                        Boolean bolCriticaFens = false;
                        Boolean result = false;
                        try {
                            Critica critica2 = new Critica(new DbHelper(MyApplication.getContext()).getDatabaseName(), MyApplication.getContext());
                            if(!strLectura.isEmpty()){
                                result = critica2.validarLectura(strRuta, strOrdenId, strLectura, strTipoLectura);
                                System.out.println(result);
                            }

                            if (result){ // 1. PASO AQUI SE VERIFICA QUE VALIDAR LECTURA SEA TRUE
                                strObservacion =  this.getCodigoObs3(obsLectura, "1");
                                strSolConsumo = this.getCodigoObs3(obsLectura,"2");
                                bolCriticaFens = result; // SE GUARDA EL VALOR
                                String strLecturaIngreso = strLectura+"000";
                                try{
                                    if(obsLectura.equals("-")){
                                        bolCriticaFens = false; // SE GUARDA EL VALOR
                                        critica2.ingresarLectura(strRuta, strOrdenId, strTipoLectura, intConsumo, strLecturaIngreso, strObservacion, strSolConsumo, datFechaLectura, bolCriticaFens); //INGRESAR LECTURA
                                    }else {
                                        critica2.ingresarLectura(strRuta, strOrdenId, strTipoLectura, intConsumo, strLecturaIngreso, strObservacion, strSolConsumo, datFechaLectura, bolCriticaFens);
                                    }//INGRESAR LECTURA
                                }catch (Exception e){
                                    String mensaje = e.getMessage();
                                    if(mensaje == "Cliente ya fue FACTURADO."){
                                    }
                                }
                                int intFactorRedondeo = 0; // DETERMINAR VALOR CON ACTSIS
                                Double dblLimiteRedondeo= 0.0; // DETERMINAR VALOR CON ACTSIS
                                Liquidador liquida= new Liquidador(new DbHelper(MyApplication.getContext()).getDatabaseName(), intFactorRedondeo, dblLimiteRedondeo, MyApplication.getContext());
                                liquida.liquidarOt(strRuta, strOrdenId);
                                String sqlConsultaupdate = "UPDATE " + m.TABLE_DETALLE_ORDEN_FACTURACION + " SET " + m.SINCRONIZADO + " = 0 WHERE " + m.ORDEN_ID + " IN ( " + strOrdenId + " );";
                                db.execSQL(sqlConsultaupdate);



                            }else{
                                if (obsLectura.equals("-")){
                                    strObservacion =  this.getCodigoObs3(motivoConsumo, "1");
                                    strSolConsumo = this.getCodigoObs3(motivoConsumo,"2");

                                }else{
                                    strObservacion =  this.getCodigoObs3(obsLectura, "1");
                                    strSolConsumo = this.getCodigoObs3(obsLectura,"2");
                                }

                                if(obsLectura.equals("")){
                                    bolCriticaFens = false; // SE GUARDA EL VALOR
                                    critica2.ingresarLectura(strRuta, strOrdenId, strTipoLectura, intConsumo, strLectura, strObservacion, strSolConsumo, datFechaLectura, bolCriticaFens); //INGRESAR LECTURA
                                }else {
                                    critica2.ingresarLectura(strRuta, strOrdenId, strTipoLectura, intConsumo, strLectura, strObservacion, strSolConsumo, datFechaLectura, bolCriticaFens);
                                }//INGRESAR LECTURA
                                critica2.marcarNoEntreago(strRuta, strOrdenId);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } while (cursor.moveToNext());//accessing data upto last row from table
                }

                rowsAff = db.update(m.TABLE_EJECUCION, generarContentValuesUpdateLectura(lectura, codigo_obs_lectura_normal,codigo_no_lectura, indicador_lectura,
                        critica, intentos, encontro_medidor,  medidor_correcto, serie_medidor_encontrado,longitud, latitud, consumo, reconexion_no),
                        m.EO_OSE_CODIGO + "=?", new String[] {ose_codigo});

                System.out.println("ACTUALIZO ORDEN LECTURA " + rowsAff);*/
/*            }else if(rowsAff == 0){*/
                //new obtenerFactura();
                //db.insert(m.TABLE_SCM_ELEMENTOS_LECTURA,null, generarContentValuesInstertarLectura(lectura,) );
                String sqlConsulta2 = "SELECT *" + " FROM "+ m.TABLE_SCM_ELEMENTOS_LECTURA + " WHERE " + m.ELL_ERIE + " = " + serie_medidor_encontrado + " AND " + m.ELL_TIPO_LECTURA + " = " + franja_consumo ;
                Cursor cursor2 = db.rawQuery(sqlConsulta2, null);
                if (cursor2.moveToFirst()) {
                    do {
                        String strRuta = cursor2.getString(cursor2.getColumnIndex("RUTA"));
                        String strOrdenId = cursor2.getString(cursor2.getColumnIndex("ORDEN_ID"));
                        String strTipoLectura = cursor2.getString(cursor2.getColumnIndex("TIPO_LECTURA"));
                        int factorConsumo = cursor2.getInt(cursor2.getColumnIndex("FACTOR_MULTIPLICACION"));
                        int intConsumo = consumo;
                        String strLectura = lectura;
                        String strObservacion =  cursor2.getString(cursor2.getColumnIndex("OBSERVACION")) ;
                        int decimales = cursor2.getInt(cursor2.getColumnIndex("DECIMALES"));
                        //strObservacion
                        String strSolConsumo = "";
                        SimpleDateFormat dateFormat = new SimpleDateFormat(
                                "dd-MM-yyyy HH:mm:ss", Locale.getDefault());
                        Date datFechaLectura = new Date();
                        dateFormat.format(datFechaLectura);
                        System.out.println(datFechaLectura);
                        Boolean bolCriticaFens = false;
                        Boolean result2 = false;

                        try {

                            Critica criticanew = new Critica(new DbHelper(MyApplication.getContext()).getDatabaseName(), MyApplication.getContext());
                            if(!strLectura.isEmpty()){
                                    result2 = criticanew.validarLectura(strRuta, strOrdenId, strLectura, strTipoLectura);
                                    System.out.println(result2);
                            }

                            if (result2){ // 1. PASO AQUI SE VERIFICA QUE VALIDAR LECTURA SEA TRUE
                                //String strLecturaIngreso = strLectura+"000";
                                codigo_obs_lectura = this.getCodigoObs(obsLectura, "61");
                                strObservacion =  this.getCodigoObs3(obsLectura, "1");
                                bolCriticaFens = result2; // SE GUARDA EL VALOR
                                try{
                                    if(obsLectura.equals("-")){
                                        if(decimales == 3) {
                                            strSolConsumo = this.getCodigoObs3(obsLectura,"2");
                                            String strlecturadecimales = strLectura+"000";
                                            intConsumo = intConsumo * factorConsumo / 10000;
                                            bolCriticaFens = true; // SE GUARDA EL VALOR
                                            criticanew.ingresarLectura(strRuta, strOrdenId, strTipoLectura, intConsumo, strlecturadecimales, strObservacion, strSolConsumo, datFechaLectura, bolCriticaFens); //INGRESAR LECTURA
                                        }else {
                                            strSolConsumo = this.getCodigoObs3(obsLectura,"2");
                                            bolCriticaFens = true; // SE GUARDA EL VALOR
                                            criticanew.ingresarLectura(strRuta, strOrdenId, strTipoLectura, intConsumo, strLectura, strObservacion, strSolConsumo, datFechaLectura, bolCriticaFens); //INGRESAR LECTURA
                                        }
                                    }else {
                                        if(decimales == 3) {
                                            bolCriticaFens = true; // SE GUARDA EL VALOR
                                            String strlecturadecimales = strLectura+"000";
                                            intConsumo = intConsumo * factorConsumo / 10000;
                                            criticanew.ingresarLectura(strRuta, strOrdenId, strTipoLectura, intConsumo, strlecturadecimales, strObservacion, strSolConsumo, datFechaLectura, bolCriticaFens);
                                        }else {
                                            bolCriticaFens = true; // SE GUARDA EL VALOR
                                            criticanew.ingresarLectura(strRuta, strOrdenId, strTipoLectura, intConsumo, strLectura, strObservacion, strSolConsumo, datFechaLectura, bolCriticaFens);
                                        }
                                    }//INGRESAR LECTURA
                                }catch (Exception e){
                                    String mensaje = e.getMessage();
                                    if(mensaje == "Cliente ya fue FACTURADO."){
                                    }
                                }

                                int intFactorRedondeo = 1;
                                Double dblLimiteRedondeo= 0.5;
                                Liquidador liquidanew= new Liquidador(new DbHelper(MyApplication.getContext()).getDatabaseName(), intFactorRedondeo, dblLimiteRedondeo, MyApplication.getContext());
                                liquidanew.liquidarOt(strRuta, strOrdenId);
                            }else{
                                if(strLectura == "" && !motivoConsumo.equals("-")){
                                    String strLecturaConsumo = strLectura;
                                    strObservacion =  this.getCodigoObs3(motivoConsumo, "1");
                                    strSolConsumo = this.getCodigoObs3(motivoConsumo,"2");
                                    bolCriticaFens = true;
                                    criticanew.ingresarLectura(strRuta, strOrdenId, strTipoLectura, intConsumo, strLecturaConsumo, strObservacion, strSolConsumo, datFechaLectura, bolCriticaFens); //INGRESAR LECTURA
                                }else{
                                    if(decimales == 3) {
                                        strSolConsumo = this.getCodigoObs3(motivoConsumo,"2");
                                        String strlecturadecimales = strLectura+"000";
                                        intConsumo = intConsumo * factorConsumo / 10000;
                                        bolCriticaFens = true; // SE GUARDA EL VALOR
                                        criticanew.ingresarLectura(strRuta, strOrdenId, strTipoLectura, intConsumo, strlecturadecimales, strObservacion, strSolConsumo, datFechaLectura, bolCriticaFens); //INGRESAR LECTURA
                                    }else{
                                        bolCriticaFens = true;
                                        strObservacion =  this.getCodigoObs3(obsLectura, "1");
                                        criticanew.ingresarLectura(strRuta, strOrdenId, strTipoLectura, intConsumo, strLectura, strObservacion, strSolConsumo, datFechaLectura, bolCriticaFens); //INGRESAR LECTURA

                                    }
                                    criticanew.marcarNoEntreago(strRuta, strOrdenId);
                                }
/*                                String strLecturaIngreso = strLectura+"000";
                                if (obsLectura.equals("-")){
                                    strObservacion =  this.getCodigoObs3(motivoConsumo, "1");
                                    strSolConsumo = this.getCodigoObs3(motivoConsumo,"2");

                                }else{
                                    strObservacion =  this.getCodigoObs3(obsLectura, "1");
                                    strSolConsumo = this.getCodigoObs3(obsLectura,"2");
                                }

                                if(obsLectura.equals("")){
                                    bolCriticaFens = false; // SE GUARDA EL VALOR
                                    criticanew.ingresarLectura(strRuta, strOrdenId, strTipoLectura, intConsumo, strLecturaIngreso, strObservacion, strSolConsumo, datFechaLectura, bolCriticaFens); //INGRESAR LECTURA
                                }else {
                                    criticanew.ingresarLectura(strRuta, strOrdenId, strTipoLectura, intConsumo, strLecturaIngreso, strObservacion, strSolConsumo, datFechaLectura, bolCriticaFens);
                                }//INGRESAR LECTURA
                                criticanew.marcarNoEntreago(strRuta, strOrdenId);*/

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        String sqlConsultaupdate = "UPDATE " + m.TABLE_DETALLE_ORDEN_FACTURACION + " SET " + m.SINCRONIZADO + " = 0 WHERE " + m.ORDEN_ID + " IN ( " + strOrdenId + " );";
                        db.execSQL(sqlConsultaupdate);
                    } while (cursor2.moveToNext());//accessing data upto last row from table
                }
                db.insert(m.TABLE_EJECUCION, null, generarContentValuesInstertarLectura(ose_codigo, lectura, codigo_obs_lectura_normal, codigo_no_lectura, indicador_lectura,
                        critica, intentos, encontro_medidor,  medidor_correcto, serie_medidor_encontrado, longitud, latitud, consumo, reconexion_no));
                System.out.println( "INSERTO LECTURA " );
            /*}*/
        }

    }// Ingresa o actualizar una Lectura/Motivo

    public void marcarLecturaNoEntregado(String orden_id, String ruta_fens) throws Exception {
        Critica marcar = new Critica(new DbHelper(MyApplication.getContext()).getDatabaseName(), MyApplication.getContext());
        marcar.marcarNoEntreago(ruta_fens,orden_id);
    }
    /**
     * Contenedor de valores UPDATE LECTURA
     * @param lectura
     * @param codigo_no_lectura
     * @param codigo_obs_lectura
     * @param indicador_lectura
     * @param critica
     * @param intentos
     * @param encontro_medidor
     * @param medidor_correcto
     * @param serie_medidor_encontrado
     * @return BOLSA DE VALORES
     */
    private ContentValues generarContentValuesUpdateLectura(
            String lectura,int codigo_obs_lectura,int codigo_no_lectura, int indicador_lectura,
            int critica,int intentos,int encontro_medidor, int medidor_correcto,
            String serie_medidor_encontrado, String longitud, String latitud, int consumo,
            String reconexion_no){
        ContentValues valores = new ContentValues();
        valores.put(m.EO_LECTURA, lectura);
        valores.put(m.EO_CODIGO_NO_LECTURA, codigo_no_lectura);
        valores.put(m.EO_CODIGO_OBS_LECTURA, codigo_obs_lectura);
        valores.put(m.EO_INDICADOR_LECTURA, indicador_lectura);
        valores.put(m.EO_FECHA_ACTUALIZACION, getDateTime());
        valores.put(m.EO_CRITICA, critica);
        valores.put(m.EO_INTENTOS, intentos);
        valores.put(m.EO_ENCONTRO_MEDIDOR, encontro_medidor);
        valores.put(m.EO_MEDIDOR_CORRECTO, medidor_correcto);
        valores.put(m.EO_SERIE_MEDIDOR_ENCONTRADO, serie_medidor_encontrado);
        valores.put(m.EO_LONGITUD, longitud);
        valores.put(m.EO_LATITUD, latitud);
        valores.put(m.EO_CONSUMO, consumo);
        valores.put(m.EO_RECONEXION_NO_AUTORIZADA, reconexion_no);
        valores.put(m.EO_SINCRONIZADO, 0);

        return valores;
    }// Cierre Contenedor de valores UPDATE LECTURA

    /**
     * Contenedor de valores INSERTAR LECTURA
     * @param ose_codigo
     * @param lectura
     * @param codigo_no_lectura
     * @param codigo_obs_lectura
     * @param indicador_lectura
     * @param critica
     * @param intentos
     * @param encontro_medidor
     * @param medidor_correcto
     * @param serie_medidor_encontrado
     * @return BOLSA VALORES
     */
    private ContentValues generarContentValuesInstertarLectura(String ose_codigo,String lectura,int codigo_obs_lectura,int codigo_no_lectura, int indicador_lectura,
                                                               int critica,int intentos,int encontro_medidor, int medidor_correcto,String serie_medidor_encontrado,
                                                               String longitud, String latitud, int consumo, String reconexion_no){
        ContentValues valores = new ContentValues();
        valores.put(m.EO_OSE_CODIGO, ose_codigo);
        valores.put(m.EO_LECTURA, lectura);
        valores.put(m.EO_CODIGO_NO_LECTURA, codigo_no_lectura);
        valores.put(m.EO_CODIGO_OBS_LECTURA, codigo_obs_lectura);
        valores.put(m.EO_INDICADOR_LECTURA, indicador_lectura);
        valores.put(m.EO_FECHA_CREACION, getDateTime());
        valores.put(m.EO_FECHA_ACTUALIZACION, getDateTime());
        valores.put(m.EO_CRITICA, critica);
        valores.put(m.EO_INTENTOS, intentos);
        valores.put(m.EO_ENCONTRO_MEDIDOR, encontro_medidor);
        valores.put(m.EO_MEDIDOR_CORRECTO, medidor_correcto);
        valores.put(m.EO_SERIE_MEDIDOR_ENCONTRADO, serie_medidor_encontrado);
        valores.put(m.EO_LONGITUD, longitud);
        valores.put(m.EO_LATITUD, latitud);
        valores.put(m.EO_CONSUMO, consumo);
        valores.put(m.EO_RECONEXION_NO_AUTORIZADA, reconexion_no);
        valores.put(m.EO_SINCRONIZADO, 0);

        return valores;
    }// Cierre Contenedor de valores INSERTAR LECTURA

    /**
     * Obtener la fecha y hora actual
     */
    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    } // Cierre obtener Hora

    private String getDateTime2() {

        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    } // Cierre obtener Hora


    /**
     * FInalizar una orden sin actividad
     * @param ose_codigo
     * @param observacion_no_lectura
     * @param sellos
     * @param retiro_acometida
     * @param datos_retiro_acometida
     * @param censo_carga
     * @param reconexion_no_autorizada
     */
    public void finalizarOrdenSinActividad(String ose_codigo, String observacion_no_lectura, String sellos,int retiro_acometida,
                                           String datos_retiro_acometida,String censo_carga, double reconexion_no_autorizada){
        int rowsAff = 0;
        //ACTUALIZO CANTIDAD STOCK A CERO
        rowsAff = db.update(m.TABLE_EJECUCION, generarContentValuesUpdateFinalizarOrdenActividad(observacion_no_lectura,
                        sellos, retiro_acometida, datos_retiro_acometida, censo_carga,reconexion_no_autorizada),
                m.EO_OSE_CODIGO + "=?", new String[] {ose_codigo});
        System.out.println("GUARDO ORDEN FINALIZO " + rowsAff);

        rowsAff = db.update(m.TABLE_ORDEN, generarContentValuesUpdateEstadoOrdenOrden(27),
                m.EO_OSE_CODIGO + "=?", new String[] {ose_codigo});
        System.out.println("CAMBIO ESTADO ORDEN FINALIZO " + rowsAff);
    }

    /**
     * Generar content Values Finalizar ordenes sin actividad
     * @param observacion_no_lectura
     * @param sellos
     * @param retiro_acometida
     * @param datos_retiro_acometida
     * @param censo_carga
     * @param reconexion_no_autorizada
     * @return
     */
    private ContentValues generarContentValuesUpdateFinalizarOrdenActividad(
            String observacion_no_lectura, String sellos,int retiro_acometida,
            String datos_retiro_acometida,String censo_carga,double reconexion_no_autorizada){
        ContentValues valores = new ContentValues();
        valores.put(m.EO_OBS_NO_LECTURA, observacion_no_lectura);
        valores.put(m.EO_SELLOS_INSTALADOS, sellos);
        valores.put(m.EO_RETIRO_ACOMETIDA, retiro_acometida);
        valores.put(m.EO_DATOS_RETIRO_ACOMETIDA, datos_retiro_acometida);
        valores.put(m.EO_CENSO_CARGA, censo_carga);
        valores.put(m.EO_RECONEXION_NO_AUTORIZADA, reconexion_no_autorizada);
        valores.put(m.EO_SINCRONIZADO, 0);
        return valores;
    }

    /**
     * Generar Content Values Orden
     * @param estado
     * @return
     */
    private ContentValues generarContentValuesUpdateEstadoOrdenOrden(int estado){

        return m.generarContentValuesUpdateEstadoOrdenOrden(estado);
    }

    /**
     * Change state to check for finish
     * @param ose_codigo
     */
    public void finalizarOrdenRevision(String ose_codigo){

        int rowsAff = 0;
        ContentValues valores = new ContentValues();
        valores.put(m.OS_OSE_TIP_ORDEN, 211);
        rowsAff = db.update(m.TABLE_ORDEN, valores, m.EO_OSE_CODIGO + "=?", new String[] {ose_codigo});
        System.out.println("CAMBIO TIPO ORDEN REVISIONES " + rowsAff);
    }//finalizarOrdenRevision

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
     * Consulta datos para imprimir lectura
     * @param ose_codigo
     * @return
     */
    public Cursor consultaLecturaImprimir(String ose_codigo) {
        // TODO Auto-generated method stub

        String sqlConsulta =
                "SELECT * " +
                        "  FROM "+ m.TABLE_EJECUCION +" AS oe INNER JOIN " + m.TABLE_ORDEN + " AS os ON oe." + m.EO_OSE_CODIGO + "= os." + m.OS_OSE_CODIGO + " " +
                        "  WHERE oe." + m.EO_OSE_CODIGO + " =" + ose_codigo ;
        return db.rawQuery(sqlConsulta, null);
    }

    /**
     * Inserta una foto en base de datos
     * @param ose_codigo
     * @param foto_url
     */
    public void insertarFoto(int ose_codigo, String foto_url){
        db.insert(m.TABLE_FOTO, null, generarContentValuesInstertarFoto(ose_codigo, foto_url));
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
        valores.put(m.FT_OSE_CODIGO, ose_codigo);
        valores.put(m.FT_SINCRONIZADO, 0);
        valores.put(m.FT_FECHA, getDateTime());
        valores.put(m.FT_FOTO_URL, foto_url);

        return valores;
    }// Cierre Generacion de valores

    /**
     * Retorna true si la orden previa esta con desocupado
     * @param ose_codigo
     * @param suscriptor
     * @param observacionActual
     * @return true si esta false no esta
     */
    public Boolean consultaOrdenPreviaDesocupadoParado(int ose_codigo, int suscriptor, int observacionActual)
    {
        Boolean previa = false;
        int observacionAnterior = 0;
        String sqlConsulta = "SELECT l.codigo_observacion_lectura from orden_servicio o, ejecucion_orden l" +
                " WHERE o.ose_codigo = l.ose_codigo " +
                " AND o.cli_contrato= '" + suscriptor + "' " +
                " AND o.ose_codigo != " + ose_codigo;

        Cursor cursor = db.rawQuery(sqlConsulta, null);
        if (cursor.moveToFirst()) {
            do {
                observacionAnterior = cursor.getInt(cursor.getColumnIndex("codigo_observacion_lectura"));
            } while (cursor.moveToNext());
        }

        if(observacionAnterior > 0){
            if ((observacionActual == 806 && observacionAnterior == 776) || (observacionActual == 776 && observacionAnterior == 806))
            {
                previa = true;
            }
        }
        cursor.close();

        return previa;
    }

    /**
     * Consulta datos del medidor posterior
     * @param cuadrilla
     * @param ruta
     * @return
     */
    public Bundle consultaDatosMedidor(String cuadrilla, int ruta){
        int ciclo, ruta_cons = 0, contrato_posterior = 0;
        Bundle bolsa = new Bundle();

        String sqlConsulta = "SELECT " + m.OS_CICLO +
                " FROM "+ m.TABLE_ORDEN +
                " WHERE "+ m.OS_CUADRILLA +" = " + cuadrilla;
        Cursor cursor = db.rawQuery(sqlConsulta, null);
        if (cursor.moveToFirst()) {
            ciclo = cursor.getInt(cursor.getColumnIndex("ciclo"));
            bolsa.putInt("ciclo", ciclo);
        }

        sqlConsulta =
                " SELECT  " + m.OS_RUTA_CONS + " AS ruta_posterior ,"
                        + m.OS_CLI_CONTRATO + " AS contrato_posterior" +
                "  FROM "+ m.TABLE_ORDEN +
                "  WHERE " + m.OS_RUTA_CONS +  " > " + ruta +
                "  ORDER BY ruta_cons ASC LIMIT 1 ";
        cursor = db.rawQuery(sqlConsulta, null);
        if (cursor.moveToFirst()) {
            ruta_cons = cursor.getInt(cursor.getColumnIndex("ruta_posterior"));
            contrato_posterior = cursor.getInt(cursor.getColumnIndex("contrato_posterior"));
            bolsa.putInt("ruta_posterior", ruta_cons);
            bolsa.putInt("contrato_posterior", contrato_posterior);
        }

        cursor.close();
        return bolsa;
    }// Cierre material disponible

//    public String EnvioDatosApi (String strApp, String strProceso, String strTablaApp, String strRuta, String [] lisDatosDescarga){
//        final String[] resultadoEnvio = new String[1];
//        Call<EnvioDatos> call = new ApiClient().getApiClient().create(ApiServices.class).postDescargaDatos(strApp,strProceso,strTablaApp,strRuta,lisDatosDescarga);
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
//        return String.valueOf(resultadoEnvio);
//    } COMENTADO ALEJODURAN
}
