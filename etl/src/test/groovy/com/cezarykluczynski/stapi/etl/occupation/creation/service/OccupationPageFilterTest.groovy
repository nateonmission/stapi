package com.cezarykluczynski.stapi.etl.occupation.creation.service

import com.cezarykluczynski.stapi.etl.common.service.CategorySortingService
import com.cezarykluczynski.stapi.sources.mediawiki.dto.Page
import com.cezarykluczynski.stapi.sources.mediawiki.dto.PageHeader
import com.google.common.collect.Lists
import spock.lang.Specification

class OccupationPageFilterTest extends Specification {

	private CategorySortingService categorySortingServiceMock

	private OccupationPageFilter occupationPageFilter

	void setup() {
		categorySortingServiceMock = Mock()
		occupationPageFilter = new OccupationPageFilter(categorySortingServiceMock)
	}

	void "returns true when redirect path is not empty"() {
		given:
		PageHeader pageHeader = Mock()
		Page page = new Page(redirectPath: Lists.newArrayList(pageHeader))

		when:
		boolean shouldBeFilteredOut = occupationPageFilter.shouldBeFilteredOut(page)

		then:
		0 * _
		shouldBeFilteredOut
	}

	void "returns true when page is sorted on top of any category"() {
		given:
		Page page = new Page()

		when:
		boolean shouldBeFilteredOut = occupationPageFilter.shouldBeFilteredOut(page)

		then:
		1 * categorySortingServiceMock.isSortedOnTopOfAnyCategory(page) >> true
		0 * _
		shouldBeFilteredOut
	}

	void "returns false when redirect path is empty and page is not sorted on top of any category"() {
		given:
		Page page = new Page()

		when:
		boolean shouldBeFilteredOut = occupationPageFilter.shouldBeFilteredOut(page)

		then:
		1 * categorySortingServiceMock.isSortedOnTopOfAnyCategory(page) >> false
		0 * _
		!shouldBeFilteredOut
	}

}
