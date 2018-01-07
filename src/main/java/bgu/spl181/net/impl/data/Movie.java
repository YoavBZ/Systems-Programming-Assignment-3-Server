package bgu.spl181.net.impl.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Movie {

	private static AtomicInteger idGenerator = new AtomicInteger();

	@SerializedName("id")
	@Expose
	private String id;
	@SerializedName("name")
	@Expose
	private String name;
	@SerializedName("price")
	@Expose
	private String price;
	@SerializedName("bannedCountries")
	@Expose
	private List<String> bannedCountries;
	@SerializedName("availableAmount")
	@Expose
	private String availableAmount;
	@SerializedName("totalAmount")
	@Expose
	private String totalAmount;

	public Movie(String id, String name, String price, String availableAmount, List<String> bannedCountries) {
		this.id = id;
		this.name = name;
		this.price = price;
		this.availableAmount = availableAmount;
		this.bannedCountries = bannedCountries;
	}

	public int getId() {
		return Integer.parseInt(id);
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public String getQuotedName() {
		return '"' + name + '"';
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPrice() {
		return Integer.valueOf(price);
	}

	public void setPrice(int price) {
		this.price = Integer.toString(price);
	}

	public List<String> getBannedCountries() {
		return bannedCountries;
	}

	public void setBannedCountries(List<String> bannedCountries) {
		this.bannedCountries = bannedCountries;
	}

	public int getAvailableAmount() {
		return Integer.valueOf(availableAmount);
	}

	public int getTotalAmount() {
		return Integer.parseInt(totalAmount);
	}

	public void setTotalAmount(int totalAmount) {
		this.totalAmount = Integer.toString(totalAmount);
	}

	public void incAvailableAmount() {
		availableAmount = Integer.toString(Integer.valueOf(availableAmount) + 1);
	}

	public void decAvailableAmount() {
		availableAmount = Integer.toString(Integer.valueOf(availableAmount) - 1);
	}

	@Override
	public String toString() {
		String countries = String.join("\" \"", bannedCountries);
		return "\"" + name + "\" " + availableAmount + " " + price + " \"" + countries + "\"";
	}
}
