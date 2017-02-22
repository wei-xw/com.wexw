package wordcount;

import java.io.IOException;
import java.net.URI;
import java.util.StringTokenizer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
public class chartsConnect {
	public static class WcountMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

		private final static IntWritable one = new IntWritable(1);
		private Text word =new Text();
		public void map(LongWritable ikey, Text ivalue, Context context)
				throws IOException, InterruptedException {
			StringTokenizer itr=new StringTokenizer(ivalue.toString());
			while(itr.hasMoreTokens()){
				word.set(itr.nextToken());
				context.write(word, one);
			}
		}

	}
	public static class WcountReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
		private IntWritable result =new IntWritable();

		public void reduce(Text _key, Iterable<IntWritable> values, Context context)
				throws IOException, InterruptedException {
			int sum =0;
			for (IntWritable val : values) {
				sum+=val.get();
			}
			result.set(sum);
			context.write(_key,result);	
		}

	}

	 public static void main(String[] args) throws Exception {
		 Configuration conf = new Configuration();
		    String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
		    if (otherArgs.length != 2) {
		      System.err.println("Usage: wordcount <in> <out>");
		      System.exit(2);
		    }
		    final FileSystem fs = FileSystem.get(new URI(otherArgs[0]), conf);
	        if(fs.exists(new Path(otherArgs[1]))){
	            fs.delete(new Path(otherArgs[1]),true);
	        }
		    
		    Job job = Job.getInstance(conf, "wordcount");//new Job(conf, "LazyMapReduce");
		    job.setJarByClass(chartsConnect.class);
		    job.setMapperClass(WcountMapper.class);
		    job.setReducerClass(WcountReducer.class);
		    job.setOutputKeyClass(Text.class);
		    job.setOutputValueClass(IntWritable.class);
		        //设置任务数据的输入、输出路径；
		    FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
		    FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
		        //执行job任务，执行成功后退出；
		    System.exit(job.waitForCompletion(true) ? 0 : 1);
	 }



}
