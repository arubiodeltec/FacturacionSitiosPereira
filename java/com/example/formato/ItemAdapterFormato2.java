package com.example.formato;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.example.gestiondeltec.R;
import java.util.List;

/**
 * Created by DELTEC on 11/09/2015.
 */
public class ItemAdapterFormato2 extends BaseAdapter {

    private Context context;
    private List<ItemFormato2> items;

    public ItemAdapterFormato2(Context context, List<ItemFormato2> items) {
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
        final ItemFormato2 item = this.items.get(position);
        //OPTIMIZACION DE MEMORIA CON LOS OBJETOS YA CREADOS
        if (convertView == null) {//INICIALIZO LAS VARIABLES
            // Create a new view into the list.
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.list_item_formato, parent, false);

            holder = new ViewHolder();
            holder.tvCfid = (TextView) rowView.findViewById(R.id.tvCfid);
            holder.tvCfModulo = (TextView) rowView.findViewById(R.id.tvCfModulo);
            holder.tvCfNombre = (TextView) rowView.findViewById(R.id.tvCfNombre);
            holder.rgRespuestaSeleccion = (RadioGroup) rowView.findViewById(R.id.rgRespuestaSeleccion);

            holder.tvRespuestaString = (TextView) rowView.findViewById(R.id.tvRespuestaString);
            holder.llRespuestaSeleccion = (LinearLayout) rowView.findViewById(R.id.llRespuestaSeleccion);
            holder.llRespuestaString = (LinearLayout) rowView.findViewById(R.id.llRespuestaString);

            holder.ivCfIcon = (ImageView) rowView.findViewById(R.id.ivCfIcon);
            holder.lvRespuestaLvHijo = (ListView) rowView.findViewById(R.id.lvRespuestaLvHijo);
            holder.llRespuestaHijo = (LinearLayout) rowView.findViewById(R.id.llRespuestaHijo);

            rowView.setTag(holder);
        } else {
            holder = (ViewHolder) rowView.getTag();
        }

        holder.tvCfid.setText(String.valueOf(item.getCf_id()));
        holder.tvCfModulo.setText(item.getMf_nombre());
        holder.tvCfNombre.setText(item.getcf_nombre() + "? ");

        int tc_id = item.getTc_id();
        holder.rgRespuestaSeleccion.removeAllViews();
        holder.llRespuestaString.setVisibility(View.GONE);
        holder.llRespuestaSeleccion.setVisibility(View.GONE);
        holder.llRespuestaHijo.setVisibility(View.GONE);
        holder.ivCfIcon.setImageResource(R.drawable.editcopy);

