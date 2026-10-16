# Shield Animations (Fabric, Minecraft 26.3)

Voraussetzungen: JDK 25, Internet.

## Gradle-Wrapper hinzufügen (einmalig)
Kopiere aus dem offiziellen Fabric-Example-Mod (https://github.com/FabricMC/fabric-example-mod,
Branch passend zu 26.x) diese Dateien in dieses Projekt:
    gradlew, gradlew.bat, gradle/wrapper/ (gradle-wrapper.jar + gradle-wrapper.properties)
Oder, wenn Gradle installiert ist:  gradle wrapper --gradle-version latest

## Bauen
    ./gradlew build          (Windows: gradlew.bat build)
Ergebnis: build/libs/shieldanim-1.0.0.jar   (NICHT die -sources.jar hochladen)

## Testen
    ./gradlew runClient

## Modrinth
Loader: Fabric | Spielversion: 26.3 | Umgebung: nur Client | Abhängigkeit: Fabric API (required)

## Falls der Build meckert
- Import `...keymapping.v1.KeyMappingHelper` nicht gefunden -> per IDE-Autoimport korrigieren.
- Mixin-Fehler "submitArmWithItem" -> Signatur in ItemInHandRenderer prüfen (Parameter können sich zwischen 26.x ändern).
- Aktuelle Loom-/Loader-Versionen: https://fabricmc.net/develop
