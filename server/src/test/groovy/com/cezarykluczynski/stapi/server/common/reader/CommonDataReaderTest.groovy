package com.cezarykluczynski.stapi.server.common.reader

import com.cezarykluczynski.stapi.server.common.dto.RestEndpointDetailsDTO
import com.cezarykluczynski.stapi.server.common.dto.RestEndpointStatisticsDTO
import spock.lang.Specification

class CommonDataReaderTest extends Specification {

	private CommonEntitiesStatisticsReader commonEntitiesStatisticsReaderMock

	private CommonEntitiesDetailsReader commonEntitiesDetailsReaderMock

	private CommonHitsStatisticsReader commonHitsStatisticsReaderMock

	private CommonDataReader commonDataReader

	void setup() {
		commonEntitiesStatisticsReaderMock = Mock()
		commonEntitiesDetailsReaderMock = Mock()
		commonHitsStatisticsReaderMock = Mock()
		commonDataReader = new CommonDataReader(commonEntitiesStatisticsReaderMock, commonEntitiesDetailsReaderMock, commonHitsStatisticsReaderMock)
	}

	void "gets entities statistics from CommonEntitiesStatisticsReader"() {
		given:
		RestEndpointStatisticsDTO restEndpointStatisticsDTO = Mock()

		when:
		RestEndpointStatisticsDTO restEndpointStatisticsDTOOutput = commonDataReader.entitiesStatistics()

		then:
		1 * commonEntitiesStatisticsReaderMock.entitiesStatistics() >> restEndpointStatisticsDTO
		0 * _
		restEndpointStatisticsDTOOutput == restEndpointStatisticsDTO
	}

	void "gets hits statistics from CommonHitsStatisticsReader"() {
		given:
		RestEndpointStatisticsDTO restEndpointStatisticsDTO = Mock()

		when:
		RestEndpointStatisticsDTO restEndpointStatisticsDTOOutput = commonDataReader.hitsStatistics()

		then:
		1 * commonHitsStatisticsReaderMock.hitsStatistics() >> restEndpointStatisticsDTO
		0 * _
		restEndpointStatisticsDTOOutput == restEndpointStatisticsDTO
	}

	void "gets details from CommonEntitiesDetailsReader"() {
		given:
		RestEndpointDetailsDTO restEndpointDetailsDTO = Mock()

		when:
		RestEndpointDetailsDTO restEndpointDetailsDTOOutput = commonDataReader.details()

		then:
		1 * commonEntitiesDetailsReaderMock.details() >> restEndpointDetailsDTO
		0 * _
		restEndpointDetailsDTOOutput == restEndpointDetailsDTO
	}

}
