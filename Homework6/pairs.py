from shutil import rmtree
from pyspark import SparkContext
import sys

sc = SparkContext("local", "app")

graph_file = sc.textFile(sys.argv[1])

counts = graph_file.map(lambda line: (line.split('\t')[0],line.split('\t')[1]))
counts_rev = graph_file.map(lambda line: (line.split('\t')[1],line.split('\t')[0]))

#intersection of the two rdds
unwanted_rows = counts.subtractByKey(counts_rev)
wanted_rows = counts.subtractByKey(unwanted_rows)

wanted_rows = counts.groupByKey(numPartitions=None)\
.mapValues(list)\
.sortByKey(ascending=True)

rmtree(sys.argv[2])
wanted_rows.saveAsTextFile(sys.argv[2])