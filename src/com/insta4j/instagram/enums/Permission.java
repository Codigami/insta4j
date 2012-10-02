package com.insta4j.instagram.enums;

public enum Permission {
	
	/**
	 * to read any and all data related to a user (e.g. following/followed-by lists, photos, etc.) (granted by default)
	 */
	BASIC, 
	
	/**
	 * to create or delete comments on a user’s behalf
	 */
	COMMENTS , 
	
	/**
	 * to follow and unfollow users on a user’s behalf
	 */
	RELATIONSHIPS, 
	
	/**
	 * to like and unlike items on a user’s behalf
	 */
	LIKES; 
	
}
