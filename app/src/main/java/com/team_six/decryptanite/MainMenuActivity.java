package com.team_six.decryptanite;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.team_six.decryptanite.models.DbHelper;
import com.team_six.decryptanite.models.Status;
import com.team_six.decryptanite.models.Tesseract;

import java.io.File;
import java.sql.Timestamp;

public class MainMenuActivity extends AppCompatActivity {
    private static final String TAG = MainMenuActivity.class.getSimpleName();
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int SELECT_PICTURE = 100;

    private DbHelper db = new DbHelper(this);
    private Tesseract tesseract;
    private Uri outputFileUri;

    private boolean isNewPic;
    private String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        Intent intent = getIntent();
        user = intent.getStringExtra("user");
        tesseract = new Tesseract(this, user);
    }

    public void explore(View view) throws Exception {
        isNewPic = false;
        try {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void snapPic(View view) throws Exception {
        isNewPic = true;
        try {
            String IMAGES_PATH = tesseract.getDataPath() + "images";
            tesseract.prepareDirectory(IMAGES_PATH);

            String imagePath = IMAGES_PATH + "/" + new Timestamp(System.currentTimeMillis()).toString() + ".jpg";
            outputFileUri = Uri.fromFile(new File(imagePath));

            final Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (isNewPic) {
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
                db.logNewPicEvent(user, Status.SUCCESS);
                tesseract.startOcr(outputFileUri);
            } else {
                Log.e(TAG, "ERROR: Image was not obtained.");
                db.logNewPicEvent(user, Status.FAILURE);
            }
        } else {
            if (resultCode == RESULT_OK && requestCode == SELECT_PICTURE) {
                db.logLoadPicEvent(user, Status.SUCCESS);

                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    tesseract.startOcr(selectedImageUri);
                }
            } else {
                Log.e(TAG, "ERROR: Image was not found.");
                db.logLoadPicEvent(user, Status.FAILURE);
            }
        }
    }
}
