import java.util.LinkedList;
import java.util.List;

public class SearchMethodProfile {
	private String methodName;
	private List<SearchPerformance> performances;
	
	public SearchMethodProfile(String name) {
		this.methodName = name;
		performances = new LinkedList<SearchPerformance>();
	}
	
	
	
	public String getMethodName() {
		return methodName;
	}



	public List<SearchPerformance> getPerformances() {
		return performances;
	}



	public void addPerf(SearchPerformance sp) {
		performances.add(sp);
	}
}
