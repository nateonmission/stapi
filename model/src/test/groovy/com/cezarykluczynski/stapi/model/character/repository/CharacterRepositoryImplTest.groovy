package com.cezarykluczynski.stapi.model.character.repository

import com.cezarykluczynski.stapi.model.character.dto.CharacterRequestDTO
import com.cezarykluczynski.stapi.model.character.entity.Character
import com.cezarykluczynski.stapi.model.character.entity.Character_
import com.cezarykluczynski.stapi.model.character.query.CharacterQueryBuilderFactory
import com.cezarykluczynski.stapi.model.common.dto.RequestOrderDTO
import com.cezarykluczynski.stapi.model.common.entity.enums.Gender
import com.cezarykluczynski.stapi.model.common.query.QueryBuilder
import com.cezarykluczynski.stapi.util.tool.LogicUtil
import com.google.common.collect.Lists
import com.google.common.collect.Sets
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import spock.lang.Specification

class CharacterRepositoryImplTest extends Specification {

	private static final String GUID = 'GUID'
	private static final String NAME = 'NAME'
	private static final Gender GENDER = Gender.F
	private static final Boolean DECEASED = LogicUtil.nextBoolean()
	private static final RequestOrderDTO ORDER = new RequestOrderDTO()

	private CharacterQueryBuilderFactory characterQueryBuilderMock

	private CharacterRepositoryImpl characterRepositoryImpl

	private QueryBuilder<Character> characterQueryBuilder

	private Pageable pageable

	private CharacterRequestDTO characterRequestDTO

	private Character character

	private Page page

	def setup() {
		characterQueryBuilderMock = Mock(CharacterQueryBuilderFactory)
		characterRepositoryImpl = new CharacterRepositoryImpl(characterQueryBuilderMock)
		characterQueryBuilder = Mock(QueryBuilder)
		pageable = Mock(Pageable)
		characterRequestDTO = Mock(CharacterRequestDTO)
		page = Mock(Page)
		character = Mock(Character)
	}

	def "query is built and performed"() {
		when:
		Page pageOutput = characterRepositoryImpl.findMatching(characterRequestDTO, pageable)

		then:
		1 * characterQueryBuilderMock.createQueryBuilder(pageable) >> characterQueryBuilder

		then: 'guid criteria is set'
		1 * characterRequestDTO.getGuid() >> GUID
		1 * characterQueryBuilder.equal(Character_.guid, GUID)

		then: 'string criteria are set'
		1 * characterRequestDTO.getName() >> NAME
		1 * characterQueryBuilder.like(Character_.name, NAME)

		then: 'enum criteria is set'
		1 * characterRequestDTO.getGender() >> GENDER
		1 * characterQueryBuilder.equal(Character_.gender, GENDER)

		then: 'boolean criteria are set'
		1 * characterRequestDTO.getDeceased() >> DECEASED
		1 * characterQueryBuilder.equal(Character_.deceased, DECEASED)

		then: 'order is set'
		1 * characterRequestDTO.getOrder() >> ORDER
		1 * characterQueryBuilder.setOrder(ORDER)

		then: 'fetch is performed with true flag'
		1 * characterQueryBuilder.fetch(Character_.performers, true)

		then: 'page is searched for and returned'
		1 * characterQueryBuilder.findPage() >> page
		0 * page.getContent()
		pageOutput == page

		then: 'no other interactions are expected'
		0 * _
	}

	def "proxies are cleared when no related entities should be fetched"() {
		when:
		Page pageOutput = characterRepositoryImpl.findMatching(characterRequestDTO, pageable)

		then:
		1 * characterQueryBuilderMock.createQueryBuilder(pageable) >> characterQueryBuilder

		then: 'guid criteria is set to null'
		1 * characterRequestDTO.getGuid() >> null

		then: 'fetch is performed with false flag'
		1 * characterQueryBuilder.fetch(Character_.performers, false)

		then: 'page is searched for and returned'
		1 * characterQueryBuilder.findPage() >> page

		then: 'proxies are cleared'
		1 * page.getContent() >> Lists.newArrayList(character)
		1 * character.setPerformers(Sets.newHashSet())
		pageOutput == page
	}

}
