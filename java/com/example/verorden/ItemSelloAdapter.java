package com.example.verorden;

import com.example.gestiondeltec.R;
import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ItemSelloAdapter extends BaseAdapter {
 
    private Context context;
    private List<ItemSello> items;
 
    public ItemSelloAdapter(Context context, List<ItemSello> items) {
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
            rowView = inflater.inflate(R.layout.list_item_sellos, parent, false);
        }
 
        // Set data into the view.
        //ImageView ivItem = (ImageView) rowView.findViewById(R.id.imageView_imagen);
        //ivItem.setImageResource(item.getImage());
                
        TextView tvSerieSello = (TextView) rowView.findViewById(R.id.tvSerieSello); 
        TextView tvPrefijoSello = (TextView) rowView.findViewById(R.id.tvPrefijoSello); 
        TextView tvTipoSello = (TextView) rowView.findViewById(R.id.tvTipoSello); 
        
 
        ItemSello item = this.items.get(position);
        tvSerieSello.setText(item.getSerie());
        tvPrefijoSello.setText(item.getPrefijo());
        tvTipoSello.setText(item.getNombre());      
 
        return rowView;
    }

}
