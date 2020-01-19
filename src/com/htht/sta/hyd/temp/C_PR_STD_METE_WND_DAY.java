package com.htht.sta.hyd.temp;

import java.awt.Insets;
import java.awt.image.DataBufferDouble;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.experimental.max.MaxCore;

import com.htht.utils.Utils;
import com.sun.javafx.binding.StringFormatter;
import com.sun.org.apache.bcel.internal.generic.NEW;

import sun.java2d.ScreenUpdateManager;

/**
 *	海洋站风常规统计日产品
 *	输入：风观测数据（T_OR_STD_MET_WIND）
 *	输出：日产品（T_PR_STD_METE_PRCI_DAY）
 *	数据库连接信息/表名/输出字段
 * @author edz
 *
 */
public class C_PR_STD_METE_WND_DAY {

	
	String StationInfoTablename="T_ZD_STATION";
	String ITablename="T_OR_STD_MET_WIND";
	String OTablename="T_PR_STD_METE_WND_DAY";
	
	private static int q = 0;
	
	private static int numCommit = 30000;
	
	private static int QS = -9999;
	private static int arrCount;
	
	public static void main(String[] args) {
		Long startTime = System.currentTimeMillis();
//		String url = "jdbc:oracle:thin:@120.55.160.67:1521:orcl";
		String url = "jdbc:oracle:thin:@localhost:1521:helowin";
		String username = "TJFX";
		String password = "TJFX";
	
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		String code = null;
		Double lat,lon;
		Date daytime;
		
		String windcode;
		Integer[] WD = new Integer[24]; 
		Double[] WS = new Double[24];
		Integer[] WSQ = new Integer[24]; 
		Double WSMAX1,WSMAX2;
		Integer WDMAX1,WDMAX2,WSMAXQ1,WSMAXQ2;
		String WSMAXTIME1,WSMAXTIME2;
		Double[] WS_U = new Double[24],WS_V = new Double[24];
		String[] WSTIME = new String[24];
		String time;
		Double WSAVG;
		Double[] WS_ALL;
		Integer[] WD_ALL,WSQ_ALL;
		String[] WSTIME_ALL;
		Double WSMAXMAX;
		Integer WDMAXMAX;
		String MAXMAXTIME;
		
		Double WSMAX,WSMIN;
		Integer WDMAX,WDMIN;
		String MAXTIME,MINTIME;
		//%UV的平均值
		Double UAVG,VAVG;
		//%U极值
		Double UMAX,UMIN;
		String UMAXTIME,UMINTIME;
		//%V极值
		Double VMAX,VMIN;
		String VMAXTIME,VMINTIME;
		
		PreparedStatement insertps = null;
		try {
			conn = Utils.getConnection("oracle.jdbc.driver.OracleDriver", url, username, password);
			String selectsql = "select * from T_OR_STD_MET_WIND WHERE F_ID=563a9eb7015243b39f9c2fbe5949cc13" ;
			String demo = "select * from T_OR_STD_MET_WIND WHERE F_ID='563a9eb7015243b39f9c2fbe5949cc13'";
			String insertsql = "insert into T_PR_STD_METE_WND_DAY_SYN(f_id,f_stdname ,f_lat ,f_lon,f_obsdate,f_windcode,f_processtime,f_wsavg,f_wsmax,f_wdmax,f_maxtime,f_wsmax2,f_wdmax2,f_maxtime2,f_wsmin,f_wdmin,f_mintime,f_uavg,f_vavg,F_N1F,F_N2F,F_N3F,F_N4F,F_N5F,F_NE1F,F_NE2F,F_NE3F,F_NE4F,F_NE5F,F_E1F,F_E2F,F_E3F,F_E4F,F_E5F,F_SE1F,F_SE2F,F_SE3F,F_SE4F,F_SE5F,F_S1F,F_S2F,F_S3F,F_S4F,F_S5F,F_SW1F,F_SW2F,F_SW3F,F_SW4F,F_SW5F,F_W1F,F_W2F,F_W3F,F_W4F,F_W5F,  F_NW1F,F_NW2F,F_NW3F,F_NW4F,F_NW5F,F_N1AVG,F_N2AVG,F_N3AVG,F_N4AVG,F_N5AVG, F_NE1AVG, F_NE2AVG,F_NE3AVG,F_NE4AVG,F_NE5AVG,F_E1AVG,F_E2AVG,F_E3AVG,F_E4AVG,F_E5AVG ,F_SE1AVG,F_SE2AVG,F_SE3AVG,F_SE4AVG,F_SE5AVG,F_S1AVG,F_S2AVG,F_S3AVG,F_S4AVG,F_S5AVG, F_SW1AVG,F_SW2AVG,F_SW3AVG,F_SW4AVG,F_SW5AVG,F_W1AVG,F_W2AVG,F_W3AVG,F_W4AVG,F_W5AVG,F_NW1AVG,F_NW2AVG,F_NW3AVG,F_NW4AVG,F_NW5AVG,F_NMAX,F_NEMAX,F_EMAX,F_SEMAX,F_SMAX,F_SWMAX,F_WMAX,F_NWMAX,F_WNDNUM1,F_WNDNUM2,F_WNDNUM3,F_WNDNUM4,F_WNDNUM5,F_WNDNUM6,F_WNDNUM8, F_UMAX,F_UMAXTIME,F_UMIN,F_UMINTIME,F_VMAX,F_VMAXTIME,F_VMIN,F_VMINTIME, F_NAVG,F_NEAVG,F_EAVG,F_SEAVG,F_SAVG,F_SWAVG,F_WAVG,F_NWAVG)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";
			ps = conn.prepareStatement(demo);
			rs = ps.executeQuery();
			while(rs.next()) {
				q++;
				code = rs.getString("F_STDNAME");
				lat = rs.getDouble("F_LAT");
				lon = rs.getDouble("F_LON");
				daytime = rs.getDate("F_OBSDATE");
				windcode = rs.getString("F_WINDCODE");
				for(int i = 0 ;i < WD.length ; i++) {
					WD[i] = rs.getInt("F_WD"+String.format("%02d", i));
					WS[i] = rs.getDouble("F_WS"+String.format("%02d", i));
					WSQ[i] = rs.getInt("F_WSQ"+String.format("%02d", i));
					
					
				}
				WSMAX1 = rs.getDouble("F_WSMAX1");
				WSMAX2 = rs.getDouble("F_WSMAX2");
				WDMAX1 = rs.getInt("F_WDMAX1");
				WDMAX2 = rs.getInt("F_WDMAX2");
				WSMAXQ1 = rs.getInt("F_WSMAXQ1");
				WSMAXQ2 = rs.getInt("F_WSMAXQ2");
				WSMAXTIME1 = String.valueOf(rs.getInt("F_WSMAXTIME1"));
				WSMAXTIME2 = String.valueOf(rs.getInt("F_WSMAXTIME2"));
				time = String.valueOf(daytime).substring(0,10);
				for (int i = 0; i < WS_V.length; i++) {
					WSTIME[i] = time+" "+String.format("%02d", i);
					WS_U[i] = -WS[i]*Math.sin(WD[i]*Math.PI/180);
					WS_V[i] = -WS[i]*Math.cos(WD[i]*Math.PI/180);
				}
				Double[] WSMAX12 = {WSMAX1,WSMAX2};
				Integer[] WDMAX12 = {WDMAX1,WDMAX2};
				Integer[] WSMAXQ12 = {WSMAXQ1,WSMAXQ2};
				String[] WSMAXTIME12= {time+" "+WSMAXTIME1,time+" "+WSMAXTIME2};
				
				for(int i=0;i<WSQ.length;i++) {
					if(WS[i]==QS|WS[i]>90)
						WSQ[i]=1;
				}
				for(int i=0;i<WSMAXQ12.length;i++) {
					if(WSMAX12[i]>90|WSMAX12[i]==QS)
						WSMAXQ12[i]=1;
				}
				int invalid=4;
				
				Integer num_invalid_WS = JFMUtils.SUM(WSQ);
				
				
				if(num_invalid_WS>invalid)
					WSAVG = (double) QS;
				else {
					List list = new ArrayList<Integer>();
					for(int i=0;i<WSQ.length;i++) {
						if(WSQ[i]==0)
							list.add(WS[i]);
						
					}
					WSAVG = JFMUtils.MEAN(list.toArray());
				}
				
				
				WS_ALL = Utils.addALL(WS, WSMAX12);
				WD_ALL = Utils.addALL(WD, WDMAX12);
				WSQ_ALL = Utils.addALL(WSQ, WSMAXQ12);
				
				WSTIME_ALL =Utils.addALL(WSTIME, WSMAXTIME12);
				WSMAXMAX = Utils.MAX(Utils.getEqualsArray(WSQ_ALL, WS_ALL, 0));
				List l = Utils.getEqualsArray(WS_ALL, WD_ALL, WSMAXMAX);
				WDMAXMAX = (Integer)l.get(l.size()-1);
				List MAXMAXTIME_T = Utils.getEqualsArray(WS_ALL, WSTIME_ALL, WSMAXMAX);

				MAXMAXTIME = Utils.extremtime(MAXMAXTIME_T);
				//风速极大值（24小时）
				
				WSMAX = Utils.MAX(Utils.getEqualsArray(WSQ, WS, 0));
				l= Utils.getEqualsArray(WS, WD, WSMAX);
				System.out.println("l是："+Arrays.toString(l.toArray()));
				WDMAX = (Integer) l.get(l.size()-1);
				MAXTIME = Utils.extremtime(Utils.getEqualsArray(WS, WSTIME, WSMAX));
				//风速极小值（24小时）
				System.out.println("WSQ:"+Arrays.toString(WSQ));
				System.out.println("WS: "+Arrays.toString(WS));
				System.out.println("   WD:  "+Arrays.toString(WD));
				
				WSMIN = Utils.MIN(Utils.getEqualsZeroArrayAndBigZero(WSQ, WS));
				System.out.println("   WSMIN: "+WSMIN);
				l = Utils.getEqualsArray(WS, WD, WSMIN);
				System.out.println("l是："+Arrays.toString(l.toArray()));
				WDMIN = (Integer) l.get(l.size()-1);
				MINTIME = Utils.extremtime(Utils.getEqualsArray(WS, WSTIME, WSMIN));
				//UV的平均值
				UAVG = Utils.MEAN(Utils.getEqualsArray(WSQ, WS_U, 0));
				VAVG = Utils.MEAN(Utils.getEqualsArray(WSQ, WS_V, 0));
				//U极值
				
				UMAX = Utils.MAX(Utils.getEqualsArray(WSQ, WS_U, 0));
				UMAXTIME =Utils.extremtime(Utils.getEqualsArray(WS_U, WSTIME, UMAX)); 
				UMIN = Utils.MIN(Utils.getEqualsArray(WSQ, WS_U, 0));
				UMINTIME = Utils.extremtime(Utils.getEqualsArray(WS_U, WSTIME, UMIN));
				
				//V极值
				
				VMAX = Utils.MAX(Utils.getEqualsArray(WSQ, WS_V, 0));
				VMAXTIME =Utils.extremtime(Utils.getEqualsArray(WS_V, WSTIME, VMAX)); 
				VMIN = Utils.MIN(Utils.getEqualsArray(WSQ, WS_V, 0));
				VMINTIME = Utils.extremtime(Utils.getEqualsArray(WS_V, WSTIME, VMIN));
				//
				
				int F_NUMS = WS.length;
				
//				int ind_N=(WD>=337.5&WD<=360)|(WD>=0&WD<22.5);
//				int ind_NE=WD>=22.5&WD<67.5;
//				int ind_E=WD>=67.5&WD<112.5;
//				int ind_SE=WD>=112.5&WD<157.5;
//				int ind_S=WD>=157.5&WD<202.5;
//				int ind_SW=WD>=202.5&WD<247.5;
//				int ind_W=WD>=247.5&WD<292.5;
//				int ind_NW=WD>=292.5&WD<337.5;
					
				int ind_N=Integer.parseInt(getDoubleNum(WD, 337.5, 360),2) |Integer.parseInt(getDoubleNum(WD, 0, 22.5),2);
				int ind_NE=Integer.parseInt(getDoubleNum(WD, 22.5, 67.5),2);
				int ind_E=Integer.parseInt(getDoubleNum(WD, 67.5, 112.5),2);
				int ind_SE=Integer.parseInt(getDoubleNum(WD, 112.5, 157.5),2);
				int ind_S=Integer.parseInt(getDoubleNum(WD, 157.5, 202.5),2);
				int ind_SW=Integer.parseInt(getDoubleNum(WD, 202.5, 247.5),2);
				int ind_W=Integer.parseInt(getDoubleNum(WD, 247.5, 292.5),2);
				int ind_NW=Integer.parseInt(getDoubleNum(WD, 292.5, 337.5),2);
				
				int ind_W1 = Integer.parseInt(getDoubleNum(WS, 1.6, QS),2);
				int ind_W2 = Integer.parseInt(getDoubleNum(WS, 1.6, 5.5),2);
				int ind_W3 = Integer.parseInt(getDoubleNum(WS, 5.5, 10.8),2);
				int ind_W4 = Integer.parseInt(getDoubleNum(WS, 10.8, 17.2),2);
				int ind_W5 = Integer.parseInt(getDoubleNum(WS, 17.2, QS),2);
				//%g各级各向频率
				
				int[] ind_Ws = {ind_W1,ind_W2,ind_W3,ind_W4,ind_W5};
				
				double F_NF[] = new double[5];
				for (int i = 0; i < F_NF.length; i++) {
					F_NF[i] = findAndLength(ind_Ws[i]|ind_N)/F_NUMS*100;
				}
				
				double F_NEF[] = new double[5];
				for (int i = 0; i < F_NEF.length; i++) {
					F_NEF[i] = findAndLength(ind_Ws[i]|ind_NE)/F_NUMS*100;
				}
				
				double F_EF[] = new double[5];
				for (int i = 0; i < F_NF.length; i++) {
					F_EF[i] = findAndLength(ind_Ws[i]|ind_E)/F_NUMS*100;
				}
				
				double F_SEF[] = new double[5];
				for (int i = 0; i < F_NF.length; i++) {
					F_SEF[i] = findAndLength(ind_Ws[i]|ind_SE)/F_NUMS*100;
				}
			
				double F_SF[] = new double[5];
				for (int i = 0; i < F_NF.length; i++) {
					F_SF[i] = findAndLength(ind_Ws[i]|ind_S)/F_NUMS*100;
				}
				
				double F_SWF[] = new double[5];
				for (int i = 0; i < F_NF.length; i++) {
					F_SWF[i] = findAndLength(ind_Ws[i]|ind_SW)/F_NUMS*100;
				}
				
				double F_WF[] = new double[5];
				for (int i = 0; i < F_NF.length; i++) {
					F_WF[i] = findAndLength(ind_Ws[i]|ind_W)/F_NUMS*100;
				}
				
				double F_NWF[] = new double[5];
				for (int i = 0; i < F_NF.length; i++) {
					F_NWF[i] = findAndLength(ind_Ws[i]|ind_NW)/F_NUMS*100;
				}
			
				//各级各向平均风速
				double[] F_NAVGs = new double[5];
				for(int i=0;i<ind_Ws.length;i++) {
					F_NAVGs[i] = Utils.MEAN(getEqualsIndexList(WS, ind_Ws[i]|ind_N));
				}
				
				double[] F_NEAVGs = new double[5];
				for(int i=0;i<ind_Ws.length;i++) {
					F_NEAVGs[i] = Utils.MEAN(getEqualsIndexList(WS, ind_Ws[i]|ind_NE));
				}
				
				double[] F_EAVGs = new double[5];
				for(int i=0;i<ind_Ws.length;i++) {
					List ls= getEqualsIndexList(WS, ind_Ws[i]|ind_E);
					System.out.println("******************:"+Arrays.toString(ls.toArray()));
					System.out.println(ind_Ws[i]);
					System.out.println(ind_E);
					F_EAVGs[i] = Utils.MEAN(ls);
				}
				
				double[] F_SEAVGs = new double[5];
				for(int i=0;i<ind_Ws.length;i++) {
					F_SEAVGs[i] = Utils.MEAN(getEqualsIndexList(WS, ind_Ws[i]|ind_SE));
				}
				
				double[] F_SAVGs = new double[5];
				for(int i=0;i<ind_Ws.length;i++) {
					F_SAVGs[i] = Utils.MEAN(getEqualsIndexList(WS, ind_Ws[i]|ind_S));
				}
				
				double[] F_SWAVGs = new double[5];
				for(int i=0;i<ind_Ws.length;i++) {
					F_SWAVGs[i] = Utils.MEAN(getEqualsIndexList(WS, ind_Ws[i]|ind_SW));
				}
				
				double[] F_WAVGs = new double[5];
				for(int i=0;i<ind_Ws.length;i++) {
					F_WAVGs[i] = Utils.MEAN(getEqualsIndexList(WS, ind_Ws[i]|ind_W));
				}
				
				double[] F_NWAVGs = new double[5];
				for(int i=0;i<ind_Ws.length;i++) {
					F_NWAVGs[i] = Utils.MEAN(getEqualsIndexList(WS, ind_Ws[i]|ind_NW));
				}
				
				
				//方向最大风速
				double F_NMAX,F_NEMAX,F_EMAX,F_SEMAX,F_SMAX,F_SWMAX,F_WMAX,F_NWMAX;
				double F_NAVG,F_NEAVG,F_EAVG,F_SEAVG,F_SAVG,F_SWAVG,F_WAVG,F_NWAVG;
				 
				F_NMAX=Utils.MAX(getEqualsIndexList(WS, ind_N));
		        F_NEMAX=Utils.MAX(getEqualsIndexList(WS, ind_NE));
		        F_EMAX=Utils.MAX(getEqualsIndexList(WS, ind_E));
		        F_SEMAX=Utils.MAX(getEqualsIndexList(WS, ind_SE));
		        F_SMAX=Utils.MAX(getEqualsIndexList(WS, ind_S));
		        F_SWMAX=Utils.MAX(getEqualsIndexList(WS, ind_SW));
		        F_WMAX=Utils.MAX(getEqualsIndexList(WS, ind_W));
		        F_NWMAX=Utils.MAX(getEqualsIndexList(WS, ind_NW));
				//各方向平均风速
		        F_NAVG=Utils.MEAN(getEqualsIndexList(WS, ind_N));
		        F_NEAVG=Utils.MEAN(getEqualsIndexList(WS, ind_NE));
		        F_EAVG=Utils.MEAN(getEqualsIndexList(WS, ind_E));
		        F_SEAVG=Utils.MEAN(getEqualsIndexList(WS, ind_SE));
		        F_SAVG=Utils.MEAN(getEqualsIndexList(WS, ind_S));
		        F_SWAVG=Utils.MEAN(getEqualsIndexList(WS, ind_SW));
		        F_WAVG=Utils.MEAN(getEqualsIndexList(WS, ind_W));
		        F_NWAVG=Utils.MEAN(getEqualsIndexList(WS, ind_NW));
				
		        int F_WNDNUM[]=new int[7];
				if(WSMAXMAX>=5&&WSMAXMAX<10) 
					F_WNDNUM[0]=1;
				else if(WSMAXMAX>=10&&WSMAXMAX<12)
					F_WNDNUM[1]=1;
				else if(WSMAXMAX>=12&&WSMAXMAX<15)
					F_WNDNUM[2]=1;
				else if(WSMAXMAX>=15&&WSMAXMAX<17)
					F_WNDNUM[3]=1;
				else if(WSMAXMAX>=10&&WSMAXMAX<12)
					F_WNDNUM[4]=1;
				else if(WSMAXMAX>=10.8&&WSMAXMAX<17.2)
					F_WNDNUM[5]=1;
				else if(WSMAXMAX>=17.2)
					F_WNDNUM[6]=1;
				
				Date date = new Date(System.currentTimeMillis());
				conn.setAutoCommit(false);
				insertps = conn.prepareStatement(insertsql);
				
		        DecimalFormat df = new DecimalFormat("#.00");
				System.out.println(Arrays.toString(F_EAVGs));
		        
		        
				insertps.setString(1, String.valueOf(RandomStringUtils.random(10)));
				insertps.setString(2, code);
				insertps.setDouble(3, Double.parseDouble(df.format(lat)));
				insertps.setDouble(4, Double.parseDouble(df.format(lon)));
				insertps.setDate(5, daytime);
				insertps.setString(6, windcode);
				insertps.setDate(7, date);
				insertps.setDouble(8, Double.parseDouble(df.format(WSAVG)));
				insertps.setDouble(9, Double.parseDouble(df.format(WSMAXMAX)));
				insertps.setInt(10, WDMAXMAX);
				insertps.setString(11 ,MAXMAXTIME);
				insertps.setDouble(12 , Double.parseDouble(df.format(WSMAX)));
				insertps.setInt(13, WDMAX);
				insertps.setString(14, MAXTIME);
				insertps.setDouble(15 , Double.parseDouble(df.format(WSMIN)));
				insertps.setInt(16, WDMIN);
				insertps.setString(17, MINTIME);
				insertps.setDouble(18 ,	Double.parseDouble(df.format(UAVG)));
				insertps.setDouble(19, Double.parseDouble(df.format(VAVG)));
				insertps.setDouble(20 , Double.parseDouble(df.format(F_NF[0])));
				insertps.setDouble(21 , Double.parseDouble(df.format(F_NF[1])));
				insertps.setDouble(22 , Double.parseDouble(df.format(F_NF[2])));
				insertps.setDouble(23 , Double.parseDouble(df.format(F_NF[3])));
				insertps.setDouble(24 , Double.parseDouble(df.format(F_NF[4])));
				insertps.setDouble(25 , Double.parseDouble(df.format(F_NEF[0])));
				insertps.setDouble(26 , Double.parseDouble(df.format(F_NEF[1])));
				insertps.setDouble(27 , Double.parseDouble(df.format(F_NEF[2])));
				insertps.setDouble(28 , Double.parseDouble(df.format(F_NEF[3])));
				insertps.setDouble(29 , Double.parseDouble(df.format(F_NEF[4])));
				insertps.setDouble(30 , Double.parseDouble(df.format(F_EF[0])));
				insertps.setDouble(31 , Double.parseDouble(df.format(F_EF[1])));
				insertps.setDouble(32 , Double.parseDouble(df.format(F_EF[2])));
				insertps.setDouble(33 , Double.parseDouble(df.format(F_EF[3])));
				insertps.setDouble(34 , Double.parseDouble(df.format(F_EF[4])));
				insertps.setDouble(35 , Double.parseDouble(df.format(F_SEF[0])));
				insertps.setDouble(36 , Double.parseDouble(df.format(F_SEF[1])));
				insertps.setDouble(37 , Double.parseDouble(df.format(F_SEF[2])));
				insertps.setDouble(38 , Double.parseDouble(df.format(F_SEF[3])));
				insertps.setDouble(39 , Double.parseDouble(df.format(F_SEF[4])));
				insertps.setDouble(40 , Double.parseDouble(df.format(F_SF[0])));
				insertps.setDouble(41 , Double.parseDouble(df.format(F_SF[1])));
				insertps.setDouble(42 , Double.parseDouble(df.format(F_SF[2])));
				insertps.setDouble(43 , Double.parseDouble(df.format(F_SF[3])));
				insertps.setDouble(44 , Double.parseDouble(df.format(F_SF[4])));
				insertps.setDouble(45 , Double.parseDouble(df.format(F_SWF[0])));
				insertps.setDouble(46 , Double.parseDouble(df.format(F_SWF[1])));
				insertps.setDouble(47 , Double.parseDouble(df.format( F_SWF[2])));
				insertps.setDouble(48 , Double.parseDouble(df.format( F_SWF[3])));
				insertps.setDouble(49 , Double.parseDouble(df.format( F_SWF[4])));
				insertps.setDouble(50 , Double.parseDouble(df.format( F_WF[0])));
				insertps.setDouble(51 , Double.parseDouble(df.format( F_WF[1])));
				insertps.setDouble(52 , Double.parseDouble(df.format( F_WF[2])));
				insertps.setDouble(53 , Double.parseDouble(df.format( F_WF[3])));
				insertps.setDouble(54 , Double.parseDouble(df.format( F_WF[4])));
				insertps.setDouble(55 , Double.parseDouble(df.format( F_NWF[0])));
				insertps.setDouble(56 , Double.parseDouble(df.format( F_NWF[1])));
				insertps.setDouble(57 , Double.parseDouble(df.format( F_NWF[2])));
				insertps.setDouble(58 , Double.parseDouble(df.format( F_NWF[3])));
				insertps.setDouble(59 , Double.parseDouble(df.format( F_NWF[4])));
				
				insertps.setDouble(60 , Double.parseDouble(df.format( F_NAVGs[0])));
				insertps.setDouble(61 , Double.parseDouble(df.format( F_NAVGs[1])));
				insertps.setDouble(62 , Double.parseDouble(df.format( F_NAVGs[2])));
				insertps.setDouble(63 , Double.parseDouble(df.format( F_NAVGs[3])));
				insertps.setDouble(64 , Double.parseDouble(df.format( F_NAVGs[4])));
				insertps.setDouble(65 , Double.parseDouble(df.format( F_NEAVGs[0])));
				insertps.setDouble(66 , Double.parseDouble(df.format( F_NEAVGs[1])));
				insertps.setDouble(67 , Double.parseDouble(df.format( F_NEAVGs[2])));
				insertps.setDouble(68 , Double.parseDouble(df.format( F_NEAVGs[3])));
				insertps.setDouble(69 , Double.parseDouble(df.format( F_NEAVGs[4])));
				insertps.setDouble(70 , Double.parseDouble(df.format( F_EAVGs[0])));
				insertps.setDouble(71 , Double.parseDouble(df.format( F_EAVGs[1])));
				insertps.setDouble(72 , Double.parseDouble(df.format( F_EAVGs[2])));
				insertps.setDouble(73 , Double.parseDouble(df.format( F_EAVGs[3])));
				insertps.setDouble(74 , Double.parseDouble(df.format( F_EAVGs[4])));
				insertps.setDouble(75 , Double.parseDouble(df.format( F_SEAVGs[0])));
				insertps.setDouble(76 , Double.parseDouble(df.format( F_SEAVGs[1])));
				insertps.setDouble(77 , Double.parseDouble(df.format( F_SEAVGs[2])));
				insertps.setDouble(78 , Double.parseDouble(df.format( F_SEAVGs[3])));
				insertps.setDouble(79 , Double.parseDouble(df.format( F_SEAVGs[4])));
				insertps.setDouble(80 , Double.parseDouble(df.format( F_SAVGs[0])));
				insertps.setDouble(81 , Double.parseDouble(df.format( F_SAVGs[1])));
				insertps.setDouble(82 , Double.parseDouble(df.format( F_SAVGs[2])));
				insertps.setDouble(83 , Double.parseDouble(df.format( F_SAVGs[3])));
				insertps.setDouble(84 , Double.parseDouble(df.format( F_SAVGs[4])));
				insertps.setDouble(85 , Double.parseDouble(df.format( F_SWAVGs[0])));
				insertps.setDouble(86 , Double.parseDouble(df.format( F_SWAVGs[1])));
				insertps.setDouble(87 , Double.parseDouble(df.format( F_SWAVGs[2])));
				insertps.setDouble(88 , Double.parseDouble(df.format( F_SWAVGs[3])));
				insertps.setDouble(89 , Double.parseDouble(df.format( F_SWAVGs[4])));
				insertps.setDouble(90 , Double.parseDouble(df.format( F_WAVGs[0])));
				insertps.setDouble(91 , Double.parseDouble(df.format( F_WAVGs[1])));
				insertps.setDouble(92 , Double.parseDouble(df.format( F_WAVGs[2])));
				insertps.setDouble(93 , Double.parseDouble(df.format( F_WAVGs[3])));
				insertps.setDouble(94 , Double.parseDouble(df.format( F_WAVGs[4])));
				insertps.setDouble(95 , Double.parseDouble(df.format( F_NWAVGs[0])));
				insertps.setDouble(96 , Double.parseDouble(df.format( F_NWAVGs[1])));
				insertps.setDouble(97 , Double.parseDouble(df.format( F_NWAVGs[2])));
				insertps.setDouble(98 , Double.parseDouble(df.format( F_NWAVGs[3])));
				insertps.setDouble(99 , Double.parseDouble(df.format( F_NWAVGs[4])));
				
				insertps.setDouble(100 , Double.parseDouble(df.format( F_NMAX)));
				insertps.setDouble(101 , Double.parseDouble(df.format( F_NEMAX)));
				insertps.setDouble(102 , Double.parseDouble(df.format( F_EMAX)));
				insertps.setDouble(103 , Double.parseDouble(df.format( F_SEMAX)));
				insertps.setDouble(104 , Double.parseDouble(df.format( F_SMAX)));
				insertps.setDouble(105 , Double.parseDouble(df.format( F_SWMAX)));
				insertps.setDouble(106 , Double.parseDouble(df.format( F_WMAX)));
				insertps.setDouble(107 , Double.parseDouble(df.format( F_NWMAX)));
				
				insertps.setInt(108 , F_WNDNUM[0]);
				insertps.setInt(109 , F_WNDNUM[1]);
				insertps.setInt(110 , F_WNDNUM[2]);
				insertps.setInt(111 , F_WNDNUM[3]);
				insertps.setInt(112 , F_WNDNUM[4]);
				insertps.setInt(113 , F_WNDNUM[5]);
				insertps.setInt(114 , F_WNDNUM[6]);
				
				insertps.setDouble(115, Double.parseDouble(df.format( UMAX)));
				insertps.setString(116 , UMAXTIME);
				insertps.setDouble(117, Double.parseDouble(df.format( UMIN)));
				insertps.setString(118 , UMINTIME);
				insertps.setDouble(119 , Double.parseDouble(df.format( VMAX)));
				insertps.setString(120 , VMAXTIME);
				insertps.setDouble(121, Double.parseDouble(df.format( VMIN)));
				insertps.setString(122 , VMINTIME);
				insertps.setDouble(123, Double.parseDouble(df.format( F_NAVG)));
				insertps.setDouble(124, Double.parseDouble(df.format( F_NEAVG)));
				insertps.setDouble(125, Double.parseDouble(df.format( F_EAVG)));
				insertps.setDouble(126, Double.parseDouble(df.format( F_SEAVG)));
				insertps.setDouble(127, Double.parseDouble(df.format( F_SAVG)));
				insertps.setDouble(128, Double.parseDouble(df.format( F_SWAVG)));
				insertps.setDouble(129, Double.parseDouble(df.format( F_WAVG)));
				insertps.setDouble(130, Double.parseDouble(df.format( F_NWAVG)));
				
				insertps.addBatch();
				
				if (q % numCommit == 0) {
					insertps.executeBatch();
					conn.commit();
					insertps.clearBatch();
				}
				
			}
			
				insertps.executeBatch();
				conn.commit();
				insertps.clearBatch();
		
		
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.err.println("连接执行失败");
		}
		
		
		
		
		
	}
	public static String getDoubleNum(Integer[] array,double a,double b) {
		String str = "";
		for (int i = 0; i < array.length; i++) {
			Integer d =array[i];
			if(b==360.0)
				if(d>=a&d<=b)
					str+="1";
				else
					str+="0";
			
			else
				if(d>=a&d<b)
					str+="1";
				else
					str+="0";
		}
		return str;
	}
	public static String getDoubleNum(Double[] array,double a,double b) {
		String str = "";
		for (int i = 0; i < array.length; i++) {
			double d = array[i];
			
			if(b==QS||b==-9999.0)
				if(a<10)
					if(d<a)
						str+="1";
					else
						str+="0";
				else
					if(d>=a)
						str+="1";
					else
						str+="0";
			else
				if(d>=a&d<b)
					str+="1";
				else
					str+="0";
		}
		return str;
	}
	public static List<Double> getEqualsIndexList(Double[] doubles,int num) {
		
		List<Double> list = new ArrayList<Double>();
		char[] c = Integer.toBinaryString(num).toCharArray();
		for (int i = 0; i < c.length; i++) {
			if(c[i]=='1')
				list.add(doubles[i]);
			
				
		}
		return list;
	}
	public static int findAndLength (int num) {
		List<Integer> list = new ArrayList<Integer>();
		char[] chars = Integer.toBinaryString(num).toString().toCharArray();
		for(int i=0;i<chars.length;i++)
		{
			if(chars[i]=='1')
				list.add(i);
		}
		return list.size();
	}
	
