package com.feed_the_beast.ftbt;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import minetweaker.MineTweakerAPI;
import minetweaker.MineTweakerImplementationAPI;
import minetweaker.mc1710.MineTweakerMod;
import minetweaker.mc1710.network.MineTweakerLoadScriptsPacket;
import minetweaker.mc1710.util.MineTweakerHacks;
import minetweaker.runtime.IScriptProvider;
import minetweaker.runtime.providers.ScriptProviderCascade;
import minetweaker.runtime.providers.ScriptProviderDirectory;
import net.minecraft.server.MinecraftServer;
import serverutils.lib.api.EventServerUtilitiesReload;
import serverutils.lib.api.GameModes;

import java.io.File;
import java.util.ArrayList;

public class ReloadHandler {
	@SubscribeEvent
	public void onReloaded(EventServerUtilitiesReload event) {
        if (!event.world.getMode().getFile("scripts").exists()) {
            event.world.getMode().getFile("scripts").mkdirs();
        }
        if (!GameModes.getGameModes().commonMode.getFile("scripts").exists()) {
            GameModes.getGameModes().commonMode.getFile("scripts").mkdirs();
        }
		MineTweakerAPI.tweaker.rollback();

		ArrayList<IScriptProvider> providers = new ArrayList<>();

		providers.add(new ScriptProviderDirectory(event.world.getMode().getFile("scripts")));
		providers.add(new ScriptProviderDirectory(GameModes.getGameModes().commonMode.getFile("scripts")));

		// Don't have to mkdir these because MT does that for us already.
		providers.add(new ScriptProviderDirectory(new File("scripts")));
		if (event.world.side.isServer()) {
			providers.add(new ScriptProviderDirectory(new File(MineTweakerHacks.getWorldDirectory(MinecraftServer.getServer()), "scripts")));
		}

		MineTweakerImplementationAPI.setScriptProvider(new ScriptProviderCascade(providers.toArray(new IScriptProvider[providers.size()])));
		MineTweakerImplementationAPI.reload();
        MineTweakerMod.NETWORK.sendToAll(new MineTweakerLoadScriptsPacket(MineTweakerAPI.tweaker.getScriptData()));
	}
}
