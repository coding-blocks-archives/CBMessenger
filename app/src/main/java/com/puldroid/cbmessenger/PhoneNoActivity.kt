package com.puldroid.cbmessenger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import com.hbb20.CountryCodePicker
import kotlinx.android.synthetic.main.activity_phone_no.*

const val PHONE_NUMBER = "phone"

class PhoneNoActivity : AppCompatActivity() {

  val phoneNoEt: EditText by lazy {
    findViewById<EditText>(R.id.mobileET)
  }

  val button: Button by lazy {
    findViewById<Button>(R.id.nextBtn)
  }

  lateinit var countryCode: String
  lateinit var phoneNumber: String

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_phone_no)

    phoneNoEt.addTextChangedListener { value ->
      button.isEnabled = !(value.isNullOrEmpty() || value.length < 10)
    }

    nextBtn.setOnClickListener {
      checkNumber()
    }
  }

  private fun checkNumber() {
    countryCode = findViewById<CountryCodePicker>(R.id.cpp).selectedCountryCodeWithPlus
    phoneNumber = countryCode + phoneNoEt.text.toString()

    //Add some validation here
    startActivity(Intent(this, OtpActivity::class.java).putExtra(PHONE_NUMBER, phoneNumber))
  }
}