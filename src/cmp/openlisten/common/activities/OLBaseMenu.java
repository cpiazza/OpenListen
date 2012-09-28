package cmp.openlisten.common.activities;

import cmp.openlisten.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;



public abstract class OLBaseMenu extends Activity {
	protected static final int HOME_ID 				= Menu.FIRST;
	protected static final int RANKINGS_ID 			= Menu.FIRST + 1;
	protected static final int WHOS_LISTENING_ID 	= Menu.FIRST + 2;
	protected static final int PLAYLIST_ID			= Menu.FIRST + 3;
	protected static final int LOGOUT_ID			= Menu.FIRST + 4;	
	
	public int _mExcludedMenu = -1;
	
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setExcludedMenu();
    }

    abstract void setExcludedMenu();
    
    protected void setMenuExclusion(int iWhichOne) {
    	_mExcludedMenu = iWhichOne;
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
	
   

}
