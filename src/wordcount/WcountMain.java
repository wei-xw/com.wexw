package wordcount;

import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.io.Text;


public class WcountMain {
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
		    job.setJarByClass(WcountMain.class);
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
