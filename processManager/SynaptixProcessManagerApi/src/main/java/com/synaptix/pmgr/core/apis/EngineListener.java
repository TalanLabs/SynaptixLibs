package com.synaptix.pmgr.core.apis;

public interface EngineListener {
	public void notifyHandle(String channelName, Object message);

	public void notifyNewSlot(ChannelSlot slot);

	public void notifySlotPlug(ChannelSlot slot);

	public void notifySlotUnplug(ChannelSlot slot);

	public void notifySlotBuffering(ChannelSlot slot, Object message);

	public void notifySlotFlushing(ChannelSlot slot, Object message);

	public void notifySlotTrashing(ChannelSlot slot, Object message);
}
