# Hospital

Aplicacion web Spring Boot MVC para gestion de consultas medicas con roles `ADMINISTRADOR`, `MEDICO` y `PACIENTE`.

## Arquitectura

- `model`: entidades JPA que generan el esquema automaticamente con Hibernate.
- `repository`: interfaces `JpaRepository` y consultas derivadas.
- `service`: reglas de negocio, validaciones y restricciones por usuario.
- `controller`: controladores MVC con vistas Thymeleaf.
- `templates`: pantallas de login, panel admin, panel medico y panel paciente.
- `static/css`: estilos responsive basicos.
- `config`: seguridad, Swagger/OpenAPI y datos iniciales por JPA.


```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/hospital"
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="tu_password"
.\mvnw.cmd spring-boot:run
```

URL principal:

- App: `http://localhost:8080`
- Swagger: `http://localhost:8080/swagger-ui.html`

Usuarios iniciales creados por JPA al arrancar:

- `admin` / `admin123`
- `medico` / `medico123`
- `paciente` / `paciente123`

