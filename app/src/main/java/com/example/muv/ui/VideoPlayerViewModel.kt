package com.example.muv.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.muv.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoPlayerViewModel @Inject constructor() : BaseViewModel() {

    // Video information
    private val _videoKey = MutableLiveData<String>()
    val videoKey: LiveData<String> = _videoKey

    private val _videoTitle = MutableLiveData<String>()
    val videoTitle: LiveData<String> = _videoTitle

    private val _videoType = MutableLiveData<String>()
    val videoType: LiveData<String> = _videoType

    // Playback state
    private val _currentTime = MutableLiveData<Float>()
    val currentTime: LiveData<Float> = _currentTime

    private val _duration = MutableLiveData<Float>()
    val duration: LiveData<Float> = _duration

    private val _currentTimeFormatted = MutableLiveData<String>()
    val currentTimeFormatted: LiveData<String> = _currentTimeFormatted

    private val _durationFormatted = MutableLiveData<String>()
    val durationFormatted: LiveData<String> = _durationFormatted

    // Player state
    private val _isPlaying = MutableLiveData<Boolean>()
    val isPlaying: LiveData<Boolean> = _isPlaying

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _playerError = MutableLiveData<String?>()
    val playerError: LiveData<String?> = _playerError

    // Player settings
    private val _playbackSpeed = MutableLiveData<Float>()
    val playbackSpeed: LiveData<Float> = _playbackSpeed

    private val _quality = MutableLiveData<String>()
    val quality: LiveData<String> = _quality

    init {
        // Initialize default values
        _currentTime.value = 0f
        _duration.value = 0f
        _currentTimeFormatted.value = "0:00"
        _durationFormatted.value = "0:00"
        _isPlaying.value = false
        _isLoading.value = false
        _playbackSpeed.value = 1.0f
        _quality.value = "HD"
    }

    fun setVideoInfo(videoKey: String, title: String, type: String) {
        _videoKey.value = videoKey
        _videoTitle.value = title
        _videoType.value = type

        // Clear any previous errors
        _playerError.value = null
//        clearError()
    }

    fun onPlayerReady() {
        _isLoading.value = false
        showMessage("Video loaded successfully")
    }

    fun updateCurrentTime(timeInSeconds: Float) {
        _currentTime.value = timeInSeconds
        _currentTimeFormatted.value = formatTime(timeInSeconds)
    }

    fun updateDuration(durationInSeconds: Float) {
        _duration.value = durationInSeconds
        _durationFormatted.value = formatTime(durationInSeconds)
    }

    fun setPlayingState(isPlaying: Boolean) {
        _isPlaying.value = isPlaying
    }

    fun setLoadingState(isLoading: Boolean) {
        _isLoading.value = isLoading
    }

    fun setPlayerError(errorMessage: String) {
        _playerError.value = errorMessage
        showError(errorMessage)
        _isLoading.value = false
    }

    fun clearPlayerError() {
        _playerError.value = null
//        clearError()
    }

    fun retryLoadVideo() {
        _playerError.value = null
//        clearError()
        _isLoading.value = true

        // Trigger video reload by re-emitting the video key
        val currentKey = _videoKey.value
        if (!currentKey.isNullOrEmpty()) {
            _videoKey.value = currentKey
        } else {
            showError("No video to retry")
        }
    }

    fun setPlaybackSpeed(speed: Float) {
        _playbackSpeed.value = speed
        showMessage("Playback speed: ${speed}x")
    }

    fun setQuality(quality: String) {
        _quality.value = quality
        showMessage("Quality: $quality")
    }

    fun onVideoEnded() {
        _isPlaying.value = false
        _currentTime.value = 0f
        _currentTimeFormatted.value = "0:00"
        showMessage("Video ended")
    }

    fun onVideoBuffering() {
        _isLoading.value = true
    }

    fun onVideoPlaying() {
        _isPlaying.value = true
        _isLoading.value = false
    }

    fun onVideoPaused() {
        _isPlaying.value = false
        _isLoading.value = false
    }

    fun getProgress(): Int {
        val current = _currentTime.value ?: 0f
        val total = _duration.value ?: 1f
        return if (total > 0) ((current / total) * 100).toInt() else 0
    }

    fun getTimeRemaining(): String {
        val current = _currentTime.value ?: 0f
        val total = _duration.value ?: 0f
        val remaining = total - current
        return if (remaining > 0) "-${formatTime(remaining)}" else "0:00"
    }

    private fun formatTime(timeInSeconds: Float): String {
        val totalSeconds = timeInSeconds.toInt()
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        return if (hours > 0) {
            String.format("%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%d:%02d", minutes, seconds)
        }
    }

    // Analytics and tracking methods
    fun trackVideoStarted() {
        viewModelScope.launch {
            try {
                // TODO: Implement analytics tracking
                // analyticsRepository.trackVideoStarted(videoKey.value, videoTitle.value)
            } catch (e: Exception) {
                // Silent fail for analytics
            }
        }
    }

    fun trackVideoCompleted() {
        viewModelScope.launch {
            try {
                // TODO: Implement analytics tracking
                // analyticsRepository.trackVideoCompleted(videoKey.value, videoTitle.value)
            } catch (e: Exception) {
                // Silent fail for analytics
            }
        }
    }

    fun trackVideoProgress(progressPercentage: Int) {
        viewModelScope.launch {
            try {
                // TODO: Implement analytics tracking
                // Track at 25%, 50%, 75% milestones
                if (progressPercentage in listOf(25, 50, 75)) {
                    // analyticsRepository.trackVideoProgress(videoKey.value, progressPercentage)
                }
            } catch (e: Exception) {
                // Silent fail for analytics
            }
        }
    }

    fun saveWatchHistory() {
        viewModelScope.launch {
            try {
                val key = _videoKey.value ?: return@launch
                val title = _videoTitle.value ?: return@launch
                val currentTime = _currentTime.value ?: return@launch
                val duration = _duration.value ?: return@launch

                // TODO: Save to watch history
                // watchHistoryRepository.saveWatchProgress(key, title, currentTime, duration)
            } catch (e: Exception) {
                // Silent fail for watch history
            }
        }
    }
}