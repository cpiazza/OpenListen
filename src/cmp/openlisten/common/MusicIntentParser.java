package cmp.openlisten.common;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;

public class MusicIntentParser {
	public MusicTrack parseIntent(Context ctx, String action, Bundle bundle) 
			throws IllegalArgumentException {
		MusicTrack mt = new MusicTrack();
		
		if (bundle == null) {
			throw new IllegalArgumentException("bundle is empty!");
		}
		
		// Check to see if the ID was passed from the local music store
		long audioid = -1;
		Object idBundle = bundle.get("id");
		if (idBundle != null) {
			if (idBundle instanceof Long)
				audioid = (Long) idBundle;
			else if (idBundle instanceof Integer)
				audioid = (Integer) idBundle;
		}
		
		if (audioid != -1) { // read from MediaStore
			final String[] columns = new String[] {
					MediaStore.Audio.AudioColumns.ARTIST,
					MediaStore.Audio.AudioColumns.TITLE,
					MediaStore.Audio.AudioColumns.DURATION,
					MediaStore.Audio.AudioColumns.ALBUM,
					MediaStore.Audio.AudioColumns.TRACK, };

			Cursor cur = ctx.getContentResolver().query(
					ContentUris.withAppendedId(
							MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
							audioid), columns, null, null, null);

			if (cur == null) {
				throw new IllegalArgumentException(
						"could not open cursor to media in media store");
			}

			try {
				if (!cur.moveToFirst()) {
					throw new IllegalArgumentException(
							"no such media in media store");
				}
				String artist = cur.getString(cur
						.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST));
				mt.mArtistName = artist;

				String track = cur.getString(cur
						.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE));
				mt.mTrackName = track;

				String album = cur.getString(cur
						.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM));
				mt.mAlbumName = album;

			} finally {
				cur.close();
			}

		} else {
			CharSequence ar = bundle.getCharSequence("artist");
			CharSequence al = bundle.getCharSequence("album");
			CharSequence tr = bundle.getCharSequence("track");
			if (ar == null || al == null || tr == null) {
				throw new IllegalArgumentException("null track values");
			}

			mt.mArtistName = ar.toString();
			mt.mAlbumName = al.toString();
			mt.mTrackName = tr.toString();
		}
		
		return mt;
	}
}
