package com.example.config;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.Logueo.DataBaseManager;
import com.example.Logueo.DataBasePostgresManager;
import com.example.gestionOrdenes.EjecucionOrdenes;
import com.example.gestiondeltec.R;

import java.sql.ResultSet;

/**
 * Created by jasson on 3/03/17.
 */

public class GestionObservaciones extends Activity {
    /** Called when the activity is first created. */

    private TextView tvServidorObs, tvLocalObs, tvLocalCenso;
    private Button btUpdateObservaciones,btUpdateCenso, btUpdateRefrescar;
    private ButtonClicked clicked;
    DataBasePostgresManager managerServer;
    ProgressBar progressBarUpdate;
    int progreso = 0;
    private Handler mHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_update_obs);
        tvServidorObs = (TextView) findViewById(R.id.tvServidorObs);
        tvLocalCenso = (TextView) findViewById(R.id.tvLocalCenso);
        tvLocalObs = (TextView) findViewById(R.id.tvLocalObs);

        btUpdateObservaciones = (Button) findViewById(R.id.btUpdateObservaciones);
        btUpdateCenso = (Button) findViewById(R.id.btUpdateCenso);
        btUpdateRefrescar = (Button) findViewById(R.id.btUpdateRefrescar);
        progressBarUpdate = (ProgressBar)findViewById(R.id.progressBarUpdate);
        clicked = new ButtonClicked();
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        mHandler = new Handler();
        btUpdateObservaciones.setOnClickListener(clicked);
        btUpdateRefrescar.setOnClickListener(clicked);
        btUpdateCenso.setOnClickListener(clicked);

        managerServer = new DataBasePostgresManager(1);
        refrescarDatos();
    }

    @Override
    public void finish() {

        Intent i = new Intent(this, EjecucionOrdenes.class);
        this.setResult(RESULT_OK, i);
        super.finish();
        System.out.println("Intenta cerrar updateObs 222");
    }

    /**
     * Sincronizar observaciones del servidor
     */
    public void sincronizarObservaciones(){

        Thread sqlThread = new Thread() {
            public void run() {
                ResultSet rs  = managerServer.sincronizarObservaciones();
                insertarObservaciones(rs);
                //manager.insertar_stock_masivo_bulk(rs,codigoObtenido);
            }
        };
        sqlThread.start();
    }// Cierre insertar Observaciones de rs ResultSet en Base de datos

    /**
     * Insertar Observaciones de rs ResultSet en Base de datos
     * @param   rs
     */
    public void insertarObservaciones(ResultSet rs)
    {
        DataBaseManager managerOrden = new DataBaseManager(getBaseContext());

        try
        {
            int i_cod_observacion  = rs.findColumn("codobservacion");
            int i_tip_codigo   = rs.findColumn("tip_codigo");
            int i_sol_consumo    = rs.findColumn("solconsumo");
            int i_tip_nombre   = rs.findColumn("descripcion");
            int tip_codigo = 0;
            String tip_nombre = "", sol_consumo= "", cod_observacion = "";

            rs.last(); //me voy al ultimo
            final int tamano = rs.getRow(); //pillo el tamano
            rs.beforeFirst(); // lo dejo donde estaba para tratarlo
            progressBarUpdate.setProgress(0);//Ponemos la barra de progreso a 0
            progressBarUpdate.setMax(tamano);//El maximo de la barra de progreso son 60, de los 60 segundo que va a durar la tarea en segundo plano.

            if(tamano > 0) {
                managerOrden.open();
                managerOrden.eliminarObservaciones();
                managerOrden.close();
            }
            mHandler.post(new Runnable() {
                public void run() {
                    tvServidorObs.setText(String.valueOf(tamano));
                }
            });

            progreso = 0;
            int contador = 0;

            while(rs.next()) {

                cod_observacion = rs.getString(i_cod_observacion);
                tip_codigo = rs.getInt(i_tip_codigo);
                sol_consumo = rs.getString(i_sol_consumo);
                tip_nombre = rs.getString(i_tip_nombre);

                if(tip_nombre.length() < 4)
                    tip_nombre = "";
                managerOrden.open();
                managerOrden.insertar_observacion(cod_observacion, tip_codigo, sol_consumo, tip_nombre);
                managerOrden.close();
                progreso++;
                // Update the progress bar
                if (progreso < tamano){
                    contador++;
                    if(contador == 40){
                        contador = 0;
                        // Update the progress bar
                        mHandler.post(new Runnable() {
                            public void run() {
                                progressBarUpdate.setProgress(progreso);
                            }
                        });
                    }
                }else{
                    // Update the progress bar
                    mHandler.post(new Runnable() {
                        public void run() {
                            progressBarUpdate.setProgress(progreso);
                        }
                    });
                    System.gc();
                }
            }
            rs.close();
        }
        catch(Exception e){}
    } // Cierre insertar Observaciones de rs ResultSet en Base de datos



    public void refrescarDatos(){
        DataBaseManager managerOrden = new DataBaseManager(getBaseContext());
        managerOrden.open();
        Bundle bolsa = managerOrden.getCountObsCodigo();
        managerOrden.close();
        int total = bolsa.getInt("observacion");
        tvLocalObs.setText(String.valueOf(total));
        //total = bolsa.getInt("censo");
        //tvLocalCenso.setText(String.valueOf(total));
    }

    class ButtonClicked implements View.OnClickListener
    {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btUpdateObservaciones:
                    sincronizarObservaciones();
                    break;
                case R.id.btUpdateCenso:
                    //sincronizarCenso();
                    break;
                case R.id.btUpdateRefrescar:
                    refrescarDatos();
                    break;
                default:
                    break;
            }
        }
    }
}
