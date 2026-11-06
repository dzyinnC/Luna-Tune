package com.example.playback

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.example.LunaTuneApp

class LunaTuneMediaService : MediaSessionService() {

    companion object {
        const val CHANNEL_ID = "lunatune_playback_channel"
        const val NOTIFICATION_ID = 1001
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        val app = application as? LunaTuneApp
        return app?.playerManager?.mediaSession
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "LunaTune Musik",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notifikasi pemutar musik LunaTune"
                setShowBadge(false)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
