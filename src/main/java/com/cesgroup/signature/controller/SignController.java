package com.cesgroup.signature.controller;

import cn.hutool.core.util.StrUtil;
import com.cesgroup.signature.config.SignedConfig;
import com.cesgroup.signature.entity.SignRecord;
import com.cesgroup.signature.service.ISignReocrdService;
import com.cesgroup.signature.util.HttpResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author ：Yangzhao
 * @description：签名Controller
 * @date ：2023/8/29 8:10 下午
 * @version: 1.0
 */
@Api(tags = "签名相关接口")
@Slf4j
@RestController
@RequestMapping( "/signature")
public class SignController {

    @Autowired
    private SignedConfig signedConfig;

    @Resource
    private ISignReocrdService signReocrdService;

    @ApiOperation(value = "保存签名信息", notes = "保存文件签名信息，供客户端调用")
    @PostMapping(value = "/saveOrUpdate")
    public HttpResult saveOrUpdate(SignRecord sign){
        if (!signedConfig.getEnabled()){
            return HttpResult.failed("没有启用文件签名");
        }
        HttpResult httpResult;
        try {
            if (StrUtil.hasBlank(sign.getFilePath(), sign.getSignValue(), sign.getFromIp())){
                return HttpResult.failed("参数不能为空");
            }
            // 文件签名
            signReocrdService.serverSignature(sign);
            httpResult = HttpResult.success();
        }catch (Exception e){
            httpResult = HttpResult.failed("签名失败," + e.getMessage());
            e.printStackTrace();
        }
        return httpResult;
    }

    @ApiOperation("根据文件路径和客户端IP查询签名记录")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "filePath", value = "文件绝对路径", required = true),
        @ApiImplicitParam(name = "clientIp", value = "客户端Ipv4地址", required = true)
    })
    @GetMapping(value = "/findByFilePathAndIp")
    public HttpResult findByFilePathAndClientIP(String filePath, String clientIp){
        if (StrUtil.hasBlank(filePath, clientIp)){
            return HttpResult.failed("参数不能为空");
        }
        try {
            SignRecord signRecord = signReocrdService.findByFilePathAndClientIp(filePath, clientIp);
            return HttpResult.success(signRecord);
        }catch (Exception ex){
            ex.printStackTrace();
            return HttpResult.failed(ex.getMessage());
        }
    }

    @ApiOperation("根据客户端IP查询签名记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "clientIp", value = "客户端Ipv4地址", required = true)
    })
    @GetMapping(value = "/findByClientIp")
    public HttpResult findByClientIP(String clientIp){
        if (StrUtil.isEmpty(clientIp)){
            return HttpResult.failed("参数不能为空");
        }
        try {
            List<SignRecord> signRecordList = signReocrdService.findByClientIp(clientIp);
            return HttpResult.success(signRecordList);
        }catch (Exception ex){
            ex.printStackTrace();
            return HttpResult.failed(ex.getMessage());
        }
    }

    @ApiOperation("根据ID删除签名记录")
    @PostMapping(value = "/delete/{id}")
    @ApiImplicitParam(name = "id", value = "签名记录ID", required = true)
    public HttpResult deleteById(@PathVariable(name = "id") String id){
        try {
            signReocrdService.deleteById(id);
            return HttpResult.success("删除成功");
        }catch (Exception ex) {
            ex.printStackTrace();
            return HttpResult.failed(ex.getMessage());
        }
    }

    @ApiOperation("文件签名调用-本地文件")
    @GetMapping(value = "/local")
    public HttpResult localSignature(){
        HttpResult httpResult;
        try {
            if (!signedConfig.getEnabled()){
                return HttpResult.failed("没有启用文件签名");
            }
            signReocrdService.localSignature();;
            httpResult = HttpResult.success();
        } catch (Exception e) {
            httpResult = HttpResult.failed("签名失败," + e.getMessage());
            e.printStackTrace();
        }
        return httpResult;
    }

}
