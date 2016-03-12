package sudhanva.narayana.cryptick;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sudhanva.narayana.cryptick.adapter.MessageChatAdapter;
import sudhanva.narayana.cryptick.model.MessageChatModel;
import sudhanva.narayana.cryptick.model.UsersChatModel;
import sudhanva.narayana.cryptick.utils.Constants;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = ChatActivity.class.getSimpleName();
    /* Sender and Recipient status*/
    private static final int SENDER_STATUS = 0;
    private static final int RECIPIENT_STATUS = 1;
    /* unique Firebase ref for this chat */
    public Firebase mFirebaseTick;
    /* Listen to change in chat in firabase-remember to remove it */
    public ChildEventListener mMessageChatListener;
    public String mTick;
    public String mRemoveTick;
    private RecyclerView mChatRecyclerView;
    private TextView mUserMessageChatText;
    private MessageChatAdapter mMessageChatAdapter;
    /* Recipient uid */
    private String mRecipientUid;
    /* Sender uid */
    private String mSenderUid;
    /* unique Firebase ref for this chat */
    private Firebase mFirebaseMessagesChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        final Button button = (Button) findViewById(R.id.sendUserMessage);

        button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(ChatActivity.this, button);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        mTick = item.getTitle().toString();
                        button.setText(mTick);
                        return true;
                    }
                });
                popup.show();
                return true;
            }
        });


        // Get information from the previous activity
        Intent getUsersData = getIntent();
        UsersChatModel usersDataModel = getUsersData.getParcelableExtra(Constants.KEY_PASS_USERS_INFO);

        // Set recipient uid
        mRecipientUid = usersDataModel.getRecipientUid();

        // Set sender uid;
        mSenderUid = usersDataModel.getCurrentUserUid();

        // Reference to recyclerView and text view
        mChatRecyclerView = (RecyclerView) findViewById(R.id.chat_recycler_view);
        mUserMessageChatText = (TextView) findViewById(R.id.chat_user_message);

        // Set recyclerView and adapter
        mChatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mChatRecyclerView.setHasFixedSize(true);

        // Initialize adapter
        List<MessageChatModel> emptyMessageChat = new ArrayList<MessageChatModel>();
        mMessageChatAdapter = new MessageChatAdapter(emptyMessageChat);

        // Set adapter to recyclerView
        mChatRecyclerView.setAdapter(mMessageChatAdapter);

        // Initialize firebase for this chat
        mFirebaseMessagesChat = new Firebase(Constants.FIREBASE_URL).child(Constants.CHILD_CHAT).child(usersDataModel.getChatRef());
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Log.e(TAG, " I am onDestroy");
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.e(TAG, " I am onStart");
        mMessageChatListener = mFirebaseMessagesChat.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildKey) {

                if (dataSnapshot.exists()) {
                    // Log.e(TAG, "A new chat was inserted");

                    MessageChatModel newMessage = dataSnapshot.getValue(MessageChatModel.class);
                    if (newMessage.getSender().equals(mSenderUid)) {
                        newMessage.setRecipientOrSenderStatus(SENDER_STATUS);
                    } else {
                        newMessage.setRecipientOrSenderStatus(RECIPIENT_STATUS);
                    }

                    mMessageChatAdapter.refillAdapter(newMessage);
                    mChatRecyclerView.scrollToPosition(mMessageChatAdapter.getItemCount() - 1);
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "I am onPause");

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "I am onStop");

        // Remove listener
        if (mMessageChatListener != null) {
            // Remove listener
            mFirebaseMessagesChat.removeEventListener(mMessageChatListener);
        }
        // Clean chat message
        mMessageChatAdapter.cleanUp();

    }


    public void sendMessageToFireChat(View sendButton) {
        String senderMessage = mUserMessageChatText.getText().toString();
        senderMessage = senderMessage.trim();

        if (!senderMessage.isEmpty()) {

            // Log.e(TAG, "send message");
            mFirebaseTick = mFirebaseMessagesChat.push();

            mRemoveTick = (mFirebaseMessagesChat + "/" + mFirebaseTick.getKey() + "/");
            // Send message to firebase
            Map<String, String> newMessage = new HashMap<String, String>();
            newMessage.put("sender", mSenderUid); // Sender uid
            newMessage.put("recipient", mRecipientUid); // Recipient uid
            newMessage.put("message", senderMessage); // Message
            newMessage.put("tick", mTick);// Tick
            newMessage.put("tickURL",mRemoveTick);

            final Button button = (Button) findViewById(R.id.sendUserMessage);
            button.setText("send");

            mFirebaseTick.setValue(newMessage);

            // Clear text
            mUserMessageChatText.setText("");


            System.out.println(mRemoveTick);
        }
    }
}


