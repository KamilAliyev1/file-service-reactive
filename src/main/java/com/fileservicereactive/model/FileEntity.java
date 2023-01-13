package com.fileservicereactive.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
//import org.springframework.data.annotation.Id;
//import org.springframework.data.annotation.Transient;
//import org.springframework.data.mongodb.core.mapping.Document;
//import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.*;
import org.springframework.http.MediaType;




@AllArgsConstructor
@Builder
@NoArgsConstructor
@Data
@Document(value = "fls")
public class FileEntity {

    @Id
    String name;

    String contentType = MediaType.APPLICATION_JSON_VALUE;

    @Field(targetType = FieldType.PATTERN)
    FileAccessModifier fileAccessModifier;

    String userName;

}
