package com.example.formato;

import android.database.Cursor;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import androidx.fragment.app.Fragment;

import com.example.gestiondeltec.R;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by DELTEC on 11/09/2015.
 */
public class EjecucionFormatos2 extends Fragment {

    private EjecucionFormatoModel manager;
    private Cursor cursor;
    private ExpandableListView  lvFormatos;
    List<ItemFormato2> items = new ArrayList<ItemFormato2>();
    public static Bundle bolsaDatosIniciales;

    private int mf_codigo = 0, ef_id = 0, tf_id = 0, cuadrilla = 0 ;


    public static EjecucionFormatos2 newInstance(Bundle arguments) {
        EjecucionFormatos2 f = new EjecucionFormatos2();
        if (arguments != null) {
            f.setArguments(arguments);
            bolsaDatosIniciales = arguments;
            if (bolsaDatosIniciales != null) {

            }
        }
        return f;
    }

    public void setIds(int imf_codigo, int ief_id, int itf_id, int icuadrilla){
        mf_codigo = imf_codigo;
        ef_id = ief_id;
        tf_id = itf_id;
        cuadrilla = icuadrilla;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_formatos2, container, false);
        manager = new EjecucionFormatoModel(getActivity());
        lvFormatos = (ExpandableListView) view.findViewById(R.id.lvFormatosExpandible);

        /*if (bolsaDatosIniciales != null) {
            mf_codigo = bolsaDatosIniciales.getInt("mf_id");
            ef_id = bolsaDatosIniciales.getInt("ef_id");

            System.out.println(" LLEGO MF " + mf_codigo + " EF " + ef_id);
        }*/

        cargarFormato();
        lvFormatos.setAdapter(new ItemAdapterFormatoExpandible(getActivity(), items,tf_id,ef_id, cuadrilla));

