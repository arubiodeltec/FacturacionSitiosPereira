package com.example.formato;


/**
 * Created by Jackson on 10/10/2015.
 */
public class ItemEjecucionFormato {
    private String ef_fecha_creacion, ef_hora_creacion, tf_nombre;
    private int tf_id, ef_id, ef_calificacion, ef_estado;



    public ItemEjecucionFormato() {
        super();
    }


    public ItemEjecucionFormato(int tf_id, String tf_nombre, int ef_id,String ef_fecha_creacion, String ef_hora_creacion,
                        int ef_calificacion, int ef_estado ) {
        super();
        this.tf_id = tf_id;
        this.tf_nombre = tf_nombre;
        this.ef_id = ef_id;
        this.ef_fecha_creacion = ef_fecha_creacion;
        this.ef_hora_creacion = ef_hora_creacion;
        this.ef_calificacion = ef_calificacion;
        this.ef_estado = ef_estado;
    }

    public int getTf_id(){return  tf_id;}
    public String getTf_nombre(){ return tf_nombre;}
    public int getEf_id(){return ef_id;}
    public String getEf_fecha_creacion(){return ef_fecha_creacion;}
    public String getEf_hora_creacion(){return ef_hora_creacion;}
    public int getEf_calificacion(){return ef_calificacion;}
    public int getEf_estado(){return ef_estado;}
}
