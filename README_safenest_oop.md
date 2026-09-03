# SafeNest — OOP Edition

A console-based emergency alert system connecting two roles — **Senders**, who raise alerts, and **Volunteers**, who respond to them — built to apply core object-oriented design principles in a real, working program rather than isolated exercises.

## How it works

1. On launch, the user picks a role: **Sender** or **Volunteer**
2. A **Sender** can submit an alert with a description and location, validated before it's accepted
3. A **Volunteer** can view all alerts, filter them by location or status, and update an alert's status (In Process / Successful / Failed) as it's handled

## OOP concepts demonstrated

| Concept | Where |
|---|---|
| **Abstraction** | `UserMenu` is an abstract class defining the shared shape of a menu (`displayActions()`), without knowing what a Sender's or Volunteer's menu actually shows |
| **Interfaces** | `MenuConstraints` enforces that any menu class implements `showMenu()` |
| **Inheritance** | `SenderMenu` and `VolunteerMenu` both extend `UserMenu`, inheriting the shared scanner and alert manager reference |
| **Custom exceptions** | `InvalidAlertException` is a checked exception thrown when a submitted alert fails validation (blank or too-short description/location), keeping validation errors distinct from generic program errors |
| **Method overloading** | `AlertManagement` has two `updateStatus()` methods — a public entry point and a private recursive helper with the same name but a different signature |
| **Recursion** | Alert lookup by ID in `updateStatus()` is implemented recursively instead of an iterative loop |
| **Encapsulation** | `Alert` keeps its fields private, exposing only controlled getters and a single setter for status changes |

## Project structure

```
safenest/
├── Main.java                    # Entry point, role selection
├── exceptions/
│   └── InvalidAlertException.java
├── model/
│   └── Alert.java                # Core alert entity
├── service/
│   └── AlertManagement.java     # Business logic: add, filter, update alerts
└── userinterface/
    ├── MenuConstraints.java      # Interface all menus implement
    ├── UserMenu.java             # Abstract base menu
    ├── SenderMenu.java
    └── VolunteerMenu.java
```

## Tech

Java (no external libraries — core language + `java.util.Scanner`)

## Run it

```
javac safenest/Main.java safenest/**/*.java
java safenest.Main
```
