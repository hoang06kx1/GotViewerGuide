package co.k2lab.gotguide.model;

import java.util.ArrayList;

public class Season {
	private String name;
	private ArrayList<Episode> episodes;
	private int backgroundId;
	private boolean expanded = false;
	
	public Season(String name, ArrayList<Episode> episodes, int backgroundId) {
		this.episodes = episodes;
		this.name = name;
		this.backgroundId = backgroundId;
	}
	public ArrayList<Episode> getEpisodes() {
		return episodes;
	}
	public void setEpisodes(ArrayList<Episode> episodes) {
		this.episodes = episodes;
	}
	
	public int AiredEpisodesCount() {
		int count = 0;
		for (Episode epi:this.episodes) {
			if (epi.isAired()) {
				count++;
			}
		}
		return count;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getBackgroundId() {
		return backgroundId;
	}
	public void setBackgroundId(int backgroundId) {
		this.backgroundId = backgroundId;
	}
	public boolean isExpanded() {
		return expanded;
	}
	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}	
}
