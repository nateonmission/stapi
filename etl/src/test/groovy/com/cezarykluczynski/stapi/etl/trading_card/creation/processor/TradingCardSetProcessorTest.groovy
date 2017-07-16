package com.cezarykluczynski.stapi.etl.trading_card.creation.processor

import com.cezarykluczynski.stapi.etl.common.service.JsoupParser
import com.cezarykluczynski.stapi.etl.trading_card.creation.service.TradingCardSetFilter
import com.cezarykluczynski.stapi.model.common.service.UidGenerator
import com.cezarykluczynski.stapi.model.trading_card.entity.TradingCard
import com.cezarykluczynski.stapi.model.trading_card_set.entity.TradingCardSet
import com.cezarykluczynski.stapi.sources.wordpress.dto.Page
import com.google.common.collect.Sets
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import spock.lang.Specification

class TradingCardSetProcessorTest extends Specification {

	private static final Long ID = 1L
	private static final String RENDERED_TITLE = 'RENDERED_TITLE'
	private static final String RENDERED_CONTENT = 'RENDERED_CONTENT'
	private static final String UID = 'UID'

	private TradingCardSetFilter tradingCardSetFilterMock

	private JsoupParser jsoupParserMock

	private TradingCardSetTableProcessor tradingCardSetTableProcessorMock

	private TradingCardsTablesProcessor tradingCardsTablesProcessorMock

	private UidGenerator uidGeneratorMock

	private TradingCardSetProcessor tradingCardSetProcessor

	void setup() {
		tradingCardSetFilterMock = Mock()
		jsoupParserMock = Mock()
		tradingCardSetTableProcessorMock = Mock()
		tradingCardsTablesProcessorMock = Mock()
		uidGeneratorMock = Mock()
		tradingCardSetProcessor = new TradingCardSetProcessor(tradingCardSetFilterMock, jsoupParserMock, tradingCardSetTableProcessorMock,
				tradingCardsTablesProcessorMock, uidGeneratorMock)
	}

	void "returns null when TradingCardSetFilter returns true"() {
		given:
		Page page = Mock()

		when:
		TradingCardSet tradingCardSet = tradingCardSetProcessor.process(page)

		then:
		1 * tradingCardSetFilterMock.shouldBeFilteredOut(page) >> true
		0 * _
		tradingCardSet == null
	}

	void "when trading card set table is found, it is passed to TradingCardSetTableProcessor"() {
		given:
		Page page = new Page(
				id: ID,
				renderedTitle: RENDERED_TITLE,
				renderedContent: RENDERED_CONTENT)
		Document document = Mock()
		Element tradingCardSetTable = Mock()
		Elements tradingCardSetTableCandidates = new Elements(tradingCardSetTable)
		Elements tradingCardsTableCandidates = new Elements()
		TradingCardSet tradingCardSet = new TradingCardSet()

		when:
		TradingCardSet tradingCardSetOutput = tradingCardSetProcessor.process(page)

		then:
		1 * tradingCardSetFilterMock.shouldBeFilteredOut(page) >> false
		1 * jsoupParserMock.parse(RENDERED_CONTENT) >> document
		1 * document.getElementsByClass(TradingCardSetProcessor.TRADING_CARD_SET_TABLE_CLASS) >> tradingCardSetTableCandidates
		1 * document.getElementsByClass(TradingCardSetProcessor.TRADING_CARDS_TABLE_CLASS) >> tradingCardsTableCandidates
		1 * tradingCardSetTableProcessorMock.process(tradingCardSetTable) >> tradingCardSet
		1 * uidGeneratorMock.generateForTradingCardSet(ID) >> UID
		0 * _
		tradingCardSetOutput == tradingCardSet
		tradingCardSetOutput.name == RENDERED_TITLE
		tradingCardSetOutput.uid == UID
	}

	void "when trading card set table and trading cards tabled are found, and TradingCardsSet with cards is returned"() {
		given:
		Page page = new Page(
				id: ID,
				renderedTitle: RENDERED_TITLE,
				renderedContent: RENDERED_CONTENT)
		Document document = Mock()
		Element tradingCardSetTable = Mock()
		Element tradingCardsTable = Mock()
		Elements tradingCardSetTableCandidates = new Elements(tradingCardSetTable)
		Elements tradingCardsTableCandidates = new Elements(tradingCardsTable)
		TradingCardSet tradingCardSet = new TradingCardSet()
		TradingCard tradingCard1 = Mock()
		TradingCard tradingCard2 = Mock()

		when:
		TradingCardSet tradingCardSetOutput = tradingCardSetProcessor.process(page)

		then:
		1 * tradingCardSetFilterMock.shouldBeFilteredOut(page) >> false
		1 * jsoupParserMock.parse(RENDERED_CONTENT) >> document
		1 * document.getElementsByClass(TradingCardSetProcessor.TRADING_CARD_SET_TABLE_CLASS) >> tradingCardSetTableCandidates
		1 * document.getElementsByClass(TradingCardSetProcessor.TRADING_CARDS_TABLE_CLASS) >> tradingCardsTableCandidates
		1 * tradingCardSetTableProcessorMock.process(tradingCardSetTable) >> tradingCardSet
		1 * uidGeneratorMock.generateForTradingCardSet(ID) >> UID
		1 * tradingCardsTablesProcessorMock.process(tradingCardsTableCandidates) >> Sets.newHashSet(tradingCard1, tradingCard2)
		0 * _
		tradingCardSetOutput.tradingCards.size() == 2
		tradingCardSetOutput.tradingCards.contains tradingCard1
		tradingCardSetOutput.tradingCards.contains tradingCard2
	}

	void "when trading card set table is not found, but trading cards tabled is found, nothing happens"() {
		given:
		Page page = new Page(
				renderedTitle: RENDERED_TITLE,
				renderedContent: RENDERED_CONTENT)
		Document document = Mock()
		Element tradingCardsTable = Mock()
		Elements tradingCardSetTableCandidates = new Elements()
		Elements tradingCardsTableCandidates = new Elements(tradingCardsTable)

		when:
		TradingCardSet tradingCardSetOutput = tradingCardSetProcessor.process(page)

		then:
		1 * tradingCardSetFilterMock.shouldBeFilteredOut(page) >> false
		1 * jsoupParserMock.parse(RENDERED_CONTENT) >> document
		1 * document.getElementsByClass(TradingCardSetProcessor.TRADING_CARD_SET_TABLE_CLASS) >> tradingCardSetTableCandidates
		1 * document.getElementsByClass(TradingCardSetProcessor.TRADING_CARDS_TABLE_CLASS) >> tradingCardsTableCandidates
		0 * _
		tradingCardSetOutput == null
	}

}
