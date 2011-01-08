package io.loft
import scala.util.continuations._
//import org.deftserver.web.http._
//import org.deftserver.web.handler._

object ContinuationBased {

  def main(args : Array[String]) {
    get()
  }

  //@Asynchronous
  def get() {
    println("entering get()")
    reset {
      val id = lookup("roger schildmeijer"); // "blocking" style
      val value = fetch("http://127.0.0.1:8080/" + id); // "blocking" style
      println(value) // eg. response.write(value).finish 
    }
    println("leaving get()")
  }

  /**
   * mocks an asynchronous database lookup.
   */
  def lookup(key : String) = shift { k : (String => Unit) =>
    val runnable = new Runnable {
      def run = {
        Thread.sleep(500) // simulate disk seek latency
        k(key)
      }
    }
    new Thread(runnable).start()
  }
  
  
  /**
   * mocks an asynchronous http fetch
   */
  def fetch(key : String) = shift { k : (String => Unit) =>
    val runnable = new Runnable {
      def run = {
        Thread.sleep(2000) // simulate network latency
        k(key.dropWhile(_ != 'r'))
      }
    }
    new Thread(runnable).start()
  }

}