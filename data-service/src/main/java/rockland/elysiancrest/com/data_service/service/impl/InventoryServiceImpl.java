package rockland.elysiancrest.com.data_service.service.impl;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rockland.elysiancrest.com.data_service.dto.InventoryDTO;
import rockland.elysiancrest.com.data_service.entity.cart.InventoryFreezeMaster;
import rockland.elysiancrest.com.data_service.entity.inventory.Inventory;
import rockland.elysiancrest.com.data_service.repo.InventoryFreezeRepo;
import rockland.elysiancrest.com.data_service.repo.InventoryRepo;
import rockland.elysiancrest.com.data_service.service.BlobStorageService;
import rockland.elysiancrest.com.data_service.service.InventoryService;

import javax.sql.DataSource;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

@Service
@Transactional
public class InventoryServiceImpl extends BaseServiceImpl<Inventory, InventoryDTO> implements InventoryService {

    private final ModelMapper modelMapper;
    private final InventoryRepo inventoryRepo;
    private final InventoryFreezeRepo inventoryFreezeRepository;
    private final DataSource dataSource;
    private final BlobStorageService blobStorageService;
    private final EntityManager entityManager;




    @Value("${file.in.folder}")
    private String inFolder;

    @Value("${file.out.folder}")
    private String outFolder;

    @Value("${file.error.folder}")
    private String errorFolder;


    public InventoryServiceImpl(InventoryRepo repository, ModelMapper modelMapper, InventoryRepo inventoryRepo, InventoryFreezeRepo inventoryFreezeRepository, DataSource dataSource, BlobStorageService blobStorageService, EntityManager entityManager) {
        super(repository);
        this.modelMapper = modelMapper;
        this.inventoryRepo = inventoryRepo;
        this.inventoryFreezeRepository = inventoryFreezeRepository;
        this.dataSource = dataSource;
        this.blobStorageService = blobStorageService;
        this.entityManager = entityManager;
    }

    @Override
    public InventoryDTO convertToDto(Inventory inventory) {
        return modelMapper.map(inventory, InventoryDTO.class);
    }

    @Override
    public Inventory convertToEntity(InventoryDTO inventoryDTO) {
        return modelMapper.map(inventoryDTO, Inventory.class);
    }

    @Override
    public boolean reserveQuantity(Long productId, int quantity, Long plantMasterId) {
        return inventoryRepo.findByProductIdAndPlantMasterId(productId, plantMasterId)
                .map(inventory -> {
                    boolean reserved = inventory.reserveQuantity(quantity, plantMasterId);
                    if (reserved) {
                        inventoryRepo.save(inventory);
                    }
                    return reserved;
                })
                .orElse(false);
    }

    @Override
    public void releaseReservedQuantity(Long productId, int quantity, Long plantMasterId) {
        inventoryRepo.findByProductId(productId)
                .ifPresent(inventory -> {
                    inventory.releaseReservedQuantity(quantity, plantMasterId);
                    inventoryRepo.save(inventory);
                });
    }

//    @Override
//    public void finalizePurchase(Long productId, int quantity) {
//        inventoryRepo.findByProductId(productId)
//                .ifPresent(inventory -> {
//                    inventory.finalizePurchase(quantity);
//                    inventoryRepo.save(inventory);
//                });
//    }

    @Override
    public void finalizePurchase(Long productId, int quantity, Long plantMasterId) {
        Inventory inventory = inventoryRepo.findByProductId(productId)
                .orElseThrow(() -> new EntityNotFoundException("Inventory not found for the product"));

        // Deduct reserved quantity and update stock
        inventory.deductReservedQuantity(quantity, plantMasterId);

        // Optionally, save the updated inventory to the repository
        inventoryRepo.save(inventory);
    }

    @Override
    public Optional<Inventory> findByProductId(Long productId) {
        return inventoryRepo.findByProductId(productId);
    }


