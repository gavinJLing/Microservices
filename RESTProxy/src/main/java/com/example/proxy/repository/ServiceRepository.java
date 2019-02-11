package com.example.proxy.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.proxy.model.ServiceDefinition;

public interface ServiceRepository extends MongoRepository<ServiceDefinition, String> {
    // Get Service config where name = logical request Name (expect 1)
    List<ServiceDefinition> findByName(String LogicalName);
}