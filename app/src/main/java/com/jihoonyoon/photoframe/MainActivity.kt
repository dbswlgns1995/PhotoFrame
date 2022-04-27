package com.jihoonyoon.photoframe

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private val addPhotoBuilder: Button by lazy {
        findViewById(R.id.addPhotoButton)
    }

    private val startPhotoFrameModeButton: Button by lazy {
        findViewById(R.id.startPhotoFrameModeButton)
    }

    private val imageViewList: List<ImageView> by lazy {
        mutableListOf<ImageView>().apply {
            add(findViewById(R.id.imageView11))
            add(findViewById(R.id.imageView12))
            add(findViewById(R.id.imageView13))
            add(findViewById(R.id.imageView21))
            add(findViewById(R.id.imageView22))
            add(findViewById(R.id.imageView23))
        }
    }

    private val imageUriList: MutableList<Uri> = mutableListOf()

    private var getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        val selectedImageUri: Uri? = uri
        if (selectedImageUri != null) {

            if (imageUriList.size == 6) {
                Toast.makeText(this, "이미 사진이 꽉 찼습니다.", Toast.LENGTH_SHORT).show()
                return@registerForActivityResult
            }
            imageUriList.add(selectedImageUri)
            imageViewList[imageUriList.size - 1].setImageURI(selectedImageUri)
        } else {
            Toast.makeText(this, "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){ isGranted: Boolean ->
        if (isGranted){
            navigatePhotos()
        } else {
            Toast.makeText(this, "권한이 거부되었습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initAddPhotoButton()
        initStartPhotoFrameModeButton()
    }

    private fun initAddPhotoButton() {
        addPhotoBuilder.setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // todo 권한이 잘 부여되었을 때 갤러리에서 사진을 선택하는 기능
                    navigatePhotos()
                }
                shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                    showPermissionContextPopup()
                }
                else -> {
                    requestPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }
        }
    }

    private fun navigatePhotos() {
        // todo 권한이 잘 부여되었을 때 갤러리에서 사진을 선택하는 기능
        getContent.launch("image/*")
    }

    private fun showPermissionContextPopup() {
        AlertDialog.Builder(this)
                .setTitle("권한이 필요합니다.")
                .setMessage("전자액자 앱에서 사진을 불러오기 위해 권한이 필요합니다.")
                .setPositiveButton("동의하기") { _, _ ->
                    requestPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                }
                .setNegativeButton("취소하기") { _, _ -> }
                .create()
                .show()
    }

    private fun initStartPhotoFrameModeButton() {
        startPhotoFrameModeButton.setOnClickListener {
            if(imageUriList.count()>0){
                val intent = Intent(this, PhotoFrameActivity::class.java)
                imageUriList.forEachIndexed { index, uri ->
                    intent.putExtra("photo$index", uri.toString())
                }
                intent.putExtra("photoListSize", imageUriList.size)
                startActivity(intent)
            }
        }
    }
}