package com.administrator.projecte;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by bluekey630 on 6/1/2017.
 */

public class CustomAdapter extends BaseAdapter {
    Context context;
    ArrayList<String> user;
    ArrayList<String> checked;
    LayoutInflater inflater;

    public CustomAdapter(Context applicationContext, ArrayList<String> user, ArrayList<String> checked) {
        this.context = context;
        this.user = user;
        this.checked = checked;
        inflater = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return user.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.list_user, null);
        TextView country = (TextView)view.findViewById(R.id.label);
        ImageView icon = (ImageView) view.findViewById(R.id.check_image);
        country.setText(user.get(i));
        icon.setImageResource(Integer.parseInt(checked.get(i)));
        return view;
    }
}
