package com.vicary.zalandoscraper.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "users", schema = "public")
public class UserEntity {
    @Id
    @Column(name = "user_id")
    private String userId;

    @Column(name = "nick")
    private String nick;

    @Column(name = "nationality")
    private String nationality;

    @Column(name = "premium")
    private boolean premium;

    @Column(name = "admin")
    private boolean admin;

    @OneToMany(mappedBy = "user")
    @ToString.Exclude
    private List<ProductEntity> products;
}
