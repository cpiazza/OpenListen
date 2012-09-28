package cmp.openlisten.common.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import josecgomez.com.android.webservices.json.WebService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Address;
import android.util.Log;
import cmp.openlisten.common.MusicTrack;
import cmp.openlisten.common.UserAccount;

public class OLServiceConnection {
	
	//private String urlBase = "http://10.0.2.2:51351/";
	private String urlBase = "http://svc.openlisten.com/";
	
	private String url = urlBase + "OLService.svc/request";
	private String APIKey = "ad8fc09a-c355-42b0-9f4a-6da082a39a80";
	
	public static final int OL_SERVICE_ERROR = 0;
	public static final int OL_SERVICE_SUCCESS = 1;
	
    public int CreateUserAccount(long FBID, String strFirstName, String strLastName, String strUserName) {
    	int iRet = -1;
    	
    	Map<String, String> params = new HashMap<String, String>();
    	params.put("FBID", String.valueOf(FBID) );
    	params.put("FirstName", strFirstName);
    	params.put("LastName", strLastName);
    	params.put("Username", strUserName);
    	
    	JSONObject jso = callService("CreateUserFromFBID", params);
    	
    	try {
    		if (jso.getInt("ResponseCode") == OL_SERVICE_SUCCESS) {
    			JSONObject jsoNewUser = new JSONObject(jso.getString("Extras"));
    			iRet = jsoNewUser.getInt("UserId");
    		} else {
    			iRet = -1;
    		}
    	} catch (Exception e) {
    		Log.d("OpenListen", e.toString());
    	}
    	
    	return iRet;
    }
    
    public void like(int id, int uid) {
    	Map<String, String> params = new HashMap<String, String>();
    	params.put("ObjectId", String.valueOf(id) );
    	params.put("UserId", String.valueOf(uid) );
    	params.put("ObjectTypeId", "1");
    	
    	callService("Like", params);
    }
    
    public void unlike(int id, int uid) {
    	Map<String, String> params = new HashMap<String, String>();
    	params.put("ObjectId", String.valueOf(id) );
    	params.put("UserId", String.valueOf(uid) );
    	params.put("ObjectTypeId", "1");
    	
    	callService("Unlike", params);
    }
    
    public JSONObject getLikeSummary(int id, int uid) {
    	Map<String, String> params = new HashMap<String, String>();
    	params.put("ObjectId", String.valueOf(id) );
    	params.put("UserId", String.valueOf(uid) );
    	params.put("ObjectTypeId", "1");
    	
    	return(callService("LikeSummary", params));
    }
    
    public JSONObject sendComment(int id, int uid, String strComment) {
    	Map<String, String> params = new HashMap<String, String>();
    	params.put("ObjectId", String.valueOf(id) );
    	params.put("Comment", strComment );
    	params.put("UserId", String.valueOf(uid) );
    	
    	return(callService("AddComment", params));
    }
    
    public JSONArray getTopArtists(String strWhich) {
    	Map<String, String> params = new HashMap<String, String>();
    	params.put("Scope", strWhich );

       	JSONObject jso = callService("TopArtists", params);
    	JSONArray jsoResponse = null;
    	
		try {
			jsoResponse = new JSONArray(jso.getString("Extras"));
		} catch (JSONException e) {
			jsoResponse = null;
		}
		
		return jsoResponse;    	
    }
    
    public JSONArray getUserRankDetails(int iOLID, int iRankId) {
    	Map<String, String> params = new HashMap<String, String>();
    	params.put("UserId", String.valueOf(iOLID) );
    	params.put("RankId", String.valueOf(iRankId) );

       	JSONObject jso = callService("UserRankDetails", params);
    	JSONArray jsoResponse = null;
    	
		try {
			jsoResponse = new JSONArray(jso.getString("Extras"));
		} catch (JSONException e) {
			jsoResponse = null;
		}
		
		return jsoResponse;
    }
    
    public JSONArray getUserRankSummary(int id) {
    	Map<String, String> params = new HashMap<String, String>();
    	params.put("UserId", String.valueOf(id) );

       	JSONObject jso = callService("UserRankSummary", params);
    	JSONArray jsoResponse = null;
    	
		try {
			jsoResponse = new JSONArray(jso.getString("Extras"));
		} catch (JSONException e) {
			jsoResponse = null;
		}
		
		return jsoResponse;    	
    }
    
