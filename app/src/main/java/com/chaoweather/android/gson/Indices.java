package com.chaoweather.android.gson;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Indices {

    /**
     * code : 200
     * updateTime : 2020-06-21T12:57+08:00
     * daily : [{"date":"2020-06-21","type":"1","name":"运动指数","level":"2","category":"较适宜",
     * "txt":"天气较好，但因气温较高且风力较强，请适当降低运动强度并注意户外防风。"}]
     */

    public String code = "000";
    public String updateTime;
    public List<DailyBean> daily;
    // public List<IndicesDailyBean> daily;

    public static class DailyBean {
        /**
         * date : 2020-06-21
         * type : 1
         * name : 运动指数
         * level : 2
         * category : 较适宜
         * txt : 天气较好，但因气温较高且风力较强，请适当降低运动强度并注意户外防风。
         */

        public String date;
        public String type;
        public String name;
        public String level;
        public String category;
        @SerializedName("text")
        public String txt;

        public static DailyBean objectFromData(String str) {

            return new Gson().fromJson(str, DailyBean.class);
        }
    }
}
