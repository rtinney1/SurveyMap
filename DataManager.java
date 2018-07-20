import java.awt.*;

public interface DataManager
{
	public void add(SurveyCall call);

	public void replace(SurveyCall call, int indexOfCall);
}