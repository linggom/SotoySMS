package com.sotoy.spam.controller.sms;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.sotoy.spam.controller.CountVectorizer;
import com.sotoy.spam.controller.TextDetector;
import com.sotoy.spam.model.SMS;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by goman on 9/24/16.
 */

public class SMSController {

    private final TextDetector textDetector;
    private Context mContext;
    private List<SMS> mListSms;
    private int sms;

    public SMSController(Context context) {
        this.mContext = context;
        mListSms = new ArrayList<>();
        textDetector = new TextDetector();
        textDetector.setupTextDetector(context);
    }


    private List<SMS> loadSMSFromCursor() {
        Cursor cursor = mContext.getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);

        if (cursor.moveToFirst()) { // must check the result to prevent exception
            do {
                String message = cursor.getString(cursor.getColumnIndexOrThrow("body"));
                String sender = cursor.getString(2);
                boolean isSpam = textDetector.detectText(CountVectorizer.getInstance(mContext).transform(message)) == 0;
                Log.e("GOMAN", String.format("\"%s\",\"%s\"", message, sender));
                mListSms.add(new SMS(sender, message, isSpam));
            } while (cursor.moveToNext());
        }
        return mListSms;
    }


    public List<SMS> loadSMS() {
        if (mListSms.isEmpty()) return loadSMSFromCursor();
        return mListSms;
    }

    public SMS getSms(int position) {
        return mListSms.get(position);
    }
}
