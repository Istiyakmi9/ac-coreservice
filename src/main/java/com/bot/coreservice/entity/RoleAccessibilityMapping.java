package com.bot.coreservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "role_accessibility_mapping")
public class RoleAccessibilityMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int roleAccessibilityMappingId;

    int accessLevelId;

    int accessCode;

    int accessibilityId;
}
