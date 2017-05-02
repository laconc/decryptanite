package com.team_six.decryptanite;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.team_six.decryptanite.models.DbHelper;
import com.team_six.decryptanite.models.Status;

public class LoginActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_WRITE_EXTERNAL_PERMISSIONS = 1;

    private DbHelper db = new DbHelper(this);

    EditText userField;
    EditText passField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getStorageAccessPermissions();
    }

    public void login(View view) throws Exception {
        userField = (EditText) findViewById(R.id.userField);
        passField = (EditText) findViewById(R.id.passField);

        String user = userField.getText().toString();
        String pass = passField.getText().toString();

        if(db.verifyCredentials(user, pass)) {
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

    @TargetApi(23)
    private void getStorageAccessPermissions() {
        int hasWriteStoragePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_PERMISSIONS);
        }
    }
}