    public JSONArray getRecommendationComments(int id) {
    	Map<String, String> params = new HashMap<String, String>();
    	params.put("ObjectId", String.valueOf(id) );

       	JSONObject jso = callService("GetComments", params);
    	JSONArray jsoResponse = null;
    	
		try {
			jsoResponse = new JSONArray(jso.getString("Extras"));
		} catch (JSONException e) {
			jsoResponse = null;
		}
		
		return jsoResponse;
    }
    
    public JSONArray getRecentRecommendations(int id) {
    	
    	Map<String, String> params = new HashMap<String, String>();
    	params.put("UserId", String.valueOf(id) );

    	
    	JSONObject jso = callService("RecentRecommendations", params);
    	JSONArray jsoResponse = null;
    	
		try {
			jsoResponse = new JSONArray(jso.getString("Extras"));
		} catch (JSONException e) {
			jsoResponse = null;
		}
		
		return jsoResponse;
   }
    
    public JSONArray getArtistFanClubMembers(int id) {
    	Map<String, String> params = new HashMap<String, String>();
    	params.put("ArtistId", String.valueOf(id) );

    	
    	JSONObject jso = callService("ArtistFanClubMembers", params);
    	JSONArray jsoResponse = null;
    	
		try {
			jsoResponse = new JSONArray(jso.getString("Extras"));
		} catch (JSONException e) {
			jsoResponse = null;
		}
		
		return jsoResponse;
    }
    
    public JSONArray getRecentListens(int id) {
    	
    	Map<String, String> params = new HashMap<String, String>();
    	params.put("UserId", String.valueOf(id) );

    	
    	JSONObject jso = callService("RecentListens", params);
    	JSONArray jsoResponse = null;
    	
		try {
			jsoResponse = new JSONArray(jso.getString("Extras"));
		} catch (JSONException e) {
			jsoResponse = null;
		}
		
		return jsoResponse;
   }    
    
    public JSONArray getRecentListeners() {
    	
    	JSONObject jso = callService("RecentListeners", null);
    	JSONArray jsoResponse = null;
    	
		try {
			jsoResponse = new JSONArray(jso.getString("Extras"));
		} catch (JSONException e) {
			jsoResponse = null;
		}
		
		return jsoResponse;
   }
	
    public int UpdateUsername(int iOLID, String strUsername, String strDateOfBirth, String strGender) {
    	int iRet = -1;
    	
    	Map<String, String> params = new HashMap<String, String>();
    	params.put("UserId", String.valueOf(iOLID));
    	params.put("Username", strUsername);
    	params.put("DateOfBirth", strDateOfBirth);
    	params.put("Gender", strGender);
    	
    	JSONObject jso = callService("UpdateUser", params);
    	
    	try {
    		if (jso.getInt("ResponseCode") == OL_SERVICE_SUCCESS) {
    			JSONObject jsoNewUser = new JSONObject(jso.getString("Extras"));
    			iRet = jsoNewUser.getInt("UserId");
    		} else {
    			iRet = -1;
    		}
    	} catch (Exception e) {
    		Log.d("OpenListen", e.toString());
    	}
    	
    	return iRet;
    }
    
    public int UploadRecommendation(int iOLID, MusicTrack track, Address a, String strFBPostID) {
    	int iRet = -1;
    	
    	Map<String, String> params = new HashMap<String, String>();
    	params.put("UserId", String.valueOf(iOLID));
    	params.put("TrackName", track.mTrackName);
    	params.put("ArtistName", track.mArtistName);
    	params.put("AlbumName", track.mAlbumName);
    	params.put("FBPostID", strFBPostID);
    	
    	if (a != null) {
    		params.put("Country", a.getCountryName());
    		params.put("State", a.getAdminArea());
    		params.put("City", a.getLocality());
    		params.put("Latitude", String.valueOf(a.getLatitude()));
    		params.put("Longitude", String.valueOf(a.getLongitude()));
    	}
    	
    	JSONObject jso = callService("Recommendation", params);
    	
    	try {
    		if (jso.getInt("ResponseCode") == OL_SERVICE_SUCCESS) {
    			JSONObject jsoNewUser = new JSONObject(jso.getString("Extras"));
    			iRet = jsoNewUser.getInt("UserId");
    		} else {
    			iRet = -1;
    		}
    	} catch (Exception e) {
    		Log.d("OpenListen", e.toString());
    	}
    	
    	
    	return iRet;
    }
    
