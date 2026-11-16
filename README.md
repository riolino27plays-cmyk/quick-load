# Quick Load (Fabric, Minecraft 1.21.11)

Mod de cliente. No necesita Fabric API.

## Compilar con GitHub

1. Crea un repositorio nuevo (mejor en **Private**) y sube el contenido de esta carpeta.
2. Si no aparece la carpeta `.github`, crea a mano el archivo `.github/workflows/build.yml`
   (Add file > Create new file) con el contenido del `build.yml` que viene en el zip.
3. Pestaña **Actions**: espera al círculo verde, abre la ejecución y baja `quick-load`
   de la sección *Artifacts*. Dentro está `quick-load-1.1.0.jar`.

## Compilar en tu PC

Con Java 21: `./gradlew build` (o `gradlew.bat build` en Windows). El jar queda en `build/libs/`.

## Instalar

Copia `quick-load-1.1.0.jar` (no el `-sources`) a la carpeta `mods`.
