package ust.hk.entity;

public class APIUsage {
	
	public String API;
	public String instance;
	public int sourceStart;
	public int sourceEnd;
	public int lineStart;
	public int lineEnd;
	public String enclosingStatement = " ";
	public String enclosingStatementContent = " ";
	public String enclosingStructure = " ";
	public int enclosingStatementStart;
	public int enclosingStatementEnd;
	public int enclosingStructureStart;
	public int enclosingStructureEnd;
	public String enclosingStructureContent = " ";
	
	public APIUsage(String API, String instance, int sourceStart, int sourceEnd, int lineStart, int lineEnd) {
		this.API = API;
		this.instance = instance;
		this.sourceStart = sourceStart;
		this.sourceEnd = sourceEnd;
		this.lineStart = lineStart;
		this.lineEnd = lineEnd;
	}	
	
	public String toString() {
		return instance.replaceAll("\n", " ") + "\t" + sourceStart + "\t" + sourceEnd + "\t" + lineStart + "\t" + lineEnd + "\t" 
				+ enclosingStatement + "::::" + enclosingStatementContent + "::::" + enclosingStatementStart + "::::" + enclosingStatementEnd + "::::" 
				+ enclosingStructure + "::::" + enclosingStructureContent + "::::" + enclosingStructureStart + "::::" + enclosingStructureEnd;
	}
	
	public void setEnclosingStatement(String enclosingStatement, String enclosingStatementContent, int start, int end) {
		this.enclosingStatement = enclosingStatement;
		this.enclosingStatementContent = enclosingStatementContent;
		this.enclosingStatementStart = start;
		this.enclosingStatementEnd = end;
	}
	
	public void setEnclosingStructure(String enclosingStructure, String enclosingStructureContent, int start, int end) {
		this.enclosingStructure = enclosingStructure;
		this.enclosingStructureContent = enclosingStructureContent;
		this.enclosingStructureStart = start;
		this.enclosingStructureEnd = end;
	}
	
	public static APIUsage readAPIInstance(String targetAPI, String line) {
		String[] split = line.split("\t");
		APIUsage usage = new APIUsage(targetAPI, split[0], Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]), Integer.parseInt(split[4]));
		split = split[5].split("::::");
		if (split.length >= 4)
			usage.setEnclosingStatement(split[0], split[1], Integer.parseInt(split[2]), Integer.parseInt(split[3]));
		if (split.length >= 8)
			usage.setEnclosingStructure(split[4], split[5], Integer.parseInt(split[6]), Integer.parseInt(split[7]));
		return usage;
	} 
}

