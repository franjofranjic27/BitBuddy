// BotStateService.java
package com.bitbuddy.viewer.service;

import com.bitbuddy.viewer.dto.BotStatusDto;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class BotStateService {
    private final AtomicBoolean running = new AtomicBoolean(false);
    private volatile String symbol = "BTC/USD";
    private volatile String lastSignal = "hold";
    private volatile long updatedAt = System.currentTimeMillis();

    public BotStatusDto status() {
        return new BotStatusDto(running.get(), symbol, lastSignal, updatedAt);
    }

    public void start(String symbol) {
        this.symbol = symbol;
        this.running.set(true);
        this.updatedAt = System.currentTimeMillis();
    }

    public void stop() {
        this.running.set(false);
        this.updatedAt = System.currentTimeMillis();
    }

    public void updateSignal(String signal) {
        this.lastSignal = signal;
        this.updatedAt = System.currentTimeMillis();
    }
}
