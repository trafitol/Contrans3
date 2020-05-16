package com.example.contrans2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.provider.DocumentsContract;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    static {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    }

    private Toolbar mtoolbar;
    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private TabsAccessorAdapter myTabsAccessorAdapter;
    private FirebaseUser currentUser;
    //authenticatie
    private FirebaseAuth mAuth;
    //reference naar database
    private DatabaseReference RootRef,GroupNameKey,GroupNameKeyRef;
    private String currentDate, currentTime;





    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();


        //referance naar database
        RootRef = FirebaseDatabase.getInstance().getReference();
        GroupNameKey=FirebaseDatabase.getInstance().getReference().child("GroupsMetaData");


        mtoolbar=(Toolbar) findViewById(R.id.main_page_toolbar);
                setSupportActionBar(mtoolbar);
                getSupportActionBar().setTitle("Contrans2");

                myViewPager = (ViewPager) findViewById(R.id.main_tabs_pager);
                myTabsAccessorAdapter= new TabsAccessorAdapter(getSupportFragmentManager());
                myViewPager.setAdapter(myTabsAccessorAdapter);

                myTabLayout=(TabLayout)(findViewById(R.id.main_tabs));
                myTabLayout.setupWithViewPager(myViewPager);

    }

    //Als user niet is ingelogd ==null dan naar de loginactivity gestuurd worden
    @Override
    protected void onStart() 
    {
        super.onStart();
        if (currentUser == null)
        {
            SendUserToLogInActivity();
        }
        else
        {
            VerifyUserExistance();
        }
    }

    private void VerifyUserExistance()
    {
        String currentUserId = mAuth.getCurrentUser().getUid();
        //child is een subclass van de currentuserid waar we de username gaan retrieven c.q. verifieren
        //Zie firebase14 tutorial 19.35. users is child van rootref en currentuserid is weer subchild van users
        RootRef.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((dataSnapshot.child("name").exists()))
                {
                    Toast.makeText(MainActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                }
                else
                {
                  SendUserToSettingsActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    //zorgt dat er rechtsboven een menu'tje komt en dat ie kan inflaten
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options_menu,menu);
        return true;
    }

    //laten zien van menu optie, vanuit de xml
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.main_logout_option)
        {
            mAuth.signOut();
            SendUserToLogInActivity();
        }
        if(item.getItemId() == R.id.main_settings_option)
    {
        SendUserToSettingsActivity();
    }
        if(item.getItemId() == R.id.main_create_group_option)
        {
            RequestNewGroup();
        }
        if(item.getItemId() == R.id.main_find_friends_option)
        {
        }
