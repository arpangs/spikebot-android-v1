package com.spike.bot.model;

import java.io.Serializable;

/**
 * Created by Sagar on 19/9/19.
 * Gmail : vipul patel
 */
public class RepeaterModel implements Serializable {


    //  "repeator_module_id": "B04A1D1A004B1200",
    //      "repeator_name": "t",
    //      "is_active": 1
    //    }


    public String getRepeator_module_id() {
        return repeator_module_id;
    }

    public void setRepeator_module_id(String repeator_module_id) {
        this.repeator_module_id = repeator_module_id;
    }

    public String getRepeator_name() {
        return repeator_name;
    }

    public void setRepeator_name(String repeator_name) {
        this.repeator_name = repeator_name;
    }

    public String getIs_active() {
        return is_active;
    }

    public void setIs_active(String is_active) {
        this.is_active = is_active;
    }

    String repeator_module_id,repeator_name,is_active;
}
