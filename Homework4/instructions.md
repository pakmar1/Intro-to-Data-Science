

CMSC 691: 
Introduction to Data Science:
Homework 4
Name: Prasad Akmar<pakmar1@umbc.edu>
Student ID: LE10772
K-means implementation using Hadoop MapReduce framework:
program: Kmeans.java

steps to run:
1) compile:
	hadoop com.sun.tools.javac.Main Kmeans.java
2) create jar executable
	jar cf kmn.jar Kmeans*.class
	
3) run the program
	hadoop jar jarfile Kmeans k n input_dir output_dir

	k: number of clusters
	n: dimensionality of a point
	input_dir: directory of input file
	output_dir: directory of output file(s)

Check the output in:
output_dir/part-r-00000 file
