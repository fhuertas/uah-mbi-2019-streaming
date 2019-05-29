package com.fhuertas.uah.mbi.ejercicio2

object Transformations {
  val wordSplitter: Seq[String] = Seq(" ", ",", ".", "\n", "\t")

  def wordCount(text: String): Int =
    text.replaceAll(s"[${wordSplitter.mkString}]+", " ").trim.split(" ").length
}
