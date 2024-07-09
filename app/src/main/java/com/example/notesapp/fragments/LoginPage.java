package com.example.notesapp.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.notesapp.R;
import com.example.notesapp.databinding.FragmentLoginPageBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashSet;
import java.util.Set;

public class LoginPage extends Fragment {
    private FragmentLoginPageBinding binding;
    FirebaseAuth auth;
    GoogleSignInClient googleSignInClient;
    String val1,val2;
    int val3;
    Set<String> keySet;

    private final ActivityResultLauncher<Intent> activityResultLauncher= registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode()== Activity.RESULT_OK){
                Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                try {
                    GoogleSignInAccount signInAccount = accountTask.getResult(ApiException.class);
                    AuthCredential authCredential = GoogleAuthProvider.getCredential(signInAccount.getIdToken(),null);
                    SharedPreferences Users= getActivity().getSharedPreferences("User",MODE_PRIVATE);
                    FragmentManager manager=getParentFragmentManager();
                    auth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                auth = FirebaseAuth.getInstance();
                                val1=auth.getCurrentUser().getDisplayName();
                                val2=auth.getCurrentUser().getEmail();
                                SharedPreferences.Editor editor= Users.edit();
                                editor.putBoolean("flag",true);
                                editor.putString("name",val1);
                                editor.apply();
                                DatabaseReference userReferences = FirebaseDatabase.getInstance().getReference(val1);
                                userReferences.child(val1).orderByChild("tile").equalTo("").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        val2=snapshot.getKey();
                                        val3= Math.toIntExact(snapshot.getChildrenCount());
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                if(val1==val2){
                                    for(int i = 0; i<=val3; i++){
                                        Users.getStringSet("keySet",new HashSet<String>());
                                        keySet.add(String.valueOf(i));
                                        editor.putStringSet("keySet",keySet);
                                        editor.apply();
                                        FragmentTransaction tr = manager.beginTransaction();
                                        tr.replace(R.id.frameLayout,new NotesPage());
                                        tr.commit();
                                        Toast.makeText(getContext(),"Signed in successfully",Toast.LENGTH_SHORT).show();
                                    }

                                }else {
                                    FragmentTransaction tr = manager.beginTransaction();
                                    tr.replace(R.id.frameLayout, new NewNote());
                                    tr.commit();
                                    Toast.makeText(getContext(), "Signed in successfully", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else {
                                Toast.makeText(getContext(),"Failed to sigh in:"+task.getException(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (ApiException e) {
                    e.printStackTrace();
                }
            }
        }
    });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                requestIdToken(getString(R.string.client_id)).
                requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(getContext(),gso);

        auth=FirebaseAuth.getInstance();
        binding= FragmentLoginPageBinding.inflate(inflater, container, false);
        binding.signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= googleSignInClient.getSignInIntent();
                activityResultLauncher.launch(intent);


            }
        });
        return binding.getRoot();

    }
}