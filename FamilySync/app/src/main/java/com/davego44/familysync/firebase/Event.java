package com.davego44.familysync.firebase;

/**
 * Created by daveg on 5/26/2017.
 */

public class Event {
    String name;
    String message;
    String location;
    String author;
    long date;

    public Event() {
    }

    public Event(String name, String message, String location, long date, String author) {
        this.name = name;
        this.message = message;
        this.location = location;
        this.date = date;
        this.author = author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthor() {
        return author;
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }

    public String getLocation() {
        return location;
    }

    public long getDate() {
        return date;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setDate(long date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Event))
            return false;
        Event event = (Event) o;
        return ((this.name.equals(event.getName())) &&
                (this.message.equals(event.getMessage())) &&
                (this.location.equals(event.getLocation())) &&
                (this.date == (event.getDate())) &&
                (this.author.equals(event.getAuthor())));
    }

    @Override
    public String toString() {
        return name;
    }
}
