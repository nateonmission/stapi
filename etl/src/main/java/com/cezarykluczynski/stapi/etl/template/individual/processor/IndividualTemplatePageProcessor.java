package com.cezarykluczynski.stapi.etl.template.individual.processor;

import com.cezarykluczynski.stapi.etl.common.dto.EnrichablePair;
import com.cezarykluczynski.stapi.etl.template.common.processor.AbstractTemplateProcessor;
import com.cezarykluczynski.stapi.etl.template.common.processor.gender.PartToGenderProcessor;
import com.cezarykluczynski.stapi.etl.template.individual.dto.IndividualLifeBoundaryDTO;
import com.cezarykluczynski.stapi.etl.template.individual.dto.IndividualTemplate;
import com.cezarykluczynski.stapi.etl.util.constant.CategoryName;
import com.cezarykluczynski.stapi.sources.mediawiki.api.WikitextApi;
import com.cezarykluczynski.stapi.sources.mediawiki.api.dto.PageLink;
import com.cezarykluczynski.stapi.sources.mediawiki.dto.CategoryHeader;
import com.cezarykluczynski.stapi.sources.mediawiki.dto.Page;
import com.cezarykluczynski.stapi.sources.mediawiki.dto.Template;
import com.cezarykluczynski.stapi.util.constant.TemplateName;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class IndividualTemplatePageProcessor extends AbstractTemplateProcessor
		implements ItemProcessor<Page, IndividualTemplate> {

	private static final String GENDER = "gender";
	private static final String ACTOR = "actor";
	private static final String HEIGHT = "height";
	private static final String WEIGHT = "weight";
	private static final String SERIAL_NUMBER = "serial number";
	private static final String BORN = "born";
	private static final String DIED = "died";
	private static final String MARITAL_STATUS = "marital_status";
	private static final String BLOOD_TYPE = "blood type";

	private PartToGenderProcessor partToGenderProcessor;

	private IndividualLifeBoundaryProcessor individualLifeBoundaryProcessor;

	private IndividualActorLinkingProcessor individualActorLinkingProcessor;

	private IndividualDateOfDeathEnrichingProcessor individualDateOfDeathEnrichingProcessor;

	private IndividualHeightProcessor individualHeightProcessor;

	private IndividualWeightProcessor individualWeightProcessor;

	private IndividualBloodTypeProcessor individualBloodTypeProcessor;

	private IndividualMaritalStatusProcessor individualMaritalStatusProcessor;

	private WikitextApi wikitextApi;

	@Inject
	public IndividualTemplatePageProcessor(PartToGenderProcessor partToGenderProcessor,
			IndividualLifeBoundaryProcessor individualLifeBoundaryProcessor,
			IndividualActorLinkingProcessor individualActorLinkingProcessor,
			IndividualDateOfDeathEnrichingProcessor individualDateOfDeathEnrichingProcessor,
			IndividualHeightProcessor individualHeightProcessor, IndividualWeightProcessor individualWeightProcessor,
			IndividualBloodTypeProcessor individualBloodTypeProcessor,
			IndividualMaritalStatusProcessor individualMaritalStatusProcessor, WikitextApi wikitextApi) {
		this.partToGenderProcessor = partToGenderProcessor;
		this.individualLifeBoundaryProcessor = individualLifeBoundaryProcessor;
		this.individualActorLinkingProcessor = individualActorLinkingProcessor;
		this.individualDateOfDeathEnrichingProcessor = individualDateOfDeathEnrichingProcessor;
		this.individualHeightProcessor = individualHeightProcessor;
		this.individualWeightProcessor = individualWeightProcessor;
		this.individualBloodTypeProcessor = individualBloodTypeProcessor;
		this.individualMaritalStatusProcessor = individualMaritalStatusProcessor;
		this.wikitextApi = wikitextApi;
	}

	@Override
	public IndividualTemplate process(Page item) throws Exception {
		if (shouldBeFilteredOut(item)) {
			return null;
		}

		Optional<Template> templateOptional = findTemplate(item, TemplateName.SIDEBAR_INDIVIDUAL);

		IndividualTemplate individualTemplate = new IndividualTemplate();
		individualTemplate.setName(StringUtils.trim(StringUtils.substringBefore(item.getTitle(), "(")));

		if (!templateOptional.isPresent()) {
			return individualTemplate;
		}

		Template template = templateOptional.get();

		individualDateOfDeathEnrichingProcessor.enrich(EnrichablePair.of(template, individualTemplate));

		for (Template.Part part : template.getParts()) {
			String key = part.getKey();
			String value = part.getValue();

			switch(key) {
				case GENDER:
					individualTemplate.setGender(partToGenderProcessor.process(part));
					break;
				case ACTOR:
					individualActorLinkingProcessor.enrich(EnrichablePair.of(part, individualTemplate));
					break;
				case HEIGHT:
					individualTemplate.setHeight(individualHeightProcessor.process(value));
					break;
				case WEIGHT:
					individualTemplate.setWeight(individualWeightProcessor.process(value));
					break;
				case SERIAL_NUMBER:
					if (StringUtils.isNotBlank(value)) {
						individualTemplate.setSerialNumber(value);
					}
					break;
				case BORN:
					IndividualLifeBoundaryDTO birthBoundaryDTO = individualLifeBoundaryProcessor
							.process(value);
					individualTemplate.setYearOfBirth(birthBoundaryDTO.getYear());
					individualTemplate.setMonthOfBirth(birthBoundaryDTO.getMonth());
					individualTemplate.setDayOfBirth(birthBoundaryDTO.getDay());
					individualTemplate.setPlaceOfBirth(birthBoundaryDTO.getPlace());
					break;
				case DIED:
					IndividualLifeBoundaryDTO deathBoundaryDTO = individualLifeBoundaryProcessor
							.process(value);
					individualTemplate.setYearOfDeath(deathBoundaryDTO.getYear());
					individualTemplate.setMonthOfDeath(deathBoundaryDTO.getMonth());
					individualTemplate.setDayOfDeath(deathBoundaryDTO.getDay());
					individualTemplate.setPlaceOfDeath(deathBoundaryDTO.getPlace());
					break;
				case MARITAL_STATUS:
					individualTemplate.setMaritalStatus(individualMaritalStatusProcessor.process(value));
					break;
				case BLOOD_TYPE:
					individualTemplate.setBloodType(individualBloodTypeProcessor.process(value));
					break;
			}
		}

		return individualTemplate;
	}

	private boolean shouldBeFilteredOut(Page item) {
		if (item.getTitle().startsWith("Unnamed ")) {
			return true;
		}

		List<String> categoryTitles = item.getCategories()
				.stream()
				.map(CategoryHeader::getTitle)
				.collect(Collectors.toList());

		if (categoryTitles.contains(CategoryName.PRODUCTION_LISTS) || categoryTitles.contains(CategoryName.LISTS)) {
			return true;
		}

		List<PageLink> pageLinkList = wikitextApi.getPageLinksFromWikitext(item.getWikitext())
				.stream()
				.filter(pageLink -> pageLink.getTitle().toLowerCase().startsWith("category:"))
				.filter(pageLink -> pageLink.getDescription() != null && pageLink.getDescription().length() == 0)
				.collect(Collectors.toList());

		if (!pageLinkList.isEmpty()) {
			return true;
		}

		return false;
	}

}
