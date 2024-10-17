package com.bot.coreservice.services;

import com.bot.coreservice.Repository.RoleAndMenuRepository;
import com.bot.coreservice.contracts.IRoleAndMenuService;
import com.bot.coreservice.db.LowLevelExecution;
import com.bot.coreservice.entity.AccessLevel;
import com.bot.coreservice.model.DbParameters;
import com.bot.coreservice.model.MenuAndPermission;
import com.bot.coreservice.model.RolesAndMenu;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class RoleAndMenuService implements IRoleAndMenuService {
    @Autowired
    RoleAndMenuRepository roleRepository;
    @Autowired
    LowLevelExecution lowLevelExecution;
    @Autowired
    ObjectMapper objectMapper;

    public List<AccessLevel> getRolesService() {
        return roleRepository.findAll();
    }

    public List<AccessLevel> addRoleService(AccessLevel accessLevel) throws Exception {
        if (accessLevel.getRoleName().isEmpty() || accessLevel.getRoleName() == null)
            throw new Exception("Invalid role name");

        if (accessLevel.getAccessCodeDefination().isEmpty() || accessLevel.getAccessCodeDefination() == null)
            throw new Exception("Invalid access code definition");

        Date utilDate = new Date();
        var currentDate = new Timestamp(utilDate.getTime());

        accessLevel.setCreatedOn(currentDate);
        accessLevel.setUpdatedOn(currentDate);

        roleRepository.save(accessLevel);

        return getRolesService();
    }


    public List<MenuAndPermission> getMenuService(int accessLevelId) throws Exception {
        if (accessLevelId == 0)
            throw new Exception("Invalid role selected");

        List<DbParameters> dbParameters = new ArrayList<>();
        dbParameters.add(new DbParameters("_accesslevelId", accessLevelId, Types.INTEGER));

        var dataSet = lowLevelExecution.executeProcedure("sp_RolesAndMenu_GetAll", dbParameters);
        return objectMapper.convertValue(dataSet.get("#result-set-1"), new TypeReference<List<MenuAndPermission>>() {
        });
    }

    public String addUpdatePermission(RolesAndMenu rolesAndMenu) {

        rolesAndMenu.getMenu().forEach(x -> {
            List<DbParameters> dbParameters = new ArrayList<>();
            dbParameters.add(new DbParameters("_roleAccessibilityMappingId", -1, Types.INTEGER));
            dbParameters.add(new DbParameters("_accessLevelId", rolesAndMenu.getAccessLevelId(), Types.INTEGER));
            dbParameters.add(new DbParameters("_accessCode", x.getAccessCode(), Types.INTEGER));
            dbParameters.add(new DbParameters("_accessibilityId", x.getPermission(), Types.INTEGER));

            lowLevelExecution.executeProcedure("sp_role_accessibility_mapping_InsUpd", dbParameters);
        });

        return "Successfully";
    }
}
