package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Resource {
	private String id;
	private String link;
	private String name;
	private Map<String, String> attributes;
	private List<Review> reviews;
	private Integer averageScore;
	private Map<String, String> colors;

	public Resource(String id, String link, String name) {
		this.id = id;
		this.link = link;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public String getLink() {
		return link;
	}

	public String getName() {
		return name;
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public boolean hasAttribute(String key) {
		return attributes.containsKey(key);
	}

	public void addAttribute(String key, String value) {
		if (attributes == null) {
			attributes = new HashMap<>();
		}
		if (value != null) {
			attributes.put(key, value);
		}
	}

	public List<Review> getReviews() {
		return reviews;
	}

	public void addReview(Review review) {
		reviews.add(review);
	}

	public Integer getAverageScore() {
		return averageScore;
	}

	public void setAverageScore(Integer averageScore) {
		this.averageScore = averageScore;
	}

	public Map<String, String> getColors() {
		if (colors == null) {
			colors = new HashMap<>();
		}
		return colors;
	}

	public void setColors(Map<String, String> colors) {
		this.colors = colors;
	}

	@Override
	public String toString() {
		return "name=" + name + "\nbrand=" + attributes.get("brand") + "\nmodel=" + attributes.get("model") + "\ncolor="
				+ colors.toString() + "\n";

	}

}
