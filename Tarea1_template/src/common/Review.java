package common;

public class Review {

	private int movie_id;
	private int id;
	private String review;
	
	Review(int id, int movie_id, String review){
		this.id = id;
		this.movie_id = movie_id;
		this.review = review;
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
}
