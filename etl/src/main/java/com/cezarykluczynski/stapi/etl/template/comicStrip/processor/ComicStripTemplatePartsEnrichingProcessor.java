package com.cezarykluczynski.stapi.etl.template.comicStrip.processor;

import com.cezarykluczynski.stapi.etl.common.dto.EnrichablePair;
import com.cezarykluczynski.stapi.etl.common.dto.Range;
import com.cezarykluczynski.stapi.etl.common.processor.ItemEnrichingProcessor;
import com.cezarykluczynski.stapi.etl.common.processor.comicSeries.WikitextToComicSeriesProcessor;
import com.cezarykluczynski.stapi.etl.template.comicStrip.dto.ComicStripTemplate;
import com.cezarykluczynski.stapi.etl.template.comicStrip.dto.ComicStripTemplateParameter;
import com.cezarykluczynski.stapi.etl.template.common.dto.datetime.DayMonthYear;
import com.cezarykluczynski.stapi.etl.template.common.dto.datetime.YearRange;
import com.cezarykluczynski.stapi.etl.template.common.processor.DayMonthYearRangeProcessor;
import com.cezarykluczynski.stapi.etl.template.common.processor.datetime.WikitextToYearRangeProcessor;
import com.cezarykluczynski.stapi.sources.mediawiki.dto.Template;
import com.google.common.primitives.Ints;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

@Service
public class ComicStripTemplatePartsEnrichingProcessor implements ItemEnrichingProcessor<EnrichablePair<List<Template.Part>, ComicStripTemplate>> {

	private ComicStripTemplatePartStaffEnrichingProcessor comicStripTemplatePartStaffEnrichingProcessor;

	private WikitextToComicSeriesProcessor wikitextToComicSeriesProcessor;

	private DayMonthYearRangeProcessor dayMonthYearRangeProcessor;

	private ComicStripTemplateDayMonthYearRangeEnrichingProcessor comicStripTemplateDayMonthYearRangeEnrichingProcessor;

	private WikitextToYearRangeProcessor wikitextToYearRangeProcessor;

	@Inject
	public ComicStripTemplatePartsEnrichingProcessor(ComicStripTemplatePartStaffEnrichingProcessor comicStripTemplatePartStaffEnrichingProcessor,
			WikitextToComicSeriesProcessor wikitextToComicSeriesProcessor, DayMonthYearRangeProcessor dayMonthYearRangeProcessor,
			ComicStripTemplateDayMonthYearRangeEnrichingProcessor comicStripTemplateDayMonthYearRangeEnrichingProcessor,
			WikitextToYearRangeProcessor wikitextToYearRangeProcessor) {
		this.comicStripTemplatePartStaffEnrichingProcessor = comicStripTemplatePartStaffEnrichingProcessor;
		this.wikitextToComicSeriesProcessor = wikitextToComicSeriesProcessor;
		this.dayMonthYearRangeProcessor = dayMonthYearRangeProcessor;
		this.comicStripTemplateDayMonthYearRangeEnrichingProcessor = comicStripTemplateDayMonthYearRangeEnrichingProcessor;
		this.wikitextToYearRangeProcessor = wikitextToYearRangeProcessor;
	}

	@Override
	public void enrich(EnrichablePair<List<Template.Part>, ComicStripTemplate> enrichablePair) throws Exception {
		ComicStripTemplate comicStripTemplate = enrichablePair.getOutput();

		for (Template.Part part : enrichablePair.getInput()) {
			String key = part.getKey();
			String value = part.getValue();

			switch (key) {
				case ComicStripTemplateParameter.WRITER:
				case ComicStripTemplateParameter.ARTIST:
					comicStripTemplatePartStaffEnrichingProcessor.enrich(EnrichablePair.of(part, comicStripTemplate));
					break;
				case ComicStripTemplateParameter.PERIODICAL:
					comicStripTemplate.setPeriodical(value.replaceAll("'", ""));
					break;
				case ComicStripTemplateParameter.SERIES:
					comicStripTemplate.getComicSeries().addAll(wikitextToComicSeriesProcessor.process(value));
					break;
				case ComicStripTemplateParameter.PUBLISHED:
					Range<DayMonthYear> dayMonthYearRange = dayMonthYearRangeProcessor.process(part);
					if (dayMonthYearRange != null && (dayMonthYearRange.getFrom() != null || dayMonthYearRange.getTo() != null)) {
						comicStripTemplateDayMonthYearRangeEnrichingProcessor.enrich(EnrichablePair.of(dayMonthYearRange, comicStripTemplate));
					}
					break;
				case ComicStripTemplateParameter.PAGES:
					comicStripTemplate.setNumberOfPages(Ints.tryParse(value));
					break;
				case ComicStripTemplateParameter.YEAR:
					YearRange yearRange = wikitextToYearRangeProcessor.process(value);
					if (yearRange != null) {
						comicStripTemplate.setYearFrom(yearRange.getYearFrom());
						comicStripTemplate.setYearTo(yearRange.getYearTo());
					}
					break;
				default:
					break;
			}
		}
	}

}