package edu.illinois.cs125.sagittario.sagittario;

import android.app.*;
import android.content.pm.ApplicationInfo;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class SagittarioApplication extends Application {

    public ImageProvider currentProvider;
    public RequestQueue requestQueue;

    public int fieldSize = -1;
    public int nbombs = -1;

    @Override
    public ApplicationInfo getApplicationInfo() {
        ApplicationInfo info = new ApplicationInfo(super.getApplicationInfo());
        info.name = "Sagittario";
        return info;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        requestQueue = Volley.newRequestQueue(this);
    }

    /**
     * Create an image provider.
     * @param searchString
     * @return
     */
    public ImageProvider createImageProvider(String searchString){
        return currentProvider = new ImageProvider(searchString, requestQueue);
    }


    public void startSearch(String searchText, int fieldSize, int nbombs){
        //TODO
        createImageProvider(searchText);

    }

}
