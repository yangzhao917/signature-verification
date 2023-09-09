package com.cesgroup.signature.config;

import com.cesgroup.signature.dto.GeerVerifyCertOpt;
import com.cesgroup.signature.util.GeerSignedHttpApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author ding.haiyang
 * @since 2021/8/9
 */
@Component
public class SignedService {

    @Autowired
    private GeerSignedHttpApi httpApi;
    @Autowired
    private SignedConfig signedConfig;

    public String sign(String b64OriginData){
        return httpApi.signMessageDetach(b64OriginData,signedConfig.getCertAlias(),
                null,null,null,"SM3",null);
    }
    public boolean verifySigned(String b64OriginData, String b64SignedMessage){
        return httpApi.verifySignedMessageDetach(b64OriginData,b64SignedMessage,new GeerVerifyCertOpt(),
                "SM3",null);
    }
}
