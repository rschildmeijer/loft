# Loft

[Continuation] based non-blocking, asynchronous, single threaded web server. Inspired by [Swirl] (sugar for [Tornado]),
[Tornado] itself and [Deft]

# Usage:
    object ExampleHandler extends RequestHandler {

      @Asynchronous
      def get() {
        val http = AsyncHTTPClient()
        reset {
          val id = redis get("roger_schildmeijer");   //async call
          val result = http fetch("http://127.0.0.1:8080/" + id); //async call
          write(result)
          finish 
        }
      }
  
      val application = Application(Map("/".r -> this))
  
      def main(args: Array[String]) {
        val httpServer = HTTPServer(application)
        httpServer listen(8888)
        IOLoop start
      }

    }

# Disclaimer
Loft is far from (production) ready. The example above (ExampleHandler) is the proposed syntax for doing asynchronous calls.
The initial plan for Loft is to adopt as much as possible from the [facebook/tornado] master branch.
 
# Requirements
Java >= 1.5   
Scala >= 2.8.0 (for loft developers: enable Scala continuation plugin (-P:continuations:enable))

[Continuation]: http://www.scala-lang.org/node/2096
[swirl]: http://code.naeseth.com/swirl/
[Tornado]: http://github.com/facebook/tornado/
[Deft]: http://github.com/rschildmeijer/deft
[facebook/tornado]: http://github.com/facebook/tornado
