package com.chaoweather.android.gson;

import com.google.gson.Gson;

import java.util.List;

public class Basic {
    /**
     * code : 200
     * updateTime : 2020-06-30T22:00+08:00
     * fxLink : http://hfx.link/2ax1
     * now : {"obsTime":"2020-06-30T21:40+08:00","temp":"24","feelsLike":"26","icon":"101",
     * "text":"多云","wind360":"123","windDir":"东南风","windScale":"1","windSpeed":"3",
     * "humidity":"72","precip":"0.0","pressure":"1003","vis":"16","cloud":"10","dew":"21"}
     * refer : {"sources":["Weather China"],"license":["commercial license"]}
     */

    private String code = "000";
    private String updateTime;
    private String fxLink;
    private NowBean now;
    private ReferBean refer;

    public static Basic objectFromData(String str) {

        return new Gson().fromJson(str, Basic.class);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getFxLink() {
        return fxLink;
    }

    public void setFxLink(String fxLink) {
        this.fxLink = fxLink;
    }

    public NowBean getNow() {
        return now;
    }

    public void setNow(NowBean now) {
        this.now = now;
    }

    public ReferBean getRefer() {
        return refer;
    }

    public void setRefer(ReferBean refer) {
        this.refer = refer;
    }

    public static class NowBean {
        /**
         * obsTime :        实况观测时间 	2013-12-30T01:45+08:00
         * temp :           实况温度，默认单位：摄氏度 	21
         * feelsLike :      实况体感温度，默认单位：摄氏度 	23
         * icon : 101       当前天气状况和图标的代码，图标可通过天气状况和图标下载 	100
         * text :           多云    实况天气状况的文字描述，包括阴晴雨雪等天气状态的描述 	晴
         * wind360 : 123    实况风向360角度 	305
         * windDir :        实况风向 	西北
         * windScale :      实况风力等级 	3
         * windSpeed :      实况风速，公里/小时 	15
         * humidity :       实况相对湿度，百分比数值 	40
         * precip :         实况降水量，默认单位：毫米 	1.2
         * pressure :       实况大气压强，默认单位：百帕 	1020
         * vis :            实况能见度，默认单位：公里 	10
         * cloud :          实况云量，百分比数值 	23
         * dew :            实况露点温度 	12
         */

        private String obsTime;
        private String temp;
        private String feelsLike;
        private String icon;
        private String text;
        private String wind360;
        private String windDir;
        private String windScale;
        private String windSpeed;
        private String humidity;
        private String precip;
        private String pressure;
        private String vis;
        private String cloud;
        private String dew;

        public static NowBean objectFromData(String str) {

            return new Gson().fromJson(str, NowBean.class);
        }

        public String getObsTime() {
            return obsTime;
        }

        public void setObsTime(String obsTime) {
            this.obsTime = obsTime;
        }

        public String getTemp() {
            return temp;
        }

        public void setTemp(String temp) {
            this.temp = temp;
        }

        public String getFeelsLike() {
            return feelsLike;
        }

        public void setFeelsLike(String feelsLike) {
            this.feelsLike = feelsLike;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getWind360() {
            return wind360;
        }

        public void setWind360(String wind360) {
            this.wind360 = wind360;
        }

        public String getWindDir() {
            return windDir;
        }

        public void setWindDir(String windDir) {
            this.windDir = windDir;
        }

        public String getWindScale() {
            return windScale;
        }

        public void setWindScale(String windScale) {
            this.windScale = windScale;
        }

        public String getWindSpeed() {
            return windSpeed;
        }

        public void setWindSpeed(String windSpeed) {
            this.windSpeed = windSpeed;
        }

        public String getHumidity() {
            return humidity;
        }

        public void setHumidity(String humidity) {
            this.humidity = humidity;
        }

        public String getPrecip() {
            return precip;
        }

        public void setPrecip(String precip) {
            this.precip = precip;
        }

        public String getPressure() {
            return pressure;
        }

        public void setPressure(String pressure) {
            this.pressure = pressure;
        }

        public String getVis() {
            return vis;
        }

        public void setVis(String vis) {
            this.vis = vis;
        }

        public String getCloud() {
            return cloud;
        }

        public void setCloud(String cloud) {
            this.cloud = cloud;
        }

        public String getDew() {
            return dew;
        }

        public void setDew(String dew) {
            this.dew = dew;
        }
    }

    public static class ReferBean {
        private List<String> sources;
        private List<String> license;

        public static ReferBean objectFromData(String str) {

            return new Gson().fromJson(str, ReferBean.class);
        }

        public List<String> getSources() {
            return sources;
        }

        public void setSources(List<String> sources) {
            this.sources = sources;
        }

        public List<String> getLicense() {
            return license;
        }

        public void setLicense(List<String> license) {
            this.license = license;
        }
    }
    //用 gson 中的 SerializedName 接口映射返回值中的键名为自己定义的字段名
    // @SerializedName("city")

}
