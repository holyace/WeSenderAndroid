package com.michael.wesender.common.socket

import com.michael.wesender.common.transfer.SocketDTO
import java.nio.channels.SelectableChannel
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel

interface ISocketProxy {

    fun onAccept(server: ServerSocketChannel, client: SocketChannel) {}

    fun onReadable(channel: SelectableChannel) {}

    fun onWriteable(channel: SelectableChannel) {}

    fun onConnect(channel: SocketChannel) {}

    fun onDisconnect(channel: SelectableChannel) {}

    fun sendData(channel: SocketChannel, socketDTO: SocketDTO) {}
}