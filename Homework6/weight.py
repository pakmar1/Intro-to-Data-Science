from pyspark import SparkContext
import sys

sc = SparkContext("local", "app")

graph_file = sc.textFile(sys.argv[1])

counts = graph_file.map(lambda degree: (degree.split('\t')[0],int(degree.split('\t')[2]))) \
                   .reduceByKey(lambda a,b: a+b) \
                   .sortByKey(ascending=True)

counts.saveAsTextFile(sys.argv[2])