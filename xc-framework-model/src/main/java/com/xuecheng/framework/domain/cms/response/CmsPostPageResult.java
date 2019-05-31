package com.xuecheng.framework.domain.cms.response;

import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */
@Data
@ToString
@NoArgsConstructor
public class CmsPostPageResult extends ResponseResult {
    String  previewUrl;//页面预览的url，必须得到页面id才可以拼装
    public CmsPostPageResult(ResultCode resultCode, String previewUrl){
        super(resultCode);
        this.previewUrl=previewUrl;
    }
}
