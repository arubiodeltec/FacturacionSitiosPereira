package com.example.formato;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.gestiondeltec.R;

import java.util.List;

/**
 * Created by DELTEC on 11/09/2015.
 */
public class ItemAdapterFormatoExpandible extends BaseExpandableListAdapter  {

    public Context context;
    private List<ItemFormato2> items;
    public int tf_id = 0, ef_id = 0, cuadrilla = 0;

    public ItemAdapterFormatoExpandible(Context context, List<ItemFormato2> items,int tf_id,int ef_id, int cuadrilla) {
        this.context = context;
        this.items = items;
        this.tf_id = tf_id;
        this.ef_id = ef_id;
        this.cuadrilla = cuadrilla;
    }

    @Override
    public int getGroupCount() {
        return items.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
       // return items.get(groupPosition).getHijos().size();
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return items.get(groupPosition);
    }

    // Nos devuelve los datos asociados a un subitem en base
    // a la posici?n
    @Override
    public Object getChild(int groupPosition, int childPosition) {

        ItemFormato2 item = (ItemFormato2) getGroup(groupPosition);
        if(item.getTc_id() < 3){
            return item;
        }else return item.getHijos();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    // Devuelve el id de un item o subitem en base a la
    // posici?n de item y subitem
    @Override
    public long getChildId(int groupPosition, int childPosition) {

        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    //M?todo que se invoca al contraer un ?tem
    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
    }
    //M?todo que se invoca al expandir un ?tem
    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
    }

    public void onGroupExpand(int groupPosition)
    {
        for (int i = 0; i < items.size(); i++)
        {
            if (i != groupPosition)
                onGroupCollapsed(i);
        }
        super.onGroupExpanded(groupPosition);
    }

    //Obtenemos el layout para los ?tems
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        View rowView = convertView;
        final ViewHolderPadre holder;
        final ItemFormato2 item = (ItemFormato2) getGroup(groupPosition);
        //OPTIMIZACION DE MEMORIA CON LOS OBJETOS YA CREADOS
        if (convertView == null) {//INICIALIZO LAS VARIABLES
            // Create a new view into the list.
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.list_item_formato_expandible, parent, false);

