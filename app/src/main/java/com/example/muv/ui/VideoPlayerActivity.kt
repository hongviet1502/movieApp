package com.example.muv.ui

import android.content.pm.ActivityInfo
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.SeekBar
import com.example.muv.R
import com.example.muv.databinding.ActivityVideoPlayerBinding
import com.example.muv.ui.base.BaseActivity
import com.example.muv.ui.navigation.NavigationManager
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.abs

@AndroidEntryPoint
class VideoPlayerActivity : BaseActivity<ActivityVideoPlayerBinding, VideoPlayerViewModel>() {

    override val layoutId: Int = R.layout.activity_video_player
    override val viewModelClass: Class<VideoPlayerViewModel> = VideoPlayerViewModel::class.java

    private var youTubePlayer: YouTubePlayer? = null
    private var isControlsVisible = true
    private var currentVideoTime = 0f
    private var videoDuration = 0f
    private var isPlaying = false
    private var isFullscreen = false

    private val hideControlsHandler = Handler(Looper.getMainLooper())
    private val hideControlsRunnable = Runnable { hideControls() }
    private val controlsVisibilityDuration = 3000L // 3 seconds

    private lateinit var gestureDetector: GestureDetector
    private var audioManager: AudioManager? = null

    override fun shouldShowBackButton(): Boolean = false

    override fun initView() {
        Log.d(TAG, "initView started")
        Log.d("VideoPlayer", "VideoKey: ${intent.getStringExtra(EXTRA_VIDEO_KEY)}")
        Log.d("VideoPlayer", "YouTube Player View: ${binding.youtubePlayerView}")
        // Setup YouTube Player FIRST
        setupVideoPlayer()
        setupGestureDetector()
        setupSystemUI()

        // Get video info from intent
        val videoKey = intent.getStringExtra(EXTRA_VIDEO_KEY) ?: ""
        val videoTitle = intent.getStringExtra(EXTRA_VIDEO_TITLE) ?: ""
        val videoType = intent.getStringExtra(EXTRA_VIDEO_TYPE) ?: ""

        Log.d(TAG, "Video info - Key: $videoKey, Title: $videoTitle, Type: $videoType")

        if (videoKey.isEmpty()) {
            Log.e(TAG, "Video key is empty!")
            showVideoError("Invalid video")
            NavigationManager.navigateBack(this)
            return
        }

        viewModel.setVideoInfo(videoKey, videoTitle, videoType)
    }

    override fun initData() {
        // Data initialized in initView through intent
    }

    override fun initListener() {
        setupClickListeners()
        setupObservers()
    }

    override fun onRetryClicked() {
        viewModel.retryLoadVideo()
    }

