package com.app.sharedfast

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.splash_screen)

		val logo: ImageView = findViewById(R.id.logo)
		val appName: TextView = findViewById(R.id.app_name)
		val tagline: TextView = findViewById(R.id.tagline)

		val anim = AnimationUtils.loadAnimation(this, R.anim.logo_anim)
		logo.startAnimation(anim)
		appName.startAnimation(anim)
		tagline.startAnimation(anim)

		Handler(Looper.getMainLooper()).postDelayed({
			startActivity(Intent(this, MainActivity::class.java))
			finish()
		}, 2500)
	}
}
