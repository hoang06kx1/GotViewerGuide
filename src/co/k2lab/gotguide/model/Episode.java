package co.k2lab.gotguide.model;

public class Episode {
	private String name;
	private boolean newEpisose;
	private boolean aired;
	private String url;
	private int iconId;
	
	public Episode(String name, String url, int iconId, boolean isNewEpisode, boolean isAired) {
		this.name = name;
		this.newEpisose = isNewEpisode;
		this.url = url;
		this.aired = isAired;
		this.iconId = iconId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isNewEpisose() {
		return newEpisose;
	}

	public void setNewEpisose(boolean newEpisose) {
		this.newEpisose = newEpisose;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean isAired() {
		return aired;
	}

	public void setAired(boolean aired) {
		this.aired = aired;
	}

	public int getIconId() {
		return iconId;
	}

	public void setIconId(int iconId) {
		this.iconId = iconId;
	}
	
	
	

}
