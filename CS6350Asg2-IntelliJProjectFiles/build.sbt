name := "CS6350Asg2"

version := "0.1"

scalaVersion := "2.11.12"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "2.4.7",
  "org.apache.spark" %% "spark-sql" % "2.4.7",
  "org.apache.spark" %% "spark-mllib" % "2.4.7",
  "org.apache.spark" %% "spark-streaming" % "2.4.7",
  "org.apache.spark" %% "spark-hive" % "2.4.7"
)