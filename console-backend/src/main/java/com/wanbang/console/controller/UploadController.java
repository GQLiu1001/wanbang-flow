package com.wanbang.console.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.wanbang.console.common.Result;
import com.wanbang.console.enums.ResultCode;
import com.wanbang.console.exception.WanbangException;
import com.wanbang.console.service.SysUserService;
import com.wanbang.console.util.MinioUpload;
import io.minio.errors.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "上传接口")
@CrossOrigin
@RestController
public class UploadController {
    @Resource
    private SysUserService sysUserService;
    @Operation(summary = "上传接口")
    @PostMapping("/api/upload")
    public Result upload(@RequestParam("file") MultipartFile file) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String url = MinioUpload.uploadFile(file);
        if (url != null) {
            Map<String, String> data = new HashMap<>();
            data.put("fileUrl", url);
            Integer id = StpUtil.getLoginIdAsInt();
            System.out.println("loginId:" + id);
            if (id == null) {
                throw new WanbangException(ResultCode.FAIL);
            }
            Integer i = sysUserService.updateUserAvatar(id,url);
            if (i > 0) {
                System.out.println("添加成功");
            }else {
                throw new WanbangException(ResultCode.FAIL);
            }
            return Result.success(data);  // 确保返回的是 {code:200, message:"成功", data:{fileUrl:"http://..."}}
        }
        else {
            return Result.fail();
        }
    }
}
