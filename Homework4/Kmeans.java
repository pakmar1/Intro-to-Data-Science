import java.awt.Point;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.io.FileReader;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.lang.Object;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class Kmeans {

	private static String INPUT_PATH;
    private static String OUTPUT_PATH;
	private static int COUNT_CLUSTER;
	private static int CLUSTER_DIMENSION;
	private static List<Float[]> prev_centroids =null;

	/**
	 * @param args
	 * @author: Prasad Akmar<pakmar1@umbc.edu>
	 * @Studentid: LE10772 
	 * @compile: hadoop com.sun.tools.javac.Main Kmeans.java
	 *  
	 * @Usage: hadoop jar jarfile Kmeans k n input output
	 * 
	 * 
	 */
	public static final String hdfsPath = "centroids/cluster";
	
	public Kmeans(int k, int n,String inputPath, String outputPath) 
	{
        INPUT_PATH = inputPath;
        OUTPUT_PATH = outputPath;
		COUNT_CLUSTER = k;
		CLUSTER_DIMENSION = n;
	}
	
	public static class FloatArrayWritable extends ArrayWritable implements WritableComparable<ArrayWritable>
	{		
		public FloatArrayWritable() {
		 super(FloatWritable.class);
		}

		public FloatArrayWritable(FloatArrayWritable[] val) {
			super(FloatWritable.class,val);
		}

		@Override
		public int compareTo(ArrayWritable obj) 
		{ 
			Writable[] a = this.get();
			Writable[] b = obj.get();
			
			for(int i = 0; i < a.length; i++) 
			{
				float diff = Float.parseFloat(a[i].toString())-Float.parseFloat(b[i].toString());
				if(diff > 0.0001)
					return 1;
				else if(diff < -0.0001)
					return -1;
			}
			return 0;
		}

		public String toString()
		{
			Writable[] key = this.get();
			String ret = "";
			for(int i=0; i<key.length; i++)
			{
				if(i == key.length-1)
				{
					ret+=key[i].toString();
				}
				else
					ret+=key[i].toString() + " ";
			}
			return ret;
		}
	}

	@SuppressWarnings("deprecation")
	public static class KmMapper extends Mapper<LongWritable, Text, FloatArrayWritable, FloatArrayWritable>
	{
		List<Float[]> centroids;
		
		public void setup(Context context) throws IOException
		{
			centroids = new ArrayList();
			URI[] cacheFiles = context.getCacheFiles();
			if(cacheFiles!=null && cacheFiles.length > 0)
			{
				BufferedReader cacheReader = new BufferedReader(new FileReader(cacheFiles[0].toString()));
				String line = "";
				int i=0;
				while ( (line = cacheReader.readLine() )!= null) 
				{
					String[] arr = line.split(" ");
					Float[] temp = new Float[CLUSTER_DIMENSION];
					for(int j=0;j<arr.length;j++)
					{	 
						temp[j] = Float.parseFloat(arr[j]);
					}
					centroids.add(temp);
					i++;
				}
				prev_centroids = new ArrayList<>(centroids);
			}
		}

		public static float distance(Float[] a, float[] b)
		{
			if( a == null || b == null || a.length != b.length)  return Float.MAX_VALUE;
			float dis = 0;
			for ( int i = 0; i < a.length; i++)
			{
				dis += Math.pow(a[i] - b[i], 2);
			}
			return (float) Math.sqrt(dis);
		}

		public void map(LongWritable key, Text value, Context context) throws InterruptedException, IOException
		{

			FloatArrayWritable emitKey = new FloatArrayWritable();
			FloatArrayWritable emitValue = new FloatArrayWritable();
			FloatWritable[] clusterPoint = new FloatWritable[CLUSTER_DIMENSION];
			FloatWritable[] val = new FloatWritable[CLUSTER_DIMENSION];

			String line = value.toString();
			String dataPoints[] = line.split(" ");
			float[] dataPoint = new float[dataPoints.length];
			float distance = 0.0f;
			float min = Float.MAX_VALUE;
			float current = 0.0f;

			for(int j=0; j<dataPoints.length; j++)
			{
				dataPoint[j] = Float.valueOf(dataPoints[j]);
				val[j] = new FloatWritable(dataPoint[j]);
			}
			
			
			for(int i=0; i<centroids.size(); i++)
			{
				distance = distance(centroids.get(i), dataPoint);
				current = distance;
				if(min >= current)
				{
					min = current;
					for(int j=0;j<CLUSTER_DIMENSION;j++)
					{
						clusterPoint[j] = new FloatWritable(centroids.get(i)[j]);
						
					}
				}
			}
			emitKey.set(clusterPoint);
			emitValue.set(val);
			context.write(emitKey, emitValue);
		}
	}

	
	@SuppressWarnings("deprecation")
	public static class KmReducer extends Reducer<FloatArrayWritable, FloatArrayWritable, FloatArrayWritable, FloatArrayWritable>
	{
		FloatArrayWritable emitKey = new FloatArrayWritable();
		FloatArrayWritable emitValue = null;

		public void reduce(FloatArrayWritable key, Iterable<FloatArrayWritable> values, Context context) throws IOException, InterruptedException
		{
			FloatWritable[] newVal = new FloatWritable[CLUSTER_DIMENSION];
			float[] sumVal = new float[CLUSTER_DIMENSION];
			int counter = 0;

			for(FloatArrayWritable value : values)
			{
				Writable[] point = value.get();
				for(int j=0;j<point.length;j++)
				{
					sumVal[j] += Float.parseFloat(point[j].toString());
				}
				counter++;
			}
			
			for(int i=0;i<CLUSTER_DIMENSION;i++)
			{
				newVal[i] =  new FloatWritable(sumVal[i]/counter); 
			}

			
			emitKey.set(newVal);
			//emitValue.set(clusterInfo);
			context.write(emitKey, emitValue);
		}
	}
	
	private static void createCentroidFile(int COUNT_CLUSTER, int CLUSTER_DIMENSION) throws IOException
	{
		File file = new File(hdfsPath);
		if (file.getParentFile().mkdir()) 
		{
			file.createNewFile();
		} 
		else 
		{
			throw new IOException("Failed to create directory " + file.getParent());
		}
		BufferedWriter writer = new BufferedWriter(new FileWriter(hdfsPath));

		for(int i=1; i <= COUNT_CLUSTER; i++)
		{
			for(int j=0; j< CLUSTER_DIMENSION; j++)
			{
				if(j == CLUSTER_DIMENSION-1)
				{
					writer.write(String.valueOf(i)+"\n");
				}
				else
				{
					writer.write(String.valueOf(i)+" ");
				}
			}
		}
		writer.close();
	}

	public static boolean readOutput(String file1) throws IOException
	{
		List <Float[]> fcontent = new ArrayList<>();
		BufferedReader br = new BufferedReader(new FileReader(file1));
		String line = "";
		int count=0;
		while ((line = br.readLine() )!= null) {
			String[] arr = line.split(" ");
			Float[] temp = new Float[CLUSTER_DIMENSION];
			for(int j=0;j<arr.length;j++)
			{
				temp[j] = Float.valueOf(arr[j]);
			}
			fcontent.add(temp);
		count++;
		}
		br.close();
		float tsum = 0;
		float distance;
		for(int k=0; k<fcontent.size(); k++)
		{
			System.out.println("prev: " + Arrays.toString(prev_centroids.get(k)));
			System.out.println("cur: " + Arrays.toString(fcontent.get(k)));
		}
		
		for(int i=0; i<fcontent.size(); i++)
		{
			float sum = 0;
			for(int j=0; j<CLUSTER_DIMENSION; j++)
			{
				float diff = Math.abs(fcontent.get(i)[j] - prev_centroids.get(i)[j]);
				sum += (float)(diff * diff);
			}
			distance = (float) Math.sqrt(sum);
			tsum += distance;
		}
		return (tsum/count) > 0.1 ? false : true;
	}

	private static void deleteFolder(String opath) 
	{
        File index = new File(opath);
		String[]entries = index.list();
		if(entries==null)
		{	return;
	
		}
		if(entries.length!=0)
		{
			for(String s: entries)
			{
            	File currentFile = new File(index.getPath(),s);
            	currentFile.delete();
			}
		}
		index.delete();
    }


	private static void copyFileUsingStream(File source, File dest) throws IOException 
	{
		InputStream is = null;
		OutputStream os = null;
		try {
			is = new FileInputStream(source);
			os = new FileOutputStream(dest);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = is.read(buffer)) > 0) {
				os.write(buffer, 0, length);
			}
		} finally {
			is.close();
			os.close();
		}
	}

	public static void main(String args[]) throws Exception
	{
		Configuration conf = new Configuration();
		String otherArgs[] = new GenericOptionsParser(conf, args).getRemainingArgs();

		if(otherArgs.length != 4)
		{
			System.out.println("Usage is: hadoop jar jarfile MainClass k n input output");
			System.exit(1);
		}
		Kmeans kmeans = new Kmeans(Integer.valueOf(otherArgs[0]),Integer.valueOf(otherArgs[1]),otherArgs[2], otherArgs[3]);
		boolean isFirst = true;
		int iteration = 0;

		deleteFolder(OUTPUT_PATH);

		createCentroidFile(COUNT_CLUSTER,CLUSTER_DIMENSION);
		
		do
		{
			if(iteration == 50)
				break;
			
			Job job = Job.getInstance(conf, "Kmeans Clustering algorithm");
			if(!isFirst)
			{
				String p = OUTPUT_PATH+"/part-r-00000";
				if(readOutput(p))
				{
					System.out.println("Converged");
					break;
				}
				copyFileUsingStream(new File(OUTPUT_PATH+"/part-r-00000"),new File(hdfsPath));
				deleteFolder(OUTPUT_PATH);
			}
			job.addCacheFile(new Path(hdfsPath).toUri());
			isFirst = false;

			job.setJarByClass(Kmeans.class);
			
			job.setMapperClass(KmMapper.class);
			job.setReducerClass(KmReducer.class);
			
			job.setMapOutputKeyClass(FloatArrayWritable.class);
			job.setMapOutputValueClass(FloatArrayWritable.class);
			
			job.setOutputKeyClass(FloatArrayWritable.class);
			job.setOutputValueClass(FloatArrayWritable.class);
			
			job.setInputFormatClass(TextInputFormat.class);
			job.setOutputFormatClass(TextOutputFormat.class);
			
			FileInputFormat.addInputPath(job, new Path(INPUT_PATH));
			FileOutputFormat.setOutputPath(job, new Path(OUTPUT_PATH));
			job.waitForCompletion(true);
			iteration++;

		}while(true);
		deleteFolder("centroids");
	}
}