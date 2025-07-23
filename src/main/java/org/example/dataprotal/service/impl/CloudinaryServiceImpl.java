package org.example.dataprotal.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dataprotal.service.CloudinaryService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CloudinaryServiceImpl implements CloudinaryService {

    private final Cloudinary cloudinary;

    @Override
    public String uploadFile(MultipartFile file, String folder) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        Map<?, ?> uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap("folder", folder)
        );

        return uploadResult.get("url").toString();
    }

    @Override
    public List<String> uploadFiles(List<MultipartFile> files, String folder) throws IOException {
        List<String> urls = new ArrayList<>();

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                urls.add(uploadFile(file, folder));
            }
        }

        return urls;
    }

    @Override
    public void deleteFile(String url) throws IOException {
        log.warn("Remove file. file url : {}", url);
        Map result = cloudinary.uploader().destroy(extractPublicIdFromUrl(url), ObjectUtils.emptyMap());
        log.warn("Remove file result : {}", result);
    }

    private String extractPublicIdFromUrl(String url) {
        try {
            String substring = url.substring(url.indexOf("/upload/") + 8);

            String[] parts = substring.split("/");
            String publicId = Arrays.stream(parts)
                    .skip(1)
                    .collect(Collectors.joining("/"));

            return publicId.replaceAll("\\.[a-zA-Z0-9]+$", "");
        } catch (Exception e) {
            log.error("Error extracting publicId from URL:" + url, e);
            return null;
        }
    }
}
