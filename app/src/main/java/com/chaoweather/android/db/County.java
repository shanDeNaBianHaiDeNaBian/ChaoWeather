package com.chaoweather.android.db;

import org.litepal.crud.LitePalSupport;

public class County extends LitePalSupport {
    //编号 记录 id
    private int id;
    //地区代码
    private String locationId;
    //英文名称
    private String locationNameEn;
    //中文名称
    private String locationName;
    //国家代号 中文 CN
    private String countryCode;
    //国家名称 英文
    private String countryEn;
    //国家名称 中文
    private String country;
    //一级行政区划(Adm1) 英文
    private String adm1En;
    //一级行政区划(Adm1) 中文
    private String adm1;
    //二级行政区划(Adm2) 英文
    private String adm2En;
    //二级行政区划(Adm2) 中文
    private String adm2;
    //纬度
    private String latitude;
    //精度
    private String longitude;
    //广告代码
    private String adCode;

    //所属 市 一级行政区英文
    private String cityAdm1En;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getLocationNameEn() {
        return locationNameEn;
    }

    public void setLocationNameEn(String locationNameEn) {
        this.locationNameEn = locationNameEn;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryEn() {
        return countryEn;
    }

    public void setCountryEn(String countryEn) {
        this.countryEn = countryEn;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAdm1En() {
        return adm1En;
    }

    public void setAdm1En(String adm1En) {
        this.adm1En = adm1En;
    }

    public String getAdm1() {
        return adm1;
    }

    public void setAdm1(String adm1) {
        this.adm1 = adm1;
    }

    public String getAdm2En() {
        return adm2En;
    }

    public void setAdm2En(String adm2En) {
        this.adm2En = adm2En;
    }

    public String getAdm2() {
        return adm2;
    }

    public void setAdm2(String adm2) {
        this.adm2 = adm2;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAdCode() {
        return adCode;
    }

    public void setAdCode(String adCode) {
        this.adCode = adCode;
    }

    public String getCityAdm1En() {
        return cityAdm1En;
    }

    public void setCityAdm1En(String cityAdm1En) {
        this.cityAdm1En = cityAdm1En;
    }
}
