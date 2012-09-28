package cmp.openlisten.common;

import android.content.Context;
import android.content.SharedPreferences;

public class OpenListenSettingsUtil {
	
	private static final String PREFS_NAME = "OpenListenPrefsFile";
	private static final String DAILY_PREF = "dailyPublishEnabled";
	private static final String RANK_PREF = "rankPublishEnabled";	
	private static final String LAST_PUBLISHED_PREF = "lastPublishDay";
	private static final String CLEAR_PLAYLIST_PREF = "clearPlaylist";
	private static final String FB_ID = "fbID";
	private static final String OL_ID = "OLID";
	private static final String USERNAME = "OLUsername";
	private static final String SHOW_ADS = "ShowAds";
	private static final String LAST_ARTIST = "LastArtist";
	private static final String LAST_TRACK = "LastTrack";
	private static final String LAST_ALBUM = "LastAlbum";
	private static final String DEBUG_STRING = "DebugString";
	
    private static SharedPreferences settings;
    private static SharedPreferences.Editor editor;
    private static int bShowAds = -1;
	
	public OpenListenSettingsUtil() {
		// TODO Auto-generated constructor stub
	}
	
	private static void setSettings(Context ctx) {
		settings = ctx.getSharedPreferences(PREFS_NAME, 0);
	}
	
	private static void setEditor(Context ctx) {
		setSettings(ctx);
		editor = settings.edit();	
	}
	
	public boolean showAds(Context ctx) {
		if (bShowAds == -1) {
			setSettings(ctx);
			bShowAds = (settings.getBoolean(SHOW_ADS, true) == true) ? 1 : 0;
		}
		
		return ( (bShowAds == 1) ? true : false);
	}
	
	public void setShowAds(Context ctx, boolean bSet) {
		setEditor(ctx);
		bShowAds = (bSet) ? 1 : 0;
		editor.putBoolean(SHOW_ADS, bSet);
		editor.commit();
	}
	
	public String getOLUsername(Context ctx) {
		setSettings(ctx);
        return(settings.getString(USERNAME, ""));
	}
	
	public void setOLUsername(Context ctx, String strUsername) {
		setEditor(ctx);
		editor.putString(USERNAME, strUsername);
		editor.commit();
	}
	
	public String getDebugString(Context ctx) {
		setSettings(ctx);
        return(settings.getString(DEBUG_STRING, ""));
	}
	
	public void setDebugString(Context ctx, String strDebugString) {
		setEditor(ctx);
		editor.putString(DEBUG_STRING, strDebugString);
		editor.commit();
	}	
	
	public String getLastArtist(Context ctx) {
		setSettings(ctx);
        return(settings.getString(LAST_ARTIST, ""));
	}
	
	public void setLastArtist(Context ctx, String strArtist) {
		setEditor(ctx);
		editor.putString(LAST_ARTIST, strArtist);
		editor.commit();
	}
	
	public String getLastTrack(Context ctx) {
		setSettings(ctx);
        return(settings.getString(LAST_TRACK, ""));
	}
	
	public void setLastTrack(Context ctx, String strTrack) {
		setEditor(ctx);
		editor.putString(LAST_TRACK, strTrack);
		editor.commit();
	}	
	
	public String getLastAlbum(Context ctx) {
		setSettings(ctx);
        return(settings.getString(LAST_ALBUM, ""));
	}
	
	public void setLastAlbum(Context ctx, String strAlbum) {
		setEditor(ctx);
		editor.putString(LAST_ALBUM, strAlbum);
		editor.commit();
	}	
	
	
	
	public String getFbID(Context ctx) {
		setSettings(ctx);
        return(settings.getString(FB_ID, null));
	}
	
	public void setFbID(Context ctx, String strID) {
		setEditor(ctx);
		editor.putString(FB_ID, strID);
		editor.commit();
	}
	
	public boolean getDailyPublishPreference(Context ctx) {
		setSettings(ctx);
        return(settings.getBoolean(DAILY_PREF, true));
	}
	
	public void setDailyPublishPreference(Context ctx, boolean bValue) {
		setEditor(ctx);
		editor.putBoolean(DAILY_PREF, bValue);
		editor.commit();
	}
	
	public boolean getRankPublishPreference(Context ctx) {
		setSettings(ctx);
        return(settings.getBoolean(RANK_PREF, true));
	}
	
	public void setRankPublishPreference(Context ctx, boolean bValue) {
		setEditor(ctx);
		editor.putBoolean(RANK_PREF, bValue);
		editor.commit();
	}	
	
	
	public int getLastDayPublished(Context ctx) {
		setSettings(ctx);
		return (settings.getInt(LAST_PUBLISHED_PREF, 0));
	}
	
	public void setLastDayPublished(Context ctx, int i) {
		setEditor(ctx);
		editor.putInt(LAST_PUBLISHED_PREF, i);
		editor.commit();
	}
	
	public int getOLID(Context ctx) {
		setSettings(ctx);
		return (settings.getInt(OL_ID, -1));
	}
	
	public void setOLID(Context ctx, int i) {
		setEditor(ctx);
		editor.putInt(OL_ID, i);
		editor.commit();		
	}
	
	public boolean getClearPlaylistPreference(Context ctx) {
		setSettings(ctx);
		return(settings.getBoolean(CLEAR_PLAYLIST_PREF, true));
	}
	
	public void setClearPlaylistPreference(Context ctx, boolean bValue) {
		setEditor(ctx);
		editor.putBoolean(CLEAR_PLAYLIST_PREF, bValue);
		editor.commit();
	}

}
