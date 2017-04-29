package com.live.tj98.chatsnap;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by zilles on 11/10/16.
 */

public class ChatRoomDesc {
    public String name;
    public String index;

    public ChatRoomDesc(String name, String index) {
        this.name = name;
        this.index = index;
    }

    public ChatRoomDesc() {
        // Default constructor required for calls to DataSnapshot.getValue(ChatRoomDesc.class),
        // because we have a non-default constructor as well.
    }
}