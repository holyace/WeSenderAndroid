package com.michael.wesender.common.transfer

import org.json.JSONObject
import java.nio.charset.Charset

class SocketDTO internal constructor() {

    companion object {
        const val BUFFER_SIZE = 10

        const val TYPE_UNKNOWN = 0
        const val TYPE_STRING = 1
        const val TYPE_BINARY = 2
        const val TYPE_JSON = 3

        fun of(data: String, charset: Charset = Charsets.UTF_8): SocketDTO {
            return SocketDTO().apply {
                mType = TYPE_STRING
                mData = data.toByteArray(charset)
                mSize = mData!!.size
            }
        }

        fun of(data: ByteArray, offset: Int = 0, length: Int = data.size): SocketDTO {
            return SocketDTO().apply {
                mType = TYPE_BINARY
                val dataOfCopy = ByteArray(length)
                System.arraycopy(data, offset, dataOfCopy, 0, length)
                mData = dataOfCopy
                mSize = length
            }
        }

//        fun of(any: Any, charset: Charset = Charsets.UTF_8): SocketDTO {
//            val json = JSONObject(any).toString()
//            return SocketDTO().apply {
//                mData = json.toByteArray(charset)
//                mType = TYPE_JSON
//                mSize = mData?.size?: 0
//            }
//        }
    }

    internal var mSize = 0
    internal var mType = TYPE_UNKNOWN
    internal var mData: ByteArray? = null

    fun getSize(): Int = mSize

    fun getType(): Int = mType

    fun getData(): ByteArray? = mData
}