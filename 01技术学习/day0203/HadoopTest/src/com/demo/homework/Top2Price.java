package com.demo.homework;

/*
    �������Ϊ���룺
        ��Ϊ��Hadoop����GBK�ı�ʱ������������������룬ԭ��HADOOP���漰����ʱ����д����UTF-8������ļ������ʽ���������ͣ���GBK)�����������롣
        ���������
            1. ���ļ������ʽ����ΪUTF-8
 */
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.demo.WordCount;
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
public class Top2Price {


    public static class Top2PriceMapper extends Mapper<LongWritable, Text, Text, Text> {
        //ÿ�ζ�ȡһ��
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String [] reads = value.toString().trim().split(",");
            String orderNumber = reads[0] +'\t' + reads[1];

            Double price = Double.parseDouble(reads[reads.length-2]) * Double.parseDouble(reads[reads.length-1]);
            String object = reads[2] + " " + price;
            System.out.println(reads[2]);
            context.write(new Text(orderNumber), new Text(object));
        }
    }


    public static class Top2PriceReducer extends Reducer<Text, Text, Text, Text> {

        public void reduce(Text key, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {

            Map<String, Double> map = new HashMap<String, Double>();
            for (Text val : values) {
                String[] s = val.toString().split(" ");
                map.put(s[0], Double.parseDouble(s[1]));
            }
            List<Entry<String, Double>> list = new ArrayList<Entry<String, Double>>(map.entrySet());
            //����
            Collections.sort(list, new Comparator<Entry<String, Double>>() {

                @Override
                public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
                    return (o2.getValue() - o1.getValue() > 0) ? 1 : -1;
                }
            });

            String top2 = list.get(0).getKey() + " " + list.get(0).getValue()
                    + "        " + list.get(1).getKey() + " " + list.get(1).getValue();
            context.write(new Text(key), new Text(top2));
        }




    }

    public static void main(String[] args)throws Exception {
        //hadoop���л�����window��Щ���⣬����ʹ�����д���
        System.setProperty("hadoop.home.dir", "D:\\Fighting\\otherSubject\\AI\\�ƿ�ʵѵ_������\\����\\hadoop-2.7.2");
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "WordCount");
        job.setInputFormatClass(TextInputFormat.class);
        //ָ��main�������ڵ���
        job.setJarByClass(Top2Price.class);
        //ָ����ҵ��jobҪʹ�õ�mapperҵ����
        job.setMapperClass(com.demo.homework.Top2Price.Top2PriceMapper.class);
        //ָ��mapper������ݵ�kv����
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);
        //ָ����ҵ��jobҪʹ�õ�Reducerҵ����
        job.setReducerClass(com.demo.homework.Top2Price.Top2PriceReducer.class);
        //ָ���������������kv����
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        job.setOutputFormatClass(TextOutputFormat.class);
        FileSystem fs = FileSystem.get(conf);
        Path inputPath = new Path("Data\\orderproduct.txt");
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
