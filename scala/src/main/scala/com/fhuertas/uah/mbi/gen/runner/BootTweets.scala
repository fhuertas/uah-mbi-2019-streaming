package com.fhuertas.uah.mbi.gen.runner
import com.fhuertas.uah.mbi.config.KafkaConfigNs.Producer
import com.fhuertas.uah.mbi.gen.kafka.KafkaBuilder
import com.fhuertas.uah.mbi.gen.utils.TimeReader
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import org.apache.kafka.clients.producer.ProducerRecord

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.duration._
import scala.concurrent.{ Await, Future, Promise }
import scala.util.Try

object BootTweets extends App with LazyLogging {

  def delay(time: Long): Try[Future[Nothing]] =
    Try(Await.ready(Promise().future, time millis))

  val TwitterNS = "twitter.file"

  val config      = ConfigFactory.load().getConfig(TwitterNS)
  val kafkaConfig = config.getConfig(Producer)

  val file       = config.getString("file.name")
  val idField    = config.getString("file.field.id")
  val timeField  = config.getString("file.field.time.name")
  val timeFormat = config.getString("file.field.time.format")
  val factor     = config.getLong("factor")
  val topic      = config.getString("topic")

  logger.info(s"""
                 | Configuration
                 |  * Input file   : $file
                 |  * idField      : $idField
                 |  * timeField    : $timeField
                 |  * timeFormat   : $timeFormat
                 |  * Speed factor : $factor
                 |  * topic        : $topic
    """.stripMargin)

  lazy val producer = KafkaBuilder.buildProducer[String, String](kafkaConfig)

  logger.info("Reading events")
  val tweets = TimeReader.processLine(file, idField, timeField, timeFormat)
  val time   = tweets.headOption.map(_.delay).map(tweets.last.delay - _).getOrElse(0L) / 1000L / factor
  logger.info(s"Start publish. total time $time s. Events ${tweets.length}")

  val futures = tweets.zipWithIndex.map {
    case (t, k) ⇒
      Future {
        delay(t.delay / factor)
        logger.trace(s"Publish ID: $k, delay: ${t.delay}, event: ${t.event}")
        producer.send(new ProducerRecord[String, String](topic, t.id, t.event))
      }
  }
  Await.result(Future.sequence(futures), Duration.Inf)

}
