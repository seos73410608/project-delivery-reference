
package com.seos.pmis.task.dto.request;

import com.seos.pmis.task.entity.TaskStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TaskStatusChangeRequest {

    /**
     * 변경할 작업 상태
     */
    @NotNull(message = "변경할 작업 상태는 필수입니다.")
    private TaskStatus status;
}
