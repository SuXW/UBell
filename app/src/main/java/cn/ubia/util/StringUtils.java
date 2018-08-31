/*
 * 文 件 名:  StringUtil.java
 * 版    权:  Huawei Technologies Co., Ltd. Copyright YYYY-YYYY,  All rights reserved
 * 描    述:  <描述>
 * 修 改 人:  cKF46828
 * 修改时间:  2011-7-5
 * 跟踪单号:  <跟踪单号>
 * 修改单号:  <修改单号>
 * 修改内容:  <修改内容>
 */

package cn.ubia.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;
/**
 * <字符串操作>
 * 
 * @author cKF46828
 * @version [版本号, 2011-7-5]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public final class StringUtils
{
    private static final String TAG = "StringUtils";
    
    /**
     * 生成oid的最后随机数的长度
     */
    private static final int OID_RANDOM_LENGTH = 5;
    
    private StringUtils()
    {
    }
    
    /**
     * 是否为null或空字符串
     * 
     * @param str 字符串
     * @return boolean [返回类型说明]
     * @see [类、类#方法、类#成员]
     */
    public static boolean isEmpty(String str)
    {
        if (str == null || "".equals(str.trim()))
        {
            return true;
        }
        return false;
    }
    
    /** 包含 null 字符串的为空判断 
     * @param str 字符串
     * @return boolean
     * */
    public static boolean isNEmpty(String str)
    {
        return isEmpty(str)
            || (isNotEmpty(str) && "null".equals(str.trim().toLowerCase()));
    }
    
    /**
     * 非null或非空
     * 
     * @param str 字符串
     * @return boolean
     */
    public static boolean isNotEmpty(String str)
    {
        return !isEmpty(str);
    }
    
    /**
     * 用于判断输入框输入是否是中文
     * @param str 字符串
     * @return boolean
     */
    public static boolean isCN(String str)
    {
        try
        {
            byte[] bytes = str.getBytes("UTF-8");
            if (bytes.length == str.length())
            {
                return false;
            }
            else
            {
                return true;
            }
        }
        catch (UnsupportedEncodingException e)
        {
        	
        }
        return false;
    }
    
    /**
     * <判断是否为手机号码>
     * 
     * @param phoneNumber 手机号
     * @return boolean [返回类型说明]
     * @see [类、类#方法、类#成员]
     */
    public static boolean isPhoneNumber(String phoneNumber)
    {
        if (phoneNumber == null)
        {
            return false;
        }
        String reg = "1[3,4,5,7,8]{1}\\d{9}";
        return phoneNumber.matches(reg);
    }
    
    /**
     * <判断是否是数字>
     * 
     * @param str 字符串
     * @return boolean [返回类型说明]
     * @see [类、类#方法、类#成员]
     */
    public static boolean isNumber(String str)
    {
        String reg = "[0-9]+";
        return str.matches(reg);
    }
    
    /**
     * 是否是邮件
     * @param str 字符串
     * @return boolean
     */
    public static boolean isEmail(String str)
    {
        Pattern pattern =
            Pattern.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }
    
    /**
     * 字符串转为整数(如果转换失败,则返回 -1)
     * 
     * @param str  字符串
     * @return int [返回类型说明]
     * @see [类、类#方法、类#成员]
     */
    public static int stringToInt(String str)
    {
        if (isEmpty(str))
        {
            return -1;
        }
        try
        {
            return Integer.parseInt(str.trim());
        }
        catch (NumberFormatException e)
        {
            return -1;
        }
    }
    
    /**
     * 字符串转为整数(如果转换失败,则返回 defaultValue)
     * 
     * @param str 字符串
     * @param defaultValue 默认值
     * @return int [返回类型说明]
     * @see [类、类#方法、类#成员]
     */
    public static int stringToInt(String str, int defaultValue)
    {
        if (isEmpty(str))
        {
            return defaultValue;
        }
        try
        {
            return Integer.parseInt(str.trim());
        }
        catch (NumberFormatException e)
        {
            return defaultValue;
        }
    }
    
    /**
     * <字符串转为long(如果转换失败,则返回 -1)> <功能详细描述>
     * 
     * @param str 字符串转
     * @return long [返回类型说明]
     * @see [类、类#方法、类#成员]
     */
    public static long stringToLong(String str)
    {
        if (isEmpty(str))
        {
            return -1;
        }
        try
        {
            return Long.parseLong(str.trim());
        }
        catch (NumberFormatException e)
        {
            return -1;
        }
    }
    
    public static double stringToDouble(String str)
    {
        if (isEmpty(str))
        {
            return -1;
        }
        try
        {
            return Double.parseDouble(str.trim());
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
            return -1;
        }
    }
    
    /**
     * 字体串转为boolean (如果转换失败,则返回false)
     * 
     * @param str 字符串转
     * @return boolean [返回类型说明]
     * @see [类、类#方法、类#成员]
     */
    public static boolean stringToBoolean(String str)
    {
        if (isEmpty(str))
        {
            return false;
        }
        try
        {
            return Boolean.parseBoolean(str.trim());
        }
        catch (Exception e)
        {
            return false;
        }
    }
    
    /**
     * boolean转为字体串
     * 
     * @param bool boolean
     * @return boolean [返回类型说明]
     * @see [类、类#方法、类#成员]
     */
    public static String booleanToString(Boolean bool)
    {
        String booleanString = "false";
        if (bool)
        {
            booleanString = "true";
        }
        return booleanString;
    }
    
    /**
     * <从异常中获取调用栈>
     * 
     * @param ex Throwable
     * @return String [返回类型说明]
     * @see [类、类#方法、类#成员]
     */
    public static String getExceptionStackTrace(Throwable ex)
    {
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null)
        {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        return writer.toString();
    }
    
    /**
     * <Unicode转化为中文>
     * 
     * @param dataStr Unicod字体串
     * @return String [返回类型说明]
     * @see [类、类#方法、类#成员]
     */
    public static String decodeUnicode(String dataStr)
    {
        final StringBuffer buffer = new StringBuffer();
        String tempStr = "";
        String operStr = dataStr;
        if (operStr != null && operStr.indexOf("\\u") == -1)
        {
            return buffer.append(operStr).toString();
        }
        if (operStr != null && !operStr.equals("")
            && !operStr.startsWith("\\u"))
        {
            tempStr = StringUtils.substring(operStr, 0, operStr.indexOf("\\u"));
            operStr =
                StringUtils.substring(operStr,
                    operStr.indexOf("\\u"),
                    operStr.length());
        }
        buffer.append(tempStr);
        // 循环处理,处理对象一定是以unicode编码字符打头的字符串
        while (operStr != null && !operStr.equals("")
            && operStr.startsWith("\\u"))
        {
            tempStr = StringUtils.substring(operStr, 0, 6);
            operStr = StringUtils.substring(operStr, 6, operStr.length());
            String charStr = "";
            charStr = StringUtils.substring(tempStr, 2, tempStr.length());
            char letter = (char) Integer.parseInt(charStr, 16); // 16进制parse整形字符串
            buffer.append(letter);
            if (operStr.indexOf("\\u") == -1)
            {
                buffer.append(operStr);
            }
            else
            { // 处理operStr使其打头字符为unicode字符
                tempStr =
                    StringUtils.substring(operStr, 0, operStr.indexOf("\\u"));
                operStr =
                    StringUtils.substring(operStr,
                        operStr.indexOf("\\u"),
                        operStr.length());
                buffer.append(tempStr);
            }
        }
        return buffer.toString();
    }
    
    /**
     * 字条串截取
     * 
     * @param str
     *            源字符串
     * @param start
     *            开始位置
     * @param end
     *            结束位置
     * @return String [返回类型说明]
     * @see [类、类#方法、类#成员]
     */
    public static String substring(String str, int start, int end)
    {
        if (isEmpty(str))
        {
            return "";
        }
        int len = str.length();
        if (start > end)
        {
            return "";
        }
        if (start > len)
        {
            return "";
        }
        if (end > len)
        {
            return str.substring(start, len);
        }
        return str.substring(start, end);
    }
    
    /**
     * 字条串截取
     * 
     * @param str
     *            源字符串
     * @param start
     *            开始位置
     * @return String [返回类型说明]
     * @see [类、类#方法、类#成员]
     */
    public static String substring(String str, int start)
    {
        if (isEmpty(str))
        {
            return "";
        }
        int len = str.length();
        if (start > len)
        {
            return "";
        }
        return str.substring(start);
    }
    
    /**
     * <将字符串截取为较短的字符串>
     * 
     * @param content 字符串
     * @param length  长度
     * @return CharSequence
     * @see [类、类#方法、类#成员]
     */
    public static String cutString(String content, int length)
    {
        if (StringUtils.isEmpty(content))
        {
            return "";
        }
        if (content.length() <= length)
        {
            return content;
        }
        return content.substring(0, length);
    }
    
    /**
     * <将字符串多空格，多换行替换成一个空格> <功能详细描述>
     * 
     * @param content 字符串
     * @return String [返回类型说明]
     * @see [类、类#方法、类#成员]
     */
    public static String tirmString(String content)
    {
        if (StringUtils.isEmpty(content))
        {
            return "";
        }
        
        return content.replaceAll("[ \n\r\t]+", " ");
    }
    
    /**
     * 判断字符是否数字
     * 
     * @param str 字符
     * @return 数字
     */
    public static boolean isDigital(String str)
    {
        return str.matches("(-)?\\d+");
    }
    
    /**
     * 判断字符是否带小数
     * 
     * @param str 字符
     * @return boolean
     */
    public static boolean isDouble(String str)
    {
        if (isDigital(str))
        {
            return true;
        }
        return str.matches("(-)?\\d+\\.\\d+");
    }
    
    /**
     * 判断是否存在自定特殊字符
     * 
     * @param str 字符
     * @return boolean [返回类型说明]
     * @see [类、类#方法、类#成员]
     */
    public static boolean isErrorCodeStr(String str)
    {
        // 正则表达匹配是否有特殊字符
        // Pattern p = Pattern.compile(Common.STR_CREATE_CLOUD_FILE_ERROR);
        // Matcher m = p.matcher(str);
        //
        // return m.find();
        
        // "\\", "/", ":", "*", "?", "\"", "<", ">", "|"
        boolean b1 = str.contains("\\") || str.contains("/") || str.contains(":");
        boolean b2 = str.contains("*") || str.contains("?") || str.contains("\"");
        boolean b3 = str.contains("<") || str.contains(">") || str.contains("|");
        return b1
            || b2
            || b3;
    }
    
    /**
     * 返回x小数,如果小数部分不够两位则自动填充小数部分
     * 
     * @param process 字符
     * @return [参数说明]
     * @see [类、类#方法、类#成员]
     */
    public static String getProcess(String process)
    {
        // 空字符
        if (null == process || "".equals(process.trim()))
        {
            return "";
        }
        
        // 非整数或小数
        if (!(isDigital(process) || isDouble(process)))
        {
            return process;
        }
        
        int index = process.indexOf('.');
        
        // 无小数部分
        if (-1 == index)
        {
            return process + ".00";
        }
        
        // 整数部分
        String prefix = process.substring(0, index);
        
        // 小数部分
        String postfix = process.substring(index + 1);
        
        StringBuilder result = new StringBuilder();
        
        // 小数部分长度
        switch (postfix.length())
        {
        // 无小数部分
            case 0:
                result.append(prefix).append(".00");
                break;
            // 只有一位小数
            case 1:
                result.append(prefix).append('.').append(postfix).append('0');
                break;
            // 两位小数
            case 2:
                result.append(prefix).append('.').append(postfix);
                break;
            // 三位或以上小数,需要进行四舍五入
            default:
                result.append(String.valueOf(Math.round(Double.parseDouble(prefix
                    + postfix.substring(0, 3)) / 10)))
                    .insert(result.length() - 2, '.');
                break;
        }
        return result.toString();
    }
    
    /**
     * 字符串转数字
     * 
     * @param str
     *            字符串
     * @param defualtValue
     *            自定义整型
     * @return 整型
     */
    public static int getInt(String str, int defualtValue)
    {
        return isDigital(str) ? Integer.parseInt(str) : defualtValue;
    }
    
    /**
     * 去掉url中多余的斜杠
     * 
     * @param url
     *            字符串
     * @return 去掉多余斜杠的字符串
     */
    public static String fixUrl(String url)
    {
        if (null == url)
        {
            return "";
        }
        StringBuffer stringBuffer = new StringBuffer(url);
        for (int i = stringBuffer.indexOf("//", stringBuffer.indexOf("//") + 2); i != -1; i =
            stringBuffer.indexOf("//", i + 1))
        {
            stringBuffer.deleteCharAt(i);
        }
        return stringBuffer.toString();
    }
    
    /**
     * 替换字符串中特殊字符
     * 
     * @param strData
     *            源字符串
     * @return 替换了特殊字符后的字符串，如果strData为NULL，则返回空字符串
     */
    public static String encodeString(String strData)
    {
        if (strData == null)
        {
            return "";
        }
        return strData.replaceAll("&", "&amp;")
            .replaceAll("<", "&lt;")
            .replaceAll(">", "&gt;")
            .replaceAll("'", "&apos;")
            .replaceAll("\"", "&quot;");
    }
    
    /**
     * 获取记录唯一ID
     * 
     * @return String
     */
    public static String getOID()
    {
        return System.currentTimeMillis() + getRadomNum(OID_RANDOM_LENGTH);
    }
    
    /**
     * 返回特定长度的随机数
     * 
     * @param length
     *            返回长度
     * @return
     */
    private static String getRadomNum(int length)
    {
        length = (length > 0) ? length : 10;
        StringBuffer sRand = new StringBuffer();
        for (int i = 0; i < length; i++)
        {
            sRand.append((int) (Math.random() * 10));
        }
        return sRand.toString();
    }
    
    /**
     * 获取指定编码字符串的长度
     * 
     * @param str 字符串
     * @param encoding 编码
     * @return int
     */
    public static int getStringLength(String str, String encoding)
    {
        if (isEmpty(str))
        {
            return 0;
        }
        try
        {
            return str.getBytes(encoding).length;
        }
        catch (UnsupportedEncodingException e)
        {
            return -1;
        }
    }
    
    // private static int getStringByteLen(String src, String encoding) {
    // try {
    // return src.getBytes(encoding).length;
    // } catch (UnsupportedEncodingException e) {
    // Logger.e(TAG, "获取字符串长度时异常!", e);
    // return src.getBytes().length;
    // }
    // }
    
    // /**
    // * 以指定的编码格式，获取String的Byte长度
    // *
    // * @param src 源字符串
    // * @param encoding 编码格式
    // * @param limitByteLen
    // * @return
    // */
    // public static String[] splitStringByByteLength(String src, String
    // encoding, int limitByteLen) {
    // if (isEmpty(src)) {
    // // 如果源串为空，则直接返回一个仅包含一个空串的数组
    // return new String[] { "" };
    // }
    //
    // ArrayList<String> strArray = new ArrayList<String>();
    // String sourceString = new String(src);
    // int subStrLen = getLimitedByteLen(sourceString, encoding, limitByteLen);
    // int srcStrLen = src.length();
    // while (subStrLen < srcStrLen) {
    // // 将头部字段存储到数组中
    // strArray.add(sourceString.substring(0, subStrLen));
    //
    // // 将字符串重新截取
    // sourceString = sourceString.substring(subStrLen);
    //
    // // 重新计算长度
    // subStrLen = getLimitedByteLen(sourceString, encoding, limitByteLen);
    // srcStrLen = sourceString.length();
    // }
    //
    // strArray.add(sourceString);
    //
    // return (String[])strArray.toArray();
    // }
    
    // private static int getLimitedByteLen(String src, String encoding, int
    // limitedLen) {
    // int strLen = getStringByteLen(src, encoding);
    //
    // if (strLen <= limitedLen) {
    // // 如果字符串长度小于需要拆分的Byte长度，直接返回
    // return src.length();
    // }
    //
    // // 初始设定一个Utf-8字符串为3字节
    // int subLen = limitedLen / 3;
    //
    // while (true) {
    // String subStr = src.substring(0, subLen);
    // int tmpByteLen = getStringByteLen(subStr, encoding);
    //
    // if (tmpByteLen < limitedLen - 4) {
    // // 截取的长度小于限制长度，则判断一下相差值，如果值差大，则再重新进行一次对比。否则直接使用此值。
    // int distance = (limitedLen - tmpByteLen) / 3;
    // subLen += distance;
    // } else if (tmpByteLen > limitedLen) {
    // // 截取的串长于限制长度，需要再次截取
    // subLen -= (tmpByteLen - limitedLen) / 3 + 1;
    // } else {
    // return subLen;
    // }
    // }
    // }
    
    /**
     * 判断相同
     * @param str0 字符串
     * @param str1 字符串
     * @return boolean
     */
    public static boolean areStringEqual(String str0, String str1)
    {
        // if(str0!=null){
        // return str0.equals(str1);
        // }
        // return str1==null || str1.length()==0;
        
        if (str0 == null || str0.length() == 0)
        {
            return str1 == null || str1.length() == 0;
        }
        else
        {
            return str0.equals(str1);
        }
    }
    
    /**
     * 隐藏字符串中间过长的部分
     * 
     * @param src
     *            原字符串
     * @param maxLength
     *            最大长度
     * @param endLength
     *            结尾长度
     * @return 隐藏后的字符串
     */
    public static String hideMiddleString(String src, int maxLength,
        int endLength)
    {
        if (src == null)
        {
        	return null;
        }
        
        String ellipsis = "...";
        if (maxLength <= endLength + ellipsis.length())
        {
            // Logger.w(TAG,
            // "getMiddleString, maxLength <= endLength + ellipsis.length(), maxLength = "
            // + maxLength + "; endLength = " + endLength
            // + "; ellipsis.length() = " + ellipsis.length());
            return src;
        }
        if (src.length() <= maxLength)
        {
            // Logger.w(TAG,
            // "getMiddleString, src.length() <= maxLength,src.length() = "
            // + src.length() + " maxLength = " + maxLength);
            return src;
        }
        
        // 截取后半段
        String endStr = src.substring(src.length() - endLength);
        
        // 截取前半段
        String startStr =
            src.substring(0, maxLength - endLength - ellipsis.length());
        return startStr + ellipsis + endStr;
    }
    
    /***
     * 字符串的长度是否某个区间
     * @param str 字符串
     * @param minLength 最小长度
     * @param maxLength 最大长度
     * @return boolean
     */
    public static boolean isStringLengthOk(String str, int minLength,
        int maxLength)
    {
        if (!isEmpty(str))
        {
            int length = str.length();
            if (length >= minLength && length <= maxLength)
            {
                return true;
            }
        }
        return false;
    }
    
    /**
     * @Method Name: replaceStr
     * @Description: 指定处理
     * @param str
     * @param findStr
     * @param replaceStr
     * @return
     */
    public static String replaceStr(String str, String findStr, String replaceStr){
    	return str.replace(findStr, replaceStr).toUpperCase();
    }
    /**
     * @Method Name: getHex
     * @Description: 字节数组转字符串16进制处理
     * @param  
     * @param  
     * @param  
     * @return
     */
    public static String getHex(byte raw[], int size)
    {
        if(raw == null)
            return null;
        StringBuilder hex = new StringBuilder(2 * raw.length);
        int len = 0;
        byte abyte0[];
        int j = (abyte0 = raw).length;
        for(int i = 0; i < j; i++)
        {
            byte b = abyte0[i];
            hex.append("0123456789ABCDEF".charAt((b & 0xf0) >> 4)).append("0123456789ABCDEF".charAt(b & 0xf)).append(" ");
            if(++len >= size)
                break;
        }

        return hex.toString();
    }
    
    public static String getStringFromByte(byte bytestring[]) {
	//String cUserName = null;
	String cUserName =null;
	try {
		cUserName = new String(bytestring,"UTF-8");
		if(cUserName.indexOf("\0")>0)
		cUserName =cUserName.substring(0,cUserName.indexOf("\0")); 
	} catch (UnsupportedEncodingException e) {
		e.printStackTrace();
	}
	return cUserName.trim();
//	try {
//	     
//		int len = indexofZero(bytestring);
//
//		cUserName = new String(bytestring, 0, len, "UTF-8"); 
// 
//	} catch (Exception e) {
//	    e.printStackTrace();
//	}
//	return cUserName.trim();
    }
    public static String getCurrentLocaltionISOCountryCodeString(int index)
{
        String  arrayCode[]     = {"CN",
                         "AX",
                         "AF",
                         "AL",
                         "DZ",
                         "AS",
                         "AD",
                         "AO",
                         "AI",
                         "AQ",
                         "AG",
                         "AR",
                         "AM",
                         "AW",
                         "AU",
                         "AT",
                         "AZ",
                         "BS",
                         "BH",
                         "BD",
                         "BB",
                         "BY",
                         "BE",
                         "BZ",
                         "BJ",
                         "BM",
                         "BT",
                         "BO",
                         "BA",
                         "BW",
                         "BV",
                         "BR",
                         "IO",
                         "BN",
                         "BG",
                         "BF",
                         "BI",
                         "KH",
                         "CM",
                         "CA",
                         "CV",
                         "KY",
                         "CF",
                         "TD",
                         "CL",
                         "CN",
                         "CX",
                         "CC",
                         "CO",
                         "KM",
                         "CD",
                         "CG",
                         "CK",
                         "CR",
                         "CI",
                         "HR",
                         "CU",
                         "CY",
                         "CZ",
                         "DK",
                         "DJ",
                         "DM",
                         "DO",
                         "EC",
                         "EG",
                         "SV",
                         "GQ",
                         "ER",
                         "EE",
                         "ET",
                         "FK",
                         "FO",
                         "FJ",
                         "FI",
                         "FR",
                         "GF",
                         "PF",
                         "TF",
                         "GA",
                         "GM",
                         "GE",
                         "DE",
                         "GH",
                         "GI",
                         "GR",
                         "GL",
                         "GD",
                         "GP",
                         "GU",
                         "GT",
                         "GN",
                         "GW",
                         "GY",
                         "HT",
                         "HM",
                         "HN",
                         "HK",
                         "HU",
                         "IS",
                         "IN",
                         "ID",
                         "IR",
                         "IQ",
                         "IE",
                         "IL",
                         "IT",
                         "JM",
                         "JP",
                         "JO",
                         "KZ",
                         "KE",
                         "KI",
                         "KP",
                         "KR",
                         "KW",
                         "KG",
                         "LA",
                         "LV",
                         "LB",
                         "LS",
                         "LR",
                         "LY",
                         "LI",
                         "LT",
                         "LU",
                         "MO",
                         "MK",
                         "MG",
                         "MW",
                         "MY",
                         "MV",
                         "ML",
                         "MT",
                         "MH",
                         "MQ",
                         "MR",
                         "MU",
                         "YT",
                         "MX",
                         "FM",
                         "MD",
                         "MC",
                         "MN",
                         "MS",
                         "MA",
                         "MZ",
                         "MM",
                         "NA",
                         "NR",
                         "NP",
                         "NL",
                         "AN",
                         "NC",
                         "NZ",
                         "NI",
                         "NE",
                         "NG",
                         "NU",
                         "NF",
                         "MP",
                         "NO",
                         "OM",
                         "PK",
                         "PW",
                         "PS",
                         "PA",
                         "PG",
                         "PY",
                         "PE",
                         "PH",
                         "PN",
                         "PL",
                         "PT",
                         "PR",
                         "QA",
                         "RE",
                         "RO",
                         "RU",
                         "RW",
                         "SH",
                         "KN",
                         "LC",
                         "PM",
                         "VC",
                         "WS",
                         "SM",
                         "ST",
                         "SA",
                         "SN",
                         "CS",
                         "SC",
                         "SL",
                         "SG",
                         "SK",
                         "SI",
                         "SB",
                         "SO",
                         "ZA",
                         "GS",
                         "ES",
                         "LK",
                         "SD",
                         "SR",
                         "SJ",
                         "SZ",
                         "SE",
                         "CH",
                         "SY",
                         "TW",
                         "TJ",
                         "TZ",
                         "TH",
                         "TL",
                         "TG",
                         "TK",
                         "TO",
                         "TT",
                         "TN",
                         "TR",
                         "TM",
                         "TC",
                         "TV",
                         "UG",
                         "UA",
                         "AE",
                         "GB",
                         "US",
                         "UM",
                         "UY",
                         "UZ",
                         "VU",
                         "VA",
                         "VE",
                         "VN",
                         "VG",
                         "VI",
                         "WF",
                         "EH",
                         "YE",
                         "ZM",
                         "ZW"};
    Log.e("","国家码 111 strNumber ====  "+ index );
      if(index<arrayCode.length){
          return arrayCode[index];
      }else{
          return "CN";
      }
    }
