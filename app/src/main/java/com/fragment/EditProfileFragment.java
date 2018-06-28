package com.fragment;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.app.android.cync.R;
import com.backgroundTask.UpdateProfileTask;
import com.cync.model.UserDetail;
import com.rey.material.app.Dialog;
import com.rey.material.widget.Button;
import com.rey.material.widget.EditText;
import com.rey.material.widget.Spinner;
import com.rey.material.widget.TextView;
import com.squareup.picasso.Picasso;
import com.utils.CommonClass;
import com.utils.ConnectionDetector;
import com.utils.Constants;
import com.webservice.AsyncUtil;
import com.webservice.VolleySetup;
import com.webservice.VolleyStringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ketul.patel on 8/1/16.
 */
public class EditProfileFragment extends BaseContainerFragment {

    boolean isFirstMake=true,isFirstYear=true,isFirstModel=true;
    private  Activity mActivity;
    EditText profile_frist_name,profile_last_name;
    private ImageView ivBack,updateProfile;
    TextView profile_email;
    Button register_btn,cancel_btn;
    Spinner spinnerMake,spinnerYear,spinnerModel;

    ArrayList<String> listYear = new ArrayList<>();
    ArrayList<String> listModel = new ArrayList<>();
    ArrayList<String> listMake = new ArrayList<>();
    ArrayList<String> listMakeId = new ArrayList<>();
    private RequestQueue mQueue;

    UpdateProfileTask updateProfileTask;
    public EditProfileFragment() {
        // Required empty public constructor
    }

