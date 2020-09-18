package it.uniparthenope.francescobottino001.labucaasd

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

@SuppressLint("Registered")
open class BasicActivity : AppCompatActivity() {
    lateinit var app: App

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = this.applicationContext as App
    }

    override fun onResume() {
        super.onResume()
        app.currentActivity = this
    }

    override fun onPause() {
        clearReferences()
        super.onPause()
    }

    override fun onDestroy() {
        clearReferences()
        super.onDestroy()
    }

    private fun clearReferences() {
        val currActivity: BasicActivity? = app.currentActivity
        if (this == currActivity) app.currentActivity = null
    }

    fun goToActivity(activityClass: Class<out BasicActivity>) {
        val intent = Intent(this, activityClass)
        this.startActivity(intent)
    }
}