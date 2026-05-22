# E-commerce Sistema

Sistema de e-commerce desarrollado con Spring Boot 3.4.2, siguiendo principios DDD y Arquitectura Hexagonal.

## Requisitos

- Java 17
- Maven 3.6+
- MySQL 8.0 o PostgreSQL 13+

## Configuración

1. Crear base de datos:
   - MySQL: `CREATE DATABASE PanaderiaBarriosDB;`
   - PostgreSQL: `CREATE DATABASE ecommerce_db;`

2. Configurar credenciales en `application-dev-mysql.properties` o `application-dev-postgresql.properties`

3. Ejecutar:
   ```bash
   mvn spring-boot:run
   ```

## Acceso

- Admin: admin@example.com / admin123
- Cliente: cliente@example.com / 123456

## Perfiles

- `dev-mysql`: Para MySQL
- `dev-postgresql`: Para PostgreSQL

Cambiar en `application.properties`: `spring.profiles.active=dev-postgresql`

## Pruebas

```bash
mvn test
```

Cobertura: `mvn jacoco:report`

## Arquitectura

- **Domain**: Entidades, Value Objects, Repositorios (interfaces)
- **Application**: Servicios de aplicación
- **Infrastructure**: Adaptadores (JPA, Web, Security)

## Tecnologías

- Spring Boot 3.4.2
- Spring Security
- Spring Data JPA
- Thymeleaf + Bootstrap 5
- JaCoCo para cobertura
- Cucumber para BDD