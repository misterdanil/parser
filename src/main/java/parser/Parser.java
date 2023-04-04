package parser;

import java.util.List;

import model.Resource;
import model.Review;

public interface Parser {
	Resource getResource(String link);

	Resource getResource(Resource resource);

	List<Resource> getResources(String link, int page);

	List<Review> getReviews(Resource resource);

	void createWebDriver();

	void finishWebDriver();
}
