package com.vicary.zalandoscraper.service.response.reply_markup;

import com.vicary.zalandoscraper.api_telegram.api_object.ParseMode;
import com.vicary.zalandoscraper.api_telegram.api_object.keyboard.ReplyMarkup;
import com.vicary.zalandoscraper.api_telegram.api_request.send.SendMessage;
import com.vicary.zalandoscraper.api_telegram.service.QuickSender;
import com.vicary.zalandoscraper.format.MarkdownV2;
import com.vicary.zalandoscraper.messages.Messages;
import com.vicary.zalandoscraper.service.dto.ProductDTO;
import com.vicary.zalandoscraper.service.response.InlineBlock;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

class ProductDisplay {

    private final String chatId;
    private final Type type;
    private final List<ProductDTO> productDTOList;

    public ProductDisplay(@NonNull List<ProductDTO> productDTOList, @NonNull Type type, @NonNull String chatId) {
        this.productDTOList = productDTOList;
        this.type = type;
        this.chatId = chatId;
    }

    public void display() {
        List<StringBuilder> stringBuilders = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < productDTOList.size(); i++) {
            ProductDTO dto = productDTOList.get(i);

            if (i == 0)
                setTitle(sb);

            sb.append(getFullProductDescription(dto, i));

            if (i != productDTOList.size() - 1)
                sb.append("\n\n\\-\\-\\-\\-\\-\\-\\-\\-\\-\\-\\-\\-\n\n");

            int maxProductsAmountInOneMessage = 10;
            if (i % maxProductsAmountInOneMessage == 0 && i != 0) {
                stringBuilders.add(new StringBuilder(sb.toString()));
                sb.setLength(0);
            }
        }

        stringBuilders.add(new StringBuilder(sb));

        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .disableWebPagePreview(true)
                .parseMode(ParseMode.MarkdownV2)
                .text("")
                .build();

        if (stringBuilders.size() == 1) {
            sb.append(getSummaryMessage());
            message.setReplyMarkup(getReplyMarkup());

            message.setText(sb.toString());
            QuickSender.message(message);
            return;
        }


        for (StringBuilder s : stringBuilders)
            QuickSender.message(chatId, s.toString(), true);

        sb.setLength(0);

        sb.append(getSummaryMessage());
        message.setReplyMarkup(getReplyMarkup());

        message.setText(sb.toString());
        QuickSender.message(message);
    }


    private void setTitle(StringBuilder sb) {
        if (type == Type.ALL)
            sb.append("*").append(Messages.allProducts("yourProducts")).append("*\n\n");
        else if (type == Type.EDIT)
            sb.append("*").append(Messages.editPriceAlert("yourProducts")).append("*\n\n");
        else if (type == Type.DELETE)
            sb.append("Products to delete 🗑️\n\n");
    }

    private String getFullProductDescription(ProductDTO dto, int iterator) {
        String price = getFormattedPrice(dto.getPrice());
        String priceAlert = getFormattedPriceAlert(dto.getPriceAlert());
        String variant = getFormattedVariant(dto.getVariant());
        return """     
                *%s nr %d*
                                    
                *%s:* %s
                *%s:* %s
                *%s:* %s
                *%s:* %s
                *%s:* %s
                *%s:* %s""".
                formatted(
                        Messages.allProducts("product"),
                        iterator + 1,
                        Messages.allProducts("name"),
                        MarkdownV2.apply(dto.getName()).get(),
                        Messages.allProducts("description"),
                        MarkdownV2.apply(dto.getDescription()).get(),
                        Messages.allProducts("link"),
                        MarkdownV2.apply(dto.getLink()).toZalandoURL().get(),
                        Messages.allProducts("variant"),
                        MarkdownV2.apply(variant).get(),
                        Messages.allProducts("price"),
                        MarkdownV2.apply(price).get(),
                        Messages.allProducts("priceAlert"),
                        MarkdownV2.apply(priceAlert).get());
    }

    private String getFormattedPrice(double p) {
        return p == 0 ? Messages.allProducts("soldOut") : String.format("%.2f zł", p).replaceFirst(",", ".");
    }

    private String getFormattedPriceAlert(String p) {
        return (!p.equals("OFF") && !p.equals("AUTO")) ? p + " zł" : p;
    }

    private String getFormattedVariant(String v) {
        if (v.startsWith("-oneVariant"))
            v = v.substring(12);
        return v;
    }

    private String getSummaryMessage() {
        if (type == Type.ALL)
            return "\u200E \n\n\n" + Messages.allProducts("funFact");
        else if (type == Type.EDIT)
            return "\u200E \n\n*" + Messages.editPriceAlert("select") + "*\\.";
        else if (type == Type.DELETE)
            return "\u200E \n\n*Please select the item you want to delete*\\.";
        return null;
    }

    private ReplyMarkup getReplyMarkup() {
        if (type == Type.ALL)
            return InlineBlock.getBack();
        else if (type == Type.EDIT)
            return InlineBlock.getProductChoice(productDTOList, "-edit");
        else if (type == Type.DELETE)
            return InlineBlock.getProductChoice(productDTOList, "-delete");
        return null;
    }
}



















