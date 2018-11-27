Do the following problems from chapter 10 of the Data Mining book. You can find a link to a PDF of that chapter in the syllabus.
  * 10.2 (k-means)
  	* Suppose that the data mining task is to cluster points (with (x, y) representing location) into three clusters, where the points are 
				A1(2,10),A2(2,5),A3(8,4),B1(5,8),B2(7,5),B3(6,4),C1(1,2),C2(4,9).
				The distance function is Euclidean distance. Suppose initially we assign A1, B1, and C1 as the center of each cluster, respectively. Use the k-means algorithm to show only.
						(a) The three cluster centers after the first round of execution.
						(b) The final three clusters  
* 10.10 (BIRCH). Note that you'll have to read about the OPTICS algorithm in the text.
	* Why is it that BIRCH encounters difficulties in finding clusters of arbitrary shape but
OPTICS does not? Propose modifications to BIRCH to help it find clusters of arbitrary
shape.
* 10.12 (density-based clustering)
	* Present conditions under which density-based clustering is more suitable than
partitioning-based clustering and hierarchical clustering. Give application examples to
support your argument.
* 10.18 (constraints and clustering) For this problem I'm just looking for you to propose some ideas. In addition to what's asked for in the book, say something about how your proposed modifications impact the computational complexity of the clustering algorithm.
	* Suppose that you are to allocate a number of automatic teller machines (ATMs) in a
given region so as to satisfy a number of constraints. Households or workplaces may
be clustered so that typically one ATM is assigned per cluster. The clustering, however,
may be constrained by two factors: (1) obstacle objects (i.e., there are bridges, rivers, and
highways that can affect ATM accessibility), and (2) additional user-specified constraints
such as that each ATM should serve at least 10,000 households. How can a clustering
algorithm such as k-means be modified for quality clustering under both constraints?	
