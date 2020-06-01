package com.example.seungwon.openglestriangle.gles30.triangle

import android.app.ActivityManager
import android.content.Context
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.util.Log
import com.example.seungwon.openglestriangle.BaseActivity30

class TriangleActivity : BaseActivity30() {
    override fun getRenderer(): GLSurfaceView.Renderer = TriangleRenderer(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val info = activityManager.deviceConfigurationInfo
        Log.d("TAG", "info.glEsVersion ${info.glEsVersion}")
    }
}
