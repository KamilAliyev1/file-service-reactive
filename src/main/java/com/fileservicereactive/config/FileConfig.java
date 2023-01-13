package com.fileservicereactive.config;



import com.fileservicereactive.config.properties.FileConfigProperties;
import com.fileservicereactive.config.properties.PathConfigProperties;
import com.fileservicereactive.model.FileAccessModifier;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


@Configuration
@RequiredArgsConstructor
public class FileConfig {

    private final PathConfigProperties pathConfigProperties;

    @PostConstruct
    void init(){
        Path generalDocumentPath = Paths.get(pathConfigProperties.getGeneral().get("documents"));
        FileAccessModifier.PUBLIC.setDocumentPath(generalDocumentPath);
        FileAccessModifier.PRIVATE.setDocumentPath(generalDocumentPath);
    }
}
