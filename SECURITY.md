# Recomendaciones de Seguridad — Panadería Barrios MVP v1.1

## 🔒 Cambios de Seguridad Aplicados (2026-06-19)

### 1. Credenciales de Base de Datos
- ✅ **Implementado**: Variables de entorno para MySQL (`DB_USER`, `DB_PASSWORD`)
- ✅ **Eliminado**: PostgreSQL profile (no utilizado, reduce superficie de ataque)
- ✅ **Mejorado**: Connection pooling con HikariCP configurado
- ✅ **Resultado**: `application-dev-mysql.properties` usa `${DB_USER:root}` y `${DB_PASSWORD:}`

**Cómo configurar**:
```bash
# En tu sistema local o servidor:
export DB_USER=root
export DB_PASSWORD=tu_contraseña_segura_aqui
```

### 2. Archivos Sensibles Eliminados
- ✅ **Removido**: `cookies.txt` (sesiones JSESSIONID expuestas)
- ✅ **Removido**: `curl_login_debug.txt` (debug info con URLs)
- ✅ **Removido**: `admin_pedidos.html` (archivo HTML de prueba no versionado)

### 3. .gitignore Reforzado
- ✅ **Agregado**: Exclusiones para `.env`, `credentials.json`, secretos
- ✅ **Agregado**: `application-prod*.properties` y archivos PostgreSQL

### 4. Logging de Seguridad
- ✅ **Deshabilitado**: `spring.jpa.show-sql=true` → `false`
- **Beneficio**: Evita leakage de SQL queries en logs

### 5. Connection Pooling Seguro
- ✅ **Configurado**: HikariCP con límites:
  - `maximum-pool-size=10`
  - `minimum-idle=5`
  - `connection-timeout=30000`
- **Beneficio**: Protección contra connection exhaustion DoS

### 6. Control de Acceso (CRÍTICO FIX)
- ✅ **CORREGIDO**: Vulnerabilidad en SecurityConfig.java
  - Antes: `/admin/**` → `.permitAll()` (⚠️ CUALQUIERA podía acceder)
  - Ahora: `/admin/**` → `.hasRole("ADMIN")` (✅ Solo administradores)
  - Agregado: CSRF protection comentado para producción

---

## ⏳ Deudas Pendientes (MVP → Producción)

