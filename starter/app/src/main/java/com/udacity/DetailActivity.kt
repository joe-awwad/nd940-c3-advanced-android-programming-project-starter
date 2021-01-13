package com.udacity

import android.app.DownloadManager.*
import android.database.Cursor
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        okButton.setOnClickListener {
            finish()
        }

        val downloadId = intent.getIntExtra(DOWNLOAD_ID_ARG_NAME, 0)
        downloadId.apply {
            getNotificationManager().cancel(this)
        }

        handleDownloadDetails(downloadId)
    }

    private fun handleDownloadDetails(notificationId: Int) {
        val cursor =
            getDownloadManager().query(
                Query().setFilterById(notificationId.toLong())
            )

        cursor?.let {
            if (it.moveToNext()) {
                downloadText.text = it.getTitle()
                descriptionText.text = it.getDescription()

                when (it.getStatus()) {
                    STATUS_SUCCESSFUL -> {
                        statusText.text = getString(R.string.success)
                        totalSizeText.text = getFormattedSize(it.getTotalSizeBytes())
                    }
                    STATUS_FAILED -> {
                        statusText.text = getString(R.string.fail)
                        statusText.setTextColor(Color.RED)
                    }
                }
            }
            it.close()
        }
    }

    private fun Cursor.getTitle(): String {
        return getString(getColumnIndex(COLUMN_TITLE))
    }

    private fun Cursor.getDescription(): String {
        return getString(getColumnIndex(COLUMN_DESCRIPTION))
    }

    private fun Cursor.getStatus(): Int {
        return getInt(getColumnIndex(COLUMN_STATUS))
    }

    private fun Cursor.getTotalSizeBytes(): Long {
        return getLong(getColumnIndex(COLUMN_TOTAL_SIZE_BYTES))
    }

    private fun getFormattedSize(totalSizeBytes: Long): String {
        val kb = totalSizeBytes / 1024
        val mb = kb / 1024
        return if (mb > 0) "$mb MB" else if (kb > 0) "$kb KB" else "$totalSizeBytes B"
    }
}
