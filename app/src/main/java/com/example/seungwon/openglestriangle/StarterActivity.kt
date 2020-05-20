package com.example.seungwon.openglestriangle

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import com.example.seungwon.openglestriangle.gles20.Gles20StarterActivity
import com.example.seungwon.openglestriangle.gles30.Gles30StarterActivity

class StarterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_starter)

        findViewById<Button>(R.id.gles_20_btn).setOnClickListener {
            startActivity(Intent(this, Gles20StarterActivity::class.java))
        }

        findViewById<Button>(R.id.gles_30_btn).setOnClickListener {
            startActivity(Intent(this, Gles30StarterActivity::class.java))
        }
    }
}