         //VISUALIZAR SOLO 1 GRUPO Y COLAPSAR LOS ANTERIORES
        lvFormatos.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int previousItem = -1;
            @Override
            public void onGroupExpand(int groupPosition) {
                if (groupPosition != previousItem)
                    lvFormatos.collapseGroup(previousItem);
                previousItem = groupPosition;
            }
        });

        return view;
    }

    public void cargarFormato() {
        int mf_id, cf_id, cf_codigo, cf_parent_id, tc_id, ef_estado, cf_level;
        String mf_nombre, cf_nombre, cf_tabla_referencia, re_hora_creacion, cf_descripcion;
        String resultado;
        Boolean checked = false, visible = true;
        Cursor cursorSeleccion;

        items.clear();
        manager.open();
        cursor = manager.cargarCursorFormato(tf_id, ef_id, mf_codigo);

        //System.out.println(" EJECUTO MF " + mf_codigo + " EF " + ef_id);
        int idx = 0;
        if (cursor.moveToFirst()) {
            do {
                mf_id = cursor.getInt(cursor.getColumnIndex("mf_id"));
                mf_nombre = cursor.getString(cursor.getColumnIndex("mf_nombre"));
                cf_id = cursor.getInt(cursor.getColumnIndex("cf_id"));
                cf_nombre = cursor.getString(cursor.getColumnIndex("cf_nombre"));
                cf_codigo = cursor.getInt(cursor.getColumnIndex("cf_codigo"));
                cf_parent_id = cursor.getInt(cursor.getColumnIndex("cf_parent_id"));
                cf_tabla_referencia = cursor.getString(cursor.getColumnIndex("cf_tabla_referencia"));
                cf_level = cursor.getInt(cursor.getColumnIndex("cf_level"));
                cf_descripcion = cursor.getString(cursor.getColumnIndex("cf_descripcion"));
                tc_id = cursor.getInt(cursor.getColumnIndex("tc_id"));
                ef_estado = cursor.getInt(cursor.getColumnIndex("ef_estado"));
                re_hora_creacion = cursor.getString(cursor.getColumnIndex("re_hora_creacion"));
                items.add(getItem(mf_id, mf_nombre, cf_id, cf_nombre, cf_codigo, cf_parent_id,
                        cf_tabla_referencia, tc_id, re_hora_creacion, cf_descripcion, cf_level, checked, visible));

                idx++;
            } while (cursor.moveToNext());//accessing data upto last row from table
        }

        lvFormatos.invalidateViews();
        cursor.close();
        manager.close();
        System.gc();
    }

    private ItemFormato2 getItem(int mf_id, String mf_nombre, int cf_id,String cf_nombre, int cf_codigo,
                                 int cf_parent_id, String cf_tabla_referencia, int tc_id, String re_hora_creacion,
                                 String cf_descripcion, int cf_level, boolean checked, boolean visible){
        String resultado = "";
        boolean esPadre = false;
        List<ItemFormato2> itemSelect = null;

        EjecucionFormatoModel managerFormato = new EjecucionFormatoModel(getActivity());

        managerFormato.open();
        if(tc_id < 3 || tc_id > 4){//RESPUESTA NUMERICA  //RESPUESTA ABIERTA // 5 FECHA 6 FOTO
            resultado = managerFormato.getUnicaRespuesta(cf_id, ef_id, cf_tabla_referencia);
        }else{//3. SELECCION MULTIPLE MULTIPLE RESPUESTA 4. SELECCION MULTIPLE UNICA RESPUESTA
            int idx = 0;
            Cursor cursorSeleccion = managerFormato.getDatosSeleccionMultiple(cf_id, ef_id);
            itemSelect = new ArrayList<ItemFormato2>();
            if (cursorSeleccion.moveToFirst()) {
                esPadre = false;
                int cf_id_h, id_hijo, tc_id_h, cf_codigo_h, cf_level_h;
                String cf_nombre_h, resultado_h = "", cf_tabla_referencia_h, cf_descripcion_h;
                boolean checked_h = false, visible_h = false, esPadreh = false;
                do {
                    cf_id_h = cursorSeleccion.getInt(cursorSeleccion.getColumnIndex("cf_id"));
                    cf_nombre_h = cursorSeleccion.getString(cursorSeleccion.getColumnIndex("cf_nombre"));
                    tc_id_h = cursorSeleccion.getInt(cursorSeleccion.getColumnIndex("tc_id"));
                    cf_codigo_h = cursorSeleccion.getInt(cursorSeleccion.getColumnIndex("cf_codigo"));
                    cf_tabla_referencia_h = cursorSeleccion.getString(cursorSeleccion.getColumnIndex("cf_tabla_referencia"));
                    cf_descripcion_h = cursorSeleccion.getString(cursorSeleccion.getColumnIndex("cf_descripcion"));
                    cf_level_h = cursorSeleccion.getInt(cursorSeleccion.getColumnIndex("cf_level"));
                    checked_h = false;
                    visible_h = false;
                    esPadreh = false;
                    List<ItemFormato2> itemHijos = null;

                    if (!cursorSeleccion.isNull(cursorSeleccion.getColumnIndex("resultado")))
                        checked_h = true;

                    Cursor cursor2 = managerFormato.getDatosSeleccionMultiple(cf_id_h, ef_id);
                    if (cursor2.moveToFirst()) {
                        cf_nombre_h += "."; //System.out.println(" wTIENE HIJO." + cf_nombre);
                        esPadreh = true;
                        visible_h = checked_h;
                        System.out.println(" wTIENE HIJO." + cf_nombre);
                        itemHijos = new ArrayList<ItemFormato2>();
                        int idy = 0;
                        do {
                            itemHijos.add(getItem(mf_id, mf_nombre, cursor2.getInt(cursor2.getColumnIndex("cf_id")),
                                    cursor2.getString(cursor2.getColumnIndex("cf_nombre")), cursor2.getInt(cursor2.getColumnIndex("cf_codigo")),
                                    cf_id_h, cursor2.getString(cursor2.getColumnIndex("cf_tabla_referencia")), cursor2.getInt(cursor2.getColumnIndex("tc_id")),
                                    re_hora_creacion, cursor2.getString(cursor2.getColumnIndex("cf_descripcion")), cursor2.getInt(cursor2.getColumnIndex("cf_level")),
                                    false, visible_h));
                            idy++;
                        }while(cursor2.moveToNext());
                    }
                    cursor2.close();

                    //System.out.println(" llego hijo " + cf_nombre_h + " es padre " + esPadreh + " CHECKED " + checked_h);
                    itemSelect.add(new ItemFormato2(mf_id, mf_nombre, cf_id_h, cf_nombre_h, cf_codigo_h, cf_id, cf_tabla_referencia_h, tc_id_h, re_hora_creacion,
                            cf_descripcion_h, cf_level_h, checked_h, esPadreh, true, resultado_h, itemHijos));
                    idx++;
                } while (cursorSeleccion.moveToNext());
            }
            cursorSeleccion.close();
            //holder.llRespuestaSeleccion.setVisibility(View.VISIBLE);
        }
        managerFormato.close();

        //System.out.println(" wLLEGO " + cf_nombre + " ES PADRE " + esPadre + " RESULTADO " + resultado);
        return new ItemFormato2(mf_id, mf_nombre, cf_id, cf_nombre, cf_codigo,
                cf_parent_id, cf_tabla_referencia, tc_id, re_hora_creacion,
                cf_descripcion, cf_level, checked, esPadre,
                visible, resultado, itemSelect);
    }
}
