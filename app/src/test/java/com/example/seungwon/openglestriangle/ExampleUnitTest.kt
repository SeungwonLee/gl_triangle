package com.example.seungwon.openglestriangle

import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun test() {
        repeat(3) {
            System.out.println("hi")
        }
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
