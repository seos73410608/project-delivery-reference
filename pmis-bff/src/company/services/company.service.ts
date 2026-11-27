import { backendApiClient } from "../../common/backend/backend-api.client.js";
import type { BackendApiAuthContext } from "../../common/backend/backend-api.types.js";
import type {
  ChangeCompanyStatusQuery,
  CloseCompanyRoleAssignmentRequest,
  CompanyResponse,
  CompanyRoleAssignmentResponse,
  CreateCompanyRequest,
  CreateCompanyRoleAssignmentRequest,
  PageCompanyResponse,
  SearchCompaniesQuery,
  UpdateCompanyRequest,
} from "../types/company.types.js";

export class CompanyService {
  async createCompany(
    accessToken: string,
    request: CreateCompanyRequest,
    authContext?: BackendApiAuthContext,
  ): Promise<CompanyResponse | null> {
    return backendApiClient.request<CompanyResponse>({
      method: "POST",
      path: "/api/companies",
      accessToken,
      ...(authContext ? { authContext } : {}),
      body: request,
    });
  }

  async searchCompanies(
    accessToken: string,
    query: SearchCompaniesQuery,
    authContext?: BackendApiAuthContext,
  ): Promise<PageCompanyResponse | null> {
    return backendApiClient.request<PageCompanyResponse>({
      method: "GET",
      path: "/api/companies",
      accessToken,
      ...(authContext ? { authContext } : {}),
      query: { ...query },
    });
  }

  async getCompany(
    accessToken: string,
    companyId: string,
    authContext?: BackendApiAuthContext,
  ): Promise<CompanyResponse | null> {
    return backendApiClient.request<CompanyResponse>({
      method: "GET",
      path: `/api/companies/${companyId}`,
      accessToken,
      ...(authContext ? { authContext } : {}),
    });
  }

  async updateCompany(
    accessToken: string,
    companyId: string,
    request: UpdateCompanyRequest,
    authContext?: BackendApiAuthContext,
  ): Promise<CompanyResponse | null> {
    return backendApiClient.request<CompanyResponse>({
      method: "PUT",
      path: `/api/companies/${companyId}`,
      accessToken,
      ...(authContext ? { authContext } : {}),
      body: request,
    });
  }

  async changeCompanyStatus(
    accessToken: string,
    companyId: string,
    query: ChangeCompanyStatusQuery,
    authContext?: BackendApiAuthContext,
  ): Promise<CompanyResponse | null> {
    return backendApiClient.request<CompanyResponse>({
      method: "PATCH",
      path: `/api/companies/${companyId}/status`,
      accessToken,
      ...(authContext ? { authContext } : {}),
      query: { ...query },
    });
  }

  async getCompanyRoles(
    accessToken: string,
    companyId: string,
    authContext?: BackendApiAuthContext,
  ): Promise<CompanyRoleAssignmentResponse[] | null> {
    return backendApiClient.request<CompanyRoleAssignmentResponse[]>({
      method: "GET",
      path: `/api/companies/${companyId}/roles`,
      accessToken,
      ...(authContext ? { authContext } : {}),
    });
  }

  async createCompanyRoleAssignment(
    accessToken: string,
    companyId: string,
    request: CreateCompanyRoleAssignmentRequest,
    authContext?: BackendApiAuthContext,
  ): Promise<CompanyRoleAssignmentResponse | null> {
    return backendApiClient.request<CompanyRoleAssignmentResponse>({
      method: "POST",
      path: `/api/companies/${companyId}/roles`,
      accessToken,
      ...(authContext ? { authContext } : {}),
      body: request,
    });
  }

  async closeCompanyRoleAssignment(
    accessToken: string,
    companyId: string,
    assignmentId: string,
    request: CloseCompanyRoleAssignmentRequest,
    authContext?: BackendApiAuthContext,
  ): Promise<CompanyRoleAssignmentResponse | null> {
    return backendApiClient.request<CompanyRoleAssignmentResponse>({
      method: "PATCH",
      path: `/api/companies/${companyId}/roles/${assignmentId}/close`,
      accessToken,
      ...(authContext ? { authContext } : {}),
      body: request,
    });
  }
}

export const companyService = new CompanyService();