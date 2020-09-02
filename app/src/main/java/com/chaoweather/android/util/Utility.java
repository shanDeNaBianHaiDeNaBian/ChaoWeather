package com.chaoweather.android.util;

import android.text.TextUtils;
import android.util.Log;

import com.chaoweather.android.db.City;
import com.chaoweather.android.db.County;
import com.chaoweather.android.db.Province;
import com.chaoweather.android.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Utility {

    private static final String TAG = "aa";

    /**
     * 解析和处理服务器返回的省数据（解析后保存到数据库）
     *
     * @param response 服务器返回的省数据。格式：[[101090513,lunan,路南,CN,China,中国,hebei,河北省,
     *                 tangshan,唐山市,39.61516,118.21082,130202],....]
     * @return
     */
    public static boolean handleProvinceResponse(List<String[]> response) {
        // Log.d(TAG, "handleProvinceResponse: response: " + response);
        if (response.size() > 0) {
            for (int i = 0; i < response.size(); i++) {
                // Log.d(TAG, "handleProvinceResponse: 省：" + response.get(i)[7]);
                Province province = new Province();
                province.setLocationName(response.get(i)[7]);
                province.setAdm1En(response.get(i)[6]);
                province.setAdm1(response.get(i)[7]);
                province.setAdm2En(response.get(i)[8]);
                province.setAdm2(response.get(i)[9]);
                province.save();
            }
            return true;
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的市数据
     *
     * @param response 服务器返回的市数据。格式：[[101090513,lunan,路南,CN,China,中国,hebei,河北省,
     *                 tangshan,唐山市,39.61516,118.21082,130202],....]
     * @return
     */
    public static boolean handleCityResponse(List<String[]> response) {
        if (response.size() > 0) {
            for (int i = 0; i < response.size(); i++) {
                // Log.d(TAG, "handleCityResponse: 市：" + response.get(i)[2]);
                City city = new City();
                city.setLocationId(response.get(i)[0]);
                city.setLocationNameEn(response.get(i)[1]);
                city.setLocationName(response.get(i)[2]);
                city.setAdm1En(response.get(i)[6]);
                city.setProvinceAdm1En(response.get(i)[6]);
                city.setAdm1(response.get(i)[7]);
                city.setAdm2En(response.get(i)[8]);
                city.setAdm2(response.get(i)[9]);
                city.setLatitude(response.get(i)[10]);
                city.setLongitude(response.get(i)[11]);
                city.setAdCode(response.get(i)[12]);
                city.save();
            }
            return true;
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的县数据
     *
     * @param response 服务器返回的县数据。格式：[[101090513,lunan,路南,CN,China,中国,hebei,河北省,
     *                 tangshan,唐山市,39.61516,118.21082,130202],....]
     * @return
     */
    public static boolean handleCountyResponse(List<String[]> response) {
        if (response.size() > 0) {
            for (int i = 0; i < response.size(); i++) {
                // Log.d(TAG, "handleCityResponse: 县/区：" + response.get(i)[2]);
                County county = new County();
                county.setLocationId(response.get(i)[0]);
                county.setLocationNameEn(response.get(i)[1]);
                county.setLocationName(response.get(i)[2]);
                county.setAdm1En(response.get(i)[6]);
                county.setCityAdm1En(response.get(i)[8]);
                county.setAdm1(response.get(i)[7]);
                county.setAdm2En(response.get(i)[8]);
                county.setAdm2(response.get(i)[9]);
                county.setLatitude(response.get(i)[10]);
                county.setLongitude(response.get(i)[11]);
                county.setAdCode(response.get(i)[12]);
                county.save();
            }
            return true;
        }
        return false;
    }

    /**
     * 把服务器获取的JSON数据
     * {"HeWeather":[{"basic":{"cid":"CN101090502","location":"丰南","parent_city":"唐山",
     * "admin_area":"河北","cnty":"中国","lat":"36.67580795","lon":"117.00092316","tz":"+8.00",
     * "city":"丰南","id":"CN101090502","update":{"loc":"2020-08-26 15:52","utc":"2020-08-26 07:52"
     * }},"update":{"loc":"2020-08-26 15:52","utc":"2020-08-26 07:52"},"status":"ok","now":{
     * "cloud":"10","cond_code":"104","cond_txt":"阴","fl":"8","hum":"60","pcpn":"0.0",
     * "pres":"1015","tmp":"12","vis":"16","wind_deg":"261","wind_dir":"西风","wind_sc":"3",
     * "wind_spd":"17","cond":{"code":"104","txt":"阴"}},"daily_forecast":[{"date":"2020-08-27",
     * "cond":{"txt_d":"阴"},"tmp":{"max":"17","min":"6"}},{"date":"2020-08-28","cond":{"txt_d
     * ":"晴"},"tmp":{"max":"14","min":"3"}},{"date":"2020-08-29","cond":{"txt_d":"晴"},"tmp":{"max
     * ":"15","min":"6"}},{"date":"2020-08-30","cond":{"txt_d":"多云"},"tmp":{"max":"20","min":"6"}
     * },{"date":"2020-08-31","cond":{"txt_d":"晴"},"tmp":{"max":"18","min":"5"}},{"date":"2020-09
     * -01","cond":{"txt_d":"多云"},"tmp":{"max":"19","min":"7"}}],"aqi":{"city":{"aqi":"112",
     * "pm25":"60","qlty":"轻度污染"}},"suggestion":{"comf":{"type":"comf","brf":"舒适",
     * "txt":"白天不太热也不太冷，风力不大，相信您在这样的天气条件下，应会感到比较清爽和舒适。"},"sport":{"type":"sport","brf":"较适宜",
     * "txt":"阴天，较适宜进行各种户内外运动。"},"cw":{"type":"cw","brf":"较适宜",
     * "txt":"较适宜洗车，未来一天无雨，风力较小，擦洗一新的汽车至少能保持一天。"}},"msg
     * ":"所有天气数据均为模拟数据，仅用作学习目的使用，请勿当作真实的天气预报软件来使用。"}]}
     * 解析成 Weather 实体类
     *
     * @param response
     * @return
     */
    public static Weather handleWeatherResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            String weatherContent = jsonObject.toString();
            Log.d(TAG, "handleWeatherResponse: " + weatherContent);
            //返回解析后的 Weather 实体对象
            return new Gson().fromJson(weatherContent, Weather.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
