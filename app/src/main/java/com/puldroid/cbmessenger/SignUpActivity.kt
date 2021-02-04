package com.puldroid.cbmessenger

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {

  val userImgView by lazy {
    findViewById<ShapeableImageView>(R.id.userImgView)
  }

  val continueBtn by lazy {
    findViewById<Button>(R.id.nextBtn)
  }

  val nameEt by lazy {
    findViewById<EditText>(R.id.nameEt)
  }
  lateinit var downloadUrl: String

  lateinit var auth: FirebaseAuth

  val database by lazy {
    FirebaseFirestore.getInstance()
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_sign_up)
    auth = Firebase.auth
    userImgView.setOnClickListener {
      openGallery()
    }
    continueBtn.setOnClickListener {
      if (!::downloadUrl.isInitialized) {
        Toast.makeText(this, "image cannot be empty", Toast.LENGTH_SHORT).show()
      } else if (nameEt.text.isEmpty()) {
        Toast.makeText(this, "name cannot be empty", Toast.LENGTH_SHORT).show()
      } else {
        val user = User(nameEt.text.toString(), downloadUrl, auth.uid!!)
        database.collection("users").document(auth.uid!!).set(user).addOnSuccessListener {
          startActivity(Intent(this, MainActivity::class.java))
          finish()
        }.addOnFailureListener {

        }
      }
    }

  }

  override fun onBackPressed() {

  }

  private fun openGallery() {
    val intent = Intent()
    intent.type = "image/*"
    startActivityForResult(intent, 1001)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
    super.onActivityResult(requestCode, resultCode, intent)
    if (resultCode == Activity.RESULT_OK && requestCode == 1001) {
      intent?.data?.let { image ->
        userImgView.setImageURI(image)
        uploadImage(image)
      }
    }
  }

  private fun uploadImage(image: Uri) {
    continueBtn.isEnabled = false
    val ref = FirebaseStorage.getInstance().reference.child("uploads/${auth.uid.toString()}")
    val uploadTask = ref.putFile(image)
    uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
      if (!task.isSuccessful) {
        //Handle error
        Log.e("Error uploading", task.exception.toString())
      }
      return@Continuation ref.downloadUrl
    }).addOnCompleteListener { task ->
      continueBtn.isEnabled = true
      if (task.isSuccessful) {
        Log.e("Done uploading", task.result.toString())
        downloadUrl = task.result.toString()
      }

    }.addOnFailureListener {
      continueBtn.isEnabled = true

    }

  }
}