package com.example.apiretrofit;

import com.example.lectura.Auth;
import com.example.lectura.EnvioDatos;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiServices {
    @FormUrlEncoded
    @POST("api/ScmEep/autenticarUsuario")
    Call<Auth> postAuth (
            @Field("strUsuario") String strApp,
            @Field("strClave") String strProceso);

    @FormUrlEncoded
    @POST("api/ScmEep/descargarTablas")
    Call<EnvioDatos> postDescargaDatos(
            @Field("intSesionId") int intSesionId,
            @Field("strToken") String strToken,
            @Field("strApp") String strApp,
            @Field("strProceso") String strProceso,
            @Field("strTablaApp") String strTablaApp,
            @Field("lisDatosDescarga") String[] lisDatosDescarga);
    }
