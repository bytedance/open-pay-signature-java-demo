/**
 * Package com.bytedance.microapp SignTest 签名算法的单测
 * <p>
 * Package com.bytedance.microapp SignTest Unit testing of signature algorithms
 */

package com.bytedance.microapp;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class SignTest {

    /**
     * TestRequestSign 担保支付请求签名算法测试
     * <p>
     * TestRequestSign Guaranteed Payment Request Signature Algorithm Test
     */
    @Test
    void testRequestSign() {
        /**
         * 以预下单接口为例
         * <p>
         * Take the create_order interface as an example
         */
        Map<String, Object> testCase = new HashMap<String, Object>() {
            {
                put("app_id", "ttcfdbb9xxxxxxxxxxx");
                put("thirdparty_id", "tta4bad200000xxxxxx");
                put("out_order_no", "test-02167569xxxxxx");
                put("total_amount", 2376);
                put("subject", "test-payment_subject-test-paym...");
                put("body", "强烈推荐！经典腊汁肉夹馍团购价仅需7.92元！");
                put("valid_time", 172800);
                put("notify_url", "https://www.xxx.com");
                put("disable_msg", 0);
                put("msg_page", "pages/user/orderDetail/orderDetail?id=997979879879879879");
                put("sign", "edc608b160a1be3de0xxxxxx");
            }
        };

        Assertions.assertEquals("54f102e7115f8a6a3e6af4633dc33959", Sign.requestSign(testCase));
    }

    /**
     * testCallbackSign 担保支付回调签名算法测试
     * <p>
     * testCallbackSign Guaranteed payment callback signature algorithm test
     */
    @Test
    void testCallbackSign() {
        /**
         * 以支付回调为例
         * <p>
         * Take the payment callback as an example
         */
        String callbackToken = "fdsifakhflasjfxxxxxxxxx"; // callbackToken 是平台上配置的token (callbackToken is the token configured on the platform)
        String timestamp = "1652675265";
        String nonce = "9999";
        String msg = "80850852";
        List<String> sortedString = Arrays.asList(callbackToken, timestamp, nonce, msg);

        Assertions.assertEquals("c9df04a40645c4ec15c13bc542cea589eac57e64", Sign.callbackSign(sortedString));
    }
}
