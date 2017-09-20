package com.assis.redondo.daniel.appdoikeda.utils.dispatcher;

import android.content.Context;

public class GlobalDispatcher extends EventDispatcher {

	private static GlobalDispatcher instance;
	static final Object sLock = new Object();
	private Context mCtx;

	
	public static GlobalDispatcher getInstance(Context ctx) {
		synchronized (sLock) {
			if (instance == null) {
				instance = new GlobalDispatcher(ctx.getApplicationContext());
			}
			return instance;
		}

	}
	public GlobalDispatcher(Context ctx) {
		mCtx = ctx;
	}
	
}
