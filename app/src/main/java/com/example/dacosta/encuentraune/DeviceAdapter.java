package com.example.dacosta.encuentraune;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by dacosta on 7/24/2015.
 */
public class DeviceAdapter extends BaseAdapter {

    private final Context context;
    private ArrayList<Devices> values;

    public DeviceAdapter(Context context, ArrayList<Devices> values) {
        this.context = context;
        this.values = values;
    }

    @Override
    public int getCount() {
        return values.size();
    }

    @Override
    public Object getItem(int position) {
        return values.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View gridView;

        Devices item = values.get(position);
        if (convertView==null) {
            gridView = new View(context);
            // get layout from mobile.xml
            gridView = inflater.inflate(R.layout.item_device, null);

        }else{
            gridView = convertView;
        }
        if (item!=null) {
            TextView textView = (TextView) gridView.findViewById(R.id.txtName);
            textView.setText(item.getName());

            textView = (TextView) gridView.findViewById(R.id.txtMacAddress);
            textView.setText(item.getMacAddress());

        }
        return gridView;
    }
}
