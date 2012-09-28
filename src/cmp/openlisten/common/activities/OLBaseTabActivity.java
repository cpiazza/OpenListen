package cmp.openlisten.common.activities;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import cmp.openlisten.R;
import cmp.openlisten.common.OLAdListener;
import cmp.openlisten.common.OpenListenSettingsUtil;

import com.admob.android.ads.AdListener;
import com.admob.android.ads.AdView;

public abstract class OLBaseTabActivity extends TabActivity implements AdListener {
	
	protected static final int HOME_ID 				= Menu.FIRST;
	protected static final int RANKINGS_ID 			= Menu.FIRST + 1;
	protected static final int WHOS_LISTENING_ID 	= Menu.FIRST + 2;
	protected static final int PLAYLIST_ID			= Menu.FIRST + 3;
	
	private AdView ad = null;
	
	private int _mExcludedMenu = -1; 
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setExcludedMenu();
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
    
    abstract void setExcludedMenu();
    
    protected void setMenuExclusion(int iWhichOne) {
    	_mExcludedMenu = iWhichOne;
    }
    
    protected void setAdKeywords(String strKeywords) {
    	if (ad != null) {
    		ad.setKeywords("Music MP3 " + strKeywords);
    	}
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	
    	if (ad != null) {
    		ad.requestFreshAd();
    	}
    }
    
    @Override
    public void onRestart() {
    	super.onRestart();
    	
    	if (ad != null) {
    		ad.requestFreshAd();
    	}
    }
    
	@Override
	public void onConfigurationChanged(Configuration c) {
		super.onConfigurationChanged(c);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if (_mExcludedMenu != HOME_ID) 
        	menu.add(0, HOME_ID,0, R.string.home);
        
        if (_mExcludedMenu != RANKINGS_ID)
        	menu.add(0, RANKINGS_ID, 0, R.string.rankings);
        
        if (_mExcludedMenu != WHOS_LISTENING_ID)
        	menu.add(0, WHOS_LISTENING_ID, 0, R.string.whos_listening);
        
        if (_mExcludedMenu != PLAYLIST_ID)
        	menu.add(0, PLAYLIST_ID, 0, R.string.view_playlist);
        
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
        case RANKINGS_ID:
        	viewMyRankings();
        	return true;
        case WHOS_LISTENING_ID:
        	viewWhosListening();
        	return true;        	
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    private void viewWhosListening() {
    	Intent i = new Intent(this, WhosListening.class);
    	startActivity(i);
    }    
    
    private void viewPlaylist() {
    	Intent i = new Intent(this, ViewPlaylist.class);
    	startActivity(i);
    }
    
    private void viewMyRankings() {
    	Intent i = new Intent(this, cmp.openlisten.common.activities.Rankings.class);
    	startActivity(i);    	
    }    
    
    private void goHome() {
    	Intent i = new Intent(this, cmp.openlisten.OpenListen.class);
    	startActivity(i);    	
    }    
	
	protected int getIntExtra(Bundle savedInstanceState) {
		
		int _id; 
		
        _id = (int) (savedInstanceState != null ? savedInstanceState.getInt("id") : -1);
        
        if (_id == -1) {
        	Bundle extras = getIntent().getExtras();            
        	_id = (int) (extras != null ? extras.getInt("id") : -1);
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
}
