package com.wisetaskuser.entities;

import java.sql.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * An entity class representing PendingTaskToSet.
 * @author Theofanis Gkoufas
 *
 */
@Data
@Builder
@AllArgsConstructor
public class PendingTaskToSet {

	private String moduleName;
	
	private String curriculum;
	
	private String assessmentType;
	
	private String assessmentWeeks;
	
	private int assessmentId;
	
	private Date week1BeginDate;
	
	private int assessmentWeight;
	
}