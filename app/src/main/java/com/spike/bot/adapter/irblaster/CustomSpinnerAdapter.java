package com.spike.bot.adapter.irblaster;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.spike.bot.R;

import java.util.List;

import static com.spike.bot.ChatApplication.getContext;

public class CustomSpinnerAdapter extends ArrayAdapter<String> {

    LayoutInflater flater;

    public CustomSpinnerAdapter(Activity context, int resouceId, int textviewId, List<String> list){

        super(context,resouceId,textviewId, list);
//        flater = context.getLayoutInflater();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        return rowview(convertView,position);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return rowview(convertView,position);
    }

    private View rowview(View convertView , int position){

        String sValue = getItem(position);

        viewHolder holder ;
        View rowview = convertView;
        if (rowview==null) {

            holder = new viewHolder();
            flater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowview = flater.inflate(R.layout.row_spinner_dropdown_item, null, false);

            holder.txtTitle = (TextView) rowview.findViewById(R.id.spinner_item);

            rowview.setTag(holder);
        }else{
            holder = (viewHolder) rowview.getTag();
        }
        if(sValue.contains("MODE")){
            holder.txtTitle.setTextColor(Color.parseColor("#808080"));
        }else{
            holder.txtTitle.setTextColor(Color.parseColor("#111111"));
        }

        holder.txtTitle.setText(sValue);

        return rowview;
    }

    private class viewHolder{
        TextView txtTitle;
        ImageView imageView;
    }
}