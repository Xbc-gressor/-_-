package com.demo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
//mapreduce��top-n����
public class WordCountTop {
	/**
	 * KEYIN Ĭ������£���mr��ܶ���һ�����ݵ���ʼƫ����
	 * VALUEIN Ĭ������£���mr��ܶ���һ���ı����ݵ�ֵ
	 *
	 * KEYOUT �û��Զ����߼��������֮���key ���� String
	 * VALUEOUT �û��Զ����߼��������֮���value ���� Integer
	 *
	 **/

	public static class WordCountTopMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
		//ÿ�ζ�ȡһ��
		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
			String line = value.toString();
			//���տո��зֵ���
			String[] words = line.split(" ");
			//���������Ϊ<���ʣ�1>
			for(String word : words) {
				//��������Ϊkey,����1��Ϊvalue,�Ա��ں������ݷַ�reduce task
				context.write(new Text(word), new IntWritable(1));
			}
		}
	}
	/**
	 *  KEYIN VALUEIN ��Ӧmapper�����KEYOUT VALUEOUT���Ͷ�Ӧ
	 *  KEYOUT VALUEOUT ���Զ���reduce�߼����������������
	 *
	 */

	public static class WordCountTopReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
		//���弯��map�洢ÿ�鵥�ʺ͵��ʳ��ִ���
		Map<String,Integer> map=new HashMap<String, Integer>();
		public void reduce(Text key, Iterable<IntWritable> values, Context context)
				throws IOException, InterruptedException {
			//ѭ�������
			int count = 0;
			for (IntWritable val : values) {
				count += val.get();

			}
			//����
			String word=key.toString();
			//�ѵ��ʺ͵��ʳ��ִ��������map�У��������������
			map.put(word, count);


		}
		//ȫ�ִ���reduce����������map����
		protected void cleanup(Context context)throws IOException, InterruptedException {
			//map.entrySet()��map�ļ�ֵ��ת��ΪSet����
			//Ȼ���set����ת��Ϊlist���Ϸ�������
			//Entry:��ֵ�Զ���
			List<Entry<String,Integer>> list = new ArrayList<Entry<String,Integer>>(map.entrySet());
			//����
			Collections.sort(list,new Comparator<Entry<String,Integer>>(){

			@Override
			public int compare(Entry<String, Integer> e1,Entry<String, Integer> e2) {
				 //Entry�����ֵ���н�������e2-e1����   e1-e2����
				return (int) (e2.getValue() - e1.getValue());
			}});

			//�������ȡ�ü��������ݵ�ǰ2��������
			 for(int i=0;i<2;i++){
				 //����:list.get(i).getKey(),list.get(i).getValue()���ʳ��ִ���
		           context.write(new Text(list.get(i).getKey()), new IntWritable(list.get(i).getValue()));

		    }



		}





	}

	public static void main(String[] args)throws Exception {
		//hadoop���л�����window��Щ���⣬����ʹ�����д���
		System.setProperty("hadoop.home.dir", "D:\\Fighting\\otherSubject\\AI\\�ƿ�ʵѵ_������\\����\\hadoop-2.7.2");
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "WordCount");
		job.setInputFormatClass(TextInputFormat.class);
		//ָ��main�������ڵ���
		job.setJarByClass(WordCount.class);
		//ָ����ҵ��jobҪʹ�õ�mapperҵ����
		job.setMapperClass(WordCountTopMapper.class);
		//ָ��mapper������ݵ�kv����
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);
		//ָ����ҵ��jobҪʹ�õ�Reducerҵ����
		job.setReducerClass(WordCountTopReducer.class);
		//ָ���������������kv����
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);

		job.setOutputFormatClass(TextOutputFormat.class);
		FileSystem fs = FileSystem.get(conf);
		Path inputPath = new Path("Data\\wordcount.txt");
		Path outputPath = new Path("output");
		if(fs.exists(outputPath)){
			fs.delete(outputPath, true);
		}
		//ָ��job������ԭʼ�ļ����ڵ�Ŀ¼�Լ�������
		FileInputFormat.setInputPaths(job, inputPath);
		FileOutputFormat.setOutputPath(job, outputPath);
		//�ύ��ҵ�ȴ�ִ����ɣ��÷����Ĳ�������Ϊtrue��ʾ����ҵ����д������̨
		boolean isdone = job.waitForCompletion(true);
		//ִ�гɹ�(true)ʧ�ܣ�false��
		System.out.print(isdone ? "ִ�гɹ�" : "ִ��ʧ��");
	}

}
