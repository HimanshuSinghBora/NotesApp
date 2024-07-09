package com.example.notesapp.fragments;

import static android.content.Context.MODE_PRIVATE;

import static kotlin.jvm.internal.Reflection.function;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notesapp.R;
import com.example.notesapp.databinding.FragmentNotesPageBinding;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


public class NotesPage extends Fragment {
    private FragmentNotesPageBinding binding;
    private RecyclerViewAdapter recyclerViewAdapter;
    private ArrayList<RecyclerData> recyclerDataArrayList;
    private String Title;
    Set<String> keySet,noteSet;
    final String[] w = new String[1];
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       // Inflate the layout for this fragment
        binding= FragmentNotesPageBinding.inflate(inflater, container, false);
        SharedPreferences Users = requireActivity().getSharedPreferences("User", MODE_PRIVATE);
        SharedPreferences.Editor editor = Users.edit();
        String name = Users.getString("name", "");
        DatabaseReference userReferences = FirebaseDatabase.getInstance().getReference(name);
        keySet=Users.getStringSet("keySet",new HashSet<String>());
        noteSet=Users.getStringSet("noteSet",new HashSet<String>());
        Log.w("note", String.valueOf(keySet)); Log.w("note", String.valueOf(noteSet));
        recyclerDataArrayList = new ArrayList<>();
        String noteArray[]=noteSet.toArray(new String[0]);
        for (String i : keySet) {
            try {
                userReferences.child(String.valueOf(i)).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        RecyclerData userDetails = snapshot.getValue(RecyclerData.class);
                        assert userDetails != null;
                        w[0] = userDetails.getTitle();
                        Title = w[0];
                        recyclerDataArrayList.add(new RecyclerData(Title));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            } catch (Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        recyclerViewAdapter = new RecyclerViewAdapter(recyclerDataArrayList, getContext());
        LinearLayoutManager manager = new LinearLayoutManager(getContext());

        // setting layout manager for our recycler view.
        binding.recycle.setLayoutManager(manager);
        binding.recycle.setAdapter(recyclerViewAdapter);
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                keySet=Users.getStringSet("keySet",new HashSet<String>());
                noteSet=Users.getStringSet("noteSet",new HashSet<String>());
                Object[] idArray = keySet.toArray();
                String id = String.valueOf(idArray[position]);
                editor.putInt("noteId", Integer.parseInt(id));
                editor.apply();
                FragmentManager manager1 =getParentFragmentManager();
                FragmentTransaction tr = manager1.beginTransaction();
                String current = "NotesPage";
                tr.replace(R.id.frameLayout, new NewNote());
                tr.addToBackStack(current);
                tr.commit();

            }
        }).attachToRecyclerView(binding.recycle);
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                // this method is called
                // when the item is moved.
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // this method is called when we swipe our item to right direction.
                // on below line we are getting the item at a particular position.
                RecyclerData deletedCourse = recyclerDataArrayList.get(viewHolder.getAdapterPosition());
                int position = viewHolder.getAdapterPosition();
                keySet=Users.getStringSet("keySet",new HashSet<String>());
                Object[] idArray = keySet.toArray();
                String id = String.valueOf(idArray[position]);
                editor.putInt("noteId", Integer.parseInt(id));
                editor.apply();
                FragmentManager manager1 =getParentFragmentManager();
                FragmentTransaction tr = manager1.beginTransaction();
                String current = "NotesPage";
                tr.replace(R.id.frameLayout, new DeletePage());
                tr.addToBackStack(current);
                tr.commit();

                // this method is called when item is swiped.
                // below line is to remove item from our array list.

            }
            // at last we are adding this
            // to our recycler view.
        }).attachToRecyclerView(binding.recycle);
        binding.addNote.setOnClickListener(view -> {
            int st = Users.getInt("keyId",0);
            editor.putInt("noteId",st);
            editor.apply();
            FragmentManager manager1 =getParentFragmentManager();
            FragmentTransaction tr = manager1.beginTransaction();
            String current = "NotesPage";
            tr.replace(R.id.frameLayout, new NewNote());
            tr.addToBackStack(current);
            tr.commit();
        });
        return binding.getRoot();
    }
}