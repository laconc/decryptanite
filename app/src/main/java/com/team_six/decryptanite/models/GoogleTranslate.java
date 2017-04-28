package com.team_six.decryptanite.models;

import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


public class GoogleTranslate extends ContextWrapper {
    private DbHelper db = new DbHelper(this);
    private RequestQueue queue = Volley.newRequestQueue(this);
    private String TAG = GoogleTranslate.class.getSimpleName();
    private String translatedMessage = "";
    private UpdateCallback updateCallback;
    private String user;

    public GoogleTranslate(Context base, String user) {
        super(base);
        this.updateCallback = (UpdateCallback) base;
        this.user = user;
    }

    public String getTranslation(String message) throws Exception {
        String url = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=es&tl=en&dt=t&q="
                + URLEncoder.encode(message, "UTF-8");

        JsonArrayRequest request = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            parseJSON(response);
                            db.logTranslationEvent(user, Status.SUCCESS);
                        } catch (JSONException e) {
                            db.logTranslationEvent(user, Status.FAILURE);
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError e) {
                        Log.e(TAG, "ERROR: Couldn't translate the text.");
                        db.logTranslationEvent(user, Status.FAILURE);
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<>();
                params.put("User-Agent", "Mozilla/4.0");
                return params;
            }
        };
        queue.add(request);

        return translatedMessage;
    }

    private void parseJSON(JSONArray response) throws JSONException {
        translatedMessage = response.getJSONArray(0).getJSONArray(0).getString(0);
        this.updateCallback.updateFields(translatedMessage);
    }
}