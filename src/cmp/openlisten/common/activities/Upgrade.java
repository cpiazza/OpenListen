package cmp.openlisten.common.activities;

import cmp.openlisten.R;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Upgrade extends Activity {
	
	private Button _btnGo;
	private TextView _txt2;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.upgrade);
        
		_txt2 = (TextView) findViewById(R.id.txtWhyUpgrade2);
		_txt2.setText("Tired of the Ads? Upgrade to the Ad-Free OpenListen Plus!");
        
        _btnGo = (Button) findViewById (R.id.btnIWantIt);
        _btnGo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=cmp.openlisten.full")); 
				startActivity(i);
			}
        	
        });
    }
}

