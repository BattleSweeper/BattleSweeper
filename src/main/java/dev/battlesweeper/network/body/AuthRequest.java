package dev.battlesweeper.network.body;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AuthRequest {

    public static final int TYPE_ANONYMOUS  = 1;
    public static final int TYPE_REGISTERED = 2;

    public int type;
    public AuthInfo info;

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter @Setter
    public static class AuthInfo {

        // Present only on Anonymous request
        public String username;

        // Present only on Registered request
        public String email;
        public String password;
    }
}
