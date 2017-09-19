package sec;

import java.io.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.Date;


public class YunxinTest   //注意自己的类名对应好！
{
    public static void main(String[] args) throws Exception
    {
        System.out.println(sendMsg());


    }


    /**
     * 发送POST方法的请求
     * 
     * @return 所代表远程资源的响应结果
     */
    public static String sendMsg()
    {
        String appKey = "e8aa565eb1b054c4e708742f7ef589c1";
        String appSecret = "40bb82e1063a";
        String nonce = "baoluo"; // 随机数（最大长度128个字符）
        String curTime = String.valueOf((new Date()).getTime() / 1000L); // 当前UTC时间戳
        System.out.println("curTime: " + curTime);


        String checkSum = CheckSumBuilder.getCheckSum(appSecret, nonce, curTime);
        System.out.println("checkSum: " + checkSum);


        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try
        {
            String url = "https://api.netease.im/sms/sendtemplate.action"; //网址可以不修改
            String encStr1 = URLEncoder.encode("Tom", "utf-8");
            String encStr2 = URLEncoder.encode("name", "utf-8"); // url编码；防止不识别中文
            String params = "templateid=3033519&mobiles=[\"13310945568\"]" 
                            + "&params=" + "[\"" + encStr1 + "\",\""+ encStr2 + "\"]";
            System.out.println("params：" + params);


            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("AppKey", appKey);
            conn.setRequestProperty("CheckSum", checkSum);
            conn.setRequestProperty("CurTime", curTime);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            conn.setRequestProperty("Nonce", nonce);


            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
     


            // 发送请求参数
            out.print(params);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            System.out.println(conn);
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            System.out.println("in"+in);
            String line;
            while ((line = in.readLine()) != null)
            {
                result += line;
            }
        } catch (Exception e)
        {
            System.out.println("发送 POST 请求出现异常！\n" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输出流、输入流
        finally
        {
            try
            {
                if (out != null)
                {
                    out.close();
                }
                if (in != null)
                {
                    in.close();
                }
            } catch (IOException ex)
            {
                ex.printStackTrace();
            }
        }
        return result;
    }
    
}
class CheckSumBuilder {
    // 计算并获取CheckSum
    public static String getCheckSum(String appSecret, String nonce, String curTime) {
        return encode("sha1", appSecret + nonce + curTime);
    }


    // 计算并获取md5值
    public static String getMD5(String requestBody) {
        return encode("md5", requestBody);
    }


    private static String encode(String algorithm, String value) {
        if (value == null) {
            return null;
        }
        try {
            MessageDigest messageDigest
                    = MessageDigest.getInstance(algorithm);
            messageDigest.update(value.getBytes());
            return getFormattedText(messageDigest.digest());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private static String getFormattedText(byte[] bytes) {
        int len = bytes.length;
        StringBuilder buf = new StringBuilder(len * 2);
        for (int j = 0; j < len; j++) {
            buf.append(HEX_DIGITS[(bytes[j] >> 4) & 0x0f]);
            buf.append(HEX_DIGITS[bytes[j] & 0x0f]);
        }
        return buf.toString();
    }
    private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
}