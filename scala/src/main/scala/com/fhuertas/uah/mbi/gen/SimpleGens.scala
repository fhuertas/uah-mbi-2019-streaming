package com.fhuertas.uah.mbi.gen
import org.scalacheck.Gen

object SimpleGens {
  val MaxWords = 10000
  val MinSizeWord = 2
  val MaxSizeWord = 10
  lazy val genWord: Gen[String] = for {
    size <- Gen.choose(MinSizeWord,MaxSizeWord)
    word <- Gen.listOfN(size,Gen.alphaChar).map(_.mkString)
  } yield word

  def textGenerator(nWords: Option[Int] = None, deviation: Option[Int] = None): Gen[String] =
    for {
      middle         ← nWords.map(Gen.const).getOrElse(Gen.choose(0, MaxWords))
      finalDeviation ← Gen.choose(-deviation.getOrElse(0), deviation.getOrElse(0))
      finalWords     = middle + finalDeviation
      words          ← Gen.listOfN(finalWords, genWord)
    } yield words.mkString(" ")

}
