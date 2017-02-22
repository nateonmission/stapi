package com.cezarykluczynski.stapi.client.api;

import com.cezarykluczynski.stapi.client.v1.rest.api.AstronomicalObjectApi;
import com.cezarykluczynski.stapi.client.v1.rest.api.CharacterApi;
import com.cezarykluczynski.stapi.client.v1.rest.api.ComicSeriesApi;
import com.cezarykluczynski.stapi.client.v1.rest.api.ComicStripApi;
import com.cezarykluczynski.stapi.client.v1.rest.api.ComicsApi;
import com.cezarykluczynski.stapi.client.v1.rest.api.CompanyApi;
import com.cezarykluczynski.stapi.client.v1.rest.api.EpisodeApi;
import com.cezarykluczynski.stapi.client.v1.rest.api.MovieApi;
import com.cezarykluczynski.stapi.client.v1.rest.api.PerformerApi;
import com.cezarykluczynski.stapi.client.v1.rest.api.SeriesApi;
import com.cezarykluczynski.stapi.client.v1.rest.api.StaffApi;
import com.cezarykluczynski.stapi.client.v1.rest.invoker.ApiClient;
import lombok.Getter;

public class StapiRestClient extends AbstractStapiClient implements StapiClient {

	private String apiUrl;

	private ApiClient apiClient;

	@Getter
	private SeriesApi seriesApi;

	@Getter
	private PerformerApi performerApi;

	@Getter
	private StaffApi staffApi;

	@Getter
	private EpisodeApi episodeApi;

	@Getter
	private CharacterApi characterApi;

	@Getter
	private MovieApi movieApi;

	@Getter
	private AstronomicalObjectApi astronomicalObjectApi;

	@Getter
	private CompanyApi companyApi;

	@Getter
	private ComicSeriesApi comicSeriesApi;

	@Getter
	private ComicsApi comicsApi;

	@Getter
	private ComicStripApi comicStripApi;

	public StapiRestClient() {
		seriesApi = new SeriesApi();
		performerApi = new PerformerApi();
		staffApi = new StaffApi();
		characterApi = new CharacterApi();
		episodeApi = new EpisodeApi();
		movieApi = new MovieApi();
		astronomicalObjectApi = new AstronomicalObjectApi();
		companyApi = new CompanyApi();
		comicSeriesApi = new ComicSeriesApi();
		comicsApi = new ComicsApi();
		comicStripApi = new ComicStripApi();
	}

	public StapiRestClient(String apiUrl) {
		this.apiUrl = apiUrl;
		createApiClient();
		seriesApi = new SeriesApi(apiClient);
		performerApi = new PerformerApi(apiClient);
		staffApi = new StaffApi(apiClient);
		characterApi = new CharacterApi(apiClient);
		episodeApi = new EpisodeApi(apiClient);
		movieApi = new MovieApi(apiClient);
		astronomicalObjectApi = new AstronomicalObjectApi(apiClient);
		companyApi = new CompanyApi(apiClient);
		comicSeriesApi = new ComicSeriesApi(apiClient);
		comicsApi = new ComicsApi(apiClient);
		comicStripApi = new ComicStripApi(apiClient);
	}

	private void createApiClient() {
		apiClient = new ApiClient();
		apiClient.setBasePath(changeBaseUrl(apiUrl, apiClient.getBasePath()));
		apiClient.setConnectTimeout(10000);
	}

}
