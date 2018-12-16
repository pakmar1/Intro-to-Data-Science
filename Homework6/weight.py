from pyspark import SparkContext
import sys

sc = SparkContext("local", "app")

graph_file = sc.textFile(sys.argv[1])

counts = graph_file.map(lambda line: line.split('\t')[0]+'\t'+line.split('\t')[2]) \
                   .map(lambda degree: (degree.split('\t')[0],int(degree.split('\t')[1]))) \
                   .reduceByKey(lambda a,b: a+b) \
                   .sortByKey(ascending=True)

counts.saveAsTextFile(sys.argv[2])