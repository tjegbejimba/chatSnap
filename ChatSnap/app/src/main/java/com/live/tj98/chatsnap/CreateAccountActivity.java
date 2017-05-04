package com.live.tj98.chatsnap;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CreateAccountActivity extends AppCompatActivity {
    @BindView(R.id.editTextName)
    EditText name;
    @BindView(R.id.confirmPassword)
    EditText confirmPassword;
    @BindView(R.id.emailViewcreate)
    EditText email;
    @BindView(R.id.passswordViewcreate)
    EditText password;
    @BindView(R.id.loginButtoncreate)
    Button login;
    @BindView(R.id.createAccountButtoncreate)
    Button createAccount;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private char[] symbols = {'@', '%', '+', '\\', '/', '!', '#', '$', '^', '?', ':', '.', '(', ')', '{', '}', '[', ']', '~', '`', '-', '_'};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_account);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        // if user is already signed in mAth.getcurret != nnull
    }
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    @Override
    public void onResume(){
        super.onResume();
        mAuth.addAuthStateListener(mAuthListener);
    }

    public void createAccount(View v) {
        System.out.println("started creatAccount method");
        String tempE = email.getText().toString();
        String tempP = password.getText().toString();
        Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
        Matcher m = p.matcher(email.getText().toString());
        if (m.matches() && verifyPassword(tempP)) {
            mAuth.createUserWithEmailAndPassword(tempE, tempP)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                Toast.makeText(CreateAccountActivity.this, "Failed to create account",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                updateProfile();
                                Toast.makeText(CreateAccountActivity.this, "Account Created",
                                        Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(CreateAccountActivity.this, LoginActivity.class);
                                mAuth.getCurrentUser().sendEmailVerification();
                                startActivity(intent);
                            }

                            // ...
                        }
                    });
        } else {
            if (!m.matches()) {
                email.setError("Invalid Email input");
            }
            if (!verifyPassword(tempP)) {
                password.setError("Invalid Password input");
            }
        }
    }

    public void updateProfile()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name.getText().toString())
                .build();

        user.updateProfile(profileUpdates);
    }

    private boolean verifyPassword(String password) {
        if (password.contains(" ") || password.contains("\t")) {
            return false;
        } else if (password.length() < 8 || password.length() > 25) {
            Toast.makeText(CreateAccountActivity.this, "needs to be at least 8 charecters",
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (getNumInts(password) < 1) {
            Toast.makeText(CreateAccountActivity.this, "need at least one number",
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (!checkSymbols(password)) {
            Toast.makeText(CreateAccountActivity.this, "need at least one symbol",
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (!hasLowerCase(password) || !hasUpperCase(password)) {
            Toast.makeText(CreateAccountActivity.this, "Need at least one uppercase and one lowercase",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;

    }

    private int getNumInts(String password) {
        int counter = 0;
        for (char item : password.toCharArray()) {
            if (item >= '0' && item <= '9') {
                counter++;
            }
        }
        return counter;
    }

    private boolean checkSymbols(String password) {
        boolean hasSymbol = false;
        for (char item : password.toCharArray()) {
            for (char symbol : symbols) {
                if (symbol == (item)) {
                    hasSymbol = true;
                }
            }
        }
        return hasSymbol;
    }

    private boolean hasUpperCase(String password) {
        boolean hasUpperCase = false;
        for (char item : password.toCharArray()) {
            if (item >= 'A' && item <= 'Z') {
                hasUpperCase = true;
            }
        }
        return hasUpperCase;
    }

    private boolean hasLowerCase(String password) {
        boolean hasLowerCase = false;
        for (char item : password.toCharArray()) {
            if (item >= 'a' && item <= 'z') {
                hasLowerCase = true;
            }
        }
        return hasLowerCase;
    }

    public void signIn(View v) {
        Intent intent = new Intent(this, LoginActivity.class);
        this.startActivity(intent);
    }
}
