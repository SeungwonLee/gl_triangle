package com.example.seungwon.openglestriangle

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import com.example.seungwon.openglestriangle.filter.FilterActivity
import com.example.seungwon.openglestriangle.filter.emboss.EmbossActivity
import com.example.seungwon.openglestriangle.framebuffer.FrameBufferRendererActivity
import com.example.seungwon.openglestriangle.multiTxt.MultiTxtActivity
import com.example.seungwon.openglestriangle.projection.ProjectionActivity
import com.example.seungwon.openglestriangle.translation.SquareActivity
import com.example.seungwon.openglestriangle.projection.translation.StripActivity
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
        findViewById<Button>(R.id.strip_projection_txt_opengl_btn).setOnClickListener {
            startActivity(Intent(this, ProjectionActivity::class.java))
        }
        findViewById<Button>(R.id.strip_txt_opengl_btn).setOnClickListener {
            startActivity(Intent(this, StripActivity::class.java))
        }
        findViewById<Button>(R.id.filter_txt_opengl_btn).setOnClickListener {
            startActivity(Intent(this, FilterActivity::class.java))
        }
        findViewById<Button>(R.id.filter_emboss_txt_opengl_btn).setOnClickListener {
            startActivity(Intent(this, EmbossActivity::class.java))
        }
        findViewById<Button>(R.id.multi_txt_opengl_btn).setOnClickListener {
            startActivity(Intent(this, MultiTxtActivity::class.java))
        }
        findViewById<Button>(R.id.framebuffer_txt_opengl_btn).setOnClickListener {
            startActivity(Intent(this, FrameBufferRendererActivity::class.java))
        }
    }
}
