package client;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;

public class RunClient {
	public static void main(String[] args) throws RemoteException,NotBoundException {
		Client client = new Client();
		client.startClient();
		
		try (Scanner sc = new Scanner(System.in)) {
			while(true) {
				menu();
				int opcion = sc.nextInt();
				sc.nextLine();
				
				switch(opcion) {
				case 1:
					clean(10);
					client.mostrarPersonas();
					System.out.print("Presione Enter para volver al menú...");
	                sc.nextLine();
	                clean(50);
					break;
				case 2:
					clean(25);
					System.out.println("========================\n");
					System.out.print("Ingrese el nombre de la persona: ");
				    String nombre = sc.nextLine();
				    System.out.print("Ingrese la edad de la persona: ");
				    int edad = sc.nextInt();
				    sc.nextLine();
				    System.out.println("========================\n");
				    client.getServer().agregarPersona(nombre, edad);
				    System.out.println("Persona agregada con éxito\n");
					System.out.print("Presione Enter para volver al menú...");
	                sc.nextLine();
	                clean(50);
					break;
				case 3:
					clean(10);
					client.mostrarAnimales();
					System.out.print("Presione Enter para volver al menú...");
	                sc.nextLine();
	                clean(50);
					break;
				case 4:
					System.out.println("Saliendo de la aplicación.......");
					System.exit(0);
				default:
                    System.out.println("Opción no válida. Presione Enter para continuar...");
                    sc.nextLine();
                    clean(50);
                    break;
				}
			}
		}
		
	}
	
	public static void menu() 
	{
		System.out.println("==========Menú==========");
		System.out.println("Para elegir una acción, escriba el número que aparece al lado de la opción y luego presione la tecla 'Enter'.");
		System.out.println("========================\n");
		System.out.println("1. Mostrar lista de personas\n");
		System.out.println("2. Agregar una persona a la lista\n");
		System.out.println("3. Mostrar lista de animales\n");
		System.out.println("4. Salir de la aplicación");
		System.out.println("========================\n");
		System.out.print("Escriba su opción aquí y presione Enter: ");
	}
	
	 public static void clean(int i)
	 {
		 for(int clear = 0; clear < i; clear++) 
		 {
			 System.out.println("") ;
		 }
	}
}