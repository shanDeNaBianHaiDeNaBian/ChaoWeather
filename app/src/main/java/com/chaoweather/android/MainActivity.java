package com.chaoweather.android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.chaoweather.android.db.City;
import com.chaoweather.android.db.County;
import com.chaoweather.android.db.Province;
import com.chaoweather.android.util.HttpUtil;

import org.jetbrains.annotations.NotNull;
import org.litepal.LitePal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import android.app.ProgressDialog;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "aa";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LitePal.deleteAll(Province.class, "");
        LitePal.deleteAll(City.class, "");
        LitePal.deleteAll(County.class, "");
        List<Province> provinces = LitePal.findAll(Province.class);
        // List<City> cities = LitePal.findAll(City.class);
        // List<County> counties = LitePal.findAll(County.class);

        if (provinces.size() == 0) {
            AssetManager asset = getAssets();
            try {
                InputStream chinaCitys = asset.open("China-City-List-latest.csv");
                // Log.d(TAG, "onCreate: chinaCitys " + chinaCitys.toString());
                InputStreamReader is = new InputStreamReader(chinaCitys);
                BufferedReader reader = new BufferedReader(is);
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] arr_thisLine;
                    //处理 AD_code 带 "110,100,110,000,100,000" 这种格式的处理
                    if (line.indexOf(",\"") > -1) {
                        String[] before = line.substring(0, line.indexOf(",\"")).split(
                                "\\,");
                        String lastStr = line.substring(line.indexOf(",\""));
                        String lastAd = lastStr.substring(lastStr.indexOf(",\"") + 2,
                                lastStr.length() - 1);
                        arr_thisLine = new String[before.length + 1];
                        System.arraycopy(before, 0, arr_thisLine, 0, before.length);
                        arr_thisLine[before.length] = lastAd;
                    } else {
                        arr_thisLine = line.split("\\,");
                    }

                    //获取省数据
                    //直辖市/省 代码特征，Location_ID 后 4 位，直辖市：0100，省：0101
                    String municipalityProvinceCode =
                            arr_thisLine[0].substring(arr_thisLine[0].length() - 4, arr_thisLine[0].length());
                    // Log.d(TAG, "onCreate: " + municipalityProvinceCode);
                    if (municipalityProvinceCode.equals("0100") || municipalityProvinceCode.equals(
                            "0101")) {
                        // Log.d(TAG, "onCreate: 省：" + arr_thisLine[7]);
                        Province province = new Province();
                        province.setLocationName(arr_thisLine[7]);
                        province.setAdm1En(arr_thisLine[6]);
                        province.setAdm1(arr_thisLine[7]);
                        province.setAdm2En(arr_thisLine[8]);
                        province.setAdm2(arr_thisLine[9]);
                        province.save();
                    }
                    //
                    //获取市数据
                    //市 代码特征，Location_ID 后 2 位，01 如：101051101 鸡西市
                    String cityCode =
                            arr_thisLine[0].substring(arr_thisLine[0].length() - 2, arr_thisLine[0].length());
                    // Log.d(TAG, "onCreate: " + municipalityProvinceCode);
                    if (municipalityProvinceCode.equals("0100") || municipalityProvinceCode.equals(
                            "0101") || cityCode.equals("01")) {
                        // Log.d(TAG, "onCreate: 市：" + arr_thisLine[9]);
                        City city = new City();
                        city.setLocationId(arr_thisLine[0]);
                        city.setLocationNameEn(arr_thisLine[1]);
                        city.setLocationName(arr_thisLine[2]);
                        city.setAdm1En(arr_thisLine[6]);
                        city.setProvinceAdm1En(arr_thisLine[6]);
                        city.setAdm1(arr_thisLine[7]);
                        city.setAdm2En(arr_thisLine[8]);
                        city.setAdm2(arr_thisLine[9]);
                        city.setLatitude(arr_thisLine[10]);
                        city.setLongitude(arr_thisLine[11]);
                        city.setAdCode(arr_thisLine[12]);
                        city.save();
                    }
                    //
                    // //攒出 县/区 数据 Location_ID 代码特性 后台 4 位 或后 2位 不是 0100、0101、01
                    // if (!municipalityProvinceCode.equals("0100") && !municipalityProvinceCode.equals(
                    //         "0101") && !cityCode.equals("01")) {
                    //     // Log.d(TAG, "onCreate: 县/区：" + arr_thisLine[2]);
                    //     County county = new County();
                    //     county.setLocationId(arr_thisLine[0]);
                    //     county.setLocationNameEn(arr_thisLine[1]);
                    //     county.setLocationName(arr_thisLine[2]);
                    //     county.setAdm1En(arr_thisLine[6]);
                    //     county.setCityAdm1En(arr_thisLine[8]);
                    //     county.setAdm1(arr_thisLine[7]);
                    //     county.setAdm2En(arr_thisLine[8]);
                    //     county.setAdm2(arr_thisLine[9]);
                    //     county.setLatitude(arr_thisLine[10]);
                    //     county.setLongitude(arr_thisLine[11]);
                    //     county.setAdCode(arr_thisLine[12]);
                    //     county.save();
                    // }
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "onCreate: 2.5 " + e.toString());
                Toast.makeText(this, "错误：文件不存在或打开失败！", Toast.LENGTH_LONG).show();
            }


            //101090101
            // HttpUtil.sendOkHttpRequest("https://geoapi.heweather" +
            //         ".net/v2/city/lookup?location=guangdong&key" +
            //         "=c1d9dc6ffb8545f2884e7d36752ce8b7", new Callback() {
            //     @Override
            //     public void onFailure(@NotNull Call call, @NotNull IOException e) {
            //
            //     }
            //
            //     @Override
            //     public void onResponse(@NotNull Call call, @NotNull Response response) throws
            //     IOException {
            //         String res = response.body().string();
            //         Log.d(TAG, "onResponse: " + res);
            //     }
            // });
        }

        // Intent intent = new Intent(this, WeatherActivity.class);
        // startActivity(intent);
        // finish();

        //获取缓存中的天气数据，如果有直接打开天气详情界面，就不重新选择地区了
        // SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // if (prefs.getString("weather", null) != null) {
        //     Intent intent = new Intent(this, WeatherActivity.class);
        //     startActivity(intent);
        //     finish();
        // }
    }

}