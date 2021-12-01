package com.cyneck.workflow.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.cyneck.workflow.common.Result;
import com.cyneck.workflow.common.ResultUtil;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>Description: </p>
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/2/2 17:11
 **/
@Api(tags = "6-回调测试接口", value = "CallController")
@ApiSort(value = 6)
@Slf4j
@RequestMapping("/")
@RestController
public class CallController extends ApiController {

    @ApiOperation("6.1-回调测试接口")
    @ApiOperationSupport(order = 6)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "data", value = "回调接收到的数据json字符串", required = true, dataType = "String")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "回调接收到的数据对象", response = Result.class)
    })
    @PostMapping(value = "call")
    public Result callTest(@RequestBody String data) {
        log.info("任务监听、委托回调：\n {}", data);
        JSONObject jsonObject = JSONUtil.parseObj(data, true);
        return ResultUtil.data(jsonObject);
    }
}
