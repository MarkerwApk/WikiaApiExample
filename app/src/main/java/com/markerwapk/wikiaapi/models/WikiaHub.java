package com.markerwapk.wikiaapi.models;

import java.io.Serializable;

/**
 * Created by Markerwapk on 15.06.16.
 */
public class WikiaHub implements Serializable {

    private int id;
    private String name;
    private String url;
    private String language;

    private String image;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getQueryName(){
        return name.replace("Hub", "").replaceAll("\\s+", "");
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
