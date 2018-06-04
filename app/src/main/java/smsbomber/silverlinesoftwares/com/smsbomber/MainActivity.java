package smsbomber.silverlinesoftwares.com.smsbomber;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 123;
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 0;
    private RadioButton radio1;
    private Button button;
    private ArrayList<Integer> simCardList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Sms Bomber");
        final EditText txt=(EditText)findViewById(R.id.text);
        final EditText txt2=(EditText)findViewById(R.id.notime);
        button=(Button)findViewById(R.id.send);
        radio1=(RadioButton)findViewById(R.id.sim1);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
            loadIMEI();
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txt.getText().toString().isEmpty()){
                    txt.setError("Please Enter Number");
                }
                else if(txt2.getText().toString().isEmpty()){
                    txt2.setError("Please Enter Number");
                }
                else{
                    button.setEnabled(false);
                    button.setText("Sending...");
                    sendSMS(txt.getText().toString(),txt2.getText().toString());
                }
            }
        });
    }

    public void loadIMEI() {
        // Check if the READ_PHONE_STATE permission is already available.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_PHONE_STATE)) {
//                get_imei_data();
                build();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE},
                        MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
            }
        } else {

            build();

            //TelephonyManager mngr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            //IMEI = mngr.getDeviceId();
            //device_unique_id = Settings.Secure.getString(this.getContentResolver(),
                   // Settings.Secure.ANDROID_ID);
            //textView.setText(device_unique_id+"----"+mngr.getDeviceId());
            // READ_PHONE_STATE permission is already been granted.
            Toast.makeText(this,"Alredy granted",Toast.LENGTH_SHORT).show();
        }
    }

    private void build() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
            simCardList = new ArrayList<>();
            SubscriptionManager subscriptionManager;
            subscriptionManager = SubscriptionManager.from(this);
            final List<SubscriptionInfo> subscriptionInfoList;
            subscriptionInfoList = subscriptionManager
                    .getActiveSubscriptionInfoList();
            for (SubscriptionInfo subscriptionInfo : subscriptionInfoList) {
                int subscriptionId = 0;
                subscriptionId = subscriptionInfo.getSubscriptionId();

                simCardList.add(subscriptionId);
            }
        }
    }


    public static String generateString() {
        String uuid = UUID.randomUUID().toString();
        return "uuid = " + uuid;
    }

    private void sendSMS(final String string,final String noti) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {
            } else {
                // permission is already granted
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        } else {
            int tim=Integer.parseInt(noti);
            for(int i=0;i<tim;i++) {

                final int k=i;

                Handler handler = new Handler();
                Runnable r=new Runnable() {
                    public void run() {
                        if (radio1.isChecked()) {
                            button.setText("Sent ... "+k+" SMS ");
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
//                                SimUtil.sendSMS(MainActivity.this, 0, string, null, generateString(), null, null);
                                if(simCardList==null){
                                    return;
                                }
                                ArrayList<String> arrayList=new ArrayList<>();
                                arrayList.add(generateString());
                                SmsManager.getSmsManagerForSubscriptionId(simCardList.get(0))
                                        .sendMultipartTextMessage(string, null, arrayList, null,null); //use your phone number, message and pending inte
                            }
                                else {
                                    SimUtil.sendSMS(MainActivity.this, 0, string, null, generateString(), null, null);
                                }


                        } else {
                            button.setText("Sent ... "+k+" SMS ");
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
//                                SimUtil.sendSMS(MainActivity.this, 0, string, null, generateString(), null, null);
                                if(simCardList==null){
                                    return;
                                }
                                ArrayList<String> arrayList=new ArrayList<>();
                                arrayList.add(generateString());
                                SmsManager.getSmsManagerForSubscriptionId(simCardList.get(1))
                                        .sendMultipartTextMessage(string, null,arrayList , null,null); //use your phone number, message and pending inte
                            }
                            else {
                                SimUtil.sendSMS(MainActivity.this, 1, string, null, generateString(), null, null);
                            }
                        }
                    }
                };
                handler.postDelayed(r, 1000);
            }
            button.setText("Send");
            button.setEnabled(true);
        }
    }
}
