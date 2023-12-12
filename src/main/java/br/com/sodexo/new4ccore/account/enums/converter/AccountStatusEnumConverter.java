package br.com.sodexo.new4ccore.account.enums.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import br.com.sodexo.new4ccore.account.enums.AccountStatusEnum;

@Converter(autoApply = true)
public class AccountStatusEnumConverter implements AttributeConverter<AccountStatusEnum, String> {
 
    @Override
    public String convertToDatabaseColumn(AccountStatusEnum accountStatus) {
        return accountStatus.getValue();
    }
 
    @Override
    public AccountStatusEnum convertToEntityAttribute(String value) {
        return AccountStatusEnum.fromValue(value);
    }
 
}