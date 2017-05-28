package com.davego44.familysync.helper;

/**
 * Created by daveg on 5/25/2017.
 */

public abstract class Constants {
    public static class Errors {
        public static String INVALID_NAME = "Please enter a valid name";
        public static String INVALID_EMAIL = "Please enter a valid email address";
        public static String INVALID_PASSWORD = "Please enter a valid password";
    }

    public static class Firebase {
        public static String USERS = "users";
        public static String EVENTS_USERS = "events/users";
        public static String SAVED_EVENTS = "savedEvents";
        public static String GROUP = "group";
    }

    public static class Intent {
        public static String EMAIL = "INTENT_EMAIL";
        public static String PASSWORD = "INTENT_PASSWORD";
    }
}
