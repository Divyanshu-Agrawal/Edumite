package com.aaptrix.activitys.student;

import com.aaptrix.activitys.admin.AddNewVideo;
import com.aaptrix.adaptor.VideoAdapter;
import com.aaptrix.databeans.VideosData;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import com.aaptrix.R;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import static com.aaptrix.tools.HttpUrl.ALL_BATCHS;
import static com.aaptrix.tools.HttpUrl.ALL_VIDEOS;
import static com.aaptrix.tools.HttpUrl.GET_SUBS;
import static com.aaptrix.tools.HttpUrl.REMOVE_VIDEOS;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREFS_RW;
import static com.aaptrix.tools.SPClass.PREF_COLOR;

public class VideoLibrary extends AppCompatActivity {

    ListView listView;
    VideoAdapter videoAdapter;
    ArrayList<VideosData> videosArray = new ArrayList<>(), array = new ArrayList<>();
    VideosData videosData;
    AppBarLayout appBarLayout;
    String selToolColor, selStatusColor, selTextColor1;
    TextView tool_title, noVideos;
    SharedPreferences sp;
    LinearLayout add_layout, search_layout;
    String[] subjects;
    ArrayList<String> subject_array = new ArrayList<>();
    ImageView addVideo;
    String[] batch_array = {"All Batches"};
    Spinner batch_spinner;
    String selBatch = "All";
    EditText searchBox;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    String userId, userSchoolId, userRoleId, userrType, userSection, url, userName, restricted;
    ImageButton filter, search, searchBtn, offline;
    private String selSubject = "All Subjects", disable;
    LinearLayout liveVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_library);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        appBarLayout = findViewById(R.id.appBarLayout);
        tool_title = findViewById(R.id.tool_title);
        listView = findViewById(R.id.video_listview);
        noVideos = findViewById(R.id.no_videos);
        mSwipeRefreshLayout = findViewById(R.id.activity_main_swipe_refresh_layout);
        add_layout = findViewById(R.id.add_layout);
        addVideo = findViewById(R.id.add_video);
        filter = findViewById(R.id.filter);
        batch_spinner = findViewById(R.id.batch_spinner);
        liveVideo = findViewById(R.id.live_video);
        mSwipeRefreshLayout.setRefreshing(false);
        listView.setEnabled(true);
        search = findViewById(R.id.search);
        search_layout = findViewById(R.id.search_layout);
        searchBox = findViewById(R.id.search_txt);
        searchBtn = findViewById(R.id.search_btn);
        offline = findViewById(R.id.offline_videos);

        selSubject = getIntent().getStringExtra("sub");

        offline.setOnClickListener(v -> {
            if (PermissionChecker.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED &&
                    PermissionChecker.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED) {
                isPermissionGranted();
            } else {
                Intent intent = new Intent(this, OfflineVideos.class);
                intent.putExtra("sub", selSubject);
                startActivity(intent);
            }
        });

        SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
        selToolColor = settingsColor.getString("tool", "");
        selStatusColor = settingsColor.getString("status", "");
        selTextColor1 = settingsColor.getString("text1", "");

        sp = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        userSchoolId = sp.getString("str_school_id", "");
        userSection = sp.getString("userSection", "");
        userId = sp.getString("userID", "");
        userRoleId = sp.getString("str_role_id", "");
        userrType = sp.getString("userrType", "");
        userName = sp.getString("userName", "");
        restricted = sp.getString("restricted", "");

        url = sp.getString("imageUrl", "") + userSchoolId + "/InstituteVideo/";

        appBarLayout.setBackgroundColor(Color.parseColor(selToolColor));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(selStatusColor));
        }
        GradientDrawable bgShape = (GradientDrawable) add_layout.getBackground();
        bgShape.setColor(Color.parseColor(selToolColor));
        tool_title.setTextColor(Color.parseColor(selTextColor1));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            filter.setImageTintList(ColorStateList.valueOf(Color.parseColor(selTextColor1)));
        }
        addVideo.setBackgroundColor(Color.parseColor(selToolColor));

        if (userrType.equals("Guest")) {
            batch_spinner.setVisibility(View.GONE);
            filter.setVisibility(View.GONE);
        }

        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<>(this, R.layout.spinner_list_item1, new String[]{"All Batches"});
        dataAdapter1.setDropDownViewResource(R.layout.spinner_list_item1);
        batch_spinner.setAdapter(dataAdapter1);

        if (userrType.equals("Admin") || userrType.equals("Teacher")) {
            GetAllBatches b1 = new GetAllBatches(this);
            b1.execute(userSchoolId);
        }

        SharedPreferences preferences = getSharedPreferences(PREFS_RW, 0);
        String json = preferences.getString("result", "");

        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("result");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                if (object.getString("tbl_insti_buzz_cate_name").equals("Study Videos")) {
                    if (object.getString("tbl_scl_inst_buzz_detl_write_status").equals("Active")) {
                        add_layout.setVisibility(View.VISIBLE);
                    } else {
                        add_layout.setVisibility(View.GONE);
                    }
                }
                if (object.getString("tbl_insti_buzz_cate_name").equals("Video Live Streaming")) {
                    if (object.getString("tbl_scl_inst_buzz_detl_status").equals("Active")) {
                        liveVideo.setVisibility(View.VISIBLE);
                    } else {
                        liveVideo.setVisibility(View.GONE);
                    }
                }
                if (object.getString("tbl_insti_buzz_cate_name").equals("Downloadable Videos")) {
                    if (object.getString("tbl_scl_inst_buzz_detl_status").equals("Active")) {
                        offline.setVisibility(View.VISIBLE);
                    } else {
                        offline.setVisibility(View.GONE);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (selSubject.equals("All Subjects")) {
            offline.setVisibility(View.GONE);
        }

        if (userrType.equals("Student") || userrType.equals("Parent")) {
            liveVideo.setVisibility(View.GONE);
            tool_title.setText(selSubject);
        }

        liveVideo.setOnClickListener(v -> {
            Intent intent = new Intent(this, LiveStreaming.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        addVideo.setOnClickListener(view -> {
            if (isInternetOn()) {
                Intent intent = new Intent(this, AddNewVideo.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            } else {
                Toast.makeText(this, "No network Please connect with network", Toast.LENGTH_SHORT).show();
            }
        });

        if (!userrType.equals("Student")) {
            GetSubject subject = new GetSubject(this);
            subject.execute(userSchoolId, "All");
        } else {
            filter.setVisibility(View.GONE);
        }

        filter.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(this, v);
            popupMenu.getMenuInflater().inflate(R.menu.filter_menu, popupMenu.getMenu());
            if (subject_array.size() != 0) {
                for (int i = 0; i < subject_array.size(); i++) {
                    popupMenu.getMenu().add(1, i, i, subject_array.get(i));
                }
                popupMenu.show();
            } else {
                Toast.makeText(this, "Nothing to show", Toast.LENGTH_SHORT).show();
            }

            popupMenu.setOnMenuItemClickListener(item1 -> {
                selSubject = item1.getTitle().toString();
                listItems(selSubject, disable);
                return true;
            });
        });

        if (isInternetOn()) {
            GetVideos getVideos = new GetVideos(this);
            if (userrType.equals("Student")) {
                getVideos.execute(userSchoolId, userSection, userrType);
                batch_spinner.setVisibility(View.GONE);
            } else {
                getVideos.execute(userSchoolId, "All", userrType);
            }
        } else {
            Toast.makeText(this, "Please connect to internet", Toast.LENGTH_SHORT).show();
        }

        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            if (isInternetOn()) {
                mSwipeRefreshLayout.setRefreshing(true);
                listView.setEnabled(false);
                array.clear();
                videosArray.clear();
                GetVideos getVideos = new GetVideos(this);
                if (userrType.equals("Student")) {
                    getVideos.execute(userSchoolId, userSection, userrType);
                    selBatch = userSection;
                    batch_spinner.setVisibility(View.GONE);
                } else {
                    getVideos.execute(userSchoolId, selBatch, userrType);
                }
            } else {
                Toast.makeText(this, "No network Please connect with network for update", Toast.LENGTH_SHORT).show();
                mSwipeRefreshLayout.setRefreshing(false);
                listView.setEnabled(true);
            }
        });
    }

    public void isPermissionGranted() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @SuppressLint("StaticFieldLeak")
    public class GetAllBatches extends AsyncTask<String, String, String> {
        Context ctx;

        GetAllBatches(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            String school_id = params[0];
            String data;

            try {

                URL url = new URL(ALL_BATCHS);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                data = URLEncoder.encode("school_id", "UTF-8") + "=" + URLEncoder.encode(school_id, "UTF-8") + "&" +
                        URLEncoder.encode("tbl_users_id", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8");
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
            if (!result.equals("{\"result\":null}")) {
                try {
                    JSONObject jo = new JSONObject(result);
                    JSONArray ja = jo.getJSONArray("result");
                    batch_array = new String[ja.length() + 1];
                    selBatch = "All";
                    batch_array[0] = "All Batches";
                    for (int i = 0; i < ja.length(); i++) {
                        jo = ja.getJSONObject(i);
                        batch_array[i + 1] = jo.getString("tbl_batch_name");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setBatch();
            } else {
                String batch_array[] = {"All Batches"};
                ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<>(ctx, R.layout.spinner_list_item1, batch_array);
                dataAdapter1.setDropDownViewResource(R.layout.spinner_list_item1);
                batch_spinner.setAdapter(dataAdapter1);
                Toast.makeText(ctx, "No Batch", Toast.LENGTH_SHORT).show();
            }

            super.onPostExecute(result);
        }

    }

    private void setBatch() {
        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<>(this, R.layout.spinner_list_item1, batch_array);
        dataAdapter1.setDropDownViewResource(R.layout.spinner_list_item1);
        batch_spinner.setAdapter(dataAdapter1);
        batch_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (batch_array[i].equals("All Batches")) {
                    selBatch = "All";
                    GetVideos getVideos = new GetVideos(VideoLibrary.this);
                    getVideos.execute(userSchoolId, "All", userrType);
                    GetSubject subject = new GetSubject(VideoLibrary.this);
                    subject.execute(userSchoolId, "All");
                } else {
                    selBatch = batch_array[i];
                    GetVideos getVideos = new GetVideos(VideoLibrary.this);
                    getVideos.execute(userSchoolId, selBatch, userrType);
                    String section = "[{\"userName\":\"" + selBatch + "\"}]";
                    GetSubject subject = new GetSubject(VideoLibrary.this);
                    subject.execute(userSchoolId, section);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    public class GetVideos extends AsyncTask<String, String, String> {
        Context ctx;

        GetVideos(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            videosArray.clear();
            mSwipeRefreshLayout.setRefreshing(true);
            listView.setEnabled(false);
            array.clear();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            String schoolId = params[0];
            String sectionName = params[1];
            String userType = params[2];
            String data;

            try {

                URL url = new URL(ALL_VIDEOS);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                data = URLEncoder.encode("schoolId", "UTF-8") + "=" + URLEncoder.encode(schoolId, "UTF-8") + "&" +
                        URLEncoder.encode("userSection", "UTF-8") + "=" + URLEncoder.encode(sectionName, "UTF-8") + "&" +
                        URLEncoder.encode("userType", "UTF-8") + "=" + URLEncoder.encode(userType, "UTF-8") + "&" +
                        URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8") + "&" +
                        URLEncoder.encode("tbl_users_nm", "UTF-8") + "=" + URLEncoder.encode(userName, "UTF-8") + "&" +
                        URLEncoder.encode("restricted_access", "UTF-8") + "=" + URLEncoder.encode(restricted, "UTF-8");
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
            Log.e("res", result);
            mSwipeRefreshLayout.setRefreshing(false);
            listView.setEnabled(true);
            try {
                array.clear();
                videosArray.clear();
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
                JSONObject jsonRootObject = new JSONObject(result);
                if (!result.contains("\"result\":null")) {
                    JSONArray jsonArray = jsonRootObject.getJSONArray("result");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String end = jsonObject.getString("visible_till") + " " + jsonObject.getString("visible_till_time");
                        Date enddate = sdf.parse(end);
                        videosData = new VideosData();
                        videosData.setId(jsonObject.getString("tbl_school_studyvideo_id"));
                        videosData.setTitle(jsonObject.getString("tbl_school_studyvideo_title"));
                        videosData.setUrl(jsonObject.getString("tbl_school_studyvideo_video"));
                        videosData.setSubject(jsonObject.getString("subject_name"));
                        videosData.setTotalTime(jsonObject.getString("video_total_time"));
                        videosData.setDesc(jsonObject.getString("tbl_school_studyvideo_desc"));
                        videosData.setBatch(jsonObject.getString("tbl_stnt_prsnl_data_section"));
                        videosData.setDate(jsonObject.getString("tbl_school_studyvideo_date"));
                        videosData.setTags(jsonObject.getString("tbl_school_studyvideo_tag"));
                        videosData.setStart(jsonObject.getString("visible_start_date") + " " + jsonObject.getString("visible_start_time"));
                        videosData.setEnd(jsonObject.getString("visible_till") + " " + jsonObject.getString("visible_till_time"));
                        if (!end.equals("0000-00-00 00:00:00")) {
                            if (calendar.getTime().before(enddate)) {
                                array.add(videosData);
                            }
                        } else {
                            array.add(videosData);
                        }
                    }
                }
                if (!result.contains("\"instituteVideos\":null")) {
                    JSONArray jsonArray = jsonRootObject.getJSONArray("instituteVideos");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String end = jsonObject.getString("visible_till") + " " + jsonObject.getString("visible_till_time");
                        Date enddate = sdf.parse(end);
                        videosData = new VideosData();
                        videosData.setId(jsonObject.getString("tbl_school_institutevideo_id"));
                        videosData.setTitle(jsonObject.getString("tbl_school_institutevideo_title"));
                        videosData.setUrl(url + jsonObject.getString("tbl_school_institutevideo_video"));
                        videosData.setDesc(jsonObject.getString("tbl_school_institutevideo_desc"));
                        videosData.setSubject(jsonObject.getString("subject_name"));
                        videosData.setTotalTime(jsonObject.getString("video_total_time"));
                        videosData.setDate(jsonObject.getString("tbl_school_institutevideo_date"));
                        videosData.setBatch(jsonObject.getString("tbl_stnt_prsnl_data_section"));
                        videosData.setTags(jsonObject.getString("tbl_school_studyvideo_tag"));
                        videosData.setStart(jsonObject.getString("visible_start_date") + " " + jsonObject.getString("visible_start_time"));
                        videosData.setEnd(jsonObject.getString("visible_till") + " " + jsonObject.getString("visible_till_time"));
                        if (!end.equals("0000-00-00 00:00:00")) {
                            if (calendar.getTime().before(enddate)) {
                                array.add(videosData);
                            }
                        } else {
                            array.add(videosData);
                        }
                    }
                }
                if (userrType.equals("Student")) {
                    disable = jsonRootObject.getString("DisableSubject");
                    if (!result.contains("\"studyVideosStudent\":null")) {
                        JSONArray jsonArray = jsonRootObject.getJSONArray("studyVideosStudent");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String end = jsonObject.getString("visible_till") + " " + jsonObject.getString("visible_till_time");
                            Date enddate = sdf.parse(end);
                            videosData = new VideosData();
                            videosData.setId(jsonObject.getString("tbl_school_studyvideo_id"));
                            videosData.setTitle(jsonObject.getString("tbl_school_studyvideo_title"));
                            videosData.setUrl(jsonObject.getString("tbl_school_studyvideo_video"));
                            videosData.setSubject(jsonObject.getString("subject_name"));
                            videosData.setTotalTime(jsonObject.getString("video_total_time"));
                            videosData.setDesc(jsonObject.getString("tbl_school_studyvideo_desc"));
                            videosData.setBatch(jsonObject.getString("tbl_stnt_prsnl_data_section"));
                            videosData.setDate(jsonObject.getString("tbl_school_studyvideo_date"));
                            videosData.setTags(jsonObject.getString("tbl_school_studyvideo_tag"));
                            videosData.setStart(jsonObject.getString("visible_start_date") + " " + jsonObject.getString("visible_start_time"));
                            videosData.setEnd(jsonObject.getString("visible_till") + " " + jsonObject.getString("visible_till_time"));
                            if (!end.equals("0000-00-00 00:00:00")) {
                                if (calendar.getTime().before(enddate)) {
                                    array.add(videosData);
                                }
                            } else {
                                array.add(videosData);
                            }
                        }
                    }
                    for (int i = 0; i < array.size(); i++) {
                        if (!disable.contains(array.get(i).getSubject())) {
                            videosArray.add(array.get(i));
                        }
                    }
                }
                if (userrType.equals("Teacher")) {
                    String restricted = jsonRootObject.getString("RistrictedSubject");
                    if (restricted.equals("null")) {
                        videosArray.addAll(array);
                    } else {
                        for (int i = 0; i < array.size(); i++) {
                            if (restricted.contains(array.get(i).getSubject())) {
                                videosArray.add(array.get(i));
                            }
                        }
                    }
                } else {
                    videosArray.addAll(array);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (videosArray.size() > 0) {
                listItems(selSubject, disable);
            } else {
                noVideos.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
                mSwipeRefreshLayout.setRefreshing(false);
            }
            super.onPostExecute(result);
        }
    }

    private void listItems(String subject, String disable) {

        ArrayList<VideosData> arrayList = new ArrayList<>();

        ArrayList<String> ids = new ArrayList<>();
        if (!userrType.equals("Student")) {
            if (subject.equals("All Subjects")) {
                for (int i = 0; i < videosArray.size(); i++) {
                    if (!ids.contains(videosArray.get(i).getId())) {
                        ids.add(videosArray.get(i).getId());
                    }
                }
                for (int i = 0; i < ids.size(); i++) {
                    for (int j = 0; j < videosArray.size(); j++) {
                        if (ids.get(i).equals(videosArray.get(j).getId())) {
                            arrayList.add(videosArray.get(j));
                            break;
                        }
                    }
                }
            } else {
                for (int i = 0; i < videosArray.size(); i++) {
                    if (!ids.contains(videosArray.get(i).getId())) {
                        ids.add(videosArray.get(i).getId());
                    }
                }
                for (int i = 0; i < ids.size(); i++) {
                    for (int j = 0; j < videosArray.size(); j++) {
                        if (ids.get(i).equals(videosArray.get(j).getId())) {
                            if (videosArray.get(j).getSubject().contentEquals(subject) || videosArray.get(j).getSubject().equals("0")) {
                                arrayList.add(videosArray.get(j));
                                break;
                            }
                        }
                    }
                }
            }
        } else {
            if (subject.equals("All Subjects")) {
                for (int i = 0; i < videosArray.size(); i++) {
                    if (!ids.contains(videosArray.get(i).getId())) {
                        ids.add(videosArray.get(i).getId());
                    }
                }
                for (int i = 0; i < ids.size(); i++) {
                    for (int j = 0; j < videosArray.size(); j++) {
                        if (ids.get(i).equals(videosArray.get(j).getId()) && !disable.contains(videosArray.get(j).getSubject())) {
                            arrayList.add(videosArray.get(j));
                            break;
                        }
                    }
                }
            } else {
                for (int i = 0; i < videosArray.size(); i++) {
                    if (!ids.contains(videosArray.get(i).getId())) {
                        ids.add(videosArray.get(i).getId());
                    }
                }
                for (int i = 0; i < ids.size(); i++) {
                    for (int j = 0; j < videosArray.size(); j++) {
                        if (ids.get(i).equals(videosArray.get(j).getId())) {
                            if (videosArray.get(j).getSubject().contentEquals(subject) || videosArray.get(j).getSubject().equals("0")) {
                                arrayList.add(videosArray.get(j));
                                break;
                            }
                        }
                    }
                }
            }
        }

        if (arrayList.size() > 0) {
            noVideos.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        } else {
            noVideos.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }

        Collections.sort(arrayList, (o1, o2) -> {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            try {
                return sdf.parse(o1.getDate()).compareTo(sdf.parse(o2.getDate()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return 0;
        });
        Collections.reverse(arrayList);

        searchBox.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        searchBox.setSingleLine(true);
        searchBox.setInputType(InputType.TYPE_CLASS_TEXT);
        searchBox.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (searchBox.getText().toString().isEmpty()) {
                    listView.setVisibility(View.VISIBLE);
                    noVideos.setVisibility(View.GONE);
                    videoAdapter = new VideoAdapter(VideoLibrary.this, R.layout.list_item_video, arrayList, "video");
                    listView.setAdapter(videoAdapter);
                    videoAdapter.notifyDataSetChanged();
                } else {
                    filterSearch(arrayList, searchBox.getText().toString());
                }
                hideKeyboard(v);
                return true;
            }
            return false;
        });

        search.setOnClickListener(v -> {
            if (search_layout.getVisibility() == View.VISIBLE) {
                search_layout.setVisibility(View.GONE);
                videoAdapter = new VideoAdapter(this, R.layout.list_item_video, arrayList, "video");
                listView.setAdapter(videoAdapter);
                videoAdapter.notifyDataSetChanged();
                listView.setVisibility(View.VISIBLE);
                noVideos.setVisibility(View.GONE);
                search.setImageResource(R.drawable.search_icon);
            } else {
                search_layout.setVisibility(View.VISIBLE);
                search.setImageResource(R.drawable.back_icon);
            }
        });

        searchBtn.setOnClickListener(v -> {
            if (searchBox.getText().toString().isEmpty()) {
                listView.setVisibility(View.VISIBLE);
                noVideos.setVisibility(View.GONE);
                videoAdapter = new VideoAdapter(this, R.layout.list_item_video, arrayList, "video");
                listView.setAdapter(videoAdapter);
                videoAdapter.notifyDataSetChanged();
            } else {
                filterSearch(arrayList, searchBox.getText().toString());
            }
            hideKeyboard(v);
        });

        listView.setEnabled(true);
        videoAdapter = new VideoAdapter(this, R.layout.list_item_video, arrayList, "video");
        listView.setAdapter(videoAdapter);
        videoAdapter.notifyDataSetChanged();
        mSwipeRefreshLayout.setRefreshing(false);

        SharedPreferences sp = getSharedPreferences(PREFS_RW, 0);
        String json = sp.getString("result", "");

        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("result");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                if (object.getString("tbl_insti_buzz_cate_name").equals("Study Videos")) {
                    if (object.getString("tbl_scl_inst_buzz_detl_write_status").equals("Active")) {
                        listView.setOnItemLongClickListener((arg0, arg1, pos, id) -> {
                            if (isInternetOn()) {
                                AlertDialog.Builder alert = new AlertDialog.Builder(this, R.style.DialogTheme);
                                alert.setTitle("Are you sure you want to delete").setPositiveButton("Yes", (dialog, which) -> {
                                    RemoveVideo removeVideo = new RemoveVideo(this);
                                    removeVideo.execute(userSchoolId, arrayList.get(pos).getId());
                                }).setNegativeButton("No", null);
                                AlertDialog alertDialog = alert.create();
                                alertDialog.setCancelable(false);
                                alertDialog.show();
                                Button theButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                                Button theButton1 = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                                theButton.setTextColor(Color.parseColor(selToolColor));
                                theButton1.setTextColor(Color.parseColor(selToolColor));
                            } else {
                                Toast.makeText(this, "No network Please connect with network for update", Toast.LENGTH_SHORT).show();
                            }
                            return true;
                        });
                    }
                    break;
                }
            }
        } catch (
                JSONException e) {
            e.printStackTrace();
        }
    }

    private void filterSearch(ArrayList<VideosData> array, String searchTxt) {
        ArrayList<VideosData> arrayList = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            if (array.get(i).getTitle().toLowerCase().contains(searchTxt.toLowerCase())) {
                arrayList.add(array.get(i));
            }
        }
        if (arrayList.size() == 0) {
            noVideos.setVisibility(View.VISIBLE);
            noVideos.setText("Nothing Found");
            listView.setVisibility(View.GONE);
        } else {
            listView.setEnabled(true);
            listView.setVisibility(View.VISIBLE);
            noVideos.setVisibility(View.GONE);
            videoAdapter = new VideoAdapter(this, R.layout.list_item_video, arrayList, "video");
            listView.setAdapter(videoAdapter);
            videoAdapter.notifyDataSetChanged();
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class GetSubject extends AsyncTask<String, String, String> {
        Context ctx;

        GetSubject(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            String school_id = params[0];
            String batchArray = params[1];
            String data;

            Log.e("batch", batchArray);
            Log.e("user id", userId);
            Log.e("user name", userName);
            Log.e("res", restricted);

            try {

                URL url = new URL(GET_SUBS);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                data = URLEncoder.encode("school_id", "UTF-8") + "=" + URLEncoder.encode(school_id, "UTF-8") + "&" +
                        URLEncoder.encode("batchArray", "UTF-8") + "=" + URLEncoder.encode(batchArray, "UTF-8") + "&" +
                        URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8") + "&" +
                        URLEncoder.encode("tbl_users_nm", "UTF-8") + "=" + URLEncoder.encode(userName, "UTF-8") + "&" +
                        URLEncoder.encode("restricted_access", "UTF-8") + "=" + URLEncoder.encode(restricted, "UTF-8");
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
            if (result.equals("{\"SubjectList\":null}")) {
                subject_array.add("All Subjects");
            } else {
                try {
                    subject_array.clear();
                    JSONObject jsonRootObject = new JSONObject(result);
                    JSONArray jsonArray = jsonRootObject.getJSONArray("SubjectList");
                    subjects = new String[jsonArray.length()];
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        subjects[i] = jsonObject.getString("tbl_batch_subjct_name");
                    }
                    if (userrType.equals("Student")) {
                        String object = jsonRootObject.getString("DisableSubject");
                        subject_array.add("All Subjects");
                        for (String subject : subjects) {
                            if (!object.contains(subject)) {
                                subject_array.add(subject);
                            }
                        }
                    } else if (userrType.equals("Teacher")) {
                        String object = jsonRootObject.getString("RistrictedSubject");
                        subject_array.add("All Subjects");
                        if (object.equals("null")) {
                            subject_array.addAll(Arrays.asList(subjects));
                        } else {
                            for (String subject : subjects) {
                                if (object.contains(subject)) {
                                    subject_array.add(subject);
                                }
                            }
                        }
                    } else {
                        subject_array.add("All Subjects");
                        subject_array.addAll(Arrays.asList(subjects));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                super.onPostExecute(result);
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class RemoveVideo extends AsyncTask<String, String, String> {
        Context ctx;

        RemoveVideo(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String userId = params[0];
            String videoId = params[1];
            String data;

            try {
                URL url = new URL(REMOVE_VIDEOS);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                data = URLEncoder.encode("schoolId", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8") + "&" +
                        URLEncoder.encode("studyVideoId", "UTF-8") + "=" + URLEncoder.encode(videoId, "UTF-8");
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
            if (!result.isEmpty()) {
                startActivity(new Intent(ctx, VideoLibrary.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra("sub", "All Subjects"));
            } else {
                Toast.makeText(ctx, "Some Error", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public final boolean isInternetOn() {
        ConnectivityManager connec =
                (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        assert connec != null;
        if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {
            return true;
        } else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED) {
            return false;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
