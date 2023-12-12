package br.com.sodexo.new4ccore.account.util;

import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class UuidUtils {
    public  String generate(){
        return UUID.randomUUID().toString();
    }
}
