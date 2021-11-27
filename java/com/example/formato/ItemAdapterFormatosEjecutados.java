package com.example.formato;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.gestiondeltec.R;
import java.util.List;

/**
 * Created by Jackson on 12/10/2015.
 */
public class ItemAdapterFormatosEjecutados extends BaseAdapter {

    private Context context;
    private List<ItemEjecucionFormato> items;

    public ItemAdapterFormatosEjecutados(Context context, List<ItemEjecucionFormato> items) {
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
        final ItemEjecucionFormato item = this.items.get(position);
        //OPTIMIZACION DE MEMORIA CON LOS OBJETOS YA CREADOS
        if (convertView == null) {//INICIALIZO LAS VARIABLES
            // Create a new view into the list.
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.list_item_formato_ejecutado, parent, false);

            holder = new ViewHolder();
            holder.tvTfNombre = (TextView) rowView.findViewById(R.id.tvTfNombre);
            holder.tvEfCodigo = (TextView) rowView.findViewById(R.id.tvEfCodigo);
            holder.tvEfFecha = (TextView) rowView.findViewById(R.id.tvEfFecha);
            holder.tvEfHora = (TextView) rowView.findViewById(R.id.tvEfHora);
            holder.tvEfCalificacion = (TextView) rowView.findViewById(R.id.tvEfCalificacion);

            rowView.setTag(holder);
        } else {
            holder = (ViewHolder) rowView.getTag();
        }

        holder.tvTfNombre.setText(item.getTf_nombre());
        holder.tvEfCodigo.setText(String.valueOf(item.getEf_id()));
        holder.tvEfFecha.setText(item.getEf_fecha_creacion());
        holder.tvEfHora.setText(item.getEf_hora_creacion());
        holder.tvEfCalificacion.setText(String.valueOf(item.getEf_calificacion()));

        return rowView;
    }


    static class ViewHolder {
        TextView tvTfNombre;
        TextView tvEfCodigo;
        TextView tvEfFecha;
        TextView tvEfHora;
        TextView tvEfCalificacion;
    }
}
