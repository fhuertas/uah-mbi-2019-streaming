package com.fhuertas.uah.mbi.gen.runner
import com.fhuertas.uah.mbi.gen.SimpleGens
import com.fhuertas.uah.mbi.gen.kafka.KafkaBuilder
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import org.scalacheck.Gen

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.Try

object BootText extends App with LazyLogging {
  import com.fhuertas.uah.mbi.config.KafkaConfigNs._
  // namespaces
  val nsBootText = "generator.text"

  // config values
  val config           = ConfigFactory.load().getConfig(nsBootText)
  val kafkaConfig      = config.getConfig(Producer)
  val runTime          = config.getDuration(TotalTime).toMillis
  val batchTime        = config.getDuration(BatchTime).toMillis
  val mediaWords       = Try(config.getInt(MediaWords)).toOption
  val deviationWords   = Try(config.getInt(DeviationWords)).toOption
  val elementsPerBatch = config.getInt(ElementsPerBatch)
  val topic            = config.getString(TopicName)

  val producer  = KafkaBuilder.buildProducer[Nothing, String](kafkaConfig)
  val endMillis = System.currentTimeMillis() + runTime
  val generator = Gen.listOfN(elementsPerBatch, SimpleGens.textGenerator(mediaWords, deviationWords))

  val future = KafkaBuilder.publishGenRec(producer, topic, endMillis, batchTime, generator)
  Await.result(future, Duration.Inf)
}
