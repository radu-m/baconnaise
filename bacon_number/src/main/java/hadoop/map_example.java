package hadoop;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.StringTokenizer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * Created by miul on 12/13/2014.
 */
public class map_example extends Mapper<LongWritable, Text, Text,Text> {

    // Create variables which will be used in the application
    Path keyfilepath;
    BufferedReader key_buff_reader;
    Text tweet_val = new Text();

    public void map(LongWritable key, Text value, Context context)  {
        try{

            // Create configuration
            Configuration config = new Configuration();

            // Load core files in configuration
            config.addResource(new Path("/usr/etc/hadoop/core-site.xml"));
            config.addResource(new Path("/usr/etc/hadoop/hdfs-site.xml"));

            // Open file
            keyfilepath=new Path("/usr/inputs/workspace/Sent_Analysis_Twitter/files/keywords.txt");
            FileSystem file_sys = FileSystem.get(URI.create("files/keywords.txt"),new Configuration());

            // Load in buffer
            key_buff_reader=new BufferedReader(new InputStreamReader(file_sys.open(keyfilepath)));

            String key_word = "";

            while(key_buff_reader.ready())
            {
                key_word=key_buff_reader.readLine().trim();

            }

            final Text ers_key = new Text(key_word);

            if(value == null)
            {
                return;
            }
            else{
                StringTokenizer str_tokens = new StringTokenizer(value.toString(),",");
                int cnt = 0;

                while(str_tokens.hasMoreTokens()) {
                    cnt ++;
                    if(cnt <=1)
                        continue;

                    String new_tweet = str_tokens.nextToken().toLowerCase().trim().replaceAll("\\*","");

                    if(new_tweet.contains(key_word.toLowerCase().trim())) {
                        tweet_val.set(new_tweet);
                        context.write(ers_key,tweet_val);
                    }
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
