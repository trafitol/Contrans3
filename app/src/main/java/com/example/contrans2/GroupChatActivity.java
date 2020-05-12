package com.example.contrans2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;


public class GroupChatActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ImageButton SendMessageButton;
    private EditText userMessageInput;
    private ScrollView mScrollView;
    private TextView displayTextMessages;


    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef, GroupNameRef, GroupMessageKeyRef;

    private String currentGroupName, currentUserID, currentUserName, currentDate, currentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

//nu halen we dus de gklikte groepnaam uit groupsfragment en wordt opgeslagen in currentGroupName
        currentGroupName=getIntent().getExtras().get("groupName").toString();
        Toast.makeText(this, currentGroupName, Toast.LENGTH_SHORT).show();

        mAuth=FirebaseAuth.getInstance();
        currentUserID=mAuth.getCurrentUser().getUid();
        UsersRef= FirebaseDatabase.getInstance().getReference().child("Users");
        GroupNameRef= FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName);






    InitializeFields();

    getUserInfo();

    SendMessageButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SaveMessageInfoToDatabase();
            userMessageInput.setText("");
            mScrollView.fullScroll(ScrollView.FOCUS_DOWN);

        }
    });

    }

    //onStart executes whenever this activity starts
    @Override
    protected void onStart() {
        super.onStart();
        GroupNameRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            if(dataSnapshot.exists())
            {
                 DisplayMessages(dataSnapshot);

            }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if(dataSnapshot.exists())
                {
                    DisplayMessages(dataSnapshot);
                }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



    private void InitializeFields() {

    mToolbar=(Toolbar) findViewById(R.id.group_chat_bar_layout);
    setSupportActionBar(mToolbar);
    //support bar bovenin laat nu de currentgroupname zien waar op geklikt en geopend is.
    getSupportActionBar().setTitle("currentGroupName");



    SendMessageButton=(ImageButton)findViewById(R.id.send_message_button);
    userMessageInput=(EditText)findViewById(R.id.input_group_message);
    displayTextMessages=(TextView)findViewById(R.id.group_chat_text_display);
    mScrollView=(ScrollView)findViewById(R.id.my_scroll_view);
    }

    private void getUserInfo()
    {
        UsersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentUserName=dataSnapshot.child("name").getValue().toString();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void SaveMessageInfoToDatabase() {
        String message = userMessageInput.getText().toString();
        //creeert een sleutel voor ieder bericht voor de groep. En GroupNameRef is een referentie naar de key
        String messageKey=GroupNameRef.push().getKey();
        if(TextUtils.isEmpty(message))
        {
            Toast.makeText(this, "Please Write Message First...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Calendar ccalForDate = Calendar.getInstance();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy");
            currentDate=currentDateFormat.format(ccalForDate.getTime());

            Calendar ccalForTime = Calendar.getInstance();
            //moet er a.m. p.m. uren in?
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("HH:mm");
            currentTime=currentTimeFormat.format(ccalForDate.getTime());

//datum en tijd informatie moet in hashmap opgeslagen worden, zodat in firebase bewaard kan worden in de vorm van een groupmessagekey
            HashMap<String,Object> groupMessageKey = new HashMap<>();
            GroupNameRef.updateChildren(groupMessageKey);
            //Ref to the messagekey
            GroupMessageKeyRef = GroupNameRef.child(messageKey);


            HashMap<String, Object> messageInfoMap = new HashMap<>();
                messageInfoMap.put("name", currentUserName);
                messageInfoMap.put("message", message);
                messageInfoMap.put("date", currentDate);
                messageInfoMap.put("time", currentTime);

                GroupMessageKeyRef.updateChildren(messageInfoMap);
        }
    }

    //na het starten van de oncreate laat deze method messages zien die in de hashes zijn bewaard vanuit firebase
    private void DisplayMessages(DataSnapshot dataSnapshot)
    {
        Iterator iterator = dataSnapshot.getChildren().iterator();
//De eerste is de Date die moet worden laten zien
        while (iterator.hasNext())
        {
            String chatDate = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatMessage = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatName = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatTime = (String) ((DataSnapshot)iterator.next()).getValue();

            displayTextMessages.append((chatName + ":\n" + chatMessage + "\n" + chatTime + "\n" + chatDate + "\n\n\n"));
            //Is Scrollview de beste manier om de laatste berichten te laten zien?
            mScrollView.fullScroll(ScrollView.FOCUS_DOWN);

        }


    }



}
