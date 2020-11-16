package com.kanyevselon.maven.quickstart;
//@author Anirudh Balasubramanian 2020
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
//Allows users to pick if tweet comes from one of two 
//twitter accounts using Twitter4j lib and Java
public class KanyeVsElon {
    public static void main(String[] args){
    	//Try catch block catches exceptions from 
    	//getting and parsing users' tweets
    	try {
    		Scanner input = new Scanner(System.in);
    		System.out.println("Please enter the username of the first user "
    				+ "you would like to play the tweet identifier game with "
    				+ "(recommendation: kanyewest): ");
    		String username1 = input.nextLine();
    		System.out.println("Please enter the username of the second user "
    				+ "you would like to play the tweet identifier game with "
    				+ "(recommendation: elonmusk): ");
    		String username2 = input.nextLine();
    		System.out.println("Processing");
    		//Stored in ArrayList for quick access
			ArrayList<String> FirstUserCleansedTweets = getTimeLine(username1);
			ArrayList<String> SecondUserCleansedTweets = getTimeLine(username2);
			Random random1 = new Random();
			//UserChoice indicates if user thinks first username 
			//entered sent a given tweet (1), or second username entered sent a
			//given tweet (2), (-1) to quit, else is invalid
			int userChoice = 0;
			int totalQuestions = 0;
			int score = 0;
			
			//True is Kanye, false is Elon by default
			while(userChoice != -1) {
				totalQuestions++;
				boolean firstorsecond = random1.nextBoolean();
				int randomIndex = -1;
				//Depending on boolean's value, either produces a tweet from
				//first username (1) or second username (0), and processes input
				if(firstorsecond) {
					randomIndex = random1.nextInt(FirstUserCleansedTweets.size());
					System.out.println("Guess who sent this tweet? "
							+ "(1 for first user entered, 2 for second user entered, -1 to quit):"
							+ " \n" + FirstUserCleansedTweets.get(randomIndex));
					userChoice = input.nextInt();
					if(userChoice == 1) {
						System.out.println("WIN");
						score++;
					}
					else if(userChoice == 2){
						System.out.println("LOSS");
					}
					else if(userChoice == -1){}
					else {
						System.out.println("INVALID CHOICE");
					}
				}
				else {
					randomIndex = random1.nextInt(SecondUserCleansedTweets.size());
					System.out.println("Guess who sent this tweet? "
							+ "(1 for first user entered, 2 for second user entered, -1 to quit): \n" + SecondUserCleansedTweets.get(randomIndex));
					userChoice = input.nextInt();
					if(userChoice == 2) {
						System.out.println("WIN");
						score++;
					}
					else if(userChoice == 1){
						System.out.println("LOSS");
					}
					else if(userChoice == -1){}
					else {
						System.out.println("INVALID CHOICE");
					}
				}
			}
			//Once user enters -1, program quits and overall score is given
			System.out.println("In total you got " + score + "/" + (totalQuestions-1) + " questions right");
			input.close();
		} catch (TwitterException e) {
			e.printStackTrace();
			System.out.println("ERROR DURING TWEET RETRIVAL, CHECK USERNAMES EXIST AND HAVE VALID TWEETS");
		}
    }
    //Returns ArrayList of cleansed tweets with no tags, RTs, replies, or links
    //Param is username to get up to 3200 tweets from
    public static ArrayList<String> getTimeLine(String user) throws TwitterException {
    	//Connects to twitter using credentials in properties file
    	TwitterFactory tf = new TwitterFactory();
    	Twitter twitter = tf.getInstance();
    	long lastID = 0;
    	ArrayList<String> CleansedTweets = new ArrayList<String>();
	    
    	//Statuses stores current list of up to 200 tweets
    	List<Status> statuses;
    	//Page is used to advance beyond the most recent set of tweets
        Paging page = new Paging();
        page.setCount(200);
        //Provides a list of the user's tweets given a page to start from
        statuses = twitter.getUserTimeline(user, page);
        
		int overallCount = 0;
		int count = 0;
		while(overallCount <= 3200) {
			//When we hit 200 tweets, advances page, updates statuses,
			//and resets count
			if(count % 200 == 0 && count != 0) {
				page.setMaxId(statuses.get(count-1).getId() - 1);
				statuses = twitter.getUserTimeline(user, page);
				count = 0;
			}
			//If tweet contains no tags, RTs, replies, or links, it is included in the
			//final game list of tweets
			try {
				if(statuses.get(count).getText().indexOf('@') == -1) {
					if(statuses.get(count).getText().indexOf("://") == -1) {
						CleansedTweets.add(statuses.get(count).getText());
					}
				}
				//Stores last valid ID at all times
				lastID = statuses.get(count).getId();
				count++;
				overallCount++;
			}
			catch(IndexOutOfBoundsException ex) {
				if(count == 0) {
					//Occurs if we run out of tweets
					break;
				}
				//Resets tweet pulling procedure
				//if tweets have been deleted in the past
				page.setMaxId(lastID - 1); 
				statuses = twitter.getUserTimeline(user, page); 
				count = 0;
			}
		}
		//Returns ArrayList of Strings with the list of tweets for the game
		return CleansedTweets;
	}
}
