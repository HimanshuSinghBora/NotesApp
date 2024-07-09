package com.example.notesapp.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.notesapp.R;
import com.example.notesapp.databinding.FragmentNewNoteBinding;
import com.example.notesapp.databinding.FragmentNotesPageBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class NewNote extends Fragment {
    int keyId,noteId;
    String Title;
    Set<String> keySet,noteSet;
    int position;

    private FragmentNewNoteBinding binding;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding= FragmentNewNoteBinding.inflate(inflater, container, false);
        SharedPreferences Users= getActivity().getSharedPreferences("User",MODE_PRIVATE);
        String name = Users.getString("name","");
        keyId = Users.getInt("keyId",0);
        noteId = Users.getInt("noteId",0);
        keySet=Users.getStringSet("keySet",new HashSet<String>());
        noteSet=Users.getStringSet("noteSet",new HashSet<String>());
        String userId1 = String.valueOf(noteId);
        String keyArray[]=keySet.toArray(new String[0]);
        for(int i=0;i<keyArray.length;i++){
            int j= Integer.parseInt(keyArray[i]);
            if (j == noteId) {
                position = i;
            }
        }
        if(noteId!=keyId){
            String noteArray[]=noteSet.toArray(new String[0]);
            Title=noteArray[position];
            binding.newNote.setText(Title);
            binding.add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatabaseReference userReferences= FirebaseDatabase.getInstance().getReference(name);
                    SharedPreferences.Editor editor= Users.edit();
                    editor.putInt("noteId",0);
                    editor.apply();
                    String Title = binding.newNote.getText().toString();
                    RecyclerData userDetails = new RecyclerData(Title);
                    userReferences.child(userId1).setValue(userDetails);
                    noteArray[position]=Title;
                    noteSet=new HashSet<>(Arrays.asList(noteArray));
                    editor.putStringSet("noteSet",noteSet);
                    editor.apply();
                    FragmentManager manager=getParentFragmentManager();
                    FragmentTransaction tr = manager.beginTransaction();
                    tr.replace(R.id.frameLayout, new NotesPage());
                    tr.commit();
                }
            });
        }
        else {
            binding.add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatabaseReference userReferences = FirebaseDatabase.getInstance().getReference(name);
                    String userId1 = String.valueOf(keyId);
                    keySet.add(userId1);
                    keyId++;
                    SharedPreferences.Editor editor = Users.edit();
                    editor.putInt("keyId", keyId);
                    editor.putStringSet("keySet",keySet);
                    editor.apply();
                    String Title = binding.newNote.getText().toString();
                    RecyclerData userDetails = new RecyclerData(Title);
                    userReferences.child(userId1).setValue(userDetails);
                    noteSet.add(Title);
                    editor.putStringSet("noteSet",noteSet);
                    editor.apply();
                    FragmentManager manager = getParentFragmentManager();
                    FragmentTransaction tr = manager.beginTransaction();
                    tr.replace(R.id.frameLayout, new NotesPage());
                    tr.commit();
                }
            });
        }
        return binding.getRoot();
    }
}