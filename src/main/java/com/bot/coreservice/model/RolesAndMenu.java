package com.bot.coreservice.model;

import lombok.Data;

import java.util.List;

@Data
public class RolesAndMenu {
    int accessLevelId;

    List<MenuAndPermission> menu;
}
