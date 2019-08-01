package com.scrapping.controller;

import com.scrapping.model.Scrapped;
import com.scrapping.repository.ScrappedRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping
public class ScrappController {

    private final ScrappedRepository scrappedRepository;

    public ScrappController(ScrappedRepository scrappedRepository) {
        this.scrappedRepository = scrappedRepository;
    }


    @GetMapping
    public ResponseEntity scrapp() throws IOException {

        try {
            Document document = Jsoup.connect("https://www.list.am/category/23").timeout(6000).get();

            Elements elements = document.select("div.gl");
//            String[] titles = new String[1];
            for (Element element : elements) {
                Elements a1 = element.getElementsByTag("a");
                for (Element element1 : a1) {
                    String modelInfo = element1.select("div.l").text();
                    String modelPrice = element1.select("div.p").text();
                    String picture = element1.tagName("img").attr("href").substring(2);
                    System.out.println(modelInfo);
                    System.out.println(modelPrice);
                    System.out.println(picture);
                    System.out.println();

                    Scrapped scrapped = Scrapped.builder()
                            .modelInfo(modelInfo)
                            .modelPrice(modelPrice)
                            .picture(picture)
                            .build();
                    scrappedRepository.save(scrapped);
                }
            }

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}