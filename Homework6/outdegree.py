from shutil import rmtree
from pyspark import SparkContext
import sys

sc = SparkContext("local", "app")

graph_file = sc.textFile(sys.argv[1])

counts = graph_file.map(lambda degree: (degree.split('\t')[0],1)) \
.reduceByKey(lambda a,b: a+b) \
.sortByKey(ascending=True)

rmtree(sys.argv[2])
counts.saveAsTextFile(sys.argv[2])