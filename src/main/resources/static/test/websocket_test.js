const socket = new SockJS('http://localhost:8080/ws-chat');
const stompClient = Stomp.over(socket);

stompClient.connect({}, function() {
    console.log('Conectado');

    // Suscribirse al topic de respuesta
    stompClient.subscribe('/topic/test', function(message) {
        console.log('Respuesta:', message.body);
    });

    // Enviar mensaje
    stompClient.send('/app/test', {}, 'Hola servidor');
});