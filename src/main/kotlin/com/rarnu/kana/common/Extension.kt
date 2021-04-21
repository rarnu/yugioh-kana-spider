package com.rarnu.kana.common

fun String.mt(): String = replace("'","\\'")

fun String.toDBC(): String {
    val c = toCharArray()
    for (i in c.indices) {
        if (c[i] == '\u3000') {
            c[i] = ' '
        } else if (c[i] in '\uFF00'..'\uFF5F') {
            c[i] = c[i] - 65248
        }
    }
    return String(c)
}