import org.junit.Assert;
import org.junit.Test;
import services.CountriesAPIService;
import exception.CountriesAPIException;
import model.CountriesInfo;

import java.util.List;

public class TestAPI {

    @Test
    public void testSearchForCountries() {
        CountriesAPIService countriesAPIService = new CountriesAPIService("https://restcountries.com/");

        try {
            List<CountriesInfo> countriesInfoList = countriesAPIService.searchForCountries("name", "usa");

            // Assert that the list is not null
            Assert.assertNotNull(countriesInfoList);

            // Assert that the list is not empty
            Assert.assertFalse(countriesInfoList.isEmpty());

            // Add assertions to verify specific properties of the CountriesInfo objects
            for (CountriesInfo countriesInfo : countriesInfoList) {
                Assert.assertNotNull(countriesInfo.getCommon()); // Check that common name is not null
                Assert.assertNotNull(countriesInfo.getCapital());
                Assert.assertNotNull(countriesInfo.getCurrency());
                Assert.assertNotNull(countriesInfo.getLanguage());
                Assert.assertNotNull(countriesInfo.getPopulation());
                Assert.assertNotNull(countriesInfo.getContinent());
                // Check that capital is not null
                // Add more assertions based on your specific requirements
            }
        } catch (CountriesAPIException e) {
            // Handle the exception or fail the test if an exception occurs during the API call
            Assert.fail("Exception occurred: " + e.getMessage());
        }
    }
}
