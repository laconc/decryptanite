package com.team_six.decryptanite;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.team_six.decryptanite.models.DbHelper;
import com.team_six.decryptanite.models.GoogleTranslate;
import com.team_six.decryptanite.models.UpdateCallback;

public class ResultActivity extends AppCompatActivity implements UpdateCallback {
    private static final String TAG = ResultActivity.class.getSimpleName();

    private DbHelper db = new DbHelper(this);
    private String user;

    TextView originalTextView;
    TextView translatedTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        originalTextView = (TextView) findViewById(R.id.originalTextView);

        Intent intent = getIntent();
        user = intent.getStringExtra("user");

        String ocrResult = intent.getStringExtra("ocrResult");
        originalTextView.setText(ocrResult);

        try {
            translate(ocrResult);
            db.storeTranslation(user, ocrResult, translatedTextView.getText().toString());
        } catch (Exception e) {
            Log.e(TAG, "ERROR: Couldn't translate the text.");
        }
    }

    public void translate(String original) throws Exception {
        translatedTextView = (TextView) findViewById(R.id.translatedTextView);
        translatedTextView.setText("Translating...");

        new GoogleTranslate(this, user).getTranslation(original);
    }

    @Override
    public void updateFields(String... params) {
        translatedTextView.setText(params[0]);
    }
}
