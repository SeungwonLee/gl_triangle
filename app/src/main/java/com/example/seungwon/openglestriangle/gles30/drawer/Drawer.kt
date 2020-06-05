package com.example.seungwon.openglestriangle.gles30.drawer

interface Drawer {
    fun init(program: Int)
    fun draw(program: Int)
    fun release()
}