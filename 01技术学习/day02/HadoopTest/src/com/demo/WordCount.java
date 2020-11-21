package com.demo;

import java.io.IOException;
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

public class WordCount {						      // KEYIN, VALUEIN, KEYOUT, VALUEOUT
												      //    ƫ����    ����    ���
	public static class WordCountMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
		/*
		 �������ݣ�
		 hello world
		 hello China
		 hello China
		 */

		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException{
			// ÿ�ζ�ȡһ��
			String line = value.toString();
			// ���տո��зֵ���
			String[] words = line.split(" ");
			// ���������Ϊ<����, 1>
			for(String word: words) {
				// ��������Ϊkey�� ����1��Ϊvalue�� �Ա��ں������ݷַ�reduce task
				context.write(new Text(word), new IntWritable(1));
			}
			// ���map�������ʽ������key��������(China, 1) (China, 1) (hello, 1) (hello, 1) (hello, 1) (world, 1)
		}
	}

	public static class WordCountReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
		public void reduce(Text key, Iterable<IntWritable> values, Context context)
			throws IOException, InterruptedException {
			// һ�����ʳ��ֵĴ����ĺ�
			int sum = 0;
			for (IntWritable val: values) {
				sum += val.get();
			}
			context.write(key, new IntWritable(sum));
		}

		@Override
		protected void cleanup(Context context) throws IOException, InterruptedException {
			super.cleanup(context);
		}
	}

	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		// ������д��룬��windows��
		System.setProperty("hadoop.home.dir", "D:\\Fighting\\otherSubject\\AI\\�ƿ�ʵѵ_������\\����\\hadoop-2.7.2");
		Configuration conf = new Configuration();
		// ����ҵ��jib��
		Job job = Job.getInstance(conf, "WordCount");
		// ָ��main�������ڵ���
		job.setJarByClass(WordCount.class);

		// ָ����ҵ��job��Ҫʹ�õ�mapperҵ����
		job.setMapperClass(WordCountMapper.class);
		// ָ����mapper��������ݵ�key-value����
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);

		// ָ����ҵ��Ҫʹ�õ�Reducerҵ����
		job.setReducerClass(WordCountReducer.class);
		// ָ�����յ����������kv����
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);

		FileSystem fs = FileSystem.get(conf);
		// ��������ļ���·��
		Path inputPath = new Path("Data:\\wordcount.txt");
		// ���������ļ�·��
		Path outputPath = new Path("output");
		if(fs.exists(outputPath)) {
			fs.delete(outputPath, true);
		}
		// ָ��job������ԭʼ�ļ����ڵ�Ŀ¼�Լ�������
		FileInputFormat.setInputPaths(job, inputPath);
		FileOutputFormat.setOutputPath(job, outputPath);
		// �ύ��ҵ�ȴ�ִ����ɣ��÷����Ĳ�������Ϊtrue��ʾ����ҵ����д������̨
		boolean isDone = job.waitForCompletion(true);
		// ִ�гɹ���ʧ�ܣ��������ֵ��ת���ɳ����˳��Ĵ���0��1
		if(isDone) {
			System.out.println("ִ�гɹ�");
		}else {
			System.out.println("ִ��ʧ��");
		}
	}

}
