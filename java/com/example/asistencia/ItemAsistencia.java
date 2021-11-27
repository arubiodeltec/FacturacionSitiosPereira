package com.example.asistencia;

/**
 * Created by DELTEC on 31/07/2015.
 */
public class ItemAsistencia {

    private String nombre_asistente, fecha_asistente, comentario_asistencia;
    private int estado_asistencia, codigo_asistente, codigo_supervisor, tipo_asistencia;

    public ItemAsistencia() {
        super();
    }

    public ItemAsistencia(int codigo_supervisor, int codigo_asistente, String nombre_asistente, String fecha_asistente,
                          int estado_asistencia,int tipo_asistencia, String comentario_asistencia) {
        super();

        this.codigo_supervisor = codigo_supervisor;
        this.codigo_asistente = codigo_asistente;
        this.nombre_asistente = nombre_asistente;
        this.fecha_asistente = fecha_asistente;
        this.estado_asistencia = estado_asistencia;
        this.tipo_asistencia = tipo_asistencia;
        this.comentario_asistencia = comentario_asistencia;
    }

    public int getCodigoSupervisor(){ return codigo_supervisor; }
    public int getCodigoAsistente() {
        return codigo_asistente;
    }
    public String getNombre_asistente() {
        return nombre_asistente;
    }
    public String getFecha_asistente() {
        return fecha_asistente;
    }
    public String getComentario_asistencia() {
        return comentario_asistencia;
    }
    public int getEstado_asistencia() {
        return estado_asistencia;
    }
    public int getTipoAsistencia(){return tipo_asistencia;}

    public void setUpdateAsistente(String fecha_asistente, int estado_asistencia, int tipo_asistencia, String comentario_asistencia){

        this.fecha_asistente = fecha_asistente;
        this.estado_asistencia = estado_asistencia;
        this.tipo_asistencia = tipo_asistencia;
        this.comentario_asistencia = comentario_asistencia;
    }
}
