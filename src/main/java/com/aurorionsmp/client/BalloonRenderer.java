package com.aurorionsmp.client;

import com.aurorionsmp.Config;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class BalloonRenderer {
	private static final Logger LOGGER = LoggerFactory.getLogger(BalloonRenderer.class);
	private static final Minecraft client = Minecraft.getInstance();

	// Cache de posições para suavização
	private static final Map<UUID, Vector3f> lastRenderPositions = new HashMap<>();
	private static final Map<UUID, Long> lastUpdateTimes = new HashMap<>();

	// Configurações de suavização
	private static final float SMOOTHING_FACTOR = 0.7f;
	private static final long MAX_INTERPOLATION_TIME = 100; // ms

	public static void renderBalloons(PoseStack poseStack, EntityRenderDispatcher entityRenderDispatcher,
									  Font font, List<String> messages, float playerHeight, AbstractClientPlayer player) {
		try {
			if (player.isInvisible() || !player.isAlive()) return;
			if (messages == null || messages.isEmpty()) return;

			UUID playerId = player.getUUID();
			long currentTime = System.currentTimeMillis();

			// Calcular posição interpolada suave
			Vector3f currentPos = new Vector3f((float) player.getX(), (float) player.getY(), (float) player.getZ());
			Vector3f renderPos = currentPos;

			// Aplicar suavização adicional se houver posição anterior
			if (lastRenderPositions.containsKey(playerId)) {
				Long lastUpdateTime = lastUpdateTimes.get(playerId);
				if (lastUpdateTime != null && (currentTime - lastUpdateTime) < MAX_INTERPOLATION_TIME) {
					Vector3f lastPos = lastRenderPositions.get(playerId);
					renderPos = new Vector3f(lastPos);
					renderPos.lerp(currentPos, SMOOTHING_FACTOR);
				}
			}

			// Atualizar cache
			lastRenderPositions.put(playerId, new Vector3f(renderPos));
			lastUpdateTimes.put(playerId, currentTime);

			// Salvar estados atuais
			poseStack.pushPose();

			// **CONFIGURAÇÃO PARA BALÕES E TEXTO SEMPRE VISÍVEIS**
			RenderSystem.disableDepthTest();      // Desabilitar depth test completamente
			RenderSystem.enableBlend();           // Habilitar blending
			RenderSystem.defaultBlendFunc();      // Função de blend padrão

			// Posicionar acima da cabeça do jogador
			poseStack.translate(0.0, playerHeight + Config.balloonsHeightOffset, 0.0);

			// Orientar para a câmera
			Quaternionf cameraRotation = entityRenderDispatcher.cameraOrientation();
			poseStack.mulPose(cameraRotation);

			// Escalar uniformemente
			float scale = 0.025f;
			poseStack.scale(-scale, -scale, scale);

			// **RENDERIZAR PRIMEIRO OS FUNDOS DOS BALÕES**
			int totalMessages = Math.min(messages.size(), Config.maxBalloons);
			int currentYOffset = 0;

			// Lista para armazenar dados dos balões para renderizar texto depois
			List<BalloonTextData> textDataList = new ArrayList<>();

			for (int i = 0; i < totalMessages; i++) {
				try {
					String message = messages.get(messages.size() - 1 - i);
					boolean isDistant = ChatBalloonManager.isDistantMessage(player.getUUID(), messages.size() - 1 - i);

					BalloonTextData textData = renderBalloonBackground(poseStack, font, message, currentYOffset, isDistant);
					if (textData != null) {
						textDataList.add(textData);
						currentYOffset += textData.balloonHeight + 10;
					}
				} catch (Exception e) {
					LOGGER.error("Erro ao renderizar fundo do balão {}: {}", i, e.getMessage());
				}
			}

			// **GARANTIR QUE O TEXTO SEJA SEMPRE VISÍVEL**
			RenderSystem.disableCull();           // Desabilitar face culling
			RenderSystem.disableDepthTest();      // Garantir depth test desabilitado

			// **RENDERIZAR TODO O TEXTO POR ÚLTIMO** (sempre na frente)
			MultiBufferSource.BufferSource bufferSource = client.renderBuffers().bufferSource();

			for (BalloonTextData textData : textDataList) {
				renderBalloonText(poseStack, bufferSource, font, textData);
			}

			bufferSource.endBatch();
			poseStack.popPose();

			// **RESTAURAÇÃO COMPLETA DOS ESTADOS**
			RenderSystem.enableDepthTest();
			RenderSystem.enableCull();
			RenderSystem.disableBlend();

		} catch (Exception e) {
			LOGGER.error("Erro na renderização de balões: {}", e.getMessage());

			try {
				poseStack.popPose();
			} catch (Exception ignored) {
			}

			// Restaurar estados
			RenderSystem.enableDepthTest();
			RenderSystem.enableCull();
			RenderSystem.disableBlend();
		}
	}

	// Classe para armazenar dados do texto do balão
	private static class BalloonTextData {
		final List<String> lines;
		final int balloonWidth;
		final int balloonHeight;
		final int yOffset;
		final int textColor;

		BalloonTextData(List<String> lines, int balloonWidth, int balloonHeight, int yOffset, int textColor) {
			this.lines = lines;
			this.balloonWidth = balloonWidth;
			this.balloonHeight = balloonHeight;
			this.yOffset = yOffset;
			this.textColor = textColor;
		}
	}

	private static BalloonTextData renderBalloonBackground(PoseStack poseStack, Font font, String text, int yOffset, boolean isDistant) {
		try {
			List<String> lines = TextUtils.wrapText(text, font, Config.maxBalloonWidth - 12);
			if (lines.isEmpty()) return null;

			int maxWidth = 0;
			for (String line : lines) {
				maxWidth = Math.max(maxWidth, font.width(line));
			}

			int balloonWidth = Math.max(Math.min(maxWidth + 12, Config.maxBalloonWidth), Config.minBalloonWidth);
			int lineHeight = font.lineHeight + 1;
			int balloonHeight = (lines.size() * lineHeight) + 8;

			int balloonX = -(balloonWidth / 2);
			int balloonY = -yOffset - balloonHeight;

			poseStack.pushPose();

			int bgColor = isDistant ? Config.distantBalloonColor : Config.balloonColor;
			int textColor = isDistant ? Config.distantTextColor : Config.textColor;

			// Renderizar fundo do balão
			if (Config.balloonStyle.equals("rounded")) {
				RenderUtils.drawRoundedRect(poseStack, balloonX, balloonY, balloonWidth, balloonHeight, 4, bgColor);
				RenderUtils.drawRoundedBorder(poseStack, balloonX, balloonY, balloonWidth, balloonHeight, 4, Config.borderWidth, Config.borderColor);
			} else {
				RenderUtils.drawRect(poseStack, balloonX, balloonY, balloonWidth, balloonHeight, bgColor);
			}

			// Renderizar triângulo
			int triangleX = balloonX + (balloonWidth / 2);
			int triangleY = balloonY + balloonHeight;
			RenderUtils.drawTriangle(poseStack, triangleX - 4, triangleY, triangleX + 4, triangleY, triangleX, triangleY + 6, bgColor);

			poseStack.popPose();

			// Retornar dados para renderizar o texto depois
			return new BalloonTextData(lines, balloonWidth, balloonHeight, yOffset, textColor);
		} catch (Exception e) {
			LOGGER.error("Erro ao renderizar fundo do balão: {}", e.getMessage());
			return null;
		}
	}

	private static void renderBalloonText(PoseStack poseStack, MultiBufferSource bufferSource, Font font, BalloonTextData textData) {
		try {
			poseStack.pushPose();

			// **FORÇAR RENDERIZAÇÃO NA FRENTE DE TUDO**
			RenderSystem.disableDepthTest();

			int balloonX = -(textData.balloonWidth / 2);
			int balloonY = -textData.yOffset - textData.balloonHeight;
			int lineHeight = font.lineHeight + 1;

			Matrix4f matrix = poseStack.last().pose();

			// Renderizar cada linha de texto
			for (int i = 0; i < textData.lines.size(); i++) {
				String line = textData.lines.get(i);
				int textX = balloonX + (textData.balloonWidth - font.width(line)) / 2;
				int textY = balloonY + 4 + (i * lineHeight);

				// **USAR MODO SEE_THROUGH PARA GARANTIR VISIBILIDADE**
				font.drawInBatch(line, textX, textY, textData.textColor, false, matrix, bufferSource, Font.DisplayMode.SEE_THROUGH, 0, 15728880);
			}

			poseStack.popPose();
		} catch (Exception e) {
			LOGGER.error("Erro ao renderizar texto do balão: {}", e.getMessage());
		}
	}

	public static void cleanupCache() {
		try {
			lastRenderPositions.clear();
			lastUpdateTimes.clear();
		} catch (Exception e) {
			LOGGER.error("Erro ao limpar cache: {}", e.getMessage());
		}
	}
}