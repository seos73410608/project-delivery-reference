
import { backendApiClient } from "../../common/backend/backend-api.client.js";
import type { BackendApiAuthContext } from "../../common/backend/backend-api.types.js";
import type {
  ChangeOrganizationStatusQuery,
  CreateOrganizationRequest,
  OrganizationResponse,
  UpdateOrganizationRequest,
} from "../types/organization.types.js";

export class OrganizationService {
  async getOrganizations(
    accessToken: string,
    companyId: string,
    authContext?: BackendApiAuthContext,
  ): Promise<OrganizationResponse[] | null> {
    return backendApiClient.request<OrganizationResponse[]>({
      method: "GET",
      path: `/api/companies/${companyId}/organizations`,
      accessToken,
      ...(authContext ? { authContext } : {}),
    });
  }

  async getOrganization(
    accessToken: string,
    companyId: string,
    organizationId: string,
    authContext?: BackendApiAuthContext,
  ): Promise<OrganizationResponse | null> {
    return backendApiClient.request<OrganizationResponse>({
      method: "GET",
      path: `/api/companies/${companyId}/organizations/${organizationId}`,
      accessToken,
      ...(authContext ? { authContext } : {}),
    });
  }

  async createOrganization(
    accessToken: string,
    companyId: string,
    request: CreateOrganizationRequest,
    authContext?: BackendApiAuthContext,
  ): Promise<OrganizationResponse | null> {
    return backendApiClient.request<OrganizationResponse>({
      method: "POST",
      path: `/api/companies/${companyId}/organizations`,
      accessToken,
      ...(authContext ? { authContext } : {}),
      body: request,
    });
  }

  async updateOrganization(
    accessToken: string,
    companyId: string,
    organizationId: string,
    request: UpdateOrganizationRequest,
    authContext?: BackendApiAuthContext,
  ): Promise<OrganizationResponse | null> {
    return backendApiClient.request<OrganizationResponse>({
      method: "PUT",
      path: `/api/companies/${companyId}/organizations/${organizationId}`,
      accessToken,
      ...(authContext ? { authContext } : {}),
      body: request,
    });
  }

  async changeOrganizationStatus(
    accessToken: string,
    companyId: string,
    organizationId: string,
    query: ChangeOrganizationStatusQuery,
    authContext?: BackendApiAuthContext,
  ): Promise<OrganizationResponse | null> {
    return backendApiClient.request<OrganizationResponse>({
      method: "PATCH",
      path: `/api/companies/${companyId}/organizations/${organizationId}/status`,
      accessToken,
      ...(authContext ? { authContext } : {}),
      query: { ...query },
    });
  }
}

export const organizationService = new OrganizationService();
