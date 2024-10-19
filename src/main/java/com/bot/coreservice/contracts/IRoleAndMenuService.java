package com.bot.coreservice.contracts;

import com.bot.coreservice.entity.AccessLevel;
import com.bot.coreservice.model.MenuAndPermission;
import com.bot.coreservice.model.RolesAndMenu;

import java.util.List;

public interface IRoleAndMenuService {
    List<AccessLevel> getRolesService();

    List<AccessLevel> addRoleService(AccessLevel accessLevel) throws Exception;

    List<MenuAndPermission> getMenuService(int accessLevelId) throws Exception;

    String addUpdatePermission(RolesAndMenu rolesAndMenu);
}
