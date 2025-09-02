package com.aurorionsmp;

import com.aurorionsmp.client.ChatBalloonManager;
import com.aurorionsmp.network.NetworkHandler;
import com.mojang.logging.LogUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(AurorionSMPMod.MODID)
public class AurorionSMPMod {
	public static final String MODID = "aurorionsmp";
	private static final Logger LOGGER = LogUtils.getLogger();

	public AurorionSMPMod(FMLJavaModLoadingContext context) {
		IEventBus modEventBus = context.getModEventBus();
		modEventBus.addListener(this::commonSetup);

		MinecraftForge.EVENT_BUS.register(this);
		context.registerConfig(ModConfig.Type.COMMON, Config.SPEC); // Mudado para COMMON
	}

	private void commonSetup(final FMLCommonSetupEvent event) {
		// Registrar networking
		NetworkHandler.register();
		LOGGER.info("Aurorion SMP mod carregado com sucesso!");
	}

	@SubscribeEvent
	public void onServerStarting(ServerStartingEvent event) {
		LOGGER.info("Aurorion SMP iniciando no servidor");
	}

	@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
	public static class ClientModEvents {
		@SubscribeEvent
		public static void onClientSetup(FMLClientSetupEvent event) {
			LOGGER.info("Aurorion SMP configuração do cliente concluída");
		}
	}

	@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
	public static class ClientForgeEvents {
		@SubscribeEvent
		public static void onClientTick(TickEvent.ClientTickEvent event) {
			if (event.phase == TickEvent.Phase.END) {
				ChatBalloonManager.tick();
			}
		}
	}
}