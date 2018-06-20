package collection;

import java.util.ArrayList;
import java.util.List;

public class SourcesCollection {

	private List<SourceNotificationCollection> sources;
	
	public SourcesCollection() {
		this.sources = new ArrayList<SourceNotificationCollection>();
	}
	
	public void setSources(List<SourceNotificationCollection> sources) {
		this.sources = sources;
	}
	
	public List<SourceNotificationCollection> getSources() {
		return this.sources;
	}
}
