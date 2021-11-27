package com.example.bluetooh;

public class ItemMaterial {

	private int rec_codigo;
	private int tipo;
	private int ose_codigo;
	private double rec_cantidad;
	private String rec_prefijo;
	private String rec_serie;
	private String rec_nombre;
	private String rec_unidad;
	private int nodo;
	private int rec_seriado;

	public ItemMaterial() {
		super();
	}

	public ItemMaterial(int rec_codigo,int ose_codigo,double rec_cantidad, String rec_prefijo, String rec_serie,String rec_nombre,String rec_unidad,int rec_seriado,int nodo, int tipo) {
		super();

		this.rec_codigo = rec_codigo;
		this.ose_codigo = ose_codigo;
		this.rec_cantidad = rec_cantidad;
		this.tipo = tipo;
		this.rec_prefijo = rec_prefijo;
		this.rec_serie = rec_serie;
		this.rec_nombre = rec_nombre;
		this.rec_unidad = rec_unidad;
		this.nodo = nodo;
		this.rec_seriado = rec_seriado;
	}

	public int getRecCodigo() {
		return rec_codigo;
	}

	public int setOseCodigo() {
		return ose_codigo;
	}

	public double getCantidad() {
		return rec_cantidad;
	}

	public int getNodo() {
		return nodo;
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

	public String getUnidad(){
		return rec_unidad;
	}

	public int getSeriado(){
		return rec_seriado;
	}
}
