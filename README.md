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

## Entidades JPA

Hibernate genera las tablas desde las entidades porque `spring.jpa.hibernate.ddl-auto=update` esta activo. No se usan scripts SQL manuales.

- `Role`: catalogo de roles con nombre unico (`ADMINISTRADOR`, `MEDICO`, `PACIENTE`).
- `User`: credenciales, datos personales y relacion `ManyToOne` con `Role`.
- `Medico`: perfil medico `OneToOne` con `User`, `ManyToOne` con `Consultorio`, `OneToMany` con `Cita`.
- `Paciente`: perfil paciente `OneToOne` con `User`, `OneToMany` con `Cita`.
- `Consultorio`: espacio fisico con codigo unico, medicos y citas asociadas.
- `Cita`: relaciona medico, paciente y consultorio con fecha, horario, estado y observaciones.

La disponibilidad de citas se valida en `CitaService`, no con indices unicos rigidos, para que una cita cancelada no bloquee permanentemente el mismo horario.

## Configuracion

La aplicacion lee PostgreSQL desde variables de entorno y tiene valores por defecto:

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

## Pruebas

Las pruebas usan perfil `test` con H2 en memoria, sin tocar PostgreSQL:

```powershell
.\mvnw.cmd test
```

## Buenas practicas para GitHub

- No versionar credenciales reales ni archivos generados en `target/`.
- Usar variables de entorno para la conexion a PostgreSQL.
- Mantener entidades, repositorios, servicios y controladores en paquetes separados.
- Subir cambios en commits pequenos por fase: entidades, repositorios, servicios, seguridad, vistas y pruebas.
