package com.fhuertas.uah.mbi.kafka.streams

import java.time.Duration
import java.util.Properties
import java.util.concurrent.TimeUnit

import com.fhuertas.uah.mbi.config.ConfigLoader
import com.fhuertas.uah.mbi.config.Configs._
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.kstream.JoinWindows
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream._

import scala.collection.JavaConverters._

object JoinById extends App with LazyLogging {

  import org.apache.kafka.streams.scala.ImplicitConversions._
  import org.apache.kafka.streams.scala.Serdes._

  val config = ConfigFactory.load()

  val kafkaConfig = ConfigLoader.loadAsMap(ConfigFactory.load(), Some(Ej3.kafka))

  val properties = {
    val p = new Properties()
    p.putAll(kafkaConfig.asJava)
    p
  }
  val inputTopic1 = "in1"
  val inputTopic2 = "in2"
  val outputTopic = "out"

  val builder = new StreamsBuilder()

  val stream: KStream[String, String] = builder
    .stream[String, String](inputTopic1)
    .map((_, value) ⇒ {
      val split = value.split(",", 2)
      (split.head, split.last)
    })

  val status = builder
    .stream[String, String](inputTopic2)
    .map((_, value) ⇒ {
      val split = value.split(",", 2)
      (split.head, split.last)
    })
    .groupByKey
    .reduce((_, r) ⇒ r)

  val output: KStream[String, String] = stream
    .leftJoin(status)({
      case (leftValue, rightValue) ⇒
        println(s"""Left: "$leftValue", Right: "$rightValue"""")
        println(rightValue)
        s"$leftValue,$rightValue"
    })
//    }, JoinWindows.of(Duration.ofSeconds(30)))

  // Other lines example
  //  textLines.filter((_,value) => value.startsWith("ERROR:")).map((_,value) => ("ERROR", value.replaceFirst("ERROR:",""))).to("errors")
  //  textLines.filter((_,value) => value.startsWith("ERROR:")).mapValues(value => value.length.toString).to("length-errors")
  output.to(outputTopic)
  val streams: KafkaStreams = new KafkaStreams(builder.build(), properties)

  // Always (and unconditionally) clean local state prior to starting the processing topology.
  // We opt for this unconditional call here because this will make it easier for you to play around with the example
  // when resetting the application for doing a re-run (via the Application Reset Tool,
  // http://docs.confluent.io/current/streams/developer-guide.html#application-reset-tool).
  //
  // The drawback of cleaning up local state prior is that your app must rebuilt its local state from scratch, which
  // will take time and will require reading all the state-relevant data from the Kafka cluster over the network.
  // Thus in a production scenario you typically do not want to clean up always as we do here but rather only when it
  // is truly needed, i.e., only under certain conditions (e.g., the presence of a command line flag for your app).
  // See `ApplicationResetExample.java` for a production-like example.
  streams.cleanUp()

  streams.start()

  // Add shutdown hook to respond to SIGTERM and gracefully close Kafka Streams
  sys.ShutdownHookThread {
    streams.close(10, TimeUnit.SECONDS)
  }
}
