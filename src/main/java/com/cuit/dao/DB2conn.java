package com.cuit.dao;

import net.sf.json.JSONObject;

import java.sql.*;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.zip.DataFormatException;

public class DB2conn {

    /**
     * 获取某列下不重复值
     * @param tableName 表名切记包含模式名
     *                  如 YWWATER.SK_WAT_HOUR_SUMDATA_
     * @param colName
     * @return
     * @throws Exception
     */

    public String  getDistinctValue(String tableName,String colName) throws  Exception{
        ArrayList<String> list=new ArrayList();
        Connection conn=null;
        PreparedStatement pst=null;
        ResultSet rs =null;
        Class.forName("com.ibm.db2.jcc.DB2Driver");
        //加载mysql驱动程序类
        String url = "jdbc:db2://localhost:50000/INTDB";//url为连接字符串
        String user = "db2admin";//数据库用户名
        String pwd = "Admin123456";//数据库密码
        String s="select    distinct    "+colName+"  from " +tableName;
        conn= DriverManager.getConnection(url,user,pwd);
        pst=conn.prepareStatement(s);
        rs = pst.executeQuery();
        while(rs.next()){
            list.add(rs.getString(1));
        }
        rs.close();//后定义，先关闭
        pst.close();
        conn.close();//先定义，后关闭
        return list.toString();



}

    /**
     *
     * @param tableName 表名   切记不要包含模式名
     *                  由于要到系统表查找该表的列名， 所以一定不要包含模式名
     * @return 列名 字符串如 [AREA_CODE_, AVG_VALUE_, DATE_]
     * @throws Exception
     */
    public String  getColName(String tableName) throws  Exception{
        ArrayList<String> list=new ArrayList();
        Connection conn=null;
        PreparedStatement pst=null;
        ResultSet rs =null;
        Class.forName("com.ibm.db2.jcc.DB2Driver");
        //加载mysql驱动程序类
        String url = "jdbc:db2://localhost:50000/INTDB";//url为连接字符串
        String user = "db2admin";//数据库用户名
        String pwd = "Admin123456";//数据库密码
        String s="SELECT colname from syscat.columns where tabname=?";
        conn= DriverManager.getConnection(url,user,pwd);
        pst=conn.prepareStatement(s);
        pst.setString(1,tableName);
        rs = pst.executeQuery();
        while(rs.next()){
            list.add(rs.getString(1));
        }
        rs.close();//后定义，先关闭
        pst.close();
        conn.close();//先定义，后关闭
        return list.toString();

    }


    /**
     * 获取某一个水检测站的某检测属性的数值
     * @param column 列名
     * @param table 表名  切记表名一定要包含模式名,否则报错
     * @param station 检测站
     * @param name 检测属性
     * @return
     * @throws Exception  当序列是非数值时，抛出DataFormatException异常。
     */
    public Queue<Double> getData(String table ,String column, String station,String name) throws DataFormatException {
        //加载mysql驱动程序类
        String url = "jdbc:db2://localhost:50000/INTDB";//url为连接字符串
        String user = "db2admin";//数据库用户名
        String pwd = "Admin123456";//数据库密码
        StringBuffer sb=new StringBuffer("SELECT  ");
        sb.append(column);
        sb.append("  from  ");
        sb.append(table);
        sb.append("  where  ");
        sb.append(" STATION_NAME_ ='");
        sb.append(station);
        sb.append("'  and  NAME_='");
        sb.append(name);
        sb.append("'");
        String sql=sb.toString();
        Connection conn= null;
        PreparedStatement stmt = null;
        LinkedBlockingQueue<Double> data = new LinkedBlockingQueue<>();
        ResultSet  rs= null;
        try {
            try {
                Class.forName("com.ibm.db2.jcc.DB2Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            conn = DriverManager.getConnection(url,user,pwd);
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while(rs.next()){ //当前记录指针移动到下一条记录上
                try{
                Double p =rs.getDouble(1);//columnIndex – the first column is 1, the second is 2, ...
                if(p!=null){
                        data.add(p);
                }
                }catch (SQLException e){
                    rs.close();//后定义，先关闭
                    stmt.close();
                    conn.close();//先定义，后关闭
                    throw new DataFormatException("数据格式不正确，请选择数值列");
                }

            }
            rs.close();//后定义，先关闭
            stmt.close();
            conn.close();//先定义，后关闭
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;

    }


}
