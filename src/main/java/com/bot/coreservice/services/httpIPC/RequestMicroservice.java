package com.bot.coreservice.services.httpIPC;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

@Service
public class RequestMicroservice {
    @Autowired
    ObjectMapper mapper;

    public <T> T uploadFile(MicroserviceRequest microserviceRequest) throws Exception {
        HttpHeaders headers = setHeader(microserviceRequest);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        for (MultipartFile file : microserviceRequest.fileCollections) {
            HttpEntity<Resource> fileEntity = new HttpEntity<>(
                    file.getResource(),
                    createFileHeaders(file.getContentType())
            );

            body.add("files", fileEntity);
        }

        HttpEntity<String> jsonEntity = new HttpEntity<>(
                microserviceRequest.getPayload(),
                createJsonHeaders()
        );

        body.add("files", jsonEntity);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            // Prepare file array
            MultipartFile[] files = microserviceRequest.getFileCollections();
            // Call the Feign client
            return null;
        } catch (Exception ex) {
            throw  new Exception(ex.getMessage(), ex);
        }
    }

    @Async
    private HttpHeaders setHeader(MicroserviceRequest microserviceRequest) throws Exception {
        HttpHeaders headers = new HttpHeaders();

        try {
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            //headers.setBearerAuth(microserviceRequest.getToken());

            //DbConfigModal dbConfigModal = discretConnectionString(microserviceRequest.connectionString);
           // headers.add("database", mapper.writeValueAsString(dbConfigModal));
            //headers.add("companyCode", microserviceRequest.getCompanyCode());
        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }

        return headers;
    }

    private HttpHeaders createFileHeaders(String contentType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        return headers;
    }

    private HttpHeaders createJsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private DbConfigModal discretConnectionString(String cs) {
        DbConfigModal dbConfigModal = new DbConfigModal();
        String[] partedText = cs.split(";");
        if (partedText.length > 1) {
            for (String text : partedText) {
                String[] mainText = text.split("=");
                if (mainText[0].equalsIgnoreCase("OrganizationCode")) {
                    dbConfigModal.setOrganizationCode(mainText[1]);
                } else if (mainText[0].equalsIgnoreCase("Code")) {
                    dbConfigModal.setCode(mainText[1]);
                } else if (mainText[0].equalsIgnoreCase("Schema")) {
                    dbConfigModal.setSchema(mainText[1]);
                } else if (mainText[0].equalsIgnoreCase("DatabaseName")) {
                    dbConfigModal.setDatabaseName(mainText[1]);
                } else if (mainText[0].equalsIgnoreCase("Server")) {
                    dbConfigModal.setServer(mainText[1]);
                } else if (mainText[0].equalsIgnoreCase("Port")) {
                    dbConfigModal.setPort(mainText[1]);
                } else if (mainText[0].equalsIgnoreCase("Database")) {
                    dbConfigModal.setDatabase(mainText[1]);
                } else if (mainText[0].equalsIgnoreCase("user id")) {
                    dbConfigModal.setUserId(mainText[1]);
                } else if (mainText[0].equalsIgnoreCase("Password")) {
                    dbConfigModal.setPassword(mainText[1]);
                } else if (mainText[0].equalsIgnoreCase("connection timeout")) {
                    dbConfigModal.setConnectionTimeout(mainText[1] != null ? Integer.parseInt(mainText[1]) : 0);
                } else if (mainText[0].equalsIgnoreCase("connection lifetime")) {
                    dbConfigModal.setConnectionLifetime(mainText[1] != null ? Integer.parseInt(mainText[1]) : 0);
                } else if (mainText[0].equalsIgnoreCase("min pool size")) {
                    dbConfigModal.setMinPoolSize(mainText[1] != null ? Integer.parseInt(mainText[1]) : 0);
                } else if (mainText[0].equalsIgnoreCase("max pool size")) {
                    dbConfigModal.setMaxPoolSize(mainText[1] != null ? Integer.parseInt(mainText[1]) : 0);
                } else if (mainText[0].equalsIgnoreCase("Pooling")) {
                    dbConfigModal.setPooling(mainText[1] != null && Boolean.parseBoolean(mainText[1]));
                }
            }
        }

        return dbConfigModal;
    }
}
