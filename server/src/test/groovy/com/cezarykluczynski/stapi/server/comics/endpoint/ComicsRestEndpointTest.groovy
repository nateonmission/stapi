package com.cezarykluczynski.stapi.server.comics.endpoint

import com.cezarykluczynski.stapi.client.v1.rest.model.ComicsBaseResponse
import com.cezarykluczynski.stapi.client.v1.rest.model.ComicsFullResponse
import com.cezarykluczynski.stapi.server.comics.dto.ComicsRestBeanParams
import com.cezarykluczynski.stapi.server.comics.reader.ComicsRestReader
import com.cezarykluczynski.stapi.server.common.dto.PageSortBeanParams
import com.cezarykluczynski.stapi.server.common.endpoint.AbstractRestEndpointTest

class ComicsRestEndpointTest extends AbstractRestEndpointTest {

	private static final String GUID = 'GUID'
	private static final String TITLE = 'TITLE'

	private ComicsRestReader comicsRestReaderMock

	private ComicsRestEndpoint comicsRestEndpoint

	void setup() {
		comicsRestReaderMock = Mock(ComicsRestReader)
		comicsRestEndpoint = new ComicsRestEndpoint(comicsRestReaderMock)
	}

	void "passes get call to ComicsRestReader"() {
		given:
		ComicsFullResponse comicsFullResponse = Mock(ComicsFullResponse)

		when:
		ComicsFullResponse comicsFullResponseOutput = comicsRestEndpoint.getComics(GUID)

		then:
		1 * comicsRestReaderMock.readFull(GUID) >> comicsFullResponse
		comicsFullResponseOutput == comicsFullResponse
	}

	void "passes search get call to ComicsRestReader"() {
		given:
		PageSortBeanParams pageAwareBeanParams = Mock(PageSortBeanParams)
		pageAwareBeanParams.pageNumber >> PAGE_NUMBER
		pageAwareBeanParams.pageSize >> PAGE_SIZE
		ComicsBaseResponse comicsResponse = Mock(ComicsBaseResponse)

		when:
		ComicsBaseResponse comicsResponseOutput = comicsRestEndpoint.searchComics(pageAwareBeanParams)

		then:
		1 * comicsRestReaderMock.readBase(_ as ComicsRestBeanParams) >> { ComicsRestBeanParams comicsRestBeanParams ->
			assert pageAwareBeanParams.pageNumber == PAGE_NUMBER
			assert pageAwareBeanParams.pageSize == PAGE_SIZE
			comicsResponse
		}
		comicsResponseOutput == comicsResponse
	}

	void "passes search post call to ComicsRestReader"() {
		given:
		ComicsRestBeanParams comicsRestBeanParams = new ComicsRestBeanParams(title: TITLE)
		ComicsBaseResponse comicsResponse = Mock(ComicsBaseResponse)

		when:
		ComicsBaseResponse comicsResponseOutput = comicsRestEndpoint.searchComics(comicsRestBeanParams)

		then:
		1 * comicsRestReaderMock.readBase(comicsRestBeanParams as ComicsRestBeanParams) >> { ComicsRestBeanParams params ->
			assert params.title == TITLE
			comicsResponse
		}
		comicsResponseOutput == comicsResponse
	}

}