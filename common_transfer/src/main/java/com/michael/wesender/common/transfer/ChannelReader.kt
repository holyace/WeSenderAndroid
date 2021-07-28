package com.michael.wesender.common.transfer

import com.michael.wesender.common.transfer.ext.isEmpty
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel
import kotlin.math.min

class ChannelReader {

    private val mReadBuffer = ByteBuffer.allocate(SocketDTO.BUFFER_SIZE)

    fun read(socketChannel: SocketChannel, readTo: SocketDTO? = null, offset: Int = 0): SocketDTO? {
        var totalRead = offset
        var ret = readTo
        if (!mReadBuffer.isEmpty()) {
            ret = readTo?: SocketDTO()
            totalRead += readBuffer(mReadBuffer, ret, offset)
        }

        if (ret != null && ret.mSize > 0 && ret.mSize - totalRead <= 0) return ret

        do {
            mReadBuffer.clear()
            val read = socketChannel.read(mReadBuffer)
        } while (read <= 0)

        if (ret == null) ret = SocketDTO()

        mReadBuffer.flip()
        totalRead += readBuffer(mReadBuffer, ret, totalRead)

        if (ret.mSize - totalRead > 0) {
            read(socketChannel, ret, totalRead)
        }
        return ret
    }

    private fun readBuffer(byteBuffer: ByteBuffer, dest: SocketDTO, offset: Int = 0): Int {
        if (dest.mSize == 0 && byteBuffer.remaining() >= 4) {
            dest.mSize = byteBuffer.int
            dest.mData = ByteArray(dest.mSize)
        }
        if (dest.mType == SocketDTO.TYPE_UNKNOWN && byteBuffer.hasRemaining()) {
            dest.mType = byteBuffer.get().toInt()
        }
        val leftToRead = dest.mSize - offset
        val realRead = min(byteBuffer.remaining(), leftToRead)
        if (realRead > 0) {
            byteBuffer.get(dest.mData, offset, realRead)
        }
        return realRead
    }
}