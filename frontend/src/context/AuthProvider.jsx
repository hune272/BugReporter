import { useCallback, useMemo, useState } from 'react';
import { authApi } from '../api/authApi.js';
import { AuthContext } from './AuthContext.jsx';

function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [isLoading, setIsLoading] = useState(false);

  const login = useCallback(async ({ username, password }) => {
    setIsLoading(true);
    const result = await authApi.login({ username, password });
    setIsLoading(false);
    if (result.success) {
      setUser(result.user ?? { username });
    }
    return result;
  }, []);

  const logout = useCallback(async () => {
    await authApi.logout();
    setUser(null);
  }, []);

  const value = useMemo(
    () => ({ user, isLoading, login, logout }),
    [user, isLoading, login, logout],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export default AuthProvider;
