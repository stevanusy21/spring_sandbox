package com.springboot.sandbox.common.dto;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.Data;

@Data
public class PageResponse<T> {
    private int page;
    private int pageSize;
    private long totalData;
    private int totalPages;
    private List<T> data;

    public static <T> PageResponse<T> from(Page<T> page){
        PageResponse<T> pageResponse = new PageResponse<>();
        pageResponse.setPage(page.getNumber());
        pageResponse.setPageSize(page.getSize());
        pageResponse.setTotalData(page.getTotalElements());
        pageResponse.setTotalPages(page.getTotalPages());
        pageResponse.setData(page.getContent());
        return pageResponse;
    }
}
