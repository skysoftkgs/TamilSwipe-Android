package com.zoomtic.tamilswipe.Profile;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.zoomtic.tamilswipe.Accounts.Login_A;
import com.zoomtic.tamilswipe.CodeClasses.Variables;
import com.zoomtic.tamilswipe.Main_Menu.MainMenuActivity;
import com.zoomtic.tamilswipe.Main_Menu.RelateToFragment_OnBack.RootFragment;
import com.zoomtic.tamilswipe.Profile.EditProfile.EditProfile_F;
import com.zoomtic.tamilswipe.Profile.Profile_Details.Profile_Details_F;
import com.zoomtic.tamilswipe.R;

import com.squareup.picasso.Picasso;
import com.zoomtic.tamilswipe.Settings.Setting_F;

/**
 * A simple {@link Fragment} subclass.
 */
public class Profile_F extends RootFragment {

    View view;
    Context context;

     public static ImageView profile_image;
      TextView user_name;
      public static TextView age;


    Button setting_layout,edit_profile_layout,logout_btn;



    public Profile_F() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_profile, container, false);
        context=getContext();


        edit_profile_layout=view.findViewById(R.id.edit_profile_layout);
        edit_profile_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Editprofile();
            }
        });

        logout_btn=view.findViewById(R.id.logout_btn);
        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor=MainMenuActivity.sharedPreferences.edit();

                editor.putBoolean(Variables.islogin,false);
                editor.putString(Variables.f_name,"").clear();
                editor.putString(Variables.l_name,"").clear();
                editor.putString(Variables.birth_day,"").clear();
                editor.putString(Variables.country_name,"").clear();
                editor.putString(Variables.country_code,"").clear();
                editor.putString(Variables.u_pic,"").clear();
                editor.putString(Variables.created_time,"").clear();

                editor.commit();
                startActivity(new Intent(getActivity(), Login_A.class));
                getActivity().overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                getActivity().finish();
            }
        });


        setting_layout=view.findViewById(R.id.setting_layout);
        setting_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Setting_profile();
            }
        });


        profile_image=view.findViewById(R.id.profile_image);
        user_name=view.findViewById(R.id.user_name);
        age=view.findViewById(R.id.age);


        // show the picture age and username of the user
        Picasso.with(context).load(MainMenuActivity.user_pic)
                .resize(200,200)
                .centerCrop()
                .into(profile_image);

        user_name.setText(MainMenuActivity.user_name + " - ");
        age.setText(" "+MainMenuActivity.birthday);


        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Profile_detail();
            }
        });



        return view;
    }


    // open the view of Edit profile where 6 pic is visible
    public void Profile_detail(){

        Profile_Details_F profile_details_f = new Profile_Details_F();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_bottom, R.anim.out_to_top, R.anim.in_from_top, R.anim.out_from_bottom);
        transaction.addToBackStack(null);
        transaction.replace(R.id.MainMenuFragment, profile_details_f).commit();

    }



    // open the view of Edit profile where 6 pic is visible
    public void Editprofile(){
        EditProfile_F editProfile_f = new EditProfile_F();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_bottom, R.anim.out_to_top, R.anim.in_from_top, R.anim.out_from_bottom);
        transaction.addToBackStack(null);
        transaction.replace(R.id.MainMenuFragment, editProfile_f).commit();
    }


    // open the view of Edit profile where 6 pic is visible
    public void Setting_profile(){
        Setting_F setting_f = new Setting_F();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_bottom, R.anim.out_to_top, R.anim.in_from_top, R.anim.out_from_bottom);
        transaction.addToBackStack(null);
        transaction.replace(R.id.MainMenuFragment, setting_f).commit();
    }





}
