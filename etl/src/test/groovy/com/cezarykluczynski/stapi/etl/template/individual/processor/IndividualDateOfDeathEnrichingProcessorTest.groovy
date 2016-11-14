package com.cezarykluczynski.stapi.etl.template.individual.processor

import com.cezarykluczynski.stapi.etl.common.dto.EnrichablePair
import com.cezarykluczynski.stapi.etl.template.individual.dto.IndividualTemplate
import com.cezarykluczynski.stapi.sources.mediawiki.api.WikitextApi
import com.cezarykluczynski.stapi.sources.mediawiki.api.dto.PageLink
import com.cezarykluczynski.stapi.sources.mediawiki.dto.Template
import com.google.common.collect.Lists
import org.apache.commons.lang3.StringUtils
import spock.lang.Specification

class IndividualDateOfDeathEnrichingProcessorTest extends Specification {

	private static final String KIA_TITLE = "Casualties"
	private static final String KIA_DESCRIPTION = "kia"
	private static final String KIA = "[[${KIA_TITLE}|${KIA_DESCRIPTION}]] in some massacre"
	private static final String YEAR_STRING = "1960"
	private static final Integer YEAR_INTEGER = 1960
	private static final String DEAD_SYNONYM = IndividualDateOfDeathEnrichingProcessor.DEAD_SYNONYMS[0]
	private static final String NOT_DEAD_SYNONYM = IndividualDateOfDeathEnrichingProcessor.NOT_DEAD_SYNONYMS[0]
	private static final String NEITHER_WORD = 'Neither'

	private IndividualDateStatusValueToYearProcessor individualDateStatusValueToYearProcessorMock

	private WikitextApi wikitextApiMock

	private IndividualDateOfDeathEnrichingProcessor individualDateOfDeathEnrichingProcessor

	def setup() {
		individualDateStatusValueToYearProcessorMock = Mock(IndividualDateStatusValueToYearProcessor)
		wikitextApiMock = Mock(WikitextApi)
		individualDateOfDeathEnrichingProcessor = new IndividualDateOfDeathEnrichingProcessor(wikitextApiMock,
				individualDateStatusValueToYearProcessorMock)
	}

	def "does nothing to individual template when there is no parts"() {
		given:
		Template.Part templatePart = Mock(Template.Part)
		Template template = Mock(Template)
		IndividualTemplate individualTemplate = Mock(IndividualTemplate)

		when:
		individualDateOfDeathEnrichingProcessor.enrich(EnrichablePair.of(template, individualTemplate))

		then:
		1 * template.getParts() >> Lists.newArrayList(templatePart)
		1 * templatePart.getKey() >> ""
		0 * _
	}

	def "does nothing to individual template when there is only status part"() {
		given:
		Template.Part templatePart = Mock(Template.Part)
		Template template = Mock(Template)
		IndividualTemplate individualTemplate = Mock(IndividualTemplate)

		when:
		individualDateOfDeathEnrichingProcessor.enrich(EnrichablePair.of(template, individualTemplate))

		then:
		1 * template.getParts() >> Lists.newArrayList(templatePart)
		1 * templatePart.getKey() >> IndividualDateOfDeathEnrichingProcessor.STATUS
		0 * _
	}

	def "does nothing to individual template when there is only date status part"() {
		given:
		Template.Part templatePart = Mock(Template.Part)
		Template template = Mock(Template)
		IndividualTemplate individualTemplate = Mock(IndividualTemplate)

		when:
		individualDateOfDeathEnrichingProcessor.enrich(EnrichablePair.of(template, individualTemplate))

		then:
		1 * template.getParts() >> Lists.newArrayList(templatePart)
		1 * templatePart.getKey() >> IndividualDateOfDeathEnrichingProcessor.DATE_STATUS
		0 * _
	}

	def "does nothing to individual template when status part value is empty"() {
		given:
		Template.Part dateTemplatePart = Mock(Template.Part)
		Template.Part dateStatusTemplatePart = Mock(Template.Part)
		Template template = Mock(Template)
		IndividualTemplate individualTemplate = Mock(IndividualTemplate)

		when:
		individualDateOfDeathEnrichingProcessor.enrich(EnrichablePair.of(template, individualTemplate))

		then:
		1 * template.getParts() >> Lists.newArrayList(dateTemplatePart, dateStatusTemplatePart)
		1 * dateTemplatePart.getKey() >> IndividualDateOfDeathEnrichingProcessor.STATUS
		1 * dateTemplatePart.getValue() >> StringUtils.EMPTY
		1 * dateStatusTemplatePart.getKey() >> IndividualDateOfDeathEnrichingProcessor.DATE_STATUS
		0 * _
	}

