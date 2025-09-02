package com.aurorionsmp.client;

import com.aurorionsmp.AurorionSMPMod;
import com.aurorionsmp.Config;
import com.aurorionsmp.util.HistoricalData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;

@Mod.EventBusSubscriber(modid = AurorionSMPMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class BalloonRenderer {
	private static final ResourceLocation BALLOON_TEXTURE = ResourceLocation.fromNamespaceAndPath(AurorionSMPMod.MODID, "textures/gui/balloon.png");

	private static final Minecraft client = Minecraft.getInstance();

	@SubscribeEvent
	public static void onRenderLevel(RenderLevelStageEvent event) {
		if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;
		if (!Config.showBalloons) return;

		Minecraft mc = Minecraft.getInstance();
		if (mc.level == null || mc.player == null) return;

		// Renderizar balões para todos os jogadores
		for (Player player : mc.level.players()) {
			// Pular o próprio jogador em primeira pessoa
			if (player == mc.player && mc.options.getCameraType().isFirstPerson()) {
				continue;
			}

			HistoricalData<String> messages = ChatBalloonManager.getBalloonMessages(player.getUUID());
			if (messages != null && !messages.isEmpty()) {
				renderBalloons(event.getPoseStack(), mc.getEntityRenderDispatcher(), mc.font, messages, player.getBbHeight(), player);
			}
		}
	}

	public static void renderBalloons(PoseStack poseStack, EntityRenderDispatcher entityRenderDispatcher, Font font, HistoricalData<String> messages, float playerHeight, Player player) {
		poseStack.pushPose();

		// Calcular posição da câmera
		var cameraPosition = entityRenderDispatcher.camera.getPosition();
		double x = player.getX() - cameraPosition.x;
		double y = player.getY() + playerHeight + Config.balloonsHeightOffset - cameraPosition.y;
		double z = player.getZ() - cameraPosition.z;

		poseStack.translate(x, y, z);

		// Orientar para a câmera
		Quaternionf cameraRotation = entityRenderDispatcher.cameraOrientation();
		Vector3f eulerAngles = toEulerXyzDegrees(cameraRotation);
		poseStack.mulPose(Axis.YP.rotationDegrees(-eulerAngles.y));
		poseStack.mulPose(Axis.XP.rotationDegrees(eulerAngles.x));

		// Escalar apropriadamente
		poseStack.scale(-0.025f, -0.025f, 0.025f);

		MultiBufferSource.BufferSource bufferSource = client.renderBuffers().bufferSource();

		// Renderizar cada balão
		for (int i = 0; i < Math.min(messages.size(), Config.maxBalloons); i++) {
			String message = messages.get(i);
			boolean isDistant = ChatBalloonManager.isDistantMessage(player.getUUID(), i);

			renderSingleBalloon(poseStack, bufferSource, font, message, i * (Config.distanceBetweenBalloons + 15), isDistant);
		}

		bufferSource.endBatch();
		poseStack.popPose();
	}

	private static void renderSingleBalloon(PoseStack poseStack, MultiBufferSource bufferSource, Font font, String text, int yOffset, boolean isDistant) {
		// Quebrar texto em linhas
		List<FormattedCharSequence> lines = font.split(FormattedText.of(text), Config.maxBalloonWidth - 16);

		if (lines.isEmpty()) return;

		// Calcular dimensões
		int maxLineWidth = lines.stream().mapToInt(font::width).max().orElse(0);
		int balloonWidth = Math.max(maxLineWidth + 16, Config.minBalloonWidth);
		int balloonHeight = lines.size() * font.lineHeight + 12;

		int balloonX = -balloonWidth / 2;
		int balloonY = -balloonHeight - yOffset;

		poseStack.pushPose();

		// Renderizar fundo do balão usando RenderUtils (mais simples)
		if (isDistant) {
			RenderUtils.drawRoundedRect(poseStack, balloonX, balloonY, balloonWidth, balloonHeight, 4, 0xE0FFCCCC); // Rosa claro
		} else {
			RenderUtils.drawRoundedRect(poseStack, balloonX, balloonY, balloonWidth, balloonHeight, 4, 0xE0FFFFFF); // Branco
		}

		// Renderizar borda preta
		RenderUtils.drawRoundedRect(poseStack, balloonX - 1, balloonY - 1, balloonWidth + 2, balloonHeight + 2, 5, 0xFF000000); // Borda preta

		// Renderizar fundo novamente por cima da borda
		if (isDistant) {
			RenderUtils.drawRoundedRect(poseStack, balloonX, balloonY, balloonWidth, balloonHeight, 4, 0xE0FFCCCC);
		} else {
			RenderUtils.drawRoundedRect(poseStack, balloonX, balloonY, balloonWidth, balloonHeight, 4, 0xE0FFFFFF);
		}

		// Renderizar rabinho (apontando para o jogador)
		float tailX = balloonX + balloonWidth / 2.0f;
		float tailY = balloonY + balloonHeight;

		// Borda do rabinho
		RenderUtils.drawTriangle(poseStack,
				tailX - 5, tailY - 1,     // ponto esquerdo
				tailX + 5, tailY - 1,     // ponto direito
				tailX, tailY + 7,         // ponta
				0xFF000000);              // Preto

		// Rabinho interno
		RenderUtils.drawTriangle(poseStack,
				tailX - 4, tailY,         // ponto esquerdo
				tailX + 4, tailY,         // ponto direito
				tailX, tailY + 6,         // ponta
				isDistant ? 0xE0FFCCCC : 0xE0FFFFFF);

		// Renderizar texto
		int textColor = isDistant ? Config.distantTextColor : Config.normalTextColor;
		int textY = balloonY + 6;

		Matrix4f matrix = poseStack.last().pose();

		for (FormattedCharSequence line : lines) {
			int lineWidth = font.width(line);
			float textX = balloonX + (balloonWidth - lineWidth) / 2.0f;

			// Renderizar sombra do texto para melhor legibilidade
			font.drawInBatch(line, textX + 1, textY + 1, 0x000000, false, matrix, bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
			// Renderizar texto principal
			font.drawInBatch(line, textX, textY, textColor, false, matrix, bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);

			textY += font.lineHeight;
		}

		poseStack.popPose();
	}

	private static Vector3f toEulerXyz(Quaternionf quaternionf) {
		float f = quaternionf.w() * quaternionf.w();
		float g = quaternionf.x() * quaternionf.x();
		float h = quaternionf.y() * quaternionf.y();
		float i = quaternionf.z() * quaternionf.z();
		float j = f + g + h + i;
		float k = 2.0f * quaternionf.w() * quaternionf.x() - 2.0f * quaternionf.y() * quaternionf.z();
		float l = Math.abs(k) > 0.999999f ? 2.0f * (float) Math.atan2(quaternionf.y(), quaternionf.w()) : (float) Math.asin(Mth.clamp(k / j, -1.0f, 1.0f));
		return new Vector3f(l, (float) Math.atan2(2.0 * quaternionf.w() * quaternionf.y() + 2.0 * quaternionf.z() * quaternionf.x(), f - g - h + i), (float) Math.atan2(2.0 * quaternionf.w() * quaternionf.z() + 2.0 * quaternionf.x() * quaternionf.y(), f + g - h - i));
	}

	private static Vector3f toEulerXyzDegrees(Quaternionf quaternionf) {
		Vector3f vector3f = toEulerXyz(quaternionf);
		return new Vector3f((float) Math.toDegrees(vector3f.x()), (float) Math.toDegrees(vector3f.y()), (float) Math.toDegrees(vector3f.z()));
	}
}