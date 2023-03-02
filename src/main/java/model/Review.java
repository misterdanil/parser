package model;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Review {
	private String sender;
	private String text;
	private String advantages;
	private String disadvantages;
	private Date date;
	private List<String> imageLinks;
	private Integer likesCount;
	private Integer dislikesCount;

	public Review(String sender, String text, Date date) {
		this.sender = sender;
		this.text = text;
		this.date = date;
	}

	public String getSender() {
		return sender;
	}

	public String getText() {
		return text;
	}

	public String getAdvantages() {
		return advantages;
	}

	public void setAdvantages(String advantages) {
		this.advantages = advantages;
	}

	public String getDisadvantages() {
		return disadvantages;
	}

	public void setDisadvantages(String disadvantages) {
		this.disadvantages = disadvantages;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public List<String> getImageLinks() {
		return imageLinks;
	}

	public void addImageLink(String link) {
		if (imageLinks == null) {
			imageLinks = new ArrayList<>();
		}
		imageLinks.add(link);
	}

	public void addImageLinks(List<String> links) {
		if (imageLinks == null) {
			imageLinks = new ArrayList<>();
		}
		imageLinks.addAll(links);
	}

	public Integer getLikesCount() {
		return likesCount;
	}

	public void setLikesCount(Integer likesCount) {
		this.likesCount = likesCount;
	}

	public Integer getDislikesCount() {
		return dislikesCount;
	}

	public void setDislikesCount(Integer dislikesCount) {
		this.dislikesCount = dislikesCount;
	}

}
