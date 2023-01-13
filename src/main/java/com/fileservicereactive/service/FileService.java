package com.fileservicereactive.service;



import com.fileservicereactive.config.properties.FileConfigProperties;
import com.fileservicereactive.dto.FileDTO;
import com.fileservicereactive.model.FileAccessModifier;
import com.fileservicereactive.model.FileEntity;
import com.fileservicereactive.repo.FileRepo;
import com.fileservicereactive.util.AccesGrantException;
import com.fileservicereactive.util.FileExistsException;
import com.fileservicereactive.util.FileInputException;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import javax.security.auth.kerberos.KerberosPrincipal;
import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.security.Principal;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Data
public class FileService {

    @Autowired
    @Qualifier("my")
    SecurityContext securityContext2;

    private final FileConfigProperties fileConfigProperties;

    private final FileRepo fileRepo;

    public List<String> allowedDataTypes;


    @PostConstruct
    void init(){
        allowedDataTypes = fileConfigProperties.getAllowed();
    }

    @Transactional
    public Mono<FileEntity> upload(FilePart multipartFile, FileAccessModifier fileAccessModifier){

        log.info("upload args :{},{}",multipartFile,fileAccessModifier);

        checkGrant(getPrincipal().getName(),"WRITE_FILE");

        Path path = fileAccessModifier.getDocumentPath();

        String originalFilename = multipartFile.filename();

        String[] splitOriginalFilename = originalFilename.split("\\.");

        String extension = splitOriginalFilename[splitOriginalFilename.length-1];


        if(!allowedDataTypes.contains(extension)){
            throw new FileInputException("this extension type not allowed");
        }


        String name = UUID.randomUUID().toString()+multipartFile.filename();

        path = path.resolve(name);

        if(path.toFile().exists())throw new FileExistsException("this file name already exists");


        FileEntity fileEntity =
                FileEntity.builder()
                .contentType(String.valueOf(multipartFile.headers().getContentType()))
                        .fileAccessModifier(fileAccessModifier)
                        .name(name)
                        .userName(getPrincipal().getName())
                        .build();



        multipartFile.transferTo(path).subscribe();

        return fileRepo.save(fileEntity);

    }

    @SneakyThrows
    private Principal getPrincipal(){

        Principal principal = null;


        principal= new Principal() {
            @Override
            public String getName() {
                return securityContext2.getAuthentication().getPrincipal().toString();
            }
        };

        return  principal;
    }

    void checkGrant(String name,String auth){

        Set<String> auths = securityContext2.getAuthentication().getAuthorities().stream()
                .map(t->t.getAuthority()).collect(Collectors.toSet());

        if((getPrincipal().getName().equals(name) && auths.contains(auth))
        || auths.contains("ROLE_ADMIN")
        )return;

        throw new AccesGrantException();
    }

    public Mono<FileDTO> download(String name){

        log.info("download args: {}",name);

        Mono<FileEntity> entityMono = fileRepo.findById(name);

        return entityMono.map(e->{


            FileAccessModifier fileAccessModifier = e.getFileAccessModifier();

            if(fileAccessModifier.equals(FileAccessModifier.PRIVATE))
                checkGrant(e.getUserName(),"READ_FILE");

            File file = fileAccessModifier.getDocumentPath().resolve(name).toFile();

            if(!file.exists() || !file.canRead())
                throw new FileExistsException("File not exists or not readable");

            Resource resource = null;

            try {
                resource = new UrlResource(file.toURI());
            } catch (MalformedURLException s) {
                throw new RuntimeException(s);
            }


            Resource finalResource = resource;

            return FileDTO.builder()
                    .resource(finalResource)
                    .contentType(MediaType.IMAGE_JPEG)
                    .build();
        });


    }






}
