package com.example.verorden;

import com.example.gestiondeltec.R;
import java.util.ArrayList;
import java.util.List;
import com.example.lectura.VerLecturaModel;
import com.example.location.LocationManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

import androidx.fragment.app.Fragment;

public class VerLecturaFragment extends Fragment implements OnClickListener {

    LinearLayout layoutPanelPrincipal, layoutLectura, layoutMedidorEncontrado;
    CheckBox chkMotivo;
    Spinner spMotivoLectura, spObsLectura, spUnidadMedida;
    TextView tvLecturaTipoOrden,tvLecturaDireccion, tvLecturaDireccionAux, tvTituloLectura, tvLectFranja, tvLectConsumo, tvLectCliente, tvLectRutaConsecutivo;
    EditText etLectura, etObsLectura,etSerieMedidorEncontrado,etMarcaMedidorEncontrado,etDiametroMedidorEncontrado;
    Button btConfirmarLectura;
    ImageView foto;
    Switch stEncontroMedidor;

    int ose_codigo = 0, cuadrilla = 0, tipoFoto = 0, ose_precarga = 0, cli_contrato = 0, consecutivo = 0, ruta = 0;
    String tipoProducto = "", medidor = "", direccion = "", direccion2 = "", consumo = "", cli_nombre = "";
    String theBtMacAddress = "";
    private VerLecturaModel manager;


    int lectura_anterior, lectura_actual, consumo_promedio, estado, intentos, intentos2 = 2,
            indicador_lectura, causa, observacion;
    String urlFoto = "", strCausa = "";
    int cantidadDigitos = 0, tipoUsuario = 1, tipoOrden = 1;

    int fotosTomadas = 0, limiteFotos = 1;
    Boolean debeTomarFotosAdd = false;
    List<String> lablesObs = null, labelsObsImpedimento= null;
    ArrayAdapter<String> dataAdapterObsLect = null, dataAdapterObsLectImpe = null;

    public static Bundle bolsaDatosIniciales;

    private FragmentIterationListenerVerLectura mCallback = null;

    public static VerLecturaFragment newInstance(Bundle arguments){
        VerLecturaFragment f = new VerLecturaFragment();
        if(arguments != null) {
            f.setOseCodigo(arguments.getInt("ose_codigo"));
            String medidorTmp = arguments.getString("elemento");
            if(medidorTmp != null){
                if(medidorTmp.length() > 1)
                    f.setMedidor(medidorTmp);
            }

            //bolsaDatosIniciales = arguments;
            f.setArguments(arguments);
        }
        //bolsaDatosIniciales = arguments;
        return f;
    }

    /**
     * INTERFAZ PARA INTERACTUAR CON EL ACTIVITY
     * @author DELTEC
     *
     */
    public interface FragmentIterationListenerVerLectura{
        public void onFragmentIterationVerLectura(Bundle parameters);
    }

    public void setOseCodigo(int in_ose_codigo){
        ose_codigo = in_ose_codigo;
    }

    public void setMedidor(String in_medidor){
        medidor = in_medidor;
    }

    //El fragment se ha adjuntado al Activity
    @Override
    public void onAttach(android.app.Activity activity) {
        super.onAttach(activity);
        mCallback = (FragmentIterationListenerVerLectura) activity;
    }

