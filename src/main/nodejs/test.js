var http    =   require('http');
var fs      =   require('fs');
var url = require("url");


var connect = require('connect');
var app = connect.createServer(
    connect.static(__dirname)
).listen(8080);

// Socket io ecoute maintenant notre application !
var io = require('socket.io');

// Socket io ecoute maintenant notre application !
io = io.listen(app); 

// Quand une personne se connecte au serveur
io.sockets.on('connection', function (socket) {
 	console.log('connection........');
    // On donne la liste des messages (evenement cree du cote client)
    //socket.emit('recupererMessages', messages);
    // Quand on recoit un nouveau message
		socket.on('client2Server', function (data) {
        // On l'ajout au tableau (variable globale commune a tous les clients connectes au serveur)
//        messages.push(mess);
        // On envoie a tout les clients connectes (sauf celui qui a appelle l'evenement) le nouveau message
			socket.broadcast.emit('server2Client', data);
			console.log('data ' + data);
    });
});

// Notre application ecoute sur le port 8080
app.listen(8080);