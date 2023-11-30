package com.vicary.zalandoscraper.updater;

import com.vicary.zalandoscraper.service.dto.ProductDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AutoUpdaterTest {

    @Autowired
    private AutoUpdater autoUpdater;

    @Test
    void splitListIntoScrapers() {
        //given
        List<ProductDTO> givenList = getListOfDTOs();
        //when
        List<List<ProductDTO>> actualList = autoUpdater.splitListIntoScrapers(givenList);

        //then
        int i = 0;
        for (List<ProductDTO> dto : actualList) {
            System.out.println("Name: " + dto.get(i).getName());
            System.out.println("Link: " + dto.get(i).getLink());
            System.out.println("-------");
            i++;
        }
    }


    private List<ProductDTO> getListOfDTOs() {
        List<ProductDTO> DTOs =  new ArrayList<>(Arrays.asList(
                getDto("zalando1", "https://www.zalando.pl/something123"),
                getDto("nike1", "https://www.nike.pl/something123"),
                getDto("hebe1", "https://www.hebe.pl/something123"),
                getDto("allegro1", "https://www.allegro.pl/something123"),
                getDto("olx1", "https://www.olx.pl/something123"),
                getDto("zalando2", "https://www.zalando.pl/something123"),
                getDto("zalando3", "https://www.zalando.pl/something123"),
                getDto("zalando4", "https://www.zalando.pl/something123"),
                getDto("zalando5", "https://www.zalando.pl/something123"),
                getDto("zalando6", "https://www.zalando.pl/something123"),
                getDto("zalando7", "https://www.zalando.pl/something123"),
                getDto("nike2", "https://www.nike.pl/something123"),
                getDto("nike3", "https://www.nike.pl/something123"),
                getDto("nike4", "https://www.nike.pl/something123"),
                getDto("nike5", "https://www.nike.pl/something123"),
                getDto("nike6", "https://www.nike.pl/something123"),
                getDto("nike7", "https://www.nike.pl/something123"),
                getDto("nike8", "https://www.nike.pl/something123"),
                getDto("nike9", "https://www.nike.pl/something123"),
                getDto("hebe2", "https://www.hebe.pl/something123"),
                getDto("hebe3", "https://www.hebe.pl/something123"),
                getDto("hebe4", "https://www.hebe.pl/something123"),
                getDto("hebe5", "https://www.hebe.pl/something123"),
                getDto("allegro2", "https://www.allegro.pl/something123"),
                getDto("allegro3", "https://www.allegro.pl/something123"),
                getDto("allegro4", "https://www.allegro.pl/something123"),
                getDto("allegro5", "https://www.allegro.pl/something123"),
                getDto("allegro6", "https://www.allegro.pl/something123"),
                getDto("allegro7", "https://www.allegro.pl/something123"),
                getDto("allegro8", "https://www.allegro.pl/something123"),
                getDto("olx2", "https://www.olx.pl/something123"),
                getDto("olx3", "https://www.olx.pl/something123"),
                getDto("olx4", "https://www.olx.pl/something123"),
                getDto("olx5", "https://www.olx.pl/something123"),
                getDto("olx6", "https://www.olx.pl/something123")
        ));

//        Collections.shuffle(DTOs);
        return DTOs;
    }

    private List<List<ProductDTO>> expectedListOfDTOs() {
        List<ProductDTO> zalandoList = Arrays.asList();
    }

    private ProductDTO getDto(String name, String link) {
        return ProductDTO.builder()
                .name(name)
                .link(link)
                .build();
    }
}