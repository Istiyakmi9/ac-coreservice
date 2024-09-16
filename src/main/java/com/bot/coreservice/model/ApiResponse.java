package com.bot.coreservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.http.HttpStatus;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse {

    @JsonProperty("AuthenticationToken")
    public String authenticationToken;
    @JsonProperty("ResponseBody")
    public Object responseBody;
    @JsonProperty("HttpStatusCode")
    public int httpStatusCode;
    @JsonProperty("HttpStatusMessage")
    public String httpStatusMessage;

    public ApiResponse(String token) {
        this.authenticationToken = token;
    }

    public static ApiResponse Ok(Object data) {
        return ApiResponse.builder()
                .responseBody(data)
                .httpStatusCode(HttpStatus.OK.value())
                .httpStatusMessage("successfull")
                .build();
    }

    public static ApiResponse Ok(Object data, String token) {
        return ApiResponse.builder()
                .responseBody(data)
                .authenticationToken(token)
                .httpStatusCode(HttpStatus.OK.value())
                .httpStatusMessage("successfull")
                .build();
    }

    public static ApiResponse BadRequest(Object data) {
        return ApiResponse.builder()
                .responseBody(data)
                .httpStatusCode(HttpStatus.BAD_REQUEST.value())
                .httpStatusMessage("error")
                .build();
    }

}
