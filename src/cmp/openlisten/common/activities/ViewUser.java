package cmp.openlisten.common.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;
import cmp.openlisten.R;

public class ViewUser extends OLBaseTabActivity {
	
	private int _id;
	
	private String _strUsername;
	private String _strFBID;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_user);
        
        _strUsername = getStringExtra(savedInstanceState, "Username");
        _strFBID = getStringExtra(savedInstanceState, "FBID");
        _id = getIntExtra(savedInstanceState);
        
        Resources res = getResources();
        TabHost tabHost = getTabHost();
        TabHost.TabSpec spec;
        Intent intent;
        
        // Listening Now Tab
        intent = new Intent().setClass(this, RecentListens.class);
        intent.putExtra("id", _id);
        intent.putExtra("Username", _strUsername);
        intent.putExtra("FBID", _strFBID);
        spec = tabHost.newTabSpec("Recent Listens").setIndicator("Recent Listens", res.getDrawable(R.drawable.music_note)).setContent(intent);
        tabHost.addTab(spec);
        
        // Recent Recommendations Tab
        intent = new Intent().setClass(this, RecentRecommendations.class);
        intent.putExtra("id", _id);
        intent.putExtra("Username", _strUsername);
        intent.putExtra("FBID", _strFBID);
        spec = tabHost.newTabSpec("Likes").setIndicator("Likes", res.getDrawable(R.drawable.reco)).setContent(intent);
        tabHost.addTab(spec);
        
        // User Ranking Tab
        intent = new Intent().setClass(this, UserRanking.class);
        intent.putExtra("id", _id);
        intent.putExtra("Username", _strUsername);
        intent.putExtra("FBID", _strFBID);
        spec = tabHost.newTabSpec("Rankings").setIndicator("Rankings", res.getDrawable(R.drawable.star)).setContent(intent);
        tabHost.addTab(spec);
                
	}
	
	@Override
	void setExcludedMenu() {
		// TODO Auto-generated method stub
		this.setMenuExclusion(-1);
	}	
	

}

