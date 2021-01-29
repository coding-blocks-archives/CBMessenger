package com.puldroid.cbmessenger

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Message
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_otp.*
import java.lang.Exception
import java.util.concurrent.TimeUnit

class OtpActivity : AppCompatActivity() {

  val verifyTv by lazy {
    findViewById<TextView>(R.id.verifyTv)
  }
  val counterTv by lazy {
    findViewById<TextView>(R.id.counterTv)
  }
  val otpEt by lazy {
    findViewById<TextView>(R.id.firstPinView)
  }
  val verificationBtn by lazy {
    findViewById<Button>(R.id.verificationBtn)
  }
  val resendBtn by lazy {
    findViewById<Button>(R.id.resendBtn)
  }
  lateinit var phoneNumber: String

  private lateinit var auth: FirebaseAuth
  private var storedVerificationId: String? = ""
  private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
  private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
  private lateinit var progressDialog: ProgressDialog
  private lateinit var mCounter: CountDownTimer

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_otp)
    init()
    startVerify()

  }

  private fun startVerify() {
    startPhoneNumberVerification(phoneNumber)
    startCounter(60000)
    progressDialog = createDialog("Sending verification code", false)
    progressDialog.show()
  }

  private fun startCounter(time: Long) {
    resendBtn.isEnabled = false
    counterTv.isVisible = true
    mCounter = object : CountDownTimer(time, 1000) {
      override fun onFinish() {
        resendBtn.isEnabled = true
        counterTv.isVisible = false
      }

      override fun onTick(timeLeft: Long) {

        counterTv.text = "Seconds Remaining : " + timeLeft / 1000
      }

    }.start()
  }

  private fun init() {
    //Init views
    verificationBtn.setOnClickListener {
      val credential =
        PhoneAuthProvider.getCredential(storedVerificationId!!, otpEt.text.toString())
      signInWithAuth(credential)
    }
    resendBtn.setOnClickListener {
      resendVerificationCode(phoneNumber, resendToken)
      startCounter(60000)
      progressDialog = createDialog("Sending verification code again", false)
      progressDialog.show()
    }

    try {
      phoneNumber = intent.getStringExtra(PHONE_NUMBER)!!
      verifyTv.text = "Verify $phoneNumber"
    } catch (e: Exception) {
      Toast.makeText(this, "Number not found, Try Again!!!", Toast.LENGTH_SHORT).show()
      onBackPressed()
    }


    auth = Firebase.auth
    callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
      override fun onCodeSent(
        verificationId: String, token: PhoneAuthProvider.ForceResendingToken
      ) {
        super.onCodeSent(verificationId, token)
        progressDialog.dismiss()

        storedVerificationId = verificationId
        resendToken = token
      }

      override fun onVerificationCompleted(credential: PhoneAuthCredential) {

        progressDialog.dismiss()

        val smsCode = credential.smsCode
        otpEt.text = smsCode
        Log.i("Verification Completed", "The verification has been completed")
        signInWithAuth(credential)

      }

      override fun onVerificationFailed(e: FirebaseException) {

        progressDialog.dismiss()

        if (e is FirebaseAuthInvalidCredentialsException) {
          // Invalid request
          Snackbar.make(
            findViewById(android.R.id.content), "Invalid Phone Number.", Snackbar.LENGTH_SHORT
          ).show()
        } else if (e is FirebaseTooManyRequestsException) {
          // The SMS quota for the project has been exceeded
          Snackbar.make(
            findViewById(android.R.id.content), "Quota exceeded.", Snackbar.LENGTH_SHORT
          ).show()
        }
      }

    }
  }

  private fun startPhoneNumberVerification(phoneNumber: String) {
    val options =
      PhoneAuthOptions.newBuilder(auth).setPhoneNumber(phoneNumber)       // Phone number to verify
        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
        .setActivity(this)                 // Activity (for callback binding)
        .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
        .build()
    PhoneAuthProvider.verifyPhoneNumber(options)

  }

  private fun signInWithAuth(credential: PhoneAuthCredential) {
    auth.signInWithCredential(credential).addOnCompleteListener { task ->
      if (task.isSuccessful) {
        if (task.result?.additionalUserInfo?.isNewUser == true) {
          showSignUpActivity()
        } else {
          showHomeActivity()
        }
      } else {
        progressDialog = createDialog("Phone Number verification failed.", false)
        progressDialog.show()
      }

    }
  }

  private fun showHomeActivity() {
    startActivity(Intent(this, MainActivity::class.java))
    finish()
  }

  private fun showSignUpActivity() {
    startActivity(Intent(this, MainActivity::class.java))
    finish()
  }

  private fun resendVerificationCode(
    phoneNumber: String, token: PhoneAuthProvider.ForceResendingToken?
  ) {
    val optionsBuilder =
      PhoneAuthOptions.newBuilder(auth).setPhoneNumber(phoneNumber)       // Phone number to verify
        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
        .setActivity(this)                 // Activity (for callback binding)
        .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
    if (token != null) {
      optionsBuilder.setForceResendingToken(token) // callback's ForceResendingToken
    }
    PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
  }
}

//Extensions Functions
fun Context.createDialog(message: String, isCancelable: Boolean): ProgressDialog {
  return ProgressDialog(this).apply {
    setCancelable(isCancelable)
    setMessage(message)
    setCanceledOnTouchOutside(false)
  }
}