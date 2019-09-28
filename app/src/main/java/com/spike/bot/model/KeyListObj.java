package com.spike.bot.model;

import java.io.Serializable;
import java.util.ArrayList;

public class KeyListObj implements Serializable {
    int total;
    int pages;
    int pageNo;
    int pageSize;

    ArrayList<KeyObj> list;

    public ArrayList<KeyObj> getList() {
        return list;
    }

}