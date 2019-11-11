package com.spike.bot.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Sagar on 29/6/19.
 * Gmail : vipul patel
 */
public class DataHeavyModel implements Serializable {

    @SerializedName("real_power")
    @Expose
    private RealPower realPower;
    @SerializedName("data")
    @Expose
    private List<GraphDatum> graphData = null;

    public RealPower getRealPower() {
        return realPower;
    }

    public void setRealPower(RealPower realPower) {
        this.realPower = realPower;
    }

    public List<GraphDatum> getGraphData() {
        return graphData;
    }

    public void setGraphData(List<GraphDatum> graphData) {
        this.graphData = graphData;
    }
}
