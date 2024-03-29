package com.aaptrix.activitys.student;

import com.aaptrix.R;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.leanback.widget.HorizontalGridView;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aaptrix.youtubeview.core.player.YouTubePlayer;
import com.aaptrix.youtubeview.core.player.listeners.AbstractYouTubePlayerListener;
import com.aaptrix.youtubeview.core.player.listeners.YouTubePlayerFullScreenListener;
import com.aaptrix.youtubeview.core.player.utils.YouTubePlayerTracker;
import com.aaptrix.youtubeview.core.player.views.YouTubePlayerView;
import com.aaptrix.youtubeview.core.ui.menu.YouTubePlayerMenu;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.appbar.AppBarLayout;

import org.jetbrains.annotations.NotNull;

import static com.aaptrix.activitys.SplashScreen.SCHOOL_NAME;
import static com.aaptrix.tools.HttpUrl.REMOVE_VIDEOS;
import static com.aaptrix.tools.HttpUrl.VIDEO_SEEN_TIME;
import static com.aaptrix.tools.HttpUrl.VIDEO_TOTAL_TIME;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREFS_RW;
import static com.aaptrix.tools.SPClass.PREF_COLOR;

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
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class VideoDetails extends AppCompatActivity {

    String strTitle, url, id, strDesc, strEnd, strSubject, strTags, strTime;
    TextView title, desc, endDate;
    AppBarLayout appBarLayout;
    String selToolColor, selStatusColor, selTextColor1, userrType, userSchoolId, rollNo, userId;
    TextView tool_title;
    ImageButton delete;
    boolean fullscr = false;
    PlayerView player;
    SimpleExoPlayer exoPlayer;
    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;
    private boolean mExoPlayerFullscreen = false;
    private ImageView mFullScreenIcon;
    private Dialog mFullScreenDialog;
    TextView watermark, watermark_yt;
    CountDownTimer timer = null;
    LinearLayout notice;
    ImageView dismiss;
    YouTubePlayerView youTubeView;
    YouTubePlayer ytPlayer;
    FrameLayout mainframe, yt_frame;
    RelativeLayout yt_layout, exo_layout;
    HorizontalGridView gridView;
    YouTubePlayerTracker tracker;

    @SuppressLint({"NewApi", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_video_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        appBarLayout = findViewById(R.id.appBarLayout);
        tool_title = findViewById(R.id.tool_title);
        title = findViewById(R.id.video);
        desc = findViewById(R.id.video_desc);
        player = findViewById(R.id.exo_player);
        watermark = findViewById(R.id.watermark);
        watermark.bringToFront();
        notice = findViewById(R.id.notice);
        dismiss = findViewById(R.id.dismiss);
        mainframe = findViewById(R.id.main_media_frame);
        watermark_yt = findViewById(R.id.watermark_yt);
        yt_frame = findViewById(R.id.frame_layout);
        watermark_yt.bringToFront();
        notice.bringToFront();
        yt_layout = findViewById(R.id.relative);
        exo_layout = findViewById(R.id.rel_layout);
        endDate = findViewById(R.id.end_date);
        gridView = findViewById(R.id.tags);

        LoadControl loadControl = new DefaultLoadControl.Builder()
                .setAllocator(new DefaultAllocator(true, 16))
                .setBufferDurationsMs(1500,
                        5000,
                        1500,
                        1500)
                .setTargetBufferBytes(-1)
                .setPrioritizeTimeOverSizeThresholds(true).createDefaultLoadControl();

        exoPlayer = ExoPlayerFactory.newSimpleInstance(this, new DefaultTrackSelector(), loadControl);
        exoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playbackState == Player.STATE_IDLE || playbackState == Player.STATE_ENDED ||
                        !playWhenReady) {
                    player.setKeepScreenOn(false);
                } else {
                    player.setKeepScreenOn(true);
                }
            }
        });

        dismiss.setOnClickListener(v -> notice.setVisibility(View.GONE));

        player.setPlayer(exoPlayer);
        youTubeView = findViewById(R.id.youtube_view);
        getLifecycle().addObserver(youTubeView);
        delete = findViewById(R.id.delete_video);

        strTitle = getIntent().getStringExtra("title");
        url = getIntent().getStringExtra("url");
        id = getIntent().getStringExtra("id");
        strDesc = getIntent().getStringExtra("desc");
        strEnd = getIntent().getStringExtra("endDate");
        strSubject = getIntent().getStringExtra("subject");
        strTags = getIntent().getStringExtra("tags");
        strTime = getIntent().getStringExtra("time");

        assert strTags != null;
        if (strTags.equals("null") || strTags.equals("")) {
            gridView.setVisibility(View.GONE);
        } else {
            String[] tag = strTags.replace("[", "").replace("]", "").trim().split(",");
            TagsAdapter adapter = new TagsAdapter(this, tag);
            gridView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }

        if (!strEnd.equals("0000-00-00 00:00:00")) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
                Date end = sdf.parse(strEnd);
                sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm aa", Locale.getDefault());
                endDate.setVisibility(View.VISIBLE);
                assert end != null;
                endDate.setText("Visible Till : " + sdf.format(end));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (!strDesc.equals("null")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                desc.setText(Html.fromHtml(strDesc, Html.FROM_HTML_MODE_COMPACT));
            } else {
                desc.setText(Html.fromHtml(strDesc));
            }
        } else {
            desc.setVisibility(View.GONE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            title.setText(Html.fromHtml(strTitle, Html.FROM_HTML_MODE_COMPACT));
        } else {
            title.setText(Html.fromHtml(strTitle));
        }

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        userrType = settings.getString("userrType", "");
        userSchoolId = settings.getString("userSchoolId", "");
        userId = settings.getString("userID", "");

        if (getResources().getString(R.string.watermark).equals("full")) {
            rollNo = SCHOOL_NAME + "\n" + settings.getString("userName", "") + ", " + settings.getString("userPhone", "");
        } else {
            rollNo = settings.getString("unique_id", "");
        }

        if (userrType.equals("Guest")) {
            rollNo = getResources().getString(R.string.app_name);
        }

        if (userrType.equals("Admin")) {
            delete.setVisibility(View.VISIBLE);
        } else if (userrType.equals("Teacher")) {
            SharedPreferences sp = getSharedPreferences(PREFS_RW, 0);
            if (sp.getString("Study Videos", "").equals("")) {
                delete.setVisibility(View.VISIBLE);
            }
        } else {
            delete.setVisibility(View.GONE);
        }


        delete.setOnClickListener(v -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(this, R.style.DialogTheme);
            alert.setTitle("Are you sure you want to delete").setPositiveButton("Yes", (dialog, which) -> {
                RemoveVideo removeVideo = new RemoveVideo(this);
                removeVideo.execute(userSchoolId, id);
            }).setNegativeButton("No", null);
            AlertDialog alertDialog = alert.create();
            alertDialog.setCancelable(false);
            alertDialog.show();
            Button theButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            Button theButton1 = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            theButton.setTextColor(Color.parseColor(selToolColor));
            theButton1.setTextColor(Color.parseColor(selToolColor));
        });

        SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
        selToolColor = settingsColor.getString("tool", "");
        selStatusColor = settingsColor.getString("status", "");
        selTextColor1 = settingsColor.getString("text1", "");

        appBarLayout.setBackgroundColor(Color.parseColor(selToolColor));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(selStatusColor));
        }
        tool_title.setTextColor(Color.parseColor(selTextColor1));
        tool_title.setText(strTitle);

        if (url.contains("youtube") || url.contains("youtu.be")) {
            youTubeView.setVisibility(View.VISIBLE);
            watermark_yt.setText(rollNo);
            String videoKey = videoId(url);
            setTimerYT();
            youTubeView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                @Override
                public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                    super.onReady(youTubePlayer);
                    ytPlayer = youTubePlayer;
                    youTubePlayer.loadVideo(videoKey, 0);
                    tracker = new YouTubePlayerTracker();
                    youTubePlayer.addListener(tracker);
                    youTubeView.addFullScreenListener(new YouTubePlayerFullScreenListener() {
                        @Override
                        public void onYouTubePlayerEnterFullScreen() {
                            fullscr = true;
                            View decorView = getWindow().getDecorView();
                            hideSystemUi(decorView);
                        }

                        @Override
                        public void onYouTubePlayerExitFullScreen() {
                            fullscr = false;
                            View decorView = getWindow().getDecorView();
                            showSystemUi(decorView);
                        }
                    });
                }

                @Override
                public void onVideoDuration(@NotNull YouTubePlayer youTubePlayer, float duration) {
                    super.onVideoDuration(youTubePlayer, duration);
                    if (strTime == null || strTime.equals("null") || strTime.equals("00:00:00")) {
                        int hours = (int) duration / 3600;
                        int minutes = (int) (duration % 3600) / 60;
                        int seconds = (int) duration % 60;
                        @SuppressLint("DefaultLocale") String time = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                        AddTotalTime addTotalTime = new AddTotalTime(VideoDetails.this);
                        addTotalTime.execute(userSchoolId, id, time);
                    }
                }
            });

            YouTubePlayerMenu menu = youTubeView.getPlayerUiController().showMenuButton(true).getMenu();
            assert menu != null;

            menu.addItem(new com.aaptrix.youtubeview.core.ui.menu.MenuItem("0.5x", R.drawable.playback_speed, v -> {
                ytPlayer.setPlaybackRate(0.5f);
                Toast.makeText(this, "Playback speed set to 0.5x", Toast.LENGTH_SHORT).show();
                menu.dismiss();
            }))
                    .addItem(new com.aaptrix.youtubeview.core.ui.menu.MenuItem("1.0x", R.drawable.playback_speed, v -> {
                        ytPlayer.setPlaybackRate(1.0f);
                        Toast.makeText(this, "Playback speed set to 1.0x", Toast.LENGTH_SHORT).show();
                        menu.dismiss();
                    }))
                    .addItem(new com.aaptrix.youtubeview.core.ui.menu.MenuItem("1.25x", R.drawable.playback_speed, v -> {
                        ytPlayer.setPlaybackRate(1.25f);
                        Toast.makeText(this, "Playback speed set to 1.25x", Toast.LENGTH_SHORT).show();
                        menu.dismiss();
                    }))
                    .addItem(new com.aaptrix.youtubeview.core.ui.menu.MenuItem("1.5x", R.drawable.playback_speed, v -> {
                        ytPlayer.setPlaybackRate(1.5f);
                        Toast.makeText(this, "Playback speed set to 1.5x", Toast.LENGTH_SHORT).show();
                        menu.dismiss();
                    }))
                    .addItem(new com.aaptrix.youtubeview.core.ui.menu.MenuItem("1.75x", R.drawable.playback_speed, v -> {
                        ytPlayer.setPlaybackRate(1.75f);
                        Toast.makeText(this, "Playback speed set to 1.75x", Toast.LENGTH_SHORT).show();
                        menu.dismiss();
                    }))
                    .addItem(new com.aaptrix.youtubeview.core.ui.menu.MenuItem("2.0x", R.drawable.playback_speed, v -> {
                        ytPlayer.setPlaybackRate(2.0f);
                        Toast.makeText(this, "Playback speed set to 2.0x", Toast.LENGTH_SHORT).show();
                        menu.dismiss();
                    }));

            mainframe.setVisibility(View.GONE);
            watermark.setVisibility(View.GONE);

        } else {
            youTubeView.setVisibility(View.GONE);
            mainframe.setVisibility(View.VISIBLE);
            player.setVisibility(View.VISIBLE);
            initializePlayer();
        }
    }

    class TagsAdapter extends RecyclerView.Adapter<TagsAdapter.ViewHolder> {

        private Context context;
        private String[] tags;

        public TagsAdapter(Context context, String[] tags) {
            this.context = context;
            this.tags = tags;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tagsName;

            public ViewHolder(View view) {
                super(view);
                tagsName = view.findViewById(R.id.tag_name);
            }
        }

        @NotNull
        @Override
        public TagsAdapter.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
            final View view = LayoutInflater.from(this.context).inflate(R.layout.tags_list, parent, false);
            return new TagsAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NotNull TagsAdapter.ViewHolder holder, final int position) {
            if (!tags[position].isEmpty()) {
                holder.tagsName.setText(tags[position].trim());
                holder.tagsName.setOnClickListener(v -> {
                    Intent intent = new Intent(context, VideoByTag.class);
                    intent.putExtra("subject", strSubject);
                    intent.putExtra("tag", tags[position]);
                    context.startActivity(intent);
                });
            } else {
                holder.itemView.setVisibility(View.GONE);
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return tags.length;
        }
    }

    private String videoId(String url) {
        int index = url.indexOf("v=");
        String id = url.substring(index + 2, index + 13);
        if (id.equals("ttps://yout")) {
            id = url.split("/")[3];
        }
        return id;
    }

    private void initializePlayer() {
        if (url.contains("InstituteVideo")) {
            watermark.setText(rollNo);
            watermark.bringToFront();
            setTimer();
        } else {
            watermark.setVisibility(View.GONE);
        }
        Uri uri = Uri.parse(url);
        MediaSource mediaSource = buildMediaSource(uri);
        exoPlayer.setPlayWhenReady(playWhenReady);
        exoPlayer.seekTo(currentWindow, playbackPosition);
        exoPlayer.prepare(mediaSource, false, false);
        if (strTime == null || strTime.equals("null") || strTime.equals("00:00:00")) {
            long hours = exoPlayer.getDuration() / (60 * 60 * 1000) % 24;
            long minutes = exoPlayer.getDuration() / (60 * 1000) % 60;
            long seconds = exoPlayer.getDuration() / 1000 % 60;
            @SuppressLint("DefaultLocale") String time = String.format("%02d:%02d:%02d", hours, minutes, seconds);
            AddTotalTime addTotalTime = new AddTotalTime(VideoDetails.this);
            addTotalTime.execute(userSchoolId, id, time);
        }
        initFullscreenDialog();
        initFullscreenButton();
    }

    private MediaSource buildMediaSource(Uri uri) {
        DataSource.Factory dataSourceFactory =
                new DefaultHttpDataSourceFactory(Util.getUserAgent(this, getPackageName()));
        return new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri);
    }

    private void releasePlayer() {
        if (player != null) {
            playWhenReady = exoPlayer.getPlayWhenReady();
            playbackPosition = exoPlayer.getCurrentPosition();
            currentWindow = exoPlayer.getCurrentWindowIndex();
            exoPlayer.release();
            exoPlayer = null;
        }
        if (timer != null) {
            timer.cancel();
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private void hideSystemUi(View mDecorView) {
        getSupportActionBar().hide();
        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        appBarLayout.getRootView().setFitsSystemWindows(false);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        yt_frame.setLayoutParams(params);
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private void showSystemUi(View mDecorView) {
        getSupportActionBar().show();
        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        appBarLayout.getRootView().setFitsSystemWindows(true);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, -1);
        params.height = (int) getResources().getDimension(R.dimen._200sdp);
        yt_frame.setLayoutParams(params);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(0, 40, 0, 0);
        layoutParams.height = (int) getResources().getDimension(R.dimen._200sdp);
        youTubeView.setLayoutParams(layoutParams);
    }

    private void initFullscreenDialog() {

        mFullScreenDialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen) {
            public void onBackPressed() {
                if (mExoPlayerFullscreen)
                    closeFullscreenDialog();
                super.onBackPressed();
            }
        };
    }

    @Override
    public void onDestroy() {
        if (timer != null) {
            timer.cancel();
        }

        if (userrType.equals("Student") || userrType.equals("Parent"))
            if (tracker != null) {
                float duration = tracker.getCurrentSecond();
                int hours = (int) duration / 3600;
                int minutes = (int) (duration % 3600) / 60;
                int seconds = (int) duration % 60;
                @SuppressLint("DefaultLocale") String time = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                AddSeenTime addSeenTime = new AddSeenTime(this);
                addSeenTime.execute(userSchoolId, id, userId, time);
            } else if (exoPlayer != null) {
                long hours = exoPlayer.getCurrentPosition() / (60 * 60 * 1000) % 24;
                long minutes = exoPlayer.getCurrentPosition() / (60 * 1000) % 60;
                long seconds = exoPlayer.getCurrentPosition() / 1000 % 60;
                @SuppressLint("DefaultLocale") String time = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                AddSeenTime addSeenTime = new AddSeenTime(this);
                addSeenTime.execute(userSchoolId, id, userId, time);
            }

        ClipboardManager clipService = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipService != null)
            clipService.setPrimaryClip(ClipData.newPlainText("", ""));
        youTubeView.release();
        super.onDestroy();
    }


    @SuppressLint("SourceLockedOrientationActivity")
    private void openFullscreenDialog() {
        ((ViewGroup) player.getParent()).removeView(player);
        ((ViewGroup) watermark.getParent()).removeView(watermark);
        mFullScreenDialog.addContentView(player, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mFullScreenDialog.addContentView(watermark, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        watermark.bringToFront();
        mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_fullscreen_shrink));
        mExoPlayerFullscreen = true;
        mFullScreenDialog.show();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }


    @SuppressLint("SourceLockedOrientationActivity")
    private void closeFullscreenDialog() {
        ((ViewGroup) player.getParent()).removeView(player);
        ((ViewGroup) watermark.getParent()).removeView(watermark);
        ((FrameLayout) findViewById(R.id.main_media_frame)).addView(player);
        ((FrameLayout) findViewById(R.id.main_media_frame)).addView(watermark);
        watermark.bringToFront();
        mExoPlayerFullscreen = false;
        mFullScreenDialog.dismiss();
        mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_fullscreen_expand));
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }


    private void initFullscreenButton() {
        PlayerControlView controlView = player.findViewById(R.id.exo_controller);
        mFullScreenIcon = controlView.findViewById(R.id.exo_fullscreen_icon);
        FrameLayout mFullScreenButton = controlView.findViewById(R.id.exo_fullscreen_button);
        mFullScreenButton.setOnClickListener(v -> {
            if (!mExoPlayerFullscreen)
                openFullscreenDialog();
            else
                closeFullscreenDialog();
        });
    }

    private void setTimer() {
        timer = new CountDownTimer(15000, 15000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                int width = exo_layout.getWidth();
                int height = exo_layout.getHeight();
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-2, -2);
                Random random = new Random();
                int top = random.nextInt(height - 70);
                int left = random.nextInt(width - 150);
                params.setMargins(left, top, 0, 0);
                watermark.setLayoutParams(params);
                int color = Color.argb(150, random.nextInt(256), random.nextInt(256), random.nextInt(256));
                watermark.setTextColor(color);
                watermark.bringToFront();
                start();
            }
        }.start();
    }

    private void setTimerYT() {
        timer = new CountDownTimer(15000, 15000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                int width = yt_layout.getWidth();
                int height = yt_layout.getHeight();
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-2, -2);
                Random random = new Random();
                int top = random.nextInt(height - 70);
                int left = random.nextInt(width - 150);
                params.setMargins(left, top, 0, 0);
                watermark_yt.setLayoutParams(params);
                int color = Color.argb(150, random.nextInt(256), random.nextInt(256), random.nextInt(256));
                watermark_yt.setTextColor(color);
                watermark_yt.bringToFront();
                start();
            }
        }.start();
    }

    @Override
    protected void onPause() {
        if (exoPlayer != null) {
            releasePlayer();
            if (mFullScreenDialog != null)
                mFullScreenDialog.dismiss();
        }
        if (timer != null) {
            timer.cancel();
        }

        if (userrType.equals("Student") || userrType.equals("Parent"))
            if (tracker != null) {
                float duration = tracker.getCurrentSecond();
                int hours = (int) duration / 3600;
                int minutes = (int) (duration % 3600) / 60;
                int seconds = (int) duration % 60;
                @SuppressLint("DefaultLocale") String time = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                AddSeenTime addSeenTime = new AddSeenTime(this);
                addSeenTime.execute(userSchoolId, id, userId, time);
            } else if (exoPlayer != null) {
                long hours = exoPlayer.getCurrentPosition() / (60 * 60 * 1000) % 24;
                long minutes = exoPlayer.getCurrentPosition() / (60 * 1000) % 60;
                long seconds = exoPlayer.getCurrentPosition() / 1000 % 60;
                @SuppressLint("DefaultLocale") String time = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                AddSeenTime addSeenTime = new AddSeenTime(this);
                addSeenTime.execute(userSchoolId, id, userId, time);
            }

        ClipboardManager clipService = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipService != null)
            clipService.setPrimaryClip(ClipData.newPlainText("", ""));
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle outState) {
        super.onRestoreInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (exoPlayer != null) {
            releasePlayer();
            if (mFullScreenDialog != null)
                mFullScreenDialog.dismiss();
            finish();
        }

        if (userrType.equals("Student") || userrType.equals("Parent"))
            if (tracker != null) {
                float duration = tracker.getCurrentSecond();
                int hours = (int) duration / 3600;
                int minutes = (int) (duration % 3600) / 60;
                int seconds = (int) duration % 60;
                @SuppressLint("DefaultLocale") String time = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                AddSeenTime addSeenTime = new AddSeenTime(this);
                addSeenTime.execute(userSchoolId, id, userId, time);
            } else if (exoPlayer != null) {
                long hours = exoPlayer.getCurrentPosition() / (60 * 60 * 1000) % 24;
                long minutes = exoPlayer.getCurrentPosition() / (60 * 1000) % 60;
                long seconds = exoPlayer.getCurrentPosition() / 1000 % 60;
                @SuppressLint("DefaultLocale") String time = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                AddSeenTime addSeenTime = new AddSeenTime(this);
                addSeenTime.execute(userSchoolId, id, userId, time);
            }

        ClipboardManager clipService = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipService != null)
            clipService.setPrimaryClip(ClipData.newPlainText("", ""));
        super.onBackPressed();
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
                Intent intent = new Intent(ctx, VideoLibrary.class);
                intent.putExtra("sub", "All Subjects");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(ctx, "Some Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    static class AddTotalTime extends AsyncTask<String, String, String> {
        Context ctx;

        AddTotalTime(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String schId = params[0];
            String videoId = params[1];
            String time = params[2];
            String data;

            Log.e("vid", videoId);
            Log.e("time", time);

            try {
                URL url = new URL(VIDEO_TOTAL_TIME);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                data = URLEncoder.encode("tbl_school_id", "UTF-8") + "=" + URLEncoder.encode(schId, "UTF-8") + "&" +
                        URLEncoder.encode("video_id", "UTF-8") + "=" + URLEncoder.encode(videoId, "UTF-8") + "&" +
                        URLEncoder.encode("video_total_time", "UTF-8") + "=" + URLEncoder.encode(time, "UTF-8");
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
        }
    }

    @SuppressLint("StaticFieldLeak")
    static class AddSeenTime extends AsyncTask<String, String, String> {
        Context ctx;

        AddSeenTime(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String schoolId = params[0];
            String videoId = params[1];
            String userId = params[2];
            String time = params[3];
            String data;

            Log.e("vid", videoId);
            Log.e("time", time);
            Log.e("user", userId);

            try {
                URL url = new URL(VIDEO_SEEN_TIME);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                data = URLEncoder.encode("video_id", "UTF-8") + "=" + URLEncoder.encode(videoId, "UTF-8") + "&" +
                        URLEncoder.encode("tbl_users_id", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8") + "&" +
                        URLEncoder.encode("tbl_school_id", "UTF-8") + "=" + URLEncoder.encode(schoolId, "UTF-8") + "&" +
                        URLEncoder.encode("video_seen_time", "UTF-8") + "=" + URLEncoder.encode(time, "UTF-8");
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
            Log.e("result", String.valueOf(result));
        }
    }
}
