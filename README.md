#  Cypher App

Cypher es una aplicación móvil de e-commerce desarrollada en Kotlin que implementa un sistema básico de autenticación, gestión de productos y carrito de compras, utilizando como base de datos Room. El proyecto aplica principios de arquitectura limpia dividiendo la capa de datos, lógica de negocios (ViewModels) y capas de interfaz (UI).

---
## Tecnologías Utilizadas

**Kotlin:** Lenguaje principal del desarrollo.
**Room:** Manejo de base de datos local.
**XML Layouts:** Interfaz visual.
**MVVM:** Para la arquitectura de componentes.

---
## Base de Datos Local (Room)

La base de datos se implementa en AppDatabase.kt utilizando Room e incluye datos precargados para permitir la prueba de la aplicación sin un servidor remoto:
- Usuarios predefinidos.
- Categorías iniciales.
- Productos base del catálogo.

---
## Estructura del Proyecto
```bash
.
└── app/
    ├── manifests/
    │   └── AndroidManifest.xml
    ├── kotlin+java/
    │   └── com.example.appmobile/
    │       ├── activities          # Pantallas principales (Splash, Login, Inicio de sesión, etc...).
    │       ├── data                # Configuración y administración de bases de datos local.
    │       ├── session             # Un helper que permite manejar persistencia básica de la sesión de un usuario.
    │       ├── tests               # Pruebas unitarias y de instrumentación.
    │       └── ui/
    │           ├── adapters        # Adaptadores para listas (RecyclerView).
    │           └── viewmodels      # Lógica de negocio para cada entidad.
    └── assents/                    # Archivos estáticos.
    └── res/                        # Layouts XML, imágenes, colores y estilos.
        ├── color
        ├── drawable
        └── layout  
```

---
## Instalación y Ejecución

1. Clonar el repositorio.
   Clona el repositorio en una ubicación adecuada y ejecuta el siguiente comando:
```bash
  git clone https://github.com/1kArms1kEyes/DesMobNativo.git
```
2. Abrir el proyecto en Android Studio.
3. Sincronizar Gradle.
4. Ejecutar en un emulador o dispotivo físico.


    