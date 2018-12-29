package com.spike.bot.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by kaushal on 7/3/18.
 */

public class ScreenshotUtils {

    /*  Method which will return Bitmap after taking screenshot. We have to pass the view which we want to take screenshot.  */
    public static Bitmap getScreenShot(View view) {

        View screenView = view;
        screenView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache(true));
        screenView.setDrawingCacheEnabled(false);
        return bitmap;
    }

    public static Bitmap loadBitmapFromView(Context context,View v) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        v.measure(View.MeasureSpec.makeMeasureSpec(dm.widthPixels,
                View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(dm.heightPixels,
                        View.MeasureSpec.EXACTLY));
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        Bitmap returnedBitmap =
                Bitmap.createBitmap(v.getMeasuredWidth(),
                        v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(returnedBitmap);
        v.draw(c);

        return returnedBitmap;
    }


    /*  Create Directory where screenshot will save for sharing screenshot  */
    public static File getMainDirectoryName(Context context) {
        //Here we will use getExternalFilesDir and inside that we will make our Demo folder
        //benefit of getExternalFilesDir is that whenever the app uninstalls the images will get deleted automatically.
//        File mainDir = new File(
//                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Demo");
        File mainDir = new File(Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_PICTURES), "Demo");

        //If File is not present create directory
        if (!mainDir.exists()) {
            if (mainDir.mkdir())
                Log.e("Create Directory", "Main Directory Created : " + mainDir);
        }
        return mainDir;
    }

    /*  Store taken screenshot into above created path  */
    public static File store(Bitmap bm, String fileName, File saveFilePath) {
        Log.d("Create Directory", "Main Directory store : " + saveFilePath.getPath()  );
        Log.d("Create Directory", "Main Directory store : " + fileName  );
        File dir = new File(saveFilePath.getAbsolutePath());
        if (!dir.exists())
            dir.mkdirs();
        File file = new File(saveFilePath.getAbsolutePath(), fileName);
        try {
            FileOutputStream fOut = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
            fOut.flush();
            fOut.close();
            Log.d("Create Directory", "Main Directory store close : "   );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }
}
