package bgu.spl181.net.impl;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Movie {

	private static AtomicInteger idGenerator = new AtomicInteger();
	private int id;
	private String name;
	private int copiesLeft;
	private int price;
	private List<String> bannedCountries;

	public Movie(String name, int copiesLeft, int price, List<String> bannedCountries) {
		this.id = idGenerator.incrementAndGet();
		this.name = name;
		this.copiesLeft = copiesLeft;
		this.price = price;
		this.bannedCountries = bannedCountries;
	}

	public String getName() {
		return name;
	}

	public int getCopiesLeft() {
		return copiesLeft;
	}

	public void incCopiesLeft() {
		this.copiesLeft += 1;
	}

	public void decCopiesLeft() {
		this.copiesLeft -= 1;
	}

	public int getPrice() {
		return price;
	}

	public List<String> getBannedCountries() {
		return bannedCountries;
	}

	@Override
	public String toString() {
		return name + " " + copiesLeft + " " + price + " " + bannedCountries;
	}

	public void setPrice(int price) {
		this.price = price;
	}
}
