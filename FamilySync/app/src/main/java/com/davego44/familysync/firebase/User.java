package com.davego44.familysync.firebase;

import com.davego44.familysync.firebase.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by daveg on 5/26/2017.
 */

/**
 * Basic Firebase object for each user account data
 */
public class User {
    String name;
    String email;
    List<String> group;
    List<Event> savedEvents;

    public User() {
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
        group = new ArrayList<String>();
        savedEvents = new ArrayList<Event>();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGroupEmails(List<String> groupEmails) {
        this.group = groupEmails;
    }

    public List<String> getGroupEmails() {
        return group;
    }

    public List<Event> getSavedEvents() {
        return savedEvents;
    }

    public void setSavedEvents(List<Event> savedEvents) {
        this.savedEvents = savedEvents;
    }
}
