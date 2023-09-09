package com.cesgroup.signverify.controller;

import cn.hutool.core.util.StrUtil;
import com.cesgroup.signverify.entity.SignRecord;
import com.cesgroup.signverify.entity.SignVerifyRecord;
import com.cesgroup.signverify.service.ISignVerifyRecordService;
import com.cesgroup.signverify.util.HttpResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * @author ：Yangzhao
 * @description：验签Controller
 * @date ：2023/8/29 8:10 下午
 * @version: 1.0
 */
@Api(tags = "验签相关接口")
@Slf4j
@RestController
@RequestMapping( "/verify")
public class SignVerifyController {

    @Autowired
    private ISignVerifyRecordService signVerifyService;

    @ApiOperation("验证签名信息-服务器本地文件")
    @GetMapping(value = "/local")
    public HttpResult verify(){
        HttpResult httpResult;
        try {
            signVerifyService.signVerify();
            httpResult = HttpResult.success();
        } catch (Exception e){
            httpResult = HttpResult.failed(e.getMessage());
            e.printStackTrace();
        }
        return httpResult;
    }

    @ApiOperation("验证签名信息")
    @PostMapping(value = "/server")
    public HttpResult verify(SignRecord sign){
        HttpResult httpResult;
        if (Objects.isNull(sign) || StrUtil.hasBlank(sign.getFilePath(), sign.getSignValue(), sign.getFromIp())){
            return HttpResult.failed("参数不能为空");
        }
        try {
            signVerifyService.signVerify(sign);
            httpResult = HttpResult.success();
        } catch (Exception e){
            httpResult = HttpResult.failed("验签失败," + e.getMessage());
            e.printStackTrace();
        }
        return httpResult;
    }

    @GetMapping(value = "/list/{id}")
    public HttpResult findById(@PathVariable(name = "id") String id){
        if (StrUtil.isEmpty(id)){
            return HttpResult.failed("参数不能为空");
        }
        try {
            List<SignVerifyRecord> records = signVerifyService.listById(id);
            return HttpResult.success(records);
        }catch (Exception ex){
            ex.printStackTrace();
            return HttpResult.failed("查询业务异常");
        }

    }

}
