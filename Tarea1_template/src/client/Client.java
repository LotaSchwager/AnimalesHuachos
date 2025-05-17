package client;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

import common.InterfazDeServer;
import common.Persona;

public class Client {
	
	// ServerImpl
	private InterfazDeServer server;
	
	// Sesion token
	private String sessionToken = null;
	
	public Client() {};
	
	public void startClient() throws RemoteException, NotBoundException{
		Registry registry = LocateRegistry.getRegistry("localhost",1009);
		setServer((InterfazDeServer) registry.lookup("server"));
	}

	public InterfazDeServer getServer() {
		return this.server;
	}

	public void setServer(InterfazDeServer server) {
		this.server = server;
	}
	
	// ---  Funciones del servidor en accion ---

    /**
     * Llama al método remoto para iniciar sesión.
     * Almacena el token si es exitoso.
     * @return true si el login fue exitoso (se obtuvo un token), false en caso contrario.
     */
    public boolean login(String nickname, String password) throws RemoteException {
        System.out.println("Cliente: Intentando iniciar sesión con " + nickname);
        this.sessionToken = getServer().iniciarSesion(nickname, password);
        return this.sessionToken != null;
    }

    /**
     * Llama al método remoto para crear una nueva cuenta.
     * @return true si la cuenta fue creada con éxito, false si falla.
     */
    public boolean createAccount(String nickname, String name, String surname, String password) throws RemoteException {
        System.out.println("Cliente: Intentando crear cuenta para " + nickname);
        return getServer().crearCuenta(nickname, name, surname, password);
    }

     /**
     * Llama al método remoto para mostrar la información de la cuenta logeada.
     * Requiere pasar el token de sesión.
     * @return El objeto Persona con los datos de la cuenta logeada.
     */
    public Persona mostrarCuenta() throws RemoteException {
         if (this.sessionToken == null) {
             throw new RemoteException("No hay sesión iniciada en el cliente.");
         }
         return getServer().mostrarCuenta(this.sessionToken);
    }


     /**
     * Llama al método remoto para cerrar la sesión actual.
     */
    public void logout() throws RemoteException {
        if (this.sessionToken != null) {
             System.out.println("Cliente: Intentando cerrar sesión con token...");
            getServer().cerrarSesion(this.sessionToken);
            this.sessionToken = null;
             System.out.println("Cliente: Token de sesión limpiado.");
        }
    }

}