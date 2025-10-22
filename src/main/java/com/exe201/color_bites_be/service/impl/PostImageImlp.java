package com.exe201.color_bites_be.service.impl;

import com.exe201.color_bites_be.dto.response.CloudinaryResponse;
import com.exe201.color_bites_be.exception.BadRequestException;
import com.exe201.color_bites_be.service.ICloudinaryService;
import com.exe201.color_bites_be.service.IPostImageService;
import com.exe201.color_bites_be.util.FileUpLoadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.ArrayList;
import java.util.List;

@Service
public class PostImageImlp  implements IPostImageService {

    @Autowired
    private ICloudinaryService cloudinaryService;


    @Override
    @Transactional
    public List<String> uploadPostImages(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new BadRequestException("Danh sách ảnh rỗng");
        }
        List<String> urls = new ArrayList<>(files.size());
        for (MultipartFile file : files) {
            FileUpLoadUtil.assertAllowed(file, FileUpLoadUtil.IMAGE_PATTERN);

            final String fileName = FileUpLoadUtil.getFileName(file.getOriginalFilename());
            final CloudinaryResponse up = cloudinaryService.uploadFile(file, fileName);

            urls.add(up.getUrl());
        }
        return urls;
    }
}
