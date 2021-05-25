Team Members - 

Srivastchavan Rengarajan - sxr190067
Yogesh Kumar Chandrasekar - yxc180071


PageRank Links -
Jar file - https://cs6350asg2.s3.amazonaws.com/cs6350asg2_2.11-0.1.jar (root/cs6350asg2_2.11-0.1.jar)
Input CSV - https://cs6350asg2.s3.amazonaws.com/211878636_T_T100D_MARKET_US_CARRIER_ONLY.csv (root/PageRank/211878636_T_T100D_MARKET_US_CARRIER_ONLY)
Output - https://cs6350asg2.s3.amazonaws.com/PageRankOutput/part-00000
	 https://cs6350asg2.s3.amazonaws.com/PageRankOutput/part-00001
	 https://cs6350asg2.s3.amazonaws.com/PageRankOutput/part-00002
	 https://cs6350asg2.s3.amazonaws.com/PageRankOutput/part-00003

Tweets Links -
Jar file - https://cs6350asg2.s3.amazonaws.com/cs6350asg2_2.11-0.1.jar (root/cs6350asg2_2.11-0.1.jar)
Input CSV - https://cs6350asg2.s3.amazonaws.com/Tweets.csv (root/tweets/Tweets.csv)
Output - https://cs6350asg2.s3.amazonaws.com/TweetsOutput/part-00000-63323f06-b0ce-4af8-bed8-bb68c1bb8cdc-c000.csv



Steps for AWS code execution: 
1. Log into your AWS account. 
2. Create an S3 bucket.
3. Download the Input JAR and input CSV files from above link or get it from attached zip file and upload into the S3 Bucket.
4. Create an EMR cluster with necessary configuration. 
5. Add 2 new steps in the EMR cluster with below configuration -
	PageRank
		Step Type: Spark Application
		Deploy Mode: Cluster
		Spark-submit options: --class "PageRank"
		Application location: S3 URI of uploaded JAR file
		Arguments: S3 URI of Input CSV
			   Number of Iterations
			   S3 URI of Output folder
  	Tweets
		Step Type: Spark Application
		Deploy Mode: Cluster
		Spark-submit options: --class "tweets"
		Application location: S3 URI of uploaded JAR file
		Arguments: S3 URI of Input CSV
			   S3 URI of Output folder
6. Once both steps are run in EMR, the output is stored in the output folder in the S3 bucket.
