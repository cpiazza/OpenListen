package cmp.openlisten.common.db;

import cmp.openlisten.common.MusicTrack;
import android.content.Context;
import android.database.Cursor;

public final class OpenListenDBUtil {
	
	private static OpenListenDBAdapter mDbHelper;
    private static Cursor mNotesCursor;
    private static String mPlaylistFooter = "<br /><br />Playlist published by OpenListen for Android";
    public static int mPlaylistFooterLength = mPlaylistFooter.length();
    
	public OpenListenDBUtil(Context ctx) {
		// TODO Auto-generated constructor stub
		
	}
	
	public static void clearOldTracks(Context mCtx) {
        mDbHelper = new OpenListenDBAdapter(mCtx);
        mDbHelper.open();
        
        mDbHelper.deleteOldTracks();
        
        mDbHelper.close();
	}
	
	public static MusicTrack getTrackFromPlaylist(long iRowNum, Context mCtx) {
		int iTrackCol, iArtistCol, iAlbumCol;
		MusicTrack mt = new MusicTrack();
		
        mDbHelper = new OpenListenDBAdapter(mCtx);
        mDbHelper.open();
		
        mNotesCursor = mDbHelper.fetchTrack((long)iRowNum);
        
        iTrackCol = mNotesCursor.getColumnIndex(OpenListenDBAdapter.KEY_TRACK);
        iArtistCol = mNotesCursor.getColumnIndex(OpenListenDBAdapter.KEY_ARTIST);
        iAlbumCol = mNotesCursor.getColumnIndex(OpenListenDBAdapter.KEY_ALBUM);
        
        if (mNotesCursor.moveToFirst()) {
        	mt.mTrackName = mNotesCursor.getString(iTrackCol);
        	mt.mArtistName = mNotesCursor.getString(iArtistCol);
        	mt.mAlbumName = mNotesCursor.getString(iAlbumCol);
        }
        else {
        	mt = null;
        }
        
        mNotesCursor.close();
        mDbHelper.close();
        
		return mt;
	}
	
	public static String getFormattedPlaylistForPublishing(Context mCtx) {
		String strReturn = "";
		
    	boolean bContinue = false;
    	int iTrackCol, iArtistCol;
    	int i = 1;
    	
        mDbHelper = new OpenListenDBAdapter(mCtx);
        mDbHelper.open();
   	
        // Get all of the rows from the database and create the item list
        mNotesCursor = mDbHelper.fetchAllTracks();
        
        if (mNotesCursor.getCount() < 10)  {
        	// Don't publish is less than 10 tracks
        	strReturn = " ";
        } else {
        	
        	iTrackCol = mNotesCursor.getColumnIndex(OpenListenDBAdapter.KEY_TRACK);
        	iArtistCol = mNotesCursor.getColumnIndex(OpenListenDBAdapter.KEY_ARTIST);
        
        	bContinue = mNotesCursor.moveToFirst();
        
        	//String strPreamble = "%3ca href=%22httphttp%3A%2F%2Fwww.amazon.com/gp/search?ie=UTF8%26keywords=";
        	//String strPostamble = "%26tag=httpwwwtinpig-20%26index=digital-music%26linkCode=ur2%26camp=1789%26creative=9325%22%3e";
        
        	while (bContinue) {
        		strReturn += "<strong>" + String.valueOf(i) + ". " + mNotesCursor.getString(iTrackCol) + "</strong>" + " by " + mNotesCursor.getString(iArtistCol) + "<br />";
        		//mStrPlaylist += "<strong>" + strPreamble +  mNotesCursor.getString(iTrackCol).replace(" ", "%20") + strPostamble + mNotesCursor.getString(iTrackCol) + "</a></strong> by " + mNotesCursor.getString(iArtistCol) + "<br />";
        		i++;
        		bContinue = mNotesCursor.moveToNext();
        	}
        
        	strReturn = strReturn.replaceAll("&", "and");
        	strReturn += mPlaylistFooter;
        }
        
        mNotesCursor.close();
        mDbHelper.close();
		return strReturn;
	}
	
	public static void clearPlaylist(Context mCtx) {
        mDbHelper = new OpenListenDBAdapter(mCtx);
        mDbHelper.open();
        mDbHelper.clearTracks();
        mDbHelper.close();
        
	}
	

}
