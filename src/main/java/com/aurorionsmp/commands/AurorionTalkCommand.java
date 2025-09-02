package com.aurorionsmp.commands;

import com.aurorionsmp.Config;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class AurorionTalkCommand {

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("auroriontalk")
				.then(Commands.literal("cor")
						.then(Commands.argument("tipo", StringArgumentType.string())
								.suggests((context, builder) -> {
									builder.suggest("balao");
									builder.suggest("distante");
									builder.suggest("texto");
									builder.suggest("borda");
									return builder.buildFuture();
								})
								.then(Commands.argument("cor", StringArgumentType.string())
										.suggests((context, builder) -> {
											// Sugerir cores predefinidas
											builder.suggest("branco");
											builder.suggest("preto");
											builder.suggest("vermelho");
											builder.suggest("verde");
											builder.suggest("azul");
											builder.suggest("amarelo");
											builder.suggest("dourado");
											builder.suggest("roxo");
											builder.suggest("rosa");
											builder.suggest("laranja");
											builder.suggest("marrom");
											builder.suggest("cinza");
											return builder.buildFuture();
										})
										.executes(AurorionTalkCommand::changeColor))))
				.then(Commands.literal("help")
						.executes(AurorionTalkCommand::help)));
	}

	private static int changeColor(CommandContext<CommandSourceStack> context) {
		try {
			CommandSourceStack source = context.getSource();

			String tipo = StringArgumentType.getString(context, "tipo").toLowerCase();
			String cor = StringArgumentType.getString(context, "cor");

			// Validar cor (predefinida ou hex)
			if (!isValidColor(cor)) {
				source.sendFailure(Component.literal("Cor inválida! Use uma cor predefinida ou formato #RRGGBB"));
				return 0;
			}

			// Declarar as variáveis como final
			final String finalCor = cor;
			String tipoNome = "";
			boolean sucesso = false;

			switch (tipo) {
				case "balao":
				case "balloon":
					Config.setBalloonColor(finalCor);
					tipoNome = "balão normal";
					sucesso = true;
					break;

				case "distante":
				case "distant":
					Config.setDistantBalloonColor(finalCor);
					tipoNome = "balão distante";
					sucesso = true;
					break;

				case "texto":
				case "text":
					Config.setTextColor(finalCor);
					tipoNome = "texto";
					sucesso = true;
					break;

				case "borda":
				case "border":
					Config.setBorderColor(finalCor);
					tipoNome = "borda";
					sucesso = true;
					break;

				default:
					source.sendFailure(Component.literal("Tipo inválido! Use: balao, distante, texto, borda"));
					return 0;
			}

			if (sucesso) {
				// Declarar como final para uso no lambda
				final String finalTipoNome = tipoNome;
				source.sendSuccess(() -> Component.literal("§aCor do " + finalTipoNome + " alterada para " + finalCor + "!"), true);
				return 1;
			}

		} catch (Exception e) {
			context.getSource().sendFailure(Component.literal("Erro ao alterar cor: " + e.getMessage()));
		}
		return 0;
	}

	private static boolean isValidColor(String color) {
		// Verificar cores predefinidas
		if (Config.isValidPredefinedColor(color)) {
			return true;
		}

		// Verificar formato hex
		try {
			String hexColor = color.startsWith("#") ? color.substring(1) : color;
			return hexColor.matches("^[0-9a-fA-F]{6}([0-9a-fA-F]{2})?$");
		} catch (Exception e) {
			return false;
		}
	}

	private static int help(CommandContext<CommandSourceStack> context) {
		CommandSourceStack source = context.getSource();

		source.sendSuccess(() -> Component.literal("§e§l=== Aurorion Talk - Ajuda ==="), false);
		source.sendSuccess(() -> Component.literal("§f/auroriontalk cor <tipo> <cor>"), false);
		source.sendSuccess(() -> Component.literal("  §7Tipos: balao, distante, texto, borda"), false);
		source.sendSuccess(() -> Component.literal(""), false);
		source.sendSuccess(() -> Component.literal("§fCores predefinidas:"), false);
		source.sendSuccess(() -> Component.literal("  §7branco, preto, vermelho, verde, azul"), false);
		source.sendSuccess(() -> Component.literal("  §7amarelo, dourado, roxo, rosa, laranja"), false);
		source.sendSuccess(() -> Component.literal("  §7marrom, cinza"), false);
		source.sendSuccess(() -> Component.literal(""), false);
		source.sendSuccess(() -> Component.literal("§fOu use formato hex: §7#RRGGBB (ex: #FF0000)"), false);
		source.sendSuccess(() -> Component.literal(""), false);
		source.sendSuccess(() -> Component.literal("§fExemplos:"), false);
		source.sendSuccess(() -> Component.literal("  §7/auroriontalk cor balao branco"), false);
		source.sendSuccess(() -> Component.literal("  §7/auroriontalk cor texto dourado"), false);
		source.sendSuccess(() -> Component.literal("  §7/auroriontalk cor balao #80FF0000 (transparente)"), false);

		return 1;
	}
}