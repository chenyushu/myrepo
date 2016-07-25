package com.htc.hadoop.HadoopTest;

import java.security.PrivilegedExceptionAction;
import java.util.UUID;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.UserGroupInformation;

public class HadoopTest {

    public static void main(String[] args) {
        try {
            UserGroupInformation ugi = UserGroupInformation
                    .createRemoteUser("hadoopuser");

            ugi.doAs(new PrivilegedExceptionAction<Void>() {

                public Void run() throws Exception {

                    Configuration conf = new Configuration();

                    conf.addResource(HadoopTest.class
                            .getResource("/core-site.xml"));
                    conf.addResource(HadoopTest.class
                            .getResource("/hdfs-site.xml"));
                    conf.set("hadoop.job.ugi", "hadoopuser");

                    FileSystem fs = FileSystem.get(conf);

                    Path path = new Path("/user/hadoopuser/warehouse/test2");

                    if (fs.exists(path)) {

                        System.out.println("file existed");

                    } else {
                        fs.createNewFile(path);
                        System.out.println("file created");
                    }

                    // very important feature
                    FSDataOutputStream fos = fs.append(path);

                    StringBuilder sb = new StringBuilder();
                    
                    for (int i = 0; i < 500000; i++) {
                        sb.setLength(0);
                        
                        for(int j = 0; j < 10; j++) {
                            String text = String
                                    .format("%s surName%s firstName%s home%s phone%s other%s test%s\n",
                                            Integer.toString(i), UUID.randomUUID().toString(), UUID
                                                    .randomUUID().toString(), UUID
                                                    .randomUUID().toString(), UUID
                                                    .randomUUID().toString(), UUID
                                                    .randomUUID().toString(), UUID
                                                    .randomUUID().toString());
                            sb.append(text);
                        }

                        fos.writeUTF(sb.toString());
                    }
                    fos.flush();
                    fos.close();

                    return null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
