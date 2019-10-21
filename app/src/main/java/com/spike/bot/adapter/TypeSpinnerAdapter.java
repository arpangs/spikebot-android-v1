package com.spike.bot.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.spike.bot.R;
import com.spike.bot.core.Common;

import java.util.ArrayList;

/**
 * Created by kaushal on 9/1/18.
 */

public class TypeSpinnerAdapter extends BaseAdapter {

    Context context;
    ArrayList<String> flags;
    LayoutInflater inflter;
    private int iconType;
    private boolean isIcon = true;
    String upperString;

    public TypeSpinnerAdapter(Context applicationContext, ArrayList<String> flags, int type, boolean isIcon) {
        this.context = applicationContext;
        this.flags = flags;
        this.iconType = type;
        this.isIcon = isIcon;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return flags.size();
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
        view = inflter.inflate(R.layout.row_spinner_type, null);
        ImageView icon = (ImageView) view.findViewById(R.id.imageView);
        TextView names = (TextView) view.findViewById(R.id.textView);

        try {
            upperString = flags.get(i).substring(0, 1).toUpperCase() + flags.get(i).substring(1);
            names.setText(upperString);
        } catch (Exception ex) {
            ex.printStackTrace();
            names.setText(flags.get(i));
        }

        if (!names.getText().toString().equalsIgnoreCase("--")) {
            icon.setImageResource(Common.getIconForEditRoom(iconType, flags.get(i)));
        }

        if (isIcon) {
            icon.setVisibility(View.VISIBLE);
        } else {
            icon.setVisibility(View.GONE);
        }
        return view;
    }
}