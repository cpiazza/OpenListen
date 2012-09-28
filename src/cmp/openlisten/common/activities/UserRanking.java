package cmp.openlisten.common.activities;

import java.net.MalformedURLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import cmp.openlisten.R;
import cmp.openlisten.common.ImageThreadLoader;
import cmp.openlisten.common.ImageThreadLoader.ImageLoadedListener;
import cmp.openlisten.common.service.OLServiceConnection;

public class UserRanking extends Activity {
	
	private TextView _mHeader;
	private TextView _mPresident;
	private TextView _mVicePresident;
	private TextView _mLieutenant;
	private TextView _mPrivate;
	private TextView _mCivilian;
	
	private ImageView _mPresidentImg;
	private ImageView _mVicePresidentImg;
	private ImageView _mLieutenantImg;
	private ImageView _mPrivateImg;
	private ImageView _mCivilianImg;	
	
	private int _id;
	private String _Username;
	
	private ImageThreadLoader imageLoader = null;
	ImageView imgFB;
	private String _strFBID;

	private JSONArray jsoUserRankSummary;
	
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated constructor stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_ranking);
        
        _mHeader = (TextView)findViewById(R.id.txtUserRankingHeader);
        _mPresident = (TextView)findViewById(R.id.txtUserRankPres);
        _mVicePresident = (TextView)findViewById(R.id.txtUserRankVicePres);
        _mLieutenant = (TextView)findViewById(R.id.txtUserRankLieu);
        _mPrivate = (TextView)findViewById(R.id.txtUserRankPrivate);
        _mCivilian 	= (TextView)findViewById(R.id.txtUserRankCivilian);
        
        _mPresidentImg = (ImageView)findViewById(R.id.userRankPres);
        _mVicePresidentImg = (ImageView)findViewById(R.id.userRankVicePres);
        _mLieutenantImg = (ImageView)findViewById(R.id.userRankLieu);
        _mPrivateImg = (ImageView)findViewById(R.id.userRankPrivate);
        _mCivilianImg 	= (ImageView)findViewById(R.id.userRankCivilian);        
        
        _id = getIntExtra(savedInstanceState, "id");
        _Username = getStringExtra(savedInstanceState, "Username");
        _strFBID = getStringExtra(savedInstanceState, "FBID");
        
        if (_strFBID.length() > 0) {
        	imageLoader = new ImageThreadLoader();
        	imgFB = (ImageView)findViewById(R.id.imgFB);

			Bitmap cachedImage = null;
			
			try {
				String uri = "http://graph.facebook.com/" + _strFBID + "/picture?type=square";
				
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
        
        _mHeader.setText("Artist Fan Club Rankings for " + _Username);
        
        _mPresidentImg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				viewDetails(5, _Username + " is President of these Artist Fan Clubs");
				
			}
        });
        
        _mVicePresidentImg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				viewDetails(4, _Username + " is a Vice President in these Artist Fan Clubs");
				
			}
        	
        });
        
        _mLieutenantImg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				viewDetails(3, _Username + " is a Lieutenant in these Artist Fan Clubs");
				
			}
        	
        });        
        
        _mPrivateImg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				viewDetails(2, _Username + " is a Private in these Artist Fan Clubs");
				
			}
        	
        });        
        
        
        _mCivilianImg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				viewDetails(1, _Username + " is a Civilian in these Artist Fan Clubs");
				
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
    
    private void finishFillData() {
		JSONObject jso;
		try {
			jso = (JSONObject) jsoUserRankSummary.get(0);
			int i = jso.getInt("RankCount");
	    	_mPresident.setText("President (" + String.valueOf(i) + ")");
	    	
			jso = (JSONObject) jsoUserRankSummary.get(1);
			i = jso.getInt("RankCount");
	    	_mVicePresident.setText("Vice President (" + String.valueOf(i) + ")");
	    	
			jso = (JSONObject) jsoUserRankSummary.get(2);
			i = jso.getInt("RankCount");
	    	_mLieutenant.setText("Lieutenant (" + String.valueOf(i) + ")");
	    	
			jso = (JSONObject) jsoUserRankSummary.get(3);
			i = jso.getInt("RankCount");
	    	_mPrivate.setText("Private (" + String.valueOf(i) + ")");
	    	
			jso = (JSONObject) jsoUserRankSummary.get(4);
			i = jso.getInt("RankCount");
	    	_mCivilian.setText("Civilian (" + String.valueOf(i) + ")");	    	
	    	
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    }
    
	private void viewDetails(int iRankId, String strRank) {
		Intent i = new Intent(this, UserRankDetails.class);
		i.putExtra("RankId", iRankId);
		i.putExtra("RankName", strRank);
		i.putExtra("id", _id);
		i.putExtra("localuser", 0);
		
		startActivity(i);
	}
    
    private void fillDataRun() {
        OLServiceConnection ols = new OLServiceConnection();
        
        jsoUserRankSummary  = ols.getUserRankSummary(_id);

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
    
    
    private int getIntExtra(Bundle savedInstanceState, String strWhich) {
		
        int _id = (int) (savedInstanceState != null ? savedInstanceState.getInt(strWhich) : -1);
        
        if (_id == -1) {
        	Bundle extras = getIntent().getExtras();            
        	_id = (int) (extras != null ? extras.getInt(strWhich) : -1);
        }
        
        return _id;
		
	}    
}
