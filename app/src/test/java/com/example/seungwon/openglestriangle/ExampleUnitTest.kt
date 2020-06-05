package com.example.seungwon.openglestriangle

import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

class ExampleUnitTest {

    @Test
    fun testA() {
        //scale first
        val ratioW = 810
        val ratioH = 1440
        val x = 364f
        val y = 549f
        val x1 = 879f
        val y1 = 1069f

        val cx = 1440f / 2
        val cy = 2240f / 2
        val degree = Math.toRadians(90.0)
        println("ccw")
        println(ccwX(degree, x, y, cx, cy))
        println(ccwY(degree, x, y, cx, cy))
        println(ccwX(degree, x1, y1, cx, cy))
        println(ccwY(degree, x1, y1, cx, cy))

        println()
        println("cw")
        println(cwX(degree, x, y, cx, cy))
        println(cwY(degree, x, y, cx, cy))
        println(cwX(degree, x1, y1, cx, cy))
        println(cwY(degree, x1, y1, cx, cy))
    }

    private fun ccwY(degree: Double, x: Float, y: Float, cx: Float, cy: Float): Double {
        return (x - cx) * Math.sin(degree) + (y - cy) * Math.cos(degree) + cy
    }

    private fun ccwX(degree: Double, x: Float, y: Float, cx: Float, cy: Float): Double {
        return (x - cx) * Math.cos(degree) - (y - cy) * Math.sin(degree) + cx
    }

    private fun cwY(degree: Double, x: Float, y: Float, cx: Float, cy: Float): Double {
        return (y - cy) * Math.cos(degree) - ((x - cx) * Math.sin(degree)) + cy
    }

    private fun cwX(degree: Double, x: Float, y: Float, cx: Float, cy: Float): Double {
        return (x - cx) * Math.cos(degree) + (y - cy) * Math.sin(degree) + cx
    }


    @Test
    fun addition_isCorrect() {
        val sampler = 9
        var offset = 1 / 1440f
        println("Blur test offset ${String.format("%.9f", offset)}")
        for (i in 0 until sampler) {
            val multipler = (i - (sampler - 1) / 2f)
            val blurStep = multipler * offset
            val coords = 0.0 + blurStep

            println(
                "Blur test i=$i m=$multipler " +
                        "b=${String.format("%.9f", blurStep)} " +
                        "c=${String.format("%.9f", coords)}"
            )
        }

        println("")
        offset = 1 / 1440f / 300
        println("Blur test offset ${String.format("%.9f", offset)}")
        for (i in 0 until sampler) {
            val multipler = (i - (sampler - 1) / 2f)
            val blurStep = multipler * offset
            val coords = 0.0 + blurStep

            println(
                "Blur test i=$i m=$multipler " +
                        "b=${String.format("%.9f", blurStep)} " +
                        "c=${String.format("%.9f", coords)}"
            )
        }

        println("")
        offset = 0.003f
        println("Blur test offset ${String.format("%.9f", offset)}")
        for (i in 0 until sampler) {
            val multipler = (i - (sampler - 1) / 2f)
            val blurStep = multipler * offset
            val coords = 0.0 + blurStep

            println(
                "Blur test i=$i m=$multipler " +
                        "b=${String.format("%.9f", blurStep)} " +
                        "c=${String.format("%.9f", coords)}"
            )
        }

        println("")
        offset = 0.001f
        println("Blur test offset ${String.format("%.9f", offset)}")
        for (i in 0 until sampler) {
            val multipler = (i - (sampler - 1) / 2f)
            val blurStep = multipler * offset
            val coords = 0.0 + blurStep

            println(
                "Blur test i=$i m=$multipler " +
                        "b=${String.format("%.9f", blurStep)} " +
                        "c=${String.format("%.9f", coords)}"
            )
        }

        println("")
        offset = 0.002f
        println("Blur test offset ${String.format("%.9f", offset)}")
        for (i in 0 until sampler) {
            val multipler = (i - (sampler - 1) / 2f)
            val blurStep = multipler * offset
            val coords = 0.0 + blurStep

            println(
                "Blur test i=$i m=$multipler " +
                        "b=${String.format("%.9f", blurStep)} " +
                        "c=${String.format("%.9f", coords)}"
            )
        }

        println("")
        offset = 0.004f
        println("Blur test offset ${String.format("%.9f", offset)}")
        for (i in 0 until sampler) {
            val multipler = (i - (sampler - 1) / 2f)
            val blurStep = multipler * offset
            val coords = 0.0 + blurStep

            println(
                "Blur test i=$i m=$multipler " +
                        "b=${String.format("%.9f", blurStep)} " +
                        "c=${String.format("%.9f", coords)}"
            )
        }
    }
}
