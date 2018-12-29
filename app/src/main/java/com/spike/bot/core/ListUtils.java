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

    //hasmMap for selcred expanded room
    public static HashMap<RoomVO,Integer> hashMapRoom = new HashMap<>();


    public static String start_date_filter = "";
    public static String end_date_filter = "";

    /**
     *
     * @return
     */


    public static ArrayList<Filter> filters = new ArrayList<>();

    public static ArrayList<Filter> getFilterList(){
        ArrayList<Filter> filters = new ArrayList<>();

        ArrayList<Filter.SubFilter> switchFilter = new ArrayList<>();
        switchFilter.add(new Filter.SubFilter("Added",false));
        switchFilter.add(new Filter.SubFilter("Edit",false));
        switchFilter.add(new Filter.SubFilter("Deleted",false));
        switchFilter.add(new Filter.SubFilter("On",false));
        switchFilter.add(new Filter.SubFilter("Off",false));


        filters.add(new Filter("Switch",false,false,switchFilter));

        ArrayList<Filter.SubFilter> panelFilter = new ArrayList<>();
        panelFilter.add(new Filter.SubFilter("Added",false));
        panelFilter.add(new Filter.SubFilter("Edit",false));
        panelFilter.add(new Filter.SubFilter("Deleted",false));
        panelFilter.add(new Filter.SubFilter("On",false));
        panelFilter.add(new Filter.SubFilter("Off",false));
        panelFilter.add(new Filter.SubFilter("On (P)",false));
        panelFilter.add(new Filter.SubFilter("Off (P)",false));

        filters.add(new Filter("Panel",false,false,panelFilter));

        ArrayList<Filter.SubFilter> roomFilter = new ArrayList<>();
        roomFilter.add(new Filter.SubFilter("Added",false));
        roomFilter.add(new Filter.SubFilter("Edit",false));
        roomFilter.add(new Filter.SubFilter("Deleted",false));


        filters.add(new Filter("Room",false,false,roomFilter));

        ArrayList<Filter.SubFilter> moodFilter = new ArrayList<>();
        moodFilter.add(new Filter.SubFilter("Added",false));
        moodFilter.add(new Filter.SubFilter("Edit",false));
        moodFilter.add(new Filter.SubFilter("Deleted",false));


        filters.add(new Filter("Mood",false,false,moodFilter));

        ArrayList<Filter.SubFilter> scheFilter = new ArrayList<>();
        scheFilter.add(new Filter.SubFilter("Added",false));
        scheFilter.add(new Filter.SubFilter("Edit",false));
        scheFilter.add(new Filter.SubFilter("Deleted",false));
        scheFilter.add(new Filter.SubFilter("Enable",false));
        scheFilter.add(new Filter.SubFilter("Disable",false));
        scheFilter.add(new Filter.SubFilter("Updated",false));
        scheFilter.add(new Filter.SubFilter("On",false));
        scheFilter.add(new Filter.SubFilter("Off",false));

        filters.add(new Filter("Schedule",false,false,scheFilter));

        ArrayList<Filter.SubFilter> voiceFilter = new ArrayList<>();
        voiceFilter.add(new Filter.SubFilter("On",false));
        voiceFilter.add(new Filter.SubFilter("Off",false));

        filters.add(new Filter("Voice Action",false,false,voiceFilter));

       /* Gson gson = new GsonBuilder().create();
        JsonArray myCustomArray = gson.toJsonTree(filters).getAsJsonArray();

        Log.d("JSONLOG","" + myCustomArray);*/

        return filters;
    }

}
