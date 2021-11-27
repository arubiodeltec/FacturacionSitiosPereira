package com.example.gestionOrdenes;

import com.example.gestiondeltec.R;
import java.util.List;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ItemAdapter extends BaseAdapter {

    private Context context;
    private List<Item> items;

    public ItemAdapter(Context context, List<Item> items) {
        this.context = context;
        this.items = items;
        Log.i("Vuelta","Vuelta al ciclo");
        if (items.size()!= 0) {
            Log.i("Items",items.toString());
        }
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
        ViewHolder holder;

        if (convertView == null) {
            // Create a new view into the list.
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.list_item, parent, false);

            holder = new ViewHolder();
            holder.tvDireccion = (TextView)rowView.findViewById(R.id.tvDireccion);
            holder.tvNombre = (TextView)rowView.findViewById(R.id.tvNombre);
            holder.tvLectConsumo = (TextView)rowView.findViewById(R.id.tvLectConsumo);
            holder.tvCliContrato = (TextView)rowView.findViewById(R.id.tvCLiContrato);
            holder.tvCliNombre = (TextView)rowView.findViewById(R.id.tvCliNombre);
            holder.tvLectFranja = (TextView)rowView.findViewById(R.id.tvLectFranja);
            holder.tvLectRutaConsecutivo = (TextView)rowView.findViewById(R.id.tvLectRutaConsecutivo);

            rowView.setTag(holder);
        }else{
        	holder = (ViewHolder)rowView.getTag();
        }

        Item item = this.items.get(position);
        String cliNombre = item.getCliNombre().trim();

        if(cliNombre.length() > 20)
        	cliNombre.substring(0, 20);

        holder.tvDireccion.setText(item.getdireccion().trim());
        holder.tvLectRutaConsecutivo.setText(String.valueOf(item.getRuta()) + "     " + String.valueOf(item.getConsecutivo()));
        holder.tvLectFranja.setText(item.getConsumo());
        holder.tvNombre.setText(item.getElemento());
        holder.tvLectConsumo.setText(item.getProducto());
        holder.tvCliContrato.setText(String.valueOf(item.getCliContrato()));
        holder.tvCliNombre.setText(cliNombre);

        return rowView;
    }

    static class ViewHolder {
    	TextView tvDireccion;
        TextView tvNombre;
        TextView tvLectConsumo;
        TextView tvCliContrato;
        TextView tvCliNombre;
        TextView tvLectFranja;
        TextView tvLectRutaConsecutivo;
    }
}
