package com.rossi.testmt940.controller;

import com.rossi.testmt940.model.BankMutationRequest;
import com.rossi.testmt940.model.BaseResponse;
import com.rossi.testmt940.service.Mt940Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
public class Mt940Controller {

    @Autowired
    private Mt940Service mt940Service;


    @PostMapping("/parse")
    public ResponseEntity<BaseResponse> parse(@RequestBody String mtString) throws IOException {
        return ResponseEntity.ok(mt940Service.parse(mtString));
    }

    @PostMapping("/parse-file")
    public ResponseEntity<BaseResponse> parseFile(@RequestBody String fileName) throws IOException {
        File file = new File(fileName);
        String mtString = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
        return ResponseEntity.ok(mt940Service.parse(mtString));
    }

    @PostMapping("/generate")
    public ResponseEntity<BaseResponse> generate(@RequestBody BankMutationRequest request){
        return ResponseEntity.ok(mt940Service.generate(request));
    }

}
