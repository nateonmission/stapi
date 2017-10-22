package com.cezarykluczynski.stapi.model.occupation.repository;

import com.cezarykluczynski.stapi.model.occupation.entity.Occupation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OccupationRepository extends JpaRepository<Occupation, Long>, OccupationRepositoryCustom {
}
