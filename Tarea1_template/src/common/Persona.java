package common;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Persona implements Serializable {
	
	private String nombre;
	private int edad;
	
	public Persona(String nombre, int edad) {
		setNombre(nombre);
		setEdad(edad);
	}
	
	public String getNombre() {
		return this.nombre;
	}
	
	public int getEdad() {
		return this.edad;
	}
	
	private void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	private void setEdad(int edad) {
		this.edad = edad;
	}
}