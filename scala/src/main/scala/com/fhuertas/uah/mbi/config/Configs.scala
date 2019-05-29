package com.fhuertas.uah.mbi.config

object Configs {
  lazy val Ej3 = new {
    val root        = "kafka.streams"
    val kafka       = s"$root.kafka"
    val input  = s"$root.topics.input"
    val output = s"$root.topics.output"
  }
}
