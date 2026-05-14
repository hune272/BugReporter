import {describe, expect, it, vi} from 'vitest';
import {fireEvent, render, screen} from '@testing-library/react';
import RegisterForm from '@features/auth/components/RegisterForm.jsx';
import registerConfig from '@features/auth/registerConfig.json';

const defaultProps = {
    config: registerConfig,
    username: '',
    email: '',
    password: '',
    confirmPassword: '',
    isLoading: false,
    fieldErrors: {},
    errorMessage: '',
    onUsernameChange: vi.fn(),
    onEmailChange: vi.fn(),
    onPasswordChange: vi.fn(),
    onConfirmPasswordChange: vi.fn(),
    onSubmit: vi.fn(),
};

function renderForm(props = {}) {
    return render(<RegisterForm {...defaultProps} {...props} />);
}

describe('RegisterForm — rendering', () => {
    it('renders all four input fields', () => {
        renderForm();
        expect(screen.getByLabelText(/username/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/^email/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/^password/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/confirm password/i)).toBeInTheDocument();
    });

    it('renders the submit button', () => {
        renderForm();
        expect(screen.getByRole('button')).toBeInTheDocument();
    });

    it('submit button is disabled when all fields are empty', () => {
        renderForm();
        expect(screen.getByRole('button')).toBeDisabled();
    });

    it('submit button is disabled when any field is missing', () => {
        renderForm({username: 'alice', email: 'a@b.com', password: 'secret'});
        expect(screen.getByRole('button')).toBeDisabled();
    });

    it('submit button is enabled when all four fields have values', () => {
        renderForm({username: 'alice', email: 'a@b.com', password: 'secret', confirmPassword: 'secret'});
        expect(screen.getByRole('button')).not.toBeDisabled();
    });

    it('submit button is disabled when isLoading', () => {
        renderForm({
            username: 'alice', email: 'a@b.com', password: 'secret', confirmPassword: 'secret',
            isLoading: true,
        });
        expect(screen.getByRole('button')).toBeDisabled();
    });

    it('shows loading label when isLoading', () => {
        renderForm({
            username: 'alice', email: 'a@b.com', password: 'secret', confirmPassword: 'secret',
            isLoading: true,
        });
        expect(screen.getByText(registerConfig.submittingLabel)).toBeInTheDocument();
    });
});

describe('RegisterForm — field errors', () => {
    it('shows username field error', () => {
        renderForm({fieldErrors: {username: 'Username is required.'}});
        expect(screen.getByText('Username is required.')).toBeInTheDocument();
    });

    it('shows email field error', () => {
        renderForm({fieldErrors: {email: 'Enter a valid email address.'}});
        expect(screen.getByText('Enter a valid email address.')).toBeInTheDocument();
    });

    it('shows password field error', () => {
        renderForm({fieldErrors: {password: 'Password must be at least 6 characters.'}});
        expect(screen.getByText('Password must be at least 6 characters.')).toBeInTheDocument();
    });

    it('shows confirmPassword field error', () => {
        renderForm({fieldErrors: {confirmPassword: 'Passwords do not match.'}});
        expect(screen.getByText('Passwords do not match.')).toBeInTheDocument();
    });
});

describe('RegisterForm — error message', () => {
    it('shows submit error message', () => {
        renderForm({errorMessage: 'Email already exists'});
        expect(screen.getByText('Email already exists')).toBeInTheDocument();
    });
});

describe('RegisterForm — interactions', () => {
    it('calls onUsernameChange when username input changes', () => {
        const onUsernameChange = vi.fn();
        renderForm({onUsernameChange});
        fireEvent.change(screen.getByLabelText(/username/i), {target: {value: 'bob'}});
        expect(onUsernameChange).toHaveBeenCalledWith('bob');
    });

    it('calls onEmailChange when email input changes', () => {
        const onEmailChange = vi.fn();
        renderForm({onEmailChange});
        fireEvent.change(screen.getByLabelText(/^email/i), {target: {value: 'bob@b.com'}});
        expect(onEmailChange).toHaveBeenCalledWith('bob@b.com');
    });
});
