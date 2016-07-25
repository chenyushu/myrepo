package com.htc.hadoop.HadoopTest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class HBaseDemo {

    public static void main(String[] args) throws SQLException {
        Connection con = null;
        Statement stmt = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        
        try {
            con = DriverManager.getConnection("jdbc:phoenix:master,slave1,deeptheming.htcsense.com.local");
            System.out.println("connected");
            stmt = con.createStatement();
            
            // delete table 
            String dropTable = "DROP TABLE IF EXISTS myschema.phoenixDemo";
           
            stmt.execute(dropTable);
            System.out.println("delete table");
            
            // then create table
            // SALT bucket for regionserver load balancing, because current have 3 regionserver user 3 
            String createTable = "CREATE TABLE IF NOT EXISTS myschema.phoenixDemo " +
                    "( id char(10) not null primary key, date DATE , seq BIGINT, math DOUBLE, comment VARCHAR) SALT_BUCKETS = 3, COMPRESSION='LZO'";
            
            stmt.execute(createTable);
            System.out.println("create table");
            
            
            // insert and update
            String upsert = String.format("upsert into myschema.phoenixDemo values ('a', CURRENT_DATE(), 123456, 3.14156, 'hello phoenix')");
            stmt.executeUpdate(upsert);
            
            // update
            upsert = String.format("upsert into myschema.phoenixDemo values ('a', CURRENT_DATE(), 456789, 888.888, 'hello phoenix fly')");
            stmt.executeUpdate(upsert);
            
            // insert another
            upsert = String.format("upsert into myschema.phoenixDemo values ('b', CURRENT_DATE(), 777777, 777.777, 'hello phoenix demo1')");
            stmt.executeUpdate(upsert);
            
            upsert = String.format("upsert into myschema.phoenixDemo values ('c', CURRENT_DATE(), 6666, 666.2222, 'hello phoenix demo2')");
            stmt.executeUpdate(upsert);
            
            upsert = String.format("upsert into myschema.phoenixDemo values ('d', CURRENT_DATE(), 55555, 555.1111, 'hello phoenix demo3')");
            stmt.executeUpdate(upsert);
            
            System.out.println("insert");
            
            // very very important
            // current version not support auto commit
            con.commit();
            
            // query
            pstmt = con.prepareStatement("select * from myschema.phoenixDemo");
            rset = pstmt.executeQuery();
            while (rset.next()) {
                System.out.print(rset.getString(1));
                System.out.print("\t");
                System.out.print(rset.getDate(2).toString());
                System.out.print("\t");
                System.out.print(rset.getBigDecimal(3));
                System.out.print("\t");
                System.out.print(rset.getDouble(4));
                System.out.print("\t");
                System.out.println(rset.getString(5));
            }
            
            rset.close();
            pstmt.close();
            
            
            // query where
            pstmt = con.prepareStatement("select * from myschema.phoenixDemo where math < ?");
            pstmt.setDouble(1, 777.777);
            rset = pstmt.executeQuery();
            while (rset.next()) {
                System.out.print(rset.getString(1));
                System.out.print("\t");
                System.out.print(rset.getDate(2).toString());
                System.out.print("\t");
                System.out.print(rset.getBigDecimal(3));
                System.out.print("\t");
                System.out.print(rset.getDouble(4));
                System.out.print("\t");
                System.out.println(rset.getString(5));
            }
            
            rset.close();
            pstmt.close();
            
            String delete = "DELETE FROM myschema.phoenixDemo WHERE comment LIKE '%demo%'";
            stmt.executeUpdate(delete);
            
            
            con.commit();
            System.out.println("delete");

            
            // query
            pstmt = con.prepareStatement("select * from myschema.phoenixDemo");
            rset = pstmt.executeQuery();
            while (rset.next()) {
                System.out.print(rset.getString(1));
                System.out.print("\t");
                System.out.print(rset.getDate(2).toString());
                System.out.print("\t");
                System.out.print(rset.getBigDecimal(3));
                System.out.print("\t");
                System.out.print(rset.getDouble(4));
                System.out.print("\t");
                System.out.println(rset.getString(5));
            }
            
            rset.close();
            pstmt.close();     
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if(stmt != null) {
                stmt.close();
            }
            
            if(pstmt != null) {
                pstmt.close();
            }
            
            if(rset != null) {
                rset.close();
            }
            
            if (con != null) {
                con.close();
            }
            
        }

    }
    
    @SuppressWarnings("unused")
    private static String getCurrentDate() {
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();             
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return format.format(date);  
    }

}
