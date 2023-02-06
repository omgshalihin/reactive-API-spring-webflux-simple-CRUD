package com.reactive.webfluxmongoapi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reactive.webfluxmongoapi.dto.ProductDto;
import com.reactive.webfluxmongoapi.entity.Product;
import com.reactive.webfluxmongoapi.repository.ProductRepo;
import com.reactive.webfluxmongoapi.utils.AppUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Range;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductService {

    @Autowired
    private ProductRepo repo;

    public Flux<ProductDto> getAllProducts() {
//        repo.findAll().subscribe(el-> System.out.println(el));
        return repo.findAll()
                .map(el -> AppUtils.entityToDTO(el));
    }

    public Mono<ProductDto> getProduct(String id) {
        return repo.findById(id)
                .map(el -> AppUtils.entityToDTO(el));

    }

    public Flux<ProductDto> getProductInRange(double min, double max) {
        return repo.findByPriceBetween(Range.closed(min, max));
    }

    public Mono<ProductDto> saveProduct(Mono<ProductDto> productDtoMono) {
        return productDtoMono.map(AppUtils::dtoToEntity)
                .flatMap(el -> repo.save(el))
                .map(AppUtils::entityToDTO);
    }

    public Mono<ProductDto> updateProduct(Mono<ProductDto> productDtoMono, String id) {
        return repo.findById(id)
                .flatMap(el -> productDtoMono
                        .map(AppUtils::dtoToEntity)
                        .doOnNext(e -> e.setId(id))
                )
                .flatMap(repo::save)
                .map(AppUtils::entityToDTO);
    }

    public Mono<Void> deleteProduct(String id) {
        return repo.deleteById(id);
    }
}
