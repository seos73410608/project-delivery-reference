import { describe, expect, it, vi } from 'vitest';
import { fireEvent, render, screen, waitFor } from '@testing-library/react';

import type { AxiosError } from 'axios';

import ProjectForm from './ProjectForm';

describe('ProjectForm', () => {
    it('프로젝트 등록 실패 시 Backend 오류 메시지를 화면에 표시한다', async () => {
        const onCreate = vi.fn().mockRejectedValue({
            isAxiosError: true,
            response: {
                data: {
                    success: false,
                    code: 'AUTH_002',
                    message: '접근 권한이 없습니다.',
                },
            },
        } as unknown as AxiosError);

        render(
            <ProjectForm
                mode="create"
                onCreate={onCreate}
                onCancel={vi.fn()}
            />,
        );

        fireEvent.change(
            screen.getByLabelText('프로젝트 코드'),
            { target: { value: 'PRJ-2026-001' } },
        );

        fireEvent.change(
            screen.getByLabelText('프로젝트명'),
            { target: { value: 'PMIS 구축' } },
        );

        fireEvent.change(
            screen.getByLabelText('고객사'),
            { target: { value: '내부' } },
        );

        fireEvent.change(
            screen.getByLabelText('PM'),
            { target: { value: '홍길동' } },
        );

        fireEvent.change(
            screen.getByLabelText('시작일'),
            { target: { value: '2026-01-01' } },
        );

        fireEvent.change(
            screen.getByLabelText('종료일'),
            { target: { value: '2026-12-31' } },
        );

        fireEvent.click(
            screen.getByRole('button', { name: '등록' }),
        );

        await waitFor(() => {
            expect(onCreate).toHaveBeenCalledTimes(1);
        });

        expect(
            await screen.findByRole('alert'),
        ).toHaveTextContent('접근 권한이 없습니다.');

        expect(
            screen.queryByText('프로젝트 등록에 실패했습니다.'),
        ).not.toBeInTheDocument();
    });
});