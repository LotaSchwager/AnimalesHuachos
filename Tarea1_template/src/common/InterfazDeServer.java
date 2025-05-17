package common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface InterfazDeServer extends Remote {
	public String iniciarSesion(String nickname, String password) throws RemoteException;
	public boolean crearCuenta(String nickname, String name, String surname, String password) throws RemoteException;
	public void cerrarSesion(String sessionToken) throws RemoteException;
	public Persona mostrarCuenta(String sessionToken) throws RemoteException;
	public ArrayList<Movie> buscarPeliculas(String sessionToken, String criterioBusqueda) throws RemoteException;
    public ArrayList<Movie> mostrarFavoritos(String sessionToken) throws RemoteException;
    public ArrayList<Review> mostrarResenasPersonales(String sessionToken) throws RemoteException;
}