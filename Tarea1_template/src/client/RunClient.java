package client;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import common.Movie;
import common.Persona;
// import common.Movie; // Descomenta cuando implementes la lógica de Movie
// import common.Review; // Descomenta cuando implementes la lógica de Review

public class RunClient {

    private enum AppState {
        LOGIN,
        CREATE_ACCOUNT,
        MAIN_MENU,
        EXIT
    }

    private static AppState currentState = AppState.LOGIN;
    private static Persona loggedInUser = null;

    public static void main(String[] args) {
        Client client = new Client();

        try {
            client.startClient();
        } catch (RemoteException | NotBoundException e) {
            System.err.println("Error al conectar con el servidor RMI: " + e.getMessage());
            System.err.println("La aplicación se cerrará.");
            return;
        }

        try (Scanner sc = new Scanner(System.in)) {
            while (currentState != AppState.EXIT) {
                clean();

                switch (currentState) {
                    case LOGIN:
                        showLoginMenu();
                        handleLoginInput(sc, client);
                        break;
                    case CREATE_ACCOUNT:
                        showCreateAccountMenu(sc, client);
                        break;
                    case MAIN_MENU:
                        if (loggedInUser == null) {
                            System.out.println("Error: No hay sesión activa. Volviendo al login.");
                            currentState = AppState.LOGIN;
                            waitForEnter(sc);
                            continue;
                        }
                        showMainMenuDisplay(loggedInUser);
                        showMainMenuPrompt();
                        handleMainMenuCommand(sc, client);
                        break;
                    case EXIT:
                        break;
                }
            }
            System.out.println("Saliendo de la aplicación...");
            if (loggedInUser != null) {
                try {
                    client.logout();
                } catch (RemoteException e) {
                    System.err.println("Error durante el logout: " + e.getMessage());
                }
            }
        }
    }

    // --- Métodos para mostrar menús/prompts ---
    
    public static void showLoginMenu() {
        System.out.println("========== Menú de Acceso ==========");
        System.out.println("1. Iniciar sesión");
        System.out.println("2. Crear cuenta");
        System.out.println("3. Salir");
        System.out.println("====================================");
        System.out.print("Opción: ");
    }

    public static void showCreateAccountMenu(Scanner sc, Client client) {
        clean();
        System.out.println("========== Crear Cuenta ==========");
        System.out.print("Ingrese un nickname (o 'volver' para cancelar): ");
        String nicknameCreate = sc.nextLine().trim();
        if (nicknameCreate.equalsIgnoreCase("volver")) {
            currentState = AppState.LOGIN;
            return;
        }

        System.out.print("Ingrese un nombre: ");
        String nameCreate = sc.nextLine().trim();
        System.out.print("Ingrese un apellido: ");
        String surnameCreate = sc.nextLine().trim();
        System.out.print("Ingrese una contraseña: ");
        String passwordCreate = sc.nextLine().trim();

        try {
            if (client.createAccount(nicknameCreate, nameCreate, surnameCreate, passwordCreate)) {
                System.out.println("\n¡Cuenta creada con éxito! Por favor, inicie sesión.");
            } else {
                System.out.println("\nFallo al crear la cuenta. Es posible que el nickname ya exista o los datos sean inválidos.");
            }
        } catch (RemoteException e) {
            System.err.println("\nError de comunicación al crear la cuenta: " + e.getMessage());
        }
        currentState = AppState.LOGIN;
        waitForEnter(sc);
    }
    
    // Nuevo método para mostrar el menú principal visual
    public static void showMainMenuDisplay(Persona user) {
        System.out.println("========== Menú Principal ==========");
        System.out.println("Bienvenido de nuevo, " + user.getNombre() + " " + user.getSurname() + " (@" + user.getNickname() + ")"); // Usar getNombre() y getSurname()
        System.out.println("====================================");
        System.out.println("\nComandos principales:");
        System.out.println("------------------------------------");
        System.out.println("- Buscar películas : search");
        System.out.println("- Mostrar Cuenta   : show user");
        System.out.println("- Mostrar Reseñas  : show review"); // Abreviado a 'show review'
        System.out.println("- Mostrar Favoritos: show fav");
        System.out.println("- Configurar Cuenta: config user");
        System.out.println("- Ayuda            : help");
        System.out.println("- Cerrar Sesión    : logout");
        System.out.println("- Salir Aplicación : exit");
        System.out.println("====================================");
    }
    
