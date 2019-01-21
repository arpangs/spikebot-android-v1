package com.spike.bot.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Sagar on 3/4/18.
 * Gmail : jethvasagar2@gmail.com
 */

public class Filter implements Serializable{

    private String name;
    private boolean isChecked;
    private boolean isExpanded;
    private ArrayList<SubFilter> subFilters = new ArrayList<>();

    public Filter(){
        name = "";
    }

    public Filter(String name, boolean isChecked,boolean isExpanded,ArrayList<SubFilter> subFilterArrayList){
        this.name = name;
        this.isChecked = isChecked;
        this.isExpanded = isExpanded;
        this.subFilters = subFilterArrayList;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public ArrayList<SubFilter> getSubFilters() {
        return subFilters;
    }

    public void setSubFilters(ArrayList<SubFilter> subFilters) {
        this.subFilters = subFilters;
    }

    public static class SubFilter implements Serializable{

        private String name;
        private boolean isChecked;

        public SubFilter(){
            name = "";
        }
        public SubFilter(String name, boolean isChecked){
            this.name = name;
            this.isChecked = isChecked;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isChecked() {
            return isChecked;
        }

        public void setChecked(boolean checked) {
            isChecked = checked;
        }
    }
}
