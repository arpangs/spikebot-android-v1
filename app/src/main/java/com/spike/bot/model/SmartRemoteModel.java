package com.spike.bot.model;

import java.io.Serializable;

/**
 * Created by Sagar on 2/4/19.
 * Gmail : jethvasagar2@gmail.com
 */
public class SmartRemoteModel implements Serializable{

    String smart_remote_name;

    public String getSmart_remote_name() {
        return smart_remote_name;
    }

    public void setSmart_remote_name(String smart_remote_name) {
        this.smart_remote_name = smart_remote_name;
    }

    public String getSmart_remote_module_id() {
        return smart_remote_module_id;
    }

    public void setSmart_remote_module_id(String smart_remote_module_id) {
        this.smart_remote_module_id = smart_remote_module_id;
    }

    String smart_remote_module_id;
}
