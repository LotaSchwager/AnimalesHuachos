package client;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

import common.InterfazDeServer;
import common.Persona;
import common.Animal;

public class Client {
	private InterfazDeServer server;
	public Client() {};
	
	public void startClient() throws RemoteException, NotBoundException{
		Registry registry = LocateRegistry.getRegistry("localhost",1009);
		setServer((InterfazDeServer) registry.lookup("server"));
	}
	
	public void mostrarPersonas() throws RemoteException {
		ArrayList<Persona> personas = getServer().getPersona();	
		System.out.println("\nPersonas dentro de la lista");
		System.out.println("========================");
		for(int i = 0; i < personas.size(); i++) 
		{
			System.out.println(i + " " + personas.get(i).getNombre() + " " + personas.get(i).getEdad());
		}
		System.out.println("========================\n");
	}
	
	public void mostrarAnimales() throws RemoteException{
		ArrayList<Animal> animales = getServer().getAnimal();
		System.out.println("\nAnimales en adopción");
		System.out.println("========================");
		for(int i = 0; i < animales.size(); i++) 
		{
			System.out.println(i + " " + animales.get(i).getNombre());
		}
		System.out.println("========================\n");
	}

	public InterfazDeServer getServer() {
		return this.server;
	}

	public void setServer(InterfazDeServer server) {
		this.server = server;
	}
}