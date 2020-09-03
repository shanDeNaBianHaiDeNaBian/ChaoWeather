package com.chaoweather.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.bumptech.glide.Glide;
import com.chaoweather.android.gson.Forecast;
import com.chaoweather.android.gson.Weather;
import com.chaoweather.android.service.AutoUpdateService;
import com.chaoweather.android.util.HttpUtil;
import com.chaoweather.android.util.Utility;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

// 天气详情 界面
public class WeatherActivity extends AppCompatActivity {

    private static final String TAG = "aa";

    public String titleText;

    //14.5.4
    private ImageView bingPicImg;
    //14.6
    public SwipeRefreshLayout swipeRefresh;
    //14.6.2
    public DrawerLayout drawerLayout;
    //14.6.2
    private Button navButton;

    //声名一些布局
    //界面总布局（父布局），因为页面可能很长所以用滚动布局
    private ScrollView weatherLayout;
    //顶部城市名称标题
    private TextView titleCity;
    //顶部的天气数据更新时间
    private TextView titleUpdateTime;
    //当前的温度
    private TextView degreeText;
    //当前的天气状况，比如：晴
    private TextView weatherInfoText;
    //未来6天的天气情况布局
    private LinearLayout forecastLayout;
    //空气质量指数
    private TextView aqiText;
    //pm2.5
    private TextView pm25Text;
    //生活建议 - 舒适度
    private TextView comfortText;
    //生活建议 - 洗车指数
    private TextView carWashText;
    //生活建议 - 运动建议
    private TextView sportText;


    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //14.5.4 状态栏沉浸模式？ 5.0 级以上
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            //活动布局显示在状态栏上面
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            //设置状态栏背景颜色为透明
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        //BDAbstractLocationListener为7.2版本新增的Abstract类型的监听接口
        //原有BDLocationListener接口暂时同步保留。具体介绍请参考后文中的说明
        //声明LocationClient类
        mLocationClient = new LocationClient(getApplicationContext());
        //注册监听函数
        mLocationClient.registerLocationListener(myListener);

        setContentView(R.layout.activity_weather);


        //多权限动态申请。
        List<String> permissionList = new ArrayList<>();

        //ACCESS_FINE_LOCATION 精准定位。（收集没有申请通过的权限)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        // if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) !=
        // PackageManager.PERMISSION_GRANTED) {
        //     permissionList.add(Manifest.permission.READ_PHONE_STATE);
        // }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        //如果集合不等于空
        if (!permissionList.isEmpty()) {
            //把集合转换成字符串数组
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            //申请权限（第2个参数相当于 new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest
            // .permission.READ_PHONE_STATE,Manifest.permission.WRITE_EXTERNAL_STORAGE}
            ActivityCompat.requestPermissions(this, permissions, 1);
        } else {
            requestLocation();
        }

        //获取 ManiActivity 首页打开时传递过来的标题
        titleText = getIntent().getStringExtra("title");

        //14.5.4
        bingPicImg = findViewById(R.id.bing_pic_img);
        //14.6
        swipeRefresh = findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        //14.6.2
        drawerLayout = findViewById(R.id.drawer_layout);
        navButton = findViewById(R.id.nav_button);
        //初始化各控件
        weatherLayout = findViewById(R.id.weather_layout);
        titleCity = findViewById(R.id.title_city);
        titleUpdateTime = findViewById(R.id.title_update_time);
        degreeText = findViewById(R.id.degree_text);
        weatherInfoText = findViewById(R.id.weather_info_text);
        forecastLayout = findViewById(R.id.forecast_layout);
        aqiText = findViewById(R.id.aqi_text);
        pm25Text = findViewById(R.id.pm25_text);
        comfortText = findViewById(R.id.comfort_text);
        carWashText = findViewById(R.id.car_wash_text);
        sportText = findViewById(R.id.sport_text);

        //14.6
        final String locationId;

