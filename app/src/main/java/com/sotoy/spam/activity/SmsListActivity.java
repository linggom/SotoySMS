package com.sotoy.spam.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sotoy.spam.R;
import com.sotoy.spam.controller.sms.SMSController;
import com.sotoy.spam.model.SMS;

/**
 * Created by goman on 9/24/16.
 */

public class SmsListActivity extends AppCompatActivity {

    private SMSController mController;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyler_sms);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);



        mController = new SMSController(this);
        checkSMSPermission();
        SmsAdapter adapter = new SmsAdapter(mController);
        mRecyclerView.setAdapter(adapter);
    }



    private void checkSMSPermission() {
        if(ContextCompat.checkSelfPermission(getBaseContext(), "android.permission.READ_SMS") == PackageManager.PERMISSION_GRANTED) {
            mController.loadSMS();
        } else {
            final int REQUEST_CODE_ASK_PERMISSIONS = 123;
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_SMS"}, REQUEST_CODE_ASK_PERMISSIONS);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("GOMAN", "RESULT");
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView sender;
        public TextView preview;
        public TextView is_spam;
        public View layout;

        public ViewHolder(View v) {
            super(v);
            sender = (TextView) v.findViewById(R.id.sender);
            preview = (TextView) v.findViewById(R.id.preview);
            is_spam = (TextView) v.findViewById(R.id.is_spam);
            layout = v.findViewById(R.id.layout);
        }
    }

    class SmsAdapter extends RecyclerView.Adapter<ViewHolder> {

        SMSController mController;

        public SmsAdapter(SMSController controller) {
            this.mController = controller;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // create a new view
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_sms, parent, false);
                ViewHolder vh = new ViewHolder(v);
                return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            SMS sms = mController.getSms(position);
            holder.sender.setText(sms.getSender());
            holder.preview.setText(sms.getMessage());
            holder.is_spam.setText("Is Spam = " + sms.isSpam());
            if (sms.isSpam()) holder.layout.setBackgroundColor(Color.RED);
            else holder.layout.setBackgroundColor(Color.WHITE);
        }

        @Override
        public int getItemCount() {
            return mController.loadSMS().size();
        }
    }


}
