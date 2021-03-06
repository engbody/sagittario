package edu.illinois.cs125.sagittario.sagittario;

import android.content.Context;
import android.graphics.*;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ImageProvider {
    public final String searchString;
    private String contentUrl;

    private Bitmap image;
    private RequestQueue queue;
    /**
     * This is the API URL endpoint for the Bing Image search API.
     */
    private static String apiEndpoint = "https://api.cognitive.microsoft.com/bing/v7.0/images/search?q=%s";

    /**
     * The API Key.
     */
    private static String key1 = "a233c0220de5457080c0e45b2e2b02d2";

    // key2 cdbef221ec2147ae8fb7d7e14584284d
    public ImageProvider(String searchString, final Context c, RequestQueue queue, final Runnable run) {
        this.searchString = searchString;
        String url = String.format(apiEndpoint, searchString);
        this.queue = queue;
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            ///
                            JSONArray values = response.getJSONArray("value");
                            Random r = new Random();
                            int choice = r.nextInt(values.length());
                            JSONObject value = values.getJSONObject(choice);
                            ImageProvider.this.contentUrl = value.getString("contentUrl");
                            Runnable nr = new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        URL url = new URL(ImageProvider.this.contentUrl);
                                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                        connection.setDoInput(true);
                                        connection.connect();
                                        InputStream input = connection.getInputStream();
                                        ImageProvider.this.image = BitmapFactory.decodeStream(input);
                                        input.close();
                                        Log.e("Image Provider", "Download Complete!");
                                        run.run();
                                        Log.e("Image Provider", "Ran runnable");
                                    } catch (Throwable ignored) {
                                    }
                                }
                            };
                            Thread t = new Thread(nr, "Network");
                            t.start();
                            return;
                        } catch (Throwable t) {
                            t.printStackTrace();
                            Toast.makeText(c, "Error " + t.toString(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(c, "Error " + error.toString() + "\n" +
                                error.networkResponse, Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                        // network error!
                        image = null;
                        run.run();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> m = new HashMap<>();
                m.put("Ocp-Apim-Subscription-Key", key1);
                return m;
            }
        };
        // set the retry policy
        RetryPolicy retryPolicy = new DefaultRetryPolicy(2500,2, 1);
        request.setRetryPolicy(retryPolicy);
        this.queue.add(request);
    }

    public Bitmap getBackground() {
        return image;
    }
}
