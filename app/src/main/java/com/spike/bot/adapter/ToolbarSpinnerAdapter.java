package com.spike.bot.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.spike.bot.R;
import com.spike.bot.model.User;

import java.util.List;

/**
 * Created by Sagar on 17/3/18.
 * Gmail : jethvasagar2@gmail.com
 */

public class ToolbarSpinnerAdapter extends BaseAdapter{

    Context context;
    List<User> userList;
    LayoutInflater inflter;

    public ToolbarSpinnerAdapter(Context applicationContext, List<User> users) {
        this.context = applicationContext;
        this.userList = users;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return userList.size();
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
    public View getView(int pos, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.toolbar_spinner, null);
        final User user = userList.get(pos);

      //  ImageView icon = (ImageView) view.findViewById(R.id.imageView);
        TextView names = (TextView) view.findViewById(R.id.spinner_title);
     //   icon.setImageResource(flags[i]);

        names.setText(user.getFirstname()+" " + user.getLastname());

        if(user.getIsActive()){

        }

       /* names.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("GSONLIST","text click : " + user.getCloudIP());
            }
        });*/

        /*view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatApplication.url = user.getCloudIP();
            }
        });*/

        return view;
    }
}
