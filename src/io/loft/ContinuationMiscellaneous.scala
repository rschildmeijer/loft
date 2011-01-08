package io.loft
import scala.util.continuations._

object ContinuationMiscellaneous {

  def main(args : Array[String]) {
    reset {
      val a = add(1, 2)
      var b = mul(a, 3)
      val c = ident(b)
      val d = add(c, 4)
      println("final result: "+d)
    }
  }

  def add(i : Int, j : Int) = shift { k : (Int => Unit) => k(i + j) }

  def mul(i : Int, j : Int) = shift { k : (Int => Unit) => k(i * j) }

  def ident(i : Int) = { i }

  /* example of continuation passing style */
  def foo() : Int @cps[Int] = {
    shift { k : (Int => Int) =>
      k(7)
    } + 1
  }

}
