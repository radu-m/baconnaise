package hadoop;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

/**
 * Created by miul on 12/13/2014.
 */
public class driver_example {

    public static void main(String[] args) throws Exception {

        // Create config and load xml files
        Configuration config = new Configuration();

        config.addResource(new Path("D:/Java_stuff/libs/hadoop-2.6.0/etc/hadoop/core-site.xml"));
        config.addResource(new Path("D:/Java_stuff/libs/hadoop-2.6.0/etc/hadoop/hdfs-site.xml"));

        // Create MapReduce job
        Job mapjob = new Job(config,"MrBoolDriver.class");
        mapjob.setJarByClass(driver_example.class);
        mapjob.setJobName("Bacon MapReduce");

        // Set output kay and value class
        mapjob.setOutputKeyClass(Text.class);
        mapjob.setOutputValueClass(Text.class);

        // Set Map class
        mapjob.setMapperClass(map_example.class);
        mapjob.setNumReduceTasks(30);

        // Set reducer class
        mapjob.setCombinerClass(reduce_example.class);
        mapjob.setReducerClass(reduce_example.class);

        mapjob.setMapOutputKeyClass(Text.class);
        mapjob.setMapOutputValueClass(Text.class);

        // Set number of reducer tasks
        mapjob.setNumReduceTasks(10);

        mapjob.setInputFormatClass(TextInputFormat.class);
        mapjob.setOutputFormatClass(TextOutputFormat.class);

        FileInputFormat.addInputPath(mapjob, new Path("/files"));
        FileOutputFormat.setOutputPath(mapjob,new Path("/output"));

        // Start MapReduce job
        mapjob.waitForCompletion(true);

    }

}
