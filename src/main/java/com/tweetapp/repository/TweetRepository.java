/**
 * 
 */
package com.tweetapp.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.tweetapp.dao.TweetDAO;

/**
 * @author cogjava1087
 *
 */
@Repository
public interface TweetRepository extends MongoRepository<TweetDAO, String> {

	@Query(value="{'username':{'$regex':'?0'}}")
	public List<TweetDAO> findTweetByUsername(String username);
}
