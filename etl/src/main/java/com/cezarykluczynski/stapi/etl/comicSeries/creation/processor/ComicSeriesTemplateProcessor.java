package com.cezarykluczynski.stapi.etl.comicSeries.creation.processor;

import com.cezarykluczynski.stapi.etl.template.comicSeries.dto.ComicSeriesTemplate;
import com.cezarykluczynski.stapi.model.comicSeries.entity.ComicSeries;
import com.cezarykluczynski.stapi.model.common.service.GuidGenerator;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class ComicSeriesTemplateProcessor implements ItemProcessor<ComicSeriesTemplate, ComicSeries> {

	private GuidGenerator guidGenerator;

	@Inject
	public ComicSeriesTemplateProcessor(GuidGenerator guidGenerator) {
		this.guidGenerator = guidGenerator;
	}

	@Override
	public ComicSeries process(ComicSeriesTemplate item) throws Exception {
		ComicSeries comicSeries = new ComicSeries();

		comicSeries.setGuid(guidGenerator.generateFromPage(item.getPage(), ComicSeries.class));
		comicSeries.setPage(item.getPage());
		comicSeries.setTitle(item.getTitle());
		comicSeries.setPublishedYearFrom(item.getPublishedYearFrom());
		comicSeries.setPublishedMonthFrom(item.getPublishedMonthFrom());
		comicSeries.setPublishedDayFrom(item.getPublishedDayFrom());
		comicSeries.setPublishedYearTo(item.getPublishedYearTo());
		comicSeries.setPublishedMonthTo(item.getPublishedMonthTo());
		comicSeries.setPublishedDayTo(item.getPublishedDayTo());
		comicSeries.setNumberOfIssues(item.getNumberOfIssues());
		comicSeries.setStardateFrom(item.getStardateFrom());
		comicSeries.setStardateTo(item.getStardateTo());
		comicSeries.setYearFrom(item.getYearFrom());
		comicSeries.setYearTo(item.getYearTo());
		comicSeries.setMiniseries(item.getMiniseries());
		comicSeries.setPhotonovelSeries(item.getPhotonovelSeries());
		comicSeries.getPublishers().addAll(item.getPublishers());

		return comicSeries;

	}

}