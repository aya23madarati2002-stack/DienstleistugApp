# Dienstleistungs App

## Entwickler
Aya Madarati

## Beschreibung
Die "Dienstleistungs App" ist eine Android-Anwendung zur Verwaltung von Terminen und Nutzerprofilen.  
Benutzer können sich registrieren, einloggen, ihre Profile verwalten und Termine buchen, umbuchen oder stornieren.  
Die App nutzt Firebase Authentication für Login, Registrierung und Passwort-Reset.

---

## Funktionen

### 1. Login & Registrierung
- Benutzer können sich registrieren (Email + Passwort) oder einloggen.
- Passwort-Reset über E-Mail möglich.
- Firebase Authentication sorgt für sichere Anmeldung.

### 2. Profilverwaltung
- Benutzer können ihren **Namen** ändern.
- Passwort zurücksetzen über App-Link.
- Profiländerungen werden in der App gespeichert.

### 3. Terminverwaltung
- Termine buchen: Datum auswählen und bestätigen.
- Termine umbuchen: bestehende Termine ändern.
- Termine stornieren: bestehende Termine löschen.

### 4. Navigation / Screens
- Login / Register Screen
- Home Screen
- Browse Screen (Service auswählen)
- Profile Screen (Profil bearbeiten, Termine buchen)
- Appointments Screen (gebuchte Termine verwalten)
- Settings Screen (Logout, Profiländerung)

---

## Ablauf der App

### Start & Login
1. Benutzer öffnet die App.
2. System prüft, ob Benutzer bereits eingeloggt ist.
   - Wenn **ja** → Home Screen
   - Wenn **nein** → Login / Register Screen
3. Benutzer kann:
   - Einloggen (E-Mail + Passwort)  
   - Registrieren (Neues Konto erstellen)

### Home Screen
- Zugriff auf: Browse, Profile, Appointments, Settings

### Browse
- Nutzer sucht Service
- Wählt Profil eines Anbieters aus
- Möglichkeit, Termin zu buchen

### Profile Screen
- Übersicht Profilinformationen
- Buchung von Terminen
- Anzeige verfügbarer Termine

### Appointments Screen
- Anzeige gebuchter Termine
- Möglichkeit: Umbuchen oder Stornieren

### Settings
- Logout
- Daten ändern
- Passwort zurücksetzen (E-Mail-Link)
- Änderungen speichern (System meldet „Changes saved“)

### Außerhalb der App
- Passwort-Reset erfolgt über E-Mail-Link
- Benutzer kann neues Passwort eingeben
- System meldet erfolgreiche Änderung

---

## Technologien
- Android Studio
- Java
- Firebase Authentication

---

## Hinweise
- APK-Datei befindet sich im Repository
- Ablaufdiagramm ist als Referenz für die Funktionsweise enthalten
- Die App ist für 1-2 Benutzer getestet

---
