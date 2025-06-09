package common;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Review implements Serializable{

	private int movie_id;
	private int person_id;
	private String movie_name;
	private int id;
	private String review;
	
	public Review(int id, int person_id, int movie_id, String review, String name){
		this.id = id;
		this.person_id = person_id;
		this.movie_id = movie_id;
		this.review = review;
		this.movie_name = name;
	}
	
	public void setReview(String newReview) {
		this.review = newReview;
	}
	
	public String getReview() {
		return this.review;
	}
	
	public int getMovieID() {
		return this.movie_id;
	}
	
	public int getID() {
		return this.id;
	}
	
	public int getPersonID() {
		return this.person_id;
	}
	
	public String getMovieName() {
		return this.movie_name;
	}
}
