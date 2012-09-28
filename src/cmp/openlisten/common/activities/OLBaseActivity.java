package cmp.openlisten.common.activities;

import cmp.openlisten.R;
import cmp.openlisten.common.OLAdListener;
import cmp.openlisten.common.OpenListenSettingsUtil;

import com.admob.android.ads.AdListener;
import com.admob.android.ads.AdView;

import android.os.Bundle;
import android.view.View;

public class OLBaseActivity extends OLBaseMenu implements AdListener {

	private AdView ad = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    }
    
    @Override
    public void setContentView(int ID) {
    	super.setContentView(ID);
    	
    	ad = (AdView) findViewById(R.id.ad);
    	ad.setKeywords("Music MP3");
        
    	OpenListenSettingsUtil os = new OpenListenSettingsUtil();
        if (os.showAds(this))
        {
            ad.setAdListener(new OLAdListener());
        	ad.setVisibility( View.VISIBLE );
        }
        else 
        	ad.setVisibility( View.INVISIBLE );
    	
    }
    
    protected void setAdKeywords(String strKeywords) {
    	if (ad != null) {
    		ad.setKeywords("Music MP3 " + strKeywords);
    	}
    }
    
    protected int getIntExtra(Bundle savedInstanceState, String strWhich) {
		
        int _id = (int) (savedInstanceState != null ? savedInstanceState.getInt(strWhich) : -1);
        
        if (_id == -1) {
        	Bundle extras = getIntent().getExtras();            
        	_id = (int) (extras != null ? extras.getInt(strWhich) : -1);
        }
        
        return _id;
		
	}
	
	protected String getStringExtra(Bundle savedInstanceState, String strWhich) {
        // Get the row that was clicked.
        String strRet = (savedInstanceState != null ? savedInstanceState.getString(strWhich) : "");
        
        if (strRet == "") {
        	Bundle extras = getIntent().getExtras();            
        	strRet = (extras != null ? extras.getString(strWhich) : "");
        }
        
        return strRet;

	}    
	
	@Override
	public void onFailedToReceiveAd(AdView arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFailedToReceiveRefreshedAd(AdView arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onReceiveAd(AdView arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onReceiveRefreshedAd(AdView arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	void setExcludedMenu() {
		setMenuExclusion(-1);
	}

}
