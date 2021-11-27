package com.example.bluetooh;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.gestiondeltec.R;

public class ItemMaterialAdapter extends BaseAdapter {

	private Context context;
	private List<ItemMaterial> items;

	public ItemMaterialAdapter(Context context, List<ItemMaterial> items) {
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
			rowView = inflater.inflate(R.layout.list_item_material, parent, false);
		}

		TextView tvNombreMaterial = (TextView) rowView.findViewById(R.id.tvNombreMaterial); 
		TextView tvSerieMaterial = (TextView) rowView.findViewById(R.id.tvSerieMaterial); 
		TextView tvCantidadMaterial = (TextView) rowView.findViewById(R.id.tvCantidadMaterial); 
		TextView tvUnidadMaterial = (TextView) rowView.findViewById(R.id.tvUnidadMaterial); 
		TextView tvNodoMaterial = (TextView) rowView.findViewById(R.id.tvNodoMaterial); 

		ItemMaterial item = this.items.get(position);
		tvNombreMaterial.setText(item.getNombre());
		tvSerieMaterial.setText(item.getSerie());
		tvCantidadMaterial.setText(String.valueOf(item.getCantidad()));
		tvUnidadMaterial.setText(item.getUnidad());
		
		if(item.getNodo() > 0)		
			tvNodoMaterial.setText(String.valueOf(item.getNodo()));

		return rowView;
	}

}