    @Override
    public void createInventoryFreeze(Long productId, int quantity, Long userId, Long orderId, Long plantMasterId) {
        // Check if inventory is available
        Inventory inventory = inventoryRepo.findByProductIdAndPlantMasterId(productId, plantMasterId)
                .orElseThrow(() -> new EntityNotFoundException("Inventory not found for product ID: " + productId));

        // Check if enough stock is available to reserve
        System.out.println(inventory.getTotalQuantity() + " :: " + inventory.getReservedQuantity() + " :: " + inventory.getBufferQuantity()
                + " :: " + quantity);
        int availableQuantity = inventory.getTotalQuantity() - inventory.getReservedQuantity() - inventory.getBufferQuantity();
        if (availableQuantity < quantity) {
            throw new IllegalStateException("Not enough stock to freeze");
        }

        // Create a new inventory freeze entry
        InventoryFreezeMaster freeze = new InventoryFreezeMaster();
        freeze.setProductId(inventory.getProduct().getId());
        freeze.setQuantity(quantity);
        freeze.setUserId(userId); // Assuming you have a user ID to link the freeze
        freeze.setOrderId(orderId); // Link freeze to the specific cart
        freeze.setReservedAt(LocalDateTime.now());
//        freeze.setSessionId(sessionId);

        // Save the freeze entry to the database
        inventoryFreezeRepository.save(freeze);

        // Update the inventory's reserved quantity
//        inventory.setReservedQuantity(inventory.getReservedQuantity() + quantity);
        inventory.setTotalQuantity(inventory.getTotalQuantity() - quantity);
        inventoryRepo.save(inventory);
    }


    @Override
    @Transactional
    public void releaseInventoryFreeze(Long productId, int quantity, Long cartId) {
        List<InventoryFreezeMaster> freezes = inventoryFreezeRepository.findByProductIdAndOrderIdAndReleasedIsFalse(productId, cartId);

        int remainingQuantity = quantity;
        for (InventoryFreezeMaster freeze : freezes) {
            int freezeQuantity = freeze.getQuantity();

            if (freezeQuantity <= remainingQuantity) {
                freeze.setReleased(true);
                inventoryFreezeRepository.save(freeze);  // Mark as released
                remainingQuantity -= freezeQuantity;
            } else {
                // Partial release
                freeze.setQuantity(freezeQuantity - remainingQuantity);
                inventoryFreezeRepository.save(freeze);
                remainingQuantity = 0;
            }

            if (remainingQuantity <= 0) break;
        }

        // Update the reserved quantity in the Inventory record
        Inventory inventory = inventoryRepo.findByProductId(productId)
                .orElseThrow(() -> new EntityNotFoundException("Inventory not found for product ID: " + productId));
        inventory.setReservedQuantity(inventory.getReservedQuantity() - quantity);
        inventoryRepo.save(inventory);
    }

    @Override
    public Optional<Inventory> findByProductIdAndPlantMasterId(Long id, Long plantMasterId) {
        return inventoryRepo.findByProductIdAndPlantMasterId(id, plantMasterId);
    }


    @Override
    public boolean isAvailableForReservation(Long productId, int requestedQuantity, Long plantMasterId) {
        return inventoryRepo.findByProductIdAndPlantMasterId(productId, plantMasterId)
                .map(inventory -> inventory.isAvailableForReservation(requestedQuantity, plantMasterId))
                .orElse(false);
    }

    @Override
    @Transactional
    public void processInventoryFiles() throws IOException {
        // List all blobs in 'inventory/in' folder
        List<String> blobs = blobStorageService.listBlobs("inventory/in/");

        for (String blobName : blobs) {
            Path tempFile = null;
            try {
                // Download the file to a temporary location
                tempFile = Files.createTempFile("inventory", ".csv");
                try (OutputStream outputStream = Files.newOutputStream(tempFile)) {
                    blobStorageService.downloadFile(blobName, outputStream);
                }

                // Process the file in parallel
                processFileInChunks(tempFile);

                // Move the file to the 'out' folder in Blob Storage
                String outBlobName = blobName.replace("inventory/in/", "inventory/out/");
                blobStorageService.moveBlob(blobName, outBlobName);
                System.out.println("File processed and moved to 'out': " + blobName);
            } catch (Exception e) {
                System.err.println("Error processing file: " + blobName + ", Error: " + e.getMessage());
                e.printStackTrace();

                // Move the file to the 'error' folder in Blob Storage
                String errorBlobName = blobName.replace("inventory/in/", "inventory/error/");
                if (blobStorageService.blobExists(blobName)) {
                    blobStorageService.moveBlob(blobName, errorBlobName);
                } else {
                    System.err.println("Blob already moved or does not exist: " + blobName);
                }
            } finally {
                // Delete the temporary file
                if (tempFile != null && Files.exists(tempFile)) {
                    try {
                        Files.delete(tempFile);
                    } catch (IOException ex) {
                        System.err.println("Failed to delete temporary file: " + tempFile);
                        ex.printStackTrace();
                    }
                }
            }
        }
    }


