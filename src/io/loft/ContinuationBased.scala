package io.loft
import scala.util.continuations._
//import org.deftserver.web.http._
//import org.deftserver.web.handler._

object ContinuationBased {

  def main(args : Array[String]) {
    get()
  }

  //@Asynchronous
  def get( /*request: HttpRequest, response: HttpResponse*/ ) {
    /* below is the callback based (Deft) version 
    response.write("hello ");
    db.asyncIdentityGet("world", new AsyncCallback<String>() {
        public void onSuccess(String result) { response.write(result).finish(); }
    });
    */
	  
    /* continuation based version */
    println("hello") //response.write("hello");
    reset {
      val result = asyncIdentityGet("world");  // "blocking" style
      println(result) //response.write(value).finish();
    }
  }

  def asyncIdentityGet(key : String) = shift { k : (String => Unit) =>
    val runnable = new Runnable {
      def run = {
        Thread.sleep(2000) // simulate network latency
        k(key)
      }
    }
    new Thread(runnable).start()
  }

  /* example of continuation passing style */
  def foo() : Int @cps[Int] = {
    shift { k : (Int => Int) =>
      k(7)
    } + 1
  }

}