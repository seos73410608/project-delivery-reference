export interface ApiResponse<T> {
  success: boolean;
  code: string;
  message?: string;
  data: T;
}

export type EvidenceType =
  | 'OTHER'
  | 'LOG'
  | 'SCREENSHOT'
  | 'TEST_RESULT'
  | 'IMAGE'
  | 'REPORT'
  | 'CONFIGURATION'
  | 'DOCUMENT';

export type VerificationStatus =
  | 'PENDING'
  | 'APPROVED'
  | 'REJECTED';

export interface Evidence {
  id: number;
  projectId: number;
  requirementId: number;
  wbsId: number | null;

  evidenceKey: string;
  evidenceType: EvidenceType;
  title: string;
  description: string | null;

  fileName: string | null;
  filePath: string | null;

  submittedById: number;
  submittedByName: string;
  submittedAt: string;

  verificationStatus: VerificationStatus;
  verifiedById: number | null;
  verifiedByName: string | null;
  verifiedAt: string | null;
  verificationRemark: string | null;

  createdAt: string;
  updatedAt: string;
}