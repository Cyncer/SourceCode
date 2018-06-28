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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.app.android.cync.R;
import com.backgroundTask.UpdateGroupProfileTask;
import com.rey.material.app.Dialog;
import com.rey.material.widget.Button;
import com.rey.material.widget.EditText;
import com.squareup.picasso.Picasso;
import com.utils.CommonClass;
import com.utils.ConnectionDetector;
import com.utils.Constants;
import com.webservice.AsyncUtil;
import com.webservice.VolleySetup;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

/**
 * Created by ketul.patel on 8/1/16.
 */
public class UpdateGroupFragment extends BaseContainerFragment {
    //    TextView txtLabel;
    private  Activity mActivity;
    EditText group_name;
    private ImageView ivBack,imageViewProfile;
    private Button next_group_btn;
    private TextView txtTitle;
    private RequestQueue mQueue;
    private String groupId;
    private String finalimagepath;
    public static String groupName,groupImagePath;
    public UpdateGroupFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        mQueue = VolleySetup.getRequestQueue();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(getArguments()!=null&&getArguments().size()>0) {
            groupId = getArguments().getString("group_id", "");
            groupName = getArguments().getString("group_name", "");
            groupImagePath = getArguments().getString("group_image", "");
        }
        View rootView = inflater.inflate(R.layout.fragment_add_group, container, false);

        Init(rootView);
        // Inflate the layout for this fragment
        return rootView;
    }

    public void Init(View view) {
        txtTitle= (TextView) view.findViewById(R.id.txtTitle);
        txtTitle.setText("UPDATE GROUP");
        imageViewProfile = (ImageView) view.findViewById(R.id.imageViewProfile);
        imageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPhoto();
            }
        });
        group_name=(EditText) view.findViewById(R.id.group_name);
        ivBack = (ImageView) view.findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popFragment();
            }
        });
        next_group_btn=(Button) view.findViewById(R.id.next_group_btn);
        next_group_btn.setText("UPDATE");
        next_group_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
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
        if(groupId!=null&&groupId.trim().length()>0){
//            String str=CommonClass.strEncodeDecode(groupName.trim(),true);
            group_name.setText(groupName.trim());

            String imagePath = UpdateGroupFragment.groupImagePath;
            if (imagePath != null && imagePath.length() > 0) {
                imagePath = Constants.imagBaseUrl + imagePath.trim();
//                Log.e("userDetailList", "-------" + imagePath);

                Picasso.with(mActivity)
                        .load(imagePath)
                        .placeholder(R.drawable.no_image) //this is optional the image to display while the url image is downloading
                        .error(R.drawable.no_image)         //this is also optional if some error has occurred in downloading the image this image would be displayed
                        .into(imageViewProfile);
            } else
                imageViewProfile.setImageResource(R.drawable.no_image);
        }
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity=activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void updateProfile(){
        boolean valid = ValidateTask(group_name.getText().toString().trim());
        if (valid) {
            ConnectionDetector cd = new ConnectionDetector(getActivity());
            boolean isConnected = cd.isConnectingToInternet();
            if (isConnected) {
                UpdateGroupProfileTask updateGroupProfileTask = new UpdateGroupProfileTask(mupdateProfileTaskResponder, getActivity());
                AsyncUtil.execute(updateGroupProfileTask, groupId, group_name.getText().toString().trim(),finalimagepath,"");
            }
        }
    }
    public boolean ValidateTask(String groupName) {
        if (groupName.trim().length() > 0) {
                return true;
        } else {
            CommonClass.ShowToast(getActivity(), "Please enter group name.");
            return false;
        }
    }
    UpdateGroupProfileTask.Responder mupdateProfileTaskResponder = new UpdateGroupProfileTask.Responder() {

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
    //----------------------

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
                dialog.dismiss();
            }
        });
        buttonCamera.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
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
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

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
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message


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
            finalimagepath= cropUri.getPath();
            getBitmap(cropUri);
            imageViewProfile.setImageBitmap(getBitmap(cropUri));
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

//            Log.d("TAG", "bitmap size - width: " + b.getWidth() + ", height: "
//                    + b.getHeight());
            return b;
        } catch (IOException e) {
//            Log.e("TAG", e.getMessage(), e);
            return null;
        }
    }
}