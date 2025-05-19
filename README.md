# Proyecto en Java RMI
Proyecto de ICI4344-1 "Computación paralela y distribuida" hecho en Java RMI, junto a una base de datos en MySQL ejecutándose en Docker.

# Utilizando Docker
Descargar primero [Docker Desktop](https://www.docker.com/products/docker-desktop/), en caso de una distribución Linux bastaría con el [Docker Engine](https://docs.docker.com/engine/install/).
La única diferencia entre ellos dos es que uno es para utilizar en terminal y el otro tiene interfaz gráfica, pero son en sí lo mismo.

En la versión de desktop, busca en Docker hub, busca MySQL y pon el primero que sale de la búsqueda y en la parte superior derecha selecciona la opción ```pull```.
En la versión de terminal coloca el siguiente comando, recordar que en Linux usar ```sudo``` antes de cada comando:
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

Una vez dentro, usar la base de datos creada
```sql
USE bd_CPYD;
```

Para crear la tabla de personas, peliculas y reviews que utiliza el código en java:
```sql
CREATE TABLE tb_persona (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nickname VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    surname VARCHAR(150) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```
```sql
CREATE TABLE tb_movie (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    adult BOOLEAN DEFAULT FALSE,
    original_lang VARCHAR(10),
    description TEXT,
    popularity FLOAT DEFAULT 0.0,
    release_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```
```sql
CREATE TABLE tb_persona_favorite_movie (
    persona_id INT NOT NULL,
    movie_id INT NOT NULL,
    favorited_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (persona_id, movie_id),
    FOREIGN KEY (persona_id) REFERENCES tb_persona(id) ON DELETE CASCADE,
    FOREIGN KEY (movie_id) REFERENCES tb_movie(id) ON DELETE CASCADE
);
```
```sql
CREATE TABLE tb_review (
    id INT AUTO_INCREMENT PRIMARY KEY,
    movie_id INT NOT NULL,
    persona_id INT NOT NULL,
    review_text TEXT NOT NULL,
    rating TINYINT UNSIGNED,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (movie_id) REFERENCES tb_movie(id) ON DELETE CASCADE,
    FOREIGN KEY (persona_id) REFERENCES tb_persona(id) ON DELETE CASCADE,
    UNIQUE KEY uq_persona_movie_review (persona_id, movie_id)
);
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
Esta es la página de la [API](https://developer.themoviedb.org/reference/intro/getting-started), esta tiene información sobre peliculas.

Endpoints en uso por ahora:

En construcción
```shell
En proceso
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