        //获取 weather 缓存数据
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String prefs_county = prefs.getString("county",
                "{\"selected\":false,\"location_id\":\"101090513\"," +
                        "\"title_text\":\"路南\"}");
        try {
            JSONObject county = new JSONObject(prefs_county);
            if (county.getBoolean("selected")) {
                locationId = county.getString("location_id");
                titleText = county.getString("title_text");
            } else {
                locationId = getIntent().getStringExtra("location_id");
            }
            requestWeather(locationId);
            swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    requestWeather(locationId);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //14.5.4
        // String bingPic = prefs.getString("bing_pic", null);
        // if (bingPic != null) {
        //     Glide.with(this).load(bingPic).into(bingPicImg);
        // } else {
            loadBingPic();
        // }

        //14.6.2
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    //请求定位
    private void requestLocation() {
        //先初始化
        initLocation();
        //开始
        mLocationClient.start();
    }

    //初始化定位
    private void initLocation() {
        //获取定位连接配置实例
        LocationClientOption option = new LocationClientOption();
        //可选，是否需要地址信息，默认为不需要，即参数为false
        //如果开发者需要获得当前点的地址信息，此处必须为true
        option.setIsNeedAddress(true);
        //可选，设置是否需要最新版本的地址信息。默认需要，即参数为true
        option.setNeedNewVersionRgc(true);
        //设置扫描间隔，单位是毫秒 当<1000(1s)时，定时定位无效
        // option.setScanSpan(5000);
        mLocationClient.setLocOption(option);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "请务必同意权限才能使用本功能",
                                    Toast.LENGTH_LONG).show();
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                } else {
                    Toast.makeText(this, "发生未知错误！", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
        }
    }

    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取位置描述信息相关的结果
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明
            String addr = location.getAddrStr();    //获取详细地址信息
            String country = location.getCountry();    //获取国家
            String province = location.getProvince();    //获取省份
            String city = location.getCity();    //获取城市
            String district = location.getDistrict();    //获取区县
            String street = location.getStreet();    //获取街道信息
            String adcode = location.getAdCode();    //获取adcode
            String town = location.getTown();    //获取乡镇信息

