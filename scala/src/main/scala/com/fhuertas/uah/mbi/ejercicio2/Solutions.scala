package com.fhuertas.uah.mbi.ejercicio2
import java.util.Properties

import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.{ KafkaProducer, ProducerRecord }

import scala.concurrent.duration._
import scala.compat.java8.DurationConverters._
import scala.collection.JavaConverters._
import scala.util.Random

object Solutions {

  def ejercicio2a(): Unit = {
    val consumerProperties = new Properties()
    consumerProperties.setProperty("bootstrap.servers", "localhost:9092")
    consumerProperties.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    consumerProperties.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    consumerProperties.setProperty("group.id", s"${System.currentTimeMillis}")
    val consumer = new KafkaConsumer[Nothing, String](consumerProperties)

    consumer.subscribe(Seq("ejercicio2-origen").asJava)

    while (true) {
      val records = consumer.poll(100.millis.toJava)
      records.asScala.foreach(record ⇒ println(Transformations.wordCount(record.value)))
    }
  }

  def ejercicio2b(): Unit = {
    val producerProperties = new Properties()
    producerProperties.setProperty("bootstrap.servers", "localhost:9092")
    producerProperties.setProperty("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    producerProperties.setProperty("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    val producer = new KafkaProducer[Nothing, String](producerProperties)
    val destino  = "ejercicio2-destino"
    while (true) {
      val record = new ProducerRecord(destino, Random.nextInt.toString)
      producer.send(record)
      Thread.sleep(1.second.toMillis)
    }
  }

  def ejercicio2(): Unit = {
    val consumerProperties = new Properties()
    consumerProperties.setProperty("bootstrap.servers", "localhost:9092")
    consumerProperties.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    consumerProperties.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    consumerProperties.setProperty("group.id", s"${System.currentTimeMillis}")
    val consumer = new KafkaConsumer[Nothing, String](consumerProperties)

    consumer.subscribe(Seq("ejercicio2-origen").asJava)

    val producerProperties = new Properties()
    producerProperties.setProperty("bootstrap.servers", "localhost:9092")
    producerProperties.setProperty("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    producerProperties.setProperty("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    val producer = new KafkaProducer[Nothing, String](producerProperties)
    val destino  = "ejercicio2-destino"

    while (true) {
      val records = consumer.poll(100.millis.toJava)
      records.asScala.foreach(recordReaded ⇒ {
        val wc = Transformations.wordCount(recordReaded.value)
        val recordToWrite = new ProducerRecord(destino, wc.toString)
        producer.send(recordToWrite)
      })
    }
  }
}
