package com.fileservicereactive.repo;

import com.fileservicereactive.model.FileEntity;

import org.springframework.core.annotation.Order;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;



@Repository
public interface FileRepo extends ReactiveMongoRepository<FileEntity,String >
{
}
