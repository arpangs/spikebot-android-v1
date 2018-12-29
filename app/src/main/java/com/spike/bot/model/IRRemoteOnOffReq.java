package com.spike.bot.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Sagar on 2/8/18.
 * Gmail : jethvasagar2@gmail.com
 */
public class IRRemoteOnOffReq {
    @SerializedName("ir_blaster_module_id")
    private String ir_blaster_module_id;
    @SerializedName("codeset_id")
    private String codeset_id;
    @SerializedName("mode")
    private String mode;
    @SerializedName("phone_id")
    private String phoneId;
    @SerializedName("phone_type")
    private String phoneType;

    public IRRemoteOnOffReq(String ir_blaster_module_id, String codeset_id, String mode, String phoneId, String phoneType) {
        this.ir_blaster_module_id = ir_blaster_module_id;
        this.codeset_id = codeset_id;
        this.mode = mode;
        this.phoneId = phoneId;
        this.phoneType = phoneType;
    }
}