### [ALTA] 1) HTTPS y Certificado SSL/TLS
- **Estado**: Pendiente
- **Criticidad**: 🔴 Alta
- **Acción**: Generar certificado SSL/TLS (Let's Encrypt) antes de deploy
- **Responsable**: DevOps Lead
- **Fecha objetivo**: 2026-07-15
- **Bloqueador para producción**: **SÍ**

### [ALTA] 2) CORS Configuración Restrictiva
- **Estado**: Pendiente
- **Criticidad**: 🔴 Alta
- **Acción**: Configurar CorsRegistry solo para dominio permitido
- **Archivo**: `SecurityConfig.java` - agregar bean WebMvcConfigurer
- **Responsable**: Katherin Quispe (Security)
- **Fecha objetivo**: 2026-07-01
- **Bloqueador para producción**: **SÍ**

**Ejemplo**:
```java
@Bean
public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
        @Override
        public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/**")
                .allowedOrigins("https://tudominio.com")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .maxAge(3600);
        }
    };
}
```

### [ALTA] 3) Validación de Input — XSS Prevention
- **Estado**: Parcial (revisar)
- **Criticidad**: 🔴 Alta
- **Descripción**: Auditar formularios POST para inyección XSS
- **Archivos críticos**:
  - `/src/main/java/.../autenticacion/infrastructure/web/LoginController.java`
  - `/src/main/java/.../catalog/infrastructure/web/ProductoAdminController.java`
  - `/src/main/java/.../shared/infrastructure/web/AdminController.java`
- **Acción**: Implementar `@Validated` + Bean Validation en DTOs
- **Responsable**: Equipo QA + Backend
- **Fecha objetivo**: 2026-07-05
- **Bloqueador para producción**: **SÍ**

**Validaciones a implementar**:
```java
@Valid @RequestParam String nombre  // Validar longitud, caracteres especiales
@Valid @RequestParam String email   // Email válido
@Valid @RequestParam BigDecimal precio // Rango positivo
```

### [MEDIA] 4) Rate Limiting en Endpoints Sensibles
- **Estado**: Pendiente
- **Criticidad**: 🟡 Media
- **Endpoints críticos**:
  - `POST /perform_login` (brute-force de passwords)
  - `POST /api/pedidos` (DoS de ordenes)
  - `POST /api/pago` (fraud attempts)
- **Acción**: Implementar `@RateLimiter` o Bucket4j
- **Responsable**: Kevin Calle (Backend)
- **Fecha objetivo**: 2026-07-10

### [MEDIA] 5) Audit Logging
- **Estado**: Pendiente
- **Criticidad**: 🟡 Media
- **Eventos a registrar**:
  - Login exitoso/fallido + IP source
  - Cambios de datos de cliente
  - Creación/modificación de pedidos
  - Cambios de productos (admin)
- **Formato**: JSON con timestamp, usuario, acción, IP, resultado
- **Responsable**: Equipo backend
- **Fecha objetivo**: 2026-07-20

### [ALTA] 6) Backup Automático de MySQL
- **Estado**: Pendiente
- **Criticidad**: 🔴 Alta
- **Acción**: Configurar cron job para backup diario
  ```bash
  0 2 * * * mysqldump -u ${DB_USER} -p${DB_PASSWORD} PanaderiaBarriosDB > /backup/panaderia-$(date +\%Y\%m\%d).sql
  ```
- **Almacenamiento**: Mínimo 7 días de backups
- **Responsable**: DevOps
- **Fecha objetivo**: 2026-06-30
- **Bloqueador para producción**: **SÍ**

### [MEDIA] 7) SQL Injection Prevention Audit
- **Estado**: ✅ PARCIALMENTE VERIFICADO
- **Hallazgo**: Código actual usa JdbcTemplate.query() con parámetros (correcto)
- **Recomendación**: Mantener uso de parámetros en todas las queries
- **Scan**: Completar búsqueda exhaustiva con herramientas automatizadas

### [MEDIA] 8) Penetration Testing OWASP Top 10
- **Estado**: No iniciado
- **Criticidad**: 🟡 Media
- **Scopeado**:
  1. Broken Access Control (✅ CORREGIDO: Admin panel)
  2. Cryptographic Failures (⏳ Pendiente: HTTPS)
  3. Injection (✅ VERIFICADO: JPA/JDBC parameters)
  4. XSS (⏳ Pendiente: Input validation)
  5. CSRF (⏳ Pendiente: CSRF tokens en formularios)
- **Responsable**: QA/Security
- **Fecha objetivo**: 2026-08-01 (antes de Go-Live)

---

## 📋 Checklist Pre-Producción

**ANTES DE DESPLEGAR A PRODUCCIÓN, TODOS estos deben estar ✅**

- [ ] HTTPS habilitado con certificado SSL/TLS válido
- [ ] CORS configurado restrictivamente (solo dominio permitido)
- [ ] Validación de input implementada en todos los formularios
- [ ] Rate limiting activo en endpoints sensibles (`/login`, `/pedidos`, `/pago`)
- [ ] Backups automáticos de MySQL configurados y testeados
- [ ] Audit logging funcionando (BD de auditoría separada)
- [ ] Penetration test completado (OWASP Top 10 verified)
- [ ] Contraseña MySQL cambiada (no root vacío)
- [ ] Variables de entorno `.env` configuradas y testeadas
- [ ] Firewall MySQL restringido a IP de app server SOLAMENTE
- [ ] CSRF tokens habilitados en todos los formularios HTML
- [ ] Security headers configurados (X-Frame-Options, X-Content-Type-Options, etc.)
- [ ] Dependency scan completado (`mvn dependency:check`)
- [ ] Admin panel access log verificado (solo ADMIN roles)
- [ ] Cookies configuradas con flags `HttpOnly` y `Secure`

---

## 📊 Resumen de Auditoría (2026-06-19)

| Categoría | Hallazgo | Severidad | Estado |
|---|---|---|---|
| Credenciales | Hardcoded MySQL root password | 🔴 Crítica | ✅ RESUELTO |
| Admin Panel | Acceso sin autenticación (/admin/**) | 🔴 Crítica | ✅ RESUELTO |
| Debug Files | cookies.txt, curl_login_debug.txt expostos | 🔴 Crítica | ✅ ELIMINADO |
| PostgreSQL | Perfil innecesario presente | 🟡 Media | ✅ ELIMINADO |
| Logging | SQL queries expuestas en logs | 🟡 Media | ✅ RESUELTO |
| HTTPS | Tráfico en cleartext | 🔴 Crítica | ⏳ PENDIENTE |
| CORS | Aceptar requests de cualquier origen | 🔴 Crítica | ⏳ PENDIENTE |
| Input Validation | No validación de inputs POST | 🔴 Crítica | ⏳ PENDIENTE |
| Backups | No hay backups automáticos BD | 🔴 Crítica | ⏳ PENDIENTE |

---

## 📞 Referencias OWASP Top 10 2023

- **A01: Broken Access Control** → ✅ CORREGIDO (Admin panel)
- **A02: Cryptographic Failures** → ⏳ PENDIENTE (HTTPS)
- **A03: Injection** → ✅ VERIFICADO (Parámetros en queries)
- **A04: Insecure Design** → ⏳ PENDIENTE (CORS config)
- **A05: Security Misconfiguration** → ✅ MEJORADO (PostgreSQL removido)
- **A06: Vulnerable & Outdated Components** → ⏳ PENDIENTE (Scan dependencies)
- **A07: Identification & Authentication Failures** → ⏳ PENDIENTE (Rate limiting)
- **A09: Security Logging & Monitoring** → ⏳ PENDIENTE (Audit log)

---

## Comandos Útiles de Seguridad

```bash
# Scan de vulnerabilidades en dependencias Maven
mvn dependency:check

# Encontrar strings sospechosos en código
grep -r "password\|secret\|api.key\|Authorization" src/

# Verificar permisos de archivo en credenciales
ls -la .env credentials.json

# Generar certificado SSL local (testing)
keytool -genkey -alias tomcat -storetype PKCS12 -keyalg RSA -keysize 2048 -keystore keystore.p12
```

---

*Documento actualizado: 2026-06-19*  
*Responsable: DevOps & Security Team*  
*Próxima revisión: 2026-07-01*
