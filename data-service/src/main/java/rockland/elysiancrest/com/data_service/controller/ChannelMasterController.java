package rockland.elysiancrest.com.data_service.controller;

import com.commonlibrary.contract.v1.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rockland.elysiancrest.com.data_service.dto.ChanelMasterDTO;
import rockland.elysiancrest.com.data_service.entity.master_data.ChannelMaster;
import rockland.elysiancrest.com.data_service.service.ChannelMasterService;

import java.util.logging.Logger;

@Slf4j
@RestController
@RequestMapping("api/channel")
@CrossOrigin

public class ChannelMasterController extends AbstractCrudController<Channel, Long, ChannelMasterService>{

    protected ChannelMasterController(ChannelMasterService service){
        super(service);
    }
}
//    private final ChannelMasterService channelMasterService;
//    private final Logger logger = Logger.getLogger("msg-data-service");
//
//    public ChannelMasterController(ChannelMasterService channelMasterService) {
//        this.channelMasterService = channelMasterService;
//    }
//
//    @ExceptionHandler({Exception.class})
//    public String databaseError(Exception e) {
//        e.printStackTrace();
//        return "databaseError";
//    }
//
//    @PostMapping
//    public ResponseEntity<ChanelMasterDTO> createChannel(@RequestBody ChanelMasterDTO dto) {
//        logger.info("Entering createChannel method");
//        ChannelMaster channelMaster = channelMasterService.convertToEntity(dto);
//        ChannelMaster savedChannel = channelMasterService.save(channelMaster);
//
//        // Convert the saved entity back to DTO for response
//        ChanelMasterDTO responseDto = channelMasterService.convertToDto(savedChannel);
//        return ResponseEntity.ok(responseDto);
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<ChanelMasterDTO> getChannelById(@PathVariable("id") Long id) {
//        logger.info("Entering getChannelById method");
//        return channelMasterService.findById(id)
//                .map(channel -> {
//                    ChanelMasterDTO responseDto = channelMasterService.convertToDto(channel);
//                    System.out.println("ChannelMasterDTO: " + responseDto.toString());
//                    return ResponseEntity.ok(responseDto);
//                })
//                .orElseGet(() -> {
//                    System.out.println("Channerlmaster not found for id: " + id);
//                    return ResponseEntity.notFound().build();
//                });
//    }
//
//    @GetMapping("")
//    public ResponseEntity<Page<ChanelMasterDTO>> getAllChannels(
//            @RequestParam(name = "page", defaultValue = "0") int page,
//            @RequestParam(name = "size", defaultValue = "10") int size) {
//        try {
//            logger.info("Entering getAllChannels method");
//            PageRequest pageRequest = PageRequest.of(page, size);
//            Page<ChannelMaster> channelPage = channelMasterService.findAll(pageRequest);
//
//            // Convert Page<ChannelMaster> to Page<ChannelMasterDTO>
//            Page<ChanelMasterDTO> channelDtoPage = channelPage.map(channelMasterService::convertToDto);
//            return ResponseEntity.ok(channelDtoPage);
//        } catch (Exception e) {
//            logger.info("Error in getAllChannels method: " + e.getMessage());
//            e.printStackTrace();
//            return ResponseEntity.status(500).build();
//        }
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<ChanelMasterDTO> updateChannel(@PathVariable("id") Long id, @RequestBody ChanelMasterDTO dto) {
//        if (!channelMasterService.findById(id).isPresent()) {
//            return ResponseEntity.notFound().build();
//        }
//        ChannelMaster channelMaster = channelMasterService.convertToEntity(dto);
//        ChannelMaster updatedChannel = channelMasterService.update(id, channelMaster);
//        return ResponseEntity.ok(channelMasterService.convertToDto(updatedChannel));
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteChannel(@PathVariable("id") Long id) {
//        if (!channelMasterService.findById(id).isPresent()) {
//            return ResponseEntity.notFound().build();
//        }
//        channelMasterService.deleteById(id);
//        return ResponseEntity.noContent().build();
//    }
//}