	@Test
	public void test01() {
		//条件判断语句的使用
		int a[] =new int[10];
		int num =120;
				
		if(num>10)
			a[1]=10;
		else if(num>20)
			a[2]=20;
		else
			a[9]=100;
			
		System.out.println(Arrays.toString(a));
		
		
		
//		int a = Integer.parseInt("1011", 2);
//		
//		System.out.println(a|18);
//		System.out.println(Integer.toBinaryString(15));
//		
//		Integer.parseInt("1010", 2);
//		System.out.println(Integer.parseInt("1010", 2)|Integer.parseInt("0101",2));
//		System.out.println(findAndLength(Integer.parseInt("1010", 2)|Integer.parseInt("0101",2)));

		
//		System.out.println(1010&0101);
//		System.out.println(0b1010|0b0101);	
//		System.out.println(1010^0101);
		
		
//		Double[] WD = {340.0,0.0,3.0,340.0,4.0,4.0};
//		int num = Integer.parseInt(getDoubleNum(WD, 337.5, 360),2) |Integer.parseInt(getDoubleNum(WD, 0, 22.5),2);
//		System.out.println(Integer.parseInt(getDoubleNum(WD, 337.5, 360),2));
//		
//		System.out.println(Integer.parseInt(getDoubleNum(WD, 0, 22.5),2));
//		System.out.println(36|11);
//		System.out.println(2|1);
//		
//		System.out.println(num);
//		System.out.println(Integer.toBinaryString(num));
//		int i = 0b1101;
//		System.out.println(i);
//		
//		
		//判断360 360.0相等
		System.out.println(360==360.0);
		System.out.println(QS==-9999.0);
//		int a=1;
//		int b=100;
//		System.out.println(String.format("% 02d", 1));
//		System.out.println(String.format("%02d", 12));
//		
		
//		int a=1;
//		String str = String.format("% 4d", a);
//		System.out.println(String.format("%04d", 1).toString());
//		System.out.println(String.format("% 4d", a).toString());
//		int num = Integer.valueOf(str.trim());
//		System.out.println(num);
//		num = Integer.parseInt(str.trim());
//		System.out.println(num);
//		double d = -2*Math.sin(4*Math.PI/180);
//		System.out.println(-2*Math.sin(4*Math.PI/180));
//		System.out.println(d);
		
		List list = new ArrayList<Integer>();
		list.add(12);
		list.add(21);
		list.add(11);
		list.add(21);
		
		
		System.out.println(Arrays.toString(list.toArray()));
		
//		Utils.getEqualsArray(list.toArray(), new Integer[] {1,3,4,5,5,6}, 21);
		
		System.out.println(JFMUtils.MEAN(list.toArray()));
		
		
		double d = 2.300001;
		System.out.println(Double.parseDouble(String.valueOf(d)));
		
	}
}
class JFMUtils{
	public static Integer SUM(Integer[] arr) {
		int sum = 0;
		for(int i:arr) {
			sum+=i;
		}
		
		return sum;
	}
	public static Double MEAN(Object[] arr) {
		int sum = 0;
		
		
		for(Object i:arr) {
			sum+=Double.parseDouble(i.toString());
		}
		
		return (double)sum/arr.length;
	}
	
	
}