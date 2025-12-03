package com.ECommerce.utils;

import com.ECommerce.dto.request.product.AddProductImageRequest;
import com.ECommerce.exception.ApplicationException;
import com.ECommerce.model.product.ProductImageModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;

public class ImageUploadHelper {

    private static final Path UPLOAD_DIR = Paths.get("uploads/products").toAbsolutePath();
    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg", "image/jpg", "image/png", "image/webp"
    );

    public static ProductImageModel uploadImage(MultipartFile file, AddProductImageRequest request) throws ApplicationException {
        if (file.isEmpty()) {
            throw new ApplicationException("File is empty!", "FILE_IS_EMPTY", HttpStatus.BAD_REQUEST);
        }
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new ApplicationException("File too large! Max 5MB", "FILE_IS_TOO_BIG", HttpStatus.BAD_REQUEST);
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType.toLowerCase())) {
            throw new ApplicationException("Only JPEG, PNG, and WebP images are allowed", "INVALID_TYPE", HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        }

        String extension = getFileExtension(file.getOriginalFilename());
        if(extension.isEmpty()){
            extension = getExtensionFromContentType(contentType);
        }


        String basename = HelperClass.generateSlug(request.name());
        String filename=basename+"."+extension;
        Path targetPath = UPLOAD_DIR.resolve(filename);
        Integer counter = 1;
        while(Files.exists(targetPath)){
               filename = basename+"-"+counter.toString()+"."+extension;
               counter++;
               targetPath = UPLOAD_DIR.resolve(filename);
        }

        try {
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new ApplicationException("Failed to save image", "IO_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        String fileUrl = "/uploads/products/" + filename;
        ProductImageModel img = new ProductImageModel();
        img.setUrl(fileUrl);
        img.setAltText(request.altText());
        img.setSortOrder(request.sortOrder());
        img.setThumbnail(request.thumbnail());
        return img;

    }

    public static String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < filename.length() - 1) {
            return filename.substring(lastDotIndex + 1).toLowerCase();
        }

        return "";
    }

    private static String getExtensionFromContentType(String contentType) {
        return switch (contentType.toLowerCase()) {
            case "image/jpeg", "image/jpg" -> "jpg";
            case "image/png" -> "png";
            case "image/webp" -> "webp";
            default -> "jpg";
        };
    }

}







