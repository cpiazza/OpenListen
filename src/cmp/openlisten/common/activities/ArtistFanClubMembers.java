package cmp.openlisten.common.activities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import cmp.openlisten.R;
import cmp.openlisten.common.JSONArrayAdapter;
import cmp.openlisten.common.service.OLServiceConnection;

public class ArtistFanClubMembers extends OLBaseListActivity {
	
	private static final int HOME_ID 			= Menu.FIRST;
	private static final int WHOS_LISTENING_ID 	= Menu.FIRST + 1;
	private static final int PLAYLIST_ID		= Menu.FIRST + 2;
	
	private ListView _mListView;
	private TextView _mHeader;
	
	private JSONArrayAdapter ja = null;
	private JSONArray jsoRecentListeners = null;
	
	private int _id;
	private String strArtistName;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.artist_fan_club_members);
        
        _id = this.getIntExtra(savedInstanceState, "id");
        strArtistName = this.getStringExtra(savedInstanceState, "ArtistName");
        
        _mHeader = (TextView)findViewById(R.id.txtArtistFanClubHeader);
        _mHeader.setText("Fan Club Members for " + strArtistName);
        
        _mListView = (ListView) findViewById(android.R.id.list);
        _mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				JSONObject jo = (JSONObject) ja.getItem(arg2);
				int id = -1;
				String _FBID = ""; 
				
				try {
					id = jo.getInt("UserAccountId");
					_FBID = jo.getString("FBID");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				userClick(arg1, id, _FBID);
			}
        	
        });
    }
    
    private void finishFillData() {
    	//Create an array to specify the fields we want to display in the list
        String[] from = new String[]{"FBID", "RankName", "Username", "Gender", "Age", "State"};
        
        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[]{R.id.imgFBPicture, R.id.txtRank, R.id.txtUsername, R.id.txtGender, R.id.txtAge, R.id.txtState};
        
        ja = new JSONArrayAdapter(jsoRecentListeners, R.layout.artist_fan_club_members_row, from, to, true);
        
    	setListAdapter(ja);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, HOME_ID,0, R.string.home);
        menu.add(0, WHOS_LISTENING_ID, 0, R.string.whos_listening);
        menu.add(0, PLAYLIST_ID, 0, R.string.view_playlist);
        
        return true;
    }
    
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
        case PLAYLIST_ID:
            viewPlaylist();
            return true;
        case HOME_ID:
        	goHome();
        	return true;
        case WHOS_LISTENING_ID:
        	viewWhosListening();
        	return true;        	
        }
        
        return super.onMenuItemSelected(featureId, item);
    }
    
    private void viewPlaylist() {
    	Intent i = new Intent(this, ViewPlaylist.class);
    	startActivity(i);
    }
    
    private void viewWhosListening() {
    	Intent i = new Intent(this, cmp.openlisten.common.activities.WhosListening.class);
    	startActivity(i);    	
    }    
    
    private void goHome() {
    	Intent i = new Intent(this, cmp.openlisten.OpenListen.class);
    	startActivity(i);    	
    }    
    
    
    @Override
    protected void onResume() {
    	super.onResume();
    	fillData();
    }
    
    @Override
    protected void onRestart() {
    	super.onRestart();
    }
    
    private void userClick(View vw, int id, String _FBID) {
    	Intent i = new Intent(this, ViewUser.class);
    	i.putExtra("Username", ((TextView)vw.findViewById(R.id.txtUsername)).getText());
    	i.putExtra("Age", ((TextView)vw.findViewById(R.id.txtAge)).getText());
    	i.putExtra("State", ((TextView)vw.findViewById(R.id.txtState)).getText());
    	i.putExtra("Gender", ((TextView)vw.findViewById(R.id.txtGender)).getText());
    	i.putExtra("id", id);
    	i.putExtra("FBID", _FBID);
    	startActivity(i);
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
        jsoRecentListeners  = ols.getArtistFanClubMembers(_id);
    }    
}
