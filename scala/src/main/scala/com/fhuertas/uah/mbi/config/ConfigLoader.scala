package com.fhuertas.uah.mbi.config

import com.typesafe.config.Config

object ConfigLoader {

  def loadAsMap(config: Config, ns: Option[String] = None): Map[String, String] = {
    import scala.collection.JavaConverters._
    val configExtracted = ns.map(config.getConfig).getOrElse(config).entrySet().asScala
    configExtracted.map(entry ⇒ entry.getKey → entry.getValue.unwrapped().toString).toMap
  }

}
