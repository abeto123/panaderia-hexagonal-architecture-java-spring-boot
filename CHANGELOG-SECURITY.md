# CHANGELOG — Mejoras de Seguridad v1.1 (2026-06-19)

## 🔒 CAMBIOS CRÍTICOS DE SEGURIDAD

### 1. Eliminación de Exposición de Credenciales
**Fecha**: 2026-06-19  
**Severidad**: 🔴 CRÍTICA  
**Cambios**:
- Migrado `spring.datasource.username=root` → `${DB_USER:root}` (variable de entorno)
- Migrado `spring.datasource.password=` → `${DB_PASSWORD:}` (variable de entorno)
- Agregado `.env.example` con instrucciones
- Archivo: `application-dev-mysql.properties`

**Impacto**: Credenciales de base de datos ya no están expuestas en control de versiones

---

### 2. Eliminación de PostgreSQL (Superficie de Ataque Reducida)
**Fecha**: 2026-06-19  
**Severidad**: 🟡 MEDIA  
**Cambios**:
- Removido: `application-dev-postgresql.properties` (no se utilizaba)
- Actualizado: `.gitignore` para excluir `*postgresql*.properties`
- Actualizado: Registro de ICs (eliminado IC-12, ahora 19 ICs)
- Actualizado: README.md con cambio reciente notificado

**Impacto**: Reducción de complejidad de configuración, menor superficie de ataque

---

### 3. Corrección de Vulnerabilidad Crítica: Admin Panel Accesible
**Fecha**: 2026-06-19  
**Severidad**: 🔴 CRÍTICA  
**Cambios**:
- Antes: `/admin/**` → `.permitAll()` (⚠️ CUALQUIERA podía acceder)
- Ahora: `/admin/**` → `.hasRole("ADMIN")` (✅ Solo administradores)
- Archivo: `SecurityConfig.java` línea 27
- Agregado: CSRF protection configurado

**Impacto**: CRÍTICO — El panel de administración ahora solo es accesible por usuarios con rol ADMIN

---

### 4. Eliminación de Archivos de Debug Sensibles
**Fecha**: 2026-06-19  
**Severidad**: 🔴 CRÍTICA  
**Archivos Eliminados**:
- `cookies.txt` (contenía JSESSIONID expuesto)
- `curl_login_debug.txt` (debug de login con URLs)
- `admin_pedidos.html` (archivo de prueba no versionado)

**Impacto**: Sesiones y información de debug ya no están públicamente accesibles

---

### 5. Hardening de Configuración de Logging
**Fecha**: 2026-06-19  
**Severidad**: 🟡 MEDIA  
**Cambios**:
- `spring.jpa.show-sql=true` → `spring.jpa.show-sql=false`
- Agregados comentarios en `application-dev-mysql.properties`
- Agregado: Connection pooling config (HikariCP)

**Impacto**: SQL queries ya no se registran en logs, reduciendo exposición

---

### 6. Refuerzo de .gitignore
**Fecha**: 2026-06-19  
**Severidad**: 🟡 MEDIA  
**Agregaciones**:
- `.env`, `.env.local` — Variables de entorno
- `credentials.json`, `secrets.json` — Archivos de secretos
- `application-prod*.properties` — Configuración de producción
- `*postgresql*.properties` — Perfiles PostgreSQL
- Logs, SWP files, cachés — Archivos temporales

**Impacto**: Reduce riesgo de exposición accidental de secretos

---

## 📋 Deudas Técnicas Documentadas

### Pendientes para Producción (MVP → v1.2)

| Deuda | Severidad | Fecha Objetivo | Responsable |
|---|---|---|---|
| HTTPS/SSL Certificate | 🔴 CRÍTICA | 2026-07-15 | DevOps |
| CORS Restrictivo | 🔴 CRÍTICA | 2026-07-01 | Security/DevOps |
| Input Validation (XSS) | 🔴 CRÍTICA | 2026-07-05 | QA/Backend |
| MySQL Backups Automáticos | 🔴 CRÍTICA | 2026-06-30 | DevOps |
| Rate Limiting | 🟡 MEDIA | 2026-07-10 | Backend |
| Audit Logging | 🟡 MEDIA | 2026-07-20 | Backend |
| Penetration Testing | 🟡 MEDIA | 2026-08-01 | QA/Security |

**Referencia**: Ver `SCM-DEUDA.md` para detalles completos

---

## 🔍 Auditoría de Seguridad (Resumen)

### ✅ Verificado y Seguro
- SQL Injection prevention: Usando JPA parámetros ✅
- Password Hashing: BCrypt implementado ✅
- Role-Based Access Control (RBAC): CLIENTE/ADMIN ✅

### ⏳ Pendiente para Próxima Fase
- HTTPS/TLS encryption
- CORS configuration
- Input validation
- Rate limiting
- Audit logging
- Backups automation

---

## 📁 Archivos Modificados

### Creados
- `.env.example` — Template para variables de entorno
- `SECURITY.md` — Documento de recomendaciones de seguridad
- `CHANGELOG.md` — Este archivo

### Modificados
- `application-dev-mysql.properties` — Agregadas variables de entorno
- `.gitignore` — Agregadas exclusiones para archivos sensibles
- `SecurityConfig.java` — Corregida vulnerabilidad de admin panel
- `README.md` — Actualizado registro de ICs
- `SCM-DEUDA.md` — Actualizado con cambios resueltos

### Eliminados
- `application-dev-postgresql.properties` — Ya no existe
- `cookies.txt` — Sesiones expuestas
- `curl_login_debug.txt` — Debug info
- `admin_pedidos.html` — Archivo de prueba

---

## 📊 Resumen de Cambios

| Categoría | Cambios | Estado |
|---|---|---|
| **Credenciales** | Variables de entorno | ✅ RESUELTO |
| **Admin Access** | Requiere rol ADMIN | ✅ RESUELTO |
| **Debug Files** | Eliminados | ✅ RESUELTO |
| **PostgreSQL** | Removido | ✅ RESUELTO |
| **Logging** | SQL ocultado | ✅ RESUELTO |
| **HTTPS** | Pendiente | ⏳ DEUDA |
| **CORS** | Pendiente | ⏳ DEUDA |
| **Input Validation** | Pendiente | ⏳ DEUDA |

---

## 🚀 Próximos Pasos

1. **Inmediato (Esta semana)**:
   - [ ] Configurar variables de entorno `.env` en desarrollo
   - [ ] Verificar que admin panel requiere autenticación
   - [ ] Probar build sin archivos de debug

2. **Corto plazo (Próxima semana)**:
   - [ ] Implementar CORS restrictivo
   - [ ] Agregar validación de input
   - [ ] Configurar rate limiting

3. **Producción**:
   - [ ] Generar certificado SSL/TLS
   - [ ] Configurar backups automáticos
   - [ ] Realizar penetration testing
   - [ ] Implementar audit logging

---

## 📞 Referencias

- **SECURITY.md** — Recomendaciones detalladas de seguridad
- **SCM-DEUDA.md** — Deudas técnicas completas
- **README.md** — Registro de ICs actualizado
- **OWASP Top 10 2023** — Estándares de seguridad

---

**Documento creado**: 2026-06-19  
**Equipo**: DevOps & Security  
**Próxima revisión**: 2026-07-01  
**Estado de Seguridad MVP v1.1**: 🟡 PARCIALMENTE ENDURECIDO (5 de 8 deudas críticas resueltas)
