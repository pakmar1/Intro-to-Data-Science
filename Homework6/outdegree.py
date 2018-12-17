from shutil import rmtree
from pyspark import SparkContext
import sys
import os

sc = SparkContext("local", "app")

graph_file = sc.textFile(sys.argv[1])

#rdd creation
counts = graph_file.map(lambda degree: (degree.split('\t')[0],1)) \
.reduceByKey(lambda a,b: a+b) \
.sortByKey(ascending=True)

if os.path.exists(sys.argv[2]):
    rmtree(sys.argv[2])
counts.saveAsTextFile(sys.argv[2])