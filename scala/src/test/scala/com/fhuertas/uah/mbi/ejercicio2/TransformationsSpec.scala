package com.fhuertas.uah.mbi.ejercicio2
import org.scalatest.{FlatSpec, Matchers}

class TransformationsSpec extends FlatSpec with Matchers {
  "wordCount" should "count a text with new lines correctly" in {
    val text =
      """
        |first
        |second
        |  Three  Four.Five,six
      """.stripMargin
    Transformations.wordCount(text) shouldBe 6
  }

}
