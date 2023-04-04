package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.springframework.data.annotation.Transient;

import parser.Type;

public class Resource {
	private String id;
	private String link;
	private String name;
	private Double price;
	@Transient
	private List<String> images;
	@Transient
	private Document attributes;
	private List<Review> reviews;
	private Integer averageScore;
	private Type type;
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

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public List<String> getImages() {
		if (images == null) {
			images = new ArrayList<>();
		}
		return images;
	}

	public void setImages(List<String> images) {
		this.images = images;
	}

	public Object getAttribute(String key) {
		return attributes.get(key);
	}

	public boolean hasAttribute(String key) {
		return attributes.containsKey(key);
	}

	public void addAttribute(String key, Object value) {
		if (attributes == null) {
			attributes = new Document();
		}
		if (value != null) {
			attributes.put(key, value);
		}
	}

	public Document getAttributes() {
		return attributes;
	}

	public void setReviews(List<Review> reviews) {
		this.reviews = reviews;
	}

	public List<Review> getReviews() {
		return reviews;
	}

	public void addReviews(List<Review> reviews) {
		if (this.reviews == null) {
			this.reviews = new ArrayList<>();
		}
		this.reviews.addAll(reviews);
	}

	public Integer getAverageScore() {
		return averageScore;
	}

	public void setAverageScore(Integer averageScore) {
		this.averageScore = averageScore;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
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
		return "name=" + name + "\nbrand=" + attributes.get("brand") + "\nseries=" + attributes.get("series")
				+ "\ncolor=" + colors.toString() + "\n";

	}

}
