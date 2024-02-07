package services;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import exception.CountriesAPIException;
import model.CountriesInfo;
import model.countriesdb.CountriesResult;
import model.countriesdb.Currencies;
import model.countriesdb.ErrorResponse;
import model.countriesdb.Eur;
import model.countriesdb.Languages;

public class CountriesAPIService {

    private final String API_URL;

    public CountriesAPIService(String baseUrl) {
        this.API_URL = baseUrl;
    }

    public List<CountriesInfo> searchForCountries(String apiFunction, String parameter) throws CountriesAPIException {
        try {
            URI url = getAPIData(apiFunction, parameter);
            return performApiRequest(url);
        } catch (URISyntaxException e) {
            throw new CountriesAPIException("Error constructing URI", e);
        }
    }

    public List<CountriesInfo> searchAllCountries() throws CountriesAPIException {
        return searchForCountries("all", null);
    }

    public List<CountriesInfo> searchCountriesByName(String name) throws CountriesAPIException {
        return searchForCountries("name", name);
    }

    public List<CountriesInfo> searchCountriesByLanguage(String language) throws CountriesAPIException {
        return searchForCountries("lang", language);
    }

    public List<CountriesInfo> searchCountriesByCurrency(String currency) throws CountriesAPIException {
        return searchForCountries("currency", currency);
    }
    

    private URI getAPIData(String apiFunction, String parameter) throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(API_URL).setPathSegments("v3.1");
        if (parameter != null && !parameter.isBlank()) {
            if ("lang".equals(apiFunction)) {
                uriBuilder.setPathSegments("v3.1", "lang", parameter);
            } else if ("name".equals(apiFunction)) {
                uriBuilder.setPathSegments("v3.1", "name", parameter);
            } else if ("currency".equals(apiFunction)) {
                uriBuilder.setPathSegments("v3.1", "currency", parameter);
            } else {
                uriBuilder.setPathSegments("v3.1", apiFunction, parameter);
            }
        } else {
            uriBuilder.setPathSegments("v3.1", apiFunction)
                      .addParameter("fields", "name,capital,currencies,continents,population,languages");
        }
        return uriBuilder.build();
    }

    private List<CountriesInfo> performApiRequest(URI url) throws CountriesAPIException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet getRequest = new HttpGet(url);
            try (CloseableHttpResponse response = httpClient.execute(getRequest)) {
                if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                    ErrorResponse errorResponse = readErrorResponse(response.getEntity().getContent());
                    throw new CountriesAPIException("Error occurred on API call: " + errorResponse.getStatusMessage());
                }
                ObjectMapper objectMapper = new ObjectMapper();
                InputStream inputStream = response.getEntity().getContent();
                CountriesResult[] countriesResults = objectMapper.readValue(inputStream, CountriesResult[].class);
                List<CountriesInfo> countriesInfoList = new ArrayList<>();
                for (CountriesResult countriesResult : countriesResults) {
                    CountriesInfo countriesInfo = convertToCountriesInfo(countriesResult);
                    countriesInfoList.add(countriesInfo);
                }
                return countriesInfoList;
            }
        } catch (IOException e) {
            throw new CountriesAPIException("Error during API request", e);
        }
    }

    private ErrorResponse readErrorResponse(InputStream inputStream) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(inputStream, ErrorResponse.class);
    }

    private CountriesInfo convertToCountriesInfo(CountriesResult countriesResult) {
        // Extract relevant information from CountriesResult and create a CountriesInfo object
        String name = countriesResult.getName().getCommon();
        List<String> capital = countriesResult.getCapital();
        int population = countriesResult.getPopulation();
        List<String> continents = countriesResult.getContinents();
        Languages languages = countriesResult.getLanguages(); // Retrieve the Languages object
        Currencies currencies = countriesResult.getCurrencies(); // Retrieve the Currencies object

        String currency = "Not available"; // Default value

        if (currencies != null) {
            // Check if EUR currency is available
            Eur eurCurrency = currencies.getEur();
            if (eurCurrency != null) {
                currency = eurCurrency.getName();
            } else {
                // If EUR is not available, retrieve all available currencies
                StringBuilder currencyBuilder = new StringBuilder();
                for (Map.Entry<String, Object> entry : currencies.getAdditionalProperties().entrySet()) {
                    if (entry.getValue() instanceof Map) {
                        Map<?, ?> currencyMap = (Map<?, ?>) entry.getValue();
                        currencyBuilder.append(currencyMap.get("name")).append(", ");
                    } else if (entry.getValue() instanceof String) {
                        currencyBuilder.append(entry.getValue()).append(", ");
                    }
                }
                // Remove the trailing comma and space
                currency = currencyBuilder.toString().replaceAll(", $", "");
            }
        }

        // Retrieve language using the getLanguage method
        String language = "Not available"; // Default value
        if (languages != null) {
            // Check if Catalan language is available
            if (languages.getCat() != null) {
                language = languages.getCat();
            } else if (languages.getSpa() != null) { // Check if Spanish is available
                language = languages.getSpa();
            } else {
                // If neither Catalan nor Spanish is available, retrieve the first available
                // language
                for (Map.Entry<String, Object> entry : languages.getAdditionalProperties().entrySet()) {
                    language = (String) entry.getValue();
                    break; // Stop after retrieving the first available language
                }
            }
        }

        return new CountriesInfo(name, capital, currency, language, population, continents);
    }
}
