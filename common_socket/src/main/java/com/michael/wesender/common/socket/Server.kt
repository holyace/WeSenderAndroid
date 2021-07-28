package com.michael.wesender.common.socket

import com.michael.wesender.common.transfer.SocketDTO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.InetSocketAddress
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel

class Server(private val host: String,
             private val port: Int,
             private val proxy: ISocketProxy)
    : Thread() {

    @Volatile
    private var mRunning = true

    private val mServerChannel by lazy { ServerSocketChannel.open() }
    private val mSelector by lazy { Selector.open() }
    private var mClientChannel: SocketChannel? = null

    override fun run() {
        super.run()

        try {
            safeRun()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stopServer() {
        mRunning = false
        mSelector.close()
        mServerChannel.close()
    }

    private fun safeRun() {

        mServerChannel.configureBlocking(false)
        mServerChannel.socket().bind(InetSocketAddress(host, port))

        mServerChannel.register(mSelector, SelectionKey.OP_ACCEPT)

        while (mRunning) {
            val readyCount = mSelector.select()
            if (readyCount <= 0) continue

            mSelector.selectedKeys().forEach {

                when {
                    it.isAcceptable -> onAccept(it)

                    it.isReadable -> onReadable(it)
                }
            }
        }
    }

    private fun onAccept(selectionKey: SelectionKey) {
        val serverChannel = selectionKey.channel() as ServerSocketChannel
        val clientChannel: SocketChannel? = serverChannel.accept()
        clientChannel?: return
        clientChannel.configureBlocking(false)
        clientChannel.register(mSelector, SelectionKey.OP_READ or SelectionKey.OP_WRITE)
        proxy.onAccept(serverChannel, clientChannel)
    }

    private fun onReadable(selectionKey: SelectionKey) {
        val clientChannel = selectionKey.channel() as SocketChannel
        try {
            proxy.onReadable(clientChannel)
        } catch (e: Exception) {
            e.printStackTrace()
            proxy.onDisconnect(clientChannel)
        }
    }

    fun sendData(socketDTO: SocketDTO) {
        mClientChannel?: return
        GlobalScope.launch {
            try {
                proxy.sendData(mClientChannel!!, socketDTO)
            } catch (e: Exception) {
                proxy.onDisconnect(mClientChannel!!)
            }
        }
    }
}