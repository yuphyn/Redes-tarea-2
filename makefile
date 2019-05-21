all: Server.class Client.class

Server.class: 
	javac ./src/Servidor.java

Client.class: 
	javac ./src/Cliente.java

Client:
	java .out/production/Redes-tarea-2/Cliente

Server:
	java .out/production/Redes-tarea-2/Servidor