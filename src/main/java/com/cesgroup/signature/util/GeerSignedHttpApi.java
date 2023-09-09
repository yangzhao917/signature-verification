package com.cesgroup.signature.util;

import cn.hutool.core.lang.Assert;
import cn.hutool.http.*;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.cesgroup.signature.config.SignedConfig;
import com.cesgroup.signature.dto.GeerVerifyCertOpt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 格尔http接口签名验签，测试文档建议采用/api/svs/bss/signMessageDetach 和 /api/svs/bss/verifySignedMessageDetach 接口
 * @author ding.haiyang
 * @since 2021/8/9
 */
@Component
public class GeerSignedHttpApi {
    private static final Logger log = LoggerFactory.getLogger(GeerSignedHttpApi.class);

    @Autowired
    private SignedConfig signedConfig;

    private HttpRequest post(String uri){
        HttpRequest request = HttpUtil.createPost(signedConfig.getHost()+uri);
        request.setReadTimeout(5000);
        request.setConnectionTimeout(1000);
        request.header(Header.CONTENT_TYPE, ContentType.JSON.toString());
        request.header("dataFormat", "base64");
        return request;
    }

    private JSONObject validateSignHttpResponse(HttpResponse response){
        Assert.isTrue(response.isOk(),"签名接口调用失败！"+response.body());
        String body = response.body();
        Assert.isTrue(JSONUtil.isJson(body),"签名接口调用失败！返回参数错误！"+body);
        JSONObject responseBody = JSONUtil.parseObj(body);
        String errorCode = responseBody.getStr("errorCode");
        Assert.isTrue("0".equals(errorCode),"签名失败！"+body);
        return responseBody;
    }

    private JSONObject validateVerifyHttpResponse(HttpResponse response){
        Assert.isTrue(response.isOk(),"验签接口调用失败！"+response.body());
        String body = response.body();
        Assert.isTrue(JSONUtil.isJson(body),"验签接口调用失败！返回参数错误："+body);
        JSONObject responseBody = JSONUtil.parseObj(body);
        String errorCode = responseBody.getStr("errorCode");
        Assert.isTrue("0".equals(errorCode),"验签失败！message："+responseBody.getStr("message"));
        return responseBody;
    }
    /**
     * 不带原文的消息签名
     * @param b64OriginData B64签名原文
     * @param certAlias 证书别名，证书查询条件，优先级比certHexSn和certDn高
     * @param certHexSn 证书16进制SN，证书查询条件，优先级比certDn高
     * @param certDn 证书dn全字符串，证书查询条件
     * @param keyPin 私钥操作码
     * @param hashType 要求服务端用什么hash算法
     *                  RAW：服务端不对data做hash运算
     *                  SM3：服务端要对data做SM3（签名证书为SM2时的默认值）
     * @param serviceName 服务名称，默认值default
     */
    public String signMessageDetach(String b64OriginData,String certAlias,String certHexSn,String certDn,
                                    String keyPin,String hashType,String serviceName){
        String uri = "/api/svs/bss/signMessageDetach";
        HttpRequest post = post(uri);

        JSONObject body = new JSONObject(JSONConfig.create().setIgnoreNullValue(true));
        body.put("b64OriginData",b64OriginData)
                .put("certAlias",certAlias)
                .put("certHexSn",certHexSn)
                .put("certDn",certDn)
                .put("keyPin",keyPin)
                .put("hashType",hashType)
                .put("serviceName",serviceName);
        post.body(body.toString());
        HttpResponse response = post.execute();

        JSONObject jsonObject = validateSignHttpResponse(response);
        String b64SignedMessage = jsonObject.getStr("b64SignedMessage");
        Assert.notBlank(b64SignedMessage,"无签名消息！");
        return b64SignedMessage;
    }

    /**
     * 验证不带原文的消息签名
     * @param b64OriginData B64签名原文
     * @param b64SignedMessage B64签名消息
     * @param verifyCertOpt 验证证书选项
     * @param hashType 要求服务端用什么hash算法
     *                  RAW：服务端不对data做hash运算
     *                  SM3：服务端要对data做SM3（签名证书为SM2时的默认值）
     * @param serviceName 服务名称，默认值default
     * @return
     */
    public boolean verifySignedMessageDetach(String b64OriginData, String b64SignedMessage,
                                             GeerVerifyCertOpt verifyCertOpt, String hashType, String serviceName){
        String uri = "/api/svs/bss/verifySignedMessageDetach";
        HttpRequest post = post(uri);

        JSONObject body = new JSONObject(JSONConfig.create().setIgnoreNullValue(true));
        body.put("b64OriginData",b64OriginData)
                .put("b64SignedMessage",b64SignedMessage)
                .put("verifyCertOpt",verifyCertOpt)
                .put("hashType",hashType)
                .put("serviceName",serviceName);
        post.body(body.toString());
        HttpResponse response = post.execute();

        try{
            validateVerifyHttpResponse(response);
            return true;
        }catch(Exception e) {
            log.error(e.getMessage(),e);
            return false;
        }
    }
}
