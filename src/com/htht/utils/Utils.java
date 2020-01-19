package com.htht.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.channels.FileChannel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.metadata.BaseRowModel;
import com.alibaba.fastjson.JSONArray;
import com.htht.entity.RmmEntity;
import com.htht.entity.RmmSheetNo;

/**
 * 宸ュ叿绫�
 * 
 * @author 浜夸竾寮犲瘜缈�
 * 
 */
public class Utils {

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
	private static SimpleDateFormat sdfYYYY = new SimpleDateFormat("yyyy");;
	private static Calendar c1 = Calendar.getInstance();
	private static DecimalFormat df = new DecimalFormat("#.00");
	/**
	 * 鏃ユ湡杞椂闂存埑
	 * @param Date date 鏍煎紡鏁版嵁
	 * @return String String鏍煎紡鏁版嵁
	 */
	public static String dateConvertToStrDate(Date date) throws Exception{
		return sdf.format(date);
	}
	
	/**
	 * 鏃ユ湡杞椂闂存埑
	 * @param String date 骞存湀鏃ユ椂鍒嗙 12浣�(24h)
	 * @return Long 鏃堕棿鎴�
	 */
	public static Long strDateConvertToTimestamp(String date) throws Exception{
		Date d = sdf.parse(date);
		return d.getTime();
	}
	
	/**
	 * 鏃堕棿鎴宠浆鏃ユ湡
	 * @param timestamp
	 * @return String date 骞存湀鏃ユ椂鍒嗙 12浣�(24h)
	 * @throws Exception
	 */
	public static String timestampConvertToStrDate(long timestamp) throws Exception{
		return sdf.format(new Date(timestamp));
	}

	/**
	 * 鑾峰彇uuid 榛樿32浣� 鍘绘帀'-'
	 * 
	 * @return
	 */
	public static String getUUid() {
		return UUID.randomUUID().toString().replace("-", "");
	}

	/**
	 * String杞崲鎴恉ouble
	 * 
	 * @param String
	 *            value
	 * @return double
	 */
	public static double stringConvertsToDouble(String value) {
		BigDecimal b = new BigDecimal(value.trim());
		return b.doubleValue();
	}

	/**
	 * 涓や釜String鐩稿姞
	 * 
	 * @param valueOne
	 *            鍙傛暟1
	 * @param valueTwo
	 *            鍙傛暟2
	 * @return
	 */
	public static double twoStringAdding(String valueOne, String valueTwo) {
		return stringConvertsToDouble(valueOne)
				+ (stringConvertsToDouble(valueTwo));
	}

	/**
	 * 鑾峰緱鏃ユ湡鍔犲ぉ鏁扮殑缁撴灉鏃ユ湡
	 * 
	 * @param strData
	 *            闇�瑕佹槸 eg:"180001010000"
	 * @param countDay
	 * 			鐩稿姞鍑忕殑澶╂暟eg:7
	 * @param flag
	 *            "+" or "-"
	 * @return 璁＄畻鍚庣殑鏃ユ湡String鏍煎紡
	 */
	public static String getDataCountDaysResult(String strDate, int countDay,
			String flag) throws Exception {
		Date parse = sdf.parse(strDate);
		c1.setTime(parse);
		if (flag.equals("+")) {
			c1.add(Calendar.DAY_OF_YEAR, countDay);
			return dateConvertToStrDate(c1.getTime());
		} else if (flag.equals("-")) {
			c1.add(Calendar.DAY_OF_YEAR, -countDay);
			return dateConvertToStrDate(c1.getTime());
		} else {
			return "";
		}

	}

	public static int doubleConvertsToInt(double val) {
		Double d = new Double(val);
		return d.intValue();
	}

	/**
	 * 鑾峰緱杩炴帴姹�
	 * 
	 * @throws Exception
	 */
	public static Connection getConnection(String driverAddress, String url,
			String username, String password) throws Exception {
		// 娉ㄥ唽椹卞姩
		Class.forName(driverAddress);
		// 鑾峰彇鏁版嵁搴撹繛鎺�
		Connection conn = DriverManager.getConnection(url, username, password);
		return conn;
	}

