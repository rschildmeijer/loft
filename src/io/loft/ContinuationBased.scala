package io.loft
import scala.util.continuations._
//import org.deftserver.web.http._
//import org.deftserver.web.handler._



object ContinuationBased {

	def main(args: Array[String]) {
		 get()
	}

	 def get(/*request: HttpRequest, response: HttpResponse*/) {
        //response.write("hello ");
        //db.asyncIdentityGet("world", new AsyncCallback<String>() {
        //    public void onSuccess(String result) { response.write(result).finish(); }
        //});
		println("hello") //response.write("hello");
		reset {
		  val value = asyncIdentityGet("world");	//
		  println(value) //response.write(value);   // "blocking" style
	    }
     }

	 def asyncIdentityGet(key: String) = shift { k: (String => Unit) =>
	    val runnable = new Runnable {
		  def run = {
		     Thread.sleep(2000)	// simulate network latency
		     k(key)
	      }
	    }
	    new Thread(runnable).start()
	 }
	
	 /* example of continuation passing style */
	 def foo(): Int @cps[Int] = { 
      shift { k: (Int=>Int) =>
        k(7)
      } + 1
    }
	 
}