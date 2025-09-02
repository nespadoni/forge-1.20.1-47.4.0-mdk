package com.aurorionsmp.network;

import com.aurorionsmp.AurorionSMPMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkHandler {
	private static final String PROTOCOL_VERSION = "1";

	public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
			ResourceLocation.fromNamespaceAndPath(AurorionSMPMod.MODID, "main"),
			() -> PROTOCOL_VERSION,
			PROTOCOL_VERSION::equals,
			PROTOCOL_VERSION::equals
	);

	public static void register() {
		int id = 0;
		INSTANCE.messageBuilder(ChatMessagePacket.class, id++)
				.encoder(ChatMessagePacket::encode)
				.decoder(ChatMessagePacket::decode)
				.consumerMainThread(ChatMessagePacket::handle)
				.add();
	}
}