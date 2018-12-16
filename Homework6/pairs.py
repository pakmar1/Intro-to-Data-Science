from pyspark import SparkContext
import sys

sc = SparkContext("local", "app")

graph_file = sc.textFile(sys.argv[1])

counts = graph_file.map(lambda line: (line.split('\t')[0],line.split('\t')[1]))\
.groupByKey(numPartitions=None)\
.mapValues(list)\
.sortByKey(ascending=True)

counts_1 = graph_file.map(lambda line: (line.split('\t')[1],line.split('\t')[0]))


counts.saveAsTextFile(sys.argv[2])