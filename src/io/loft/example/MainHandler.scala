package io.loft.example

import io.loft.web.RequestHandler
import io.loft.web.Application
import io.loft.httpserver.HTTPServer
import io.loft.ioloop.IOLoop

object MainHandler extends RequestHandler {

  def get() {
    write("hello world")
  }
  
  /* How asynchronous request are handled
  @Asynchronous
  def post() {
    reset {
      val id = redis get("roger schildmeijer");
      val http = AsyncHTTPClient()
      val result = http fetch("http://127.0.0.1:8080/" + id); 
      write(result)
      finish 
    }
  }
  */
  val application = Application(Map(
    "/".r -> this,
    "/user".r -> this
  ))
  
  /* better using a List of tuples [Regex, RequestHandler] ? (hint: tornado does :))
  val application = List(
    ("/".r, this), 
    ("/".r, this)
  )
  */
  
  def main(args: Array[String]) {
    val httpServer = HTTPServer(application)
    httpServer listen(8888)
    IOLoop start
  }

}