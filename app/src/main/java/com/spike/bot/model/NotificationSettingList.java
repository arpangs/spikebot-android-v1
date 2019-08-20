package com.spike.bot.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class NotificationSettingList implements Serializable {

@SerializedName("id")
@Expose
private Integer id;
@SerializedName("title")
@Expose
private String title;

    public String getSensor_type() {
        return sensor_type;
    }

    public void setSensor_type(String sensor_type) {
        this.sensor_type = sensor_type;
    }

    @SerializedName("sensor_type")
@Expose
private String sensor_type;
@SerializedName("value")
@Expose
private Integer value;

public Integer getId() {
return id;
}

public void setId(Integer id) {
this.id = id;
}

public String getTitle() {
return title;
}

public void setTitle(String title) {
this.title = title;
}

public Integer getValue() {
return value;
}

public void setValue(Integer value) {
this.value = value;
}

}