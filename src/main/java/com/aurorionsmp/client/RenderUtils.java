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
		// Desenhar retângulo principal
		drawRect(poseStack, x + radius, y, width - 2 * radius, height, color);
		drawRect(poseStack, x, y + radius, width, height - 2 * radius, color);

		// Desenhar cantos arredondados (simulados com retângulos menores)
		drawRect(poseStack, x + radius, y, radius, radius, color);
		drawRect(poseStack, x + width - radius, y, radius, radius, color);
		drawRect(poseStack, x, y + height - radius, radius, radius, color);
		drawRect(poseStack, x + width - radius, y + height - radius, radius, radius, color);
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
}