package solution_in_paper;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.jobcontrol.ControlledJob;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.partition.HashPartitioner;
import org.apache.hadoop.util.GenericOptionsParser;
public class Full_Shuffle_2j {
	static int numReduceTasks =7;
	static String password="xidian320";
	static byte[] encryptV=JAES.encrypt("1", password);
	static ArrayList<String> key_set = new ArrayList<String>();
	static ArrayList<String> S_key_set = new ArrayList<String>();
        static ArrayList<String> S_key_set2 = new ArrayList<String>();
        static int[] MAX = new int[1];
        
/*job1*/
	 public static class MyMapper extends Mapper<LongWritable,Text,Text,Text>{
                
     
		@Override
		protected void setup(Mapper<LongWritable, Text, Text, Text>.Context context)
				throws IOException, InterruptedException {
   
			super.setup(context);
		}
		@Override
		protected void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			String valueStr=value.toString();
			String [] values=valueStr.split("	");
			byte[] decryptK=JAES.decrypt(JAES.parseHexStr2Byte(values[0]), password);
			String s =new String(decryptK).trim();
			context.write(new Text(values[0]), new Text(new String(JAES.parseByte2HexStr(encryptV))));
		}
		@Override
		protected void cleanup(Mapper<LongWritable, Text, Text, Text>.Context context)
			throws IOException, InterruptedException {
		super.cleanup(context);
		}
	}
	public static class ShuffleReduce extends Reducer<Text,Text,Text,Text>{
                static int Max = 0;
                static String[] strarr = new String[1000];
                static int num = 0;
                //static ArrayList<String> S_key_set = new ArrayList<String>();
		@Override
		protected void setup(Reducer<Text,Text, Text, Text>.Context context)
				throws IOException, InterruptedException {
			// TODO Auto-generated method stub
                        
                        //S_key_set.add("1111");
			super.setup(context);
		}
		@Override
		protected void reduce(Text key, Iterable<Text>values,Context context) throws IOException, InterruptedException {
			for(Text v:values){
				byte[] decryptV=JAES.decrypt(JAES.parseHexStr2Byte(v.toString()), password);
				String s=new String(decryptV).trim();
                                int keynum =Integer.parseInt(s);
                                if(Max < keynum){
                                    Max = keynum;
                                  }
				
			}

                        String[] str = key.toString().split("%");
                        for(int i=0;i<str.length;i++){
                            strarr[num] = str[i];
                            num++;
                        }

                        for (String entry : key_set){
                                
				
                                
                          }
			
				
		}	
		@Override
		protected void cleanup(Reducer<Text, Text, Text, Text>.Context context)
				throws IOException, InterruptedException {
			// TODO Auto-generated method stub
                        for(int i= 0;i<num;i++){
                           String enkey = strarr[i];
                           byte[] encryptV=JAES.encrypt(String.valueOf(Max), password);
                           context.write(new Text(enkey), new Text(new String(JAES.parseByte2HexStr(encryptV))));
                           
                        }
			super.cleanup(context);
		}
	}
	static class MyCombiner extends Reducer<Text,Text,Text,Text>{
                static ArrayList<String> S_key_set = new ArrayList<String>();
                static String key_set = "";
		@Override
		protected void setup(Reducer<Text, Text, Text, Text>.Context context) throws IOException, InterruptedException {
			// TODO Auto-generated method stub
			super.setup(context);
		}
		@Override
		protected void reduce(Text key, Iterable<Text> values,Context context) throws IOException, InterruptedException {
                        S_key_set.add(key.toString());
			int count=0;
			for(Text v:values){
				count+=1;
			}
                        byte[] decryptK=JAES.decrypt(JAES.parseHexStr2Byte(key.toString()), password);
                        String keyStr=new String(decryptK).trim();
                        byte[] encryptV=JAES.encrypt(key + "#" +String.valueOf(count), password);
                        key_set = key_set + new String(JAES.parseByte2HexStr(encryptV)) + "%";         
                       
		}
		@Override
		protected void cleanup(Reducer<Text, Text, Text, Text>.Context context)
				throws IOException, InterruptedException {
                        byte[] encryptV=JAES.encrypt(String.valueOf(S_key_set.size()), password);
			context.write(new Text(key_set),new Text(new String(JAES.parseByte2HexStr(encryptV))));
			super.cleanup(context);
		}
	}
	static class MyPartitioner extends HashPartitioner<Text,Text>{
		
		@Override
		public int getPartition(Text key, Text value, int numReduceTasks) {
			int s = (int)(Math.random()*numReduceTasks);
			return s;
		}
	}
