package com.davego44.familysync.helper;

import android.graphics.drawable.Drawable;

/**
 * Created by daveg on 11/7/2016.
 */

/**
 * Each navigation item that goes in the drawer layout
 */
public class NavItem {

    private int picture;
    private String title;

    public NavItem(int picture, String title) {
        this.picture = picture;
        this.title = title;
    }

    public int getPicture() {
        return picture;
    }

    public String getTitle() {
        return title;
    }
}
