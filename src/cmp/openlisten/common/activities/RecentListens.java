package cmp.openlisten.common.activities;

import java.net.MalformedURLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import cmp.openlisten.R;
import cmp.openlisten.common.ImageThreadLoader;
import cmp.openlisten.common.JSONArrayAdapter;
import cmp.openlisten.common.ImageThreadLoader.ImageLoadedListener;
import cmp.openlisten.common.service.OLServiceConnection;

public class RecentListens extends ListActivity {
	
	private JSONArrayAdapter ja = null;
	private String _strUsername;
	private ListView _mListView;
	private int _id;
	
	private ImageThreadLoader imageLoader = null;
	ImageView imgFB;
	private String _strFBID;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.recent_listens);
        
        _mListView = (ListView) findViewById(android.R.id.list);
        _mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				itemClick((JSONObject) ja.getItem(arg2));
			}
        	
        });
        
        _strUsername = getStringExtra(savedInstanceState, "Username");
        _strFBID = getStringExtra(savedInstanceState, "FBID");
        parseID(savedInstanceState);
        
        if (_strFBID.length() > 0) {
        	imageLoader = new ImageThreadLoader();
        	imgFB = (ImageView)findViewById(R.id.imgFB);

			Bitmap cachedImage = null;
			
			try {
				String uri = "http://graph.facebook.com/" + _strFBID + "/picture?type=large";
				
				cachedImage = imageLoader.loadImage(uri, new ImageLoadedListener() {

					@Override
					public void imageLoaded(Bitmap imageBitmap) {
						imgFB.setImageBitmap(imageBitmap);
					}
					
				});
			} catch (MalformedURLException e) {
				imgFB.setImageResource(R.drawable.star);
			}
			
			if (cachedImage != null) {
				imgFB.setImageBitmap(cachedImage);
			}        	
        	
        }
        
        TextView tv = (TextView)findViewById(R.id.txtRecentListensHeader);
        tv.setText("Recent listens for " + _strUsername);
        
    }
    
    private void itemClick(JSONObject jo) {
    	String strArtistName = "";
    	String strTrackName = "";
    	
    	try {
    		strArtistName = jo.getString("ArtistName");
    		strTrackName = jo.getString("TrackName");
    	} catch (JSONException e) {
    		e.printStackTrace();
    		return;
    	}
    	
    	Intent i = new Intent(this, Recommend.class);
    	i.putExtra("ArtistName", strArtistName);
    	i.putExtra("TrackName", strTrackName);
    	i.putExtra("FromPlaylist", 0);    	
    	startActivity(i);	
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	fillData();
    }
    
    private void finishFillData() {
    	setListAdapter(ja);
    }
    
    private void fillData() {
    	final ProgressDialog pd = ProgressDialog.show(this, "", "Loading...", true);
    	
    	final class UpdateThread extends Thread {
			@Override
			public void run() {
		        fillDataRun();
				handler.sendEmptyMessage(0);
			}
			
			private Handler handler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					pd.dismiss();
					finishFillData();
				}
			};
    	}
    	
		UpdateThread ut = new UpdateThread();
		ut.start();
	
		}
    
    
    private void fillDataRun() {
        OLServiceConnection ols = new OLServiceConnection();
        JSONArray jsoRecentListeners  = ols.getRecentListens(_id);    	
        
        // Create an array to specify the fields we want to display in the list
        String[] from = new String[]{"TrackName", "ArtistName", "CityName", "StateName"};
        
        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[]{R.id.txtRecentListensTrack, R.id.txtRecentListensArtist, R.id.txtRecentListensCity, R.id.txtRecentListensState};
        
        ja = new JSONArrayAdapter(jsoRecentListeners, R.layout.recent_listens_row, from, to, false);
    }
    
	private void parseID(Bundle savedInstanceState) {
		
        _id = (int) (savedInstanceState != null ? savedInstanceState.getInt("id") : -1);
        
        if (_id == -1) {
        	Bundle extras = getIntent().getExtras();            
        	_id = (int) (extras != null ? extras.getInt("id") : -1);
        }
		
	}
	
	private String getStringExtra(Bundle savedInstanceState, String strWhich) {
        // Get the row that was clicked.
        String strRet = (savedInstanceState != null ? savedInstanceState.getString(strWhich) : "");
        
        if (strRet == "") {
        	Bundle extras = getIntent().getExtras();            
        	strRet = (extras != null ? extras.getString(strWhich) : "");
        }
        
        return strRet;

	}
}
