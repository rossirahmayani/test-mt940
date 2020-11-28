package com.rossi.testmt940.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;

@Service
@Slf4j
public class GenerateFileService {

    @Value("${mt940.local.directory}")
    private String localDir;

    @Value("${mt940.fileName}")
    private String fileName;

    public void generateFile(String mtString){
        try(FileOutputStream o = new FileOutputStream(localDir.concat(fileName))){
            o.write(mtString.getBytes());
        }
        catch (Exception e){
            log.warn("Error create file");
        }
    }
}
