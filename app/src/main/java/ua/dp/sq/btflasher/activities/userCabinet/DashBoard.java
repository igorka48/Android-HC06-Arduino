package ua.dp.sq.btflasher.activities.userCabinet;

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
import android.widget.Toast;

import ua.dp.sq.btflasher.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ua.dp.sq.btflasher.activities.DevicesListActivity;

public class DashBoard extends AppCompatActivity implements View.OnClickListener {

    Snackbar snackBar;

    TextView txtWelcome;
    EditText inputNewPassword;
    Button btnChangePass;
    Button btnLogout;
    RelativeLayout activityDashboard;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        txtWelcome = (TextView)findViewById(R.id.dashboard_welcome);
        inputNewPassword = (EditText)findViewById(R.id.dashboard_new_password);
        btnChangePass = (Button)findViewById(R.id.dashboard_btn_change_pass);
        btnLogout = (Button)findViewById(R.id.dashboard_btn_logout);
        activityDashboard = (RelativeLayout)findViewById(R.id.activity_dash_board);

        btnChangePass.setOnClickListener(this);
        btnLogout.setOnClickListener(this);

        auth = FirebaseAuth.getInstance();

        if(auth.getCurrentUser() != null)
            txtWelcome.setText(getString(R.string.welcome) + "\n" + auth.getCurrentUser().getEmail());
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.dashboard_btn_change_pass) {
            if(inputNewPassword.getText().toString().isEmpty()) {
                Toast.makeText(DashBoard.this, R.string.empty_new_password, Toast.LENGTH_SHORT).show();
                return;
            }
            changePassword(inputNewPassword.getText().toString());
        }
        else if(view.getId() == R.id.dashboard_btn_logout) {
            logoutUser();
        }
    }

    private void logoutUser() {
        auth.signOut();
        if(auth.getCurrentUser() == null) {
            startActivity(new Intent(DashBoard.this, DevicesListActivity.class));
            finish();
        }
    }

    private void changePassword(String newPassword) {
        FirebaseUser user = auth.getCurrentUser();
        user.updatePassword(newPassword).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    snackBar = Snackbar.make(activityDashboard, R.string.pass_сhanged, Snackbar.LENGTH_LONG);
                    snackBar.show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(DashBoard.this, DevicesListActivity.class));
        super.onBackPressed();
    }
}