    public UserAccount GetUserAccount(int iOLID) {
    	UserAccount ua = new UserAccount();
    	
    	Map<String, String> params = new HashMap<String, String>();
    	params.put("UserId", String.valueOf(iOLID));
    	
    	JSONObject jso = callService("GetUser", params);
    	
    	try {
    		if (jso.getInt("ResponseCode") == OL_SERVICE_SUCCESS) {
    			JSONObject jsoNewUser = new JSONObject(jso.getString("Extras"));
    			ua.UserAccountId = jsoNewUser.getInt("UserId");
    			ua.FBID = jsoNewUser.getLong("FBID");
    			ua.FirstName = jsoNewUser.getString("FirstName");
    			ua.LastName = jsoNewUser.getString("LastName");
    			ua.UserName = jsoNewUser.getString("Username");
    			ua.Gender = jsoNewUser.getString("Gender");

    			if (jsoNewUser.getString("DateOfBirth").startsWith("1/1/0001")) {
    				ua.DateOfBirth = null;
    			} else {
    				ua.DateOfBirth = new Date(jsoNewUser.getString("DateOfBirth"));	
    			}
    			
    			
    		} else {
    			ua = null;
    		}
    	} catch (Exception e) {
    		Log.d("OpenListen", e.toString());
    	}
    	
    	return ua;
    }
    
    public JSONObject UploadTrackPlay(int iOLID, MusicTrack track, Address a) {
    	JSONObject jsoRet = null;
    	
    	Map<String, String> params = new HashMap<String, String>();
    	params.put("UserId", String.valueOf(iOLID));
    	params.put("TrackName", track.mTrackName);
    	params.put("ArtistName", track.mArtistName);
    	params.put("AlbumName", track.mAlbumName);
    	
    	if (a != null) {
    		params.put("Country", a.getCountryName());
    		params.put("State", a.getAdminArea());
    		params.put("City", a.getLocality());
    		params.put("Latitude", String.valueOf(a.getLatitude()));
    		params.put("Longitude", String.valueOf(a.getLongitude()));
    	}
    	
    	JSONObject jso = callService("TrackPlay", params);
    	
    	try {
    		if (jso.getInt("ResponseCode") == OL_SERVICE_SUCCESS) {
    			jsoRet = new JSONObject(jso.getString("Extras"));
    			}
    		else {
    			jsoRet = null;
    		}
    	} catch (Exception e) {
    		Log.d("OpenListen", e.toString());
    	}
    	
    	return jsoRet;
    }
    
	private JSONObject callService(String strMethod, Map<String, String> extras) {
    	
    	JSONObject jso = new JSONObject();
    	JSONObject jsoResponse;
    	
    	try {
    		// Required for all calls to the OLService
			jso.put("AppName", "OpenListenFull");
			jso.put("APIKey", APIKey);
			jso.put("method", strMethod);
			
			// Method specific params provided by caller
			if ((extras != null) && (!extras.isEmpty())) {
			for (Map.Entry<String, String> e : extras.entrySet())
				jso.put(e.getKey(), e.getValue());
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	
    	WebService webService = new WebService(url);
    	
    	Map<String, String> params = new HashMap<String, String>();
    	params.put("vars", jso.toString());
    	
    	String response = null;
    	
    	try {
    		response = webService.webGet("", params);
    	} catch (Exception e) {
			try {
				jsoResponse = new JSONObject(); 
				jsoResponse.put("ResponseCode", OL_SERVICE_ERROR);
				jsoResponse.put("StatusMessage", e.getMessage());
				return jsoResponse;
			} catch (JSONException e1) {
				e1.printStackTrace();
				return null;
			}
    	}
    	
    	try {
    		
			JSONObject j2 = new JSONObject(response);
    		jsoResponse = new JSONObject(j2.getString("d"));
		} catch (JSONException e) {
			e.printStackTrace();
			
			jsoResponse = new JSONObject();
			
			try {
				jsoResponse.put("ResponseCode", OL_SERVICE_ERROR);
				jsoResponse.put("StatusMessage", "Failed to Serialize Response");
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			
		}

    	return jsoResponse;
    }
	
}

