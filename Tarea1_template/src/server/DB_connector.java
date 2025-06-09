package server;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Objects;

import common.Movie;
import common.Persona;
import common.Review;

public class DB_connector {

	// Conexión con mysql y docker
	private String connectionURL = "jdbc:mysql://localhost:3307/bd_CPYD";
	private String user = "root";
	private String password = "1234";
	
	// Constructor
	public DB_connector(){}
	
	// ============================== Verificar ==================================================//
	
	/**
	 * Busca una Persona en la lista basándose en nickname y password.
	 *
	 * @param nickname: El nickname a buscar.
	 * @param password: El password a buscar.
	 * @return El objeto Persona si se encuentra una coincidencia en la BD, null en caso contrario.
	 * @throws SQLException Si ocurre un error de base de datos.
	 */
	public Persona validarCredenciales(String nickname, String password) throws SQLException {
	
		String sql = "SELECT id, name, surname, created_at, updated_at FROM tb_persona WHERE nickname = ? AND password_hash = ?";
		
		//System.out.print("Nickname: " + nickname + " password_hash: " + password);
	
		try (Connection connection = openConnection();
	         PreparedStatement stmt = connection.prepareStatement(sql)) {
	
			stmt.setString(1, nickname);
			stmt.setString(2, password);
	
			try (ResultSet result = stmt.executeQuery()) {
				if (result.next()) {
					
					// Si encontramos una fila, creamos y retornamos el objeto Persona
					int id = result.getInt("id");
					String name = result.getString("name");
					String surname = result.getString("surname");
                    Timestamp createdAtTime = result.getTimestamp("created_at");
                    Timestamp updatedAtTime = result.getTimestamp("updated_at");
                    
                    ArrayList<Movie> favoritos = getFavorites(id);
                    ArrayList<Review> resena =  getReviews(id);
                    
					return new Persona(id, nickname, name, surname, password, favoritos, resena, createdAtTime.toLocalDateTime(), updatedAtTime.toLocalDateTime());
				}
			}
		}catch(SQLException e) {
			System.out.println(e);
		}
		// No se encontró ninguna persona con esas credenciales
		return null; 
	}
	
	/**
     * Busca una Persona en la BASE DE DATOS por su ID.
     * Útil para obtener los datos completos de la persona desde la BD si solo tienes el ID.
     *
     * @param id El ID de la Persona a buscar.
     * @return El objeto Persona si se encuentra, null en caso contrario.
     * @throws SQLException Si ocurre un error de base de datos.
     */
	public Persona getPersonaById(int id) throws SQLException {
		String sql = "SELECT id, nickname, name, surname, password_hash FROM tb_persona WHERE id = ?";
	    try (Connection connection = openConnection();
	          PreparedStatement stmt = connection.prepareStatement(sql)) {
	         stmt.setInt(1, id);
	         try (ResultSet result = stmt.executeQuery()) {
	             if (result.next()) {
	                 String dbNickname = result.getString("nickname");
	                 String dbName = result.getString("name");
	                 String dbSurname = result.getString("surname");
	                 String dbPassword = result.getString("password_hash");
	                 Timestamp createdAtTime = result.getTimestamp("created_at");
	                 Timestamp updatedAtTime = result.getTimestamp("updated_at");
		
	                 return new Persona(id, dbNickname, dbName, dbSurname, password, createdAtTime.toLocalDateTime(), updatedAtTime.toLocalDateTime());
	             }
	        }
	    }
	    // No encontrado
	    return null;
	}
	
	// ======================================================================================//
	
	// ============================ Crear y Cambiar =========================================//
	
     /**
      * Crea una nueva cuenta para una persona en la BASE DE DATOS.
      *
      * @param nickname El nickname de la nueva persona.
      * @param name El nombre de la nueva persona.
      * @param surname El apellido de la nueva persona.
      * @param password La contraseña de la nueva persona (debería ser el hash).
      * @return El ID generado para la nueva persona, o -1 si falla.
      * @throws SQLException Si ocurre un error de base de datos.
      */
	 public int crearPersonaEnBD(String nickname, String name, String surname, String password) throws SQLException {
	    int idGenerado = -1;

	    // Primero verificar si el nickname ya existe para evitar duplicados
	    if (nicknameExists(nickname)) {
	        System.err.println("Error: El nickname '" + nickname + "' ya existe.");
	        return -1;
	    }

	    String sql = "INSERT INTO tb_persona(nickname, name, surname, password_hash) VALUES (?,?,?,?)";

	    try (Connection connection = openConnection();
	         PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

	        stmt.setString(1, nickname);
	        stmt.setString(2, name);
	        stmt.setString(3, surname);
	        stmt.setString(4, password);

	        // Deberá entregar la cantidad de filas agregadas que debería ser uno.
	        int filaAfectada = stmt.executeUpdate();

	        // Verifica si al menos una fila fue añadida
	        if (filaAfectada > 0) {
	            try (ResultSet rs = stmt.getGeneratedKeys()) {
	                if (rs.next()) {
	                    // Recupera el ID
	                    idGenerado = rs.getInt(1);
	                    System.out.println("Persona insertada con ID: " + idGenerado);
	                } else {
	                    System.err.println("Error: No se obtuvo ID generado para la inserción.");
	                    idGenerado = -1;
	                }
	            }
	        } else {
	            System.err.println("Error: La inserción en la base de datos falló (0 filas afectadas).");
	            idGenerado = -1;
	        }

	        return idGenerado;

	    } catch (SQLException e) {
	        e.printStackTrace();
	        throw e;
	    }
	 }
	 
