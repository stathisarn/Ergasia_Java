package services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import exception.CountriesAPIException;
import model.CountriesInfo;
import model.countriesdb.ErrorResponse;

public class CountriesAPIService {

    private final String API_URL;

    public CountriesAPIService(String baseUrl) {
        this.API_URL = baseUrl;
    }

    public List<CountriesInfo> searchForCountries(String apiFunction, String parameter) throws CountriesAPIException {
        try {
            // Construct the appropriate URI based on the provided parameters
            URI url = getAPIData(apiFunction, parameter);

            // Perform the API request and retrieve the JSON response
            JsonNode jsonNode = performApiRequest(url);

            // Process the JsonNode and deserialize it into a list of CountriesInfo objects
            List<CountriesInfo> countriesInfoList = processJsonResponse(jsonNode);

            return countriesInfoList;
        } catch (URISyntaxException e) {
            throw new CountriesAPIException("Error constructing URI", e);
        }
    }

    private URI getAPIData(String apiFunction, String parameter) throws URISyntaxException {
        // Construct the base URI
        URIBuilder uriBuilder = new URIBuilder(API_URL).setPathSegments("v3.1");

        // Add parameter if not null or empty
        if (parameter != null && !parameter.isBlank()) {
            // Check if the apiFunction is "lang" to construct the URI accordingly
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
            // For the "all" case and other cases where parameter is null or empty
            uriBuilder.setPathSegments("v3.1", apiFunction)
                      .addParameter("fields", "name,capital,currencies,continents,population");
        }

        // Build and return the final URI
        return uriBuilder.build();
    }

    private JsonNode performApiRequest(URI url) throws CountriesAPIException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet getRequest = new HttpGet(url);

            try (CloseableHttpResponse response = httpClient.execute(getRequest)) {
                if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                    ErrorResponse errorResponse = readErrorResponse(response.getEntity().getContent());
                    throw new CountriesAPIException("Error occurred on API call: " + errorResponse.getStatusMessage());
                }

                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readTree(response.getEntity().getContent());
            }
        } catch (IOException e) {
            throw new CountriesAPIException("Error during API request", e);
        }
    }

    private List<CountriesInfo> processJsonResponse(JsonNode jsonNode) {
        List<CountriesInfo> countriesInfoList = new ArrayList<>();

        if (jsonNode != null && jsonNode.isArray()) {
            for (JsonNode countryNode : jsonNode) {
                JsonNode nameNode = countryNode.get("name");
                JsonNode capitalNode = countryNode.get("capital");
                JsonNode currenciesNode = countryNode.get("currencies");
                JsonNode languagesNode = countryNode.get("languages");
                JsonNode populationNode = countryNode.get("population");
                JsonNode continentsNode = countryNode.get("continents");

                // Check if any essential nodes are missing or null
                if (nameNode != null && capitalNode != null && currenciesNode != null && languagesNode != null &&
                        populationNode != null && continentsNode != null) {

                    String commonName = nameNode.get("common") != null ? nameNode.get("common").asText() : null;
                    String capital = capitalNode.isArray() && capitalNode.size() > 0 ? capitalNode.get(0).asText() : null;

                    // Extract currency information
                    String currency = extractCurrency(currenciesNode);

                    // Extract language information
                    String language = extractLanguage(languagesNode);

                    long population = populationNode.asLong();
                    List<String> continents = extractContinents(continentsNode);

                    CountriesInfo countriesInfo = new CountriesInfo(commonName, capital, currency, language, population, continents);
                    countriesInfoList.add(countriesInfo);

                    // Print the countries info
                    System.out.println(countriesInfo);
                } else {
                    // If any essential nodes are missing, log a warning or handle the situation accordingly
                    System.out.println("Warning: Essential node(s) missing in JSON response for a country.");
                }
            }
        } else {
            // If the JSON response is null or not an array, log a warning or handle the situation accordingly
            System.out.println("Warning: JSON response is null or not an array.");
        }

        return countriesInfoList;
    }

    private String extractCurrency(JsonNode currenciesNode) {
        String currency = null;
        if (currenciesNode != null && !currenciesNode.isEmpty()) {
            Iterator<Map.Entry<String, JsonNode>> currencyFields = currenciesNode.fields();
            if (currencyFields.hasNext()) {
                Map.Entry<String, JsonNode> currencyEntry = currencyFields.next();
                currency = currencyEntry.getValue().get("name").asText();
            }
        }
        return currency;
    }

    private String extractLanguage(JsonNode languagesNode) {
        String language = null;
        if (languagesNode != null && !languagesNode.isEmpty()) {
            Iterator<Map.Entry<String, JsonNode>> languageFields = languagesNode.fields();
            if (languageFields.hasNext()) {
                Map.Entry<String, JsonNode> languageEntry = languageFields.next();
                language = languageEntry.getValue().asText();
            }
        }
        return language;
    }

    private List<String> extractContinents(JsonNode continentsNode) {
        List<String> continents = new ArrayList<>();
        if (continentsNode != null && continentsNode.isArray()) {
            for (JsonNode continentNode : continentsNode) {
                continents.add(continentNode.asText());
            }
        }
        return continents;
    }

    private ErrorResponse readErrorResponse(InputStream inputStream) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(inputStream, ErrorResponse.class);
    }
}
