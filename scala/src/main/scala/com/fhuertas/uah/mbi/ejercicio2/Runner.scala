package com.fhuertas.uah.mbi.ejercicio2
import java.util.Properties

import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer

import scala.concurrent.duration._
import scala.compat.java8.DurationConverters._
import scala.collection.JavaConverters._

object Runner extends App {
//  val consumerProperties = new Properties()
  // Consumer properties.
  // principal properties:
  //  * bootstrap.servers
  //  * key.deserializer
  //  * value.deserializer
  //  * group.id
  // Example: https://gist.github.com/fancellu/f78e11b1808db2727d76
  // More properties: https://kafka.apache.org/documentation/#consumerconfigs
  // consumerProperties.setProperty("foo", "bar")

//  val producerProperties = new Properties()
  // Producer properties.
  // principal properties:
  //  * bootstrap.servers kafka location
  //  * key.serializer
  //  * value.serializer
  // More properties: https://kafka.apache.org/documentation/#consumerconfigs
  // producerProperties.setProperty("foo", "bar")

//  val consumer = new KafkaConsumer[String, String](consumerProperties)

//  val producer = new KafkaProducer[String, String](producerProperties)

  // Ejercicio 2.a Solo consumer
  // Ejercicio 2.b Solo producir
  // Ejercicio 2 Consumir y producir
}
