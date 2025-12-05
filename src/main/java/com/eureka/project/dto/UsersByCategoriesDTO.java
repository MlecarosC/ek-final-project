package com.eureka.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsersByCategoriesDTO {
    private Integer departmentId;
    private String departmentName;
    private Long userCount;
}
