import org.junit.Test;
import services.CountriesAPIService;
import exception.CountriesAPIException;
import model.CountriesInfo;
import static org.junit.Assert.*;
import java.util.List;


public class TestAPI {

    @Test
    public void testSearchAllCountries() throws CountriesAPIException {
        final CountriesAPIService countriesSearchService = CountriesAPI.getCountriesAPIService();
        final List<CountriesInfo> countriesInfoList = countriesSearchService.searchAllCountries();

        assertNotNull(countriesInfoList);
        assertFalse(countriesInfoList.isEmpty());
        countriesInfoList.forEach(System.out::println);
    }

    @Test
    public void testSearchCountriesByName() throws CountriesAPIException {
        final CountriesAPIService countriesSearchService = CountriesAPI.getCountriesAPIService();
        final List<CountriesInfo> countriesInfoList = countriesSearchService.searchCountriesByName("Germany");

        assertNotNull(countriesInfoList);
        assertFalse(countriesInfoList.isEmpty());
        countriesInfoList.forEach(System.out::println);
    }

    @Test
    public void testSearchCountriesByLanguage() throws CountriesAPIException {
        final CountriesAPIService countriesSearchService = CountriesAPI.getCountriesAPIService();
        final List<CountriesInfo> countriesInfoList = countriesSearchService.searchCountriesByLanguage("English");

        assertNotNull(countriesInfoList);
        assertFalse(countriesInfoList.isEmpty());
        countriesInfoList.forEach(System.out::println);
    }

    @Test
    public void testSearchCountriesByCurrency() throws CountriesAPIException {
        final CountriesAPIService countriesSearchService = CountriesAPI.getCountriesAPIService();
        final List<CountriesInfo> countriesInfoList = countriesSearchService.searchCountriesByCurrency("USD");

        assertNotNull(countriesInfoList);
        assertFalse(countriesInfoList.isEmpty());
        countriesInfoList.forEach(System.out::println);
    }
}
