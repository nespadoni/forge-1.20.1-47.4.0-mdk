package com.aurorionsmp.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;

public class RenderUtils {

	public static void drawRect(PoseStack poseStack, float x, float y, float width, float height, int color) {
		Matrix4f matrix = poseStack.last().pose();
		float alpha = (float) (color >> 24 & 255) / 255.0F;
		float red = (float) (color >> 16 & 255) / 255.0F;
		float green = (float) (color >> 8 & 255) / 255.0F;
		float blue = (float) (color & 255) / 255.0F;

		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShader(GameRenderer::getPositionColorShader);

		Tesselator tesselator = Tesselator.getInstance();
		BufferBuilder buffer = tesselator.getBuilder();

		buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
		buffer.vertex(matrix, x, y + height, 0.0F).color(red, green, blue, alpha).endVertex();
		buffer.vertex(matrix, x + width, y + height, 0.0F).color(red, green, blue, alpha).endVertex();
		buffer.vertex(matrix, x + width, y, 0.0F).color(red, green, blue, alpha).endVertex();
		buffer.vertex(matrix, x, y, 0.0F).color(red, green, blue, alpha).endVertex();

		tesselator.end();
		RenderSystem.disableBlend();
	}

	public static void drawRoundedRect(PoseStack poseStack, float x, float y, float width, float height, float radius, int color) {
		if (radius <= 0) {
			drawRect(poseStack, x, y, width, height, color);
			return;
		}

		// Limitar o raio para não ser maior que metade da menor dimensão
		float maxRadius = Math.min(width, height) / 2;
		radius = Math.min(radius, maxRadius);

		// Desenhar retângulo principal (centro)
		drawRect(poseStack, x + radius, y, width - 2 * radius, height, color);
		drawRect(poseStack, x, y + radius, width, height - 2 * radius, color);

		// Desenhar cantos arredondados (simulação com múltiplos retângulos pequenos)
		drawRoundedCorner(poseStack, x + radius, y + radius, radius, color, 0); // Top-left
		drawRoundedCorner(poseStack, x + width - radius, y + radius, radius, color, 1); // Top-right
		drawRoundedCorner(poseStack, x + radius, y + height - radius, radius, color, 2); // Bottom-left
		drawRoundedCorner(poseStack, x + width - radius, y + height - radius, radius, color, 3); // Bottom-right
	}

	private static void drawRoundedCorner(PoseStack poseStack, float centerX, float centerY, float radius, int color, int corner) {
		int segments = 8; // Quantos segmentos usar para simular o arco

		for (int i = 0; i < segments; i++) {
			float angle1 = (float) (corner * Math.PI / 2 + i * Math.PI / (2 * segments));
			float angle2 = (float) (corner * Math.PI / 2 + (i + 1) * Math.PI / (2 * segments));

			float x1 = centerX + (float) Math.cos(angle1) * radius;
			float y1 = centerY + (float) Math.sin(angle1) * radius;
			float x2 = centerX + (float) Math.cos(angle2) * radius;
			float y2 = centerY + (float) Math.sin(angle2) * radius;

			// Desenhar pequeno triângulo para simular o arco
			drawTriangle(poseStack, centerX, centerY, x1, y1, x2, y2, color);
		}
	}

	public static void drawTriangle(PoseStack poseStack, float x1, float y1, float x2, float y2, float x3, float y3, int color) {
		Matrix4f matrix = poseStack.last().pose();
		float alpha = (float) (color >> 24 & 255) / 255.0F;
		float red = (float) (color >> 16 & 255) / 255.0F;
		float green = (float) (color >> 8 & 255) / 255.0F;
		float blue = (float) (color & 255) / 255.0F;

		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShader(GameRenderer::getPositionColorShader);

		Tesselator tesselator = Tesselator.getInstance();
		BufferBuilder buffer = tesselator.getBuilder();

		buffer.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);
		buffer.vertex(matrix, x1, y1, 0.0F).color(red, green, blue, alpha).endVertex();
		buffer.vertex(matrix, x2, y2, 0.0F).color(red, green, blue, alpha).endVertex();
		buffer.vertex(matrix, x3, y3, 0.0F).color(red, green, blue, alpha).endVertex();

		tesselator.end();
		RenderSystem.disableBlend();
	}

	public static void drawRoundedBorder(PoseStack poseStack, float x, float y, float width, float height, float radius, int borderWidth, int color) {
		// Desenhar borda arredondada (versão simplificada)
		for (int i = 0; i < borderWidth; i++) {
			drawRoundedRectOutline(poseStack, x - i, y - i, width + 2 * i, height + 2 * i, radius + i, color);
		}
	}

	private static void drawRoundedRectOutline(PoseStack poseStack, float x, float y, float width, float height, float radius, int color) {
		// Top
		drawRect(poseStack, x + radius, y, width - 2 * radius, 1, color);
		// Bottom
		drawRect(poseStack, x + radius, y + height - 1, width - 2 * radius, 1, color);
		// Left
		drawRect(poseStack, x, y + radius, 1, height - 2 * radius, color);
		// Right
		drawRect(poseStack, x + width - 1, y + radius, 1, height - 2 * radius, color);
	}
}