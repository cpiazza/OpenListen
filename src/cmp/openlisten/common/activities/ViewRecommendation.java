package cmp.openlisten.common.activities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import cmp.openlisten.R;
import cmp.openlisten.common.JSONArrayAdapter;
import cmp.openlisten.common.OpenListenSettingsUtil;
import cmp.openlisten.common.service.OLServiceConnection;
import cmp.openlisten.facebook.OpenListenFBConnect;

public class ViewRecommendation extends ListActivity {
	
	private JSONArrayAdapter ja = null;
	private JSONObject joLikeSummary = null;
	private String _strUsername;
	private String _strFBPostId;
	private int _id;
	private Button _btnAddComment;
	private EditText _txtComment;
	private ImageView _likeButton;
	private TextView _likeText;
	private int _iOLID;
	private int _userLikeCount = -1;
	private OpenListenFBConnect fb;
	private Context _mctx;
	private TextView _tvTitle;
	private TextView _tvArtist;
	private TextView _tvWho;
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.view_recommendation);
        
        _mctx = this;
        
        _strUsername = getStringExtra(savedInstanceState, "Username");
        _strFBPostId = getStringExtra(savedInstanceState, "FBPostId");
        parseID(savedInstanceState);
        
        _likeButton = (ImageView)findViewById(R.id.likeButton);
        _likeButton.setImageResource(R.drawable.like_icon);
        _likeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				likeClick();
			}
        	
        });
        
        _likeText = (TextView)findViewById(R.id.likeText);
        _likeText.setText("Like");
        _likeText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				likeClick();
			}
        	
        });
        
        _tvTitle = (TextView)findViewById(R.id.RecommendedTitle);
        _tvArtist = (TextView)findViewById(R.id.RecommendedArtist);
        _tvWho = (TextView)findViewById(R.id.RecommendedWho);
        
        _tvTitle.setText(getStringExtra(savedInstanceState, "Track"));
        _tvArtist.setText("by " + getStringExtra(savedInstanceState, "Artist"));
        _tvWho.setText("Recommended by " + _strUsername);
        
        _btnAddComment = (Button)findViewById(R.id.btnAddComment);
    	_txtComment = (EditText)findViewById(R.id.editComment);
    	
    }
    
    private void likeClick() {
    	OLServiceConnection ols = new OLServiceConnection();
    	
    	if (_userLikeCount > 0) {
    		ols.unlike(_id, _iOLID);
    		_userLikeCount = 0;
    		
    	} else {
    		ols.like(_id, _iOLID);
    		
    		if (fb != null) {
    			fb.like(_strFBPostId);
    		}
    		
    		_userLikeCount = 1;
    	}
    	
    	setLikeButton();
    }
    
	private void checkFBSession() {
		
		OpenListenSettingsUtil os = new OpenListenSettingsUtil();
		_iOLID = os.getOLID(this);
		
		if (fb == null) {
			fb = new OpenListenFBConnect();
			fb.restoreSession(this);
		}
		
		if (!fb.isSessionValid() || (_iOLID < 0)) {
        	_txtComment.setHint("can't comment till you login!");
        	_btnAddComment.setText("Login!");
        	_btnAddComment.setOnClickListener(new OnClickListener() {

        		@Override
        		public void onClick(View v) {
			    	gotoSettings();        			
        		}
        	});
		}
		else {
        	_txtComment.setHint("have something to say?");
        	_btnAddComment.setOnClickListener(new OnClickListener() {

        		@Override
        		public void onClick(View v) {
        			addComment();
        		}
        	
        	});
		}
		
	}
    
    private void gotoSettings() {
    	Intent i = new Intent(this, OpenListenSettings.class);
    	startActivityForResult(i, 0);
    }
    
    private void addComment() {
    	
    	final ProgressDialog pd = ProgressDialog.show(this, "", "Saving Comment...", true);
    	
    	final class UpdateThread extends Thread {
			@Override
			public void run() {
				
		    	String strComment = _txtComment.getEditableText().toString();
		    	
		    	if ( (strComment != null) && (strComment.length() > 0) ) {
		    		
		    		OLServiceConnection ols = new OLServiceConnection();
		    		ols.sendComment(_id, _iOLID, strComment);
		    			
		    		if (_strFBPostId.length() > 2) {
		    			fb.addComment(_strFBPostId, strComment);
		    		}
		    		
		    	}

				handler.sendEmptyMessage(0);
			}
			
			private Handler handler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					pd.dismiss();
					
		   			Intent i = new Intent(_mctx, CommentSent.class);
		   			startActivity(i);    				
				}
			};
    	}
    	
		UpdateThread ut = new UpdateThread();
		ut.start();
    	
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	checkFBSession();
    	fillData();
    }
    
    private void setLikeButton() {
		if (_userLikeCount > 0) {
			_likeButton.setImageResource(R.drawable.unlike_icon);
			_likeText.setText("UnLike");
		} else {
			_likeButton.setImageResource(R.drawable.like_icon);
			_likeText.setText("Like");
		}
    }
    
    private void finishFillData() {
    	
    	if (joLikeSummary != null) {
    		try {
				JSONObject jso = new JSONObject(joLikeSummary.getString("Extras"));
				
				_userLikeCount = jso.getInt("UserLikeCount");
				setLikeButton();
				
			} catch (JSONException e) {
				e.printStackTrace();
			};
    	}
    	
    	setListAdapter(ja);
    }
    
    private void fillData() {
    	final ProgressDialog pd = ProgressDialog.show(this, "", "Loading Comments...", true);
    	
    	final class UpdateThread extends Thread {
			@Override
			public void run() {
		        fillDataRun();
				handler.sendEmptyMessage(0);
			}
			
			private Handler handler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					pd.dismiss();
					finishFillData();
				}
			};
    	}
    	
		UpdateThread ut = new UpdateThread();
		ut.start();
	
		}
    
    
    private void fillDataRun() {

    	JSONArray jsoRecentComments;
    	String[] from;
    	
    	OLServiceConnection ols = new OLServiceConnection();
    	joLikeSummary = ols.getLikeSummary(_id, _iOLID);
    	
    	if (_strFBPostId.length() < 2) {
    		ols = new OLServiceConnection();
    		jsoRecentComments  = ols.getRecommendationComments(_id);
    		
    		from = new String[]{"username", "Comment"};
    	}
    	else {
    		jsoRecentComments  = fb.getComments(_strFBPostId);
    		
    		from = new String[]{"from|name", "message"};
    	}
    	
		int[] to = new int[]{R.id.txtVRUsername, R.id.txtVRComment};
		
		ja = new JSONArrayAdapter(jsoRecentComments, R.layout.view_recommendation_row, from, to, false);
    }
    
    

	private void parseID(Bundle savedInstanceState) {
		
        _id = (int) (savedInstanceState != null ? savedInstanceState.getInt("id") : -1);
        
        if (_id == -1) {
        	Bundle extras = getIntent().getExtras();            
        	_id = (int) (extras != null ? extras.getInt("id") : -1);
        }
		
	}
	
	private String getStringExtra(Bundle savedInstanceState, String strWhich) {
        // Get the row that was clicked.
        String strRet = (savedInstanceState != null ? savedInstanceState.getString(strWhich) : "");
        
        if (strRet == "") {
        	Bundle extras = getIntent().getExtras();            
        	strRet = (extras != null ? extras.getString(strWhich) : "");
        }
        
        return strRet;

	}
    
}
