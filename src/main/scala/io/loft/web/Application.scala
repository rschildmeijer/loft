package io.loft.web

import scala.util.matching._

class Application(mapping: Map[Regex, RequestHandler]) {

}

object Application {
	
  def apply(mappings: Map[Regex, RequestHandler]) = new Application(mappings)	

}