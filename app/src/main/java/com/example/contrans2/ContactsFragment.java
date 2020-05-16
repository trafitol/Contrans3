package com.example.contrans2;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends Fragment {

    private RecyclerView myGroupsRecyclerView;
    private View myGroupsView;
    private DatabaseReference GroupsRequestMetaRef;


    public ContactsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment, creert de view met mygroupsview
        myGroupsView =  inflater.inflate(R.layout.fragment_contacts, container, false);


        myGroupsRecyclerView = (RecyclerView) myGroupsView.findViewById(R.id.recyclerview_My_Groups_Layout);
        //of moet getContext op this staan?
        myGroupsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        GroupsRequestMetaRef = FirebaseDatabase.getInstance().getReference().child("GroupsMetaData");
        return myGroupsView;

    }

    @Override
    public void onStart() {
        super.onStart();
    FirebaseRecyclerOptions options =
            new FirebaseRecyclerOptions.Builder<GetSetOpenGroups>()
            .setQuery(GroupsRequestMetaRef, GetSetOpenGroups.class)
            .build();

        FirebaseRecyclerAdapter<GetSetOpenGroups, myViewHolderMyGroups> adapter
                = new FirebaseRecyclerAdapter<GetSetOpenGroups, myViewHolderMyGroups>(options) {
            @Override
            protected void onBindViewHolder(@NonNull myViewHolderMyGroups holder, int position, @NonNull GetSetOpenGroups model) {
            holder.myGroupsGroupName.setText(model.getName());
            holder.myGroupsByUser.setText(model.getUser());
            holder.myGroupsFavoring.setText(model.getFavoring());
            holder.myGroupsAgainst.setText(model.getAgainst());
            holder.myGroupsMessage.setText(model.getMessage());
            holder.myGroupsTime.setText(model.getTime_created());

            }

            @NonNull
            @Override
            public myViewHolderMyGroups onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_my_groups_new, parent, false);
                myViewHolderMyGroups viewHolderMyGroups = new myViewHolderMyGroups(view);
                return viewHolderMyGroups;

            }
        };
        adapter.startListening();
        myGroupsRecyclerView.setAdapter(adapter);

    }

    //deze static class is bedoeld voor de onbindview en oncreateviewholder
    public static class myViewHolderMyGroups extends RecyclerView.ViewHolder
    {
        TextView myGroupsGroupName, myGroupsMessage, myGroupsByUser, myGroupsTime, myGroupsFavoring, myGroupsAgainst;


        public myViewHolderMyGroups(@NonNull View itemView) {
            super(itemView);

            myGroupsGroupName = itemView.findViewById(R.id.my_Groups_Group_name);
            myGroupsMessage = itemView.findViewById(R.id.my_Groups_Message);
            myGroupsByUser = itemView.findViewById(R.id.my_Groups_By_User);
            myGroupsTime = itemView.findViewById(R.id.my_Groups_Time);
            myGroupsFavoring = itemView.findViewById(R.id.my_Groups_Favoring);
            myGroupsAgainst = itemView.findViewById(R.id.my_Groups_Against);


        }
    }


}
