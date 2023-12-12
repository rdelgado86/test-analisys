package br.com.sodexo.new4ccore.account.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PageDTO implements Serializable {

    private static final long serialVersionUID = 3611904900906594211L;

    protected Integer previousPage;
    protected Integer currentPage;
    protected Integer nextPage;
    protected boolean isLastPage;
    protected Integer totalPages;
    protected Long totalItems;
    protected Integer maxItemsPerPage;
    protected Integer totalItemsPage;

}
