package cmp.openlisten.facebook;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import cmp.openlisten.common.OpenListenSettingsUtil;
import cmp.openlisten.common.db.OpenListenDBUtil;
import cmp.openlisten.facebook.android.AsyncFacebookRunner;
import cmp.openlisten.facebook.android.AsyncRequestListener;
import cmp.openlisten.facebook.android.Facebook;
import cmp.openlisten.facebook.android.FacebookError;
import cmp.openlisten.facebook.android.SessionStore;

public class OpenListenFBConnect {

    public static final String APP_ID = "c8e44addca5aab706efec22cad5e7534";
    
    //private static final String[] PERMISSIONS =
    //   new String[] {"publish_stream", "read_stream", "offline_access"};
    
    private Facebook mFacebook;
    private AsyncFacebookRunner mAsyncRunner;
    private Context _mCtx = null;
	
    //private static final String kApiSecret = "fa04745989809c1c5adcea56d13017f2";
    // /////////////////////////////////////////////////////////////////////////////////////////////////
    
    public String mLabel;
    public boolean mHasPermission;
    public boolean mIsLoggedIn;
    
	public OpenListenFBConnect() {
		super();
		
		mIsLoggedIn = false;
		mHasPermission = false;

       	mFacebook = new Facebook();
       	mAsyncRunner = new AsyncFacebookRunner(mFacebook);
	}
	
	public void restoreSession(Context ctx){
		_mCtx = ctx;
		SessionStore.restore(mFacebook, ctx);
		
       	mHasPermission = (mFacebook.getAccessToken() != null) ? true : false;
       	if (isSessionValid()) {
       		mLabel = "You are Logged In!";
       		getUser();
       	}
	}
	
	public void like(String strFBPostId) {
        AsyncFacebookRunner fb = mAsyncRunner;
        Bundle params = new Bundle();
        
        fb.request(strFBPostId + "/likes", params, "POST", new AsyncRequestListener() {

            @Override
        	public void onFacebookError(FacebookError e) {
                Log.e("stream", "Facebook Error:" + e.getMessage()); 
            }
        	
        	public void onComplete(JSONObject obj) {
        		
            	return;
            }
        });
        
	return;
		
	}
		
	
	public void addComment(String strFBPostId, String strMessage){
        AsyncFacebookRunner fb = mAsyncRunner;
        Bundle params = new Bundle();
        params.putString("message", strMessage);
        
        fb.request(strFBPostId + "/comments", params, "POST", new AsyncRequestListener() {

            @Override
        	public void onFacebookError(FacebookError e) {
                Log.e("stream", "Facebook Error:" + e.getMessage()); 
            }
        	
        	public void onComplete(JSONObject obj) {
        		
            	return;
            }
        });
        
	return;
		
	}
	
	public AsyncFacebookRunner getRunner() {
		return mAsyncRunner;
	}
	
	public boolean isSessionValid() {
		return mFacebook.isSessionValid();
	}
	
	public Facebook getFB() {
		return mFacebook;
	}
	
	public String syncPublishFeed(String strLink, String strMessage, String strName, String strDesc, String strCap) {
		
        String strRet;
        
		Bundle params = new Bundle();
        params.putString("message", strMessage);
        params.putString("link", strLink);
        params.putString("name", strName);
        params.putString("description", strDesc);
        params.putString("caption", strCap);
        
        try {
			strRet =  mFacebook.request("me/feed", params, "POST");
		} catch (MalformedURLException e) {
			JSONObject jo = new JSONObject();
			try {
				jo.put("Feed Error", e.toString());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			strRet = jo.toString();
		} catch (IOException e) {

			JSONObject jo = new JSONObject();
			try {
				jo.put("Feed Error", e.toString());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			
			strRet =jo.toString();
		}
        
		return strRet;
	}
	
	public JSONArray getComments(String strFBPostId) {
		
		JSONArray ja = null;
		
		try {
			String strResponse = mFacebook.request(strFBPostId + "/comments");
			JSONObject jo = new JSONObject(strResponse);
			ja = new JSONArray(jo.getString("data"));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ja;
	}	
	
	public void publishFeed(String strLink, String strMessage, String strName, String strDesc, String strCap, String strImageURL) {
		goFeedPublish("me/feed", strLink, strMessage, strName, strDesc, strCap, strImageURL);
		return;
	}
	
	public void publishOtherFeed(String strWho, String strLink, String strMessage, String strName, String strDesc, String strCap, String strImageURL) {
		goFeedPublish(strWho, strLink, strMessage, strName, strDesc, strCap, strImageURL);
		return;	}
	
	private void goFeedPublish(String strWho, String strLink, String strMessage, String strName, String strDesc, String strCap, String strImageURL) {
        AsyncFacebookRunner fb = mAsyncRunner;
        Bundle params = new Bundle();
        params.putString("message", strMessage);
        params.putString("link", strLink);
        params.putString("name", strName);
        params.putString("description", strDesc);
        params.putString("picture", strImageURL);
        params.putString("caption", strCap);
        
        fb.request(strWho, params, "POST", new AsyncRequestListener() {

            @Override
        	public void onFacebookError(FacebookError e) {
                Log.e("stream", "Facebook Error:" + e.getMessage()); 
            }
        	
        	public void onComplete(JSONObject obj) {
            	return;
            }
        });
        
        return;
	}

	
	public void publishStream(String strSubject, String strMessage, String strWhere) {

        AsyncFacebookRunner fb = mAsyncRunner;
        Bundle params = new Bundle();
        params.putString("message", strMessage);
        params.putString("subject", strSubject);
        
        fb.request("me/" + strWhere, params, "POST", new AsyncRequestListener() {

            @Override
        	public void onFacebookError(FacebookError e) {
                Log.e("stream", "Facebook Error:" + e.getMessage()); 
            }
        	
        	public void onComplete(JSONObject obj) {

            	return;
            }
        });
        
        return;
	}
	
	public void publishPlaylist(String strSubject, String strMessage, String strWhere) {

	        AsyncFacebookRunner fb = mAsyncRunner;
	        Bundle params = new Bundle();
	        params.putString("message", strMessage);
	        params.putString("subject", strSubject);
	        
        	Date d = new Date();
        	OpenListenSettingsUtil os = new OpenListenSettingsUtil();
			os.setLastDayPublished(_mCtx, d.getDay());
			
	        fb.request("me/" + strWhere, params, "POST", new AsyncRequestListener() {

	            @Override
	        	public void onFacebookError(FacebookError e) {
	                Log.e("stream", "Facebook Error:" + e.getMessage()); 
	            }
	        	
	        	public void onComplete(JSONObject obj) {
	        		
	        		if (_mCtx != null) {
	        			OpenListenSettingsUtil os = new OpenListenSettingsUtil();
	        			if (os.getClearPlaylistPreference(_mCtx)) {
	        				OpenListenDBUtil.clearPlaylist(_mCtx);
	        			}
	        		}
					
	            	return;
	            }
	        });
	        
		return;
	}
	
	public void getUser() {
        AsyncFacebookRunner fb = mAsyncRunner;
        Bundle params = new Bundle();
        
        fb.request("", params, "GET", new AsyncRequestListener() {

            @Override
        	public void onFacebookError(FacebookError e) {
                Log.e("stream", "Facebook Error:" + e.getMessage()); 
            }
        	
        	public void onComplete(JSONObject obj) {
        		
            	return;
            }
        });
        
	return;
	}
	
    

	


	
}
