package rockland.elysiancrest.com.data_service.service.impl;

import com.commonlibrary.contract.v1.ComboPackDto;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rockland.elysiancrest.com.data_service.entity.ComboPack;
import rockland.elysiancrest.com.data_service.entity.master_data.ProductMaster;
import rockland.elysiancrest.com.data_service.repo.ComboPackRepo;
import rockland.elysiancrest.com.data_service.repo.ProductFilterRepo;
import rockland.elysiancrest.com.data_service.repo.ProductMasterRepo;
import rockland.elysiancrest.com.data_service.service.ComboPackService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class ComboPackServiceImpl extends CrudServiceImpl<ComboPack, Long, ComboPackRepo, ComboPackDto>implements ComboPackService {

    private final ProductMasterRepo productMasterRepo;

    public ComboPackServiceImpl(ComboPackRepo repository, ModelMapper modelMapper, ProductMasterRepo productMasterRepo) {
        super(repository, modelMapper);
        this.productMasterRepo = productMasterRepo;
    }
//
//    @Override
//    @Transactional
//    public ComboPackDto create(ComboPackDto dto) {
//        ComboPack entity = convertToEntity(dto);
//
//        // Validate and set products if provided
//        if (dto.getProductIds() != null && !dto.getProductIds().isEmpty()) {
//            List<ProductMaster> products = productMasterRepo.findAllById(dto.getProductIds());
//            if (products.size() != dto.getProductIds().size()) {
//                throw new RuntimeException("Some product IDs are invalid or not found.");
//            }
//            entity.setProducts(products);
//        }
//
//        ComboPack savedEntity = repository.save(entity);
//        return convertToDto(savedEntity);
//    }
//
//    @Override
//    @Transactional
//    public ComboPackDto update(Long id, ComboPackDto dto) {
//        ComboPack existingEntity = repository.findById(id)
//                .orElseThrow(() -> new RuntimeException("ComboPack with id " + id + " not found."));
//
//        // Update fields
//        modelMapper.map(dto, existingEntity);
//
//        // Validate and update products if provided
//        if (dto.getProductIds() != null && !dto.getProductIds().isEmpty()) {
//            List<ProductMaster> products = productMasterRepo.findAllById(dto.getProductIds());
//            if (products.size() != dto.getProductIds().size()) {
//                throw new RuntimeException("Some product IDs are invalid or not found.");
//            }
//            existingEntity.setProducts(products);
//        }
//
//        ComboPack updatedEntity = repository.save(existingEntity);
//        return convertToDto(updatedEntity);
//    }
//
//    @Override
//    protected ComboPackDto convertToDto(ComboPack entity) {
//        ComboPackDto dto = super.convertToDto(entity);
//
//        // Manually set the products field
//        if (entity.getProducts() != null) {
//            dto.setProductList(entity.getProducts().stream()
//                    .map(product -> {
//                        Map<String, String> productDetails = new HashMap<>();
//                        productDetails.put("id", product.getId().toString());
//                        productDetails.put("productName", product.getCorrectName());
//                        productDetails.put("imageUrl", product.getImageUrl());
//                        return productDetails;
//                    })
//                    .collect(Collectors.toList()));
//        }
//
//        return dto;
//    }
//
//    @Override
//    protected ComboPack convertToEntity(ComboPackDto dto) {
//        ComboPack entity = super.convertToEntity(dto);
//        if (dto.getProductIds() != null) {
//            List<ProductMaster> products = productMasterRepo.findAllById(dto.getProductIds());
//            entity.setProducts(products);
//        }
//        return entity;
//    }

}
