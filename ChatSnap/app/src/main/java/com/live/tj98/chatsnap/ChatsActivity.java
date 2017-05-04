package com.live.tj98.chatsnap;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.RemoteMessage;
import com.live.tj98.chatsnap.ChatsMessage;
import com.live.tj98.chatsnap.R;

import java.util.List;

public class ChatsActivity extends AppCompatActivity {
    public static final String EXTRA_CHAT_KEY = "CHAT_KEY";

    private FirebaseAuth mAuth;

    private RecyclerView mRecyclerView;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mChatRef;
    private EditText mNewPostText;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter<ChatsMessage, ChatMessageViewHolder> mChatViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);
        mAuth = FirebaseAuth.getInstance();
        String chatKey = getIntent().getStringExtra(EXTRA_CHAT_KEY);
        mChatRef = database.getReference(chatKey);

        mRecyclerView = (RecyclerView) findViewById(R.id.chat_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mNewPostText = (EditText) findViewById(R.id.newPostText);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mChatViewAdapter = new FirebaseRecyclerAdapter<ChatsMessage, ChatMessageViewHolder>(ChatsMessage.class,
                R.layout.chat_message, ChatMessageViewHolder.class, mChatRef) {
            @Override
            protected void populateViewHolder(ChatMessageViewHolder viewHolder,
                                              ChatsMessage model, int position) {
                viewHolder.bind(model);
                viewHolder.setColor(viewHolder, model, mAuth.getCurrentUser().getDisplayName().toString());
            }

            @Override
            public ChatMessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                final View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
                return new ChatMessageViewHolder(view, getApplicationContext());
//                return super.onCreateViewHolder(parent, viewType);
            }

        };
        mChatViewAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mChatViewAdapter.getItemCount();
                int lastVisiblePosition =
                        linearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    mRecyclerView.scrollToPosition(positionStart);
                }
            }
        });
        mRecyclerView.setAdapter(mChatViewAdapter);

    }

    public static class ChatMessageViewHolder extends RecyclerView.ViewHolder {
        private final TextView mNameTextView;
        private final TextView mMessageTextView;
        private Context context;


        public ChatMessageViewHolder(View itemView, Context context) {
            super(itemView);
            mMessageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
            mNameTextView = (TextView) itemView.findViewById(R.id.nameTextView);
            this.context = context;
        }

        public void bind(ChatsMessage chatMessage) {
            mMessageTextView.setText(chatMessage.message);
            mNameTextView.setText(chatMessage.author);
        }

        public void setColor(RecyclerView.ViewHolder view, ChatsMessage model, String currentUser) {
            if (!model.author.equals(currentUser)) {

                view.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.lightGrey));
            }
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mChatViewAdapter != null) {
            mChatViewAdapter.cleanup();
        }
    }

    public void newPostClick(View view) {
        String newPostString = mNewPostText.getText().toString();
        mNewPostText.setText("");

        if (mChatRef != null) {
//            ChatMessage chatMessage = new ChatMessage(newPostString, "George");
            ChatsMessage chatMessage = new ChatsMessage();
            chatMessage.message = newPostString;
            chatMessage.author = mAuth.getCurrentUser().getDisplayName().toString();

            mChatRef.push().setValue(chatMessage);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            //sendNotification(chatMessage.message, chatMessage.author);
            //Toast.makeText(view.getContext(),"New Messages Below!",Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(view.getContext(), "Database not available", Toast.LENGTH_SHORT).show();
        }
    }

//    public void sendNotification(String sender, String body) {
//
//        NotificationCompat.Builder mBuilder =
//                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
//                        .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
//                        .setContentTitle(sender)
//                        .setContentText(body);
//// Creates an explicit intent for an Activity in your app
//        Intent resultIntent = new Intent(this, ChatsActivity.class);
//
//// The stack builder object will contain an artificial back stack for the
//// started Activity.
//// This ensures that navigating backward from the Activity leads out of
//// your application to the Home screen.
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//// Adds the back stack for the Intent (but not the Intent itself)
//        stackBuilder.addParentStack(ChatsActivity.class);
//// Adds the Intent that starts the Activity to the top of the stack
//        stackBuilder.addNextIntent(resultIntent);
//        PendingIntent resultPendingIntent =
//                stackBuilder.getPendingIntent(
//                        0,
//                        PendingIntent.FLAG_UPDATE_CURRENT
//                );
//        mBuilder.setContentIntent(resultPendingIntent);
//        NotificationManager mNotificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        mNotificationManager.notify(0, mBuilder.build());
//
//    }


}