package com.live.tj98.chatsnap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.live.tj98.chatsnap.ChatRoomDesc;
import com.live.tj98.chatsnap.ChatsActivity;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private TextView mMessageTextView;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mMessageRef = database.getReference("message");
    DatabaseReference mDirectoryRef = database.getReference("directory");
    DatabaseReference mChatRoomRef = database.getReference("rooms");
    private EditText mNewRoomEditText;
    private RecyclerView mChatRoomRecyclerView;
    private FirebaseRecyclerAdapter<ChatRoomDesc, ChatRoomViewHolder> mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNewRoomEditText = (EditText) findViewById(R.id.newRoomText);
        mChatRoomRecyclerView = (RecyclerView) findViewById(R.id.chatRoomRecyclerView);
        mChatRoomRecyclerView.setHasFixedSize(true);
        //mChatRoomRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mMessageTextView = (TextView) findViewById(R.id.messageTextView2);
        mMessageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mMessageTextView.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // nothing
            }
        });
    }

    public void newRoomClick(View view) {
        String newRoomName = mNewRoomEditText.getText().toString();
        if (newRoomName.length() == 0) {
            return;
        }
        String key = mChatRoomRef.push().getKey();
        ChatRoomDesc chatRoomDesc = new ChatRoomDesc(newRoomName, key);
        mDirectoryRef.push().setValue(chatRoomDesc);
        mNewRoomEditText.setText("");
    }


    @Override
    protected void onStart() {
        super.onStart();

        mAdapter = new FirebaseRecyclerAdapter<ChatRoomDesc, ChatRoomViewHolder>(ChatRoomDesc.class,
                android.R.layout.simple_list_item_1, ChatRoomViewHolder.class, mDirectoryRef) {
            @Override
            protected void populateViewHolder(ChatRoomViewHolder viewHolder,
                                              final ChatRoomDesc model, int position) {
                viewHolder.mTextView.setText(model.name);

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Launch PostDetailActivity
                        Intent intent = new Intent(getApplicationContext(), ChatsActivity.class);
                        intent.putExtra(ChatsActivity.EXTRA_CHAT_KEY, model.index);
                        startActivity(intent);
                    }
                });
            }

            @Override
            protected ChatRoomDesc parseSnapshot(DataSnapshot snapshot) {
                Log.d("mDirectoryRef", "parseSnapshot: " + snapshot.toString());
                return super.parseSnapshot(snapshot);
            }
        };

        mChatRoomRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.cleanup();
        }
    }

    public static class ChatRoomViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;

        public ChatRoomViewHolder(View view) {
            super(view);
            mTextView = (TextView) view.findViewById(android.R.id.text1);
        }
    }
}