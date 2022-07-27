/**
 * Copyright 2022 Beijing Douyin Information Service Co., Ltd.
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Package com.bytedance.microapp Sign 签名算法
 * <p>
 * Package com.bytedance.microapp Sign implement the signature algorithm
 */

package com.bytedance.microapp;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

public class Sign {

    /**
     * 担保支付请求不参与签名参数
     * app_id 小程序appID
     * thirdparty_id 代小程序进行该笔交易调用的第三方平台服务商id
     * sign 签名
     * other_settle_params 其他分账方参数
     *
     * Guaranteed payment requests do not participate in signature parameters
     * app_id Applets appID
     * thirdparty_id The id of the third-party platform service provider that calls the transaction on behalf of the Applets
     * sign sign
     * other_settle_params Other settle params
     */
    public final static List<String> REQUEST_NOT_NEED_SIGN_PARAMS = Arrays.asList("app_id", "thirdparty_id", "sign", "other_settle_params");

    /**
     * 支付密钥值，需要替换为自己的密钥(完成进件后，开发者可在字节开放平台-【某小程序】-【功能】-【支付】-【担保交易设置】中查看支付系统秘钥 SALT)
     *
     * Payment key value, you need to replace it with your own key
     */
    private static final String SALT = "your_payment_salt";

    /**
     * RequestSign 担保支付请求签名算法
     * @param paramsMap {@code Map<String, Object>} 所有的请求参数
     * @return：签名字符串
     *
     * RequestSign Guaranteed payment request signature algorithm
     * @param paramsMap {@code Map<String, Object>} all request parameters
     * @return: Signature string
     */
    public static String requestSign(Map<String, Object> paramsMap) {
        List<String> paramsArr = new ArrayList<>();
        for (Map.Entry<String, Object> entry : paramsMap.entrySet()) {
            String key = entry.getKey();
            if (REQUEST_NOT_NEED_SIGN_PARAMS.contains(key)) {
                continue;
            }
            String value = entry.getValue().toString();

            value = value.trim();
            if (value.startsWith("\"") && value.endsWith("\"") && value.length() > 1) {
                value = value.substring(1, value.length() - 1);
            }
            value = value.trim();
            if (value.equals("") || value.equals("null")) {
                continue;
            }
            paramsArr.add(value);
        }
        paramsArr.add(SALT);
        Collections.sort(paramsArr);
        StringBuilder signStr = new StringBuilder();
        String sep = "";
        for (String s : paramsArr) {
            signStr.append(sep).append(s);
            sep = "&";
        }
        return md5FromStr(signStr.toString());
    }

    /**
     * CallbackSign 担保支付回调签名算法
     * @param params {@code List<String>} 所有字段（验证时注意不包含 sign 签名本身，不包含空字段与 type 常量字段）内容与平台上配置的 token
     * @return：签名字符串
     *
     * CallbackSign Guaranteed payment callback signature algorithm
     * @param params {@code List<String>} The content of all fields (note that the sign signature itself is not included during verification, and does not include empty fields and type constant fields) content and the token configured on the platform
     * @return: signature string
     */
    public static String callbackSign(List<String> params) {
        try {
            String concat = params.stream().sorted().collect(Collectors.joining(""));
            byte[] arrayByte = concat.getBytes(StandardCharsets.UTF_8);
            MessageDigest mDigest = MessageDigest.getInstance("SHA1");
            byte[] digestByte = mDigest.digest(arrayByte);

            StringBuffer signBuilder = new StringBuffer();
            for (byte b : digestByte) {
                signBuilder.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
            }
            return signBuilder.toString();
        } catch (Exception exp) {
            return "";
        }
    }

    /**
     * md5FromStr md5算法对该字符串计算摘要
     * @param inStr {@code String} 需要签名的字符串
     * @return：签名字符串
     *
     * md5FromStr The md5 algorithm computes a digest of the string
     * @param inStr {@code String} String to be signed
     * @return: signature string
     */
    private static String md5FromStr(String inStr) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }

        byte[] byteArray = inStr.getBytes(StandardCharsets.UTF_8);
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuilder hexValue = new StringBuilder();
        for (byte md5Byte : md5Bytes) {
            int val = ((int) md5Byte) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }
}
