package cmp.openlisten.common.activities;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import cmp.openlisten.R;
import cmp.openlisten.common.OpenListenSettingsUtil;
import cmp.openlisten.common.service.OLServiceConnection;
import cmp.openlisten.facebook.OpenListenFBConnect;
import cmp.openlisten.facebook.android.BaseRequestListener;
import cmp.openlisten.facebook.android.FacebookError;
import cmp.openlisten.facebook.android.LoginButton;
import cmp.openlisten.facebook.android.SessionEvents;
import cmp.openlisten.facebook.android.Util;
import cmp.openlisten.facebook.android.SessionEvents.AuthListener;
import cmp.openlisten.facebook.android.SessionEvents.LogoutListener;

public class FBLogin extends OLBaseActivity {

	private LoginButton _loginButton;
    private TextView _label;	
    private OpenListenFBConnect fb;
    private Context mCtx;
	
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.fblogin);
    	
        fb = new OpenListenFBConnect();
        fb.restoreSession(this); 
        
        SessionEvents.addAuthListener(new SampleAuthListener());
        SessionEvents.addLogoutListener(new SampleLogoutListener());
    	
    	mCtx = this;
    	
        _label = (TextView) findViewById(R.id.label);
        _label.setText(fb.mLabel);
        
        _loginButton = (LoginButton) findViewById(R.id.login);
        _loginButton.init(fb.getFB(), new String[] {"publish_stream", "read_stream", "offline_access"});
    	
    	
    }
    
public class SampleAuthListener implements AuthListener {
        
        public void onAuthSucceed() {
        	_label.setText("You have logged in! ");
        	
        	//Get the User Record so we can check if a corresponding OW Record exists
        	fb.getRunner().request("me", new FBUserRequestListener());
        	//_permissionButton.setVisibility(View.VISIBLE);
        }

        public void onAuthFail(String error) {
        	_label.setText("Login Failed: " + error);
        }
    }
    
    public class SampleLogoutListener implements LogoutListener {
        public void onLogoutBegin() {
        	_label.setText("Logging out...");
        }
        
        public void onLogoutFinish() {
        	_label.setText("You have logged out! ");
        	//_permissionButton.setVisibility(View.INVISIBLE);
        }
    }
    
    
    public class FBUserRequestListener extends BaseRequestListener {

        public void onComplete(final String response) {
            try {
                // process the response here: executed in background thread
                Log.d("Facebook-Example", "Response: " + response.toString());
                JSONObject json = Util.parseJson(response);
                final String name = json.getString("name");
                final String id = json.getString("id");
                final String firstname = json.getString("first_name");
                final String lastname = json.getString("last_name");
                //final String gender = json.getString("gender");
                
                //Save the FB user information
                if ( (id != null) && (id.length() > 0) ) {
                	OpenListenSettingsUtil os = new OpenListenSettingsUtil();
                	os.setFbID(FBLogin.this, id);
                	
	            	// Create an OL account
					OLServiceConnection ols = new OLServiceConnection();
					int iOLID = ols.CreateUserAccount(Long.parseLong(id), firstname, lastname, os.getOLUsername(FBLogin.this));
					os.setOLID(FBLogin.this, iOLID);
					
					Intent i = new Intent(mCtx, OpenListenSettings.class);
			    	startActivity(i);
                }
                
                // then post the processed result back to the UI thread
                // if we do not do this, an runtime exception will be generated
                // e.g. "CalledFromWrongThreadException: Only the original 
                // thread that created a view hierarchy can touch its views."
                FBLogin.this.runOnUiThread(new Runnable() {
                    public void run() {
                        _label.setText("Hello there, " + name + "!");
                    }
                });
            } catch (JSONException e) {
                Log.w("Facebook-Example", "JSON Error in response");
            } catch (FacebookError e) {
                Log.w("Facebook-Example", "Facebook Error: " + e.getMessage());
            }
        }
    }    
	
}
