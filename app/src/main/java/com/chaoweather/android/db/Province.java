package com.chaoweather.android.db;

import org.litepal.crud.LitePalSupport;

//省 表
public class Province extends LitePalSupport {
    //编号 记录 id （应该是自增）
    private int id;
    //中文名称
    private String locationName;
    //国家代号 中文 CN 死
    private String countryCode = "CN";
    //国家名称 英文 China
    private String countryEn = "China";
    //国家名称 中文
    private String country = "中国";
    //一级行政区划(Adm1) 英文
    private String adm1En;
    //一级行政区划(Adm1) 中文
    private String adm1;
    //二级行政区划(Adm2) 英文
    private String adm2En;
    //二级行政区划(Adm2) 中文
    private String adm2;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
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

    public String getCountryCode() {
        return countryCode;
    }

    public String getCountryEn() {
        return countryEn;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setCountryEn(String countryEn) {
        this.countryEn = countryEn;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
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
}
