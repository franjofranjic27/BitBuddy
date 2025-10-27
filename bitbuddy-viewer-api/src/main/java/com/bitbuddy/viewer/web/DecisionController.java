package com.bitbuddy.viewer.web;

import com.bitbuddy.viewer.dto.DecisionDto;
import com.bitbuddy.viewer.repo.DecisionRepository;
import com.bitbuddy.viewer.service.BotStateService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class DecisionController {
    private final DecisionRepository decisionRepository;
    private final BotStateService botStateService;

    public DecisionController(DecisionRepository decisionRepository, BotStateService botStateService) {
        this.decisionRepository = decisionRepository;
        this.botStateService = botStateService;
    }

    @GetMapping("/decisions")
    public List<DecisionDto> decisions(@RequestParam String symbol,
                                       @RequestParam(defaultValue = "100") int limit) {
        var list = decisionRepository.findRecent(symbol, Math.min(limit, 1000))
                .stream()
                .map(p -> new DecisionDto(p.getTs(), p.getSignal(), p.getPrice(), p.getSymbol()))
                .toList();
        if (!list.isEmpty()) botStateService.updateSignal(list.getFirst().signal());
        return list;
    }
}
