package common;

import java.io.Serializable;

//Serializable porque usamos rmi
public class Genre implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String nombre;
	
	public Genre(int id, String nombre){
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
