package com.chaoweather.android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.chaoweather.android.db.City;
import com.chaoweather.android.db.County;
import com.chaoweather.android.db.Province;
import com.chaoweather.android.util.HttpUtil;
import com.chaoweather.android.util.Utility;

import org.jetbrains.annotations.NotNull;
import org.litepal.LitePal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChooseAreaFragment extends Fragment {
    private final static String TAG = "aa";

    //当前选页面的级别
    //省级
    public static final int LEVEL_PROVINCE = 0;
    //市级
    public static final int LEVEL_CITY = 1;
    //县级
    public static final int LEVEL_COUNTY = 2;

    //等待对话框
    private ProgressDialog progressDialog;

    //标题
    private TextView titleText;
    //返回按钮
    private Button backButton;
    //数据列表
    private ListView listView;
    //数据列表 listView 的 adapter
    private ArrayAdapter<String> adapter;
    //数据列表 listView 的 adapter 的集合数据
    private List<String> dataList = new ArrayList<>();

    //从数据库中查询得来的 省 列表
    private List<Province> provinceList;
    //从数据库中查询得来的 市 列表
    private List<City> cityList;
    //从数据库中查询得来的 县 列表
    private List<County> countyList;

    //选中的省
    private Province selectedProvince;
    //选中的市
    private City selectedCity;

    //当前选中的级别
    private int currentLevel;

    /**
     * 当 choose_area view 创建时执行，初始化一些变量
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // //获取当前 view 布局（choose_area.xml）
        View view = inflater.inflate(R.layout.choose_area, container, false);
        //获取 标题 布局
        titleText = view.findViewById(R.id.title_text);
        //获取 返回按钮 布局
        backButton = view.findViewById(R.id.back_button);
        //获取 列表 布局
        listView = view.findViewById(R.id.list_view);
        //初始化 listView 的 adapter
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList);
        //给 listView 列表布局设置 adapter
        listView.setAdapter(adapter);
        return view;
    }

    /**
     * 当活动创建时执行
     *
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //给 listView 的每项设置 adapter 的点击监听
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //如果当前 选中级别 等于 省级
                if (currentLevel == LEVEL_PROVINCE) {
                    //从省数据集合中获取当前省数据
                    selectedProvince = provinceList.get(position);
                    //查询当前省下的市数据
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    //如果当前 选中级别 等于 市级
                    //从市数据集合中获取当前市数据
                    selectedCity = cityList.get(position);
                    //查询当前市下县数据
                    queryCounties();
                } else if (currentLevel == LEVEL_COUNTY) {
                    County county = countyList.get(position);
                    String locationId = county.getLocationId();
                    String title = county.getLocationName();
                    if (getActivity() instanceof MainActivity) {
                        Intent intent = new Intent(getActivity(), WeatherActivity.class);
                        intent.putExtra("weather_id", locationId);
                        intent.putExtra("title", title);
                        startActivity(intent);

                        //如果是 MainActivity 首页打开，设置一下选择过县/区缓存，目的是下次在首页时直接进入天气页面
                        SharedPreferences.Editor editor =
                                PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
                        editor.putString("county",
                                "{\"selected\":true,\"location_id\":\"" + locationId + "\"," +
                                        "\"title_text\":\"" + title + "\"}");
                        editor.apply();

                        getActivity().finish();
                    } else if (getActivity() instanceof WeatherActivity) {
                        WeatherActivity weatherActivity = (WeatherActivity) getActivity();
                        weatherActivity.drawerLayout.closeDrawers();
                        weatherActivity.swipeRefresh.setRefreshing(true);
                        weatherActivity.requestWeather(locationId);
                        weatherActivity.titleText = title;
                    }
                }
            }
        });

        //设置返回按钮的监听
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果当前 选中级别 是 县级
                if (currentLevel == LEVEL_COUNTY) {
                    //查询当前省下的市数据
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    //如果当前 选中级别 是 市级
                    //查询所有省的数据
                    queryProvinces();
                }
            }
        });

        //默认查询所有省的数据
        queryProvinces();
    }

    /**
     * 查询所有省的数据，优先从数据库中查询，如果没有就从服务器中查询
     */
    private void queryProvinces() {
        showProgressDialog();
        //设置页面标题
        titleText.setText("中国");
        //因为是省级所以不显示返回按钮
        backButton.setVisibility(View.GONE);
        //从数据库中查询 province 省表中的所有数据
        provinceList = LitePal.findAll(Province.class);
        // Log.d(TAG, "queryProvinces: " + provinceList.size());
        //如果有数据
        if (provinceList.size() > 0) {
            //清除 数据集合 防止重复
            dataList.clear();
            //遍历每个省数据
            for (Province province : provinceList) {
                //把省名称添加到 dataList 中
                dataList.add(province.getLocationName());
            }
            //因为 dataList 是 ListView adapter 中使用的数据，所以当 dataList 发生改变后要通知 adapter 数据发生改变了。
            adapter.notifyDataSetChanged();
            //默认设置 listView 中 adapter 默认选中项为 第 1 项
            listView.setSelection(0);
            //设置当前 选中级别 为 省级
            currentLevel = LEVEL_PROVINCE;
            closeProgressDialog();
        } else {
            //如果数据库中没数据，就从 src/main/assets/China-City-List-latest.csv 中查询
            queryFromCSV("province");
        }
    }

    /**
     * 查询当前 省 下所有的 市 数据
     */
    private void queryCities() {
        //设置 市 页面标题为当前选中省数据的 省 名称
        titleText.setText(selectedProvince.getLocationName());
        //因为市级是第二级所以要显示返回按钮
        backButton.setVisibility(View.VISIBLE);
        //查询当前省对应的市数据（不能查询全部市，所以用 where 查询，条件是 市 的 一级行政区划 等于选中 省 的 一级行政区划）
        cityList =
                LitePal.where("adm1En = ?", selectedProvince.getAdm1En()).find(City.class);
        //如果有数据
        if (cityList.size() > 0) {
            //清除 数据集合 防止重复
            dataList.clear();
            //遍历每个市数据
            for (City city : cityList) {
                //把市名称添加到 dataList 中
                dataList.add(city.getLocationName());
            }
            //因为 dataList 是 ListView adapter 中使用的数据，所以当 dataList 发生改变后要通知 adapter 数据发生改变了。
            adapter.notifyDataSetChanged();
            //默认设置 listView 中 adapter 默认选中项为 第 1 项
            listView.setSelection(0);
            //设置当前 选中级别 为 省级
            currentLevel = LEVEL_CITY;
        } else {
            //如果数据库中没数据，就从 src/main/assets/China-City-List-latest.csv 中查询
            queryFromCSV("city");
        }
    }

    private void queryCounties() {
        titleText.setText(selectedCity.getLocationName());
        backButton.setVisibility(View.VISIBLE);
        //查询当前省对应的市数据（不能查询全部市，所以用 where 查询，条件是 市 的 二级行政区划 等于选中 市 的 二级行政区划）
        countyList =
                LitePal.where("adm2En = ?", selectedCity.getAdm2En()).find(County.class);
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getLocationName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        } else {
            //如果数据库中没数据，就从 src/main/assets/China-City-List-latest.csv 中查询
            queryFromCSV("county");
        }
    }

    /**
     * 从 src/main/assets/China-City-List-latest.csv 中查询
     *
     * @param type 类别 province city county
     */
    private void queryFromCSV(final String type) {
        //因为是子线程查询（耗时操作）所以显示一下等待对话框
        showProgressDialog();
        try {
            InputStream chinaCitys = getActivity().getAssets().open("China-City-List-latest.csv");
            // Log.d(TAG, "onCreate: chinaCitys " + chinaCitys.toString());
            InputStreamReader is = new InputStreamReader(chinaCitys);
            BufferedReader reader = new BufferedReader(is);
            String line;
            //获取 省 数据
            List<String[]> provincesData = new ArrayList();
            //获取对应省下的 市 数据
            List<String[]> citiesData = new ArrayList();
            //获取 县/区 数据
            List<String[]> countiesData = new ArrayList();
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

                //直辖市/省 代码特征，Location_ID 后 4 位，直辖市：0100，省：0101
                String municipalityProvinceCode =
                        arr_thisLine[0].substring(arr_thisLine[0].length() - 4,
                                arr_thisLine[0].length());
                //市 代码特征，Location_ID 后 2 位，01 如：101051101 鸡西市
                String cityCode =
                        arr_thisLine[0].substring(arr_thisLine[0].length() - 2,
                                arr_thisLine[0].length());
                // Log.d(TAG, "onCreate: " + municipalityProvinceCode);

                //如果请求类型是 省
                if ("province".equals(type)) {
                    // Log.d(TAG, "onCreate: " + municipalityProvinceCode);
                    if (municipalityProvinceCode.equals("0100") || municipalityProvinceCode.equals(
                            "0101")) {
                        // Log.d(TAG, "onCreate: 省：" + arr_thisLine[7]);
                        provincesData.add(arr_thisLine);
                    }
                } else if ("city".equals(type)) {
                    if (municipalityProvinceCode.equals("0100") || municipalityProvinceCode.equals(
                            "0101") || cityCode.equals("01")) {
                        //如果当前市的 adm2En 等于选中省的 adm2En，就是当前
                        if (arr_thisLine[6].equals(selectedProvince.getAdm1En())) {
                            citiesData.add(arr_thisLine);
                        }
                    }
                } else if ("county".equals(type)) {
                    //攒出 县/区 数据 Location_ID 代码特性 后台 4 位 或后 2位 不是 0100、0101、01
                    if (!municipalityProvinceCode.equals("0100") && !municipalityProvinceCode.equals(
                            "0101") && !cityCode.equals("01")) {
                        // Log.d(TAG, "onCreate: 县/区：" + arr_thisLine[2]);
                        if (arr_thisLine[8].equals(selectedCity.getAdm2En())) {
                            countiesData.add(arr_thisLine);
                        }
                    }
                }
            }

            //声明数据库保存结果
            boolean result = false;
            //如果请求类型是 省
            if ("province".equals(type)) {
                result = Utility.handleProvinceResponse(provincesData);
            } else if ("city".equals(type)) {
                Log.d(TAG, "queryFromCSV: citiesData.size() " + citiesData.size());
                result = Utility.handleCityResponse(citiesData);
            } else if ("county".equals(type)) {
                result = Utility.handleCountyResponse(countiesData);
            }

            //如果保存成功
            if (result) {
                //执行在子线程中修改 UI 界面
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //关闭 progress
                        closeProgressDialog();
                        //如果类型为 省 从数据库查询 省 并更新界面（下面以此类推）
                        if ("province".equals(type)) {
                            queryProvinces();
                        } else if ("city".equals(type)) {
                            queryCities();
                        } else if ("county".equals(type)) {
                            queryCounties();
                        }
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "onCreate: 2.5 " + e.toString());
            Toast.makeText(getContext(), "错误：文件不存在或打开失败！", Toast.LENGTH_LONG).show();
        }
    }

    //显示 progress 对话框
    private void showProgressDialog() {
        //判断一下防止重复创建
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            //设置显示信息
            progressDialog.setMessage("加载中...");
            //设置点击遮罩不关闭
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    //关闭 progreee 对话框
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