    ArrayAdapter mAdapterMake,mAdapterModel,mAdapterYear;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        mQueue = VolleySetup.getRequestQueue();
        Init(rootView);
        setUserData();
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        mActivity=activity;
        super.onAttach(activity);
    }
    public void Init(View view){
        updateProfile = (ImageView) view.findViewById(R.id.updateProfile);
        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPhoto();
            }
        });

        spinnerMake=(Spinner) view.findViewById(R.id.spinnerMake);
        spinnerYear=(Spinner) view.findViewById(R.id.spinnerYear);
        spinnerModel=(Spinner) view.findViewById(R.id.spinnerModel);



        getMake();


        spinnerMake.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(Spinner parent, View view, int position, long id) {


                listYear.clear();
                listYear.add("Select Year");
                if(mAdapterYear!=null) {
                    mAdapterYear.notifyDataSetChanged();
                    spinnerYear.setSelection(0);
                }

                listModel.clear();
                listModel.add("Select Model");
                if(mAdapterModel!=null) {
                    mAdapterModel.notifyDataSetChanged();
                    spinnerModel.setSelection(0);
                }

                if (position > 0) {







//                    listYear.clear();
//                    listYear.add("Select Year");
//                    if(mAdapterYear!=null) {
//                        mAdapterYear.notifyDataSetChanged();
//                        spinnerYear.setSelection(0);
//                    }
//
//                    listModel.clear();
//                    listModel.add("Select Model");
//                    if(mAdapterModel!=null) {
//                        mAdapterModel.notifyDataSetChanged();
//                        spinnerModel.setSelection(0);
//                    }

                    getYear();

                }

            }
        });


        spinnerYear.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(Spinner parent, View view, int position, long id) {

                listModel.clear();
                listModel.add("Select Model");
                if(mAdapterModel!=null) {
                    mAdapterModel.notifyDataSetChanged();
                    spinnerModel.setSelection(0);
                }

                if (position > 0) {

//                    listModel.clear();
//                    listModel.add("Select Model");
//                    if(mAdapterModel!=null) {
//                        mAdapterModel.notifyDataSetChanged();
//                        spinnerModel.setSelection(0);
//                    }
                    getModel();

                }




            }
        });


        spinnerModel.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(Spinner parent, View view, int position, long id) {


            }
        });


        profile_frist_name=(EditText) view.findViewById(R.id.profile_frist_name);
        profile_last_name=(EditText) view.findViewById(R.id.profile_last_name);
        profile_email=(TextView) view.findViewById(R.id.profile_email);
        ivBack = (ImageView) view.findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popFragment();
            }
        });
        register_btn=(Button) view.findViewById(R.id.save_btn);
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
updateProfile();
            }
        });
        cancel_btn=(Button) view.findViewById(R.id.cancel_btn);
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popFragment();
            }
        });
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
                    // handle back button's click listener
                    popFragment();
                    return true;
                }
                return false;
            }
        });

    }



    private void getModel() {
        ConnectionDetector cd = new ConnectionDetector(getActivity());
        boolean isConnected = cd.isConnectingToInternet();
        if (isConnected) {
            Log.i("EditProfileFramgment", "URL >==" + Constants.getModel + listMakeId.get(spinnerMake.getSelectedItemPosition()));
            VolleyStringRequest apiRequest = new VolleyStringRequest(Request.Method.GET, Constants.getModel + listMakeId.get(spinnerMake.getSelectedItemPosition()) + "&year=" + listYear.get(spinnerYear.getSelectedItemPosition()), modelSuccessLisner(),
                    ErrorLisner()) {
                @Override
                protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                    HashMap<String, String> requestparam = new HashMap<>();

                    return requestparam;
                }
            };
            CommonClass.showLoading(getActivity());
            mQueue.add(apiRequest);
        }
    }


    private void getYear() {

        ConnectionDetector cd = new ConnectionDetector(getActivity());
        boolean isConnected = cd.isConnectingToInternet();
        if (isConnected) {
            Log.i("EditProfileFramgment", "URL >==" + Constants.getYear + listMakeId.get(spinnerMake.getSelectedItemPosition()));
            VolleyStringRequest apiRequest = new VolleyStringRequest(Request.Method.GET, Constants.getYear + listMakeId.get(spinnerMake.getSelectedItemPosition()), yearSuccessLisner(),
                    ErrorLisner()) {
                @Override
                protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                    HashMap<String, String> requestparam = new HashMap<>();

                    return requestparam;
                }
            };
            CommonClass.showLoading(getActivity());
            mQueue.add(apiRequest);
        }
    }

    private void getMake() {
        ConnectionDetector cd = new ConnectionDetector(getActivity());
        boolean isConnected = cd.isConnectingToInternet();
        if (isConnected) {
            VolleyStringRequest apiRequest = new VolleyStringRequest(Request.Method.GET, Constants.getMake, makeSuccessLisner(),
                    ErrorLisner()) {
                @Override
                protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                    HashMap<String, String> requestparam = new HashMap<>();

                    return requestparam;
                }
            };
            CommonClass.showLoading(getActivity());
            mQueue.add(apiRequest);
        } else {

            CommonClass.ShowToast(getActivity(), getResources().getString(R.string.check_internet));


        }
    }

    private com.android.volley.Response.Listener<String> makeSuccessLisner() {
        return new com.android.volley.Response.Listener<String>() {
            private String responseMessage = "";
            @Override
            public void onResponse(String response) {
                Log.d("SuccessLisner Json", "==> " + response);
                CommonClass.closeLoding(getActivity());
                boolean Status;
                String message;
                try {
                    JSONObject jresObjectMain = new JSONObject(response);

                    Status = jresObjectMain.getBoolean("status");
                    message = jresObjectMain.getString("message");
                    if (Status) {

                        if(jresObjectMain.has("result"))
                        {
                            JSONArray resultArray=jresObjectMain.getJSONArray("result");
                            listMake.clear();
                            listMakeId.clear();

                            listMake.add("Select Make");
                            listMakeId.add("-1");

                            for(int i=0;i<resultArray.length();i++)
                            {
                                JSONObject jsonObject = resultArray.getJSONObject(i);

                                listMake.add("" + CommonClass.getDataFromJson(jsonObject, "make_name"));
                                listMakeId.add("" + CommonClass.getDataFromJsonInt(jsonObject, "term_id"));
                            }


                            if(isFirstMake) {

                                mAdapterMake = new ArrayAdapter<String>(getActivity(), R.layout.spiner_item_layout, listMake);
                                spinnerMake.setAdapter(mAdapterMake);
                                mAdapterMake.setDropDownViewResource(R.layout.spiner_dropdown_item_layout);
                                android.widget.TextView tv = (android.widget.TextView) spinnerMake.getSelectedView();
                                tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.drop_down_arrow, 0, 0, 0);


                                UserDetail udm = CommonClass.getUserpreference(getActivity());
                                if (udm.make.trim().length() != 0) {

                                    int index = listMake.indexOf(udm.make);
                                    if (index != -1)
                                        spinnerMake.setSelection(index);
                                }
                                isFirstMake=false;
                            }
                            else
                                mAdapterMake.notifyDataSetChanged();

                        }


                    } else {
                        CommonClass.ShowToast(getActivity(), message);
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    message = getResources().getString(R.string.s_wrong);
                    e.printStackTrace();
                }

            }
        };
    }



    private com.android.volley.Response.Listener<String> yearSuccessLisner() {
        return new com.android.volley.Response.Listener<String>() {
            private String responseMessage = "";
            @Override
            public void onResponse(String response) {
                Log.d("SuccessLisner Json", "==> " + response);
                CommonClass.closeLoding(getActivity());
                boolean Status;
                String message;
                try {
                    JSONObject jresObjectMain = new JSONObject(response);

                    Status = jresObjectMain.getBoolean("status");
                    message = jresObjectMain.getString("message");
                    if (Status) {

                        if(jresObjectMain.has("result"))
                        {
                            JSONArray resultArray=jresObjectMain.getJSONArray("result");
                            listYear.clear();
                            listYear.add("Select Year");
                            for(int i=0;i<resultArray.length();i++)
                            {
                                JSONObject jsonObject = resultArray.getJSONObject(i);

                                listYear.add("" + CommonClass.getDataFromJson(jsonObject, "year"));

                            }


                            if(isFirstYear) {


                                mAdapterYear = new ArrayAdapter<String>(getActivity(), R.layout.spiner_item_layout, listYear);
                                spinnerYear.setAdapter(mAdapterYear);
                                mAdapterYear.setDropDownViewResource(R.layout.spiner_dropdown_item_layout);
                                android.widget.TextView tv = (android.widget.TextView) spinnerYear.getSelectedView();
                                tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.drop_down_arrow, 0, 0, 0);



                                UserDetail udm = CommonClass.getUserpreference(getActivity());
                                if (udm.year.trim().length() != 0) {

                                    int index = listYear.indexOf(udm.year);
                                    if (index != -1)
                                        spinnerYear.setSelection(index);
                                }
                                isFirstYear=false;

                            }
                            else {
                                mAdapterYear.notifyDataSetChanged();
                                spinnerYear.setSelection(0);
                            }
                        }


                    } else {
                        CommonClass.ShowToast(getActivity(), message);
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    message = getResources().getString(R.string.s_wrong);
                    e.printStackTrace();
                }

            }
        };
    }

    private com.android.volley.Response.Listener<String> modelSuccessLisner() {
        return new com.android.volley.Response.Listener<String>() {
            private String responseMessage = "";
            @Override
            public void onResponse(String response) {
                Log.d("SuccessLisner Json", "==> " + response);
                CommonClass.closeLoding(getActivity());
                boolean Status;
                String message;
                try {
                    JSONObject jresObjectMain = new JSONObject(response);

                    Status = jresObjectMain.getBoolean("status");
                    message = jresObjectMain.getString("message");
                    if (Status) {

                        if(jresObjectMain.has("result"))
                        {
                            JSONArray resultArray=jresObjectMain.getJSONArray("result");

                            listModel.clear();
                            listModel.add("Select Model");
                            for(int i=0;i<resultArray.length();i++)
                            {
                                JSONObject jsonObject = resultArray.getJSONObject(i);

                                listModel.add("" + CommonClass.getDataFromJson(jsonObject, "model_name"));

                            }


                            if(isFirstModel) {

                                mAdapterModel = new ArrayAdapter<String>(getActivity(), R.layout.spiner_item_layout, listModel);
                                spinnerModel.setAdapter(mAdapterModel);
                                mAdapterModel.setDropDownViewResource(R.layout.spiner_dropdown_item_layout);
                                android.widget.TextView tv = (android.widget.TextView) spinnerModel.getSelectedView();
                                tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.drop_down_arrow, 0, 0, 0);



                                UserDetail udm = CommonClass.getUserpreference(getActivity());
                                if (udm.model.trim().length() != 0) {

                                    int index = listModel.indexOf(udm.model);
                                    if (index != -1)
                                        spinnerModel.setSelection(index);
                                }

                                isFirstModel=false;
                            }
                            else {
                                mAdapterModel.notifyDataSetChanged();
                                spinnerModel.setSelection(0);
                            }
                        }


                    } else {
                        CommonClass.ShowToast(getActivity(), message);
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    message = getResources().getString(R.string.s_wrong);
                    e.printStackTrace();
                }

            }
        };
    }

    private com.android.volley.Response.ErrorListener ErrorLisner() {
        return new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Log.e("Json Error", "==> " + error.getMessage());
                CommonClass.closeLoding(getActivity());
                CommonClass.ShowToast(getActivity(), getResources().getString(R.string.s_wrong));
            }
        };
    }



    public void updateProfile(){
        boolean valid = ValidateTask(profile_frist_name.getText().toString().trim(), profile_last_name.getText().toString().trim());
        if (valid) {
            ConnectionDetector cd = new ConnectionDetector(getActivity());
            boolean isConnected = cd.isConnectingToInternet();
            if (isConnected) {


                if(spinnerMake.getSelectedItemPosition()>0) {

                    if(spinnerYear.getSelectedItemPosition()>0 && spinnerModel.getSelectedItemPosition()>0) {
                        //    email,password,full_name,contact_no, address,avatar(file field),device_token,device_type
                        if (finalimagepath == null || finalimagepath.length() == 0)
                            finalimagepath = "";
                        updateProfileTask = new UpdateProfileTask(mupdateProfileTaskResponder, getActivity());
                        AsyncUtil.execute(updateProfileTask, profile_frist_name.getText().toString(), profile_last_name.getText().toString(), finalimagepath, spinnerMake.getSelectedItem().toString(), spinnerYear.getSelectedItem().toString(), spinnerModel.getSelectedItem().toString());
                    }
                    else
                    {
                        if(spinnerYear.getSelectedItemPosition()==0 && spinnerModel.getSelectedItemPosition()==0)
                            CommonClass.ShowToast(getActivity(), "Please select year and model.");
                        if(spinnerYear.getSelectedItemPosition()==0)
                            CommonClass.ShowToast(getActivity(), "Please select year.");
                        else  if(spinnerModel.getSelectedItemPosition()==0)
                            CommonClass.ShowToast(getActivity(), "Please select model.");

                    }
                }
                else
                {




                    String sMake="",sYear="",sModel="";




                    try {
                        if (finalimagepath == null || finalimagepath.length() == 0)
                            finalimagepath = "";
                        updateProfileTask = new UpdateProfileTask(mupdateProfileTaskResponder, getActivity());
                        AsyncUtil.execute(updateProfileTask, profile_frist_name.getText().toString(), profile_last_name.getText().toString(), finalimagepath,sMake, sYear, sModel);
                    }
                    catch (Exception e)
                    {
                        CommonClass.ShowToast(getActivity(), "Something went wrong, please try again later.");
                    }
                }
            }

        }
    }
    UpdateProfileTask.Responder mupdateProfileTaskResponder = new UpdateProfileTask.Responder() {

        @Override
        public void onComplete(boolean result, String message) {
            if (result) {
                CommonClass.ShowToast(getActivity(), message);
            } else {
                CommonClass.ShowToast(getActivity(), message);
            }
            popFragment();
        }
    };

    @Override
    public void onDetach() {
        super.onDetach();
    }
    public void setUserData(){
        UserDetail userDetail = CommonClass.getUserpreference(getActivity());
        if(userDetail != null) {
            String imagePath = userDetail.user_image;
            String str="";
            if (!userDetail.first_name.equalsIgnoreCase("null")&& userDetail.first_name.length() > 0) {
//                str=CommonClass.strEncodeDecode(userDetail.first_name,true);
                profile_frist_name.setText(userDetail.first_name);
                if (!userDetail.last_name.equalsIgnoreCase("null") && userDetail.last_name.length() > 0) {
//                     str=CommonClass.strEncodeDecode(userDetail.last_name,true);
                    profile_last_name.setText(userDetail.last_name);
                }
//                Log.e("login_type","userDetail.email"+userDetail.email);
                if (userDetail.login_type.equalsIgnoreCase("facebook")) {
                    profile_email.setText("Facebook User");
//                    profile_last_name.setVisibility(View.GONE);
                }
                else if (!userDetail.login_type.equalsIgnoreCase("normal")) {
                         profile_email.setText(userDetail.email);
//                    profile_last_name.setVisibility(View.VISIBLE);
                }else
                    profile_email.setText(userDetail.email);

                if (imagePath != null && imagePath.length() > 0) {
                    if(userDetail.login_type.equalsIgnoreCase("facebook")){
                        if(imagePath.contains("https://")||imagePath.contains("http://"))
                            imagePath=imagePath.trim();
                        else
                            imagePath = Constants.imagBaseUrl + imagePath.trim();
                    }else
                        imagePath = Constants.imagBaseUrl + imagePath.trim();



                    Picasso.with(getActivity())
                            .load(imagePath)
                            .placeholder(R.drawable.no_image) //this is optional the image to display while the url image is downloading
                            .error(R.drawable.no_image)         //this is also optional if some error has occurred in downloading the image this image would be displayed
                            .into(updateProfile);


//                    Log.e("-imagePath", "-------" + imagePath);
                } else
                    updateProfile.setBackgroundResource(R.drawable.no_image);
            }
        }
    }

    //--------------------
    private String finalimagepath;
