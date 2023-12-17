package fun.sast.sastlogin.client.sastlink;

import java.io.IOException;

public class UserChecker {

    private final static String redirect_uri = "http://mc.sast.fun:6066";
    private final static String client_id = "e1ab6676-88c3-4997-93b1-73fa78ef7f1a";
    private final static String code_challenge = "Qn3Kywp0OiU4NK_AFzGPlmrcYJDJ13Abj_jdL08Ahg8%3D";
    private final static String code_challenge_method = "S256";


    public static boolean openSastLink() throws IOException {
        //open sast-link website
        if(ClientOS.isLinux()){
            Runtime.getRuntime().exec("x-www-browser '"+getUrl()+"'");
        } else if (ClientOS.isWindows()) {
            Runtime.getRuntime().exec("cmd /c start '"+getUrl()+"'");
        } else if (ClientOS.isMacOS()||ClientOS.isMacOSX()) {
            Runtime.getRuntime().exec("open '"+getUrl()+"'");
        } else {
            return false;
        }
        return true;
    }

    public static String getUrl() {
        return "https://link.sast.fun/auth" +
                "?client_id=" + client_id +
                "&code_challenge=" + code_challenge +
                "&code_challenge_method=" + code_challenge_method +
                "&redirect_uri=" + redirect_uri +
                "&response_type=code&scope=all" +
                "&state=xyz";
    }

    public static String getUrlForWin() {
        return "https://link.sast.fun/auth" +
                "?client_id=" + client_id +
                "^&code_challenge=" + code_challenge +
                "^&code_challenge_method=" + code_challenge_method +
                "^&redirect_uri=" + redirect_uri +
                "^&response_type=code&scope=all" +
                "^&state=xyz";
    }

    private static class ClientOS {
        private static String OS = System.getProperty("os.name").toLowerCase();
        public static boolean isLinux() {
            return OS.contains("linux");
        }

        public static boolean isMacOS() {
            return OS.contains("mac") && OS.indexOf("os") > 0 && !OS.contains("x");
        }

        public static boolean isMacOSX() {
            return OS.contains("mac") && OS.indexOf("os") > 0 && OS.indexOf("x") > 0;
        }

        public static boolean isWindows() {
            return OS.contains("windows");
        }

    }
}
