package com.spike.bot.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GraphDatum implements Serializable {

@SerializedName("energy")
@Expose
private Integer energy;
@SerializedName("day")
@Expose
private String day;

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    @SerializedName("month")
@Expose
private String month;

public Integer getEnergy() {
return energy;
}

public void setEnergy(Integer energy) {
this.energy = energy;
}

public String getDay() {
return day;
}

public void setDay(String day) {
this.day = day;
}

}