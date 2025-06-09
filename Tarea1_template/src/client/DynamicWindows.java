package client;

import java.util.List;
import common.Movie;
import common.Persona;
import common.Review;

public class DynamicWindows {
    
    private static DynamicWindows instance;
    
    private DynamicWindows() {}
    
    public static DynamicWindows getInstance() {
        if (instance == null) {
            instance = new DynamicWindows();
        }
        return instance;
    }
    
    // Muestra el menu principal
    public void showMainMenuDisplay(Persona user) {
        System.out.println("========== Menú Principal ==========");
        System.out.println("Bienvenido de nuevo, " + user.getNombre() + " " + user.getSurname() + " (@" + user.getNickname() + ")");
        System.out.println("====================================");
        System.out.println("\nComandos principales:");
        System.out.println("------------------------------------");
        System.out.println("- Buscar películas : search");
        System.out.println("- Mostrar Cuenta   : show user");
        System.out.println("- Mostrar Reseñas  : show review");
        System.out.println("- Mostrar Favoritos: show fav");
        System.out.println("- Configurar Cuenta: config user");
        System.out.println("- Ayuda            : help");
        System.out.println("- Cerrar Sesión    : logout");
        System.out.println("- Salir Aplicación : exit");
        System.out.println("====================================");
    }
    
    // Muestra la informacion de la cuenta
    public void showUserAccount(Persona user) {
        StaticWindows.getInstance().clearScreen();
        System.out.println("========== Mostrar Cuenta ==========");
        
        if (user != null) {
            System.out.println("ID: " + user.getID());
            System.out.println("Nickname: " + user.getNickname());
            System.out.println("Nombre: " + user.getNombre());
            System.out.println("Apellido: " + user.getSurname());
            System.out.println("Cantidad de reseñas: " + user.getReviews().size());
            System.out.println("Favoritos: ");
            for(Movie pelicula : user.getMovies()) {
            	System.out.println(pelicula.getNombre());
            }
        } else {
            System.out.println("No hay información de usuario disponible. Por favor, inicie sesión.");
        }
        System.out.println("==================================");
    }
    
    // Muestra la informacion de la cuenta
    public void showConfigUserAccount(Persona user) {
        StaticWindows.getInstance().clearScreen();
        System.out.println("========== Configuración de la Cuenta ==========");
        System.out.println("==================================");
        System.out.println("Editar el nombre: [edit --name 'Nuevo nombre']");
        System.out.println("Editar el apellido: [edit --surname 'Nuevo apellido']");
        System.out.println("Editar el nickname: [edit --nickname 'Nuevo nickname'] (Ten cuidado: no puede haber dos usuarios con el mismo nickname)");
        System.out.println("Ejemplo: edit --name 'Ejemplo' --surname 'de comando' --nickname 'CPYD'");
        System.out.println("==================================\n");
        
        if (user != null) {
            System.out.println("Actual nickname: " + user.getNickname());
            System.out.println("Actual nombre: " + user.getNombre());
            System.out.println("Actual apellido: " + user.getSurname());
        } else {
            System.out.println("No hay información de usuario disponible. No puede editar si no tiene datos de usuario.");
        }
        System.out.println("\n==================================");
    }
    
    // Muestra los resultados de la busqueda
    public void showMovieSearchResults(List<Movie> movies, String searchCriteria) {
        System.out.println("========== Resultados de Búsqueda ==========");
        if (searchCriteria != null && !searchCriteria.isEmpty()) {
            System.out.println("Criterios: " + searchCriteria);
            System.out.println("-------------------------------------------");
        }
        
        if (movies.isEmpty()) {
            System.out.println("No se encontraron películas con esos filtros.");
        } else {
            System.out.println("Películas encontradas (" + movies.size() + "):");
            System.out.println("-------------------------------------------");
            for (Movie movie : movies) {
                System.out.printf("ID: %-4d | %-30s | Pop: %.1f%n", 
                    movie.getId(), 
                    truncateString(movie.getNombre(), 30), 
                    movie.getPopularity());
            }
        }
        System.out.println("==========================================");
    }
    
