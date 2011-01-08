package io.loft.httpserver

import io.loft.web.Application

class HTTPServer(app: Application) {

	def listen(port: Int) {
		
	}
	
}

object HTTPServer {
  
  def apply(app: Application) = new HTTPServer(app)	
	
}