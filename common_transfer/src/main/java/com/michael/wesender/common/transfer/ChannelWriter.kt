package com.michael.wesender.common.transfer

import java.nio.ByteBuffer
import java.nio.channels.SocketChannel
import kotlin.math.min

class ChannelWriter {
    private val mWriteBuffer = ByteBuffer.allocate(SocketDTO.BUFFER_SIZE)

    fun write(socketChannel: SocketChannel, socketDTO: SocketDTO,
              offset: Int = 0, length: Int = socketDTO.mSize): Boolean {
        mWriteBuffer.clear()
        if (offset <= 0) {
            mWriteBuffer.putInt(socketDTO.mSize)
            mWriteBuffer.put(socketDTO.mType.toByte())
        }
        val remaining = mWriteBuffer.remaining()
        val write = min(remaining, length)
        mWriteBuffer.put(socketDTO.mData, offset, write)
        val position = offset + write
        mWriteBuffer.flip()
        socketChannel.write(mWriteBuffer)
        if (socketDTO.mSize > position) {
            return write(socketChannel, socketDTO, position, socketDTO.mSize - position)
        }
        return true
    }

    private fun writeInternal(socketChannel: SocketChannel, data: ByteBuffer) {
        socketChannel.write(data)
    }
}