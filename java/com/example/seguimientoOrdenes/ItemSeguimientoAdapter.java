package com.example.seguimientoOrdenes;

import com.example.gestiondeltec.R;
import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

public class ItemSeguimientoAdapter extends BaseAdapter {
 
    private Context context;
    private List<ItemSeguimiento> items;
    //private PieChart pieChart;
    private BarChart barChart;
 
    public ItemSeguimientoAdapter(Context context, List<ItemSeguimiento> items) {
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
 
        if (convertView == null) {
            // Create a new view into the list.
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.list_item_seguimiento, parent, false);
        }
                
        TextView tvSegCodigoCuadrilla = (TextView) rowView.findViewById(R.id.tvSegCodigoCuadrilla); 
        TextView tvSegNombreCuadrilla = (TextView) rowView.findViewById(R.id.tvSegNombreCuadrilla);
        TextView tvSegTotalOrdenes = (TextView) rowView.findViewById(R.id.tvRepTotalOrdenes);
        TextView tvSegTotalEject = (TextView) rowView.findViewById(R.id.tvRepTotalEnviadas);
        TextView tvSegTotalPendientes = (TextView) rowView.findViewById(R.id.tvRepTotalPendientes);
        
        TextView tvSegTotalCausas = (TextView) rowView.findViewById(R.id.tvRepTotalFotos);
        /*TextView tvSegMedNoExiste = (TextView) rowView.findViewById(R.id.tvSegMedNoExiste);
        TextView tvSegMedIlegible = (TextView) rowView.findViewById(R.id.tvSegMedIlegible);
        TextView tvSegSinAcceso = (TextView) rowView.findViewById(R.id.tvSegSinAcceso);
        TextView tvSegLote = (TextView) rowView.findViewById(R.id.tvSegLote);
        TextView tvSegFuerzaMayor = (TextView) rowView.findViewById(R.id.tvSegFuerzaMayor); */
        
        ItemSeguimiento item = this.items.get(position);        
        
        String nombre = String.valueOf(item.getPer_codigo()) + " " + item.getNombre().trim();
        
        if(nombre.length() > 20)
        	nombre = nombre.substring(0, 20);        	
        
        tvSegNombreCuadrilla.setText(item.getFecha_fin());
        tvSegCodigoCuadrilla.setText(nombre);
        tvSegTotalOrdenes.setText(String.valueOf(item.getTotal()));
        tvSegTotalEject.setText(String.valueOf(item.getEjecutada()));
        tvSegTotalPendientes.setText(String.valueOf(item.getPendiente()));
        tvSegTotalCausas.setText(String.valueOf(item.getTotal_causas()));
        /*tvSegMedNoExiste.setText(String.valueOf(item.getMed_no_existe()));
        tvSegMedIlegible.setText(String.valueOf(item.getMed_ilegible()));
        tvSegSinAcceso.setText(String.valueOf(item.getSin_acceso()));
        tvSegLote.setText(String.valueOf(item.getLote()));
        tvSegFuerzaMayor.setText(String.valueOf(item.getFuerza_mayor()));  */

        barChart = (BarChart) rowView.findViewById(R.id.BarChartReport);

        if(item.getGraficos()) {

            barChart.setVisibility(View.VISIBLE);
        /*definimos algunos atributos*/
            //pieChart.setHoleRadius(40f);
            //pieChart.setRotationEnabled(false);
            //pieChart.animateXY(1500, 1500);

            ArrayList<BarEntry> entries = new ArrayList<BarEntry>();
            entries.add(new BarEntry(item.getTotal(), 0));
            entries.add(new BarEntry(item.getEjecutada(), 1));
            entries.add(new BarEntry(item.getPendiente(), 2));
            entries.add(new BarEntry(item.getImpedimento_tapado(), 3));
            entries.add(new BarEntry(item.getImpedimento_cambio(), 4));
            entries.add(new BarEntry(item.getImpedimento_reja(), 5));

		/*creamos una lista para los valores Y
        ArrayList<Entry> valsY = new ArrayList<Entry>();
        valsY.add(new Entry(item.getPendiente(),0));
        valsY.add(new Entry(item.getEjecutada(),1));*/

 		/*creamos una lista para los valores X
        ArrayList<String> valsX = new ArrayList<String>();
        valsX.add("Pend");
        valsX.add("Ejec");*/

 		/*creamos una lista de colores
        ArrayList<Integer> colors = new ArrayList<Integer>();
        colors.add(Color.RED);
        colors.add(Color.GREEN);*/

 		/*seteamos los valores de Y y los colores
        PieDataSet set1 = new PieDataSet(valsY, "Resul");
        set1.setSliceSpace(1f);
        set1.setColors(colors);*/

		/*seteamos los valores de X
        PieData data = new PieData(valsX, set1);
        pieChart.setData(data);
        pieChart.highlightValues(null);
        pieChart.invalidate();*/

            BarDataSet dataset = new BarDataSet(entries, "# of Calls");
            dataset.setBarSpacePercent(5f);
            ArrayList<String> labels = new ArrayList<String>();
            labels.add("Total");
            labels.add("Ejec");
            labels.add("Pend");
            labels.add("12");
            labels.add("7");
            labels.add("64");

            dataset.setColors(ColorTemplate.JOYFUL_COLORS);
            BarData data = new BarData(labels, dataset);
            barChart.setData(data);
            barChart.getLegend().setEnabled(false);
            barChart.setDescription("");
            barChart.getAxisLeft().setDrawLabels(false);
            barChart.invalidate();
        }else barChart.setVisibility(View.GONE);
        
        return rowView;
    }
}
