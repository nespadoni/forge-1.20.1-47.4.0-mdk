package com.aurorionsmp;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = AurorionSMPMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

	// Configurações do chat
	private static final ForgeConfigSpec.DoubleValue CHAT_RANGE = BUILDER
			.comment("Range para mensagens de chat em blocos")
			.defineInRange("chatRange", 20.0, 5.0, 100.0);

	// Configurações dos balões (baseadas no Talk Balloons)
	private static final ForgeConfigSpec.DoubleValue BALLOONS_HEIGHT_OFFSET = BUILDER
			.comment("Altura dos balões acima da cabeça do jogador")
			.defineInRange("balloonsHeightOffset", 0.9, 0.1, 3.0);

	private static final ForgeConfigSpec.IntValue DISTANCE_BETWEEN_BALLOONS = BUILDER
			.comment("Distância entre balões empilhados")
			.defineInRange("distanceBetweenBalloons", 3, 1, 10);

	private static final ForgeConfigSpec.IntValue MAX_BALLOONS = BUILDER
			.comment("Número máximo de balões por jogador")
			.defineInRange("maxBalloons", 7, 1, 20);

	private static final ForgeConfigSpec.IntValue MIN_BALLOON_WIDTH = BUILDER
			.comment("Largura mínima dos balões em pixels")
			.defineInRange("minBalloonWidth", 13, 5, 50);

	private static final ForgeConfigSpec.IntValue MAX_BALLOON_WIDTH = BUILDER
			.comment("Largura máxima dos balões em pixels")
			.defineInRange("maxBalloonWidth", 180, 50, 500);

	private static final ForgeConfigSpec.IntValue BALLOON_AGE = BUILDER
			.comment("Duração dos balões em segundos")
			.defineInRange("balloonAge", 15, 5, 60);

	private static final ForgeConfigSpec.IntValue TEXT_COLOR = BUILDER
			.comment("Cor do texto normal (formato RGB hex)")
			.defineInRange("textColor", 0xFFFFFF, 0x000000, 0xFFFFFF);

	private static final ForgeConfigSpec.IntValue DISTANT_TEXT_COLOR = BUILDER
			.comment("Cor do texto para mensagens distantes (formato RGB hex)")
			.defineInRange("distantTextColor", 0xFF6B6B, 0x000000, 0xFFFFFF);

	private static final ForgeConfigSpec.BooleanValue SHOW_BALLOONS = BUILDER
			.comment("Se deve mostrar balões de chat")
			.define("showBalloons", true);

	private static final ForgeConfigSpec.BooleanValue SHOW_OWN_BALLOON = BUILDER
			.comment("Se deve mostrar balões das próprias mensagens")
			.define("showOwnBalloon", true);

	private static final ForgeConfigSpec.ConfigValue<String> BALLOON_STYLE = BUILDER
			.comment("Estilo do balão: 'rounded', 'circular', 'squared'")
			.define("balloonStyle", "rounded");

	static final ForgeConfigSpec SPEC = BUILDER.build();

	// Variáveis públicas para acesso
	public static double chatRange;
	public static double balloonsHeightOffset;
	public static int distanceBetweenBalloons;
	public static int maxBalloons;
	public static int minBalloonWidth;
	public static int maxBalloonWidth;
	public static int balloonAge;
	public static int textColor;
	public static int distantTextColor;
	public static boolean showBalloons;
	public static boolean showOwnBalloon;
	public static String balloonStyle;

	// Valores legados para compatibilidade
	public static int normalTextColor;
	public static int balloonDuration;
	public static int balloonPadding = 6;
	public static int borderWidth = 1;

	@SubscribeEvent
	static void onLoad(final ModConfigEvent event) {
		chatRange = CHAT_RANGE.get();
		balloonsHeightOffset = BALLOONS_HEIGHT_OFFSET.get();
		distanceBetweenBalloons = DISTANCE_BETWEEN_BALLOONS.get();
		maxBalloons = MAX_BALLOONS.get();
		minBalloonWidth = MIN_BALLOON_WIDTH.get();
		maxBalloonWidth = MAX_BALLOON_WIDTH.get();
		balloonAge = BALLOON_AGE.get();
		textColor = TEXT_COLOR.get();
		distantTextColor = DISTANT_TEXT_COLOR.get();
		showBalloons = SHOW_BALLOONS.get();
		showOwnBalloon = SHOW_OWN_BALLOON.get();
		balloonStyle = BALLOON_STYLE.get();

		// Valores legados
		normalTextColor = textColor;
		balloonDuration = balloonAge * 20; // Converter para ticks
	}
}