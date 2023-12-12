package br.com.sodexo.new4ccore.account.enums;

public enum AccountStatusEnum {

    ACTIVE("A"),
    BLOCKED("B"),
    CANCELLED("C");
	
    private final String value;

    AccountStatusEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
    
    public static AccountStatusEnum fromValue(String value) {
        switch (value) {
	        case "A":
	            return AccountStatusEnum.ACTIVE;
	 
	        case "B":
	            return AccountStatusEnum.BLOCKED;
	 
	        case "C":
	            return AccountStatusEnum.CANCELLED;
	 
	        default:
	            throw new IllegalArgumentException("Valeu [" + value + "] not supported.");
        }
    }
    
}
