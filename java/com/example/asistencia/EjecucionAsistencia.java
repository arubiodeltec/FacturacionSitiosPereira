package com.example.asistencia;

import com.example.gestiondeltec.R;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.location.LocationSyncActivity;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by DELTEC on 21/07/2015.
 */
public class EjecucionAsistencia extends LocationSyncActivity implements View.OnClickListener {

    private EjecucionAsistenciaModel manager;
    private Cursor cursor;
    private Button btAsisBuscar;
    private EditText etAsisBuscar;
    private ListView lvAsistencia;
    private Spinner spAsisEstado;
    private ImageButton imageButtonAsisAddTecnico;
    private TextView tvAsisEje, tvAsisEnv, tvAsisFaltas, tvAsisDiaSuper, tvAsisNombreCuadrilla, tvAsisTotalAsis, tvAsisConsultaServer;
    private LinearLayout pAsisResumen;
    private int cuadrilla = 0;
    List<ItemAsistencia> items = new ArrayList<ItemAsistencia>();

    public boolean banderaEnvio = true;
    ProgressDialog barProgressDialog;
    Handler updateBarHandler;
    String strBuscar = "", nombreCuadrilla = "";
    int indiceInicial = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asistencia);
        manager = new EjecucionAsistenciaModel(this);

        btAsisBuscar = (Button) findViewById(R.id.btAsisBuscar);
        etAsisBuscar = (EditText) findViewById(R.id.etAsisBuscar);
        lvAsistencia = (ListView) findViewById(R.id.lvAsistencia);
        spAsisEstado = (Spinner) findViewById(R.id.spAsisEstado);
        imageButtonAsisAddTecnico = (ImageButton) findViewById(R.id.imageButtonAsisAddTecnico);
        tvAsisEje = (TextView) findViewById(R.id.tvAsisTotalEje);
        tvAsisEnv = (TextView) findViewById(R.id.tvAsisTotalEnv);
        tvAsisFaltas = (TextView) findViewById(R.id.tvAsisTotalFalt);
        tvAsisDiaSuper = (TextView) findViewById(R.id.tvAsisDiaSuper);
        tvAsisNombreCuadrilla = (TextView) findViewById(R.id.tvAsisNombreCuadrilla);
        tvAsisTotalAsis = (TextView) findViewById(R.id.tvAsisTotalAsis);
        pAsisResumen = (LinearLayout) findViewById(R.id.pAsisResumen);
        tvAsisConsultaServer = (TextView) findViewById(R.id.tvAsisConsultaServer);

        Bundle bolsaDatosIniciales = getIntent().getExtras();
        cuadrilla = bolsaDatosIniciales.getInt("cuadrilla");
        nombreCuadrilla = bolsaDatosIniciales.getString("nombreKey");
        if (nombreCuadrilla.length() > 12)
            nombreCuadrilla = nombreCuadrilla.substring(0, 12);

        tvAsisNombreCuadrilla.setText(nombreCuadrilla);
        ArrayAdapter<CharSequence> adapterAsistenciaEstado = ArrayAdapter.createFromResource(this, R.array.asistencia_estado, android.R.layout.simple_spinner_item);
        adapterAsistenciaEstado.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spAsisEstado.setAdapter(adapterAsistenciaEstado);
        lvAsistencia.setAdapter(new ItemAdapterAsistencia(this, items));
        imageButtonAsisAddTecnico.setOnClickListener(this);
        btAsisBuscar.setOnClickListener(this);

        updateBarHandler = new Handler();
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.imageButtonAsisAddTecnico:
                showAddTecnico();
                break;
            case R.id.btAsisBuscar:
                String codCuadrilla = etAsisBuscar.getText().toString();
                if (codCuadrilla.length() > 0) {
                    if (pAsisResumen.getVisibility() == View.VISIBLE) {
                        searchAssintantLocal(codCuadrilla);
                    } else {
                        searchAssintantServer(codCuadrilla);
                    }

                } else etAsisBuscar.setError("Codigo Tecnico a Buscar");
                break;
        }
    }

    private void searchAssintantLocal(String busqueda){

        int iCodAsistente;
        int iBusqueda = Integer.valueOf(busqueda);

        if (busqueda.compareTo(strBuscar) != 0) {
            System.out.println("CAMBIO BUSQUEDA " + busqueda + " por " + strBuscar);
            indiceInicial = 0;
            strBuscar = busqueda;
        }

        if (!lvAsistencia.getAdapter().isEmpty()) {
            for (int idx = indiceInicial; idx < lvAsistencia.getAdapter().getCount(); idx++) {
                ItemAsistencia item = (ItemAsistencia) lvAsistencia.getAdapter().getItem(idx);
                iCodAsistente = item.getCodigoAsistente();
                if (iCodAsistente == iBusqueda ) {
                    lvAsistencia.setSelection(idx);
                    indiceInicial = idx + 1;
                    break;
                } else if (idx == (lvAsistencia.getAdapter().getCount() - 1)) {
                    indiceInicial = 0;
                    Toast.makeText(getApplicationContext(), "No encontrado", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    private void showAddTecnico() {

        if (pAsisResumen.getVisibility() == View.VISIBLE) {
            pAsisResumen.setVisibility(View.GONE);
            tvAsisConsultaServer.setVisibility(View.VISIBLE);
            imageButtonAsisAddTecnico.setImageResource(R.drawable.delete_user);
        } else {
            pAsisResumen.setVisibility(View.VISIBLE);
            tvAsisConsultaServer.setVisibility(View.VISIBLE);
            tvAsisConsultaServer.setVisibility(View.GONE);
            imageButtonAsisAddTecnico.setImageResource(R.drawable.add_user);
        }
    }

    public void searchAssintantServer(final String tecnicoBuscar) {
        barProgressDialog = new ProgressDialog(this);
        barProgressDialog.setTitle("CONSULTANDO LECTOR ...");
        barProgressDialog.setMessage("Cargando lector ...");
        barProgressDialog.setProgressStyle(barProgressDialog.STYLE_HORIZONTAL);
        barProgressDialog.setProgress(0);
        barProgressDialog.setMax(200);
        barProgressDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ResultSet rs;
                    EjecucionAsistenciaModelServer managerServerEnvio = new EjecucionAsistenciaModelServer(1);
                    rs = managerServerEnvio.getAssistant(tecnicoBuscar);

                    while (rs.next()) {
                        final String perNombre = rs.getString(rs.findColumn("per_nombre"));
                        Thread.sleep(500);
                        updateBarHandler.post(new Runnable() {
                            public void run() {
                                barProgressDialog.setProgress(180);
                                barProgressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "SE ENCONTRO LECTOR " + perNombre + " EN SERVIDOR", Toast.LENGTH_LONG).show();
                                agregarTecnicoAsistente(Integer.valueOf(tecnicoBuscar), perNombre);
                                showAddTecnico();
                            }
                        });
                    }
                    barProgressDialog.dismiss();
                } catch (Exception e) {
                    barProgressDialog.dismiss();
                }
            }
        }).start();
        System.gc();
    }

    private void agregarTecnicoAsistente(final int codigoTecnico, final String nombreTecnico) {

        AlertDialog.Builder invalid_input_dialog = new AlertDialog.Builder(this);
        invalid_input_dialog.setTitle("TECNICO " + nombreTecnico)
                .setMessage("Desea EL TECNICO " + codigoTecnico + " - " + nombreTecnico + " a la conformaci?n de su grupo")
                .setCancelable(true)
                .setPositiveButton("AGREGAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        manager.open();
                        manager.insertAssistants(cuadrilla, codigoTecnico, nombreTecnico);
                        manager.close();
                        cargarAsistencia("", 0);
                    }
                })
                .setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                    }
                })
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_asistencia, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_cargar_asistencia:
                descargarAsistencia();
                return true;
            case R.id.menu_enviar_asistencia:
                enviarDatosServer();
                return true;
            case R.id.menu_refrescar_asistencia:
                cargarAsistencia("", 0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Envia datos al server
     */
    private void descargarAsistencia() {

        AlertDialog.Builder invalid_input_dialog = new AlertDialog.Builder(this);
        invalid_input_dialog.setTitle("CARGAR ASISTENCIA DIARIA ")
                .setMessage("Desea cargar el listado de lectores asignado?")
                .setCancelable(true)
                .setPositiveButton("CARGAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (banderaEnvio) {
                            launchBarDialog();
                            System.out.println("ATUALIZANDO ASISTENTES A CARGAR");
                        } else
                            Toast.makeText(getApplicationContext(), "Actualmente se encuentra enviando informacion, por favor espere", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                    }
                })
                .show();
    }// Cierre enviarDatosServer

    /**
     * Lanza el envio de ordenes pendientes por un dialog
     */
    public void launchBarDialog() {
        barProgressDialog = new ProgressDialog(this);
        barProgressDialog.setTitle("REGISTRO DE ASISTENCIA ...");
        barProgressDialog.setMessage("Cargando tecnicos ...");
        barProgressDialog.setProgressStyle(barProgressDialog.STYLE_HORIZONTAL);
        barProgressDialog.setProgress(0);
        barProgressDialog.setMax(200);
        barProgressDialog.show();
        System.gc();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ResultSet rs;
                    EjecucionAsistenciaModelServer managerServerEnvio = new EjecucionAsistenciaModelServer(1);
                    rs = managerServerEnvio.getAssistantSuper(String.valueOf(cuadrilla));
                    System.out.println("ejecuto consulta");
                    final int upload = insertarAssintent(rs, 2);
                    Thread.sleep(500);
                    System.out.println("consulto " + upload);

                    if (upload > 0) {
                        updateBarHandler.post(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), "SE LE CARGARON " + upload + " lecturas para REVISAR", Toast.LENGTH_LONG).show();
                                cargarAsistencia("", 0);
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
     * Insertar Ordenes de rs ResultSet en Base de datos
     *
     * @param rs
     */
    private int insertarAssintent(ResultSet rs, int callType) {
        int tamano = 0;
        try {
            int i_sup_super_per_codigo = rs.findColumn("sup_super_per_codigo");
            int i_lect_per_codigo = rs.findColumn("sup_lect_per_codigo");
            int i_per_nombre = rs.findColumn("per_nombre");//LECTURA

            EjecucionAsistenciaModel managerAsistencia = new EjecucionAsistenciaModel(getBaseContext());
            int sup_super_per_codigo = 0, lect_per_codigo = 0;
            String per_nombre = "";

            rs.last(); //me voy al ultimo
            tamano = rs.getRow(); //pillo el tamano
            rs.beforeFirst(); // lo dejo donde estaba para tratarlo

            System.out.println("TAMANO DE CONSULTA " + tamano);

            barProgressDialog.setProgress(0);//Ponemos la barra de progreso a 0
            barProgressDialog.setMax(tamano + 1);//El maximo de la barra de progreso son 60, de los 60 segundo que va a durar la tarea en segundo plano

            managerAsistencia.open();
            managerAsistencia.deleteAssintant(String.valueOf(cuadrilla));

            while (rs.next()) {

                sup_super_per_codigo = rs.getInt(i_sup_super_per_codigo);
                lect_per_codigo = rs.getInt(i_lect_per_codigo);
                per_nombre = rs.getString(i_per_nombre);
                managerAsistencia.insertAssistants(sup_super_per_codigo,  lect_per_codigo, per_nombre);
                if (callType == 2) {
                    updateBarHandler.post(new Runnable() {
                        public void run() {
                            barProgressDialog.incrementProgressBy(1);
                            barProgressDialog.setMessage("Cargando LECTORES ...");
                        }
                    });
                }
                Thread.sleep(100);
            }
            managerAsistencia.close();
        } catch (Exception e) {
            System.out.println("ERROR " + e.getMessage());
        }
        return tamano;
    } // Cierre insertar Ordenes de rs ResultSet en Base de datos

    public void cargarAsistencia(String busqueda,  int estado) {
        String nombre_asistente, fecha_asistente, comentario_asistencia;
        int estado_asistencia, tipo_asistencia, codigo_asistente;
        String tip_orden = "209";
        String strEstado = "";

        switch (estado) {
            case 0: //PENDIENTES
                strEstado = "";
                break;
            case 1://Finalizadas
                strEstado = "27";
                break;
            case 2://FALTAS
                estado = 27;
                tip_orden = "210";
                break;
            case 3://TARDE
                estado = 27;
                tip_orden = "210";
                break;
            default:
                estado = 1;
                break;
        }

        items.clear();

        manager.open();
        cursor = manager.cargarCursorAsistencia(String.valueOf(cuadrilla), "", estado);
        if (cursor.moveToFirst()) {
            do {
                codigo_asistente = cursor.getInt(cursor.getColumnIndex("sup_lect_per_codigo"));
                nombre_asistente = cursor.getString(cursor.getColumnIndex("sup_lect_per_nombre"));
                fecha_asistente =  cursor.getString(cursor.getColumnIndex("asi_fecha"));
                tipo_asistencia  = cursor.getInt(cursor.getColumnIndex("asi_tipo"));
                comentario_asistencia = cursor.getString(cursor.getColumnIndex("asi_observacion"));
                if(cursor.isNull(cursor.getColumnIndex("asi_estado")))
                    estado_asistencia = -1;
                else estado_asistencia = cursor.getInt(cursor.getColumnIndex("asi_estado"));

                items.add(new ItemAsistencia(cuadrilla, codigo_asistente, nombre_asistente, fecha_asistente, estado_asistencia, tipo_asistencia, comentario_asistencia));

            } while (cursor.moveToNext());//accessing data upto last row from table
        }

        lvAsistencia.invalidateViews();

        cursor.close();
        manager.close();

        actualizarAsisPendientes();
        System.gc();
    }// Cierra cargarOrdenes

    /**
     * Actualiza Totales ordenes, ejecutadas, anuladas, enviadas, pendientes
     */
    public Bundle actualizarAsisPendientes() {
        Bundle bolsa;
        manager.open();
        bolsa = manager.reporteAsistencia(String.valueOf(cuadrilla));
        manager.close();
        if (!bolsa.isEmpty()) {
            tvAsisTotalAsis.setText(String.valueOf(bolsa.getInt("total")));
            tvAsisEje.setText(String.valueOf(bolsa.getInt("ejecutadas")));
            tvAsisFaltas.setText(String.valueOf(bolsa.getInt("pendientes")));
            tvAsisEnv.setText(String.valueOf(bolsa.getInt("enviadas")));
        }
        return bolsa;
    }// Cierra actualizarPendientes

    /**
     * Envia datos al server
     */
    private void enviarDatosServer() {

        AlertDialog.Builder invalid_input_dialog = new AlertDialog.Builder(this);
        invalid_input_dialog.setTitle("ENVIAR ASISTENCIA " + tvAsisTotalAsis.getText().toString() + " EJECUTADAS ")
                .setMessage("DESEA ENVIAR EL LISTADO DE ASISTENCIA DE D?A DE HOY " + tvAsisEje.getText().toString() + " ASISTENTES POR ENVIAR ")
                .setCancelable(true)
                .setPositiveButton("ENVIAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (banderaEnvio) {
                            launchBarDialogSendAssintant();
                        } else
                            Toast.makeText(getApplicationContext(), "Actualmente se encuentra enviando informacion, por favor espere", Toast.LENGTH_SHORT).show();
                        System.out.println("ATUALIZANDO LECTURAS A ENVIAR");
                    }
                })
                .setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                    }
                })
                .show();
    }// Cierre enviarDatosServer

    /**
     * Lanza el envio de ordenes pendientes por un dialog
     */
    public void launchBarDialogSendAssintant() {
        barProgressDialog = new ProgressDialog(this);

        barProgressDialog.setTitle("ENVIO LISTADO ASISTENCIA ...");
        barProgressDialog.setMessage("Enviando Asistencia...");
        barProgressDialog.setProgressStyle(barProgressDialog.STYLE_HORIZONTAL);
        barProgressDialog.setProgress(0);
        barProgressDialog.setMax(20);
        barProgressDialog.show();

        banderaEnvio = false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Cursor cursor;
                    int asi_per_codigo, asi_supervisor = 0, asi_estado = 0, asi_tipo = 0, tamano= 0;
                    boolean actualizoOrden = false;
                    String asi_fecha = "", asi_fecha_creacion = "", asi_fecha_actulizacion = "", asi_observacion = "";

                    EjecucionAsistenciaModel managerOrden = new EjecucionAsistenciaModel(getBaseContext());
                    managerOrden.open();
                    cursor = managerOrden.asistenciaPendinte();
                    if (cursor.moveToFirst()) {
                        EjecucionAsistenciaModelServer managerServerEnvio = new EjecucionAsistenciaModelServer(1);
                        tamano = cursor.getCount();
                        barProgressDialog.setProgress(0);//Ponemos la barra de progreso a 0
                        barProgressDialog.setMax(tamano);//El m�ximo de la barra de progreso son 60, de los 60 segundo que va a durar la tarea en segundo plano.

                        do {
                            asi_fecha = cursor.getString(cursor.getColumnIndex("asi_fecha"));
                            asi_per_codigo = cursor.getInt(cursor.getColumnIndex("asi_per_codigo"));
                            asi_supervisor = cursor.getInt(cursor.getColumnIndex("asi_supervisor"));
                            asi_estado = cursor.getInt(cursor.getColumnIndex("asi_estado"));
                            asi_tipo = cursor.getInt(cursor.getColumnIndex("asi_tipo"));
                            asi_fecha_creacion = cursor.getString(cursor.getColumnIndex("asi_fecha_creacion"));
                            asi_fecha_actulizacion = cursor.getString(cursor.getColumnIndex("asi_fecha_actulizacion"));
                            asi_observacion = cursor.getString(cursor.getColumnIndex("asi_observacion"));

                            actualizoOrden = managerServerEnvio.actualizarAsistencia(asi_fecha, asi_per_codigo, asi_supervisor, asi_estado, asi_tipo,
                                                                                    asi_fecha_creacion, asi_fecha_actulizacion, asi_observacion );
                            Thread.sleep(50);
                            updateBarHandler.post(new Runnable() {
                                public void run() {
                                    barProgressDialog.incrementProgressBy(1);
                                    barProgressDialog.setMessage("Enviando ASISTENTES ...");
                                }
                            });

                            if (actualizoOrden) {
                                managerOrden.updateSincronizado_asistencia(asi_supervisor, asi_per_codigo, asi_fecha_creacion);
                            }

                        } while (cursor.moveToNext());//accessing data upto last row from table
                    }
                    managerOrden.close();
                    banderaEnvio = true;

                    updateBarHandler.post(new Runnable() {
                        public void run() {
                            barProgressDialog.dismiss();
                            actualizarAsisPendientes();
                        }
                    });
                } catch (Exception e) {
                    barProgressDialog.dismiss();
                    banderaEnvio = true;
                }
            }
        }).start();
    }// Cierra launchBarDialog Barra de envio de ordenes

}
