package net.architects.RandomItemCommandMod;

import net.architects.RandomItemCommandMod.command.GetGroupsCommand;
import net.architects.RandomItemCommandMod.command.GiveRandomCommand;
import net.architects.RandomItemCommandMod.config.ModConfigs;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class RandomItemCommandMod implements ModInitializer {
	public static final String MOD_ID = "randomitemcommandmod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModConfigs.registerConfigs();
		registerCommands();

	}

	private static void registerCommands() {

		CommandRegistrationCallback.EVENT.register(GiveRandomCommand::register);
		CommandRegistrationCallback.EVENT.register(GetGroupsCommand::register);
	}
}