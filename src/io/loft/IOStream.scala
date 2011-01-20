package io.loft

import io.loft.ioloop._

import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.net.InetSocketAddress
import java.nio.channels.SocketChannel
import scala.collection.mutable.MutableList
import java.nio.channels.SelectableChannel

class IOStream(var key: SelectionKey, maxBufferSize: Long = 104857600, readChunkSize: Int = 4096) {

  key.channel.configureBlocking(false)

  private val readBuffer = new StringBuilder()
  private val writeBuffer = new StringBuilder()
  private var readDelimeter = ""
  private var bytesToRead = 0

  private var readCallback: (String) => Unit = null
  private var writeCallback: () => Unit = null
  private var closeCallback: () => Unit = null
  private var connectCallback: () => Unit = null

  private var connecting = false

  def connect(address: (String, Int), connectCallback: () => Unit = null) {
    key.channel match {
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
      // checkClosed()
      if (readToBuffer() == 0) continue = false
    }
    addIOState(SelectionKey.OP_READ)
  }

  def readBytes(numBytes: Int, callback: (String) => Unit) {
    if (numBytes == 0) {
      callback("")
      return
    }
    bytesToRead = numBytes
    readCallback = callback
    var continue = true
    while (continue) {
      if (readFromBuffer()) return
      //checkClosed()
      if (readToBuffer() == 0) continue = false
    }
    addIOState(SelectionKey.OP_READ)
  }

  def readFromBuffer(): Boolean = {
    if (bytesToRead != 0 && readBuffer.size >= bytesToRead) {
      val numBytes = bytesToRead
      val callback = readCallback
      readCallback = null
      bytesToRead = 0
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

  def write(data: String, callback: () => Unit) {
    //checkClosed()
    writeBuffer.append(data)
    addIOState(SelectionKey.OP_WRITE)
    writeCallback = callback
  }

  def setCloseCallback(callback: () => Unit) = closeCallback = callback;

  def close() {
    if (key != null) {
      IOLoop.removeHandler(key.channel)
      key.channel().close()
      key = null
      if (closeCallback != null) closeCallback()
    }

  }
  
  def reading() = readCallback != null
  
  def writing() = writeBuffer.size > 0
  
  def closed() = key != null
  
  def handleEvent() { 
	  // TODO RS 110120
  }

  // def checkClosed() { if(channel == null) throw new IOException("Stream is closed") }

  def readToBuffer(): Int = {
    val buffer: ByteBuffer = key.attachment().asInstanceOf[ByteBuffer]

    key.channel match {
      case c: SocketChannel => c.read(buffer)
      case _ => return 0
    }
    buffer.flip

    val chunk = new String(buffer.array(), 0, buffer.limit(), "UTF-8")
    readBuffer.append(chunk)
    //     if self._read_buffer.tell() >= self.max_buffer_size:
    //            logging.error("Reached maximum read buffer size")
    //            self.close()
    //            raise IOError("Reached maximum read buffer size")
    chunk.size
  }

  def addIOState(ops: Int) {
    if (key == null) return
    IOLoop.updateHandler(key.channel, key.interestOps | ops)
  }

  def runCallback(callback: (String) => Unit, data: String) = callback(data)

  def consume(numBytes: Int): String = {
    val result = readBuffer.substring(0, numBytes)
    readBuffer.delete(0, numBytes)
    result
  }

}