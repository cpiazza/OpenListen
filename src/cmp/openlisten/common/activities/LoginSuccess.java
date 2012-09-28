package cmp.openlisten.common.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import cmp.openlisten.R;

public class LoginSuccess extends OLBaseActivity {
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated constructor stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_success);
        
        Button b = (Button) findViewById(R.id.btnReturnToSettings);
        b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				viewSettings();
			}
        	
        });
	}
	
    private void viewSettings() {
        
    	Intent i = new Intent(this, OpenListenSettings.class);
    	startActivity(i);
    }
}
