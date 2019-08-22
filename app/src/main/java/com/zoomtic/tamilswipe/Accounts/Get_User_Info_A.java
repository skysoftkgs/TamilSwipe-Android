package com.zoomtic.tamilswipe.Accounts;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.gmail.samehadar.iosdialog.IOSDialog;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.soundcloud.android.crop.Crop;
import com.ybs.countrypicker.Country;
import com.ybs.countrypicker.CountryPicker;
import com.ybs.countrypicker.CountryPickerListener;
import com.zoomtic.tamilswipe.CodeClasses.Functions;
import com.zoomtic.tamilswipe.CodeClasses.Variables;
import com.zoomtic.tamilswipe.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Get_User_Info_A extends AppCompatActivity {

    ImageView profile_image;
    TextView first_name,last_name;
    SharedPreferences sharedPreferences;

    DatabaseReference rootref;

    Button nextbtn;
    IOSDialog iosDialog;

    ImageButton edit_profile_image;
    EditText dateofbrith_edit, country_edit;
    RadioButton male_btn,female_btn;
    byte [] image_byte_array;

    String user_id;
    String countryCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature( Window.FEATURE_NO_TITLE );
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN );
        setContentView(R.layout.activity_get_user_info);



        sharedPreferences=getSharedPreferences(Variables.pref_name, Context.MODE_PRIVATE);

        rootref= FirebaseDatabase.getInstance().getReference();

        iosDialog = new IOSDialog.Builder(this)
                .setCancelable(false)
                .setSpinnerClockwise(false)
                .setMessageContentGravity(Gravity.END)
                .build();


        profile_image=findViewById(R.id.profile_image);

        edit_profile_image=findViewById(R.id.edit_profile_image);
        edit_profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });


        first_name=findViewById(R.id.first_name);
        last_name=findViewById(R.id.last_name);


        dateofbrith_edit=findViewById(R.id.dateofbirth_edit);

        male_btn=findViewById(R.id.male_btn);
        female_btn=findViewById(R.id.female_btn);

        country_edit= findViewById(R.id.country_edit);
        country_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCountry();
            }
        });

        dateofbrith_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Functions.Opendate_picker(Get_User_Info_A.this,dateofbrith_edit);

            }
        });


        nextbtn=findViewById(R.id.nextbtn);
        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String f_name= first_name.getText().toString();
                String l_name=last_name.getText().toString();
                String date_of_birth=dateofbrith_edit.getText().toString();
                String country = country_edit.getText().toString();

                if(image_byte_array==null){
                    Toast.makeText(Get_User_Info_A.this, "Select Image", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(f_name)){

                    Toast.makeText(Get_User_Info_A.this, "Please enter your Name", Toast.LENGTH_SHORT).show();

                }
//                else if(TextUtils.isEmpty(l_name)){
//
//                    Toast.makeText(Get_User_Info_A.this, "Please enter your Last Name", Toast.LENGTH_SHORT).show();
//
//                }
                else if(TextUtils.isEmpty(date_of_birth)){

                    Toast.makeText(Get_User_Info_A.this, "Please enter your Date of Birth", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(country)){

                    Toast.makeText(Get_User_Info_A.this, "Please enter your country", Toast.LENGTH_SHORT).show();
                }
                else {

                    Save_info();
                }

            }
        });




        Intent intent=getIntent();
        if(intent.hasExtra("id")) {
            user_id = intent.getExtras().getString("id");
            user_id = user_id.replace("+", "");
        }
        if(intent.hasExtra("fname")){
            if (intent.getExtras().getString("fname") != null && !intent.getExtras().getString("fname").equals("null"))
                first_name.setText(intent.getExtras().getString("fname"));
        }

        if(intent.hasExtra("lname")){
            if (intent.getExtras().getString("lname") != null && !intent.getExtras().getString("lname").equals("null"))
                last_name.setText(intent.getExtras().getString("lname"));
        }


    }



    // open the gallary to select and upload the picture
    private void selectImage() {
        Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 2);
    }


    // on select the bottom method will reture the uri of that image
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK){

        if (requestCode == 2) {

            Uri selectedImage = data.getData();
            beginCrop(selectedImage);
        }
        else if (requestCode == 123) {
            handleCrop(resultCode, data);
        }

        }

    }



    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().withMaxSize(500,500).start(this,123);
    }


    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            Uri userimageuri=Crop.getOutput(result);

            InputStream imageStream = null;
            try {
                imageStream =getContentResolver().openInputStream(userimageuri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            final Bitmap imagebitmap = BitmapFactory.decodeStream(imageStream);

            String path=userimageuri.getPath();
            Matrix matrix = new Matrix();
            android.media.ExifInterface exif = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                try {
                    exif = new android.media.ExifInterface(path);
                    int orientation = exif.getAttributeInt(android.media.ExifInterface.TAG_ORIENTATION, 1);
                    switch (orientation) {
                        case android.media.ExifInterface.ORIENTATION_ROTATE_90:
                            matrix.postRotate(90);
                            break;
                        case android.media.ExifInterface.ORIENTATION_ROTATE_180:
                            matrix.postRotate(180);
                            break;
                        case android.media.ExifInterface.ORIENTATION_ROTATE_270:
                            matrix.postRotate(270);
                            break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            Bitmap rotatedBitmap = Bitmap.createBitmap(imagebitmap, 0, 0, imagebitmap.getWidth(), imagebitmap.getHeight(), matrix, true);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            image_byte_array = out.toByteArray();


            profile_image.setImageBitmap(null);
            profile_image.setImageURI(null);
            profile_image.setImageBitmap(rotatedBitmap);



        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }



    // this method is used to store the selected image into database
    public void Save_info(){
        iosDialog.show();
        // first we upload image after upload then get the picture url and save the group data in database
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference filelocation = storageReference.child("User_image")
                .child(user_id + ".jpg");
        filelocation.putBytes(image_byte_array).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {


                Call_Api_For_Signup(user_id,
                        first_name.getText().toString(),
                        last_name.getText().toString(),
                        dateofbrith_edit.getText().toString(),
                        country_edit.getText().toString(),
                        countryCode, taskSnapshot.getDownloadUrl().toString());

            }});
    }



    // this method will store the info of user to  database
    private void Call_Api_For_Signup(String user_id,
                                     String f_name,String l_name,
                                     String birthday,String countryName, String countryCode, String picture) {

        f_name=f_name.replaceAll("\\W+","");
        l_name=l_name.replaceAll("\\W+","");

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("fb_id", user_id);
            parameters.put("first_name",f_name);
            parameters.put("last_name", l_name);
            parameters.put("birthday", birthday);
            parameters.put("country_name", countryName);
            parameters.put("country_code", countryCode);

            if(male_btn.isChecked()){
                parameters.put("gender","Male");

            }else{
                parameters.put("gender","Female");
            }
            parameters.put("image1",picture);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestQueue rq = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Variables.SignUp, parameters, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        String respo=response.toString();
                        Log.d("responce",respo);

                        iosDialog.cancel();
                        Parse_signup_data(respo);

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        iosDialog.cancel();
                        Toast.makeText(Get_User_Info_A.this, "Somthing Wrong with Api", Toast.LENGTH_LONG).show();
                        Log.d("respo",error.toString());
                    }
                });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.getCache().clear();
        rq.add(jsonObjectRequest);
    }


    // if the signup successfull then this method will call and it store the user info in local
    public void Parse_signup_data(String loginData){
        try {
            JSONObject jsonObject=new JSONObject(loginData);
            String code=jsonObject.optString("code");
            if(code.equals("200")){
                JSONArray jsonArray=jsonObject.getJSONArray("msg");
                JSONObject userdata = jsonArray.getJSONObject(0);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString(Variables.uid,userdata.optString("fb_id"));
                editor.putString(Variables.f_name,userdata.optString("first_name"));
                editor.putString(Variables.l_name,userdata.optString("last_name"));
                editor.putString(Variables.birth_day,userdata.optString("age"));
                editor.putString(Variables.gender,userdata.optString("gender"));
                editor.putString(Variables.country_name,userdata.optString("country_name"));
                editor.putString(Variables.country_code,userdata.optString("country_code"));
                editor.putString(Variables.u_pic,userdata.optString("image1"));
                editor.putString(Variables.created_time,userdata.optString("created"));
                List<Country> allCountries = Country.getAllCountries();
                String str = "";
                for(int i=0;i<allCountries.size();i++){
                    str += allCountries.get(i).getCode() + "_";
                }
                editor.putString(Variables.country_from,str);
                editor.putBoolean(Variables.islogin,true);
                editor.commit();

                // after all things done we will move the user to enable location screen
                enable_location();
            }else {
                Toast.makeText(this, ""+jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            iosDialog.cancel();
            e.printStackTrace();
        }

    }


    private void enable_location() {

        // will move the user for enable location screen
        Enable_location_F enable_location_f = new Enable_location_F();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left,R.anim.in_from_left,R.anim.out_to_right);
        getSupportFragmentManager().popBackStackImmediate();
        transaction.replace(R.id.Get_Info_F, enable_location_f).addToBackStack(null).commit();
    }


    public void Goback(View view) {
        finish();
    }

    public void openCountry(){
        final CountryPicker picker = CountryPicker.newInstance("Select Country");
        List<Country> countryArrayList = Country.getAllCountries();
        Collections.sort(countryArrayList, new Comparator<Country>() {
            @Override
            public int compare(Country country1, Country country2) {
                return country1.getName().trim().compareToIgnoreCase(country2.getName().trim());
            }
        });
        picker.setCountriesList(countryArrayList);
        picker.setListener(new CountryPickerListener() {
            @Override
            public void onSelectCountry(String name, String code, String dialCode, int flagDrawableResID) {
                // Implement your code here
                country_edit.setText(name);
                countryCode = code;
                picker.dismiss();
            }
        });

        picker.setStyle(R.style.countrypicker_style,R.style.countrypicker_style);
        picker.show(getSupportFragmentManager(), "Select Country");
    }

}
