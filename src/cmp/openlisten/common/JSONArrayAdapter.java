package cmp.openlisten.common;

import java.net.MalformedURLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cmp.openlisten.common.ImageThreadLoader.ImageLoadedListener;

import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class JSONArrayAdapter extends BaseAdapter {
	
	private JSONArray _mMyArray;
	private String[] _mFrom;
	private int[] _mTo;
	private int _mLayoutRow;
	private boolean _bImageFirst;
	
	private ImageThreadLoader imageLoader = null;

	public JSONArrayAdapter(JSONArray jso, int layoutRow, String[] from, int[] to, boolean bImage) {
		_mMyArray = jso;
		_mFrom = from;
		_mTo = to;
		_mLayoutRow = layoutRow;
		_bImageFirst = bImage;
		
		if (_bImageFirst) {
			imageLoader = new ImageThreadLoader();
		}
	}

	@Override
	public int getCount() {
		if (_mMyArray != null)
			return _mMyArray.length();
		else
			return 0;
	}

	@Override
	public Object getItem(int arg0) {

		try {
			return _mMyArray.get(arg0);
		} catch (JSONException e) {
			return null;
		}
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		try {
			return _mMyArray.getLong(position);
		} catch (JSONException e) {
			return -1;
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View vw = new View(parent.getContext());
		vw = View.inflate(parent.getContext(), _mLayoutRow, null);
		
		int len = _mFrom.length;
		int iStart = 0;
		int i;
		
		//Hack to support list rows where we're pulling in the facebook image. Convention is to list it first in the from / to arrays
		if (_bImageFirst) {
			
			final ImageView iv = ((ImageView)vw.findViewById(_mTo[0]));
			Bitmap cachedImage = null;
			
			try {
				iStart = 1;
				String uri = "http://graph.facebook.com/" + _mMyArray.getJSONObject(position).getString(_mFrom[0]) + "/picture?type=square";
				
				cachedImage = imageLoader.loadImage(uri, new ImageLoadedListener() {

					@Override
					public void imageLoaded(Bitmap imageBitmap) {
						// TODO Auto-generated method stub
						iv.setImageBitmap(imageBitmap);
						notifyDataSetChanged();
					}
					
				});
			} catch (JSONException e) {
				convertView = null;
			} catch (MalformedURLException e) {
				convertView = null;
			}
			
			if (cachedImage != null) {
				iv.setImageBitmap(cachedImage);
			}
		}
		
		for (i = iStart; i < len; i++) {
			try {
				if (_mFrom[i].contains("|")) {
					int iIndex = _mFrom[i].indexOf("|");
					String strObjectName = _mFrom[i].substring(0, iIndex);
					String strFieldName = _mFrom[i].substring(iIndex+1);
					
					JSONObject jo = new JSONObject(_mMyArray.getJSONObject(position).getString(strObjectName));
					((TextView)vw.findViewById(_mTo[i])).setText((jo.getString(strFieldName)));
				}
				else {
					((TextView)vw.findViewById(_mTo[i])).setText((_mMyArray.getJSONObject(position).getString(_mFrom[i])));
				}
			} catch (JSONException e) {
				convertView = null;
			}
		}
		
		return vw;
	}

}

