package com.spike.bot.core;

import com.spike.bot.model.Filter;
import com.spike.bot.model.MoodVO;
import com.spike.bot.model.RoomVO;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Sagar on 14/2/18.
 * Gmail : jethvasagar2@gmail.com
 */

public class ListUtils {

    public static MoodVO section = new MoodVO();
    public static RoomVO sectionRoom = new RoomVO();
    //RoomVO section;

    //for multiple
    public static ArrayList<RoomVO> arrayListMood = new ArrayList<>();
    public static ArrayList<RoomVO> arrayListRoom = new ArrayList<>();

    public static String start_date_filter = "";
    public static String end_date_filter = "";

}
