package com.spike.bot.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class NotificationList implements Serializable{

@SerializedName("id")
@Expose
private Integer id;
@SerializedName("activity_action")
@Expose
private String activityAction;
@SerializedName("activity_type")
@Expose
private String activityType;
@SerializedName("activity_description")
@Expose
private String activityDescription;
@SerializedName("activity_time")
@Expose
private String activityTime;

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    @SerializedName("image_url")
@Expose
private String image_url;

public Integer getId() {
return id;
}

public void setId(Integer id) {
this.id = id;
}

public String getActivityAction() {
return activityAction;
}

public void setActivityAction(String activityAction) {
this.activityAction = activityAction;
}

public String getActivityType() {
return activityType;
}

public void setActivityType(String activityType) {
this.activityType = activityType;
}

public String getActivityDescription() {
return activityDescription;
}

public void setActivityDescription(String activityDescription) {
this.activityDescription = activityDescription;
}

public String getActivityTime() {
return activityTime;
}

public void setActivityTime(String activityTime) {
this.activityTime = activityTime;
}

}