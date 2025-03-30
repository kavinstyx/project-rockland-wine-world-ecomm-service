package rockland.elysiancrest.com.data_service.config;

import com.azure.storage.blob.BlobClientBuilder;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.azure.storage.common.StorageSharedKeyCredential;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AzureBlobStorageConfig {

    @Value("${azure.storage.account-name}")
    private String accountName;

    @Value("${azure.storage.account-key}")
    private String accountKey;

    @Value("${azure.storage.blob-url}")
    private String blobUrl;

    @Value("${azure.storage.container-name}")
    private String containerName;

    @Value("${azure.storage.sas-token}")
    private String sasToken;

    @Bean
    public BlobContainerClient blobContainerClient(){
        //create a storage shared jey credential
        StorageSharedKeyCredential credential = new StorageSharedKeyCredential(accountName, accountKey);

        return new BlobContainerClientBuilder()
                .endpoint(blobUrl)
                .credential(credential)
                .containerName(containerName)
                .buildClient();
    }
}
