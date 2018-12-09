In this final homework you'll write a couple of simple Spark programs to compute statistics on a graph. The first thing to do is install Spark. For Linux systems, do the following:
Go to the Spark downloads page and get the latest version. I got Spark 2.4.0 pre-built for Hadoop 2.7. For what we're doing, you don't actually need to have Hadoop installed, so don't worry about that.
gunzip and untar the file that you downloaded.
For me, that resulted in a directory named spark-2.4.0-bin-hadoop2.7, which I moved to ~/spark.
You'll also need to have Java installed on your machine.
To run a python program, you just give the name of the file and any arguments to the submit-spark script. For example, suppose you've got the script below in a file named wordcount.py in your home directory.

  from pyspark import SparkContext
  import sys

  sc = SparkContext("local", "app")

  text_file = sc.textFile(sys.argv[1])
  counts = text_file.flatMap(lambda line: line.split(" ")) \
             .map(lambda word: (word, 1)) \
             .reduceByKey(lambda a, b: a + b)
  counts.saveAsTextFile(sys.argv[2])

To count the words in a file named book.txt and put the results in a directory named output, execute the following command:

  ~/spark/bin/submit-spark ~/wordcount.py book.txt output

Here's what you need to do:

Get graph.tsv, which contains a file in which each line is a triple of the form: SOURCE_NODE <TAB> DESTINATION_NODE <TAB> WEIGHT. Note that source and destination nodes are integers, as are weights.
Write one python program per item in the list below that uses Spark to compute the desired information. Each program should accept two command line parameters: a path to the graph.tsv file and a path to an output directory. Use Spark's saveAsTextFile() to save the final RDD to the specified output directory. Note that, by default, if the target output directory already exists when you attempt to save to it, you'll get an error. One solution is to remove the target directory between runs.
  * For each node, compute the outdegree (number of outgoing edges) and output the (node, count) pairs in sorted order by node. The code should be in a single file named outdegree.py.
  * For each node, compute the sum of weights of incoming edges and output the (node, weight_sum) pairs in order sorted by node. The code should be in a single file named weight.py.
  * For each node X, find a list of all other nodes Y such that there is an (X, Y) edge in the graph and a (Y, X) edge in the graph, and output the (X, [Y1, Y2, ..., Yn]) pairs in order sorted by X. Hint: I solved this by building two RDDs, one in which edge source nodes are keys and destination nodes are values, and one in which edge destination nodes are keys and source nodes are values. The code should be in a single file named pairs.py.

Put all 3 programs in a tarball and submit it via slack to the TA. A goal in all cases is to load the data in and keep it within Spark for as long as possible. That is, resist the temptation to, for example, do a .collect() and do the rest of the work in pure python. My code for the 3rd program stayed in Spark RDDs the entire time.
