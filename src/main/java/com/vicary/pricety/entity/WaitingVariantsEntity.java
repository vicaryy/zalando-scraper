package com.vicary.pricety.entity;

import com.vicary.pricety.thread_local.ActiveUser;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "waiting_variants")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WaitingVariantsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "url")
    private String url;

    public static WaitingVariantsEntity requestEntity(String url) {
        return WaitingVariantsEntity.builder()
                .url(url)
                .build();
    }

    public static WaitingVariantsEntity emptyEntity() {
        return WaitingVariantsEntity.builder()
                .url("")
                .build();
    }
}
