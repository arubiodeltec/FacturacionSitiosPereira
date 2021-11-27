package com.example.config;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.Logueo.DataBaseManager;

/**
 * Created by jasson on 22/07/17.
 */

public class GestionMedidorEncontradoModel {
    private SQLiteDatabase db;
    private DataBaseManager m;

    /**
     * Constructor Manejador de Bd
     * @param  context  Contexto
     */
    public GestionMedidorEncontradoModel(Context context){

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
     * Ingresa un nuevo medidor a la BD local
     * @param med_ciclo
     * @param med_ruta
     * @param med_cons_ruta_anterior
     * @param med_cons_ruta_posterior
     * @param med_direccion
     * @param med_medidor
     * @param med_indicador_med
     * @param med_lectura
     * @param med_servicio
     * @param med_observacion
     * @param med_persona
     * @param med_latitud
     * @param med_longitud
     */
    public void insertar_medidor(int med_ciclo,int med_ruta,int med_cons_ruta_anterior, int med_cons_ruta_posterior,
                                 String med_direccion,String med_medidor, int med_indicador_med,String med_lectura,
                                 int med_servicio,String med_observacion, int med_persona,String med_latitud,
                                 String med_longitud ){

        db.insert(m.TABLE_MEDIDOR_ENCONTRADO, null, generarContentValuesMedidor(med_ciclo, med_ruta,med_cons_ruta_anterior,
                med_cons_ruta_posterior, med_direccion, med_medidor, med_indicador_med, med_lectura, med_servicio, med_observacion,
                med_persona, med_latitud, med_longitud));

    }

    private ContentValues generarContentValuesMedidor(int med_ciclo,int med_ruta,int med_cons_ruta_anterior, int med_cons_ruta_posterior,
                                                      String med_direccion,String med_medidor, int med_indicador_med,String med_lectura,
                                                      int med_servicio,String med_observacion, int med_persona,String med_latitud,
                                                      String med_longitud){
        ContentValues valores = new ContentValues();
        valores.put(m.MED_CICLO, med_ciclo);
        valores.put(m.MED_RUTA, med_ruta);
        valores.put(m.MED_CONS_RUTA_ANTERIOR, med_cons_ruta_anterior);
        valores.put(m.MED_CONS_RUTA_POSTERIOR, med_cons_ruta_posterior);
        valores.put(m.MED_DIRECCION, med_direccion);
        valores.put(m.MED_MEDIDOR, med_medidor);
        valores.put(m.MED_INDICADOR_MED, med_indicador_med);
        valores.put(m.MED_LECTURA, med_lectura);
        valores.put(m.MED_SERVICIO, med_servicio);
        valores.put(m.MED_OBSERVACION, med_observacion);
        valores.put(m.MED_PERSONA, med_persona);
        valores.put(m.MED_LATITUD, med_latitud);
        valores.put(m.MED_LONGITUD, med_longitud);
        valores.put(m.MED_FECHA, m.getDateTime());
        valores.put(m.MED_SINCRONIZADO, 0);

        return valores;
    }// Cierre Contenedor de valores Ingreso Datos

}
