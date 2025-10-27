// LiveMarketDataBridge.java
package com.bitbuddy.viewer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.net.URI;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Brückt Kafka market-data-topic -> WebSockets.
 * Erwartet JSON der Form: { "ts":..., "price":..., "base":"BTC", "counter":"USD", ... }
 */
@Component
@RequiredArgsConstructor
public class LiveMarketDataBridge {

    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();

    public void register(WebSocketSession session) {
        sessions.add(session);
    }
    public void unregister(WebSocketSession session) {
        sessions.remove(session);
    }

    @Value("${viewer.kafka.marketDataTopic}")
    private String topic;

    @KafkaListener(topics = "${viewer.kafka.marketDataTopic}", groupId = "viewer-ws")
    public void onMarketTick(String payload) {
        // Optional: symbol-Filter anhand der Session-Query ?symbol=...
        sessions.forEach(session -> {
            try {
                if (session.isOpen()) {
                    if (shouldSendToSession(session, payload)) {
                        session.sendMessage(new TextMessage(payload));
                    }
                }
            } catch (IOException ignored) {}
        });
    }

    private boolean shouldSendToSession(WebSocketSession session, String payload) {
        try {
            // sehr leichte Filterung: prüfe ob base/counter im payload enthalten sind
            URI uri = session.getUri();
            if (uri == null) return true;
            String query = uri.getQuery(); // e.g. symbol=BTC/USD
            if (query == null) return true;
            String[] parts = query.split("=");
            if (parts.length != 2) return true;
            String sym = parts[1];
            String base = sym.split("/")[0];
            String counter = sym.split("/")[1];
            return payload.contains("\"base\":\"" + base + "\"") && payload.contains("\"counter\":\"" + counter + "\"");
        } catch (Exception e) {
            return true;
        }
    }
}
