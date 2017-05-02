package com.team_six.decryptanite.models;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;
import com.team_six.decryptanite.ResultActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Tesseract extends ContextWrapper {
    private static final String TAG = Tesseract.class.getSimpleName();
    private static final String TESSDATA = "tessdata";
    private final String DATA_PATH;

    private DbHelper db = new DbHelper(this);
    private TessBaseAPI tessBaseApi;

    private String user;

    public Tesseract(Context context, String user) {
        super(context);
        this.user = user;
        DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/Decryptanite/";

        try {
            prepareDirectory(DATA_PATH + TESSDATA);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        copyTessDataFiles(TESSDATA);
    }

    public void startOcr(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

            String ocrResult = extractText(bitmap);
            db.logOcrEvent(user, Status.SUCCESS);
            showResult(ocrResult);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            db.logOcrEvent(user, Status.FAILURE);
        }
    }

    private String extractText(Bitmap bitmap) {
        try {
            tessBaseApi = new TessBaseAPI();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            if (tessBaseApi == null) {
                Log.e(TAG, "ERROR: TessBaseAPI is null. TessFactory not returning tess object.");
            }
        }

        tessBaseApi.init(DATA_PATH, "spa");
        tessBaseApi.setImage(bitmap);

        String extractedText = "error";
        try {
            extractedText = tessBaseApi.getUTF8Text();
        } catch (Exception e) {
            Log.e(TAG, "ERROR: Couldn't recognize the text.");
        }
        tessBaseApi.end();

        return extractedText;
    }

    private void showResult(String ocrResult) {
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("user", user);
        intent.putExtra("ocrResult", ocrResult);
        startActivity(intent);
    }

    public void prepareDirectory(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                Log.e(TAG, "ERROR: Creation of directory " + path + " failed.");
            } else {
                Log.e(TAG, path + " created.");
            }
        }
    }

    private void copyTessDataFiles(String path) {
        try {
            String fileList[] = getAssets().list(path);

            for (String fileName : fileList) {
                String pathToDataFile = DATA_PATH + path + "/" + fileName;
                if (!(new File(pathToDataFile)).exists()) {
                    InputStream in = getAssets().open(path + "/" + fileName);
                    OutputStream out = new FileOutputStream(pathToDataFile);

                    byte[] buf = new byte[1024];
                    int len;

                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    in.close();
                    out.close();
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "ERROR: Unable to copy files to tessdata " + e.toString());
        }
    }

    public String getDataPath() {
        return DATA_PATH;
    }
}
