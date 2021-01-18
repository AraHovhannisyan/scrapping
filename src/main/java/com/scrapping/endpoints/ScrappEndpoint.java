package com.scrapping.endpoints;

import com.scrapping.model.Scrapped;
import com.scrapping.repository.ScrappedRepository;
import lombok.extern.slf4j.Slf4j;
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

import static java.lang.Thread.sleep;

@RestController
@RequestMapping
@Slf4j
public class ScrappEndpoint {

    private final ScrappedRepository scrappedRepository;

    public ScrappEndpoint(ScrappedRepository scrappedRepository) {
        this.scrappedRepository = scrappedRepository;
    }

    @GetMapping
    public ResponseEntity scrapp() throws IOException {

        try {
            Document document = Jsoup.connect("https://www.list.am/category/23").timeout(100000).get();
            log.info("Jsoup connects to the URL");
            Elements select = document.select("div#contentr");
            Element first = select.first();
            Elements link = first.getElementsByTag("a");

            for (Element linkElement : link) {
                String modelInfo = linkElement.select("div.l").text();
                String modelPrice = linkElement.select("div.p").text();
                Element imageElement = linkElement.select("img").first();

                if (imageElement != null) {
                    String absoluteUrl = imageElement.absUrl("src");

                    if (absoluteUrl == null || absoluteUrl.equals("")) {
                        absoluteUrl = imageElement.absUrl("data-original");
                    }

                    log.info(modelInfo + " : " + absoluteUrl + " : " + modelPrice);
                    Scrapped scrapped = Scrapped.builder()
                            .modelInfo(modelInfo)
                            .modelPrice(modelPrice)
                            .picture(absoluteUrl)
                            .build();
                    scrappedRepository.save(scrapped);
                    sleep(200);
                }

            }

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}