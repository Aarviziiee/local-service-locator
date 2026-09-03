# LocalServ — Local Services Search System

A console-based Java application for finding local service providers (electricians, plumbers, tutors, etc.) — built as a Data Structures & Algorithms project where every core structure and sorting algorithm is implemented from scratch, without relying on Java's built-in `java.util` collections or `Collections.sort()`.

## Why build it this way

The goal wasn't to make a working search tool the easy way — it was to demonstrate an understanding of *how* these structures work internally: how a hash table resolves collisions, how a BST maintains order on insert, how a Quick Sort partitions a list. Using `HashMap` or `Arrays.sort()` would have skipped the part that was actually being tested.

## Data structures implemented

| Structure | Used for |
|---|---|
| **Hash Table** (custom) | Fast lookup of service providers by category or location |
| **Binary Search Tree** | Keeping providers ordered (e.g. by rating or name) for in-order traversal and range queries |
| **Stack** | [describe what it's used for — e.g. undo/history of searches] |
| **Queue** | [describe what it's used for — e.g. processing service requests in order] |
| **Quick Sort** (custom, in-place) | Sorting results by rating/relevance without relying on a built-in sort |

## Tech

Java (core language only — no external libraries or `java.util` collections)

## Run it

```
javac Main.java
java Main
```
