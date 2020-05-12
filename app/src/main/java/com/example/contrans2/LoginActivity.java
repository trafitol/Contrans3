package com.example.contrans2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;

// Zie tutorial 8 om een nieuwe button toe te voegen
    private Button LoginButton, PhoneLoginButton;
    private EditText UserEmail, UserPassword;
    private TextView RegisterOnLoginPage, ForgetPasswordLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//connectie met firebase auth instance maken
        mAuth = FirebaseAuth.getInstance();
        //Zodat er automatisch ingelogd kan worden en naar mainactivity gestuurd kan worden


        InitializeFields();
        RegisterOnLoginPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToRegisterActivity();
            }
        });

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllowUserToLogin();
            }
        });

        PhoneLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent phoneLoginIntent = new Intent(LoginActivity.this, PhoneLoginActivity.class);
                startActivity(phoneLoginIntent);
            }
        });


        }
// inloggen wanneer email en password wordt ingevuld
    private void AllowUserToLogin() {
        String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();

        //als email of password leeg is...
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter email...", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter password...", Toast.LENGTH_SHORT).show();
        }
        //wanneer email en password is ingevuld
        else{
            loadingBar.setTitle("Sign In");
            loadingBar.setMessage("Please wait, while we are logging you in");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

    mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
    @Override
    public void onComplete(@NonNull Task<AuthResult> task) {
        //als geverifieerd is, doorsturen naar mainactivity
        if (task.isSuccessful()){
            SendUserToMainActivity();
            Toast.makeText(LoginActivity.this, "Logged in Succesfully", Toast.LENGTH_SHORT).show();
        loadingBar.dismiss();
        }
        //wanneer niet gelukt is
        else
        {
            String message = task.getException().toString();
            Toast.makeText(LoginActivity.this, "Error : " + message, Toast.LENGTH_SHORT).show();
        loadingBar.dismiss();
        }

    }
});

        }

    }

    private void InitializeFields() {
        LoginButton = (Button) findViewById(R.id.login_button);
        PhoneLoginButton = (Button) findViewById(R.id.login_using_your_phone);
        UserEmail=(EditText)findViewById(R.id.login_email);
        UserPassword = (EditText) findViewById(R.id.login_password);
        RegisterOnLoginPage = (TextView) findViewById(R.id.register_button_on_login_page);
        ForgetPasswordLink = (TextView) findViewById(R.id.forget_password_link);
        loadingBar = new ProgressDialog(this);

    }

//dus zal daardoor altijd inglogd blijven, tot uitloggen
private void SendUserToMainActivity() {
    Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
    //zorgt ervoor als users op de back knop drukken dus niet terug kunnen naar login, en dus op mainactivity blijven
    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(mainIntent);
    finish();
}

//wanneer user op button drukt om te registreren, naar registreer activity sturen
    private void SendUserToRegisterActivity() {
        Intent registerIntent = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(registerIntent);
    }
}
