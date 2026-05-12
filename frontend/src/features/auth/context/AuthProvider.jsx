import {useCallback, useEffect, useMemo, useState} from 'react';
import {useIsRestoring, useMutation, useQuery, useQueryClient} from '@tanstack/react-query';
import { authKeys } from '@shared/api/queryKeys.js';
import {authApi} from '../api.js';
import {AuthContext} from './AuthContext.jsx';

function AuthProvider({children}) {
    const queryClient = useQueryClient();
    const isRestoring = useIsRestoring();
    const [user, setUser] = useState(undefined);

    const sessionQuery = useQuery({
        queryKey: authKeys.me,
        queryFn: authApi.checkSession,
        staleTime: 30_000,
        retry: false,
    });

    const loginMutation = useMutation({
        mutationFn: authApi.login,
        onSuccess: (result, variables) => {
            if (!result.success) return;
            const nextUser = result.user ?? {email: variables.email};
            setUser(nextUser);
            queryClient.setQueryData(authKeys.me, {
                authenticated: true,
                user: nextUser,
            });
        },
    });

    const logoutMutation = useMutation({
        mutationFn: authApi.logout,
        onSuccess: () => {
            setUser(null);
            queryClient.setQueryData(authKeys.me, {authenticated: false});
        },
    });

    const registerMutation = useMutation({
        mutationFn: authApi.register,
    });

    useEffect(() => {
        function handleUnauthorized() {
            setUser(null);
            queryClient.setQueryData(authKeys.me, {authenticated: false});
        }

        window.addEventListener('auth:unauthorized', handleUnauthorized);
        return () => window.removeEventListener('auth:unauthorized', handleUnauthorized);
    }, [queryClient]);

    const login = useCallback(async ({email, password}) => {
        return loginMutation.mutateAsync({email, password});
    }, [loginMutation]);

    const register = useCallback(async ({username, email, password}) => {
        return registerMutation.mutateAsync({username, email, password});
    }, [registerMutation]);

    const logout = useCallback(async () => {
        await logoutMutation.mutateAsync();
    }, [logoutMutation]);

    const sessionUser = sessionQuery.data?.authenticated ? sessionQuery.data.user : null;
    const currentUser = user === undefined ? sessionUser : user;
    const isLoading =
        isRestoring ||
        (sessionQuery.isPending && user === undefined) ||
        loginMutation.isPending ||
        logoutMutation.isPending ||
        registerMutation.isPending;

    const value = useMemo(
        () => ({user: currentUser, isLoading, login, logout, register}),
        [currentUser, isLoading, login, logout, register],
    );

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export default AuthProvider;