            Log.d(TAG, "onReceiveLocation: addr is: " + addr);
            Log.d(TAG, "onReceiveLocation: country is: " + country);
            Log.d(TAG, "onReceiveLocation: province is: " + province);
            Log.d(TAG, "onReceiveLocation: city is: " + city);
            Log.d(TAG, "onReceiveLocation: district is: " + district);
            Log.d(TAG, "onReceiveLocation: street is: " + street);
            Log.d(TAG, "onReceiveLocation: adcode is: " + adcode);
            Log.d(TAG, "onReceiveLocation: town is: " + town);
        }
    }

    private int requestCounts = 0;
    private String basicResponseText = "{}";
    private String apqiResponseText = "{}";
    private String indicesResponseText = "{}";
    private String dayForecast7Text = "{}";

    /**
     * 根据天气 id 请求城市天气信息
     */
    public void requestWeather(final String locationId) {
        //攒出对应请求地址
        String weatherUrl =
                "https://devapi.heweather.net/v7/weather/now?location=" + locationId + "&key" +
                        "=c1d9dc6ffb8545f2884e7d36752ce8b7";
        //发送请求
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                //防止篡改所以弄个 final ？。获取请求结果。
                final String responseText = response.body().string();
                // Log.d(TAG, "requestWeather onResponse: basic responseText is " + responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObject = new JSONObject(responseText);
                            if (jsonObject.getString("code").equals("200")) {
                                basicResponseText = responseText;
                                showWeatherInfo();
                            } else {
                                Toast.makeText(WeatherActivity.this, "获取实时数据失败！", Toast.LENGTH_LONG)
                                        .show();
                            }
                            swipeRefresh.setRefreshing(false);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取实时数据失败！", Toast.LENGTH_LONG).show();
                        //14.6
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });

        //攒出对应请求地址
        String AQIUrl =
                "https://devapi.heweather.net/v7/air/now?location=" + locationId + "&key" +
                        "=c1d9dc6ffb8545f2884e7d36752ce8b7";
        //发送请求
        HttpUtil.sendOkHttpRequest(AQIUrl, new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws
                    IOException {
                //防止篡改所以弄个 final ？。获取请求结果。
                final String responseText = response.body().string();
                // Log.d(TAG, "requestWeather onResponse: AQI responseText is " + responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObject = new JSONObject(responseText);
                            if (jsonObject.getString("code").equals("200")) {
                                apqiResponseText = responseText;
                                showWeatherInfo();
                            } else {
                                Toast.makeText(WeatherActivity.this, "获取空气质量失败！", Toast.LENGTH_LONG)
                                        .show();
                            }
                            swipeRefresh.setRefreshing(false);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取空气质量失败！", Toast.LENGTH_LONG)
                                .show();
                        //14.6
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });


        //攒出对应请求地址
        String indicesUrl =
                "https://devapi.heweather.net/v7/indices/1d?location=" + locationId + "&key" +
                        "=c1d9dc6ffb8545f2884e7d36752ce8b7&type=8,1,2";
        //发送请求
        HttpUtil.sendOkHttpRequest(indicesUrl, new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws
                    IOException {
                //防止篡改所以弄个 final ？。获取请求结果。
                final String responseText = response.body().string();
                // Log.d(TAG,
                //         "requestWeather onResponse: indices responseText is " + responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObject = new JSONObject(responseText);
                            if (jsonObject.getString("code").equals("200")) {
                                indicesResponseText = responseText;
                                showWeatherInfo();
                            } else {
                                Toast.makeText(WeatherActivity.this, "获取生活指数失败！",
                                        Toast.LENGTH_LONG)
                                        .show();
                            }
                            swipeRefresh.setRefreshing(false);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取生活指数失败！",
                                Toast.LENGTH_LONG)
                                .show();
                        //14.6
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });

        //攒出对应请求地址
        String dayForecast7Url =
                "https://devapi.heweather.net/v7/weather/7d?location=" + locationId +
                        "&key" +
                        "=c1d9dc6ffb8545f2884e7d36752ce8b7";
        //发送请求
        HttpUtil.sendOkHttpRequest(dayForecast7Url, new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws
                    IOException {
                //防止篡改所以弄个 final ？。获取请求结果。
                final String responseText = response.body().string();
                // Log.d(TAG,
                //         "requestWeather onResponse: indices responseText is " + responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObject = new JSONObject(responseText);
                            if (jsonObject.getString("code").equals("200")) {
                                dayForecast7Text = responseText;
                                showWeatherInfo();
                            } else {
                                Toast.makeText(WeatherActivity.this, "获取7天预报失败！",
                                        Toast.LENGTH_LONG)
                                        .show();
                            }
                            swipeRefresh.setRefreshing(false);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取7天预报失败！",
                                Toast.LENGTH_LONG)
                                .show();
                        //14.6
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });

        //14.5.4
        loadBingPic();
    }

    //14.5.4
    private void loadBingPic() {
        String requestBingPic = "https://cn.bing.com/th?id=OHR.FinancialTowers_ZH-CN6494148642_1920x1080.jpg";
        Glide.with(WeatherActivity.this).load(requestBingPic).into(bingPicImg);
        // HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
        //     @Override
        //     public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
        //         final String bingPic = response.body().string();
        //         SharedPreferences.Editor editor =
        //                 PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
        //         editor.putString("bing_pic", bingPic);
        //         editor.apply();
        //         // Log.d(TAG, "onResponse: bingPic: " + bingPic);
        //         runOnUiThread(new Runnable() {
        //             @Override
        //             public void run() {
        //                 Toast.makeText(WeatherActivity.this, bingPic, Toast.LENGTH_LONG).show();
        //                 Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
        //             }
        //         });
        //     }
        //
        //     @Override
        //     public void onFailure(@NotNull Call call, @NotNull IOException e) {
        //         final String err = e.toString();
        //         runOnUiThread(new Runnable() {
        //             @Override
        //             public void run() {
        //                 Log.d(TAG, "onFailure: bingPic: " + err);
        //                 Toast.makeText(WeatherActivity.this, err, Toast.LENGTH_LONG).show();
        //             }
        //         });
        //         e.printStackTrace();
        //     }
        // });
    }

    /**
     * 处理并展示实体类 Weather 中的数据
     */
    private void showWeatherInfo() {
        //设置标题城市名称
        titleCity.setText(titleText);

        String weatherJSON =
                "{\"basic\":" + basicResponseText + ",\"aqi\":" + apqiResponseText + "," +
                        "\"indices" +
                        "\":" + indicesResponseText + ",\"forecast" +
                        "\":" + dayForecast7Text + "}";
        Weather weather = Utility.handleWeatherResponse(weatherJSON);

        if (weather != null && "200".equals(weather.code)) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            // Log.d(TAG,
            //         "showWeatherInfo: weather.basic.getCode() is " + weather.basic.getCode());
            if (!weather.basic.getCode().equals("000")) {
                //2013-12-30 T 01:45 + 08:00
                String updateTime = weather.basic.getUpdateTime().split("\\+")[1];
                //获取 weather 实例中 now 的 temperature
                String degree = weather.basic.getNow().getTemp() + "℃";
                //获取 weather 实例中 now 的 more 的 info
                String weatherInfo = weather.basic.getNow().getText();
                //设置标题天气更新时间
                titleUpdateTime.setText(updateTime);
                //设置温度
                degreeText.setText(degree);
                //设置天气状况
                weatherInfoText.setText(weatherInfo);
            }

            //因为未来 6 天天气数据是集合列表所以先把 forecastLayout 布局中的所有子布局全部移除，防止累计追加
            if (!weather.forecast.code.equals("000")) {
                forecastLayout.removeAllViews();
                //遍历 weather 中的 forecastList 集合
                for (Forecast.DailyBean forecast : weather.forecast.daily) {
                    //根据 R.layout.forecast_item 子布局创建 view 布局对象
                    View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,
                            forecastLayout,
                            false);
                    //获取 forecast_item 里面的子布局
                    //未来日期
                    TextView dateText = view.findViewById(R.id.date_text);
                    //天气状况
                    TextView infoText = view.findViewById(R.id.info_text);
                    //最高温度
                    TextView maxText = view.findViewById(R.id.max_text);
                    //最低温度
                    TextView minText = view.findViewById(R.id.min_text);

                    //设置对应的数据
                    dateText.setText(forecast.fxDate);
                    infoText.setText(forecast.textDay);
                    maxText.setText(forecast.tempMax);
                    minText.setText(forecast.tempMin);

                    //追加到布局中
                    forecastLayout.addView(view);
                }
            }

            // //如果空气质量有值
            if (!weather.aqi.code.equals("000")) {
                aqiText.setText(weather.aqi.now.aqi);
                pm25Text.setText(weather.aqi.now.pm2p5);
            }

            if (!weather.indices.code.equals("000")) {
                //设置。。。
                String comfort = "舒适度：" + weather.indices.daily.get(2).txt;
                comfortText.setText(comfort);

                String carWash = "洗车指数：" + weather.indices.daily.get(0).txt;
                carWashText.setText(carWash);

                String sport = "运动建议：" + weather.indices.daily.get(1).txt;
                sportText.setText(sport);
            }

            //最后设置父总局可见
            weatherLayout.setVisibility(View.VISIBLE);
            //
            // //启动服务，用于定时长期在后台更新天气数据到缓存
            // Intent intent = new Intent(this, AutoUpdateService.class);
            // startService(intent);
        } else {
            Toast.makeText(WeatherActivity.this, "获取天气信息失败！", Toast.LENGTH_LONG).show();
        }
    }


}