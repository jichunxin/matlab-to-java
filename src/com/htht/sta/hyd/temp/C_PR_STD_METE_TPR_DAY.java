package com.htht.sta.hyd.temp;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import com.htht.utils.Arith;
import com.htht.utils.Utils;

/**
 * 读取中心综合数据库版本
 * 海洋站-气象-温湿压 插件 2019年12月9日 16:17:08
 * 质量符Q=0 有效数据 Q=1无效数据  (ps:目前综合数据库无质量符Q,默认全部是0)
 * @author Administrator
 */
public class C_PR_STD_METE_TPR_DAY {
	// 统计入库条数
	private static int q = 0;
	// 累积多少条入库
	private static int numCommit = 30000;
	// 无效值定义
	private static int QS = -9999;
	private static int arrCount;
	public static void main(String[] args) {
		Long startTime = System.currentTimeMillis();
		String url = "jdbc:oracle:thin:@192.168.101.129:1521:helowin";
		String username = "system";
		String password = "123123";
		// String url = "jdbc:oracle:thin:@"+args[0];
		// String username = args[1];
		// String password = args[2];
		Connection conn = null;
		PreparedStatement pstm = null;
		PreparedStatement pstmQuery = null;
		String code;
		double lat;
		double lon;
		String daytime = "";// 观测日期
		String pflag = "";// L：本站 S： 海平面
		int platH;// 观测台高度
		int preH;// 传感器高度
		double preHd;// 传感器高度 double类型
		int precode;// 气压仪器代码
		int tempcode;// 温度仪器代码
		int rhcode;// 相对湿度仪器代码
		//int prcicode;// 降水仪器代码
		//int viscode;// 能见度仪器代码
		
		// 目前综合数据库无质量符Q,默认全部是0
		double ap02;// 气压02时数据
		int ap02Q = 0;// 气压02时数据质控符
		double ap08;//
		int ap08Q = 0;//
		double ap14;//
		int ap14Q = 0;//
		double ap20;//
		int ap20Q = 0;//
		double apmax;// 气压最大值
		int apmaxQ = 0;// 气压最大值质控符
		double apmin;// 气压最小值
		int apminQ = 0;// 气压最小值质控符

		double at02;// 气温
		int at02Q = 0;//
		double at08;//
		int at08Q = 0;//
		double at14;//
		int at14Q = 0;//
		double at20;//
		int at20Q = 0;//
		double atmax;//
		int atmaxQ = 0;//
		double atmin;//
		int atminQ = 0;//

		int rh02;// 相对湿度
		int rh02Q = 0;//
		int rh08;//
		int rh08Q = 0;//
		int rh14;//
		int rh14Q = 0;//
		int rh20;//
		int rh20Q = 0;//
		int rhmin;//
		int rhminQ = 0;//
		
		double[] at = new double[4];
        int[] atQ = new int[4];
        double[] ap = new double[4];
        int[] apQ = new int[4];
        int[] rh = new int[4];
        int[] rhQ = new int[4];
		
		double[] QXap_s;
        int[] QXap_sQ;
        double[] GBap_s;
        int[] GBap_sQ;
        double[] ap_l;
        int[] ap_lQ;
        double[] C;
		double[] M;
		// 无效数据数目阈值
		int invalid = 0;
		int num_invalid_at; 
		double atavg;// 气温平均值
		double[] at_all;
		int[] atQ_all;
		String time;
		String atmaxtime;
		String atmintime;
		double atdif; // 极值差值
		
		double Lapavg;
		double Lapmax;
		double Lapmin;
        String Lapmaxtime;
        String Lapmintime;
        
        int num_invalid_Lap;
        double[] ap_l_all;
        int[] ap_lQ_all;
        int num_invalid_QXap;
        double QXapavg;
        double QXapmax;
        double QXapmin;
		String QXapmaxtime;
		String QXapmintime;
		
		int num_invalid_GBap;
		double GBapavg;
        double GBapmax;
        double GBapmin;
		String GBapmaxtime;
		String GBapmintime;
		
		//相对湿度
		int num_invalid_rh;
		double rhavg;
		int rhmax;
		String rhmaxtime;
		String rhmintime;
		int[] rh_all;
        int[] rhQ_all;
        Date date;
		try {
			conn = Utils.getConnection("oracle.jdbc.driver.OracleDriver", url,
					username, password);
			String sql = "insert into T_PR_STD_METE_TPR_DAY_SYN values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			pstm = conn.prepareStatement(sql);
			conn.setAutoCommit(false);
			// 查询全表数据 排序正序  后续可加时间范围内数据  and F_OBSDATE between to_date('" + date + " 00','YYYY-MM-dd HH24') and "+ "to_date('" + date + " 23','YYYY-MM-DD HH24');";
			//String queryStaSql = "select * from T_OR_STD_MET_TPRV order by F_OBSDATE";
			//String queryStaSql = "select * from T_OR_STD_MET_TPRV where F_OBSDATE between to_date('1994-01-01 00','YYYY-MM-dd HH24') and to_date('1995-01-01 23','YYYY-MM-DD HH24') order by F_OBSDATE";
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT tai.A3011300408 F_STDNAME, tai.A3001100200 F_LAT, tai.A3001100100 F_LON, tai.A3020000600 F_PLATH, tai.A3020200500 F_PREH,");
			sb.append("tai.A3020200403 F_PRECLASS, tai.A3020200108 F_PRECODE, tai.A3020100108 F_TEMPCODE, tai.A3020300108 F_RHCODE, tai.A3020800908 F_PRCIPCODE,");
			sb.append("dad.A3001200400 F_OBSDATE, dad.A3020201801 F_PRE02, dad.A3020201802 F_PRE02Q, dad.A3020201901 F_PRE08, dad.A3020201902 F_PRE08Q,");
			sb.append("dad.A3020202001 F_PRE14, dad.A3020202002 F_PRE14Q, dad.A3020202101 F_PRE20, dad.A3020202102 F_PRE20Q, dad.A3020202201 	F_PREMAX, ");
			sb.append("dad.A3020202202 F_PREMAXQ, dad.A3020202301 F_PREMIN, dad.A3020202302 F_PREMINQ, dad.A3020103200 F_TEMP02, dad.A3020103202 F_TEMP02Q, ");
			sb.append("dad.A3020103300 F_TEMP08, dad.A3020103302 F_TEMP08Q, dad.A3020103400 F_TEMP14, dad.A3020103402 F_TEMP14Q, dad.A3020103500 F_TEMP20, ");
			sb.append("dad.A3020103502 F_TEMP20Q, dad.A3020101300 F_TEMPMAX, dad.A3020101302 F_TEMPMAXQ, dad.A3020101400 F_TEMPMIN, dad.A3020101402 F_TEMPMINQ,");
			sb.append("dad.A3020104000 F_RH02, dad.A3020104002 F_RH02Q, dad.A3020104100 F_RH08, dad.A3020104102 F_RH08Q, dad.A3020104200 F_RH14, dad.A3020104202 F_RH14Q,");
			sb.append("dad.A3020104300 F_RH20, dad.A3020104302 F_RH20Q, dad.A3020104400 F_RHMIN, dad.A3020104402 F_RHMINQ");
		    sb.append("FROM T_OR_STD_MET_TPRV");
			String queryStaSql = sb.toString();
		 	pstmQuery = conn.prepareStatement(queryStaSql);
			ResultSet executeQuery = pstmQuery.executeQuery();
			// 根据原始数据中日产品统计 中心数据库正确度，每天只有一条数据
			while (executeQuery.next()) {
				q++;
				code = executeQuery.getString("F_STDNAME");
				lat = executeQuery.getDouble("F_LAT");
				lon = executeQuery.getDouble("F_LON");
				daytime = executeQuery.getString("F_OBSDATE");
				pflag = executeQuery.getString("F_PFLAG");
				platH = executeQuery.getInt("F_PLATH");
				preH = executeQuery.getInt("F_PREH");
				preHd = Arith.mul(String.valueOf(preH), "0.1");
				precode = executeQuery.getInt("F_PRECODE");
				tempcode = executeQuery.getInt("F_TEMPCODE");
				rhcode = executeQuery.getInt("F_RHCODE");
				//prcicode = executeQuery.getInt("F_PRCIPCODE");
				//viscode = executeQuery.getInt("F_VISCODE");

				ap02 = executeQuery.getDouble("F_PRE02");
				//ap02Q = executeQuery.getInt("F_PRE02Q");
				ap08 = executeQuery.getDouble("F_PRE08");
				//ap08Q = executeQuery.getInt("F_PRE08Q");
				ap14 = executeQuery.getDouble("F_PRE14");
				//ap14Q = executeQuery.getInt("F_PRE14Q");
				ap20 = executeQuery.getDouble("F_PRE20");
				//ap20Q = executeQuery.getInt("F_PRE20Q");
				apmax = executeQuery.getDouble("F_PREMAX");
				//apmaxQ = executeQuery.getInt("F_PREMAXQ");
				if (apmaxQ == 2 | apmax == QS) {
					apmaxQ = 1;
				}
				apmin = executeQuery.getDouble("F_PREMIN");
				//apminQ = executeQuery.getInt("F_PREMINQ");
				if (apminQ == 2 | apmin == QS) {
					apminQ = 1;
				}
				
				at02 = executeQuery.getDouble("F_TEMP02");
				//at02Q = executeQuery.getInt("F_TEMP02Q");
				at08 = executeQuery.getDouble("F_TEMP08");
				//at08Q = executeQuery.getInt("F_TEMP08Q");
				at14 = executeQuery.getDouble("F_TEMP14");
				//at14Q = executeQuery.getInt("F_TEMP14Q");
				at20 = executeQuery.getDouble("F_TEMP20");
				//at20Q = executeQuery.getInt("F_TEMP20Q");
				atmax = executeQuery.getDouble("F_TEMPMAX");
				//atmaxQ = executeQuery.getInt("F_TEMPMAXQ");
				if (atmaxQ == 2 | atmax == QS) {
					atmaxQ = 1;
				}
				atmin = executeQuery.getDouble("F_TEMPMIN");
				//atminQ = executeQuery.getInt("F_TEMPMINQ");
				if (atminQ == 2 | atmin == QS) {
					atminQ = 1;
				}
				
				rh02 = executeQuery.getInt("F_RH02");
				//rh02Q = executeQuery.getInt("F_RH02Q");
				rh08 = executeQuery.getInt("F_RH08");
				//rh08Q = executeQuery.getInt("F_RH08Q");
				rh14 = executeQuery.getInt("F_RH14");
				//rh14Q = executeQuery.getInt("F_RH14Q");
				rh20 = executeQuery.getInt("F_RH20");
				//rh20Q = executeQuery.getInt("F_RH20Q");
				rhmin = executeQuery.getInt("F_RHMIN");
				//rhminQ = executeQuery.getInt("F_RHMINQ");
				if (rhminQ == 2 | rhmin == QS | rhmin == 99) {
					atminQ = 1;
				}
				time = daytime.substring(0, 10);//　取yyyy-mm-dd
				
				//02缺省值用08代替
				if (rh02 == QS || rh02Q == 1 || rh02Q == 2) {
					rh02 = rh08;
		            rh02Q =rh08Q;
				}
				at[0] = at02;
				at[1] = at08;
				at[2] = at14;
				at[3] = at20;
				
				atQ[0] = at02Q;
				atQ[1] = at08Q;
				atQ[2] = at14Q;
				atQ[3] = at20Q;
				atQ = arrQCI(atQ, 2, 1);
				
				ap[0] = ap02;
				ap[1] = ap08;
				ap[2] = ap14;
				ap[3] = ap20;
				apQ[0] = ap02Q;
				apQ[1] = ap08Q;
				apQ[2] = ap14Q;
				apQ[3] = ap20Q;
		        apQ = arrQCI(apQ, 2, 1);
		        
		        rh[0] = rh02;
		        rh[1] = rh08;
		        rh[2] = rh14;
		        rh[3] = rh20;
		        
		        rhQ[0] = rh02Q;
		        rhQ[1] = rh08Q;
		        rhQ[2] = rh14Q;
		        rhQ[3] = rh20Q;
		        rhQ = arrQCI(rhQ, 2, 1); 
		        // 观测日期格式规定为10位，‘yyyy-mm-dd'
		        // 当标识符为“s",表示：海平面
		        if ("s".equals(pflag)) {
		        	QXap_s = ap;
		            QXap_sQ = apQ;
		            GBap_s = ap;
		            GBap_sQ = apQ;
		            ap_l = null;
		            ap_lQ = null;
		        } else {// L本站
		        	ap_l = ap;
		            ap_lQ = apQ;
		            //QXap_s=ap_l.*exp(0.03415* preH./(273+at));
		            QXap_s = new double[4];// 2 8 14 20四个值
		            QXap_s = compute(QXap_s, ap_l, preHd, at);
		            GBap_s = new double[4];
		            C = new double[4];
		    		M = new double[4];
		            GBap_s = GBap(GBap_s, ap_l, preH, at, C, M);
		        }
		        
		        // 气温
		        atQ = arrQCEqGtd(at, atQ, QS, 100, 1);
		        // 无效数据个数  atQ  (ps:0是正常1是不正常)
		        num_invalid_at = 0;
		        num_invalid_at = arrSum(num_invalid_at, atQ);
		        atavg = 0; // 重置数据
		        if (num_invalid_at > invalid) {
		        	atavg = QS;
		        } else {
		        	Lapavg = 0;
		        	atavg = arrMean(atavg, at, atQ);
		        }
		        at_all = new double[6];
		        at_all = arrTranD(at_all, at);
		        at_all[4] = atmax;
		        at_all[5] = atmin;
		        		
		        atQ_all = new int[6];
		        atQ_all = arrTranI(atQ_all, atQ);
		        atQ_all[4] = atmaxQ;
        		atQ_all[5] = atminQ;
        		// 求出最大值和最小值
        		atmax = at_all[0];
        		atmax = arrMax(atmax, at_all, atQ_all);
        		atmin = at_all[0];
        		atmin = arrMin(atmin, at_all, atQ_all);
        		if ((Double)atmax == null) { // 如果最大值为空时
        			atmax = QS;
        			atmaxtime = String.valueOf(QS);
        		} else {
        			atmaxtime = time;
        		}
        		if ((Double)atmin == null) { // 如果最大值为空时
        			atmin = QS;
        			atmintime = String.valueOf(QS);
        		} else {
        			atmintime = time;
        		}
        		// 若最大值是无效值 最小值是无效值  最大值是最小值  
        		if (atmax == QS || atmin == QS || atmax == atmin) {
        			// 极值差值  无法计算差值 按无效值入手 
        			atdif = QS;
        		} else {
        			atdif = atmax - atmin; //极值差值
        		}

        		// 本站气压
        		ap_lQ = arrQCEqGtd(ap_l, ap_lQ, QS, 9000, 1);
        		// 如果数组是空
        		if (ap_l.length == 0 || ap_lQ.length == 0) {
        			Lapavg = QS;
                    Lapmax = QS;
                    Lapmin = QS;
                    Lapmaxtime = String.valueOf(QS);
                    Lapmintime = String.valueOf(QS);
        		} else {
        			num_invalid_Lap = 0; // 无效值个数
        			// sum(ap_lQ(2:4));
        			num_invalid_Lap = arrSum24(num_invalid_Lap,ap_lQ);
        			if (num_invalid_Lap > invalid){
        				Lapavg = QS;
        			} else {
        				Lapavg = 0;
        				Lapavg = arrMean(Lapavg, ap_l, ap_lQ);
        			}
        			ap_l_all = new double[6];
        			ap_l_all = arrTranD(ap_l_all,ap_l);
        			ap_l_all[4] = apmax;
        			ap_l_all[5] = apmin;
        			
        			ap_lQ_all = new int[6];
                    ap_lQ_all = arrTranI(ap_lQ_all,ap_lQ);
                    ap_lQ_all[4] = apmaxQ;
        			ap_lQ_all[5] = apminQ;
        			Lapmax = ap_l_all[0];
                    Lapmax = arrMax(Lapmax, ap_l_all, ap_lQ_all);
                    Lapmin = ap_l_all[0];
                    Lapmin = arrMin(Lapmin, ap_l_all, ap_lQ_all);
                    
                    if ((Double)Lapmax ==null) {
                    	Lapmax = QS;
                        Lapmaxtime = String.valueOf(QS);
                    } else {
                    	Lapmaxtime = time;
                    }
                    if ((Double)Lapmin ==null) {
                    	Lapmin = QS;
                    	Lapmintime = String.valueOf(QS);
                    } else {
                    	Lapmintime = time;
                    }
        		}
        		
        		// 海面气压(气象规范)
        		// 气压质控符值和气温质控符值之和
        		QXap_sQ = new int[4];
        		QXap_sQ = towArrSum(QXap_sQ, ap_lQ, atQ);
        		// QXap_sQ(QXap_sQ~=0)=1;
        		for (int i = 0; i < QXap_sQ.length; i++) {
        			if (QXap_sQ[i] != 0) QXap_sQ[i] = 1;
				}
        		num_invalid_QXap = 0;
        		num_invalid_QXap = arrSum24(num_invalid_QXap, QXap_sQ);
        		if (num_invalid_QXap > invalid) {
        			QXapavg = QS;
        		} else {
        			QXapavg = 0l;
        			QXapavg = arrMean(QXapavg, QXap_s, QXap_sQ);
        		}
        		QXapmax = QXap_s[0];
        		QXapmax = arrMax(QXapmax, QXap_s, QXap_sQ);
        		QXapmin = QXap_s[0];
        		QXapmin = arrMin(QXapmin, QXap_s, QXap_sQ);
        		if ((Double)QXapmax == null) {
        			QXapmax = QS;
                    QXapmaxtime = String.valueOf(QS);
        		} else {
        			QXapmaxtime = time;
        		}
        		if ((Double)QXapmin == null) {
        			QXapmin = QS;
        			QXapmintime = String.valueOf(QS);
        		} else {
        			QXapmintime = time;
        		}
        		
        		// 海面气压（海滨规范）
        		GBap_sQ = new int[4];
        		GBap_sQ = towArrSum(GBap_sQ, ap_lQ, atQ);
        		// QXap_sQ(QXap_sQ~=0)=1;
        		for (int i = 0; i < GBap_sQ.length; i++) {
        			if (GBap_sQ[i] != 0) GBap_sQ[i] = 1;
				}
        		num_invalid_GBap = 0;
        		num_invalid_GBap = arrSum24(num_invalid_GBap, GBap_sQ);
        		if (num_invalid_QXap > invalid) {
        			GBapavg = QS;
        		} else {
        			GBapavg = 0l;
        			GBapavg = arrMean(GBapavg, GBap_s, GBap_sQ);
        		}
        		GBapmax = GBap_s[0];
        		GBapmax = arrMax(GBapmax, GBap_s, GBap_sQ);
        		GBapmin = GBap_s[0];
        		GBapmin = arrMin(GBapmin, GBap_s, GBap_sQ);
        		if ((Double)GBapmax == null) {
        			GBapmax = QS;
        			GBapmaxtime = String.valueOf(QS);
        		} else {
        			GBapmaxtime = time;
        		}
        		if ((Double)GBapmin == null) {
        			GBapmin = QS;
        			GBapmintime = String.valueOf(QS);
        		} else {
        			GBapmintime = time;
        		}
        		
        		// 相对湿度
        		// rhQ(rh== QS | rh>100 | rh==99)=1;
        		for (int i = 0; i < rhQ.length; i++) {
					if (rhQ[i] == QS | rhQ[i] > 100 | rhQ[i] == 99) {
						rhQ[i] = 1;
					}
				}
        		num_invalid_rh = 0;
        		num_invalid_rh = arrSum(num_invalid_rh, rhQ);
        		if (num_invalid_rh > invalid) {
        			rhavg = QS;
        		} else {
        			rhavg = 0l;
        			rhavg = arrMean(rhavg, rh, rhQ);
        		}
        		rh_all = new int[5];
        		rh_all = arrTranI(rh_all, rh);
        		rh_all[4] = rhminQ;
        		rhQ_all = new int[5];
        		rhQ_all = arrTranI(rhQ_all, rhQ);
        		rhQ_all[4] = rhminQ;
        		
        		rhmax = rh_all[0];
        		rhmax = arrMax(rhmax, rh_all, rhQ_all);
        		rhmin = rh_all[0];
        		rhmin = arrMin(rhmin, rh_all, rhQ_all);
        		if ((Integer)rhmax == null) {
        			rhmax = QS;
        			rhmaxtime = String.valueOf(QS);
        		} else {
        			rhmaxtime = time;
        		}
        		if ((Integer)rhmin == null) {
        			rhmin = QS;
        			rhmintime = String.valueOf(QS);
        		} else {
        			rhmintime = time;
        		}
        		pstm.setNull(1, Types.NULL);
        		pstm.setString(2, code);
        		pstm.setDouble(3, lat);
        		pstm.setDouble(4, lon);
        		date = new Date(Utils.strDateConvertToTimestamp(time));
        		pstm.setDate(5, date);// time
        		pstm.setDouble(6, atavg);
        		pstm.setDouble(7, atmax);
        		pstm.setString(8, atmaxtime);
        		pstm.setDouble(9, atmin);
        		pstm.setString(10, atmintime);
        		pstm.setDouble(11, Lapavg);
        		pstm.setDouble(12, Lapmax);
        		pstm.setString(13, Lapmaxtime);
        		pstm.setDouble(14, Lapmin);
        		pstm.setString(15, Lapmintime);
        		pstm.setDouble(16, QXapavg);
        		pstm.setDouble(17, QXapmax);
        		pstm.setString(18, QXapmaxtime);
        		pstm.setDouble(19, QXapmin);
        		pstm.setString(20, QXapmintime);
        		pstm.setDouble(21, GBapavg);
        		pstm.setDouble(22, GBapmax);
        		pstm.setString(23, GBapmaxtime);
        		pstm.setDouble(24, GBapmin);
        		pstm.setString(25, GBapmintime);
        		pstm.setDouble(26, rhavg);
        		pstm.setDouble(27, rhmax);
        		pstm.setString(28, rhmaxtime);
        		pstm.setDouble(29, rhmin);
        		pstm.setString(30, rhmintime);
        		pstm.setDouble(31, atdif);
        		pstm.setDate(32, new Date(System.currentTimeMillis()));// 处理时间
        		pstm.setInt(33, tempcode);
        		pstm.setInt(34, precode);
        		pstm.setInt(35, rhcode);
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
		System.out.println("执行完毕" + q + "条!");
		System.out.println("用时：" + (endTime - startTime) / 1000 + "s");
	}
	

	private static int arrSum24(int num_invalid_Lap, int[] ap_lQ) {
		for (int i = 0; i < ap_lQ.length; i++) {
			if (i >= 2 && i <= 4) {
				num_invalid_Lap += ap_lQ[i];
			}
		}
		return num_invalid_Lap;
	}


	private static int[] towArrSum(int[] QXap_sQ, int[] ap_lQ, int[] atQ) {
		for (int i = 0; i < ap_lQ.length; i++) {
			QXap_sQ[i] = ap_lQ[i] + atQ[i];
		}
		return QXap_sQ;
	}


	private static double arrMin(double atmin, double[] at_all, int[] atQ_all) {
		for (int i = 1; i < at_all.length; i++) {
			if (atQ_all[i] == 0) {
				if (at_all[i] < atmin) {
					atmin = at_all[i];
				}
			}
	    }
		return atmin;
	}
	
	private static int arrMin(int atmin, int[] at_all, int[] atQ_all) {
		for (int i = 1; i < at_all.length; i++) {
			if (atQ_all[i] == 0) {
				if (at_all[i] < atmin) {
					atmin = at_all[i];
				}
			}
		}
		return atmin;
	}

	private static double arrMax(double atmax, double[] at_all, int[] atQ_all) {
		for (int i = 1; i < at_all.length; i++) {
			if (atQ_all[i] == 0) {
				if (at_all[i] > atmax) {
					atmax = at_all[i];
				}
			}
	    }
		return atmax;
	}
	
	private static int arrMax(int atmax, int[] at_all, int[] atQ_all) {
		for (int i = 1; i < at_all.length; i++) {
			if (atQ_all[i] == 0) {
				if (at_all[i] > atmax) {
					atmax = at_all[i];
				}
			}
		}
		return atmax;
	}


	/**
	 * 数组转移
	 * @param at_all
	 * @param at
	 * @return
	 */
	private static double[] arrTranD(double[] at_all, double[] at) {
		for (int i = 0; i < at.length; i++) {
			at_all[i] = at[i];
		}
		return at_all;
	}
	private static int[] arrTranI(int[] at_all, int[] at) {
		for (int i = 0; i < at.length; i++) {
			at_all[i] = at[i];
		}
		return at_all;
	}



	/**
	 * 数组平均数
	 * @param atavg 带入值计算结果
	 * @param at 数据值
	 * @param atQ 质控服
	 * @return
	 */
	private static double arrMean(double atavg, double[] at, int[] atQ) {
		//atavg = mean(at(atQ==0));
		arrCount = 0;
		for (int i = 0; i < at.length; i++) {
			if (atQ[i] == 0){
				atavg += at[i];
				arrCount++;
			}
	    }
		return Utils.formatDouble2(atavg/arrCount);
	}
	
	private static double arrMean(double atavg, int[] at, int[] atQ) {
		//atavg = mean(at(atQ==0));
		arrCount = 0;
		for (int i = 0; i < at.length; i++) {
			if (atQ[i] == 0){
				atavg += at[i];
				arrCount++;
			}
		}
		return Utils.formatDouble2(atavg/arrCount);
	}



	/**
	 * 替换数组中的值
	 * @param atQ 数组
	 * @param eqNum 如果等于的数值
	 * @param repNum 要替换成的值
	 * @return
	 */
	private static int[] arrQCI(int[] atQ, int eqNum, int repNum) {
		for (int i = 0; i < atQ.length; i++) {
			if(atQ[i] == eqNum){
				atQ[i] = repNum;
			}
		}
		return atQ;
	}
	
	/**
	 * 质量控制方法
	 * @param at 数据
	 * @param atQ 质量符号
	 * @param eqNum 等于值
	 * @param gtNum 大于值
	 * @param repNum 替换值
	 * @return
	 */
	private static int[] arrQCEqGtd(double[] at, int[] atQ, int eqNum, int gtNum, int repNum) {
		for (int i = 0; i < at.length; i++) {
			if(at[i] == eqNum | at[i] > gtNum){
				atQ[i] = repNum;
			}
		}
		return atQ;
	}
	
	
	private static double[] compute(double[] QXap_s, double[] ap_l, double preHd, double[] at) {
        for (int i = 0; i < ap_l.length; i++) {
			QXap_s[i] = Utils.formatDouble2(ap_l[i]
					* Math.exp(0.03415 * preHd / (273+at[i])));
		}
		return QXap_s;
	}
	
	
	/**
	 * 本站气压按照海边观测规范转为海平面气压
	 * @param ap_l 本站气压
	 * @param preH 气压计海拔高速
	 * @param at 气温
	 * @return
	 */
	private static double[] GBap(double[] GBap_s, double[] ap_l, int preH, double[] at,double[] C, double[] M) {
		if (preH < 15) { // C=34.68.*h./(at+273); 
			for (int i = 0; i < at.length; i++) {
				C[i] = 34.68*preH/(at[i]+273);
			}
		} else { // M=(10.^(h./(18400*(1+(at./273))))-1)*1000;
			for (int i = 0; i < at.length; i++) {
				M[i] = Math.pow(10, preH/(18400*(1+(at[i]/273))))*1000;
				C[i] = ap_l[i]* (M[i]/1000);
			}
		}
		//ap = ap_l+C;
		for (int i = 0; i < ap_l.length; i++) {
			GBap_s[i] = Utils.formatDouble2(
					ap_l[i] +C[i]);
		}
		return GBap_s;
	}
	
	/**
	 * 求数组的合
	 * @param num_invalid_at  0带进来求总数
	 * @param atQ 需要求和的数组
	 * @return
	 */
	private static int arrSum(int num_invalid_at, int[] atQ) {
		for (int i = 0; i < atQ.length; i++) {
			num_invalid_at += atQ[i];
	    }
		return num_invalid_at;
	}
	
}
