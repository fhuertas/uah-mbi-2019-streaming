package com.fhuertas.uah.mbi.config

import com.typesafe.config.ConfigException.Missing
import com.typesafe.config.ConfigFactory
import org.scalatest.{ FlatSpec, Matchers }

class ConfigLoaderSpec extends FlatSpec with Matchers {
  "KafkaConfig" should "be extracted correctly" in {
    val config = ConfigFactory.load()
    val result = ConfigLoader.loadAsMap(config, Some("this.is.a.valid.path"))
    result shouldBe Map(
      "key1"         → "this is a value",
      "key2"         → "223",
      "key3.subKey1" → "aaaa",
      "key3.subKey3" → "unuadnfasd"
    )
  }

  "KafkaConfig" should "thrown an exception if not exists" in {
    val config = ConfigFactory.load()
    a[Missing] shouldBe thrownBy(ConfigLoader.loadAsMap(config, Some("this.is.a.invalid.path")))
  }

}
