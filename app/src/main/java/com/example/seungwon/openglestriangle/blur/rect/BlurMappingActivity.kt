package com.example.seungwon.openglestriangle.blur.rect

import android.opengl.GLSurfaceView
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import com.example.seungwon.openglestriangle.R

class BlurMappingActivity : AppCompatActivity(), View.OnTouchListener {
    private var glSurfaceView: GLSurfaceView? = null
    private val renderer: BlurRendererWithMapper = BlurRendererWithMapper(this)

    var renderRectWidth: Int = 0
    var renderRectHeight: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blur_mapping)

        glSurfaceView = findViewById(R.id.gl_surface)
        glSurfaceView?.let {
            it.setEGLContextClientVersion(2)
            it.setRenderer(renderer)
            // Draw only `requestRender()` called.
            it.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
            it.setOnTouchListener(this)
        }

        renderer.onSurfaceSizeChanged = {
            renderRectWidth = it.width()
            renderRectHeight = it.height()
        }
        initSeekBar()
        initBlurAlgorithmBtn()
    }

    private fun initBlurAlgorithmBtn() {
        val boxBtn = findViewById<Button>(R.id.box_blur_btn)
        val gaussianBtn = findViewById<Button>(R.id.gaussian_blur_btn)
        val stackBtn = findViewById<Button>(R.id.stack_blur_btn)

        boxBtn.setOnClickListener {
            updateBlurAlgorithm(BlurType.Box)
        }
        gaussianBtn.setOnClickListener {
            updateBlurAlgorithm(BlurType.Gaussian)
        }
        stackBtn.setOnClickListener {
            updateBlurAlgorithm(BlurType.StackBlur)
        }
    }

    private fun getBlurTypeStr(blurType: BlurType): String = when (blurType) {
        BlurType.Gaussian -> "Gaussian Blur"
        BlurType.Box -> "Box Blur"
        BlurType.StackBlur -> "Stack Blur"
    }

    private fun updateBlurAlgorithm(blurType: BlurType) {
        val blurTypeTxt = findViewById<TextView>(R.id.blur_type_txt)
        blurTypeTxt.text = getBlurTypeStr(blurType)

        renderer.blurType = blurType
        glSurfaceView?.requestRender()
    }

    private fun initSeekBar() {
        val blurOffsetSeekBar = findViewById<SeekBar>(R.id.blur_offset_seekbar)
        val blurOffsetSeekBarTextView = findViewById<TextView>(R.id.blur_offset_seekbar_text)
        blurOffsetSeekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val offset = (progress / 100f)
                Log.d("BlurRectActivity", "onProgressChanged $offset")
                glSurfaceView?.queueEvent {
                    renderer.blurOffset = offset
                    glSurfaceView?.requestRender()
                }
                blurOffsetSeekBarTextView.text = "offset($offset): "
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
        })

        val blurIntensitySeekBar = findViewById<SeekBar>(R.id.blur_intensity_seekbar)
        val blurIntensitySeekBarTextView = findViewById<TextView>(R.id.blur_intensity_seekbar_text)
        blurIntensitySeekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val intensity =
                    when {
                        progress < 30 -> {
                            1
                        }
                        progress > 80 -> {
                            3
                        }
                        else -> {
                            2
                        }
                    }
                glSurfaceView?.queueEvent {
                    renderer.loopCount = intensity
                    Log.d("BlurRectActivity", "onProgressChanged2 ${renderer.loopCount}")
                    glSurfaceView?.requestRender()
                }
                blurIntensitySeekBarTextView.text = "intensity($intensity): "
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
        })
    }

    override fun onResume() {
        super.onResume()
        glSurfaceView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        glSurfaceView?.onPause()
    }

    private fun getDeltaX(renderRectWidth: Int, scaledWidth: Float): Float =
        (renderRectWidth - scaledWidth) / 2f

    private fun getScaledFactorX(renderRectWidth: Int, scaledWidth: Float): Float =
        renderRectWidth / scaledWidth

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (event == null) {
            return false
        }

        Log.d(TAG, "onTouch ${event.action} ${event.x} ${event.y}")

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

                // TODO render rect size
                if (BlurRendererWithMapper.SCALED_WIDTH > renderRectWidth ||
                    BlurRendererWithMapper.SCALED_HEIGHT > renderRectHeight
                ) {
                    // TODO
                    Log.d(TAG, "onTouch false")
                    return false
                }
                val deltaX = getDeltaX(renderRectWidth, BlurRendererWithMapper.SCALED_WIDTH)
                val scaleFactorX =
                    getScaledFactorX(renderRectWidth, BlurRendererWithMapper.SCALED_WIDTH)

                val deltaY = getDeltaX(renderRectHeight, BlurRendererWithMapper.SCALED_HEIGHT)
                val scaleFactorY =
                    getScaledFactorX(renderRectHeight, BlurRendererWithMapper.SCALED_HEIGHT)

