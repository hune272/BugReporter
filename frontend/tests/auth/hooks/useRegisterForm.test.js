import {beforeEach, describe, expect, it, vi} from 'vitest';
import {act, renderHook} from '@testing-library/react';
import {useAuth} from '@features/auth/hooks/useAuth.js';
import {useRegisterForm} from '@features/auth/hooks/useRegisterForm.js';
import registerConfig from '@features/auth/registerConfig.json';

vi.mock('@features/auth/hooks/useAuth.js', () => ({
    useAuth: vi.fn(),
}));

const mockNavigate = vi.fn();
vi.mock('react-router-dom', async (importOriginal) => {
    const actual = await importOriginal();
    return {...actual, useNavigate: () => mockNavigate};
});

const registerMock = vi.fn();

function setup() {
    useAuth.mockReturnValue({register: registerMock, isLoading: false});
    return renderHook(() => useRegisterForm(registerConfig));
}

function fillValidForm(result) {
    act(() => result.current.setUsername('alice'));
    act(() => result.current.setEmail('alice@example.com'));
    act(() => result.current.setPassword('password123'));
    act(() => result.current.setConfirmPassword('password123'));
}

beforeEach(() => {
    vi.clearAllMocks();
});

describe('useRegisterForm — initial state', () => {
    it('starts with empty fields and no errors', () => {
        const {result} = setup();
        expect(result.current.username).toBe('');
        expect(result.current.email).toBe('');
        expect(result.current.password).toBe('');
        expect(result.current.confirmPassword).toBe('');
        expect(result.current.errorMessage).toBe('');
        expect(result.current.fieldErrors).toEqual({});
    });
});

describe('useRegisterForm — username validation', () => {
    it('sets error when username is empty', async () => {
        const {result} = setup();
        await act(async () => result.current.handleSubmit({preventDefault: vi.fn()}));
        expect(result.current.fieldErrors.username).toBeTruthy();
    });

    it('sets error when username is too short', async () => {
        const {result} = setup();
        act(() => result.current.setUsername('ab'));
        await act(async () => result.current.handleSubmit({preventDefault: vi.fn()}));
        expect(result.current.fieldErrors.username).toBeTruthy();
    });

    it('sets error when username exceeds maxLength (50)', async () => {
        const {result} = setup();
        act(() => result.current.setUsername('a'.repeat(51)));
        await act(async () => result.current.handleSubmit({preventDefault: vi.fn()}));
        expect(result.current.fieldErrors.username).toBeTruthy();
    });

    it('accepts username at exactly maxLength (50)', async () => {
        registerMock.mockResolvedValue({success: true});
        const {result} = setup();
        act(() => result.current.setUsername('a'.repeat(50)));
        act(() => result.current.setEmail('alice@example.com'));
        act(() => result.current.setPassword('password123'));
        act(() => result.current.setConfirmPassword('password123'));
        await act(async () => result.current.handleSubmit({preventDefault: vi.fn()}));
        expect(result.current.fieldErrors.username).toBeFalsy();
    });
});

describe('useRegisterForm — email validation', () => {
    it('sets error when email is empty', async () => {
        const {result} = setup();
        act(() => result.current.setUsername('alice'));
        await act(async () => result.current.handleSubmit({preventDefault: vi.fn()}));
        expect(result.current.fieldErrors.email).toBeTruthy();
    });

    it('sets error for invalid email format', async () => {
        const {result} = setup();
        act(() => result.current.setUsername('alice'));
        act(() => result.current.setEmail('notanemail'));
        await act(async () => result.current.handleSubmit({preventDefault: vi.fn()}));
        expect(result.current.fieldErrors.email).toBeTruthy();
    });
});

describe('useRegisterForm — password validation', () => {
    it('sets error when password is empty', async () => {
        const {result} = setup();
        act(() => result.current.setUsername('alice'));
        act(() => result.current.setEmail('alice@example.com'));
        await act(async () => result.current.handleSubmit({preventDefault: vi.fn()}));
        expect(result.current.fieldErrors.password).toBeTruthy();
    });

    it('sets error when password is shorter than minLength', async () => {
        const {result} = setup();
        act(() => result.current.setUsername('alice'));
        act(() => result.current.setEmail('alice@example.com'));
        act(() => result.current.setPassword('abc'));
        await act(async () => result.current.handleSubmit({preventDefault: vi.fn()}));
        expect(result.current.fieldErrors.password).toBeTruthy();
    });
});

describe('useRegisterForm — confirmPassword validation', () => {
    it('sets error when confirmPassword is empty', async () => {
        const {result} = setup();
        act(() => result.current.setUsername('alice'));
        act(() => result.current.setEmail('alice@example.com'));
        act(() => result.current.setPassword('password123'));
        await act(async () => result.current.handleSubmit({preventDefault: vi.fn()}));
        expect(result.current.fieldErrors.confirmPassword).toBeTruthy();
    });

    it('sets error when passwords do not match', async () => {
        const {result} = setup();
        act(() => result.current.setUsername('alice'));
        act(() => result.current.setEmail('alice@example.com'));
        act(() => result.current.setPassword('password123'));
        act(() => result.current.setConfirmPassword('different'));
        await act(async () => result.current.handleSubmit({preventDefault: vi.fn()}));
        expect(result.current.fieldErrors.confirmPassword).toBeTruthy();
    });
});

describe('useRegisterForm — submit outcomes', () => {
    it('navigates to /login on successful registration', async () => {
        registerMock.mockResolvedValue({success: true});
        const {result} = setup();
        fillValidForm(result);
        await act(async () => result.current.handleSubmit({preventDefault: vi.fn()}));
        expect(mockNavigate).toHaveBeenCalledWith('/login');
    });

    it('does not call register when validation fails', async () => {
        const {result} = setup();
        await act(async () => result.current.handleSubmit({preventDefault: vi.fn()}));
        expect(registerMock).not.toHaveBeenCalled();
    });

    it('sets network error for status 0', async () => {
        registerMock.mockResolvedValue({success: false, status: 0});
        const {result} = setup();
        fillValidForm(result);
        await act(async () => result.current.handleSubmit({preventDefault: vi.fn()}));
        expect(result.current.errorMessage).toBe(registerConfig.errors.network);
    });

    it('sets server error message on failure', async () => {
        registerMock.mockResolvedValue({success: false, status: 409, error: 'Email already exists'});
        const {result} = setup();
        fillValidForm(result);
        await act(async () => result.current.handleSubmit({preventDefault: vi.fn()}));
        expect(result.current.errorMessage).toBe('Email already exists');
    });

    it('sets unknown error when result has no error message', async () => {
        registerMock.mockResolvedValue({success: false, status: 500});
        const {result} = setup();
        fillValidForm(result);
        await act(async () => result.current.handleSubmit({preventDefault: vi.fn()}));
        expect(result.current.errorMessage).toBe(registerConfig.errors.unknown);
    });

    it('calls register with trimmed username and email', async () => {
        registerMock.mockResolvedValue({success: true});
        const {result} = setup();
        act(() => result.current.setUsername('  alice  '));
        act(() => result.current.setEmail('  alice@example.com  '));
        act(() => result.current.setPassword('password123'));
        act(() => result.current.setConfirmPassword('password123'));
        await act(async () => result.current.handleSubmit({preventDefault: vi.fn()}));
        expect(registerMock).toHaveBeenCalledWith(
            expect.objectContaining({username: 'alice', email: 'alice@example.com'}),
        );
    });
});
