package com.example.lectura;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Auth {
    @SerializedName("strUsuario")
    @Expose
    private String strUsuario;
    @SerializedName("strClave")
    @Expose
    private String strClave;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("intSesionId")
    @Expose
    private int intSesionId;
    @SerializedName("strToken")
    @Expose
    private String strToken;

    public String getStrUsuario() {
        return strUsuario;
    }

    public void setStrUsuario(String strUsuario) {
        this.strUsuario = strUsuario;
    }

    public String getStrClave() {
        return strClave;
    }

    public void setStrClave(String strClave) {
        this.strClave = strClave;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getIntSesionId() {
        return intSesionId;
    }

    public void setIntSesionId(int intSesionId) {
        this.intSesionId = intSesionId;
    }

    public String getStrToken() {
        return strToken;
    }

    public void setStrToken(String strToken) {
        this.strToken = strToken;
    }
}
