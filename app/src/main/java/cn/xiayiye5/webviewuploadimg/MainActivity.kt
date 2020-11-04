package cn.xiayiye5.webviewuploadimg

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun goUpload(view: View) {
        startActivity(Intent(this, WebViewUploadActivity::class.java))
    }

    fun getPermission(view: View) {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            //如果没有权限，开始请求
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), 1000
            )
        } else {
            Toast.makeText(this, "已获取权限", Toast.LENGTH_SHORT).show()
            camera()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        Log.e("打印权限", requestCode.toString() + "")
        for (permission in permissions) {
            Log.e("打印权限名字", permission)
        }
        if (requestCode == 1000) {
            for (grantResult in grantResults) {
                Log.e("打印权限结果", grantResult.toString() + "")
            }
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "权限获取成功", Toast.LENGTH_SHORT).show()
                camera()
            }
        }
    }

    private fun camera() {
        try {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, 1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}