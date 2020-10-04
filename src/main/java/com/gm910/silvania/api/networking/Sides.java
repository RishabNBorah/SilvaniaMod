package com.gm910.silvania.api.networking;

import net.minecraftforge.fml.common.thread.SidedThreadGroups;

public class Sides {

	public static boolean guessIfLogicalClient() {
		return Thread.currentThread().getThreadGroup() == SidedThreadGroups.CLIENT;
	}
	
	public static boolean guessIfLogicalServer() {
		return Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER;
	}
}
