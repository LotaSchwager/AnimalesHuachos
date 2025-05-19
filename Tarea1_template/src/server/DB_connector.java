package server;

import java.rmi.RemoteException;
import java.sql.Connection;
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
	
					return new Persona(id, nickname, name, surname, password, createdAtTime.toLocalDateTime(), updatedAtTime.toLocalDateTime());
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
	
	// ============================== Crear ==================================================//
	
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
     * Verifica si un nickname ya existe en la base de datos.
     * @param nickname El nickname a verificar.
     * @return true si el nickname ya existe, false en caso contrario.
     * @throws SQLException Si ocurre un error de base de datos.
     */
     public boolean nicknameExists(String nickname) throws SQLException {
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

 	// ============================== Utilidad ==================================================//
     
	private Connection openConnection() throws SQLException {
	    return DriverManager.getConnection(this.connectionURL, this.user, this.password);
	}
	
	// ======================================================================================//
}

