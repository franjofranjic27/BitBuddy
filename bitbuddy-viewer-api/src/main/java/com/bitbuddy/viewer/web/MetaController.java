package com.bitbuddy.viewer.web;

import com.bitbuddy.viewer.service.SymbolService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MetaController {
    private final SymbolService symbolService;

    public MetaController(SymbolService symbolService) {
        this.symbolService = symbolService;
    }

    @GetMapping("/symbols")
    public List<String> symbols() {
        return symbolService.listSymbols();
    }
}
