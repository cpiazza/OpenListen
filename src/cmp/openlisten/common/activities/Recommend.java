package cmp.openlisten.common.activities;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;
import cmp.openlisten.R;
import cmp.openlisten.common.MusicTrack;
import cmp.openlisten.common.OpenListenGeoUtil;
import cmp.openlisten.common.OpenListenSettingsUtil;
import cmp.openlisten.common.db.OpenListenDBUtil;
import cmp.openlisten.common.service.OLServiceConnection;
import cmp.openlisten.facebook.OpenListenFBConnect;

public class Recommend extends OLBaseActivity {
	
	private TextView _Artist;
	private TextView _Track;
	private TextView _Album;
	private Button _btn;
	private MusicTrack mt;
	private RadioButton rbTrack;
	private RadioButton rbAlbum;
	private CheckBox cbNewFave;
	private CheckBox cbOldie; 
	private CheckBox cbCurrentObsession;
	private OpenListenFBConnect fb;
	private boolean _noSession;
    protected static final String strPreamble = "http%3A%2F%2Fwww.amazon.com/gp/search?ie=UTF8%26keywords=";
    protected static final String strPostamble = "%26tag=openlisten-20%26index=digital-music%26linkCode=ur2%26camp=1789%26creative=9325";
    protected String strWhich = "";
    protected String strMessage = "Recommends...";
    protected Context _mctx;
    
