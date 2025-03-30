package rockland.elysiancrest.com.data_service.config;


import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import rockland.elysiancrest.com.data_service.dto.CartItemDTO;
import rockland.elysiancrest.com.data_service.dto.FavoriteItemDTO;
import rockland.elysiancrest.com.data_service.dto.OrderDTO;
import rockland.elysiancrest.com.data_service.entity.cart.CartItem;
import rockland.elysiancrest.com.data_service.entity.favourites.FavoriteItem;
import rockland.elysiancrest.com.data_service.entity.order.OrderItemMaster;

import jakarta.annotation.PostConstruct;
import java.util.TimeZone;

@Configuration
public class ModelMapperConfig {

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Colombo"));
    }

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // Define mapping for CartItem to CartItemDTO
        modelMapper.addMappings(new PropertyMap<CartItem, CartItemDTO>() {
            @Override
            protected void configure() {
                map().setProductName(source.getProduct().getCorrectName());
                // Map other properties if needed
            }
        });

        // Define mapping for OrderItemMaster to OrderItemDTO
        modelMapper.addMappings(new PropertyMap<OrderItemMaster, OrderDTO.OrderItemDTO>() {
            @Override
            protected void configure() {
                map().setProductId(source.getProduct().getId());
                // Map other properties if needed
            }
        });

        modelMapper.typeMap(FavoriteItem.class, FavoriteItemDTO.class)
                .addMapping(src -> src.getUser().getId(), FavoriteItemDTO::setUserId)
                .addMapping(src -> src.getProduct().getId(), FavoriteItemDTO::setProductId)
                .addMapping(src -> src.getProduct().getCorrectName(), FavoriteItemDTO::setCorrectName)
                .addMapping(src -> src.getProduct().getImageUrl(), FavoriteItemDTO::setProductImageUrl)
                .addMapping(src -> src.getProduct().getRegularPriceInRupee(), FavoriteItemDTO::setRegularPriceInRupee)
                .addMapping(src -> src.getProduct().getRegularPriceInDollar(), FavoriteItemDTO::setRegularPriceInDollar);

        return modelMapper;
    }
}

