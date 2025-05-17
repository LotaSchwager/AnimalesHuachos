package common;

public class Genre {
	
	private int id;
	private String nombre;
	
	Genre(int id, String nombre){
		this.id = id;
		this.nombre = nombre;
	}
	
	// Getter de id
	public int getID() {
		return this.id;
	}
	
	// Getter de nombre
	public String getNombre() {
		return this.nombre;
	}

}
