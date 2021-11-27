package com.example.config;

import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.example.gestiondeltec.R;
import com.example.location.LocationManager;
import com.example.location.LocationSyncActivity;

/**
 * Created by jasson on 21/07/17.
 */

public class GestionMedidorEncontrado extends LocationSyncActivity {
    /** Called when the activity is first created. */

    private LinearLayout MedidorPanelLectura, MedidorPanelNoEncontrado;
    private EditText etMedidorRutaAnterior, etMedidorRutaPosterior, etMedidorDireccion;
    private EditText etSerieMedidorEncontrado, etMedidorLectura, etObsAbierta;
    private RadioButton rbMedidorConServicio,rbMedidorSinServicio;
    private Button btMedidorConfirmar;
    private ButtonClicked clicked;
    private Switch switchEncontroMedidor;
    private Spinner spMedidorObs;

    Bundle bolsaR;
    GestionMedidorEncontradoModel manager;

    int ciclo = 0, ruta = 0, rutaAnterior = 0, rutaPosterior = 0, med_persona = 0,
            med_indicador_med = 0, conServicio = -1;
    String dirSugerida = "", med_latitud = "", med_longitud = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_predio_no_codificado);
        etMedidorRutaAnterior = (EditText) findViewById(R.id.etMedidorRutaAnterior);
        etMedidorRutaPosterior = (EditText) findViewById(R.id.etMedidorRutaPosterior);
        etMedidorDireccion = (EditText) findViewById(R.id.etMedidorDireccion);

        etSerieMedidorEncontrado = (EditText) findViewById(R.id.etSerieMedidorEncontrado);
        etMedidorLectura = (EditText) findViewById(R.id.etMedidorLectura);
        etObsAbierta = (EditText) findViewById(R.id.etObsAbierta);

        switchEncontroMedidor = (Switch) findViewById(R.id.switchEncontroMedidor);
        MedidorPanelLectura = (LinearLayout) findViewById(R.id.MedidorPanelLectura);
        MedidorPanelNoEncontrado = (LinearLayout) findViewById(R.id.MedidorPanelNoEncontrado);

        rbMedidorConServicio = (RadioButton) findViewById(R.id.rbMedidorConServicio);
        rbMedidorSinServicio = (RadioButton) findViewById(R.id.rbMedidorSinServicio);
        spMedidorObs = (Spinner)findViewById(R.id.spMedidorObs);

        ArrayAdapter<CharSequence> adapterMedidorObs = ArrayAdapter.createFromResource(this, R.array.med_no_codificados, android.R.layout.simple_spinner_item);
        adapterMedidorObs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMedidorObs.setAdapter(adapterMedidorObs);

        btMedidorConfirmar = (Button) findViewById(R.id.btMedidorConfirmar);

        switchEncontroMedidor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                // TODO Auto-generated method stub
                MedidorPanelLectura.setVisibility(View.GONE);
                MedidorPanelNoEncontrado.setVisibility(View.GONE);
                if (switchEncontroMedidor.isChecked()) {
                    MedidorPanelLectura.setVisibility(View.VISIBLE);
                } else {
                    MedidorPanelNoEncontrado.setVisibility(View.VISIBLE);
                }
            }
        });


        manager = new GestionMedidorEncontradoModel(this);
        clicked = new ButtonClicked();
        bolsaR = getIntent().getExtras();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (bolsaR != null) {

            ruta = bolsaR.getInt("ruta");
            ciclo = bolsaR.getInt("ciclo");
            dirSugerida = bolsaR.getString("direccion");

            rutaAnterior = bolsaR.getInt("contrato_anterior");
            rutaPosterior = bolsaR.getInt("contrato_posterior");
            med_persona = bolsaR.getInt("cuadrilla");
        }

        etMedidorRutaAnterior.setText(String.valueOf(rutaAnterior));
        etMedidorRutaPosterior.setText(String.valueOf(rutaPosterior));

        etMedidorDireccion.setText(dirSugerida);
        actualizarGPS();
    }

    /**
     * Validate and Insert data Medidor
     */
    public void validarMedidor(){

        String med_direccion= "", med_medidor = "", med_lectura = "",
                med_observacion = "", mensaje = "";

        conServicio = -1;

        med_direccion = etMedidorDireccion.getText().toString();

        if(switchEncontroMedidor.isChecked()){
            med_medidor = etSerieMedidorEncontrado.getText().toString();
            med_lectura = etMedidorLectura.getText().toString();
            med_indicador_med = 1;
            if(med_medidor.isEmpty() || med_lectura.isEmpty())
                mensaje = "DEBE INDICAR LA LECTURA O EL MEDIDOR";
        }else {
            med_indicador_med = 0;
            if(rbMedidorConServicio.isChecked())
                conServicio = 1;
            else conServicio = 0;
        }

        med_observacion = spMedidorObs.getSelectedItem().toString()
                + "||" +etObsAbierta.getText().toString();

        if(mensaje=="") {

            manager.open();
            manager.insertar_medidor(ciclo, ruta, rutaAnterior, rutaPosterior, med_direccion,
                    med_medidor, med_indicador_med, med_lectura, conServicio, med_observacion,
                    med_persona, med_latitud, med_longitud);
            manager.close();
            this.finish();
        }else Toast.makeText(this,mensaje,Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();

        btMedidorConfirmar.setOnClickListener(clicked);
    }

    @Override
    public void onLocationChanged(Location location) {
        //Do cool stuff here
        med_latitud = String.valueOf(location.getLatitude());
        med_longitud = String.valueOf(location.getLongitude());

        System.out.println("GPS latitudMED: " + location.getLatitude());
        System.out.println("GPS longitudMED: " + location.getLongitude());
    }

    /*
    Actualiza datos GPS para el reporte del medidor Encontrado
     */
    public void actualizarGPS(){
        LocationManager locationManager = LocationManager.getInstance(this);
        Location location = locationManager.getLastLocation();

        try {
            med_latitud = String.valueOf(location.getLatitude());
            med_longitud = String.valueOf(location.getLongitude());
        } catch (Exception ex) {}
    }

    class ButtonClicked implements View.OnClickListener
    {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btMedidorConfirmar:
                    validarMedidor();
                    break;
            }
        }
    }
}
