package com.team_six.decryptanite;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.team_six.decryptanite.models.DbHelper;
import com.team_six.decryptanite.models.RequestPermissionsTool;
import com.team_six.decryptanite.models.RequestPermissionsToolImpl;
import com.team_six.decryptanite.models.Status;

public class LoginActivity extends AppCompatActivity {
    DbHelper db = new DbHelper(this);

    EditText usernameField;
    EditText passField;

    private RequestPermissionsTool requestTool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions();
        }
    }

    public void login(View view) throws Exception {
        usernameField = (EditText) findViewById(R.id.usernameField);
        passField = (EditText) findViewById(R.id.passField);

        String user = usernameField.getText().toString();
        String pass = passField.getText().toString();

        boolean isAuthCorrect = false;

        if(db.verifyCredentials(user, pass)) {
            isAuthCorrect = true;
        }

        if (isAuthCorrect) {
            db.logAccessEvent(user, Status.SUCCESS);
            displayMainMenu(user);
        } else {
            db.logAccessEvent(user, Status.FAILURE);
            Toast.makeText(getApplicationContext(), "Incorrect Credentials", Toast.LENGTH_SHORT).show();
        }
    }

    public void displayMainMenu(String user) {
        Intent intent = new Intent(this, MainMenuActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    private void requestPermissions() {
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        requestTool = new RequestPermissionsToolImpl();
        requestTool.requestPermissions(this, permissions);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        boolean grantedAllPermissions = true;
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                grantedAllPermissions = false;
            }
        }

        if (grantResults.length != permissions.length || (!grantedAllPermissions)) {
            requestTool.onPermissionDenied();
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
