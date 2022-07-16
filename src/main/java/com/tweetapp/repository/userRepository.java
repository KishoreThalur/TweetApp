package com.tweetapp.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.tweetapp.dao.UsersDAO;

@Repository
public interface userRepository extends MongoRepository<UsersDAO,String> {

	@Query(value="{'username':{'$regex':'?0','$options':'i'}}")
	public List<UsersDAO> findByUsername(String username);
	
	@Query(value="{'email':{'$regex':'?0'}}")
	public List<UsersDAO> findByEmail(String email);
}
