package com.example.gestionOrdenes;

public class Item {

    private int image;
    private String ordenId;
    private String rutaOrden;
    private String direccion;
    private String elemento;
    private String barrio;
    private String producto;
    private String consumo;
    private int ruta, consecutivo;
    private int ose_codigo,ose_precarga, cli_contrato;
    private String cli_nombre;
    private String estrato, municipio;
    private String estado_fens;


    public Item() {
        super();
    }

    public Item(String ordenId, String rutaOrden, String direccion, String elemento, String barrio,String producto,String info_add, int ose_codigo,int ose_precarga,int cli_contrato,String cli_nombre,int ruta, int consecutivo, String consumo, String estado_fens) {
        super();

        this.ordenId = ordenId;
        this.rutaOrden = rutaOrden;
        this.direccion = direccion;
        this.elemento = elemento;
        this.barrio = barrio;
        this.producto = producto;
        this.ose_codigo = ose_codigo;
        this.ose_precarga = ose_precarga;
        this.cli_contrato = cli_contrato;
        this.cli_nombre = cli_nombre;
        this.ruta = ruta;
        this.consecutivo = consecutivo;
        this.municipio = info_add;
        this.consumo = consumo;
        this.estado_fens = estado_fens;

    }

    public String getRutaOrden() {
        return rutaOrden;
    }

    public void setRutaOrden(String rutaOrden) {
        this.rutaOrden = rutaOrden;
    }

    public String getOrdenId() {
        return ordenId;
    }

    public void setOrdenId(String ordenId) {
        this.ordenId = ordenId;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getdireccion() {
        return direccion;
    }

    public void setdireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getElemento() {
        return elemento;
    }

    public String getbarrio() {
        return barrio;
    }

    public String getProducto(){
        return producto;
    }

    public String getMunicipio(){
        return municipio;
    }

    public void setbarrio(String barrio) {
        this.barrio = barrio;
    }

    public int getose_codigo() {
        return ose_codigo;
    }

    public String getCliNombre(){
        return cli_nombre;
    }

    public int getCliContrato() {
        return cli_contrato;
    }

    public int getOse_precarga() {
        return ose_precarga;
    }

    public String getEstrato(){
        return estrato;
    }

    public int getRuta() {
        return ruta;
    }


    public int getConsecutivo() {
        return consecutivo;
    }

    public String getConsumo(){
        return consumo;
    }

    public String getEstado_fens() {
        return estado_fens;
    }

    public void setEstado_fens(String estado_fens) {
        this.estado_fens = estado_fens;
    }
}