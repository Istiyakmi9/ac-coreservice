package com.bot.coreservice.controller;

import com.bot.coreservice.contracts.IUserService;
import com.bot.coreservice.model.ApiResponse;
import com.bot.coreservice.model.FilterModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/core/user/")
public class UserController {

    @Autowired
    IUserService userService;

    @PostMapping("addUserExcel")
    public ResponseEntity<ApiResponse> addUserExcel(
            @RequestPart(value = "userExcel", required = true) MultipartFile userExcel) throws Exception {
        var result = userService.addUserExcelService(userExcel);
        return ResponseEntity.ok(ApiResponse.Ok(result));
    }

    @PostMapping("filterUser")
    public ResponseEntity<ApiResponse> filterUser(@RequestBody FilterModel filterModel) {
        var result = userService.filterUserService(filterModel);
        return ResponseEntity.ok(ApiResponse.Ok(result));
    }

    @GetMapping("getUserById/{userId}")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable long userId) throws Exception {
        var result = userService.getUserByIdService(userId);
        return ResponseEntity.ok(ApiResponse.Ok(result));
    }

    @PostMapping("addNewUser")
    public ResponseEntity<ApiResponse> addNewUser(
            @RequestPart(value = "profile", required = false) MultipartFile profile,
            @RequestPart("user") String user) throws Exception {
        var result = userService.addNewUserService(user, profile);
        return ResponseEntity.ok(ApiResponse.Ok(result));
    }

    @PostMapping("updateUser")
    public ResponseEntity<ApiResponse> updateUser(
            @RequestPart(value = "profile", required = false) MultipartFile profile,
            @RequestPart("user") String user) throws Exception {
        var result = userService.updateUserService(user, profile);
        return ResponseEntity.ok(ApiResponse.Ok(result));
    }

    @PostMapping("addUserAndInvestmentExcel")
    public ResponseEntity<ApiResponse> addUserAndInvestmentExcel(
            @RequestPart(value = "userExcel", required = true) MultipartFile userExcel) throws Exception {
        var result = userService.addUserAndInvestmentExcelService(userExcel);
        return ResponseEntity.ok(ApiResponse.Ok(result));
    }

    @PostMapping("downloadUserExcel")
    public ResponseEntity<ApiResponse> downloadUserExcel(@RequestBody FilterModel filterModel) throws Exception {
        var result = userService.downloadUserExcelService(filterModel);
        return ResponseEntity.ok(ApiResponse.Ok(result));
    }
}