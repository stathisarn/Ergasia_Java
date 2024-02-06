package model;

import java.util.List;

public class CountriesInfo {
    private final String common;
    private final String capital;
    private final String currency;
    private final String language;
    private final long population;
    private final List<String> continent;
    

    public CountriesInfo(String common, String capital, String currency, String language, long population, List<String> continent) {
        this.common = common;
        this.capital = capital;
        this.currency = currency;
        this.language = language;
        this.population = population;
        this.continent = continent;
        
      
    }

    public String getCommon() {
        return common;
    }
    
    public String getCapital() {
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
        return continent;
    }



    @Override
    public String toString() {
        return "Info{" +
                "name='" + common + "'\n" +
                ", capital='" + capital + "'\n" +
                ", currency='" + currency + "'\n" +
                ", language='" + language + "'\n" +
                ", population='" + population + "'\n"+
                ", continent='" + continent + "'\n" +
                '}';
    }
}
