# VSP

[![Build Status Master](https://travis-ci.org/Octav14n/VSP.svg?branch=master)](https://travis-ci.org/Octav14n/VSP)

## Wie führe ich das Programm in Docker aus
Den Container erstellen per:
``docker build -t vsp1 .``

Die alte Docker-Maschine anhalten (falls vorhanden):
``docker stop vsp``

Die alte Docker-Maschine löschen (falls vorhanden):
``docker rm vsp``

Den neuen Container starten:
``docker run -p 8080 --name vsp -t vsp1``

Die IP-Addresse herausfinden:
``docker inspect vsp | grep IPAddress``


Aus dem letzten Step die IP-Addresse aufrufen per "http://<ip>:4567/"

# Zeitmanagement
Praktikum 1: 4h
Praktikum 2: 2½h (27. Okt) 1h (04. Nov) 1½h (11.11.2015) 2h (16.11.2015)