/*job2*/
	 public static class MyMapper2 extends Mapper<LongWritable,Text,Text,Text>{
                        static ArrayList<String> S_key_set2 = new ArrayList<String>();
			@Override
			protected void setup(Mapper<LongWritable, Text, Text, Text>.Context context)
					throws IOException, InterruptedException {
				// TODO Auto-generated method stub
				super.setup(context);
			}
			@Override
			protected void map(LongWritable key, Text value, Context context)
					throws IOException, InterruptedException {
				String[] split=value.toString().split("	");
				byte[] decryptK=JAES.decrypt(JAES.parseHexStr2Byte(split[0]), password);	
				byte[] decryptV=JAES.decrypt(JAES.parseHexStr2Byte(split[1]), password);
				String keyStr=new String(decryptK).trim();
				String valueStr=new String(decryptV).trim();
                                String K = keyStr.toString().substring(0,keyStr.toString().indexOf("#"));
                                String V = keyStr.toString().substring(keyStr.toString().indexOf("#")+1,keyStr.toString().length());
                                MAX[0] = Integer.parseInt(V);
				if(! S_key_set2.contains(K)){
				S_key_set2.add(K);
				}
				int r=(K.hashCode()&Integer.MAX_VALUE)%numReduceTasks;
				byte[] encryptK=JAES.encrypt(K, password);
				for(int i=0;i<numReduceTasks;i++){
				    byte[] encryptV=JAES.encrypt(V+"_"+i+"#"+r, password);
				    context.write(new Text(new String(JAES.parseByte2HexStr(encryptK))),new Text(new String(JAES.parseByte2HexStr(encryptV))));
				}
			}
			@Override
			protected void cleanup(Mapper<LongWritable, Text, Text, Text>.Context context)
			throws IOException, InterruptedException {
                                int fakenum = 16;
				int p = MAX[0]-S_key_set2.size()+fakenum;
				for(int i=0;i<numReduceTasks;i++){
					for(int j =0;j<p;j++){
                                             byte[] encryptK=JAES.encrypt("FAKE", password);
                                             byte[] encryptV=JAES.encrypt(0+"_"+i+"#"+i, password);
                                             context.write(new Text(new String(JAES.parseByte2HexStr(encryptK))),new Text(new String(JAES.parseByte2HexStr(encryptV))));
                                    }
				   
				    
				}
			super.cleanup(context);
			}
		}
		public static class ShuffleReduce2 extends Reducer<Text,Text,Text,Text>{
			@Override
			protected void setup(Reducer<Text,Text, Text, Text>.Context context)
					throws IOException, InterruptedException {
				// TODO Auto-generated method stub
				super.setup(context);
			}
			@Override
			protected void reduce(Text key, Iterable<Text>values,Context context) throws IOException, InterruptedException {
				int count=0;
				Text newKey=new Text();
				for(Text value:values){
					byte[] decryptV=JAES.decrypt(JAES.parseHexStr2Byte(value.toString()), password);
					String v = new String(decryptV).trim();
					int j=Integer.parseInt(v.toString().substring(v.toString().indexOf("_")+1,v.toString().indexOf("#")));
					int r=Integer.parseInt(v.toString().substring(v.toString().indexOf("#")+1,v.toString().length()));
		            if(j==r){
							count+=Integer.parseInt(v.toString().substring(0,v.toString().indexOf("_")));
							byte[] decryptK=JAES.decrypt(JAES.parseHexStr2Byte(key.toString()), password);
							newKey=new Text(new String(decryptK).trim().replace("\"", ""));
		            }
				}
				if(count!=0){
				context.write(newKey, new Text(String.valueOf(count)));}
			}
			@Override
			protected void cleanup(Reducer<Text, Text, Text, Text>.Context context)
					throws IOException, InterruptedException {
				// TODO Auto-generated method stub
				super.cleanup(context);
			}
		}
		static class MyPartitioner2 extends HashPartitioner<Text,Text>{
			
			@Override
			public int getPartition(Text key, Text value, int numReduceTasks) {
				byte[] decryptV=JAES.decrypt(JAES.parseHexStr2Byte(value.toString()), password);
				String vuleStr=new String(decryptV).trim();
				int r = Integer.parseInt(vuleStr.substring(vuleStr.indexOf("_")+1,vuleStr.indexOf("#")));
				return r;
			}
		}
		
    public static void main(String[] args) throws IOException,URISyntaxException, ClassNotFoundException, InterruptedException{
    	long startTime=System.currentTimeMillis();
    	
    	Configuration conf = new Configuration();
	String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        if (otherArgs.length < 2) {
         System.err.println("Usage: wordcount <in> [<in>...] <out>");
         System.exit(2);
        }
    	Job job1 =Job.getInstance(conf,"job1"); 
    	//Job job1 =new Job();
    	
    	job1.setJarByClass(Full_Shuffle_2j.class);
    	FileInputFormat.setInputPaths(job1, new Path(otherArgs[otherArgs.length - 3]));
    	
    	job1.setMapperClass(MyMapper.class);
    	job1.setMapOutputKeyClass(Text.class);
    	job1.setMapOutputValueClass(Text.class);
    	//Partition
    	job1.setCombinerClass(MyCombiner.class);
    	job1.setPartitionerClass(MyPartitioner.class); 
    	
    	job1.setReducerClass(ShuffleReduce.class);
    	job1.setOutputKeyClass(Text.class);
    	job1.setNumReduceTasks(1);//reduce 
    	job1.setOutputValueClass(Text.class);
    	
    	FileOutputFormat.setOutputPath(job1, new Path(otherArgs[otherArgs.length - 2]));
    	
        ControlledJob ctrlJob1= new ControlledJob(conf);
        ctrlJob1.setJob(job1);

        Job job2 =Job.getInstance(conf,"job2");
        //Job job2 =new Job();
    	
        job2.setJarByClass(Full_Shuffle_2j.class);
    	FileInputFormat.setInputPaths(job2, new Path(otherArgs[otherArgs.length - 2]));
    	
    	job2.setMapperClass(MyMapper2.class);
    	job2.setMapOutputKeyClass(Text.class);
    	job2.setMapOutputValueClass(Text.class);
    	//Partition
    	job2.setPartitionerClass(MyPartitioner2.class);
    	
    	job2.setReducerClass(ShuffleReduce2.class);
    	job2.setOutputKeyClass(Text.class);
    	job2.setNumReduceTasks(7);
    	job2.setOutputValueClass(Text.class);
    	
    	FileOutputFormat.setOutputPath(job2, new Path(otherArgs[otherArgs.length - 1]));
    	
        ControlledJob ctrlJob2= new ControlledJob(conf);
        ctrlJob2.setJob(job2);    
        if (job1.waitForCompletion(true)) {
                System.exit(job2.waitForCompletion(true) ? 0 : 1);
        	       }
    	long endTime=System.currentTimeMillis();
    	
    }
}
