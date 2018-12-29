package com.spike.cameraapp;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.kp.core.dialog.ConfirmDialog;

import org.json.JSONArray;
import org.json.JSONException;

public class MainActivity extends AppCompatActivity implements CameraAdapter.ItemClick{
    RecyclerView camera_list;
    CameraAdapter cameraAdapter;
    JSONArray array = new JSONArray();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        camera_list  = (RecyclerView) findViewById(R.id.camera_list  );
        camera_list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        array = Common.getCameraList(this);
        Log.d("array","array " +  array.length());
        cameraAdapter = new CameraAdapter(this,array);
        camera_list.setAdapter(cameraAdapter);
        cameraAdapter.setItemClick(this);
    }
    public void startVideo(String url,String name){

        Intent intent = new Intent(this,VideoVLC2Activity.class);
        intent.putExtra("videoUrl",url);
        intent.putExtra("name",name);

        startActivity(intent);
        //

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_room_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            Log.d("","action_add " );
            AddDialog addDialog = new AddDialog(this,-1, "", "", new ICallback() {
                @Override
                public void onSuccess(String str) {
                    if(str.equalsIgnoreCase("yes")){
                        array = Common.getCameraList(MainActivity.this);
                        cameraAdapter.setCameraArray(array);
                        cameraAdapter.notifyDataSetChanged();

                    }
                }
            });
            addDialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void deleteClick(final int position) {
        ConfirmDialog newFragment = new ConfirmDialog("Yes","No" ,"Confirm", "Are you sure you want to Delete ?",new ConfirmDialog.IDialogCallback() {
            @Override
            public void onConfirmDialogYesClick() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    array.remove(position);
                    Common.saveCamera(MainActivity.this,array);
                    cameraAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onConfirmDialogNoClick() {

//                      Toast.makeText(activity, " Saved Successfully. " ,Toast.LENGTH_SHORT).show();
            }
        });
        newFragment.show(getFragmentManager(), "dialog");
    }

    @Override
    public void editClick(int position) {

        try {
            String url = array.getJSONObject(position).getString("url");
            String name = array.getJSONObject(position).getString("name");

            AddDialog addDialog = new AddDialog(this,position, url, name, new ICallback() {
                @Override
                public void onSuccess(String str) {
                    if(str.equalsIgnoreCase("yes")){
                        array = Common.getCameraList(MainActivity.this);
                        cameraAdapter.setCameraArray(array);
                        cameraAdapter.notifyDataSetChanged();

                    }
                }
            });
            addDialog.show();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cameraClick(int position) {

        try {
            String url = array.getJSONObject(position).getString("url");
            String name = array.getJSONObject(position).getString("name");
            startVideo(url,name);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
