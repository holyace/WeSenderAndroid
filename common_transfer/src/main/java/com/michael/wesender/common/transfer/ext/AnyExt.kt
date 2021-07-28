package com.michael.wesender.common.transfer.ext

fun Any?.hexHash(): String {
    this?: return "null"
    return "0x${Integer.toHexString(this.hashCode())}"
}