package com.fhuertas.uah.mbi.gen.kafka
import java.util.Properties

import com.fhuertas.uah.mbi.config.ConfigLoader
import com.typesafe.config.Config
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord, RecordMetadata}
import org.scalacheck.Gen

import scala.annotation.tailrec
import scala.concurrent.Future

object KafkaBuilder {
  val logger = org.log4s.getLogger

  def buildProducer[K, V](config: Config): KafkaProducer[K, V] = {
    import scala.collection.JavaConverters._
    val props = new Properties
    props.putAll(ConfigLoader.loadAsMap(config = config).asJava)
    logger.debug(s"Producer with the follow configuration: $props")
    new KafkaProducer[K, V](props)
  }

  @tailrec
  def publishGenRec[T](producer: KafkaProducer[Nothing, T],
                       topic: String,
                       endTime: Long,
                       batchTime: Long,
                       gen: Gen[List[T]],
                       nBatch: Int = 0,
                       acc: List[Future[List[RecordMetadata]]] = List.empty): Future[List[RecordMetadata]] = {

    import scala.concurrent.ExecutionContext.Implicits.global
    System.currentTimeMillis() match {
      case current if endTime > current ⇒
        logger.info(s"Batch number: $nBatch, endTime: $endTime, batchTime: $batchTime, currentTime: $current")
        val result = Future {
          val rcs = gen.sample.get
            .map(new ProducerRecord[Nothing, T](topic, _))
            .map(rc ⇒ Future(producer.send(rc).get))
          Future.sequence(rcs)
        }.flatMap(identity)
        Thread.sleep(batchTime)
        publishGenRec(producer, topic, endTime, batchTime, gen, nBatch + 1, acc :+ result)
      case _ ⇒ Future.sequence(acc).map(_.flatten)
    }
  }

}
