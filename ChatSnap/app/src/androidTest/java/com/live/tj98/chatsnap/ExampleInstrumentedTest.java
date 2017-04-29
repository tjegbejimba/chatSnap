package com.live.tj98.chatsnap;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    private String initialValue;
    private String finalValue;

    private FirebaseDatabase database;
    private DatabaseReference myRefChoice;

    @Before
    public void setup() {
        database = FirebaseDatabase.getInstance();
//        myRefChoice = database.getReference("Questions/Question 1/Choice 1/Number chosen");
        myRefChoice = database.getReference("Chatrooms/chatroom1/Message1/message");
    }

    @Test
    public void testNumberChosenIncrement() throws Exception {
        // Write a message to the database

        final CountDownLatch latch = new CountDownLatch(1);
        myRefChoice.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                initialValue = dataSnapshot.getValue().toString();
                latch.countDown();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        latch.await(10, TimeUnit.SECONDS);

        myRefChoice.setValue(initialValue+"m");

        final CountDownLatch checkerLatch = new CountDownLatch(1);
        myRefChoice.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                finalValue = dataSnapshot.getValue().toString();
                latch.countDown();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        checkerLatch.await(10,TimeUnit.SECONDS);
        System.out.println(myRefChoice.toString());
        assertEquals(initialValue+"m",finalValue);
    }
}

