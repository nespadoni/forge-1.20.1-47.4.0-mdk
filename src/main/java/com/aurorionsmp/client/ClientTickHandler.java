package com.aurorionsmp.client;

import com.aurorionsmp.AurorionSMPMod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AurorionSMPMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientTickHandler {
	private static int tickCounter = 0;

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			tickCounter++;
			// Executar limpeza a cada 20 ticks (1 segundo)
			if (tickCounter % 20 == 0) {
				ChatBalloonManager.tick();
			}
		}
	}
}