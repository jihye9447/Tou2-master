
package com.finaltou.Weather;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Pressure {

    @SerializedName("surface")
    @Expose
    private String surface;
    @SerializedName("seaLevel")
    @Expose
    private String seaLevel;

    public String getSurface() {
        return surface;
    }

    public void setSurface(String surface) {
        this.surface = surface;
    }

    public String getSeaLevel() {
        return seaLevel;
    }

    public void setSeaLevel(String seaLevel) {
        this.seaLevel = seaLevel;
    }

}
