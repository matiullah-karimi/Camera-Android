package com.example.mkarimi.icu;

import java.io.File;

/**
 * Created by mkarimi on 11/22/17.
 */

public class Multipart {
    private String name;
    private String province;
    private String district;
    private String organization;
    private File file;

    public void setName(String name) {
        this.name = name;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public void setFile(File path) {
        this.file = path;
    }

    public String getName() {
        return name;
    }

    public String getProvince() {
        return province;
    }

    public String getDistrict() {
        return district;
    }

    public String getOrganization() {
        return organization;
    }

    public File getFile() {
        return file;
    }
}
