package chartsconnect;

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
	public static class WcountMapper extends Mapper<LongWritable, Text, Text, Text> {
		public void map(LongWritable ikey, Text ivalue, Context context)
				throws IOException, InterruptedException {
			String line =ivalue.toString();
			String[] str=line.split(",");
			if(str.length>6)
			{

				String tmp=str[20];
				if(tmp.length()>3&&tmp.substring(tmp.length()-3).equals("000")){
					tmp=tmp.substring(0, tmp.length()-3);
				}
				context.write(new Text(tmp), new Text("1,"+str[8]+","+str[1]+","+tmp));
                
			}
			else if(str.length>3)
				context.write(new Text(str[3]), new Text("2,"+str[0]+","+str[1]+","+str[3]));
			}
		}
	public static class WcountReducer extends Reducer<Text, Text, Text, Text> {
		public void reduce(Text _key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {
			int i =0;
			String str="o";
			String[] strA=new String[30];
			for (Text val : values) {
				String tmp=val.toString();
				if(tmp.charAt(0)=='2'){
					str=tmp.substring(2);
				}
				else {
					strA[i]=tmp.substring(2);	
					i++;
				}			
			}
			if(i!=0){
				for(int i1=0;i1<i;i1++){
					if(!str.equals("o")){
						context.write(new Text(strA[i1]), new Text(str));
					}
				}
			}
			

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
		    job.setOutputValueClass(Text.class);
		        //设置任务数据的输入、输出路径；
		    FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
		    FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
		        //执行job任务，执行成功后退出；
		    System.exit(job.waitForCompletion(true) ? 0 : 1);
	 }
}
