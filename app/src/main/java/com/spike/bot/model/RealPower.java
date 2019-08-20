package com.spike.bot.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RealPower implements Serializable {

@SerializedName("power")
@Expose
private Integer power;

public Integer getPower() {
return power;
}

public void setPower(Integer power) {
this.power = power;
}

}