package com.vicary.zalandoscraper.api_telegram.api_object.chat.chat_member;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import com.vicary.zalandoscraper.api_telegram.api_object.User;

@Data
public class ChatMemberLeft implements ChatMember {
    @JsonProperty("status")
    private final String status = "left";

    @JsonProperty("user")
    private User user;

    public ChatMemberLeft() {
    }
}