            holder = new ViewHolderPadre();
            holder.tvCfid = (TextView) rowView.findViewById(R.id.tvCfid);
            holder.tvCfModulo = (TextView) rowView.findViewById(R.id.tvCfModulo);
            holder.tvCfNombre = (TextView) rowView.findViewById(R.id.tvCfNombre);
            holder.ivCfIcon = (ImageView) rowView.findViewById(R.id.ivCfIcon);
            rowView.setTag(holder);
        } else {
            holder = (ViewHolderPadre) rowView.getTag();
        }

        holder.tvCfid.setText(String.valueOf(item.getCf_id()));
        holder.tvCfModulo.setText(item.getMf_nombre());
        holder.tvCfNombre.setText(item.getcf_nombre() + "? ");

        int tc_id = item.getTc_id();
        holder.ivCfIcon.setImageResource(R.drawable.editcopy);

        if (tc_id < 3 || tc_id > 4) {//RESPUESTA NUMERICA  //RESPUESTA ABIERTA
            //onGroupExpand(groupPosition);
            if (item.getResultado().length() > 0)
                holder.ivCfIcon.setImageResource(R.drawable.button_ok);
        } else  {
            for(int i = 0; i < item.getHijos().size(); i++) {
                ItemFormato2 itemFormato = item.getHijos().get(i);
                if (itemFormato.getChecked()) {
                    holder.ivCfIcon.setImageResource(R.drawable.button_ok);
                }
            }
        }
        return rowView;
    }

    // En base a la posici?n del item y de subitem nos devuelve
    // el objeto view correspondiente y el layout para los subitems
    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        View rowView = convertView;
        final ViewHolderHijo holder;
        final ItemFormato2 item = (ItemFormato2) getGroup(groupPosition);
        //OPTIMIZACION DE MEMORIA CON LOS OBJETOS YA CREADOS
        if (convertView == null) {//INICIALIZO LAS VARIABLES
            // Create a new view into the list.
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.list_subitem_formato_expandible, parent, false);

            holder = new ViewHolderHijo();
            holder.rgRespuestaSeleccion = (RadioGroup) rowView.findViewById(R.id.rgRespuestaSeleccion);

            holder.tvRespuestaString = (TextView) rowView.findViewById(R.id.tvRespuestaString);
            holder.llRespuestaSeleccion = (LinearLayout) rowView.findViewById(R.id.llRespuestaSeleccion);
            holder.llRespuestaString = (LinearLayout) rowView.findViewById(R.id.llRespuestaString);
            holder.llRespuestaHijo = (LinearLayout) rowView.findViewById(R.id.llRespuestaHijo);
            rowView.setTag(holder);
        } else {
            holder = (ViewHolderHijo) rowView.getTag();
        }

        int tc_id = item.getTc_id();
        holder.rgRespuestaSeleccion.removeAllViews();
        holder.llRespuestaHijo.removeAllViews();
        holder.llRespuestaString.setVisibility(View.GONE);
        holder.llRespuestaSeleccion.setVisibility(View.GONE);
        holder.llRespuestaHijo.setVisibility(View.GONE);
        holder.tvRespuestaString.setText("");

        if (tc_id < 3 || tc_id > 4) {//1.RESPUESTA NUMERICA  //2.RESPUESTA ABIERTA  //5.FECHA //6.FOTO
            String txt = item.getResultado();
            holder.tvRespuestaString.setHint(item.getCf_descripcion());
            if(txt.length() > 0)
                holder.tvRespuestaString.setText(txt);
            holder.tvRespuestaString.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    insertCampoFormato(item);
                }
            });
            holder.llRespuestaString.setVisibility(View.VISIBLE);
        } else switch (tc_id) {
            case 3://MULTIPLE RESPUESTA
                for(int i = 0; i < item.getHijos().size(); i++) {
                    final ItemFormato2 itemFormato = item.getHijos().get(i);
                    final CheckBox cb = new CheckBox(context);
                    cb.setId(itemFormato.getCf_id());
                    cb.setText(itemFormato.getcf_nombre());
                    if (itemFormato.getChecked()) {
                        cb.setChecked(true);
                        if (itemFormato.getEsPadre()) {
                            holder.llRespuestaHijo.setVisibility(View.VISIBLE);
                            CustExpListview SecondLevelexplv = new CustExpListview(context);
                            SecondLevelexplv.setAdapter(new SecondLevelAdapter(context,itemFormato.getHijos()));
                            SecondLevelexplv.setGroupIndicator(null);
                            holder.llRespuestaHijo.addView(SecondLevelexplv);
                        }else if (!otroItemChecked(item)) {
                            holder.llRespuestaHijo.setVisibility(View.GONE);
                        }
                    } if (itemFormato.getEsPadre()&& !otroItemChecked(item)) {
                        holder.llRespuestaHijo.setVisibility(View.GONE);
                        holder.llRespuestaHijo.removeAllViews();
                        //lvRespuestaLvHijo.setAdapter(null);
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
                    final RadioButton cb = new RadioButton(context);
                    cb.setId(itemFormato.getCf_id());
                    cb.setText(itemFormato.getcf_nombre());
                    if (itemFormato.getChecked()) {
                        cb.setChecked(true);
                        if (itemFormato.getEsPadre()) {
                            holder.llRespuestaHijo.setVisibility(View.VISIBLE);
                            final CustExpListview SecondLevelexplv = new CustExpListview(context);
                            SecondLevelexplv.setAdapter(new SecondLevelAdapter(context,itemFormato.getHijos()));
                            SecondLevelexplv.setGroupIndicator(null);
                            SecondLevelexplv.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                                int previousItem = -1;
                                @Override
                                public void onGroupExpand(int groupPosition) {
                                    if (groupPosition != previousItem)
                                        SecondLevelexplv.collapseGroup(previousItem);
                                    previousItem = groupPosition;
                                }
                            });
                            holder.llRespuestaHijo.addView(SecondLevelexplv);
                        } else if (!otroItemChecked(item)) {
                            holder.llRespuestaHijo.setVisibility(View.GONE);
                        }
                    }else if (itemFormato.getEsPadre()&& !otroItemChecked(item)) {
                        holder.llRespuestaHijo.setVisibility(View.GONE);
                        holder.llRespuestaHijo.removeAllViews();
                        //lvRespuestaLvHijo.setAdapter(null);
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

        return rowView;
    }

    //Nos informa si es seleccionable o no un ?tem o subitem
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
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
        else if (item.getTc_id() == 2)
            etFormatoDRespuesta.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_NULL);
        else if (item.getTc_id() == 5)
            etFormatoDRespuesta.setInputType(InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_NORMAL | InputType.TYPE_NULL);


        etFormatoDRespuesta.setText(resultado);//O VA ANTES DEL INPUTTYPE

        AlertDialog MyDialog;
        AlertDialog.Builder MyBuilder = new AlertDialog.Builder(context);
        MyBuilder.setTitle(tituloStr);
        MyBuilder.setCancelable(true);
        MyBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        if(item.getTc_id() != 6){
            MyBuilder.setView(layout);
            MyBuilder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String strObsAbierta = etFormatoDRespuesta.getEditableText().toString();
                    EjecucionFormatoModel managerFormato = new EjecucionFormatoModel(context);
                    managerFormato.open();
                    managerFormato.ingresarActualizarRegistroEjecucion(cf_id, ef_id, cuadrilla, cf_tabla_referencia, strObsAbierta);
                    managerFormato.close();
                    item.setResultado(strObsAbierta);

                }
            });
        }else {
            MyBuilder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    iniciarCamara(cf_id, ef_id, cuadrilla, cf_tabla_referencia, item);
                }
            });
        }
        MyDialog = MyBuilder.create();
        MyDialog.show();
    }

    public void iniciarCamara(int cf_id, int ef_id, int cuadrilla, String cf_tabla_referencia, ItemFormato2 item) {
        Intent int1 = new Intent(context, Camera.class);
        Bundle bolsa = new Bundle();
        bolsa.putInt("cf_id", cf_id);
        bolsa.putInt("ef_id", ef_id);
        bolsa.putInt("cuadrilla", cuadrilla);
        bolsa.putString("cf_tabla_referencia", cf_tabla_referencia);
        bolsa.putSerializable("item", item);

        int1.putExtras(bolsa);
        context.startActivity(int1);
    }


    private void insertarSeleccionMultiple(ItemFormato2 item, ViewHolderHijo holder) {

        EjecucionFormatoModel managerFormato = new EjecucionFormatoModel(context);
        managerFormato.open();
        System.out.println(" VOY A GUARDAR" + item.getcf_nombre());
        if (item.getChecked()) {
            managerFormato.ingresarActualizarRegistroEjecucionMultiple(item.getCf_parent_id(), ef_id, cuadrilla, item.getCf_tabla_referencia(), String.valueOf(item.getCf_id()));//PENDIENTE
            if (item.getEsPadre()) {
                holder.llRespuestaHijo.setVisibility(View.VISIBLE);
                final CustExpListview SecondLevelexplv = new CustExpListview(context);
                SecondLevelexplv.setAdapter(new SecondLevelAdapter(context,item.getHijos()));
                SecondLevelexplv.setGroupIndicator(null);
                SecondLevelexplv.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                    int previousItem = -1;
                    @Override
                    public void onGroupExpand(int groupPosition) {
                        if (groupPosition != previousItem)
                            SecondLevelexplv.collapseGroup(previousItem);
                        previousItem = groupPosition;
                    }
                });
            } else {
                holder.llRespuestaHijo.setVisibility(View.GONE);
            }
        } else {
            managerFormato.deleteEjecucionRespuesta(item.getCf_id(),ef_id, item.getCf_tabla_referencia());
            if (item.getEsPadre()) {
                holder.llRespuestaHijo.setVisibility(View.GONE);
            }
        }
        managerFormato.close();
    }

    private void insertarSeleccionUnica(ItemFormato2 item, ViewHolderHijo holder) {

        EjecucionFormatoModel managerFormato = new EjecucionFormatoModel(context);
        managerFormato.open();
        managerFormato.ingresarActualizarRegistroEjecucion(item.getCf_parent_id(), ef_id, cuadrilla, item.getCf_tabla_referencia(), String.valueOf(item.getCf_id()));
        if (item.getEsPadre()) {
            holder.llRespuestaHijo.removeAllViews();
            holder.llRespuestaHijo.setVisibility(View.VISIBLE);
            final CustExpListview SecondLevelexplv = new CustExpListview(context);
            SecondLevelexplv.setAdapter(new SecondLevelAdapter(context,item.getHijos()));
            SecondLevelexplv.setGroupIndicator(null);
            SecondLevelexplv.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                int previousItem = -1;
                @Override
                public void onGroupExpand(int groupPosition) {
                    if (groupPosition != previousItem)
                        SecondLevelexplv.collapseGroup(previousItem);
                    previousItem = groupPosition;
                }
            });
            holder.llRespuestaHijo.addView(SecondLevelexplv);
        } else {
            holder.llRespuestaHijo.setVisibility(View.GONE);
            holder.llRespuestaHijo.removeAllViews();
        }
        managerFormato.close();
    }

    public class CustExpListview extends ExpandableListView {

        int intGroupPosition, intChildPosition, intGroupid;

        public CustExpListview(Context context) {
            super(context);
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            //widthMeasureSpec = MeasureSpec.makeMeasureSpec(960, MeasureSpec.AT_MOST);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(1000, MeasureSpec.AT_MOST);
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    public class SecondLevelAdapter extends BaseExpandableListAdapter {

        private List<ItemFormato2> items;
        public Context context;

        public SecondLevelAdapter(Context context, List<ItemFormato2> items) {
            this.context = context;
            this.items = items;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            ItemFormato2 item = (ItemFormato2) getGroup(groupPosition);
            if(item.getTc_id() < 3){
                return item;
            }else return item.getHijos();
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
            View rowView = convertView;
            final ViewHolderHijo holder;
            final ItemFormato2 item = (ItemFormato2) getGroup(groupPosition);
            //OPTIMIZACION DE MEMORIA CON LOS OBJETOS YA CREADOS
            if (convertView == null) {//INICIALIZO LAS VARIABLES
                // Create a new view into the list.
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rowView = inflater.inflate(R.layout.list_subitem_formato_expandible, parent, false);

                holder = new ViewHolderHijo();
                holder.rgRespuestaSeleccion = (RadioGroup) rowView.findViewById(R.id.rgRespuestaSeleccion);

                holder.tvRespuestaString = (TextView) rowView.findViewById(R.id.tvRespuestaString);
                holder.llRespuestaSeleccion = (LinearLayout) rowView.findViewById(R.id.llRespuestaSeleccion);
                holder.llRespuestaString = (LinearLayout) rowView.findViewById(R.id.llRespuestaString);
                holder.llRespuestaHijo = (LinearLayout) rowView.findViewById(R.id.llRespuestaHijo);
                rowView.setTag(holder);
            } else {
                holder = (ViewHolderHijo) rowView.getTag();
            }

            int tc_id = item.getTc_id();
            holder.rgRespuestaSeleccion.removeAllViews();
            holder.llRespuestaHijo.removeAllViews();
            holder.llRespuestaString.setVisibility(View.GONE);
            holder.llRespuestaSeleccion.setVisibility(View.GONE);
            holder.llRespuestaHijo.setVisibility(View.GONE);
            holder.tvRespuestaString.setText("");

            if (tc_id < 3 || tc_id > 4) {//1 RESPUESTA NUMERICA  //2 RESPUESTA ABIERTA //5 FECHA //6 FOTO
                String txt = item.getResultado();
                holder.tvRespuestaString.setHint(item.getCf_descripcion());
                if(txt.length() > 0)
                    holder.tvRespuestaString.setText(txt);
                holder.tvRespuestaString.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        insertCampoFormato(item);
                    }
                });
                holder.llRespuestaString.setVisibility(View.VISIBLE);
            } else switch (tc_id) {
                case 3://MULTIPLE RESPUESTA
                    for(int i = 0; i < item.getHijos().size(); i++) {
                        final ItemFormato2 itemFormato = item.getHijos().get(i);
                        final CheckBox cb = new CheckBox(context);
                        cb.setId(itemFormato.getCf_id());
                        cb.setText(itemFormato.getcf_nombre());
                        if (itemFormato.getChecked()) {
                            cb.setChecked(true);
                        }

                        if (itemFormato.getEsPadre()) {
                            holder.llRespuestaHijo.setVisibility(View.VISIBLE);
                            CustExpListview2 SecondLevelexplv = new CustExpListview2(context);
                            SecondLevelexplv.setAdapter(new SecondLevelAdapter(context,itemFormato.getHijos()));
                            SecondLevelexplv.setGroupIndicator(null);
                            holder.llRespuestaHijo.addView(SecondLevelexplv);
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
                    for (final ItemFormato2 itemFormato : item.getHijos()) {
                        final RadioButton cb = new RadioButton(context);
                        cb.setId(itemFormato.getCf_id());
                        cb.setText(itemFormato.getcf_nombre());
                        if (itemFormato.getChecked()) {
                            cb.setChecked(true);
                            if (itemFormato.getEsPadre()) {
                                holder.llRespuestaHijo.setVisibility(View.VISIBLE);
                                final CustExpListview2 SecondLevelexplv = new CustExpListview2(context);
                                int tamano = getTamano(itemFormato.getHijos());
                                System.out.println("Tamano es igual a " + tamano * 120);
                                SecondLevelexplv.setLenght(tamano * 120);
                                SecondLevelexplv.setAdapter(new SecondLevelAdapter(context, itemFormato.getHijos()));
                                SecondLevelexplv.setGroupIndicator(null);
                                SecondLevelexplv.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                                    int previousItem = -1;

                                    @Override
                                    public void onGroupExpand(int groupPosition) {
                                        if (groupPosition != previousItem)
                                            SecondLevelexplv.collapseGroup(previousItem);
                                        previousItem = groupPosition;
                                    }
                                });
                                holder.llRespuestaHijo.addView(SecondLevelexplv);
                            } else if (!otroItemChecked(item)) {
                                holder.llRespuestaHijo.setVisibility(View.GONE);
                            }
                        }else if (itemFormato.getEsPadre()&& !otroItemChecked(item)) {
                            holder.llRespuestaHijo.setVisibility(View.GONE);
                            //lvRespuestaLvHijo.setAdapter(null);
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

            return rowView;
        }

        public int getTamano(List<ItemFormato2> itemsAux){
            int tamano = 1;
            for (ItemFormato2 itemFormato : itemsAux) {
                if(itemFormato.getTc_id() < 3)
                    tamano++;
                else tamano += getTamano(itemFormato.getHijos());
            }
            return tamano;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return 1;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return items.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return items.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

            View rowView = convertView;
            final ViewHolderPadre holder;
            final ItemFormato2 item = (ItemFormato2) getGroup(groupPosition);
            //OPTIMIZACION DE MEMORIA CON LOS OBJETOS YA CREADOS
            if (convertView == null) {//INICIALIZO LAS VARIABLES
                // Create a new view into the list.
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rowView = inflater.inflate(R.layout.list_item_formato_expandible, parent, false);

                holder = new ViewHolderPadre();
                holder.tvCfid = (TextView) rowView.findViewById(R.id.tvCfid);
                holder.tvCfModulo = (TextView) rowView.findViewById(R.id.tvCfModulo);
                holder.tvCfNombre = (TextView) rowView.findViewById(R.id.tvCfNombre);
                holder.ivCfIcon = (ImageView) rowView.findViewById(R.id.ivCfIcon);
                rowView.setTag(holder);
            } else {
                holder = (ViewHolderPadre) rowView.getTag();
            }

            holder.tvCfid.setText(String.valueOf(item.getCf_id()));
            holder.tvCfModulo.setText(item.getMf_nombre());
            holder.tvCfNombre.setText(item.getcf_nombre() + "? ");

            holder.ivCfIcon.setId(item.getCf_id());

            int tc_id = item.getTc_id();
            holder.ivCfIcon.setImageResource(R.drawable.editcopy);

            if (tc_id < 3 || tc_id > 4) {//RESPUESTA NUMERICA  //RESPUESTA ABIERTA
                //onGroupExpand(groupPosition);
                if (item.getResultado().length() > 0)
                    holder.ivCfIcon.setImageResource(R.drawable.button_ok);
            } else  {
                for(int i = 0; i < item.getHijos().size(); i++) {
                    ItemFormato2 itemFormato = item.getHijos().get(i);
                    if (itemFormato.getChecked()) {
                        holder.ivCfIcon.setImageResource(R.drawable.button_ok);
                    }
                }
            }
            return rowView;
        }

        @Override
        public boolean hasStableIds() {
            // TODO Auto-generated method stub
            return true;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            // TODO Auto-generated method stub
            return true;
        }
    }

    public class CustExpListview2 extends ExpandableListView {

        int intGroupPosition, intChildPosition, intGroupid;
        int tamano = 200;

        public CustExpListview2(Context context){
            super(context);
        }

        public void setLenght(int tamano){
            this.tamano = tamano;
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            //widthMeasureSpec = MeasureSpec.makeMeasureSpec(960, MeasureSpec.AT_MOST);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(tamano, MeasureSpec.EXACTLY);
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    static class ViewHolderPadre {
        TextView tvCfid;
        TextView tvCfModulo;
        TextView tvCfNombre;
        ImageView ivCfIcon;
    }

    static class ViewHolderHijo{
        RadioGroup rgRespuestaSeleccion;
        TextView tvRespuestaString;
        LinearLayout llRespuestaSeleccion;
        LinearLayout llRespuestaString;
        LinearLayout llRespuestaHijo;
    }

}