//    private ImageView imageViewProfile;
    Dialog dialog;
    public void selectPhoto() {
        CommonClass.closeKeyboard(getActivity());
//                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                dialog = new Dialog(RegisterActivity.this, R.style.AppCompatAlertDialogStyle);
        dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent);
        // dialog.setCancelable(false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.stockAnimation;
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        dialog.setContentView(R.layout.alert_dialog_image_picker);
        dialog.show();


        Button buttonGallery = (Button) dialog.findViewById(R.id.buttonGallery);
        Button buttonCamera = (Button) dialog.findViewById(R.id.buttonCamera);
        Button buttonimgCancel = (Button) dialog.findViewById(R.id.buttonimgCancel);


        buttonimgCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });

        buttonCamera.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialog.dismiss();
                PackageManager pm = getActivity().getPackageManager();
                if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                    Calendar calendar = Calendar.getInstance();
                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH) + 1;
                    int date = calendar.get(Calendar.DATE);
                    int hour = calendar.get(Calendar.HOUR);
                    int minute = calendar.get(Calendar.MINUTE);
                    int second = calendar.get(Calendar.SECOND);

                    File file = new File(Environment.getExternalStorageDirectory()
                            + "/Cync/Camera/Images");
                    file.mkdirs();

                    String imageName = date + "_" + month + "_" + year + "_" + hour + "_"
                            + minute + "_" + second + ".jpg";

                    File image = new File(file.getAbsolutePath(), imageName);
                    cameraUri = Uri.fromFile(image);

                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);

                    startActivityForResult(takePictureIntent,
                            CAMERA_CODE);
                } else {
                    Toast.makeText(getActivity(),
                            "Device has no camera!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        buttonGallery.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(intent, GALLERY_CODE);

            }
        });

    }

    public Uri mImageCaptureUri, cameraUri, cropUri;
    public static final int CAMERA_CODE = 101, GALLERY_CODE = 102;
    public static final int CROP_FROM_CAMERA = 2;

    private void performCrop(Uri picUri) {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("outputX", 512);
            cropIntent.putExtra("outputY", 512);
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("scale", true);
            cropIntent.putExtra("return-data", false);
            // save output image in uri

            String folderName = "/.Cync";
            String filename = Calendar.getInstance().getTimeInMillis() + ".jpg";

            File f = new File(Environment.getExternalStorageDirectory()
                    + folderName);

            if (!f.exists()) {
                f.mkdirs();
            }
            File file = null;
            try {
                file = new File(f.getPath() + filename);
                file.createNewFile();
            } catch (IOException ex) {
//                Log.e("io", ex.getMessage());
            }
            cropUri = Uri.fromFile(file);
            cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, cropUri);
            startActivityForResult(cropIntent,
                    CROP_FROM_CAMERA);
        }
        catch (ActivityNotFoundException anfe) {
            anfe.printStackTrace();
        }
    }

    @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CODE && resultCode == getActivity().RESULT_OK
                && null != data) {
            mImageCaptureUri = data.getData();
            performCrop(mImageCaptureUri);
        } else if (requestCode == CROP_FROM_CAMERA && resultCode == getActivity().RESULT_OK) {
            finalimagepath = cropUri.getPath();
            getBitmap(cropUri);
            updateProfile.setImageBitmap(getBitmap(cropUri));
        } else if (requestCode == CROP_FROM_CAMERA && resultCode == getActivity().RESULT_CANCELED) {
            finalimagepath = "";
        } else if (requestCode == CAMERA_CODE
                && resultCode == Activity.RESULT_OK) {
            performCrop(cameraUri);
        }
    }

    private Bitmap getBitmap(Uri path) {
        ContentResolver mContentResolver = getActivity().getContentResolver();
        Uri uri = path;
        InputStream in = null;
        try {
            final int IMAGE_MAX_SIZE = 1200000; // 1.2MP
            in = mContentResolver.openInputStream(uri);
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, o);
            in.close();
            int scale = 1;
            while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) > IMAGE_MAX_SIZE) {
                scale++;
            }
