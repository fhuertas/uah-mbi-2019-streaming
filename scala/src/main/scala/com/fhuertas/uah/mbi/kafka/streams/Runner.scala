package com.fhuertas.uah.mbi.kafka.streams

import java.util.Properties
import java.util.concurrent.TimeUnit

import com.fhuertas.uah.mbi.config.ConfigLoader
import com.typesafe.config.ConfigFactory
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream._


import scala.collection.JavaConverters._
import com.fhuertas.uah.mbi.config.Configs._

object Runner extends App {
  import org.apache.kafka.streams.scala.Serdes._
  import org.apache.kafka.streams.scala.ImplicitConversions._


  val config = ConfigFactory.load()

  val kafkaConfig = ConfigLoader.loadAsMap(ConfigFactory.load(), Some(Ej3.kafka))

  val properties = {
    val p = new Properties()
    p.putAll(kafkaConfig.asJava)
    p
  }

  val inputTopic = config.getString(Ej3.input)
  val outputTopic = config.getString(Ej3.output)


}
