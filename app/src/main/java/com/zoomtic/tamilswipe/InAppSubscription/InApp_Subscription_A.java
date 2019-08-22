package com.zoomtic.tamilswipe.InAppSubscription;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.zoomtic.tamilswipe.CodeClasses.Variables;
import com.zoomtic.tamilswipe.Main_Menu.MainMenuActivity;
import com.zoomtic.tamilswipe.Main_Menu.RelateToFragment_OnBack.RootFragment;
import com.zoomtic.tamilswipe.R;
import com.gmail.samehadar.iosdialog.IOSDialog;
import com.labo.kaji.fragmentanimations.MoveAnimation;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Context.MODE_PRIVATE;

public class InApp_Subscription_A extends RootFragment implements BillingProcessor.IBillingHandler {

    BillingProcessor bp;
    public IOSDialog iosDialog;
    SharedPreferences sharedPreferences;
    View view;
    Context context;
    Button purchase_btn;

    TextView Goback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate
        // the layout for this fragment
        view= inflater.inflate(R.layout.activity_in_app_subscription, container, false);
        context=getContext();

        // get the sharepreference
        sharedPreferences=context.getSharedPreferences(Variables.pref_name,MODE_PRIVATE);

        purchase_btn=view.findViewById(R.id.purchase_btn);
        purchase_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Puchase_item();
            }
        });


        Goback=view.findViewById(R.id.Goback);
        Goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Goback();
            }
        });

        return view;
    }


    public void initlize_billing(){

        // intialize the billing process
        iosDialog = new IOSDialog.Builder(context)
                .setCancelable(false)
                .setSpinnerClockwise(false)
                .setMessageContentGravity(Gravity.END)
                .build();

        iosDialog.show();


        bp = new BillingProcessor(context, Variables.licencekey, this);
        bp.initialize();


    }


    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {

        // if the user is subcripbe successfully then we will store the data in local and also call the api
        sharedPreferences.edit().putBoolean(Variables.ispuduct_puchase,true).commit();
        MainMenuActivity.purduct_purchase=true;
        Call_Api_For_update_purchase();

    }


    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {

    }


    @Override
    public void onBillingInitialized() {
        // on billing intialize we will get the data from google
        if(bp.loadOwnedPurchasesFromGoogle()){

            // check user is already subscribe or not
        if(bp.isSubscribed(Variables.product_ID)){
            // if already subscribe then we will change the static variable and goback
            MainMenuActivity.purduct_purchase=true;
            iosDialog.cancel();
            Call_Api_For_update_purchase();
        }
        else {

            iosDialog.cancel();
        }
        }
    }



    // when we click the continue btn this method will call
    public void Puchase_item() {
        boolean isAvailable = BillingProcessor.isIabServiceAvailable(getActivity());
        if(isAvailable)
        bp.subscribe(getActivity(),Variables.product_ID);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("responce", "onActivity Result Code : " + resultCode);
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }



    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (enter) {
            Animation anim= MoveAnimation.create(MoveAnimation.UP, enter, 300);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}
                @Override
                public void onAnimationEnd(Animation animation) {
                    initlize_billing();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
            return anim;

        } else {
            return MoveAnimation.create(MoveAnimation.DOWN, enter, 300);
        }
    }


    // on destory we will release the billing process
    @Override
    public void onDestroy() {
        if (bp != null) {
            bp.release();
        }
        super.onDestroy();
    }


    public void Goback() {
        getActivity().onBackPressed();
    }


    // when user subscribe the app then this method will call that will store the status of user
    // into the database
    private void Call_Api_For_update_purchase() {
        iosDialog.show();

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("fb_id", MainMenuActivity.user_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue rq = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Variables.update_purchase_Status, parameters, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        String respo=response.toString();
                        Log.d("responce",respo);
                        iosDialog.cancel();
                        Goback();


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        iosDialog.cancel();
                        Log.d("respo",error.toString());
                    }
                });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.getCache().clear();
        rq.add(jsonObjectRequest);
    }


}
