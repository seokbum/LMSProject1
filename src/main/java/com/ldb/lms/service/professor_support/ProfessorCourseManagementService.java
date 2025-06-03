package com.ldb.lms.service.professor_support;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.ldb.lms.dto.professor_support.PaginationDto;
import com.ldb.lms.dto.professor_support.RegistCourseDto;
import com.ldb.lms.mapper.professor_support.ConvertDtoMapper;
import com.ldb.lms.mapper.professor_support.ProfessorCourseMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProfessorCourseManagementService {
	
	private final ProfessorCourseMapper professorCourseMapper;
	private final ConvertDtoMapper convertMapper;
	
	public ProfessorCourseManagementService(
			@Qualifier("convertDtoMapperImpl") ConvertDtoMapper convertMapper,
			ProfessorCourseMapper professorCourseMapper) {
		
		this.convertMapper = convertMapper;
		this.professorCourseMapper = professorCourseMapper;
	}

	public void calcPage(PaginationDto paginationDto) {
		
		Map<String, String> map = new HashMap<>();
		map.put("professorId", paginationDto.getProfessorId());
		map.put("search", paginationDto.getSearch());
		
		Integer itemPerPage = paginationDto.getItemsPerPage();
		Integer page = paginationDto.getPage();
		Integer totalRows = professorCourseMapper.getCourseCountRows(map);
		Integer totalPages = (int) Math.ceil((double) totalRows / itemPerPage);
		Integer startPage = ((page - 1) / itemPerPage) * itemPerPage + 1;
		Integer endPage = Math.min((startPage + itemPerPage -1), totalPages);
		Integer offset = (page - 1) * itemPerPage;
		
		paginationDto.setTotalRows(totalRows);
		paginationDto.setTotalPages(totalPages);
		paginationDto.setStartPage(startPage);
		paginationDto.setEndPage(endPage);
		paginationDto.setOffset(offset);
		log.info("paginationDto: {}", paginationDto.toString());
	}
	
	public List<RegistCourseDto> getCourses(PaginationDto paginationDto) {
		List<RegistCourseDto> courses = professorCourseMapper.searchCourseInfo(paginationDto);
		log.info("list: {}", courses);
		return courses;
	}
	


	

	

   

	
	
}