	def "sets deceased flag to true when status contains 'kia'"() {
		given:
		Template.Part dateTemplatePart = Mock(Template.Part)
		Template.Part dateStatusTemplatePart = Mock(Template.Part)
		Template template = Mock(Template)
		IndividualTemplate individualTemplate = Mock(IndividualTemplate)

		when:
		individualDateOfDeathEnrichingProcessor.enrich(EnrichablePair.of(template, individualTemplate))

		then:
		1 * template.getParts() >> Lists.newArrayList(dateTemplatePart, dateStatusTemplatePart)
		1 * dateTemplatePart.getKey() >> IndividualDateOfDeathEnrichingProcessor.STATUS
		2 * dateTemplatePart.getValue() >> KIA
		1 * dateStatusTemplatePart.getKey() >> IndividualDateOfDeathEnrichingProcessor.DATE_STATUS
		1 * dateStatusTemplatePart.getValue() >> YEAR_STRING
		1 * wikitextApiMock.getPageLinksFromWikitext(KIA) >> Lists.newArrayList(
				new PageLink(
						title: KIA_TITLE,
						description: KIA_DESCRIPTION
				)
		)
		1 * individualTemplate.setDeceased(true)
		1 * individualDateStatusValueToYearProcessorMock.process(YEAR_STRING) >> YEAR_INTEGER
		1 * individualTemplate.setYearOfDeath(YEAR_INTEGER)
		0 * _
	}

	def "sets deceased flag to true when status contains dead word"() {
		given:
		Template.Part dateTemplatePart = Mock(Template.Part)
		Template.Part dateStatusTemplatePart = Mock(Template.Part)
		Template template = Mock(Template)
		IndividualTemplate individualTemplate = Mock(IndividualTemplate)

		when:
		individualDateOfDeathEnrichingProcessor.enrich(EnrichablePair.of(template, individualTemplate))

		then:
		1 * template.getParts() >> Lists.newArrayList(dateTemplatePart, dateStatusTemplatePart)
		1 * dateTemplatePart.getKey() >> IndividualDateOfDeathEnrichingProcessor.STATUS
		2 * dateTemplatePart.getValue() >> DEAD_SYNONYM
		1 * dateStatusTemplatePart.getKey() >> IndividualDateOfDeathEnrichingProcessor.DATE_STATUS
		1 * dateStatusTemplatePart.getValue() >> YEAR_STRING
		1 * wikitextApiMock.getPageLinksFromWikitext(DEAD_SYNONYM) >> Lists.newArrayList()
		1 * individualTemplate.setDeceased(true)
		1 * individualDateStatusValueToYearProcessorMock.process(YEAR_STRING) >> YEAR_INTEGER
		1 * individualTemplate.setYearOfDeath(YEAR_INTEGER)
		0 * _
	}

	def "does not set deceased flag to true when status contains not dead word"() {
		given:
		Template.Part dateTemplatePart = Mock(Template.Part)
		Template.Part dateStatusTemplatePart = Mock(Template.Part)
		Template template = Mock(Template)
		IndividualTemplate individualTemplate = Mock(IndividualTemplate)

		when:
		individualDateOfDeathEnrichingProcessor.enrich(EnrichablePair.of(template, individualTemplate))

		then:
		1 * template.getParts() >> Lists.newArrayList(dateTemplatePart, dateStatusTemplatePart)
		1 * dateTemplatePart.getKey() >> IndividualDateOfDeathEnrichingProcessor.STATUS
		2 * dateTemplatePart.getValue() >> NOT_DEAD_SYNONYM
		1 * dateStatusTemplatePart.getKey() >> IndividualDateOfDeathEnrichingProcessor.DATE_STATUS
		1 * wikitextApiMock.getPageLinksFromWikitext(NOT_DEAD_SYNONYM) >> Lists.newArrayList()
		0 * _
	}

	def "does not set deceased flag and logs when status contains neither dead nor not dead word"() {
		given:
		Template.Part dateTemplatePart = Mock(Template.Part)
		Template.Part dateStatusTemplatePart = Mock(Template.Part)
		Template template = Mock(Template)
		IndividualTemplate individualTemplate = Mock(IndividualTemplate)

		when:
		individualDateOfDeathEnrichingProcessor.enrich(EnrichablePair.of(template, individualTemplate))

		then:
		1 * template.getParts() >> Lists.newArrayList(dateTemplatePart, dateStatusTemplatePart)
		1 * dateTemplatePart.getKey() >> IndividualDateOfDeathEnrichingProcessor.STATUS
		2 * dateTemplatePart.getValue() >> NEITHER_WORD
		1 * dateStatusTemplatePart.getKey() >> IndividualDateOfDeathEnrichingProcessor.DATE_STATUS
		1 * wikitextApiMock.getPageLinksFromWikitext(NEITHER_WORD) >> Lists.newArrayList()
		1 * individualTemplate.getName()
		0 * _
	}

	def "does not set deceased flag and logs when status contains both dead word and not dead word"() {
		given:
		Template.Part dateTemplatePart = Mock(Template.Part)
		Template.Part dateStatusTemplatePart = Mock(Template.Part)
		Template template = Mock(Template)
		IndividualTemplate individualTemplate = Mock(IndividualTemplate)

		when:
		individualDateOfDeathEnrichingProcessor.enrich(EnrichablePair.of(template, individualTemplate))

		then:
		1 * template.getParts() >> Lists.newArrayList(dateTemplatePart, dateStatusTemplatePart)
		1 * dateTemplatePart.getKey() >> IndividualDateOfDeathEnrichingProcessor.STATUS
		2 * dateTemplatePart.getValue() >> DEAD_SYNONYM + " " + NOT_DEAD_SYNONYM
		1 * dateStatusTemplatePart.getKey() >> IndividualDateOfDeathEnrichingProcessor.DATE_STATUS
		1 * wikitextApiMock.getPageLinksFromWikitext(DEAD_SYNONYM + " " + NOT_DEAD_SYNONYM) >> Lists.newArrayList()
		1 * individualTemplate.getName()
		0 * _
	}

}
