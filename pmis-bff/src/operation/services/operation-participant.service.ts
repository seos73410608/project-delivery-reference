import { backendApiClient } from "../../common/backend/backend-api.client.js";
import type { BackendApiAuthContext } from "../../common/backend/backend-api.types.js";
import type {
  OperationParticipantResponse,
  OperationParticipantsQuery,
} from "../types/operation.types.js";

export class OperationParticipantService {
  async findHandoverEligibleParticipants(
    accessToken: string,
    operationId: string,
    query: OperationParticipantsQuery = {},
    authContext?: BackendApiAuthContext,
  ): Promise<OperationParticipantResponse[] | null> {
    const searchParams = new URLSearchParams();

    if (query.plannedHandoverDate) {
      searchParams.set("plannedHandoverDate", query.plannedHandoverDate);
    }

    const queryString = searchParams.toString();
    const path = `/api/operations/${operationId}/participants${
      queryString ? `?${queryString}` : ""
    }`;

    return backendApiClient.request<OperationParticipantResponse[]>({
      method: "GET",
      path,
      accessToken,
      ...(authContext ? { authContext } : {}),
    });
  }
}

export const operationParticipantService =
  new OperationParticipantService();