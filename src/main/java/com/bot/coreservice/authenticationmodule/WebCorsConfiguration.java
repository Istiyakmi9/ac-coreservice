package com.bot.coreservice.authenticationmodule;

import com.bot.coreservice.model.FileStorageProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebCorsConfiguration implements WebMvcConfigurer {
    @Autowired
    FileStorageProperties fileStorageProperties;
    @Autowired
    ResourceLoader resourceLoader;
    private final Logger logger = LoggerFactory.getLogger(WebCorsConfiguration.class);

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String resourcePath = getResourceDetail();
        System.out.println("Resource path:--------------: " + resourcePath);
        registry.addResourceHandler("/**")
                .addResourceLocations("file:" + resourcePath);
    }

    private String getResourceDetail() {
        String basePath = "";

        try {
            logger.info("Getting static folder class path");

            if (fileStorageProperties.getActive().equals("dev")) {
                Resource resource = resourceLoader.getResource("classpath:");
                File file = resource.getFile();

                logger.info("[INFO]: Classpath location: " + file.getAbsolutePath());

                // Assuming that the resources folder is at the same level as the classpath root
                File resourcesFolder = new File(file.getParent(), "resources");

                basePath = resourcesFolder.getAbsolutePath();
            } else {
                basePath = System.getProperty("user.dir") + File.separator + "resources";

                // Get the current working directory
                logger.info("[INFO]: " + basePath);
            }

            // Print the absolute path
            logger.info("[INFO]: Current Working Directory: " + basePath);
        } catch (Exception ex) {
            logger.error("[ERROR]: Fail: " + ex.getMessage());
        }

        return basePath;
    }
}
