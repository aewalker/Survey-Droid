package com.peoples.android.services;

import com.peoples.android.database.PeoplesDB;

import android.app.IntentService;
import android.content.Intent;
//import android.database.Cursor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CallLogService extends IntentService {

    public CallLogService() {
        super(CallLogService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        PeoplesDB pdb = new PeoplesDB(this);
        
        //FIXME remove when used
		SQLiteDatabase db = pdb.getWritableDatabase();
        
        
        
        Cursor d = this.getContentResolver().query(android.provider.CallLog.Calls.CONTENT_URI,
                null, null, null,
                android.provider.CallLog.Calls.DATE + " DESC");
        Cursor c = db.query(PeoplesDB.CALLLOG_TABLE_NAME, null, null, null, null, null, null);
        if (c.moveToLast() && d.moveToFirst()) {
            int ddateindex = d.getColumnIndex(android.provider.CallLog.Calls.DATE);
            int cdateindex = c.getColumnIndex(PeoplesDB.CallLogTable.TIME);
            
            int ddate = d.getInt(ddateindex);
            int cdate = c.getInt(cdateindex);
            
            if (ddate != cdate) {
                // do something
            }
        }
        
    }
    
}
