package com.example.asistencia;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.example.gestiondeltec.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by DELTEC on 31/07/2015.
 */
public class ItemAdapterAsistencia extends BaseAdapter {

    private Context context;
    private List<ItemAsistencia> items;

    public ItemAdapterAsistencia(Context context, List<ItemAsistencia> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return this.items.size();
    }

    @Override
    public Object getItem(int position) {
        return this.items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;
        final ViewHolder holder;
        final ItemAsistencia item = this.items.get(position);
        //OPTIMIZACION DE MEMORIA CON LOS OBJETOS YA CREADOS
        if (convertView == null) {//INICIALIZO LAS VARIABLES
            // Create a new view into the list.
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.list_item_asistencia, parent, false);

            holder = new ViewHolder();
            holder.tvAsisFecha = (TextView)rowView.findViewById(R.id.tvAsisFecha);
            holder.tvAsisNombre = (TextView)rowView.findViewById(R.id.tvAsisNombre);
            holder.tvAsisCodigo = (TextView)rowView.findViewById(R.id.tvAsisCodigo);
            holder.tvAsisComment = (TextView)rowView.findViewById(R.id.tvAsisComment);
            holder.stAsistencia  = (Switch)rowView.findViewById(R.id.stAsistencia );
            holder.ivAsistencia = (ImageView)rowView.findViewById(R.id.imageViewAsis);
            holder.pAsisMotivo = (LinearLayout)rowView.findViewById(R.id.pAsisMotivo);
            holder.spAsisMotivo = (Spinner)rowView.findViewById(R.id.spAsisMotivo);
            //Verificar consumo de memoria
            EjecucionAsistenciaModel managerAsistencia = new EjecucionAsistenciaModel(context);
            managerAsistencia.open();
            List<String> lables = managerAsistencia.getAllLabels("69", 1);
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item, lables);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            holder.spAsisMotivo.setAdapter(dataAdapter);
            managerAsistencia.close();

            //ArrayAdapter<CharSequence> adapterAsistenciaEstado = ArrayAdapter.createFromResource(context, R.array.asistencia_motivo, android.R.layout.simple_spinner_item);
            //adapterAsistenciaEstado.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //holder.spAsisMotivo.setAdapter(adapterAsistenciaEstado);
            holder.btAsisConfirmar = (Button)rowView.findViewById(R.id.btAsisConfirmar);
            holder.tvAsisObs = (EditText)rowView.findViewById(R.id.etAsisObs);