//                val boundary = Rect(
//                    deltaX.toInt(),
//                    (deltaX + BlurRendererWithMapper.SCALED_WIDTH).toInt(),
//                    deltaY.toInt(),
//                    (deltaY + BlurRendererWithMapper.SCALED_HEIGHT).toInt()
//                )
//                !boundary.contains(
//                    event.x,
//                    event.y
//                )
                val newPositionXForGl = (event.x - deltaX) * scaleFactorX
                val newPositionYForGl = (event.y - deltaY) * scaleFactorY
                Log.d(TAG, "onTouch newPositionXForGl $newPositionXForGl")
                Log.d(TAG, "onTouch scaleFactorX $scaleFactorX")
                Log.d(TAG, "onTouch deltaX $deltaX")

                renderer.textureRectStartPointX = newPositionXForGl
                renderer.textureRectStartPointY = newPositionYForGl

                renderer.vertexRectStartPointX = newPositionXForGl
                renderer.vertexRectStartPointY = newPositionYForGl
//                event.offsetLocation()
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                return true
            }
            MotionEvent.ACTION_UP -> {
                glSurfaceView?.queueEvent {

                    val deltaX = getDeltaX(renderRectWidth, BlurRendererWithMapper.SCALED_WIDTH)
                    val scaleFactorX =
                        getScaledFactorX(renderRectWidth, BlurRendererWithMapper.SCALED_WIDTH)

                    val deltaY = getDeltaX(renderRectHeight, BlurRendererWithMapper.SCALED_HEIGHT)
                    val scaleFactorY =
                        getScaledFactorX(renderRectHeight, BlurRendererWithMapper.SCALED_HEIGHT)

                    val newPositionXForGl = (event.x - deltaX) * scaleFactorX
                    val newPositionYForGl = (event.y - deltaY) * scaleFactorY

//                    Log.d(TAG, "onTouch ${event.x}")
//                    val translatedMatrix = FloatArray(16)
//                    Matrix.setIdentityM(translatedMatrix, 0)
//                    Matrix.translateM(translatedMatrix, 0, -deltaX, -deltaY, 0f)
//
//                    val scaledMatrix = FloatArray(16)
//                    Matrix.setIdentityM(scaledMatrix, 0)
//                    Matrix.scaleM(scaledMatrix, 0, scaleFactorX, scaleFactorY, 0f)
//
//                    val combinedMatrix = FloatArray(16)
//                    Matrix.setIdentityM(combinedMatrix, 0)
//                    Matrix.multiplyMM(combinedMatrix, 0, translatedMatrix, 0, scaledMatrix, 0)
//
//                    val matrix = android.graphics.Matrix()
//                    matrix.setValues(combinedMatrix)
//                    event.transform(matrix)

                    Log.d(TAG, "onTouch transform newPositionXForGl ${event.x}  ${event.y}")
                    Log.d(TAG, "onTouch newPositionXForGl $newPositionXForGl $newPositionYForGl")
                    Log.d(TAG, "onTouch scaleFactorX $scaleFactorX")
                    Log.d(TAG, "onTouch deltaX $deltaX")

                    renderer.textureRectEndPointX = newPositionXForGl
                    renderer.textureRectEndPointY = newPositionYForGl

                    renderer.vertexRectEndPointX = newPositionXForGl
                    renderer.vertexRectEndPointY = newPositionYForGl

                    renderer.onDrawBlurRect { glSurfaceView?.requestRender() }
                }
                return true
            }
        }
        return false
    }

    companion object {
        private const val TAG = "BlurMappingActivity"
    }
}
