package cmp.openlisten.common.activities;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import cmp.openlisten.OpenListen;
import cmp.openlisten.R;
import cmp.openlisten.common.OpenListenSettingsUtil;
import cmp.openlisten.common.UserAccount;
import cmp.openlisten.common.service.OLServiceConnection;
import cmp.openlisten.facebook.OpenListenFBConnect;

public class OpenListenSettings extends OLBaseActivity {

    private Button _saveButton;
    private Button _logoutButton;
    private CheckBox _cbDaily;
    private CheckBox _cbClear;
    private CheckBox _cbRank;
    private EditText _username;
    private EditText _dateOfBirth;
    private RadioButton _rbMale;
    private RadioButton _rbFemale;
    private TextView _tvProfileHeader;
    private Context mCtx;
    private OpenListenFBConnect fb;
    
	    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.settings);
    	
        fb = new OpenListenFBConnect();
        fb.restoreSession(this); 
   	
    	mCtx = this;
        
        _username = (EditText) findViewById(R.id.editOLUserName);
        _dateOfBirth = (EditText) findViewById(R.id.txtDateOfBirth);
        _rbMale = (RadioButton) findViewById(R.id.GenderMale);
        _rbFemale = (RadioButton) findViewById(R.id.GenderFemale);
        _tvProfileHeader = (TextView) findViewById(R.id.strProfileHeader);
        
        OpenListenSettingsUtil os = new OpenListenSettingsUtil();
        
        _tvProfileHeader.setText(os.getDebugString(this));
        
        // Let's see if we have an existing OLID and, if so, retrieve data
        int iOLID = os.getOLID(mCtx);
        
        if (iOLID > 0)
        {
        	OLServiceConnection olsc = new OLServiceConnection();
        	UserAccount ua = olsc.GetUserAccount(iOLID);
        	
        	if (ua != null) {
        		_username.setText( (ua.UserName != null) ? ua.UserName.toString() : "" );
        	
        		if (ua.DateOfBirth != null) {
        			_dateOfBirth.setText(String.valueOf(ua.DateOfBirth.getMonth() + 1) + "/" + String.valueOf(ua.DateOfBirth.getDate()) + "/" + String.valueOf(ua.DateOfBirth.getYear() + 1900));
        		}
        		
        		if (ua.Gender != null) {
        			_rbMale.setChecked((ua.Gender.equals("Male")) ? true : false);
        			_rbFemale.setChecked((ua.Gender.equals("Female")) ? true : false);
        		}
        	}
        }
        
        _cbDaily = (CheckBox) findViewById(R.id.cbDaily);
        _cbDaily.setChecked(os.getDailyPublishPreference(mCtx));
        
        _cbClear = (CheckBox) findViewById(R.id.cbClear);
        _cbClear.setChecked(os.getClearPlaylistPreference(mCtx));
        
        _cbRank = (CheckBox) findViewById(R.id.cbRank);
        _cbRank.setChecked(os.getRankPublishPreference(mCtx));
        
        _logoutButton = (Button) findViewById(R.id.btnLogout);
        _logoutButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				viewFBLogin();
			}
        	
        });
        
        _saveButton =(Button) findViewById(R.id.btnSaveSettings);
        _saveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// Validate DOB entry before doing anything
				String strDOB = _dateOfBirth.getText().toString();
				
				if (strDOB.length() > 0)
				{
					Date dtTest;
					
					try {
						dtTest = new Date(strDOB);
					} catch (Exception e) {
						Toast t = Toast.makeText(mCtx, "Date format must be MM/DD/YYYY", 15);
						t.show();
						return;
					}
					
					if ( (dtTest.getYear() < 0) ) {
						Toast t = Toast.makeText(mCtx, "We find it hard to believe you were born in that year. Please try again." , 15);
						t.show();
						return;
					}
				} else {
					strDOB = null;
				}
				
				String strTest = _username.getText().toString();
				
				String REGEX = "[a-zA-Z0-9]*";
				Pattern P = Pattern.compile(REGEX);
				Matcher M = P.matcher(strTest);
				
				if (!M.matches()) {
					Toast t = Toast.makeText(mCtx, "Only letters and numbers in the Username, please", 20);
					t.show();
					return;					
				}
				
				// If the user is already logged in via Facebook, we should update the new OLUsername
				final ProgressDialog pd = ProgressDialog.show(OpenListenSettings.this, "", "Saving settings...", true);
				
				final class UpdateThread extends Thread {
					@Override
					public void run() {
						String strUsername = _username.getText().toString();
						String strGender = "";
						
						OpenListenSettingsUtil os = new OpenListenSettingsUtil();
						
						if (_rbMale.isChecked())
							strGender = "Male";
						else if (_rbFemale.isChecked())
							strGender = "Female";
						
						if (fb.isSessionValid()) {
							int iOL_ID = os.getOLID(mCtx);
							
							if (iOL_ID > 0) {
								String strDOB = _dateOfBirth.getText().toString();
								if (strDOB.length() == 0)
									strDOB = null;
								
								OLServiceConnection ols = new OLServiceConnection();
								ols.UpdateUsername(iOL_ID, strUsername, strDOB, strGender);
							}
						}
						
						os.setOLUsername(mCtx, strUsername);
						os.setClearPlaylistPreference(mCtx, _cbClear.isChecked());
						os.setDailyPublishPreference(mCtx, _cbDaily.isChecked());
						os.setRankPublishPreference(mCtx, _cbRank.isChecked());
						handler.sendEmptyMessage(0);
					}
					
					private Handler handler = new Handler() {
						@Override
						public void handleMessage(Message msg) {
							pd.dismiss();
							
					    	Intent i = new Intent(mCtx, OpenListen.class);
					    	startActivity(i);
						}
					};
					
				}
				
				UpdateThread ut = new UpdateThread();
				ut.start();
			}
        });
        
    }
    
    private void viewFBLogin() {
    	Intent i = new Intent(this, FBLogin.class);
    	startActivity(i);
    }    
    
    
}
