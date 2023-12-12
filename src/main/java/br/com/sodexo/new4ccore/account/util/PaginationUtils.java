package br.com.sodexo.new4ccore.account.util;

import org.springframework.data.domain.Page;

import br.com.sodexo.new4ccore.account.dto.PageDTO;
import br.com.sodexo.new4ccore.account.dto.PaginationDTO;

public class PaginationUtils {

    public static <T> PaginationDTO<T> buildResponse(Page<T> page) {
        final int pageNumberReadable = page.getNumber() + 1;
        final int totalPageReadable = Math.max(page.getTotalPages(),1);
        
        PageDTO pageDTO = new PageDTO();
        pageDTO.setPreviousPage(Math.max(pageNumberReadable - 1, 1));
        pageDTO.setCurrentPage(pageNumberReadable);
        pageDTO.setNextPage(Math.min(pageNumberReadable + 1,totalPageReadable));
        pageDTO.setTotalPages(totalPageReadable);
        pageDTO.setTotalItems(page.getTotalElements());
        pageDTO.setMaxItemsPerPage(page.getPageable().getPageSize());
        pageDTO.setTotalItemsPage(page.getNumberOfElements());
        pageDTO.setLastPage(totalPageReadable == pageNumberReadable);
        
        PaginationDTO<T> paginationDTO = new PaginationDTO<>();
        paginationDTO.setAccounts(page.getContent());
        paginationDTO.setPage(pageDTO);
        
        return paginationDTO;
    }
    
}