//            Log.d("TAG", "scale = " + scale + ", orig-width: " + o.outWidth
//                    + ", orig-height: " + o.outHeight);
            Bitmap b = null;
            in = mContentResolver.openInputStream(uri);
            if (scale > 1) {
                scale--;
                // scale to max possible inSampleSize that still yields an image
                // larger than target
                o = new BitmapFactory.Options();
                o.inSampleSize = scale;
                b = BitmapFactory.decodeStream(in, null, o);
                // resize to desired dimensions
                int height = b.getHeight();
                int width = b.getWidth();
//                Log.d("TAG", "1th scale operation dimenions - width: " + width
//                        + ", height: " + height);
                double y = Math.sqrt(IMAGE_MAX_SIZE
                        / (((double) width) / height));
                double x = (y / height) * width;
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, (int) x,
                        (int) y, true);
                b.recycle();
                b = scaledBitmap;

                System.gc();
            } else {
                b = BitmapFactory.decodeStream(in);
            }
            in.close();
//
//            Log.d("TAG", "bitmap size - width: " + b.getWidth() + ", height: "
//                    + b.getHeight());
            return b;
        } catch (IOException e) {
//            Log.e("TAG", e.getMessage(), e);
            return null;
        }
    }
    public boolean ValidateTask(String firstname, String lastname) {
        if (firstname.trim().length() > 0) {
            if(CommonClass.getUserpreference(getActivity()).login_type.equalsIgnoreCase("normal")) {
                if (lastname.trim().length() > 0) {
                    return true;
                } else {
                    CommonClass.ShowToast(getActivity(), "Please enter last name.");
                    return false;
                }
            }else
                return true;
        } else {
            CommonClass.ShowToast(getActivity(), "Please enter first name.");
            return false;
        }
    }







}