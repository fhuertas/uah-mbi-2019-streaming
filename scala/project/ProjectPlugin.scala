import sbt.Keys._
import sbt._

object ProjectPlugin extends AutoPlugin {

  override def trigger: PluginTrigger = allRequirements

  object autoImport {

    object V {
      val compatJavaScala = "0.9.0"
      val config          = "1.3.3"
      val logback         = "1.2.3"
      val log4s           = "1.6.1"
      val scalaCheck      = "1.14.0"
      val scalaTest       = "3.0.5"
      val kafka           = "2.1.0"
      val javaxWs         = "2.1.1"
      val jackson         = "2.9.8"

    }

    lazy val settings: Seq[Def.Setting[_]] = Seq(
      resolvers += Resolver
        .url("confluent", url("https://packages.confluent.io/maven/")),
      name := "uah-mbi-2019",
      libraryDependencies ++= Seq(
        "ch.qos.logback"               % "logback-classic"       % V.logback,
        "com.typesafe"                 % "config"                % V.config,
        "org.apache.kafka"             %% "kafka"                % V.kafka,
        "org.apache.kafka"             % "kafka-streams"         % V.kafka,
        "org.apache.kafka"             %% "kafka-streams-scala"  % V.kafka,
        "org.log4s"                    %% "log4s"                % V.log4s,
        "org.scalacheck"               %% "scalacheck"           % V.scalaCheck,
        "com.fasterxml.jackson.module" %% "jackson-module-scala" % V.jackson,
        "org.scalatest"                %% "scalatest"            % V.scalaTest % Test,
        "org.scala-lang.modules"       %% "scala-java8-compat"   % V.compatJavaScala,
        // workaround: https://github.com/sbt/sbt/issues/3618#issuecomment-413257502
        ("javax.ws.rs" % "javax.ws.rs-api" % V.javaxWs)
          .artifacts(Artifact("javax.ws.rs-api", "jar", "jar"))
      ),
      scalacOptions := Seq(
        "-encoding",
        "UTF-8", // Specify character encoding used by source files.
        "-target:jvm-1.8", // Define what our target JVM is for object files
        "-unchecked", // Enable additional warnings where generated code depends on assumptions.
        "-deprecation", // Emit warning and location for usages of deprecated APIs.
        "-feature", // Emit warning and location for usages of features that should be imported explicitly.
        "-language:existentials", // Existential types (besides wildcard types) can be written and inferred
        "-language:higherKinds", // Allow higher-kinded types
        "-language:implicitConversions", // Allow definition of implicit functions called views
        "-language:postfixOps", // Allows you to use operator syntax in postfix position
        "-Xfuture", // Turn on future language features.
        "-Yno-adapted-args", // Do not adapt an argument list (either by inserting () or creating a tuple) to match the receiver.
        "-Ywarn-dead-code", // Warn when dead code is identified.
        "-Ywarn-infer-any",     // Warn when a type argument is inferred to be `Any`.
        "-Ywarn-unused-import", // Warn if an import selector is not referenced.
        //        "-Xfatal-warnings",     // Fail the compilation if there are any warnings.
        "-Ywarn-numeric-widen", // Warn when numerics are widened.
        "-Ywarn-value-discard", // Warn when non-Unit expression results are unused.
        "-Ywarn-unused:imports", // Warn if an import selector is not referenced.
        "-Ywarn-unused:locals", // Warn if a local definition is unused.
        "-Ywarn-unused:patvars", // Warn if a variable bound in a pattern is unused.
        "-Ywarn-unused:privates",
        "-Ywarn-unused:params", // Warn if a value parameter is unused.
        "-Ywarn-unused:params,-implicits",
        "-Ypartial-unification", // better type inference when multiple type parameters are involved
        "-Xlint"
      )
    )
  }

}
