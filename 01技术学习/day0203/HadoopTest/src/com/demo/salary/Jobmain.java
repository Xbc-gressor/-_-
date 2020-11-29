package com.demo.salary;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.File;

// �̳����ã�ʵ�����������
public class Jobmain extends Configured implements Tool {


	@Override
	public int run(String[] arg0) throws Exception {

		// 1. ���� job ����
		Job job = Job.getInstance(super.getConf(), "salary");
		// ���� jar ����
		job.setJarByClass(Jobmain.class);

		//2. ���� job ���� �˴���
		// ������������
		job.setInputFormatClass(TextInputFormat.class);
		// ���ö�ȡ�ļ���·��
		TextInputFormat.addInputPath(job, new Path("Data\\51job_salary.txt"));
		// �����Զ���� Mapper ��
		job.setMapperClass(SalaryMapper.class);
		// ����Map�׶ε������������
		// key
		job.setMapOutputKeyClass(Text.class);
		// value
		job.setMapOutputValueClass(Text.class);

		// �� �����ġ��塢���� shuffle �׶β���Ĭ��

		///// ���ö���� Reducer ��
		job.setReducerClass(SalaryReduce.class);
		///// ���� reduce �׶� ����� k3,v3����������
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		///// ���� �������
		job.setOutputFormatClass(TextOutputFormat.class);
		///// �����·��
			// �Ѿ����ڣ�Ҫɾ��ԭĿ¼
		File dirFile = new File("output\\salaryOut");
		if (dirFile.exists()){
			File files[] = dirFile.listFiles();
			for(int i=0;i<files.length;i++)
				files[i].delete();
			dirFile.delete();
			System.out.println("�������еĽ����");
		}
		Path outputPath = new Path("output\\salaryOut");
		TextOutputFormat.setOutputPath(job, outputPath);
//		if(fs.exists(outputPath)) {
//			fs.delete(outputPath, true);
//		}

		// 3.�ȴ��������
		boolean b = job.waitForCompletion(true);
		// ����ֵΪ int ���� , �� boolean ת int
		return b?0:-1;
	}

	public static void main(String[] args) throws Exception {
		// ������д��룬��windows��
		Configuration configuration = new Configuration();
		System.setProperty("hadoop.home.dir", "D:\\Fighting\\otherSubject\\AI\\�ƿ�ʵѵ_������\\����\\hadoop-2.7.2");
		int run = ToolRunner.run(configuration, new Jobmain(), args);
		System.exit(run);
	}

}
