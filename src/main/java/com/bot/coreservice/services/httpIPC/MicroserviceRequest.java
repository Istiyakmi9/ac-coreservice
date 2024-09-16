package com.bot.coreservice.services.httpIPC;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MicroserviceRequest {
    String url;
    String payload;
    String token;
    String companyCode;
    MultipartFile[] fileCollections;
    DbConfigModal database;
    String connectionString;
}
