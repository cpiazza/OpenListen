package cmp.openlisten.common.service;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.os.IBinder;
import android.util.Log;
import cmp.openlisten.common.MediaPlayerServiceConnection;
import cmp.openlisten.common.MusicIntentParser;
import cmp.openlisten.common.MusicTrack;
import cmp.openlisten.common.OpenListenGeoUtil;
import cmp.openlisten.common.OpenListenSettingsUtil;
import cmp.openlisten.common.db.OpenListenDBAdapter;
import cmp.openlisten.common.db.OpenListenDBUtil;
import cmp.openlisten.facebook.OpenListenFBConnect;
import cmp.openlisten.facebook.android.BaseRequestListener;
import cmp.openlisten.facebook.android.FacebookError;
import cmp.openlisten.facebook.android.Util;


public class OpenListenService extends Service {
	
	//private static final int ACTIVITY_SEND_PLAYLIST = 0;
	
	private static MediaPlayerServiceConnection conn = null;
	private String LastTrack = "NoSong";
    private OpenListenDBAdapter mDbHelper;
    private Context _mctx;
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	} 
	
	public void onCreate() {
		// Don't start the service is the Full Version is installed
		super.onCreate();
		
		LastTrack = "NoSong";
		setDBConnection();
	}
	
	public static boolean isConnected()
	{
		return (conn != null); 
	}
	
	
	public void onStart(Intent intent, int startId) {
		
		// Examine the intent
		_mctx = this;
		
		MusicTrack track = null;

		String action = null;
		
		try {
			action = (intent == null) ? null : intent.getAction();
		} catch (Exception e) {
			Log.d("OpenListen", "NULL Intent in OpenListen Service");
		}
		
		if (action != null) {
			
			checkFBUserId();
			
			//if (intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
			//	publishPlaylist();
			//}

			track = getTrackFromStockPlayer(action);
			
			if (track == null) {
				try {
					MusicIntentParser mp = new MusicIntentParser();
					track = mp.parseIntent(this, action, intent.getExtras());
				} catch (IllegalArgumentException e) {
					track = null;
				} 
			}

			if ( (track != null) && (track.mTrackName != null) && (track.mArtistName != null) ) {
				
				publishPlaylist();
				
				if (!track.mTrackName.equalsIgnoreCase(LastTrack.toString())) {
					OpenListenSettingsUtil os = new OpenListenSettingsUtil();
					int iOLID = os.getOLID(this);
					
					os.setLastTrack(this, track.mTrackName);
					os.setLastArtist(this, track.mArtistName);
					
					if (track.mAlbumName != null) {
						os.setLastAlbum(this, track.mAlbumName);
					}
					
					LastTrack = track.mTrackName;
					
					OpenListenGeoUtil mgeo = OpenListenGeoUtil.getInstance();
					Address a = mgeo.getAddress(this);
					
					mDbHelper.insertTrack(track, a);
					
					if (iOLID > 0){
						OLServiceConnection ols = new OLServiceConnection();
						JSONObject jsoRank = ols.UploadTrackPlay(iOLID, track, a);
						
		    			String strRank = "";
		    			String strArtistFBPage = "";
		    			boolean bIsNew = false;
		    			
						try {
							strRank = jsoRank.getString("Rank");
							bIsNew = jsoRank.getBoolean("IsNewHighRank");
							//strArtistFBPage = jsoRank.getString("ArtistFBPage");
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (NullPointerException ep) {
							ep.printStackTrace();
						}
		    			
		    			if ( (bIsNew) && (os.getRankPublishPreference(this)) )  {
		        			String strMessage;
		        			
		        			if (strRank == "President")
		        				strMessage = "Hello, Mr. President!";
		        			else
		        				strMessage = "Just rose to the rank of " + strRank;
		    				
		    				OpenListenFBConnect fb = new OpenListenFBConnect();
		    				fb.restoreSession(this);
		    				fb.publishFeed("", strMessage, "", "There's a new " + strRank + " in the " + track.mArtistName + " OpenListen Fan Club!", track.mArtistName +  " on OpenListen for Android", "http://svc.openlisten.com/images/" + strRank.replace(" ", "") + ".png");
		    				
		    				if (strArtistFBPage.length() > 2) {
		    						fb.publishOtherFeed(strArtistFBPage,"", strMessage, "", "There's a new " + strRank + " in the " + track.mArtistName + " OpenListen Fan Club!", track.mArtistName +  " on OpenListen for Android", "http://svc.openlisten.com/images/" + strRank.replace(" ", "") + ".png");		    					
		    				}
					}
					
				}
			}
		  }
		}
	}
	
	private void checkFBUserId() {
		OpenListenSettingsUtil os = new OpenListenSettingsUtil();
		String strCurrentFBId = os.getFbID(this);
		
		if (strCurrentFBId == null) {
			OpenListenFBConnect fb = new OpenListenFBConnect();
			fb.restoreSession(this);
			fb.getRunner().request("me", new UserRequestListener());
		} else {
			// Once we have Facebook UserId, create an OpenListen account if we need to
			int iOLID = os.getOLID(this);
			
			if (iOLID == -1) {
				OLServiceConnection ols = new OLServiceConnection();
				iOLID = ols.CreateUserAccount(Long.parseLong(strCurrentFBId), "", "", os.getOLUsername(this));
				os.setOLID(this, iOLID);
			}
		}
	}
	
	
	private MusicTrack getTrackFromStockPlayer(String strIntent) {
		MusicTrack track = null;
		
		// Make sure we have a connection and, if not, establish one
		if (conn == null) {
			setMediaPlayerConnection(strIntent);
		}
		
		if (conn != null) {
			if (conn.isConnected() && conn.isPlaying()) {
				track = conn.getCurrentMusicTrack();
			}
		}
		
		return track;
	}
	
	
	private void publishPlaylist() {
		// Publish the daily list, if requested
		OpenListenSettingsUtil os = new OpenListenSettingsUtil();
		//os.setDebugString(this, "Inside PublishPlaylist");
		
		if (os.getDailyPublishPreference(this)) {
			//os.setDebugString(this, "Daily Publish Preference is True");
			Date d = new Date();
			int iDay = d.getDay();
			int iLast = os.getLastDayPublished(this); 
			
			//os.setDebugString(this, "Last Published: " + String.valueOf(iLast) + ", Today: " + String.valueOf(iDay));
			
			if ( (iLast != iDay) ) {
				//os.setDebugString(this, "After 5pm and New Day");
				String strPlaylist = OpenListenDBUtil.getFormattedPlaylistForPublishing(this);
				if ( (strPlaylist != null) && (strPlaylist.length() > (OpenListenDBUtil.mPlaylistFooterLength + 2)) ) {
					//os.setDebugString(this, "Good Playlist");
					OpenListenFBConnect fb = new OpenListenFBConnect();
					fb.restoreSession(this);
					if (fb.isSessionValid()) {
						//os.setDebugString(this, "Valid FB Session");
						fb.publishPlaylist("My Daily Playlist", strPlaylist, "notes");
					}
				}
			}
		}
	}
	
	private void setDBConnection()
	{
        mDbHelper = new OpenListenDBAdapter(this);
        mDbHelper.open();
	}

	private void setMediaPlayerConnection(String strIntent)
	{
		if (strIntent.contains("music")) {
			//ActivityManager am = (ActivityManager)this.getSystemService(ACTIVITY_SERVICE);
			//List<ActivityManager.RunningServiceInfo> rs = am.getRunningServices(50);
			String strProvider = "com.android.music";
			Intent i = new Intent();
			
			if (strIntent.contains(".htc")) {
				strProvider = "com.htc.music";
				i.setClassName(strProvider, strProvider + ".MediaPlaybackService");				
			} else if (strIntent.contains(".mixzing")) {
				strProvider = "com.mixzing.basic";
				i.setClassName(strProvider, strProvider + ".MediaPlaybackService");				
			} else if (strIntent.contains("cmp.openlisten")) {
				strProvider = "cmp.openlisten.music";
				i.setClassName(strProvider, strProvider + ".MediaPlaybackService");				
			}
			else if (strIntent.contains(".mplayer")) {
				strProvider = "com.rb.mplayer";
				i.setClassName(strProvider, strProvider + ".PlaybackService");				
			}
			
			if (conn == null) {
				conn = new MediaPlayerServiceConnection();
				boolean b = this.bindService(i, conn, 0);
				
				if (!b) {
					Log.d("OpenListen", "Can't Conenct to media player");
				}
		}	
		}
	}


	public class UserRequestListener extends BaseRequestListener {

	    public void onComplete(final String response) {
	        try {
	            // process the response here: executed in background thread
	            Log.d("Facebook-Example", "Response: " + response.toString());
	            JSONObject json = Util.parseJson(response);
	            final String id = json.getString("id");
	            final String firstname = json.getString("first_name");
	            final String lastname = json.getString("last_name");
	            //final String gender = json.getString("gender");
	            
	            //Save the FB user information
	            if ( (id != null) && (id.length() > 0) ) {
	            	OpenListenSettingsUtil os = new OpenListenSettingsUtil();
	            	// Set the local store of the facebook ID
	            	os.setFbID(OpenListenService.this, id);
	            	
	            	// Create an OL account
					OLServiceConnection ols = new OLServiceConnection();
					int iOLID = ols.CreateUserAccount(Long.parseLong(id), firstname, lastname, os.getOLUsername(OpenListenService.this));
					os.setOLID(_mctx, iOLID);
	            }
	            
	        } catch (JSONException e) {
	            Log.w("Facebook-Example", "JSON Error in response");
	        } catch (FacebookError e) {
	            Log.w("Facebook-Example", "Facebook Error: " + e.getMessage());
	        }
	    }
	}

}
