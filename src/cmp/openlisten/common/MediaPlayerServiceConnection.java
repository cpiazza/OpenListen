package cmp.openlisten.common;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;


import com.android.music.IMediaPlaybackService;

public class MediaPlayerServiceConnection implements ServiceConnection {

	public IMediaPlaybackService mService = null;
	public com.htc.music.IMediaPlaybackService htcService = null;
	public com.google.android.music.IMediaPlaybackService gService = null;
	public com.mixzing.basic.IMediaPlaybackService mxService = null;
	public com.rb.mplayer.IPlaybackService rbService = null;
	
	public void onServiceConnected(ComponentName name, IBinder service) {
			if (name.getClassName().contains(".htc")) {
					htcService = (com.htc.music.IMediaPlaybackService) com.htc.music.IMediaPlaybackService.Stub.asInterface(service);
			}
			else if (name.getClassName().contains(".google")) {
				gService = (com.google.android.music.IMediaPlaybackService) com.google.android.music.IMediaPlaybackService.Stub.asInterface(service);
			}
			else if (name.getClassName().contains(".mixzing")) {
				mxService = (com.mixzing.basic.IMediaPlaybackService) com.google.android.music.IMediaPlaybackService.Stub.asInterface(service);
			}
			else if (name.getClassName().contains(".mplayer")) {
				rbService = (com.rb.mplayer.IPlaybackService) com.rb.mplayer.IPlaybackService.Stub.asInterface(service);
			}			
			else {
				mService = IMediaPlaybackService.Stub.asInterface(service);
			}
	}
	
	public void onServiceDisconnected (ComponentName name) {
		mService = null;
		htcService = null;
		gService = null;
		mxService = null;
		rbService = null;		
	}
	
	public boolean isConnected() {
		if ( (mService == null) && (htcService == null) && (gService == null) && (mxService == null)  && (rbService == null))
			return false;
		else
			return true;
	}
	
	public boolean isPlaying() {
		
		if (mService != null) {
			try {
				return (mService == null) ? false : mService.isPlaying();
			} catch (Exception e) {
				Log.e(this.toString(), e.toString());
				return false;
			}
		} else if (gService != null) {
			try {
				return (gService == null) ? false : gService.isPlaying();
			} catch (Exception e) {
				Log.e(this.toString(), e.toString());
				return false;
			}
		} else if (mxService != null) {
			try {
				return (mxService == null) ? false : mxService.isPlaying();
			} catch (Exception e) {
				Log.e(this.toString(), e.toString());
				return false;
			}
		}  else if (rbService != null) {
			try {
				return (rbService == null) ? false : rbService.isPlaying();
			} catch (Exception e) {
				Log.e(this.toString(), e.toString());
				return false;
			}
		}
		else {
			try {
				return (htcService == null) ? false : htcService.isPlaying();
			} catch (Exception e) {
				Log.e(this.toString(), e.toString());
				return false;			
			}
		}
	}
	
	public MusicTrack getCurrentMusicTrack() {
		MusicTrack track = new MusicTrack();
		
		track.mTrackName = getCurrentTrackName();
		track.mArtistName = getCurrentArtistName();
		track.mAlbumName = getCurrentAlbumName();
		
		return track;
	}
	
	public String getCurrentTrackName() {
		
		String strRet;
		
		if (mService != null) {
			try {
				strRet = mService.getTrackName();
			} catch (Exception e) {
				Log.e(this.toString(), e.toString());
				strRet = null;
			}
		} else if (gService != null) {
			try {
				strRet = gService.getTrackName();
			} catch (Exception e) {
				Log.e(this.toString(), e.toString());
				strRet = null;
			}
		} else if (mxService != null) {
			try {
				strRet = mxService.getTrackName();
			} catch (Exception e) {
				Log.e(this.toString(), e.toString());
				strRet = null;
			}
		} else if (rbService != null) {
			try {
				strRet = rbService.getTrackName();
			} catch (Exception e) {
				Log.e(this.toString(), e.toString());
				strRet = null;
			}
		}
		else {
			try {
				strRet = htcService.getTrackName();
			} catch (Exception e) {
				Log.e(this.toString(), e.toString());
				strRet = null;
			}
		}
			
		return (strRet);
	}
	
	public String getCurrentArtistName() {
		
		String strRet;
		
		if (mService != null) {
			try {
				strRet = mService.getArtistName();
			} catch (Exception e) {
				Log.e(this.toString(), e.toString());
				strRet = null;
			}
		} else if (gService != null) {
			try {
				strRet = gService.getArtistName();
			} catch (Exception e) {
				Log.e(this.toString(), e.toString());
				strRet = null;
			}
		} else if (mxService != null) {
			try {
				strRet = mxService.getArtistName();
			} catch (Exception e) {
				Log.e(this.toString(), e.toString());
				strRet = null;
			}
		} else if (rbService != null) {
			try {
				strRet = rbService.getArtistName();
			} catch (Exception e) {
				Log.e(this.toString(), e.toString());
				strRet = null;
			}
		}
		else {
			try {
				strRet = htcService.getArtistName();
			} catch (Exception e) {
				Log.e(this.toString(), e.toString());
				strRet = null;
			}
		}
		
		return (strRet);
	}

	public String getCurrentAlbumName() {
		
		String strRet;
		
		if (mService != null) {
			try {
				strRet = mService.getAlbumName();
			} catch (Exception e) {
				Log.e(this.toString(), e.toString());
				strRet = null;
			}
		} else if (gService != null) {
			try {
				strRet = gService.getAlbumName();
			} catch (Exception e) {
				Log.e(this.toString(), e.toString());
				strRet = null;
			}
		} else if (mxService != null) {
			try {
				strRet = mxService.getAlbumName();
			} catch (Exception e) {
				Log.e(this.toString(), e.toString());
				strRet = null;
			}
		}
		else if (rbService != null) {
			try {
				strRet = rbService.getAlbumName();
			} catch (Exception e) {
				Log.e(this.toString(), e.toString());
				strRet = null;
			}
		}		
		else {
			try {
				strRet = htcService.getAlbumName();
			} catch (Exception e) {
				Log.e(this.toString(), e.toString());
				strRet = null;
			}
		}
		
		return (strRet);
	}	

}
