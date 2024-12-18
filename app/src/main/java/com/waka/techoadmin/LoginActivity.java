package com.waka.techoadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.waka.techoadmin.model.Admin;

import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private EditText emailText, passwordText;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db =FirebaseFirestore.getInstance();

        coordinatorLayout = findViewById(R.id.coordinatorLayout);

        emailText = findViewById(R.id.emailText);
        passwordText = findViewById(R.id.passwordText);

        // Login Btn -------------------------------------------------------------------------------
        findViewById(R.id.loginBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailText.getText().toString();
                String password = passwordText.getText().toString();

                if (email.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) { //TextUtils.isEmpty(email)
//                    Toast.makeText(LoginActivity.this, "Please enter your email", Toast.LENGTH_LONG).show();
                    emailText.requestFocus();
                    emailText.setError("Please enter your email");
                } else if (password.isEmpty()) {
//                    Toast.makeText(LoginActivity.this, "Please enter your password", Toast.LENGTH_LONG).show();
                    passwordText.requestFocus();
                    passwordText.setError("Please enter your password");
                } else {
                    adminLogin(email, password);

                }
            }
        });


        // Forgot password btn ---------------------------------------------------------------------
        findViewById(R.id.fogotPassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailText.getText().toString();
                if (email.isEmpty()) {
                    emailText.setError("Firstly enter your email");
                    emailText.requestFocus();
                } else {
                    forgotPassword(email);
                }
            }
        });
    }


    //Forgot password method -----------------------------------------------------------------------
    private void forgotPassword(String email) {

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Snackbar.make(coordinatorLayout, "Please check your email inbox for password reset link", Snackbar.LENGTH_LONG)
                            .setAction("Open Gmail",
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            gmailIntent();
                                        }
                                    }
                            ).show();
                } else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e) {
                        emailText.setError("User does not exists.Please register again");
                        emailText.requestFocus();
                    } catch (Exception e) {
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                }

            }
        });
    }

    public void gmailIntent() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_APP_EMAIL);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    // Admin login ---------------------------------------------------------------------------------
    private void adminLogin(String email, String password) {
        firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();

                            if (user != null) {

                                if (!user.isEmailVerified()) {
                                    user.sendEmailVerification();

                                    Snackbar.make(coordinatorLayout, "Please Verify Your Email", Snackbar.LENGTH_LONG)
                                            .setAction("Open Gmail",
                                                    new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            gmailIntent();
                                                        }
                                                    }
                                            ).show();
                                    return;
                                }

                                // search admin ----------------------------------------------------
                                db.collection("admin").get().addOnCompleteListener(
                                        new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                QuerySnapshot result = task.getResult();
                                                List<DocumentSnapshot> documents = result.getDocuments();
                                                documents.forEach(u -> {

                                                    String emailAdmin = u.getString("email");

                                                    if (emailAdmin.equals(email)) {
                                                        Toast.makeText(getApplicationContext(), "Logged", Toast.LENGTH_SHORT).show();
                                                        System.out.println("logged");
                                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                    } else {
                                                        Toast.makeText(LoginActivity.this, "You are not an Admin!", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    }
                                                });
                                            }
                                        }
                                ).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(LoginActivity.this, "Connection Fail, Please try again later!", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Please enter valid details", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

    }
}