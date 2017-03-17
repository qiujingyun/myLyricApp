package com.wasu.launcher.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class SwitchUtils {

    public static boolean isFreeAuthSwitchOn(Context con) {
    	
    	String CONTENT_AUTHORITY = "com.wasu.live.provider";
	    Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
	    Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath("wasu_free_auth_switch_path").build();
	    String[] SWITCH_PROJECT = new String[] { "switch"};
	    Cursor cursor = null;
		boolean isFreeAuth = true;
	    try {
		    cursor = con.getContentResolver().query(CONTENT_URI, SWITCH_PROJECT, null, null, null);
		    if (cursor != null && cursor.moveToFirst()) {
			    isFreeAuth = cursor.getInt(cursor.getColumnIndex("switch"))==1?true:false;
		    }
        } catch (Exception e) {
			
		} finally {
			
			if (cursor != null) {
				cursor.close();
			}
		}
	    
	    return isFreeAuth;
    }
    
    

}
