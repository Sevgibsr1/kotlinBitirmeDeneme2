package com.surendramaran.yolov8tflite

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class SplashActivity : AppCompatActivity() {
    
    companion object {
        private const val CAMERA_PERMISSION_REQUEST = 100
        private const val CALL_PERMISSION_REQUEST = 101
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            // Galeriden seçilen resmi MainActivity'ye gönder
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("IMAGE_URI", uri.toString())
                putExtra("SOURCE", "gallery")
            }
            startActivity(intent)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Kamerayı başlat butonu
        findViewById<Button>(R.id.startButton).setOnClickListener {
            if (checkCameraPermission()) {
                startMainActivity()
            } else {
                requestCameraPermission()
            }
        }

        // Galeriden seç butonu
        findViewById<Button>(R.id.galleryButton).setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        // Ayarlar butonu
        findViewById<Button>(R.id.settingsButton).setOnClickListener {
            showSettingsDialog()
        }

        // 112'yi Ara butonu
        findViewById<Button>(R.id.aboutButton).setOnClickListener {
            if (checkCallPermission()) {
                call112()
            } else {
                requestCallPermission()
            }
        }
    }

    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkCallPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CALL_PHONE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST
        )
    }

    private fun requestCallPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CALL_PHONE),
            CALL_PERMISSION_REQUEST
        )
    }

    private fun call112() {
        try {
            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("tel:112")
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Arama yapılırken bir hata oluştu", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun showSettingsDialog() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Ayarlar")
            .setMessage("Yakında eklenecek özellikler:\n\n" +
                    "- Model seçimi\n" +
                    "- Algılama hassasiyeti\n" +
                    "- Kamera çözünürlüğü\n" +
                    "- Karanlık/Aydınlık tema\n" +
                    "- Dil seçenekleri")
            .setPositiveButton("Tamam", null)
            .show()
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("SOURCE", "camera")
        }
        startActivity(intent)
        finish()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        when (requestCode) {
            CAMERA_PERMISSION_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startMainActivity()
                } else {
                    Toast.makeText(
                        this,
                        "Kamera izni olmadan uygulama çalışamaz",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            CALL_PERMISSION_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    call112()
                } else {
                    Toast.makeText(
                        this,
                        "Arama yapabilmek için izin gerekli",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
} 