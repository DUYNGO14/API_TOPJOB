package com.duyngo.topjob.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    // create folder
    public void createUploadFolder(String folder) throws URISyntaxException;

    // save file
    public String store(MultipartFile file, String folder) throws URISyntaxException, IOException;

    public long getFileLength(String fileName, String folder) throws URISyntaxException;

    public InputStreamResource getResource(String fileName, String folder)
            throws URISyntaxException, FileNotFoundException;
}
