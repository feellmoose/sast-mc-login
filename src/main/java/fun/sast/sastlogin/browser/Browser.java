package fun.sast.sastlogin.browser;

import fun.sast.sastlogin.config.Config;
import fun.sast.sastlogin.config.SastLinkConfig;
import fun.sast.sastlogin.sastlink.UserChecker;
import sast.sastlink.sdk.model.response.data.AccessToken;
import sast.sastlink.sdk.model.response.data.User;
import sast.sastlink.sdk.test.TestSastLinkServiceAdapter;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.atomic.AtomicReference;

public class Browser {
    public static void openBrowse(String url) throws IOException {
        if (Desktop.isDesktopSupported()) {
            URI uri = URI.create(url);
            Desktop dp = Desktop.getDesktop();
            if (dp.isSupported(Desktop.Action.BROWSE)) {
                dp.browse(uri);
            }
        }
    }

}