package com.cezarykluczynski.stapi.etl.character.creation.configuration

import com.cezarykluczynski.stapi.etl.character.creation.processor.CharacterReader
import com.cezarykluczynski.stapi.etl.common.configuration.AbstractCreationConfigurationTest
import com.cezarykluczynski.stapi.etl.common.service.JobCompletenessDecider
import com.cezarykluczynski.stapi.etl.util.constant.CategoryName
import com.cezarykluczynski.stapi.sources.mediawiki.api.CategoryApi
import com.cezarykluczynski.stapi.sources.mediawiki.api.enums.MediaWikiSource
import org.springframework.context.ApplicationContext

class CharacterCreationConfigurationTest extends AbstractCreationConfigurationTest {

	private static final String TITLE_INDIVIDUALS = 'TITLE_INDIVIDUALS'

	private ApplicationContext applicationContextMock

	private CategoryApi categoryApiMock

	private JobCompletenessDecider jobCompletenessDeciderMock

	private CharacterCreationConfiguration characterCreationConfiguration

	def setup() {
		applicationContextMock = Mock(ApplicationContext)
		categoryApiMock = Mock(CategoryApi)
		jobCompletenessDeciderMock = Mock(JobCompletenessDecider)
		characterCreationConfiguration = new CharacterCreationConfiguration(
				applicationContext: applicationContextMock,
				categoryApi: categoryApiMock,
				jobCompletenessDecider: jobCompletenessDeciderMock)
	}

	def "CharacterReader is created with all pages when step is not completed"() {
		when:
		CharacterReader characterReader = characterCreationConfiguration.characterReader()
		List<String> categoryHeaderTitleList = readerToList(characterReader)

		then:
		1 * jobCompletenessDeciderMock.isStepComplete(JobCompletenessDecider.STEP_004_CREATE_CHARACTERS) >> false
		1 * categoryApiMock.getPagesIncludingSubcategories(CategoryName.INDIVIDUALS, MediaWikiSource.MEMORY_ALPHA_EN) >> createListWithPageHeaderTitle(TITLE_INDIVIDUALS)
		1 * categoryApiMock.getPagesIncludingSubcategories(CategoryName.MILITARY_PERSONNEL, MediaWikiSource.MEMORY_ALPHA_EN) >> createListWithPageHeaderTitle(TITLE_INDIVIDUALS)
		1 * categoryApiMock.getPagesIncludingSubcategories(CategoryName.Q_CONTINUUM, MediaWikiSource.MEMORY_ALPHA_EN) >> createListWithPageHeaderTitle(TITLE_INDIVIDUALS)
		1 * categoryApiMock.getPagesIncludingSubcategories(CategoryName.STARFLEET_PERSONNEL, MediaWikiSource.MEMORY_ALPHA_EN) >> createListWithPageHeaderTitle(TITLE_INDIVIDUALS)
		0 * _
		categoryHeaderTitleList.contains TITLE_INDIVIDUALS
	}

	def "CharacterReader is created with no pages when step is completed"() {
		when:
		CharacterReader characterReader = characterCreationConfiguration.characterReader()
		List<String> categoryHeaderTitleList = readerToList(characterReader)

		then:
		1 * jobCompletenessDeciderMock.isStepComplete(JobCompletenessDecider.STEP_004_CREATE_CHARACTERS) >> true
		0 * _
		categoryHeaderTitleList.empty
	}

}