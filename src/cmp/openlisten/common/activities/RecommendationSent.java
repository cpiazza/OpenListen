package cmp.openlisten.common.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import cmp.openlisten.R;

public class RecommendationSent extends OLBaseActivity {
	
	ImageView _btnWhosListening;
	ImageView _btnViewPlaylist;
	
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated constructor stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recommendation_sent);
        
        _btnViewPlaylist = (ImageView)findViewById(R.id.btnViewPlaylist);
        _btnViewPlaylist.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				_btnViewPlaylist.setVisibility(View.INVISIBLE);
				viewPlaylist();
			}
        	
        });
        
        _btnWhosListening = (ImageView) findViewById(R.id.btnWhosListening);
        _btnWhosListening.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				_btnWhosListening.setVisibility(View.INVISIBLE);
				viewWhosListening();
			}        	
        });
        
        Button b = (Button) findViewById(R.id.btnsendAnotherRecommendation);
        b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				viewPlaylist();
			}
        	
        });
	}
	
    @Override
    public void onResume() {
    	super.onResume();
    	_btnWhosListening.setVisibility(View.VISIBLE);
    	_btnViewPlaylist.setVisibility(View.VISIBLE);
    }
    
    @Override
    public void onRestart() {
    	super.onRestart();
    	_btnWhosListening.setVisibility(View.VISIBLE);
    	_btnViewPlaylist.setVisibility(View.VISIBLE);
    }
	
    private void viewWhosListening() {
    	Intent i = new Intent(this, WhosListening.class);
    	startActivity(i);    	
    }  
	
    private void viewPlaylist() {
        
    	Intent i = new Intent(this, ViewPlaylist.class);
    	startActivity(i);
    }

}