 	/**
     * Cambiar una columna de Usuario
     * @param nombre: El nuevo nombre a cambiar.
     * @param id: El id del usuario.
     * @return true si se realizo el cambio, false en caso contrario.
     * @throws SQLException Si ocurre un error de base de datos.
     */
	 public boolean editName(String nombre, int id) {
		 
	    String sql = "UPDATE tb_persona SET name = ? WHERE id = ?";
	    
	    try (Connection connection = openConnection();
		         PreparedStatement stmt = connection.prepareStatement(sql)) {

		        stmt.setString(1, nombre);
		        stmt.setInt(2, id);

		        // Deberá entregar la cantidad de filas agregadas o modificadas, que debería ser uno.
		        int filaAfectada = stmt.executeUpdate();

		        // Verifica si al menos una fila fue añadida
		        if (filaAfectada > 0) {
		        	return true;
		        } else {
		            System.err.println("Error: La inserción en la base de datos falló (0 filas afectadas).");
		            return false;
		        }

		    } catch (SQLException e) {
		        e.printStackTrace();
		        return false;
		    }
	 }
	 
 	/**
     * Cambiar una columna de Usuario
     * @param apellido: El nuevo apellido a cambiar.
     * @param id: El id del usuario.
     * @return true si se realizo el cambio, false en caso contrario.
     * @throws SQLException Si ocurre un error de base de datos.
     */
	 public boolean editSurname(String apellido, int id) {
		 
	    String sql = "UPDATE tb_persona SET surname = ? WHERE id = ?";
	    
	    try (Connection connection = openConnection();
		         PreparedStatement stmt = connection.prepareStatement(sql)) {

		        stmt.setString(1, apellido);
		        stmt.setInt(2, id);

		        // Deberá entregar la cantidad de filas agregadas o modificadas, que debería ser uno.
		        int filaAfectada = stmt.executeUpdate();

		        // Verifica si al menos una fila fue añadida
		        if (filaAfectada > 0) {
		        	return true;
		        } else {
		            System.err.println("Error: La inserción en la base de datos falló (0 filas afectadas).");
		            return false;
		        }

		    } catch (SQLException e) {
		        e.printStackTrace();
		        return false;
		    }
	 }
	 
 	/**
     * Cambiar una columna de Usuario
     * @param nickname: El nuevo nickname a cambiar.
     * @param id: El id del usuario.
     * @return true si se realizo el cambio, false en caso contrario.
     * @throws SQLException Si ocurre un error de base de datos.
     */
	 public boolean editNickname(String nickname, int id) throws SQLException{
		 
		 // Verificar si existe el nickname
	    if (nicknameExists(nickname)) {
	        System.err.println("Error: El nickname '" + nickname + "' ya existe.");
	        return false;
	    }
	    
	    String sql = "UPDATE tb_persona SET nickname = ? WHERE id = ?";
	    
	    try (Connection connection = openConnection();
		         PreparedStatement stmt = connection.prepareStatement(sql)) {

		        stmt.setString(1, nickname);
		        stmt.setInt(2, id);

		        // Deberá entregar la cantidad de filas agregadas o modificadas, que debería ser uno.
		        int filaAfectada = stmt.executeUpdate();

		        // Verifica si al menos una fila fue añadida
		        if (filaAfectada > 0) {
		        	return true;
		        } else {
		            System.err.println("Error: La inserción en la base de datos falló (0 filas afectadas).");
		            return false;
		        }

		    } catch (SQLException e) {
		        e.printStackTrace();
		        return false;
		    }
	 }
	 

