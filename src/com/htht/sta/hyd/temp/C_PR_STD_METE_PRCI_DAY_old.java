package com.htht.sta.hyd.temp;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.HashSet;
import java.util.Set;

import javax.swing.text.StyledEditorKit.ItalicAction;

import com.htht.utils.Utils;
import com.sun.org.apache.xml.internal.utils.StringVector;

/**
 * 海洋站降水常规统计日产品
 * @author jcx
 * 输入降水的观测数据(T_OR_STD_MET_TPRV)
 * 输出日产品(T_PR_STD_METE_PRCI_DAY)
 * 数据库连接信息/表名/输出字段
 *
 */


public class C_PR_STD_METE_PRCI_DAY_old {
	// 统计入库条数
	private static int q = 0;
	// 累积多少条入库
	private static int numCommit = 30000;
	// 无效值定义
	private static int QS = -9999;
	
	private static int arrCount;
	
	
	
	public static void main(String[] args) {
		Long startTime = System.currentTimeMillis();
//		String url = "jdbc:oracle:thin:@127.0.0.1:1521:orcl";
//		String username = "TJFX";
//		String password = "TJFX";
		
		// String url = "jdbc:oracle:thin:@120.55.160.67:1521:helowin";
		String url = "jdbc:oracle:thin:@192.168.99.100:1521:helowin";
		String username = "TJFX";
		String password = "123123";
		
		
		String ITablename="T_OR_STD_MET_TPRV";
		String OTablename="T_PR_STD_METE_PRCI_DAY";
		
		Connection conn = null;
		PreparedStatement pstm = null;
		PreparedStatement pstmQuery = null;
		
		
		try {
			conn = Utils.getConnection("oracle.jdbc.driver.OracleDriver", url,
					username, password);
			conn.setAutoCommit(false);
		}catch (Exception e) {
			// TODO: handle exception
			System.err.println("数据库连接错误。。");
			e.printStackTrace();
		}
		String sql_code_lat_lon = "select distinct F_STDNAME, F_LAT,F_LON from "+"TJFX."+ITablename;
		
		String code = null;
		double lon = 0.0;
		double lat = 0.0;
		Date time = null;
		double prcicode = 0.0;
		double prci = 0.0;
		double prci_total,prciavg,prcimax,prcimin;
		String prcimaxtime,prcimintime;
		
		String insertSQL = "insert into TJFX.T_PR_STD_METE_PRCI_DAY values(null,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement insertPS = null;
		try {//
			//站点查询
			

			
			
			insertPS = conn.prepareStatement(insertSQL);
				q++;
				
				//"select * from TJFX.T_OR_STD_MET_TPRV where F_STDNAME= "
				
				String str_days="select to_char(F_OBSDATE,'YYYY-MM-DD')  from TJFX.T_OR_STD_MET_TPRV group by to_char(F_OBSDATE, "+"'YYYY-MM-DD')";
				
				PreparedStatement days_stat = conn.prepareStatement(str_days);
				ResultSet days_resultSet = days_stat.executeQuery();
				
					
					String strSQLdata = "select * from TJFX.T_OR_STD_MET_TPRV where F_OBSDATE between to_date(?,'YYYY-MM-DD HH24') and to_date(?,'YYYY-MM-DD HH24') order by F_OBSDATE";
			
					PreparedStatement dayDataStat =  conn.prepareStatement(strSQLdata);
					
					
					
						while(days_resultSet.next()){
							
							dayDataStat.setString(1, days_resultSet.getString(1)+" 00");
							dayDataStat.setString(2, days_resultSet.getString(1)+" 23");
							ResultSet dayDataSet =  dayDataStat.executeQuery();
							dayDataSet.setFetchSize(1000);
							HashSet set = new HashSet<String>();
								while(dayDataSet.next()) {
									
								code = dayDataSet.getString(2);
								lat = dayDataSet.getDouble(3);
								lon = dayDataSet.getDouble(4);
//								System.out.println(dayDataSet.getString(1)+"   ");
//								System.out.println(code+"     "+lat+"     "+lon+"     ");
//								
//								System.out.println("执行插入");
								time = dayDataSet.getDate("F_OBSDATE");
								prcicode = dayDataSet.getDouble("F_PRCIPCODE");
								prci = dayDataSet.getDouble("F_DPRCI_TOTAL");
								if(prci>9000 || prci<0) {
									prci_total=prciavg=prcimax=prcimin=QS;
									prcimaxtime=prcimintime=String.valueOf(QS);
								}else {
									prci_total=prciavg=prcimax=prcimin=prci;
									prcimaxtime=prcimintime=String.valueOf(time);
								}
								Date processtime = new Date(System.currentTimeMillis());
								
								Date date = days_resultSet.getDate(1);
								
								StringBuffer strBuffer = new StringBuffer();
								
								strBuffer.append(code.toString());
								strBuffer.append(String.valueOf(lat));
								strBuffer.append(String.valueOf(lon));
								strBuffer.append(String.valueOf(date));
								strBuffer.append(String.valueOf(prci_total));
								strBuffer.append(String.valueOf(prciavg));
								strBuffer.append(String.valueOf(prcimax));
								strBuffer.append(String.valueOf(prcimaxtime));
								strBuffer.append(String.valueOf(prcimin));
								strBuffer.append(String.valueOf(prcimintime));
								strBuffer.append(String.valueOf(prcicode));
								strBuffer.append(String.valueOf(time));
								
								if(set.contains(strBuffer.toString())) {
									continue;
								}else {
								set.add(strBuffer.toString());
								insertPS.setString(1, code);
								insertPS.setDouble(2, lat );
								insertPS.setDouble(3, lon);
								insertPS.setDate(4, date);
								insertPS.setDouble(5, prci_total);
								insertPS.setDouble(6, prciavg);
								insertPS.setDouble(7, prcimax);
								insertPS.setString(8, prcimaxtime);
								insertPS.setDouble(9, prcimin);
								insertPS.setString(10, prcimintime);
								insertPS.setDouble(11, prcicode);
								insertPS.setDate(12, time);
								insertPS.addBatch();
								
									if (q % numCommit == 0) {
										insertPS.executeBatch();
										conn.commit();
										insertPS.clearBatch();
									}
								}
								
							
							
							
						
							
						}
						
						insertPS.executeBatch();
						conn.commit();
						insertPS.clearBatch();
				
						set.clear();
				
				}
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
		
		
		long endTime = System.currentTimeMillis();
		
		System.out.println("所用时间:"+ new Time(endTime-startTime));
		
		
		
		
		
		
	}
	
	
}
