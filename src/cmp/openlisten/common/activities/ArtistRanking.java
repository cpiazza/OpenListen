package cmp.openlisten.common.activities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import cmp.openlisten.R;
import cmp.openlisten.common.JSONArrayAdapter;
import cmp.openlisten.common.service.OLServiceConnection;

public class ArtistRanking extends ListActivity {

	private JSONArrayAdapter ja = null;
	private ListView _mListView;
	
	private RadioButton _rbDaily;
	private RadioButton _rbWeekly;
	private RadioButton _rbAllTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.artist_ranking);
        
        _rbDaily = (RadioButton)findViewById(R.id.daily);
        _rbDaily.setChecked(true);
        _rbDaily.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if (arg1) {
					fillData();
				}
			}
        	
        });
        
        _rbWeekly = (RadioButton)findViewById(R.id.weekly);
        _rbWeekly.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if (arg1) {
					fillData();
				}
			}
        	
        });

        
        _rbAllTime = (RadioButton)findViewById(R.id.allTime);
        _rbAllTime.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if (arg1) {
					fillData();
				}
			}
        	
        });

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
				
				artistClick(arg1, id);
			}
        	
        });

    }
    
    private void artistClick(View vw, int id) {
    	Intent i = new Intent(this, ArtistFanClubMembers.class);
    	i.putExtra("ArtistName", ((TextView)vw.findViewById(R.id.txtArtistName)).getText());
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
        JSONArray jsoRecentListeners = null;
        
        if (_rbDaily.isChecked()) {
        	jsoRecentListeners = ols.getTopArtists("Daily");
        } else if (_rbWeekly.isChecked()) {
        	jsoRecentListeners = ols.getTopArtists("Weekly");
        } else {
        	jsoRecentListeners = ols.getTopArtists("AllTime");
        }
        
        // Create an array to specify the fields we want to display in the list
        String[] from = new String[]{"ArtistName", "ArtistRank"};
        
        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[]{R.id.txtArtistName, R.id.txtArtistRank};
        
        ja = new JSONArrayAdapter(jsoRecentListeners, R.layout.artist_ranking_row, from, to, false);
    }
    
	
}
