package com.michael.wesender.android

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.michael.wesender.common.socket.ISocketProxy
import com.michael.wesender.common.socket.Server
import com.michael.wesender.common.transfer.ChannelReader
import com.michael.wesender.common.transfer.SocketDTO
import java.nio.channels.SelectableChannel
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel

class MainActivity: AppCompatActivity(), ISocketProxy {

    private val HOST = "localhost"
    private val PORT = 8999
    private var mServer: Server? = null
    private var mTextView: TextView? = null
    private val mReader by lazy { ChannelReader() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mTextView = TextView(this)
        setContentView(mTextView)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopServer()
    }

    override fun onPause() {
        super.onPause()
        stopServer()
    }

    override fun onResume() {
        super.onResume()
        startServer()
    }

    private fun startServer() {
        if (mServer?.isAlive == true) return
        mServer = Server(HOST, PORT, this)
        mServer!!.start()
        showMessage("start server on port: $PORT")
    }

    private fun stopServer() {
        if (mServer?.isAlive != true) return
        mServer!!.stopServer()
    }

    override fun onAccept(server: ServerSocketChannel, client: SocketChannel) {
        super.onAccept(server, client)
        showMessage("client connect $client")
    }

    override fun onReadable(channel: SelectableChannel) {
        super.onReadable(channel)
        if (channel !is SocketChannel) return
        val dto = mReader.read(channel)
        showMessage("onReadable $dto")
        dto?: return
//        if (dto.getType() != SocketDTO.TYPE_STRING) return
        showMessage(String(dto.getData()!!, 0, dto.getSize(), Charsets.UTF_8))
    }

    private fun showMessage(msg: String) {
        runOnUiThread {
            mTextView?: return@runOnUiThread
            mTextView?.append("\n" + msg)
        }
    }
}