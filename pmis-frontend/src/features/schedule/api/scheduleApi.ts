import client from '@/api/client';

import type {
ScheduleCreateRequest,
ScheduleResponse,
ScheduleUpdateRequest,
} from '../types/schedule';

const SCHEDULE_API = '/schedules';
const PROJECT_API = '/projects';

/**

* 프로젝트 Schedule 목록 조회
*
* GET /api/projects/{projectId}/schedules
*
* Backend Response:
* [
* {
* ```
  id,
  ```
* ```
  projectId,
  ```
* ```
  wbsId,
  ```
* ```
  scheduleName,
  ```
* ```
  startDate,
  ```
* ```
  endDate,
  ```
* ```
  status,
  ```
* ```
  sortOrder,
  ```
* ```
  description,
  ```
* ```
  createdAt,
  ```
* ```
  updatedAt
  ```
* }
* ]
  */
  export const getProjectSchedules = async (
  projectId: number,
  ): Promise<ScheduleResponse[]> => {
  const response = await client.get<ScheduleResponse[]>(
  `${PROJECT_API}/${projectId}/schedules`,
  );

return response.data;
};

/**

* Schedule 단건 조회
*
* GET /api/schedules/{id}
*
* Backend Response:
* {
* id,
* projectId,
* wbsId,
* scheduleName,
* startDate,
* endDate,
* status,
* sortOrder,
* description,
* createdAt,
* updatedAt
* }
  */
  export const getSchedule = async (
  scheduleId: number,
  ): Promise<ScheduleResponse> => {
  const response = await client.get<ScheduleResponse>(
  `${SCHEDULE_API}/${scheduleId}`,
  );

return response.data;
};

/**

* Schedule 생성
*
* POST /api/projects/{projectId}/schedules
  */
  export const createSchedule = async (
  projectId: number,
  request: ScheduleCreateRequest,
  ): Promise<ScheduleResponse> => {
  const response = await client.post<ScheduleResponse>(
  `${PROJECT_API}/${projectId}/schedules`,
  request,
  );

return response.data;
};

/**

* Schedule 수정
*
* PUT /api/schedules/{id}
  */
  export const updateSchedule = async (
  scheduleId: number,
  request: ScheduleUpdateRequest,
  ): Promise<ScheduleResponse> => {
  const response = await client.put<ScheduleResponse>(
  `${SCHEDULE_API}/${scheduleId}`,
  request,
  );

return response.data;
};

/**

* Schedule 삭제
*
* DELETE /api/schedules/{id}
  */
  export const deleteSchedule = async (
  scheduleId: number,
  ): Promise<void> => {
  await client.delete(
  `${SCHEDULE_API}/${scheduleId}`,
  );
  };
