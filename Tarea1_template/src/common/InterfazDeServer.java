package common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface InterfazDeServer extends Remote {
	public String iniciarSesion(String nickname, String password) throws RemoteException;
	public boolean crearCuenta(String nickname, String name, String surname, String password) throws RemoteException;
	public void cerrarSesion(String sessionToken) throws RemoteException;
	public Persona mostrarCuenta(String sessionToken) throws RemoteException;
	public ArrayList<Movie> buscarPeliculas(String sessionToken, String genero, int anio, float rating) throws RemoteException;
    public ArrayList<Movie> mostrarFavoritos(String sessionToken) throws RemoteException;
    public ArrayList<Review> mostrarResenasPersonales(String sessionToken) throws RemoteException;
    public Boolean[] editCuenta(String nombre, String surname, String nickname, int id) throws RemoteException;
    public Movie agregarMovieFav(int id, int movie_id) throws RemoteException;
    public Review agregarReview(int id, Movie movie, String texto) throws RemoteException;
    public Movie getMovieByID(int id) throws RemoteException;
}