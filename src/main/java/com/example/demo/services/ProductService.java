package com.example.demo.services;

import com.example.demo.DTOS.ProductDTO;
import com.example.demo.model.Entities.ProductEntity;
import com.example.demo.model.Entities.WarehouseEntity;
import com.example.demo.model.Repositories.ProductRepository;
import com.example.demo.model.Repositories.WarehouseRepository;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    WarehouseRepository warehouseRepository;
    @Autowired
     ModelMapper modelMapper;

    public List<ProductDTO> getAllProducts(){
        List<ProductEntity> productEntities = productRepository.findAll();
        return productEntities.stream()
                .map(ProductEntity -> modelMapper.map(productEntities, ProductDTO.class))
                .collect(Collectors.toList());
    }
    public Optional<ProductDTO> getProductById(ObjectId id){
        Optional<ProductEntity> optionalProductEntity = productRepository.findById(id);
        return optionalProductEntity.map(ProductEntity -> modelMapper.map(optionalProductEntity, ProductDTO.class));
    }
    public Optional<ProductDTO> deleteProductById(ObjectId id){
        Optional<ProductEntity> optionalProductEntity = productRepository.findById(id);

        optionalProductEntity.ifPresent(productEntity -> {
            productRepository.deleteById(id);
            System.out.println("Product with ID: " + id + " was deleted!");
        });

        return optionalProductEntity.map(ProductEntity-> modelMapper.map(optionalProductEntity, ProductDTO.class));
    }
    public ProductDTO createProduct(ProductDTO productDTO, int quantity){
        ProductEntity productEntity = modelMapper.map(productDTO, ProductEntity.class);
        ProductEntity savedProduct = productRepository.insert(productEntity);
        WarehouseEntity warehouseEntity = new WarehouseEntity();
        warehouseEntity.setCount(quantity);
        warehouseEntity.setProductId(savedProduct.getId());
        warehouseRepository.save(warehouseEntity);
        return modelMapper.map(savedProduct, ProductDTO.class);

    }

    public ProductDTO updateProduct(ObjectId id, ProductDTO updatedProduct){
        Optional<ProductEntity> optionalProductEntity = productRepository.findById(id);
        if(optionalProductEntity.isPresent()){
            ProductEntity productEntity = optionalProductEntity.get();
            modelMapper.map(updatedProduct, productEntity);
            ProductEntity savedProduct = productRepository.save(productEntity);
            return modelMapper.map(savedProduct, ProductDTO.class);
        }else{
            System.out.println("There is no product with ID: " + id);
            return null;
        }
    }
}
