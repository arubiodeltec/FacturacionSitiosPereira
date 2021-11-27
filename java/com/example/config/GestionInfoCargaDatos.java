package com.example.config;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.Logueo.DataBaseManager;
import com.example.Logueo.DataBasePostgresManager;
import com.example.exportDatabase.ExportImportDB;
import com.example.gestiondeltec.R;

import java.sql.ResultSet;


public class GestionInfoCargaDatos extends Activity implements View.OnClickListener {
    private TextView tvRepFacImpresionLocal, tvRepFacImpresion, tvRepFacLaborConceptos, tvRepFacLaborConceptosLocal, tvRepFacRangos,tvRepFacRangosLocal,tvRepScmElementosLectura,tvRepScmElementosLecturaLocal,tvRepScmOrdenesTrabajo,tvRepScmOrdenesTrabajoLocal;
    private ImageButton ibExportDatabase;
    String ordenesCargadas;
    DataBaseManager manager;
    private DataBasePostgresManager managerServer;
    ProgressBar progressBarUpdate;
    Cursor countLocalFacImpresion;
    private Handler mHandler;
    Bundle bolsaR;

    int cuadrilla;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        System.out.println("print oncreate");
        super.onCreate(savedInstanceState);
        managerServer = new DataBasePostgresManager(1);
        setContentView(R.layout.dialog_reporte_ordenes_fens);
        tvRepFacImpresionLocal = (TextView) findViewById(R.id.tvRepFacImpresionLocal);
        tvRepFacImpresion = (TextView) findViewById(R.id.tvRepFacImpresion);
        tvRepFacLaborConceptos = (TextView) findViewById(R.id.tvRepFacLaborConceptos);
        tvRepFacLaborConceptosLocal = (TextView) findViewById(R.id.tvRepFacLaborConceptosLocal);
        tvRepFacRangos = (TextView) findViewById(R.id.tvRepFacRangos);
        tvRepFacRangosLocal = (TextView) findViewById(R.id.tvRepFacRangosLocal);
        tvRepScmElementosLectura = (TextView) findViewById(R.id.tvRepScmElementosLectura);
        tvRepScmElementosLecturaLocal = (TextView) findViewById(R.id.tvRepScmElementosLecturaLocal);
        tvRepScmOrdenesTrabajo = (TextView) findViewById(R.id.tvRepScmOrdenesTrabajo);
        tvRepScmOrdenesTrabajoLocal = (TextView) findViewById(R.id.tvRepScmOrdenesTrabajoLocal);
        ibExportDatabase = (ImageButton) findViewById(R.id.ibExportDatabase);
        ibExportDatabase.setOnClickListener(this);
        bolsaR = getIntent().getExtras();

        final String ordenesCargadas = sincronizarDatosServer();

        countLocalFacImpresion = consultOrdenesFacImpresion(ordenesCargadas);

        if(countLocalFacImpresion.moveToFirst()) {
            do {
                String facImpresionLocal = String.valueOf(countLocalFacImpresion.getInt(countLocalFacImpresion.getColumnIndex("fac_impresion")));
                tvRepFacImpresionLocal.setText(facImpresionLocal);
                String facLabor = String.valueOf(countLocalFacImpresion.getInt(countLocalFacImpresion.getColumnIndex("fac_laborconceptos")));
                tvRepFacLaborConceptosLocal.setText(facLabor);
                String facRangos = String.valueOf(countLocalFacImpresion.getInt(countLocalFacImpresion.getColumnIndex("rangos")));
                tvRepFacRangosLocal.setText(facRangos);
                String scmElementos = String.valueOf(countLocalFacImpresion.getInt(countLocalFacImpresion.getColumnIndex("scm_elementos_lectura")));
                tvRepScmElementosLecturaLocal.setText(scmElementos);
                String scmOrdenes = String.valueOf(countLocalFacImpresion.getInt(countLocalFacImpresion.getColumnIndex("scm_trabajo")));
                tvRepScmOrdenesTrabajoLocal.setText(scmOrdenes);
            } while (countLocalFacImpresion.moveToNext());
        }

        Thread sqlThread = new Thread() {
            public void run() {
                ResultSet rs  = managerServer.sincronizarDatosServer(ordenesCargadas, "20210922224730");
                try {
                    while (rs.next()) {

                        String facdatoimpresion = rs.getString("fac_impresion");
                        String facrangos = rs.getString("rangos");
                        String faclabor = rs.getString("fac_laborconceptos");
                        String scmelemento = rs.getString("scm_elementos_lectura");
                        String scmordenes = rs.getString("scm_trabajo");


                        setText(facdatoimpresion, faclabor, facrangos, scmelemento, scmordenes);




                    }

                }catch(Exception e){
                    e.toString();

                }
                //System.out.println(rs);
                //manager.insertar_stock_masivo_bulk(rs,codigoObtenido);
            }
        };
        sqlThread.start();


    }

    @Override
    public void onResume() {
        super.onResume();

        if (bolsaR != null) {

            cuadrilla = bolsaR.getInt("tipo");

        }
    }
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.ibExportDatabase:
                ExportImportDB db = new ExportImportDB();
                db.exportDB(cuadrilla);
                break;
        }

    }


    private Cursor consultOrdenesFacImpresion(String ordenesCargadas) {
        DataBaseManager managerOrden = new DataBaseManager(getBaseContext());
        managerOrden.open();
        Cursor conteo = managerOrden.getCountFacImpresion(ordenesCargadas,"20210922224730" );
        return conteo;
    }





    public String sincronizarDatosServer() {
        Cursor cursor;
        int tamano = 0, aff = 0;

        DataBaseManager managerOrden = new DataBaseManager(getBaseContext());

        managerOrden.open();
        cursor = managerOrden.cargarConteoOrdenes();
        String strOrdenServicio ="";
        if(cursor.moveToFirst()){
            int orden_servicio = 0;

            do{
                orden_servicio = cursor.getInt(cursor.getColumnIndex("ose_codigo"));

                strOrdenServicio += orden_servicio  + ",";

            }while(cursor.moveToNext());//accessing data upto last row from table
        }
        strOrdenServicio = strOrdenServicio.substring(0,strOrdenServicio.length()-1);
        return strOrdenServicio;


    }// Cierre insertar Observaciones de rs ResultSet en Base de datos

    private void setText(final String facdatoimpresion, final String faclabor, final String facrangos, final String scmelemento, final String scmordenes){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvRepFacImpresion.setText(facdatoimpresion);
                tvRepFacLaborConceptos.setText(faclabor);
                tvRepFacRangos.setText(facrangos);
                tvRepScmElementosLectura.setText(scmelemento);
                tvRepScmOrdenesTrabajo.setText(scmordenes);
            }
        });
    }



}
