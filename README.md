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

## Registro de Ítems de Configuración (IC) — Línea Base v1.1.0

Este registro incluye **19 ítems de configuración** distribuidos en todas las categorías críticas. Cada IC tiene asignado un **responsable nominal**, un **rol estandarizado** y una **criticidad** basada en impacto operativo.

**CAMBIO RECIENTE (2026-06-19)**: PostgreSQL eliminado por no ser utilizado en producción (reduce superficie de ataque).

### Tabla Resumida de ICs

| ID-IC | Categoría | Nombre | Responsable | Rol | Criticidad |
|---|---|---|---|---|---|
| IC-01 | Código | pom.xml | Guillermo Mogrovejo | Backend Lead | Alta |
| IC-02 | Código | EcommerceApplication | Kevin Calle | Backend Developer | Alta |
| IC-03 | Código | SecurityConfig | Noemi Chura | Security Engineer | Alta |
| IC-04 | Código | LoginController | Alberto Barrios | Backend Developer | Media |
| IC-05 | Código | ProductoAdminController | Alberto Barrios | Backend Developer | Media |
| IC-06 | Infraestructura | mvnw | Guillermo Mogrovejo | DevOps | Alta |
| IC-07 | Infraestructura | mvnw.cmd | Kevin Calle | DevOps | Alta |
| IC-08 | Infraestructura | maven-wrapper.properties | Guillermo Mogrovejo | DevOps | Alta |
| IC-09 | Infraestructura | panaderiaDB.sql | Alberto Barrios | Data Engineer | Media |
| IC-10 | Configuración | application.properties | Katherin Quispe | DevOps | Alta |
| IC-11 | Configuración | application-dev-mysql.properties | Guillermo Mogrovejo | DevOps | Alta |
| IC-12 | Configuración | schema.sql | Alberto Barrios | Data Engineer | Media |
| IC-13 | Documentación | README.md | Lizeth Hancco | Architect | Baja |
| IC-14 | Documentación | TABLA_VINCULACION_ARQUITECTONICA.md | Leticia Calderón | Architect | Baja |
| IC-15 | Configuración | data.sql | Katherin Quispe | Data Engineer | Media |
| IC-16 | Código | ProductoService | Kevin Calle | Backend Developer | Media |
| IC-17 | Código | ClienteService | Noemi Chura | Backend Developer | Media |
| IC-18 | Código | AdminController | Leticia Calderón | Backend Developer | Media |
| IC-19 | Infraestructura | carrito.js | Katherin Quispe | Frontend Developer | Baja |

### Criterios de Criticidad Aplicados

- **Alta**: Bloquea compilación o despliegue en < 30 minutos
- **Media**: Afecta flujos entre 30 minutos y 4 horas
- **Baja**: Impacto > 4 horas o documental

### Notas

- Cada IC tiene un **responsable nominal** único (no "el grupo"), facilitando trazabilidad operativa
- Se asignaron **roles estandarizados**: Backend Lead, Backend Developer, DevOps, Data Engineer, Security Engineer, Architect, Frontend Developer
- Para el informe formal completo, consultar [Registro_ICs_Baseline_v0.md](Registro_ICs_Baseline_v0.md)
- Deudas técnicas del SCM registradas en [SCM-DEUDA.md](SCM-DEUDA.md)
- Recomendaciones de seguridad en [SECURITY.md](SECURITY.md)

---

## 📚 Documentación de Apoyo

| Documento | Propósito |
|---|---|
| [**SECURITY.md**](SECURITY.md) | Recomendaciones de seguridad y checklist pre-producción |
| [**SECURITY-SUMMARY.md**](SECURITY-SUMMARY.md) | Resumen ejecutivo de cambios de seguridad |
| [**STRUCTURE-OPTIMIZATION.md**](STRUCTURE-OPTIMIZATION.md) | Guía de estructura optimizada y limpieza del repositorio |
| [**SCM-DEUDA.md**](SCM-DEUDA.md) | Deudas técnicas con fechas objetivo |
| [**CHANGELOG-SECURITY.md**](CHANGELOG-SECURITY.md) | Histórico detallado de cambios de seguridad |

---

## 🔧 Configuración Inicial para Desarrolladores

**Después de clonar el repositorio**:

```bash
# 1. Instalar dependencias y compilar
mvn clean compile

# 2. Configurar variables de entorno (ver .env.example)
export DB_USER=root
export DB_PASSWORD=tu_contraseña_segura

# 3. Ejecutar tests
mvn test

# 4. Iniciar la aplicación
mvn spring-boot:run
```

Para más detalles sobre estructura y limpieza, ver [STRUCTURE-OPTIMIZATION.md](STRUCTURE-OPTIMIZATION.md)