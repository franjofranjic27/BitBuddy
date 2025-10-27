package com.bitbuddy.viewer.web;

import com.bitbuddy.viewer.service.LiveMarketDataBridge;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class MarketDataWebSocketHandler extends TextWebSocketHandler {

    private final LiveMarketDataBridge bridge;

    public MarketDataWebSocketHandler(LiveMarketDataBridge bridge) {
        this.bridge = bridge;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        bridge.register(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        bridge.unregister(session);
    }
}
