# Eco Solicitud

Aplicación Java que se ejecuta en el puerto 8080 utilizando Spring Boot.

## Requisitos

- Java 11 o superior
- Maven

## Configuración del Entorno

### Modo Desarrollo (H2 Database)

1. Ejecutar el script `set-env.bat` para configurar las variables de entorno necesarias:
   ```bash
   set-env.bat
   ```

2. La aplicación utilizará H2 Database (en memoria) por defecto

### Modo Producción (PostgreSQL + AWS)

1. Editar el script `set-env.bat` y descomentar la línea:
   ```bash
   set SPRING_PROFILES_ACTIVE=prod
   ```

2. Ejecutar el script para configurar las variables de entorno:
   ```bash
   set-env.bat
   ```

## Cómo ejecutar la aplicación

1. Clonar el repositorio
2. Navegar al directorio del proyecto
3. Configurar el entorno como se indicó anteriormente
4. Ejecutar el siguiente comando:

```bash
mvn spring-boot:run
```

5. Acceder a la aplicación en [http://localhost:8080](http://localhost:8080)

## Estructura del proyecto

- `src/main/java/com/eco/app/` - Código fuente de la aplicación
- `src/main/resources/` - Archivos de configuración
