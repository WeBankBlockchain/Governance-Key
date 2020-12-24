package com.webank.controller;

import com.webank.model.R;
import com.webank.service.KeyGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("api/keygen")
public class KeyGeneratorController {

    @Autowired
    private KeyGeneratorService service;

    @GetMapping("random")
    public R random(@RequestParam("curve") String curve) throws Exception{
        return service.random(curve);
    }

    @GetMapping("com.webank.keygen.mnemonic")
    public R mnemonic(){
        return service.mnemonic();
    }


    @PostMapping("transform")
    public void transform(MultipartFile file, String password, String tgtFormat, HttpServletResponse response) throws Exception{
        service.transform(file.getBytes(), file.getOriginalFilename(), password, tgtFormat, response);
    }



    @GetMapping("encrypt")
    public void downloadEncryptKey(@RequestParam("privKey") String privKey,
                           @RequestParam("eccType") String eccType,
                           @RequestParam("encType") String encType,
                           @RequestParam(value = "password")String password,
                           HttpServletResponse response) throws Exception{
        service.downloadEncryptKey(privKey, eccType, encType, password, response);
    }

    @PostMapping("decrypt")
    public ResponseEntity<R> decryptEncryptKey(MultipartFile file, @RequestParam("password") String password, HttpServletRequest request) throws Exception {
        R r =  service.decryptFile(file.getBytes(), file.getOriginalFilename(), password);
        return ResponseEntity.ok(r);
    }

    @GetMapping("detail")
    public ResponseEntity<R> getKeyDetail(@RequestParam("privKey")String privKey, @RequestParam("eccType")String eccType)
            throws Exception{
        R r = service.getKeyDetail(privKey, eccType);
        return ResponseEntity.ok(r);
    }


}












