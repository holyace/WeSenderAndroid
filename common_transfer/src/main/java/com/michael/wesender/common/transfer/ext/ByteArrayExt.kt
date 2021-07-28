package com.michael.wesender.common.transfer.ext

fun ByteArray.startWith(other: ByteArray): Boolean {
    if (this.size < other.size) return false
    other.forEachIndexed { index, byte ->
        if (this[index] != byte) return false
    }
    return true
}

fun ByteArray.contentEquals(offset: Int = 0, other: ByteArray,
                            start: Int = 0, length: Int = other.size)
        : Boolean {
    if (offset + length <= this.size) return false
    for (i: Int in start..start + length) {
        if (other[i] != this[offset + i]) return false
    }
    return true
}