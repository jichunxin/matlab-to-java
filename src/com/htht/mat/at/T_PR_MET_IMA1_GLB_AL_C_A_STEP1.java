package com.htht.mat.at;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.htht.entity.RmmEntity;
import com.htht.entity.RmmSheetNo;
import com.htht.utils.Utils;

/**
 * 大面气象-温湿压-数据清洗
 * @author Administrator
 *
 */
public class T_PR_MET_IMA1_GLB_AL_C_A_STEP1 {

	public static void main(String[] args) {
		/*String xmlPath = "";
		if (args.length > 0 ) {
			if (StringUtils.isBlank(args[0])) {
				try {
					xmlPath = args[0].trim();
				} catch (NumberFormatException e1) {
					System.out.println("输入参数异常:"+args[0]);
					e1.printStackTrace();
					System.exit(0);// 程序退出
				}
			} else {
				System.out.println("参数为空！期待输入xml路径。");
				System.exit(0);// 程序退出
			}
		} else {
			System.out.println("无参数输入！期待输入xml路径。");
			System.exit(0);// 程序退出
		}*/
		String xmlPath = "C:/Users/Administrator/Desktop/2019101515294188302.xml";
		try {
			// 读取xml属性 返回map集合
			Map<String,String> map = Utils.readXmlFileMsg(xmlPath);
			String f_uesrId = map.get("F_USERID")!=null?map.get("F_USERID").toString():"";
			checkParam(f_uesrId, "F_USERID");
			String f_orderId = map.get("F_ORDERID")!=null?map.get("F_ORDERID").toString():"";
			checkParam(f_orderId, "F_ORDERID");
			System.out.println("当前用户ID:" + f_uesrId);
			System.out.println("当前任务ID:" + f_orderId);
			//String rmmFilePath = map.get("F_PATH").toString() + "RMM.xlsx";
			String rmmFilePath = "C:/Users/Administrator/Desktop/RMM.xlsx";
			// 读取excel RMM文件
			List<Object> eleResult = Utils.readExcelMsg(rmmFilePath, RmmEntity.class, RmmSheetNo.RMM_F_ELE);
			String f_ele = map.get("F_ELE")!=null?map.get("F_ELE").toString():"";
			checkParam(f_ele, "F_ELE");
			// 要素名称匹配
			String feleName = feleMatch(f_ele, eleResult);
			System.out.println("要素:" + feleName);
			
			// 经纬度阀值过滤
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}

	private static void checkParam(String fele, String paramName) {
		if (StringUtils.isBlank(fele)) {
			throw new RuntimeException(paramName+"-参数为空。运行出错");
		}
	}

	/**
	 * 要素名称匹配
	 * @param eleResult
	 * @return
	 */
	private static String feleMatch(String f_ele, List<Object> eleResult) {
		String eleName = "";
		for (Object eleObj : eleResult) {
			RmmEntity ele = (RmmEntity)eleObj;
			if (f_ele.equals(ele.getRow1())) {
				eleName = ele.getRow2();
			}
		}
		checkParam(eleName, "要素名匹配结果");
		return eleName;
	}
}
