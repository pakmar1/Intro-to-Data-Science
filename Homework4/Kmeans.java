import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class Kmeans {

	public static String INPUT_PATH;
    public static String OUTPUT_PATH;
	public static int COUNT_CLUSTER;
	public static int CLUSTER_DIMENSION;
	/**
	 * @param args
	 * @Usage: hadoop jar jarfile mainclass inputlocation outputlocation
	 * 
	 * Example:- This is used to cluster the n data points into k clusters by Kmeans cluseting Algorithm... It is basic implimentation
	 * 
	 * Take 10 points in 2-Dimensional space like [(1,2), (2,3), (3,4), (4,5), (10,10), (11,10), (3,3), (9,8), (12,10), (10,9)]
	 * Take two initial centriods like (3,4), (10,10) put these in this 0.0.0.0.0."/kmeans/cluster" file one per line...
	 * Put all the 10 points in the inputfile one per line.....
	 */
	public static final String hdfsPath = "kmeans/cluster";
	
	public Kmeans(int k, int n,String inputPath, String outputPath) 
	{
        INPUT_PATH = inputPath;
        OUTPUT_PATH = outputPath;
		COUNT_CLUSTER = k;
		CLUSTER_DIMENSION = n;
	}
	
	@SuppressWarnings("deprecation")
	public static class KmMapper extends Mapper<LongWritable, Text, Text, Text>
	{
		Integer[][] centroids = new Integer[COUNT_CLUSTER][CLUSTER_DIMENSION];

		public void setup(Context context) throws IOException
		{
			FSDataInputStream in = null;
            BufferedReader br = null;
            FileSystem fs = FileSystem.get(context.getConfiguration());
            Path path = new Path(hdfsPath);
            in = fs.open(path);
            br  = new BufferedReader(new InputStreamReader(in));
			String line = "";
			int i=0;	
			while ((line = br.readLine())!= null)
			{
				String[] arr = line.split(" ");
				for(int j=0; j<arr.length;j++)
				{
					centroids[i][j]= Integer.valueOf(arr[j]);	
					System.out.print(centroids[i][j]);	
				}
				System.out.println();
				i++;			
			}
			
            in.close();
		}

		public static double distance(Integer[] a, Integer[] b)
		{
			if( a == null || b == null || a.length != b.length)  return Double.MAX_VALUE;
			double dis = 0;
			for ( int i = 0; i < a.length; i++){
				dis += Math.pow(a[i] - b[i], 2);
			}
			return Math.sqrt(dis);
		}

		public void map(LongWritable key, Text value, Context context) throws InterruptedException, IOException
		{
			String line = value.toString();
			String dataPoints[] = line.split(" ");
			Integer[] dataPoint = new Integer[dataPoints.length];

			for(int i = 0; i < dataPoints.length; i++)
			{
				dataPoint[i] = Integer.valueOf(dataPoints[i]);
			}
			float distance = 0.0f;
			Text emitValue = new Text();
			Text emitKey = new Text();
			float min = Float.MAX_VALUE;
			float current = 0.0f;
			String clusterPoint = "";
			for(int i=0; i<centroids.length; i++)
			{
				distance = (float) distance(centroids[i], dataPoint);
				current = distance;
				if(min >= current)
				{
					min = current;
					String temp = String.valueOf(centroids[i][0]);
					for(int j=1; j<centroids[0].length; j++)
					{
						temp = " "+ centroids[i][j];
					}
					clusterPoint = temp;
				}
			}
			
			emitKey.set(clusterPoint);
			emitValue.set(line);
			context.write(emitKey, emitValue);
		}
	}
	
	@SuppressWarnings("deprecation")
	public static class KmReducer extends Reducer<Text, Text, Text, Text>
	{
		Text emitKey = new Text();
		Text emitValue = new Text();

		public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException
		{

			float[] newVal = new float[CLUSTER_DIMENSION];
			int[] sumVal = new int[CLUSTER_DIMENSION];

			int counter = 0;
			String clusterPoints = "";
			
			for(Text value : values)
			{
				String line = value.toString();
				String coordinates[] = line.split(" ");

				for(int i=0; i<coordinates.length; i++)
				{
					sumVal[i]= sumVal[i]+ Integer.valueOf(coordinates[i]);
				}
				counter++;
			}
			
			for(int i=0;i<CLUSTER_DIMENSION;i++)
			{
				newVal[i] = (float) sumVal[i]/counter;
			}

			String clusterKey = "Cluster: " + key.toString();
			String clusterInfo = String.valueOf(newVal[0]);
			
			for(int i=1; i<CLUSTER_DIMENSION; i++)
			{
				clusterInfo += 	" " + newVal[i];
			}
			emitKey.set(clusterKey);
			emitValue.set(clusterInfo);
			context.write(emitKey, emitValue);
		}
	} 
	
	public static void main(String args[]) throws Exception
	{
		Configuration conf = new Configuration();
		String otherArgs[] = new GenericOptionsParser(conf, args).getRemainingArgs();

		if(otherArgs.length != 4)
		{
			System.out.println("Usage is: hadoop jar jarfile MainClass input output");
			System.exit(1);
		}
		Kmeans kmeans = new Kmeans(Integer.valueOf(otherArgs[0]),Integer.valueOf(otherArgs[1]),otherArgs[2], otherArgs[3]);
		//COUNT_CLUSTER = Integer.valueOf(otherArgs[0]);
		//CLUSTER_DIMENSION = Integer.valueOf(otherArgs[1]);

		Job job = Job.getInstance(conf, "Kmeans Clustering algorithm");
		job.setJarByClass(Kmeans.class);
		
		job.setMapperClass(KmMapper.class);
		job.setReducerClass(KmReducer.class);
		
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		
		FileInputFormat.addInputPath(job,new Path(INPUT_PATH));
		FileOutputFormat.setOutputPath(job, new Path(OUTPUT_PATH));

		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}