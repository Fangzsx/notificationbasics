package com.fangzsx.notificationbasics

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.ClipData
import android.content.ClipDescription
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.DragEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.fangzsx.notificationbasics.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    val CHANNEL_ID = "channelID"
    val CHANNEL_NAME = "channelName"

    val NOTIFICATION_ID = 0

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        createNotificationChannel()


        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(intent)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                getPendingIntent(0, PendingIntent.FLAG_MUTABLE)
            } else {
                getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
            }
        }

        val notificationManager = NotificationManagerCompat.from(this)

        binding.btnSubmit.setOnClickListener {
            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Nagru-run to love sa background. Tap to return to app. dun mo sya i-dismiss")
                .setContentText("Custom Message: ${binding.etContent.text}")
                .setSmallIcon(R.drawable.ic_sttaus_icon)
                .setOngoing(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build()

            notificationManager.notify(NOTIFICATION_ID, notification)
            Snackbar.make(binding.root, "Notification Sent!", Snackbar.LENGTH_SHORT).show()
        }

        binding.btnDismiss.setOnClickListener {
            notificationManager.cancel(NOTIFICATION_ID)
            Snackbar.make(binding.root, "Notification Dismissed!", Snackbar.LENGTH_SHORT).show()
        }

        binding.clock.setOnLongClickListener {
            val parent  = binding.clock.parent as LinearLayout
            val clockStartPosition : String = parent.tag.toString()
            val item = ClipData.Item(clockStartPosition)
            val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
            val data = ClipData(clockStartPosition, mimeTypes, item)

            val dragShadowBuilder = View.DragShadowBuilder(it)
            it.startDragAndDrop(data, dragShadowBuilder, it, 0)

            it.visibility = View.INVISIBLE
            true
        }


        val dragListener = View.OnDragListener { view, event ->
            when(event.action){
                DragEvent.ACTION_DRAG_STARTED -> {
                    event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
                }
                DragEvent.ACTION_DRAG_ENTERED -> {
                    view.invalidate()
                    true
                }
                DragEvent.ACTION_DRAG_LOCATION -> {
                    true
                }
                DragEvent.ACTION_DRAG_EXITED -> {
                    view.invalidate()
                    true
                }
                DragEvent.ACTION_DROP -> {
                    val item = event.clipData.getItemAt(0)
                    val from = item.text

                    view.invalidate()

                    //current drag view
                    val dragView = event.localState as View

                    val owner = dragView.parent as ViewGroup
                    owner.removeView(dragView)

                    val destination = view as LinearLayout
                    destination.addView(dragView)
                    dragView.visibility = View.VISIBLE

                    Toast.makeText(this, "From : $from To: ${destination.tag}", Toast.LENGTH_SHORT).show()
                    true
                }

                DragEvent.ACTION_DRAG_ENDED -> {
                    view.invalidate()
                    true
                }

                else -> false
            }
        }

        binding.llTop.setOnDragListener(dragListener)
        binding.llBottom.setOnDragListener(dragListener)
        binding.llLeft.setOnDragListener(dragListener)
        binding.llRight.setOnDragListener(dragListener)

    }

    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}