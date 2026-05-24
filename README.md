# Drop Number Game with Multi-Linked List

This project is a Java implementation of a Drop Number game using a multi-linked list data structure.

The game board is represented with nodes instead of a traditional array-based structure. Each occupied cell is stored as a `Node`, and the nodes are connected through multiple pointers such as `next`, `up`, `down`, `left`, and `right`.

## Features

- 7x5 game board
- Multi-linked list based board structure
- Node-based representation of occupied cells
- Number dropping logic
- Merge operation for matching values
- Game-over control
- Console output
- Java Swing GUI visualization

## Technologies Used

- Java
- Java Swing
- NetBeans IDE
- Object-Oriented Programming
- Linked List
- Multi-Linked List

## Project Structure

```text
DataProject1
├── src
│   └── dataproject1
│       ├── Game.java
│       ├── GameBoard.java
│       ├── GameFrame.form
│       ├── GameFrame.java
│       ├── Main.java
│       ├── Move.java
│       └── Node.java
├── nbproject
├── build.xml
└── manifest.mf
