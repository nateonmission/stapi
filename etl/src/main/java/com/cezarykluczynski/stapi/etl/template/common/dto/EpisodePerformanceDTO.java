package com.cezarykluczynski.stapi.etl.template.common.dto;

import com.cezarykluczynski.stapi.etl.template.common.dto.enums.PerformanceType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class EpisodePerformanceDTO {

	private String performerName;

	private String performingFor;

	private String characterName;

	private PerformanceType performanceType;

}
