package com.fileservicereactive.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.http.MediaType;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@Data
public class FileDTO {

    Resource resource;

    MediaType contentType;

}
