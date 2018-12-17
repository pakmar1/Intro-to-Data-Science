from shutil import rmtree
from pyspark import SparkContext
import sys
import os


sc = SparkContext("local", "app")

graph_file = sc.textFile(sys.argv[1])

#rdd1 (source, destination)
counts = graph_file.map(lambda line: (line.split('\t')[0],line.split('\t')[1]))
#rdd2 (destination, source)
counts_rev = graph_file.map(lambda line: (line.split('\t')[1],line.split('\t')[0]))

#intersection of the two rdds
wanted_rows = counts.intersection(counts_rev)

wanted_rows = wanted_rows.map(lambda ele: (ele[0],[ele[1]]))
wanted_rows = wanted_rows.reduceByKey(lambda a,b: a+b)
wanted_rows = wanted_rows.sortByKey()

if os.path.exists(sys.argv[2]):
    rmtree(sys.argv[2])
wanted_rows.saveAsTextFile(sys.argv[2])