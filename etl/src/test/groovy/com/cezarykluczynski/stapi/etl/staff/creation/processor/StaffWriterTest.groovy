package com.cezarykluczynski.stapi.etl.staff.creation.processor

import com.cezarykluczynski.stapi.model.page.entity.Page
import com.cezarykluczynski.stapi.model.page.service.DuplicateFilteringPreSavePageAwareFilter
import com.cezarykluczynski.stapi.model.page.service.DuplicateReattachingPreSavePageAwareFilter
import com.cezarykluczynski.stapi.model.staff.entity.Staff
import com.cezarykluczynski.stapi.model.staff.repository.StaffRepository
import com.google.common.collect.Lists
import spock.lang.Specification

class StaffWriterTest extends Specification {

	private static final Long PAGE_ID = 1L

	private StaffRepository staffRepositoryMock

	private DuplicateFilteringPreSavePageAwareFilter duplicateFilteringPreSavePageAwareProcessorMock

	private DuplicateReattachingPreSavePageAwareFilter duplicateReattachingPreSavePageAwareFilterMock

	private StaffWriter staffWriterMock

	def setup() {
		staffRepositoryMock = Mock(StaffRepository)
		duplicateFilteringPreSavePageAwareProcessorMock = Mock(DuplicateFilteringPreSavePageAwareFilter)
		duplicateReattachingPreSavePageAwareFilterMock = Mock(DuplicateReattachingPreSavePageAwareFilter)
		staffWriterMock = new StaffWriter(staffRepositoryMock, duplicateFilteringPreSavePageAwareProcessorMock,
				duplicateReattachingPreSavePageAwareFilterMock)
	}

	def "filters duplicates, then writes all entities using repository"() {
		given:
		Staff staff = new Staff(page: new Page(pageId: PAGE_ID))
		List<Staff> seriesList = Lists.newArrayList(staff)

		when:
		staffWriterMock.write(seriesList)

		then:
		1 * duplicateFilteringPreSavePageAwareProcessorMock.process(_, Staff) >> { args ->
			assert args[0][0] == staff
			return seriesList
		}
		1 * duplicateReattachingPreSavePageAwareFilterMock.process(_, Staff) >> { args ->
			assert args[0][0] == staff
			return seriesList
		}
		1 * duplicateFilteringPreSavePageAwareProcessorMock.process(_, Staff) >> { args ->
			assert args[0][0] == staff
			return seriesList
		}
		1 * staffRepositoryMock.save(seriesList)
		0 * _
	}

}