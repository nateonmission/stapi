package com.cezarykluczynski.stapi.etl.configuration.job

import com.cezarykluczynski.stapi.etl.util.constant.JobName
import com.cezarykluczynski.stapi.etl.util.constant.StepName
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.job.flow.FlowJob
import org.springframework.batch.core.job.flow.support.SimpleFlow
import org.springframework.batch.core.job.flow.support.state.SplitState
import org.springframework.batch.core.repository.JobRepository
import org.springframework.context.ApplicationContext
import org.springframework.core.task.TaskExecutor
import spock.lang.Specification

class JobBuilderTest extends Specification {

	private ApplicationContext applicationContextMock

	private JobBuilderFactory jobBuilderFactoryMock

	private StepConfigurationValidator stepConfigurationValidatorMock

	private JobBuilder jobBuilder

	def setup() {
		applicationContextMock = Mock(ApplicationContext)
		jobBuilderFactoryMock = Mock(JobBuilderFactory)
		stepConfigurationValidatorMock = Mock(StepConfigurationValidator)
		jobBuilder = new JobBuilder(applicationContextMock, jobBuilderFactoryMock, stepConfigurationValidatorMock)
	}

	def "Job is built"() {
		given:
		Step createSeriesStep = Mock(Step)
		Step createPerformersStep = Mock(Step)
		Step createStaffStep = Mock(Step)
		Step createCharactersStep = Mock(Step)
		Step createEpisodesStep = Mock(Step)
		JobRepository jobRepository = Mock(JobRepository)
		org.springframework.batch.core.job.builder.JobBuilder jobBuilderMock =
				new org.springframework.batch.core.job.builder.JobBuilder(JobName.JOB_CREATE)
		jobBuilderMock.repository(jobRepository)
		TaskExecutor taskExecutor = Mock(TaskExecutor)

		when:
		FlowJob job = jobBuilder.build()

		then: 'validation is performed'
		1 * stepConfigurationValidatorMock.validate()

		then: 'jobCreate builder is retrieved'
		1 * jobBuilderFactoryMock.get(JobName.JOB_CREATE) >> jobBuilderMock

		then: 'CREATE_SERIES step is retrieved from application context'
		1 * applicationContextMock.getBean(StepName.CREATE_SERIES, Step) >> createSeriesStep
		1 * createSeriesStep.getName() >> ''

		then: 'CREATE_PERFORMERS step is retrieved from application context'
		1 * applicationContextMock.getBean(StepName.CREATE_PERFORMERS, Step) >> createPerformersStep
		1 * createPerformersStep.getName() >> ''

		then: 'CREATE_STAFF step is retrieved from application context'
		1 * applicationContextMock.getBean(StepName.CREATE_STAFF, Step) >> createStaffStep
		1 * createStaffStep.getName() >> ''

		then: 'CREATE_CHARACTERS step is retrieved from application context'
		1 * applicationContextMock.getBean(StepName.CREATE_CHARACTERS, Step) >> createCharactersStep
		1 * createCharactersStep.getName() >> ''

		then: 'CREATE_EPISODES step is retrieved from application context'
		1 * applicationContextMock.getBean(StepName.CREATE_EPISODES, Step) >> createEpisodesStep
		1 * createEpisodesStep.getName() >> ''

		then: 'Task executor is retrieved from application context'
		1 * applicationContextMock.getBean(TaskExecutor) >> taskExecutor

		then: 'no other interactions are expected'
		0 * _

		then: 'job is being returned'
		job.name == JobName.JOB_CREATE
		job.jobRepository == jobRepository
		((SplitState) ((SimpleFlow) job.flow).startState).flows.size() == 2
		((SplitState) ((SimpleFlow) job.flow).startState).flows[0].name == 'flow1'
		((SplitState) ((SimpleFlow) job.flow).startState).flows[1].name == 'flow2'
	}

}