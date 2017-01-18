package com.cezarykluczynski.stapi.server.movie.query;

import com.cezarykluczynski.stapi.model.movie.dto.MovieRequestDTO;
import com.cezarykluczynski.stapi.model.movie.entity.Movie;
import com.cezarykluczynski.stapi.model.movie.repository.MovieRepository;
import com.cezarykluczynski.stapi.server.common.mapper.PageMapper;
import com.cezarykluczynski.stapi.server.movie.dto.MovieRestBeanParams;
import com.cezarykluczynski.stapi.server.movie.mapper.MovieRestMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class MovieRestQuery {

	private MovieRestMapper movieRestMapper;

	private PageMapper pageMapper;

	private MovieRepository movieRepository;

	@Inject
	public MovieRestQuery(MovieRestMapper movieRestMapper, PageMapper pageMapper,
			MovieRepository movieRepository) {
		this.movieRestMapper = movieRestMapper;
		this.pageMapper = pageMapper;
		this.movieRepository = movieRepository;
	}

	public Page<Movie> query(MovieRestBeanParams movieRestBeanParams) {
		MovieRequestDTO movieRequestDTO = movieRestMapper.map(movieRestBeanParams);
		PageRequest pageRequest = pageMapper.fromPageSortBeanParamsToPageRequest(movieRestBeanParams);
		return movieRepository.findMatching(movieRequestDTO, pageRequest);
	}

}