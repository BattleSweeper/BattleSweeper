package dev.battlesweeper.network.body;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter @ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonDeserialize
public class TokenInfo {

    private String grantType;
    private String accessToken;
    private String refreshToken;
}
