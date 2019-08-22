package com.zoomtic.tamilswipe.Settings;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.github.guilhe.views.SeekBarRangedView;
import com.gmail.samehadar.iosdialog.IOSDialog;
import com.ybs.countrypicker.Country;
import com.zoomtic.tamilswipe.Accounts.Login_A;
import com.zoomtic.tamilswipe.CodeClasses.Variables;
import com.zoomtic.tamilswipe.GoogleMap.MapsActivity;
import com.zoomtic.tamilswipe.Main_Menu.MainMenuActivity;
import com.zoomtic.tamilswipe.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */



// All the setting values will be used when we will get the Nearby user then we pass
// that setting value as a parameter to the api


public class Setting_F extends Fragment {


    SwitchCompat current_loction_switch,selected_location_switch;
    SwitchCompat men_switch,women_switch,show_me_on_binder;
    LinearLayout showMeLayout;
    ImageButton showMeImageButton;

    SeekBarRangedView age_seekbar;
    TextView age_range_txt,distance_txt;
    TextView privacy_policy_txt;
    View view;
    Context context;
    Button back_btn, done_btn;

    ArrayList<String> alreadySelectedCountries;
    List<Country> allCountries;
    LinearLayout logout_btn,delete_account_btn;
    boolean isCountryDetail = false;

    IOSDialog loadingDialog;

    ExpandableRelativeLayout expandable_layout;

    TextView new_location_txt;

    ImageView radioMenImageView, radioWomenImageView, radioEveryoneImageView;
    RelativeLayout radioMenLayout, radioWomenLayout, radioEveryoneLayout;
    //    ImageButton countryDetailImageButton;
    ListView countryListView;
    CountryListAdapter countryListAdapter;
    int ageMinValue, ageMaxValue;
    boolean isShowMe;
    ImageView countryMoreImageView;

