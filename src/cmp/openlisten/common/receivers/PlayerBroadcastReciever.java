package cmp.openlisten.common.receivers;

import cmp.openlisten.common.service.*;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PlayerBroadcastReciever extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		//Intent i = new Intent(context, OpenListenService.class);
        //i.setAction(intent.getAction());
		intent.setClass(context, OpenListenService.class);
        context.startService(intent);
	}

}
