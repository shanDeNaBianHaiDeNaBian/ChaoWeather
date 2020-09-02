package com.chaoweather.android.gson;

import com.google.gson.Gson;

import java.util.List;

/*
空气质量 JSON 解析
aqi: {
    aqi: "33"
    pm25: "33"
}
 */
public class AQI {

    /**
     * code : 200   API状态码，具体含义请参考状态码 	200
     * updateTime : 当前API的最近更新时间 	2013-12-30T01:45+08:00
     * fxLink : 该城市的空气质量自适应网页，可嵌入网站或应用 	http://hfx.link/ae45 https://www.heweather
     * .com/air/beijing-101010100.html
     * now : {"pubTime":"2020-06-21T11:00+08:00","aqi":"82","category":"良","primary":"O3",
     * "pm10":"82","pm2p5":"39","no2":"16","so2":"5","co":"0.7","o3":"185"}
     */

    public String code = "000";
    public String updateTime;
    public String fxLink;
    public NowBean now;

    public static AQI objectFromData(String str) {
        return new Gson().fromJson(str, AQI.class);
    }

    public static class NowBean {
        /**
         * pubTime :        实时空气质量数据发布时间 	2013-12-30T01:45+08:00
         * aqi :            实时空气质量指数 	74
         * category :       实时空气质量指数级别 	良
         * primary :        实时空气质量的主要污染物，空气质量为优时，返回值为NA 	pm2.5
         * pm10 :           实时 pm10 	78
         * pm2p5 :          实时 pm2.5 	66
         * no2 :            实时 二氧化氮 	40
         * so2 :            实时 二氧化硫 	30
         * co :             实时 一氧化碳 	33
         * o3 :             实时 臭氧 	20
         */

        public String pubTime;
        public String aqi;
        public String category;
        public String primary;
        public String pm10;
        public String pm2p5;
        public String no2;
        public String so2;
        public String co;
        public String o3;

        public static NowBean objectFromData(String str) {

            return new Gson().fromJson(str, NowBean.class);
        }

    }
}
