package io.loft.web

trait RequestHandler {

  def get()

  def write(s: String) {
    println(s)
  }
	
}