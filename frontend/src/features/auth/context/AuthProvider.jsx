import {useCallback, useMemo, useState} from 'react';
import {authApi} from '../api.js';
import {AuthContext} from './AuthContext.jsx';

function AuthProvider({children}) {
    const [user, setUser] = useState(null);
    const [isLoading, setIsLoading] = useState(false);

    const login = useCallback(async ({email, password}) => {
        setIsLoading(true);
        const result = await authApi.login({email, password});
        setIsLoading(false);
        if (result.success) {
            setUser(result.user ?? {email});
        }
        return result;
    }, []);

    const logout = useCallback(async () => {
        await authApi.logout();
        setUser(null);
    }, []);

    const value = useMemo(() => ({user, isLoading, login, logout}), [user, isLoading, login, logout],);

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export default AuthProvider;
