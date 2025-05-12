package common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface InterfazDeServer extends Remote {
	public ArrayList<Persona> getPersona() throws RemoteException;
	public Persona Persona(String nombre, int edad) throws RemoteException;
	public void agregarPersona(String nombre, int edad) throws RemoteException;
	public ArrayList<Animal> getAnimal() throws RemoteException;
}