import {useState} from 'react';
import {useNavigate} from 'react-router-dom';
import {useAuth} from './useAuth.js';

function validateRegister(values, rules) {
    const errors = {};

    const username = values.username.trim();
    const uRules = rules.username;
    if (username.length === 0) {
        errors.username = uRules.messages.required;
    } else if (username.length < uRules.minLength) {
        errors.username = uRules.messages.minLength;
    } else if (username.length > uRules.maxLength) {
        errors.username = uRules.messages.maxLength;
    }

    const email = values.email.trim();
    const emailRules = rules.email;
    if (email.length === 0) {
        errors.email = emailRules.messages.required;
    } else if (!new RegExp(emailRules.pattern).test(email)) {
        errors.email = emailRules.messages.pattern;
    }

    const pRules = rules.password;
    if (values.password.length === 0) {
        errors.password = pRules.messages.required;
    } else if (values.password.length < pRules.minLength) {
        errors.password = pRules.messages.minLength;
    }

    const cRules = rules.confirmPassword;
    if (values.confirmPassword.length === 0) {
        errors.confirmPassword = cRules.messages.required;
    } else if (values.confirmPassword !== values.password) {
        errors.confirmPassword = cRules.messages.mismatch;
    }

    return errors;
}

export function useRegisterForm(config) {
    const {register, isLoading} = useAuth();
    const navigate = useNavigate();

    const [username, setUsername] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [errorMessage, setErrorMessage] = useState('');
    const [fieldErrors, setFieldErrors] = useState({});

    async function handleSubmit(event) {
        event.preventDefault();
        setErrorMessage('');

        const errors = validateRegister(
            {username, email, password, confirmPassword},
            config.validation,
        );
        setFieldErrors(errors);
        if (Object.keys(errors).length > 0) return;

        const result = await register({
            username: username.trim(),
            email: email.trim(),
            password,
        });

        if (result.success) {
            navigate('/login');
            return;
        }

        if (result.status === 0) {
            setErrorMessage(config.errors.network);
        } else {
            setErrorMessage(result.error || config.errors.unknown);
        }
    }

    return {
        username,
        email,
        password,
        confirmPassword,
        isLoading,
        fieldErrors,
        errorMessage,
        setUsername,
        setEmail,
        setPassword,
        setConfirmPassword,
        handleSubmit,
    };
}
