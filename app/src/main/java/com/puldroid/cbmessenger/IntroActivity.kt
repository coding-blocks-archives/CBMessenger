package com.puldroid.cbmessenger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import kotlinx.android.synthetic.main.activity_intro.*

class IntroActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_intro)

    findViewById<Button>(R.id.nextBtn).setOnClickListener {
      startActivity(Intent(this, PhoneNoActivity::class.java))
    }
  }
}