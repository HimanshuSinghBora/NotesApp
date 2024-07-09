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
import com.example.notesapp.databinding.FragmentDeletePageBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class DeletePage extends Fragment {
    private FragmentDeletePageBinding binding;
    int keyId, noteId;
    String Title;
    private RecyclerViewAdapter recyclerViewAdapter;
    private ArrayList<RecyclerData> recyclerDataArrayList;
    Set<String> keySet, noteSet;
    int position;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDeletePageBinding.inflate(inflater, container, false);
        SharedPreferences Users = getActivity().getSharedPreferences("User", MODE_PRIVATE);
        SharedPreferences.Editor editor = Users.edit();
        String name = Users.getString("name", "");
        noteId = Users.getInt("noteId", 0);
        keyId = Users.getInt("keyId", 0);
        noteId = Users.getInt("noteId", 0);
        keySet = Users.getStringSet("keySet", new HashSet<String>());
        noteSet = Users.getStringSet("noteSet", new HashSet<String>());
        String userId1 = String.valueOf(noteId);
        String keyArray[] = keySet.toArray(new String[0]);
        for (int i = 0; i < keyArray.length; i++) {
            int j= Integer.parseInt(keyArray[i]);
            if (j == noteId) {
                position = i;
            }
        }
        if (noteId != keyId) {
            String noteArray[] = noteSet.toArray(new String[0]);
            Title = noteArray[position];
            binding.note.setText(Title);
            binding.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // below line is to display our snackbar with action.
                    keySet = Users.getStringSet("keySet", new HashSet<String>());
                    Log.w("key", String.valueOf(keySet));
                    keySet.remove(String.valueOf(noteId));
                    Log.w("key", String.valueOf(keySet));
                    noteSet = Users.getStringSet("noteSet", new HashSet<String>());
                    Log.w("note", String.valueOf(noteSet));
                    noteSet.remove(String.valueOf(Title));
                    Log.w("note", String.valueOf(noteSet));
                    editor.putStringSet("keySet", keySet);
                    editor.putStringSet("noteSet", noteSet);
                    editor.apply();
                    FragmentManager manager1 = getParentFragmentManager();
                    FragmentTransaction tr = manager1.beginTransaction();
                    tr.replace(R.id.frameLayout, new NotesPage());
                    tr.commit();
                }
            });
        }
        return binding.getRoot();
    }
}