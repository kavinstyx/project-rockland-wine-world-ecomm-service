//package rockland.elysiancrest.com.data_service.service;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.stereotype.Service;
//import rockland.elysiancrest.com.data_service.dto.ProductFilterDTO;
//import rockland.elysiancrest.com.data_service.entity.master_data.ProductMaster;
//import rockland.elysiancrest.com.data_service.repo.ProductFilterRepo;
//
//import java.util.List;
//
//@Service
//public class ProductService {
//
//
//    private ProductFilterRepo productFilterRepo;
//
//    public ProductService(ProductFilterRepo productFilterRepo) {
//        this.productFilterRepo = productFilterRepo;
//    }
//
//    public List<ProductMaster> findProducts(ProductFilterDTO filter) {
//        Sort sort = determineSortOrder(filter.getSortBy());
//        return productFilterRepo.findAll(ProductSpecification.createSpecification(filter), sort);
//    }
//
//    private Sort determineSortOrder(String sortBy) {
//        switch (sortBy) {
//            case "priceLowToHigh":
//                return Sort.by(Sort.Direction.ASC, "regularPrice");
//            case "priceHighToLow":
//                return Sort.by(Sort.Direction.DESC, "regularPrice");
//            case "fastMoving":
//                // Assuming there's a way to sort by how fast a product moves (e.g., sales volume)
//                return Sort.by(Sort.Direction.DESC, "salesVolume");
//            default:
//                return Sort.unsorted();
//        }
//    }
//
//
//}
