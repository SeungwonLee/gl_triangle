package com.example.seungwon.openglestriangle.gles30

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import com.example.seungwon.openglestriangle.R
import com.example.seungwon.openglestriangle.gles30.triangle.TriangleActivity

class Gles30StarterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gles30_starter)

        findViewById<Button>(R.id.gles_30_triangle_btn).setOnClickListener {
            startActivity(Intent(this, TriangleActivity::class.java))
        }
    }
}
