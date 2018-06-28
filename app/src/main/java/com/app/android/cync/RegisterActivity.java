package com.app.android.cync;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.backgroundTask.RegisterTask;
import com.customwidget.materialEditText;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.rey.material.app.Dialog;
import com.rey.material.widget.Button;
import com.utils.CommonClass;
import com.utils.ConnectionDetector;
import com.utils.TextValidator;
import com.webservice.AsyncUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Created by ketul.patel on 18/1/16.
 */
public class RegisterActivity extends Activity {

    private materialEditText rg_first_name, rg_last_name, rg_email, rg_password, rg_conform_password;
    private Button register_btn;
    private ImageView ivBack;
    private RegisterTask mRegisterTask;
    private String finalimagepath;
    private ImageView imageViewProfile;
    private TextView txtTermsCondition;
    private com.rey.material.widget.CheckBox chkTermsCondition;
    Dialog dialog;
    String ride_id="";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {


            if(bundle.containsKey("ride_id"))
            {
                ride_id = bundle.getString("ride_id", "");
            }
        }


        init();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void init() {

        rg_first_name = (materialEditText) findViewById(R.id.rg_first_name);
        rg_last_name = (materialEditText) findViewById(R.id.rg_last_name);
        rg_email = (materialEditText) findViewById(R.id.rg_email);
        rg_password = (materialEditText) findViewById(R.id.rg_password);

     //  rg_password.setTransformationMethod(new PasswordTransformationMethod());
      //  rg_password.setMaxLines(1);
        rg_conform_password = (materialEditText) findViewById(R.id.rg_conform_password);
    //    rg_conform_password.setTransformationMethod(new PasswordTransformationMethod());
    //    rg_password.setMaxLines(1);
        imageViewProfile = (ImageView) findViewById(R.id.imageViewProfile);

        imageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // only for gingerbread and newer versions
                    if (CommonClass.doWeHavePermisiionFor(RegisterActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) || CommonClass.doWeHavePermisiionFor(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        selectPhoto();
                    }
                } else {
                    selectPhoto();
                }
            }
        });

        register_btn = (Button) findViewById(R.id.register_btn);
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ConnectionDetector cd = new ConnectionDetector(RegisterActivity.this);
                boolean isConnected = cd.isConnectingToInternet();
                if (isConnected) {
                    boolean valid = ValidateTask(rg_first_name.getText().toString().trim(), rg_last_name.getText().toString().trim(), rg_email.getText().toString().trim(), rg_password.getText().toString().trim(), rg_conform_password.getText().toString().trim());
                    if (valid) {
                        if (chkTermsCondition.isChecked()) {
                            //    email,password,full_name,contact_no, address,avatar(file field),device_token,device_type
                            if (finalimagepath == null || finalimagepath.length() == 0)
                                finalimagepath = "";
                            mRegisterTask = new RegisterTask(mResponder, RegisterActivity.this);
                            AsyncUtil.execute(mRegisterTask, rg_email.getText().toString(), rg_password.getText().toString(), rg_first_name.getText().toString(),
                                    rg_last_name.getText().toString(), finalimagepath, "");
                        }else {
                            CommonClass.ShowToast(RegisterActivity.this, "Please accept terms and conditions.");
                        }
                    }
                }
                else
                {
                    CommonClass.ShowToast(getApplicationContext(), getResources().getString(R.string.check_internet));
                }
            }
        });
        ivBack = (ImageView) findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                intent.putExtra("ride_id", ride_id);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                finish();
            }
        });
        chkTermsCondition = (com.rey.material.widget.CheckBox) findViewById(R.id.chkTrems);
        chkTermsCondition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkTermsCondition.isChecked())
                    chkTermsCondition.setChecked(true);
                else
                    chkTermsCondition.setChecked(false);
            }
        });
        txtTermsCondition = (TextView) findViewById(R.id.txtTermsCondition);
        txtTermsCondition.setText(Html.fromHtml("<u>Terms and Conditions.</u>"));
        txtTermsCondition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, TermConditionActiivty.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
            }
        });
    }


    RegisterTask.Responder mResponder = new RegisterTask.Responder() {

        @Override
        public void onComplete(boolean result, String message) {

//            Log.e("Status Register", "" + result);
            if (result) {
                CommonClass.ShowToast(getApplicationContext(), message);
                Intent intent = new Intent(getApplicationContext(), NavigationDrawerActivity.class);
                intent.setPackage(RegisterActivity.this.getPackageName());
                intent.putExtra("ride_id", ride_id);
                intent.putExtra("notification", "");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
//                overridePendingTransition(R.anim.fb_slide_in_from_right, R.anim.fb_forward);
                finish();
            } else {
                CommonClass.ShowToast(RegisterActivity.this, message);
            }

        }
    };


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
            // cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, cropUri);

            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent,
                    CROP_FROM_CAMERA);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK
                && null != data) {

            mImageCaptureUri = data.getData();
            performCrop(mImageCaptureUri);

        } else if (requestCode == CROP_FROM_CAMERA && resultCode == RESULT_OK) {

            finalimagepath = cropUri.getPath();
            getBitmap(cropUri);
            imageViewProfile.setImageBitmap(getBitmap(cropUri));
        } else if (requestCode == CROP_FROM_CAMERA && resultCode == RESULT_CANCELED) {

            finalimagepath = "";


        } else if (requestCode == CAMERA_CODE
                && resultCode == Activity.RESULT_OK) {
            performCrop(cameraUri);
        }

    }

    private Bitmap getBitmap(Uri path) {

        ContentResolver mContentResolver = getContentResolver();
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        intent.putExtra("ride_id", ride_id);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
        finish();
        super.onBackPressed();
    }

    public boolean ValidateTask(String strFirstName2, String strLastName2,
                                String strEmail2, String strPassword2, String strConfirmPassword2) {
        if (strFirstName2.trim().length() > 0) {
            if (strLastName2.trim().length() > 0) {
                if (strEmail2.trim().length() > 0) {
                    if (TextValidator.isAValidEmail(strEmail2)) {
                        String[] domain = {".aero", ".asia", ".biz",
                                ".cat", ".com", ".coop", ".edu",
                                ".gov", ".info", ".int", ".jobs",
                                ".mil", ".mobi", ".museum", ".name",
                                ".net", ".org", ".pro", ".tel",
                                ".travel", ".ac", ".ad", ".ae", ".af",
                                ".ag", ".ai", ".al", ".am", ".an",
                                ".ao", ".aq", ".ar", ".as", ".at",
                                ".au", ".aw", ".ax", ".az", ".ba",
                                ".bb", ".bd", ".be", ".bf", ".bg",
                                ".bh", ".bi", ".bj", ".bm", ".bn",
                                ".bo", ".br", ".bs", ".bt", ".bv",
                                ".bw", ".by", ".bz", ".ca", ".cc",
                                ".cd", ".cf", ".cg", ".ch", ".ci",
                                ".ck", ".cl", ".cm", ".cn", ".co",
                                ".cr", ".cu", ".cv", ".cx", ".cy",
                                ".cz", ".de", ".dj", ".dk", ".dm",
                                ".do", ".dz", ".ec", ".ee", ".eg",
                                ".er", ".es", ".et", ".eu", ".fi",
                                ".fj", ".fk", ".fm", ".fo", ".fr",
                                ".ga", ".gb", ".gd", ".ge", ".gf",
                                ".gg", ".gh", ".gi", ".gl", ".gm",
                                ".gn", ".gp", ".gq", ".gr", ".gs",
                                ".gt", ".gu", ".gw", ".gy", ".hk",
                                ".hm", ".hn", ".hr", ".ht", ".hu",
                                ".id", ".ie", " No", ".il", ".im",
                                ".in", ".io", ".iq", ".ir", ".is",
                                ".it", ".je", ".jm", ".jo", ".jp",
                                ".ke", ".kg", ".kh", ".ki", ".km",
                                ".kn", ".kp", ".kr", ".kw", ".ky",
                                ".kz", ".la", ".lb", ".lc", ".li",
                                ".lk", ".lr", ".ls", ".lt", ".lu",
                                ".lv", ".ly", ".ma", ".mc", ".md",
                                ".me", ".mg", ".mh", ".mk", ".ml",
                                ".mm", ".mn", ".mo", ".mp", ".mq",
                                ".mr", ".ms", ".mt", ".mu", ".mv",
                                ".mw", ".mx", ".my", ".mz", ".na",
                                ".nc", ".ne", ".nf", ".ng", ".ni",
                                ".nl", ".no", ".np", ".nr", ".nu",
                                ".nz", ".om", ".pa", ".pe", ".pf",
                                ".pg", ".ph", ".pk", ".pl", ".pm",
                                ".pn", ".pr", ".ps", ".pt", ".pw",
                                ".py", ".qa", ".re", ".ro", ".rs",
                                ".ru", ".rw", ".sa", ".sb", ".sc",
                                ".sd", ".se", ".sg", ".sh", ".si",
                                ".sj", ".sk", ".sl", ".sm", ".sn",
                                ".so", ".sr", ".st", ".su", ".sv",
                                ".sy", ".sz", ".tc", ".td", ".tf",
                                ".tg", ".th", ".tj", ".tk", ".tl",
                                ".tm", ".tn", ".to", ".tp", ".tr",
                                ".tt", ".tv", ".tw", ".tz", ".ua",
                                ".ug", ".uk", ".us", ".uy", ".uz",
                                ".va", ".vc", ".ve", ".vg", ".vi",
                                ".vn", ".vu", ".wf", ".ws", ".ye",
                                ".yt", ".za", ".zm", ".zw"};
                        List<String> list = Arrays.asList(domain);
                        String tmp = strEmail2.substring(
                                strEmail2.lastIndexOf("."), strEmail2.length());
                        if (list.contains("" + tmp)) {

                            if (strPassword2.trim().length() > 0) {

                                if (strPassword2.trim().length() >= 6) {

                                    if (strConfirmPassword2.trim().length() > 0) {
                                        if (strPassword2.equals(strConfirmPassword2)) {
                                            return true;
                                        } else {
                                            CommonClass.ShowToast(RegisterActivity.this, "Password and confirm password do not match.");
                                            return false;
                                        }
                                    } else {
                                        CommonClass.ShowToast(RegisterActivity.this, "Password and confirm password do not match.");
                                        return false;
                                    }
                                } else {
                                    CommonClass.ShowToast(RegisterActivity.this, "Password should contain minimum 6 characters.");
                                    return false;
                                }
                            } else {
                                CommonClass.ShowToast(RegisterActivity.this, "Please enter your password.");
                                return false;
                            }
                        } else {
                            CommonClass.ShowToast(RegisterActivity.this, "Please enter valid email address.");
                            return false;

                        }
                    } else {
                        CommonClass.ShowToast(RegisterActivity.this, "Please enter valid email address.");
                        return false;
                    }
                } else {
                    CommonClass.ShowToast(RegisterActivity.this, "Please enter email address.");
                    return false;
                }
            } else {
                CommonClass.ShowToast(RegisterActivity.this, "Please enter last name.");
                return false;
            }
        } else {
            CommonClass.ShowToast(RegisterActivity.this, "Please enter first name.");
            return false;
        }
    }

    public void selectPhoto() {
        CommonClass.closeKeyboard(RegisterActivity.this);
//                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                dialog = new Dialog(RegisterActivity.this, R.style.AppCompatAlertDialogStyle);
        dialog = new Dialog(RegisterActivity.this, android.R.style.Theme_Translucent);
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
                PackageManager pm = RegisterActivity.this.getPackageManager();
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
                    Toast.makeText(RegisterActivity.this,
                            "Device has no camera!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        buttonGallery.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialog.dismiss();
                Intent intent = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(intent, GALLERY_CODE);

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Register Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.app.android.cync/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Register Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.app.android.cync/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
