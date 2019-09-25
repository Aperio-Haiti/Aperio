package com.example.aprio;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Message")
class Message extends ParseObject {
    private static final String USER_ID_KEY = "userId";
    private static final String BODY_KEY = "body";

    String getUserId() {
        return getString(USER_ID_KEY);
    }

    String getBody() {
        return getString(BODY_KEY);
    }

    void setUserId(String userId) {
        put(USER_ID_KEY, userId);
    }

    void setBody(String body) {
        put(BODY_KEY, body);
    }
}