	/**
	 * 鎻愪緵鍏叡鐨勯潤鎬佺殑close鏂规硶
	 * 
	 * @throws Exception
	 */
	public static void close(ResultSet rs, Statement st, Connection conn)
			throws Exception {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				rs = null;// 鎵嬪姩缃┖
			}
		}
		if (st != null) {
			try {
				st.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				st = null;// 鎵嬪姩缃┖
			}
		}
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				conn = null;// 鎵嬪姩缃┖
			}
		}
	}

	/**
	 * 璇诲彇鏂囦欢txt杞簩缁寸煩闃�
	 * 
	 * @param path
	 * @return
	 */
	public static double[][] readTxtToArray(String path) {
		List<List<Double>> conList = new ArrayList<List<Double>>();
		double[][] douline = null;
		try {
			File file = new File(path);
			String encoding = "UTF-8";
			if (file.isFile() && file.exists()) {
				InputStreamReader read = new InputStreamReader(
						new FileInputStream(file), encoding);
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = "";
				int len = 0;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					String[] myArray = lineTxt.trim().replaceAll("\\s+", ",")
							.split(",");
					List<Double> lineList = new ArrayList<Double>();
					for (String str : myArray) {
						lineList.add(Double.parseDouble(str));
					}
					conList.add(lineList);
					len++;
				}
				read.close();
				douline = new double[conList.size()][conList.get(0).size()];
				for (int i = 0; i < conList.size(); i++) {
					for (int j = 0; j < conList.get(i).size(); j++) {
						if (!conList.get(i).isEmpty()
								&& conList.get(i).size() != 0) {
							douline[i][j] = conList.get(i).get(j);
						}
					}
				}
			} else {
				System.out.println("鏂囦欢涓嶅瓨鍦�!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return douline;
	}

	/**
	 * 聽聽聽浣跨敤鏂囦欢閫氶亾鐨勬柟寮忓鍒舵枃浠� 聽聽聽
	 * 聽@param s 聽聽
	 *		聽婧愭枃浠� 聽聽聽
	 * 聽@param t 聽聽聽 聽 聽 聽
	 * 聽 聽 聽澶嶅埗鍒扮殑鏂版枃浠� 
	 *  鍏堝叧绠￠亾锛屽湪鍏虫祦 聽聽聽聽
	 */

	public static void fileChannelCopy(File s, File t) {
		FileInputStream fi = null;
		FileOutputStream fo = null;
		FileChannel in = null;
		FileChannel out = null;
		try {
			fi = new FileInputStream(s);
			fo = new FileOutputStream(t);
			in = fi.getChannel();// 寰楀埌瀵瑰簲鐨勬枃浠堕�氶亾
			out = fo.getChannel();// 寰楀埌瀵瑰簲鐨勬枃浠堕�氶亾
			in.transferTo(0, in.size(), out);// 杩炴帴涓や釜閫氶亾锛屽苟涓斾粠in閫氶亾璇诲彇锛岀劧鍚庡啓鍏ut閫氶亾
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(out!=null){
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(fo!=null){
				try {
					fo.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(in!=null){
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(fi!=null){
				try {
					fi.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	/**
     * 
     * @Method: getStrFromInputSteam  
     * @Description: (璇诲彇鏂囦欢杞崲鎴怱tring瀛楃涓�)
     * @param in InputStream娴�
     * @return
     * @throws Exception    (鍙傛暟鎻忚堪) 
     * String    (杩斿洖绫诲瀷鎻忚堪) 
     */
    public static String getStrFromInputSteam(InputStream in) throws Exception{  
	     BufferedReader bf=new BufferedReader(new InputStreamReader(in,"UTF-8"));  
	     //鏈�濂藉湪灏嗗瓧鑺傛祦杞崲涓哄瓧绗︽祦鐨勬椂鍊� 杩涜杞爜  
	     StringBuffer buffer=new StringBuffer();  
	     String line="";  
	     while((line=bf.readLine())!=null){  
	         buffer.append(line);  
	     }  
	    return buffer.toString();  
	}
    
    /**
     * 閬嶅巻鏌ョ湅鏄惁鍖呭惈鏌愪竴鍏冪礌
     * @param array 
     * 				jsonArray瀵硅薄
     * @param targetValue 
     * 				鐩爣瀵硅薄
     * @return 鍖呭惈杩斿洖true 涓嶅寘鍚繑鍥瀎alse
     */
    public static boolean useLoop(JSONArray array, String targetValue) {
        for (Object s : array) {
            if (s.equals(targetValue))
                return true;
        }
        return false;
    }
    
    /**
     * 鍒犻櫎鏂囦欢澶�
     * @param folderPath
     */
    public static void delFolder(String folderPath) {  
        try {  
           delAllFile(folderPath); //鍒犻櫎瀹岄噷闈㈡墍鏈夊唴瀹�  
           String filePath = folderPath;  
           filePath = filePath.toString();  
           java.io.File myFilePath = new java.io.File(filePath);  
           myFilePath.delete(); //鍒犻櫎绌烘枃浠跺す  
        } catch (Exception e) {  
          e.printStackTrace();   
        }  
   }  
    
    /**
     * 鍒犻櫎鎸囧畾鏂囦欢澶逛笅鐨勬墍鏈夋枃浠�
     * @param path
     * @return
     */
    public static boolean delAllFile(String path) {  
        boolean flag = false;  
        File file = new File(path);  
        if (!file.exists()) {  
          return flag;  
        }  
        if (!file.isDirectory()) {  
          return flag;  
        }  
        String[] tempList = file.list();  
        File temp = null;  
        for (int i = 0; i < tempList.length; i++) {  
           if (path.endsWith(File.separator)) {  
              temp = new File(path + tempList[i]);  
           } else {  
               temp = new File(path + File.separator + tempList[i]);  
           }  
           if (temp.isFile()) {  
              temp.delete();  
           }  
           if (temp.isDirectory()) {  
              delAllFile(path + "/" + tempList[i]);//鍏堝垹闄ゆ枃浠跺す閲岄潰鐨勬枃浠�  
              delFolder(path + "/" + tempList[i]);//鍐嶅垹闄ょ┖鏂囦欢澶�  
              flag = true;  
           }  
        }  
        return flag;  
      }  
    
    /**
     * 鏃ユ湡鏍煎紡杞崲
     * long fileLM杞崲鎴愭棩鏈熸牸寮忓瓧绗︿覆 stirng
     * @return
     */
    public static String strDateTraNormStrDate(String val) throws Exception {
    	String rest = "";
    	// 濡傛灉绛変簬8浣嶈嚜鍔ㄨˉ榻� 鏃跺垎绉�
    	if (val.length() == 8) {
    		rest = val.substring(0, 4)+"-"+val.substring(4, 6)+"-"+val.substring(6, 8)
    				+" 00:00:00";
    	} else {
    		rest = val.substring(0, 4)+"-"+val.substring(4, 6)+"-"+val.substring(6, 8)+" "+
        			val.substring(8, 10);
    		if (val.length()>=12) {
    			rest += ":"+val.substring(10, 12);
    		} else {
    			rest += ":00";
    		}
    		if (val.length()>=14) {
        		rest += ":"+val.substring(12, 14);
        	} else {// 绉掍笉澶熷瓧绗︿覆鎷煎噾
        		rest += ":00";
        	}
    	}
    	return rest;
    }
    
    /**
     * 鏃ユ湡鏍煎紡杞崲
     * long fileLM杞崲鎴愭棩鏈熸牸寮忓瓧绗︿覆 stirng
     * @return
     */
    public static String strDateTraNormStrDateYYYYMMDD(String val) throws Exception {
    	if (val.length() == 8) {
    		return  val.substring(0, 4)+"-"+val.substring(4, 6)+"-"+val.substring(6, 8);
    	}
    	return "";
    }
    
    	
    
    /**
     * 缁忕含搴﹁浆鎹�
     * @parent 119掳17.67鈥睧 
     *  to
     * @return 119.2945
     */
    public static double latLonTransfer(String val) throws Exception{
    	if (val.contains("掳")){
    		String[] split = val.replace("鈥�", "掳").split("掳");
        	return formatDouble2(stringConvertsToDouble(split[0])+((Utils.stringConvertsToDouble(split[1])/60)));
    	} else {//濡傛灉涓嶆槸瑙勫畾鏍煎紡锛岃繑鍥炲師鏁扮殑double绫诲瀷
    		return stringConvertsToDouble(val);
    	}
    }
    
    /**
     * 淇濈暀涓や綅灏忔暟
     * @param d
     * @return double
     */
    public static double formatDouble2(double d) {
        return Utils.stringConvertsToDouble(df.format(d));
    }
    
    /**
     * 淇濈暀涓や綅灏忔暟
     * @param d
     * @return double
     */
    public static String formatString2(double d) {
    	return df.format(d);
    }

    /**
     * 璇诲彇缁熻鍒嗘瀽xml淇℃伅杩斿洖map闆嗗悎
     */
	public static Map readXmlFileMsg(String xmlPath) throws Exception{
		File inputXml = new File(xmlPath);
		SAXReader saxReader = null;
		Map<String,String> map = new HashMap<String,String>();
		saxReader = new SAXReader();
		Document document = saxReader.read(inputXml);
		Element root = document.getRootElement();
		Element query = root.element("query");
		List<Element> columns = query.elements("column");
		for (Element column : columns) {
			List<Element> column_values = column.elements();
			String column_name = "";// value
			String column_desc = "";// 鎻忚堪
			// xml column涓嬪睘鎬у悕绉伴兘鏄笁涓� 鍙� 0 涓� column_name 1 涓� column_desc
			for (int j = 0; j < column_values.size(); j++) {
				column_name = column_values.get(0).getTextTrim();
				column_desc = column_values.get(1).getTextTrim();
			}
			if (column_name.contains("$")) { // 鍖呭惈鈥�$鈥濊〃绀烘棤鍊� 涓嶅叆闆嗗悎
				continue;
			}
			map.put(column_desc, column_name);	
		}
		return map;
	}
	
	/**
	 * 璇诲彇excel淇℃伅 鎸囧畾瀹炰綋绫�
	 */
	public static List<Object> readExcelMsg(String excelPath, Class<?> clazz, int sheetNo) throws Exception {
		return EasyExcel.read(excelPath).head(clazz)
        		.sheet(sheetNo).doReadSync();
	}
	
	public static Integer SUM(Integer[] arr) {
		int sum = 0;
		for(int i:arr) {
			sum+=i;
		}
		
		return sum;
	}
	public static Double MAX(List list) {
		Double MAX = 0.0;
		for(Object i:list) {
			if(Double.parseDouble(String.valueOf(i))>MAX)
				MAX=Double.parseDouble(String.valueOf(i));
		}
		
		return MAX;
	}
	public static Double MIN(List<Double> list) {
		// TODO Auto-generated method stub
		list.sort(new Comparator<Double>() {

			@Override
			public int compare(Double o1, Double o2) {
				// TODO Auto-generated method stub
				if(o1.compareTo((double) 02)==0)
					return 0;
				else if(o1.compareTo((double) 02)==1)
					return 1;
				else if(o1.compareTo((double) 02)==-1)
					return -1;
				return 0;
			}
		});
		List l = list;
		return (Double) l.get(0);
	}
	public static Double MEAN(List list) {
		int sum = 0;
		
		
		for(Object i:list) {
			sum+=Double.parseDouble(i.toString());
		}
		
		return list.size()==0?0:(double)sum/list.size();
	}
	//
	public static List<Double> getEqualsArray(Integer[] arr1,Double[] arr2,Integer num) {
		List<Double> list = new ArrayList<Double>();
		System.out.println(Arrays.toString(arr1)+"---"+Arrays.toString(arr2));
		for (int i = 0; i < arr1.length; i++) {
			
			if(arr1[i]!=null&&arr1[i].compareTo(num)==0)
				list.add(arr2[i]);
		}
		
		return list;
		
	}
	//
	public static List<Integer> getEqualsArray(Double[] arr1,Integer[] arr2,Double num) {
		List<Integer> list = new ArrayList<Integer>();
		System.out.println(Arrays.toString(arr1)+"---"+Arrays.toString(arr2));
		for (int i = 0; i < arr1.length; i++) {
			
			if(arr1[i]!=null&&arr1[i].compareTo(num)==0)
				list.add(arr2[i]);
		}
		
		return list;
		
	}
	
	public static List<String> getEqualsArray(Double[] arr1,String[] arr2,Double num) {
		List<String> list = new ArrayList<String>();
		System.out.println(Arrays.toString(arr1)+"---"+Arrays.toString(arr2));
		for (int i = 0; i < arr1.length; i++) {
			
			if(arr1[i]!=null&&arr1[i].compareTo(num)==0)
				list.add(arr2[i]);
		}
		
		return list;
		
	}
	public static List<Double> getEqualsArray(Double[] arr1,Double[] arr2,Double num) {
		List<Double> list = new ArrayList<Double>();
		System.out.println(Arrays.toString(arr1)+"---"+Arrays.toString(arr2));
		for (int i = 0; i < arr1.length; i++) {
			
			if(arr1[i]!=null&&arr1[i].compareTo(num)==0)
				list.add(arr2[i]);
		}
		
		return list;
		
	}
	public static List<Double> getEqualsZeroArrayAndBigZero(Integer[] arr1,Double[] arr2) {
		List<Double> list = new ArrayList<Double>();
		System.out.println(Arrays.toString(arr1)+"---"+Arrays.toString(arr2));
		for (int i = 0; i < arr1.length; i++) {
			
			if(arr1[i]!=null&&arr1[i].compareTo(0)==0)
				if(arr2[i].compareTo(Double.parseDouble("0"))>0)
				list.add(arr2[i]);
		}
		
		return list;
		
	}
	
	public static String extremtime(List list) {
		// TODO Auto-generated method stub
		List l = new ArrayList<String>();
		int num=0;
		if(list.size()>1) {
			
			
			for(Object s:list.toArray())
			{
				String str = String.valueOf(s);
				if(str.length()<6)
					num+=Integer.parseInt(str);
				else
					l.add(str);
			}
		}
		if(l.size()==0)
			return String.valueOf(num);
		else {
			String str ="";
			Object[] os = list.toArray();
			for(int i=0;i<os.length;i++)
			{
				str+=i==os.length-1?String.valueOf(os[i]):String.valueOf(os[i])+",";
			}
			num=num+os.length;
			if(num>=3)
				return String.valueOf(num);
			else
				return str;
		}
	}
	public static Double[] addALL(Double[] a,Double[] b) {
		Double[] ds = new Double[a.length+b.length];
		for(int i=0;i<a.length;i++)
		{
			ds[i] = a[i];
		}
		for(int j=0;j<b.length;j++)
			ds[a.length-1+j]=b[j];
		
		return ds;
	}
	public static Integer[] addALL(Integer[] a,Integer[] b) {
		Integer[] is = new Integer[a.length+b.length];
		for(int i=0;i<a.length;i++)
		{
			is[i] = a[i];
		}
		for(int j=0;j<b.length;j++)
			is[a.length-1+j]=b[j];
		
		return is;
		
	}
	public static String[] addALL(String[] a,String[] b) {
		String[] is = new String[a.length+b.length];
		for(int i=0;i<a.length;i++)
		{
			is[i] = a[i];
		}
		for(int j=0;j<b.length;j++)
			is[a.length-1+j]=b[j];
		
		return is;
		
	}
	


	
} 
