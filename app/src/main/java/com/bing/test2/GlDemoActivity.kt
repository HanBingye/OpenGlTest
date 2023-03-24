package com.bing.test2

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_gl_demo.*

class GlDemoActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gl_demo)
        tvHockey.setOnClickListener {
            startActivity(Intent(this,OpenGlActivity::class.java))
        }
        tvParticles.setOnClickListener {
            startActivity(Intent(this,ParticlesActivity::class.java))
        }
    }
}