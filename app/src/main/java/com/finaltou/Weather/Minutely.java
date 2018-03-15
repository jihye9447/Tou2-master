
package com.finaltou.Weather;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Minutely {

    @SerializedName("pressure")
    @Expose
    private Pressure pressure;
    @SerializedName("station")
    @Expose
    private Station station;
    @SerializedName("wind")
    @Expose
    private Wind wind;
    @SerializedName("precipitation")
    @Expose
    private Precipitation precipitation;
    @SerializedName("sky")
    @Expose
    private Sky sky;
    @SerializedName("rain")
    @Expose
    private Rain rain;
    @SerializedName("temperature")
    @Expose
    private Temperature temperature;
    @SerializedName("humidity")
    @Expose
    private String humidity;
    @SerializedName("lightning")
    @Expose
    private String lightning;
    @SerializedName("timeObservation")
    @Expose
    private String timeObservation;

    public Pressure getPressure() {
        return pressure;
    }

    public void setPressure(Pressure pressure) {
        this.pressure = pressure;
    }

    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
    }

    public Wind getWind() {
        return wind;
    }

    public void setWind(Wind wind) {
        this.wind = wind;
    }

    public Precipitation getPrecipitation() {
        return precipitation;
    }

    public void setPrecipitation(Precipitation precipitation) {
        this.precipitation = precipitation;
    }

    public Sky getSky() {
        return sky;
    }

    public void setSky(Sky sky) {
        this.sky = sky;
    }

    public Rain getRain() {
        return rain;
    }

    public void setRain(Rain rain) {
        this.rain = rain;
    }

    public Temperature getTemperature() {
        return temperature;
    }

    public void setTemperature(Temperature temperature) {
        this.temperature = temperature;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getLightning() {
        return lightning;
    }

    public void setLightning(String lightning) {
        this.lightning = lightning;
    }

    public String getTimeObservation() {
        return timeObservation;
    }

    public void setTimeObservation(String timeObservation) {
        this.timeObservation = timeObservation;
    }

}