    // Muestra un mensaje de exito
    public void showSuccessMessage(String message) {
        System.out.println("\n✓ " + message);
    }
    
    // Muestra un mensaje de error
    public void showErrorMessage(String message) {
        System.out.println("\n✗ Error: " + message);
    }
    
    // Muestra un mensaje de advertencia
    public void showWarningMessage(String message) {
        System.out.println("\n⚠ Advertencia: " + message);
    }
    
    // Muestra un mensaje de informacion
    public void showInfoMessage(String message) {
        System.out.println("\nℹ " + message);
    }
    
    public void showFavoriteMovies(List<Movie> favoriteMovies, String username) {
        StaticWindows.getInstance().clearScreen();
        System.out.println("========== Películas Favoritas ==========");
        System.out.println("Usuario: @" + username);
        System.out.println("=========================================");
        
        if (favoriteMovies.isEmpty()) {
            System.out.println("No tienes películas favoritas aún.");
            System.out.println("Usa 'set fav <ID_PELICULA>' para agregar favoritos.");
        } else {
            System.out.println("Total de favoritos: " + favoriteMovies.size());
            System.out.println("-----------------------------------------");
            for (Movie movie : favoriteMovies) {
                System.out.printf("ID: %-4d | %-20s | Release: %s | Pop: %.1f%n",
                    movie.getId(),
                    truncateString(movie.getNombre(), 20),
                    movie.getReleaseDate(),
                    movie.getPopularity());
            }
        }
        System.out.println("=========================================");
    }
    
    // Muestra los resultados de la busqueda
    public void showReviews(List<Review> reviews, String username) {
        StaticWindows.getInstance().clearScreen();
        System.out.println("========== Películas Favoritas ==========");
        System.out.println("Usuario: @" + username);
        System.out.println("=========================================");
        
        if (reviews.isEmpty()) {
            System.out.println("No tienes reseñas aún.");
            System.out.println("Usa 'make review <ID_PELICULA>' para crear una reseña.");
        } else {
            System.out.println("Total de reseñas: " + reviews.size());
            System.out.println("-----------------------------------------");
            for (Review resena : reviews) {
                System.out.printf("ID: %-4d | %-20s ",
                	resena.getID(),
                    truncateString(resena.getMovieName(), 20));
                System.out.println("\n" + resena.getReview() + "\n");
            }
        }
        System.out.println("=========================================");
    }
    
    // Muestra una confirmacion de reseña
    public void showReviewConfirmation(String reviewText, String nombre) {
        System.out.println("\n========== Confirmación de Reseña ==========");
        System.out.println("Película: " + nombre);
        System.out.println("Reseña: \"" + reviewText + "\"");
        System.out.println("==========================================");
    }
    
    // Muestra un mensaje de comando no reconocido
    public void showUnrecognizedCommand(String command) {
        System.out.println("Comando no reconocido: " + command + ". Escriba 'help' para ver los comandos disponibles.");
    }
    
    // Muestra un mensaje de uso incorrecto de comando
    public void showInvalidCommandUsage(String command, String correctUsage) {
        System.out.println("Comando '" + command + "' incorrecto. Uso: " + correctUsage + ". Use 'help'.");
    }
    
    // Muestra un mensaje de funcionalidad pendiente
    public void showPendingFunctionality(String functionality) {
        System.out.println("Activaste este comando: " + functionality + " (Funcionalidad pendiente)");
    }
    
    // Muestra un prompt de entrada
    public void showInputPrompt(String prompt) {
        System.out.print(prompt + ": ");
    }
    
    // Utility method to truncate strings for display formatting
    private String truncateString(String str, int maxLength) {
        if (str == null) return "";
        if (str.length() <= maxLength) return str;
        return str.substring(0, maxLength - 3) + "...";
    }
    
    // Muestra un encabezado de tabla
    public void showTableHeader(String title, String... columns) {
        System.out.println("========== " + title + " ==========");
        StringBuilder header = new StringBuilder();
        for (int i = 0; i < columns.length; i++) {
            header.append(String.format("%-15s", columns[i]));
            if (i < columns.length - 1) header.append(" | ");
        }
        System.out.println(header.toString());
        System.out.println("-".repeat(header.length()));
    }
}