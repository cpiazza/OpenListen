package cmp.openlisten.common.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;
import cmp.openlisten.R;

public class WhosListening extends OLBaseTabActivity{
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.whos_listening);
        
        Resources res = getResources();
        TabHost tabHost = getTabHost();
        TabHost.TabSpec spec;
        Intent intent;
        
        // Listening Now Tab
        intent = new Intent().setClass(this, ListeningNow.class);
        spec = tabHost.newTabSpec("Now").setIndicator("Now", res.getDrawable(R.drawable.music_note)).setContent(intent);
        tabHost.addTab(spec);
        
        // Recent Recommendations Tab
        intent = new Intent().setClass(this, RecentRecommendations.class);
        spec = tabHost.newTabSpec("Likes").setIndicator("Likes", res.getDrawable(R.drawable.reco)).setContent(intent);
        tabHost.addTab(spec);
    }

	@Override
	void setExcludedMenu() {
		// TODO Auto-generated method stub
		this.setMenuExclusion(OLBaseTabActivity.WHOS_LISTENING_ID);
	}
    


}
