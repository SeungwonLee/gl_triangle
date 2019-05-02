package com.example.seungwon.openglestriangle

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.seungwon.openglestriangle.square.SquareActivity
import com.example.seungwon.openglestriangle.strip.StripActivity
import com.example.seungwon.openglestriangle.text.TextureActivity
import com.example.seungwon.openglestriangle.triangle.MainActivity

class StarterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_starter)

        findViewById<Button>(R.id.triangle_opengl_btn).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
        findViewById<Button>(R.id.square_opengl_btn).setOnClickListener {
            startActivity(Intent(this, SquareActivity::class.java))
        }
        findViewById<Button>(R.id.txt_opengl_btn).setOnClickListener {
            startActivity(Intent(this, TextureActivity::class.java))
        }
        findViewById<Button>(R.id.strip_txt_opengl_btn).setOnClickListener {
            startActivity(Intent(this, StripActivity::class.java))
        }
    }
}
