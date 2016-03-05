var io = require('socket.io').listen(3000);

var socketCount = 0;


io.sockets.on('connection', function(socket){

    // Socket has connected, increase socket count
    socketCount++;
	console.log(socketCount);
    // Let all sockets know how many are connected
    io.sockets.emit('users_connected', socketCount);
 
    socket.on('disconnect', function() {
        // Decrease the socket count on a disconnect, emit
        socketCount--;
		console.log(socketCount);
        io.sockets.emit('users_connected', socketCount);
    });
	
	socket.on('sendNotificacion', function(data){
		console.log(data);
		io.sockets.emit('Notificacion', data);
	});
});