package com.exe201.color_bites_be.service;

import com.exe201.color_bites_be.dto.response.CloudinaryResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * Interface định nghĩa các phương thức upload file lên Cloudinary
 * Hỗ trợ upload ảnh và video
 */
public interface ICloudinaryService {
    
    /**
     * Upload file (ảnh) lên Cloudinary
     * @param file File cần upload
     * @param fileName Tên file
     * @return CloudinaryResponse Thông tin file đã upload
     */
    CloudinaryResponse uploadFile(MultipartFile file, String fileName);
    
    /**
     * Upload file video hoặc ảnh với tùy chọn folder và resource type
     * @param file File cần upload
     * @param folder Folder lưu trữ trên Cloudinary
     * @param fileName Tên file
     * @param resourceType Loại resource ("image", "video", "auto")
     * @return CloudinaryResponse Thông tin file đã upload
     */
    CloudinaryResponse uploadFileVideo(MultipartFile file, String folder, String fileName, String resourceType);
    
    /**
     * Upload video lên Cloudinary
     * @param file File video cần upload
     * @param fileName Tên file
     * @return CloudinaryResponse Thông tin video đã upload
     */
    CloudinaryResponse uploadVideo(MultipartFile file, String fileName);
}
