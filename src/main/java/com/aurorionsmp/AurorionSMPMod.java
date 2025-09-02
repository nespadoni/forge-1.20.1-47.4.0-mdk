package com.aurorionsmp;

import com.aurorionsmp.commands.AurorionTalkCommand;
import com.aurorionsmp.network.NetworkHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(AurorionSMPMod.MODID)
public class AurorionSMPMod {
	public static final String MODID = "aurorionsmp";
	private static final Logger LOGGER = LoggerFactory.getLogger("AurorionSMP");

	public AurorionSMPMod(FMLJavaModLoadingContext context) {
		IEventBus modEventBus = context.getModEventBus();

		// Registrar eventos do mod
		modEventBus.addListener(this::commonSetup);

		// Registrar eventos do jogo
		MinecraftForge.EVENT_BUS.register(this);

		LOGGER.info("AurorionSMP carregado!");
	}

	private void commonSetup(final FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			NetworkHandler.register();
			LOGGER.info("Setup comum do AurorionSMP concluído!");
		});
	}

	@SubscribeEvent
	public void onCommandsRegister(RegisterCommandsEvent event) {
		AurorionTalkCommand.register(event.getDispatcher());
		LOGGER.info("Comandos AuroriontTalk registrados!");
	}
}