package com.aaptrix.activitys.admin;

import com.aaptrix.activitys.student.ActivitiesActivity;
import com.aaptrix.adaptor.GridImageAdapter;
import com.aaptrix.R;
import com.aaptrix.tools.FileUtil;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.conn.ssl.SSLConnectionSocketFactory;
import cz.msebera.android.httpclient.conn.ssl.SSLContexts;
import cz.msebera.android.httpclient.entity.mime.HttpMultipartMode;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;
import cz.msebera.android.httpclient.entity.mime.content.FileBody;
import cz.msebera.android.httpclient.impl.client.HttpClients;
import cz.msebera.android.httpclient.util.EntityUtils;
import id.zelory.compressor.Compressor;
import pl.droidsonroids.gif.GifImageView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import javax.net.ssl.SSLContext;

import static com.aaptrix.tools.SPClass.PREFS_DAIRY;
import static com.aaptrix.tools.HttpUrl.ADD_ACTIVITY;
import static com.aaptrix.tools.HttpUrl.REMOVE_ACTIVITY;
import static com.aaptrix.tools.HttpUrl.UPDATE_ACTIVITY;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREF_COLOR;
import static cz.msebera.android.httpclient.conn.ssl.SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER;

public class AddNewActivity extends AppCompatActivity {

