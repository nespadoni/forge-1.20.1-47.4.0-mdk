package com.aurorionsmp.client;

import com.aurorionsmp.AurorionSMPMod;
import com.aurorionsmp.Config;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Mod.EventBusSubscriber(modid = AurorionSMPMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class BalloonRenderer {
	private static final Logger LOGGER = LoggerFactory.getLogger(BalloonRenderer.class);
	private static final Minecraft client = Minecraft.getInstance();

	@SubscribeEvent
	public static void onRenderLevel(RenderLevelStageEvent event) {
		try {
			if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;
			if (!Config.showBalloons) return;

			Minecraft mc = Minecraft.getInstance();
			if (mc.level == null || mc.player == null) return;

			// Renderizar balões para todos os jogadores
			for (Player player : mc.level.players()) {
				try {
					// Mostrar balões do próprio jogador se configurado
					if (player == mc.player && !Config.showOwnBalloon) {
						continue;
					}

					List<String> messages = ChatBalloonManager.getBalloonMessages(player.getUUID());
					if (messages != null && !messages.isEmpty()) {
						renderBalloons(event.getPoseStack(), mc.getEntityRenderDispatcher(), mc.font, messages, player.getBbHeight(), player);
					}
				} catch (Exception e) {
					LOGGER.error("Erro ao renderizar balões para jogador {}: {}", player.getName().getString(), e.getMessage());
				}
			}
		} catch (Exception e) {
			LOGGER.error("Erro geral na renderização de balões: {}", e.getMessage());
		}
	}

	public static void renderBalloons(PoseStack poseStack, EntityRenderDispatcher entityRenderDispatcher, Font font, List<String> messages, float playerHeight, Player player) {
		try {
			if (player.isInvisible() || !player.isAlive()) return;
			if (messages == null || messages.isEmpty()) return;

			poseStack.pushPose();

			// Posição relativa à câmera
			var cameraPosition = entityRenderDispatcher.camera.getPosition();
			double x = player.getX() - cameraPosition.x;
			double y = player.getY() + playerHeight + Config.balloonsHeightOffset - cameraPosition.y;
			double z = player.getZ() - cameraPosition.z;

			poseStack.translate(x, y, z);

			// Orientar para a câmera
			Quaternionf cameraRotation = entityRenderDispatcher.cameraOrientation();
			poseStack.mulPose(cameraRotation);

			// Escalar uniformemente
			float scale = 0.025f;
			poseStack.scale(-scale, -scale, scale);

			MultiBufferSource.BufferSource bufferSource = client.renderBuffers().bufferSource();

			// Renderizar múltiplas mensagens empilhadas
			int totalMessages = Math.min(messages.size(), Config.maxBalloons);
			int currentYOffset = 0;

			for (int i = 0; i < totalMessages; i++) {
				try {
					String message = messages.get(messages.size() - 1 - i); // Mais recente primeiro
					boolean isDistant = ChatBalloonManager.isDistantMessage(player.getUUID(), messages.size() - 1 - i);

					int balloonHeight = renderSingleBalloon(poseStack, bufferSource, font, message, currentYOffset, isDistant);
					currentYOffset += balloonHeight + 10; // Espaçamento entre balões
				} catch (Exception e) {
					LOGGER.error("Erro ao renderizar balão individual {}: {}", i, e.getMessage());
				}
			}

			bufferSource.endBatch();
			poseStack.popPose();
		} catch (Exception e) {
			LOGGER.error("Erro na renderização de balões: {}", e.getMessage());
			try {
				poseStack.popPose();
			} catch (Exception ignored) {
			}
		}
	}

	private static int renderSingleBalloon(PoseStack poseStack, MultiBufferSource bufferSource, Font font, String text, int yOffset, boolean isDistant) {
		try {
			if (text == null || text.trim().isEmpty()) return 0;

			// Quebrar texto em múltiplas linhas
			int maxTextWidth = Config.maxBalloonWidth - Config.balloonPadding * 2;
			List<String> lines = TextUtils.wrapText(text, font, maxTextWidth);

			if (lines.isEmpty()) return 0;

			// Calcular dimensões do balão
			int maxLineWidth = 0;
			for (String line : lines) {
				int lineWidth = font.width(line);
				if (lineWidth > maxLineWidth) {
					maxLineWidth = lineWidth;
				}
			}

			int balloonWidth = Math.max(maxLineWidth + Config.balloonPadding * 2, Config.minBalloonWidth);
			int balloonHeight = (font.lineHeight * lines.size()) + Config.balloonPadding * 2 + (lines.size() - 1) * 2; // +2 para espaçamento entre linhas

			// Centralizar balão
			int balloonX = -balloonWidth / 2;
			int balloonY = -balloonHeight - yOffset;

			poseStack.pushPose();

			// Cores configuráveis
			int backgroundColor = isDistant ? Config.distantBalloonColor : Config.balloonColor;
			int borderColor = Config.borderColor;
			int textColor = isDistant ? Config.distantTextColor : Config.textColor;

			// Renderizar balão com bordas arredondadas
			float cornerRadius = 3.0f;

			// Fundo arredondado
			RenderUtils.drawRoundedRect(poseStack, balloonX, balloonY, balloonWidth, balloonHeight, cornerRadius, backgroundColor);

			// Borda arredondada
			RenderUtils.drawRoundedBorder(poseStack, balloonX, balloonY, balloonWidth, balloonHeight, cornerRadius, Config.borderWidth, borderColor);

			// Desenhar pequeno "bico" do balão apontando para baixo
			float tipX = balloonX + balloonWidth / 2f;
			float tipY = balloonY + balloonHeight;
			RenderUtils.drawTriangle(poseStack,
					tipX - 4, tipY,  // Esquerda
					tipX + 4, tipY,  // Direita
					tipX, tipY + 6,  // Ponta
					backgroundColor);

			// Renderizar texto linha por linha
			Matrix4f matrix = poseStack.last().pose();
			float textX = balloonX + Config.balloonPadding;
			float baseTextY = balloonY + Config.balloonPadding;

			for (int i = 0; i < lines.size(); i++) {
				String line = lines.get(i);
				float textY = baseTextY + (i * (font.lineHeight + 2)); // +2 para espaçamento

				font.drawInBatch(line, textX, textY, textColor & 0x00FFFFFF, false, matrix, bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
			}

			poseStack.popPose();
			return balloonHeight + 6; // +6 para incluir o "bico"

		} catch (Exception e) {
			LOGGER.error("Erro na renderização de balão individual: {}", e.getMessage());
			try {
				poseStack.popPose();
			} catch (Exception ignored) {
			}
			return 0;
		}
	}
}