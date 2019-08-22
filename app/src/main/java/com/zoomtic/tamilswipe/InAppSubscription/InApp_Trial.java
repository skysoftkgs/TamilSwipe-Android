package com.zoomtic.tamilswipe.InAppSubscription;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.zoomtic.tamilswipe.Accounts.Enable_location_F;
import com.zoomtic.tamilswipe.CodeClasses.Variables;
import com.zoomtic.tamilswipe.Main_Menu.MainMenuActivity;
import com.zoomtic.tamilswipe.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class InApp_Trial extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    Button continue_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature( Window.FEATURE_NO_TITLE );
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN );

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences=getSharedPreferences(Variables.pref_name,MODE_PRIVATE);

        String savedDay = sharedPref.getString(Variables.trial_last_day,"");
        int trialPastDays = sharedPref.getInt(Variables.trial_past_days,0);
//        trialPastDays = 14;
        if (savedDay.isEmpty() && trialPastDays == 0){
            setContentView(R.layout.activity_trial_a);
            TextView learnMoreTextView = findViewById(R.id.textView_learnMore);
            learnMoreTextView.setMovementMethod(LinkMovementMethod.getInstance());
            continue_btn=this.findViewById(R.id.trail_continue_btn);
            continue_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ContinueApp();
                }
            });

        }else{
            if(trialPastDays < 14){
                setContentView(R.layout.activity_trial_b);
                TextView daysTextView = findViewById(R.id.textView_trial_days);
                daysTextView.setText(String.valueOf(14 - trialPastDays) + " DAY'S");
                continue_btn=this.findViewById(R.id.trail_continue_btn);
                continue_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ContinueApp();
                    }
                });
            }else{
                setContentView(R.layout.activity_trial_c);
                TextView learnMoreTextView = findViewById(R.id.textView_learnMore);
                learnMoreTextView.setMovementMethod(LinkMovementMethod.getInstance());
                continue_btn=this.findViewById(R.id.trail_continue_btn);
                continue_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        open_subscription_view();
                    }
                });
            }
        }

        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy/MM/dd");
        String strToday = sdfDate.format(new Date());
        if(!savedDay.isEmpty() && !savedDay.equals(strToday))
            trialPastDays++;

        SharedPreferences.Editor editor=sharedPref.edit();
        editor.putInt(Variables.trial_past_days, trialPastDays);
        editor.putString(Variables.trial_last_day, strToday);
        editor.commit();

//        ///trail counter
//        trail_counter = this.findViewById(R.id.trail_counter);
//        trail_start_free = this.findViewById(R.id.trail_start_free);
//        trail_now = this.findViewById(R.id.trail_now);
//        Date cur_date = Calendar.getInstance().getTime();
//        String created_date_str = sharedPreferences.getString(Variables.created_time,null);
//        Date created_date = cur_date;
//
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
//        try {
//            created_date = format.parse(created_date_str);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        int isSigned = (int)( (cur_date.getTime() - created_date.getTime()));
//        Log.d("signed",String.valueOf(cur_date.getTime()));
//        Log.d("signed",String.valueOf(created_date.getTime()));
//        Log.d("signed",String.valueOf(isSigned));
//
//        continue_btn=this.findViewById(R.id.trail_continue_btn);
//        int remain_days = (14 - (int)( (cur_date.getTime() - created_date.getTime()) / (1000 * 60 * 60 * 24)));
//        if(remain_days > 0){
//
//            Log.d("tttt",String.valueOf(isSigned));
//            if(isSigned<50000){
//                String trail_counter_str = "START YOUR 14 DAY";
//                trail_counter.setText(trail_counter_str);
//                continue_btn.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        ContinueApp();
//                    }
//                });
//            }else{
//                trail_start_free.setVisibility(View.INVISIBLE);
//                trail_counter.setText("YOUR TRAIL FREE");
//                trail_counter.setTextSize(40);
//                trail_counter.setPadding(0,-15,0,0);
//                String trail_counter_str = "END'S IN " + remain_days + " DAY'S";
//                trail_now.setText(trail_counter_str);
//                continue_btn.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        ContinueApp();
//                    }
//                });
//            }
//        }else{
//            trail_start_free.setBackgroundResource(0);
//            trail_start_free.setText("YOUR TRAIL FREE");
//            trail_start_free.setTextColor(Color.BLACK);
//            trail_start_free.setTextSize(40);
//            trail_start_free.setPadding(0,80,0,0);
////            String trail_counter_str = "YOUR TRAIL FREE";
//            trail_counter.setText("ENDEND");
//            trail_now.setText("Just $4.99 a month");
//            trail_now.setTextSize(40);
//            continue_btn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    startActivity(new Intent(InApp_trail.this, InApp_Subscription_A.class));
//
//                }
//            });
//        }

    }

    public void open_subscription_view(){

        InApp_Subscription_A inApp_subscription_a = new InApp_Subscription_A();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.MainLayout, inApp_subscription_a)
                .addToBackStack(null)
                .commit();

    }

    public void ContinueApp(){
        if(getIntent().hasExtra("action_type")){
            Intent intent= new Intent(InApp_Trial.this, MainMenuActivity.class);
            String action_type=getIntent().getExtras().getString("action_type");
            String receiverid=getIntent().getExtras().getString("senderid");
            String title=getIntent().getExtras().getString("title");
            String icon=getIntent().getExtras().getString("icon");

            intent.putExtra("icon",icon);
            intent.putExtra("action_type",action_type);
            intent.putExtra("receiverid",receiverid);
            intent.putExtra("title",title);


            startActivity(intent);
            finish();
        }
        else
            GPSStatus();

    }

    public void GPSStatus(){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(!GpsStatus)
        {
            // if gps is not on then we will go to the setting screen to on the gps
            Toast.makeText(this, "On Location in High Accuracy", Toast.LENGTH_SHORT).show();
            startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),2);

        }else {

            // if on then get the location of the user and save the location into the local database

            GetCurrentlocation();
        }
    }

    private FusedLocationProviderClient mFusedLocationClient;
    private void GetCurrentlocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // here if user did not give the permission of location then we move user to enable location screen
            enable_location();
            return;
        }

        // else we will get the location and save it in local and move to main Screen
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {

                            // if we successfully get the location of the user then we will save the locatio into
                            //locally and go to the Main view
                            SharedPreferences.Editor editor=sharedPreferences.edit();
                            editor.putString(Variables.current_Lat,""+location.getLatitude());
                            editor.putString(Variables.current_Lon,""+location.getLongitude());
                            editor.commit();
                            startActivity(new Intent(InApp_Trial.this, MainMenuActivity.class));
                            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                            finish();
                        }
                        else  {
                            // else we will use the basic location

                            if(sharedPreferences.getString(Variables.current_Lat,"").equals("") || sharedPreferences.getString(Variables.current_Lon,"").equals("") ){
                                SharedPreferences.Editor editor=sharedPreferences.edit();
                                editor.putString(Variables.current_Lat,"33.738045");
                                editor.putString(Variables.current_Lon,"73.084488");
                                editor.commit();
                            }
                            startActivity(new Intent(InApp_Trial.this, MainMenuActivity.class));
                            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                            finish();
                        }
                    }
                });
    }


    // if user does not permitt the app to get the location then we will go to the enable location screen to enable the location permission
    private void enable_location() {
        Enable_location_F enable_location_f = new Enable_location_F();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left,R.anim.in_from_left,R.anim.out_to_right);
        getSupportFragmentManager().popBackStackImmediate();
        transaction.replace(R.id.Login_A, enable_location_f).addToBackStack(null).commit();

    }


}
