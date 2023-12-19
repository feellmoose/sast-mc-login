package fun.sast.sastlogin.config;

import com.google.common.io.Files;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class SastLinkConfig implements Config {
    public static final SastLinkConfig sastLinkConfig = read();
    private String clientId;
    private String clientSecret;
    private String codeVerifier;
    private String hostName;
    private String redirectUri;
    private String codeChallenge;
    private String codeChallengeMethod;
    private int port;

    private static SastLinkConfig read() {
        if (!SAST_LINK.exists()) {
            throw new RuntimeException("error! sast-link.json not exist! error path: " + SAST_LINK.getPath());
        }
        SastLinkConfig config = null;
        try (BufferedReader bufferedReader = Files.newReader(SAST_LINK, StandardCharsets.UTF_8)) {
            config = gson.fromJson(bufferedReader, SastLinkConfig.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return config;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getCodeVerifier() {
        return codeVerifier;
    }

    public void setCodeVerifier(String codeVerifier) {
        this.codeVerifier = codeVerifier;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public String getCodeChallenge() {
        return codeChallenge;
    }

    public void setCodeChallenge(String codeChallenge) {
        this.codeChallenge = codeChallenge;
    }

    public String getCodeChallengeMethod() {
        return codeChallengeMethod;
    }

    public void setCodeChallengeMethod(String codeChallengeMethod) {
        this.codeChallengeMethod = codeChallengeMethod;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "SastLinkConfig{" +
                "clientId='" + clientId + '\'' +
                ", clientSecret='" + clientSecret + '\'' +
                ", codeVerifier='" + codeVerifier + '\'' +
                ", hostName='" + hostName + '\'' +
                ", redirectUri='" + redirectUri + '\'' +
                ", codeChallenge='" + codeChallenge + '\'' +
                ", codeChallengeMethod='" + codeChallengeMethod + '\'' +
                ", port=" + port +
                '}';
    }
}