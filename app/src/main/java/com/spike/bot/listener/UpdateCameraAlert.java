package com.spike.bot.listener;

import androidx.appcompat.widget.SwitchCompat;

import com.spike.bot.model.CameraAlertList;

/**
 * Created by Sagar on 27/11/18.
 * Gmail : jethvasagar2@gmail.com
 */
public interface UpdateCameraAlert {
    public void updatecameraALert(String delete, CameraAlertList cameraAlertList, SwitchCompat switchAlert, int position);
}
