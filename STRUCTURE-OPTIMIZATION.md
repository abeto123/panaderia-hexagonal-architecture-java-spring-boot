# 📦 Guía de Estructura Optimizada del Proyecto

**Objetivo**: Mantener el repositorio limpio, rápido y sin "sobrecargo" de archivos innecesarios.

---

## 🚀 Carpetas que NUNCA Deben Versionarse

| Carpeta | Tamaño Típico | Razón | Acción |
|---|---|---|---|
| **`target/`** | 100+ MB | Compilados generados por Maven | ✅ En `.gitignore` |
| **`.mvn/wrapper/maven-wrapper.jar`** | 5-10 MB | Caché de Maven | ✅ En `.gitignore` |
| **`build/`** | 50+ MB | Compilados generados por Gradle (si aplica) | ✅ En `.gitignore` |
| **`.idea/`** | 50+ MB | Caché de IntelliJ IDEA | ✅ En `.gitignore` |
| **`node_modules/`** | 1000+ MB | Dependencias NPM (futuro) | ✅ En `.gitignore` |
| **`.vscode/settings.json`** | 5-50 KB | Configuración local de VS Code | ✅ En `.gitignore` |

---

## 🛡️ Archivos Sensibles que NUNCA Deben Versionarse

| Archivo | Contenido | Acción |
|---|---|---|
| **`.env`** | Variables de entorno con secretos | ✅ En `.gitignore` |
| **`*.pem`, `*.key`** | Claves privadas SSH/SSL | ✅ En `.gitignore` |
| **`credentials.json`** | Credenciales API/Cloud | ✅ En `.gitignore` |
| **`*.log`** | Logs de ejecución | ✅ En `.gitignore` |
| **`cookies.txt`** | Sesiones HTTP | ✅ Eliminado |
| **`application-prod*.properties`** | Config de producción con secretos | ✅ En `.gitignore` |

---

## 📁 Estructura Recomendada del Proyecto

```
panaderia-hexagonal-architecture-java-spring-boot/
├── .gitignore                          # ✅ Controlado
├── .github/
│   └── workflows/                      # ✅ Controlado (CI/CD)
├── .mvn/wrapper/                       # ✅ Controlado (Maven Wrapper ejecutable)
│   ├── maven-wrapper.jar              # ⚠️ Gitignored (no necesario)
│   └── maven-wrapper.properties       # ✅ Controlado (pequeño)
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/panaderia/ecommerce/
│   │   └── resources/
│   │       ├── application.properties
│   │       └── application-dev-mysql.properties
│   └── test/
│       ├── java/
│       └── resources/
├── target/                             # ❌ Ignorado (se genera con mvn clean compile)
├── build/                              # ❌ Ignorado (si existe, Gradle)
├── pom.xml                             # ✅ Controlado
├── mvnw                                # ✅ Controlado (ejecutable)
├── mvnw.cmd                            # ✅ Controlado (ejecutable Windows)
├── README.md                           # ✅ Controlado
├── SECURITY.md                         # ✅ Controlado
├── SCM-DEUDA.md                        # ✅ Controlado
└── .env.example                        # ✅ Controlado (template, no secretos)
```

---

## 🧹 Limpieza Local (Para Desarrolladores)

**Después de clonar el repositorio, ejecuta**:

```bash
# Limpiar compilados anteriores
mvn clean

# Limpiar caché de IDEs
rm -rf .idea
rm -rf .vscode/settings.json

# Limpiar archivos de sistema
rm -rf .DS_Store
rm -rf Thumbs.db
```

---

## 🔄 Antes de Hacer Commit

**Checklist** (Ejecuta antes de `git push`):

```bash
# 1. Verifica qué vas a subir
git status

# 2. Revisa que NO hay carpetas grandes
git add . --dry-run | grep -E "target/|node_modules/|\.idea/"

# 3. Si accidentalmente agregaste una carpeta grande, quítala
git reset HEAD target/
git reset HEAD .idea/

# 4. Commit solo código, documentación y config
git add src/ pom.xml .gitignore README.md
git commit -m "Cambios productivos"
git push
```

---

## 📊 Tamaño Actual del Repositorio

**Óptimo** (Git):
```
Total: ~5-10 MB (código fuente + docs)
```

**Si incluye sobrecargo**:
```
Total: ~300+ MB (si target/ o node_modules/ estuvieran incluidos)
```

---

## ✅ Cambios Aplicados Hoy (2026-06-19)

- ✅ **Mejorado `.gitignore`** — Agregadas exclusiones para `.mvn/`, `build/`, más exten siones
- ✅ **Eliminados archivos de debug** — `cookies.txt`, `curl_login_debug.txt`, `admin_pedidos.html`
- ✅ **PostgreSQL removido** — No más `application-dev-postgresql.properties`
- ✅ **Credenciales en variables de entorno** — Creado `.env.example`

---

## 🎯 Próximos Pasos para el Equipo

1. **Cada desarrollador ejecuta**:
   ```bash
   git pull origin main
   mvn clean compile
   ```

2. **Verifica tamaño actual**:
   ```bash
   du -sh .  # En Linux/Mac
   # o
   Get-ChildItem -Recurse -ErrorAction SilentlyContinue | Measure-Object -Property Length -Sum  # Windows PowerShell
   ```

3. **Si `target/` o `.idea/` están en Git, limpia con**:
   ```bash
   git rm -r --cached target/
   git rm -r --cached .idea/
   git commit -m "Remove build artifacts from Git history"
   git push
   ```

---

## 📞 Referencia Rápida

| Problema | Solución |
|---|---|
| "Mi clone es muy grande (~300 MB)" | `target/` está en Git. Ejecuta: `git rm -r --cached target/` |
| "Mi VS Code es lento" | `.idea/` o `.vscode/settings.json` en Git. Ejecuta: `git rm -r --cached .idea/` |
| "Subo accidentalmente un `.env`" | No hay problema si está en `.gitignore`. Simplemente elimina: `git rm --cached .env` |
| "Mi build tarda mucho" | Ejecuta: `mvn clean compile -q` (quiet mode) |

---

**Estado Final**: 🟢 **Repositorio Optimizado**

- Tamaño: ~5-10 MB (óptimo)
- Limpieza: 100% (sin artifacts compilados ni config local)
- Seguridad: Credenciales en variables de entorno

---

*Documento actualizado: 2026-06-19*  
*Responsable: DevOps Team*