return true;
    }


    private void RequestNewGroup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialog);
        builder.setTitle("Enter Group Name");

        final EditText groupNameField = new EditText(MainActivity.this);
        groupNameField.setHint("Place your GroupName");
        builder.setView(groupNameField);

        //aanmaken creer nieuwe groep knop
        builder.setPositiveButton("Against", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String groupName = groupNameField.getText().toString();

                if (TextUtils.isEmpty(groupName)) {
                    Toast.makeText(MainActivity.this, "State your subject in your language", Toast.LENGTH_SHORT).show();
                } else {
                    //Voert deze method uit, maakt groupname aan in firebase

                    CreateNewGroupAgainst(groupName);

                }


            }
        });

        builder.setNeutralButton("Favoring", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String groupName = groupNameField.getText().toString();

                        if (TextUtils.isEmpty(groupName)) {
                            Toast.makeText(MainActivity.this, "State your subject in your language", Toast.LENGTH_SHORT).show();
                        } else {
                            //Voert deze method uit, maakt groupname aan in firebase

                        CreateNewGroupFavoring(groupName);
                        }
                        }
                    });

                    //cancel knop voor creer groep
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();

                        }
                    });

        builder.show();


    }






    private void CreateNewGroupFavoring(final String groupName)
    {
        String groupUID = GroupNameKey.push().getKey();
        final String currentUserId = mAuth.getCurrentUser().getUid();
        Calendar ccalForDate = Calendar.getInstance();
        SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy");
        currentDate=currentDateFormat.format(ccalForDate.getTime());

        Calendar ccalForTime = Calendar.getInstance();
        //moet er a.m. p.m. uren in?
        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("HH:mm");
        currentTime=currentTimeFormat.format(ccalForDate.getTime());

//datum en tijd informatie moet in hashmap opgeslagen worden, zodat in firebase bewaard kan worden in de vorm van een groupmessagekey
        HashMap<String,Object> groupNameKeyMap = new HashMap<>();
        GroupNameKey.updateChildren(groupNameKeyMap);
        //Ref to the messagekey
        GroupNameKeyRef = GroupNameKey.child(groupUID);


        //child is een subclass van de currentuserid waar we de username gaan retrieven c.q. verifieren
        //Zie firebase14 tutorial 19.35. users is child van rootref en currentuserid is weer subchild van users
        RootRef.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    String profileName=dataSnapshot.child("name").getValue().toString();

                HashMap<String, Object> createGroupMap = new HashMap<>();
                createGroupMap.put("name", groupName);
                createGroupMap.put("user", currentUserId);
                createGroupMap.put("username", profileName);
                createGroupMap.put("favoring", "favor 1");
                createGroupMap.put("against", "against 0");
                createGroupMap.put("date_created", currentDate);
                createGroupMap.put("message", "no Messages for now");
                createGroupMap.put("time_created", currentTime);

                GroupNameKeyRef.updateChildren(createGroupMap)

                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                {
                                    Toast.makeText(MainActivity.this, groupName + " group is Created Succesfully", Toast.LENGTH_SHORT).show();



                                }
                            }
                        });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Error : " + databaseError, Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void CreateNewGroupAgainst(final String groupName)
    {
        String groupUID = GroupNameKey.push().getKey();
        final String currentUserId = mAuth.getCurrentUser().getUid();
        Calendar ccalForDate = Calendar.getInstance();
        SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy");
        currentDate=currentDateFormat.format(ccalForDate.getTime());

        Calendar ccalForTime = Calendar.getInstance();
        //moet er a.m. p.m. uren in?
        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("HH:mm");
        currentTime=currentTimeFormat.format(ccalForDate.getTime());

//datum en tijd informatie moet in hashmap opgeslagen worden, zodat in firebase bewaard kan worden in de vorm van een groupmessagekey
        HashMap<String,Object> groupNameKeyMap = new HashMap<>();
        GroupNameKey.updateChildren(groupNameKeyMap);
        //Ref to the messagekey
        GroupNameKeyRef = GroupNameKey.child(groupUID);

        RootRef.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String profileName=dataSnapshot.child("name").getValue().toString();

                HashMap<String, Object> createGroupMap = new HashMap<>();
                createGroupMap.put("name", groupName);
                createGroupMap.put("user", currentUserId);
                createGroupMap.put("username", profileName);
                createGroupMap.put("favoring", "favor 0");
                createGroupMap.put("against", "against 1");
                createGroupMap.put("date_created", currentDate);
                createGroupMap.put("message", "no Messages for now");
                createGroupMap.put("time_created", currentTime);

                GroupNameKeyRef.updateChildren(createGroupMap)

                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                {
                                    Toast.makeText(MainActivity.this, groupName + " group is Created Succesfully", Toast.LENGTH_SHORT).show();



                                }
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }




    //vanaf hier wordt user van mainactivity naar loginactivity gestuurd
    private void SendUserToLogInActivity() {
        Intent loginIntent = new Intent(MainActivity.this,LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }
    //vanaf hier wordt user van mainactivity SettingsActivity gestuurd via die sliding dropdown
    private void SendUserToSettingsActivity() {
        Intent settingsIntent = new Intent(MainActivity.this,SettingsActivity.class);
        //zodat user niet terug kan drukken
        settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(settingsIntent);
        finish();

    }
}
