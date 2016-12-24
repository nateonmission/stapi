package com.cezarykluczynski.stapi.etl.movie.creation.processor

import com.cezarykluczynski.stapi.etl.page.common.processor.PageHeaderProcessor
import com.cezarykluczynski.stapi.etl.template.movie.dto.MovieTemplate
import com.cezarykluczynski.stapi.etl.template.movie.processor.ToMovieTemplateProcessor
import com.cezarykluczynski.stapi.model.movie.entity.Movie
import com.cezarykluczynski.stapi.sources.mediawiki.dto.Page
import com.cezarykluczynski.stapi.sources.mediawiki.dto.PageHeader
import spock.lang.Specification

class MovieProcessorTest extends Specification {

	private PageHeaderProcessor pageHeaderProcessorMock

	private ToMovieTemplateProcessor toMovieTemplateProcessorMock

	private ToMovieEntityProcessor toMovieEntityProcessorMock

	private MovieProcessor episodeProcessor

	def setup() {
		pageHeaderProcessorMock = Mock(PageHeaderProcessor)
		toMovieTemplateProcessorMock = Mock(ToMovieTemplateProcessor)
		toMovieEntityProcessorMock = Mock(ToMovieEntityProcessor)
		episodeProcessor = new MovieProcessor(pageHeaderProcessorMock, toMovieTemplateProcessorMock,
				toMovieEntityProcessorMock)
	}

	def "converts PageHeader to Movie"() {
		given:
		PageHeader pageHeader = PageHeader.builder().build()
		Page page = new Page()
		MovieTemplate episodeTemplate = new MovieTemplate()
		Movie episode = new Movie()

		when:
		Movie outputMovie = episodeProcessor.process(pageHeader)

		then: 'processors are used in right order'
		1 * pageHeaderProcessorMock.process(pageHeader) >> page
		1 * toMovieTemplateProcessorMock.process(page) >> episodeTemplate
		1 * toMovieEntityProcessorMock.process(episodeTemplate) >> episode

		then: 'last processor output is returned'
		outputMovie == episode
	}

}