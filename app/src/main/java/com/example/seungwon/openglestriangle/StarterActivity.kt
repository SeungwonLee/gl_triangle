package com.example.seungwon.openglestriangle

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import com.example.seungwon.openglestriangle.blur.BlurActivity
import com.example.seungwon.openglestriangle.blur.framebuffer.Blur2Activity
import com.example.seungwon.openglestriangle.blur.rect.BlurMappingActivity
import com.example.seungwon.openglestriangle.blur.rect.BlurRectActivity
import com.example.seungwon.openglestriangle.blur.rect.BlurScaledActivity
import com.example.seungwon.openglestriangle.blur.rect.glsl.GlslRectBlurActivity
import com.example.seungwon.openglestriangle.blur.stackblur.StackBlurActivity
import com.example.seungwon.openglestriangle.filter.FilterActivity
import com.example.seungwon.openglestriangle.filter.emboss.EmbossActivity
import com.example.seungwon.openglestriangle.framebuffer.FrameBufferRendererActivity
import com.example.seungwon.openglestriangle.mosaic.MosaicActivity
import com.example.seungwon.openglestriangle.mosaic.rect.MosaicRectActivity
import com.example.seungwon.openglestriangle.multiTxt.MultiTxtActivity
import com.example.seungwon.openglestriangle.projection.ProjectionActivity
import com.example.seungwon.openglestriangle.projection.translation.StripActivity
import com.example.seungwon.openglestriangle.text.TextureActivity
import com.example.seungwon.openglestriangle.translation.SquareActivity
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
        findViewById<Button>(R.id.blur_txt_opengl_btn).setOnClickListener {
            startActivity(Intent(this, BlurActivity::class.java))
        }
        findViewById<Button>(R.id.blur_framebuffer_txt_opengl_btn).setOnClickListener {
            startActivity(Intent(this, Blur2Activity::class.java))
        }
        findViewById<Button>(R.id.blur_rect_txt_opengl_btn).setOnClickListener {
            startActivity(Intent(this, BlurRectActivity::class.java))
        }
        findViewById<Button>(R.id.blur_rect_txt_mapping_opengl_btn).setOnClickListener {
            startActivity(Intent(this, BlurMappingActivity::class.java))
        }
        findViewById<Button>(R.id.blur_rect_txt_mapping_transform_opengl_btn).setOnClickListener {
            startActivity(Intent(this, BlurScaledActivity::class.java))
        }
        findViewById<Button>(R.id.blur_function_blur_opengl_btn).setOnClickListener {
            startActivity(Intent(this, GlslRectBlurActivity::class.java))
        }
        findViewById<Button>(R.id.mosaic_txt_opengl_btn).setOnClickListener {
            startActivity(Intent(this, MosaicActivity::class.java))
        }
        findViewById<Button>(R.id.mosaic_rect_txt_opengl_btn).setOnClickListener {
            startActivity(Intent(this, MosaicRectActivity::class.java))
        }
        findViewById<Button>(R.id.blur_stack_blur_opengl_btn).setOnClickListener {
            startActivity(Intent(this, StackBlurActivity::class.java))
        }
    }
}
