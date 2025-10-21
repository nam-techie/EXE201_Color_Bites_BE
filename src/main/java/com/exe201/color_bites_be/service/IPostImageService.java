package com.exe201.color_bites_be.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IPostImageService {
    List<String> uploadPostImages(List<MultipartFile> files);
}
