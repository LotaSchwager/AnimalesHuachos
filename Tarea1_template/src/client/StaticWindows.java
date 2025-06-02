package client;

public class StaticWindows {
    
    private static StaticWindows instance;
    
    private StaticWindows() {}
    
    public static StaticWindows getInstance() {
        if (instance == null) {
            instance = new StaticWindows();
        }
        return instance;
    }
    
    // Muestra el menu de login
    public void showLoginMenu() {
        System.out.println("========== Menú de Acceso ==========");
        System.out.println("1. Iniciar sesión");
        System.out.println("2. Crear cuenta");
        System.out.println("3. Salir");
        System.out.println("====================================");
        System.out.print("Opción: ");
    }
    
    // Muestra el prompt del menu principal
    public void showMainMenuPrompt(String nickname) {
        System.out.print(nickname + "@pucv-movies> ");
    }
    
    // Muestra el menu de ayuda
    public void showHelp() {
        clearScreen();
        System.out.println("============================== Ayuda Detallada ==============================");
        System.out.println("A continuación se listan los comandos disponibles y ejemplos de uso:\n");

        System.out.println("VISUALIZACIÓN:");
        System.out.println("  show user");
        System.out.println("    Descripción: Muestra la información de tu cuenta actual (ID, nickname, nombre, apellido).");
        System.out.println("    Ejemplo: show user\n");
        

        System.out.println("  show fav");
        System.out.println("    Descripción: Muestra todas las películas que has marcado como favoritas.");
        System.out.println("    Ejemplo: show fav\n");

        System.out.println("  show review");
        System.out.println("    Descripción: Muestra todas las reseñas que has escrito.");
        System.out.println("    Ejemplo: show review");

        System.out.println("BÚSQUEDA DE PELÍCULAS:");
        System.out.println("  search [-g GENERO...] [-y AÑO]");
        System.out.println("    Descripción: Busca películas aplicando filtros opcionales.");
        System.out.println("    Flags:");
        System.out.println("      -g GENERO... : Filtra por uno o más géneros. Separa los géneros por espacios.");
        System.out.println("                     (ej: -g accion terror comedia)");
        System.out.println("      -y AÑO       : Filtra por año de lanzamiento exacto.");
        System.out.println("                     (ej: -y 2020)");
        System.out.println("    Ejemplos:");
        System.out.println("      search");
        System.out.println("      search -g comedia");
        System.out.println("      search -y 1999");
        System.out.println("      search -g aventura fantasia -y 2005\n");

        System.out.println("GESTIÓN DE FAVORITOS:");
        System.out.println("  set fav <ID_PELICULA>");
        System.out.println("    Descripción: Marca una película como favorita usando su ID numérico.");
        System.out.println("    Ejemplo: set fav 101\n");

        System.out.println("  rm fav <ID_PELICULA>");
        System.out.println("    Descripción: Elimina una película de tu lista de favoritos usando su ID.");
        System.out.println("    Ejemplo: rm fav 101\n");

        System.out.println("GESTIÓN DE RESEÑAS:");
        System.out.println("  make review <ID_PELICULA>");
        System.out.println("    Descripción: Inicia el proceso para escribir y guardar una reseña para una película específica.");
        System.out.println("                 Se te pedirá que ingreses el texto de la reseña.");
        System.out.println("    Ejemplo: make review 77\n");

        System.out.println("CONFIGURACIÓN DE CUENTA:");
        System.out.println("  config user");
        System.out.println("    Descripción: Permite modificar datos de tu cuenta como nickname, nombre, apellido o contraseña.");
        System.out.println("    Ejemplo: config user\n");
        System.out.println("    ¿CÓMO EDITAR TU CUENTA?.");
        System.out.println("    edit --name 'Tu nuevo nombre'");
        System.out.println("    edit --surname 'Tu nuevo apellido'");
        System.out.println("    edit --nickname 'Tu nuevo nickname' ==> Te en cuenta que dos usuarios no pueden tener el mismo nickname.");
        
        System.out.println("NAVEGACIÓN Y AYUDA:");
        System.out.println("  help");
        System.out.println("    Descripción: Muestra esta pantalla de ayuda detallada.\n");

        System.out.println("  logout");
        System.out.println("    Descripción: Cierra tu sesión actual y regresa al menú de acceso.\n");

        System.out.println("  exit");
        System.out.println("    Descripción: Cierra la aplicación por completo.\n");
        System.out.println("==============================================================================");
    }
    
    // Muestra el encabezado de creacion de cuenta
    public void showCreateAccountHeader() {
        clearScreen();
        System.out.println("========== Crear Cuenta ==========");
    }
    
    // Muestra el encabezado de login
    public void showLoginHeader() {
        clearScreen();
        System.out.println("--- Iniciar Sesión ---");
    }
    
    // Muestra el encabezado de creacion de reseña
    public void showMakeReviewHeader(int movieId) {
        clearScreen();
        System.out.println("--- Escribir Reseña para Película ID: " + movieId + " ---");
        System.out.println("Escribe tu reseña (presiona Enter para guardar, o escribe 'cancelar' en una línea para salir):");
    }
    
    // Utility method to clear screen (cross-platform)
    public void clearScreen() {
        String os = System.getProperty("os.name").toLowerCase();
        try {
            if (os.contains("win")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (final Exception e) {
            // Fallback
            for (int i = 0; i < 50; ++i) System.out.println();
        }
    }
    
    // Espera a que el usuario presione Enter
    public void waitForEnter(java.util.Scanner sc) {
        System.out.print("Presione Enter para continuar...");
        sc.nextLine();
    }
}