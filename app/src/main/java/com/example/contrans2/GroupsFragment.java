package com.example.contrans2;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;



//De GroupsFragment laat het stuk zien van de groepen, op de mainActivity, en de interactie daarvan met de database
//Indien de user op een bepaalde group drukt gaat deze naar groupChatActivity en wordt er een groupchat geopend.
/**
 * A simple {@link Fragment} subclass.
 */
public class GroupsFragment extends Fragment {

    private View groupFragmentView;
    private ListView list_view;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> list_of_groups = new ArrayList<>();
    private DatabaseReference GroupRef;

    public GroupsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Layout Inflater pakt de xml als input en vult met de view objecten (listview/arrayadapter)
        groupFragmentView = inflater.inflate(R.layout.fragment_groups, container, false);

        GroupRef= FirebaseDatabase.getInstance().getReference().child("Groups");

IntializeFields();

RetrieveAndDisplayGroups();

list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    @Override
//
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
    {
        //wanneer er geklikt wordt op groep, dan wordt dit met database kenbaar gemaakt
        String currentGroupName = adapterView.getItemAtPosition(position).toString();
        //hier wordt de user van de groupfragment naar groupchatactivity gestuurd
        Intent groupChatIntent = new Intent(getContext(), GroupChatActivity.class);
        //moeten wel laten weten welke group ze moeten op groupChatActivity
        groupChatIntent.putExtra("groupName", currentGroupName);
        startActivity(groupChatIntent);
    }
});
        return groupFragmentView;

    }



    private void IntializeFields() {
        list_view=(ListView) groupFragmentView.findViewById(R.id.list_view);
        arrayAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,list_of_groups);
list_view.setAdapter(arrayAdapter);

    }

    //
    private void RetrieveAndDisplayGroups() {
        //wordt een nieuwe groep aan groepen toegevoegd
    GroupRef.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
        {
            //Voor Volledige groep wordt een hashset gemaakt
            Set<String>set = new HashSet<>();
            //Iterator gaat op een bepaalde manier door de groups heen om te indexeren
            Iterator iterator=dataSnapshot.getChildren().iterator();
            //zodra de groeplijst een volgende value laat zien
            while (iterator.hasNext())
            {//zodat er geen duplicates komen
set.add(((DataSnapshot)iterator.next()).getKey());
            }
            list_of_groups.clear();
            list_of_groups.addAll(set);
            arrayAdapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError)
        {

        }
    });
    }


}
