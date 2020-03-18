name := "potigol-editor"
organization := "potigol"
version := "0.9.16"

scalaVersion := "2.12.11"

javacOptions in Compile ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint")
scalacOptions in Compile ++= Seq("-deprecation")

mainClass in (Compile, packageBin) := Some("br.edu.ifrn.potigol.editor.Editor")

assemblyOutputPath in assembly := file("./epotigol.jar")

libraryDependencies ++= Seq(
   "potigol" %% "potigol" % "0.9.16",
  ("org.antlr" % "antlr4" % "4.8-1").
    exclude("com.ibm.icu", "icu4j").
    exclude("org.abego.treelayout", "org.abego.treelayout.core").
    exclude("org.antlr", "ST4").
    exclude("org.glassfish", "javax.json"),

  "org.scala-lang" % "scala-library" % "2.12.11",
  "org.scala-lang" % "scala-reflect" % "2.12.11",
  "org.scala-lang.modules" %% "scala-swing" % "2.1.1"

)

EclipseKeys.withSource in ThisBuild := true
EclipseKeys.withJavadoc in ThisBuild := true
EclipseKeys.createSrc in ThisBuild := EclipseCreateSrc.Default + EclipseCreateSrc.ManagedClasses
