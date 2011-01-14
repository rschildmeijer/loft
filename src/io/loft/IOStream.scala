package io.loft

import java.nio.channels.SelectionKey
import java.net.InetSocketAddress
import java.nio.channels.SocketChannel
import scala.collection.mutable.MutableList
import java.nio.channels.SelectableChannel

class IOStream(channel: SelectableChannel, maxBufferSize: Long = 104857600, readChunkSize: Int = 4096) {

  channel.configureBlocking(false)

  private val readBuffer = new StringBuilder()
  private val writeBuffer = new StringBuilder()
  private var readDelimeter = ""
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

  def readUntil(delimeter: String, callback: (String) => Unit) {
    readCallback = callback
    readDelimeter = delimeter

    var continue = true

    while (continue) {
      if (readFromBuffer()) return
      checkClosed()
      if (readToBuffer() == 0) continue = false
    }
    addIOState(SelectionKey.OP_READ)
  }

  def readFromBuffer(): Boolean = {
    if (readBytes != 0 && readBuffer.size >= readBytes) {
      val numBytes = readBytes
      val callback = readCallback
      readCallback = null
      readBytes = 0
      runCallback(callback, consume(numBytes))
      return true
    } else if (!readDelimeter.isEmpty) {
      val loc = readBuffer.indexOf(readDelimeter)
      if (loc != -1) {
        val callback = readCallback
        val delimeterLength = readDelimeter.size
        readCallback = null
        readDelimeter = ""
        runCallback(callback, consume(loc + delimeterLength))
        return true
      }
    }
    false
  }

  def checkClosed() {

  }

  def readToBuffer(): Int = {
    2
  }

  def addIOState(ops: Int) {

  }

  def runCallback(callback: (String) => Unit, data: String) = callback(data)

  def consume(numBytes: Int): String = {
    ""
  }

}