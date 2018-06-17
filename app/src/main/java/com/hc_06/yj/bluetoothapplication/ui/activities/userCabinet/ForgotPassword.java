package com.hc_06.yj.bluetoothapplication.ui.activities.userCabinet;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hc_06.yj.bluetoothapplication.R;
import com.hc_06.yj.bluetoothapplication.ui.activities.DevicesListActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity implements View.OnClickListener {

    EditText inputEmail;
    Button btnResetPass;
    TextView btnBack;
    RelativeLayout activityForgot;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        //View
        inputEmail = (EditText)findViewById(R.id.forgot_email);
        btnResetPass = (Button)findViewById(R.id.forgot_btn_reset);
        btnBack = (TextView)findViewById(R.id.forgot_btn_back);
        activityForgot = (RelativeLayout)findViewById(R.id.activity_forgot_password);

        btnResetPass.setOnClickListener(this);
        btnBack.setOnClickListener(this);

        auth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.forgot_btn_back) {
            startActivity(new Intent(this, DevicesListActivity.class));
            finish();
        } else if(view.getId() == R.id.forgot_btn_reset) {
            resetPassword(inputEmail.getText().toString());
        }
    }

    private void resetPassword(final String email) {
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(ForgotPassword.this, R.string.change_password_sended, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(ForgotPassword.this, DevicesListActivity.class));
                        } else {
                            Toast.makeText(ForgotPassword.this, R.string.failed_to_send_password, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}

