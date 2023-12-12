package br.com.sodexo.new4ccore.account.enums;

import lombok.Getter;

import java.util.Arrays;

public enum CardProcessorStrategyName {
    DOCK("Dock"),
    DXC("DXC");

    @Getter
    private final String label;

    CardProcessorStrategyName(String label) {
        this.label = label;
    }

    public static CardProcessorStrategyName fromLabel(String cardProcessor) {
        return Arrays.stream(values()).filter(item -> item.label.equals(cardProcessor)).findFirst().orElse(null);
    }
}
