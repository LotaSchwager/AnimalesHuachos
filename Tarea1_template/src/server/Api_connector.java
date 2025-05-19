package server;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Api_connector {
	
	// Conexión con la api
	private String auth_url = "https://api.themoviedb.org/3/authentication";
	private String genre_list = "https://api.themoviedb.org/3/genre/movie/list?language=es";
	private String movie_list = "https://api.themoviedb.org/3/discover/movie?include_adult=false&include_video=false&language=es&page=1";
	private String movie_get = "https://api.themoviedb.org/3/movie/2?language=es";
	
	// Credenciales de la api
	private ArrayList<String> header_accept = new ArrayList<>(List.of("accept", "application/json"));
	private ArrayList<String> header_auth = new ArrayList<>
		(List.of("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI2ODE3NTAxNDI4YzQ1NDg1NGU4OTcxMzg5MGEwZDkxYiIsIm5iZiI6MTc0NzM2MDU1MC41MjEsInN1YiI6IjY4MjY5YjI2ZmJmYmQyMGFjYjJkMzAxOSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.LofHm7zasFRxpI-amZQbKMhhRxbigB8bBz-cKLk305g"));
	
	// Costructor
	public Api_connector(){}
	
	// Funcion para saber si se conecto con la API
	private void authAPI() throws RemoteException {
		try {
			HttpClient client = HttpClient.newHttpClient();
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(auth_url))
					.header(this.header_accept.get(0), this.header_accept.get(1))
					.header(this.header_auth.get(0), this.header_auth.get(1))
					.method("GET", HttpRequest.BodyPublishers.noBody())
					.build();
			
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			
			System.out.println(response.body());
		}catch(Exception e) {
			System.out.println(e);
		}
	}
	
	// Funcion para obtener una lista de generos
	public void get_Genre_List() {
		try {
			HttpRequest request = HttpRequest.newBuilder()
				    .uri(URI.create(genre_list))
					.header(this.header_accept.get(0), this.header_accept.get(1))
					.header(this.header_auth.get(0), this.header_auth.get(1))
					.method("GET", HttpRequest.BodyPublishers.noBody())
					.build();
			
				HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
				System.out.println(response.body());
		}catch(Exception e) {
			System.out.println(e);
		}
	}
	
	// Funcion para obtener una lista de peliculas.
	
	/*
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
	*/
	
	// Utilidad
	// extraer el numero dentro del json
	/*
	private int extraerEdadComoEntero(String edadStr) {
	    Pattern p = Pattern.compile("\\d+");
	    Matcher m = p.matcher(edadStr);
	    if (m.find()) {
	        return Integer.parseInt(m.group());
	    }
	    return -1;
	}*/
}
