package com.bilyoner.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.bilyoner.model.Number;

public interface NumberRepository extends MongoRepository<Number, String>{

}
