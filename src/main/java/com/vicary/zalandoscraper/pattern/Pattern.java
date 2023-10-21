package com.vicary.zalandoscraper.pattern;

import com.vicary.zalandoscraper.api_object.Update;
import org.springframework.stereotype.Component;

@Component
public class Pattern {

    public static boolean isZalandoURL(String text) {
        return text.startsWith("https://www.zalando.pl/") || text.startsWith("https://zalando.pl/");
    }

    public static boolean isCommand(String text) {
        return text.startsWith("/");
    }

    public static boolean isReplyMarkup(Update update) {
        return update.getCallbackQuery() != null;
    }
}
