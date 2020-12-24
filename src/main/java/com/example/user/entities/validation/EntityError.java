package com.example.user.entities.validation;

import lombok.Builder;
import lombok.Data;

/**
 * <description>
 *
 * @author Ahmed Shakir
 * @version 1.0
 * @since 2020-10-22
 */
@Data
@Builder
public class EntityError {
    private String field;
    private String message;
    private String rejectedValue;
}
