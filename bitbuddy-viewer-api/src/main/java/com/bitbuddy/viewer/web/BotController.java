package com.bitbuddy.viewer.web;

import com.bitbuddy.viewer.dto.BotStatusDto;
import com.bitbuddy.viewer.service.BotStateService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/bot")
public class BotController {

    private final BotStateService state;

    public BotController(BotStateService state) {
        this.state = state;
    }

    @GetMapping("/status")
    public BotStatusDto status() {
        return state.status();
    }

    @PostMapping("/start")
    public Map<String, Object> start(@RequestBody Map<String, Object> payload) {
        String symbol = String.valueOf(payload.getOrDefault("symbol", "BTC/USD"));
        state.start(symbol);
        return Map.of("ok", true, "symbol", symbol);
    }

    @PostMapping("/stop")
    public Map<String, Object> stop() {
        state.stop();
        return Map.of("ok", true);
    }
}
