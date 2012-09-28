package cmp.openlisten.common;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

public class OpenListenGeoUtil {
	
	// Singleton to keep track of last address
	private static OpenListenGeoUtil instance = null;
	private static Address _mLastAddress = null;
	
	private OpenListenGeoUtil() {
		
	}
	
	public static OpenListenGeoUtil getInstance() {
		if (instance == null) {
			instance = new OpenListenGeoUtil();
		}
		
		return instance;
	}
	
	public Address getAddress(Context ctx) {
		Address a = null;
		Location l = null;
		Geocoder gc = null;
		List<Address> la = null;

			try {
				// Let's find out where we are...
					LocationManager lm = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
					Criteria c = new Criteria();
					String strBest = lm.getBestProvider(c, true);

					l = lm.getLastKnownLocation(strBest);
			
			} catch (Exception e) {
				Log.e(e.toString(),"OpenListen Service: Failed to get location");
			}

			gc = new Geocoder(ctx);

			if (l != null) {
				try {
					la = gc.getFromLocation(l.getLatitude(), l.getLongitude(), 5);
					
					if (la.size() > 0) {
						a = la.get(0);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.e(e.toString(),"OpenListen Service: Failed to get Address");
				}
			}
			/**
			else {
				a = new Address(null);
				a.setCountryName("United States");
				a.setCountryCode("US");
				a.setLatitude(47.12);
				a.setLongitude(12.34);
				a.setLocality("Newark");
				a.setAdminArea("New Jersey");
			}
			**/
			
		if (a == null)
			a = _mLastAddress;
		else
			_mLastAddress = a;

		return a;
	}
}
