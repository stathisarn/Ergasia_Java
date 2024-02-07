package model;

import java.util.List;

public class CountriesInfo {
	private String name;
	private List<String> capital;
	private String currency;
	private String language;
	private int population;
	private List<String> continents;

	public CountriesInfo(String name, List<String> capital, String currency, String language, int population,
			List<String> continents) {
		this.name = name;
		this.capital = capital;
		this.currency = currency;
		this.language = language;
		this.population = population;
		this.continents = continents;
	}

	// Getters and setters for each field
	// ...

	public String getCommon() {
		return name;
	}

	public List<String> getCapital() {
		return capital;
	}

	public String getCurrency() {
		return currency;
	}

	public String getLanguage() {
		return language;
	}

	public long getPopulation() {
		return population;
	}

	public List<String> getContinent() {
		return continents;
	}

	@Override
	public String toString() {
		return "Info{" + "name='" + name + "'\n" + ", capital='" + capital + "'\n" + ", currency='" + currency + "'\n"
				+ ", language='" + language + "'\n" + ", population='" + population + "'\n" + ", continent='"
				+ continents + "'\n" + '}';
	}
}
