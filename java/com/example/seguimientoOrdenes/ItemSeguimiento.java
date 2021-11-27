package com.example.seguimientoOrdenes;

public class ItemSeguimiento {
	 
    private int per_codigo;
    private String nombre;
    private int total, ejecutada, pendiente, total_causas, med_no_existe, med_ilegible, impedimento_cambio, lote, fuerza_mayor, impedimento_tapado, impedimento_reja ;
    private String fecha_inicio;
    private String fecha_fin;
    private Boolean graficos;
    
 
    public ItemSeguimiento() {
        super();
    }
 
    public ItemSeguimiento(int per_codigo, String nombre, int total,int ejecutada,int pendiente,int total_causas,
    		int med_no_existe,int med_ilegible,int impedimento_cambio,int lote,int fuerza_mayor,int impedimento_tapado,
                           int impedimento_reja, String fecha_inicio, String fecha_fin, Boolean graficos) {
        super();

        this.per_codigo = per_codigo;
        this.nombre = nombre;
        this.total = total;
        this.ejecutada = ejecutada;
        this.pendiente = pendiente;
        this.total_causas = total_causas;
        this.med_no_existe = med_no_existe;
        this.med_ilegible = med_ilegible;
        this.impedimento_cambio = impedimento_cambio;
        this.lote = lote;
        this.fuerza_mayor = fuerza_mayor;
        this.impedimento_tapado = impedimento_tapado;
        this.impedimento_reja = impedimento_reja;
        this.fecha_inicio = fecha_inicio;
        this.fecha_fin = fecha_fin;
        this.graficos = graficos;
    }
 
    public int getPer_codigo() {
        return per_codigo;
    }
 
    public String getNombre() {
        return nombre;
    }
    
    public int getTotal() {
        return total;
    }
    
    public int getEjecutada(){
    	return ejecutada;
    }
    
    public int getTotal_causas(){
    	return total_causas;
    }
    
    public int getMed_no_existe(){
    	return med_no_existe;
    }
    
    public int getImpedimento_tapado(){
    	return impedimento_tapado;
    }
    
    public int getImpedimento_reja(){
    	return impedimento_reja;
    }
    
    public int getMed_ilegible(){
    	return med_ilegible;
    }
    
    public int getImpedimento_cambio(){
    	return impedimento_cambio;
    }
    
    public int getLote(){
    	return lote;
    }
    
    public int getFuerza_mayor(){
    	return fuerza_mayor;
    }
    
    public int getPendiente(){
    	return pendiente;
    }
    
    public String getFecha_inicio() {
        return fecha_inicio;
    }
    
    public String getFecha_fin() {
        return fecha_fin;
    }

    public boolean getGraficos() {return  graficos;}
    
}
