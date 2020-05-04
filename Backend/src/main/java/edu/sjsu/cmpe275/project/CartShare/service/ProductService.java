package edu.sjsu.cmpe275.project.CartShare.service;

import edu.sjsu.cmpe275.project.CartShare.model.Product;
import edu.sjsu.cmpe275.project.CartShare.model.ProductId;
import edu.sjsu.cmpe275.project.CartShare.repository.ProductRepository;
import edu.sjsu.cmpe275.project.CartShare.repository.StoreRepository;
import edu.sjsu.cmpe275.project.CartShare.model.Address;
import edu.sjsu.cmpe275.project.CartShare.model.Store;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.*;

@Service
public class ProductService {

    private static final String FETCH_PROPERTY_DETAILS_EXCEPTION_MESSAGE = "No product details found for the store";
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private EntityManager entityManager;

    public ResponseEntity<?> addProduct(Product product){
        ProductId id = product.getId();

        Optional<Store> store = storeRepository.findById(id.getStoreId());
        product.setStore(store.get());
//        id.setSku(productRepository.getMaxSku());
        productRepository.saveAndFlush(product);
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(product);
    }

//    public Product addProduct(Product prop) {
//        System.out.println(prop.toString());
//        return productRepository.saveAndFlush(prop);
//    }

    public ResponseEntity<?> getproducts(Long id) {
        System.out.printf("inside getProduct : ", id);
        Optional<Store> store = storeRepository.findById(id);
        List<Product> products =store.get().getProducts();
        System.out.println(products);
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(products);
    }
    public ResponseEntity<?> getMaxSku() {
        System.out.printf("inside getMaxSku");
        try{
            Optional<Long> maxSku = Optional.ofNullable(productRepository.getMaxSku());
            if(maxSku.isPresent()){
                Long newMaxSku = maxSku.get() +1;
                return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(newMaxSku);
            }else{
                return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(1);
            }
        }catch(NullPointerException e){
            return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(1);
        }
    }

    public ResponseEntity<?> deleteProduct(Product product) {
        System.out.println("inside delete Product service");

        System.out.println(product.toString());
        productRepository.deleteById(product.getId());
        return ResponseEntity.status(HttpStatus.OK).body("Successfully Deleted");

    }

    public ResponseEntity<?> deleteProductById(ProductId product) {
        System.out.println("inside delete Product service");

        Optional<Product> existingProduct = productRepository.findById(product);
        System.out.println(existingProduct.get().toString());
        System.out.println(product.getSku()+"sku, storeId"+product.getStoreId());
        Optional<Store> store = storeRepository.findById(product.getStoreId());

        if(store.isPresent()&&existingProduct.isPresent()){
            System.out.println("store.get().toString()"+store.get().getName());
            store.get().getProducts().remove(existingProduct.get());
            storeRepository.saveAndFlush(store.get());
            return ResponseEntity.status(HttpStatus.OK).body("Successfully Deleted");
        }else{
            return ResponseEntity.status(HttpStatus.OK).body("Not Deleted");
        }
    }

}