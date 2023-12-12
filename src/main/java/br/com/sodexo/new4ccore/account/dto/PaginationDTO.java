package br.com.sodexo.new4ccore.account.dto;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PaginationDTO<T> implements Serializable {

    private static final long serialVersionUID = 5011204800306544211L;

    protected List<T> accounts;
    protected PageDTO page;

}
