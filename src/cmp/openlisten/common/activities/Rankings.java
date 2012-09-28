package cmp.openlisten.common.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;
import cmp.openlisten.R;

public class Rankings extends OLBaseTabActivity {
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rankings);
        
        Resources res = getResources();
        TabHost tabHost = getTabHost();
        TabHost.TabSpec spec;
        Intent intent;
        
        // My Ranking
        intent = new Intent().setClass(this, MyRanking.class);
        spec = tabHost.newTabSpec("My Ranking").setIndicator("My Ranking", res.getDrawable(R.drawable.star)).setContent(intent);
        tabHost.addTab(spec);
        
        // Artist Ranking
        intent = new Intent().setClass(this, ArtistRanking.class);
        spec = tabHost.newTabSpec("Top Artists").setIndicator("Top Artists", res.getDrawable(R.drawable.music_note)).setContent(intent);
        tabHost.addTab(spec);

    }
    
	@Override
	void setExcludedMenu() {
		// TODO Auto-generated method stub
		this.setMenuExclusion(OLBaseTabActivity.RANKINGS_ID);
	}
}
