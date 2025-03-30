package rockland.elysiancrest.com.data_service.controller;


import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rockland.elysiancrest.com.data_service.dto.AddonDTO;
import rockland.elysiancrest.com.data_service.entity.cart.addon.Addon;
import rockland.elysiancrest.com.data_service.service.AddonService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/addons")
@CrossOrigin
public class AddonsController extends AbstractController {


    private final AddonService addonService;
    private final ModelMapper modelMapper;

    public AddonsController(AddonService addonService, ModelMapper modelMapper) {
        this.addonService = addonService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/get")
    public ResponseEntity<List<AddonDTO>> getAvailableAddons() {
        List<Addon> addons = addonService.findAll();
        List<AddonDTO> addonDTOs = addons.stream()
                .map(addon -> modelMapper.map(addon, AddonDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(addonDTOs);
    }

}
