package com.markerwapk.wikiaapi.models;

import java.util.ArrayList;

/**
 * Created by Markerwapk on 15.06.16.
 */
public class WikisList {

    private ArrayList<Wiki> items;

    private int batches;
    private int total;
    private int currentBatch;
    private int next;

    public ArrayList<Wiki> getItems() {
        return items;
    }

    public int getNext() {
        return next;
    }
}
