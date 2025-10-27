// SymbolService.java
package com.bitbuddy.viewer.service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SymbolService {
    // TODO: Optional dynamisch aus Konfiguration / DB (market-data-service) lesen
    public List<String> listSymbols() {
        return List.of("BTC/USD","ETH/USD"); // Placeholder
    }
}
