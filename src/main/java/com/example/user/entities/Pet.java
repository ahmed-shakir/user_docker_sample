package com.example.user.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * <description>
 *
 * @author Ahmed Shakir
 * @version 1.0
 * @since 2020-10-26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "pet")
public class Pet implements Serializable {
    private static final long serialVersionUID = 4491473151329079066L;
    @Id
    @Schema(example = "1234")
    private String id;
    @Schema(example = "Pluto")
    private String name;
    @Schema(example = "Dog")
    private String race;
    @Schema(example = "Male")
    private String sex;
    @Schema(example = "2")
    private int age;
    private Binary image;
}
