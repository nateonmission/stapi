package com.cezarykluczynski.stapi.etl.template.movie.processor;

import com.cezarykluczynski.stapi.etl.common.dto.EnrichablePair;
import com.cezarykluczynski.stapi.etl.common.service.PageBindingService;
import com.cezarykluczynski.stapi.etl.template.movie.dto.MovieTemplate;
import com.cezarykluczynski.stapi.etl.template.service.TemplateFinder;
import com.cezarykluczynski.stapi.etl.util.TitleUtil;
import com.cezarykluczynski.stapi.sources.mediawiki.dto.Page;
import com.cezarykluczynski.stapi.sources.mediawiki.dto.Template;
import com.cezarykluczynski.stapi.util.constant.PageName;
import com.cezarykluczynski.stapi.util.constant.TemplateName;
import com.google.common.collect.Lists;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

@Service
public class ToMovieTemplateProcessor implements ItemProcessor<Page, MovieTemplate> {

	private static final List<String> IGNORABLE_TITLES = Lists.newArrayList(PageName.STAR_TREK_FILMS,
			PageName.STAR_TREK_XIV);

	private MovieTemplateProcessor movieTemplateProcessor;

	private TemplateFinder templateFinder;

	private PageBindingService pageBindingService;

	private MovieTemplateTitleLanguagesEnrichingProcessor movieTemplateTitleLanguagesEnrichingProcessor;

	@Inject
	public ToMovieTemplateProcessor(MovieTemplateProcessor movieTemplateProcessor, TemplateFinder templateFinder,
			PageBindingService pageBindingService,
			MovieTemplateTitleLanguagesEnrichingProcessor movieTemplateTitleLanguagesEnrichingProcessor) {
		this.movieTemplateProcessor = movieTemplateProcessor;
		this.templateFinder = templateFinder;
		this.pageBindingService = pageBindingService;
		this.movieTemplateTitleLanguagesEnrichingProcessor = movieTemplateTitleLanguagesEnrichingProcessor;
	}

	@Override
	public MovieTemplate process(Page item) throws Exception {
		Optional<Template> templateOptional = templateFinder.findTemplate(item, TemplateName.SIDEBAR_FILM);

		if (!isMoviePage(item) || !templateOptional.isPresent()) {
			return null;
		}

		MovieTemplate movieTemplate = movieTemplateProcessor.process(templateOptional.get());
		movieTemplate.setTitle(TitleUtil.getNameFromTitle(item.getTitle()));
		movieTemplate.setPage(pageBindingService.fromPageToPageEntity(item));

		movieTemplateTitleLanguagesEnrichingProcessor.enrich(EnrichablePair.of(item, movieTemplate));

		return movieTemplate;
	}

	private boolean isMoviePage(Page source) {
		return !IGNORABLE_TITLES.contains(source.getTitle());
	}

}