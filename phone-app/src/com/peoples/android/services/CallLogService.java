package com.peoples.android.services;

import java.util.ArrayList;

import com.peoples.android.PeoplesConfig;
import com.peoples.android.database.PeoplesDB;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent; //import android.database.Cursor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CallLogService extends IntentService {

    public CallLogService() {
        super(CallLogService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // This code checks to see whether call logging is enabled in the PEOPLES
        // control panel. Ideally this should be checked in CoordinatorService
        PeoplesConfig config = new PeoplesConfig(getApplicationContext());
        if(!config.isCallLogEnabled()) {
            return;
        }
        
        PeoplesDB pdb = new PeoplesDB(this);
        SQLiteDatabase db = pdb.getWritableDatabase();

        // This cursor scans through the Android system call log, starting with
        // the most recent call
        Cursor sysCur = this.getContentResolver().query(
                android.provider.CallLog.Calls.CONTENT_URI, null, null, null,
                android.provider.CallLog.Calls.DATE + " DESC");
        // This cursor scans through the PEOPLES database on the phone
        Cursor pplsCur = db.query(PeoplesDB.CALLLOG_TABLE_NAME, null, null,
                null, null, null, null);

        // This ArrayList will store the new calls, if any
        ArrayList<ContentValues> newCalls;

        // If there are calls both in the system call log and the database, only
        // get the new calls
        if (pplsCur.moveToLast() && sysCur.moveToFirst()) {
            newCalls = getNewCalls(pplsCur, sysCur);
        }
        // If either the system or database call log is empty, get all the calls
        else {
            newCalls = getAllCalls(sysCur);
        }

        db.beginTransaction();
        try {
            // The calls were added to newCalls in reverse order, so we need to
            // fix that
            for (int i = newCalls.size() - 1; i >= 0; i--) {
                ContentValues call = newCalls.get(i);
                db.insert(PeoplesDB.CALLLOG_TABLE_NAME, null, call);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        // Close everything
        sysCur.close();
        pplsCur.close();
        db.close();
        pdb.close();
    }

    private ArrayList<ContentValues> getAllCalls(Cursor sysCur) {
        // This ArrayList will store the new calls, if any
        ArrayList<ContentValues> newCalls = new ArrayList<ContentValues>();

        // Retrieve the column-indices of phoneNumber, date and calltype
        int phoneIndex = sysCur
                .getColumnIndex(android.provider.CallLog.Calls.NUMBER);
        int dateIndex = sysCur
                .getColumnIndex(android.provider.CallLog.Calls.DATE);
        int typeIndex = sysCur
                .getColumnIndex(android.provider.CallLog.Calls.TYPE);

        do {
            String callerPhoneNumber = sysCur.getString(phoneIndex);
            int callDate = sysCur.getInt(dateIndex);
            int callType = sysCur.getInt(typeIndex);
            String stringCallType;

            ContentValues call = new ContentValues();

            stringCallType = PeoplesDB.CallLogTable.getCallTypeString(callType);

            call.put(PeoplesDB.CallLogTable.PHONE_NUMBER, callerPhoneNumber);
            call.put(PeoplesDB.CallLogTable.CALL_TYPE, stringCallType);
            call.put(PeoplesDB.CallLogTable.TIME, callDate);

            newCalls.add(call);

        } while (sysCur.moveToNext());

        return newCalls;
    }

    private ArrayList<ContentValues> getNewCalls(Cursor pplsCur, Cursor sysCur) {
        // This ArrayList will store the new calls, if any
        ArrayList<ContentValues> newCalls = new ArrayList<ContentValues>();

        // Get the system column indices
        int sysDateIndex = sysCur
                .getColumnIndex(android.provider.CallLog.Calls.DATE);
        int sysPhoneIndex = sysCur
                .getColumnIndex(android.provider.CallLog.Calls.NUMBER);
        int sysTypeIndex = sysCur
                .getColumnIndex(android.provider.CallLog.Calls.TYPE);

        // Get the PEOPLES database column index for the date
        int pplsDateIndex = pplsCur.getColumnIndex(PeoplesDB.CallLogTable.TIME);

        String sysDate = sysCur.getString(sysDateIndex);
        String pplsDate = pplsCur.getString(pplsDateIndex);

        // only add calls that don't exist in the database already
        while (sysDate.compareTo(pplsDate) > 0) {
            sysDate = sysCur.getString(sysDateIndex);
            String callerPhoneNumber = sysCur.getString(sysPhoneIndex);
            int callType = sysCur.getInt(sysTypeIndex);
            String stringCallType;

            ContentValues call = new ContentValues();

            stringCallType = PeoplesDB.CallLogTable.getCallTypeString(callType);

            call.put(PeoplesDB.CallLogTable.PHONE_NUMBER, callerPhoneNumber);
            call.put(PeoplesDB.CallLogTable.CALL_TYPE, stringCallType);
            call.put(PeoplesDB.CallLogTable.TIME, sysDate);

            newCalls.add(call);

            if (!sysCur.moveToNext())
                break;
        }

        return newCalls;
    }

}
