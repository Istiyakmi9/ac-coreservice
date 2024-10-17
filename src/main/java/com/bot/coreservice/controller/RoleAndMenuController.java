package com.bot.coreservice.controller;

import com.bot.coreservice.contracts.IRoleAndMenuService;
import com.bot.coreservice.entity.AccessLevel;
import com.bot.coreservice.model.ApiResponse;
import com.bot.coreservice.model.RolesAndMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/core/roles/")
public class RoleAndMenuController {
    @Autowired
    IRoleAndMenuService iRoleService;

    @GetMapping("getRoles")
    public ResponseEntity<ApiResponse> getRoles() {
        var result = iRoleService.getRolesService();
        return ResponseEntity.ok(ApiResponse.Ok(result));
    }

    @PostMapping("addRole")
    public ResponseEntity<ApiResponse> addRole(@RequestBody AccessLevel accessLevel) throws Exception {
        var result = iRoleService.addRoleService(accessLevel);
        return ResponseEntity.ok(ApiResponse.Ok(result));
    }

    @GetMapping("getMenu/{accessLevelId}")
    public ResponseEntity<ApiResponse> getMenu(@PathVariable int accessLevelId) throws Exception {
        var result = iRoleService.getMenuService(accessLevelId);
        return ResponseEntity.ok(ApiResponse.Ok(result));
    }

    @PostMapping("addUpdatePermission")
    public ResponseEntity<ApiResponse> addUpdatePermission(@RequestBody RolesAndMenu rolesAndMenu) throws Exception {
        var result = iRoleService.addUpdatePermission(rolesAndMenu);
        return ResponseEntity.ok(ApiResponse.Ok(result));
    }
}
