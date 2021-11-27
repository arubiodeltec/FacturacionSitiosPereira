package com.example.formato;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.app.ProgressDialog;
import android.os.Handler;
import android.widget.Toast;

import com.example.gestiondeltec.R;
import com.example.location.LocationSyncActivity;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jackson on 10/10/2015.
 */
public class ListarFormatos extends LocationSyncActivity implements View.OnClickListener {

    private EjecucionFormatoModel manager;
    private Cursor cursor;
    private Button btFormatosDiligenciar;
    private ListView lvFormatosEjecutados;
    private Spinner spFormatosLista;

    private int cuadrilla = 0;
    List<ItemEjecucionFormato> items = new ArrayList<ItemEjecucionFormato>();

    public boolean banderaEnvio = true;
    ProgressDialog barProgressDialog;
    Handler updateBarHandler;
    List<String> lablesFormatos = null;
    ArrayAdapter<String> dataAdapterFormatos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_formatos);
        manager = new EjecucionFormatoModel(this);

        btFormatosDiligenciar = (Button) findViewById(R.id.btFormatosDiligenciar);
        lvFormatosEjecutados = (ListView) findViewById(R.id.lvFormatosEjecutados);
        spFormatosLista = (Spinner) findViewById(R.id.spFormatosLista);

        Bundle bolsaDatosIniciales = getIntent().getExtras();
        cuadrilla = bolsaDatosIniciales.getInt("cuadrilla");

        actualizarSpinnerFormatos();

        cargarFormatoEjecutados();
        lvFormatosEjecutados.setAdapter(new ItemAdapterFormatosEjecutados(this, items));
        lvFormatosEjecutados.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
                ItemEjecucionFormato item = (ItemEjecucionFormato) lvFormatosEjecutados.getAdapter().getItem(position);
                abrirFormato(item.getTf_id(), item.getEf_id());
            }
        });

        btFormatosDiligenciar.setOnClickListener(this);

        updateBarHandler = new Handler();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_cargar_formato:
                descargarFormatos();
                return true;
            case R.id.menu_refrescar_formato:
                cargarFormatoEjecutados();
                actualizarSpinnerFormatos();
                return true;
            case R.id.menu_enviar_formato:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.btFormatosDiligenciar:
                String formatoElegido = (String) spFormatosLista.getSelectedItem();
                if(formatoElegido.length() > 0){
                    manager.open();
                    int tf_id = manager.getCodigoFormato(formatoElegido);
                    int ef_id = manager.getNextEjecucionFormato();
                    manager.insertEjecucionFormato(ef_id, 1, 1, tf_id); // FALTA MANEJAR EL CAMPO EJECUCION FORMATO EF.id
                    manager.close();
                    abrirFormato(tf_id,ef_id);
                }
                break;
        }
    }

    private void actualizarSpinnerFormatos(){
        manager.open();
        lablesFormatos = manager.getFormatos();
        manager.close();

        dataAdapterFormatos = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, lablesFormatos);
        dataAdapterFormatos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFormatosLista.setAdapter(dataAdapterFormatos);
    }

    /**
     * Lista los formatos que se han realizado
     */
    public void cargarFormatoEjecutados() {

        String formatoElegido = "", tf_nombre, ef_hora_creacion, ef_fecha_creacion;
        int tf_id = 0, ef_estado = 0, ef_calificacion = 0, ef_id = 0;

        items.clear();
        manager.open();

        formatoElegido = (String) spFormatosLista.getSelectedItem();
        if(formatoElegido != null){
            if(formatoElegido.length() > 0)
                tf_id = manager.getCodigoFormato(formatoElegido);
        }

        cursor = manager.cargarCursorFormatoEjecutado(tf_id, 0);
        if (cursor.moveToFirst()) {
            do {
                tf_nombre = cursor.getString(cursor.getColumnIndex("tf_nombre"));
                ef_id = cursor.getInt(cursor.getColumnIndex("ef_id"));
                ef_fecha_creacion = cursor.getString(cursor.getColumnIndex("ef_fecha_creacion"));
                ef_hora_creacion = cursor.getString(cursor.getColumnIndex("ef_hora_creacion"));
                ef_calificacion = cursor.getInt(cursor.getColumnIndex("ef_calificacion"));
                ef_estado = cursor.getInt(cursor.getColumnIndex("ef_estado"));

                items.add(new ItemEjecucionFormato(tf_id, tf_nombre, ef_id, ef_fecha_creacion,
                        ef_hora_creacion, ef_calificacion, ef_estado));

            } while (cursor.moveToNext());//accessing data upto last row from table
        }

        lvFormatosEjecutados.invalidateViews();
        cursor.close();
        manager.close();
        System.gc();
    }



    private void abrirFormato(int tf_id, int ef_id){
        Intent i = new Intent(this, VerFormato.class);
        Bundle bolsa = new Bundle();
        bolsa.putInt("cuadrilla", cuadrilla);
        bolsa.putInt("ef_id", ef_id);
        bolsa.putInt("tf_id", tf_id);
        i.putExtras(bolsa);
        startActivityForResult(i, 116);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_formato, menu);
        return true;
    }



    /**
     * Envia datos al server
     */
    private void descargarFormatos() {

        AlertDialog.Builder invalid_input_dialog = new AlertDialog.Builder(this);
        invalid_input_dialog.setTitle("DESCARGAR FORMATOS")
                .setMessage("Desea cargar el formato?")
                .setCancelable(true)
                .setPositiveButton("CARGAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (banderaEnvio) {
                            launchRest();
                            //launchBarDialog();
                            System.out.println("ATUALIZANDO ASISTENTES A CARGAR");
                        } else
                            Toast.makeText(getApplicationContext(), "Actualmente se encuentra enviando informacion, por favor espere", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }// Cierre enviarDatosServer


    public void launchRest(){
        RestFormatoModel restModel = new RestFormatoModel(this);
        restModel.getToken("jmunoz","jmunoz");
    }

    /**
     * Lanza el envio de ordenes pendientes por un dialog
     */
    public void launchBarDialog() {
        barProgressDialog = new ProgressDialog(this);
        barProgressDialog.setTitle("FORMATOS ...");
        barProgressDialog.setMessage("Cargando registros ...");
        barProgressDialog.setProgressStyle(barProgressDialog.STYLE_HORIZONTAL);
        barProgressDialog.setProgress(0);
        barProgressDialog.setMax(200);
        barProgressDialog.show();
        System.gc();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ///////////////////////////////////////////////////
                    ///////////////// TIPO FORMATO ////////////////////
                    ResultSet rs;
                    EjecucionFormatoModelServer managerServerEnvio = new EjecucionFormatoModelServer(1);
                    rs = managerServerEnvio.getInspeccionesTipoFormato();
                    System.out.println("ejecuto Inspecciones tipo formato");
                    final int upload = insertTipoFormato(rs, 2);
                    Thread.sleep(500);
                    System.out.println("consulto " + upload);

                    if (upload > 0) {
                        updateBarHandler.post(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), "SE LE CARGARON " + upload + " TIPO FORMATO", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    ////////////////////////////////////////////////////
                    ///////////////// MODULO FORMATO ////////////////////
                    rs = managerServerEnvio.getModuloFormato();
                    System.out.println("ejecuto Modulo Consulta");
                    final int upload2 = insertModuloFormato(rs, 2);
                    Thread.sleep(500);
                    System.out.println("consulto " + upload);

                    if (upload > 0) {
                        updateBarHandler.post(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), "SE LE CARGARON " + upload2 + " MODULOFORMATO", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    /////////////////////////////////////////////////
                    ///////////////// TIPO CAMPO ////////////////////
                    rs = managerServerEnvio.getTipoCampo();
                    System.out.println("ejecuto TIPOCAMPO");
                    final int upload3 = insertTipoCampo(rs, 2);
                    Thread.sleep(500);
                    System.out.println("consulto " + upload);

                    if (upload > 0) {
                        updateBarHandler.post(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), "SE LE CARGARON " + upload3 + " TIPOCAMPO", Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    ////////////////////////////////////////////////////
                    ///////////////// CAMPO FORMATO ////////////////////
                    rs = managerServerEnvio.getCampoFormato();
                    System.out.println("ejecuto CAMPOFORMATO");
                    final int upload4 = insertCampoFormato(rs, 2);
                    Thread.sleep(500);
                    System.out.println("consulto " + upload);

                    if (upload > 0) {
                        updateBarHandler.post(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), "SE LE CARGARON " + upload4 + " CAMPOFORMATO", Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    System.gc();
                    barProgressDialog.dismiss();
                } catch (Exception e) {
                    barProgressDialog.dismiss();
                }
            }
        }).start();
    }// Cierra launchBarDialog Barra de envio de ordenes

    /**
     * Insert BD tipo Formato
     * @param rs
     * @param callType
     * @return
     */
    private int insertTipoFormato(ResultSet rs, int callType) {
        int tamano = 0;
        try {
            int i_tf_id = rs.findColumn("tf_id");
            int i_tf_nombre = rs.findColumn("tf_nombre");

            EjecucionFormatoModel managerFormato = new EjecucionFormatoModel(getBaseContext());
            int tf_id = 0;
            String tf_nombre = "";

            rs.last(); //me voy al ultimo
            tamano = rs.getRow(); //pillo el tamano
            rs.beforeFirst(); // lo dejo donde estaba para tratarlo

            System.out.println("TAMANO DE CONSULTA " + tamano);

            barProgressDialog.setProgress(0);//Ponemos la barra de progreso a 0
            barProgressDialog.setMax(tamano + 1);//El maximo de la barra de progreso son 60, de los 60 segundo que va a durar la tarea en segundo plano

            managerFormato.open();
            while (rs.next()) {
                tf_id = rs.getInt(i_tf_id);
                tf_nombre = rs.getString(i_tf_nombre);
                managerFormato.insertTipoFormato(tf_id, tf_nombre);
                if (callType == 2) {
                    updateBarHandler.post(new Runnable() {
                        public void run() {
                            barProgressDialog.incrementProgressBy(1);
                            barProgressDialog.setMessage("Cargando TIPOFORMATO ...");
                        }
                    });
                }
                Thread.sleep(100);
            }
            managerFormato.close();
        } catch (Exception e) {
            System.out.println("ERROR " + e.getMessage());
        }
        return tamano;
    }

    /**
     * Insertar Modulo Formato
     * @param rs
     * @param callType
     * @return
     */
    private int insertModuloFormato(ResultSet rs, int callType) {
        int tamano = 0;
        try {
            int i_tipo_formato_id = rs.findColumn("tipo_formato_id");
            int i_mf_id = rs.findColumn("mf_id");
            int i_mf_nombre = rs.findColumn("mf_nombre");
            int i_mf_codigo = rs.findColumn("mf_codigo");
            int i_mf_numero_orden = rs.findColumn("mf_numero_orden");

            EjecucionFormatoModel managerFormato = new EjecucionFormatoModel(getBaseContext());
            int tipo_formato_id = 0, mf_id = 0, mf_codigo = 0, mf_numero_orden = 0;
            String mf_nombre = "";

            rs.last(); //me voy al ultimo
            tamano = rs.getRow(); //pillo el tamano
            rs.beforeFirst(); // lo dejo donde estaba para tratarlo

            System.out.println("TAMANO DE CONSULTA " + tamano);

            barProgressDialog.setProgress(0);//Ponemos la barra de progreso a 0
            barProgressDialog.setMax(tamano + 1);//El m�ximo de la barra de progreso son 60, de los 60 segundo que va a durar la tarea en segundo plano

            managerFormato.open();
            while (rs.next()) {
                tipo_formato_id = rs.getInt(i_tipo_formato_id);
                mf_id = rs.getInt(i_mf_id);
                mf_nombre = rs.getString(i_mf_nombre);
                mf_codigo = rs.getInt(i_mf_codigo);
                mf_numero_orden = rs.getInt(i_mf_numero_orden);
                managerFormato.insertModuloFormato(mf_id, mf_nombre, mf_codigo, mf_numero_orden, tipo_formato_id);
                if (callType == 2) {
                    updateBarHandler.post(new Runnable() {
                        public void run() {
                            barProgressDialog.incrementProgressBy(1);
                            barProgressDialog.setMessage("Cargando MODULOFORMATO ...");
                        }
                    });
                }
                Thread.sleep(100);
            }
            managerFormato.close();
        } catch (Exception e) {
            System.out.println("ERROR " + e.getMessage());
        }
        return tamano;
    } // Cierre insertar Ordenes de rs ResultSet en Base de datos

    /**
     * Insertar TipoCampo
     * @param rs
     * @param callType
     * @return
     */
    private int insertTipoCampo(ResultSet rs, int callType) {
        int tamano = 0;
        try {
            int i_tc_id = rs.findColumn("tc_id");
            int i_tc_descripcion = rs.findColumn("tc_descripcion");

            EjecucionFormatoModel managerFormato = new EjecucionFormatoModel(getBaseContext());
            int tc_id = 0;
            String tc_descripcion = "";

            rs.last(); //me voy al ultimo
            tamano = rs.getRow(); //pillo el tamano
            rs.beforeFirst(); // lo dejo donde estaba para tratarlo

            System.out.println("TAMANO DE CONSULTA " + tamano);

            barProgressDialog.setProgress(0);
            barProgressDialog.setMax(tamano + 1);

            managerFormato.open();
            while (rs.next()) {
                tc_id = rs.getInt(i_tc_id);
                tc_descripcion = rs.getString(i_tc_descripcion);
                managerFormato.insertTipoCampo(tc_id, tc_descripcion);
                if (callType == 2) {
                    updateBarHandler.post(new Runnable() {
                        public void run() {
                            barProgressDialog.incrementProgressBy(1);
                            barProgressDialog.setMessage("Cargando MODULOFORMATO ...");
                        }
                    });
                }
                Thread.sleep(100);
            }
            managerFormato.close();
        } catch (Exception e) {
            System.out.println("ERROR " + e.getMessage());
        }
        return tamano;
    }

    /**
     * Insetar Campo formato
     * @param rs
     * @param callType
     * @return
     */
    private int insertCampoFormato(ResultSet rs, int callType) {
        int tamano = 0;
        try {
            int i_cf_id = rs.findColumn("cf_id");
            int i_cf_nombre = rs.findColumn("cf_nombre");
            int i_cf_codigo = rs.findColumn("cf_codigo");
            int i_cf_numero_orden = rs.findColumn("cf_numero_orden");
            int i_cf_tabla_referencia = rs.findColumn("cf_tabla_referencia");
            int i_modulo_formato_id = rs.findColumn("modulo_formato_id");
            int i_tipo_campo_id = rs.findColumn("tipo_campo_id");
            int i_cf_descripcion = rs.findColumn("cf_descripcion");
            int i_cf_parent_id = rs.findColumn("cf_parent_id");
            int i_cf_level = rs.findColumn("cf_level");


            EjecucionFormatoModel managerFormato = new EjecucionFormatoModel(getBaseContext());
            int cf_id = 0, cf_codigo = 0, cf_numero_orden = 0, cf_parent_id, modulo_formato_id = 0, tipo_campo_id = 0, cf_level = 0;
            String cf_nombre = "", cf_tabla_referencia = "", cf_descripcion = "";

            rs.last(); //me voy al ultimo
            tamano = rs.getRow(); //pillo el tamano
            rs.beforeFirst(); // lo dejo donde estaba para tratarlo

            System.out.println("TAMANO DE CONSULTA " + tamano);

            barProgressDialog.setProgress(0);//Ponemos la barra de progreso a 0
            barProgressDialog.setMax(tamano + 1);//El m�ximo de la barra de progreso son 60, de los 60 segundo que va a durar la tarea en segundo plano

            managerFormato.open();
            while (rs.next()) {
                cf_id = rs.getInt(i_cf_id);
                cf_nombre = rs.getString(i_cf_nombre);
                cf_codigo = rs.getInt(i_cf_codigo);
                cf_numero_orden = rs.getInt(i_cf_numero_orden);
                cf_parent_id = rs.getInt(i_cf_parent_id);
                cf_tabla_referencia = rs.getString(i_cf_tabla_referencia);
                modulo_formato_id = rs.getInt(i_modulo_formato_id);
                tipo_campo_id = rs.getInt(i_tipo_campo_id);
                cf_descripcion = rs.getString(i_cf_descripcion);
                cf_level = rs.getInt(i_cf_level);

                managerFormato.insertCampoFormato(cf_id, cf_nombre, cf_codigo, cf_numero_orden, cf_parent_id, cf_tabla_referencia, modulo_formato_id, tipo_campo_id, cf_descripcion, cf_level);
                if (callType == 2) {
                    updateBarHandler.post(new Runnable() {
                        public void run() {
                            barProgressDialog.incrementProgressBy(1);
                            barProgressDialog.setMessage("Cargando MODULOFORMATO ...");
                        }
                    });
                }
                Thread.sleep(100);
            }
            managerFormato.close();
        } catch (Exception e) {
            System.out.println("ERROR " + e.getMessage());
        }
        return tamano;
    }
}
