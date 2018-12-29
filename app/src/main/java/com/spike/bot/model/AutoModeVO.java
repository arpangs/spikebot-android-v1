package com.spike.bot.model;

import java.io.Serializable;

/**
 * Created by kaushal on 22/12/17.
 */

public class AutoModeVO implements Serializable {
    private String auto_on_off_id;
    private String auto_on_off_timer;
    private int auto_on_off_status;
    private int is_active;
    boolean updated;
    public AutoModeVO(String auto_on_off_id){
        this.auto_on_off_id = auto_on_off_id;
    }

    public AutoModeVO() {

    }

    public String getAuto_on_off_id() {
        return auto_on_off_id;
    }

    public void setAuto_on_off_id(String auto_on_off_id) {
        this.auto_on_off_id = auto_on_off_id;
    }

    public String getAuto_on_off_timer() {
        return auto_on_off_timer;
    }

    public void setAuto_on_off_timer(String auto_on_off_timer) {
        this.auto_on_off_timer = auto_on_off_timer;
    }

    public int getAuto_on_off_status() {
        return auto_on_off_status;
    }

    public void setAuto_on_off_status(int auto_on_off_status) {
        this.auto_on_off_status = auto_on_off_status;
    }

    public int getIs_active() {
        return is_active;
    }

    public void setIs_active(int is_active) {
        this.is_active = is_active;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof AutoModeVO)) {
            return false;
        }
        final AutoModeVO other = (AutoModeVO) obj;
      /*  if ((this.moduleId == null) ? (other.moduleId != null) : !this.moduleId.equals(other.moduleId)) {
            return false;
        }*/
        if (this.auto_on_off_timer.equalsIgnoreCase(other.auto_on_off_timer ) &&  auto_on_off_status == other.auto_on_off_status) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (this.auto_on_off_timer != null ? this.auto_on_off_timer.hashCode() : 0);
        return hash;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }
}
