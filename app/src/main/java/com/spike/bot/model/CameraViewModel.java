package com.spike.bot.model;

import java.io.Serializable;

/**
 * Created by Sagar on 26/11/18.
 * Gmail : jethvasagar2@gmail.com
 */
public class CameraViewModel implements Serializable{

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIsActivite() {
        return isActivite;
    }

    public void setIsActivite(String isActivite) {
        this.isActivite = isActivite;
    }

    String id,name,isActivite;
}