    private fun setupVideoPlayer() {
        Log.d(TAG, "Setting up YouTube Player")

        // CRITICAL: Initialize the YouTube Player manually
        binding.youtubePlayerView.initialize(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                Log.i(TAG, "YouTube Player is ready!")
                this@VideoPlayerActivity.youTubePlayer = youTubePlayer
                viewModel.onPlayerReady()

                // Enable custom UI
                youTubePlayer.addListener(playerStateListener)
            }

            override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) {
                Log.e(TAG, "YouTube Player error: $error")
                handleVideoError(error)
            }
        })

        // Add to lifecycle
        lifecycle.addObserver(binding.youtubePlayerView)
    }

    private val playerStateListener = object : AbstractYouTubePlayerListener() {
        override fun onStateChange(
            youTubePlayer: YouTubePlayer,
            state: PlayerConstants.PlayerState
        ) {
            Log.i(TAG, "Player state changed: $state")
            when (state) {
                PlayerConstants.PlayerState.PLAYING -> {
                    isPlaying = true
                    binding.playPauseButton.setImageResource(R.drawable.ic_pause_circle)
                    binding.loadingProgressBar.visibility = View.GONE
                    hideControlsDelayed()
                    viewModel.onVideoPlaying()
                }

                PlayerConstants.PlayerState.PAUSED -> {
                    isPlaying = false
                    binding.playPauseButton.setImageResource(R.drawable.ic_play_circle)
                    showControls()
                    viewModel.onVideoPaused()
                }

                PlayerConstants.PlayerState.BUFFERING -> {
                    binding.loadingProgressBar.visibility = View.VISIBLE
                    viewModel.onVideoBuffering()
                }

                PlayerConstants.PlayerState.ENDED -> {
                    isPlaying = false
                    binding.playPauseButton.visibility = View.GONE
                    binding.replayButton.visibility = View.VISIBLE
                    showControls()
                    viewModel.onVideoEnded()
                }

                PlayerConstants.PlayerState.UNSTARTED -> {
                    Log.d(TAG, "Player unstarted")
                }

                else -> {
                    Log.d(TAG, "Other state: $state")
                }
            }
        }

        override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
            currentVideoTime = second
            updateProgressBar()
            viewModel.updateCurrentTime(second)
        }

        override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {
            Log.i(TAG, "Video duration: $duration seconds")
            videoDuration = duration
            viewModel.updateDuration(duration)
        }

        override fun onVideoId(youTubePlayer: YouTubePlayer, videoId: String) {
            Log.i(TAG, "Video ID: $videoId")
        }

        override fun onVideoLoadedFraction(youTubePlayer: YouTubePlayer, loadedFraction: Float) {
            // Video loading progress
        }
    }

    private fun setupGestureDetector() {
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager

        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                toggleControlsVisibility()
                return true
            }

            override fun onDoubleTap(e: MotionEvent): Boolean {
                val screenWidth = binding.root.width
                if (e.x < screenWidth / 2) {
                    seekBackward()
                } else {
                    seekForward()
                }
                return true
            }

            override fun onScroll(
                e1: MotionEvent?,
                e2: MotionEvent,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                if (e1 == null) return false

                val screenWidth = binding.root.width
                val deltaY = e1.y - e2.y

                if (abs(deltaY) > abs(distanceX)) {
                    if (e1.x < screenWidth / 2) {
                        adjustBrightness(deltaY)
                    } else {
                        adjustVolume(deltaY)
                    }
                    return true
                }
                return false
            }
        })

        binding.gestureOverlay.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }
    }

    private fun setupSystemUI() {
        // Keep screen on
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // Force landscape orientation
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        // Hide system bars for immersive experience
        toggleSystemBars(true)
    }

    private fun setupClickListeners() {
        // Back button
        binding.backButton.setOnClickListener {
            NavigationManager.navigateBack(this)
        }

        // Play/Pause
        binding.playPauseButton.setOnClickListener {
            togglePlayPause()
        }

        // Skip controls
        binding.skipBackwardButton.setOnClickListener {
            seekBackward()
        }

        binding.skipForwardButton.setOnClickListener {
            seekForward()
        }

        // Replay
        binding.replayButton.setOnClickListener {
            replayVideo()
        }

        // Fullscreen
        binding.fullscreenButton.setOnClickListener {
            toggleFullscreen()
        }

        // Volume
        binding.volumeButton.setOnClickListener {
            toggleMute()
        }

        // Speed
        binding.speedButton.setOnClickListener {
            showSpeedDialog()
        }

        // Quality
        binding.qualityButton.setOnClickListener {
            showQualityDialog()
        }

        // More options
        binding.moreButton.setOnClickListener {
            showMoreOptions()
        }

        // Progress bar
        binding.progressSeekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val seekTime = (progress / 100f) * videoDuration
                    youTubePlayer?.seekTo(seekTime)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                showControls()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                hideControlsDelayed()
            }
        })

        // Retry button
        binding.retryButton.setOnClickListener {
            hideError()
            viewModel.retryLoadVideo()
        }
    }

    private fun setupObservers() {
        viewModel.videoKey.observe(this) { videoKey ->
            Log.d(TAG, "Video key observed: $videoKey")
            if (videoKey.isNotEmpty() && youTubePlayer != null) {
                loadVideo(videoKey)
            }
        }

        viewModel.videoTitle.observe(this) { title ->
            binding.videoTitleTextView.text = title
        }

        viewModel.videoType.observe(this) { type ->
            binding.videoTypeTextView.text = type
        }

        viewModel.currentTimeFormatted.observe(this) { time ->
            binding.currentTimeTextView.text = time
        }

        viewModel.durationFormatted.observe(this) { duration ->
            binding.totalTimeTextView.text = duration
        }

        viewModel.error.observe(this) { error ->
            if (error != null) {
                showVideoError(error)
            }
        }
    }

    private fun loadVideo(videoKey: String) {
        Log.i(TAG, "Loading video with key: $videoKey")
        try {
            youTubePlayer?.loadVideo(videoKey, 0f)
            viewModel.trackVideoStarted()
        } catch (e: Exception) {
            Log.e(TAG, "Error loading video: ${e.message}")
            showVideoError("Failed to load video: ${e.message}")
        }
    }

    private fun togglePlayPause() {
        youTubePlayer?.let { player ->
            if (isPlaying) {
                player.pause()
            } else {
                player.play()
            }
        }
    }

    private fun seekBackward() {
        youTubePlayer?.seekTo(maxOf(0f, currentVideoTime - 10f))
        showSeekIndicator(-10)
    }

    private fun seekForward() {
        youTubePlayer?.seekTo(minOf(videoDuration, currentVideoTime + 10f))
        showSeekIndicator(10)
    }

    private fun replayVideo() {
        youTubePlayer?.seekTo(0f)
        youTubePlayer?.play()
        binding.replayButton.visibility = View.GONE
        binding.playPauseButton.visibility = View.VISIBLE
    }

    private fun toggleFullscreen() {
        isFullscreen = !isFullscreen
        if (isFullscreen) {
            binding.fullscreenButton.setImageResource(R.drawable.ic_fullscreen_exit)
            toggleSystemBars(true)
        } else {
            binding.fullscreenButton.setImageResource(R.drawable.ic_fullscreen)
            toggleSystemBars(false)
        }
    }

    private fun toggleMute() {
        showMessage("Volume control through device buttons")
    }

    private fun adjustBrightness(deltaY: Float) {
        val window = window
        val layoutParams = window.attributes
        val brightness = layoutParams.screenBrightness + (deltaY / 1000f)
        layoutParams.screenBrightness = brightness.coerceIn(0.1f, 1.0f)
        window.attributes = layoutParams

        showBrightnessOverlay((layoutParams.screenBrightness * 100).toInt())
    }

    private fun adjustVolume(deltaY: Float) {
        audioManager?.let { am ->
            val maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            val currentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC)
            val newVolume = if (deltaY > 0) {
                minOf(maxVolume, currentVolume + 1)
            } else {
                maxOf(0, currentVolume - 1)
            }
            am.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, 0)

            showVolumeOverlay((newVolume * 100 / maxVolume))
        }
    }

    private fun showSeekIndicator(seconds: Int) {
        val message = if (seconds > 0) "+${seconds}s" else "${seconds}s"
        showMessage(message)
    }

    private fun showBrightnessOverlay(brightness: Int) {
        binding.overlayIcon.setImageResource(R.drawable.ic_brightness)
        binding.overlayProgressBar.progress = brightness
        showOverlay()
    }

    private fun showVolumeOverlay(volume: Int) {
        binding.overlayIcon.setImageResource(R.drawable.ic_volume_up)
        binding.overlayProgressBar.progress = volume
        showOverlay()
    }

    private fun showOverlay() {
        binding.brightnessVolumeOverlay.visibility = View.VISIBLE
        Handler(Looper.getMainLooper()).postDelayed({
            binding.brightnessVolumeOverlay.visibility = View.GONE
        }, 1000)
    }

    private fun toggleControlsVisibility() {
        if (isControlsVisible) {
            hideControls()
        } else {
            showControls()
        }
    }

    private fun showControls() {
        binding.controlsOverlay.visibility = View.VISIBLE
        isControlsVisible = true
        hideControlsHandler.removeCallbacks(hideControlsRunnable)
    }

    private fun hideControls() {
        binding.controlsOverlay.visibility = View.GONE
        isControlsVisible = false
        hideControlsHandler.removeCallbacks(hideControlsRunnable)
    }

    private fun hideControlsDelayed() {
        hideControlsHandler.removeCallbacks(hideControlsRunnable)
        hideControlsHandler.postDelayed(hideControlsRunnable, controlsVisibilityDuration)
    }

    private fun updateProgressBar() {
        if (videoDuration > 0) {
            val progress = ((currentVideoTime / videoDuration) * 100).toInt()
            binding.progressSeekBar.progress = progress
        }
    }

    private fun handleVideoError(error: PlayerConstants.PlayerError) {
        val errorMessage = when (error) {
            PlayerConstants.PlayerError.VIDEO_NOT_FOUND -> "Video not found"
            PlayerConstants.PlayerError.VIDEO_NOT_PLAYABLE_IN_EMBEDDED_PLAYER -> "Video cannot be played in embedded player"
            PlayerConstants.PlayerError.INVALID_PARAMETER_IN_REQUEST -> "Invalid video parameter"
            PlayerConstants.PlayerError.HTML_5_PLAYER -> "HTML5 player error"
            PlayerConstants.PlayerError.UNKNOWN -> "Unknown player error"
        }
        Log.e(TAG, "Video player error: $errorMessage")
        showVideoError(errorMessage)
    }

    private fun showVideoError(message: String) {
        Log.e(TAG, "Showing video error: $message")
        binding.errorContainer.visibility = View.VISIBLE
        binding.errorMessageTextView.text = message
        binding.youtubePlayerView.visibility = View.GONE
        binding.controlsOverlay.visibility = View.GONE
    }

    private fun hideError() {
        binding.errorContainer.visibility = View.GONE
        binding.youtubePlayerView.visibility = View.VISIBLE
        binding.controlsOverlay.visibility = View.VISIBLE
    }

    private fun showSpeedDialog() {
        showMessage("Speed control coming soon!")
    }

    private fun showQualityDialog() {
        showMessage("Quality control coming soon!")
    }

    private fun showMoreOptions() {
        showMessage("More options coming soon!")
    }

    override fun onBackPressed() {
        if (isFullscreen) {
            toggleFullscreen()
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        hideControlsHandler.removeCallbacks(hideControlsRunnable)
        youTubePlayer?.pause()
    }

    override fun onPause() {
        super.onPause()
        youTubePlayer?.pause()
    }

    companion object {
        const val TAG = "VideoPlayerActivity"
        const val EXTRA_VIDEO_KEY = "extra_video_key"
        const val EXTRA_VIDEO_TITLE = "extra_video_title"
        const val EXTRA_VIDEO_TYPE = "extra_video_type"
    }
}