    ArrayList<File> imageArray = new ArrayList<>();
    String file_extn;
    ArrayList<String> filepath = new ArrayList<>();
    ArrayList<Uri> image = new ArrayList<>();
    GridView gridView;
    ImageButton imageView;
    RelativeLayout layout;
    Toolbar toolbar;
    AppBarLayout appBarLayout;
    EditText title, description, date;
    ProgressBar progressBar;
    Button save, delete;
    String type, strTitle, strDesc, strDate, strImage, strId;
    Uri addImageUri = Uri.parse("android.resource://com.aaptrix/drawable/add_image");
    InputStream stream;
    String selToolColor, selStatusColor, selTextColor1, userSchoolId, userId;
    String[] oldImage;
    ArrayList<String> sendOldImage = new ArrayList<>();
    MediaPlayer mp;
    CardView cardView;
    GifImageView taskStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new);
        gridView = findViewById(R.id.choose_img_grid);
        imageView = findViewById(R.id.sel_image);
        toolbar = findViewById(R.id.toolbar);
        appBarLayout = findViewById(R.id.appBarLayout);
        setSupportActionBar(toolbar);
        setTitle("Add Announcement");
        setResult(RESULT_OK);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        type = getIntent().getStringExtra("type");
        mp = MediaPlayer.create(this, R.raw.button_click);
        cardView = findViewById(R.id.card_view);
        taskStatus = findViewById(R.id.task_status);
        layout = findViewById(R.id.layout);

        title = findViewById(R.id.tv_achive_title);
        description = findViewById(R.id.tv_achive_desc);
        date = findViewById(R.id.tv_achive_date);
        progressBar = findViewById(R.id.loader);
        save = findViewById(R.id.save_btn);
        delete = findViewById(R.id.remove_btn);
        date.setFocusable(false);

        title.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus)
                hideKeyboard(v);
        });

        description.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus)
                hideKeyboard(v);
        });

        date.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus)
                hideKeyboard(v);
        });

        SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
        selToolColor = settingsColor.getString("tool", "");
        selStatusColor = settingsColor.getString("status", "");
        selTextColor1 = settingsColor.getString("text1", "");

        SharedPreferences sp_user = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        userSchoolId = sp_user.getString("str_school_id", "");
        userId = sp_user.getString("userID", "");
        appBarLayout.setBackgroundColor(Color.parseColor(selToolColor));
        save.setBackgroundColor(Color.parseColor(selToolColor));
        delete.setBackgroundColor(Color.parseColor(selToolColor));
        save.setTextColor(Color.parseColor(selTextColor1));
        delete.setTextColor(Color.parseColor(selTextColor1));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(selStatusColor));
        }

        try {
            stream = getContentResolver().openInputStream(addImageUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        layout.setOnClickListener(v -> {

        });

        layout.setOnTouchListener((v, event) -> false);

        if (type.equals("update")) {
            setTitle("Update Announcement");
            strTitle = getIntent().getStringExtra("title");
            strDesc = getIntent().getStringExtra("description");
            strDate = getIntent().getStringExtra("date");
            strImage = getIntent().getStringExtra("image");
            strId = getIntent().getStringExtra("id");

            title.setText(strTitle);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                description.setText(Html.fromHtml(strDesc, Html.FROM_HTML_MODE_COMPACT));
            } else {
                description.setText(Html.fromHtml(strDesc));
            }
            date.setText(strDate);
            save.setText("Update");
            delete.setVisibility(View.VISIBLE);

            oldImage = strImage.split(",");
            for (String anOldImage : oldImage) {
                sendOldImage.add(anOldImage.replace("\"", "").replace("[", "").replace("]", ""));
            }

            for (int i = 0; i < sendOldImage.size(); i++) {
                Uri uri = Uri.parse(sp_user.getString("imageUrl", "") + sp_user.getString("userSchoolId", "") + "/activities/" + sendOldImage.get(i));
                image.add(uri);
            }
            if (image.size() != 4) {
                image.add(addImageUri);
            }
            GridImageAdapter addAdapter = new GridImageAdapter(this, R.layout.image_add_grid, image);
            gridView.setAdapter(addAdapter);
            imageView.setVisibility(View.GONE);
            addAdapter.notifyDataSetChanged();
        }

        imageView.bringToFront();
        imageView.setOnClickListener(v -> {
            if (PermissionChecker.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED) {
                Intent photoPickerIntent = new Intent();
                photoPickerIntent.setType("image/*");
                photoPickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                photoPickerIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(photoPickerIntent, 1);
            } else {
                isPermissionGranted();
            }
        });

        gridView.setOnItemClickListener((parent, view, position, id) -> {
            image.remove(addImageUri);
            if (PermissionChecker.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED) {
                Intent photoPickerIntent = new Intent();
                photoPickerIntent.setType("image/*");
                photoPickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                photoPickerIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(photoPickerIntent, 1);
            } else {
                isPermissionGranted();
            }
        });

        date.setOnClickListener(v -> {
            final Calendar mcurrentDate = Calendar.getInstance();
            int mYear = mcurrentDate.get(Calendar.YEAR);
            int mMonth = mcurrentDate.get(Calendar.MONTH);
            int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog mDatePicker = new DatePickerDialog(
                    this, R.style.AlertDialogCustom1, (datepicker, selectedyear, selectedmonth, selectedday) -> {
                mcurrentDate.set(Calendar.YEAR, selectedyear);
                mcurrentDate.set(Calendar.MONTH, selectedmonth);
                mcurrentDate.set(Calendar.DAY_OF_MONTH,
                        selectedday);
                SimpleDateFormat sdf = new SimpleDateFormat(
                        getResources().getString(
                                R.string.date_card_formate_dairy5),
                        Locale.US);
                date.setText(sdf.format(mcurrentDate
                        .getTime()));
            }, mYear, mMonth, mDay);
            mDatePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            mDatePicker.show();
        });

        save.setOnClickListener(v -> {
            mp.start();
            if (!TextUtils.isEmpty(title.getText().toString())) {
                if (!TextUtils.isEmpty(description.getText().toString())) {
                    if (!TextUtils.isEmpty(date.getText().toString())) {
                        layout.setVisibility(View.VISIBLE);
                        layout.bringToFront();
                        if (type.equals("add")) {
                            SharedPreferences sp = getSharedPreferences(PREFS_DAIRY, 0);
                            String studentArray = sp.getString("studentArray", "");
                            UploadActivity uploadActivity = new UploadActivity(this);
                            uploadActivity.execute(userSchoolId, title.getText().toString(),
                                    description.getText().toString(), date.getText().toString(), studentArray, sp_user.getString("userID", ""));
                        } else {
                            UpdateActivity updateActivity = new UpdateActivity(this);
                            updateActivity.execute(userId, title.getText().toString(),
                                    description.getText().toString(), date.getText().toString(), strId, sp_user.getString("userSection", ""));
                        }
                    } else {
                        Toast.makeText(this, "Please enter Date", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Please enter Description", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please enter Title", Toast.LENGTH_SHORT).show();
            }
        });

        delete.setOnClickListener(v -> {
            mp.start();
            AlertDialog.Builder alert = new AlertDialog.Builder(this, R.style.DialogTheme);
            alert.setTitle("Are you sure you want to delete").setPositiveButton("Yes", (dialog, which) -> {
                RemoveActivity removeActivity = new RemoveActivity(this);
                removeActivity.execute(userSchoolId, strId);
            }).setNegativeButton("No", null);
            AlertDialog alertDialog = alert.create();
            alertDialog.setCancelable(false);
            alertDialog.show();
            Button theButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            Button theButton1 = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            theButton.setTextColor(Color.parseColor(selToolColor));
            theButton1.setTextColor(Color.parseColor(selToolColor));
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mp.release();
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1)
            if (resultCode == Activity.RESULT_OK) {
                ClipData clipData = data.getClipData();
                if (clipData != null) {
                    int size = 4;
                    if (type.equals("update")) {
                        if (sendOldImage.size() == 1) {
                            size = 3;
                        } else if (sendOldImage.size() == 2) {
                            size = 2;
                        } else if (sendOldImage.size() == 3) {
                            size = 1;
                        }
                    }
                    if (filepath.size() > size) {
                        Toast.makeText(this, "Please select upto only " + size + " images", Toast.LENGTH_SHORT).show();
                    } else if (clipData.getItemCount() > size) {
                        Toast.makeText(this, "Please select upto only " + size + " images", Toast.LENGTH_SHORT).show();
                    } else {
                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            image.add(clipData.getItemAt(i).getUri());
                        }
                        for (int i = 0; i < image.size(); i++) {
                            filepath.add(FileUtil.getFileName(this, image.get(i)));
                            file_extn = filepath.get(i).substring(filepath.get(i).lastIndexOf(".") + 1);
                            try {
                                if (file_extn.equals("jpg") || file_extn.equals("jpeg") || file_extn.equals("png")) {
                                    imageArray.add(new Compressor(this)
                                            .setMaxWidth(1280)
                                            .setMaxHeight(720)
                                            .setQuality(25)
                                            .setCompressFormat(Bitmap.CompressFormat.JPEG)
                                            .compressToFile(FileUtil.from(this, image.get(i))));
                                } else {
                                    FileNotFoundException fe = new FileNotFoundException();
                                    Toast.makeText(this, "File not in required format.", Toast.LENGTH_SHORT).show();
                                    throw fe;
                                }
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                        if (image.size() != 4) {
                            image.add(addImageUri);
                        }
                        GridImageAdapter addAdapter = new GridImageAdapter(this, R.layout.image_add_grid, image);
                        gridView.setAdapter(addAdapter);
                        imageView.setVisibility(View.GONE);
                        addAdapter.notifyDataSetChanged();
                    }
                } else {
                    if (filepath.size() > 4) {
                        Toast.makeText(this, "Please select upto only 4 images", Toast.LENGTH_SHORT).show();
                    } else {
                        image.add(data.getData());
                        for (int i = 0; i < image.size(); i++) {
                            filepath.add(FileUtil.getFileName(this, image.get(i)));
                            file_extn = filepath.get(i).substring(filepath.get(i).lastIndexOf(".") + 1);
                            try {
                                if (file_extn.equals("jpg") || file_extn.equals("jpeg") || file_extn.equals("png")) {
                                    imageArray.add(new Compressor(this)
                                            .setMaxWidth(1280)
                                            .setMaxHeight(720)
                                            .setQuality(25)
                                            .setCompressFormat(Bitmap.CompressFormat.JPEG)
                                            .compressToFile(FileUtil.from(this, image.get(i))));
                                } else {
                                    FileNotFoundException fe = new FileNotFoundException();
                                    Toast.makeText(this, "File not in required format.", Toast.LENGTH_SHORT).show();
                                    throw fe;
                                }
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                        if (image.size() != 4) {
                            image.add(addImageUri);
                        }
                        GridImageAdapter addAdapter = new GridImageAdapter(this, R.layout.image_add_grid, image);
                        gridView.setAdapter(addAdapter);
                        imageView.setVisibility(View.GONE);
                        addAdapter.notifyDataSetChanged();
                    }
                }
            }
    }

    public void isPermissionGranted() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class UploadActivity extends AsyncTask<String, String, String> {
        Context ctx;

        UploadActivity(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            Toast.makeText(ctx, "Please wait, adding announcement entry", Toast.LENGTH_SHORT).show();
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {

            String schoolId = params[0];
            String str_title = params[1];
            String str_desc = params[2];
            String str_date = params[3];
            String userSection = params[4];
            String userId = params[5];
            String notiImage = "0";

            try {
                ArrayList<String> imageNames = new ArrayList<>();
                for (int i = 0; i < imageArray.size(); i++) {
                    try {
                        SSLContext sslContext = SSLContexts.custom().useTLS().build();
                        SSLConnectionSocketFactory f = new SSLConnectionSocketFactory(
                                sslContext,
                                new String[]{"TLSv1.1", "TLSv1.2"},
                                null,
                                BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
                        HttpClient httpclient = HttpClients.custom().setSSLSocketFactory(f).build();
                        HttpPost httppost = new HttpPost(ADD_ACTIVITY);
                        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
                        entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                        FileBody image = new FileBody(imageArray.get(i));
                        entityBuilder.addPart("image", image);
                        entityBuilder.addTextBody("schoolId", schoolId);
                        HttpEntity entity = entityBuilder.build();
                        httppost.setEntity(entity);
                        HttpResponse response = httpclient.execute(httppost);
                        HttpEntity httpEntity = response.getEntity();
                        String result = EntityUtils.toString(httpEntity);
                        JSONObject jsonObject = new JSONObject(result);
                        imageNames.add("\"" + jsonObject.getString("imageNm") + "\"");
                        if (i == 0)
                            notiImage = jsonObject.getString("imageNm");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                try {
                    SSLContext sslContext = SSLContexts.custom().useTLS().build();
                    SSLConnectionSocketFactory f = new SSLConnectionSocketFactory(
                            sslContext,
                            new String[]{"TLSv1.1", "TLSv1.2"},
                            null,
                            BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
                    HttpClient httpclient = HttpClients.custom().setSSLSocketFactory(f).build();
                    HttpPost httppost = new HttpPost(ADD_ACTIVITY);
                    MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
                    entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                    entityBuilder.addTextBody("str_img", imageNames.toString().replace(" ", ""));
                    entityBuilder.addTextBody("schoolId", schoolId);
                    entityBuilder.addTextBody("str_title", str_title);
                    entityBuilder.addTextBody("str_desc", str_desc);
                    entityBuilder.addTextBody("str_date", str_date);
                    entityBuilder.addTextBody("userSection", userSection);
                    entityBuilder.addTextBody("userId", userId);
                    entityBuilder.addTextBody("noti_image", notiImage);
                    HttpEntity entity = entityBuilder.build();
                    httppost.setEntity(entity);
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity httpEntity = response.getEntity();
                    return EntityUtils.toString(httpEntity);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("ADDED", "" + result);
            if (result.contains("submitted")) {
                cardView.setVisibility(View.VISIBLE);
                new CountDownTimer(4000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        Intent i = new Intent(AddNewActivity.this, ActivitiesActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    }
                }.start();
            } else {
                Toast.makeText(ctx, "Issue in server", Toast.LENGTH_SHORT).show();
                layout.setVisibility(View.GONE);
            }
            super.onPostExecute(result);
        }

    }

    @SuppressLint("StaticFieldLeak")
    public class UpdateActivity extends AsyncTask<String, String, String> {
        Context ctx;

        UpdateActivity(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            String userId = params[0];
            String str_title = params[1];
            String str_desc = params[2];
            String str_date = params[3];
            String achivId = params[4];
            String userSection = params[5];

            try {
                ArrayList<String> imageNames = new ArrayList<>();
                for (int i = 0; i < imageArray.size(); i++) {
                    try {
                        SSLContext sslContext = SSLContexts.custom().useTLS().build();
                        SSLConnectionSocketFactory f = new SSLConnectionSocketFactory(
                                sslContext,
                                new String[]{"TLSv1.1", "TLSv1.2"},
                                null,
                                BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
                        HttpClient httpclient = HttpClients.custom().setSSLSocketFactory(f).build();
                        HttpPost httppost = new HttpPost(UPDATE_ACTIVITY);
                        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
                        entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                        FileBody image = new FileBody(imageArray.get(i));
                        entityBuilder.addPart("image", image);
                        entityBuilder.addTextBody("schoolId", userSchoolId);
                        HttpEntity entity = entityBuilder.build();
                        httppost.setEntity(entity);
                        HttpResponse response = httpclient.execute(httppost);
                        HttpEntity httpEntity = response.getEntity();
                        String result = EntityUtils.toString(httpEntity);
                        JSONObject jsonObject = new JSONObject(result);
                        imageNames.add("\"" + jsonObject.getString("imageNm") + "\"");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                for (String aSendOldImage : sendOldImage) {
                    imageNames.add("\"" + aSendOldImage + "\"");
                }

                try {
                    SSLContext sslContext = SSLContexts.custom().useTLS().build();
                    SSLConnectionSocketFactory f = new SSLConnectionSocketFactory(
                            sslContext,
                            new String[]{"TLSv1.1", "TLSv1.2"},
                            null,
                            BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
                    HttpClient httpclient = HttpClients.custom().setSSLSocketFactory(f).build();
                    HttpPost httppost = new HttpPost(UPDATE_ACTIVITY);
                    MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
                    entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                    entityBuilder.addTextBody("str_img", imageNames.toString().replace(" ", ""));
                    entityBuilder.addTextBody("userId", userId);
                    entityBuilder.addTextBody("str_title", str_title);
                    entityBuilder.addTextBody("str_desc", str_desc);
                    entityBuilder.addTextBody("str_date", str_date);
                    entityBuilder.addTextBody("userSection", userSection);
                    entityBuilder.addTextBody("actiId", achivId);
                    HttpEntity entity = entityBuilder.build();
                    httppost.setEntity(entity);
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity httpEntity = response.getEntity();
                    String res = EntityUtils.toString(httpEntity);
                    return res;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e("Json", "" + result);
            if (result.contains("submitted")) {
                cardView.setVisibility(View.VISIBLE);
                new CountDownTimer(4000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        startActivity(new Intent(AddNewActivity.this, InstituteBuzzActivityDiff.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    }
                }.start();

            } else {
                layout.setVisibility(View.GONE);
                Toast.makeText(ctx, "Server issues", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(result);
        }

    }

    //remove
    @SuppressLint("StaticFieldLeak")
    public class RemoveActivity extends AsyncTask<String, String, String> {
        Context ctx;

        RemoveActivity(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            String userId = params[0];
            String achievId = params[1];
            String data;

            try {

                URL url = new URL(REMOVE_ACTIVITY);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                data = URLEncoder.encode("userId", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8") + "&" +
                        URLEncoder.encode("achievId", "UTF-8") + "=" + URLEncoder.encode(achievId, "UTF-8");
                outputStream.write(data.getBytes());

                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.flush();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    response.append(line);
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return response.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("Json", "" + result);
            progressBar.setVisibility(View.GONE);
            if (!result.isEmpty()) {
                startActivity(new Intent(AddNewActivity.this, InstituteBuzzActivityDiff.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            } else {
                Toast.makeText(ctx, "Some Error", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
