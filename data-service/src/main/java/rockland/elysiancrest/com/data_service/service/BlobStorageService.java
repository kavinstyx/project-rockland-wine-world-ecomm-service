package rockland.elysiancrest.com.data_service.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.models.BlobHttpHeaders;
import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.OutputStream;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class BlobStorageService {

    @Autowired
    private BlobContainerClient blobContainerClient;

    //Uploads a file to Azure Blob Storage.
    public String uploadFile(String blobName, InputStream inputStream, long fileSize){
        try {
            BlobClient blobClient = blobContainerClient.getBlobClient(blobName);
            blobClient.upload(inputStream, fileSize, true);
            return blobClient.getBlobUrl();

        } catch (Exception e){
            throw new RuntimeException("Failed to upload file to Azure Blob Storage: " + e.getMessage(), e);
        }
    }

    //Downloads a blob from Azure Blob Storage to an OutputStream.

    public void downloadFile(String blobName, OutputStream outputStream) {
        try {
            BlobClient blobClient = blobContainerClient.getBlobClient(blobName);
            blobClient.download(outputStream);
        } catch (Exception e) {
            throw new RuntimeException("Failed to download file from Azure Blob Storage: " + e.getMessage(), e);
        }
    }

    //Generates a viewable URL for a blob.
    public String generateBlobViewLink(String blobName) {
        try {
            BlobClient blobClient = blobContainerClient.getBlobClient(blobName);
            return Objects.requireNonNull(blobClient.getBlobUrl());
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate blob view link: " + e.getMessage(), e);
        }
    }

    //genrate temporarly link
    public String generateTemporaryUrl(String blobName, int expiryTimeInMinutes) {
        try {
            BlobClient blobClient = blobContainerClient.getBlobClient(blobName);

            // Update blob headers to set Content-Disposition to "inline"
            blobClient.setHttpHeaders(new BlobHttpHeaders()
                    .setContentDisposition("inline"));

            // Define expiry time for the SAS token
            OffsetDateTime expiryTime = OffsetDateTime.now().plusMinutes(expiryTimeInMinutes);

            // Define permissions for the SAS token (read-only)
            BlobSasPermission permissions = new BlobSasPermission().setReadPermission(true);

            // Create SAS token with defined permissions and expiry time
            BlobServiceSasSignatureValues sasValues = new BlobServiceSasSignatureValues(expiryTime, permissions);

            // Generate the SAS token and append it to the blob URL
            return blobClient.getBlobUrl() + "?" + blobClient.generateSas(sasValues);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate temporary URL for Azure Blob Storage: " + e.getMessage(), e);
        }
    }

    public List<String> listBlobs(String folderPath) {
        try {
            return blobContainerClient.listBlobs()
                    .stream()
                    .filter(blobItem -> blobItem.getName().startsWith(folderPath))
                    .map(blobItem -> blobItem.getName())
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Failed to list blobs in folder: " + folderPath, e);
        }
    }

    public void moveBlob(String sourceBlobName, String destinationBlobName) {
        try {
            BlobClient sourceBlobClient = blobContainerClient.getBlobClient(sourceBlobName);
            BlobClient destinationBlobClient = blobContainerClient.getBlobClient(destinationBlobName);

            // Copy the blob to the destination
            destinationBlobClient.beginCopy(sourceBlobClient.getBlobUrl(), null);

            // Delete the source blob
            sourceBlobClient.delete();
        } catch (Exception e) {
            throw new RuntimeException("Failed to move blob from " + sourceBlobName + " to " + destinationBlobName, e);
        }
    }

    public boolean blobExists(String blobName) {
        BlobClient blobClient = blobContainerClient.getBlobClient(blobName);
        return blobClient.exists();
    }




}