    // Mostrar el menu donde ira el prompt
    public static void showMainMenuPrompt() {
        System.out.print(loggedInUser.getNickname() + "@pucv-movies> ");
    }

    // --- Métodos para manejar la entrada del usuario ---

    public static void handleLoginInput(Scanner sc, Client client) {
        String input = sc.nextLine().trim();
        int opcion = -1;
        try {
            opcion = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Opción no válida. Por favor, ingrese un número.");
            waitForEnter(sc);
            return;
        }

        switch (opcion) {
            case 1:
                clean();
                System.out.println("--- Iniciar Sesión ---");
                System.out.print("Nickname: ");
                String nicknameLogin = sc.nextLine().trim();
                System.out.print("Contraseña: ");
                String passwordLogin = sc.nextLine().trim();
                try {
                    if (client.login(nicknameLogin, passwordLogin)) {
                        loggedInUser = client.mostrarCuenta();

                        if (loggedInUser != null) {
                            System.out.println("\n¡Login exitoso!");
                            currentState = AppState.MAIN_MENU;
                        } else {
                            System.out.println("\nLogin exitoso, pero no se pudieron obtener los datos del usuario. Intentando de nuevo...");
                            currentState = AppState.LOGIN;
                        }
                    } else {
                        System.out.println("\nError: Nickname o contraseña incorrectos.");
                    }
                } catch (RemoteException e) {
                    System.err.println("\nError de comunicación durante el login/obtención de cuenta: " + e.getMessage());
                    currentState = AppState.LOGIN;
                }
                waitForEnter(sc);
                break;
            case 2:
                currentState = AppState.CREATE_ACCOUNT;
                break;
            case 3:
                currentState = AppState.EXIT;
                break;
            default:
                System.out.println("Opción no válida.");
                waitForEnter(sc);
                break;
        }
    }


