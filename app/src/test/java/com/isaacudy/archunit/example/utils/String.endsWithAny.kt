package com.isaacudy.archunit.example.utils


fun String.endsWithAny(vararg endsWith: String): Boolean {
    endsWith.forEach {
        if (this.endsWith(it)) return true
    }
    return false
}