    public boolean addMovieToFavorites(int personaId, Movie movie) {
        // 1. Obtener o crear el ID de la película
        int movieId = getmovieID(movie);

        if (movieId == -1) {
            System.err.println("No se pudo obtener o crear el ID de la película '" + movie.getNombre() + "'.");
            return false;
        }

        // 2. Verificar si la película ya es favorita para este usuario
        if (isFavoriteMovieExist(personaId, movieId)) {
            System.out.println("La película '" + movie.getNombre() + "' (ID: " + movieId + ") ya es favorita para el usuario " + personaId + ".");
            return true; // Ya existe, consideramos que la operación fue exitosa
        }

        // 3. Si no es favorita, insertarla en tb_favorites
        String sql = "INSERT INTO tb_persona_favorite_movie (persona_id, movie_id) VALUES (?, ?);";

        try (Connection connection = openConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, personaId);
            stmt.setInt(2, movieId);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Película '" + movie.getNombre() + "' (ID: " + movieId + ") añadida a favoritos para el usuario " + personaId + ".");
                return true;
            } else {
                System.err.println("Fallo al añadir la película '" + movie.getNombre() + "' a favoritos para el usuario " + personaId + ". Ninguna fila afectada.");
                return false;
            }

        } catch (SQLException e) {
            System.err.println("Error de base de datos al añadir película a favoritos para el usuario " + personaId + " y película '" + movie.getNombre() + "': " + e.getMessage());
            return false;
        }
    }
    
    public int addReview(int personaid, Movie movie, String text) {
    	
        int movieId = getmovieID(movie);

        if (movieId == -1) {
            System.err.println("No se pudo obtener o crear el ID de la película '" + movie.getNombre() + "'.");
            return -1;
        }
    	
        // 1. Verificar si ya existe una reseña para esta película por esta persona
        int existingReviewId = getReviewId(movieId, personaid);

        if (existingReviewId != -1) {
            System.out.println("Ya existe una reseña para la película ID ");
            return -1;
        }

        // 2. Si no existe, proceder con la inserción
        String sql = "INSERT INTO tb_review (movie_id, persona_id, review_text, nombre) VALUES (?, ?, ?, ?)";
        int generatedId = -1;

        try (Connection connection = openConnection();
             PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, movieId);
            stmt.setInt(2, personaid);
            stmt.setString(3, text);
            stmt.setString(4,movie.getNombre());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedId = rs.getInt(1);
                        System.out.println("Reseña insertada exitosamente con ID: " + generatedId);
                    }
                }
            } else {
                System.err.println("Fallo al insertar la reseña. Ninguna fila afectada.");
            }

        } catch (SQLException e) {
            System.err.println("Error de base de datos al añadir reseña: " + e.getMessage());
            e.printStackTrace();
        }
        
        return generatedId;
    }
 
	// ======================================================================================//

 	// ============================== Utilidad ==================================================//
     
	private Connection openConnection() throws SQLException {
	    return DriverManager.getConnection(this.connectionURL, this.user, this.password);
	}
	
	private ArrayList<Movie> getFavorites(int id){
		ArrayList<Movie> favorites = new ArrayList<>();
		String sql = "SELECT m.* FROM tb_movie m JOIN tb_persona_favorite_movie pfm ON m.id = pfm.movie_id WHERE pfm.persona_id = ?";
	
		try (Connection connection = openConnection();
	         PreparedStatement stmt = connection.prepareStatement(sql)) {
	
			stmt.setInt(1, id);
	
			try (ResultSet result = stmt.executeQuery()) {
				while (result.next()) {
	                
					// Obtener los datos de la tabla
	                int movie_id = result.getInt("id");
	                String nombre = result.getString("nombre");
	                boolean adult = result.getBoolean("adult");
	                String original_lang = result.getString("original_lang");
	                String description = result.getString("description");
	                float pop = result.getFloat("popularity");
	                Timestamp release_date = result.getTimestamp("release_date");
	                
	                // Convertir release en string
	                String release = release_date.toString();
	                
	                // Convertirlo en la clase pelicula
	                Movie movie = new Movie(movie_id, nombre, adult, original_lang, description, pop, release);
	                
	                // Añadirlo a la lista de peliculas
	                favorites.add(movie);
				}
				
				return favorites;
			}
		}catch(SQLException e) {
			System.out.println(e);
			return favorites;
		}
	}
	
	private ArrayList<Review> getReviews(int id){
		ArrayList<Review> reviews = new ArrayList<>();
        String sql = "SELECT id, movie_id, persona_id, review_text, nombre " +
                     "FROM tb_review WHERE persona_id = ?;";
        
        try (Connection connection = openConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {

               stmt.setInt(1, id);
               ResultSet rs = stmt.executeQuery();

               while (rs.next()) {
                   int id_review = rs.getInt("id");
                   int movieId = rs.getInt("movie_id");
                   String reviewText = rs.getString("review_text");
                   String movie_nombre = rs.getString("nombre");

                   Review review = new Review(id_review, id, movieId, reviewText, movie_nombre);
                   reviews.add(review);
               }

           } catch (SQLException e) {
               System.err.println("Error de base de datos al obtener reviews para la persona ID " + id + ": " + e.getMessage());
               e.printStackTrace();
           }
           return reviews;
	}
	
	 private int getmovieID(Movie movie) {
		 int movieID = getMovieIDfromtable(movie.getNombre());
		 
		 System.out.println(movieID);
		 
		 if(movieID != -1) {
			 return movieID;
		 }
		 else {
			 return insertMovie(movie);
		 }
	 }
	
	private int getMovieIDfromtable(String nombre) {
		String sql = "SELECT * FROM tb_movie WHERE nombre = ? LIMIT 1;";
		int id = -1;
		
		try (Connection connection = openConnection();
		         PreparedStatement stmt = connection.prepareStatement(sql)) {
				stmt.setString(1, nombre);
				ResultSet result = stmt.executeQuery();
				
				if(result.next()) {
					id = result.getInt("id");
				}
				
		}catch(SQLException e) {
			System.out.println(e);
		}
		
		return id;
	}
	
	private int insertMovie(Movie movie) {
        String sql = "INSERT INTO tb_movie (nombre, adult, original_lang, description, popularity, release_date) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        int generatedId = -1;

        try (Connection connection = openConnection();
             PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, movie.getNombre());
            stmt.setBoolean(2, movie.isAdult());
            stmt.setString(3, movie.getOriginalLang());
            stmt.setString(4, movie.getDescription());
            stmt.setFloat(5, movie.getPopularity());

            if (movie.getReleaseDate() != null && !movie.getReleaseDate().isEmpty()) {
                stmt.setDate(6, Date.valueOf(movie.getReleaseDate()));
            } else {
                stmt.setNull(6, java.sql.Types.DATE);
            }

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                    	System.out.println("La inserción de la película '" + movie.getNombre() + "salio exitosa");
                        generatedId = rs.getInt(1);
                    }
                }
            } else {
                System.err.println("La inserción de la película '" + movie.getNombre() + "' falló, ninguna fila afectada.");
            }

        } catch (SQLException e) {
            System.err.println("Error de base de datos al insertar la película '" + movie.getNombre() + "': " + e.getMessage());
        }
        return generatedId;
    }
	
    private boolean isFavoriteMovieExist(int personaId, int movieId) {
        String sql = "SELECT 1 FROM tb_persona_favorite_movie WHERE persona_id = ? AND movie_id = ? LIMIT 1;";

        try (Connection connection = openConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, personaId);
            stmt.setInt(2, movieId);

            ResultSet result = stmt.executeQuery();
            return result.next();

        } catch (SQLException e) {
            System.err.println("Error de base de datos al verificar si la película (ID: " + movieId + ") es favorita para el usuario " + personaId + ": " + e.getMessage());
            return false;
        }
    }
    
    private int getReviewId(int movieId, int personaId) {
        String sql = "SELECT id FROM tb_review WHERE movie_id = ? AND persona_id = ? LIMIT 1;";
        int reviewId = -1;

        try (Connection connection = openConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, movieId);
            stmt.setInt(2, personaId);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                reviewId = rs.getInt("id");
            }

        } catch (SQLException e) {
            System.err.println("Error de base de datos al verificar existencia de reseña (movie_id: " + movieId + ", persona_id: " + personaId + "): " + e.getMessage());
            e.printStackTrace();
        }
        return reviewId;
    }
	
 	/**
     * Verifica si un nickname ya existe en la base de datos.
     * @param nickname El nickname a verificar.
     * @return true si el nickname ya existe, false en caso contrario.
     * @throws SQLException Si ocurre un error de base de datos.
     */
     private boolean nicknameExists(String nickname) throws SQLException {
         String sql = "SELECT COUNT(*) FROM tb_persona WHERE nickname = ?";
         try (Connection connection = openConnection();
              PreparedStatement stmt = connection.prepareStatement(sql)) {
             stmt.setString(1, nickname);
             try (ResultSet result = stmt.executeQuery()) {
                 if (result.next()) {
                     return result.getInt(1) > 0;
                 }
             }
         }
         return false;
     }
	
	// ======================================================================================//
}

