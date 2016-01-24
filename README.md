# Document Management System Mock-up

This app was build as an exercise. It is a very crude mock-up of a document management system (DMS).

Main class is used to *jump-start* application. There are four roles (*Author*, *Desktop Publisher*, *Editor*, and *Admin*), and each roles has its set of permissions defined in *roles* table in the database. Application also has a simple *publishing* workflow, which is realized using SQLite database and adequate Java programming. In essence, workflow is a [finite state machine](https://en.wikipedia.org/wiki/Finite-state_machine) which can be represented as:

![FSM](/images/fsm.png)

Conceptual model diagram of a database is represented in the figure below.

![concept](/images/db.png)

Screenshots are located in */images* folder.

Things to improve:

* Reduce tight coupling among classes (possibly by using interfaces)
* Employ adequate architecture (MVC)
* Use JTable models
* Split some methods into smaller ones