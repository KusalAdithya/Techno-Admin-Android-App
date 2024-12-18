package com.waka.techoadmin;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.waka.techoadmin.model.Admin;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProfileFragment extends Fragment {

    private EditText emailText, fNameText, lNameText, mobileText;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;
    private ImageView userDpView;
    private Uri imgUri;
    private String imgUrl;
    private String fName, lName, mobile;
    private StorageReference storageReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View fragment, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(fragment, savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();


        userDpView = fragment.findViewById(R.id.userDpView);
        emailText = fragment.findViewById(R.id.emailTextProfile);
        fNameText = fragment.findViewById(R.id.fnameTextProfile);
        lNameText = fragment.findViewById(R.id.lnameTextProfile);
        mobileText = fragment.findViewById(R.id.mobileTextProfile);

        // get data --------------------------------------------------------------------------------
        db.collection("admin").document(firebaseAuth.getUid()).get().addOnSuccessListener(
                new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(com.google.firebase.firestore.DocumentSnapshot documentSnapshot) {

                        if (documentSnapshot.exists()) {

                            Admin admin = documentSnapshot.toObject(Admin.class);

                            emailText.setText(firebaseUser.getEmail());
                            fNameText.setText(admin.getfName());
                            lNameText.setText(admin.getlName());
                            mobileText.setText(admin.getMobile());

                            if (admin.getAdminDp() != null) {
                                Glide.with(requireContext()).load(admin.getAdminDp()).circleCrop().into(userDpView);
                            } else {
                                userDpView.setImageResource(R.drawable.user_non_dp);
                            }

                        }

                    }
                }
        ).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Data getting Fail", Toast.LENGTH_SHORT).show();
            }
        });

        //back Button ------------------------------------------------------------------------------
        fragment.findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new HomeFragment());
//                fragment.findViewById(R.id.bottomNavHome).setSelected(true);

            }

            public void loadFragment(Fragment fragment) {
                FragmentManager supportFragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, fragment);
                fragmentTransaction.commit();
            }
        });


        //dp btn------------------------------------------------------------------------------------
        userDpView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        activityResultLauncher.launch(Intent.createChooser(intent, "Select Image"));

                    }
                }
        );

        //update btn -------------------------------------------------------------------------------
        fragment.findViewById(R.id.updateBtnProfile).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        fName = fNameText.getText().toString();
                        lName = lNameText.getText().toString();
                        mobile = mobileText.getText().toString();

                        String mobileRegex = "[0][0-9]{9}";
                        Matcher mobileMatcher;
                        Pattern mobilePattern = Pattern.compile(mobileRegex);
                        mobileMatcher = mobilePattern.matcher(mobile);

                        if (fName.isEmpty()) {
                            fNameText.setError("Please enter your first name");
                            fNameText.requestFocus();
                        } else if (lName.isEmpty()) {
                            lNameText.setError("Please enter your last name");
                            lNameText.requestFocus();
                        } else if (mobile.isEmpty()) {
                            mobileText.setError("Please enter your mobile number");
                            mobileText.requestFocus();
                        } else if (!mobileMatcher.matches()) {
                            mobileText.setError("Please enter your valid mobile number");
                            mobileText.requestFocus();
                        } else {

                            //add user to db
                            if (imgUri != null) { //set dp
                                imageUpload();

                            } else { // not set dp

                                Admin user = new Admin();
                                user.setEmail(firebaseUser.getEmail());
                                user.setfName(fName);
                                user.setlName(lName);
                                user.setMobile(mobile);

                                db.collection("admin").document(firebaseUser.getUid())
                                        .set(user)
                                        .addOnSuccessListener(
                                                new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(getContext(), "Updated Successfully", Toast.LENGTH_LONG).show();
                                                        loadFragment(new ProfileFragment());
                                                    }
                                                }
                                        ).addOnFailureListener(
                                                new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(getContext(), "Update Fail.", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                        );

                            }


                        }


                    }
                }
        );


    }


    private void imageUpload() {
        String path = imgUri.getPath() + UUID.randomUUID().toString();
        StorageReference reference = storageReference = FirebaseStorage.getInstance().getReference()
                .child("userImages/").child(path);
        reference.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete()) ;
                imgUrl = uriTask.getResult().toString();

                //add to db ----------------------------------------------------------------
                Admin user = new Admin();
                user.setEmail(firebaseUser.getEmail());
                user.setfName(fName);
                user.setlName(lName);
                user.setMobile(mobile);
                user.setAdminDp(imgUrl);

                db.collection("admin").document(firebaseUser.getUid())
                        .set(user)
                        .addOnSuccessListener(
                                new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(getContext(), "Updated Successfully", Toast.LENGTH_LONG).show();
                                        loadFragment(new ProfileFragment());
                                    }
                                }
                        ).addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getContext(), "Update Fail.", Toast.LENGTH_LONG).show();
                                    }
                                }
                        );
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Image add Fail.", Toast.LENGTH_LONG).show();
            }
        });
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        imgUri = result.getData().getData();
                        Glide.with(requireContext()).load(imgUri).circleCrop().into(userDpView);
                    }
                }
            }
    );

    public void loadFragment(Fragment fragment) {
        FragmentManager supportFragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commit();
    }
}