    public Setting_F() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_setting, container, false);
        context=getContext();

        loadingDialog = new IOSDialog.Builder(context)
                .setCancelable(false)
                .setSpinnerClockwise(false)
                .setMessageContentGravity(Gravity.END)
                .build();

        allCountries = Country.getAllCountries();
        alreadySelectedCountries = new ArrayList<>();

        String country_from = MainMenuActivity.sharedPreferences.getString(Variables.country_from,"");
        if(country_from.length() > 0) {
            String[] country_list = country_from.split("_");
            for (int i = 0; i < country_list.length; i++) {
                //            String[] each = country_list[i].split(":");
                alreadySelectedCountries.add(country_list[i]);
            }
        }

        countryListView = view.findViewById(R.id.listview_countries);
        final LinearLayout countryDetaillayout = view.findViewById(R.id.layout_country_detail);

        final ImageButton allCountriesImageButton = view.findViewById(R.id.imageButton_all_countries);

        if(alreadySelectedCountries.size() == allCountries.size()){
            allCountriesImageButton.setImageResource(R.drawable.item_selected);
        }else{
            allCountriesImageButton.setImageResource(R.drawable.item_unselected);
        }

        allCountriesImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(alreadySelectedCountries.size() == allCountries.size()){
                    allCountriesImageButton.setImageResource(R.drawable.item_unselected);
                    alreadySelectedCountries.clear();
                    countryListAdapter.notifyDataSetChanged();

                }else {
                    allCountriesImageButton.setImageResource(R.drawable.item_selected);
                    alreadySelectedCountries.clear();
                    for(int i=0;i<allCountries.size();i++) {
                        alreadySelectedCountries.add(allCountries.get(i).getCode());
                        countryListAdapter.notifyDataSetChanged();
                    }

                }
            }
        });

        countryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Country country = allCountries.get(position);
                if(alreadySelectedCountries.contains(country.getCode())){
                    alreadySelectedCountries.remove(country.getCode());
                    countryListAdapter.notifyDataSetChanged();
                }else{
                    alreadySelectedCountries.add(country.getCode());
                    countryListAdapter.notifyDataSetChanged();
                }

                if(alreadySelectedCountries.size() == allCountries.size()){
                    allCountriesImageButton.setImageResource(R.drawable.item_selected);
                }else{
                    allCountriesImageButton.setImageResource(R.drawable.item_unselected);
                }
            }
        });

        countryListAdapter = new CountryListAdapter(getActivity(), alreadySelectedCountries, allCountries);
        countryListView.setAdapter(countryListAdapter);
        setCountryListViewHeight();
        countryDetaillayout.setVisibility(View.GONE);

        LinearLayout countryLayout = view.findViewById(R.id.layout_countries);
        countryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isCountryDetail == false){
                    countryDetaillayout.setVisibility(View.VISIBLE);
                    countryMoreImageView.setBackground(getResources().getDrawable(R.drawable.country_detail_up));
                    isCountryDetail = true;

                }else{
                    countryDetaillayout.setVisibility(View.GONE);
                    countryMoreImageView.setBackground(getResources().getDrawable(R.drawable.country_detail));
                    isCountryDetail = false;
                }
            }
        });

        countryMoreImageView = view.findViewById(R.id.country_more);
        back_btn=view.findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        done_btn=view.findViewById(R.id.done_btn);
        done_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = "";
                for(int i=0;i<alreadySelectedCountries.size();i++){
                    str += alreadySelectedCountries.get(i) + "_";
                }
                MainMenuActivity.sharedPreferences.edit().putString(Variables.country_from, str).commit();
                MainMenuActivity.sharedPreferences.edit().putInt(Variables.max_age,ageMaxValue).commit();
                MainMenuActivity.sharedPreferences.edit().putInt(Variables.min_age,ageMinValue).commit();
                MainMenuActivity.sharedPreferences.edit().putBoolean(Variables.show_me_on_binder,isShowMe).commit();
                Variables.is_reload_users = true;

                if (isShowMe){
                    Call_Api_For_show_on_binder_or_not("0");
                }else{
                    Call_Api_For_show_on_binder_or_not("1");
                }
                getActivity().onBackPressed();
            }
        });


        expandable_layout=view.findViewById(R.id.expandable_layout);
        view.findViewById(R.id.one_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(expandable_layout.isExpanded()){

                    expandable_layout.collapse();
                }
                else
                    expandable_layout.expand();

            }
        });


        current_loction_switch=view.findViewById(R.id.current_loction_switch);
        selected_location_switch=view.findViewById(R.id.selected_location_switch);

        if(MainMenuActivity.sharedPreferences.getBoolean(Variables.is_seleted_location_selected,false)){
            selected_location_switch.setChecked(true);
        }else {
            current_loction_switch.setChecked(true);
        }



        current_loction_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                Variables.is_reload_users=true;

                if(isChecked){
                    MainMenuActivity.sharedPreferences.edit().putBoolean(Variables.is_seleted_location_selected,false).commit();
                    selected_location_switch.setChecked(false);
                }else {

                    MainMenuActivity.sharedPreferences.edit().putBoolean(Variables.is_seleted_location_selected,true).commit();
                    selected_location_switch.setChecked(true);
                }

            }
        });

        selected_location_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                Variables.is_reload_users=true;

                if(isChecked){
                    MainMenuActivity.sharedPreferences.edit().putBoolean(Variables.is_seleted_location_selected,true).commit();
                    current_loction_switch.setChecked(false);
                }else {
                    MainMenuActivity.sharedPreferences.edit().putBoolean(Variables.is_seleted_location_selected,false).commit();
                    current_loction_switch.setChecked(true);
                }


            }
        });


        new_location_txt=view.findViewById(R.id.new_location_txt);
        new_location_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Openlocation_picker();

            }
        });


        if(!MainMenuActivity.sharedPreferences.getString(Variables.selected_location_string,"").equals("")){
            new_location_txt.setText(MainMenuActivity.sharedPreferences.getString(Variables.selected_location_string,""));
        }else {
            selected_location_switch.setClickable(false);
            current_loction_switch.setClickable(false);
        }


        radioMenLayout = view.findViewById(R.id.gender_men_layout);
        radioWomenLayout = view.findViewById(R.id.gender_women_layout);
        radioEveryoneLayout = view.findViewById(R.id.gender_everyone_layout);

        radioMenImageView = view.findViewById(R.id.gender_men);
        radioWomenImageView = view.findViewById(R.id.gender_women);
        radioEveryoneImageView = view.findViewById(R.id.gender_everyone);
        men_switch=view.findViewById(R.id.men_switch);
        women_switch=view.findViewById(R.id.women_switch);

        if(MainMenuActivity.sharedPreferences.getString(Variables.show_me,"all").equals("all")){
            showEveryoneOption();
        }
        else if(MainMenuActivity.sharedPreferences.getString(Variables.show_me,"all").equals("male")){
            showMenOption();
        }
        else {
            showWomenOption();
        }


        radioMenLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Variables.is_reload_users =true;
                MainMenuActivity.sharedPreferences.edit().putString(Variables.show_me,"male").commit();
                showMenOption();
            }
        });

        radioWomenLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Variables.is_reload_users =true;
                MainMenuActivity.sharedPreferences.edit().putString(Variables.show_me,"female").commit();
                showWomenOption();
            }
        });

        radioEveryoneLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Variables.is_reload_users =true;
                MainMenuActivity.sharedPreferences.edit().putString(Variables.show_me,"all").commit();
                showEveryoneOption();
            }
        });

        distance_txt=view.findViewById(R.id.distance_txt);
        distance_txt.setText(MainMenuActivity.sharedPreferences.getInt(Variables.max_distance,Variables.default_distance)+" miles");

        // this is the age seek bar. when we change the progress of seek bar it will be locally save

        age_range_txt=view.findViewById(R.id.age_range_txt);

        age_seekbar=view.findViewById(R.id.range_age_seekbar);
        ageMinValue = MainMenuActivity.sharedPreferences.getInt(Variables.min_age,18);
        ageMaxValue = MainMenuActivity.sharedPreferences.getInt(Variables.max_age,75);
        age_seekbar.setSelectedMinValue(ageMinValue);
        age_seekbar.setSelectedMaxValue(ageMaxValue);
        age_range_txt.setText("Age Between " +
                String.valueOf(ageMinValue)+" and " +
                String.valueOf(ageMaxValue));

        age_seekbar.setOnSeekBarRangedChangeListener(new SeekBarRangedView.OnSeekBarRangedChangeListener() {
            @Override
            public void onChanged(SeekBarRangedView view, float minValue, float maxValue) {
                ageMinValue = (int)minValue;
                ageMaxValue = (int)maxValue;
            }

            @Override
            public void onChanging(SeekBarRangedView view, float minValue, float maxValue) {
                age_range_txt.setText("Age Between " +
                        String.valueOf((int)minValue)+" and " +
                        String.valueOf((int)maxValue));
            }
        });

        showMeLayout = view.findViewById(R.id.layout_show_me);
        showMeImageButton = view.findViewById(R.id.imageButton_show_me);
        isShowMe = MainMenuActivity.sharedPreferences.getBoolean(Variables.show_me_on_binder,true);
        if(isShowMe){
            showMeImageButton.setImageResource(R.drawable.item_selected);
        }else{
            showMeImageButton.setImageResource(R.drawable.item_unselected);
        }
        showMeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isShowMe = !isShowMe;
                if(isShowMe){
                    showMeImageButton.setImageResource(R.drawable.item_selected);
                }else{
                    showMeImageButton.setImageResource(R.drawable.item_unselected);
                }
                Variables.is_reload_users =true;
            }
        });
