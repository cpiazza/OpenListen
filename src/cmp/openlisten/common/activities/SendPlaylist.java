package cmp.openlisten.common.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import cmp.openlisten.R;
import cmp.openlisten.common.db.OpenListenDBUtil;
import cmp.openlisten.facebook.OpenListenFBConnect;


public class SendPlaylist extends OLBaseActivity {
	
	//private LoginButton _loginButton;
	//private Button _permissionButton;
	private Button _settingsButton;
	
	private TextView _label;
	private OpenListenFBConnect fb;

    public void onCreate(Bundle savedInstanceState) {
    	String mStrPlaylist;
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_playlist);
        
        _label = (TextView) findViewById(R.id.label);
        _settingsButton = (Button) findViewById(R.id.SettingsFromSendPlaylist);
        _settingsButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
					viewSettings();
			}
        	
        });

        mStrPlaylist = OpenListenDBUtil.getFormattedPlaylistForPublishing(this);
        
        fb = new OpenListenFBConnect();
        fb.restoreSession(this);
        
        if (mStrPlaylist.length() < OpenListenDBUtil.mPlaylistFooterLength + 2) {
        	_label.setText("Your playlist appears to empty. Perhaps you should listen to some music first.");
        }
        else if ( (fb.isSessionValid()) && (mStrPlaylist.length() > 5)) {
        	fb.publishPlaylist("My Daily Playlist", mStrPlaylist, "notes");
        	_label.setText("Playlist Sent!");
        } else {
        	_label.setText("You must login to Facebook before you can publish your playlist.");
        	_settingsButton.setVisibility(View.VISIBLE);
        }
	}
    
	private void viewSettings() {
    	Intent i = new Intent(this, FBLogin.class);
    	startActivity(i);  
	}

}
