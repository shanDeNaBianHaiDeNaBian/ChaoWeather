package com.chaoweather.android.gson;

import com.google.gson.Gson;

import java.util.List;

// 未来6天的天气 JSON 数据解析
public class Forecast {

    /**
     * code : 200
     * updateTime : 2020-06-14T16:57+08:00
     * fxLink : https://www.heweather.com/weather/beijing-101010100.html
     * daily : [{"fxDate":"2020-06-14","sunrise":"04:45","sunset":"19:44","moonrise":"01:05",
     * "moonset":"12:53","tempMax":"35","tempMin":"22","iconDay":"100","textDay":"晴",
     * "iconNight":"150","textNight":"晴","wind360Day":"358","windDirDay":"北风",
     * "windScaleDay":"1-2","windSpeedDay":"8","wind360Night":"234","windDirNight":"西南风",
     * "windScaleNight":"1-2","windSpeedNight":"6","humidity":"22","precip":"0.0",
     * "pressure":"1001","vis":"25","uvIndex":"11"}]
     */

    public String code = "000";
    public String updateTime;
    public String fxLink;
    public List<DailyBean> daily;

    public static Forecast objectFromData(String str) {

        return new Gson().fromJson(str, Forecast.class);
    }

    public static class DailyBean {
        /**
         * fxDate : 2020-06-14
         * sunrise : 04:45
         * sunset : 19:44
         * moonrise : 01:05
         * moonset : 12:53
         * tempMax : 35
         * tempMin : 22
         * iconDay : 100
         * textDay : 晴
         * iconNight : 150
         * textNight : 晴
         * wind360Day : 358
         * windDirDay : 北风
         * windScaleDay : 1-2
         * windSpeedDay : 8
         * wind360Night : 234
         * windDirNight : 西南风
         * windScaleNight : 1-2
         * windSpeedNight : 6
         * humidity : 22
         * precip : 0.0
         * pressure : 1001
         * vis : 25
         * uvIndex : 11
         */

        public String fxDate;
        public String sunrise;
        public String sunset;
        public String moonrise;
        public String moonset;
        public String tempMax;
        public String tempMin;
        public String iconDay;
        public String textDay;
        public String iconNight;
        public String textNight;
        public String wind360Day;
        public String windDirDay;
        public String windScaleDay;
        public String windSpeedDay;
        public String wind360Night;
        public String windDirNight;
        public String windScaleNight;
        public String windSpeedNight;
        public String humidity;
        public String precip;
        public String pressure;
        public String vis;
        public String uvIndex;

        public static DailyBean objectFromData(String str) {

            return new Gson().fromJson(str, DailyBean.class);
        }
    }
}
