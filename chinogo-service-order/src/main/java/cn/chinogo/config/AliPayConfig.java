package cn.chinogo.config;

import java.io.FileWriter;
import java.io.IOException;

/* *
 *类名：AlipayConfig
 *功能：基础配置类
 *详细：设置帐户有关信息及返回路径
 *修改日期：2017-04-05
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
 */

public class AliPayConfig {

    // ↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

    // 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
    public static String app_id = "2016080300156758";

    // 商户私钥，您的PKCS8格式RSA2私钥
    public static String merchant_private_key = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCnYVFzwQrc6gUbfVjMjZXUppT9Dsc0l49SiFrqU6x8T+RMliZpDtTohCFGoCtUspL8F/LWeluQaTIJiwrvX3DByKzaajFeRksJ2nLf1+BPePfWsFdeQg5ZtKqDGBVdVkeQjcHltfn60zd2IiK0UCFqMYw+JcgtJO8wK8K/m/s8DBxT7VbGTjxGUKNAB6iZQim0mvkwdIghK10se26RvK8whfC8oucaVJHY1IOmSqAZmIIIy17dfMi11OZbd/63tz+1ID9VQdsY0u8p2omUNsg+EhJigP9VK4usv6jPCTqRyOoJYrDyRD/e3BVzMF7UIbXNxZsBhmy/l4ncQ391nD0xAgMBAAECggEAVmS56Rhz5fD4r+a0BejytC9DaZbOJYwxEvWEj7yepA+MlCdQLhvUsys4Db8wfmgpy+sEkcXYKyLRC/Hzs/g8OBm/doEneDSqGKzjbDx98CdNLUgZ4HIR/CVveXjd1i5pg+hXc5zJKbkJwHUAGrfdd3WZoJ5Zw0t0FhYQUFxPgf8YdLH7qrFE1pf/O8HOGoN2JGsfEi64VKRMhbZXwoTjc219Vyd4xnAKmigh2gUXF0AKOOmbhjIIBx+4RKv7tOpYwlXP5bdaUeeMVdDfAyV/P4fHe/YzWmnrJQJAwnjLqz7uj+D4DpH59KvnIYQGEJNgIwQjSs7j10S49xN5s9H97QKBgQD71OiISRn+vVfuMhZzwc2liMgViaK/nzUpnotkqESfi5qUGK47+pVC0FVdgu+BqaybeKjXru1YNGgH901+RY1FhfPS5niG6rNFbH1zuJiJldyM4mQ5TVku/cj3chxPV81fr/88K/D/cE6cDTmJManlpL1Ib+V1JhslYfUWSCfStwKBgQCqJo/GB9Vs6zgwMaW5nFxNMuAGAfgUoXCDSjp+825YGNpQJ/TxAcHMWz8OY4o2lXcGk2APO1p8LJNfDt246yTyc8eri5KqXJd5/a6KSkmcFyOw/Nd209+b94l3gDIBXMKbwb7/iTbI5jPXXbD4fOmk9aq+LXF7LJgsLIj3LBRnVwKBgAxQxDJuCMRpdBRlBK4Si0EOKGLNQVVHruzjIQQCKqD2zc9ySYsLXSNQVuxky9u2dYeA5hjuKBNJNNE26eZD9n2w6FSnCrvmXHAHtzbijysjVg7Zv3cB7lua86oOPY4vyA5m5/+EUpvbXSzKhMbN0/Y0EUGqnzkUbP1uBfNaAgCPAoGBAJaIiTh9Y7/6Fnrk4abmA+80rGgEQ/QUpBMzj68TNTNxwWua/iRfFpLyw0W6oOQLtgM2TY0MSNCFK1i1MUpRlx19e1B0qixYwJbn7gxhDuCuxB/ogcOaTUGSbacw3ozAAViFv7IaNkLlD0ZhmJkvAhK0Wfvo/nYoDPU/7WkoMWD/AoGBAOvk9BVGJ/+Jp/zzWbJB+VBW9uDus9XM3gsfkSTePWuTi+TS4xKjNkogS9oUMkuMWLtB9FK3YjyVQP1oFbiaJIuzPSzYfq7pXjmQ3kCbHHrdKCKaiUbR9z0SzFNARq3CPuRGPOykZ9sOXKXDGfyxnvGZpuDx/7FSD+Ei7wsijtTu";

    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm
    // 对应APPID下的支付宝公钥。
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAwBqPgl0z7qWAUX4G2w3XMKhErWmKxMnCTniqylwYqSMVD6oW2UnSxa50AzXXiVH5ss+XkKVnAo4bPxzpPj+NVF1TmZYTdSeQMVT4mbOpwXGadqh+9czTHFhqlc7urZswxmjOVv72AvzI4h/BYEcL2St34/x06C7xQvYpQeMYVbK9gzzVbI+o8x4tlc+CoUY0Uq0Eum9bd2eKSHUnaY24deCM5VYD5dXytUZA90axnU4cZ6DPqdFGcRdjrtTEEteTtenJh90vEYxp/6GueTr2WDhvuNnax3E1Aelip07JzXJUAYPiQ61RfVf/SHskKyZOz8yCfaFKikYSVVYKUNXSMQIDAQAB";

    // 服务器异步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    /*public static String notify_url = "http://chino.free.ngrok.cc/pay/notifyUrl";*/

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String return_url = "http://chino.free.ngrok.cc/pay/page/returnUrl";

    // 签名方式
    public static String sign_type = "RSA2";

    // 字符编码格式
    public static String charset = "utf-8";

    // 支付宝网关
    public static String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    // 日志路径
    public static String log_path = "/log/";

    // ↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

    /**
     * 写日志，方便测试（看网站需求，也可以改成把记录存入数据库）
     *
     * @param sWord
     *            要写入日志里的文本内容
     */
    public static void logResult(String sWord) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(log_path + "alipay_log_" + System.currentTimeMillis() + ".txt");
            writer.write(sWord);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
