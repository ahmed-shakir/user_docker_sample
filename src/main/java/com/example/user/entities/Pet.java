package com.example.user.entities;

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
    private String id;
    private String name;
    private String race;
    private String sex;
    private int age;
    private Binary image;
}
