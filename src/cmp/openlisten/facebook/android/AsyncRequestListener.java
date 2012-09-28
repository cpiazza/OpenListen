package cmp.openlisten.facebook.android;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

import cmp.openlisten.facebook.android.AsyncFacebookRunner.RequestListener;

import android.util.Log;


public abstract class AsyncRequestListener implements RequestListener {

    public void onComplete(String response) {
        try {
            JSONObject obj = Util.parseJson(response);
            onComplete(obj);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("facebook-stream", "JSON Error:" + e.getMessage());
        } catch (FacebookError e) {
            Log.e("facebook-stream", "Facebook Error:" + e.getMessage());
        }

    }

    public abstract void onComplete(JSONObject obj);

    public void onFacebookError(FacebookError e) {
        Log.e("stream", "Facebook Error:" + e.getMessage());
    }

    public void onFileNotFoundException(FileNotFoundException e) {
        Log.e("stream", "Resource not found:" + e.getMessage());      
    }

    public void onIOException(IOException e) {
        Log.e("stream", "Network Error:" + e.getMessage());      
    }

    public void onMalformedURLException(MalformedURLException e) {
        Log.e("stream", "Invalid URL:" + e.getMessage());            
    }

}