public static int getCurrentLocaltionISOCountryCodeNumber(String currentCode)
{
	    String  arrayCode[]     = {"unknow",
                         "AX",
                         "AF",
                         "AL",
                         "DZ",
                         "AS",
                         "AD",
                         "AO",
                         "AI",
                         "AQ",
                         "AG",
                         "AR",
                         "AM",
                         "AW",
                         "AU",
                         "AT",
                         "AZ",
                         "BS",
                         "BH",
                         "BD",
                         "BB",
                         "BY",
                         "BE",
                         "BZ",
                         "BJ",
                         "BM",
                         "BT",
                         "BO",
                         "BA",
                         "BW",
                         "BV",
                         "BR",
                         "IO",
                         "BN",
                         "BG",
                         "BF",
                         "BI",
                         "KH",
                         "CM",
                         "CA",
                         "CV",
                         "KY",
                         "CF",
                         "TD",
                         "CL",
                         "CN",
                         "CX",
                         "CC",
                         "CO",
                         "KM",
                         "CD",
                         "CG",
                         "CK",
                         "CR",
                         "CI",
                         "HR",
                         "CU",
                         "CY",
                         "CZ",
                         "DK",
                         "DJ",
                         "DM",
                         "DO",
                         "EC",
                         "EG",
                         "SV",
                         "GQ",
                         "ER",
                         "EE",
                         "ET",
                         "FK",
                         "FO",
                         "FJ",
                         "FI",
                         "FR",
                         "GF",
                         "PF",
                         "TF",
                         "GA",
                         "GM",
                         "GE",
                         "DE",
                         "GH",
                         "GI",
                         "GR",
                         "GL",
                         "GD",
                         "GP",
                         "GU",
                         "GT",
                         "GN",
                         "GW",
                         "GY",
                         "HT",
                         "HM",
                         "HN",
                         "HK",
                         "HU",
                         "IS",
                         "IN",
                         "ID",
                         "IR",
                         "IQ",
                         "IE",
                         "IL",
                         "IT",
                         "JM",
                         "JP",
                         "JO",
                         "KZ",
                         "KE",
                         "KI",
                         "KP",
                         "KR",
                         "KW",
                         "KG",
                         "LA",
                         "LV",
                         "LB",
                         "LS",
                         "LR",
                         "LY",
                         "LI",
                         "LT",
                         "LU",
                         "MO",
                         "MK",
                         "MG",
                         "MW",
                         "MY",
                         "MV",
                         "ML",
                         "MT",
                         "MH",
                         "MQ",
                         "MR",
                         "MU",
                         "YT",
                         "MX",
                         "FM",
                         "MD",
                         "MC",
                         "MN",
                         "MS",
                         "MA",
                         "MZ",
                         "MM",
                         "NA",
                         "NR",
                         "NP",
                         "NL",
                         "AN",
                         "NC",
                         "NZ",
                         "NI",
                         "NE",
                         "NG",
                         "NU",
                         "NF",
                         "MP",
                         "NO",
                         "OM",
                         "PK",
                         "PW",
                         "PS",
                         "PA",
                         "PG",
                         "PY",
                         "PE",
                         "PH",
                         "PN",
                         "PL",
                         "PT",
                         "PR",
                         "QA",
                         "RE",
                         "RO",
                         "RU",
                         "RW",
                         "SH",
                         "KN",
                         "LC",
                         "PM",
                         "VC",
                         "WS",
                         "SM",
                         "ST",
                         "SA",
                         "SN",
                         "CS",
                         "SC",
                         "SL",
                         "SG",
                         "SK",
                         "SI",
                         "SB",
                         "SO",
                         "ZA",
                         "GS",
                         "ES",
                         "LK",
                         "SD",
                         "SR",
                         "SJ",
                         "SZ",
                         "SE",
                         "CH",
                         "SY",
                         "TW",
                         "TJ",
                         "TZ",
                         "TH",
                         "TL",
                         "TG",
                         "TK",
                         "TO",
                         "TT",
                         "TN",
                         "TR",
                         "TM",
                         "TC",
                         "TV",
                         "UG",
                         "UA",
                         "AE",
                         "GB",
                         "US",
                         "UM",
                         "UY",
                         "UZ",
                         "VU",
                         "VA",
                         "VE",
                         "VN",
                         "VG",
                         "VI",
                         "WF",
                         "EH",
                         "YE",
                         "ZM",
                         "ZW"};
    int selectIndex = 45;

    if(currentCode!=null){
        int i = 0;
        Log.e("","国家码 111 strNumber ====  "+ selectIndex+"   currentCode:"+currentCode);
        for(;i<arrayCode.length;i++){
            if(currentCode.equals(arrayCode[i]))
            {
                selectIndex = i;
                break;
            }
        }
        Log.e("","国家码 222 strNumber ====  "+ selectIndex);
    }


    return selectIndex;
 }
}
