package server;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import common.Animal;
import common.InterfazDeServer;
import common.Persona;

public class ServerImpl implements InterfazDeServer {
	
	private ArrayList<Animal> animales = new ArrayList<>();
	private ArrayList <Persona> tb_persona = new ArrayList<>();
	
	// Conexión con mysql y docker
	private String connectionURL = "jdbc:mysql://localhost:3307/bd_CPYD";
	private String user = "root";
	private String password = "1234";
	
	// Conexión con la api
	private String url = "https://huachitos.cl/api/animales";
	
	public ServerImpl() throws RemoteException {
		UnicastRemoteObject.exportObject(this,0);
		loadDB();
		loadAPI();
	}
	
	private void loadDB() {
		
        try (Connection connection = openConnection();) {
        	Statement statement = connection.createStatement();
        	ResultSet result = statement.executeQuery("SELECT * FROM tb_persona");
        	
        	while(result.next()) {
        		String nombre = result.getString("name");
        		int edad = result.getInt("age");
        		
        		Persona individuo = new Persona(nombre, edad);
        		tb_persona.add(individuo);
        	}
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
	}

	public void agregarPersona(String nombre, int edad) throws RemoteException {
		try(Connection connection = openConnection();) {
			String sql = "INSERT INTO tb_persona(name, age) VALUES (?,?)";
			
			PreparedStatement stmt = connection.prepareStatement(sql);
			
			stmt.setString(1, nombre);
			stmt.setInt(2, edad);
			stmt.executeUpdate();
			
			Persona persona = new Persona(nombre, edad);
			tb_persona.add(persona);
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void loadAPI() {
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(this.url)).GET().build();
		
		try {
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			
			if(response.statusCode() == 200) {
				
				ObjectMapper mapper = new ObjectMapper();
				JsonNode root = mapper.readTree(response.body());
				JsonNode dataArray = root.get("data");

				for (JsonNode a : dataArray) {
				    int id = a.get("id").asInt();
				    String nombre = a.get("nombre").asText();
				    String tipo = a.get("tipo").asText();
				    String edadStr = a.get("edad").asText();
				    int edadInt = extraerEdadComoEntero(edadStr);
				    String estado = a.get("estado").asText();
				    String genero = a.get("genero").asText();
				    String descFisica = a.get("desc_fisica").asText();
				    String descPersonalidad = a.get("desc_personalidad").asText();
				    String descAdicional = a.get("desc_adicional").asText();
				    int esterilizado = a.get("esterilizado").asInt();
				    int vacunas = a.get("vacunas").asInt();
				    String imagen = a.get("imagen").asText();
				    String equipo = a.get("equipo").asText();
				    String region = a.get("region").asText();
				    String comuna = a.get("comuna").asText();
				    String url = this.url;

				    Animal animal = new Animal(id, nombre, tipo, edadStr, edadInt, estado, genero,
				                                descFisica, descPersonalidad, descAdicional,
				                                esterilizado, vacunas, imagen, equipo, region, comuna, url);

				    animales.add(animal);
				}
				
			}else {
				System.out.println("Error: Código de estado HTTP " + response.statusCode());
			}
			
		}catch(IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public ArrayList<Persona> getPersona() throws RemoteException {
		return this.tb_persona;
	}
	
	@Override 
	public ArrayList<Animal> getAnimal() throws RemoteException {
		return this.animales;
	}

	@Override
	public Persona Persona(String nombre, int edad) throws RemoteException {
		// TODO Auto-generated method stub
		Persona persona = new Persona(nombre,edad);
		return persona;
	}
	
	// Entrar a la conexion de mysql
	private Connection openConnection() throws SQLException {
	    return DriverManager.getConnection(this.connectionURL, this.user, this.password);
	}
	
	// extraer el numero dentro del json
	private int extraerEdadComoEntero(String edadStr) {
	    Pattern p = Pattern.compile("\\d+");
	    Matcher m = p.matcher(edadStr);
	    if (m.find()) {
	        return Integer.parseInt(m.group());
	    }
	    return -1;
	}
}