package com.ldb.lms.service.professor_support;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.ldb.lms.dto.professor_support.PaginationDto;
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

	public void getCourses(Model model) {
		
		
	}

	public void calcPage(PaginationDto paginationDto) {
		
		Map<String, String> map = new HashMap<>();
		map.put("professorId", paginationDto.getProfessorId());
		map.put("search", paginationDto.getSearch());
		
		String pageParam = paginationDto.getPage();
		Integer currentPage = 
				(pageParam != null && !pageParam.isEmpty()) ? Integer.parseInt(pageParam) : 1;
		Integer offset = (currentPage - 1) * paginationDto.getItemsPerPage();
		Integer totalRows = professorCourseMapper.getCourseCountRows(map);
		Integer totalPages = (int) Math.ceil((double) totalRows / paginationDto.getItemsPerPage());
		
		paginationDto.setCurrentPage(currentPage);
		paginationDto.setTotalRows(totalRows);
		paginationDto.setTotalPages(totalPages);
		paginationDto.setOffset(offset);
		
		professorCourseMapper.searchCourseInfo(paginationDto);// 
	}
	
	
	


	

	

   

	
	
}