    @Transactional
    public void processFileInChunks(Path filePath) throws IOException {
        // Read the CSV file
        try (CSVReader csvReader = new CSVReader(new FileReader(filePath.toFile()))) {
            List<String[]> records = csvReader.readAll();
            // Skip the header row if present
            if (!records.isEmpty()) {
                records.remove(0); // Remove the first row (header)
            }

            // Preload products into a map
            Map<String, Long> products = loadProducts();

            // Split records into chunks
            List<List<String[]>> chunks = splitIntoChunks(records, 1000); // Batch size = 1000

            // Process each chunk in asynchronously
            List<CompletableFuture<Void>> futures = new ArrayList<>();

            for (List<String[]> chunk : chunks) {
                futures.add(processChunkAsync(chunk, products));
            }
            // Wait for all asynchronous tasks to complete
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        } catch (CsvException e) {
            throw new RuntimeException("Error reading CSV", e);
        }
    }


    @Transactional
    public void processChunk(List<String[]> chunk, Map<String, Long> products) {
        // Prepare bulk updates
        Map<Long, Map<Long, Integer>> inventoryUpdates = new HashMap<>();

        for (String[] record : chunk) {
            try {
                Long productId = products.get(record[0]); // SAP Material Code
                Long plantId = Long.parseLong(record[1]); // Plant ID
                Integer totalQuantity = Integer.parseInt(record[2]); // Total Quantity

                // Prepare updates grouped by productId and plantId
                inventoryUpdates.computeIfAbsent(productId, k -> new HashMap<>())
                        .put(plantId, totalQuantity);
            } catch (Exception e) {
                System.err.println("Error processing record: " + Arrays.toString(record) + ", Error: " + e.getMessage());
            }
        }

        // Perform bulk update for the chunk
        bulkUpdateInventory(inventoryUpdates);
    }

    @Async
    @Transactional
    public CompletableFuture<Void> processChunkAsync(List<String[]> chunk, Map<String, Long> products) {
        processChunk(chunk, products);
        return CompletableFuture.completedFuture(null);
    }



    @Transactional
    public void bulkUpdateInventory(Map<Long, Map<Long, Integer>> inventoryUpdates) {
        for (Map.Entry<Long, Map<Long, Integer>> productEntry : inventoryUpdates.entrySet()) {
            Long productId = productEntry.getKey();
            Map<Long, Integer> plantData = productEntry.getValue();

            for (Map.Entry<Long, Integer> plantEntry : plantData.entrySet()) {
                Long plantId = plantEntry.getKey();
                Integer totalQuantity = plantEntry.getValue();

                // Perform the bulk update
                String hql = "UPDATE Inventory i " +
                        "SET i.totalQuantity = :totalQuantity " +
                        "WHERE i.product.id = :productId AND i.plantMaster.id = :plantId";

                int updatedRows = entityManager.createQuery(hql)
                        .setParameter("totalQuantity", totalQuantity)
                        .setParameter("productId", productId)
                        .setParameter("plantId", plantId)
                        .executeUpdate();

                // If no rows were updated, insert a new record
                if (updatedRows == 0) {
                    String insertSQL = "INSERT INTO wine_world.inventory (product_id, plant_id, total_quantity, buffer_quantity, reserved_quantity) " +
                            "VALUES (?, ?, ?, 100, 0)";

                    entityManager.createNativeQuery(insertSQL)
                            .setParameter(1, productId) // 1st placeholder
                            .setParameter(2, plantId)   // 2nd placeholder
                            .setParameter(3, totalQuantity) // 3rd placeholder
                            .executeUpdate();
                }
            }
        }
    }


    public Map<String, Long> loadProducts() {
        String query = "SELECT p.material, p.id FROM ProductMaster p"; // Assuming ProductMaster is an entity
        List<Object[]> resultList = entityManager.createQuery(query, Object[].class).getResultList();

        Map<String, Long> products = new ConcurrentHashMap<>();
        for (Object[] result : resultList) {
            products.put((String) result[0], (Long) result[1]);
        }
        return products;
    }


    public  <T> List<List<T>> splitIntoChunks(List<T> list, int chunkSize) {
        List<List<T>> chunks = new ArrayList<>();
        for (int i = 0; i < list.size(); i += chunkSize) {
            chunks.add(list.subList(i, Math.min(list.size(), i + chunkSize)));
        }
        return chunks;
    }



}
