package cmp.openlisten;

import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import cmp.openlisten.common.OLAdListener;
import cmp.openlisten.common.OpenListenSettingsUtil;
import cmp.openlisten.common.activities.FBLogin;
import cmp.openlisten.common.activities.Rankings;
//import cmp.openlisten.common.activities.Upgrade;
import cmp.openlisten.common.activities.ViewPlaylist;
import cmp.openlisten.common.service.OpenListenService;
import cmp.openlisten.facebook.OpenListenFBConnect;

import com.admob.android.ads.AdListener;
import com.admob.android.ads.AdView;

public class OpenListen extends Activity implements AdListener  {
	private static final int ACTIVITY_VIEW_PLAYLIST = 0;
	private static final int ACTIVITY_VIEW_SETTINGS = 2;
	//private static final int ACTIVITY_VIEW_UPGRADE = 3;

    
    private ImageView _btnViewPlaylist;
    private ImageView _btnListen;    
    private TextView _btnSettings;
    //private TextView _btnUpgrade; 
    private ImageView _btnWhosListening;
    private ImageView _btnMyRanking; 
    private TextView _btnSupport;
    private TextView _txtFullWarning;
    private OpenListenFBConnect fb;
        
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        fb = new OpenListenFBConnect();
        fb.restoreSession(this);
        
        if (!fb.isSessionValid()) {
        	Intent i = new Intent(this, FBLogin.class);
        	startActivity(i);
        }
        
        OpenListenSettingsUtil os = new OpenListenSettingsUtil();
        os.setShowAds(this, true);
        
        _txtFullWarning = (TextView) findViewById(R.id.txtFullWarning);
        //_btnUpgrade = (TextView) findViewById(R.id.btnUpgrade);
        _btnSupport = (TextView) findViewById(R.id.btnSupport);
        _btnSupport.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				_btnSupport.setVisibility(View.INVISIBLE);
				supportEmail();
			}
        });
        
        _btnListen = (ImageView)findViewById(R.id.btnListen);
        _btnListen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				_btnListen.setVisibility(View.INVISIBLE);
				viewPlayer();
			}
        });
        
        _btnMyRanking = (ImageView)findViewById(R.id.btnRanking);
        _btnMyRanking.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				_btnMyRanking.setVisibility(View.INVISIBLE);
				viewRankings();
				
			}
        	
        });
        
        // Start the service
        if (!isFullInstalled()) { 
        	Intent i = new Intent(this, OpenListenService.class);
        	this.startService(i);
        	_txtFullWarning.setVisibility(View.INVISIBLE);
        	//_btnUpgrade.setVisibility(View.VISIBLE);
        } else {
        	_txtFullWarning.setVisibility(View.VISIBLE);
        	//_btnUpgrade.setVisibility(View.INVISIBLE);
        }
        
        _btnViewPlaylist = (ImageView) findViewById(R.id.btnViewPlaylist);
        _btnViewPlaylist.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				_btnViewPlaylist.setVisibility(View.INVISIBLE);
				viewPlaylist();
			}
        	
        });
        
        _btnSettings = (TextView) findViewById(R.id.btnSettings);
        _btnSettings.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				viewSettings();
			}
        	
        });
        
        _btnWhosListening = (ImageView) findViewById(R.id.btnWhosListening);
        _btnWhosListening.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				_btnWhosListening.setVisibility(View.INVISIBLE);
				viewWhosListening();
			}        	
        });
        
        AdView ad = (AdView) findViewById(R.id.ad);
        ad.setAdListener(new OLAdListener());
        ad.setVisibility( View.VISIBLE );
        
        // Register our broadcast receivers 
        // IntentFilter filter = new IntentFilter();  
        //filter.addAction(Intent.ACTION_TIME_TICK);
        //this.registerReceiver(new BroadcastReceiver() {

			//@Override
			//public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
             //   Intent i = new Intent(context, OpenListenService.class);
             //   i.setAction(intent.getAction());
             //   context.startService(i);
			//}
        	
        //}, filter);
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	_btnWhosListening.setVisibility(View.VISIBLE);
    	_btnSupport.setVisibility(View.VISIBLE);
    	_btnSettings.setVisibility(View.VISIBLE);
    	_btnViewPlaylist.setVisibility(View.VISIBLE);
    	_btnListen.setVisibility(View.VISIBLE);
    	_btnMyRanking.setVisibility(View.VISIBLE);
    }
    
    @Override
    public void onRestart() {
    	super.onRestart();
    	_btnWhosListening.setVisibility(View.VISIBLE);
    	_btnSupport.setVisibility(View.VISIBLE);
    	_btnSettings.setVisibility(View.VISIBLE);
    	_btnViewPlaylist.setVisibility(View.VISIBLE);
    	_btnListen.setVisibility(View.VISIBLE);
    	_btnMyRanking.setVisibility(View.VISIBLE);    	
    }
    
    private void supportEmail() {
    	Intent i = new Intent(android.content.Intent.ACTION_SEND);
    	String strTo[] = {"admin@openlisten.com"};
    	
    	i.putExtra(android.content.Intent.EXTRA_EMAIL, strTo);
    	i.putExtra(android.content.Intent.EXTRA_SUBJECT, "OpenListen Support Request");
    	i.setType("plain/text");
    	
    	startActivity(i);  
    } 
    
    private void viewRankings() {
    	Intent i = new Intent(this, Rankings.class);
    	startActivity(i);
    }
    /**
    private void viewUpgrade() {
    	Intent i = new Intent(this, Upgrade.class);
    	startActivityForResult(i, ACTIVITY_VIEW_UPGRADE);
    }
    **/ 
    private void viewPlaylist() { 
    	Intent i = new Intent(this, ViewPlaylist.class);
    	startActivityForResult(i, ACTIVITY_VIEW_PLAYLIST);
    }
       
    private void viewWhosListening() { 
    	Intent i = new Intent(this, cmp.openlisten.common.activities.WhosListening.class);
    	startActivityForResult(i, ACTIVITY_VIEW_SETTINGS);    	
    } 
    
    private void viewPlayer() {
    	Intent i = new Intent(this, cmp.openlisten.music.MusicBrowserActivity.class);
    	startActivity(i);    	
    }
    
    
    private void viewSettings() {
    	Intent i = new Intent(this, cmp.openlisten.common.activities.OpenListenSettings.class);
    	startActivity(i);    	
    }
    
    private boolean isFullInstalled() {
    	boolean bRet = false;
    	
    	final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
    	mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
    	
    	final List<ResolveInfo> pkgAppsList = this.getPackageManager().queryIntentActivities( mainIntent, 0);
    	
    	Iterator<ResolveInfo> it = pkgAppsList.iterator();
    	
    	while (it.hasNext()) {
    		ResolveInfo ri = (ResolveInfo)it.next();
    		if (ri.activityInfo.name.contains("OpenListenFull")) {
    			bRet = true;
    			break;
    		}
    	}

    	return bRet;
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
 
}