    public static void handleMainMenuCommand(Scanner sc, Client client) {
        String fullCommand = sc.nextLine().trim();
        if (fullCommand.isEmpty()) {
            return;
        }

        String[] parts = fullCommand.split("\\s+");
        String command = parts[0].toLowerCase();
        List<String> argsList = new ArrayList<>(Arrays.asList(parts).subList(1, parts.length));

        switch (command) {
            case "show":
                // ... (lógica para show user, show fav, show review) ...
                // La que teníamos antes
                if (argsList.size() > 0) {
                    String subCommandShow = argsList.get(0).toLowerCase();
                    if (subCommandShow.equals("user") && argsList.size() == 1) {
                        handleShowUser(client);
                    } else if (subCommandShow.equals("fav") && argsList.size() == 1) {
                        System.out.println("Activaste este comando: show fav (Funcionalidad pendiente)");
                    } else if (subCommandShow.equals("review") && argsList.size() == 1) {
                        System.out.println("Activaste este comando: show review (Funcionalidad pendiente)");
                    } else {
                        System.out.println("Comando 'show' no reconocido o argumentos incorrectos. Use 'help' para más detalles.");
                    }
                } else {
                    System.out.println("Comando 'show' incompleto. Especifique qué mostrar (user, fav, review). Use 'help'.");
                }
                break;
            case "search":
                handleSearch(argsList, client);
                break;
            case "set":
                // ... (lógica para set fav) ...
                // La que teníamos antes
                if (argsList.size() > 0 && argsList.get(0).equalsIgnoreCase("fav") && argsList.size() == 2) {
                    try {
                        int movieIdToSet = Integer.parseInt(argsList.get(1));
                        System.out.println("Activaste este comando: set fav " + movieIdToSet + " (Funcionalidad pendiente)");
                    } catch (NumberFormatException e) {
                        System.out.println("Error: El ID de la película para 'set fav' debe ser un número.");
                    }
                } else {
                     System.out.println("Comando 'set fav' incorrecto. Uso: set fav <ID_PELICULA>. Use 'help'.");
                }
                break;
            case "rm":
                // ... (lógica para rm fav) ...
                // La que teníamos antes
                if (argsList.size() > 0 && argsList.get(0).equalsIgnoreCase("fav") && argsList.size() == 2) {
                   try {
                       int movieIdToRemove = Integer.parseInt(argsList.get(1));
                       System.out.println("Activaste este comando: rm fav " + movieIdToRemove + " (Funcionalidad pendiente)");
                   } catch (NumberFormatException e) {
                       System.out.println("Error: El ID de la película para 'rm fav' debe ser un número.");
                   }
               } else {
                    System.out.println("Comando 'rm fav' incorrecto. Uso: rm fav <ID_PELICULA>. Use 'help'.");
               }
                break;
            case "make":
                // ... (lógica para make review) ...
                // La que teníamos antes
                if (argsList.size() > 0 && argsList.get(0).equalsIgnoreCase("review") && argsList.size() == 2) {
                    try {
                        int movieIdForReview = Integer.parseInt(argsList.get(1));
                        System.out.println("Activaste este comando: make review " + movieIdForReview);
                        handleMakeReview(movieIdForReview, sc, client);
                    } catch (NumberFormatException e) {
                        System.out.println("Error: El ID de la película para 'make review' debe ser un número.");
                    }
                } else {
                     System.out.println("Coman                    // client.login() solo devuelve booleando 'make review' incorrecto. Uso: make review <ID_PELICULA>. Use 'help'.");
                }
                break;
            case "config":
                // ... (lógica para config user) ...
                // La que teníamos antes
                if (argsList.size() > 0 && argsList.get(0).equalsIgnoreCase("user") && argsList.size() == 1) {
                   System.out.println("Activaste este comando: config user (Funcionalidad pendiente)");
               } else {
                    System.out.println("Comando 'config user' incorrecto. Uso: config user. Use 'help'.");
               }
                break;
            case "help": // <-- AÑADIDO EL CASO PARA 'help'
                showHelp();
                break;
            case "logout":
                try {
                    client.logout();
                    loggedInUser = null;
                    currentState = AppState.LOGIN;
                    System.out.println("Sesión cerrada.");
                } catch (RemoteException e) {
                    System.err.println("Error al cerrar sesión: " + e.getMessage());
                }
                // No llamar a waitForEnter aquí, ya que cambiamos de estado
                return; // Salir del handleMainMenuCommand para evitar el waitForEnter de abajo
            case "exit":
                currentState = AppState.EXIT;
                // No llamar a waitForEnter aquí
                return; // Salir del handleMainMenuCommand
            default:
                System.out.println("Comando no reconocido: " + command + ". Escriba 'help' para ver los comandos disponibles.");
                break;
        }

        if (currentState == AppState.MAIN_MENU) {
             waitForEnter(sc); // Solo esperar si seguimos en el menú principal y no hemos salido o deslogueado
        }
    }

