package com.udacity

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    private var downloadRequest: DownloadRequest = DownloadRequest()

    private lateinit var downloadGlideRequest: DownloadRequest
    private lateinit var downloadLoadAppRequest: DownloadRequest
    private lateinit var downloadRetrofitRequest: DownloadRequest

    private val receiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {

            downloadButton.onDownloadCompletedEvent()

            val downloadId = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            downloadId?.let {
                getNotificationManager().sendNotification(
                    it.toInt(), downloadRequest.title, applicationContext
                )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        initializeRequests()

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        downloadChoicesGroup.setOnCheckedChangeListener { _, checkedId ->
            downloadRequest = checkedId.toRequest()
        }

        downloadButton.setOnClickListener {
            download()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createDefaultNotificationChannel(applicationContext)
        }
    }

    private fun download() {
        if (downloadRequest.isValid) {

            downloadButton.onDownloadStartedEvent()

            val request =
                DownloadManager.Request(Uri.parse(downloadRequest.url))
                    .setTitle(downloadRequest.title)
                    .setDescription(downloadRequest.description)
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)

            getDownloadManager().enqueue(request)

        } else {
            Toast.makeText(
                this,
                getString(R.string.invalid_download_selection),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun Int.toRequest(): DownloadRequest {
        return when (this) {
            R.id.glideButton -> downloadGlideRequest
            R.id.loadAppButton -> downloadLoadAppRequest
            R.id.retrofitButton -> downloadRetrofitRequest
            else -> DownloadRequest()
        }
    }

    private fun initializeRequests() {
        downloadGlideRequest = DownloadRequest(
            GLIDE_URL,
            applicationContext.getString(R.string.glide_notification_title),
            applicationContext.getString(R.string.glide_choice)
        )

        downloadLoadAppRequest = DownloadRequest(
            LOAD_APP_URL,
            applicationContext.getString(R.string.load_app_notification_title),
            applicationContext.getString(R.string.load_app_choice)
        )

        downloadRetrofitRequest = DownloadRequest(
            RETROFIT_URL,
            applicationContext.getString(R.string.retrofit_notification_title),
            applicationContext.getString(R.string.retrofit_choice)
        )
    }

    companion object {
        private const val LOAD_APP_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter"

        private const val GLIDE_URL = "https://github.com/bumptech/glide"

        private const val RETROFIT_URL = "https://github.com/square/retrofit"
    }
}
