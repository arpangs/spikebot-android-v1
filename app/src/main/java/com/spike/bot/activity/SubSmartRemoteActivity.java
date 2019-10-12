package com.spike.bot.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.spike.bot.R;
import com.spike.bot.adapter.SmartRemoteAdapter;
import com.spike.bot.model.SmartRemoteModel;

import java.util.ArrayList;

import io.socket.client.Socket;

/**
 * Created by Sagar on 3/4/19.
 * Gmail : jethvasagar2@gmail.com
 */
public class SubSmartRemoteActivity extends AppCompatActivity {

    public Toolbar toolbar;
    public RecyclerView recyclerRemoteList;
    public FloatingActionButton floatingActionButton;
    public SmartRemoteAdapter smartRemoteAdapter;
    public ArrayList<SmartRemoteModel> arrayList = new ArrayList<>();

    private Socket mSocket;
    boolean addRoom = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.smart_remote_activity);

        setUiId();
    }

    private void setUiId() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("Smart Remote List");
        recyclerRemoteList = findViewById(R.id.recyclerRemoteList);
        floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setVisibility(View.GONE);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerRemoteList.setLayoutManager(linearLayoutManager);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

}
