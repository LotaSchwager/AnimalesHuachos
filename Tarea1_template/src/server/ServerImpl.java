package server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import common.InterfazDeServer;
import common.Movie;
import common.Persona;
import common.Review;

public class ServerImpl implements InterfazDeServer {
	
	// Funciones
	private DB_connector context;
	private Api_connector api;
	
	// Sesiones activas
	private Map<String, Persona> sesionesActivas;
	
	
	// Funcion que inicializa el server
	public ServerImpl() throws RemoteException {
		UnicastRemoteObject.exportObject(this,0);
		this.context = new DB_connector();
		this.api = new Api_connector();
		this.sesionesActivas = new ConcurrentHashMap<>();
	}

	@Override
	public String iniciarSesion(String nickname, String password) throws RemoteException {
		System.out.println("Intento de Login: " + nickname);
		try {
            // Usar el DB_connector para validar credenciales en la BD
            Persona personaLogeada = context.validarCredenciales(nickname, password);

            if (personaLogeada != null) {
            	
                 // Verificar si ya tiene una sesión activa y si quieres permitirla o invalidar la anterior
            	// En proceso

                // Credenciales válidas: generar token, almacenar sesión y retornar token
                String sessionToken = UUID.randomUUID().toString();
                sesionesActivas.put(sessionToken, personaLogeada);

                System.out.println("Login exitoso para " + nickname + ". Token: " + sessionToken);
                return sessionToken;
            } else {
                // Credenciales inválidas
                System.out.println("Login fallido para " + nickname + ": Credenciales inválidas.");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RemoteException("Error de base de datos durante el login", e);
        }
	}

	@Override
	public boolean crearCuenta(String nickname, String name, String surname, String password) throws RemoteException {
		System.out.println("Intento de crear cuenta para: " + nickname);
        try {
            int idGenerado = context.crearPersonaEnBD(nickname, name, surname, password);

            // Si se obtuvo un ID generado, la creación fue exitosa
            if (idGenerado != -1) {
                System.out.println("Cuenta creada con éxito para " + nickname + " (ID: " + idGenerado + ")");
                
                // Si quiero obtener la persona nueva se podria con esto, pero por ahora no se me ocurre un uso
                // Persona nuevaPersona = context.getPersonaById(idGenerado);
                // Algo .....
                
                return true;
            } else {
                System.out.println("Fallo al crear cuenta para " + nickname);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RemoteException("Error de base de datos al crear cuenta", e);
        }
	}

	@Override
	public void cerrarSesion(String sessionToken) throws RemoteException {
		Persona persona = sesionesActivas.remove(sessionToken);
        if (persona != null) {
            System.out.println("Sesión cerrada para usuario: " + persona.getNickname());
        } else {
            System.out.println("Intento de cerrar sesión con token inválido.");
        }
	}

	@Override
	public Persona mostrarCuenta(String sessionToken) throws RemoteException {
		System.out.println("Solicitud mostrarCuenta con token: " + sessionToken);
        // Validar el token de sesión
        Persona personaLogeada = validarTokenSesion(sessionToken);

        // Si llegamos aquí, el token es válido y tenemos la Persona
        System.out.println("Mostrando cuenta para usuario: " + personaLogeada.getNickname());

        // Otra opcion
        // return context.getPersonaById(personaLogeada.getId());
        return personaLogeada;
	}

	@Override
	public ArrayList<Movie> buscarPeliculas(String sessiontoken, String genero, int anio, float rating) throws RemoteException {

	    // Traduce el nombre del género (ej. "Action") a su ID (ej. "28")
	    String genreId = convertirNombreAGeneroId(genero);

	    // Llama a la API con los filtros
	    Api_connector api = new Api_connector();
	    ArrayList<Movie> resultados = (ArrayList<Movie>) api.getMovieList(genreId, anio, rating);

	    return resultados;
	}



	@Override
	public ArrayList<Movie> mostrarFavoritos(String sessionToken) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Review> mostrarResenasPersonales(String sessionToken) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}
	
	// Utilidad
	private Persona validarTokenSesion(String sessionToken) throws RemoteException {
        if (sessionToken == null || sessionToken == "") {
             throw new RemoteException("Acceso denegado: No se proporcionó token de sesión.");
        }
       Persona persona = sesionesActivas.get(sessionToken);
       if (persona == null) {
           // Token no encontrado o sesión expirada
           throw new RemoteException("Sesión inválida o expirada. Por favor, inicie sesión de nuevo.");
       }
       // Token válido, retorna la Persona asociada
       return persona; 
   }
	
	//Conviernte el genero a su id
	private String convertirNombreAGeneroId(String nombre) {
	    if (nombre == null) return "";

	    Map<String, String> mapa = Map.ofEntries(
	    	    Map.entry("Action", "28"),
	    	    Map.entry("Adventure", "12"),
	    	    Map.entry("Animation", "16"),
	    	    Map.entry("Comedy", "35"),
	    	    Map.entry("Crime", "80"),
	    	    Map.entry("Documentary", "99"),
	    	    Map.entry("Drama", "18"),
	    	    Map.entry("Family", "10751"),
	    	    Map.entry("Fantasy", "14"),
	    	    Map.entry("History", "36"),
	    	    Map.entry("Horror", "27"),
	    	    Map.entry("Music", "10402"),
	    	    Map.entry("Mystery", "9648"),
	    	    Map.entry("Romance", "10749"),
	    	    Map.entry("Science Fiction", "878"),
	    	    Map.entry("TV Movie", "10770"),
	    	    Map.entry("Thriller", "53"),
	    	    Map.entry("War", "10752"),
	    	    Map.entry("Western", "37")
	    	);


	    return mapa.getOrDefault(nombre.trim(), ""); // Devuelve "" si no se reconoce el nombre
	}

	
}