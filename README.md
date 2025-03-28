### API SERVICIO TASK

Este documento, presenta como usar la API task para hacer las operaciones CRUD

## información técnica
* Java: JDK 17
* SPRING: JPA, POSTGRESQL, SPRING SECURITY, JWT, HIBERNATE.
* SEGURIDAD: Validacion de inicio de sesion por medio de token, impedimento de fuerza bruta (bloquea la cuenta tras 3 intentos fallidos de login), refesco de tokens.
* Base de datos: PostgreSQL
* Docker: contendor por cada proyecto (backend, BD, frontend).
* Documentación en POSTMAN.
* MAVEN.
* Arquitectura: MVC - DISEÑO: repository.

## uso
* Debes clonar este repositorio desde github y ponerlo en una carpeta de libre elección.
*  utilizar tu IDE favorito (VScode, IDEA, Neatbeans, etc.).
*  Ejecuta la clase principal y el servicio quedara corriendo en localhost:8080.

## uso mas practico.

Si no cuentas con todas las especificaciones tecnicas mencionadas y requieres ejecutar todo el proyecto, hacemos lo siguiente:

#### Instrucciones para ejecutar el proyecto usando Docker
1. Instalar Docker y Docker Compose (si no lo tiene instalado):
2. Clonar el repositorio del proyecto.
3. Acceder al directorio del proyecto.
4. Revisar el archivo *docker-compose.yml*: Asegúrate de que el archivo docker-compose.yml.
5. Construir y levantar los contenedores con Docker Compose: Pídele que ejecute el siguiente comando para construir las imágenes de Docker y levantar los contenedores:
   **docker-compose up --build**
   Esto descargará las imágenes necesarias, construirá los contenedores y los levantará.
6. este proyecto tiene un archivo Dockerfile.
7. El backend estará disponible en: **http://localhost:8080**
8. El frontend estará disponible en: **http://localhost:4200**
9. La base de datos estará disponible en: **http://localhost:5432**

Y listo, podremos acceder de manera local al proyecto.





