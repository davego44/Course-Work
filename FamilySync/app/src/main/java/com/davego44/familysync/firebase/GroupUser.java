package com.davego44.familysync.firebase;

/**
 * Created by daveg on 5/27/2017.
 */

public class GroupUser {
    String email;
    String name;
    boolean show;
    int colorRes;

    public GroupUser() {
    }

    public GroupUser(String email, String name) {
        this.email = email;
        this.name = name;
        colorRes = 1;
        show = true;
    }

    public boolean getShow() {
        return show;
    }

    public void setShow(boolean bool) {
        show = bool;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setColorRes(int colorRes) {
        this.colorRes = colorRes;
    }

    public int getColorRes() {
        return colorRes;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GroupUser))
            return false;
        GroupUser groupUser = (GroupUser) o;
        return (this.getEmail().equals(groupUser.getEmail()));
    }
}
