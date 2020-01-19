package com.htht.sta.hyd.temp;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.htht.utils.Arith;
import com.htht.utils.Utils;
import com.mysql.fabric.xmlrpc.base.Array;
import com.mysql.jdbc.StringUtils;

/**
 * ����վ�ܼ��ȳ���ͳ���ղ�Ʒ
 *  ���룺��ʪѹ�۲����ݣ�T_OR_STD_MET_TPRV��
	������ղ�Ʒ��T_PR_STD_METE_VIS_DAY��
	note�����ݿ�������Ϣ/����/����ֶ�
 * @author edz
 *
 */
public class C_PR_STD_METE_VIS_DAY {

	
	String StationInfoTablename="T_ZD_STATION";
	String ITablename="T_OR_STD_MET_TPRV";
	String OTablename="T_PR_STD_METE_VIS_DAY";
	// ͳ���������
	private static int q = 0;
	// �ۻ����������
	private static int numCommit = 30000;
	// ��Чֵ����
	private static int QS = -9999;
	private static int arrCount;
	public static void main(String[] args) {
		Long startTime = System.currentTimeMillis();
		String url = "jdbc:oracle:thin:@10.1.2.63:1521:orcl";
		String username = "TJFX";
		String password = "TJFX";
		
		
		// String url = "jdbc:oracle:thin:@"+args[0];
		// String username = args[1];
		// String password = args[2];
		
		Connection conn = null;
		PreparedStatement pstm = null;
		PreparedStatement pstmQuery = null;
		String code;
		double lat;
		double lon;
		String daytime = "";// �۲�����
		Date date;
		int viscode;// �ܼ�����������

		int vis08 ;
		int vis08Q;
		int vis14;
		int vis14Q;
		int vis20;
		int vis20Q;
		
		int vis[];
		int visQ[];
		String[] timeArr=null;
		
		int invalid=0;
		int num_invalid_vis = 0;
		
		int vis_avg ,vis_max,vis_min;
		String vis_maxtime = null,vis_mintime = null;
		
		try {
			conn = Utils.getConnection("oracle.jdbc.driver.OracleDriver", url,
					username, password);
			String sql = "insert into T_PR_STD_METE_VIS_DAY values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			pstm = conn.prepareStatement(sql);
			conn.setAutoCommit(false);
			// ��ѯȫ������ ��������  �����ɼ�ʱ�䷶Χ������  and F_OBSDATE between to_date('" + date + " 00','YYYY-MM-dd HH24') and "+ "to_date('" + date + " 23','YYYY-MM-DD HH24');";
			//String queryStaSql = "select * from T_OR_STD_MET_TPRV order by F_OBSDATE";
			String queryStaSql = "select * from T_OR_STD_MET_TPRV  order by F_OBSDATE";
			pstmQuery = conn.prepareStatement(queryStaSql);
			ResultSet executeQuery = pstmQuery.executeQuery();
			// ����ԭʼ�������ղ�Ʒͳ�� �������ݿ���ȷ�ȣ�ÿ��ֻ��һ������
			while (executeQuery.next()) {
				q++;
				code = executeQuery.getString("F_STDNAME");
				lat = executeQuery.getDouble("F_LAT");
				lon = executeQuery.getDouble("F_LON");
				daytime = executeQuery.getString("F_OBSDATE");
			
				viscode = executeQuery.getInt("F_VISCODE");

				vis08 = executeQuery.getInt(50);
				vis08Q = executeQuery.getInt(51);
				vis14 = executeQuery.getInt(52);
				vis14Q = executeQuery.getInt(53);
				vis20 = executeQuery.getInt(54);
				vis20Q = executeQuery.getInt(55);
				vis  = new int[] {vis08,vis14,vis20};
				visQ = new int[] {vis08Q,vis14Q,vis20Q};
				timeArr = new String[] {daytime+" 08",daytime+" 14",daytime+" 20"};
				
				for(int i=0;i<timeArr.length;i++) {
					
					if(vis[i]==99.7|vis[i]==QS|vis[i]>90)
						visQ[i]=1;
				}
				
				for(int num:visQ) {
					num_invalid_vis+=num;
				}
				if(isEmpty(vis)) {
					vis_avg=vis_max=vis_min=QS;
					vis_maxtime=vis_mintime=String.valueOf(QS);
				}else {
					if(num_invalid_vis > invalid)
						vis_avg=QS;
					else
						vis_avg=(vis[0]+vis[1]+vis[2])/3;
				List list = new ArrayList<Integer>();
					for(int i=0;i<visQ.length;i++) {
						if(visQ[i]==0) {
							list.add(vis[i]);
						}
							
					}
				vis_max = getMax(list.toArray());
				vis_min= getMin(list.toArray());
					//�ж�vis_max�Ƿ�Ϊ�գ���Ϊvis_maxtime����ֵ��ȫ��ֵΪmax  *time����Ϊ3��3�죩��������
					if(com.alibaba.excel.util.StringUtils.isEmpty(vis_max)|String.valueOf(vis_max).equals("")) {
						vis_max = QS;
						vis_maxtime = String.valueOf(QS);
					
					}else{
						for (int i = 0; i < vis.length; i++) {
							if(vis[i]==vis_max) {
								vis_maxtime += timeArr[i]+",";
							}
						}
						int size=vis_maxtime.split(",").length;
						if(size==4)
							vis_maxtime=String.valueOf(size-1);
						else
							vis_maxtime=vis_maxtime.substring(0,vis_maxtime.length()-1);
					}
					//�ж�vis_minֵ���Ƿ�Ϊ�ա���Ϊvis_maxtime����ֵ��ȫ��ֵһ��Ϊmin ��*time����Ϊ���� 3��������
					if(com.alibaba.excel.util.StringUtils.isEmpty(vis_min)|String.valueOf(vis_min).equals("")) {
						vis_min = QS;
						vis_mintime = String.valueOf(QS);
					
					}else{
						for (int i = 0; i < vis.length; i++) {
							if(vis[i]==vis_min) {
								vis_mintime += timeArr[i]+",";
							}
						}
						int size=vis_mintime.split(",").length;
						if(size>=4)
							vis_mintime=String.valueOf(size-1);
						else
							vis_mintime=vis_mintime.substring(0,vis_mintime.length()-1);
					}	
				}
				
				
				
		        // �۲����ڸ�ʽ�涨Ϊ10λ����yyyy-mm-dd'
		        // ����ʶ��Ϊ��s",��ʾ����ƽ��
		        
		        
		        
        		pstm.setNull(1, Types.NULL);
        		pstm.setString(2, code);
        		pstm.setDouble(3, lat);
        		pstm.setDouble(4, lon);
        		date = new Date(Utils.strDateConvertToTimestamp(daytime));
        		pstm.setDate(5, date);// time
        		pstm.setDouble(6, vis_avg);
        		pstm.setDouble(7, vis_max);
        		pstm.setString(8, vis_maxtime);
        		pstm.setDouble(9, vis_min);
        		pstm.setString(10, vis_mintime);
        		pstm.setDouble(11, viscode);
        		pstm.setDate(12, new Date(System.currentTimeMillis()));
        		
        		pstm.addBatch();
				if (q % numCommit == 0) {
					pstm.executeBatch();
					conn.commit();
					pstm.clearBatch();
				}
			}
			pstm.executeBatch();
			conn.commit();
			pstm.clearBatch();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (pstm != null) {
				try {
					pstm.close();
				} catch (SQLException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}
		}
		Long endTime = System.currentTimeMillis();
		System.out.println("ִ�����" + q + "��!");
		System.out.println("��ʱ��" + (endTime - startTime) / 1000 + "s");
	}
	

	public static boolean isEmpty(int ...vis) {
		for(Object o:vis)
		{
			if(isEmpty(String.valueOf(o))) {
				return true;
			}
		}
		
		return false;
	}
	public static boolean isEmpty(String str ) {
		
		if(str!=null&&str.length()!=0) {
			return false;
		}
		return true;
		
	}
	public static double  mean(int[] arr) {
		double sum=0;
		for(int a:arr)
		{
			sum+=a;
		}
		return sum/arr.length;
	}
	public static int getMax(Object arr[]) {
		
		int [] arrInt = new int[arr.length];
		
		for(int i=0;i<arr.length;i++) {
			arrInt[i]=(Integer)arr[i];
			
		}	
//		return Collections.max(Arrays.asList(arr));//����Ϊ��װ��
		
		Arrays.sort(arrInt);
		return arrInt[arrInt.length-1];
	}
	public static int getMin(Object arr[]) {
		int [] arrInt = new int[arr.length];
		
		for(int i=0;i<arr.length;i++) {
			arrInt[i]=(Integer)arr[i];
			
		}	
		return Arrays.stream(arrInt).min().getAsInt();
		
	}
	@Test
	public void test01() {
		Integer[] a = {1,3,4,5,6,9};
		int num =(int)Collections.max(Arrays.asList(a));
		System.out.println(num);
		
		Arrays.sort(a);
		System.err.println(a[a.length-1]);
		
		String aa = new String();
		String bb = "";
		String cc = null;
		System.out.println(aa.equals(bb));
		System.out.println(bb.isEmpty());
		System.out.println(aa.isEmpty());
		Integer[] inta ={};
		System.out.println(getMax(inta));
		
		
		
	}
}
