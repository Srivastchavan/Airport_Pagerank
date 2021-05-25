import org.apache.spark.{SparkConf, SparkContext}

import org.apache.spark.sql.{Row, SaveMode, SparkSession}
import org.apache.spark.ml.feature.HashingTF
import org.apache.spark.ml.feature.StopWordsRemover
import org.apache.spark.ml.feature.Tokenizer
import org.apache.spark.ml.feature.StringIndexer
import org.apache.spark.ml.tuning.ParamGridBuilder
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.tuning._
import org.apache.spark.mllib.evaluation.MulticlassMetrics
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.LogisticRegression

object tweets
{
  def main(args: Array[String]): Unit = {
    if (args.length!=2)
    {
      println("Please specify 2 arguments namely Input loc and Output loc")
    }

    val spconf = new SparkConf().setAppName("Tweets")
    val spark = new SparkSession
    .Builder()
      .config(spconf)
      .getOrCreate()
    import spark.implicits._
    val input = args(0)
    val output = args(1)
    val tweetsdf = spark.read.format("csv").option("header", "true").option("inferSchema", "true").option("delimiter", ",").load(input)
    val tweetsna = tweetsdf.na.drop("all", Seq("text"))
    val tokenizer = new Tokenizer().setInputCol("text").setOutputCol("words")
    val stopwordRemover = new StopWordsRemover().setInputCol(tokenizer.getOutputCol).setOutputCol("sw_removed")
    val hashingTF = new HashingTF().setInputCol(stopwordRemover.getOutputCol).setOutputCol("features")
    val Label = new StringIndexer().setInputCol("airline_sentiment").setOutputCol("label")
    val lr = new LogisticRegression().setMaxIter(10).setRegParam(0.1)
    val pipeline = new Pipeline().setStages(Array(tokenizer, stopwordRemover, hashingTF, Label, lr))
    val paramGrid = new ParamGridBuilder().addGrid(hashingTF.numFeatures, Array(10, 100, 1000)).addGrid(lr.regParam, Array(0.1, 0.01)).build()
    val cv = new CrossValidator().setEstimator(pipeline).setEvaluator(new MulticlassClassificationEvaluator).setEstimatorParamMaps(paramGrid).setNumFolds(5)
    val Array(train_data, test_data) = tweetsna.randomSplit(Array(0.7, 0.3), 24)
    val train_model = cv.fit(train_data)
    val predictedResults = train_model.bestModel.transform(test_data)
    val PredictionAndLabels = predictedResults.select("prediction", "label").rdd.map{case Row(prediction: Double, label: Double) => (prediction, label)}
    val metrics = new MulticlassMetrics(PredictionAndLabels)
    val op_data = Seq(("Accuracy", metrics.accuracy), ("Weighted Precision", metrics.weightedPrecision), ("Weighted Recall Value", metrics.weightedRecall),("Weighted F1 Measure",metrics.weightedFMeasure),("Weighted FPR",metrics.weightedFalsePositiveRate),("Weighted TPR", metrics.weightedTruePositiveRate))
    val op_DF = op_data.toDF("Metric","Metric Value")
    op_DF.coalesce(1)
      .write
      .mode(SaveMode.Overwrite)
      .format("csv")
      .option("header", "true")
      .save(output)
  }
}
