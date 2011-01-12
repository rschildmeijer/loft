package io.loft.ioloop

import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.SelectableChannel

import scala.collection.mutable.Map
import scala.collection.mutable.ListBuffer
import scala.Math.min

object IOLoop {

  private val selector: Selector = Selector.open
  private val handlers = Map[SelectableChannel, SelectionKey => Unit]()
  private val events = Map[SelectableChannel, String]()
  private val callbacks = ListBuffer[() => Unit]()
  private val timeouts = ListBuffer[Timeout]()
  private var running = false
  private var stopped = false

  def addHandler(channel: SelectableChannel, handler: SelectionKey => Unit, events: Int) {
    handlers += channel -> handler
    register(channel, events)
  }

  def register(channel: SelectableChannel, events: Int) { channel.register(selector, events) }

  def unregister(channel: SelectableChannel) { channel.keyFor(selector).cancel }

  def updateHandler(channel: SelectableChannel, events: Int) { modify(channel, events) }

  def modify(channel: SelectableChannel, events: Int) { channel.register(selector, events) }

  def removeHandler(channel: SelectableChannel) {
    handlers -= channel
    events -= channel;
    unregister(channel)
  }

  def start {
    if (stopped) {
      stopped = false
      return
    }
    running = true;
    while (true) {
      var pollTimeout = 200L
      val callbacks = this.callbacks.toList
      this.callbacks.remove(0, callbacks.size) // remove all elements
      callbacks.foreach(runCallback(_))

      if (!this.callbacks.isEmpty) pollTimeout = 0

      val now = System.currentTimeMillis
      timeouts.takeWhile(_.deadline <= now).foreach(timeout => runCallback(timeout.callback))
      val index = timeouts.findIndexOf(_.deadline > now)
      index match {
        case i if i != -1 => timeouts.remove(0, i + 1)
      }
      if (!timeouts.isEmpty) {
        val ms = timeouts.head.deadline - now
        pollTimeout = min(ms, pollTimeout);
      }

      //if (!running) break	// TODO RS should work in scala 2.8

      if (selector.select(pollTimeout) != 0) {
        val keys = selector.selectedKeys.iterator;
        while (keys.hasNext) {
          val key = keys.next
          handlers(key.channel)(key)
          keys.remove
        }
      }

      stopped = false
    }
  }

  def stop() {
    running = false
    stopped = true
  }

  def isRunning = running

  def runCallback(callback: () => Unit) { callback }

  def addTimeout(deadline: Long, callback: () => Unit): Timeout = {
    val timeout = Timeout(deadline, callback)
    timeouts += timeout
    timeouts.sortWith(_.deadline < _.deadline) // TODO RS verify this sort
    timeout
  }

  def removeTimeout(timeout: Timeout) {
    val index = timeouts.findIndexOf(_ == timeout)
    index match {
      case i if i != -1 => timeouts.remove(i)
    }
  }

  def addCallback(callback: () => Unit) { callbacks += callback }

}

case class Timeout(deadline: Long, callback: () => Unit)

case class PeriodicTimeout(callbackTime: Long, callback: () => Unit) {
  var running = false

  def start {
    running = true
    val timeout = System.currentTimeMillis + callbackTime
    IOLoop.addTimeout(timeout, () => this.callback)
  }

  def stop { running = false }

  def run { callback }
}