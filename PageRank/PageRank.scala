
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession


object PageRank
{
  def main(args: Array[String]): Unit = {
    if (args.length!=3)
    {
      println("Please specify 3 arguments namely Input loc, Iterations and Output loc")
    }
    val sconf = new SparkConf().setAppName("Page Rank")
    val sc = new SparkContext(sconf)
    val spark = new SparkSession
      .Builder()
      .config(sconf)
      .getOrCreate()

    val input = args(0)
    val iterations = args(1).toInt
    val output = args(2)
    val inputdf = spark.read.format("csv").option("header", "true").option("inferSchema", "true").option("delimiter", ",").load(input)
    val airport_info = inputdf.select("ORIGIN","DEST")
    val air_orgdest = airport_info.rdd.map(r=>(r.getString(0),r.getString(1)))
    val air_grps = air_orgdest.groupByKey()
    var pagerank = air_grps.map{case(k, v) => (k, 10.0)}
    val total_airports = air_grps.count()
    for (i <- 1 to iterations) {
        val links = air_grps.join(pagerank).values.flatMap{case(inlinks, pageRank) =>
        val pr_ratio = pageRank / inlinks.size
        inlinks.map(inlink => (inlink, pr_ratio))
    }
     pagerank = links.reduceByKey((a,b)=>a+b).mapValues((0.15 / total_airports) + 0.85 * _)
    }
    val tempoutput = pagerank.sortBy(-_._2).collect()
    sc.parallelize(tempoutput).saveAsTextFile(output + "")
  }
}




