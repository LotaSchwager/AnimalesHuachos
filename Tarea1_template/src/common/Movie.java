package common;

import java.io.Serializable;
import java.util.ArrayList;

//Serializable porque usamos rmi
public class Movie implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String nombre;
	private boolean adult;
	private ArrayList<Genre> genres = new ArrayList<>();
	private String original_lang;
	private String description;
	private float popularity;
	private String release_date;
	
	public Movie(int id, String nombre, boolean adult, /*ArrayList<Genre> genres,*/ String og_lang, String dscp, float popularity, String release){
		this.id = id;
		this.nombre = nombre;
		this.adult = adult;
		//this.genres = genres;
		this.original_lang = og_lang;
		this.description = dscp;
		this.popularity = popularity;
		this.release_date = release;
	}
	
	public Movie(int id, String nombre, boolean adult, ArrayList<Genre> genres, String og_lang, String dscp, float popularity, String release){
		this.id = id;
		this.nombre = nombre;
		this.adult = adult;
		this.genres = genres;
		this.original_lang = og_lang;
		this.description = dscp;
		this.popularity = popularity;
		this.release_date = release;
	}
	
    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public boolean isAdult() {
        return adult;
    }

    public ArrayList<Genre> getGenres() {
        return genres;
    }

    public String getOriginalLang() {
        return original_lang;
    }

    public String getDescription() {
        return description;
    }

    public float getPopularity() {
        return popularity;
    }

    public String getReleaseDate() {
        return release_date;
    }
	
}
