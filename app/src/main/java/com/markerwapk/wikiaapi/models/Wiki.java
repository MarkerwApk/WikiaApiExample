package com.markerwapk.wikiaapi.models;

import java.io.Serializable;

/**
 * Created by Markerwapk on 15.06.16.
 */
public class Wiki implements Serializable {

    private String name;
    private String desc;
    private String url;

    private String image;

    public String getDesc() {
        return desc;
    }

    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }
}
