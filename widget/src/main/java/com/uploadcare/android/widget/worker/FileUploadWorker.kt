package com.uploadcare.android.widget.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import androidx.work.*
import com.uploadcare.android.library.api.UploadcareFile
import com.uploadcare.android.library.callbacks.ProgressCallback
import com.uploadcare.android.library.upload.FileUploader
import com.uploadcare.android.library.upload.Uploader
import com.uploadcare.android.library.upload.UrlUploader
import com.uploadcare.android.widget.R
import com.uploadcare.android.widget.controller.UploadcareWidget
import kotlin.math.roundToInt

class FileUploadWorker(appContext: Context,
                       workerParams: WorkerParameters)
    : CoroutineWorker(appContext, workerParams), ProgressCallback {

    private val notificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as
            NotificationManager

    private var uploader: Uploader? = null
    private var cancelable: Boolean = false
    private var showProgress: Boolean = false

    override suspend fun doWork(): Result {
        cancelable = inputData.getBoolean(KEY_CANCELABLE, false)
        showProgress = inputData.getBoolean(KEY_SHOW_PROGRESS, false)
        // Mark the Worker as important
        setForeground(createForegroundInfo(
                createNotification(
                        applicationContext.getString(R.string.ucw_notification_content_started))))

        uploader = prepareUploader(inputData)
        val uploadcareFile = try {
            uploader?.upload(this)
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.failure(workDataOf(KEY_ERROR to e.message))
        } catch (e: RuntimeException){
            e.printStackTrace()
            return Result.failure(workDataOf(KEY_ERROR to e.message))
        }

        uploader = null

        return if (uploadcareFile != null) {
            updateNotificationProgress(
                    applicationContext.getString(R.string.ucw_notification_content_completed),
                    100)
            val fileJson = UploadcareWidget
                    .getInstance().uploadcareClient.objectMapper
                    .toJson(uploadcareFile, UploadcareFile::class.java)
            Result.success(workDataOf(KEY_UPLOADCARE_FILE to  fileJson))
        } else {
            updateNotificationProgress(
                    applicationContext.getString(R.string.ucw_notification_content_error),
                    100)
            Result.failure(workDataOf(KEY_ERROR to "Error uploading file"))
        }
    }

    private fun createNotification(content: String): NotificationCompat.Builder {
        val channelId = applicationContext.getString(R.string.ucw_notification_channel_id)
        val title = applicationContext.getString(R.string.ucw_notification_title)

        // Create a Notification channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel(channelId)
        }

        val notificationBuilder = NotificationCompat.Builder(applicationContext, channelId)
                .setContentTitle(title)
                .setTicker(title)
                .setContentText(content)
                .setSmallIcon(R.drawable.ic_ucw_file_upload)
                .setOngoing(true)

        if (cancelable) {
            val cancel = applicationContext.getString(R.string.ucw_notification_cancel_download)
            // This PendingIntent can be used to cancel the worker
            val intent = WorkManager.getInstance(applicationContext)
                    .createCancelPendingIntent(id)
            // Add the cancel action to the notification which can
            // be used to cancel the worker
            notificationBuilder.addAction(android.R.drawable.ic_delete, cancel, intent)
        }

        return notificationBuilder
    }

    // Creates an instance of ForegroundInfo which can be used to update the
    // ongoing notification.
    private fun createForegroundInfo(notificationBuilder: NotificationCompat.Builder)
            : ForegroundInfo {
        //Notification ID
        return ForegroundInfo(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun updateNotificationProgress(content: String, progress: Int) {
        //Notification ID
        val notification = createNotification(content)
        if (showProgress) {
            notification.setProgress(100, progress, false)
        }

        setForegroundAsync(createForegroundInfo(notification))
    }

    private fun prepareUploader(inputData: Data): Uploader? {
        val storeUponUpload = inputData.getBoolean(KEY_STORE, false)
        val signature = inputData.getString(KEY_SIGNATURE)
        val expire = inputData.getString(KEY_EXPIRE)
        val url = inputData.getString(KEY_FILE_URL)
        val uriString = inputData.getString(KEY_FILE_URI)
        val uri = try {
            uriString?.toUri()
        } catch (e: RuntimeException) {
            null
        }

        var uploader: Uploader? = null
        if (url != null) {
            uploader = UrlUploader(UploadcareWidget
                    .getInstance().uploadcareClient, url)
                    .store(storeUponUpload)
        } else if (uri != null) {
            uploader = FileUploader(UploadcareWidget
                    .getInstance().uploadcareClient, uri, applicationContext)
                    .store(storeUponUpload)
                    .signedUpload(signature ?: "", expire ?: "")
        }

        return uploader
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel(channelId: String) {
        // Create the NotificationChannel
        val name = applicationContext.getString(R.string.ucw_notification_channel_name)
        val descriptionText = applicationContext.getString(R.string.ucw_notification_channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val mChannel = NotificationChannel(channelId, name, importance)
        mChannel.description = descriptionText
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        notificationManager.createNotificationChannel(mChannel)
    }

    override fun onProgressUpdate(bytesWritten: Long, contentLength: Long, progress: Double) {
        //check if worker has been stopped/canceled
        if (isStopped) {
            uploader?.cancel()
            uploader = null
        }

        val progressInt = (progress * 100).roundToInt()
        // Worker progress
        setProgressAsync(workDataOf(KEY_WORKER_PROGRESS to progressInt))
        // Notification progress
        updateNotificationProgress(
                applicationContext.getString(R.string.ucw_notification_content_progress),
                progressInt)
    }

    companion object {
        private const val NOTIFICATION_ID = 641
        internal const val TAG = "uploadcare_worker"
        internal const val KEY_UPLOADCARE_FILE = "KEY_UPLOADCARE_FILE"
        internal const val KEY_WORKER_PROGRESS = "KEY_WORKER_PROGRESS"
        internal const val KEY_ERROR = "KEY_ERROR"
        internal const val KEY_FILE_URI = "KEY_FILE_URI"
        internal const val KEY_FILE_URL = "KEY_FILE_URL"
        internal const val KEY_CANCELABLE = "KEY_CANCELABLE"
        internal const val KEY_SHOW_PROGRESS = "KEY_SHOW_PROGRESS"
        internal const val KEY_STORE = "KEY_STORE"
        internal const val KEY_SIGNATURE = "KEY_SIGNATURE"
        internal const val KEY_EXPIRE = "KEY_EXPIRE"
    }
}