package com.example.seguimientoOrdenes;

import com.example.gestiondeltec.R;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;

public class SeguimientoOrdenes extends Activity implements OnClickListener {

    private Spinner spOrden, spRowConsult, spSegRuta;
    private EditText etBuscar, etSegCicloBuscar;
    private Button btBuscar, btSegCicloBuscar, btSegRutaSelectAll;
    private ListView lvSeguimiento, lvSegRutas;
    private LinearLayout llSegConsulta, llSegRutas;
    private CheckBox cbGraficos;
    int codigo_cuadrilla, rowConsult = 0;
    String cicloConsulta = "", ordenConsulta = "ASC";

    List<ItemSeguimiento> itemsSeguimiento = new ArrayList<ItemSeguimiento>();
    ProgressDialog barProgressDialog;
    Handler updateBarHandler;
    ResultSet rsSeguimiento = null, rsRutas = null;

    String rutas = "";
    List<String> labelsRutas;
    List<String> labelsRutasSelect;
    Boolean salidaActivity = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seguimiento);

        spOrden = (Spinner) findViewById(R.id.spSegOrden);
        spRowConsult = (Spinner) findViewById(R.id.spRowConsult);
        etBuscar = (EditText) findViewById(R.id.etSegBuscar);
        btBuscar = (Button) findViewById(R.id.btSegBuscar);
        lvSeguimiento = (ListView) findViewById(R.id.lvSeguimiento);
        lvSegRutas = (ListView) findViewById(R.id.lvSegRutas);

        btSegCicloBuscar = (Button) findViewById(R.id.btSegCicloBuscar);
        etSegCicloBuscar = (EditText) findViewById(R.id.etSegCicloBuscar);
        spSegRuta = (Spinner) findViewById(R.id.spSegRuta);
        btSegRutaSelectAll = (Button) findViewById(R.id.btSegRutaSelectAll);
        llSegConsulta  = (LinearLayout) findViewById(R.id.llSegConsulta);
        llSegRutas  = (LinearLayout) findViewById(R.id.llSegRutas);
        cbGraficos = (CheckBox) findViewById(R.id.cbGraficos);

        Bundle bolsaR = getIntent().getExtras();
        codigo_cuadrilla = bolsaR.getInt("cuadrilla");

        ArrayAdapter<CharSequence> adapterOrdenEjecucion = ArrayAdapter.createFromResource(this, R.array.orden_seguimiento, android.R.layout.simple_spinner_item);
        adapterOrdenEjecucion.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spOrden.setAdapter(adapterOrdenEjecucion);

        ArrayAdapter<CharSequence> adapterRowConsult = ArrayAdapter.createFromResource(this, R.array.tipo_seguimiento, android.R.layout.simple_spinner_item);
        adapterRowConsult.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRowConsult.setAdapter(adapterRowConsult);

        lvSeguimiento.setAdapter(new ItemSeguimientoAdapter(this, itemsSeguimiento));

        btBuscar.setOnClickListener(this);
        btSegCicloBuscar.setOnClickListener(this);
        btSegRutaSelectAll.setOnClickListener(this);
        updateBarHandler = new Handler();

        labelsRutasSelect = new ArrayList<String>();

        lvSegRutas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
                labelsRutasSelect.remove(position);
                addRutasSeleccionadas();
            }
        });

        cbGraficos.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(itemsSeguimiento.size() > 0){
                    actualizarListView();
                }
            }
        });

        spSegRuta.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String ruta = (String) spSegRuta.getSelectedItem();
                if(ruta.length() > 0){
                    labelsRutasSelect.add(ruta);
                    addRutasSeleccionadas();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.actividy_seguimiento, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_rutas:
                if(llSegConsulta.getVisibility() == View.VISIBLE){
                    llSegConsulta.setVisibility(View.GONE);
                    llSegRutas.setVisibility(View.VISIBLE);
                    if(etBuscar.getText().toString().length() > 0)
                        etSegCicloBuscar.setText(etBuscar.getText().toString());
                }else{
                    llSegConsulta.setVisibility(View.VISIBLE);
                    llSegRutas.setVisibility(View.GONE);
                    if(etSegCicloBuscar.getText().toString().length() > 0)
                        etBuscar.setText(etSegCicloBuscar.getText().toString());
                }
                return true;
            case R.id.menu_salir:
                salidaActivity = true;
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btSegBuscar:
                cargarSeguimiento(etBuscar.getText().toString(), (String) spOrden.getSelectedItem(), spRowConsult.getSelectedItemPosition());
                break;
            case R.id.btSegCicloBuscar:
                String ciclo = etSegCicloBuscar.getText().toString();
                if(ciclo.length() > 0) {
                    cargarRutas(ciclo);
                }
                break;
            case R.id.btSegRutaSelectAll:
                if(labelsRutas.size() > 0){
                    labelsRutasSelect.clear();
                    if(btSegRutaSelectAll.getText().toString().contains("Todas")) {
                        labelsRutasSelect = new ArrayList<String>(labelsRutas);
                        btSegRutaSelectAll.setText("Ninguna");
                    }else btSegRutaSelectAll.setText("Todas");
                    addRutasSeleccionadas();
                }
                break;
        }
    }

    @Override
    public void finish() {
        if (salidaActivity) {
            super.finish();
        }
    }

    /**
     * @param textBuscar
     * @param ordenDatos
     * @param irowConsult
     */
    public void cargarSeguimiento(String textBuscar, String ordenDatos, int irowConsult) {

        cicloConsulta = textBuscar;
        ordenConsulta = ordenDatos;

        switch (irowConsult) {
            case 0: //PENDIENTES
                rowConsult = 5;
                break;
            case 1: //EJECUTADAS
                rowConsult = 4;
                break;
            case 2://CAUSADAS
                rowConsult = 6;
                break;
            case 3://TOTAL
                rowConsult = 3;
                break;
            case 4://SIN ACCESO
                rowConsult = 9;
                break;
            case 5://SIN MEDIDOR
                rowConsult = 7;
                break;
            default:
                rowConsult = 1;
                break;
        }

        rutas = convertRutas();

        barProgressDialog = new ProgressDialog(this);
        barProgressDialog.setTitle("CONSULTA SEGUIMIENTO ...");
        barProgressDialog.setMessage("Consultando Base de Datos ...");
        barProgressDialog.setProgressStyle(barProgressDialog.STYLE_HORIZONTAL);
        barProgressDialog.setProgress(0);
        barProgressDialog.setMax(20);
        barProgressDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SeguimientoOrdenesModelServer managerServerEnvio = new SeguimientoOrdenesModelServer(2);
                    Thread.sleep(100);
                    rsSeguimiento = managerServerEnvio.get_seguimiento_lectores(String.valueOf(codigo_cuadrilla), cicloConsulta, ordenConsulta, rowConsult, rutas);

                    Thread.sleep(100);
                    updateBarHandler.post(new Runnable() {
                        public void run() {
                            barProgressDialog.incrementProgressBy(15);
                            barProgressDialog.setMessage("Cargando Datos ...");
                            actualizarListView();
                        }
                    });

                    barProgressDialog.dismiss();
                } catch (Exception e) {
                    barProgressDialog.dismiss();
                }
            }
        }).start();
        System.gc();
    }// Cierra cargarOrdenes

    public void actualizarListView() {
        int per_codigo;
        String nombre;
        int total = 0, ejecutada = 0, pendiente = 0, total_causas = 0, med_no_existe = 0,
                med_ilegible = 0, impedimento_cambio = 0, lote = 0, fuerza_mayor = 0, impedimento_tapado = 0, impedimento_reja = 0;
        String fecha_inicio;
        String fecha_fin;

        itemsSeguimiento.clear();
        Boolean graficos = cbGraficos.isChecked();

        if (rsSeguimiento != null) {
            try {
                rsSeguimiento.beforeFirst();
                while (rsSeguimiento.next()) {
                    per_codigo = rsSeguimiento.getInt("per_codigo");
                    nombre = rsSeguimiento.getString("nombre");
                    per_codigo = rsSeguimiento.getInt("per_codigo");
                    total = rsSeguimiento.getInt("total");
                    ejecutada = rsSeguimiento.getInt("ejecutada");
                    pendiente = rsSeguimiento.getInt("pendiente");
                    total_causas = rsSeguimiento.getInt("total_causas");
                    impedimento_tapado = rsSeguimiento.getInt("impedimento_tapado");
                    impedimento_reja = rsSeguimiento.getInt("impedimento_reja");
                    //med_ilegible = rsSeguimiento.getInt("med_ilegible");
                    impedimento_cambio = rsSeguimiento.getInt("impedimento_cambio");
                    //lote = rsSeguimiento.getInt("lote");
                    //fuerza_mayor = rsSeguimiento.getInt("fuerza_mayor");
                    fecha_inicio = rsSeguimiento.getString("fecha_inicio");
                    fecha_fin = rsSeguimiento.getString("fecha_fin");

                    itemsSeguimiento.add(new ItemSeguimiento(per_codigo, nombre, total, ejecutada, pendiente, total_causas,
                            med_no_existe, med_ilegible, impedimento_cambio, lote, fuerza_mayor, impedimento_tapado,
                            impedimento_reja, fecha_inicio, fecha_fin, graficos));
                }
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } // lo dejo donde estaba para tratarlo
        }

        lvSeguimiento.invalidateViews();
        System.gc();
    }


    public void cargarRutas(String textBuscar) {

        cicloConsulta = textBuscar;

        barProgressDialog = new ProgressDialog(this);
        barProgressDialog.setTitle("CONSULTA RUTAS ...");
        barProgressDialog.setMessage("Consultando Base de Datos ...");
        barProgressDialog.setProgressStyle(barProgressDialog.STYLE_HORIZONTAL);
        barProgressDialog.setProgress(0);
        barProgressDialog.setMax(20);
        barProgressDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SeguimientoOrdenesModelServer managerServerEnvio = new SeguimientoOrdenesModelServer(2);
                    Thread.sleep(100);
                    labelsRutas = managerServerEnvio.get_rutas_ciclo(cicloConsulta);

                    Thread.sleep(100);
                    updateBarHandler.post(new Runnable() {
                        public void run() {
                            barProgressDialog.incrementProgressBy(15);
                            barProgressDialog.setMessage("Cargando Datos ...");
                            actualizarSpRutas();
                        }
                    });

                    barProgressDialog.dismiss();
                } catch (Exception e) {
                    barProgressDialog.dismiss();
                }
            }
        }).start();
        System.gc();
    }

    public void actualizarSpRutas(){
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, labelsRutas);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spSegRuta.setAdapter(dataAdapter);
    }

    public void addRutasSeleccionadas(){
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                labelsRutasSelect );
        lvSegRutas.setAdapter(arrayAdapter);
    }

    public String convertRutas(){
        String rutassql = "";
        for(int idx = 0;idx < labelsRutasSelect.size();idx++){
            rutassql += "'" + labelsRutasSelect.get(idx).toString() + "'";
            if(idx < (labelsRutasSelect.size()-1))
                rutassql += ",";
        }
        return rutassql;
    }
}