        if (tc_id < 3) {//RESPUESTA NUMERICA  //RESPUESTA ABIERTA
            String txt = item.getResultado();
            holder.tvRespuestaString.setText(txt);
            holder.tvRespuestaString.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    insertCampoFormato(item);
                }
            });
            if (txt.length() > 0)
                holder.ivCfIcon.setImageResource(R.drawable.button_ok);
            holder.llRespuestaString.setVisibility(View.VISIBLE);
        } else switch (tc_id) {
            case 3://MULTIPLE RESPUESTA
                for (final ItemFormato2 itemFormato : item.getHijos()) {
                    final CheckBox cb = new CheckBox(this.context);
                    cb.setId(itemFormato.getCf_id());
                    cb.setText(itemFormato.getcf_nombre());
                    if (itemFormato.getChecked()) {
                        holder.ivCfIcon.setImageResource(R.drawable.button_ok);
                        cb.setChecked(true);
                    }

                    if (itemFormato.getEsPadre()) {
                        holder.lvRespuestaLvHijo.setAdapter(new ItemAdapterFormato2(this.context, itemFormato.getHijos()));
                        if (itemFormato.getChecked())
                            holder.llRespuestaHijo.setVisibility(View.VISIBLE);
                    }

                    cb.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            System.out.println(" was chosen." + cb.getText());
                            itemFormato.setChecked(cb.isChecked());
                            insertarSeleccionMultiple(itemFormato, holder);
                        }
                    });
                    holder.rgRespuestaSeleccion.addView(cb);
                }
                holder.llRespuestaSeleccion.setVisibility(View.VISIBLE);
                break;
            case 4://UNICA RESPUESTA
                //holder.rgRespuestaSeleccion.clearCheck();
                //List<ItemFormato2> itemsFormato2 = item.getLista();
                for (final ItemFormato2 itemFormato : item.getHijos()) {
                    final RadioButton cb = new RadioButton(this.context);
                    cb.setId(itemFormato.getCf_id());
                    cb.setText(itemFormato.getcf_nombre());
                    if (itemFormato.getChecked()) {
                        holder.ivCfIcon.setImageResource(R.drawable.button_ok);
                        cb.setChecked(true);
                        if (itemFormato.getEsPadre()) {
                            holder.llRespuestaHijo.setVisibility(View.VISIBLE);
                            holder.lvRespuestaLvHijo.setAdapter(new ItemAdapterFormato2(this.context, itemFormato.getHijos()));
                        } else if (!otroItemChecked(item)) {
                            holder.llRespuestaHijo.setVisibility(View.GONE);
                        }
                    }else if (itemFormato.getEsPadre()&& !otroItemChecked(item)) {
                        holder.llRespuestaHijo.setVisibility(View.GONE);
                        holder.lvRespuestaLvHijo.setAdapter(null);
                    }

                    cb.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            System.out.println(" was chosen." + cb.getText());
                            unCheckedItem(item);
                            itemFormato.setChecked(cb.isChecked());
                            insertarSeleccionUnica(itemFormato, holder);
                        }
                    });
                    holder.rgRespuestaSeleccion.addView(cb);
                }
                holder.llRespuestaSeleccion.setVisibility(View.VISIBLE);
                break;
        }
        //System.gc();

        return rowView;
    }

    private void unCheckedItem(ItemFormato2 itemAux){
        for (ItemFormato2 itemFormato : itemAux.getHijos()) {
            itemFormato.setChecked(false);
        }
    }

    private boolean otroItemChecked(ItemFormato2 itemAux){
        boolean otroChecked = false;
        for (ItemFormato2 itemFormato : itemAux.getHijos()) {
            if(itemFormato.getChecked())
                otroChecked = true;
        }
        return otroChecked;
    }

    /**
     * @param item
     */
    private void insertCampoFormato(final ItemFormato2 item) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dialog_campo_formato, null);
        String tituloStr = item.getcf_nombre();

        TextView tvFormatoDCodigo = (TextView) layout.findViewById(R.id.tvFormatoDCodigo);
        TextView tvFormatoDNombre = (TextView) layout.findViewById(R.id.tvFormatoDNombre);
        TextView tvFormatoDModulo = (TextView) layout.findViewById(R.id.tvFormatoDModulo);
        final EditText etFormatoDRespuesta = (EditText) layout.findViewById(R.id.etFormatoDRespuesta);

        tvFormatoDCodigo.setText(item.getMf_nombre());
        tvFormatoDModulo.setText(String.valueOf(item.getCf_codigo()));
        tvFormatoDNombre.setText(item.getcf_nombre());

        final int cf_id = item.getCf_id();
        final String cf_tabla_referencia = item.getCf_tabla_referencia();
        String resultado = item.getResultado();

        if (item.getTc_id() == 1)
            etFormatoDRespuesta.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NULL);
        else
            etFormatoDRespuesta.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_NULL);

        etFormatoDRespuesta.setText(resultado);//O VA ANTES DEL INPUTTYPE

        AlertDialog MyDialog;
        AlertDialog.Builder MyBuilder = new AlertDialog.Builder(context);
        MyBuilder.setTitle(tituloStr);
        MyBuilder.setView(layout);
        MyBuilder.setCancelable(true);
        MyBuilder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strObsAbierta = etFormatoDRespuesta.getEditableText().toString();
                EjecucionFormatoModel managerFormato = new EjecucionFormatoModel(context);
                managerFormato.open();
                managerFormato.ingresarActualizarRegistroEjecucion(cf_id, 1, 1, cf_tabla_referencia, strObsAbierta);
                managerFormato.close();
                item.setResultado(strObsAbierta);

            }
        });
        MyBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        MyDialog = MyBuilder.create();
        MyDialog.show();
    }

    private void insertarSeleccionMultiple(ItemFormato2 item, ViewHolder holder) {

        EjecucionFormatoModel managerFormato = new EjecucionFormatoModel(context);
        managerFormato.open();
        System.out.println(" VOY A GUARDAR" + item.getcf_nombre());
        if (item.getChecked()) {
            managerFormato.ingresarActualizarRegistroEjecucionMultiple(item.getCf_parent_id(), 1, 1, item.getCf_tabla_referencia(), String.valueOf(item.getCf_id()));//PENDIENTE
            if (item.getEsPadre()) {
                ItemAdapterFormato2 ia2 = new ItemAdapterFormato2(context, item.getHijos());
                holder.lvRespuestaLvHijo.setAdapter(ia2);
                holder.llRespuestaHijo.setVisibility(View.VISIBLE);
            } else {
                holder.lvRespuestaLvHijo.setAdapter(null);
                holder.llRespuestaHijo.setVisibility(View.GONE);
            }
        } else {
            managerFormato.deleteEjecucionRespuesta(item.getCf_id(), 1, item.getCf_tabla_referencia());
            if (item.getEsPadre()) {
                holder.llRespuestaHijo.setVisibility(View.GONE);
                holder.lvRespuestaLvHijo.setAdapter(null);
            }
        }
        managerFormato.close();
    }

    private void insertarSeleccionUnica(ItemFormato2 item, ViewHolder holder) {

        EjecucionFormatoModel managerFormato = new EjecucionFormatoModel(context);
        managerFormato.open();
        managerFormato.ingresarActualizarRegistroEjecucion(item.getCf_parent_id(), 1, 1, item.getCf_tabla_referencia(), String.valueOf(item.getCf_id()));
        if (item.getEsPadre()) {
            holder.llRespuestaHijo.setVisibility(View.VISIBLE);

            ArrayAdapter<CharSequence> adapterAsistenciaEstado = ArrayAdapter.createFromResource(context, R.array.asistencia_estado, android.R.layout.simple_spinner_item);
            adapterAsistenciaEstado.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            holder.lvRespuestaLvHijo.setAdapter(adapterAsistenciaEstado);
            /*
            ItemAdapterFormato2 ia2 = new ItemAdapterFormato2(context, item.getHijos());
            holder.lvRespuestaLvHijo.setAdapter(ia2);
            holder.lvRespuestaLvHijo.invalidateViews();*/
        } else {
            holder.llRespuestaHijo.setVisibility(View.GONE);
            holder.lvRespuestaLvHijo.setAdapter(null);
        }
        managerFormato.close();
    }

    static class ViewHolder {
        TextView tvCfid;
        TextView tvCfModulo;
        TextView tvCfNombre;
        RadioGroup rgRespuestaSeleccion;
        TextView tvRespuestaString;
        ImageView ivCfIcon;
        LinearLayout llRespuestaSeleccion;
        LinearLayout llRespuestaString;
        LinearLayout llRespuestaHijo;

        ListView lvRespuestaLvHijo;
    }
}
