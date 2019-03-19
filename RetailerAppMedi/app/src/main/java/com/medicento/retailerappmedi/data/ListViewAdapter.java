package com.medicento.retailerappmedi.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.medicento.retailerappmedi.R;

import java.util.ArrayList;

public class ListViewAdapter extends ArrayAdapter<MenuItems> {

    public ListViewAdapter(Context context, ArrayList<MenuItems> user) {
        super(context, 0, user);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        MenuItems menuItems = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.menu_items,parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.image);

        TextView tvName = (TextView) convertView.findViewById(R.id.text);

        // Populate the data into the template view using the data object

        tvName.setText(menuItems.getName());

        imageView.setImageDrawable(menuItems.getDrawable());


        // Return the completed view to render on screen

        return convertView;


    }
}
