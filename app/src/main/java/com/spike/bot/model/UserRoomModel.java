package com.spike.bot.model;

import java.io.Serializable;

/**
 * Created by Sagar on 22/3/19.
 * Gmail : jethvasagar2@gmail.com
 */
public class UserRoomModel implements Serializable {

    String room_name;

    public String getRoom_name() {
        return room_name;
    }

    public void setRoom_name(String room_name) {
        this.room_name = room_name;
    }

    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }

    String room_id;
}
