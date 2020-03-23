package com.example.seungwon.openglestriangle.util

fun getTempFilePath(
    parentDir: String,
    postString: String = ""
): String = "${parentDir}/${System.currentTimeMillis()}_${postString}.jpg"