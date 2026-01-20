import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

const SOCKET_URL = process.env.REACT_APP_SOCKET_URL || 'http://localhost:8080/ws';

export const stompClient = new Client({
    brokerURL: 'ws://localhost:8080/ws', // For native WebSocket
    // For SockJS fallback if needed (usually tricky with Client in v6+, but v7 supports webSocketFactory)
    webSocketFactory: () => new SockJS(SOCKET_URL),

    debug: function (str) {
        console.log(str);
    },
    reconnectDelay: 5000,
    heartbeatIncoming: 4000,
    heartbeatOutgoing: 4000,
});

