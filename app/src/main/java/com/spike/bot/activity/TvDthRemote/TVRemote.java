package com.spike.bot.activity.TvDthRemote;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.spike.bot.R;

public class TVRemote extends AppCompatActivity {

    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv_remote);

        bindView();
    }

    private void bindView() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
       // toolbar.setTitle(room_name);
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_room_edit, menu);
        MenuItem menuAdd = menu.findItem(R.id.action_add);
        MenuItem actionEdit = menu.findItem(R.id.actionEdit);
        MenuItem action_save = menu.findItem(R.id.action_save);
        menuAdd.setVisible(false);
        action_save.setVisible(false);
        menu.findItem(R.id.actionEdit).setIcon(resizeImage(R.drawable.edit_white_new, 190, 190));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.actionEdit) {
          //  showBottomSheetDialog(room);

        }
        return super.onOptionsItemSelected(item);
    }

    private Drawable resizeImage(int resId, int w, int h) {
        // load the origial Bitmap
        Bitmap BitmapOrg = BitmapFactory.decodeResource(getResources(), resId);
        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();
        int newWidth = w;
        int newHeight = h;
        // calculate the scale
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width, height, matrix, true);
        return new BitmapDrawable(resizedBitmap);
    }
}
