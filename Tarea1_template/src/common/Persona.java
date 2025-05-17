package common;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class Persona implements Serializable {
	
	private int id;
	private String nickname;
	private String name;
	private String surname;
	private String password;
	private ArrayList<Movie> movies = new ArrayList<>();
	private ArrayList<Review> reviews = new ArrayList<>();
	
	// Constructor de todas las variables
	public Persona(int id,String nickname, String name, String surname, String password, ArrayList<Movie> movies, ArrayList<Review> reviews) {
		this.id = id;
		this.nickname = nickname;
		this.name = name;
		this.surname = surname;
		this.password = password;
		this.movies = movies;
		this.reviews = reviews;
	}
	
	// Constructor para la tabla Persona
	public Persona(int id,String nickname, String name, String surname, String password) {
		this.id = id;
		this.nickname = nickname;
		this.name = name;
		this.surname = surname;
		this.password = password;
	}
	
	// ======================== id ================== //
	public int getID() {
		return this.id;
	}
	// ================================================= //
	
	// ======================== Nickname ================== //
	public String getNickname() {
		return this.nickname;
	}
	
	public void setNickname(String newNickname) {
		this.nickname = newNickname;
	}
	// ================================================= //
	
	// ======================== Nombre ================== //
	public String getNombre() {
		return this.name;
	}
	
	public void setNombre(String nombre) {
		this.name = nombre;
	}
	// ================================================= //
	
	// ======================== Apellido ================== //
	public String getSurname() {
		return this.surname;
	}
	
	public void setSurname(String newSurname) {
		this.surname = newSurname;
	}
	// ================================================= //
	
	// ======================== Contraseña ================== //
	public String getPassword() {
		return this.password;
	}
	
	public void setPassword(String newPassword) {
		this.password = newPassword;
	}
	// ================================================= //
	
	// ======================== Lista de peliculas ================== //
	public ArrayList<Movie> getMovies() {
		return this.movies;
	}
	
	public void setMovies(ArrayList<Movie> movies) {
		this.movies = movies;
	}
	// ================================================= //
	
	// ======================== Reseñas de la persona ================== //
	public ArrayList<Review> getReviews() {
		return this.reviews;
	}
	
	public void setReview(ArrayList<Review> reviews) {
		this.reviews = reviews;
	}
	// ================================================= //
	
}