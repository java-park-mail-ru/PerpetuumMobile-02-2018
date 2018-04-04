package server.services;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import server.storage.StorageException;
import server.storage.StorageFileNotFoundException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;


import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;


@Service
public class FileSystemStorageService implements StorageService {

    private StorageProperties storageProperties;

    private void createFileUploadDirectory() {
        if (!Files.exists(storageProperties.getPath())) {
            try {
                Files.createDirectory(storageProperties.getPath());
            } catch (IOException e) {
                throw new StorageException("Could not initialize storage", e);
            }
        }
    }

    public FileSystemStorageService() {
        storageProperties = new StorageProperties("upload-dir");
        createFileUploadDirectory();
    }

    @Override
    public void store(MultipartFile file, String fileName) {
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file " + file.getOriginalFilename());
            }

            Files.copy(file.getInputStream(), this.storageProperties.getPath().resolve(fileName), REPLACE_EXISTING);
        } catch (IOException e) {
            throw new StorageException("Failed to store file " + file.getOriginalFilename(), e);
        }
    }

    @Override
    public Path load(String filename) {
        return storageProperties.getPath().resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new StorageFileNotFoundException("Could not read file: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }

    @Override
    public void delete(String fileName) {
        try {
            Files.delete(storageProperties.getPath().resolve(fileName));
        } catch (IOException e) {
            throw new StorageException("Failed to delete file " + fileName, e);
        }
    }

}
