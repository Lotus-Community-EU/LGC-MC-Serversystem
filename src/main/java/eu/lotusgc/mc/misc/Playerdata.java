package eu.lotusgc.mc.misc;

public enum Playerdata {
	MinecraftUUID("mcuuid"),
	LotusHardID("id"),
	LotusChangeID("lgcid"),
	Clan("clan"),
	Name("name"),
	Nick("nick"),
	FirstJoin("firstJoin"),
	LastJoin("lastJoin"),
	CurrentLastServer("currentLastServer"),
	Playtime("playTime"),
	SideboardState("scoreboardState"),
	isOnline("isOnline"),
	isStaff("isStaff"),
	MoneyBank("money_bank"),
	MoneyPocket("money_pocket"),
	MoneyInterestLevel("money_interestLevel"),
	Language("language"),
	PlayerGroup("playerGroup"),
	CustomTimeFormat("customTimeFormat"),
	CustomDateFormat("customDateFormat"),
	TimeZone("timeZone"); //Will be determined automatically upon joining.
	
	public String playerData;
	
	Playerdata(String playerData) {
		this.playerData = playerData;
	}
	
	public String getColumnName() {
		return playerData;
	}
	
}
