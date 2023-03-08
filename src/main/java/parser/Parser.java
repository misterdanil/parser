package parser;

import java.util.List;

import model.Resource;
import model.Review;

public interface Parser {
	Resource getResource(String link);

	List<Resource> getResources(String link, int page);
	
	List<String> getLinks(String link, int page);

	List<Review> getReviews(String productId);
}
