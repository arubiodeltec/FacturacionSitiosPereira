package com.example.formato;

import java.io.Serializable;
import java.util.List;

/**
 * Created by DELTEC on 11/09/2015.
 */
public class ItemFormato2 implements Serializable {
    private String mf_nombre, cf_nombre, cf_tabla_referencia, re_hora_creacion, cf_descripcion;
    private int mf_id, cf_id, cf_codigo, cf_parent_id, tc_id, cf_level;
    private boolean checked, esPadre, visible;
    private String resultado;
    private List<ItemFormato2>  hijos;

    public ItemFormato2() {
        super();
    }

    /**
     *
     * @param mf_id
     * @param mf_nombre
     * @param cf_id
     * @param cf_nombre
     * @param cf_codigo
     * @param cf_parent_id
     * @param cf_tabla_referencia
     * @param tc_id
     * @param re_hora_creacion
     * @param cf_descripcion
     * @param checked
     * @param resultado
     * @param cf_level
     * @param items
     */
    public ItemFormato2(int mf_id, String mf_nombre, int cf_id,String cf_nombre, int cf_codigo,
                       int cf_parent_id, String cf_tabla_referencia, int tc_id, String re_hora_creacion,
                        String cf_descripcion, int cf_level, boolean checked,boolean esPadre,
                        boolean visible, String resultado, List<ItemFormato2> hijos ) {
        super();

        this.mf_id = mf_id;
        this.mf_nombre = mf_nombre;
        this.cf_id = cf_id;
        this.cf_nombre = cf_nombre;
        this.cf_codigo = cf_codigo;
        this.cf_parent_id = cf_parent_id;
        this.cf_tabla_referencia = cf_tabla_referencia;
        this.tc_id = tc_id;
        this.re_hora_creacion = re_hora_creacion;
        this.cf_descripcion = cf_descripcion;
        this.cf_level = cf_level;
        this.checked = checked;
        this.resultado = resultado;
        this.esPadre = esPadre;
        this.visible = visible;
        this.hijos = hijos;
    }

    public int getMf_id(){ return mf_id; }
    public String getMf_nombre() {  return mf_nombre;}
    public int getCf_id() {   return cf_id;}
    public String getcf_nombre() {  return cf_nombre;}
    public int getCf_codigo() {  return cf_codigo;}
    public int getCf_parent_id() {  return cf_parent_id;    }
    public String getCf_tabla_referencia() { return cf_tabla_referencia;   }
    public int getTc_id() {  return tc_id;    }
    public String getRe_hora_creacion() { return re_hora_creacion;   }
    public String getCf_descripcion() { return cf_descripcion; }
    public int getCf_level(){return cf_level;}
    public boolean getChecked(){return checked;}
    public String getResultado(){return resultado;}
    public boolean getEsPadre () {return esPadre;}
    public boolean getVisible(){return visible;}
    public List<ItemFormato2> getHijos() {return hijos;}

    public void setResultado(String resultado){
        this.resultado = resultado;
    }
    public void setChecked(boolean checked){
        this.checked = checked;
    }

}
