package test;

import java.beans.Transient;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import com.htht.utils.Utils;

import org.junit.Test;

/**
 * 海洋站能见度常规统计日产品
 *  输入：温湿压观测数据（T_OR_STD_MET_TPRV）
	输出：日产品（T_PR_STD_METE_VIS_DAY）
	note：数据库连接信息/表名/输出字段
 * @author edz
 *
 */
public class C_PR_STD_METE_VIS_DAY {
	
	
	public static void main(String[] args) {

		String url="10.1.7.5:1521:";
		String username="scott";
		String password="SCOTT";
		
		url ="jdbc:oracle:thin:@120.55.160.67:1521:helowin";
		username="test";
		password="123123";
		
//		IP='71.3.1.230:1521:';
//		USER='TJFX';
//		PASSWORD='TJFX';	
		String StationInfoTablename="T_ZD_STATION";
		String ITablename="T_OR_STD_MET_TPRV";
		String OTablename="T_PR_STD_METE_VIS_DAY";
		double QS=-9999;
		
		Connection conn = null;
		PreparedStatement pstm = null;
		PreparedStatement pstmQuery = null;
		try {
			conn = Utils.getConnection("oracle.jdbc.driver.OracleDriver", url, username, password);
			
		}catch (Exception e) {
			// TODO: handle exception
			System.err.println("数据库连接错误。。");
			e.printStackTrace();
		}
		String sql = "select distinct F_STDNAME, F_LAT,F_LON from "+ITablename;
		ResultSet pstmQuerySet = null;
		
		try {
			pstm = conn.prepareStatement(sql);
			pstmQuerySet = pstm.executeQuery();
			while(pstmQuerySet.next()) {
				System.out.println(pstmQuerySet.getString("F_STDNAME"));
				
				
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	@Test
	public void test01(){
		System.out.println("hello");
		Scanner sc =	new Scanner(System.in);
		System.out.println(sc.nextLine());
	}
}
