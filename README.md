# Loft

Continuation based non blocking, asynchronous, single threaded web server. Inspired by [Swirl] (sugar for [Tornado]),
[Tornado] itself and [Deft]

#Usage:
    object ExampleHandler extends RequestHandler {

      @Asynchronous
      def get() {
        val http = AsyncHTTPClient()
          reset {
            val id = redis.get("roger_schildmeijer");
            val result = http.fetch("http://127.0.0.1:8080/" + id); 
            write(result)
            finish 
          }
        }
  
      val application = Application(Map("/".r -> this))
  
      def main(args: Array[String]) {
        val httpServer = HTTPServer(application)
        httpServer.listen(8888)
        IOLoop.start
      }

    }

[swirl]: http://code.naeseth.com/swirl/
[Tornado]: http://github.com/facebook/tornado/
[Deft]: http://github.com/rschildmeijer/deft