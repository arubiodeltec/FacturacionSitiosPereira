package com.example.verorden;

public class ItemSello {

    private int rec_codigo;
    private int tipo;
    private int ose_codigo;
    private String rec_prefijo;
    private String rec_serie;
    private String rec_nombre;
 
    public ItemSello() {
        super();
    }
 
    public ItemSello(int rec_codigo,int ose_codigo, String rec_prefijo, String rec_serie,String rec_nombre, int tipo) {
        super();
        

        this.rec_codigo = rec_codigo;
        this.ose_codigo = ose_codigo;
        this.tipo = tipo;
        this.rec_prefijo = rec_prefijo;
        this.rec_serie = rec_serie;
        this.rec_nombre = rec_nombre;
    }
     
    public int getRecCodigo() {
        return rec_codigo;
    }
 
    public int setOseCodigo() {
    	 return ose_codigo;
    }
 
    public int getTipo() {
        return tipo;
    }
 
    public String getPrefijo() { 
        return rec_prefijo;
    }
    
    public String getSerie() {
        return rec_serie;
    }
    
    public String getNombre(){
    	return rec_nombre;
    }
}
