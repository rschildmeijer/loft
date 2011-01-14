package io.loft

import java.net.InetSocketAddress
import java.nio.channels.SocketChannel
import scala.collection.mutable.MutableList
import java.nio.channels.SelectableChannel

class IOStream(channel: SelectableChannel, maxBufferSize: Long = 104857600, readChunkSize: Int = 4096) {

  channel.configureBlocking(false)

  private val readBuffer = new StringBuilder()
  private val writeBuffer = new StringBuilder()
  private var readDelimeter = ' '
  private var readBytes = 0

  private var readCallback: (String) => Unit = null
  private var writeCallback: () => Unit = null
  private var closeCallback: () => Unit = null
  private var connectCallback: () => Unit = null

  private var connecting = false

  def connect(address: (String, Int), connectCallback: () => Unit = null) {
    channel match {
      case c: SocketChannel => {
        c.connect(new InetSocketAddress(address._1, address._2))
        this.connectCallback = connectCallback
      }
    }
  }

}