package com.exe201.color_bites_be.service.impl;

import com.cloudinary.Cloudinary;
import com.exe201.color_bites_be.dto.response.CloudinaryResponse;
import com.exe201.color_bites_be.exception.FuncErrorException;
import com.exe201.color_bites_be.service.ICloudinaryService;
import com.exe201.color_bites_be.util.FileUpLoadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * Implementation của ICloudinaryService
 * Xử lý logic upload file lên Cloudinary
 */
@Service
public class CloudinaryServiceImpl implements ICloudinaryService {
    
    @Autowired
    Cloudinary cloudinary;

    @Override
    @Transactional
    public CloudinaryResponse uploadFile(final MultipartFile file, final String fileName) {
        try {
            final Map<String, Object> result = this.cloudinary.uploader()
                    .upload(file.getBytes(),
                            Map.of("public_id",
                                    "colorbites/user/"
                                            + fileName));
            final String url = (String) result.get("secure_url");
            final String publicId = (String) result.get("public_id");
            return CloudinaryResponse.builder().publicId(publicId).url(url).build();
        } catch (Exception e) {
            throw new FuncErrorException("Failed to upload file");
        }
    }

    @Override
    @Transactional
    public CloudinaryResponse uploadFileVideo(MultipartFile file, String folder, String fileName, String resourceType) {
        try {
            Map<?,?> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    Map.of(
                            "public_id", folder + "/" + fileName,
                            "resource_type", resourceType  // "image" hoặc "video" hoặc "auto"
                    )
            );
            return CloudinaryResponse.builder()
                    .publicId((String) result.get("public_id"))
                    .url((String)  result.get("secure_url"))
                    .build();
        } catch (Exception e) {
            throw new FuncErrorException("Failed to upload " + resourceType);
        }
    }

    @Override
    public CloudinaryResponse uploadVideo(MultipartFile file, String fileName) {
        // Kiểm tra đuôi và kích thước
        FileUpLoadUtil.assertAllowed(file, FileUpLoadUtil.VIDEO_PATTERN);
        // Upload vào folder riêng
        return uploadFileVideo(file, "colorbites/video", fileName, "video");
    }
}
