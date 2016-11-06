package com.cezarykluczynski.stapi.server.performer.endpoint

import com.cezarykluczynski.stapi.client.v1.rest.model.PerformerResponse
import com.cezarykluczynski.stapi.server.series.common.EndpointIntegrationTest

class PerformerRestEndpointIntegrationTest extends EndpointIntegrationTest {

	private static final Long ID = 100L

	def setup() {
		createRestClient()
	}

	def "gets first page of performers"() {
		given:
		Integer pageNumber = 0
		Integer pageSize = 10

		when:
		PerformerResponse performerResponse = stapiRestClient.performerApi.performerGet(pageNumber, pageSize)

		then:
		performerResponse.page.pageNumber == pageNumber
		performerResponse.page.pageSize == pageSize
		performerResponse.performers.size() == 10
	}

	def "gets the only person to star in 6 series"() {
		when:
		PerformerResponse performerResponse = stapiRestClient.performerApi.performerPost(null, null, null, null, null,
				null, null, null, null, null, null, null, true, true, null, null, null, true, true, true, null, null,
				true)

		then:
		performerResponse.page.totalElements == 1
		performerResponse.performers[0].name == "Majel Barrett-Roddenberry"
	}

	def "gets performer by id"() {
		when:
		PerformerResponse performerResponse = stapiRestClient.performerApi.performerPost(null, null, ID, null, null,
				null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
				null)

		then:
		performerResponse.page.totalElements == 1
		performerResponse.performers[0].id == ID
	}

}
