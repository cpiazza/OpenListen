package cmp.openlisten.common.activities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TableRow;
import android.widget.TextView;
import cmp.openlisten.R;
import cmp.openlisten.common.OpenListenSettingsUtil;
import cmp.openlisten.common.service.OLServiceConnection;

public class MyRanking extends Activity {
	
	private TextView _mPresText;
	private TextView _mVPText;
	private TextView _mLieuText;
	private TextView _mPrivateText;
	private TextView _mCivText;
	
	private TextView _mPresTextHeader;
	private TextView _mVPTextHeader;
	private TextView _mLieuTextHeader;
	private TextView _mPrivateTextHeader;
	private TextView _mCivTextHeader;	

	private TableRow _mTableRow1;
	private TableRow _mTableRow2;
	private TableRow _mTableRow3;
	private TableRow _mTableRow4;
	private TableRow _mTableRow5;
	
	private JSONArray jsoUserRankSummary;
	private int _id;

	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated constructor stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_ranking);
        
        _mPresText = (TextView)findViewById(R.id.txtPresident);
        _mVPText = (TextView)findViewById(R.id.txtVicePresident);
        _mLieuText = (TextView)findViewById(R.id.txtLieutenant);
        _mPrivateText = (TextView)findViewById(R.id.txtPrivate);
        _mCivText = (TextView)findViewById(R.id.txtCivilian);
        
        _mPresTextHeader = (TextView)findViewById(R.id.txtPresidentHeader);
        _mVPTextHeader = (TextView)findViewById(R.id.txtVicePresidentHeader);
        _mLieuTextHeader = (TextView)findViewById(R.id.txtLieutenantHeader);
        _mPrivateTextHeader = (TextView)findViewById(R.id.txtPrivateHeader);
        _mCivTextHeader = (TextView)findViewById(R.id.txtCivilianHeader);        
        
        _mTableRow1 = (TableRow)findViewById(R.id.myRankRow1);
        _mTableRow1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				viewDetails(5, "President");
			}
        	
        });
        
        _mTableRow2 = (TableRow)findViewById(R.id.myRankRow2);
        _mTableRow2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				viewDetails(4, "Vice President");
			}
        	
        });  
        
        _mTableRow3 = (TableRow)findViewById(R.id.myRankRow3);
        _mTableRow3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				viewDetails(3, "Lieutenant");
			}
        	
        });        
        
        _mTableRow4 = (TableRow)findViewById(R.id.myRankRow4);
        _mTableRow4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				viewDetails(2, "Private");
			}
        	
        });        
        
        _mTableRow5 = (TableRow)findViewById(R.id.myRankRow5);
        _mTableRow5.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				viewDetails(1, "Civilian");
			}
        	
        });
        
        
        
        fillData();
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
    
	private void viewDetails(int iRankId, String strRank) {
		Intent i = new Intent(this, UserRankDetails.class);
		i.putExtra("RankId", iRankId);
		i.putExtra("RankName", strRank);
		i.putExtra("id", _id);
		i.putExtra("localuser", 1);
		
		startActivity(i);
	}

    private void finishFillData() {
    	try {
    		if ( (jsoUserRankSummary != null) && (jsoUserRankSummary.length() > 0) ) {
    			//President
    			JSONObject jso = (JSONObject) jsoUserRankSummary.get(0);
    			int strRankCount = jso.getInt("RankCount");
			
    			if (strRankCount == 0)
    				_mPresText.setText("Oh, Dear. You have yet to achieve the lofty rank of President in any OpenListen Artist Fan Clubs. The only way to rise to the top is by listening to more songs and making more recommendations. Better get crackin'");
    			else if (strRankCount == 1)
    				_mPresText.setText("Well, this is a start. You're President in just one Artist Fan Club. Keep listening and making recommendations.");
    			else
    				_mPresText.setText("Impressive. You are Top Dog, Head Honcho, Mr. President in " + String.valueOf(strRankCount) + " OpenListen Artist Fan Clubs.");
    			
    			_mPresTextHeader.setText("President (" + String.valueOf(strRankCount) + ")");
    			
    			// Vice President
    			jso = (JSONObject) jsoUserRankSummary.get(1);
    			int strVPRankCount = jso.getInt("RankCount");
    			
    			if (strVPRankCount == 0) {
    				if (strRankCount == 0)
    					_mVPText.setText("Seems the higher ranks are eluding you. No Presidencies AND no Vice Presidencies? You know what needs to be done...");
    				else
    					_mVPText.setText("You've tasted the top, but don't get lazy on us. Fire up that music player.");
    			}
    			else if (strVPRankCount == 1)
    				_mVPText.setText("Okay, okay. You're Vice President in One OpenListen Artist Fan Club. You can do better.");
    			else
    				_mVPText.setText("This is more like it. You've risen to Vice Presidnet in " + String.valueOf(strVPRankCount) + " OpenListen Artist Fan Clubs. Keep up the good work.");
    			
    			_mVPTextHeader.setText("Vice President (" + String.valueOf(strVPRankCount) + ")");
    			
    			// Lieutenant
    			jso = (JSONObject) jsoUserRankSummary.get(2);
    			int strLieuRankCount = jso.getInt("RankCount");
    			
    			if (strLieuRankCount == 0) {
    				if ( (strVPRankCount == 0) && (strRankCount == 0) )  
    					_mLieuText.setText("Well, this is embarassing. The top three ranks are EMPTY for you. How do you look at yourself in the mirror?");
    				else
    					_mLieuText.setText("Find some new Artists to recommend and fill out this rank a bit.");
    			}
    			else if (strLieuRankCount == 1)
    				_mLieuText.setText("Find some new Artists to recommend and fill out this rank a bit.");
    			else
    				_mLieuText.setText("Solid showing here. You're a Lieutenant in " + String.valueOf(strLieuRankCount) + " OpenListen Artist Fan Clubs.");
    			
    			_mLieuTextHeader.setText("Lieutenant (" + String.valueOf(strLieuRankCount) + ")");
    			
    			// Private
    			jso = (JSONObject) jsoUserRankSummary.get(3);
    			int strPrivateRankCount = jso.getInt("RankCount");
    			
    			if (strPrivateRankCount == 0) {
    				if ( (strVPRankCount == 0) && (strRankCount == 0) && (strLieuRankCount == 0) )  
    					_mPrivateText.setText("You're demonstrating a disturbing lack of ambition. Nothing a Private or above.");
    				else
    					_mPrivateText.setText("Nothing wrong with the rank of Private. Listen to some new Artists, don't be shy.");
    			}
    			else if (strPrivateRankCount == 1)
    				_mPrivateText.setText("Nothing wrong with the rank of Private. Listen to some new Artists, don't be shy.");
    			else
    				_mPrivateText.setText("A Private in " + String.valueOf(strPrivateRankCount) + " OpenListen Artist Fan Clubs. You're serving your Nation well.");    			
    			
    			_mPrivateTextHeader.setText("Private (" + String.valueOf(strPrivateRankCount) + ")");
    			
    			// Civilian
    			jso = (JSONObject) jsoUserRankSummary.get(4);
    			int strCivilianRankCount = jso.getInt("RankCount");
    			
    			if (strCivilianRankCount == 0) {
    				if ( (strVPRankCount == 0) && (strRankCount == 0) && (strLieuRankCount == 0) && (strPrivateRankCount == 0))  
    					_mCivText.setText("Nothing. Nada. Zip. And you call yourself a music fan!?!");
    				else
    					_mCivText.setText("Keep expanding those musical horizons. Every new artist you listen to gets you started as a Civilian.");
    			}
    			else if (strCivilianRankCount == 1)
    				_mCivText.setText("Keep expanding those musical horizons. Every new artist you listen to can get you started as a Civilian.");
    			else
    				_mCivText.setText("Ah, to be among the masses. You're a Civilian in " + String.valueOf(strCivilianRankCount) + " OpenListen Artist Fan Clubs. We have high hopes for your continued rise to the top!");
    			
    			_mCivTextHeader.setText("Civilian (" + String.valueOf(strCivilianRankCount) + ")");
    			
    		}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }    
    
    private void fillDataRun() {
        OLServiceConnection ols = new OLServiceConnection();
        OpenListenSettingsUtil ou = new OpenListenSettingsUtil();
        _id = ou.getOLID(this);
        
        jsoUserRankSummary  = ols.getUserRankSummary(_id);

     }
}
