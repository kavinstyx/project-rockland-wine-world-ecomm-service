package rockland.elysiancrest.com.data_service.controller;


import com.commonlibrary.contract.v1.Response;
import com.commonlibrary.contract.v1.Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import rockland.elysiancrest.com.data_service.service.BlobStorageService;
import rockland.elysiancrest.com.data_service.service.InventoryService;

import java.io.InputStream;

@Slf4j
@RestController
@RequestMapping("api/inventory")
@CrossOrigin
public class InventoryController {

    private final InventoryService inventoryService;
    private final BlobStorageService blobStorageService;

    public InventoryController(InventoryService inventoryService, BlobStorageService blobStorageService) {
        this.inventoryService = inventoryService;
        this.blobStorageService = blobStorageService;
    }

    @PostMapping("/upload-sync")
    public ResponseEntity<Response<Object>> uploadAndSyncInventory(
            @RequestParam(value = "file") MultipartFile file){
        try {
            //validate the file type
            if (file.isEmpty() || !file.getOriginalFilename().endsWith(".csv")){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        Response.builder()
                                .code(HttpStatus.BAD_REQUEST.value())
                                .status(Status.ERROR)
                                .message("Invalid file. Please upload a valid CSV file.")
                                .build()
                );
            }

            //save the file to Azure blob storage in the 'inventory/in' folder
            String blobName = "inventory/in/" + file.getOriginalFilename();
            try (InputStream inputStream = file.getInputStream()){
                String blobUrl = blobStorageService.uploadFile(blobName, inputStream, file.getSize());
                System.out.println("File uploaded to Azure Blob Storage: " + blobUrl);
            }

            //trigger the in ventory sync
            inventoryService.processInventoryFiles();

            System.out.println("Inventory Sync Success");

            return ResponseEntity.ok(
                    Response.success(null,"File uploaded and inventory synced successfully.") );
        } catch (Exception e){
            System.out.println("Error occurred during inventory sync: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Response.<Object>builder()
                            .status(Status.ERROR)
                            .data(null)
                            .message("Error occurred during inventory sync:" + e.getMessage())
                            .build()
            );
        }

    }
}
