from pyspark import SparkContext
import sys
import csv
import os 

sc = SparkContext("local", "app")

if os.path.exists("temp.txt"):
  os.remove("temp.txt")
file = open("temp.txt", "w") 

with open(sys.argv[1]) as tsvfile:
    tsvreader = csv.reader(tsvfile,delimiter ="\t")
    for line in tsvreader:
        file.write(line[0]+' ')
    file.close()

graph_file = sc.textFile("temp.txt")



counts = graph_file.flatMap(lambda line: line.split(" "))\
    .map(lambda degree: (degree,1)) \
    .reduceByKey(lambda a,b: a+b)

counts.saveAsTextFile(sys.argv[2])

os.remove("temp.txt")