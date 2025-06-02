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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import common.Movie;
import common.Persona;

public class RunClient {

    // Enumeración de estados de la aplicación
    private enum AppState {
        LOGIN, // Estado de login
        CREATE_ACCOUNT, // Estado de creación de cuenta
        MAIN_MENU, // Estado del menú principal
        EXIT, // Estado de salida
        CONFIG // Estado de configurar el usuario
    }

    // Estado actual de la aplicación
    private static AppState currentState = AppState.LOGIN;
    
    // Usuario logueado
    private static Persona loggedInUser = null;
    
    // Instancias de las ventanas estáticas y dinámicas
    private static final StaticWindows staticWindows = StaticWindows.getInstance();
    private static final DynamicWindows dynamicWindows = DynamicWindows.getInstance();

    public static void main(String[] args) throws RemoteException {
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
                staticWindows.clearScreen();

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
                            staticWindows.waitForEnter(sc);
                            continue;
                        }
                        showMainMenuDisplay(loggedInUser);
                        showMainMenuPrompt();
                        handleMainMenuCommand(sc, client);
                        break;
                    case CONFIG:
                    	handleConfigUser(sc, client);
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


    // Método para crear una cuenta
    private static void showCreateAccountMenu(Scanner sc, Client client) {
        staticWindows.showCreateAccountHeader();
        System.out.print("Ingrese un nickname (o escriba 'volver' para regresar): ");
        
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
                dynamicWindows.showSuccessMessage("¡Cuenta creada con éxito! Por favor, inicie sesión.");
            } else {
                dynamicWindows.showErrorMessage("Fallo al crear la cuenta. Es posible que el nickname ya exista o los datos sean inválidos.");
            }
        } catch (RemoteException e) {
            dynamicWindows.showErrorMessage("Error de comunicación al crear la cuenta: " + e.getMessage());
        }
        currentState = AppState.LOGIN;
        staticWindows.waitForEnter(sc);
    }

    // Métodos para manejar la entrada del usuario
    private static void handleLoginInput(Scanner sc, Client client) {
        String input = sc.nextLine().trim();
        int opcion = -1;
        try {
            opcion = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            dynamicWindows.showErrorMessage("Opción no válida. Por favor, ingrese un número.");
            staticWindows.waitForEnter(sc);
            return;
        }

        switch (opcion) {
            case 1:
                staticWindows.showLoginHeader();
                System.out.print("Nickname: ");
                String nicknameLogin = sc.nextLine().trim();
                System.out.print("Contraseña: ");
                String passwordLogin = sc.nextLine().trim();
                try {
                    if (client.login(nicknameLogin, passwordLogin)) {
                        loggedInUser = client.mostrarCuenta();

                        if (loggedInUser != null) {
                            dynamicWindows.showSuccessMessage("¡Login exitoso!");
                            currentState = AppState.MAIN_MENU;
                        } else {
                            dynamicWindows.showWarningMessage("Login exitoso, pero no se pudieron obtener los datos del usuario. Intentando de nuevo...");
                            currentState = AppState.LOGIN;
                        }
                    } else {
                        dynamicWindows.showErrorMessage("Nickname o contraseña incorrectos.");
                    }
                } catch (RemoteException e) {
                    dynamicWindows.showErrorMessage("Error de comunicación durante el login/obtención de cuenta: " + e.getMessage());
                    currentState = AppState.LOGIN;
                }
                staticWindows.waitForEnter(sc);
                break;
            case 2:
                currentState = AppState.CREATE_ACCOUNT;
                break;
            case 3:
                currentState = AppState.EXIT;
                break;
            default:
                dynamicWindows.showErrorMessage("Opción no válida.");
                staticWindows.waitForEnter(sc);
                break;
        }
    }

    private static void handleMainMenuCommand(Scanner sc, Client client) {
        String fullCommand = sc.nextLine().trim();
        if (fullCommand.isEmpty()) {
            return;
        }

        String[] parts = fullCommand.split("\\s+");
        String command = parts[0].toLowerCase();
        List<String> argsList = new ArrayList<>(Arrays.asList(parts).subList(1, parts.length));

        switch (command) {
            case "show":
                if (argsList.size() > 0) {
                    String subCommandShow = argsList.get(0).toLowerCase();
                    if (subCommandShow.equals("user") && argsList.size() == 1) {
                        handleShowUser();
                    } else if (subCommandShow.equals("fav") && argsList.size() == 1) {
                        handleShowFavorites(client);
                    } else if (subCommandShow.equals("review") && argsList.size() == 1) {
                        dynamicWindows.showPendingFunctionality("show review");
                    } else {
                        dynamicWindows.showInvalidCommandUsage("show", "show [user|fav|review]");
                    }
                } else {
                    dynamicWindows.showErrorMessage("Comando 'show' incompleto. Especifique qué mostrar (user, fav, review). Use 'help'.");
                }
                break;
            case "search":
                handleSearch(argsList, client);
                break;
            case "set":
                if (argsList.size() > 0 && argsList.get(0).equalsIgnoreCase("fav") && argsList.size() == 2) {
                    try {
                        int movieIdToSet = Integer.parseInt(argsList.get(1));
                        dynamicWindows.showPendingFunctionality("set fav " + movieIdToSet);
                    } catch (NumberFormatException e) {
                        dynamicWindows.showErrorMessage("El ID de la película para 'set fav' debe ser un número.");
                    }
                } else {
                    dynamicWindows.showInvalidCommandUsage("set fav", "set fav <ID_PELICULA>");
                }
                break;
            case "rm":
                if (argsList.size() > 0 && argsList.get(0).equalsIgnoreCase("fav") && argsList.size() == 2) {
                   try {
                       int movieIdToRemove = Integer.parseInt(argsList.get(1));
                       dynamicWindows.showPendingFunctionality("rm fav " + movieIdToRemove);
                   } catch (NumberFormatException e) {
                       dynamicWindows.showErrorMessage("El ID de la película para 'rm fav' debe ser un número.");
                   }
               } else {
                    dynamicWindows.showInvalidCommandUsage("rm fav", "rm fav <ID_PELICULA>");
               }
                break;
            case "make":
                if (argsList.size() > 0 && argsList.get(0).equalsIgnoreCase("review") && argsList.size() == 2) {
                    try {
                        int movieIdForReview = Integer.parseInt(argsList.get(1));
                        handleMakeReview(movieIdForReview, sc, client);
                    } catch (NumberFormatException e) {
                        dynamicWindows.showErrorMessage("El ID de la película para 'make review' debe ser un número.");
                    }
                } else {
                    dynamicWindows.showInvalidCommandUsage("make review", "make review <ID_PELICULA>");
                }
                break;
            case "config":
                if (argsList.size() > 0 && argsList.get(0).equalsIgnoreCase("user") && argsList.size() == 1) {
                	currentState = AppState.CONFIG;
               } else {
                    dynamicWindows.showInvalidCommandUsage("config user", "config user");
               }
                break;
            case "help":
                staticWindows.showHelp();
                break;
            case "logout":
                try {
                    client.logout();
                    loggedInUser = null;
                    currentState = AppState.LOGIN;
                    dynamicWindows.showSuccessMessage("Sesión cerrada.");
                } catch (RemoteException e) {
                    dynamicWindows.showErrorMessage("Error al cerrar sesión: " + e.getMessage());
                }
                return; // Salir del handleMainMenuCommand para evitar el waitForEnter de abajo
            case "exit":
                currentState = AppState.EXIT;
                return; // Salir del handleMainMenuCommand
            default:
                dynamicWindows.showUnrecognizedCommand(command);
                break;
        }

        if (currentState == AppState.MAIN_MENU) {
             staticWindows.waitForEnter(sc); // Solo esperar si seguimos en el menú principal y no hemos salido o deslogueado
        }
    }
    
    // Manejador de mostrar las peliculas favoritas
    private static void handleShowFavorites(Client client) {
        try {
            // Assuming you have a method to get favorite movies
            // List<Movie> favoriteMovies = client.getFavoriteMovies();
            // For now, using empty list as placeholder
            List<Movie> favoriteMovies = new ArrayList<>();
            dynamicWindows.showFavoriteMovies(favoriteMovies, loggedInUser.getNickname());
        } catch (Exception e) {
            dynamicWindows.showErrorMessage("Error al obtener películas favoritas: " + e.getMessage());
        }
    }
    
    // Manejador para configurar el usuario
    private static void handleConfigUser(Scanner sc, Client client) throws RemoteException {
    	dynamicWindows.showConfigUserAccount(loggedInUser);
    	System.out.print("Comando (ingrese 'volver' si quiere volver al menú): ");
    	
    	// Si dice volver, llevar al usuario al menú principal
        String command = sc.nextLine().trim();
        if (command.equalsIgnoreCase("volver")) {
            currentState = AppState.MAIN_MENU;
            return;
        }
        
        // Instanciar los valores
        String nombre = null;
        String apellido = null;
        String nickname = null;
    	
        if (command.startsWith("edit")){
        	
        	// Remover el "edit" dentro del comando
        	String argsString = command.substring("edit".length()).trim();
        	
        	// Utilizar expresiones regulares para definir y separar los --comando con los 'valores'
        	Pattern pattern = Pattern.compile("--(\\w+)\\s+'([^']*)'");
        	Matcher matcher = pattern.matcher(argsString);
        	
        	// Esto se repite mientras haya --comandos dados
        	while(matcher.find()){
        		String llave = matcher.group(1); // Tiene los comandos con --comando
        		String valor = matcher.group(2); // Tiene los valores de cada comando
        		
        		switch(llave) {
        		case "name":
        			nombre = valor;
        			break;
        		case "surname":
        			apellido = valor;
        			break;
        		case "nickname":
        			nickname = valor;
        			break;
        		default:
        			System.out.println("no es un comando valido");
        			break;
        		}
        	}
        	
        	// Ahora llamamos a la funcion que hara la actualizacion
        	Boolean[] resultados = client.editCuenta(nombre, apellido, nickname, loggedInUser.getID());
        	
        	// Verificar si se cambiaron los valores pertinentes
        	if (resultados[0]) {
        	    loggedInUser.setNombre(nombre);
        	    dynamicWindows.showSuccessMessage("Cambio el nombre exitosamente");
        	}
        	if (resultados[1]) {
        	    loggedInUser.setSurname(apellido);
        	    dynamicWindows.showSuccessMessage("Cambio el apellido exitosamente");
        	}
        	if (resultados[2]) {
        	    loggedInUser.setNickname(nickname);
        	    dynamicWindows.showSuccessMessage("Cambio el nickname exitosamente");
        	}
        	
        	try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				dynamicWindows.showErrorMessage("Error con el thread sleep");
			}
        	
        	return;
        }
    	
    }
    
    //Permite buscar usando los filtros opcionales genero, año y puntuacion minima
    private static void handleSearch(List<String> args, Client client) {
        String genero = null;
        int anio = -1;
        float rating = -1;
        StringBuilder searchCriteria = new StringBuilder();

        //Procesamiento de las flags
        try {
            for (int i = 0; i < args.size() - 1; i++) {
                switch (args.get(i)) {
                    case "-g":
                        genero = args.get(i + 1);
                        searchCriteria.append("Género: ").append(genero).append(" ");
                        break;
                    case "-y":
                        anio = Integer.parseInt(args.get(i + 1));
                        searchCriteria.append("Año: ").append(anio).append(" ");
                        break;
                    case "-r":
                        rating = Float.parseFloat(args.get(i + 1));
                        searchCriteria.append("Rating mín: ").append(rating).append(" ");
                        break;
                }
            }

            List<Movie> resultados = client.buscarPeliculas(client.getSessionToken(), genero, anio, rating);
            staticWindows.clearScreen();
            dynamicWindows.showMovieSearchResults(resultados, searchCriteria.toString().trim());

        } catch (NumberFormatException e) {
            dynamicWindows.showErrorMessage("Error en los parámetros de búsqueda. Verifique que el año y rating sean números válidos.");
        } catch (RemoteException e) {
            dynamicWindows.showErrorMessage("Error al buscar películas: " + e.getMessage());
        }
    }
    
    // Manejador de hacer una reseña
    private static void handleMakeReview(int movieId, Scanner sc, Client client) {
        staticWindows.showMakeReviewHeader(movieId);
        String reviewText = sc.nextLine().trim();

        if (reviewText.equalsIgnoreCase("cancelar")) {
            dynamicWindows.showInfoMessage("Creación de reseña cancelada.");
            return;
        }

        if (reviewText.isEmpty()) {
            dynamicWindows.showErrorMessage("La reseña no puede estar vacía.");
            return;
        }
        
        dynamicWindows.showReviewConfirmation(reviewText, movieId);
        dynamicWindows.showPendingFunctionality("make review " + movieId);
    }
    
    // Mostrar las ventanas de Dynamic o static window
    
    private static void handleShowUser() {
        dynamicWindows.showUserAccount(loggedInUser);
    }
    
    private static void showMainMenuDisplay(Persona user) {
        dynamicWindows.showMainMenuDisplay(user);
    }
    
    private static void showMainMenuPrompt() {
        staticWindows.showMainMenuPrompt(loggedInUser.getNickname());
    }
    
    private static void showLoginMenu() {
        staticWindows.showLoginMenu();
    }
}