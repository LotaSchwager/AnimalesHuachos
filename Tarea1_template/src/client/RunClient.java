package client;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.InputMismatchException;
import java.util.Scanner;

import common.Persona;
import common.Movie;
import common.Review;

public class RunClient {

    // Enum para manejar los estados de la aplicación
    private enum AppState {
        LOGIN, // Menú de acceso
        MAIN_MENU, // Menú principal
        EXIT // Salir de la aplicación
    }

    private static AppState currentState = AppState.LOGIN;

    public static void main(String[] args) throws RemoteException, NotBoundException {
        Client client = new Client();
        
        
        try {
        	client.startClient();
        }catch(RemoteException | NotBoundException e) {
        	System.err.println("Error al conectar con el servidor RMI: " + e.getMessage());
        	e.printStackTrace();
        	System.err.println("La aplicacion se cerrara.");
        	return;
        }

        try (Scanner sc = new Scanner(System.in)) {
            while (currentState != AppState.EXIT) {

                clean(50);

                switch (currentState) {
                    case LOGIN:
                        showLoginMenu();
                        handleLoginInput(sc, client);
                        break;
                    case MAIN_MENU:
                        showMainMenu();
                        handleMainMenuInput(sc, client);
                        break;
                    case EXIT:
                        break;
                }
            }
            System.out.println("Saliendo de la aplicación...");
            client.logout();
        }
    }

    // --- Métodos para mostrar menús ---

    public static void showLoginMenu() {
        System.out.println("========== Menú de Acceso ==========");
        System.out.println("Para elegir una acción, escriba el número y presione 'Enter'.");
        System.out.println("========================\n");
        System.out.println("1. Iniciar sesion");
        System.out.println("2. Crear cuenta");
        System.out.println("3. Salir de la aplicación");
        System.out.println("========================\n");
        System.out.print("Escriba su opción aquí y presione Enter: ");
    }

    public static void showMainMenu() {
        System.out.println("========== Menú Principal ==========");
        System.out.println("Para elegir una acción, escriba el número y presione 'Enter'.");
        System.out.println("========================\n");
        System.out.println("1. Mostrar cuenta");
        System.out.println("2. Buscar peliculas");
        System.out.println("3. Mostrar favoritos");
        System.out.println("4. Mostrar reseñas personales");
        System.out.println("5. Salir de la aplicación");
        System.out.println("========================\n");
        System.out.print("Escriba su opción aquí y presione Enter: ");
    }

    // --- Métodos para manejar la entrada del usuario ---

    public static void handleLoginInput(Scanner sc, Client client) {
        int opcion = getUserInput(sc);

        switch (opcion) {
            case 1:
                System.out.println("\n--- Iniciar Sesión ---");
                System.out.print("Ingrese su nickname: ");
                String nicknameLogin = sc.nextLine();
                System.out.print("Ingrese su contraseña: ");
                String passwordLogin = sc.nextLine();
                
                try {
                	if(client.login(nicknameLogin, passwordLogin)) {
                		System.out.println("\nLogin exitoso!");
                		currentState = AppState.MAIN_MENU;
                	}else {
                		System.out.println("\nError: Nickname o contraseña incorrectos");
                		currentState = AppState.LOGIN;
                	}
                }catch(RemoteException e) {
                	System.err.println("\nError: " + e.getMessage());
                	e.printStackTrace();
                	System.out.println("Intente de nuevo más tarde.");
                	currentState = AppState.LOGIN;
                }
                waitForEnter(sc);
                break;
            case 2:
                System.out.println("\n--- Crear Cuenta ---");
                System.out.print("Ingrese un nickname: ");
                String nicknameCreate = sc.nextLine();
                System.out.print("Ingrese un nombre: ");
                String nameCreate = sc.nextLine();
                System.out.print("Ingrese un apellido: ");
                String surnameCreate = sc.nextLine();
                System.out.print("Ingrese una contraseña: ");
                String passwordCreate = sc.nextLine();
                
                try {
                    if (client.createAccount(nicknameCreate, nameCreate, surnameCreate, passwordCreate)) {
                        System.out.println("\nCuenta creada con éxito! Por favor, inicie sesión.");
                        currentState = AppState.LOGIN;
                    } else {
                        System.out.println("\nFallo al crear la cuenta. Es posible que el nickname ya exista.");
                        currentState = AppState.LOGIN;
                    }
                } catch (RemoteException e) {
                    System.err.println("\nError: " + e.getMessage());
                     e.printStackTrace();
                     System.out.println("Intente de nuevo más tarde.");
                    currentState = AppState.LOGIN;
                }
                
                waitForEnter(sc);
                break;
            case 3:
                currentState = AppState.EXIT;
                break;
            default:
                System.out.println("Opción no válida. Presione Enter para continuar...");
                waitForEnter(sc);
                currentState = AppState.LOGIN;
                break;
        }
    }

    public static void handleMainMenuInput(Scanner sc, Client client) {
        int opcion = getUserInput(sc);

        switch (opcion) {
            case 1:
                System.out.println("\n========== Mostrar cuenta ==========");
                Persona cuentaInfo;
				try {
					cuentaInfo = client.mostrarCuenta();
	                if (cuentaInfo != null) {
	                    System.out.println("ID: " + cuentaInfo.getID());
	                    System.out.println("Nickname: " + cuentaInfo.getNickname());
	                    System.out.println("Nombre: " + cuentaInfo.getNombre());
	                    System.out.println("Apellido: " + cuentaInfo.getSurname());
	                    System.out.println("");
	                } else {
	                    System.out.println("No se pudieron obtener los datos de la cuenta.");
	                    currentState = AppState.MAIN_MENU;
	                }
				} catch (RemoteException e) {
                    System.err.println("\nError: " + e.getMessage());
                    e.printStackTrace();
                    System.out.println("Intente de nuevo más tarde.");
                    currentState = AppState.MAIN_MENU;
				}
				System.out.println("========================\n");

                waitForEnter(sc);
                break;
            case 2:
                System.out.println("\n--- Buscar películas (Funcionalidad pendiente) ---");
                waitForEnter(sc);
                break;
            case 3:
                System.out.println("\n--- Mostrar favoritos (Funcionalidad pendiente) ---");
                waitForEnter(sc);
                break;
            case 4:
                System.out.println("\n--- Mostrar reseñas personales (Funcionalidad pendiente) ---");
                waitForEnter(sc);
                break;
            case 5:
                currentState = AppState.EXIT; 
                break;
            default:
                System.out.println("Opción no válida. Presione Enter para continuar...");
                waitForEnter(sc);
                currentState = AppState.MAIN_MENU; 
                break;
        }
    }

    // --- Métodos de utilidad ---

    // Método para obtener la entrada del usuario de forma segura
    private static int getUserInput(Scanner sc) {
        try {
            System.out.print(">> ");
            int option = sc.nextInt();
            sc.nextLine();
            return option;
        } catch (InputMismatchException e) {
            System.out.println("Entrada no válida. Por favor ingrese un número.");
            sc.nextLine();
            // Devolver -1 para indicar una entrada inválida
            return -1; 
        }
    }

    // Método para esperar a que el usuario presione Enter
    private static void waitForEnter(Scanner sc) {
        System.out.print("Presione Enter para continuar...");
        sc.nextLine();
    }

    // Método para limpiar la consola (imprimir líneas en blanco)
    public static void clean(int lines) {
        for (int i = 0; i < lines; i++) {
            System.out.println();
        }
    }
}