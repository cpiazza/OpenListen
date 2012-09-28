package cmp.openlisten.common.db;


import java.util.Date;

import cmp.openlisten.common.MusicTrack;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Address;
import android.util.Log;

public class OpenListenDBAdapter {

	public static final String KEY_ROWID = "_id";
	public static final String KEY_DATETIME = "playdate";
    public static final String KEY_TRACK = "track";
    public static final String KEY_ALBUM = "album";
    public static final String KEY_ARTIST = "artist";
    public static final String KEY_CITY = "city";
    public static final String KEY_STATE = "state";
    public static final String KEY_COUNTRY = "country";
    public static final String KEY_FEATURE = "feature";
    public static final String KEY_LON = "lon";
    public static final String KEY_LAT = "lat";
    	

    private static final String TAG = "OpenListenDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    
    /**
     * Database creation sql statement
     */
    private static final String DATABASE_CREATE =
            "create table OLPlaylist (_id integer primary key autoincrement, " +
                    "playdate long not null, " +
                    "track text not null, " +
                    "album text null, " +
                    "artist text null, " +
                    "city text null, " +
                    "state text null, " +
                    "country text null, " +
                    "feature text null, " +
                    "lon double null, " +
                    "lat double null" +
                    ");";

    private static final String DATABASE_NAME = "OLPlaylist";
    private static final String DATABASE_TABLE = "OLPlaylist";
    private static final int DATABASE_VERSION = 1;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public OpenListenDBAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the notes database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public OpenListenDBAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }
    
    public void close() {
        mDbHelper.close();
    }


    /**
     * Create a new note using the title and body provided. If the note is
     * successfully created return the new rowId for that note, otherwise return
     * a -1 to indicate failure.
     * 
     * @param title the title of the note
     * @param body the body of the note
     * @return rowId or -1 if failed
     */
    public long insertTrack(MusicTrack track, Address a) {
    	Date d = new Date();
    	
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_DATETIME, d.getTime());
        initialValues.put(KEY_TRACK, track.mTrackName);
        initialValues.put(KEY_ALBUM, track.mAlbumName);
        initialValues.put(KEY_ARTIST, track.mArtistName);
        
        if (a != null) {
        	initialValues.put(KEY_CITY, a.getLocality() );
        	initialValues.put(KEY_STATE, a.getAdminArea());
        	initialValues.put(KEY_COUNTRY, a.getCountryName());
        	initialValues.put(KEY_FEATURE, a.getFeatureName());
        	initialValues.put(KEY_LON, a.getLongitude());
        	initialValues.put(KEY_LAT, a.getLatitude());
        }
        
        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    /**
     * Delete the note with the given rowId
     * 
     * @param rowId id of note to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteTrack(long rowId) {

        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }
    
    public void deleteOldTracks() {
    	Date d = new Date();
    	mDb.delete(DATABASE_TABLE, KEY_DATETIME + "<=" + String.valueOf(((long)(d.getTime() - (86400000 * 5)))) , null);
    	return;
    }

    /**
     * Return a Cursor over the list of all notes in the database
     * 
     * @return Cursor over all notes
     */
    public Cursor fetchAllTracks() {

        return mDb.query(DATABASE_TABLE, new String[] {
        		KEY_ROWID, 
        		KEY_DATETIME,
        	    KEY_TRACK,
        	    KEY_ALBUM,
        	    KEY_ARTIST,
        	    KEY_CITY,
        	    KEY_STATE,
        	    KEY_COUNTRY,
        	    KEY_FEATURE,
        	    KEY_LON,
        	    KEY_LAT}, null, null, null, null, null);
    }

    /**
     * Return a Cursor positioned at the note that matches the given rowId
     * 
     * @param rowId id of note to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if note could not be found/retrieved
     */
    public Cursor fetchTrack(long rowId) throws SQLException {

        Cursor mCursor =

                mDb.query(true, DATABASE_TABLE, new String[] {
                		KEY_ROWID,
                		KEY_DATETIME,
                	    KEY_TRACK,
                	    KEY_ALBUM,
                	    KEY_ARTIST,
                	    KEY_CITY,
                	    KEY_STATE,
                	    KEY_COUNTRY,
                	    KEY_FEATURE,
                	    KEY_LON,
                	    KEY_LAT}, KEY_ROWID + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    /**
     * Update the note using the details provided. The note to be updated is
     * specified using the rowId, and it is altered to use the title and body
     * values passed in
     * 
     * @param rowId id of note to update
     * @param title value to set note title to
     * @param body value to set note body to
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateTrack(long rowId, MusicTrack track, Address a) {
        ContentValues initialValues = new ContentValues();
        
        Date d = new Date();
        
        initialValues.put(KEY_DATETIME, d.getTime());
        initialValues.put(KEY_TRACK, track.mTrackName);
        initialValues.put(KEY_ALBUM, track.mAlbumName);
        initialValues.put(KEY_ARTIST, track.mArtistName);
        initialValues.put(KEY_CITY, a.getLocality() );
        initialValues.put(KEY_STATE, a.getAdminArea());
        initialValues.put(KEY_COUNTRY, a.getCountryName());
        initialValues.put(KEY_FEATURE, a.getFeatureName());
        initialValues.put(KEY_LON, a.getLongitude());
        initialValues.put(KEY_LAT, a.getLatitude());
        
        return mDb.update(DATABASE_TABLE, initialValues, KEY_ROWID + "=" + rowId, null) > 0;
    }
    
    public boolean clearTracks() {
    	mDb.delete(DATABASE_TABLE, "1", null);
    	return true;
    }
    
}
