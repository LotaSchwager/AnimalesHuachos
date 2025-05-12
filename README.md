# AnimalesHuachos
Proyecto de ICI4344-1 "Computación paralela y distribuida" hecho en Java RMI, junto a una base de datos en MySQL ejecutándose en Docker.

# Utilizando Docker
Descargar primero [Docker Desktop](https://www.docker.com/products/docker-desktop/), en caso de una distribución Linux bastaría con el [Docker Engine](https://docs.docker.com/engine/install/).
La única diferencia entre ellos dos es que uno es para utilizar en terminal y el otro tiene interfaz gráfica, pero son en sí lo mismo.

En la versión de desktop, busca en Docker hub, busca MySQL y pon el primero que sale de la búsqueda y en la parte superior derecha selecciona la opción ```pull```.
En la versión de terminal coloca el siguiente comando:
```shell
docker pull mysql:latest
```

De aqui en adelante solo utilice la terminal.

## Para crear un contenedor de MySQL

Utiliza este comando en el terminal:
```shell
docker run --name nombre-del-contenedor -e MYSQL_ROOT_PASSWORD=la-contraseña-del-root -p 3307:3306 -d mysql:latest
```

En mi caso puse estos valores:<br>
* **nombre-del-contenedor**: mysql-cpyd <br>
* **MYSQL_ROOT_PASSWORD**: 1234

## Para ingresar a MySQL a través de Docker

Utiliza el siguiente comando en el terminal:
```shell
docker exec -it nombre-del-contenedor mysql -u root -p
```
Te pedirá contraseña, esta es la que utilizaste en ```MYSQL_ROOT_PASSWORD```.

## Dentro de MySQL

Para crear una base de datos
```sql
CREATE DATABASE bd_CPYD;
```

Para crear la tabla de personas que utiliza el código en java:
```sql
CREATE TABLE tb_persona (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    age INT
);

INSERT INTO tb_persona (name, age) VALUES ('Ademir Munoz', 26), ('Nestor Retamal', 27);
```

## Dependencias de MySQL en Java

En este caso tendran que añadir una dependencia dentro del proyecto de eclipse, para esto deben:

1. Apretar clic derecho dentro del árbol de carpetas del proyecto, sobre la carpeta que dice ```Tarea1_template```
2. Ir a la opción que dice ```Build Path -> Configure Build Path```
3. Ir a la opción que dice ```Libraries```
4. Seleccionar ```Modulepath``` y apretar ```Add_JARs...```
5. Buscar por ```mysql-connector-j-9.3.0```
6. Apply and Close, con eso debería funcionar

Parámetros para la conexión con MySQL desde Java:
```java
private String connectionURL = "jdbc:mysql://localhost:3307/bd_CPYD";
private String user = "root";
private String password = "1234";
```

# API
Esta es la página de la [API](https://huachitos.cl/nosotros), esta tiene información sobre animales en adopción.

Endpoints en uso por ahora:

Método ```GET```, esta entrega una lista con todos los animales en adopción.
```shell
https://huachitos.cl/api/animales
```

## Dependencias de la API en Java

(Es lo mismo que la vez anterior solo que con otros .jar)
En este caso tendran que añadir una dependencia dentro del proyecto de eclipse, para esto deben:

1. Apretar clic derecho dentro del árbol de carpetas del proyecto, sobre la carpeta que dice ```Tarea1_template```
2. Ir a la opción que dice ```Build Path -> Configure Build Path```
3. Ir a la opción que dice ```Libraries```
4. Seleccionar ```Modulepath``` y apretar ```Add_JARs...```
5. Buscar por ```jackson-annotations-2.14.0``` ```jackson-core-2.14.0``` ```jackson-databind-2.14.0```
6. Apply and Close, con eso debería funcionar
