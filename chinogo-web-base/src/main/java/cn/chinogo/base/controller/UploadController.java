package cn.chinogo.base.controller;

import cn.chinogo.utils.FastJsonConvert;
import cn.chinogo.utils.StorageService;
import io.swagger.annotations.Api;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 图片上传Controller
 * 
 * @author chinotan
 */
@Api("图片上传Controller")
@RestController
@RequestMapping("/upload")
public class UploadController {

    private static Logger logger = Logger.getLogger(UploadController.class);

    @Value("${fastdfs.base.url}")
    private String FASTDFS_BASE_URL;

    @Autowired
    private StorageService storageService;

    @PostMapping("/file")
    public Object upload(MultipartFile uploadFile) {
        HashMap<String, Object> map = new HashMap<>();
        if (uploadFile != null) {

            String oName = uploadFile.getOriginalFilename();

            String extName = oName.substring(oName.lastIndexOf(".") + 1);

            try {
                String uploadUrl = storageService.upload(uploadFile.getBytes(), extName);
                if (StringUtils.isNotBlank(uploadUrl)) {
                    map.put("status", "success");
                    map.put("file_path", FASTDFS_BASE_URL + uploadUrl);
                } else {
                    map.put("status", "failed");
                    map.put("file_path", "file_path为空，上传失败");
                }
            } catch (IOException e) {
                logger.error("文件上传失败！");

                map.put("status", "failed");
                map.put("file_path", "文件上传失败！");
            }
            return map;
        }
        logger.error("MultipartFile不存在！");

        map.put("status", "failed");
        map.put("file_path", "MultipartFile不存在！");
        return map;
    }

    @DeleteMapping("/file")
    public Object delete(@RequestBody String fileIdString) {
        Map mapFileId = FastJsonConvert.convertJSONToObject(fileIdString, Map.class);
        String fileId = (String) mapFileId.get("fileId");
        
        HashMap<String, Object> map = new HashMap<>();
        // 判断文件id是否存在
        if (StringUtils.isBlank(fileId)) {
            map.put("status", "failed");
            map.put("message", "文件名错误");

            logger.error("fileId错误！");
            return map;
        }

        int i;
        try {
            i = storageService.delete(fileId);
        } catch (Exception e) {
            map.put("status", "failed");
            map.put("message", "文件删除失败");

            logger.error("文件删除失败！");
            return map;
        }

        if (i != 0) {
            map.put("status", "failed");
            map.put("message", "文件删除失败");

            logger.error("文件删除失败！");
            return map;
        } else {
            map.put("status", "success");
            map.put("message", "文件删除成功");

            return map;
        }
    }
}
