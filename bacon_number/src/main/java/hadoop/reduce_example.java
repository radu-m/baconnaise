package hadoop;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.RandomAccess;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * Created by miul on 12/13/2014.
 */
public class reduce_example extends Reducer<Text,Text,Text,Text> implements RandomAccess{
    Path pos_file_path;
    Path neg_file_path;
    Path out_file_path;
    Path kword_file_path;

    BufferedReader pos_buff_reader;
    BufferedReader neg_buff_reader;
    BufferedReader key_buff_reader;

    static Double tot_rec=new Double("0");

    static Double neg_cnt=new Double("0");
    static Double pos_cnt=new Double("0");
    static Double neu_cnt=new Double("0");

    static Double neg_percent=new Double("0");
    static Double pos_percent=new Double("0");
    static Double neu_percent=new Double("0");

    Pattern pattrn;
    Matcher matcher_one;
    static int row_new=0;
    FSDataOutputStream out_one,out_two;

    public void reduce(Text key, Iterable<Text> values,Context context) throws IOException, InterruptedException
    {
        Configuration conf_one = new Configuration();
        conf_one.addResource(new Path("/usr/etc/hadoop/core-site.xml"));
        conf_one.addResource(new Path("/usr/etc/hadoop/hdfs-site.xml"));


        kword_file_path=new Path("/usr/inputs/workspace/SentimentAnalysis_Twitter/files/keywords.txt");
        FileSystem fskeyread = FileSystem.get(URI.create("files/keywords.txt"),new Configuration());
        key_buff_reader=new BufferedReader(new InputStreamReader(fskeyread.open(kword_file_path)));

        String keywrd = "";
        while(key_buff_reader.ready())
        {
            keywrd=key_buff_reader.readLine().trim();

        }

        String check_one=keywrd;

        FileSystem file_sys = FileSystem.get(conf_one);
        FileSystem fileSys_PosNeg = FileSystem.get(conf_one);

        Path path_one = new Path("/user/sentimentoutput.txt");
        Path path_Pos_Neg = new Path("/user/posnegetiveoutput.txt");

        if (!file_sys.exists(path_one)) {
            out_one = file_sys.create(path_one);
            out_two = fileSys_PosNeg.create(path_Pos_Neg);

        }

        if(check_one.equals(key.toString().toLowerCase()))
        {
            for(Text twit_new:values)
            {
                // Load positive dictionary file
                pos_file_path=new Path("/usr/inputs/workspace/Sent_Analysis_Twitter/files/positive-words.txt");
                FileSystem fs_one = FileSystem.get(URI.create("files/positive-words.txt"),new Configuration());
                pos_buff_reader=new BufferedReader(new InputStreamReader(fs_one.open(pos_file_path)));

                // Load negative dictionary file
                neg_file_path = new Path("/usr/inputs/workspace/Sent_Analysis_Twitter/files/negative-words.txt");
                FileSystem fs_two = FileSystem.get(URI.create("files/negative-words.txt"),new Configuration());
                neg_buff_reader =new BufferedReader(new InputStreamReader(fs_two.open(neg_file_path)));

                ++tot_rec;

                boolean flag_one=false;
                boolean flag_two=false;

                String mytwit_all=twit_new.toString();
                String regex_one = "";
                String regex_two = "";

                while(pos_buff_reader.ready())
                {
                    regex_one=pos_buff_reader.readLine().trim();
                    row_new++;
                    pattrn = Pattern.compile(regex_one, Pattern.CASE_INSENSITIVE);
                    matcher_one = pattrn.matcher(mytwit_all);
                    flag_one=matcher_one.find();

                    if(flag_one)
                    {
                        out_two.writeBytes(mytwit_all);
                        context.write(new Text(regex_one),new Text(mytwit_all));
                        break;
                    }
                }
                while(neg_buff_reader.ready())
                {
                    row_new++;
                    regex_two=neg_buff_reader.readLine().trim();
                    pattrn = Pattern.compile(regex_two, Pattern.CASE_INSENSITIVE);
                    matcher_one = pattrn.matcher(mytwit_all);
                    flag_two=matcher_one.find();
                    if(flag_two)
                    {
                        out_two.writeBytes(mytwit_all);
                        context.write(new Text(regex_two),new Text(mytwit_all));
                        break;
                    }

                }
                if(flag_one&flag_two)
                {
                    ++neu_cnt;
                }
                else
                {
                    if(flag_one)
                    {
                        ++pos_cnt;
                    }
                    if(flag_two)
                    {
                        ++neg_cnt;
                    }
                    if(flag_one==false&flag_two==false)
                    {
                        ++neu_cnt;
                    }
                }
                neg_buff_reader.close();
                pos_buff_reader.close();

            }//for
            pos_percent=pos_cnt/tot_rec*100;
            neg_percent=neg_cnt/tot_rec*100;
            neu_percent=neu_cnt/tot_rec*100;

            try{
                out_one.writeBytes("\n"+keywrd);
                out_one.writeBytes(","+tot_rec);
                out_one.writeBytes(","+neg_percent);
                out_one.writeBytes(","+pos_percent);
                out_one.writeBytes(","+neu_percent);

                out_one.close();
                file_sys.close();
            }catch(Exception e){
                e.printStackTrace();
            }

        }
    }
}
