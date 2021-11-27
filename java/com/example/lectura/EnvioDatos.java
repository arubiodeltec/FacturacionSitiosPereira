
package com.example.lectura;

import android.support.v4.media.session.PlaybackStateCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EnvioDatos {

    @SerializedName("intSesionId")
    @Expose
    private int intSesionId;
    @SerializedName("strToken")
    @Expose
    private String strToken;
    @SerializedName("strApp")
    @Expose
    private String strApp;
    @SerializedName("strTablaApp")
    @Expose
    private String strTablaApp;
    @SerializedName("lisDatosDescarga")
    @Expose
    private String[] lisDatosDescarga = null;
    @SerializedName("succes")
    @Expose
    private Boolean succes;
    @SerializedName("message")
    @Expose
    private String message;

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

    public String getStrApp() {
        return strApp;
    }

    public void setStrApp(String strApp) {
        this.strApp = strApp;
    }

    public String getStrTablaApp() {
        return strTablaApp;
    }

    public void setStrTablaApp(String strTablaApp) {
        this.strTablaApp = strTablaApp;
    }

    public String[] getLisDatosDescarga() {
        return lisDatosDescarga;
    }

    public void setLisDatosDescarga(String[] lisDatosDescarga) {
        this.lisDatosDescarga = lisDatosDescarga;
    }

    public Boolean getSucces() {
        return succes;
    }

    public void setSucces(Boolean succes) {
        this.succes = succes;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
