package br.com.sodexo.new4ccore.account.service.strategy;

import br.com.sodexo.new4ccore.account.enums.CardProcessorStrategyName;
import br.com.sodexo.new4ccore.account.exception.CardProcessorStrategyNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class CardProcessorStrategyFactory {
    private Map<CardProcessorStrategyName, CardProcessorStrategy> strategies;

    @Autowired
    public CardProcessorStrategyFactory(Set<CardProcessorStrategy> strategySet) {
        createStrategy(strategySet);
    }

    public CardProcessorStrategy findStrategy(String cardProcessor) {
        var strategyName = CardProcessorStrategyName.fromLabel(cardProcessor);
        if (! strategies.containsKey(strategyName)) {
            throw new CardProcessorStrategyNotFoundException("Could not find any strategy for the Card Processor");
        }
        
        return strategies.get(strategyName);
    }

    private void createStrategy(Set<CardProcessorStrategy> strategySet) {
        strategies = strategySet.stream().collect(Collectors.toMap(CardProcessorStrategy::getStrategyName, Function.identity()));
    }
}