    //El Activity que contiene el Fragment ha terminado su creaci?n
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            bolsaDatosIniciales = savedInstanceState.getBundle("bolsaDatosIniciales");
            ose_codigo = savedInstanceState.getInt("ose_codigo");
            tipoUsuario = savedInstanceState.getInt("tipoUsuario");
            medidor = savedInstanceState.getString("medidor");
        }
    }

    //El Fragment ha sido quitado de su Activity y ya no est? disponible
    @Override
    public void onDetach() {
        mCallback = null;
        super.onDetach();
    }

    //La vista ha sido creada y cualquier configuraci?n guardada est? cargada
    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tab_orden_tomar_lectura, container, false);

        tvTituloLectura = (TextView)view.findViewById(R.id.tvTituloTomarLectura);
        layoutPanelPrincipal = (LinearLayout)view.findViewById(R.id.LecturaPanelPrincipal);
        layoutLectura = (LinearLayout)view.findViewById(R.id.LecturaPanelLectura);
        layoutMedidorEncontrado = (LinearLayout)view.findViewById(R.id.LecturaPanelMedidorEncontrado);
        tvLecturaTipoOrden = (TextView)view.findViewById(R.id.tvLecturaTipoOrden);
        tvLecturaDireccion = (TextView)view.findViewById(R.id.tvLecturaDireccionOrden);
        tvLecturaDireccionAux = (TextView)view.findViewById(R.id.tvLecturaDireccionAux);
        chkMotivo = (CheckBox)view.findViewById(R.id.checkBoxReconexionNoAutorizada);
        btConfirmarLectura = (Button)view.findViewById(R.id.btTomarLectura);
        etLectura = (EditText)view.findViewById(R.id.etLecturaMedidor);
        etObsLectura = (EditText)view.findViewById(R.id.etObservacionLectura);
        etSerieMedidorEncontrado = (EditText)view.findViewById(R.id.etSerieMedidorEncontrado);
        etMarcaMedidorEncontrado = (EditText)view.findViewById(R.id.etMarcaMedidorEncontrado);
        etDiametroMedidorEncontrado = (EditText)view.findViewById(R.id.etDiametroMedidorEncontrado);
        tvLectFranja = (TextView)view.findViewById(R.id.tvLectFranja);
        tvLectConsumo = (TextView)view.findViewById(R.id.tvLectConsumo);
        tvLectCliente = (TextView)view.findViewById(R.id.tvLectCliente);
        foto = (ImageView)view.findViewById(R.id.imageViewFoto);
        spMotivoLectura = (Spinner)view.findViewById(R.id.spinnerMotivoNoLectura);
        spObsLectura = (Spinner)view.findViewById(R.id.spObervacionLectura);
        stEncontroMedidor = (Switch)view.findViewById(R.id.stEncontroMedidor);
        spUnidadMedida = (Spinner)view.findViewById(R.id.spUnidadMedidor);
        tvLectRutaConsecutivo = (TextView)view.findViewById(R.id.tvLectRutaConsecutivo);

        manager = new VerLecturaModel(getActivity());
        bolsaDatosIniciales = getArguments();

        if(bolsaDatosIniciales != null) {

            ose_codigo = bolsaDatosIniciales.getInt("ose_codigo");
            cuadrilla = bolsaDatosIniciales.getInt("cuadrilla");
            tipoProducto = bolsaDatosIniciales.getString("producto");
            ose_precarga = bolsaDatosIniciales.getInt("ose_precarga");
            medidor = bolsaDatosIniciales.getString("elemento");
            direccion = bolsaDatosIniciales.getString("direccion");
            cli_contrato = bolsaDatosIniciales.getInt("cli_contrato");
            ruta = bolsaDatosIniciales.getInt("ruta");
            consecutivo = bolsaDatosIniciales.getInt("consecutivo");
            tipoUsuario = bolsaDatosIniciales.getInt("tipo");
            tipoOrden = bolsaDatosIniciales.getInt("tipo_orden");
            consumo = bolsaDatosIniciales.getString("consumo");
            theBtMacAddress = bolsaDatosIniciales.getString("theBtMacAddress");
            cli_nombre = bolsaDatosIniciales.getString("cli_nombre");

            tvTituloLectura.setText(medidor);
            tvLectConsumo.setText(String.valueOf(tipoProducto));
            tvLecturaTipoOrden.setText(String.valueOf(cli_contrato));
            tvLecturaDireccion.setText(direccion);
            stEncontroMedidor.setText("Encontro Medidor " + medidor  + "?");
            tvLectFranja.setText(consumo);
            tvLectCliente.setText(cli_nombre);
            tvLecturaDireccionAux.setVisibility(View.GONE);
            tvLectRutaConsecutivo.setText(String.valueOf(ruta) + "     " + String.valueOf(consecutivo));

            btConfirmarLectura.setBackgroundResource(android.R.drawable.btn_default);

            cargarAdaptadores();
            cargarDatos();
            cargarListenner();

            etLectura.requestFocus();
        }

        return view;
    }

    /*
    @Override
    public void onResume() {
        super.onResume();
    }*/


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle("bolsaDatosIniciales", bolsaDatosIniciales);
        outState.putInt("ose_codigo", ose_codigo);
        outState.putInt("tipoUsuario", tipoUsuario);
        outState.putString("medidor", medidor);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch(v.getId()){
            case R.id.btTomarLectura:
                validarLectura();
                break;
        }
    }

    /**
     * Return text read validate
     */
    private void validarLectura(){
        final String lecturaStr = etLectura.getText().toString();
        final String motivoNoLecturaStr = (String) spMotivoLectura.getSelectedItem();
        final String obsLectura = (String) spObsLectura.getSelectedItem();
        final String obsLecturaAbierta = (String) etObsLectura.getText().toString();

        String msjMostrado = "",strMensaje = "",  strTitulo = "", mensaje_critica = "";
        String serieMedidor = "", marcaMedidor = "", diametroMedidor = "", unidad_medidor = "", datos_medidor = "";
        int consumo = 0;

        if(!stEncontroMedidor.isChecked()){
            serieMedidor = etSerieMedidorEncontrado.getText().toString();
            marcaMedidor = etMarcaMedidorEncontrado.getText().toString();
            diametroMedidor = etDiametroMedidorEncontrado.getText().toString();
            unidad_medidor = (String) spUnidadMedida.getSelectedItem();
            if(serieMedidor.length() == 0)
                msjMostrado += "DEBE INGRESAR LA SERIE DEL MEDIDOR";
        }

        if(chkMotivo.isChecked()){
            strMensaje = motivoNoLecturaStr;
            strTitulo = medidor + " " + tipoProducto;
            lectura_actual = -1;

            if((motivoNoLecturaStr.contains("36") || motivoNoLecturaStr.contains("58")) && obsLectura.length() > 6){
                // 36 - NO EXISTE DIRECCION O 58 - DEMOLICION
                msjMostrado += " NO PUEDE AGREGAR OBSERVACIONES CON " + motivoNoLecturaStr;
            }else if(motivoNoLecturaStr.contains("37") && obsLectura.length() < 6){
                // 37 - IMPEDIMENTO SIN OBS LECTURA
                msjMostrado += " DEBE SELECCIONAR UNA OBSERVACION DE LECTURA ";
            }else if(motivoNoLecturaStr.contains("13") && obsLecturaAbierta.length() < 3){
                // 13 MEDIDOR CAMBIADO SIN REGISTRAR EL MEDIDOR EN LA OBS
                msjMostrado += " DEBE INDICAR LA SERIE DEL MEDIDOR ENCONTRADO ";
            }else if(motivoNoLecturaStr.contains("71") && obsLecturaAbierta.length() < 3){
                // 71 1 - APARATO DE MEDICION NO INSTALADO
                msjMostrado += " DEBE ESCRIBIR UNA OBSERVACION DEL MEDIDOR NO INSTALADO ";
            }else if(motivoNoLecturaStr.contains("45") && obsLecturaAbierta.length() < 3){
                // 45 - DIRECCION ERRADA
                msjMostrado += " DEBE ESCRIBIR LA DIRECCION DEL PREDIO ";
            }else if(motivoNoLecturaStr.contains("71") && !tipoProducto.contains("ENERGIA")){
                // 71 MACRO NO INSTALADO EN TERRENO
                msjMostrado += " NO PUEDE AGREGAR CAUSA 71 CON AGUA ";
            }
        }else if(!lecturaStr.isEmpty()){
            lectura_actual = Integer.parseInt(lecturaStr);
            consumo = lectura_actual - lectura_anterior;
            double criticaBajo = 0.20, criticaAlto = 0.20; //CONSUMO mAYOR o IGUAL a 40

            if(tipoProducto.contains("ENERGIA")){
                criticaAlto = 0.60;
            }else if (consumo_promedio < 40){
                criticaBajo = 0.60;
                criticaAlto = 0.60;
            }

            if (consumo < (consumo_promedio * (1 - criticaBajo)))  {
                if (lectura_actual < lectura_anterior)
                {
                    mensaje_critica += "CONSUMO NEGATIVO";
                }
                else mensaje_critica += " -BAJO CONSUMO";
            }
            else if (consumo > (consumo_promedio * (criticaAlto + 1)))  {
                mensaje_critica = " -ALTO CONSUMO";
            }else
                mensaje_critica = "CONSUMO NORMAL";

            if(consumo == 1){
                strTitulo = "VERIFIQUE DIGITO CAMBIANTE";
                strMensaje = "Consumo 1 URGENTE ****<<VERIFIQUE>>**** DIGITO CAMBIANTE, por favor VERIFIQUE DIGITO CAMBIANTE";
            }
        }else msjMostrado = " Debe ingresar la LECTURA";

        if(intentos <= 0)
            msjMostrado = "Se agotaron los cambios en la LECTURA";

        /*
        if(obsLectura != null){

            if(obsLectura.contains("74") && tipoProducto.contains("ENERGIA"))
                msjMostrado += " No puede agregar esta observacion con ENERGIA";

            if (obsLectura.contains("70") && obsLectura.contains("59"))
            {
                if (consultaOrdenPreviaDesocupadoParado(obsLectura))
                    msjMostrado += " No puede Agregar la observacion " + obsLectura;
            }
            else if (obsLectura.contains("45") && obsLecturaAbierta == "")
            { // 774 45-DIRECCION ERRADA
                msjMostrado += " DEBE INDICAR LA DIRECCION DEL PREDIO ";
            }
        }
        */

        intentos2--;

        if(lectura_actual == lectura_anterior ){
            //LECTURAS IGUALES  ||  //CAUSA && 758 CAMBIAR APARATO MEDICION O 764 DEMOLICION
            mensaje_critica = "LECTURAS IGUALES";

            showEqualRead(lecturaStr, obsLectura, motivoNoLecturaStr, obsLecturaAbierta);

            /*
            if(msjMostrado == "") {
                showEqualRead(lecturaStr, obsLectura, motivoNoLecturaStr, obsLecturaAbierta);
            }else Toast.makeText(getActivity(), msjMostrado, Toast.LENGTH_SHORT).show();
            */
        } else if(msjMostrado == ""){

            strMensaje = "CONFIRMA OJO PRESENTA UN " + mensaje_critica ;
            strTitulo = medidor + " LEC:" + lecturaStr;// + " " + mensaje_critica; // "LECTURA " + medidor;

            if(mensaje_critica == "CONSUMO NORMAL"){//CONSUMO NORMAL NO MUESTRO MENSAJE
                confirmarLecturaMotivo(lecturaStr, obsLectura, motivoNoLecturaStr,obsLecturaAbierta);
            }else if(intentos2 >= 0){
                AlertDialog.Builder invalid_input_dialog = new AlertDialog.Builder(getActivity());
                final String finalMensaje_critica = mensaje_critica;
                invalid_input_dialog.setTitle(strTitulo)
                        .setMessage(strMensaje)
                        .setCancelable(true)
                        .setPositiveButton("CONFIRMAR", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if(finalMensaje_critica.contains("NEGATIVO")){
                                    confirmarLecturaMotivo(lecturaStr, "K - ANALISIS ESPECIAL",
                                            motivoNoLecturaStr,obsLecturaAbierta);
                                }else confirmarLecturaMotivo(lecturaStr, obsLectura,
                                        motivoNoLecturaStr,obsLecturaAbierta);
                            }
                        })
                        .setNegativeButton("CANCELAR", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                            }
                        })
                        .show();
            }else confirmarLecturaMotivo(lecturaStr, obsLectura, motivoNoLecturaStr,obsLecturaAbierta);
        }else Toast.makeText(getActivity(), msjMostrado, Toast.LENGTH_SHORT).show();
    }

    private void showEqualRead(String lecturaStr,String obsLectura, String motivoNoLecturaStr, String obsLecturaAbierta){

        LayoutInflater inflater = this.getLayoutInflater(null);
        View layout = inflater.inflate(R.layout.dialog_lecturas_iguales, null);
        String tituloStr = "LECTURAS IGUALES";

        TextView tvTituloLecIguales = (TextView)layout.findViewById(R.id.tvTituloLecIguales);
        TextView tvTituloLectLectura = (TextView)layout.findViewById(R.id.tvTituloLectLectura);
        final TextView tvTituloObsLectura = (TextView)layout.findViewById(R.id.tvTituloObsLectura);
        final Spinner spLectIguales1 = (Spinner)layout.findViewById(R.id.spLectIguales1);
        final LinearLayout lyMotivoLectIguales = (LinearLayout)layout.findViewById(R.id.lyMotivoLectIguales);
        final Spinner spLectIguales2 = (Spinner)layout.findViewById(R.id.spLectIguales2);
        final EditText etObsLecturaIguales = (EditText)layout.findViewById(R.id.etObsLecturaIguales);

        uploadSpinnerEqualRead(spLectIguales1,spLectIguales2);
        lyMotivoLectIguales.setVisibility(View.GONE);

        spLectIguales1.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub
                String obsLecturaIguales = (String) spLectIguales1.getSelectedItem();
                if(obsLecturaIguales.contains("DANADO") || obsLecturaIguales.contains("DEMOLIDO") ){
                    lyMotivoLectIguales.setVisibility(View.VISIBLE);
                }else lyMotivoLectIguales.setVisibility(View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

        spLectIguales2.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub
                String obsLecturaIguales = (String) spLectIguales2.getSelectedItem();
                if(obsLecturaIguales.contains("CON APARATO DESCONECTADO") ){
                    tvTituloObsLectura.setText("INGRESE LA ***<< LECTURA >>***");
                    tvTituloObsLectura.requestFocus();
                }else {
                    tvTituloObsLectura.setText("Observacion Lectura Abierta");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

        tvTituloLectLectura.setText(lecturaStr);
        if(chkMotivo.isChecked()){
            tvTituloLecIguales.setText(motivoNoLecturaStr);
            tituloStr = motivoNoLecturaStr;
            lyMotivoLectIguales.setVisibility(View.VISIBLE);
        }

        if(obsLecturaAbierta.length() >= 1){
            etObsLecturaIguales.setText(obsLecturaAbierta, TextView.BufferType.EDITABLE);
        }

        AlertDialog MyDialog;
        AlertDialog.Builder MyBuilder = new AlertDialog.Builder(getActivity());
        MyBuilder.setTitle(tituloStr);
        MyBuilder.setView(layout);
        MyBuilder.setCancelable(true);
        MyBuilder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String strLectIguales1 = (String) spLectIguales1.getSelectedItem();
                String strLectIguales2 = (String) spLectIguales2.getSelectedItem();
                String strObsAbierta = etObsLecturaIguales.getEditableText().toString();

                if(strLectIguales1 == "DEMOLIDO" && strLectIguales2 == "CON APARATO CONECTADO"){
                    cambiarLecturaCausa("DEMOLIDO CON APARATO CONECTADO");
                    if (!debeTomarFotosAdd){
                        fotosTomadas = 0;
                        limiteFotos = 2;
                    }
                    debeTomarFotosAdd = true;

                }else confirmEqualRead(strLectIguales1, strLectIguales2, strObsAbierta);
            }
        });
        MyBuilder.setNegativeButton("Cancelar",  new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        MyDialog = MyBuilder.create();
        MyDialog.show();
    }

    private void cambiarLecturaCausa(String strObsAbierta){
        if(chkMotivo.isChecked()){
            chkMotivo.setChecked(false);
        }
        etObsLectura.setText(strObsAbierta);
        etLectura.requestFocus();
        Toast.makeText(getActivity(), "INGRESE LA LECTURA DEL MEDIDOR", Toast.LENGTH_LONG).show();
    }

    /**
     *
     * @param strLectIguales1
     * @param strLectIguales2
     * @param strObsAbierta
     */
    private void confirmEqualRead(String strLectIguales1, String strLectIguales2, String strObsAbierta){

        String lecturaStr = etLectura.getText().toString();
        String motivoNoLecturaStr = (String) spMotivoLectura.getSelectedItem();
        String obsLectura = (String) spObsLectura.getSelectedItem();


        if(strLectIguales1 == "C - PREDIO DESOCUPADO"){//806 70-predio desocupado
            if(strObsAbierta.length() < 3)
                strObsAbierta = "DESOCUPADO LECT:" + lecturaStr;
            obsLectura = "C - PREDIO DESOCUPADO";

        }else if(strLectIguales1 == "Y - PREDIO SUSPENDIDO"){
            if(strObsAbierta.length() < 3)
                strObsAbierta = "SUSPENDIDO LECT:" + lecturaStr;
            obsLectura = "Y - PREDIO SUSPENDIDO";

        }else if(strLectIguales1 == "Q - MEDIDOR FRENADO"){//"769"; //MEDIDOR CON VIDRIO AVERIADO QUITO LECTURA PONGO CAUSA 16
            if(strObsAbierta.length() < 3)
                strObsAbierta = "FRENADO LECT:" + lecturaStr;
            obsLectura = "Q - MEDIDOR FRENADO";
        }

        if (!debeTomarFotosAdd){
            fotosTomadas = 0;
            limiteFotos = 2;
        }
        debeTomarFotosAdd = true;
        confirmarLecturaMotivo(lecturaStr, obsLectura, motivoNoLecturaStr, strObsAbierta);
    }

    private void uploadSpinnerEqualRead(Spinner sp1, Spinner sp2){

        String motivoNoLecturaStr = (String) spMotivoLectura.getSelectedItem();
        List<String> labelsp1 = new ArrayList<String>();
        List<String> labelsp2 = new ArrayList<String>();
        labelsp1.add("C - PREDIO DESOCUPADO");
        labelsp1.add("Y - PREDIO SUSPENDIDO");
        labelsp1.add("Q - MEDIDOR FRENADO");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, labelsp1);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp1.setAdapter(dataAdapter);

        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, labelsp2);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp2.setAdapter(dataAdapter2);
    }

    /**
     * Ingresa  la Lectura o el Motivo de no lectura a la BD
     * @param lecturaStr
     * @param obsLectura
     * @param motivoNoLecturaStr
     * @param obsLecturaAbierta
     */
    private void confirmarLecturaMotivo(String lecturaStr, String obsLectura, String motivoNoLecturaStr, String obsLecturaAbierta ){

        int motivoNoLectura = 0, codigoObsLectura = 0, codigoObsLecturaFens=0, consumo = 0;
        int encontroMedidor = 0, medidorCorrecto = 1, critica = 0, inDesviacion = 0;
        double desviacion = 0;
        String lectura = "";
        String strLongitud = "";
        String strLatitud = "";
        String serieMedidor = "", marcaMedidor = "", diametroMedidor = "", unidad_medidor = "", datos_medidor = "";
        String estado_fens= "";
        int consumo_franja=0;

        LocationManager locationManager = LocationManager.getInstance(getActivity());
        Location location = locationManager.getLastLocation();

        Boolean debeTomarFoto = false;
        //CAMBIO RURALES
        debeTomarFoto = true;

        btConfirmarLectura.setTextColor(Color.BLUE);

        try{
            strLongitud = String.valueOf(location.getLongitude());
            strLatitud = String.valueOf(location.getLatitude());
        }catch(Exception ex){}

        manager.open();

        if(chkMotivo.isChecked()){
            motivoNoLectura = manager.getCodigoObs(motivoNoLecturaStr, "60");
            indicador_lectura = 0;
            causa = motivoNoLectura;
            strCausa = motivoNoLecturaStr;
            debeTomarFoto = true;

        }else if(!lecturaStr.isEmpty()){
            lectura_actual = Integer.parseInt(lecturaStr);
            lectura = String.valueOf(lectura_actual);
            indicador_lectura = 1;
            consumo = lectura_actual - lectura_anterior;
            double criticaBajo = 0.20, criticaAlto = 0.20; //CONSUMO mAYOR o IGUAL a 40

            if(tipoProducto.contains("ENERGIA")){
                criticaAlto = 0.60;
            }else if (consumo_promedio < 40){
                criticaBajo = 0.60;
                criticaAlto = 0.60;
            }

            if (consumo < (consumo_promedio * (1 - criticaBajo)))  {
                critica = 1;
                debeTomarFoto = true;
            }
            else if (consumo > (consumo_promedio * (criticaAlto + 1)))  {
                critica = 3;
                debeTomarFoto = true;
            }
            else  {// 2. CONSUMO NORMAL
                critica = 2;
                if(tipoProducto.contains("ENERGIA")){
                    if(consumo >= 500)
                        debeTomarFoto = true;
                }else if(consumo >= 50) //**  AGUA  **//
                    debeTomarFoto = true;

                if(intentos < 3) // FOTO A LAS MODIFICADAS
                    debeTomarFoto = true;
            }
            desviacion = calcularDesviacion(consumo, consumo_promedio);

            try{
                inDesviacion = (int)desviacion;
            }catch(Exception ex){}
        }

        if(!stEncontroMedidor.isChecked()){
            serieMedidor = etSerieMedidorEncontrado.getText().toString();
            marcaMedidor = etMarcaMedidorEncontrado.getText().toString();
            diametroMedidor = etDiametroMedidorEncontrado.getText().toString();
            unidad_medidor = (String) spUnidadMedida.getSelectedItem();
            datos_medidor = "MEDIDOR:"+ serieMedidor + "_MARCA:" + marcaMedidor + "_DIAMETRO:" + diametroMedidor + "_UNIDAD:" + unidad_medidor;
            medidorCorrecto = 0;
        }

        codigoObsLectura =  manager.getCodigoObs(obsLectura, "61");
        if(!obsLecturaAbierta.isEmpty()) {
            if (obsLecturaAbierta.length() > 0)
                obsLecturaAbierta = removerTildes(obsLecturaAbierta.toUpperCase());
        }
        codigoObsLecturaFens =  manager.getCodigoObs(obsLectura, "61");
        if(!obsLecturaAbierta.isEmpty()) {
            if (obsLecturaAbierta.length() > 0)
                obsLecturaAbierta = removerTildes(obsLecturaAbierta.toUpperCase());
        }

        intentos--;
        manager.ingresarActualizarLectura(String.valueOf(ose_codigo), lectura, codigoObsLectura, codigoObsLecturaFens,
                motivoNoLectura, indicador_lectura, critica, intentos, encontroMedidor,
                medidorCorrecto, datos_medidor, strLongitud, strLatitud, consumo,
                String.valueOf(desviacion), motivoNoLecturaStr, obsLectura, estado_fens, consumo_franja);

        manager.finalizarOrdenSinActividad(String.valueOf(ose_codigo), obsLecturaAbierta,
                "", 0, "", "", desviacion);

        if(tipoOrden == 2){
            manager.finalizarOrdenRevision(String.valueOf(ose_codigo));
            debeTomarFoto = true;
        }

        if(codigoObsLectura > 0)
            debeTomarFoto = true;

        tipoFoto = 1;

        if(motivoNoLecturaStr.contains("71")){
            if (!debeTomarFotosAdd)
            {
                fotosTomadas = 0;
                limiteFotos = 3;
            }
            debeTomarFotosAdd = true;
        }
        //captureImage(); /////DEBO LLAMAR AL APDRE PARA QUE HABILITE LA CAMARA
        Toast.makeText(getActivity(), "MEDIDOR: " + medidor + " LECTURA:" + lectura, Toast.LENGTH_LONG).show();

        btConfirmarLectura.setTextColor(Color.BLUE);
        Bundle bundle = new Bundle();
        if(tipoUsuario == 1 && manager.existJobPreExecute(ose_codigo, cli_contrato)){
            bundle.putInt("call", 0);//IMPRIMIR
            bundle.putInt("cli_contrato", cli_contrato);
            enviarDatosActivity(bundle);
        }
        manager.close();

        //TOMAR FOTOS
        bundle.clear();
        bundle.putInt("call", 1);//FOTO
        bundle.putInt("ose_codigo", ose_codigo);
        bundle.putInt("indicador_lectura", indicador_lectura);
        bundle.putInt("lectura_actual", lectura_actual);
        bundle.putInt("lectura_anterior", lectura_anterior);
        bundle.putString("motivoNoLecturaStr", motivoNoLecturaStr);
        bundle.putInt("critica", critica);
        bundle.putInt("cli_contrato", cli_contrato);
        bundle.putInt("tipo_foto", 1);
        bundle.putInt("fotosTomadas", fotosTomadas);
        bundle.putInt("limiteFotos", limiteFotos);
        bundle.putBoolean("debeTomarFotosAdd", debeTomarFotosAdd);
        bundle.putString("medidor",medidor);


        enviarDatosActivity(bundle);
    }

    public double calcularDesviacion(int consumo, int promedio)
    {
        double desviacion = 0;
        if (consumo == 0)
        {
            if (promedio == 0)
            {
                desviacion = 0;
            }
            else
            {
                desviacion = -9999;
            }
        }
        else
        {
            if (promedio == 0)
            {
                if (consumo > 80)
                {
                    desviacion = 20;
                }
                else if (consumo > 40)
                {
                    desviacion = 12;
                }
                else if (consumo > 20)
                {
                    desviacion = 8;
                }
                else if (consumo > 10)
                {
                    desviacion = 6;
                }
                else if (consumo > 5)
                {
                    desviacion = 4;
                }
                else desviacion = 2;
            }
            else
            {
                double d_consumo = 0, d_promedio = 0;
                d_consumo = Double.parseDouble(String.valueOf(consumo)) ;
                d_promedio = Double.parseDouble(String.valueOf(promedio)) ;
                desviacion = (d_consumo - d_promedio) / d_promedio;
            }
        }
        return desviacion;
    }


    /**
     * Funci?n que elimina acentos y caracteres especiales de
     * una cadena de texto.
     * @param input
     * @return cadena de texto limpia de acentos y caracteres especiales.
     */
    public static String removerTildes(String input) {

        // Cadena de caracteres original a sustituir.
        String original = "??????????????????????????????????";
        // Cadena de caracteres ASCII que reemplazar?n los originales.
        String ascii    = "aaaeeeiiiooouuunAAAEEEIIIOOOUUUNcC";
        String output = input;
        for (int i=0; i<original.length(); i++) {
            // Reemplazamos los caracteres especiales.
            output = output.replace(original.charAt(i), ascii.charAt(i));
        }//for i

        output = output.replace(';','.');
        output = output.replace('"',' ');
        output = output.replace("'"," ");

        return output;
    }//remove1

    public void enviarDatosActivity(Bundle bundle){

        mCallback.onFragmentIterationVerLectura(bundle);
    }

    /**
     * Retorna true o false si la obs anterior cuadra
     * @param inObservacionActual
     * @return
     */
    public Boolean consultaOrdenPreviaDesocupadoParado(String inObservacionActual)
    {
        Boolean salida = false;
        int observacionActual = 0;
        manager.open();
        observacionActual = manager.getCodigoObs(inObservacionActual, "61");
        salida = manager.consultaOrdenPreviaDesocupadoParado(ose_codigo, cli_contrato, observacionActual);
        manager.close();

        return salida;
    }

    private void cargarAdaptadores(){
        manager.open();
        // Cargar motivos no lectura cod 60
        List<String> lables = manager.getAllLabels(1);
        lablesObs = manager.getAllLabels(1);
        labelsObsImpedimento = manager.getAllLabels(1);

        manager.close();

        ArrayAdapter <CharSequence>  adapterUnidadMedida = ArrayAdapter.createFromResource(getActivity(), R.array.unidad_medida, android.R.layout.simple_spinner_item);
        adapterUnidadMedida.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spUnidadMedida.setAdapter(adapterUnidadMedida);

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, lables);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMotivoLectura.setAdapter(dataAdapter);

        // Creating adapter for spinner
        dataAdapterObsLect = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, lablesObs);
        dataAdapterObsLect.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spObsLectura.setAdapter(dataAdapterObsLect);

        dataAdapterObsLectImpe = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, labelsObsImpedimento);
        dataAdapterObsLectImpe.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    }

    private void cargarDatos(){

        manager.open();

        Cursor datosLectura = manager.cargarDatosLectura(String.valueOf(ose_codigo));
        String strCantidadDigitos = "9999";

        if(datosLectura.moveToFirst()){
            lectura_anterior = datosLectura.getInt(datosLectura.getColumnIndex("lectura_anterior"));
            consumo_promedio = datosLectura.getInt(datosLectura.getColumnIndex("consumo_promedio"));
            estado = datosLectura.getInt(datosLectura.getColumnIndex("estado"));
            direccion2 = datosLectura.getString(datosLectura.getColumnIndex("direccion2"));

            try{
                cantidadDigitos = datosLectura.getInt(datosLectura.getColumnIndex("cantidad_digitos"));
                strCantidadDigitos = String.valueOf(cantidadDigitos);
                if(cantidadDigitos > 0)
                    etLectura.setFilters(new InputFilter[] {new InputFilter.LengthFilter((strCantidadDigitos.length() + 1))});

            }catch(Exception ex){}


            if(!datosLectura.isNull(datosLectura.getColumnIndex("indicador_lectura"))){
                intentos = datosLectura.getInt(datosLectura.getColumnIndex("intentos"));
                indicador_lectura = datosLectura.getInt(datosLectura.getColumnIndex("indicador_lectura"));
                lectura_actual = datosLectura.getInt(datosLectura.getColumnIndex("lectura"));
                causa = datosLectura.getInt(datosLectura.getColumnIndex("codigo_observacion_no_lectura"));
                observacion = datosLectura.getInt(datosLectura.getColumnIndex("codigo_observacion_lectura"));
                btConfirmarLectura.setText("<<< MODIFICAR LECTURA >>>");
                btConfirmarLectura.setTextColor(Color.RED);
            }else{
                intentos = 3;//DISPONIBLES TODOS LOS INTENTOS
                indicador_lectura = 3;//SIN REGISTRO
            }
            urlFoto = manager.cargarUltimaFoto(String.valueOf(ose_codigo));
            if(urlFoto != ""){
                Uri imgUri=Uri.parse(urlFoto);
                foto.setImageURI(imgUri);
            }
        }
        datosLectura.close();
        manager.close();
    }

    private void cargarListenner(){

        btConfirmarLectura.setOnClickListener(this);

        chkMotivo.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                // TODO Auto-generated method stub
                if(chkMotivo.isChecked()){
                    spMotivoLectura.setVisibility(View.VISIBLE);
                    layoutLectura.setVisibility(View.VISIBLE);
                    etLectura.setVisibility(View.GONE);
                    spObsLectura.setVisibility(View.GONE);
                }else{
                    spMotivoLectura.setVisibility(View.GONE);
                    etLectura.setVisibility(View.VISIBLE);
                    spObsLectura.setVisibility(View.VISIBLE);
                }
            }
        });

        stEncontroMedidor.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                // TODO Auto-generated method stub
                if (stEncontroMedidor.isChecked()) {
                    layoutMedidorEncontrado.setVisibility(View.GONE);
                } else {
                    layoutMedidorEncontrado.setVisibility(View.VISIBLE);
                }
            }
        });

        spMotivoLectura.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                String motivoStr = (String) spMotivoLectura.getSelectedItem();
                if (motivoStr.contains("37")) {
                    spObsLectura.setAdapter(dataAdapterObsLectImpe);
                } else {
                    spObsLectura.setAdapter(dataAdapterObsLect);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });
    }
}