//        show_me_on_binder=view.findViewById(R.id.show_me_on_binder);
//        show_me_on_binder.setChecked(MainMenuActivity.sharedPreferences.getBoolean(Variables.show_me_on_binder,true));
//        show_me_on_binder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                MainMenuActivity.sharedPreferences.edit().putBoolean(Variables.show_me_on_binder,isChecked).commit();
//                Variables.is_reload_users =true;
//                if(isChecked){
//                    Call_Api_For_show_on_binder_or_not("0");
//                }else {
//                    Call_Api_For_show_on_binder_or_not("1");
//                }
//            }
//        });


        privacy_policy_txt=view.findViewById(R.id.privacy_policy_txt);
        privacy_policy_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://google.com"));
                startActivity(browserIntent);

            }
        });


        // on logout button click it will delete the user session and go to the login screen
        logout_btn=view.findViewById(R.id.logout_btn);
        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on press logout btn we will chat the local value and move the user to login screen
                Logout_User();
            }
        });



        delete_account_btn=view.findViewById(R.id.delete_account_btn);
        delete_account_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Show_delete_account_alert();
            }
        });


        return view;

    }



    // below two methods are related with "save the value in database" that he is want to show
    // as a public or keep it private

    public void setCountryListViewHeight(){
        if (countryListAdapter != null) {
            int totalHeight = 0, itemHeight;
            View listItem = countryListAdapter.getView(0, null, countryListView);
            listItem.measure(0, 0);
            itemHeight = listItem.getMeasuredHeight();
            totalHeight = itemHeight * countryListAdapter.getCount();
//            for (int i = 0; i < countryListAdapter.getCount(); i++) {
//                View listItem = countryListAdapter.getView(i, null, countryListView);
//                listItem.measure(0, 0);
//                totalHeight += listItem.getMeasuredHeight();
////                totalHeight += 50;
//            }
            ViewGroup.LayoutParams params = countryListView.getLayoutParams();
            params.height = totalHeight + (countryListView.getDividerHeight() * (countryListAdapter.getCount() - 1));
            countryListView.setLayoutParams(params);
            countryListView.requestLayout();
        }
    }

    public void showMenOption(){
        radioMenImageView.setVisibility(view.VISIBLE);
        radioWomenImageView.setVisibility(view.INVISIBLE);
        radioEveryoneImageView.setVisibility(View.INVISIBLE);
    }

    public void showWomenOption(){
        radioMenImageView.setVisibility(view.INVISIBLE);
        radioWomenImageView.setVisibility(view.VISIBLE);
        radioEveryoneImageView.setVisibility(View.INVISIBLE);
    }

    public void showEveryoneOption(){
        radioMenImageView.setVisibility(view.INVISIBLE);
        radioWomenImageView.setVisibility(view.INVISIBLE);
        radioEveryoneImageView.setVisibility(View.VISIBLE);
    }

    private void Call_Api_For_show_on_binder_or_not(String status) {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("fb_id", MainMenuActivity.user_id);
            parameters.put("status",status);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestQueue rq = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Variables.show_or_hide_profile, parameters, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        String respo=response.toString();

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.d("respo",error.toString());
                    }
                });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.getCache().clear();
        rq.add(jsonObjectRequest);
    }


    public void Show_delete_account_alert(){
//        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_delete_account, null);

//        AlertDialog.Builder alert=new AlertDialog.Builder(context);
//        alert.setTitle("Are you Sure?")
//                .setMessage("Delete an Account will Erase all of your data Completely")
//                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Call_api_to_Delete_Account();
//                    }
//                })
//                .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.dismiss();
//                    }
//                });
//
//        alert.setCancelable(true);
//        alert.show();

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_delete_account);
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button bt_yes = dialog.findViewById(R.id.yesbtn);
        Button bt_no = dialog.findViewById(R.id.nobtn);

        bt_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Call_api_to_Delete_Account();
            }
        });
        bt_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }



    // below two method is used get the user pictures and about text from our server
    private void Call_api_to_Delete_Account() {
        loadingDialog.show();

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("fb_id", MainMenuActivity.user_id);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestQueue rq = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Variables.deleteAccount, parameters, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loadingDialog.cancel();
                        String respo=response.toString();
                        Log.d("responce",respo);

                        try {
                            JSONObject jsonObject=new JSONObject(respo);
                            String code=jsonObject.optString("code");
                            if(code.equals("200")){

                                Logout_User();

                            }
                        } catch (JSONException e) {

                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.d("respo",error.toString());
                        loadingDialog.cancel();
                    }
                });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.getCache().clear();
        rq.add(jsonObjectRequest);
    }




    public  void Logout_User(){
        SharedPreferences.Editor editor=MainMenuActivity.sharedPreferences.edit();

        editor.putBoolean(Variables.islogin,false);
        editor.putString(Variables.f_name,"").clear();
        editor.putString(Variables.l_name,"").clear();
        editor.putString(Variables.birth_day,"").clear();
        editor.putString(Variables.country_code,"").clear();
        editor.putString(Variables.country_name,"").clear();
        editor.putString(Variables.u_pic,"").clear();
        editor.putString(Variables.created_time,"").clear();

        editor.commit();
        startActivity(new Intent(getActivity(), Login_A.class));
        getActivity().overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
        getActivity().finish();
    }


    public void Openlocation_picker(){

        startActivityForResult(new Intent(getContext(), MapsActivity.class),111);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111) {
            if(resultCode == RESULT_OK) {
                String latSearch = data.getStringExtra("lat");
                String longSearch = data.getStringExtra("lng");

                String location_string = data.getStringExtra("location_string");


                new_location_txt.setText(location_string);
                selected_location_switch.setClickable(true);
                current_loction_switch.setClickable(true);

                MainMenuActivity.sharedPreferences.edit().putString(Variables.seleted_Lat,latSearch).commit();
                MainMenuActivity.sharedPreferences.edit().putString(Variables.selected_Lon,longSearch).commit();

                MainMenuActivity.sharedPreferences.edit().putString(Variables.selected_location_string,location_string).commit();

            }
        }
    }

}
