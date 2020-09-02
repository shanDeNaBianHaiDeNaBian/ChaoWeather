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