package io.loft.ioloop

import java.nio.channels.SelectableChannel

import scala.collection.mutable.Map

object IOLoop {

  private[this] val handlers = Map[SelectableChannel, () => Unit]()
  private[this] val events = Map[SelectableChannel, String]()
  private[this] val callbacks = List()
  private[this] val timeouts = List()
  private[this] val running = false
  private[this] val stopped = false
  private[this] val blockingSignalThreshold = null

  def start {}

  def addHandler(channel: SelectableChannel, handler: () => Unit, events: Int) {
    handlers += channel -> handler
    register(channel, events)
  }

  private def register(channel: SelectableChannel, events: Int) {

  }

  def updateHandler(channel: SelectableChannel, events: Int) { modify(channel, events) }

  private def modify(channel: SelectableChannel, events: Int) {}
  
  def removeHandler(channel: SelectableChannel) {
	  handlers -= channel
	  events -= channel;
  }

}