#!/bin/bash

# Funkcja uruchamiająca program w nowym terminalu na Windows
run() {
    local port=$1
    local number=$2

    #start cmd /k "java DAS $port $number"
 start powershell -NoExit -Command "java DAS $port $number"

}

# Główna część skryptu
main() {
    run  7777 129
    run  7777 652
    run  7777 12
    run  7777 98
    run  7777 0
    run  7777 33
    run  7777 0
    run  7777 -1
}

# Uruchomienie głównej funkcji
main