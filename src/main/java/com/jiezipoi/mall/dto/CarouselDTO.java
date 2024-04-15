package com.jiezipoi.mall.dto;

import org.springframework.web.multipart.MultipartFile;

public class CarouselDTO {
    private MultipartFile image;
    private String redirectUrl;
    private Integer carouselRank;
    private Integer userId;
}
