package com.splitapp.activities;

import com.splitapp.models.ModelFriendList;

import java.util.ArrayList;

class FriendsList {
    private static FriendsList instance;
    final ArrayList<ModelFriendList> friends = new ArrayList<>();

    private FriendsList() {
    }

    static FriendsList getInstance() {
        if (instance == null) {
            instance = new FriendsList();
        }
        return instance;
    }

}
