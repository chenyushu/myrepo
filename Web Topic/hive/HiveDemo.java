package com.htc.hadoop.HadoopTest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class HiveDemo {
    private static String HIVE_DRIVER_NAME = "org.apache.hive.jdbc.HiveDriver";
    private static String HOST = "master:10000";
    private static String ACCOUNT = "hive";
    private static String PASSWD = "hive";
    
    // the default URL
    private static String JDBC_URL = String.format("jdbc:hive2://%s/default", HOST);
    // private static String JDBC_URL = String.format("jdbc:hive2://%s/animal", HOST);
    
    public static void main(String[] args) throws SQLException {
        try {
            Class.forName(HIVE_DRIVER_NAME);
          } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.exit(1);
          }
        
          Connection con = DriverManager.getConnection(JDBC_URL, ACCOUNT, PASSWD);
          System.out.println("connected");
          
          Statement stmt = con.createStatement();
          String dbName = "animal";
          
          String tbName = "animal_type";
          stmt.execute(String.format("drop table if exists %s.%s", dbName, tbName));
          stmt.execute(String.format("drop table if exists %s.%s", "default", "testhivedrivertable"));

          
          stmt.execute(String.format("drop database if exists %s", dbName));
          stmt.execute(String.format("create database if not exists %s", dbName));
          
          stmt.execute(String.format("USE %s", dbName));
          
          // current insert SQL not support complex type
          // so we should define customized String if we want to complex
          
          String createTable = "create table animal_type (id BIGINT, animal STRING, type STRING, height FLOAT) "
                  // cureent insert not support complex type
//                  + "comment ARRAY<MAP<BIGINT, STRUCT<col1: STRING, col2: STRING>>>) "
                  + "PARTITIONED BY(year STRING, month STRING, day STRING) "
                  // if we want to support update and delete, need to add cluster by in n buckets
                  + "CLUSTERED BY (id) into 2 buckets "
                  + "ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t' ESCAPED BY '\' "
                  //current insert not support complex type
//                  + "COLLECTION ITEMS TERMINATED BY '|' "
//                  + "MAP KEYS TERMINATED BY ':' "
                  + "LINES TERMINATED BY '\n' "

                  // if we don't want to support update and delete add this line
                  // save as plain file
//                  + "STORED AS TextFile";
                  
                  // if we want to support update and delete need add this line
                  + "STORED AS ORC TBLPROPERTIES ('transactional'='true')";
          
          stmt.execute(createTable);
          
          // insert data
             
          String insert =  "INSERT INTO TABLE animal_type PARTITION (year, month, day) " +
                  "VALUES "
                  + "(1, 'mouse', 'A', 30.0, '2016', '01', '27'), "
                  + "(2, 'dragon', 'B', 180.235, '2016', '01', '27'), "
                  + "(3, 'ox', 'B', 200.25, '2015', '01', '26'), "
                  + "(4, 'tiger', null, 300.25, '2015', '12', '26'), "
                  + "(5, 'snake', 'B', -100, '2015', '12', '26'), "
                  + "(6, 'snake', 'B', 1000.235, '2015', '12', '26'), "
                  + "(7, 'snake\tsnake\thi\thello', 'B', 1000.235, '2015', '12', '26'), "
                  + "(8, 'snake\tsnake\thi\thello', 'B', 1000.235, '2018', '12', '26'), "
                  + "(9, 'snake\tsnake\thi\thello', 'B', 1000.235, '2018', '12', '27'), "
                  + "(10, 'snake\tsnake\thi\thello', 'B', 1000.235, '2018', '1', '27') ";
          
          stmt.execute(insert);

          // query
          
          String sql = "select * from animal_type where type = 'B'";
          ResultSet res = stmt.executeQuery(sql);
          while (res.next()) {
              System.out.println("Query");
              System.out.print(res.getLong(1));
              System.out.print('\t');
              System.out.print(res.getString(2));
              System.out.print('\t');
              System.out.print(res.getString(3));
              System.out.print('\t');
              System.out.print(res.getFloat(4));
              System.out.print('\t');
              System.out.print(res.getString(5));
              System.out.print('\t');
              System.out.print(res.getString(6));
              System.out.print('\t');
              System.out.println(res.getString(7));
          }
          
          // query count
          
          sql = "select count(*) from animal_type where type = 'B'";
          res = stmt.executeQuery(sql);
          if(res.next()) {
              System.out.println("Count");
              System.out.println(res.getInt(1));
          }
          
          // query groupby
          
          sql = "select animal, sum(height) from animal_type where type = 'B' group by animal";
          res = stmt.executeQuery(sql);
          while (res.next()) {
              System.out.println("Query Groupby");
              System.out.print(res.getString(1));
              System.out.print('\t');
              System.out.println(res.getFloat(2));
          }
          
          // show index
          
          String  index = "SHOW FORMATTED INDEX ON animal_type";
          res = stmt.executeQuery(index);
          if(res.next()) {
              System.out.println(res.getString(1));
              System.out.println(res.getString(2));
              System.out.println(res.getString(3));
              System.out.println(res.getString(4));
              System.out.println(res.getString(5));
              System.out.println(res.getString(6));
          }
          
          // drop  partition
          String dropPartition = "alter table  animal_type DROP PARTITION (year='2018', month='12')";
          stmt.execute(dropPartition);
          
          /*****************************************/
          /********* Delete and Update ************/
          
          // update
          // UPDATE tablename SET column = value [, column = value ...] [WHERE expression]
          
          sql = "update animal_type set animal = 'monkey', type = 'O' where height = 1000.235 and year = '2018'";
          stmt.execute(sql);
          
          sql = "select * from animal_type where type = 'O'";
          res = stmt.executeQuery(sql);
          while (res.next()) {
              System.out.println("Query");
              System.out.print(res.getLong(1));
              System.out.print('\t');
              System.out.print(res.getString(2));
              System.out.print('\t');
              System.out.print(res.getString(3));
              System.out.print('\t');
              System.out.print(res.getFloat(4));
              System.out.print('\t');
              System.out.print(res.getString(5));
              System.out.print('\t');
              System.out.print(res.getString(6));
              System.out.print('\t');
              System.out.println(res.getString(7));
          }
                    
          
          // delete
          // DELETE FROM tablename [WHERE expression]
          
          sql = "delete from animal_type where type = 'O'";
          stmt.execute(sql);
          sql = "select * from animal_type where type = 'O'";
          res = stmt.executeQuery(sql);
          while (res.next()) {
              System.out.println("Query");
              System.out.print(res.getLong(1));
              System.out.print('\t');
              System.out.print(res.getString(2));
              System.out.print('\t');
              System.out.print(res.getString(3));
              System.out.print('\t');
              System.out.print(res.getFloat(4));
              System.out.print('\t');
              System.out.print(res.getString(5));
              System.out.print('\t');
              System.out.print(res.getString(6));
              System.out.print('\t');
              System.out.println(res.getString(7));
          }

           
          stmt.close();
          con.close();      
    }
}
