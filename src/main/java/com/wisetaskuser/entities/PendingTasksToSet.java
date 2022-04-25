package com.wisetaskuser.entities;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import lombok.Data;
import lombok.NoArgsConstructor;

@Component
@Scope("prototype")
@NoArgsConstructor
@Data
public class PendingTasksToSet {

	@Autowired
	@Qualifier("pendingTasksList")
	private List<PendingTaskToSet> pendingTasksToSetList;
	
}
