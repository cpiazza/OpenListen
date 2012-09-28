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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import cmp.openlisten.R;
import cmp.openlisten.common.JSONArrayAdapter;
import cmp.openlisten.common.service.OLServiceConnection;

public class UserRankDetails extends OLBaseListActivity {
	
	private static final int HOME_ID 			= Menu.FIRST;
	private static final int MY_RANKINGS_ID 	= Menu.FIRST + 1;
	private static final int PLAYLIST_ID		= Menu.FIRST + 2;
	private static final int WHOS_LISTENING_ID 	= Menu.FIRST + 3;
	
	private TextView _mRankName;
	private TextView _mRankDescription;
	private ListView _mListView;
	private ImageView _RankIcon;
	private JSONArrayAdapter ja = null;
	private int _mOLID;
	private int _mRankId;
	private int _mlocaluser;
	
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated constructor stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_rank_details);
        
        _RankIcon = (ImageView)findViewById(R.id.RankIcon);
        
        String strRankName = getStringExtra(savedInstanceState, "RankName");
        _mOLID = getIntExtra(savedInstanceState, "id");
        _mRankId = getIntExtra(savedInstanceState, "RankId");
        _mlocaluser = getIntExtra(savedInstanceState, "localuser");
        
        if (_mRankId == 5)
        	_RankIcon.setImageResource(R.drawable.pres_50);
        else if (_mRankId == 4)
        	_RankIcon.setImageResource(R.drawable.vp_50);
        else if (_mRankId == 3)
        	_RankIcon.setImageResource(R.drawable.lieutenant_50);
        else if (_mRankId == 2)
        	_RankIcon.setImageResource(R.drawable.private_50);
        else
        	_RankIcon.setImageResource(R.drawable.civ_50);
        
        _mRankName = (TextView)findViewById(R.id.txtRankName);
        _mRankDescription =(TextView)findViewById(R.id.txtRankDescription);
        
        _mRankName.setText(strRankName);
        
        if (_mlocaluser == 1)
        	_mRankDescription.setText("Here are the top artists for which you hold the rank of " + strRankName);
        else 
        	_mRankDescription.setText("");
        
        _mListView = (ListView) findViewById(android.R.id.list);
        _mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				JSONObject jo = (JSONObject) ja.getItem(arg2);
				int id = -1;
				
				try {
					id = jo.getInt("ArtistId");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				userClick(arg1, id);
			}
        	
        });
        
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, HOME_ID,0, R.string.home);
        menu.add(0, MY_RANKINGS_ID, 0, R.string.rankings);
        menu.add(0, PLAYLIST_ID, 0, R.string.view_playlist);
        menu.add(0, WHOS_LISTENING_ID, 0, R.string.whos_listening);        
        
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
        case PLAYLIST_ID:
            viewPlaylist();
            return true;
        case HOME_ID:
        	goHome();
        	return true;
        case MY_RANKINGS_ID:
        	viewRankings();
        	return true;   
        case WHOS_LISTENING_ID:
        	viewWhosListening();
        	return true;         	
        }
        
        return super.onOptionsItemSelected(item);
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
    
    private void viewRankings() {
    	Intent i = new Intent(this, cmp.openlisten.common.activities.Rankings.class);
    	startActivity(i);    	
    }        
	
    private void userClick(View vw, int id) {
    	Intent i = new Intent(this, ArtistFanClubMembers.class);
    	i.putExtra("ArtistName", ((TextView)vw.findViewById(R.id.ArtistName)).getText());
    	i.putExtra("id", id);
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
        JSONArray jsoRecentListeners  = ols.getUserRankDetails(_mOLID, _mRankId);    	
        
        // Create an array to specify the fields we want to display in the list
        String[] from = new String[]{"ArtistName"};
        
        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[]{R.id.ArtistName};
        
        ja = new JSONArrayAdapter(jsoRecentListeners, R.layout.user_rank_details_row, from, to, false);
    }
    
	

	
	
	
}
