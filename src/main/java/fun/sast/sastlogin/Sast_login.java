package fun.sast.sastlogin;

import fun.sast.sastlogin.commands.BindCommand;
import fun.sast.sastlogin.commands.LoginCommand;
import fun.sast.sastlogin.model.UserAdapter;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class Sast_login implements ModInitializer {


    /**
     * Runs the mod initializer.
     */
    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            BindCommand.bindRegister(dispatcher);
            BindCommand.unBindRegister(dispatcher);
            BindCommand.accountRegister(dispatcher);
            LoginCommand.register(dispatcher);
        });
        UserAdapter.init();
    }
}