            rowView.setTag(holder);
        }else{
            holder = (ViewHolder)rowView.getTag();
        }

        int estadoAsistencia = item.getEstado_asistencia();
        String nombreAsistente = item.getNombre_asistente().trim();
        if(nombreAsistente.length() > 22)
            nombreAsistente = nombreAsistente.substring(0, 22);
        
        holder.tvAsisFecha.setText(item.getFecha_asistente());
        holder.tvAsisNombre.setText(nombreAsistente);
        holder.tvAsisCodigo.setText(String.valueOf(item.getCodigoAsistente()));
        holder.tvAsisComment.setText(item.getComentario_asistencia());
        holder.stAsistencia.setText(String.valueOf(item.getEstado_asistencia()));

        switch ( estadoAsistencia ) {
            case 0:
                holder.ivAsistencia.setImageResource(R.drawable.button_cancel);
                holder.stAsistencia.setChecked(false);
                holder.pAsisMotivo.setVisibility(View.GONE);
                holder.tvAsisComment.setText("Falta");
                break;
            case 1:
                holder.ivAsistencia.setImageResource(R.drawable.button_ok);
                holder.stAsistencia.setChecked(true);
                holder.pAsisMotivo.setVisibility(View.GONE);
                holder.tvAsisComment.setText("Asiste");
                break;
            case -2:
                holder.ivAsistencia.setImageResource(R.drawable.editcopy);
                holder.stAsistencia.setChecked(false);
                holder.pAsisMotivo.setVisibility(View.VISIBLE);
                holder.tvAsisComment.setText("Pendiente?");
                break;
            default:
                holder.ivAsistencia.setImageResource(R.drawable.editcopy);
                holder.stAsistencia.setChecked(false);
                holder.pAsisMotivo.setVisibility(View.GONE);
                holder.tvAsisComment.setText("Pendiente");
                break;
        }

        holder.stAsistencia.setOnClickListener(new CompoundButton.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                String horaAsis = getDateTime("HH:mm:ss");
                String fechaAsis = getDateTime("yyyy-MM-dd");
                int perCodigo = item.getCodigoAsistente();
                int supervisorAsi = item.getCodigoSupervisor();
                int estadoAsis = 0, tipoAsis = 0;
                String obsAsis = item.getComentario_asistencia();
                estadoAsis = item.getEstado_asistencia();
                int inicial = 0, fin  = 0;

                if (estadoAsis == 0 || estadoAsis == -1) {
                    holder.ivAsistencia.setImageResource(R.drawable.button_ok);
                    holder.tvAsisComment.setText("Asiste");
                    holder.pAsisMotivo.setVisibility(View.GONE);
                    fin =  -holder.pAsisMotivo.getHeight();
                    holder.stAsistencia.setEnabled(true);
                    estadoAsis = 1;
                } else if (estadoAsis == 1) {
                    holder.ivAsistencia.setImageResource(R.drawable.editcopy);
                    holder.tvAsisComment.setText("Falta");
                    inicial =  -holder.pAsisMotivo.getHeight();
                    holder.pAsisMotivo.setVisibility(View.VISIBLE);
                    estadoAsis = -2;
                    holder.stAsistencia.setEnabled(false);
                }

                holder.animate = new TranslateAnimation(0, 0, inicial, fin);
                holder.animate.setDuration(300);
                holder.animate.setFillBefore(true);
                holder.pAsisMotivo.startAnimation(holder.animate);

                EjecucionAsistenciaModel managerAsistencia = new EjecucionAsistenciaModel(context);
                managerAsistencia.open();
                managerAsistencia.ingresarActualizarAsistencia(horaAsis, perCodigo, supervisorAsi, estadoAsis,
                        tipoAsis, fechaAsis, obsAsis);
                managerAsistencia.close();

                item.setUpdateAsistente(horaAsis, estadoAsis, tipoAsis, obsAsis);
                holder.tvAsisFecha.setText(horaAsis);
                holder.tvAsisObs.setText(obsAsis);

            }
        });

        holder.btAsisConfirmar.setOnClickListener(new CompoundButton.OnClickListener() {
            public void onClick(View v) {

                String strSpMotivoAsis = (String) holder.spAsisMotivo.getSelectedItem();
                String obsAsis = strSpMotivoAsis + ":" + holder.tvAsisObs.getText().toString().trim();

                String horaAsis = getDateTime("HH:mm:ss");
                String fechaAsis = getDateTime("yyyy-MM-dd");
                int perCodigo = item.getCodigoAsistente();
                int supervisorAsi = item.getCodigoSupervisor();
                int estadoAsis = 0, tipoAsis = 0;
                //tipoAsis = getTipoAsis(strSpMotivoAsis);
                System.out.println(" TIPOASIS: " + strSpMotivoAsis + " con codigo " + tipoAsis);

                EjecucionAsistenciaModel managerAsistencia = new EjecucionAsistenciaModel(context);
                managerAsistencia.open();
                tipoAsis = managerAsistencia.getCodigoObs(strSpMotivoAsis,"69");
                managerAsistencia.ingresarActualizarAsistencia(horaAsis, perCodigo, supervisorAsi, estadoAsis,
                        tipoAsis, fechaAsis, obsAsis);
                managerAsistencia.close();
                item.setUpdateAsistente(horaAsis, estadoAsis, tipoAsis, obsAsis);

                holder.pAsisMotivo.setVisibility(View.GONE);
                holder.animate = new TranslateAnimation(0, 0, 0, -holder.pAsisMotivo.getHeight());
                holder.animate.setDuration(300);
                holder.animate.setFillBefore(true);
                holder.pAsisMotivo.startAnimation(holder.animate);

                holder.ivAsistencia.setImageResource(R.drawable.button_cancel);
                holder.stAsistencia.setEnabled(true);
            }
            });

        return rowView;
    }

    private String getDateTime(String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                format, Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    } // Cierre obtener Hora

    private int getTipoAsis(String tipoAsistencia){
        int tipoAsis = 0;

        if(tipoAsistencia.contains("FALTA"))
            tipoAsis = 0;
        else if(tipoAsistencia.contains("CAMBIO"))
            tipoAsis = 1;
        else if(tipoAsistencia.contains("INCAPACIDAD"))
            tipoAsis = 2;
        else if(tipoAsistencia.contains("VACACIONES"))
            tipoAsis = 3;
        else if(tipoAsistencia.contains("RETIRADO"))
            tipoAsis = 4;
        else if(tipoAsistencia.contains("TARDE"))
            tipoAsis = 5;
        else if(tipoAsistencia.contains("CAMINO"))
            tipoAsis = 6;
        else if(tipoAsistencia.contains("OTRO"))
            tipoAsis = 7;

        return tipoAsis;
    }

    static class ViewHolder {
        TextView tvAsisFecha;
        TextView tvAsisNombre;
        TextView tvAsisCodigo;
        TextView tvAsisComment;
        Switch stAsistencia;
        ImageView ivAsistencia;
        LinearLayout pAsisMotivo;
        Spinner spAsisMotivo;
        TranslateAnimation animate;
        Button btAsisConfirmar;
        EditText tvAsisObs;
    }

}