    private static void handleShowUser(Client client) { // Ya no necesitamos pasar 'client' si solo usamos loggedInUser
        clean();
        System.out.println("========== Mostrar Cuenta ==========");

        if (loggedInUser != null) {
            System.out.println("ID: " + loggedInUser.getID()); // Asumiendo que tienes getID() en Persona
            System.out.println("Nickname: " + loggedInUser.getNickname());
            System.out.println("Nombre: " + loggedInUser.getNombre()); // Asumiendo que tienes getNombre() en Persona
            System.out.println("Apellido: " + loggedInUser.getSurname());
            // No mostrar la contraseña ni datos sensibles como created_at/updated_at a menos que sea necesario
            // System.out.println("Miembro desde: " + loggedInUser.getCreatedAt()); // Ejemplo si quisieras mostrarlo
        } else {
            // Este caso sería muy raro si estamos en MAIN_MENU, ya que se verifica loggedInUser
            // antes de entrar al manejo de comandos del menú principal.
            // Pero es una buena guarda por si acaso.
            System.out.println("No hay información de usuario disponible. Por favor, inicie sesión.");
            currentState = AppState.LOGIN; // Forzar re-login si los datos se perdieron
        }
        System.out.println("==================================");
    }

    
    //Permite buscar usando los filtros opcionales genero, año y puntuacion minima
    //Ej -g Action -y 2020 -r 80
    private static void handleSearch(List<String> args, Client client) {
        String genero = null;
        int anio = -1;
        float rating = -1;

        //Procesamiento de las flags
        for (int i = 0; i < args.size() - 1; i++) {
            switch (args.get(i)) {
                case "-g":
                    genero = args.get(i + 1);
                    break;
                case "-y":
                    anio = Integer.parseInt(args.get(i + 1));
                    break;
                case "-r":
                    rating = Float.parseFloat(args.get(i + 1));
                    break;
            }
        }

        try {
            List<Movie> resultados = client.buscarPeliculas(client.getSessionToken(), genero, anio, rating);

            if (resultados.isEmpty()) {
                System.out.println("No se encontraron películas con esos filtros.");
            } else {
                System.out.println("Películas encontradas:");
                for (Movie m : resultados) {
                    System.out.println("- " + m.getNombre() + " | Pop: " + m.getPopularity());
                }
            }

        } catch (RemoteException e) {
            System.out.println("Error al buscar películas: " + e.getMessage());
        }
    }



    private static void handleMakeReview(int movieId, Scanner sc, Client client) {
        clean();
        System.out.println("--- Escribir Reseña para Película ID: " + movieId + " ---");
        // Aquí podrías mostrar primero info de la película obteniéndola con client.getMovieById(movieId)
        System.out.println("Escribe tu reseña (presiona Enter para guardar, o escribe 'cancelar' en una línea para salir):");
        String reviewText = sc.nextLine().trim();

        if (reviewText.equalsIgnoreCase("cancelar")) {
            System.out.println("Creación de reseña cancelada.");
            return;
        }

        if (reviewText.isEmpty()) {
            System.out.println("La reseña no puede estar vacía.");
            return;
        }
        
        System.out.println("Reseña a guardar: \"" + reviewText + "\" para película ID " + movieId + " (Funcionalidad pendiente)");
        // Aquí llamarías a client.makeReview(movieId, reviewText);
        // Y manejarías la respuesta (éxito/error)
    }


    // --- Métodos de utilidad ---
    private static void waitForEnter(Scanner sc) {
        System.out.print("Presione Enter para continuar...");
        // Asegurarse de que cualquier entrada numérica pendiente sea consumida
        // Esto puede ser necesario si la última entrada fue sc.nextInt() sin sc.nextLine()
        // Aunque ahora estamos usando sc.nextLine() para todo, es una buena práctica
        // si se mezclan. En nuestro caso actual, es probable que no siempre sea necesario.
        // sc.nextLine(); // Si la línea anterior fue sc.nextInt(), esta es crucial.
                       // Si la línea anterior fue sc.nextLine(), esta podría esperar una nueva entrada.
                       // Para simplificar, si el problema es que espera dos veces, quítala.
                       // Si el problema es que no espera, asegúrate que el input anterior se consumió.
                       // Dado que leemos el comando con sc.nextLine(), esta espera es correcta.
        sc.nextLine();
    }
    
    // Comando clean para windows o linux....... creo
    public static void clean() {
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
    
    // Método de ayuda detallado
    public static void showHelp() {
        clean();
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
        // Opcional: "  show review <ID_PELICULA>"
        // System.out.println("    Ejemplo (específica): show review 123\n");


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
        
        System.out.println("NAVEGACIÓN Y AYUDA:");
        System.out.println("  help");
        System.out.println("    Descripción: Muestra esta pantalla de ayuda detallada.\n");

        System.out.println("  logout");
        System.out.println("    Descripción: Cierra tu sesión actual y regresa al menú de acceso.\n");

        System.out.println("  exit");
        System.out.println("    Descripción: Cierra la aplicación por completo.\n");
        System.out.println("==============================================================================");
    }
}