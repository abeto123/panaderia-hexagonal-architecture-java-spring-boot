# SCM Debt & Security Debt — Registro de Deudas Técnicas

## Contexto
Este documento registra deudas técnicas de configuración (SCM) y seguridad que fueron resueltas y las que aún están pendientes para el paso a producción.

---

## ✅ DEUDAS RESUELTAS (2026-06-19)

### 1) PostgreSQL Eliminado
- **Resolución**: Completamente removido (no se utilizaba)
- **Beneficio**: Reducción de superficie de ataque, simplificación de config
- **Archivos eliminados**: `application-dev-postgresql.properties`
- **Status**: ✅ **RESUELTO**

### 2) Credenciales en Variables de Entorno
- **Resolución**: Migrado `DB_USER` y `DB_PASSWORD` a variables de entorno en `application-dev-mysql.properties`
- **Beneficio**: Evita exponer credenciales en repositorio
- **Archivo actualizado**: `application-dev-mysql.properties`
- **Implementación**: Usar `${DB_USER:root}` y `${DB_PASSWORD:}` con fallback
- **Status**: ✅ **RESUELTO**

### 3) .gitignore Reforzado con Secretos
- **Resolución**: Agregadas exclusiones para archivos sensibles:
  - `.env`, `.env.local`, `credentials.json`, `secrets.json`
  - `application-prod*.properties`
  - `*postgresql*.properties`
- **Status**: ✅ **RESUELTO**

### 4) Logging de Seguridad
- **Resolución**: Deshabilitado `spring.jpa.show-sql=true` → `false`
- **Beneficio**: Evita leakage de queries en logs
- **Status**: ✅ **RESUELTO**

### 5) Connection Pooling Seguro
- **Resolución**: Configurado HikariCP con límites:
  - `maximum-pool-size=10`
  - `minimum-idle=5`
  - `connection-timeout=30000`
- **Beneficio**: Protección contra connection exhaustion DoS
- **Status**: ✅ **RESUELTO**

---

## ⏳ DEUDAS PENDIENTES (MVP → Producción)

### [ALTA] 1) HTTPS y Certificado SSL/TLS
- **Criticidad**: 🔴 Alta
- **Descripción**: Todas las conexiones deben ser HTTPS en producción
- **Acción**: Generar certificado (Let's Encrypt) e instalar en servidor
- **Responsable**: DevOps Lead
- **Fecha objetivo**: 2026-07-15
- **Bloqueador para producción**: SÍ

### [ALTA] 2) CORS Configuración Restrictiva  
- **Criticidad**: 🔴 Alta
- **Descripción**: Actualmente CORS permite requests desde cualquier origen
- **Acción**: Actualizar `SecurityConfig.java` para restricción explícita
  ```java
  registry.addMapping("/**")
    .allowedOrigins("https://tudominio.com")
    .allowedMethods("GET", "POST", "PUT", "DELETE");
  ```
- **Responsable**: Katherin Quispe (Security)
- **Fecha objetivo**: 2026-07-01
- **Bloqueador para producción**: SÍ

### [ALTA] 3) Validación de Input — XSS Prevention
- **Criticidad**: 🔴 Alta
- **Descripción**: Revisar todos los formularios POST para inyección XSS
- **Archivos críticos**:
  - `/src/main/java/com/panaderia/ecommerce/autenticacion/infrastructure/web/LoginController.java`
  - `/src/main/java/com/panaderia/ecommerce/catalog/infrastructure/web/ProductoAdminController.java`
- **Acción**: Implementar `@Validated` + Bean Validation
- **Responsable**: Equipo QA + Backend
- **Fecha objetivo**: 2026-07-05
- **Bloqueador para producción**: SÍ

### [MEDIA] 4) Rate Limiting en Endpoints Sensibles
- **Criticidad**: 🟡 Media
- **Descripción**: Proteger contra brute-force y DoS en login, checkout
- **Endpoints críticos**:
  - `POST /login`
  - `POST /api/pedidos`
  - `POST /api/pago`
- **Acción**: Implementar `@RateLimiter` o Bucket4j
- **Responsable**: Kevin Calle (Backend)
- **Fecha objetivo**: 2026-07-10

### [MEDIA] 5) Audit Logging
- **Criticidad**: 🟡 Media
- **Descripción**: Registrar cambios sensibles para compliance
- **Eventos a registrar**:
  - Login exitoso/fallido + IP
  - Cambios de datos de cliente
  - Creación/modificación de pedidos
  - Cambios de productos (admin)
- **Responsable**: Equipo backend
- **Fecha objetivo**: 2026-07-20

### [ALTA] 6) Backup Automático de MySQL
- **Criticidad**: 🔴 Alta
- **Descripción**: Sin backups, pérdida de BD es catastrófica
- **Acción**: Configurar cron job para backup diario
  ```bash
  0 2 * * * mysqldump -u root -p${DB_PASSWORD} PanaderiaBarriosDB > /backup/panaderia-$(date +\%Y\%m\%d).sql
  ```
- **Responsable**: DevOps
- **Fecha objetivo**: 2026-06-30
- **Bloqueador para producción**: SÍ

### [MEDIA] 7) Penetration Testing OWASP Top 10
- **Criticidad**: 🟡 Media
- **Descripción**: Auditoría de seguridad pre-producción
- **Responsable**: QA/Security
- **Fecha objetivo**: 2026-08-01 (antes de Go-Live)

---

## Seguimiento de Cambios

| Deuda | Estado | Fecha | Evidencia |
|---|---|---|---|
| PostgreSQL eliminado | ✅ Resuelto | 2026-06-19 | `.gitignore`, archivos removidos |
| Credenciales en env | ✅ Resuelto | 2026-06-19 | `application-dev-mysql.properties` |
| .gitignore mejorado | ✅ Resuelto | 2026-06-19 | `.gitignore` actualizado |
| Logging deshab. | ✅ Resuelto | 2026-06-19 | `show-sql=false` |
| HTTPS/SSL | ⏳ En progreso | — | — |
| CORS restrictivo | ⏳ En progreso | — | — |
| Input validation | ⏳ En progreso | — | — |
| Rate limiting | ⏳ Pendiente | — | — |
| Audit logging | ⏳ Pendiente | — | — |
| Backups MySQL | ⏳ Pendiente | — | — |

---

## Checklist Pre-Producción

**Antes de desplegar a producción, TODOS estos ítems deben estar ✅**

- [ ] HTTPS habilitado con certificado válido
- [ ] CORS configurado restrictivamente (solo dominio permitido)
- [ ] Validación de input implementada en todos los formularios
- [ ] Rate limiting activo en endpoints sensibles
- [ ] Backups automáticos de BD configurados y testeados
- [ ] Audit logging funcionando
- [ ] Penetration test completado (OWASP Top 10)
- [ ] Contraseña MySQL cambiada (no root vacío)
- [ ] Variables de entorno `.env` configuradas
- [ ] Firewall MySQL restringido a IP de app server

---

*Último actualización: 2026-06-19*  
*Responsable: DevOps & Security Team*

