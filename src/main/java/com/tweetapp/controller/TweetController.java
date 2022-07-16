package com.tweetapp.controller;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tweetapp.TweetListener;
import com.tweetapp.dao.TweetDAO;
import com.tweetapp.dao.UsersDAO;
import com.tweetapp.model.TweetRequest;
import com.tweetapp.repository.TweetRepository;
import com.tweetapp.repository.userRepository;

@RestController
@RequestMapping("api/v1.0/tweets")
public class TweetController {

	
	Logger log=LoggerFactory.getLogger(TweetController.class);
	@Autowired
	userRepository userRepo;
	@Autowired
	TweetRepository tweetRepo;
	
	private KafkaTemplate<String,String> kafkaTemplate;
	
	public TweetController(KafkaTemplate<String,String> kafkaTemplate){
		log.info("Called KafkaTemplate");
		this.kafkaTemplate=kafkaTemplate;
	}
	
	/*
	 * User Registered
	 * */
	@PostMapping("/register")
	public ResponseEntity<?> registerUser(@RequestBody UsersDAO user) {
		String email=user.getEmail();
		if(email!=null && !"".equals(email)){
			List<UsersDAO> userT=userRepo.findByEmail(email);
			List<UsersDAO> usernameT=userRepo.findByUsername(user.getUsername());
			if(userT.isEmpty() && usernameT.isEmpty()) {
				user.setCreatedAt(new Date(System.currentTimeMillis()));
				userRepo.save(user);
				kafkaTemplate.send("tweets","User Created For Username :");
				log.info("User Created For Username ");
				return new ResponseEntity<UsersDAO>(user,HttpStatus.OK);
			}
			
		}
		return new ResponseEntity<UsersDAO>(user,HttpStatus.NOT_ACCEPTABLE);
		
	}
	
	@PostMapping("/login")
	public ResponseEntity<?> LoginUser(@RequestBody UsersDAO user) {
		
		String email=user.getEmail();
		String password=user.getPassword();
		List<UsersDAO> userT=null;
		if(email!=null && !"".equals(email)){
			userT=userRepo.findByEmail(email);
//			List<UsersDAO> usernameT=userRepo.findByEmail(email);
			if(!userT.isEmpty() && userT.get(0).getPassword().equals(password)) {
				
				kafkaTemplate.send("tweets","User Created For Username :");
				log.info("User Created For Username ");
				return new ResponseEntity<UsersDAO>(userT.get(0),HttpStatus.OK);
			}
			
		}
		return new ResponseEntity<UsersDAO>(HttpStatus.NO_CONTENT);
	}
	
	/*
	 * Forgot Password
	 * */
	@GetMapping("/{username}/forgot")
	public ResponseEntity<?> frogotUser( @PathVariable("username") String username) {
		//Check the password
		Optional<UsersDAO> userResult=userRepo.findById(username);
		log.info("User - " +username+" forgot their password");
		kafkaTemplate.send("tweets","User forgot their password");
		return new ResponseEntity<>(userResult,HttpStatus.OK);
	}
	
	/*
	 * View all the tweets
	 * */
	@GetMapping("/all")
	public ResponseEntity<?> allTweets() {
		//Query to return all tweets
		List<TweetDAO> tweetList=tweetRepo.findAll();
		kafkaTemplate.send("tweets","User viewed all tweets");
		log.debug("User viewed all the tweets ");
		if(!tweetList.isEmpty())
		return new ResponseEntity<>(tweetList,HttpStatus.OK);
		else {
			return new ResponseEntity<>("No Tweets Obtained",HttpStatus.OK);
		}
	}
	/*
	 * Get All Users
	 * */
	@GetMapping("/users/all")
	public ResponseEntity<?> viewAllUsers() {
		
		List<UsersDAO> userList=userRepo.findAll();
		log.info("View All Users ");
		kafkaTemplate.send("tweets","Viewed all users");
		if(!userList.isEmpty())
		return new ResponseEntity<>(userList,HttpStatus.OK);
		else {
			return new ResponseEntity<>("No Tweets Obtained",HttpStatus.OK);
		}

		
	}
	
	/*
	 * View Users with the username
	 * */
	@GetMapping("/user/search/{username}")
	public ResponseEntity<?> searchUsers(@PathVariable("username") String id) {
		List<UsersDAO> userResult=userRepo.findByUsername(id);
		
		log.info("Search Users... ");
		kafkaTemplate.send("tweets","Searching for tweet");
		if(!userResult.isEmpty()) {
		return new ResponseEntity<>(userResult,HttpStatus.OK);
		}
		else {
			return new ResponseEntity<>("No Tweet Found with id"+id,HttpStatus.OK);
		}
	}
	
