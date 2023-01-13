package com.fileservicereactive.contoller;


import com.fileservicereactive.dto.FileDTO;
import com.fileservicereactive.model.FileAccessModifier;
import com.fileservicereactive.model.FileEntity;
import com.fileservicereactive.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.concurrent.ExecutionException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/")
public class FileController {

    private final FileService fileService;

    @PostMapping
    public Mono<FileEntity> upload(@RequestPart("file") FilePart filePart
            , @RequestParam("modifier") FileAccessModifier fileAccessModifier){

        return fileService.upload(filePart, fileAccessModifier);
    }

    @GetMapping
    public Mono<ResponseEntity<Resource>> download(@RequestParam("name") String name) {

        return fileService.download(name)
                .map(fileDTO ->  ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION,"attachment;filename=\""+name+"\"")
                        .contentType(fileDTO.getContentType())
                        .body(fileDTO.getResource())
                );
    }

}
