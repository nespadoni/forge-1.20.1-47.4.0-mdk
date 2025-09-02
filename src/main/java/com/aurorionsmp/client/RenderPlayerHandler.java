package com.aurorionsmp.client;

import com.aurorionsmp.AurorionSMPMod;
import com.aurorionsmp.Config;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Mod.EventBusSubscriber(modid = AurorionSMPMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class RenderPlayerHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger("RenderPlayerHandler");

	@SubscribeEvent
	public static void onRenderPlayer(RenderPlayerEvent.Post event) {
		// Obter o Player genérico primeiro
		Player genericPlayer = event.getEntity();
		PoseStack poseStack = event.getPoseStack();

		// Verificar se é um AbstractClientPlayer
		if (!(genericPlayer instanceof AbstractClientPlayer player)) {
			LOGGER.warn("RenderPlayerHandler: Player não é AbstractClientPlayer: {}",
					genericPlayer.getClass().getSimpleName());
			return;
		}

		// Cast seguro para AbstractClientPlayer

		// LOG PARA DEBUG
		LOGGER.info("RenderPlayerHandler: Renderizando jogador {}", player.getName().getString());

		if (!Config.showBalloons) {
			LOGGER.info("RenderPlayerHandler: showBalloons está desabilitado");
			return;
		}

		if (player.isInvisible() || !player.isAlive()) {
			LOGGER.info("RenderPlayerHandler: Jogador invisível ou morto");
			return;
		}

		// Verificar se deve mostrar balão próprio
		if (player.isLocalPlayer() && !Config.showOwnBalloon) {
			LOGGER.info("RenderPlayerHandler: Não mostrando balão próprio");
			return;
		}

		List<String> messages = ChatBalloonManager.getBalloonMessages(player.getUUID());
		LOGGER.info("RenderPlayerHandler: {} mensagens para {}",
				messages != null ? messages.size() : 0,
				player.getName().getString());

		if (messages == null || messages.isEmpty()) {
			return;
		}

		LOGGER.info("RenderPlayerHandler: Chamando BalloonRenderer.renderBalloons");
		try {
			// Usar Minecraft.getInstance() para acessar EntityRenderDispatcher e Font
			Minecraft mc = Minecraft.getInstance();
			BalloonRenderer.renderBalloons(poseStack, mc.getEntityRenderDispatcher(),
					mc.font, messages, player.getBbHeight(), player);
		} catch (Exception e) {
			LOGGER.error("Erro ao renderizar balões para {}: {}", player.getName().getString(), e.getMessage());
		}
	}
}