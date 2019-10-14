package com.spike.bot.activity.ir.blaster;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.kp.core.ActivityHelper;
import com.spike.bot.R;
import com.spike.bot.adapter.SearchAdapter;
import com.spike.bot.adapter.SearchClick;
import com.spike.bot.model.DataSearch;
import com.spike.bot.model.DeviceBrandRemoteList;

import java.util.ArrayList;

/**
 * Created by Sagar on 22/1/19.
 * Gmail : jethvasagar2@gmail.com
 */
public class SearchActivity extends AppCompatActivity implements SearchClick {

    public EditText edSearch;
    public RecyclerView recyclerSearch;
    public String mBrandId = "";
    public DataSearch arrayList;

    SearchAdapter searchAdapter;
    ArrayList<DeviceBrandRemoteList> filterdNames = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mBrandId = getIntent().getStringExtra("mBrandId");
        arrayList = IRRemoteBrandListActivity.arrayList;

        Toolbar toolbar =  findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Search");

        init();
    }

    private void init() {
        recyclerSearch = findViewById(R.id.recyclerSearch);
        edSearch = findViewById(R.id.edSearch);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerSearch.setLayoutManager(linearLayoutManager);

        edSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //after the change calling the method and passing the search input
                filter(editable.toString());
            }
        });

        filterdNames.clear();
        for (DeviceBrandRemoteList s : arrayList.getDeviceBrandRemoteList()) {
            filterdNames.add(s);
        }
        setAdapter(filterdNames);

    }

    private void filter(String search) {
        filterdNames.clear();
        if (search.length() > 0) {

            for (DeviceBrandRemoteList s : arrayList.getDeviceBrandRemoteList()) {
                if (s.getModelNumber().toLowerCase().contains(search.toLowerCase())) {
                    filterdNames.add(s);
                }
            }
            setAdapter(filterdNames);
        } else {
            for (DeviceBrandRemoteList s : arrayList.getDeviceBrandRemoteList()) {
                filterdNames.add(s);
            }
            setAdapter(filterdNames);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        ActivityHelper.hideKeyboard(this);
        onBackPressed();
        return true;
    }

    private void setAdapter(ArrayList<DeviceBrandRemoteList> filterdNames) {

        if (filterdNames.size() > 0) {
            recyclerSearch.setVisibility(View.VISIBLE);
            searchAdapter = new SearchAdapter(this, filterdNames, this);
            recyclerSearch.setAdapter(searchAdapter);
            searchAdapter.notifyDataSetChanged();
        } else {
            recyclerSearch.setVisibility(View.GONE);
        }
    }

    @Override
    public void searchItemClick(DeviceBrandRemoteList deviceBrandRemoteList) {
        if (deviceBrandRemoteList.getIrCode() != null) {
            ActivityHelper.hideKeyboard(SearchActivity.this);
            Intent intent = new Intent();
            intent.putExtra("onOffValue", deviceBrandRemoteList.getIrCode());
            intent.putExtra("brand_name", deviceBrandRemoteList.getBrandName());
            intent.putExtra("model_number", deviceBrandRemoteList.getModelNumber());
            intent.putExtra("remote_codeset_id", "" + deviceBrandRemoteList.getremote_codeset_id());
            setResult(RESULT_OK, intent);
            finish();

        }

    }
}