    public static final String SEND_RECOMMENDATION = "cmp.openlisten.recommend.SEND";
	
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated constructor stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recommend);
        
        _mctx = this;
        
        _Track = (TextView) findViewById(R.id.txtTrack);
        _Artist = (TextView) findViewById(R.id.txtArtist);
        _Album = (TextView) findViewById(R.id.txtAlbum);
        _btn = (Button) findViewById(R.id.btnSend);
        
        cbNewFave = (CheckBox) findViewById(R.id.cbNewFave);
        cbOldie = (CheckBox) findViewById(R.id.cbOldir);
        cbCurrentObsession = (CheckBox) findViewById(R.id.cbCurrentObsession);
        rbAlbum = (RadioButton) findViewById(R.id.recommendAlbum);
        rbTrack = (RadioButton) findViewById(R.id.recommendTrack);
        rbTrack.setChecked(true);
        
        _btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (_noSession) {
					viewSettings();
				}
				else {
					sendRecommendation();
				}
			}
        	
        });
        
        if (this.getIntExtra(savedInstanceState, "FromPlaylist") == 1) {
            long mRowId = savedInstanceState != null ? savedInstanceState.getLong("whichTrack") : -1;
            
            if (mRowId == -1) {
            	Bundle extras = getIntent().getExtras();            
            	mRowId = extras != null ? extras.getLong("whichTrack") : -1;
            }
            
        	mt = OpenListenDBUtil.getTrackFromPlaylist(mRowId, this);
        	
        } else {
        	mt = new MusicTrack();
        	
        	mt.mAlbumName = "";
    		mt.mTrackName = this.getStringExtra(savedInstanceState, "TrackName");
    		mt.mArtistName = this.getStringExtra(savedInstanceState, "ArtistName");

        	if (mt.mTrackName == null || mt.mArtistName == null || mt.mTrackName.length() == 0) {
        		OpenListenSettingsUtil os = new OpenListenSettingsUtil();
        		mt.mTrackName = os.getLastTrack(this);
        		mt.mArtistName = os.getLastArtist(this);
        		mt.mAlbumName = os.getLastAlbum(this);
        	}
        }
        
        if (mt != null) {
        	_Track.setText(mt.mTrackName);
        	_Artist.setText("by " + mt.mArtistName);
        	
        	if ( (mt.mAlbumName != null) && (mt.mAlbumName.length() > 0) ) {
        		_Album.setText("Album: " + mt.mAlbumName);
        	} else {
        		_Album.setVisibility(View.INVISIBLE);
        		rbAlbum.setVisibility(View.INVISIBLE);
        	}
        	
        	this.setAdKeywords(mt.mArtistName);
        }
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		checkFBSession();
	}
	
	
	@Override
	protected void onRestart() {
		super.onRestart();
		checkFBSession();
	}
	

	private void viewSettings() {
    	Intent i = new Intent(this, OpenListenSettings.class);
    	startActivity(i);  
	}
	
	private void checkFBSession() {
		
		if (fb == null) {
			fb = new OpenListenFBConnect();
			fb.restoreSession(this);
		}
		
		if (!fb.isSessionValid()) {
			_noSession = true;
			_btn = (Button) findViewById(R.id.btnSend);
			_btn.setText("Login to Facebook");
			TextView tv = (TextView) findViewById(R.id.cannotRecommend);
			tv.setVisibility(View.VISIBLE);
		}
		else {
			_noSession = false;
			_btn = (Button) findViewById(R.id.btnSend);
			_btn.setText("Send It!");
			TextView tv = (TextView) findViewById(R.id.cannotRecommend);
			tv.setVisibility(View.GONE);
		}
		
	}
	
	private void sendRecommendation() {

		String strTrack = (String) _Track.getText();
		String strAlbum = (String) _Album.getText();
		String strItem = "";
		boolean bGo;
		
        strMessage = "Recommends...";
        
        if (rbTrack.isChecked()) {
        	strWhich = strTrack;
        	strItem = "song";
        	bGo = (strTrack.length() > 0);
        } else {
        	strWhich = strAlbum;
        	strItem = "album";
        	bGo = (strAlbum.length() > 0);
        }
        
        if (fb == null) {
    		fb = new OpenListenFBConnect();
    		fb.restoreSession(this);
        }
        
        if ( cbNewFave.isChecked() && !cbCurrentObsession.isChecked() && !cbOldie.isChecked() ) {
        	strMessage = "has a new favorite " + strItem;
        } else if ( cbNewFave.isChecked() && cbCurrentObsession.isChecked() && !cbOldie.isChecked() ) {
        	strMessage = "has a new current obsession";
        } else if ( !cbNewFave.isChecked() && !cbCurrentObsession.isChecked() && cbOldie.isChecked() ) {
        	strMessage = "recommends this blast from the past";
        } else if ( !cbNewFave.isChecked() && cbCurrentObsession.isChecked() && cbOldie.isChecked() ) {
        	strMessage = "is currently obsessed with this blast from the past";
        } else if ( !cbNewFave.isChecked() && cbCurrentObsession.isChecked() && !cbOldie.isChecked() ) {
        	strMessage = "can't stop listening to this " + strItem;
        } else {
        	strMessage = "recommends this " + strItem;
        }
		
		if (bGo) {
			
			final ProgressDialog pd = ProgressDialog.show(_mctx, "", "Sending Recommendation...", true);

			final class UpdateThread extends Thread {
				@Override
				public void run() {
					String strPostID = null;
					String strResponse = fb.syncPublishFeed(strPreamble + strWhich.replace(" ", "%20") + "%20" + mt.mArtistName.replace(" ", "%20") + strPostamble, strMessage, strWhich, "", (String) _Artist.getText());
					JSONObject joResponse = null;
					
					try {
						// Get the post ID from the response
						joResponse = new JSONObject(strResponse);
						strPostID = joResponse.getString("id");
					} catch (JSONException e) {
						joResponse = null;
						strPostID = null;
					}
					
					// Send Recommendation to service
					OpenListenSettingsUtil os = new OpenListenSettingsUtil();
					int iOLID = os.getOLID(_mctx);
					
					if (iOLID > 0){			
						OpenListenGeoUtil mgeo = OpenListenGeoUtil.getInstance();
						Address a = mgeo.getAddress(_mctx);
					
						MusicTrack track = new MusicTrack();
						track.mAlbumName = mt.mAlbumName;
						track.mArtistName = mt.mArtistName;
						
						if (rbTrack.isChecked())
							track.mTrackName = mt.mTrackName;
						else
							track.mTrackName = "";

						OLServiceConnection ols = new OLServiceConnection();
						ols.UploadRecommendation(iOLID, track, a, strPostID);
						handler.sendEmptyMessage(0);
					}				
				}
				
				private Handler handler = new Handler() {
					@Override
					public void handleMessage(Message msg) {
						pd.dismiss();
						
						Intent i = new Intent(_mctx, RecommendationSent.class);
						startActivity(i);
					}
				};
			}
			
			UpdateThread ut = new UpdateThread();
			ut.start();
		}
	}
}
