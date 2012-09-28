package cmp.openlisten.common.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import cmp.openlisten.R;
import cmp.openlisten.common.db.OpenListenDBAdapter;

public class ViewPlaylist extends OLBaseListActivity {
	private static final int ACTIVITY_SEND_PLAYLIST = 0;
	private static final int ACTIVITY_RECOMMEND 	= 1;
	
	private static final int HOME_ID 			= Menu.FIRST;
	private static final int RANKINGS_ID		= Menu.FIRST + 1;
	private static final int WHOS_LISTENING_ID 	= Menu.FIRST + 2;
	private static final int SEND_PLAYLIST_ID 	= Menu.FIRST + 3;
	
	private ListView _mListView;
	
    private OpenListenDBAdapter mDbHelper;
    private Cursor mNotesCursor;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_playlist);
        
        TextView tv = new TextView(this);
        tv.setText("Select track to make recommendation");
        tv.setTextColor(R.drawable.black);
        tv.setTextSize(20);
        
        _mListView = (ListView) findViewById(android.R.id.list);
        _mListView.addHeaderView(tv);
        _mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				trackClick(arg3);
			}
        	
        });
        
        mDbHelper = new OpenListenDBAdapter(this);
        mDbHelper.open();
        mDbHelper.deleteOldTracks();
        fillData();
        mDbHelper.close();
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	_mListView = (ListView) findViewById(android.R.id.list);
    	
    	if (_mListView.getCount() == 0) {
    		mDbHelper = new OpenListenDBAdapter(this);
    		mDbHelper.open();
    		fillData();
    		mDbHelper.close();
    	}
    }
    
    @Override
    protected void onRestart() {
    	super.onRestart();
		mDbHelper = new OpenListenDBAdapter(this);
		mDbHelper.open();
		fillData();
		mDbHelper.close();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, HOME_ID,0, R.string.home);
        menu.add(0, RANKINGS_ID, 0, R.string.rankings);
     	menu.add(0, WHOS_LISTENING_ID, 0, R.string.whos_listening);
        menu.add(0, SEND_PLAYLIST_ID,0, R.string.send_playlist);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
        case HOME_ID:
        	goHome();
        	return true;
        case RANKINGS_ID:
        	viewMyRankings();
        	return true;
        case WHOS_LISTENING_ID:
        	viewWhosListening();
        	return true;
        case SEND_PLAYLIST_ID:
            sendPlaylist();
            return true;
        }
        
        return super.onMenuItemSelected(featureId, item);
    }

    private void viewWhosListening() {
    	Intent i = new Intent(this, WhosListening.class);
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
    
    private void trackClick(long iWhichOne) {
    	Intent i = new Intent(this, Recommend.class);
    	i.putExtra("whichTrack", iWhichOne);
    	i.putExtra("FromPlaylist", 1);
    	startActivityForResult(i, ACTIVITY_RECOMMEND);	
    }
    
    private void sendPlaylist() {
    	Intent i = new Intent(this, SendPlaylist.class);
    	startActivityForResult(i, ACTIVITY_SEND_PLAYLIST);
    }
    
    private void fillData(){
        // Get all of the rows from the database and create the item list
        mNotesCursor = mDbHelper.fetchAllTracks();
        startManagingCursor(mNotesCursor);
        
        // Create an array to specify the fields we want to display in the list
        String[] from = new String[]{OpenListenDBAdapter.KEY_ARTIST, OpenListenDBAdapter.KEY_TRACK, OpenListenDBAdapter.KEY_CITY};
        
        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[]{R.id.text1, R.id.text2, R.id.text3};
        
        // Now create a simple cursor adapter and set it to display
        SimpleCursorAdapter tracks = 
        	    new SimpleCursorAdapter(this, R.layout.track_row, mNotesCursor, from, to);
        
        setListAdapter(tracks);
    }
    
}
