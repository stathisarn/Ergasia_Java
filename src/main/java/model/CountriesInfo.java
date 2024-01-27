package model;


//class CountriesInfo and fields
public class CountriesInfo {
	private final String countryName;
	private final String countryCapital;
	private final String countryCurrencies;
	private final String countryPopulation;
	private final String countryContinents;
	
	
	//constructors
	public CountriesInfo(String countryName, String countryCapital, String countryCurrencies ,String countryPopulation, 
			String countryContinents) {
		this.countryName = countryName;
		this.countryCapital = countryCapital;
		this.countryCurrencies = countryCurrencies;
		this.countryPopulation = countryPopulation;
		this.countryContinents = countryContinents;
	
	}
	
	
	//getters
	public String getCoyntryName() {
		return countryName;
	}
	
	public String getCapital() {
		return countryCapital;
	}
	
	public String getCurrencies() {
		return countryCurrencies;
	}
	
	public String getPopulation() {
		return countryPopulation;
	}
	
	public String getContinents() {
		return countryContinents;
	}

	
	
	//text for output
	@Override
	public String toString() {
		return "MovieInfo{" +
	           "Name='" + countryName + "'\n" +
			   ", Capital='" + countryCapital + "'\n" +
	           ", Currency='" + countryCurrencies + "'\n" +
			   ", Population='" + countryPopulation + "'\n" +
			   ", Continent='" + countryContinents + "'\n" +
	           '}';
	}

	}


