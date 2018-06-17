package ua.dp.sq.bluetoothflasher.activities.userCabinet;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ua.dp.sq.bluetoothflasher.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import ua.dp.sq.bluetoothflasher.activities.SendToHCModule;

public class SignUp extends AppCompatActivity implements View.OnClickListener {

    Snackbar snackBar;

    Button btnSignUp;
    TextView btnLogin;
    TextView btnForgotPass;
    EditText inputEmail;
    EditText inputPass;
    RelativeLayout activitySignUp;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        btnSignUp = (Button)findViewById(R.id.signup_btn_register);
        btnLogin = (TextView)findViewById(R.id.signup_btn_login);
        btnForgotPass = (TextView)findViewById(R.id.signup_btn_forgot_pass);
        inputEmail = (EditText)findViewById(R.id.signup_email);
        inputPass = (EditText)findViewById(R.id.signup_password);
        activitySignUp = (RelativeLayout)findViewById(R.id.activity_sign_up);

        btnSignUp.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        btnForgotPass.setOnClickListener(this);

        auth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.signup_btn_login){
            startActivity(new Intent(SignUp.this, SendToHCModule.class));
            finish();
        } else if(view.getId() == R.id.signup_btn_forgot_pass) {
            startActivity(new Intent(SignUp.this, ForgotPassword.class));
            finish();
        } else if(view.getId() == R.id.signup_btn_register) {
            signUpUser(inputEmail.getText().toString(), inputPass.getText().toString());
        }
    }

    private void signUpUser(String email, String password) {
        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()) {
                            snackBar = Snackbar.make(activitySignUp, getString(R.string.error)
                                    + task.getException(),Snackbar.LENGTH_LONG);
                            snackBar.show();
                        } else {
                            snackBar = Snackbar.make(activitySignUp, R.string.registation_success, Snackbar.LENGTH_LONG);
                            snackBar.show();
                        }
                    }
                });
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(SignUp.this, LoginActivity.class));
        super.onBackPressed();
    }
}

