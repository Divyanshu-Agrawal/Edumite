package com.aaptrix.youtubeview.core.player.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import com.aaptrix.R
import com.aaptrix.youtubeview.core.player.PlayerConstants
import com.aaptrix.youtubeview.core.player.YouTubePlayer
import com.aaptrix.youtubeview.core.player.YouTubePlayerBridge
import com.aaptrix.youtubeview.core.player.listeners.YouTubePlayerListener
import com.aaptrix.youtubeview.core.player.options.IFramePlayerOptions
import com.aaptrix.youtubeview.core.player.utils.Utils
import java.util.*

/**
 * WebView implementation of [YouTubePlayer]. The player runs inside the WebView, using the IFrame Player API.
 */
internal class WebViewYouTubePlayer constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : WebView(context, attrs, defStyleAttr), YouTubePlayer, YouTubePlayerBridge.YouTubePlayerBridgeCallbacks {

    private lateinit var youTubePlayerInitListener: (YouTubePlayer) -> Unit

    private val youTubePlayerListeners = HashSet<YouTubePlayerListener>()
    private val mainThreadHandler: Handler = Handler(Looper.getMainLooper())

    internal var isBackgroundPlaybackEnabled = false

    internal fun initialize(initListener: (YouTubePlayer) -> Unit, playerOptions: IFramePlayerOptions?) {
        youTubePlayerInitListener = initListener
        initWebView(playerOptions ?: IFramePlayerOptions.default)
    }

    override fun onYouTubeIFrameAPIReady() = youTubePlayerInitListener(this)

    override fun getInstance(): YouTubePlayer = this

    override fun loadVideo(videoId: String, startSeconds: Float) {
        loadVideo(videoId, startSeconds, PlayerConstants.PlaybackQuality.DEFAULT)
    }

    override fun loadVideo(videoId: String, startSeconds: Float, suggestedQuality: PlayerConstants.PlaybackQuality) {
        mainThreadHandler.post {
            loadUrl("javascript:loadVideo('$videoId', $startSeconds, '${suggestedQuality.jsValue}')")

        }
    }

    override fun cueVideo(videoId: String, startSeconds: Float) {
        cueVideo(videoId, startSeconds, PlayerConstants.PlaybackQuality.DEFAULT)
    }

    override fun cueVideo(videoId: String, startSeconds: Float, suggestedQuality: PlayerConstants.PlaybackQuality) {
        mainThreadHandler.post {
            loadUrl("javascript:cueVideo('$videoId', $startSeconds, '${suggestedQuality.jsValue}')")
        }
    }

    override fun play() {
        mainThreadHandler.post { loadUrl("javascript:playVideo()") }
    }

    override fun pause() {
        mainThreadHandler.post { loadUrl("javascript:pauseVideo()") }
    }

    override fun mute() {
        mainThreadHandler.post { loadUrl("javascript:mute()") }
    }

    override fun unMute() {
        mainThreadHandler.post { loadUrl("javascript:unMute()") }
    }

    override fun setVolume(volumePercent: Int) {
        require(!(volumePercent < 0 || volumePercent > 100)) { "Volume must be between 0 and 100" }

        mainThreadHandler.post { loadUrl("javascript:setVolume($volumePercent)") }
    }

    override fun seekTo(time: Float) {
        mainThreadHandler.post { loadUrl("javascript:seekTo($time)") }
    }

    override fun setPlaybackRate(rate: Float) {
        mainThreadHandler.post { loadUrl("javascript:setPlaybackRate($rate)") }
    }

    override fun destroy() {
        youTubePlayerListeners.clear()
        mainThreadHandler.removeCallbacksAndMessages(null)
        super.destroy()
    }

    override fun getListeners(): Collection<YouTubePlayerListener> {
        return Collections.unmodifiableCollection(HashSet(youTubePlayerListeners))
    }

    override fun addListener(listener: YouTubePlayerListener): Boolean {
        return youTubePlayerListeners.add(listener)
    }

    override fun removeListener(listener: YouTubePlayerListener): Boolean {
        return youTubePlayerListeners.remove(listener)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView(playerOptions: IFramePlayerOptions) {
        settings.javaScriptEnabled = true
        settings.mediaPlaybackRequiresUserGesture = false
        settings.cacheMode = WebSettings.LOAD_NO_CACHE

        addJavascriptInterface(YouTubePlayerBridge(this), "YouTubePlayerBridge")

        val htmlPage = Utils
                .readHTMLFromUTF8File(resources.openRawResource(R.raw.ayp_youtube_player))
                .replace("<<injectedPlayerVars>>", playerOptions.toString().replace("rel:0", "ecver=2"))

        loadDataWithBaseURL(playerOptions.getOrigin(), htmlPage, "text/html", "utf-8", null)
        // if the video's thumbnail is not in memory, show a black screen
        webChromeClient = object : WebChromeClient() {
            override fun getDefaultVideoPoster(): Bitmap? {
                val result = super.getDefaultVideoPoster()

                return result ?: Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565)
            }
        }
    }

    override fun onWindowVisibilityChanged(visibility: Int) {
        if (isBackgroundPlaybackEnabled && (visibility == View.GONE || visibility == View.INVISIBLE))
            return

        super.onWindowVisibilityChanged(visibility)
    }
}