	/*
	 * View Tweet with the Id
	 * */
	@GetMapping("/search/{id}")
	public ResponseEntity<?> searchTweet(@PathVariable("id") String id) {
		Optional<TweetDAO> tweetResult=tweetRepo.findById(id);
		log.info("Search using tweetid ");
		kafkaTemplate.send("tweets","Searching for tweet");
		if(tweetResult.isPresent()) {
		return new ResponseEntity<>(tweetResult,HttpStatus.OK);
		}
		else {
			return new ResponseEntity<>("No Tweet Found with id"+id,HttpStatus.OK);
		}
	}
	
	/*
	 * Get All Tweets of Users
	 * */
	@GetMapping("{username}")
	public ResponseEntity<?> getUserTweets(@PathVariable("username") String username) {

		log.info("Get All tweets of user - "+username);
		List<TweetDAO> tweetResult=tweetRepo.findTweetByUsername(username);
		kafkaTemplate.send("tweets","View user tweet");
		if(!tweetResult.isEmpty()) {
		return new ResponseEntity<>(tweetResult,HttpStatus.OK);
		}
		else {
			return new ResponseEntity<>("No Tweets by : "+username,HttpStatus.OK);
		}
		
	}
	
	/*
	 *Add Tweet 
	 */
	@PostMapping("/{username}/add")
	public ResponseEntity<?> addTweet(@RequestBody TweetDAO tweet, @PathVariable("username") String username) {
		
		log.info("Add tweet ");
		tweet.setCreatedAt(new Date(System.currentTimeMillis()));
		tweetRepo.save(tweet);
		kafkaTemplate.send("tweets",tweet.getTweet());
		return new ResponseEntity<TweetDAO>(tweet,HttpStatus.OK);
		
	}
	
	/*
	 * Tweet Updated
	 * */
	@PutMapping("/{username}/update/{id}")
	public ResponseEntity<?> updateTweet(@RequestBody TweetDAO tweet,@PathVariable("id") String id, @PathVariable("username") String username) {
		
		log.info("Update Tweet ");
		TweetDAO oldTweet=new TweetDAO();
		Optional<TweetDAO> tweetResult=tweetRepo.findById(id);
		kafkaTemplate.send("tweets","Tweet Updated for User :");
		if(tweetResult.isPresent()) {
			oldTweet=tweetResult.get();
			oldTweet.setTweet(tweet.getTweet());
			oldTweet.setUpdatedAt(new Date(System.currentTimeMillis()));
			tweetRepo.save(oldTweet);
			return new ResponseEntity<TweetDAO>(HttpStatus.OK);
		}
		else {
			return new ResponseEntity<TweetDAO>(HttpStatus.OK);
		}

	}
	
	/*
	 * Tweet Deleted
	 * */
	@DeleteMapping("/{username}/delete/{id}")
	public ResponseEntity<?> deleteTweet(@PathVariable("id") String id, @PathVariable("username") String username) {
		tweetRepo.deleteById(id);
		log.info("--- Tweet Deleted --- ");
		kafkaTemplate.send("tweets","Delete tweet");
		return new ResponseEntity<TweetDAO>(HttpStatus.OK);
	}
	
	/*
	 * Tweet Liked
	 * */
	@PutMapping("/{username}/like/{id}")
	public ResponseEntity<?> likeTweet(@PathVariable("id") String id, @PathVariable("username") String username) {
		TweetDAO tweet=new TweetDAO();
		log.info("Tweet Liked ");
		Optional<TweetDAO> tweetResult=tweetRepo.findById(id);
		kafkaTemplate.send("tweets","Tweet Liked");
		if(tweetResult.isPresent()) {
			tweet=tweetResult.get();
			tweet.setLikes(tweet.getLikes()+1);
			tweet.setUpdatedAt(new Date(System.currentTimeMillis()));
			tweetRepo.save(tweet);
			return new ResponseEntity<TweetDAO>(HttpStatus.OK);
		}
		else {
			return new ResponseEntity<>("No Such Tweet",HttpStatus.OK);
		}

		
	}
	
	/*
	 * Replied to Tweet
	 * */
	@PostMapping("/{username}/reply/{id}")
	public ResponseEntity<?> replyTweet(@RequestBody TweetDAO tweet,@PathVariable("id") String id, @PathVariable("username") String username) {
		log.info("Replied To Tweet ");
		tweet.setCreatedAt(new Date(System.currentTimeMillis()));
		tweet.setReplyTo(id);
		tweetRepo.save(tweet);
		kafkaTemplate.send("tweets","Replied to Tweet -"+tweet.getUsername());
		return new ResponseEntity<TweetDAO>(tweet,HttpStatus.OK);
	}
	
	
}
