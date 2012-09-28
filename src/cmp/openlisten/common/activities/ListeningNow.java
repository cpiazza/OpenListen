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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import cmp.openlisten.R;
import cmp.openlisten.common.JSONArrayAdapter;
import cmp.openlisten.common.service.OLServiceConnection;


public class ListeningNow extends ListActivity {
	private static final int ACTIVITY_VIEW_USER 	= 1;
	
	private ListView _mListView;
	private JSONArrayAdapter ja = null;
	private JSONArray jsoRecentListeners = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.listening_now);
        
        _mListView = (ListView) findViewById(android.R.id.list);
        _mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				JSONObject jo = (JSONObject) ja.getItem(arg2);
				int id = -1;
				String strFBID = "";
				
				try {
					id = jo.getInt("UserAccountId");
					strFBID = jo.getString("FBID");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				userClick(arg1, id, strFBID);
			}
        	
        });
        
        
    }
    
    private void finishFillData() {
    	
    	if (jsoRecentListeners != null) {
    		//Create an array to specify the fields we want to display in the list
    		String[] from = new String[]{"FBID", "Username", "Gender", "Age", "State"};
        
    		// and an array of the fields we want to bind those fields to (in this case just text1)
    		int[] to = new int[]{R.id.imgFBPicture, R.id.txtListeningNowUsername, R.id.txtListeningNowGender, R.id.txtListeningNowAge, R.id.txtListeningNowState};
        
    		ja = new JSONArrayAdapter(jsoRecentListeners, R.layout.listening_now_row, from, to, true);
        
    		setListAdapter(ja);
    	}
    }
    
    
    @Override
    protected void onResume() {
    	super.onResume();
   		fillData();
    }
    
    
    private void userClick(View vw, int id, String FBID) {
    	Intent i = new Intent(this, ViewUser.class);
    	i.putExtra("Username", ((TextView)vw.findViewById(R.id.txtListeningNowUsername)).getText());
    	i.putExtra("Age", ((TextView)vw.findViewById(R.id.txtListeningNowAge)).getText());
    	i.putExtra("State", ((TextView)vw.findViewById(R.id.txtListeningNowState)).getText());
    	i.putExtra("Gender", ((TextView)vw.findViewById(R.id.txtListeningNowGender)).getText());
    	i.putExtra("FBID", FBID);
    	i.putExtra("id", id);
    	startActivityForResult(i, ACTIVITY_VIEW_USER);
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
        jsoRecentListeners  = ols.getRecentListeners();
    }
}
