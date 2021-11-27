package com.example.formato;


import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import android.app.ProgressDialog;
import com.loopj.android.http.*;

import org.apache.commons.io.FileUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Random;

/**
 * Created by Jackson on 15/10/2015.
 */
public class RestFormatoModel {

    String token = "";
    Context context;
    ProgressDialog prgDialog;
    //String ip = "http://190.156.237.114:8010";
    String ip = "http://192.168.0.177:8010";


    public RestFormatoModel(Context context){
        this.context = context;
        prgDialog = new ProgressDialog(context);
        prgDialog.setMessage("Please wait...");
        prgDialog.setCancelable(false);
    }

    public void getToken(String user, String password){

        RequestParams params = new RequestParams();
        params.put("username", user);
        params.put("password", password);
        prgDialog.show();
        AsyncHttpClient client = new AsyncHttpClient();

        client.post(ip + "/api_rest/api/obtener_token/", params, new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http response code '200'
            @Override
            public void onSuccess(String response) {
                prgDialog.hide();
                // Hide Progress Dialog
                try {
                    // JSON Object
                    JSONObject obj = new JSONObject(response);
                    // When the JSON response has status boolean value assigned with true
                    token = "Token " + obj.getString("token");
                    System.out.println("EXITO " + token);
                    getInspeccionesTipoFormato();
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    System.out.println("Error Occured [Server's JSON response might be invalid]!");
                    e.printStackTrace();
                }
            }

            // When the response returned by REST has Http response code other than '200'
            @Override
            public void onFailure(int statusCode, Throwable error,
                                  String content) {
                // Hide Progress Dialog
                prgDialog.hide();
                // When Http response code is '404'
                if (statusCode == 404) {
                    Toast.makeText(context, "Requested resource not found", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                    Toast.makeText(context, "Something went wrong at server end", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else {
                    Toast.makeText(context, "Unexpected Error occcured! [Most common Error: Device might not be connecrted to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Call REST TipoFormato
     */
    public void getInspeccionesTipoFormato(){
        // Make RESTful webservice call using AsyncHttpClient object
        // Show Progress Dialog
        prgDialog.setMessage("Get TipoFormato");
        prgDialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Authorization", token);
        client.get(ip + "/api_rest/api/tipo_formato/", null, new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http response code '200'
            @Override
            public void onSuccess(String response) {
                // Hide Progress Dialog
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    insertTipoFormato(jsonArray);
                } catch (JSONException e) {
                    Toast.makeText(context, "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }

            // When the response returned by REST has Http response code other than '200'
            @Override
            public void onFailure(int statusCode, Throwable error,
                                  String content) {
                // Hide Progress Dialog
                prgDialog.hide();
                // When Http response code is '404'
                if (statusCode == 404) {
                    Toast.makeText(context, "Requested resource not found", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                    Toast.makeText(context, "Something went wrong at server end", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else {
                    Toast.makeText(context, "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }
            }
        });

    }// getInspeccionesTipoFormato

    /**
     * Insertar Tabla TipoFormato
     * @param jsonArray
     */
    public void insertTipoFormato(JSONArray jsonArray){
        EjecucionFormatoModel manager = new EjecucionFormatoModel(context);
        prgDialog.setMessage("Download TipoFormato");
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                int tf_id = jsonObject.getInt("id");
                String tf_nombre = jsonObject.getString("nombre");
                manager.open();
                manager.insertTipoFormato(tf_id, tf_nombre);
                manager.close();

                System.out.println("CARGA REST " + tf_nombre);
            }
            if(jsonArray.length() > 0)
                getInspeccionesModuloFormato();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            // Hide Progress Dialog
            prgDialog.hide();
            Toast.makeText(context, "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public void getInspeccionesModuloFormato(){
        // Make RESTful webservice call using AsyncHttpClient object
        // Show Progress Dialog
        prgDialog.setMessage("Get ModuloFormato");
        prgDialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Authorization", token);
        client.get(ip + "/api_rest/api/modulo_formato/", null, new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http response code '200'
            @Override
            public void onSuccess(String response) {
                // Hide Progress Dialog
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    insertModuloFormato(jsonArray);
                } catch (JSONException e) {
                    Toast.makeText(context, "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }

            // When the response returned by REST has Http response code other than '200'
            @Override
            public void onFailure(int statusCode, Throwable error,
                                  String content) {
                // Hide Progress Dialog
                prgDialog.hide();
                // When Http response code is '404'
                if (statusCode == 404) {
                    Toast.makeText(context, "Requested resource not found", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                    Toast.makeText(context, "Something went wrong at server end", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else {
                    Toast.makeText(context, "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void insertModuloFormato(JSONArray jsonArray){
        EjecucionFormatoModel manager = new EjecucionFormatoModel(context);
        prgDialog.setMessage("Download ModuloFormato");
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                int tipo_formato_id = jsonObject.getInt("tipo_formato");
                int mf_id = jsonObject.getInt("id");
                String mf_nombre = jsonObject.getString("nombre");
                int mf_codigo = jsonObject.getInt("codigo");
                int mf_numero_orden = jsonObject.getInt("numero_orden");

                manager.open();
                manager.insertModuloFormato(mf_id, mf_nombre, mf_codigo, mf_numero_orden, tipo_formato_id);
                manager.close();

                System.out.println("CARGA REST MODULO FORMATO " + mf_nombre);
            }
            if(jsonArray.length() > 0)
                getInspeccionesTipoCampo();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            // Hide Progress Dialog
            prgDialog.hide();
            Toast.makeText(context, "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public void getInspeccionesTipoCampo(){
        // Make RESTful webservice call using AsyncHttpClient object
        // Show Progress Dialog
        prgDialog.setMessage("Get TipoCampo");
        prgDialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Authorization", token);
        client.get(ip + "/api_rest/api/tipo_campo/", null, new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http response code '200'
            @Override
            public void onSuccess(String response) {
                // Hide Progress Dialog
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    insertTipoCampo(jsonArray);
                } catch (JSONException e) {
                    Toast.makeText(context, "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }

            // When the response returned by REST has Http response code other than '200'
            @Override
            public void onFailure(int statusCode, Throwable error,
                                  String content) {
                // Hide Progress Dialog
                prgDialog.hide();
                // When Http response code is '404'
                if (statusCode == 404) {
                    Toast.makeText(context, "Requested resource not found", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                    Toast.makeText(context, "Something went wrong at server end", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else {
                    Toast.makeText(context, "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void insertTipoCampo(JSONArray jsonArray){
        EjecucionFormatoModel manager = new EjecucionFormatoModel(context);
        prgDialog.setMessage("Download TipoCampo");
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                int tc_id = jsonObject.getInt("id");
                String tc_descripcion = jsonObject.getString("descripcion");

                manager.open();
                manager.insertTipoCampo(tc_id, tc_descripcion);
                manager.close();
                System.out.println("CARGA REST TIPO CAMPO " + tc_descripcion);
            }
            if(jsonArray.length() > 0)
                getInspeccionesCampoFormato();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            // Hide Progress Dialog
            prgDialog.hide();
            Toast.makeText(context, "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public void getInspeccionesCampoFormato(){
        // Make RESTful webservice call using AsyncHttpClient object
        // Show Progress Dialog
        prgDialog.setMessage("Get CampoFormato");
        prgDialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Authorization", token);
        client.get(ip + "/api_rest/api/campo_formato/", null, new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http response code '200'
            @Override
            public void onSuccess(String response) {
                // Hide Progress Dialog
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    insertCampoFormato(jsonArray);
                } catch (JSONException e) {
                    Toast.makeText(context, "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }

            // When the response returned by REST has Http response code other than '200'
            @Override
            public void onFailure(int statusCode, Throwable error,
                                  String content) {
                // Hide Progress Dialog
                prgDialog.hide();
                // When Http response code is '404'
                if (statusCode == 404) {
                    Toast.makeText(context, "Requested resource not found", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                    Toast.makeText(context, "Something went wrong at server end", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else {
                    Toast.makeText(context, "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void insertCampoFormato(JSONArray jsonArray){
        EjecucionFormatoModel manager = new EjecucionFormatoModel(context);
        prgDialog.setMessage("Download CampoFormato");
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                int cf_id = jsonObject.getInt("id");
                String cf_nombre = jsonObject.getString("nombre");
                int cf_codigo = jsonObject.getInt("codigo");
                int cf_numero_orden = jsonObject.getInt("numero_orden");
                String cf_tabla_referencia = jsonObject.getString("tabla_referencia");
                int modulo_formato_id = jsonObject.getInt("modulo_formato");
                int tipo_campo_id = jsonObject.getInt("tipo_campo");
                String cf_descripcion = jsonObject.getString("descripcion");
                int cf_parent_id = 0;
                try {
                    cf_parent_id = jsonObject.getInt("parent");
                }catch (Exception e){}
                int cf_level = jsonObject.getInt("level");

                manager.open();
                manager.insertCampoFormato(cf_id, cf_nombre, cf_codigo, cf_numero_orden, cf_parent_id, cf_tabla_referencia, modulo_formato_id, tipo_campo_id, cf_descripcion, cf_level);
                manager.close();
                System.out.println("CARGA REST TIPO CAMPO " + cf_nombre);
            }
            prgDialog.hide();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            // Hide Progress Dialog
            prgDialog.hide();
            Toast.makeText(context, "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public void enviarRest(String user, String password, final String strJson){

        RequestParams params = new RequestParams();
        params.put("username", user);
        params.put("password", password);
        prgDialog.show();
        AsyncHttpClient client = new AsyncHttpClient();

        client.post(ip + "/api_rest/api/obtener_token/", params, new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http response code '200'
            @Override
            public void onSuccess(String response) {
                prgDialog.hide();
                // Hide Progress Dialog
                try {
                    // JSON Object
                    JSONObject obj = new JSONObject(response);
                    // When the JSON response has status boolean value assigned with true
                    token = "Token " + obj.getString("token");
                    System.out.println("EXITO " + token);
                    enviarJsonRest(strJson);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    System.out.println("Error Occured [Server's JSON response might be invalid]!");
                    e.printStackTrace();
                }
            }

            // When the response returned by REST has Http response code other than '200'
            @Override
            public void onFailure(int statusCode, Throwable error,
                                  String content) {
                // Hide Progress Dialog
                prgDialog.hide();
                // When Http response code is '404'
                if (statusCode == 404) {
                    Toast.makeText(context, "Requested resource not found", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                    Toast.makeText(context, "Something went wrong at server end", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else {
                    Toast.makeText(context, "Unexpected Error occcured! [Most common Error: Device might not be connecrted to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void enviarJsonRest(String jsonParams){

        prgDialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        StringEntity entity = null;
        entity = new StringEntity(jsonParams, "UTF-8");
        client.addHeader("Content-Type", "application/json;charset=UTF-8");
        client.post(context, ip + "/api_rest/ejecucionformato/", entity, "application/json",
                new AsyncHttpResponseHandler() {
                    // When the response returned by REST has Http response code '200'
                    @Override
                    public void onSuccess(String response) {
                        prgDialog.hide();
                        // Hide Progress Dialog
                        System.out.println("EXITO " + response);
                    }

                    // When the response returned by REST has Http response code other than '200'
                    @Override
                    public void onFailure(int statusCode, Throwable error,
                                          String content) {
                        // Hide Progress Dialog
                        prgDialog.hide();
                        // When Http response code is '404'
                        if (statusCode == 404) {
                            Toast.makeText(context, "Requested resource not found", Toast.LENGTH_LONG).show();
                        }
                        // When Http response code is '500'
                        else if (statusCode == 500) {
                            Toast.makeText(context, "Something went wrong at server end", Toast.LENGTH_LONG).show();
                        }
                        // When Http response code other than 404, 500
                        else {
                            Toast.makeText(context, "Unexpected Error occcured! [Most common Error: Device might not be connecrted to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    /**
     * Function send IMG with TOKEN
     * @param user
     * @param password
     * @param path
     */
    public void enviarRestImgFile(String user, String password, final String path){

        RequestParams params = new RequestParams();
        params.put("username", user);
        params.put("password", password);
        prgDialog.show();
        AsyncHttpClient client = new AsyncHttpClient();

        client.post(ip + "/api_rest/api/obtener_token/", params, new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http response code '200'
            @Override
            public void onSuccess(String response) {
                prgDialog.hide();
                // Hide Progress Dialog
                try {
                    // JSON Object
                    JSONObject obj = new JSONObject(response);
                    // When the JSON response has status boolean value assigned with true
                    token = "Token " + obj.getString("token");
                    System.out.println("EXITO " + token);
                    uploadImage(path);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    System.out.println("Error Occured [Server's JSON response might be invalid]!");
                    e.printStackTrace();
                }
            }

            // When the response returned by REST has Http response code other than '200'
            @Override
            public void onFailure(int statusCode, Throwable error,
                                  String content) {
                // Hide Progress Dialog
                prgDialog.hide();
                // When Http response code is '404'
                if (statusCode == 404) {
                    Toast.makeText(context, "Requested resource not found", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                    Toast.makeText(context, "Something went wrong at server end", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else {
                    Toast.makeText(context, "Unexpected Error occcured! [Most common Error: Device might not be connecrted to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Metodo para carga de archivos REST
     * @param pathFile
     */
    public void uploadImage(String pathFile) {

        //Create a new RequestParams that will send paremeters with our AsyncHttpClient request.
        RequestParams data = new RequestParams();
        data.put("titulo","TITULOFOTO");
        File photo_200 = new File(pathFile);
        try {
            data.put("archivo", photo_200);
        } catch(FileNotFoundException e) {}

        prgDialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Authorization", token);
        client.post(ip + "/api_rest/api/foto_evidencia/", data, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  String responseString) {
                prgDialog.hide();
                System.out.println("EXITO REST FOTO RELOAD NOTIFICATIONS SUCCESS 2 " + responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  String responseString, Throwable throwable) {
                // Hide Progress Dialog
                prgDialog.hide();
                if (statusCode == 404) {
                    Toast.makeText(context, "Requested resource not found 2", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                    Toast.makeText(context, "Something went wrong at server end 2", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else {
                    Toast.makeText(context, "ERROR JSONOBJECT 2 CODE:" + statusCode, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  Throwable throwable, JSONObject errorResponse) {
                // Hide Progress Dialog
                prgDialog.hide();
                // When Http response code is '404'
                if (statusCode == 404) {
                    Toast.makeText(context, "Requested resource not found", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                    Toast.makeText(context, "Something went wrong at server end", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else {
                    Toast.makeText(context, "ERROR JSONOBJECT CODE:" + statusCode, Toast.LENGTH_LONG).show();
                }
            }

            /*
            public void onProgress(int bytesWritten, int totalSize) {
                System.out.println("RELOAD NOTIFICATIONS PROGRESS bytes:" + bytesWritten + " totalSize:" + totalSize );
            }*/

            @Override
            public void onStart() {
                System.out.println("RELOAD NOTIFICATIONS START");
            }

            @Override
            public void onFinish() {
                // Hide Progress Dialog
                System.out.println("RELOAD NOTIFICATIONS FINISH");
            }
        });
    }

    public void obtenerOrdenes(String user, String password, final String codigo){

        RequestParams params = new RequestParams();
        params.put("username", user);
        params.put("password", password);
        prgDialog.show();
        AsyncHttpClient client = new AsyncHttpClient();

        client.post(ip + "/api_rest/api/obtener_token/", params, new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http response code '200'
            @Override
            public void onSuccess(String response) {
                prgDialog.hide();
                // Hide Progress Dialog
                try {
                    // JSON Object
                    JSONObject obj = new JSONObject(response);
                    // When the JSON response has status boolean value assigned with true
                    token = "Token " + obj.getString("token");
                    System.out.println("EXITO getOrdenes " + token);
                    getOrdenes(codigo);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    System.out.println("Error Occured [Server's JSON response might be invalid]!");
                    e.printStackTrace();
                }
            }

            // When the response returned by REST has Http response code other than '200'
            @Override
            public void onFailure(int statusCode, Throwable error,
                                  String content) {
                // Hide Progress Dialog
                prgDialog.hide();
                // When Http response code is '404'
                if (statusCode == 404) {
                    Toast.makeText(context, "Requested resource not found", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                    Toast.makeText(context, "Something went wrong at server end", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else {
                    Toast.makeText(context, "Unexpected Error occcured! [Most common Error: Device might not be connecrted to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void getOrdenes(String codigo){

        RequestParams params = new RequestParams();
        params.put("cedula", codigo);
        //params.put("password", password);
        prgDialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Authorization", token);
        //client.addHeader("cedula", "18");

        client.post(ip + "/api_rest/api/sincronizartrabajo/", params, new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http response code '200'
            @Override
            public void onSuccess(String response) {
                prgDialog.hide();
                // Hide Progress Dialog
                System.out.println("RESPUESTA " + response);
            }

            // When the response returned by REST has Http response code other than '200'
            @Override
            public void onFailure(int statusCode, Throwable error,
                                  String content) {
                // Hide Progress Dialog
                prgDialog.hide();
                // When Http response code is '404'
                if (statusCode == 404) {
                    Toast.makeText(context, "Requested resource not found", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                    Toast.makeText(context, "Something went wrong at server end", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else {
                    Toast.makeText(context, "Unexpected Error occcured! STATUS:" + statusCode, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}


