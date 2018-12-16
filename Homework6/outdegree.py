from pyspark import SparkContext
import sys

sc = SparkContext("local", "app")

graph_file = sc.textFile(sys.argv[1])

counts = graph_file.map(lambda line: line.split('\t')[0]) \
                   .map(lambda degree: (degree,1)) \
                   .reduceByKey(lambda a,b: a+b) \
                   .sortByKey(ascending=True)

counts.saveAsTextFile(sys.argv[2])